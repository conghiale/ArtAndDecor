package org.artanddecor.services;

import org.artanddecor.dto.SeoMetaDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * SeoMeta Service Interface
 * Defines business operations for SeoMeta management
 */
public interface SeoMetaService {

    // =============================================
    // BASIC FIND OPERATIONS
    // =============================================

    /**
     * Find SEO meta by ID
     */
    Optional<SeoMetaDto> findById(Long id);

    /**
     * Find SEO meta by title
     */
    Optional<SeoMetaDto> findByTitle(String title);

    /**
     * Find SEO meta by canonical URL
     */
    Optional<SeoMetaDto> findByCanonicalUrl(String canonicalUrl);

    // =============================================
    // SEARCH OPERATIONS
    // =============================================

    /**
     * Full-text search in SEO meta
     */
    List<SeoMetaDto> fullTextSearch(String searchTerm);

    /**
     * Search by keywords
     */
    List<SeoMetaDto> findByKeyword(String keyword);

    /**
     * Search in title and description
     */
    List<SeoMetaDto> findByTitleOrDescription(String searchTerm);

    // =============================================
    // CATEGORY-BASED OPERATIONS
    // =============================================

    /**
     * Find SEO meta by category ID
     */
    List<SeoMetaDto> findBySeoMetaCategoryId(Long categoryId);

    /**
     * Find SEO meta by category name
     */
    List<SeoMetaDto> findBySeoMetaCategoryName(String categoryName);

    /**
     * Find SEO meta by category with pagination
     */
    Page<SeoMetaDto> findByCategoryWithPagination(Long categoryId, int page, int size);

    // =============================================
    // PAGINATION OPERATIONS
    // =============================================

    /**
     * Get all SEO meta with pagination
     */
    Page<SeoMetaDto> getAllWithPagination(int page, int size);

    // =============================================
    // INDEX AND FOLLOW OPERATIONS
    // =============================================

    /**
     * Find indexable SEO meta
     */
    List<SeoMetaDto> findIndexableSeoMeta();

    /**
     * Find followable SEO meta
     */
    List<SeoMetaDto> findFollowableSeoMeta();

    /**
     * Find indexable and followable SEO meta
     */
    List<SeoMetaDto> findIndexableAndFollowableSeoMeta();

    // =============================================
    // SCHEMA OPERATIONS
    // =============================================

    /**
     * Find SEO meta by schema type
     */
    List<SeoMetaDto> findBySchemaType(String schemaType);

    /**
     * Find SEO meta with custom schema
     */
    List<SeoMetaDto> findWithCustomSchema();

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    /**
     * Create new SEO meta
     */
    SeoMetaDto createSeoMeta(SeoMetaDto seoMetaDto);

    /**
     * Update SEO meta by ID
     */
    SeoMetaDto updateSeoMeta(Long id, SeoMetaDto seoMetaDto);

    /**
     * Delete SEO meta by ID
     */
    void deleteSeoMeta(Long id);

    // =============================================
    // VALIDATION OPERATIONS
    // =============================================

    /**
     * Check if title exists
     */
    boolean existsByTitle(String title);

    /**
     * Check if canonical URL exists
     */
    boolean existsByCanonicalUrl(String canonicalUrl);

    // =============================================
    // ANALYTICS & REPORTING OPERATIONS
    // =============================================

    /**
     * Get total SEO meta count
     */
    long getTotalSeoMetaCount();

    /**
     * Get SEO meta count by category
     */
    List<Object[]> getSeoMetaCountByCategory();

    /**
     * Get count by index status
     */
    List<Object[]> getCountByIndexStatus();

    /**
     * Find SEO meta with complete category data
     */
    Optional<SeoMetaDto> findByIdWithCategory(Long id);

    /**
     * Find SEO meta for specific content types
     */
    List<SeoMetaDto> findByContentTypes(List<String> categoryNames);

    /**
     * Find SEO meta by criteria (admin search/filter)
     */
    List<SeoMetaDto> findByCriteria(String title, String description, String keywords, 
                                   Long categoryId, String schemaType);

    /**
     * Find orphaned SEO meta
     */
    List<SeoMetaDto> findOrphanedSeoMeta();
}