package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OrderItem DTO for API requests and responses
 * Contains information from ORDER_ITEM table with nested related entities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    
    private Long orderItemId;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price must not be negative")
    @Digits(integer = 13, fraction = 2, message = "Invalid unit price format")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer orderItemQuantity;
    
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.0", message = "Total price must not be negative")
    @Digits(integer = 13, fraction = 2, message = "Invalid total price format")
    private BigDecimal orderItemTotalPrice;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Nested related entities following clean architecture
    private OrderDto order;
    private ProductDto product;
    
    // Computed fields
    private BigDecimal savings; // If there's a discount
    private String displayName;
    
    /**
     * Calculate total price based on unit price and quantity
     * @return Calculated total
     */
    public BigDecimal calculateTotal() {
        if (unitPrice != null && orderItemQuantity != null) {
            return unitPrice.multiply(BigDecimal.valueOf(orderItemQuantity));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Get display name for this order item
     * @return Product name or default text
     */
    public String getDisplayNameValue() {
        if (product != null && product.getProductName() != null && !product.getProductName().isEmpty()) {
            return product.getProductName() + " (x" + orderItemQuantity + ")";
        }
        return "Unknown Product (x" + (orderItemQuantity != null ? orderItemQuantity : 0) + ")";
    }
    
    /**
     * Check if there are savings (difference between calculated and actual total)
     * @return true if there are savings
     */
    public boolean hasSavings() {
        return savings != null && savings.compareTo(BigDecimal.ZERO) > 0;
    }
}