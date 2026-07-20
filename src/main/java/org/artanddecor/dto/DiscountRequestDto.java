package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Discount DTO for API requests and responses
 * Contains comprehensive information from DISCOUNT table with related entities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRequestDto {
    
    private Long discountId;
    
    @NotBlank(message = "Mã giảm giá là bắt buộc")
    @Size(max = 100, message = "Mã giảm giá không được vượt quá 100 ký tự")
    private String discountCode;
    
    @NotBlank(message = "Tên mã giảm giá là bắt buộc")
    @Size(max = 64, message = "Tên mã giảm giá không được vượt quá 64 ký tự")
    private String discountName;
    
    @NotNull(message = "Giá trị giảm giá là bắt buộc")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá trị giảm giá phải lớn hơn 0")
    private BigDecimal discountValue;
    
    @NotNull(message = "Số tiền giảm giá tối đa là bắt buộc")
    @DecimalMin(value = "0.0", message = "Số tiền giảm giá tối đa không được là số âm")
    private BigDecimal maxDiscountAmount;
    
    @NotNull(message = "Giá trị đơn hàng tối thiểu là bắt buộc")
    @DecimalMin(value = "0.0", message = "Giá trị đơn hàng tối thiểu không được là số âm")
    private BigDecimal minOrderAmount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Ngày bắt đầu là bắt buộc")
    private LocalDateTime startAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Ngày kết thúc là bắt buộc")
    private LocalDateTime endAt;

    @Min(value = 1, message = "Giới hạn sử dụng phải lớn hơn hoặc bằng 1")
    private Integer totalUsageLimit;
    
    @Min(value = 0, message = "Số lần đã dùng không được là số âm")
    private Integer usedCount;
    
    @NotNull(message = "Trạng thái kích hoạt là bắt buộc")
    private Boolean isActive;
    
    @Size(max = 256, message = "Tên hiển thị không được vượt quá 256 ký tự")
    private String discountDisplayName;

    @Size(max = 256, message = "Ghi chú giảm giá không được vượt quá 256 ký tự")
    private String discountRemark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Nested DTO for related entity
    @NotNull(message = "Loại giảm giá là bắt buộc")
    private Long discountTypeId;

    /**
     * Check if discount is expired
     */
    public boolean isExpired() {
        return endAt != null && LocalDateTime.now().isAfter(endAt);
    }
    
    /**
     * Check if discount usage is exhausted
     */
    public boolean isUsageExhausted() {
        return totalUsageLimit != null && usedCount >= totalUsageLimit;
    }
}