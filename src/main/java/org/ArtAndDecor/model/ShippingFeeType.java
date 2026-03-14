package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ShippingFeeType Entity
 * Represents types of shipping fee calculation (PERCENTAGE, FIXED_AMOUNT, FREE_SHIPPING)
 */
@Entity
@Table(name = "SHIPPING_FEE_TYPE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFeeType {
    
    private static final Logger logger = LoggerFactory.getLogger(ShippingFeeType.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SHIPPING_FEE_TYPE_ID")
    private Long shippingFeeTypeId;

    @Column(name = "SHIPPING_FEE_TYPE_NAME", nullable = false, unique = true, length = 64)
    private String shippingFeeTypeName;

    @Column(name = "SHIPPING_FEE_TYPE_DISPLAY_NAME", length = 256)
    private String shippingFeeTypeDisplayName;

    @Column(name = "SHIPPING_FEE_TYPE_REMARK", nullable = false, length = 256)
    private String shippingFeeTypeRemark;

    @Column(name = "SHIPPING_FEE_TYPE_ENABLED", nullable = false)
    private Boolean shippingFeeTypeEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with ShippingFee
    @OneToMany(mappedBy = "shippingFeeType", fetch = FetchType.LAZY)
    private List<ShippingFee> shippingFees;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new ShippingFeeType: {}", shippingFeeTypeName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating ShippingFeeType: {}", shippingFeeTypeName);
        this.modifiedDt = LocalDateTime.now();
    }
}