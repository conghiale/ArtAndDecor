package org.artanddecor.repository;

import org.artanddecor.model.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Page Repository
 * Handles database operations for Page table
 */
@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

    /**
     * Find page by slug
     * @param pageSlug the page slug
     * @return Optional containing Page if found
     */
    Optional<Page> findByPageSlug(String pageSlug);

    /**
     * Check if page slug exists
     * @param pageSlug Page slug
     * @return true if exists
     */
    boolean existsByPageSlug(String pageSlug);

    /**
     * Find pages by multiple criteria with pagination
     * @param pageName Filter by page name (exact match)
     * @param pageEnabled Filter by enabled status
     * @param pagePositionId Filter by page position ID
     * @param pageGroupId Filter by page group ID
     * @param textSearch Search text in name, slug, display name, remark, content fields
     * @param pageable Pagination information
     * @return Page of matching pages
     */
    @Query("SELECT p FROM Page p " +
           "LEFT JOIN FETCH p.pagePosition pp " +
           "LEFT JOIN FETCH p.pageGroup pg " +
           "WHERE " +
           "(:pageName IS NULL OR p.pageName = :pageName) AND " +
           "(:pageEnabled IS NULL OR p.pageEnabled = :pageEnabled) AND " +
           "(:pagePositionId IS NULL OR p.pagePosition.pagePositionId = :pagePositionId) AND " +
           "(:pageGroupId IS NULL OR p.pageGroup.pageGroupId = :pageGroupId) AND " +
           "(:textSearch IS NULL OR :textSearch = '' OR " +
           " LOWER(p.pageName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(p.pageSlug) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(p.pageDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(p.pageRemark) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(p.pageContent) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    org.springframework.data.domain.Page<Page> findPagesByCriteria(
        @Param("pageName") String pageName,
        @Param("pageEnabled") Boolean pageEnabled,
        @Param("pagePositionId") Long pagePositionId,
        @Param("pageGroupId") Long pageGroupId,
        @Param("textSearch") String textSearch,
        Pageable pageable);
}