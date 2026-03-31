package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "BLOG_CATEGORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogCategory {

    private static final Logger logger = LoggerFactory.getLogger(BlogCategory.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BLOG_CATEGORY_ID")
    private Long blogCategoryId;

    @Column(name = "BLOG_CATEGORY_SLUG", length = 64, nullable = false, unique = true)
    private String blogCategorySlug;

    @Column(name = "BLOG_CATEGORY_NAME", length = 64, nullable = false, unique = true)
    private String blogCategoryName;

    @Column(name = "BLOG_CATEGORY_DISPLAY_NAME", length = 256)
    private String blogCategoryDisplayName;

    @Column(name = "BLOG_CATEGORY_REMARK", length = 256, nullable = false)
    private String blogCategoryRemark;

    @Column(name = "BLOG_CATEGORY_ENABLED", nullable = false)
    private Boolean blogCategoryEnabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BLOG_TYPE_ID")
    private BlogType blogType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID")
    private Image image;

    @Column(name = "SEO_META_ID")
    private Long seoMetaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEO_META_ID", referencedColumnName = "SEO_META_ID", insertable = false, updatable = false)
    private SeoMeta seoMeta;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with Blog
    @OneToMany(mappedBy = "blogCategory", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Blog> blogs;

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
