package org.ArtAndDecor.dto;

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
    
    @NotBlank(message = "Payment method name is required")
    @Size(max = 64, message = "Payment method name must not exceed 64 characters")
    private String paymentMethodName;
    
    @Size(max = 256, message = "Payment method display name must not exceed 256 characters")
    private String paymentMethodDisplayName;
    
    @NotBlank(message = "Payment method remark is required")
    @Size(max = 256, message = "Payment method remark must not exceed 256 characters")
    private String paymentMethodRemark;
    
    @NotNull(message = "Payment method enabled flag is required")
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
