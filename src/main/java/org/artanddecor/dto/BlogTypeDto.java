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
    
    @NotBlank(message = "Blog type slug is required")
    @Size(max = 64, message = "Blog type slug must not exceed 64 characters")
    private String blogTypeSlug;
    
    @NotBlank(message = "Blog type name is required")
    @Size(max = 64, message = "Blog type name must not exceed 64 characters")
    private String blogTypeName;
    
    @Size(max = 256, message = "Blog type display name must not exceed 256 characters")
    private String blogTypeDisplayName;
    
    @NotBlank(message = "Blog type remark is required")
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String blogTypeRemark;
    
    private Boolean blogTypeEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;

    private ImageDto image;
    private SeoMetaDto seoMeta;
}
