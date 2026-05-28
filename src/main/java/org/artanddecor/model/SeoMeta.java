package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * SeoMeta Entity
 * Represents SEO metadata for various content types
 */
@Entity
@Table(name = "SEO_META")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeoMeta {
    
    private static final Logger logger = LoggerFactory.getLogger(SeoMeta.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEO_META_ID")
    private Long seoMetaId;

    @Column(name = "SEO_META_TITLE", length = 150)
    private String seoMetaTitle;

    @Column(name = "SEO_META_DESCRIPTION", length = 500)
    private String seoMetaDescription;

    @Column(name = "SEO_META_KEYWORDS", length = 300)
    private String seoMetaKeywords;

    @Column(name = "SEO_META_INDEX")
    private Boolean seoMetaIndex;

    @Column(name = "SEO_META_FOLLOW")
    private Boolean seoMetaFollow;

    @Column(name = "SEO_META_CANONICAL_URL", length = 500)
    private String seoMetaCanonicalUrl;

    @Column(name = "SEO_META_IMAGE_NAME", length = 150)
    private String seoMetaImageName;

    @Column(name = "SEO_META_SCHEMA_TYPE", length = 50)
    private String seoMetaSchemaType;

    @Column(name = "SEO_META_CUSTOM_JSON", columnDefinition = "JSON")
    private String seoMetaCustomJson;

    @Column(name = "SEO_META_ENABLED")
    private Boolean seoMetaEnabled;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new SeoMeta: {}", seoMetaTitle);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating SeoMeta: {}", seoMetaTitle);
        this.modifiedDt = LocalDateTime.now();
    }
}