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
    
    @NotNull(message = "New state is required")
    private Long newState;
    
    @Size(max = 1000, message = "Remarks must not exceed 1000 characters")
    private String remarks;
}