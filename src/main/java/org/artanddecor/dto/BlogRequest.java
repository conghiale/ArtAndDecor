package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

/**
 * Common Request DTO for Blog Create and Update operations
 * Contains only necessary fields for both operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogRequest {
    
    @NotBlank(message = "Blog title is required")
    @Size(max = 256, message = "Blog title must not exceed 256 characters")
    private String blogTitle;
    
    @NotBlank(message = "Blog slug is required")
    @Size(max = 64, message = "Blog slug must not exceed 64 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Blog slug must contain only lowercase letters, numbers, and hyphens")
    private String blogSlug;
    
    @NotBlank(message = "Blog content is required")
    @Size(min = 50, message = "Blog content must be at least 50 characters")
    private String blogContent;
    
    @NotNull(message = "Blog enabled status is required")
    private Boolean blogEnabled;
    
    @NotBlank(message = "Blog remark is required")
    @Size(max = 256, message = "Blog remark must not exceed 256 characters")
    private String blogRemark;
    
    // Foreign key IDs only (not nested DTOs)
    @NotNull(message = "Blog category ID is required")
    @Positive(message = "Blog category ID must be positive")
    private Long blogCategoryId;
    
    // SEO Meta information for creating SEO metadata
    @Valid
    private SeoMetaRequestDto seoMeta;
    
    @Positive(message = "Image ID must be positive")
    private Long imageId;
}