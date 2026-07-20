package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * PaymentMethod DTO for API requests and responses
 * Contains information from PAYMENT_METHOD table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDto {
    
    private Long paymentMethodId;
    
    @NotBlank(message = "Tên phương thức thanh toán là bắt buộc")
    @Size(max = 64, message = "Tên phương thức thanh toán không được vượt quá 64 ký tự")
    private String paymentMethodName;
    
    @Size(max = 256, message = "Tên hiển thị phương thức thanh toán không được vượt quá 256 ký tự")
    private String paymentMethodDisplayName;
    
    @NotBlank(message = "Ghi chú phương thức thanh toán là bắt buộc")
    @Size(max = 256, message = "Ghi chú phương thức thanh toán không được vượt quá 256 ký tự")
    private String paymentMethodRemark;
    
    @NotNull(message = "Cờ kích hoạt phương thức thanh toán là bắt buộc")
    private Boolean paymentMethodEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Constructor for essential fields
    public PaymentMethodDto(String paymentMethodName, String paymentMethodRemark) {
        this.paymentMethodName = paymentMethodName;
        this.paymentMethodRemark = paymentMethodRemark;
    }
}
