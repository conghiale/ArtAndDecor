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
    
    @Size(max = 150, message = "SEO meta title không được vượt quá 150 ký tự")
    private String seoMetaTitle;
    
    @Size(max = 500, message = "SEO meta description không được vượt quá 500 ký tự")
    private String seoMetaDescription;
    
    @Size(max = 300, message = "SEO meta keywords không được vượt quá 300 ký tự")
    private String seoMetaKeywords;
    
    @Size(max = 500, message = "Canonical URL không được vượt quá 500 ký tự")
    private String seoMetaCanonicalUrl;
    
    private Boolean seoMetaIndex = true;
    
    private Boolean seoMetaFollow = true;
    
    @Size(max = 50, message = "Schema type không được vượt quá 50 ký tự")
    private String seoMetaSchemaType;
    
    @Size(max = 2000, message = "Schema markup không được vượt quá 2000 ký tự")
    private String seoMetaCustomJson;
    
    private Boolean seoMetaEnabled = true;
}