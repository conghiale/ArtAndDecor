package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * ProductImage Entity
 * Represents images associated with products
 */
@Entity
@Table(name = "PRODUCT_IMAGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductImage.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_IMAGE_ID")
    private Long productImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID", nullable = false)
    private Image image;

    @Column(name = "PRODUCT_IMAGE_PRIMARY", nullable = false)
    private Boolean productImagePrimary = false;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new ProductImage for product ID: {}, image ID: {}", 
                    product != null ? product.getProductId() : null,
                    image != null ? image.getImageId() : null);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating ProductImage ID: {}", productImageId);
        this.modifiedDt = LocalDateTime.now();
    }
}