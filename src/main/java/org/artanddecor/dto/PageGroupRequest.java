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
    
    @Size(max = 64, message = "Page group slug không được vượt quá 64 ký tự")
    private String pageGroupSlug;
    
    @NotBlank(message = "Page group name là bắt buộc")
    @Size(max = 100, message = "Page group name không được vượt quá 100 ký tự")
    private String pageGroupName;
    
    private Boolean pageGroupEnabled;
    
    @Size(max = 256, message = "Page group display name không được vượt quá 256 ký tự")
    private String pageGroupDisplayName;
    
    @NotBlank(message = "Page group remark là bắt buộc")
    @Size(max = 256, message = "Page group remark không được vượt quá 256 ký tự")
    private String pageGroupRemark;
}