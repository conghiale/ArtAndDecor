package org.artanddecor.services;

import org.artanddecor.dto.PagePositionDto;
import org.artanddecor.dto.PagePositionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * PagePosition Service Interface
 * Business logic operations for PagePosition management
 */
public interface PagePositionService {
    
    /**
     * Find page positions by criteria with pagination
     * @param pagePositionName Filter by page position name (exact match)
     * @param pagePositionEnabled Filter by enabled status
     * @param textSearch Search text in multiple fields
     * @param pageable Pagination information
     * @return Page of matching PagePositionDto objects
     */
    Page<PagePositionDto> findPagePositionsByCriteria(String pagePositionName, Boolean pagePositionEnabled, String textSearch, Pageable pageable);
    
    /**
     * Get page position by ID
     * @param pagePositionId the page position ID
     * @return Optional containing PagePositionDto if found
     */
    Optional<PagePositionDto> findPagePositionById(Long pagePositionId);
    
    /**
     * Create new page position
     * @param pagePositionRequest the page position request with data
     * @return created PagePositionDto with ID
     * @throws IllegalArgumentException if validation fails or slug already exists
     */
    PagePositionDto createPagePosition(PagePositionRequest pagePositionRequest);
    
    /**
     * Update existing page position
     * @param pagePositionId the page position ID to update
     * @param pagePositionRequest the page position request with updated data
     * @return updated PagePositionDto
     * @throws IllegalArgumentException if page position not found or validation fails
     */
    PagePositionDto updatePagePosition(Long pagePositionId, PagePositionRequest pagePositionRequest);
    
    /**
     * Update page position status (enable/disable)
     * @param pagePositionId the page position ID to update
     * @param enabled the new enabled status
     * @return updated PagePositionDto
     * @throws IllegalArgumentException if page position not found
     */
    PagePositionDto updatePagePositionStatus(Long pagePositionId, Boolean enabled);
}