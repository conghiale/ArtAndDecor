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
 * ProductType Entity
 * Represents product types (IMAGE, DECOR, TOOLS)
 */
@Entity
@Table(name = "PRODUCT_TYPE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductType {
    
    private static final Logger logger = LogManager.getLogger(ProductType.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_TYPE_ID")
    private Long productTypeId;

    @Column(name = "PRODUCT_TYPE_SLUG", nullable = false, unique = true, length = 64)
    private String productTypeSlug;

    @Column(name = "PRODUCT_TYPE_NAME", nullable = false, unique = true, length = 64)
    private String productTypeName;

    @Column(name = "PRODUCT_TYPE_REMARK_EN", length = 256)
    private String productTypeRemarkEn;

    @Column(name = "PRODUCT_TYPE_REMARK", nullable = false, length = 256)
    private String productTypeRemark;

    @Column(name = "PRODUCT_TYPE_ENABLED", nullable = false)
    private Boolean productTypeEnabled = true;

    @Column(name = "PRODUCT_TYPE_DISPLAY", nullable = false)
    private Boolean productTypeDisplay = true;

    @Column(name = "SEO_META_ID")
    private Long seoMetaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEO_META_ID", referencedColumnName = "SEO_META_ID", insertable = false, updatable = false)
    private SeoMeta seoMeta;

    @Column(name = "PRODUCT_TYPE_IMAGE_NAME", length = 256)
    private String productTypeImageName;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with Product
    @OneToMany(mappedBy = "productType", fetch = FetchType.LAZY)
    private List<Product> products;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new ProductType: {}", productTypeName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
        if (this.productTypeEnabled == null) {
            this.productTypeEnabled = true;
        }
        if (this.productTypeDisplay == null) {
            this.productTypeDisplay = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating ProductType: {}", productTypeName);
        this.modifiedDt = LocalDateTime.now();
    }
}