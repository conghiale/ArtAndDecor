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
 * CartItem DTO for API requests and responses
 * Contains information from CART_ITEM, CART_ITEM_STATE, PRODUCT tables
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    
    private Long cartItemId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
    private BigDecimal totalPrice;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // =============================================
    // NESTED DTOs (complete related entity data)
    // =============================================
    private CartDto cart;
    private ProductDto product;
    private CartItemStateDto cartItemState;
    
    // Computed fields
    private Boolean isAvailable;
    private Boolean isPriceChanged;
    private BigDecimal priceDifference;
    
    /**
     * Constructor for essential cart item fields
     * @param cart Cart object
     * @param product Product object  
     * @param quantity Item quantity
     * @param unitPrice Unit price
     */
    public CartItemDto(CartDto cart, ProductDto product, Integer quantity, BigDecimal unitPrice) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice != null && quantity != null ? 
                         unitPrice.multiply(new BigDecimal(quantity)) : BigDecimal.ZERO;
    }
    
    /**
     * Check if cart item is active
     */
    public boolean isActive() {
        return cartItemState != null && "ACTIVE".equalsIgnoreCase(cartItemState.getCartItemStateName());
    }
    
    /**
     * Check if cart item is ordered
     */
    public boolean isOrdered() {
        return cartItemState != null && "ORDERED".equalsIgnoreCase(cartItemState.getCartItemStateName());
    }
    
    /**
     * Calculate updated total price with current product price
     */
    public BigDecimal getUpdatedTotalPrice() {
        if (product != null && product.getProductPrice() != null && quantity != null) {
            return product.getProductPrice().multiply(new BigDecimal(quantity));
        }
        return totalPrice;
    }
}