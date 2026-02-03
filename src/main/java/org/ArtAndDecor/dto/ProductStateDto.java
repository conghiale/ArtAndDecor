package org.ArtAndDecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * ProductState DTO for API requests and responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStateDto {
    
    private Long productStateId;
    
    @NotBlank(message = "Product state name is required")
    @Size(max = 50, message = "Product state name must not exceed 50 characters")
    private String productStateName;
    
    private String remark;
    
    @NotNull(message = "Product state enabled flag is required")
    private Boolean productStateEnabled;
    
    // Additional information for reporting
    private Long productCount;
    
    /**
     * Check if this is an available state
     */
    public boolean isAvailableState() {
        return "AVAILABLE".equalsIgnoreCase(productStateName);
    }
    
    /**
     * Check if this is an out of stock state
     */
    public boolean isOutOfStockState() {
        return "OUT_OF_STOCK".equalsIgnoreCase(productStateName);
    }
    
    /**
     * Check if this is a discontinued state
     */
    public boolean isDiscontinuedState() {
        return "DISCONTINUED".equalsIgnoreCase(productStateName);
    }
}