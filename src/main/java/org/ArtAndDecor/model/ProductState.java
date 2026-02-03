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
 * ProductState Entity
 * Represents product states (ACTIVE, INACTIVE, OUT_OF_STOCK, DISCONTINUED)
 */
@Entity
@Table(name = "PRODUCT_STATE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductState {
    
    private static final Logger logger = LogManager.getLogger(ProductState.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_STATE_ID")
    private Long productStateId;

    @Column(name = "PRODUCT_STATE_NAME", nullable = false, unique = true, length = 64)
    private String productStateName;

    @Column(name = "PRODUCT_STATE_ENABLED", nullable = false)
    private Boolean productStateEnabled = true;

    @Column(name = "PRODUCT_STATE_REMARK_EN", length = 256)
    private String productStateRemarkEn;

    @Column(name = "PRODUCT_STATE_REMARK", nullable = false, length = 256)
    private String productStateRemark;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with Product
    @OneToMany(mappedBy = "productState", fetch = FetchType.LAZY)
    private List<Product> products;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new ProductState: {}", productStateName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
        if (this.productStateEnabled == null) {
            this.productStateEnabled = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating ProductState: {}", productStateName);
        this.modifiedDt = LocalDateTime.now();
    }
}