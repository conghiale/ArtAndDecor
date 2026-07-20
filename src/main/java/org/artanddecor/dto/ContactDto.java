package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Contact DTO for API requests and responses
 * Contains information from CONTACT table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {
    
    private Long contactId;
    
    @NotBlank(message = "Tên liên hệ là bắt buộc")
    @Size(max = 64, message = "Tên liên hệ không được vượt quá 64 ký tự")
    private String contactName;
    
    @Size(max = 64, message = "Slug liên hệ không được vượt quá 64 ký tự")
    private String contactSlug;
    
    @NotBlank(message = "Địa chỉ liên hệ là bắt buộc")
    @Size(max = 256, message = "Địa chỉ liên hệ không được vượt quá 256 ký tự")
    private String contactAddress;
    
    @NotBlank(message = "Email liên hệ là bắt buộc")
    @Email(message = "Định dạng email không hợp lệ")
    @Size(max = 64, message = "Email liên hệ không được vượt quá 64 ký tự")
    private String contactEmail;
    
    @NotBlank(message = "Số điện thoại liên hệ là bắt buộc")
    @Pattern(regexp = "^(\\+84|0)(3[2-9]|5[689]|7[06-9]|8[1-689]|9[0-46-9])[0-9]{7}$", 
             message = "Định dạng số điện thoại không hợp lệ")
    @Size(max = 15, message = "Số điện thoại liên hệ không được vượt quá 15 ký tự")
    private String contactPhone;
    
    @Size(max = 256, message = "Fanpage liên hệ không được vượt quá 256 ký tự")
    private String contactFanpage;
    
    private Boolean contactEnabled;
    
    @NotBlank(message = "Ghi chú liên hệ là bắt buộc")
    @Size(max = 256, message = "Ghi chú liên hệ không được vượt quá 256 ký tự")
    private String contactRemark;
    
    private Long seoMetaId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // =============================================
    // NESTED DTO (complete related entity data)
    // =============================================
    private SeoMetaDto seoMeta;
    
    // SEO data for response
    private String seoMetaTitle;
    private String seoMetaDescription;
}
