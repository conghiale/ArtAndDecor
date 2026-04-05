package org.artanddecor.utils;

import org.artanddecor.dto.PageDto;
import org.artanddecor.dto.PageGroupDto;
import org.artanddecor.dto.PageGroupRequest;
import org.artanddecor.dto.PagePositionDto;
import org.artanddecor.dto.PagePositionRequest;
import org.artanddecor.dto.PageRequest;
import org.artanddecor.model.Page;
import org.artanddecor.model.PageGroup;
import org.artanddecor.model.PagePosition;

/**
 * Unified Page Mapper utility for all Page-related DTOs and entities
 * Handles mapping for Page, PagePosition, and PageGroup
 */
public class PageMapper {
    
    // =============================================
    // PAGE POSITION MAPPING METHODS
    // =============================================
    
    /**
     * Convert PagePosition entity to PagePositionDto
     * @param pagePosition PagePosition entity
     * @return PagePositionDto
     */
    public static PagePositionDto toPagePositionDto(PagePosition pagePosition) {
        if (pagePosition == null) {
            return null;
        }
        
        return PagePositionDto.builder()
                .pagePositionId(pagePosition.getPagePositionId())
                .pagePositionSlug(pagePosition.getPagePositionSlug())
                .pagePositionName(pagePosition.getPagePositionName())
                .pagePositionEnabled(pagePosition.getPagePositionEnabled())
                .pagePositionDisplayName(pagePosition.getPagePositionDisplayName())
                .pagePositionRemark(pagePosition.getPagePositionRemark())
                .createdDt(pagePosition.getCreatedDt())
                .modifiedDt(pagePosition.getModifiedDt())
                .build();
    }
    
    /**
     * Convert PagePositionRequest to new PagePosition entity
     * @param pagePositionRequest PagePositionRequest
     * @return new PagePosition entity
     */
    public static PagePosition toPagePositionEntity(PagePositionRequest pagePositionRequest) {
        if (pagePositionRequest == null) {
            return null;
        }
        
        PagePosition pagePosition = new PagePosition();
        pagePosition.setPagePositionSlug(pagePositionRequest.getPagePositionSlug());
        pagePosition.setPagePositionName(pagePositionRequest.getPagePositionName());
        pagePosition.setPagePositionEnabled(pagePositionRequest.getPagePositionEnabled() != null ? pagePositionRequest.getPagePositionEnabled() : true);
        pagePosition.setPagePositionDisplayName(pagePositionRequest.getPagePositionDisplayName());
        pagePosition.setPagePositionRemark(pagePositionRequest.getPagePositionRemark());
        
        return pagePosition;
    }
    
    /**
     * Update existing PagePosition entity from PagePositionRequest
     * @param pagePosition existing PagePosition entity to update
     * @param pagePositionRequest PagePositionRequest with new data
     */
    public static void updatePagePositionEntityFromRequest(PagePosition pagePosition, PagePositionRequest pagePositionRequest) {
        if (pagePosition == null || pagePositionRequest == null) {
            return;
        }
        
        pagePosition.setPagePositionSlug(pagePositionRequest.getPagePositionSlug());
        pagePosition.setPagePositionName(pagePositionRequest.getPagePositionName());
        pagePosition.setPagePositionDisplayName(pagePositionRequest.getPagePositionDisplayName());
        pagePosition.setPagePositionRemark(pagePositionRequest.getPagePositionRemark());
        pagePosition.setPagePositionEnabled(pagePositionRequest.getPagePositionEnabled() != null ? pagePositionRequest.getPagePositionEnabled() : true);
    }
    
    // =============================================
    // PAGE GROUP MAPPING METHODS
    // =============================================
    
    /**
     * Convert PageGroup entity to PageGroupDto
     * @param pageGroup PageGroup entity
     * @return PageGroupDto
     */
    public static PageGroupDto toPageGroupDto(PageGroup pageGroup) {
        if (pageGroup == null) {
            return null;
        }
        
        return PageGroupDto.builder()
                .pageGroupId(pageGroup.getPageGroupId())
                .pageGroupSlug(pageGroup.getPageGroupSlug())
                .pageGroupName(pageGroup.getPageGroupName())
                .pageGroupEnabled(pageGroup.getPageGroupEnabled())
                .pageGroupDisplayName(pageGroup.getPageGroupDisplayName())
                .pageGroupRemark(pageGroup.getPageGroupRemark())
                .createdDt(pageGroup.getCreatedDt())
                .modifiedDt(pageGroup.getModifiedDt())
                .build();
    }
    
