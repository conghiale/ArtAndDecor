package org.artanddecor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating order status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    
    @NotNull(message = "New order status ID is required")
    private Long newOrderStateId;
    
    private String statusNote;  // Optional note for status change
    
    // For tracking who made the change
    private Long changedByUserId;
    
    /**
     * Check if status note is provided
     * @return true if note is not empty
     */
    public boolean hasStatusNote() {
        return statusNote != null && !statusNote.trim().isEmpty();
    }
}