package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ShippingFee Entity
 * Represents shipping fee configuration based on order amount ranges
 */
@Entity
@Table(name = "SHIPPING_FEE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFee {
    
    private static final Logger logger = LoggerFactory.getLogger(ShippingFee.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SHIPPING_FEE_ID")
    private Long shippingFeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPPING_FEE_TYPE_ID", nullable = false)
    private ShippingFeeType shippingFeeType;

    @Column(name = "MIN_ORDER_PRICE", nullable = false, precision = 15, scale = 2)
    private BigDecimal minOrderPrice;

    @Column(name = "MAX_ORDER_PRICE", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxOrderPrice;

    @Column(name = "SHIPPING_FEE_VALUE", nullable = false, precision = 15, scale = 2)
    private BigDecimal shippingFeeValue;

    @Column(name = "SHIPPING_FEE_DISPLAY_NAME", length = 256)
    private String shippingFeeDisplayName;

    @Column(name = "SHIPPING_FEE_REMARK", nullable = false, length = 256)
    private String shippingFeeRemark;

    @Column(name = "SHIPPING_FEE_ENABLED", nullable = false)
    private Boolean shippingFeeEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new ShippingFee with value: {}", shippingFeeValue);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating ShippingFee ID: {}", shippingFeeId);
        this.modifiedDt = LocalDateTime.now();
    }
}