package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ImageDto;
import org.artanddecor.dto.ImageUploadDto;
import org.artanddecor.enums.ImageEmbeddingStatus;
import org.artanddecor.exception.UnsupportedImageFormatException;
import org.artanddecor.model.Image;
import org.artanddecor.model.ImageEmbedding;
import org.artanddecor.model.Policy;
import org.artanddecor.repository.ImageRepository;
import org.artanddecor.repository.ImageEmbeddingRepository;
import org.artanddecor.repository.PolicyRepository;
import org.artanddecor.services.ImageService;
import org.artanddecor.services.ImageFileService;
import org.artanddecor.services.ImageFileService.FileUploadResult;
import org.artanddecor.utils.ImageMapperUtil;
import org.artanddecor.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.io.StringReader;
import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Image Service Implementation
 * Handles business logic for image management
 * Supports JPG, JPEG, PNG, WEBP, HEIC formats with enhanced dimension detection
 * Note: ImageFormat table has been removed; imageSize field now stores format information
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageServiceImpl implements ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageRepository imageRepository;
    private final ImageFileService imageFileService;
    private final ImageEmbeddingRepository imageEmbeddingRepository;
    private final PolicyRepository policyRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

     /*=============================================
     RETRIEVE OPERATIONS - Used by Controller
     =============================================*/

    @Override
    public Optional<ImageDto> findImageBySlug(String imageSlug) {
        logger.debug("Finding image by slug: {}", imageSlug);
        
        return imageRepository.findByImageSlug(imageSlug)
                .map(this::convertToDto);
    }

    @Override
    public Optional<ImageDto> findImageById(Long imageId) {
        logger.debug("Finding image by ID: {}", imageId);
        
        return imageRepository.findById(imageId)
                .map(this::convertToDto);
    }

     /*=============================================
     CRUD OPERATIONS - Upload and Update Only
     =============================================*/

    @Override
    @Transactional
    public ImageDto uploadImage(ImageUploadDto imageUploadDto) throws IOException {
        MultipartFile file = imageUploadDto.getImageFile();

        if (file == null || file.isEmpty()) {
            throw new IOException("Image file is required");
        }

        try {
            String displayName = (imageUploadDto.getImageDisplayName() != null && !imageUploadDto.getImageDisplayName().trim().isEmpty())
                    ? imageUploadDto.getImageDisplayName().trim()
                    : extractFileNameWithoutExtension(file.getOriginalFilename());

            FileUploadResult uploadResult = imageFileService.uploadImage(file, displayName);
            boolean isNewPhysicalUpload = !uploadResult.isAlreadyExists();

            if (uploadResult.isAlreadyExists()) {
                // Deduplicated: file already exists on disk – return existing DB record if present
                Optional<Image> existingDbImage = imageRepository.findByImageName(uploadResult.getFileName());
                if (existingDbImage.isPresent()) {
                    logger.info("Duplicate file – returning existing image record: ID {}", existingDbImage.get().getImageId());
                    return convertToDto(existingDbImage.get());
                }
                // Disk/DB inconsistency: file on disk but no DB record – fall through to create record
                logger.warn("File exists on disk but not in database: {}, creating new DB record", uploadResult.getFileName());
            }

            // New file (or disk/DB inconsistency): create database record
            String slug = (imageUploadDto.getImageSlug() != null && !imageUploadDto.getImageSlug().trim().isEmpty())
                    ? imageUploadDto.getImageSlug().trim()
                    : Utils.generateSlug(displayName);

            String imageSize = (imageUploadDto.getImageSize() != null && !imageUploadDto.getImageSize().trim().isEmpty())
                    ? imageUploadDto.getImageSize().trim()
                    : imageFileService.getImageDimensions(file);

            String imageFormat;
            if (imageUploadDto.getImageFormat() != null && !imageUploadDto.getImageFormat().trim().isEmpty()) {
                imageFormat = imageUploadDto.getImageFormat().trim().toLowerCase();
            } else {
                String fileName = uploadResult.getFileName();
                imageFormat = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase() : "unknown";
            }

            Image image = new Image();
            image.setImageName(uploadResult.getFileName());
            image.setImageDisplayName(displayName);
            image.setImageSlug(slug);
            image.setImageSize(imageSize);
            image.setImageFormat(imageFormat);
            image.setPathFile(uploadResult.getPathFile());
            image.setImageRemark((imageUploadDto.getImageRemark() != null && !imageUploadDto.getImageRemark().trim().isEmpty())
                    ? imageUploadDto.getImageRemark().trim() : null);

            Image savedImage = imageRepository.save(image);
            logger.info("Image record created: '{}' ID={} (size: {}, pathFile: {})",
                       displayName, savedImage.getImageId(), imageSize, uploadResult.getPathFile());

            ImageDto resultDto = convertToDto(savedImage);

            // Trigger AI embedding only when a brand-new file was uploaded to disk
            if (isNewPhysicalUpload) {
                processImageEmbeddingsAsync(List.of(resultDto));
            }

            return resultDto;

        } catch (UnsupportedImageFormatException e) {
            logger.error("Unsupported format for image upload: {}", e.getMessage(), e);
            throw new IOException("Unsupported image format. Only JPG, JPEG, PNG, WEBP, HEIC are allowed: " + e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Failed to upload image: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ImageDto updateImage(Long imageId, ImageUploadDto imageUploadDto) throws IOException {
        Image existingImage = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with ID: " + imageId));

        MultipartFile imageFile = imageUploadDto.getImageFile();
        boolean hasFile = imageFile != null && !imageFile.isEmpty();

        if (hasFile) {
            return updateImageWithFile(existingImage, imageUploadDto, imageFile);
        } else {
            return updateImageMetadataOnly(existingImage, imageUploadDto);
        }
    }

    /**
     * Update image with a new file: upload file, swap physical file, update all metadata.
     */
    private ImageDto updateImageWithFile(Image existingImage, ImageUploadDto imageUploadDto, MultipartFile imageFile) throws IOException {
        logger.info("Updating image with new file - ID: {}", existingImage.getImageId());
        try {
            String displayName = (imageUploadDto.getImageDisplayName() != null && !imageUploadDto.getImageDisplayName().trim().isEmpty())
                    ? imageUploadDto.getImageDisplayName().trim()
                    : extractFileNameWithoutExtension(imageFile.getOriginalFilename());

            FileUploadResult uploadResult = imageFileService.uploadImage(imageFile, displayName);
            boolean isNewPhysicalUpload = !uploadResult.isAlreadyExists();

            // Delete old physical file only when a new file is uploaded and the path changes
            String oldPathFile = existingImage.getPathFile();
            if (isNewPhysicalUpload && oldPathFile != null && !oldPathFile.equals(uploadResult.getPathFile())) {
                try {
                    imageFileService.deleteImage(oldPathFile);
                    logger.info("Deleted old image file: {}", oldPathFile);
                } catch (IOException e) {
                    logger.warn("Failed to delete old image file {}: {}", oldPathFile, e.getMessage());
                }
            }

            String slug = (imageUploadDto.getImageSlug() != null && !imageUploadDto.getImageSlug().trim().isEmpty())
                    ? imageUploadDto.getImageSlug().trim()
                    : Utils.generateSlug(displayName);

            String imageSize = (imageUploadDto.getImageSize() != null && !imageUploadDto.getImageSize().trim().isEmpty())
                    ? imageUploadDto.getImageSize().trim()
                    : imageFileService.getImageDimensions(imageFile);

            String imageFormat;
            if (imageUploadDto.getImageFormat() != null && !imageUploadDto.getImageFormat().trim().isEmpty()) {
                imageFormat = imageUploadDto.getImageFormat().trim().toLowerCase();
            } else {
                String fileName = uploadResult.getFileName();
                imageFormat = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase() : "unknown";
            }

            existingImage.setImageName(uploadResult.getFileName());
            existingImage.setImageDisplayName(displayName);
            existingImage.setImageSlug(slug);
            existingImage.setImageSize(imageSize);
            existingImage.setImageFormat(imageFormat);
            existingImage.setPathFile(uploadResult.getPathFile());
            existingImage.setImageRemark((imageUploadDto.getImageRemark() != null && !imageUploadDto.getImageRemark().trim().isEmpty())
                    ? imageUploadDto.getImageRemark().trim() : null);
            existingImage.setModifiedDt(LocalDateTime.now());

            Image updatedImage = imageRepository.save(existingImage);
            logger.info("Image (file+metadata) updated: ID={} pathFile={}", updatedImage.getImageId(), uploadResult.getPathFile());

            ImageDto updatedImageDto = convertToDto(updatedImage);

            // Trigger AI embedding only when a new physical file was uploaded
            if (isNewPhysicalUpload) {
                processImageEmbeddingsAsync(List.of(updatedImageDto));
            }

            return updatedImageDto;

        } catch (UnsupportedImageFormatException e) {
            logger.error("Unsupported format for image update - ID: {}: {}", existingImage.getImageId(), e.getMessage(), e);
            throw new IOException("Unsupported image format. Only JPG, JPEG, PNG, WEBP, HEIC are allowed: " + e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Failed to update image with file - ID: {}: {}", existingImage.getImageId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Update image metadata only (no file change): only apply non-null/non-blank fields from DTO.
     */
    private ImageDto updateImageMetadataOnly(Image existingImage, ImageUploadDto imageUploadDto) {
        logger.info("Updating image metadata only - ID: {}", existingImage.getImageId());

        boolean changed = false;

        if (imageUploadDto.getImageDisplayName() != null && !imageUploadDto.getImageDisplayName().trim().isEmpty()) {
            existingImage.setImageDisplayName(imageUploadDto.getImageDisplayName().trim());
            changed = true;
        }
        if (imageUploadDto.getImageSlug() != null && !imageUploadDto.getImageSlug().trim().isEmpty()) {
            existingImage.setImageSlug(imageUploadDto.getImageSlug().trim());
            changed = true;
        }
        if (imageUploadDto.getImageSize() != null && !imageUploadDto.getImageSize().trim().isEmpty()) {
            existingImage.setImageSize(imageUploadDto.getImageSize().trim());
            changed = true;
        }
        if (imageUploadDto.getImageFormat() != null && !imageUploadDto.getImageFormat().trim().isEmpty()) {
            existingImage.setImageFormat(imageUploadDto.getImageFormat().trim().toLowerCase());
            changed = true;
        }
        // imageRemark: allow explicit clear (empty string) or set new value
        if (imageUploadDto.getImageRemark() != null) {
            String remark = imageUploadDto.getImageRemark().trim();
            existingImage.setImageRemark(remark.isEmpty() ? null : remark);
            changed = true;
        }

        if (changed) {
            existingImage.setModifiedDt(LocalDateTime.now());
            existingImage = imageRepository.save(existingImage);
            logger.info("Image metadata updated: ID={}", existingImage.getImageId());
        } else {
            logger.debug("No metadata changes supplied for image ID: {}", existingImage.getImageId());
        }

        return convertToDto(existingImage);
    }

    @Override
    @Transactional
    public void deleteImageById(Long imageId) {
        logger.info("Deleting image by ID: {}", imageId);
        
        if (!imageRepository.existsById(imageId)) {
            throw new IllegalArgumentException("Image not found with ID: " + imageId);
        }

        imageRepository.deleteById(imageId);
        logger.info("Image deleted successfully with ID: {}", imageId);
    }

    // =============================================
    // ANALYTICS & REPORTING OPERATIONS
    // =============================================

    @Override
    public long getTotalImageCount() {
        logger.debug("Getting total image count");
        return imageRepository.countTotalImages();
    }

    @Override
    public List<String> getAllImageSizes() {
        logger.debug("Getting all distinct image sizes");
        return imageRepository.findDistinctImageSizes();
    }

    @Override
    public List<String> getAllImageFormats() {
        logger.debug("Getting all distinct image formats");
        return imageRepository.findDistinctImageFormats();
    }

    // =============================================
    // FILE SERVING OPERATIONS
    // =============================================

    @Override
    public byte[] getImageFileContent(String absolutePath) throws IOException {
        logger.debug("Getting image file content for path: {}", absolutePath);
        
        if (absolutePath == null || absolutePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        
        return imageFileService.downloadImageByAbsolutePath(absolutePath);
    }

    @Override
    public String getImageContentType(String filePath) {
        logger.debug("Getting content type for file: {}", filePath);
        return imageFileService.getContentType(filePath);
    }

    // =============================================
    // FILTER OPERATIONS
    // =============================================

    @Override
    public Page<ImageDto> getImagesByCriteria(String imageSize, String imageFormat, String textSearch, Pageable pageable) {
        logger.debug("Getting images with filters (paginated) - size: {}, format: {}, textSearch: {}", 
                    imageSize, imageFormat, textSearch);
        
        Page<Image> imagesPage = imageRepository.findImagesByCriteriaPaginated(
            imageSize, imageFormat, textSearch, pageable);
        
        return imagesPage.map(this::convertToDto);
    }

    // =============================================
    // UTILITY METHODS
    // =============================================

    /**
     * Convert Image entity to ImageDto
     * Uses ImageMapperUtil for comprehensive mapping
     */
    private ImageDto convertToDto(Image image) {
        return ImageMapperUtil.toDetailedDto(image);
    }

    private String extractFileNameWithoutExtension(String filename) {
        return Utils.extractFileNameWithoutExtension(filename);
    }

    // =============================================
    // AI EMBEDDING HELPER METHODS
    // =============================================

    /**
     * Process image embeddings asynchronously for uploaded images
     * This method doesn't affect the main response flow
     * @param uploadedImages List of successfully uploaded images
     */
    private void processImageEmbeddingsAsync(List<ImageDto> uploadedImages) {
        if (uploadedImages == null || uploadedImages.isEmpty()) {
            return;
        }

        // Process embeddings asynchronously to not block the main response
        CompletableFuture.runAsync(() -> {
            for (ImageDto imageDto : uploadedImages) {
                try {
                    processImageEmbedding(imageDto.getImageId());
                } catch (Exception e) {
                    logger.error("Failed to process embedding for image ID {}: {}", 
                               imageDto.getImageId(), e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Process embedding for a single image
     * @param imageId Image ID to process
     */
    private void processImageEmbedding(Long imageId) {
        try {
            // Check if embedding already exists with completed status
            Optional<ImageEmbedding> existingEmbedding = imageEmbeddingRepository.findByImageId(imageId);
            if (existingEmbedding.isPresent() && 
                existingEmbedding.get().getImageEmbeddingStatus() == ImageEmbeddingStatus.COMPLETED) {
                logger.debug("Embedding already completed for image ID: {}", imageId);
                return;
            }

            // Create or update embedding record with PENDING status
            if (existingEmbedding.isPresent()) {
                // Targeted JPQL update — avoids merging a detached entity with uninitialized lazy image
                imageEmbeddingRepository.updateStatusByImageId(imageId, ImageEmbeddingStatus.PENDING, LocalDateTime.now());
            } else {
                // New embedding: must set via the @OneToOne relationship because imageId field
                // is insertable=false, updatable=false — Hibernate ignores setImageId() on INSERT
                Image imageEntity = imageRepository.findById(imageId).orElse(null);
                if (imageEntity == null) {
                    logger.error("Cannot create embedding: Image not found with ID: {}", imageId);
                    return;
                }
                ImageEmbedding newEmbedding = new ImageEmbedding();
                newEmbedding.setImage(imageEntity);
                newEmbedding.setImageEmbeddingStatus(ImageEmbeddingStatus.PENDING);
                imageEmbeddingRepository.save(newEmbedding);
            }

            logger.info("Creating/updating embedding for image ID: {} with PENDING status", imageId);
            callAiEmbeddingService(imageId);
            
        } catch (Exception e) {
            logger.error("Error processing embedding for image ID {}: {}", imageId, e.getMessage(), e);
            updateEmbeddingStatus(imageId, ImageEmbeddingStatus.FAILED);
        }
    }

    /**
     * Call AI service to generate embedding for image
     * @param imageId Image ID to generate embedding for
     */
    private void callAiEmbeddingService(Long imageId) {
        try {
            // Get AI service configuration from policy
            Properties aiConfig = getAiServiceConfig();
            if (aiConfig == null) {
                logger.error("AI service configuration not found in policies");
                updateEmbeddingStatus(imageId, ImageEmbeddingStatus.FAILED);
                return;
            }

            String host = aiConfig.getProperty("host");
            if (host == null || host.trim().isEmpty()) {
                logger.error("AI service host not configured");
                updateEmbeddingStatus(imageId, ImageEmbeddingStatus.FAILED);
                return;
            }

            // Prepare API call
            String apiUrl = host.trim() + "/api/v1/embeddings/" + imageId;
            logger.debug("Calling AI embedding service: {}", apiUrl);

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));

            // Create request entity (empty body for POST)
            HttpEntity<String> requestEntity = new HttpEntity<>("", headers);

            // Make the API call using RestTemplate
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                // Parse response
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                String status = jsonResponse.path("status").asText();
                
                if ("success".equals(status)) {
                    logger.info("Embedding created successfully for image ID: {}", imageId);
                    updateEmbeddingStatus(imageId, ImageEmbeddingStatus.COMPLETED);
                } else {
                    logger.error("AI service returned non-success status for image ID {}: {}",
                              imageId, jsonResponse.path("message").asText());
                    updateEmbeddingStatus(imageId, ImageEmbeddingStatus.FAILED);
                }
            } else {
                logger.error("AI service returned error status {} for image ID {}: {}", 
                           response.getStatusCode(), imageId, response.getBody());
                updateEmbeddingStatus(imageId, ImageEmbeddingStatus.FAILED);
            }
            
        } catch (Exception e) {
            logger.error("Failed to call AI embedding service for image ID {}: {}", imageId, e.getMessage(), e);
            updateEmbeddingStatus(imageId, ImageEmbeddingStatus.FAILED);
        }
    }

    /**
     * Get AI service configuration from policy table
     * @return Properties object containing AI service configuration or null if not found
     */
    private Properties getAiServiceConfig() {
        try {
            Optional<Policy> aiConfigPolicy = policyRepository.findByPolicyName("SIMILAR_IMG_CONFIG");
            if (aiConfigPolicy.isEmpty()) {
                logger.error("SIMILAR_IMG_CONFIG policy not found");
                return null;
            }

            String configValue = aiConfigPolicy.get().getPolicyValue();
            if (configValue == null || configValue.trim().isEmpty()) {
                logger.error("SIMILAR_IMG_CONFIG policy value is empty");
                return null;
            }

            // Parse properties format: host=http://ai-service:8000\nthreshold=90\ntop_k=20
            Properties properties = new Properties();
            properties.load(new StringReader(configValue));
            
            logger.debug("AI service configuration loaded: host={}, threshold={}, top_k={}", 
                       properties.getProperty("host"), 
                       properties.getProperty("threshold"), 
                       properties.getProperty("top_k"));
            
            return properties;
            
        } catch (Exception e) {
            logger.error("Failed to load AI service configuration: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Update embedding status for a specific image
     * @param imageId Image ID
     * @param status New status
     */
    private void updateEmbeddingStatus(Long imageId, ImageEmbeddingStatus status) {
        try {
            int updated = imageEmbeddingRepository.updateStatusByImageId(imageId, status, LocalDateTime.now());
            if (updated == 0) {
                logger.warn("Embedding not found for image ID: {} when trying to update status to {}", imageId, status);
            } else {
                logger.debug("Updated embedding status to {} for image ID: {}", status, imageId);
            }
        } catch (Exception e) {
            logger.error("Failed to update embedding status to {} for image ID {}: {}", status, imageId, e.getMessage(), e);
        }
    }
}