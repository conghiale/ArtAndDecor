package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

/**
 * Common Request DTO for Blog Create and Update operations
 * Contains only necessary fields for both operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogRequest {
    
    @NotBlank(message = "Tiêu đề blog là bắt buộc")
    @Size(max = 256, message = "Tiêu đề blog không được vượt quá 256 ký tự")
    private String blogTitle;
    
    @NotBlank(message = "Slug blog là bắt buộc")
    @Size(max = 64, message = "Slug blog không được vượt quá 64 ký tự")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug blog chỉ được chứa chữ thường, số và dấu gạch nối")
    private String blogSlug;
    
    @NotBlank(message = "Nội dung blog là bắt buộc")
    @Size(min = 50, message = "Nội dung blog phải có ít nhất 50 ký tự")
    private String blogContent;
    
    @NotNull(message = "Trạng thái bật blog là bắt buộc")
    private Boolean blogEnabled;
    
    @NotBlank(message = "Ghi chú blog là bắt buộc")
    @Size(max = 256, message = "Ghi chú blog không được vượt quá 256 ký tự")
    private String blogRemark;
    
    // Foreign key IDs only (not nested DTOs)
    @NotNull(message = "ID danh mục blog là bắt buộc")
    @Positive(message = "ID danh mục blog phải lớn hơn 0")
    private Long blogCategoryId;
    
    // SEO Meta information for creating SEO metadata
    @Valid
    private SeoMetaRequestDto seoMeta;
    
    @Positive(message = "ID ảnh phải lớn hơn 0")
    private Long imageId;
}