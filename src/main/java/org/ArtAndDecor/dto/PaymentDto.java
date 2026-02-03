package org.ArtAndDecor.dto;

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
    
    @NotBlank(message = "Payment slug is required")
    @Size(max = 64, message = "Payment slug must not exceed 64 characters")
    private String paymentSlug;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", message = "Amount must not be negative")
    private BigDecimal amount;
    
    @NotBlank(message = "Transaction ID is required")
    @Size(max = 100, message = "Transaction ID must not exceed 100 characters")
    private String transactionId;
    
    @Size(max = 256, message = "English remark must not exceed 256 characters")
    private String paymentRemarkEn;
    
    @Size(max = 256, message = "Remark must not exceed 256 characters")
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
    private SeoMetaDto seoMeta;
    
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