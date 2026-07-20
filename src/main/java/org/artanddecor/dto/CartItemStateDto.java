package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * CartItemState DTO for API requests and responses
 * Auxiliary class - contains only CART_ITEM_STATE table data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemStateDto {
    
    private Long cartItemStateId;
    
    @NotBlank(message = "Tên trạng thái mục giỏ hàng là bắt buộc")
    @Size(max = 64, message = "Tên trạng thái mục giỏ hàng không được vượt quá 64 ký tự")
    private String cartItemStateName;
    
    @Size(max = 256, message = "Tên hiển thị không được vượt quá 256 ký tự")
    private String cartItemStateDisplayName;
    
    @NotBlank(message = "Ghi chú trạng thái mục giỏ hàng là bắt buộc")
    @Size(max = 256, message = "Ghi chú không được vượt quá 256 ký tự")
    private String cartItemStateRemark;
    
    @NotNull(message = "Cờ kích hoạt trạng thái mục giỏ hàng là bắt buộc")
    private Boolean cartItemStateEnabled;
    
    // Additional information for reporting
    private Long cartItemCount;
    
    /**
     * Check if this is an active cart item state
     */
    public boolean isActiveCartItemState() {
        return "ACTIVE".equalsIgnoreCase(cartItemStateName);
    }
    
    /**
     * Check if this is an ordered cart item state
     */
    public boolean isOrderedCartItemState() {
        return "ORDERED".equalsIgnoreCase(cartItemStateName);
    }
    
    /**
     * Check if this is a removed cart item state
     */
    public boolean isRemovedCartItemState() {
        return "REMOVED".equalsIgnoreCase(cartItemStateName);
    }
}