package org.artanddecor.repository;

import org.artanddecor.model.PagePosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * PagePosition Repository
 * Handles database operations for PagePosition table
 */
@Repository
public interface PagePositionRepository extends JpaRepository<PagePosition, Long> {

    /**
     * Find page position by slug
     * @param pagePositionSlug the page position slug
     * @return Optional containing PagePosition if found
     */
    Optional<PagePosition> findByPagePositionSlug(String pagePositionSlug);

    /**
     * Check if page position slug exists
     * @param pagePositionSlug Page position slug
     * @return true if exists
     */
    boolean existsByPagePositionSlug(String pagePositionSlug);

    /**
     * Find page positions by multiple criteria with pagination
     * @param pagePositionName Filter by page position name (exact match)
     * @param pagePositionEnabled Filter by enabled status
     * @param textSearch Search text in name, slug, display name, remark fields
     * @param pageable Pagination information
     * @return Page of matching page positions
     */
    @Query("SELECT pp FROM PagePosition pp WHERE " +
           "(:pagePositionName IS NULL OR pp.pagePositionName = :pagePositionName) AND " +
           "(:pagePositionEnabled IS NULL OR pp.pagePositionEnabled = :pagePositionEnabled) AND " +
           "(:textSearch IS NULL OR :textSearch = '' OR " +
           " LOWER(pp.pagePositionName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(pp.pagePositionSlug) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(pp.pagePositionDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(pp.pagePositionRemark) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<PagePosition> findPagePositionsByCriteria(
        @Param("pagePositionName") String pagePositionName,
        @Param("pagePositionEnabled") Boolean pagePositionEnabled,
        @Param("textSearch") String textSearch,
        Pageable pageable);
}