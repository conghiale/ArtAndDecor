package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAGE_POSITION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagePosition {

    private static final Logger logger = LoggerFactory.getLogger(PagePosition.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAGE_POSITION_ID")
    private Long pagePositionId;

    @Column(name = "PAGE_POSITION_SLUG", length = 64, nullable = false, unique = true)
    private String pagePositionSlug;

    @Column(name = "PAGE_POSITION_NAME", length = 100, nullable = false)
    private String pagePositionName;

    @Column(name = "PAGE_POSITION_ENABLED", nullable = false)
    private Boolean pagePositionEnabled = true;

    @Column(name = "PAGE_POSITION_DISPLAY_NAME", length = 256)
    private String pagePositionDisplayName;

    @Column(name = "PAGE_POSITION_REMARK", length = 256, nullable = false)
    private String pagePositionRemark;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new PagePosition: {}", pagePositionName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating PagePosition: {}", pagePositionName);
        this.modifiedDt = LocalDateTime.now();
    }
}
