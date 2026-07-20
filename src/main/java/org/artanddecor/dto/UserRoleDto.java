package org.artanddecor.dto;

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
    
    @NotBlank(message = "Tên vai trò người dùng là bắt buộc")
    @Size(max = 64, message = "Tên vai trò người dùng không được vượt quá 64 ký tự")
    private String userRoleName;
    
    @Size(max = 256, message = "Tên hiển thị vai trò người dùng không được vượt quá 256 ký tự")
    private String userRoleDisplayName;
    
    @NotBlank(message = "Ghi chú vai trò người dùng là bắt buộc")
    @Size(max = 256, message = "Ghi chú vai trò người dùng không được vượt quá 256 ký tự")
    private String userRoleRemark;
    
    @NotNull(message = "Cờ kích hoạt vai trò người dùng là bắt buộc")
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