package org.artanddecor.repository;

import org.artanddecor.model.BannerImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for BannerImage junction entity
 */
@Repository
public interface BannerImageRepository extends JpaRepository<BannerImage, Long> {

    /**
     * Delete all image associations for a given banner
     */
    @Modifying
    @Query("DELETE FROM BannerImage bi WHERE bi.banner.bannerId = :bannerId")
    void deleteByBannerId(@Param("bannerId") Long bannerId);

    /**
     * Check if a banner-image combination already exists
     */
    boolean existsByBannerBannerIdAndImageImageId(Long bannerId, Long imageId);
}
