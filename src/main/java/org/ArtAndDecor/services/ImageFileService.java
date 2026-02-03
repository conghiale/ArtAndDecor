package org.ArtAndDecor.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Image File Service Interface
 * Handles file operations for images (upload, download, delete)
 * Files are stored locally on disk, path configured in Policy table
 */
public interface ImageFileService {

    /**
     * Upload/save image file
     * File will be hashed using SHA-256 for filename
     * Original filename stored in database imageDisplayName
     *
     * @param file Image file to upload
     * @param imageDisplayName Original file name (stored in DB)
     * @return Hashed filename for storage reference
     * @throws IOException If file operation fails
     */
    String uploadImage(MultipartFile file, String imageDisplayName) throws IOException;

    /**
     * Download image file
     * 
     * @param hashedFilename Hashed filename to retrieve
     * @return File bytes
     * @throws IOException If file not found or read fails
     */
    byte[] downloadImage(String hashedFilename) throws IOException;

    /**
     * Delete image file
     * 
     * @param hashedFilename Hashed filename to delete
     * @throws IOException If file deletion fails
     */
    void deleteImage(String hashedFilename) throws IOException;

    /**
     * Replace existing image file
     * If file already exists, it will be replaced
     * 
     * @param file New image file
     * @param imageDisplayName Original filename
     * @return Hashed filename
     * @throws IOException If file operation fails
     */
    String replaceImage(MultipartFile file, String imageDisplayName) throws IOException;

    /**
     * Get storage path from Policy table
     * 
     * @return Storage directory path
     */
    String getStoragePath();

    /**
     * Check if file exists in storage
     * 
     * @param hashedFilename Filename to check
     * @return true if file exists
     */
    boolean fileExists(String hashedFilename);

    /**
     * Get image dimensions (width x height) from uploaded file
     * Reads actual image data to determine dimensions
     * Example return: "2048x1024" or "unknown" if dimensions cannot be determined
     * 
     * @param file Image file to analyze
     * @return Image dimensions as "widthxheight" string
     * @throws IOException If file cannot be read
     */
    String getImageDimensions(MultipartFile file) throws IOException;
}
