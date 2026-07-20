package org.artanddecor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Request DTO for forgot-password step 2: verify the OTP.
 * {@code identifier} accepts either a USERNAME or an EMAIL address.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {

    @NotBlank(message = "Tên đăng nhập hoặc email là bắt buộc")
    private String identifier;

    @NotBlank(message = "Mã OTP là bắt buộc")
    @Size(min = 6, max = 6, message = "Mã OTP phải gồm đúng 6 chữ số")
    private String otpCode;
}
