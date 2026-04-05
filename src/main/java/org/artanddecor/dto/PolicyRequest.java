package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * Policy Request DTO for creating new policy
 * Contains only the fields required for policy creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRequest {
    
    @NotBlank(message = "Policy name is required")
    @Size(max = 64, message = "Policy name must not exceed 64 characters")
    private String policyName;
    
    @Size(max = 64, message = "Policy slug must not exceed 64 characters")
    private String policySlug;
    
    @NotBlank(message = "Policy value is required")
    private String policyValue;
    
    @Size(max = 256, message = "Policy display name must not exceed 256 characters")
    private String policyDisplayName;
    
    @NotBlank(message = "Policy remark is required")
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String policyRemark;
    
    private Boolean policyEnabled;
    
}