package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment DTO for API requests and responses
 * Contains PAYMENT table data with nested related entities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    
    private Long paymentId;
    
    @NotBlank(message = "Slug thanh toán là bắt buộc")
    @Size(max = 64, message = "Slug thanh toán không được vượt quá 64 ký tự")
    private String paymentSlug;
    
    @NotNull(message = "Số tiền là bắt buộc")
    @DecimalMin(value = "0.0", message = "Số tiền không được là số âm")
    private BigDecimal amount;
    
    @NotBlank(message = "Mã giao dịch là bắt buộc")
    @Size(max = 100, message = "Mã giao dịch không được vượt quá 100 ký tự")
    private String transactionId;
    
    @NotBlank(message = "Ghi chú thanh toán là bắt buộc")
    @Size(max = 256, message = "Ghi chú thanh toán không được vượt quá 256 ký tự")
    private String paymentRemark;
    
    private Boolean paymentEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Nested related entities following clean architecture
    private OrderDto order;
    private PaymentMethodDto paymentMethod;
    private PaymentStateDto paymentState;
    
    /**
     * Check if payment is pending
     */
    public boolean isPending() {
        return paymentState != null && "PENDING".equalsIgnoreCase(paymentState.getPaymentStateName());
    }
    
    /**
     * Check if payment is completed
     */
    public boolean isCompleted() {
        return paymentState != null && "COMPLETED".equalsIgnoreCase(paymentState.getPaymentStateName());
    }
    
    /**
     * Check if payment failed
     */
    public boolean isFailed() {
        return paymentState != null && "FAILED".equalsIgnoreCase(paymentState.getPaymentStateName());
    }
    
    /**
     * Check if payment is refunded
     */
    public boolean isRefunded() {
        return paymentState != null && "REFUNDED".equalsIgnoreCase(paymentState.getPaymentStateName());
    }
}