package org.artanddecor.services;

import org.artanddecor.dto.SeoMetaDto;

import java.util.Optional;

/**
 * SeoMeta Service Interface
 * Defines essential business operations for SeoMeta management
 * Only includes methods actually used in the application
 */
public interface SeoMetaService {

    // =============================================
    // BASIC FIND OPERATIONS
    // =============================================

    /**
     * Find SEO meta by ID
     */
    Optional<SeoMetaDto> findById(Long id);

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
}