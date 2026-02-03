package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * BlogCategory DTO for API requests and responses
 * Contains information from BLOG_CATEGORY table
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogCategoryDto {
    
    private Long blogCategoryId;
    
    @NotBlank(message = "Blog category slug is required")
    @Size(max = 64, message = "Blog category slug must not exceed 64 characters")
    private String blogCategorySlug;
    
    @NotBlank(message = "Blog category name is required")
    @Size(max = 64, message = "Blog category name must not exceed 64 characters")
    private String blogCategoryName;
    
    @Size(max = 256, message = "English remark must not exceed 256 characters")
    private String blogCategoryRemarkEn;
    
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String blogCategoryRemark;
    
    private Boolean blogCategoryEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // =============================================
    // NESTED DTO (complete related entity data)
    // =============================================
    private SeoMetaDto seoMeta;
    
    // Constructor for essential fields
    public BlogCategoryDto(String blogCategorySlug, String blogCategoryName, String blogCategoryRemark) {
        this.blogCategorySlug = blogCategorySlug;
        this.blogCategoryName = blogCategoryName;
        this.blogCategoryRemark = blogCategoryRemark;
        this.blogCategoryEnabled = true;
    }
}
