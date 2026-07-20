package org.artanddecor.dto;

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
    
    @NotBlank(message = "Tên trạng thái giỏ hàng là bắt buộc")
    @Size(max = 64, message = "Tên trạng thái giỏ hàng không được vượt quá 64 ký tự")
    private String cartStateName;
    
    @Size(max = 256, message = "Tên hiển thị không được vượt quá 256 ký tự")
    private String cartStateDisplayName;
    
    @NotBlank(message = "Ghi chú trạng thái giỏ hàng là bắt buộc")
    @Size(max = 256, message = "Ghi chú không được vượt quá 256 ký tự")
    private String cartStateRemark;
    
    @NotNull(message = "Cờ kích hoạt trạng thái giỏ hàng là bắt buộc")
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