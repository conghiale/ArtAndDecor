package org.artanddecor.repository;

import org.artanddecor.model.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Banner entity
 */
@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    /**
     * Find banners by criteria with pagination
     * @param bannerEnabled Filter by enabled status (null = all)
     * @param textSearch Search text in title and link fields
     * @param pageable Pagination parameters
     * @return Page of matching banners
     */
    @Query("SELECT b FROM Banner b WHERE " +
           "(:bannerEnabled IS NULL OR b.bannerEnabled = :bannerEnabled) AND " +
           "(:textSearch IS NULL OR :textSearch = '' OR " +
           " LOWER(b.bannerTitle) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(b.bannerLink) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<Banner> findByCriteria(
        @Param("bannerEnabled") Boolean bannerEnabled,
        @Param("textSearch") String textSearch,
        Pageable pageable);
}
