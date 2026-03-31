package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * ProductAttribute Entity  
 * Represents attributes of products (size, color, material, etc.)
 */
@Entity
@Table(name = "PRODUCT_ATTRIBUTE",
       uniqueConstraints = @UniqueConstraint(columnNames = {"PRODUCT_ID", "PRODUCT_ATTR_ID", "PRODUCT_ATTRIBUTE_VALUE"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttribute {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductAttribute.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ATTRIBUTE_ID")
    private Long productAttributeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ATTR_ID", nullable = false)
    private ProductAttr productAttr;

    @Column(name = "PRODUCT_ATTRIBUTE_VALUE", nullable = false, length = 256)
    private String productAttributeValue;

    @Column(name = "PRODUCT_ATTRIBUTE_QUANTITY", nullable = false)
    private Integer productAttributeQuantity = 0;

    @Column(name = "PRODUCT_ATTRIBUTE_ENABLED", nullable = false)
    private Boolean productAttributeEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new ProductAttribute for product ID: {}, attr ID: {}", 
                    product != null ? product.getProductId() : null,
                    productAttr != null ? productAttr.getProductAttrId() : null);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating ProductAttribute ID: {}", productAttributeId);
        this.modifiedDt = LocalDateTime.now();
    }
}