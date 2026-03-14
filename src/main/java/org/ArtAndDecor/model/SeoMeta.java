package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * SeoMeta Entity
 * Represents SEO metadata for various content types
 */
@Entity
@Table(name = "SEO_META")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeoMeta {
    
    private static final Logger logger = LoggerFactory.getLogger(SeoMeta.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEO_META_ID")
    private Long seoMetaId;

    @Column(name = "SEO_META_TITLE", nullable = false, length = 150)
    private String seoMetaTitle;

    @Column(name = "SEO_META_DESCRIPTION", nullable = false, length = 500)
    private String seoMetaDescription;

    @Column(name = "SEO_META_KEYWORDS", length = 300)
    private String seoMetaKeywords;

    @Column(name = "SEO_META_INDEX", nullable = false)
    private Boolean seoMetaIndex = true;

    @Column(name = "SEO_META_FOLLOW", nullable = false)
    private Boolean seoMetaFollow = true;

    @Column(name = "SEO_META_CANONICAL_URL", length = 500)
    private String seoMetaCanonicalUrl;

    @Column(name = "SEO_META_IMAGE_NAME", length = 150)
    private String seoMetaImageName;

    @Column(name = "SEO_META_SCHEMA_TYPE", length = 50)
    private String seoMetaSchemaType;

    @Column(name = "SEO_META_CUSTOM_JSON", columnDefinition = "JSON")
    private String seoMetaCustomJson;

    @Column(name = "SEO_META_ENABLED", nullable = false)
    private Boolean seoMetaEnabled = true;

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
        if (this.seoMetaIndex == null) {
            this.seoMetaIndex = true;
        }
        if (this.seoMetaFollow == null) {
            this.seoMetaFollow = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating SeoMeta: {}", seoMetaTitle);
        this.modifiedDt = LocalDateTime.now();
    }
}