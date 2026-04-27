package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

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
    @Builder.Default
    private Integer quantity = 1;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Unit price from frontend must not be negative")
    private BigDecimal cartItemUnitPrice;
    
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
    
    // Selected product attributes for this cart item
    @Builder.Default
    private List<CartItemAttributeDto> cartItemAttributes = new ArrayList<>();
    
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
     * Constructor with cart item unit price from frontend
     * @param cart Cart object
     * @param product Product object  
     * @param quantity Item quantity
     * @param cartItemUnitPrice Unit price from frontend
     * @param unitPrice Calculated unit price
     */
    public CartItemDto(CartDto cart, ProductDto product, Integer quantity, BigDecimal cartItemUnitPrice, BigDecimal unitPrice) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.cartItemUnitPrice = cartItemUnitPrice;
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
     * Calculate updated total price using cartItemUnitPrice if available, otherwise current product price
     */
    public BigDecimal getUpdatedTotalPrice() {
        // Priority 1: Use cartItemUnitPrice if set (from frontend calculation)
        if (cartItemUnitPrice != null && cartItemUnitPrice.compareTo(BigDecimal.ZERO) >= 0 && quantity != null) {
            return cartItemUnitPrice.multiply(new BigDecimal(quantity));
        }
        
        // Priority 2: Fallback to current product price (legacy behavior)
        if (product != null && product.getProductPrice() != null && quantity != null) {
            return product.getProductPrice().multiply(new BigDecimal(quantity));
        }
        
        // Priority 3: Use existing totalPrice as last resort
        return totalPrice != null ? totalPrice : BigDecimal.ZERO;
    }
    
    /**
     * Check if this cart item has selected attributes
     * @return true if has attributes, false otherwise
     */
    public boolean hasAttributes() {
        return cartItemAttributes != null && !cartItemAttributes.isEmpty();
    }
    
    /**
     * Get count of selected attributes
     * @return Number of selected attributes
     */
    public int getAttributesCount() {
        return cartItemAttributes != null ? cartItemAttributes.size() : 0;
    }
    
    /**
     * Get formatted attributes display text
     * @return Comma-separated attribute display text
     */
    public String getFormattedAttributes() {
        if (!hasAttributes()) {
            return "No attributes selected";
        }
        
        return cartItemAttributes.stream()
                .map(CartItemAttributeDto::getFormattedDisplay)
                .reduce((a, b) -> a + ", " + b)
                .orElse("No attributes selected");
    }
    
    /**
     * Add a cart item attribute
     * @param attribute Attribute to add
     */
    public void addAttribute(CartItemAttributeDto attribute) {
        if (attribute != null) {
            if (cartItemAttributes == null) {
                cartItemAttributes = new ArrayList<>();
            }
            cartItemAttributes.add(attribute);
        }
    }
}