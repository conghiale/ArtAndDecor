package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
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
    
    // Order reference
    private Long orderId;
    
    @NotBlank(message = "Shipment code is required")
    @Size(max = 64, message = "Shipment code must not exceed 64 characters")
    private String shipmentCode;
    
    // Receiver information snapshot
    @NotBlank(message = "Receiver name is required")
    @Size(max = 150, message = "Receiver name must not exceed 150 characters")
    private String receiverName;
    
    @NotBlank(message = "Receiver phone is required")
    @Pattern(regexp = "^(\\+84|0)(3[2-9]|5[689]|7[06-9]|8[1-689]|9[0-46-9])[0-9]{7}$", 
             message = "Invalid phone number format")
    @Size(max = 20, message = "Receiver phone must not exceed 20 characters")
    private String receiverPhone;
    
    @Email(message = "Invalid receiver email format")
    @Size(max = 150, message = "Receiver email must not exceed 150 characters")
    private String receiverEmail;
    
    // Address information
    @NotBlank(message = "Address line is required")
    @Size(max = 255, message = "Address line must not exceed 255 characters")
    private String addressLine;
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @Size(max = 100, message = "Ward must not exceed 100 characters")
    private String ward;
    
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
    
    // Shipping fee snapshot
    @NotNull(message = "Shipping fee amount is required")
    @DecimalMin(value = "0.0", message = "Shipping fee amount must not be negative")
    private BigDecimal shippingFeeAmount;
    
    @Size(max = 256, message = "Shipment remark must not exceed 256 characters")
    private String shipmentRemark;
    
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
    private ShipmentStateDto shipmentState;
    
    // Computed fields for display
    private String fullAddress;
    
    /**
     * Get full formatted address
     */
    public String getFullAddressValue() {
        StringBuilder sb = new StringBuilder();
        if (addressLine != null) sb.append(addressLine);
        if (ward != null) sb.append(", ").append(ward);
        if (city != null) sb.append(", ").append(city);
        if (country != null) sb.append(", ").append(country);
        return sb.toString();
    }
    
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