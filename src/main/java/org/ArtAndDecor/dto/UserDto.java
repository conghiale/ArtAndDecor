package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * User DTO for API requests and responses
 * Contains comprehensive information from USER, USER_PROVIDER, USER_ROLE tables
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    // USER table fields
    private Long userId;
    
    private Boolean userEnabled;
    
    @NotBlank(message = "Username is required")
    @Size(max = 64, message = "Username must not exceed 64 characters")
    private String userName;
    
    @Size(max = 150, message = "Password must not exceed 150 characters")
    private String password;
    
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @Pattern(regexp = "^(\\+84|0)(3[2-9]|5[689]|7[06-9]|8[1-689]|9[0-46-9])[0-9]{7}$", 
             message = "Invalid phone number format")
    private String phoneNumber;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Size(max = 150, message = "Image avatar name must not exceed 150 characters")
    private String imageAvatarName;
    
    private String socialMedia;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // =============================================
    // NESTED DTOs (complete related entity data)
    // =============================================
    private UserProviderDto userProvider;
    private UserRoleDto userRole;
    
    // Computed fields
    private String fullName;
    private Integer totalOrders;
    private Integer totalReviews;
    private Integer totalCartItems;

    /**
     * Get full name from first and last name
     * @return Full name or empty string
     */
    public String getFullNameValue() {
        if (firstName == null && lastName == null) {
            return "";
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    /**
     * Check if user has admin role
     * @return true if user is admin
     */
    public boolean isAdmin() {
        return userRole != null && 
               "ADMIN".equalsIgnoreCase(userRole.getUserRoleName());
    }

    /**
     * Check if user has customer role
     * @return true if user is customer
     */
    public boolean isCustomer() {
        return userRole != null && 
               "CUSTOMER".equalsIgnoreCase(userRole.getUserRoleName());
    }

    /**
     * Check if user uses local authentication
     * @return true if using local auth
     */
    public boolean isLocalAuth() {
        return userProvider != null && 
               "LOCAL".equalsIgnoreCase(userProvider.getUserProviderName());
    }
}