package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

/**
 * Common Request DTO for BlogCategory Create and Update operations
 * Contains only necessary fields for both operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogCategoryRequest {
    
    @NotBlank(message = "Slug danh mục blog là bắt buộc")
    @Size(max = 64, message = "Slug danh mục blog không được vượt quá 64 ký tự")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug danh mục blog chỉ được chứa chữ thường, số và dấu gạch nối")
    private String blogCategorySlug;
    
    @NotBlank(message = "Tên danh mục blog là bắt buộc")
    @Size(max = 64, message = "Tên danh mục blog không được vượt quá 64 ký tự")
    private String blogCategoryName;
    
    @Size(max = 256, message = "Tên hiển thị danh mục blog không được vượt quá 256 ký tự")
    private String blogCategoryDisplayName;
    
    @NotBlank(message = "Ghi chú danh mục blog là bắt buộc")
    @Size(max = 256, message = "Ghi chú danh mục blog không được vượt quá 256 ký tự")
    private String blogCategoryRemark;
    
    @NotNull(message = "Trạng thái bật danh mục blog là bắt buộc")
    private Boolean blogCategoryEnabled;
    
    // Foreign key IDs only (not nested DTOs)
    @NotNull(message = "ID loại blog là bắt buộc")
    @Positive(message = "ID loại blog phải lớn hơn 0")
    private Long blogTypeId;
    
    @Positive(message = "ID ảnh phải lớn hơn 0")
    private Long imageId;
    
    // SEO Meta information for creating SEO metadata
    @Valid
    private SeoMetaRequestDto seoMeta;
}