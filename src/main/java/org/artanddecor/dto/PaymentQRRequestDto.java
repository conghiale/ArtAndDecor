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
    
    @NotBlank(message = "Order code is required")
    @Size(max = 64, message = "Order code must not exceed 64 characters")
    private String orderCode;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1000", message = "Amount must be at least 1,000 VND")
    @DecimalMax(value = "999999999", message = "Amount must not exceed 999,999,999 VND")
    private BigDecimal amount;
}