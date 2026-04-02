package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

/**
 * Common Request DTO for BlogType Create and Update operations
 * Contains only necessary fields for both operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogTypeRequest {
    
    @NotBlank(message = "Blog type slug is required")
    @Size(max = 64, message = "Blog type slug must not exceed 64 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Blog type slug must contain only lowercase letters, numbers, and hyphens")
    private String blogTypeSlug;
    
    @NotBlank(message = "Blog type name is required")
    @Size(max = 64, message = "Blog type name must not exceed 64 characters")
    private String blogTypeName;
    
    @Size(max = 256, message = "Blog type display name must not exceed 256 characters")
    private String blogTypeDisplayName;
    
    @NotBlank(message = "Blog type remark is required")
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String blogTypeRemark;
    
    @NotNull(message = "Blog type enabled status is required")
    private Boolean blogTypeEnabled;
    
    // Foreign key IDs only (not nested DTOs)
    @Positive(message = "Image ID must be positive")
    private Long imageId;
    
    // SEO Meta information for creating SEO metadata
    @Valid
    private SeoMetaRequestDto seoMeta;
}