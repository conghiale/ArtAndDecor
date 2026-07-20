package org.artanddecor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    
    @NotBlank(message = "Tên đăng nhập là bắt buộc")
    @Size(min = 3, max = 64, message = "Tên đăng nhập phải có từ 3 đến 64 ký tự")
    private String userName;
    
    @NotBlank(message = "Mật khẩu là bắt buộc")
    @Size(min = 8, max = 150, message = "Mật khẩu phải có từ 8 đến 150 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "Mật khẩu phải chứa ít nhất một chữ hoa, một chữ thường và một chữ số")
    private String password;
    
    @NotBlank(message = "Tên là bắt buộc")
    @Size(max = 50, message = "Tên không được vượt quá 50 ký tự")
    private String firstName;
    
    @Size(max = 50, message = "Họ không được vượt quá 50 ký tự")
    private String lastName;
    
    @NotBlank(message = "Email là bắt buộc")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;
    
    @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự")
    private String phoneNumber;
    
    @Size(max = 150, message = "Tên ảnh đại diện không được vượt quá 150 ký tự")
    private String imageAvatarName;
    
    private String socialMedia;
    
    // Provider will default to LOCAL if not specified
    private Long userProviderId;
    
    // Role will default to CUSTOMER if not specified
    private Long userRoleId;
}