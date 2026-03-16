package org.artanddecor.repository;

import org.artanddecor.model.SeoMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SeoMeta Repository for database operations
 */
@Repository
public interface SeoMetaRepository extends JpaRepository<SeoMeta, Long> {

    // =============================================
    // FIND OPERATIONS BY TITLE AND KEYWORDS
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

    // =============================================
    // SEARCH OPERATIONS
    // =============================================

    /**
     * Full-text search in SEO meta (MySQL FULLTEXT)
     */
    @Query(value = "SELECT * FROM SEO_META sm WHERE " +
                   "MATCH(sm.SEO_META_TITLE, sm.SEO_META_DESCRIPTION, sm.SEO_META_KEYWORDS) " +
                   "AGAINST (?1 IN NATURAL LANGUAGE MODE)", nativeQuery = true)
    List<SeoMeta> fullTextSearch(String searchTerm);

    /**
     * Search by keywords
     */
    @Query("SELECT sm FROM SeoMeta sm WHERE " +
           "LOWER(sm.seoMetaKeywords) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SeoMeta> findByKeyword(@Param("keyword") String keyword);

    /**
     * Search in title and description
     */
    @Query("SELECT sm FROM SeoMeta sm WHERE " +
           "LOWER(sm.seoMetaTitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(sm.seoMetaDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<SeoMeta> findByTitleOrDescription(@Param("searchTerm") String searchTerm);

    // =============================================
    // PAGINATION OPERATIONS
    // =============================================

    /**
     * Find all with pagination (ordered by creation date)
     */
    @Query("SELECT sm FROM SeoMeta sm ORDER BY sm.createdDt DESC")
    Page<SeoMeta> findAllWithPagination(Pageable pageable);

    // =============================================
    // INDEX AND FOLLOW OPERATIONS
    // =============================================

    /**
     * Find SEO meta that are indexable
     */
    @Query("SELECT sm FROM SeoMeta sm WHERE sm.seoMetaIndex = true")
    List<SeoMeta> findIndexableSeoMeta();

    /**
     * Find SEO meta that are followable
     */
    @Query("SELECT sm FROM SeoMeta sm WHERE sm.seoMetaFollow = true")
    List<SeoMeta> findFollowableSeoMeta();

    /**
     * Find SEO meta that are both indexable and followable
     */
    @Query("SELECT sm FROM SeoMeta sm WHERE sm.seoMetaIndex = true AND sm.seoMetaFollow = true")
    List<SeoMeta> findIndexableAndFollowableSeoMeta();

    // =============================================
    // SCHEMA AND STRUCTURED DATA
    // =============================================

    /**
     * Find by schema type
     */
    @Query("SELECT sm FROM SeoMeta sm WHERE sm.seoMetaSchemaType = :schemaType")
    List<SeoMeta> findBySchemaType(@Param("schemaType") String schemaType);

    /**
     * Find SEO meta with custom JSON schema
     */
    @Query("SELECT sm FROM SeoMeta sm WHERE sm.seoMetaCustomJson IS NOT NULL")
    List<SeoMeta> findWithCustomSchema();

    // =============================================
    // ANALYTICS & REPORTING
    // =============================================

    /**
     * Count total SEO meta records
     */
    @Query("SELECT COUNT(sm) FROM SeoMeta sm")
    long countTotalSeoMeta();

    /**
     * Count indexable vs non-indexable
     */
    @Query("SELECT sm.seoMetaIndex, COUNT(sm) FROM SeoMeta sm GROUP BY sm.seoMetaIndex")
    List<Object[]> countByIndexStatus();

    // =============================================
    // MAINTENANCE OPERATIONS
    // =============================================

    /**
     * Find SEO meta by criteria (admin search/filter)
     */
    @Query("SELECT sm FROM SeoMeta sm WHERE " +
           "(:title IS NULL OR LOWER(sm.seoMetaTitle) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:description IS NULL OR LOWER(sm.seoMetaDescription) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
           "(:keywords IS NULL OR LOWER(sm.seoMetaKeywords) LIKE LOWER(CONCAT('%', :keywords, '%'))) AND " +
           "(:schemaType IS NULL OR sm.seoMetaSchemaType = :schemaType)")
    List<SeoMeta> findByCriteria(
            @Param("title") String title,
            @Param("description") String description,
            @Param("keywords") String keywords,
            @Param("categoryId") Long categoryId,
            @Param("schemaType") String schemaType);
}