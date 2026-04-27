package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * ProductVariant Entity  
 * Maps products to specific attributes with stock management
 * Represents the association between a product and its available attribute variations
 */
@Entity
@Table(name = "PRODUCT_VARIANT",
       uniqueConstraints = @UniqueConstraint(columnNames = {"PRODUCT_ID", "PRODUCT_ATTRIBUTE_ID"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductVariant.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_VARIANT_ID")
    private Long productVariantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ATTRIBUTE_ID", nullable = false)
    private ProductAttribute productAttribute;

    @Column(name = "PRODUCT_VARIANT_STOCK", nullable = false)
    private Integer productVariantStock = 0;

    @Column(name = "PRODUCT_VARIANT_ENABLED", nullable = false)
    private Boolean productVariantEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new ProductVariant for product ID: {}, attribute ID: {}, stock: {}", 
                    product != null ? product.getProductId() : null,
                    productAttribute != null ? productAttribute.getProductAttributeId() : null,
                    productVariantStock);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating ProductVariant ID: {}, stock: {}", productVariantId, productVariantStock);
        this.modifiedDt = LocalDateTime.now();
    }
    
    /**
     * Check if variant is available (enabled and has stock)
     */
    public boolean isAvailable() {
        return Boolean.TRUE.equals(productVariantEnabled) && productVariantStock != null && productVariantStock > 0;
    }
    
    /**
     * Check if variant is out of stock
     */
    public boolean isOutOfStock() {
        return productVariantStock == null || productVariantStock <= 0;
    }
}