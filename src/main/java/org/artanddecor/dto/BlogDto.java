package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Blog DTO for API requests and responses
 * Contains comprehensive information from BLOG table with related entities
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogDto {
    
    private Long blogId;
    
    @NotBlank(message = "Tiêu đề blog là bắt buộc")
    @Size(max = 256, message = "Tiêu đề blog không được vượt quá 256 ký tự")
    private String blogTitle;
    
    @NotBlank(message = "Slug blog là bắt buộc")
    @Size(max = 64, message = "Slug blog không được vượt quá 64 ký tự")
    private String blogSlug;
    
    @NotBlank(message = "Nội dung blog là bắt buộc")
    private String blogContent;
    
    private Boolean blogEnabled;
    
    @NotBlank(message = "Ghi chú blog là bắt buộc")
    @Size(max = 256, message = "Ghi chú blog không được vượt quá 256 ký tự")
    private String blogRemark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;

    private BlogCategoryDto blogCategory;
    private SeoMetaDto seoMeta;
    private ImageDto image;

}
