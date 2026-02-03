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
    @Size(max = 50, message = "User role name must not exceed 50 characters")
    private String userRoleName;
    
    private String userRoleRemark;
    private String userRoleRemarkEn;
    
    @NotNull(message = "User role enabled flag is required")
    private Boolean userRoleEnabled;
    
    // Additional information for reporting
    private Long userCount;
    
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