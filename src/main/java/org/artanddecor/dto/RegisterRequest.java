package org.artanddecor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User registration request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 64, message = "Username must be between 3 and 64 characters")
    private String userName;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 150, message = "Password must be between 6 and 150 characters")
    private String password;
    
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    private String phoneNumber;
    
    @Size(max = 150, message = "Avatar name must not exceed 150 characters")
    private String imageAvatarName;
    
    private String socialMedia;
    
    // Provider will default to LOCAL if not specified
    private Long userProviderId;
    
    // Role will default to CUSTOMER if not specified
    private Long userRoleId;
}