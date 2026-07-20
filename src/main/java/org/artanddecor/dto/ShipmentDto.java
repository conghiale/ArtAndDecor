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
    
    @NotBlank(message = "Shipment code là bắt buộc")
    @Size(max = 64, message = "Shipment code không được vượt quá 64 ký tự")
    private String shipmentCode;
    
    // Receiver information snapshot
    @NotBlank(message = "Tên người nhận là bắt buộc")
    @Size(max = 150, message = "Tên người nhận không được vượt quá 150 ký tự")
    private String receiverName;
    
    @NotBlank(message = "Số điện thoại người nhận là bắt buộc")
    @Pattern(regexp = "^(\\+84|0)(3[2-9]|5[689]|7[06-9]|8[1-689]|9[0-46-9])[0-9]{7}$", 
             message = "Định dạng số điện thoại không hợp lệ")
    @Size(max = 20, message = "Số điện thoại người nhận không được vượt quá 20 ký tự")
    private String receiverPhone;
    
    @Email(message = "Định dạng email người nhận không hợp lệ")
    @Size(max = 150, message = "Email người nhận không được vượt quá 150 ký tự")
    private String receiverEmail;
    
    // Address information
    @NotBlank(message = "Địa chỉ chi tiết là bắt buộc")
    @Size(max = 255, message = "Địa chỉ chi tiết không được vượt quá 255 ký tự")
    private String addressLine;
    
    @NotBlank(message = "Thành phố là bắt buộc")
    @Size(max = 100, message = "Thành phố không được vượt quá 100 ký tự")
    private String city;
    
    @Size(max = 100, message = "Phường/Xã không được vượt quá 100 ký tự")
    private String ward;
    
    @NotBlank(message = "Quốc gia là bắt buộc")
    @Size(max = 100, message = "Quốc gia không được vượt quá 100 ký tự")
    private String country;
    
    // Shipping fee snapshot
    @NotNull(message = "Phí vận chuyển là bắt buộc")
    @DecimalMin(value = "0.0", message = "Phí vận chuyển không được là số âm")
    private BigDecimal shippingFeeAmount;
    
    @Size(max = 256, message = "Shipment remark không được vượt quá 256 ký tự")
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