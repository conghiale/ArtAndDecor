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
    
    @NotBlank(message = "Blog title is required")
    @Size(max = 256, message = "Blog title must not exceed 256 characters")
    private String blogTitle;
    
    @NotBlank(message = "Blog slug is required")
    @Size(max = 64, message = "Blog slug must not exceed 64 characters")
    private String blogSlug;
    
    @NotBlank(message = "Blog content is required")
    private String blogContent;
    
    private Boolean blogEnabled;
    
    @NotBlank(message = "Blog remark is required")
    @Size(max = 256, message = "Blog remark must not exceed 256 characters")
    private String blogRemark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;

    private BlogCategoryDto blogCategory;
    private SeoMetaDto seoMeta;
    private ImageDto image;

}
