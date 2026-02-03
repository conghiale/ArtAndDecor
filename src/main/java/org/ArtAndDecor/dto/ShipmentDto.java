package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Shipment DTO for API requests and responses
 * Contains SHIPMENT table data with nested related entities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDto {
    
    private Long shipmentId;
    
    @NotBlank(message = "Shipment slug is required")
    @Size(max = 64, message = "Shipment slug must not exceed 64 characters")
    private String shipmentSlug;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(\\+84|0)(3[2-9]|5[689]|7[06-9]|8[1-689]|9[0-46-9])[0-9]{7}$", 
             message = "Invalid phone number format")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    @NotBlank(message = "Address is required")
    @Size(max = 256, message = "Address must not exceed 256 characters")
    private String address;
    
    @Size(max = 256, message = "English remark must not exceed 256 characters")
    private String shipmentRemarkEn;
    
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String shipmentRemark;
    
    private Boolean shipmentEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shippedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveredAt;
    
    // Nested related entities following clean architecture
    private OrderDto order;
    private ShippingFeeDto shippingFee;
    private ShipmentStateDto shipmentState;
    private SeoMetaDto seoMeta;
    
    /**
     * Check if shipment is being prepared
     */
    public boolean isPreparing() {
        return shipmentState != null && "PREPARING".equalsIgnoreCase(shipmentState.getShipmentStateName());
    }
    
    /**
     * Check if shipment is shipped
     */
    public boolean isShipped() {
        return shipmentState != null && "SHIPPED".equalsIgnoreCase(shipmentState.getShipmentStateName());
    }
    
    /**
     * Check if shipment is in transit
     */
    public boolean isInTransit() {
        return shipmentState != null && "IN_TRANSIT".equalsIgnoreCase(shipmentState.getShipmentStateName());
    }
    
    /**
     * Check if shipment is delivered
     */
    public boolean isDelivered() {
        return shipmentState != null && "DELIVERED".equalsIgnoreCase(shipmentState.getShipmentStateName());
    }
    
    /**
     * Check if delivery failed
     */
    public boolean isDeliveryFailed() {
        return shipmentState != null && "FAILED_DELIVERY".equalsIgnoreCase(shipmentState.getShipmentStateName());
    }
    
    /**
     * Get delivery duration in days
     */
    public Long getDeliveryDurationInDays() {
        if (shippedAt != null && deliveredAt != null) {
            return java.time.Duration.between(shippedAt, deliveredAt).toDays();
        }
        return null;
    }
}