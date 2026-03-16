package org.artanddecor.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Image File Service Interface
 * Handles file operations for images (upload, download, delete)
 * Files are stored locally on disk, path configured in Policy table
 * New logic: Hash-only filename with folder scaling
 */
public interface ImageFileService {

    /**
     * File upload result containing filename and path information
     */
    public static class FileUploadResult {
        private final String fileName;
        private final String pathFile;
        private final boolean alreadyExists;
        
        public FileUploadResult(String fileName, String pathFile, boolean alreadyExists) {
            this.fileName = fileName;
            this.pathFile = pathFile;
            this.alreadyExists = alreadyExists;
        }
        
        public String getFileName() { return fileName; }
        public String getPathFile() { return pathFile; }
        public boolean isAlreadyExists() { return alreadyExists; }
    }

    /**
     * Upload/save image file with hash-only filename and folder scaling
     * Logic: 
     * - Hash file content (no timestamp)
     * - Check if file exists in storage
     * - If exists: return filename only (don't save again)
     * - If not exists: save with folder scaling (ab/cd/abcdef.png)
     * - Return fileName and pathFile
     *
     * @param file Image file to upload
     * @param imageDisplayName Original file name for logging
     * @return FileUploadResult containing fileName, pathFile, and existence status
     * @throws IOException If file operation fails
     */
    FileUploadResult uploadImage(MultipartFile file, String imageDisplayName) throws IOException;

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
     * Deprecated: Use uploadImage() method which handles deduplication automatically
     * 
     * @param file New image file
     * @param imageDisplayName Original filename
     * @return FileUploadResult with new file info
     * @throws IOException If file operation fails
     * @deprecated Use uploadImage() for consistent deduplication logic
     */
    @Deprecated
    FileUploadResult replaceImage(MultipartFile file, String imageDisplayName) throws IOException;

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
