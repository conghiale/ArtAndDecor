package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Entity
@Table(name = "BANNER_IMAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannerImage {

    private static final Logger logger = LoggerFactory.getLogger(BannerImage.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BANNER_IMAGE_ID")
    private Long bannerImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANNER_ID", nullable = false)
    private Banner banner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID", nullable = false)
    private Image image;

    @Column(name = "BANNER_IMAGE_DISPLAY_ORDER", nullable = false)
    private Integer bannerImageDisplayOrder = 0;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating BannerImage for banner ID: {}, image ID: {}",
                banner != null ? banner.getBannerId() : null,
                image != null ? image.getImageId() : null);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating BannerImage ID: {}", bannerImageId);
        this.modifiedDt = LocalDateTime.now();
    }
}
