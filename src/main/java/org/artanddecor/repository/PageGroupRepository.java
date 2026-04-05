package org.artanddecor.repository;

import org.artanddecor.model.PageGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * PageGroup Repository
 * Handles database operations for PageGroup table
 */
@Repository
public interface PageGroupRepository extends JpaRepository<PageGroup, Long> {

    /**
     * Find page group by slug
     * @param pageGroupSlug the page group slug
     * @return Optional containing PageGroup if found
     */
    Optional<PageGroup> findByPageGroupSlug(String pageGroupSlug);

    /**
     * Check if page group slug exists
     * @param pageGroupSlug Page group slug
     * @return true if exists
     */
    boolean existsByPageGroupSlug(String pageGroupSlug);

    /**
     * Find page groups by multiple criteria with pagination
     * @param pageGroupName Filter by page group name (exact match)
     * @param pageGroupEnabled Filter by enabled status
     * @param textSearch Search text in name, slug, display name, remark fields
     * @param pageable Pagination information
     * @return Page of matching page groups
     */
    @Query("SELECT pg FROM PageGroup pg WHERE " +
           "(:pageGroupName IS NULL OR pg.pageGroupName = :pageGroupName) AND " +
           "(:pageGroupEnabled IS NULL OR pg.pageGroupEnabled = :pageGroupEnabled) AND " +
           "(:textSearch IS NULL OR :textSearch = '' OR " +
           " LOWER(pg.pageGroupName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(pg.pageGroupSlug) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(pg.pageGroupDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(pg.pageGroupRemark) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<PageGroup> findPageGroupsByCriteria(
        @Param("pageGroupName") String pageGroupName,
        @Param("pageGroupEnabled") Boolean pageGroupEnabled,
        @Param("textSearch") String textSearch,
        Pageable pageable);
}