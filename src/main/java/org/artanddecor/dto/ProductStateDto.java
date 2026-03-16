package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

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
    @Size(max = 64, message = "Product state name must not exceed 64 characters")
    private String productStateName;
    
    @NotNull(message = "Product state enabled flag is required")
    private Boolean productStateEnabled;

    @Size(max = 256, message = "Product state display name must not exceed 256 characters")
    private String productStateDisplayName;

    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String productStateRemark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
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