package org.artanddecor.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating order items in admin create order API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemRequest {
    
    @NotNull(message = "ID sản phẩm là bắt buộc")
    private Long productId;
    
    private Long variantId; // Optional for products with variants
    
    @NotNull(message = "Số lượng là bắt buộc")
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
    private Integer quantity;
}