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
    
    @Size(max = 64, message = "Page position slug không được vượt quá 64 ký tự")
    private String pagePositionSlug;
    
    @NotBlank(message = "Page position name là bắt buộc")
    @Size(max = 100, message = "Page position name không được vượt quá 100 ký tự")
    private String pagePositionName;
    
    private Boolean pagePositionEnabled;
    
    @Size(max = 256, message = "Page position display name không được vượt quá 256 ký tự")
    private String pagePositionDisplayName;
    
    @NotBlank(message = "Page position remark là bắt buộc")
    @Size(max = 256, message = "Page position remark không được vượt quá 256 ký tự")
    private String pagePositionRemark;
}