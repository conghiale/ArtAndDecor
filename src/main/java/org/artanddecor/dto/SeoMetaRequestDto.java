package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * SEO Meta Request DTO for API create and update requests
 * Contains SEO metadata information for creating/updating SEO meta entries
 * Field names match SeoMetaDto for consistency
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoMetaRequestDto {
    
    @NotBlank(message = "SEO meta title is required")
    @Size(max = 150, message = "SEO meta title must not exceed 150 characters")
    private String seoMetaTitle;
    
    @Size(max = 500, message = "SEO meta description must not exceed 500 characters")
    private String seoMetaDescription;
    
    @Size(max = 300, message = "SEO meta keywords must not exceed 300 characters")
    private String seoMetaKeywords;
    
    @Size(max = 500, message = "Canonical URL must not exceed 500 characters")
    private String seoMetaCanonicalUrl;
    
    private Boolean seoMetaIndex = true;
    
    private Boolean seoMetaFollow = true;
    
    @Size(max = 50, message = "Schema type must not exceed 50 characters")
    private String seoMetaSchemaType;
    
    @Size(max = 2000, message = "Schema markup must not exceed 2000 characters")
    private String seoMetaCustomJson;
    
    private Boolean seoMetaEnabled = true;
}