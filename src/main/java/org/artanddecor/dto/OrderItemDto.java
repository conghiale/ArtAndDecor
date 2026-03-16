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
 * OrderItem DTO for API requests and responses
 * Contains information from ORDER_ITEM table with nested related entities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    
    private Long orderItemId;
    
    // Foreign key references for easy API usage
    private Long orderId;
    private Long productId;
    
    // Product snapshot information
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String productName;
    
    @NotBlank(message = "Product code is required")
    @Size(max = 64, message = "Product code must not exceed 64 characters")
    private String productCode;
    
    @NotBlank(message = "Product category name is required")
    @Size(max = 100, message = "Product category name must not exceed 100 characters")
    private String productCategoryName;
    
    @NotBlank(message = "Product type name is required")
    @Size(max = 100, message = "Product type name must not exceed 100 characters")
    private String productTypeName;
    
    private String productAttrJson;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price must not be negative")
    @Digits(integer = 13, fraction = 2, message = "Invalid unit price format")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.0", message = "Total price must not be negative")
    @Digits(integer = 13, fraction = 2, message = "Invalid total price format")
    private BigDecimal totalPrice;
    
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
        if (unitPrice != null && quantity != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Get display name for this order item
     * @return Product name or default text
     */
    public String getDisplayNameValue() {
        if (productName != null && !productName.isEmpty()) {
            return productName + " (x" + quantity + ")";
        }
        return "Unknown Product (x" + (quantity != null ? quantity : 0) + ")";
    }
    
    /**
     * Check if there are savings (difference between calculated and actual total)
     * @return true if there are savings
     */
    public boolean hasSavings() {
        return savings != null && savings.compareTo(BigDecimal.ZERO) > 0;
    }
}