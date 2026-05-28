package org.artanddecor.services;

import org.artanddecor.dto.ImageDto;
import org.artanddecor.dto.ImageDto;
import org.artanddecor.dto.ImageUploadDto;
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
     * Upload a single image with metadata.
     * Handles file storage with SHA-256 hash-based naming, deduplication, and database persistence.
     * When the uploaded file is new (not already on disk), AI embedding is triggered asynchronously.
     *
     * @param imageUploadDto Contains file, display name, and optional metadata
     * @return ImageDto of the uploaded (or deduplicated existing) image
     * @throws IOException If file operations fail
     */
    ImageDto uploadImage(ImageUploadDto imageUploadDto) throws IOException;

    /**
     * Update image with optional new file and/or metadata.
     * <ul>
     *   <li>When {@code imageUploadDto.imageFile} is present: uploads the new file, deletes the old
     *       physical file (if changed), updates all metadata fields, and triggers AI embedding.</li>
     *   <li>When {@code imageUploadDto.imageFile} is absent: only updates the supplied metadata
     *       fields (displayName, slug, size, format, remark) without touching the stored file.</li>
     * </ul>
     *
     * @param imageId      Image ID to update
     * @param imageUploadDto Contains optional file and optional metadata fields
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

    // =============================================
    // FILE SERVING OPERATIONS
    // =============================================

    /**
     * Get image file content by absolute path
     * Used for rendering and downloading images directly from file system
     * 
     * @param absolutePath Absolute file path on server
     * @return Image file content as byte array
     * @throws IOException If file not found or read fails
     * @throws IllegalArgumentException If path is invalid or unsafe
     */
    byte[] getImageFileContent(String absolutePath) throws IOException;

    /**
     * Get MIME content type for image file
     * Determines appropriate Content-Type header based on file extension
     * 
     * @param filePath File path to analyze
     * @return MIME type (e.g., "image/jpeg", "image/png")
     */
    String getImageContentType(String filePath);
}