package org.ArtAndDecor.dto;

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
    
    @NotBlank(message = "Policy name is required")
    @Size(max = 64, message = "Policy name must not exceed 64 characters")
    private String policyName;
    
    @Size(max = 64, message = "Policy slug must not exceed 64 characters")
    private String policySlug;
    
    @NotBlank(message = "Policy value is required")
    private String policyValue;
    
    @Size(max = 256, message = "English remark must not exceed 256 characters")
    private String policyRemarkEn;
    
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String policyRemark;
    
    private Boolean policyEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;

}
