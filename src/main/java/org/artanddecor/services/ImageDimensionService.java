package org.artanddecor.services;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * Interface for image dimension detection services
 * Each format has its own specialized implementation
 */
public interface ImageDimensionService {

    /**
     * Get image dimensions from MultipartFile
     * 
     * @param file Image file to analyze
     * @return Image dimensions as "widthxheight" string (e.g., "1920x1080")
     * @throws IOException If file cannot be read or processed
     */
    String getDimensions(MultipartFile file) throws IOException;

    /**
     * Get image dimensions from byte array
     * 
     * @param imageBytes Image content as byte array
     * @return Image dimensions as "widthxheight" string (e.g., "1920x1080")
     * @throws IOException If image cannot be processed
     */
    String getDimensions(byte[] imageBytes) throws IOException;

    /**
     * Check if this service can handle the given format
     * 
     * @param format Format to check (case-insensitive)
     * @return true if this service can handle the format
     */
    boolean supportsFormat(String format);
}