package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Image DTO for API requests and responses
 * Contains comprehensive information from IMAGE table with related entities
 * Used for:
 * 1. Returning image information after upload
 * 2. Accepting image metadata in requests
 * 3. Supporting Product API image associations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    
    private Long imageId;
    
    @NotBlank(message = "Tên ảnh là bắt buộc")
    @Size(max = 150, message = "Tên ảnh không được vượt quá 150 ký tự")
    private String imageName;
    
    @NotBlank(message = "Tên hiển thị ảnh là bắt buộc")
    @Size(max = 64, message = "Tên hiển thị ảnh không được vượt quá 64 ký tự")
    private String imageDisplayName;
    
    @NotBlank(message = "Slug ảnh là bắt buộc")
    @Size(max = 64, message = "Slug ảnh không được vượt quá 64 ký tự")
    private String imageSlug;
    
    @NotBlank(message = "Kích thước ảnh là bắt buộc")
    @Size(max = 64, message = "Kích thước ảnh không được vượt quá 64 ký tự")
    private String imageSize;
    
    @Size(max = 10, message = "Định dạng ảnh không được vượt quá 10 ký tự")
    private String imageFormat;
    
    @NotBlank(message = "Đường dẫn tập là bắt buộc")
    @Size(max = 512, message = "Đường dẫn tập không được vượt quá 512 ký tự")
    private String pathFile;
    
    @Size(max = 256, message = "Ghi chú không được vượt quá 256 ký tự")
    private String imageRemark;
    
    private Boolean imageEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;

}
