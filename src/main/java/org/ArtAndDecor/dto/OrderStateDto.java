package org.ArtAndDecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * OrderState DTO for API requests and responses
 * Auxiliary class containing only ORDER_STATE table data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStateDto {
    
    private Long orderStateId;
    
    @NotBlank(message = "Order state name is required")
    @Size(max = 50, message = "Order state name must not exceed 50 characters")
    private String orderStateName;
    
    private String remark;
    
    @NotNull(message = "Order state enabled flag is required")
    private Boolean orderStateEnabled;
    
    // Additional information for reporting
    private Long orderCount;
    
    /**
     * Check if this is a pending order state
     */
    public boolean isPendingState() {
        return "PENDING".equalsIgnoreCase(orderStateName) ||
               "CREATED".equalsIgnoreCase(orderStateName);
    }
    
    /**
     * Check if this is a confirmed order state
     */
    public boolean isConfirmedState() {
        return "CONFIRMED".equalsIgnoreCase(orderStateName);
    }
    
    /**
     * Check if this is a processing order state
     */
    public boolean isProcessingState() {
        return "PROCESSING".equalsIgnoreCase(orderStateName) ||
               "PREPARING".equalsIgnoreCase(orderStateName);
    }
    
    /**
     * Check if this is a shipped order state
     */
    public boolean isShippedState() {
        return "SHIPPED".equalsIgnoreCase(orderStateName) ||
               "IN_TRANSIT".equalsIgnoreCase(orderStateName);
    }
    
    /**
     * Check if this is a delivered order state
     */
    public boolean isDeliveredState() {
        return "DELIVERED".equalsIgnoreCase(orderStateName) ||
               "COMPLETED".equalsIgnoreCase(orderStateName);
    }
    
    /**
     * Check if this is a cancelled order state
     */
    public boolean isCancelledState() {
        return "CANCELLED".equalsIgnoreCase(orderStateName) ||
               "CANCELED".equalsIgnoreCase(orderStateName);
    }
    
    /**
     * Check if this is a refunded order state
     */
    public boolean isRefundedState() {
        return "REFUNDED".equalsIgnoreCase(orderStateName);
    }
    
    /**
     * Check if this is a final state (cannot be changed)
     */
    public boolean isFinalState() {
        return isDeliveredState() || isCancelledState() || isRefundedState();
    }
}