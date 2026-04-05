package org.artanddecor.services;

import org.artanddecor.dto.PageDto;
import org.artanddecor.dto.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Page Service Interface
 * Business logic operations for Page management
 */
public interface PageService {
    
    /**
     * Find pages by criteria with pagination
     * @param pageName Filter by page name (exact match)
     * @param pageEnabled Filter by enabled status
     * @param pagePositionId Filter by page position ID
     * @param pageGroupId Filter by page group ID
     * @param textSearch Search text in multiple fields
     * @param pageable Pagination information
     * @return Page of matching PageDto objects
     */
    Page<PageDto> findPagesByCriteria(String pageName, Boolean pageEnabled, Long pagePositionId, Long pageGroupId, String textSearch, Pageable pageable);
    
    /**
     * Get page by ID
     * @param pageId the page ID
     * @return Optional containing PageDto if found
     */
    Optional<PageDto> findPageById(Long pageId);
    
    /**
     * Get page by slug
     * @param pageSlug the page slug
     * @return Optional containing PageDto if found
     */
    Optional<PageDto> findPageBySlug(String pageSlug);
    
    /**
     * Create new page
     * @param pageRequest the page request with data
     * @return created PageDto with ID
     * @throws IllegalArgumentException if validation fails or slug already exists
     */
    PageDto createPage(PageRequest pageRequest);
    
    /**
     * Update existing page
     * @param pageId the page ID to update
     * @param pageRequest the page request with updated data
     * @return updated PageDto
     * @throws IllegalArgumentException if page not found or validation fails
     */
    PageDto updatePage(Long pageId, PageRequest pageRequest);
    
    /**
     * Update page status (enable/disable)
     * @param pageId the page ID to update
     * @param enabled the new enabled status
     * @return updated PageDto
     * @throws IllegalArgumentException if page not found
     */
    PageDto updatePageStatus(Long pageId, Boolean enabled);
}