package org.artanddecor.repository;

import org.artanddecor.model.SeoMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * SeoMeta Repository for essential database operations
 * Only includes methods actually used in the application
 */
@Repository
public interface SeoMetaRepository extends JpaRepository<SeoMeta, Long> {

    // =============================================
    // BASIC FIND OPERATIONS
    // =============================================

    /**
     * Find by SEO title (exact match)
     */
    Optional<SeoMeta> findBySeoMetaTitle(String seoMetaTitle);

    /**
     * Check if title exists
     */
    boolean existsBySeoMetaTitle(String seoMetaTitle);

    /**
     * Find by canonical URL
     */
    Optional<SeoMeta> findBySeoMetaCanonicalUrl(String canonicalUrl);

    /**
     * Check if canonical URL exists
     */
    boolean existsBySeoMetaCanonicalUrl(String canonicalUrl);
}