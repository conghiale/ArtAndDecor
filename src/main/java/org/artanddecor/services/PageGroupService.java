package org.artanddecor.services;

import org.artanddecor.dto.PageGroupDto;
import org.artanddecor.dto.PageGroupRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * PageGroup Service Interface
 * Business logic operations for PageGroup management
 */
public interface PageGroupService {
    
    /**
     * Find page groups by criteria with pagination
     * @param pageGroupName Filter by page group name (exact match)
     * @param pageGroupEnabled Filter by enabled status
     * @param textSearch Search text in multiple fields
     * @param pageable Pagination information
     * @return Page of matching PageGroupDto objects
     */
    Page<PageGroupDto> findPageGroupsByCriteria(String pageGroupName, Boolean pageGroupEnabled, String textSearch, Pageable pageable);
    
    /**
     * Get page group by ID
     * @param pageGroupId the page group ID
     * @return Optional containing PageGroupDto if found
     */
    Optional<PageGroupDto> findPageGroupById(Long pageGroupId);
    
    /**
     * Create new page group
     * @param pageGroupRequest the page group request with data
     * @return created PageGroupDto with ID
     * @throws IllegalArgumentException if validation fails or slug already exists
     */
    PageGroupDto createPageGroup(PageGroupRequest pageGroupRequest);
    
    /**
     * Update existing page group
     * @param pageGroupId the page group ID to update
     * @param pageGroupRequest the page group request with updated data
     * @return updated PageGroupDto
     * @throws IllegalArgumentException if page group not found or validation fails
     */
    PageGroupDto updatePageGroup(Long pageGroupId, PageGroupRequest pageGroupRequest);
    
    /**
     * Update page group status (enable/disable)
     * @param pageGroupId the page group ID to update
     * @param enabled the new enabled status
     * @return updated PageGroupDto
     * @throws IllegalArgumentException if page group not found
     */
    PageGroupDto updatePageGroupStatus(Long pageGroupId, Boolean enabled);
}