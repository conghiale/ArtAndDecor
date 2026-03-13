package org.ArtAndDecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Image Upload DTO for handling batch file uploads with metadata
 * Receives multiple image files via form-data along with metadata
 * 
 * File name (imageName) is automatically generated via hash + current timestamp (milliseconds)
 * to ensure uniqueness - client does NOT provide this value
 * 
 * Display names are taken from client for user-friendly identification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadDto {
    
    /**
     * Array of image files uploaded via form-data
     * Required for upload operations
     */
    @NotNull(message = "Image files are required")
    private MultipartFile[] imageFiles;
    
    /**
     * Array of display names corresponding to each file
     * Optional field - if not provided, will be extracted from original filename
     * Each represents the user-friendly name for the image
     */
    private String[] imageDisplayNames;
    
    /**
     * Array of image sizes (dimensions) - e.g., "2048x1024", "1920x1080", "800x600"
     * Optional field, if not provided, will be auto-detected from image file
     */
    private String[] imageSizes;
    
    /**
     * Array of image formats - e.g., "JPG", "PNG", "WEBP", "GIF"
     * Optional field, if not provided, will be extracted from file extension
     */
    private String[] imageFormats;
    
    /**
     * Array of remarks for each image (Vietnamese descriptions)
     * Optional field
     */
    private String[] imageRemarks;
    
    /**
     * Array of slugs for each image
     * If not provided, will be generated from display name
     * Optional field
     */
    private String[] imageSlugs;
}
