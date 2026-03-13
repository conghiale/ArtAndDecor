package org.ArtAndDecor.services;

import org.ArtAndDecor.dto.ImageDto;
import org.ArtAndDecor.dto.ImageUploadDto;
import org.ArtAndDecor.dto.ImageUploadResponseDto;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Image Service Interface
 * Defines business operations for image management
 */
public interface ImageService {

    // =============================================
    // CUSTOMER-FOCUSED OPERATIONS (slug > name > ID priority)
    // =============================================

    /**
     * Find image by slug for customer view
     * Priority: slug (customer-friendly URLs)
     */
    Optional<ImageDto> findImageBySlug(String imageSlug);

    /**
     * Get images by multiple criteria with pagination (all parameters optional)
     * @param imageSize Image size filter (partial match, case-insensitive) 
     * @param imageFormat Image format filter (exact match, case-insensitive)
     * @param textSearch Text search in imageName, imageDisplayName, imageSlug, imageRemark (partial match, case-insensitive)
     * @param pageable Pagination and sorting information
     * @return Page of ImageDto matching criteria
     */
    Page<ImageDto> getImagesByCriteria(String imageSize, String imageFormat, String textSearch, Pageable pageable);

    // =============================================
    // ADMIN-FOCUSED OPERATIONS (ID > name > slug priority)
    // =============================================

    /**
     * Find image by ID for admin management
     * Priority: ID (direct database access)
     */
    Optional<ImageDto> findImageById(Long imageId);

    // =============================================
    // CRUD OPERATIONS - Upload and Update Only
    // =============================================

    /**
     * Upload multiple images with metadata
     * Handles file storage with automatic hash + timestamp naming and database persistence
     * File names are generated automatically to ensure uniqueness
     * 
     * @param imageUploadDto Contains files, display names, and optional metadata
     * @return ImageUploadResponseDto with results and errors
     * @throws IOException If file operations fail
     */
    ImageUploadResponseDto uploadImages(ImageUploadDto imageUploadDto) throws IOException;

    /**
     * Update image with new file and metadata
     * Uploads new file, deletes old one, updates metadata in database
     * File name (imageName) is regenerated with new hash + timestamp
     * 
     * @param imageId Image ID to update
     * @param imageUploadDto Contains file and metadata
     * @return Updated ImageDto
     * @throws IOException If file operations fail
     */
    ImageDto updateImage(Long imageId, ImageUploadDto imageUploadDto) throws IOException;

    /**
     * Delete image by ID (admin)
     */
    void deleteImageById(Long imageId);

    // =============================================
    // ANALYTICS OPERATIONS
    // =============================================

    /**
     * Get total image count
     */
    long getTotalImageCount();

    /**
     * Get all distinct image sizes available in database
     * Used for UI combobox/dropdown options
     */
    List<String> getAllImageSizes();

    /**
     * Get all distinct image formats available in database
     * Used for UI combobox/dropdown options
     */
    List<String> getAllImageFormats();
}