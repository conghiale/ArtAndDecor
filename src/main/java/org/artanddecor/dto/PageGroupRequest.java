package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * PageGroup Request DTO for creating/updating page groups
 * Contains only the fields required for page group operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageGroupRequest {
    
    @Size(max = 64, message = "Page group slug must not exceed 64 characters")
    private String pageGroupSlug;
    
    @NotBlank(message = "Page group name is required")
    @Size(max = 100, message = "Page group name must not exceed 100 characters")
    private String pageGroupName;
    
    private Boolean pageGroupEnabled;
    
    @Size(max = 256, message = "Page group display name must not exceed 256 characters")
    private String pageGroupDisplayName;
    
    @NotBlank(message = "Page group remark is required")
    @Size(max = 256, message = "Page group remark must not exceed 256 characters")
    private String pageGroupRemark;
}