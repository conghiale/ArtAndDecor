package org.artanddecor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication request DTO for login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    
    @NotBlank(message = "Tên đăng nhập hoặc email là bắt buộc")
    private String usernameOrEmail;
    
    @NotBlank(message = "Mật khẩu là bắt buộc")
    private String password;
}