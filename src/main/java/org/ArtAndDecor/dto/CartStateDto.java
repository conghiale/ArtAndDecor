package org.ArtAndDecor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * CartState DTO for API requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartStateDto {
    
    private Long cartStateId;
    
    @NotBlank(message = "Cart state name is required")
    @Size(max = 50, message = "Cart state name must not exceed 50 characters")
    private String cartStateName;
    
    private String remark;
    
    @NotNull(message = "Cart state enabled flag is required")
    private Boolean cartStateEnabled;
    
    // Additional information for reporting
    private Long cartCount;
    
    /**
     * Check if this is an active cart state
     */
    public boolean isActiveCartState() {
        return "ACTIVE".equalsIgnoreCase(cartStateName) ||
               "IN_PROGRESS".equalsIgnoreCase(cartStateName);
    }
    
    /**
     * Check if this is an abandoned cart state
     */
    public boolean isAbandonedCartState() {
        return "ABANDONED".equalsIgnoreCase(cartStateName);
    }
    
    /**
     * Check if this is a completed cart state
     */
    public boolean isCompletedCartState() {
        return "COMPLETED".equalsIgnoreCase(cartStateName) ||
               "ORDERED".equalsIgnoreCase(cartStateName);
    }
    
    /**
     * Check if this is an expired cart state
     */
    public boolean isExpiredCartState() {
        return "EXPIRED".equalsIgnoreCase(cartStateName);
    }
}