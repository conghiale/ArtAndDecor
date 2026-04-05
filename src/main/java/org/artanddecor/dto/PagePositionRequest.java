package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * PagePosition Request DTO for creating/updating page positions
 * Contains only the fields required for page position operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagePositionRequest {
    
    @Size(max = 64, message = "Page position slug must not exceed 64 characters")
    private String pagePositionSlug;
    
    @NotBlank(message = "Page position name is required")
    @Size(max = 100, message = "Page position name must not exceed 100 characters")
    private String pagePositionName;
    
    private Boolean pagePositionEnabled;
    
    @Size(max = 256, message = "Page position display name must not exceed 256 characters")
    private String pagePositionDisplayName;
    
    @NotBlank(message = "Page position remark is required")
    @Size(max = 256, message = "Page position remark must not exceed 256 characters")
    private String pagePositionRemark;
}