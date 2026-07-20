package org.artanddecor.dto;

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
    @NotBlank(message = "Mật khẩu mới là bắt buộc")
    @Size(min = 8, max = 150, message = "Mật khẩu phải có từ 8 đến 150 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "Mật khẩu phải chứa ít nhất một chữ hoa, một chữ thường và một chữ số")
    private String newPassword;
    
    private Boolean forceChangeOnLogin = true; // Default to true for security

}