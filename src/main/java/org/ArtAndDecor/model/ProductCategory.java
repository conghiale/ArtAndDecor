package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ProductCategory Entity
 * Represents product categories (tranh-treo-tuong, dung-cu-ve, etc.)
 */
@Entity
@Table(name = "PRODUCT_CATEGORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {
    
    private static final Logger logger = LogManager.getLogger(ProductCategory.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_CATEGORY_ID")
    private Long productCategoryId;

    @Column(name = "PRODUCT_CATEGORY_SLUG", nullable = false, unique = true, length = 64)
    private String productCategorySlug;

    @Column(name = "PRODUCT_CATEGORY_NAME", nullable = false, unique = true, length = 64)
    private String productCategoryName;

    @Column(name = "PRODUCT_CATEGORY_DISPLAY_NAME", length = 256)
    private String productCategoryDisplayName;

    @Column(name = "PRODUCT_CATEGORY_REMARK", nullable = false, length = 256)
    private String productCategoryRemark;

    @Column(name = "PRODUCT_CATEGORY_ENABLED", nullable = false)
    private Boolean productCategoryEnabled = true;

    @Column(name = "PRODUCT_CATEGORY_VISIBLE", nullable = false)
    private Boolean productCategoryVisible = true;

    @Column(name = "SEO_META_ID")
    private Long seoMetaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEO_META_ID", referencedColumnName = "SEO_META_ID", insertable = false, updatable = false)
    private SeoMeta seoMeta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCT_TYPE_ID", nullable = false)
    private ProductType productType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_CATEGORY_PARENT_ID")
    private ProductCategory parentCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID")
    private Image image;

    @CreationTimestamp
    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @UpdateTimestamp
    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with Product
    @OneToMany(mappedBy = "productCategory", fetch = FetchType.LAZY)
    private List<Product> products;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new ProductCategory: {}", productCategoryName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
        if (this.productCategoryEnabled == null) {
            this.productCategoryEnabled = true;
        }
        if (this.productCategoryVisible == null) {
            this.productCategoryVisible = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating ProductCategory: {}", productCategoryName);
        this.modifiedDt = LocalDateTime.now();
    }
}