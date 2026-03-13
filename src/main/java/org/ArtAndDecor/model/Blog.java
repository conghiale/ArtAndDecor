package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

@Entity
@Table(name = "BLOG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blog {

    private static final Logger logger = LogManager.getLogger(Blog.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BLOG_ID")
    private Long blogId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BLOG_CATEGORY_ID", nullable = false)
    private BlogCategory blogCategory;

    @Column(name = "BLOG_TITLE", length = 256, nullable = false)
    private String blogTitle;

    @Column(name = "BLOG_SLUG", length = 64, nullable = false, unique = true)
    private String blogSlug;

    @Column(name = "BLOG_CONTENT", nullable = false, columnDefinition = "LONGTEXT")
    private String blogContent;

    @Column(name = "BLOG_ENABLED", nullable = false)
    private Boolean blogEnabled = true;

    @Column(name = "BLOG_REMARK", length = 256, nullable = false)
    private String blogRemark;

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
        logger.debug("Creating new Blog: {}", blogTitle);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating Blog ID: {}", blogId);
        this.modifiedDt = LocalDateTime.now();
    }
}