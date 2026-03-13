package org.ArtAndDecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.ImageDto;
import org.ArtAndDecor.dto.ImageUploadDto;
import org.ArtAndDecor.dto.ImageUploadResponseDto;
import org.ArtAndDecor.dto.ImageUploadErrorDto;
import org.ArtAndDecor.model.Image;
import org.ArtAndDecor.repository.ImageRepository;
import org.ArtAndDecor.services.ImageService;
import org.ArtAndDecor.services.ImageFileService;
import org.ArtAndDecor.utils.ImageMapperUtil;
import org.ArtAndDecor.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Image Service Implementation
 * Handles business logic for image management
 * Note: ImageFormat table has been removed; imageSize field now stores format information
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageServiceImpl implements ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageRepository imageRepository;
    private final ImageFileService imageFileService;

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
    public ImageUploadResponseDto uploadImages(ImageUploadDto imageUploadDto) {
        logger.info("Uploading {} images", imageUploadDto.getImageFiles().length);
        
        List<ImageDto> uploadedImages = new ArrayList<>();
        List<ImageUploadErrorDto> failedImages = new ArrayList<>();
        
        MultipartFile[] imageFiles = imageUploadDto.getImageFiles();
        String[] displayNames = imageUploadDto.getImageDisplayNames();
        String[] imageSizes = imageUploadDto.getImageSizes();
        String[] imageFormats = imageUploadDto.getImageFormats();
        String[] remarks = imageUploadDto.getImageRemarks();
        String[] slugs = imageUploadDto.getImageSlugs();

//        Process each file
        for (int i = 0; i < imageFiles.length; i++) {
            try {
                MultipartFile file = imageFiles[i];
                String originalFilename = file.getOriginalFilename();
                
//                Validate file
                if (file.isEmpty()) {
                    throw new IOException("File at index " + i + " is empty");
                }
                
//                Use provided displayName, or extract from original filename if not provided
                String filenameWithoutExt = Utils.extractFileNameWithoutExtension(originalFilename);
                String displayName = (displayNames != null && displayNames.length > i && displayNames[i] != null && !displayNames[i].trim().isEmpty())
                        ? displayNames[i].trim()
                        : filenameWithoutExt;
                
//                Upload file to storage
                String uploadedFilename = imageFileService.uploadImage(file, displayName);
                
//                Generate slug from displayName (or provided slug), slug should be without file extension
                String slug = (slugs != null && slugs.length > i && slugs[i] != null) 
                        ? slugs[i] 
                        : Utils.generateSlug(displayName);
                
//                Get image dimensions from file (width x height), or use provided size if given
                String imageSize;
                if (imageSizes != null && imageSizes.length > i && imageSizes[i] != null && !imageSizes[i].trim().isEmpty()) {
                    imageSize = imageSizes[i].trim();
                } else {
                    imageSize = imageFileService.getImageDimensions(file);
                }
                
//                Create image record
                Image image = new Image();
                image.setImageName(uploadedFilename);
                image.setImageDisplayName(displayName);
                image.setImageSlug(slug);
                image.setImageSize(imageSize);
                // Use provided image format or extract from file extension
                String imageFormat;
                if (imageFormats != null && imageFormats.length > i && imageFormats[i] != null && !imageFormats[i].trim().isEmpty()) {
                    imageFormat = imageFormats[i].trim().toUpperCase();
                } else {
                    // Extract file extension for imageFormat
                    String fileExtension = uploadedFilename.substring(uploadedFilename.lastIndexOf(".") + 1).toUpperCase();
                    imageFormat = fileExtension;
                }
                image.setImageFormat(imageFormat);
                image.setImageRemark((remarks != null && remarks.length > i && remarks[i] != null && !remarks[i].trim().isEmpty()) 
                        ? remarks[i].trim() 
                        : null);
                
                Image savedImage = imageRepository.save(image);
                uploadedImages.add(convertToDto(savedImage));
                logger.info("Image uploaded successfully: {} with ID: {} (dimensions: {})", displayName, savedImage.getImageId(), imageSize);
                
            } catch (Exception e) {
                logger.error("Failed to upload image at index {}: {}", i, e.getMessage(), e);

                ImageUploadErrorDto errorDto = ImageUploadErrorDto.builder()
                        .fileIndex(i)
                        .displayName((displayNames != null && displayNames.length > i) ? displayNames[i] : "Unknown")
                        .originalFilename(imageFiles[i] != null
                                ? imageFiles[i].getOriginalFilename() 
                                : "Unknown")
                        .errorMessage(e.getMessage())
                        .errorCode("UPLOAD_FAILED")
                        .build();
                failedImages.add(errorDto);
            }
        }
        
        ImageUploadResponseDto response = ImageUploadResponseDto.builder()
                .uploadedImages(uploadedImages)
                .failedImages(failedImages)
                .successCount(uploadedImages.size())
                .failureCount(failedImages.size())
                .success(failedImages.isEmpty())
                .uploadedAt(LocalDateTime.now())
                .message(String.format("Successfully uploaded %d image(s), %d failed", 
                        uploadedImages.size(), failedImages.size()))
                .build();
        
        logger.info("Image upload completed: {} succeeded, {} failed", 
                uploadedImages.size(), failedImages.size());
        
        return response;
    }

    @Override
    @Transactional
    public ImageDto updateImage(Long imageId, ImageUploadDto imageUploadDto) throws IOException {
        logger.info("Updating image with file - ID: {}", imageId);
        
        Image existingImage = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with ID: " + imageId));
        
        // Validate that only one file is provided for update
        if (imageUploadDto.getImageFiles() == null || imageUploadDto.getImageFiles().length != 1) {
            throw new IOException("Exactly one file is required for update operation");
        }
        
        MultipartFile imageFile = imageUploadDto.getImageFiles()[0];
        
        // Validate file
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IOException("File is empty");
        }
        
        try {
            String originalFilename = imageFile.getOriginalFilename();
            String filenameWithoutExt = extractFileNameWithoutExtension(originalFilename);
            
            // Use provided displayName, or extract from original filename if not provided
            String displayName = (imageUploadDto.getImageDisplayNames() != null && 
                                imageUploadDto.getImageDisplayNames().length > 0 &&
                                imageUploadDto.getImageDisplayNames()[0] != null && 
                                !imageUploadDto.getImageDisplayNames()[0].trim().isEmpty())
                    ? imageUploadDto.getImageDisplayNames()[0].trim()
                    : filenameWithoutExt;

            // Upload new file
            String newImageName = imageFileService.uploadImage(imageFile, displayName);
            
            // Delete old file if it exists
            try {
                imageFileService.deleteImage(existingImage.getImageName());
            } catch (IOException e) {
                logger.warn("Failed to delete old image file: {}", e.getMessage());
            }
            
            // Generate slug from displayName or use provided slug
            String slug = (imageUploadDto.getImageSlugs() != null && 
                         imageUploadDto.getImageSlugs().length > 0 &&
                         imageUploadDto.getImageSlugs()[0] != null && 
                         !imageUploadDto.getImageSlugs()[0].trim().isEmpty())
                    ? imageUploadDto.getImageSlugs()[0].trim()
                    : Utils.generateSlug(displayName);
            
            // Get image dimensions from file or use provided size
            String imageSize;
            if (imageUploadDto.getImageSizes() != null && 
                imageUploadDto.getImageSizes().length > 0 &&
                imageUploadDto.getImageSizes()[0] != null && 
                !imageUploadDto.getImageSizes()[0].trim().isEmpty()) {
                imageSize = imageUploadDto.getImageSizes()[0].trim();
            } else {
                imageSize = imageFileService.getImageDimensions(imageFile);
            }
            
            // Use provided image format or extract from file extension
            String imageFormat;
            if (imageUploadDto.getImageFormats() != null && 
                imageUploadDto.getImageFormats().length > 0 &&
                imageUploadDto.getImageFormats()[0] != null && 
                !imageUploadDto.getImageFormats()[0].trim().isEmpty()) {
                imageFormat = imageUploadDto.getImageFormats()[0].trim().toUpperCase();
            } else {
                String fileExtension = newImageName.substring(newImageName.lastIndexOf(".") + 1).toUpperCase();
                imageFormat = fileExtension;
            }
            
            // Use provided remark if available
            String imageRemark = (imageUploadDto.getImageRemarks() != null && 
                                imageUploadDto.getImageRemarks().length > 0 &&
                                imageUploadDto.getImageRemarks()[0] != null && 
                                !imageUploadDto.getImageRemarks()[0].trim().isEmpty())
                    ? imageUploadDto.getImageRemarks()[0].trim()
                    : null;
            
            // Update image record
            existingImage.setImageName(newImageName);
            existingImage.setImageDisplayName(displayName);
            existingImage.setImageSlug(slug);
            existingImage.setImageSize(imageSize);
            existingImage.setImageFormat(imageFormat);
            existingImage.setImageRemark(imageRemark);
            existingImage.setModifiedDt(LocalDateTime.now());
            
            Image updatedImage = imageRepository.save(existingImage);
            logger.info("Image updated with file successfully: {} (dimensions: {})", updatedImage.getImageId(), imageSize);
            return convertToDto(updatedImage);
            
        } catch (IOException e) {
            logger.error("Failed to update image with file - ID: {}: {}", imageId, e.getMessage(), e);
            throw e;
        }
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

    /**
     * Extract filename without extension from original filename
     * Example: "image-photo.jpg" -> "image-photo"
     * @param filename Original filename
     * @return Filename without extension
     */
    private String extractFileNameWithoutExtension(String filename) {
        return Utils.extractFileNameWithoutExtension(filename);
    }
}