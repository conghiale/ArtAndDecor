package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * SeoMeta DTO for API requests and responses
 * Contains comprehensive SEO metadata from SEO_META table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoMetaDto {
    
    private Long seoMetaId;
    
    @NotBlank(message = "SEO meta title is required")
    @Size(max = 150, message = "SEO meta title must not exceed 150 characters")
    private String seoMetaTitle;
    
    @NotBlank(message = "SEO meta description is required")
    @Size(max = 500, message = "SEO meta description must not exceed 500 characters")
    private String seoMetaDescription;
    
    @Size(max = 300, message = "SEO meta keywords must not exceed 300 characters")
    private String seoMetaKeywords;
    
    private Boolean seoMetaIndex;
    
    private Boolean seoMetaFollow;
    
    @Size(max = 500, message = "Canonical URL must not exceed 500 characters")
    private String seoMetaCanonicalUrl;
    
    @Size(max = 150, message = "Image name must not exceed 150 characters")
    private String seoMetaImageName;
    
    @Size(max = 50, message = "Schema type must not exceed 50 characters")
    private String seoMetaSchemaType;
    
    private String seoMetaCustomJson;
    
    private Boolean seoMetaEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
}