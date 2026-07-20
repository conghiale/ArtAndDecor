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
    @NotBlank(message = "Tên sản phẩm là bắt buộc")
    @Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự")
    private String productName;
    
    @NotBlank(message = "Mã sản phẩm là bắt buộc")
    @Size(max = 64, message = "Mã sản phẩm không được vượt quá 64 ký tự")
    private String productCode;
    
    @NotBlank(message = "Tên danh mục sản phẩm là bắt buộc")
    @Size(max = 100, message = "Tên danh mục sản phẩm không được vượt quá 100 ký tự")
    private String productCategoryName;
    
    @NotBlank(message = "Tên loại sản phẩm là bắt buộc")
    @Size(max = 100, message = "Tên loại sản phẩm không được vượt quá 100 ký tự")
    private String productTypeName;
    
    private String productAttrJson;
    
    @NotNull(message = "Đơn giá là bắt buộc")
    @DecimalMin(value = "0.0", message = "Đơn giá không được là số âm")
    @Digits(integer = 13, fraction = 2, message = "Định dạng đơn giá không hợp lệ")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Số lượng là bắt buộc")
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
    private Integer quantity;
    
    @NotNull(message = "Tổng tiền là bắt buộc")
    @DecimalMin(value = "0.0", message = "Tổng tiền không được là số âm")
    @Digits(integer = 13, fraction = 2, message = "Định dạng tổng tiền không hợp lệ")
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
        return "Sản phẩm không xác định (x" + (quantity != null ? quantity : 0) + ")";
    }
    
    /**
     * Check if there are savings (difference between calculated and actual total)
     * @return true if there are savings
     */
    public boolean hasSavings() {
        return savings != null && savings.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Check if this order item has product attributes
     * @return true if has attributes JSON, false otherwise
     */
    public boolean hasAttributes() {
        return productAttrJson != null && !productAttrJson.trim().isEmpty() && 
               !productAttrJson.equals("null") && !productAttrJson.equals("{}");
    }
    
    /**
     * Get formatted attributes display text from JSON
     * @return Formatted attribute text or default message
     */
    public String getFormattedAttributes() {
        if (!hasAttributes()) {
            return "Chưa chọn thuộc tính nào";
        }
        
        // For now, return simplified JSON content
        // In a full implementation, this would parse JSON and format nicely
        return productAttrJson.length() > 100 ? 
               productAttrJson.substring(0, 97) + "..." : 
               productAttrJson;
    }
}