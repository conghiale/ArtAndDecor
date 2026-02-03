package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order DTO for API requests and responses
 * Contains comprehensive information from ORDERS, ORDER_STATE, DISCOUNT, USER tables and related data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    
    private Long orderId;
    
    @NotBlank(message = "Order code is required")
    @Size(max = 50, message = "Order code must not exceed 50 characters")
    private String orderCode;
    
    @NotBlank(message = "Order slug is required")
    @Size(max = 64, message = "Order slug must not exceed 64 characters")
    private String orderSlug;
    
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", message = "Total amount must not be negative")
    private BigDecimal totalAmount;
    
    @Size(max = 1000, message = "Note must not exceed 1000 characters")
    private String note;
    
    @Size(max = 256, message = "English remark must not exceed 256 characters")
    private String orderRemarkEn;
    
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String orderRemark;
    
    private Boolean orderEnabled;
    
    @DecimalMin(value = "0.0", message = "Final amount must not be negative")
    private BigDecimal finalAmount;
    
    @Size(max = 500, message = "Shipping address must not exceed 500 characters")
    private String shippingAddress;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // =============================================
    // NESTED DTOs (complete related entity data)
    // =============================================
    private UserDto user;
    private CartDto cart;
    private OrderStateDto orderState;
    private DiscountDto discount;
    private SeoMetaDto seoMeta;
    
    // Related data
    private List<OrderStateHistoryDto> orderStateHistories;
    private List<PaymentDto> payments;
    private List<ShipmentDto> shipments;
    
    // Computed fields
    private Integer totalItems;
    private Boolean hasValidDiscount;
    private String currentOrderStatus;
    private BigDecimal savedAmount;
    
    /**
     * Generate full name from user object
     */
    public String generateFullName() {
        if (user == null) {
            return "Unknown User";
        }
        return user.getFullNameValue();
    }
    
    /**
     * Check if order is pending
     */
    public boolean isPending() {
        return orderState != null && "PENDING".equalsIgnoreCase(orderState.getOrderStateName());
    }
    
    /**
     * Check if order is confirmed
     */
    public boolean isConfirmed() {
        return orderState != null && "CONFIRMED".equalsIgnoreCase(orderState.getOrderStateName());
    }
    
    /**
     * Check if order is processing
     */
    public boolean isProcessing() {
        return orderState != null && "PROCESSING".equalsIgnoreCase(orderState.getOrderStateName());
    }
    
    /**
     * Check if order is shipped
     */
    public boolean isShipped() {
        return orderState != null && "SHIPPED".equalsIgnoreCase(orderState.getOrderStateName());
    }
    
    /**
     * Check if order is delivered
     */
    public boolean isDelivered() {
        return orderState != null && "DELIVERED".equalsIgnoreCase(orderState.getOrderStateName());
    }
    
    /**
     * Check if order is cancelled
     */
    public boolean isCancelled() {
        return orderState != null && "CANCELLED".equalsIgnoreCase(orderState.getOrderStateName());
    }
    
    /**
     * Calculate final amount
     */
    public BigDecimal calculateFinalAmount() {
        BigDecimal total = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        BigDecimal discountAmount = (discount != null && discount.getDiscountValue() != null) ? 
                                   discount.getDiscountValue() : BigDecimal.ZERO;
        
        return total.subtract(discountAmount);
    }
}