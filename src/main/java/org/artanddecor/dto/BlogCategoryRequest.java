package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

/**
 * Common Request DTO for BlogCategory Create and Update operations
 * Contains only necessary fields for both operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogCategoryRequest {
    
    @NotBlank(message = "Blog category slug is required")
    @Size(max = 64, message = "Blog category slug must not exceed 64 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Blog category slug must contain only lowercase letters, numbers, and hyphens")
    private String blogCategorySlug;
    
    @NotBlank(message = "Blog category name is required")
    @Size(max = 64, message = "Blog category name must not exceed 64 characters")
    private String blogCategoryName;
    
    @Size(max = 256, message = "Blog category display name must not exceed 256 characters")
    private String blogCategoryDisplayName;
    
    @NotBlank(message = "Blog category remark is required")
    @Size(max = 256, message = "Blog category remark must not exceed 256 characters")
    private String blogCategoryRemark;
    
    @NotNull(message = "Blog category enabled status is required")
    private Boolean blogCategoryEnabled;
    
    // Foreign key IDs only (not nested DTOs)
    @NotNull(message = "Blog type ID is required")
    @Positive(message = "Blog type ID must be positive")
    private Long blogTypeId;
    
    @Positive(message = "Image ID must be positive")
    private Long imageId;
    
    // SEO Meta information for creating SEO metadata
    @Valid
    private SeoMetaRequestDto seoMeta;
}