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
 * Image Entity
 * Represents images in the system
 */
@Entity
@Table(name = "IMAGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    
    private static final Logger logger = LogManager.getLogger(Image.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_ID")
    private Long imageId;

    @Column(name = "IMAGE_NAME", nullable = false, length = 150)
    private String imageName;

    @Column(name = "IMAGE_DISPLAY_NAME", nullable = false, length = 64)
    private String imageDisplayName;

    @Column(name = "IMAGE_SLUG", nullable = false, unique = true, length = 64)
    private String imageSlug;

    @Column(name = "IMAGE_SIZE", nullable = false, length = 64)
    private String imageSize;

    @Column(name = "IMAGE_FORMAT", nullable = false, length = 10)
    private String imageFormat;

    @Column(name = "IMAGE_REMARK", length = 256)
    private String imageRemark;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with ProductImage
    @OneToMany(mappedBy = "image", fetch = FetchType.LAZY)
    private List<ProductImage> productImages;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new Image: {}", imageName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating Image: {}", imageName);
        this.modifiedDt = LocalDateTime.now();
    }
}