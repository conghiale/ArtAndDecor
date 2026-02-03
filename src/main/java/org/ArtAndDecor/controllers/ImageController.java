package org.ArtAndDecor.controllers;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.BaseResponseDto;
import org.ArtAndDecor.dto.ImageDto;
import org.ArtAndDecor.dto.ImageUploadDto;
import org.ArtAndDecor.dto.ImageUploadResponseDto;
import org.ArtAndDecor.services.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Optional;

/**
 * Image Management REST Controller
 * Handles HTTP requests for image operations with role-based access control
 * Streamlined version with essential CRUD operations only
 */
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    private final ImageService imageService;

    // =============================================
    // PUBLIC ENDPOINTS - Customer Image Browsing
    // =============================================

    /**
     * Get image by slug (Customer-friendly)
     * Public access allowed for image viewing
     */
    @GetMapping("/slug/{imageSlug}")
    public ResponseEntity<BaseResponseDto<ImageDto>> getImageBySlug(@PathVariable String imageSlug) {
        logger.info("Requesting image by slug: {}", imageSlug);
        try {
            Optional<ImageDto> image = imageService.findImageBySlug(imageSlug);
            return image.map(imageDto -> ResponseEntity.ok(BaseResponseDto.success("Image retrieved successfully", imageDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound("Image not found with slug: " + imageSlug)));
        } catch (Exception e) {
            logger.error("Error retrieving image by slug {}: {}", imageSlug, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve image: " + e.getMessage()));
        }
    }

    // =============================================
    // ADMIN ENDPOINTS - Image Management
    // =============================================

    /**
     * Get image by ID (Admin)
     * Requires admin authentication
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<BaseResponseDto<ImageDto>> getImageById(@PathVariable Long imageId) {
        logger.info("Admin requesting image by ID: {}", imageId);
        try {
            Optional<ImageDto> image = imageService.findImageById(imageId);
            return image.map(imageDto -> ResponseEntity.ok(BaseResponseDto.success("Image retrieved successfully", imageDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound("Image not found with ID: " + imageId)));
        } catch (Exception e) {
            logger.error("Error retrieving image by ID {}: {}", imageId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve image: " + e.getMessage()));
        }
    }

    /**
     * Upload multiple images with metadata via form-data
     * Handles batch image upload with file hashing (hash + timestamp) and database persistence
     * Receives request object instead of separate parameters for cleaner API design
     * 
     * Public endpoint accessible for product image uploads
     */
    @PostMapping("/upload")
    public ResponseEntity<BaseResponseDto<ImageUploadResponseDto>> uploadImages(
            @Valid @ModelAttribute ImageUploadDto imageUploadDto) {
        
        logger.info("Uploading {} images via API", imageUploadDto.getImageFiles() != null ? imageUploadDto.getImageFiles().length : 0);
        
        try {
            // Validate required parameters
            if (imageUploadDto.getImageFiles() == null || imageUploadDto.getImageFiles().length == 0) {
                return ResponseEntity.badRequest()
                        .body(BaseResponseDto.badRequest("No image files provided"));
            }
            
            // Process upload
            ImageUploadResponseDto response = imageService.uploadImages(imageUploadDto);
            
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Upload completed: %d succeeded, %d failed", 
                            response.getSuccessCount(), response.getFailureCount()),
                    response
            ));
            
        } catch (IllegalArgumentException e) {
            logger.error("Validation error uploading images: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Validation error: " + e.getMessage()));
        } catch (IOException e) {
            logger.error("IO error uploading images: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponseDto.serverError("File operation failed: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error uploading images: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponseDto.serverError("Failed to upload images: " + e.getMessage()));
        }
    }

    /**
     * Update image with new file via form-data
     * Handles file replacement - receives request object instead of separate parameters
     * Updates both file and metadata in one operation
     */
    @PostMapping("/{imageId}/upload")
    public ResponseEntity<BaseResponseDto<ImageDto>> updateImage(
            @PathVariable Long imageId,
            @Valid @ModelAttribute ImageUploadDto imageUploadDto) {
        
        logger.info("Updating image with file - ID: {}", imageId);
        
        try {
            // Validate
            if (imageUploadDto.getImageFiles() == null || imageUploadDto.getImageFiles().length == 0) {
                return ResponseEntity.badRequest()
                        .body(BaseResponseDto.badRequest("Image file is required"));
            }
            
            if (imageUploadDto.getImageFiles()[0].isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(BaseResponseDto.badRequest("Image file cannot be empty"));
            }
            
            // Get display name (optional), will be extracted from filename if not provided
            String displayName = (imageUploadDto.getImageDisplayNames() != null 
                    && imageUploadDto.getImageDisplayNames().length > 0 
                    && imageUploadDto.getImageDisplayNames()[0] != null)
                    ? imageUploadDto.getImageDisplayNames()[0]
                    : null;
            
            // Process update (using first file from array)
            ImageDto updatedImage = imageService.updateImage(
                    imageId, 
                    imageUploadDto.getImageFiles()[0], 
                    displayName
            );
            
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Image file updated successfully", 
                    updatedImage
            ));
            
        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating image {}: {}", imageId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponseDto.notFound(e.getMessage()));
        } catch (IOException e) {
            logger.error("IO error updating image {}: {}", imageId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponseDto.serverError("File operation failed: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating image {}: {}", imageId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponseDto.serverError("Failed to update image: " + e.getMessage()));
        }
    }

    /**
     * Get total image count (Admin dashboard)
     */
    @GetMapping("/admin/total-count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BaseResponseDto<Long>> getTotalImageCount() {
        logger.info("Getting total image count");
        try {
            long count = imageService.getTotalImageCount();
            return ResponseEntity.ok(BaseResponseDto.success("Total count retrieved successfully", count));
        } catch (Exception e) {
            logger.error("Error getting total image count: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get total count: " + e.getMessage()));
        }
    }
}
