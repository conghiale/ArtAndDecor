package org.artanddecor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Request DTO for forgot-password step 3: reset password after OTP is verified.
 * OTP is re-validated server-side for security — client must resend the same code.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordWithOtpRequest {

    @NotBlank(message = "Tên đăng nhập hoặc email là bắt buộc")
    private String identifier;

    @NotBlank(message = "Mã OTP là bắt buộc")
    @Size(min = 6, max = 6, message = "Mã OTP phải gồm đúng 6 chữ số")
    private String otpCode;

    @NotBlank(message = "Mật khẩu mới là bắt buộc")
    @Size(min = 8, max = 150, message = "Mật khẩu phải có từ 8 đến 150 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
             message = "Mật khẩu phải chứa ít nhất một chữ hoa, một chữ thường và một chữ số")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu là bắt buộc")
    private String confirmPassword;

    /**
     * Validate that new password and confirm password match.
     */
    public boolean isPasswordsMatch() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}
