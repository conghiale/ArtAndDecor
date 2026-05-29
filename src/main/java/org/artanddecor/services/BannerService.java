package org.artanddecor.services;

import org.artanddecor.dto.BannerDto;
import org.artanddecor.dto.BannerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Banner Service Interface
 * Business logic operations for Banner management
 */
public interface BannerService {

    /**
     * Create a new banner with associated images
     * @param request banner creation data (includes ordered imageIds)
     * @return created BannerDto
     */
    BannerDto createBanner(BannerRequest request);

    /**
     * Update an existing banner and replace its image associations
     * @param bannerId the banner ID to update
     * @param request updated banner data (includes ordered imageIds)
     * @return updated BannerDto
     */
    BannerDto updateBanner(Long bannerId, BannerRequest request);

    /**
     * Get a banner by ID (includes images)
     * @param bannerId the banner ID
     * @return BannerDto
     */
    BannerDto getBannerById(Long bannerId);

    /**
     * Find banners by criteria with pagination
     * @param bannerEnabled Filter by enabled status (null = all)
     * @param textSearch Search text in title and link fields
     * @param pageable Pagination information
     * @return Page of matching BannerDto objects
     */
    Page<BannerDto> getBannersByCriteria(Boolean bannerEnabled, String textSearch, Pageable pageable);
}
