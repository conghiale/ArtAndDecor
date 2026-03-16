package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Image DTO for API requests and responses
 * Contains comprehensive information from IMAGE table with related entities
 * Used for:
 * 1. Returning image information after upload
 * 2. Accepting image metadata in requests
 * 3. Supporting Product API image associations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    
    private Long imageId;
    
    @NotBlank(message = "Image name is required")
    @Size(max = 150, message = "Image name must not exceed 150 characters")
    private String imageName;
    
    @NotBlank(message = "Image display name is required")
    @Size(max = 64, message = "Image display name must not exceed 64 characters")
    private String imageDisplayName;
    
    @NotBlank(message = "Image slug is required")
    @Size(max = 64, message = "Image slug must not exceed 64 characters")
    private String imageSlug;
    
    @NotBlank(message = "Image size is required")
    @Size(max = 64, message = "Image size must not exceed 64 characters")
    private String imageSize;
    
    @Size(max = 10, message = "Image format must not exceed 10 characters")
    private String imageFormat;
    
    @NotBlank(message = "Path file is required")
    @Size(max = 512, message = "Path file must not exceed 512 characters")
    private String pathFile;
    
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String imageRemark;
    
    private Boolean imageEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;

}
