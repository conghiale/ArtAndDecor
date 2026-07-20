package org.artanddecor.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating order state
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStateRequest {
    
    @NotNull(message = "Trạng thái mới là bắt buộc")
    private Long newState;
    
    @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
    private String remarks;
}