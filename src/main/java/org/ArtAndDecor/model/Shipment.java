package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Shipment Entity
 * Represents shipment information for orders - Updated to match database schema
 */
@Entity
@Table(name = "SHIPMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {
    
    private static final Logger logger = LogManager.getLogger(Shipment.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SHIPMENT_ID")
    private Long shipmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private Order order;

    @Column(name = "SHIPMENT_CODE", length = 64, nullable = false, unique = true)
    private String shipmentCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPMENT_STATE_ID", nullable = false)
    private ShipmentState shipmentState;

    // Receiver information snapshot
    @Column(name = "RECEIVER_NAME", nullable = false, length = 150)
    private String receiverName;

    @Column(name = "RECEIVER_PHONE", nullable = false, length = 20)
    private String receiverPhone;

    @Column(name = "RECEIVER_EMAIL", length = 150)
    private String receiverEmail;

    // Address fields
    @Column(name = "ADDRESS_LINE", nullable = false, length = 255)
    private String addressLine;

    @Column(name = "CITY", nullable = false, length = 100)
    private String city;

    @Column(name = "DISTRICT", length = 100)
    private String district;

    @Column(name = "WARD", length = 100)
    private String ward;

    @Column(name = "COUNTRY", nullable = false, length = 100)
    private String country;

    // Shipping fee snapshot
    @Column(name = "SHIPPING_FEE_AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal shippingFeeAmount = BigDecimal.ZERO;

    // Timeline
    @Column(name = "SHIPPED_AT")
    private LocalDateTime shippedAt;

    @Column(name = "DELIVERED_AT")
    private LocalDateTime deliveredAt;

    @Column(name = "SHIPMENT_REMARK", length = 256)
    private String shipmentRemark;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new Shipment for order ID: {}, code: {}", 
                    order != null ? order.getOrderId() : null, shipmentCode);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating Shipment ID: {}, code: {}", shipmentId, shipmentCode);
        this.modifiedDt = LocalDateTime.now();
    }

    /**
     * Get full formatted address
     * @return Complete address string
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (addressLine != null) sb.append(addressLine);
        if (ward != null) sb.append(", ").append(ward);
        if (district != null) sb.append(", ").append(district);
        if (city != null) sb.append(", ").append(city);
        if (country != null) sb.append(", ").append(country);
        return sb.toString();
    }

    /**
     * Check if shipment has been shipped
     * @return true if shipped
     */
    public boolean isShipped() {
        return shippedAt != null;
    }

    /**
     * Check if shipment has been delivered
     * @return true if delivered
     */
    public boolean isDelivered() {
        return deliveredAt != null;
    }
}