package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page {

    private static final Logger logger = LogManager.getLogger(Page.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAGE_ID")
    private Long pageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PAGE_POSITION_ID", nullable = false)
    private PagePosition pagePosition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PAGE_GROUP_ID", nullable = false)
    private PageGroup pageGroup;

    @Column(name = "TARGET_URL", length = 256)
    private String targetUrl;

    @Column(name = "PAGE_SLUG", length = 64, nullable = false, unique = true)
    private String pageSlug;

    @Column(name = "PAGE_NAME", length = 100, nullable = false)
    private String pageName;

    @Column(name = "PAGE_CONTENT", columnDefinition = "LONGTEXT")
    private String pageContent;

    @Column(name = "PAGE_ENABLED", nullable = false)
    private Boolean pageEnabled = true;

    @Column(name = "PAGE_REMARK_EN", length = 256)
    private String pageRemarkEn;

    @Column(name = "PAGE_REMARK", length = 256, nullable = false)
    private String pageRemark;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new Page: {}", pageName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating Page: {}", pageName);
        this.modifiedDt = LocalDateTime.now();
    }
}
