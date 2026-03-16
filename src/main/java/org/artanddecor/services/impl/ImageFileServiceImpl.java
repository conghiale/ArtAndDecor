package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.model.Policy;
import org.artanddecor.repository.PolicyRepository;
import org.artanddecor.services.ImageFileService;
import org.artanddecor.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Image File Service Implementation
 * Handles file operations for images stored on local disk
 * Files are hashed using SHA-256, original names stored in database
 */
@Service
@RequiredArgsConstructor
public class ImageFileServiceImpl implements ImageFileService {

    private static final Logger logger = LoggerFactory.getLogger(ImageFileServiceImpl.class);
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    private final PolicyRepository policyRepository;

    @Override
    public FileUploadResult uploadImage(MultipartFile file, String imageDisplayName) throws IOException {
        logger.info("Processing image file: {}", imageDisplayName);

        // Validate file
        validateFile(file);

        // Get file extension
        String fileExtension = "";
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // Hash file content only (no timestamp)
        byte[] fileContent = file.getBytes();
        String hashOnly = Utils.hashFileContent(fileContent); // Need to create this method
        String fileName = hashOnly + fileExtension;
        
        // Generate folder scaling path
        String scalingPath = generateScalingPath(hashOnly);
        String pathFile = scalingPath + "/" + fileName;
        
        // Get storage path and create full directory structure
        String storagePath = getStoragePath();
        Path fullDirectoryPath = Paths.get(storagePath + scalingPath);
        Path fullFilePath = Paths.get(storagePath + pathFile);
        
        // Check if file already exists
        if (Files.exists(fullFilePath)) {
            logger.info("File already exists, skipping upload: {}", pathFile);
            return new FileUploadResult(fileName, pathFile, true);
        }
        
        // Create directory structure if not exists
        Files.createDirectories(fullDirectoryPath);
        
        // Save new file
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, fullFilePath);
            logger.info("New image saved successfully: {} -> {}", imageDisplayName, pathFile);
        } catch (IOException e) {
            logger.error("Failed to save image {}: {}", imageDisplayName, e.getMessage(), e);
            throw e;
        }

        return new FileUploadResult(fileName, pathFile, false);
    }

    @Override
    public byte[] downloadImage(String hashedFilename) throws IOException {
        logger.debug("Downloading image file: {}", hashedFilename);

        String storagePath = getStoragePath();
        Path filePath = Paths.get(storagePath).resolve(hashedFilename);

        if (!Files.exists(filePath)) {
            logger.warn("Image file not found: {}", hashedFilename);
            throw new IOException("Image file not found: " + hashedFilename);
        }

        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            logger.error("Failed to download image {}: {}", hashedFilename, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteImage(String hashedFilename) throws IOException {
        logger.info("Deleting image file: {}", hashedFilename);

        String storagePath = getStoragePath();
        Path filePath = Paths.get(storagePath).resolve(hashedFilename);

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("Image deleted successfully: {}", hashedFilename);
            } else {
                logger.warn("Image file not found for deletion: {}", hashedFilename);
            }
        } catch (IOException e) {
            logger.error("Failed to delete image {}: {}", hashedFilename, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Deprecated
    public FileUploadResult replaceImage(MultipartFile file, String imageDisplayName) throws IOException {
        logger.info("Replacing image file (deprecated method): {}", imageDisplayName);
        logger.warn("replaceImage() is deprecated. Use uploadImage() for consistent deduplication logic.");
        
        // Delegate to uploadImage() for consistency
        return uploadImage(file, imageDisplayName);
    }

    @Override
    public String getStoragePath() {
        logger.debug("Retrieving storage path from Policy");

        try {
            return policyRepository.findByPolicyName("STORAGE_PATH")
                    .map(Policy::getPolicyValue)
                    .orElseThrow(() -> new RuntimeException("STORAGE_PATH policy not found in database"));
        } catch (Exception e) {
            logger.error("Error getting storage path: {}", e.getMessage(), e);
//            Fallback to default path
            String defaultPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "images";
            logger.warn("Using default storage path: {}", defaultPath);
            return defaultPath;
        }
    }

    @Override
    public boolean fileExists(String hashedFilename) {
        logger.debug("Checking if file exists: {}", hashedFilename);

        String storagePath = getStoragePath();
        Path filePath = Paths.get(storagePath).resolve(hashedFilename);
        return Files.exists(filePath);
    }

    /**
     * Get image dimensions (width x height) from uploaded file
     * Delegates to Utils.getImageDimensions() for reusable image analysis
     * Example: "2048x1024"
     * Returns "unknown" if dimensions cannot be determined
     *
     * @param file Image file to analyze
     * @return Image dimensions as "widthxheight" string
     * @throws IOException If file cannot be read
     */
    @Override
    public String getImageDimensions(MultipartFile file) throws IOException {
        logger.debug("Reading image dimensions from file");
        
        if (file == null || file.isEmpty()) {
            logger.warn("File is null or empty");
            return "unknown";
        }
        
        try {
            byte[] imageBytes = file.getBytes();
            String dimensions = Utils.getImageDimensions(imageBytes);
            logger.debug("Image dimensions retrieved: {}", dimensions);
            return dimensions;
        } catch (IOException e) {
            logger.warn("Failed to read image dimensions: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Validate uploaded file
     * Check file size, MIME type, etc.
     *
     * @param file File to validate
     * @throws IOException If validation fails
     */
    private void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds maximum limit: " + MAX_FILE_SIZE);
        }

        String contentType = file.getContentType();
        if (!isValidImageType(contentType)) {
            throw new IOException("Invalid file type. Only image files are allowed: " + contentType);
        }
    }

    /**
     * Check if file type is valid image format
     *
     * @param contentType MIME type
     * @return true if valid image type
     */
    private boolean isValidImageType(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    /**
     * Generate folder scaling path from hash
     * Takes first 4 characters and creates 2-level directory structure
     * Example: hash "abcd1234..." -> "/ab/cd"
     *
     * @param hash SHA-256 hash string
     * @return Scaling path (e.g., "/ab/cd")
     */
    private String generateScalingPath(String hash) {
        if (hash == null || hash.length() < 4) {
            throw new IllegalArgumentException("Hash must be at least 4 characters long");
        }
        
        String firstLevel = hash.substring(0, 2).toLowerCase(); 
        String secondLevel = hash.substring(2, 4).toLowerCase();
        
        return "/" + firstLevel + "/" + secondLevel;
    }
}
