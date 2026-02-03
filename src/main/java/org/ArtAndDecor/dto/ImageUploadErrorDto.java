package org.ArtAndDecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Image Upload Error DTO
 * Contains information about failed image uploads
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadErrorDto {
    
    /**
     * Index of the file in the uploaded array
     */
    private Integer fileIndex;
    
    /**
     * Display name of the file that failed
     */
    private String displayName;
    
    /**
     * Original filename
     */
    private String originalFilename;
    
    /**
     * Error message describing why the upload failed
     */
    private String errorMessage;
    
    /**
     * Error code for programmatic handling
     */
    private String errorCode;
}
