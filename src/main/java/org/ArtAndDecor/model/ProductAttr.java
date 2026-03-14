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
 * ProductAttr Entity
 * Represents product attribute definitions (SIZE, COLOR, MATERIAL, etc.)
 */
@Entity
@Table(name = "PRODUCT_ATTR")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttr {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductAttr.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ATTR_ID")
    private Long productAttrId;

    @Column(name = "PRODUCT_ATTR_NAME", nullable = false, unique = true, length = 64)
    private String productAttrName;

    @Column(name = "PRODUCT_ATTR_ENABLED", nullable = false)
    private Boolean productAttrEnabled = true;

    @Column(name = "PRODUCT_ATTR_DISPLAY_NAME", length = 256)
    private String productAttrDisplayName;

    @Column(name = "PRODUCT_ATTR_REMARK", nullable = false, length = 256)
    private String productAttrRemark;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with ProductAttribute
    @OneToMany(mappedBy = "productAttr", fetch = FetchType.LAZY)
    private List<ProductAttribute> productAttributes;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new ProductAttr: {}", productAttrName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
        if (this.productAttrEnabled == null) {
            this.productAttrEnabled = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating ProductAttr: {}", productAttrName);
        this.modifiedDt = LocalDateTime.now();
    }
}