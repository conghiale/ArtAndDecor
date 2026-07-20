package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

/**
 * Common Request DTO for BlogType Create and Update operations
 * Contains only necessary fields for both operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogTypeRequest {
    
    @NotBlank(message = "Slug loại blog là bắt buộc")
    @Size(max = 64, message = "Slug loại blog không được vượt quá 64 ký tự")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug loại blog chỉ được chứa chữ thường, số và dấu gạch nối")
    private String blogTypeSlug;
    
    @NotBlank(message = "Tên loại blog là bắt buộc")
    @Size(max = 64, message = "Tên loại blog không được vượt quá 64 ký tự")
    private String blogTypeName;
    
    @Size(max = 256, message = "Tên hiển thị loại blog không được vượt quá 256 ký tự")
    private String blogTypeDisplayName;
    
    @NotBlank(message = "Ghi chú loại blog là bắt buộc")
    @Size(max = 256, message = "Ghi chú không được vượt quá 256 ký tự")
    private String blogTypeRemark;
    
    @NotNull(message = "Trạng thái bật loại blog là bắt buộc")
    private Boolean blogTypeEnabled;
    
    // Foreign key IDs only (not nested DTOs)
    @Positive(message = "ID ảnh phải lớn hơn 0")
    private Long imageId;
    
    // SEO Meta information for creating SEO metadata
    @Valid
    private SeoMetaRequestDto seoMeta;
}