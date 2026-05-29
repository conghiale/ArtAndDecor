package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BANNER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Banner {

    private static final Logger logger = LoggerFactory.getLogger(Banner.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BANNER_ID")
    private Long bannerId;

    @Column(name = "BANNER_TITLE", length = 256, nullable = false)
    private String bannerTitle;

    @Column(name = "BANNER_LINK", length = 512)
    private String bannerLink;

    @Column(name = "BANNER_ENABLED", nullable = false)
    private Boolean bannerEnabled = true;

    @Column(name = "BANNER_DISPLAY_ORDER")
    private Integer bannerDisplayOrder;

    @OneToMany(mappedBy = "banner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("bannerImageDisplayOrder ASC")
    private List<BannerImage> bannerImages = new ArrayList<>();

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new Banner: {}", bannerTitle);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating Banner: {}", bannerTitle);
        this.modifiedDt = LocalDateTime.now();
    }
}
