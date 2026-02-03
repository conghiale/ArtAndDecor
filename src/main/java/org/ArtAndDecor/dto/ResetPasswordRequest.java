package org.ArtAndDecor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Request DTO for admin password reset operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    // Getters and Setters
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 150, message = "Password must be between 8 and 150 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
    private String newPassword;
    
    private Boolean forceChangeOnLogin = true; // Default to true for security

}