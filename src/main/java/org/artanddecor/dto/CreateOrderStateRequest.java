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
    
    @NotBlank(message = "Order state name is required")
    @Size(max = 64, message = "Order state name must not exceed 64 characters")
    private String orderStateName;
    
    @Size(max = 256, message = "Order state display name must not exceed 256 characters")
    private String orderStateDisplayName;
    
    @NotBlank(message = "Order state remark is required")
    @Size(max = 256, message = "Order state remark must not exceed 256 characters")
    private String orderStateRemark;
    
    @NotNull(message = "Order state enabled flag is required")
    private Boolean orderStateEnabled;
}