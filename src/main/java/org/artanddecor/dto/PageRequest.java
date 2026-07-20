package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * Page Request DTO for creating/updating pages
 * Contains only the fields required for page operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    
    @NotNull(message = "ID vị trí trang là bắt buộc")
    @Positive(message = "ID vị trí trang phải lớn hơn 0")
    private Long pagePositionId;
    
    @NotNull(message = "ID nhóm trang là bắt buộc")
    @Positive(message = "ID nhóm trang phải lớn hơn 0")
    private Long pageGroupId;
    
    @Size(max = 256, message = "URL đích không được vượt quá 256 ký tự")
    private String targetUrl;
    
    @Size(max = 64, message = "Slug trang không được vượt quá 64 ký tự")
    private String pageSlug;
    
    @NotBlank(message = "Tên trang là bắt buộc")
    @Size(max = 100, message = "Tên trang không được vượt quá 100 ký tự")
    private String pageName;
    
    private String pageContent;
    
    private Boolean pageEnabled;
    
    @Size(max = 256, message = "Tên hiển thị trang không được vượt quá 256 ký tự")
    private String pageDisplayName;
    
    @NotBlank(message = "Ghi chú trang là bắt buộc")
    @Size(max = 256, message = "Ghi chú trang không được vượt quá 256 ký tự")
    private String pageRemark;
}