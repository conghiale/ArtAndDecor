package org.ArtAndDecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * UserRole DTO for API requests and responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDto {
    
    private Long userRoleId;
    
    @NotBlank(message = "User role name is required")
    @Size(max = 64, message = "User role name must not exceed 64 characters")
    private String userRoleName;
    
    @Size(max = 256, message = "User role display name must not exceed 256 characters")
    private String userRoleDisplayName;
    
    @NotBlank(message = "User role remark is required")
    @Size(max = 256, message = "User role remark must not exceed 256 characters")
    private String userRoleRemark;
    
    @NotNull(message = "User role enabled flag is required")
    private Boolean userRoleEnabled;
    
    // Additional information for reporting
    private Long userCount;
    
    /**
     * Get display text for the role
     */
    public String getDisplayText() {
        return userRoleDisplayName != null ? userRoleDisplayName : userRoleName;
    }
    
    /**
     * Check if this is an admin role
     */
    public boolean isAdminRole() {
        return "ADMIN".equalsIgnoreCase(userRoleName) || 
               "ADMINISTRATOR".equalsIgnoreCase(userRoleName);
    }
    
    /**
     * Check if this is a customer role
     */
    public boolean isCustomerRole() {
        return "CUSTOMER".equalsIgnoreCase(userRoleName) || 
               "USER".equalsIgnoreCase(userRoleName);
    }
    
    /**
     * Check if this is a staff role
     */
    public boolean isStaffRole() {
        return "STAFF".equalsIgnoreCase(userRoleName) || 
               "EMPLOYEE".equalsIgnoreCase(userRoleName);
    }
}