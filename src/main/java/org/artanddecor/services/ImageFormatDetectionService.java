package org.artanddecor.services;

import org.artanddecor.exception.UnsupportedImageFormatException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * Service for detecting image formats from file content and metadata
 * Supports detection of JPG, PNG, WEBP, HEIC formats
 */
public interface ImageFormatDetectionService {

    /**
     * Detect image format from MultipartFile
     * Uses both content-type and binary signature for accurate detection
     * 
     * @param file MultipartFile to analyze
     * @return Detected format (jpg, png, webp, heic) in lowercase
     * @throws IOException If file cannot be read
     * @throws UnsupportedImageFormatException If format is not supported
     */
    String detectFormat(MultipartFile file) throws IOException;

    /**
     * Detect image format from byte array
     * Uses binary signature (magic bytes) for accurate detection
     * 
     * @param fileBytes File content as byte array
     * @param filename Original filename for additional context
     * @return Detected format (jpg, png, webp, heic) in lowercase
     * @throws UnsupportedImageFormatException If format is not supported
     */
    String detectFormat(byte[] fileBytes, String filename);

    /**
     * Check if the given format is supported for dimension detection
     * 
     * @param format Format to check (case-insensitive)
     * @return true if format is supported (jpg, jpeg, png, webp, heic)
     */
    boolean isSupportedFormat(String format);

    /**
     * Validate that the file format is supported
     * Throws exception if not supported
     * 
     * @param file File to validate
     * @throws IOException If file cannot be read
     * @throws UnsupportedImageFormatException If format is not supported
     */
    void validateSupportedFormat(MultipartFile file) throws IOException;
}