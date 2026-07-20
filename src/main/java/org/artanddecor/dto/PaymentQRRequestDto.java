package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Payment QR Request DTO
 * Contains data needed to generate payment QR code
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentQRRequestDto {
    
    @NotBlank(message = "Mã đơn hàng là bắt buộc")
    @Size(max = 64, message = "Mã đơn hàng không được vượt quá 64 ký tự")
    private String orderCode;
    
    @NotNull(message = "Số tiền là bắt buộc")
    @DecimalMin(value = "1000", message = "Số tiền phải từ 1.000 VND trở lên")
    @DecimalMax(value = "999999999", message = "Số tiền không được vượt quá 999.999.999 VND")
    private BigDecimal amount;
}