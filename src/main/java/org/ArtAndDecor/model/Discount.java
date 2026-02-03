package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Discount Entity
 * Represents discount information for orders
 */
@Entity
@Table(name = "DISCOUNT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discount {
    
    private static final Logger logger = LogManager.getLogger(Discount.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISCOUNT_ID")
    private Long discountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISCOUNT_TYPE_ID", nullable = false)
    private DiscountType discountType;

    @Column(name = "DISCOUNT_CODE", nullable = false, unique = true, length = 100)
    private String discountCode;

    @Column(name = "DISCOUNT_NAME", nullable = false, length = 64)
    private String discountName;

    @Column(name = "DISCOUNT_VALUE", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "MAX_DISCOUNT_AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "MIN_ORDER_AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal minOrderAmount;

    @Column(name = "TOTAL_USAGE_LIMIT", nullable = false)
    private Integer totalUsageLimit;

    @Column(name = "USED_COUNT", nullable = false)
    private Integer usedCount = 0;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @Column(name = "START_AT", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "END_AT", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "DISCOUNT_REMARK_EN", length = 256)
    private String discountRemarkEn;

    @Column(name = "DISCOUNT_REMARK", length = 256, nullable = false)
    private String discountRemark;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with Orders
    @OneToMany(mappedBy = "discount", fetch = FetchType.LAZY)
    private List<Orders> orders;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new Discount: {}", discountCode);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
        if (this.usedCount == null) {
            this.usedCount = 0;
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating Discount: {}", discountCode);
        this.modifiedDt = LocalDateTime.now();
    }

    /**
     * Check if discount is currently valid
     * @return true if discount can be used
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && 
               now.isAfter(startAt) && 
               now.isBefore(endAt) &&
               (totalUsageLimit == null || usedCount < totalUsageLimit);
    }

    /**
     * Calculate discount amount for given order value
     * @param orderValue Order value
     * @return Discount amount
     */
    public BigDecimal calculateDiscountAmount(BigDecimal orderValue) {
        if (!isValid() || orderValue == null) {
            return BigDecimal.ZERO;
        }

        if (minOrderAmount != null && orderValue.compareTo(minOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount;
        if ("PERCENTAGE".equals(discountType.getDiscountTypeName())) {
            discountAmount = orderValue.multiply(discountValue).divide(new BigDecimal(100));
        } else {
            discountAmount = discountValue;
        }

        // Apply maximum discount limit
        if (maxDiscountAmount != null && discountAmount.compareTo(maxDiscountAmount) > 0) {
            discountAmount = maxDiscountAmount;
        }

        return discountAmount;
    }
}