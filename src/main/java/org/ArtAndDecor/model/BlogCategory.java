package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

@Entity
@Table(name = "BLOG_CATEGORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogCategory {

    private static final Logger logger = LogManager.getLogger(BlogCategory.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BLOG_CATEGORY_ID")
    private Long blogCategoryId;

    @Column(name = "BLOG_CATEGORY_SLUG", length = 64, nullable = false, unique = true)
    private String blogCategorySlug;

    @Column(name = "BLOG_CATEGORY_NAME", length = 64, nullable = false, unique = true)
    private String blogCategoryName;

    @Column(name = "BLOG_CATEGORY_REMARK_EN", length = 256)
    private String blogCategoryRemarkEn;

    @Column(name = "BLOG_CATEGORY_REMARK", length = 256, nullable = false)
    private String blogCategoryRemark;

    @Column(name = "BLOG_CATEGORY_ENABLED", nullable = false)
    private Boolean blogCategoryEnabled = true;

    @Column(name = "SEO_META_ID")
    private Long seoMetaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEO_META_ID", referencedColumnName = "SEO_META_ID", insertable = false, updatable = false)
    private SeoMeta seoMeta;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new BlogCategory: {}", blogCategoryName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating BlogCategory: {}", blogCategoryName);
        this.modifiedDt = LocalDateTime.now();
    }
}
