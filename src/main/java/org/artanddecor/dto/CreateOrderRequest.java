package org.artanddecor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for create order (Admin/Manager)
 * Refactored to remove DISCOUNT functionality as per requirements
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotNull(message = "ID khách hàng là bắt buộc")
    private Long customerId;
    
    @NotNull(message = "ID địa chỉ giao hàng là bắt buộc")
    private Long shippingAddressId;
    
    @NotEmpty(message = "Danh sách sản phẩm đặt hàng là bắt buộc")
    @Valid
    private List<CreateOrderItemRequest> orderItems;
    
    private String orderNote; // Optional order note
}