package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * BlogType DTO for API requests and responses
 * Contains information from BLOG_TYPE table
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogTypeDto {
    
    private Long blogTypeId;
    
    @NotBlank(message = "Blog type slug là bắt buộc")
    @Size(max = 64, message = "Blog type slug không được vượt quá 64 ký tự")
    private String blogTypeSlug;
    
    @NotBlank(message = "Blog type name là bắt buộc")
    @Size(max = 64, message = "Blog type name không được vượt quá 64 ký tự")
    private String blogTypeName;
    
    @Size(max = 256, message = "Blog type display name không được vượt quá 256 ký tự")
    private String blogTypeDisplayName;
    
    @NotBlank(message = "Blog type remark là bắt buộc")
    @Size(max = 256, message = "Blog type remark không được vượt quá 256 ký tự")
    private String blogTypeRemark;
    
    private Boolean blogTypeEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;

    private ImageDto image;
    private SeoMetaDto seoMeta;
}
