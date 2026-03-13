package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DiscountType Entity
 * Represents discount types (PERCENTAGE, FIXED_AMOUNT)
 */
@Entity
@Table(name = "DISCOUNT_TYPE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountType {
    
    private static final Logger logger = LogManager.getLogger(DiscountType.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISCOUNT_TYPE_ID")
    private Long discountTypeId;

    @Column(name = "DISCOUNT_TYPE_NAME", nullable = false, unique = true, length = 64)
    private String discountTypeName;

    @Column(name = "DISCOUNT_TYPE_DISPLAY_NAME", length = 256)
    private String discountTypeDisplayName;

    @Column(name = "DISCOUNT_TYPE_REMARK", nullable = false, length = 256)
    private String discountTypeRemark;

    @Column(name = "DISCOUNT_TYPE_ENABLED", nullable = false)
    private Boolean discountTypeEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with Discount
    @OneToMany(mappedBy = "discountType", fetch = FetchType.LAZY)
    private List<Discount> discounts;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new DiscountType: {}", discountTypeName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating DiscountType: {}", discountTypeName);
        this.modifiedDt = LocalDateTime.now();
    }
}