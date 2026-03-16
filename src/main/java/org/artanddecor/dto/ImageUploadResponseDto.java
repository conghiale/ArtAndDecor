package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Image Upload Response DTO
 * Contains detailed information about uploaded images
 * Used for API response after image creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponseDto {
    
    /**
     * List of successfully uploaded image DTOs
     */
    private List<ImageDto> uploadedImages;
    
    /**
     * List of any images that failed during upload
     */
    private List<ImageUploadErrorDto> failedImages;
    
    /**
     * Total count of successfully uploaded images
     */
    private Integer successCount;
    
    /**
     * Total count of failed uploads
     */
    private Integer failureCount;
    
    /**
     * Overall success status
     */
    private Boolean success;
    
    /**
     * Timestamp of the upload operation
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadedAt;
    
    /**
     * Overall message for the upload operation
     */
    private String message;
}