    /**
     * Convert PageGroupRequest to new PageGroup entity
     * @param pageGroupRequest PageGroupRequest
     * @return new PageGroup entity
     */
    public static PageGroup toPageGroupEntity(PageGroupRequest pageGroupRequest) {
        if (pageGroupRequest == null) {
            return null;
        }
        
        PageGroup pageGroup = new PageGroup();
        pageGroup.setPageGroupSlug(pageGroupRequest.getPageGroupSlug());
        pageGroup.setPageGroupName(pageGroupRequest.getPageGroupName());
        pageGroup.setPageGroupEnabled(pageGroupRequest.getPageGroupEnabled() != null ? pageGroupRequest.getPageGroupEnabled() : true);
        pageGroup.setPageGroupDisplayName(pageGroupRequest.getPageGroupDisplayName());
        pageGroup.setPageGroupRemark(pageGroupRequest.getPageGroupRemark());
        
        return pageGroup;
    }
    
    /**
     * Update existing PageGroup entity from PageGroupRequest
     * @param pageGroup existing PageGroup entity to update
     * @param pageGroupRequest PageGroupRequest with new data
     */
    public static void updatePageGroupEntityFromRequest(PageGroup pageGroup, PageGroupRequest pageGroupRequest) {
        if (pageGroup == null || pageGroupRequest == null) {
            return;
        }
        
        pageGroup.setPageGroupSlug(pageGroupRequest.getPageGroupSlug());
        pageGroup.setPageGroupName(pageGroupRequest.getPageGroupName());
        pageGroup.setPageGroupDisplayName(pageGroupRequest.getPageGroupDisplayName());
        pageGroup.setPageGroupRemark(pageGroupRequest.getPageGroupRemark());
        pageGroup.setPageGroupEnabled(pageGroupRequest.getPageGroupEnabled() != null ? pageGroupRequest.getPageGroupEnabled() : true);
    }
    
    // =============================================
    // PAGE MAPPING METHODS
    // =============================================
    
    /**
     * Convert Page entity to PageDto
     * @param page Page entity
     * @return PageDto
     */
    public static PageDto toDto(Page page) {
        if (page == null) {
            return null;
        }
        
        return PageDto.builder()
                .pageId(page.getPageId())
                .pagePosition(toPagePositionDto(page.getPagePosition()))
                .pageGroup(toPageGroupDto(page.getPageGroup()))
                .targetUrl(page.getTargetUrl())
                .pageSlug(page.getPageSlug())
                .pageName(page.getPageName())
                .pageContent(page.getPageContent())
                .pageEnabled(page.getPageEnabled())
                .pageDisplayName(page.getPageDisplayName())
                .pageRemark(page.getPageRemark())
                .createdDt(page.getCreatedDt())
                .modifiedDt(page.getModifiedDt())
                .build();
    }
    
    /**
     * Convert PageRequest to new Page entity
     * @param pageRequest PageRequest
     * @param pagePosition PagePosition entity
     * @param pageGroup PageGroup entity
     * @return new Page entity
     */
    public static Page toEntity(PageRequest pageRequest, PagePosition pagePosition, PageGroup pageGroup) {
        if (pageRequest == null) {
            return null;
        }
        
        Page page = new Page();
        page.setPagePosition(pagePosition);
        page.setPageGroup(pageGroup);
        page.setTargetUrl(pageRequest.getTargetUrl());
        page.setPageSlug(pageRequest.getPageSlug());
        page.setPageName(pageRequest.getPageName());
        page.setPageContent(pageRequest.getPageContent());
        page.setPageEnabled(pageRequest.getPageEnabled() != null ? pageRequest.getPageEnabled() : true);
        page.setPageDisplayName(pageRequest.getPageDisplayName());
        page.setPageRemark(pageRequest.getPageRemark());
        
        return page;
    }
    
    /**
     * Update existing Page entity from PageRequest
     * @param page existing Page entity to update
     * @param pageRequest PageRequest with new data
     * @param pagePosition PagePosition entity
     * @param pageGroup PageGroup entity
     */
    public static void updateEntityFromRequest(Page page, PageRequest pageRequest, PagePosition pagePosition, PageGroup pageGroup) {
        if (page == null || pageRequest == null) {
            return;
        }
        
        page.setPagePosition(pagePosition);
        page.setPageGroup(pageGroup);
        page.setTargetUrl(pageRequest.getTargetUrl());
        page.setPageSlug(pageRequest.getPageSlug());
        page.setPageName(pageRequest.getPageName());
        page.setPageContent(pageRequest.getPageContent());
        page.setPageDisplayName(pageRequest.getPageDisplayName());
        page.setPageRemark(pageRequest.getPageRemark());
        page.setPageEnabled(pageRequest.getPageEnabled() != null ? pageRequest.getPageEnabled() : true);
    }
}