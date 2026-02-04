package org.ArtAndDecor.dto;

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
    
    @NotBlank(message = "Contact name is required")
    @Size(max = 64, message = "Contact name must not exceed 64 characters")
    private String contactName;
    
    @Size(max = 64, message = "Contact slug must not exceed 64 characters")
    private String contactSlug;
    
    @NotBlank(message = "Contact address is required")
    @Size(max = 256, message = "Contact address must not exceed 256 characters")
    private String contactAddress;
    
    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    @Size(max = 64, message = "Contact email must not exceed 64 characters")
    private String contactEmail;
    
    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = "^(\\+84|0)(3[2-9]|5[689]|7[06-9]|8[1-689]|9[0-46-9])[0-9]{7}$", 
             message = "Invalid phone number format")
    @Size(max = 15, message = "Contact phone must not exceed 15 characters")
    private String contactPhone;
    
    @Size(max = 256, message = "Contact fanpage must not exceed 256 characters")
    private String contactFanpage;
    
    private Boolean contactEnabled;
    
    @Size(max = 256, message = "English remark must not exceed 256 characters")
    private String contactRemarkEn;
    
    @Size(max = 256, message = "Remark must not exceed 256 characters")
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
