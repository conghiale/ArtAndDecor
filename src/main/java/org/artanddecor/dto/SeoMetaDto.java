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
    
    @NotBlank(message = "SEO meta title là bắt buộc")
    @Size(max = 150, message = "SEO meta title không được vượt quá 150 ký tự")
    private String seoMetaTitle;
    
    @NotBlank(message = "SEO meta description là bắt buộc")
    @Size(max = 500, message = "SEO meta description không được vượt quá 500 ký tự")
    private String seoMetaDescription;
    
    @Size(max = 300, message = "SEO meta keywords không được vượt quá 300 ký tự")
    private String seoMetaKeywords;
    
    private Boolean seoMetaIndex;
    
    private Boolean seoMetaFollow;
    
    @Size(max = 500, message = "Canonical URL không được vượt quá 500 ký tự")
    private String seoMetaCanonicalUrl;
    
    @Size(max = 150, message = "Tên ảnh không được vượt quá 150 ký tự")
    private String seoMetaImageName;
    
    @Size(max = 50, message = "Schema type không được vượt quá 50 ký tự")
    private String seoMetaSchemaType;
    
    private String seoMetaCustomJson;
    
    private Boolean seoMetaEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
}