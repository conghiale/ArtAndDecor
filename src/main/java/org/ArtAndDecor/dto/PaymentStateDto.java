package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * PaymentState DTO for API requests and responses
 * Contains information from PAYMENT_STATE table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStateDto {
    
    private Long paymentStateId;
    
    @NotBlank(message = "Payment state name is required")
    @Size(max = 64, message = "Payment state name must not exceed 64 characters")
    private String paymentStateName;
    
    @Size(max = 256, message = "English remark must not exceed 256 characters")
    private String paymentStateRemarkEn;
    
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String paymentStateRemark;
    
    @NotNull(message = "Payment state enabled flag is required")
    private Boolean paymentStateEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
}
