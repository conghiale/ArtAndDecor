package org.artanddecor.dto;

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
    
    @NotBlank(message = "Blog category slug là bắt buộc")
    @Size(max = 64, message = "Blog category slug không được vượt quá 64 ký tự")
    private String blogCategorySlug;
    
    @NotBlank(message = "Blog category name là bắt buộc")
    @Size(max = 64, message = "Blog category name không được vượt quá 64 ký tự")
    private String blogCategoryName;
    
    @Size(max = 256, message = "Blog category display name không được vượt quá 256 ký tự")
    private String blogCategoryDisplayName;
    
    @NotBlank(message = "Blog category remark là bắt buộc")
    @Size(max = 256, message = "Blog category remark không được vượt quá 256 ký tự")
    private String blogCategoryRemark;
    
    private Boolean blogCategoryEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // =============================================
    // NESTED DTO (complete related entity data)
    // =============================================
    private BlogTypeDto blogType;
    private ImageDto image;
    private SeoMetaDto seoMeta;
    
    // Constructor for essential fields
    public BlogCategoryDto(String blogCategorySlug, String blogCategoryName, String blogCategoryRemark) {
        this.blogCategorySlug = blogCategorySlug;
        this.blogCategoryName = blogCategoryName;
        this.blogCategoryRemark = blogCategoryRemark;
        this.blogCategoryEnabled = true;
    }
}
