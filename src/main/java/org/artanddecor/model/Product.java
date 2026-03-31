package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Product Entity
 * Represents products in the system
 */
@Entity
@Table(name = "PRODUCT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    private static final Logger logger = LoggerFactory.getLogger(Product.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "PRODUCT_NAME", nullable = false, unique = true, length = 100)
    private String productName;

    @Column(name = "PRODUCT_SLUG", nullable = false, unique = true, length = 64)
    private String productSlug;

    @Column(name = "PRODUCT_CODE", nullable = false, unique = true, length = 64)
    private String productCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_CATEGORY_ID", nullable = false)
    private ProductCategory productCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_STATE_ID", nullable = false)
    private ProductState productState;

    @Column(name = "SOLD_QUANTITY", nullable = false)
    private Integer soldQuantity = 0;

    @Column(name = "STOCK_QUANTITY", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "PRODUCT_DESCRIPTION", nullable = false, columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "PRODUCT_PRICE", nullable = false, precision = 15, scale = 2)
    private BigDecimal productPrice;

    @Column(name = "PRODUCT_ENABLED", nullable = false)
    private Boolean productEnabled = true;

    @Column(name = "PRODUCT_FEATURED", nullable = false)
    private Boolean productFeatured = false;

    @Column(name = "PRODUCT_HIGHLIGHTED", nullable = false)
    private Boolean productHighlighted = false;

    @Column(name = "SEO_META_ID")
    private Long seoMetaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEO_META_ID", referencedColumnName = "SEO_META_ID", insertable = false, updatable = false)
    private SeoMeta seoMeta;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationships
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ProductAttribute> productAttributes;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Review> reviews;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<CartItem> cartItems;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new Product: {}", productName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
        if (this.stockQuantity == null) {
            this.stockQuantity = 0;
        }
        if (this.soldQuantity == null) {
            this.soldQuantity = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating Product: {}", productName);
        this.modifiedDt = LocalDateTime.now();
    }

    /**
     * Check if product is in stock
     * @return true if stock quantity > 0
     */
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    /**
     * Calculate total value of available stock
     * @return total value
     */
    public BigDecimal getTotalStockValue() {
        if (stockQuantity == null || productPrice == null) {
            return BigDecimal.ZERO;
        }
        return productPrice.multiply(new BigDecimal(stockQuantity));
    }
}