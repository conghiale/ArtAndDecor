package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ProductAttribute Entity  
 * Represents master attribute definitions with pricing (e.g., Size: "40x60cm" - 1,500,000 VND)
 * No longer tied to specific products - serves as a master catalog of available attributes
 */
@Entity
@Table(name = "PRODUCT_ATTRIBUTE",
       uniqueConstraints = @UniqueConstraint(columnNames = {"PRODUCT_ATTR_ID", "PRODUCT_ATTRIBUTE_VALUE", "PRODUCT_ATTRIBUTE_PRICE"}))
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
    @JoinColumn(name = "PRODUCT_ATTR_ID", nullable = false)
    private ProductAttr productAttr;

    @Column(name = "PRODUCT_ATTRIBUTE_VALUE", nullable = false, length = 256)
    private String productAttributeValue;

    @Column(name = "PRODUCT_ATTRIBUTE_DISPLAY_NAME", nullable = true, length = 256)
    private String productAttributeDisplayName;

    @Column(name = "PRODUCT_ATTRIBUTE_PRICE", nullable = true, precision = 15, scale = 2)
    private BigDecimal productAttributePrice;

    @Column(name = "PRODUCT_ATTRIBUTE_ENABLED", nullable = false)
    private Boolean productAttributeEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new ProductAttribute: attr ID: {}, value: {}, display name: {}, price: {}", 
                    productAttr != null ? productAttr.getProductAttrId() : null,
                    productAttributeValue, productAttributeDisplayName, productAttributePrice);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating ProductAttribute ID: {}, value: {}, display name: {}", 
                    productAttributeId, productAttributeValue, productAttributeDisplayName);
        this.modifiedDt = LocalDateTime.now();
    }
}