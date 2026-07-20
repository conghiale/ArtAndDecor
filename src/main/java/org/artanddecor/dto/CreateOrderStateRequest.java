package org.artanddecor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating and updating order state
 * Used for both create and update operations for consistency
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderStateRequest {
    
    @NotBlank(message = "Tên trạng thái đơn hàng là bắt buộc")
    @Size(max = 64, message = "Tên trạng thái đơn hàng không được vượt quá 64 ký tự")
    private String orderStateName;
    
    @Size(max = 256, message = "Tên hiển thị trạng thái đơn hàng không được vượt quá 256 ký tự")
    private String orderStateDisplayName;
    
    @NotBlank(message = "Ghi chú trạng thái đơn hàng là bắt buộc")
    @Size(max = 256, message = "Ghi chú trạng thái đơn hàng không được vượt quá 256 ký tự")
    private String orderStateRemark;
    
    @NotNull(message = "Cờ kích hoạt trạng thái đơn hàng là bắt buộc")
    private Boolean orderStateEnabled;
}