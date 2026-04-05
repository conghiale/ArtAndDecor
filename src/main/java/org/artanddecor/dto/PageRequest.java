package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * Page Request DTO for creating/updating pages
 * Contains only the fields required for page operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    
    @NotNull(message = "Page position ID is required")
    @Positive(message = "Page position ID must be positive")
    private Long pagePositionId;
    
    @NotNull(message = "Page group ID is required")
    @Positive(message = "Page group ID must be positive")
    private Long pageGroupId;
    
    @Size(max = 256, message = "Target URL must not exceed 256 characters")
    private String targetUrl;
    
    @Size(max = 64, message = "Page slug must not exceed 64 characters")
    private String pageSlug;
    
    @NotBlank(message = "Page name is required")
    @Size(max = 100, message = "Page name must not exceed 100 characters")
    private String pageName;
    
    private String pageContent;
    
    private Boolean pageEnabled;
    
    @Size(max = 256, message = "Page display name must not exceed 256 characters")
    private String pageDisplayName;
    
    @NotBlank(message = "Page remark is required")
    @Size(max = 256, message = "Page remark must not exceed 256 characters")
    private String pageRemark;
}