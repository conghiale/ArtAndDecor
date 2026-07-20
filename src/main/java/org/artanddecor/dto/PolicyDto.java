package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Policy DTO for API requests and responses
 * Contains information from POLICY table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDto {
    
    private Long policyId;
    
    @NotBlank(message = "Tên chính sách là bắt buộc")
    @Size(max = 64, message = "Tên chính sách không được vượt quá 64 ký tự")
    private String policyName;
    
    @Size(max = 64, message = "Slug chính sách không được vượt quá 64 ký tự")
    private String policySlug;
    
    @NotBlank(message = "Giá trị chính sách là bắt buộc")
    private String policyValue;
    
    @Size(max = 256, message = "Tên hiển thị chính sách không được vượt quá 256 ký tự")
    private String policyDisplayName;
    
    @NotBlank(message = "Ghi chú chính sách là bắt buộc")
    @Size(max = 256, message = "Ghi chú chính sách không được vượt quá 256 ký tự")
    private String policyRemark;
    
    private Boolean policyEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;

}
