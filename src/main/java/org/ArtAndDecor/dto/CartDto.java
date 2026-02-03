package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Cart DTO for API requests and responses
 * Contains information from CART, CART_STATE, USER tables and cart items
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    
    private Long cartId;
    
    private String sessionId;
    
    @NotBlank(message = "Cart slug is required")
    @Size(max = 64, message = "Cart slug must not exceed 64 characters")
    private String cartSlug;
    
    @NotNull(message = "Total amount is required")
    @Min(value = 0, message = "Total amount must not be negative")
    private Integer totalAmount;
    
    @Size(max = 256, message = "English remark must not exceed 256 characters")
    private String cartRemarkEn;
    
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String cartRemark;
    
    private Boolean cartEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;

    // NESTED DTOs (complete related entity data)
    private CartStateDto cartState;
    private UserDto user;
    private SeoMetaDto seoMeta;
    
    // Cart items
    private List<CartItemDto> cartItems;
    
    // Computed fields
    private Integer totalItemCount;
    private Integer uniqueProductCount;
    private Boolean isEmpty;
    
    /**
     * Generate full name from user's first and last name
     * @return Full name or empty string if no user
     */
    public String generateFullName() {
        if (user == null) {
            return "";
        }
        return user.getFullNameValue();
    }
    
    /**
     * Calculate total item count
     */
    public void calculateTotals() {
        if (cartItems != null && !cartItems.isEmpty()) {
            this.totalItemCount = cartItems.stream()
                .mapToInt(CartItemDto::getQuantity)
                .sum();
            
            BigDecimal totalBigDecimal = cartItems.stream()
                .map(CartItemDto::getTotalPrice)
                .filter(price -> price != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.totalAmount = totalBigDecimal.intValue();
                
            this.uniqueProductCount = cartItems.size();
            this.isEmpty = false;
        } else {
            this.totalItemCount = 0;
            this.totalAmount = 0;
            this.uniqueProductCount = 0;
            this.isEmpty = true;
        }
    }
    
    /**
     * Check if cart is active
     */
    public boolean isActive() {
        return cartState != null && 
               "ACTIVE".equalsIgnoreCase(cartState.getCartStateName());
    }
    
    /**
     * Check if cart is checked out
     */
    public boolean isCheckedOut() {
        return cartState != null && (
               "CHECKED_OUT".equalsIgnoreCase(cartState.getCartStateName()) ||
               "CHECKED_OUT_PART".equalsIgnoreCase(cartState.getCartStateName()));
    }
}