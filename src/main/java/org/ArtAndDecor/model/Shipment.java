package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

/**
 * Shipment Entity
 * Represents shipment information for orders
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
    private Orders order;

    @Column(name = "SHIPMENT_SLUG", length = 64, nullable = false, unique = true)
    private String shipmentSlug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPPING_FEE_ID", nullable = false)
    private ShippingFee shippingFee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPMENT_STATE_ID", nullable = false)
    private ShipmentState shipmentState;

    @Column(name = "PHONE", nullable = false, length = 20)
    private String phone;

    @Column(name = "ADDRESS", nullable = false, length = 256)
    private String address;

    @Column(name = "SHIPMENT_REMARK_EN", length = 256)
    private String shipmentRemarkEn;

    @Column(name = "SHIPMENT_REMARK", length = 256, nullable = false)
    private String shipmentRemark;

    @Column(name = "SEO_META_ID")
    private Long seoMetaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEO_META_ID", referencedColumnName = "SEO_META_ID", insertable = false, updatable = false)
    private SeoMeta seoMeta;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new Shipment for order ID: {}", 
                    order != null ? order.getOrderId() : null);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating Shipment ID: {}", shipmentId);
        this.modifiedDt = LocalDateTime.now();
    }
}