package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "BLOG_TYPE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogType {

    private static final Logger logger = LoggerFactory.getLogger(BlogType.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BLOG_TYPE_ID")
    private Long blogTypeId;

    @Column(name = "BLOG_TYPE_SLUG", length = 64, nullable = false, unique = true)
    private String blogTypeSlug;

    @Column(name = "BLOG_TYPE_NAME", length = 64, nullable = false, unique = true)
    private String blogTypeName;

    @Column(name = "BLOG_TYPE_DISPLAY_NAME", length = 256)
    private String blogTypeDisplayName;

    @Column(name = "BLOG_TYPE_REMARK", length = 256, nullable = false)
    private String blogTypeRemark;

    @Column(name = "BLOG_TYPE_ENABLED", nullable = false)
    private Boolean blogTypeEnabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID")
    private Image image;

    @Column(name = "SEO_META_ID")
    private Long seoMetaId;

    // Many-to-One relationship with SeoMeta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEO_META_ID", referencedColumnName = "SEO_META_ID", insertable = false, updatable = false)
    private SeoMeta seoMeta;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // Relationship: One BlogType to Many BlogCategory
    @OneToMany(mappedBy = "blogType", fetch = FetchType.LAZY)
    private List<BlogCategory> blogCategories;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new BlogType: {}", blogTypeName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating BlogType: {}", blogTypeName);
        this.modifiedDt = LocalDateTime.now();
    }
}