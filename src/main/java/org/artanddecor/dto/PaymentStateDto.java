package org.artanddecor.dto;

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
    
    @NotBlank(message = "Tên trạng thái thanh toán là bắt buộc")
    @Size(max = 64, message = "Tên trạng thái thanh toán không được vượt quá 64 ký tự")
    private String paymentStateName;
    
    @Size(max = 256, message = "Tên hiển thị trạng thái thanh toán không được vượt quá 256 ký tự")
    private String paymentStateDisplayName;
    
    @NotBlank(message = "Ghi chú trạng thái thanh toán là bắt buộc")
    @Size(max = 256, message = "Ghi chú trạng thái thanh toán không được vượt quá 256 ký tự")
    private String paymentStateRemark;
    
    @NotNull(message = "Cờ kích hoạt trạng thái thanh toán là bắt buộc")
    private Boolean paymentStateEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
}
