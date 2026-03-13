package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAGE_GROUP")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageGroup {

    private static final Logger logger = LogManager.getLogger(PageGroup.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAGE_GROUP_ID")
    private Long pageGroupId;

    @Column(name = "PAGE_GROUP_SLUG", length = 64, nullable = false, unique = true)
    private String pageGroupSlug;

    @Column(name = "PAGE_GROUP_NAME", length = 100, nullable = false)
    private String pageGroupName;

    @Column(name = "PAGE_GROUP_ENABLED", nullable = false)
    private Boolean pageGroupEnabled = true;

    @Column(name = "PAGE_GROUP_DISPLAY_NAME", length = 256)
    private String pageGroupDisplayName;

    @Column(name = "PAGE_GROUP_REMARK", length = 256, nullable = false)
    private String pageGroupRemark;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new PageGroup: {}", pageGroupName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating PageGroup: {}", pageGroupName);
        this.modifiedDt = LocalDateTime.now();
    }
}
