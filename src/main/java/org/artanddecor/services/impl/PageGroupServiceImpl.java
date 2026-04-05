package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.PageGroupDto;
import org.artanddecor.dto.PageGroupRequest;
import org.artanddecor.model.PageGroup;
import org.artanddecor.repository.PageGroupRepository;
import org.artanddecor.services.PageGroupService;
import org.artanddecor.utils.PageMapper;
import org.artanddecor.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * PageGroup Service Implementation
 * Implements business logic for PageGroup management
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PageGroupServiceImpl implements PageGroupService {
    
    private static final Logger logger = LoggerFactory.getLogger(PageGroupServiceImpl.class);
    private final PageGroupRepository pageGroupRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Page<PageGroupDto> findPageGroupsByCriteria(String pageGroupName, Boolean pageGroupEnabled, String textSearch, Pageable pageable) {
        logger.debug("Finding page groups by criteria - name: {}, enabled: {}, textSearch: {}, page: {}", 
                    pageGroupName, pageGroupEnabled, textSearch, pageable.getPageNumber());
        return pageGroupRepository.findPageGroupsByCriteria(pageGroupName, pageGroupEnabled, textSearch, pageable)
                .map(PageMapper::toPageGroupDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PageGroupDto> findPageGroupById(Long pageGroupId) {
        logger.debug("Finding page group by ID: {}", pageGroupId);
        return pageGroupRepository.findById(pageGroupId)
                .map(PageMapper::toPageGroupDto);
    }
    
    @Override
    public PageGroupDto createPageGroup(PageGroupRequest pageGroupRequest) {
        logger.info("Creating new page group: {}", pageGroupRequest.getPageGroupName());
        
        // Auto-generate slug if not provided
        String slug = pageGroupRequest.getPageGroupSlug();
        if (slug == null || slug.isBlank()) {
            slug = Utils.generateSlug(pageGroupRequest.getPageGroupName());
            logger.info("Auto-generated slug for page group: {}", slug);
        }
        
        // Check if slug already exists
        if (pageGroupRepository.existsByPageGroupSlug(slug)) {
            logger.warn("Page group slug already exists: {}", slug);
            throw new IllegalArgumentException("Page group slug already exists: " + slug);
        }
        
        // Create entity from request
        PageGroup pageGroup = PageMapper.toPageGroupEntity(pageGroupRequest);
        pageGroup.setPageGroupSlug(slug);
        
        PageGroup saved = pageGroupRepository.save(pageGroup);
        logger.info("Page group created successfully with ID: {}", saved.getPageGroupId());
        
        return PageMapper.toPageGroupDto(saved);
    }
    
    @Override
    public PageGroupDto updatePageGroup(Long pageGroupId, PageGroupRequest pageGroupRequest) {
        logger.info("Updating page group with ID: {}", pageGroupId);
        
        PageGroup pageGroup = pageGroupRepository.findById(pageGroupId)
                .orElseThrow(() -> {
                    logger.error("Page group not found with ID: {}", pageGroupId);
                    return new IllegalArgumentException("Page group not found with ID: " + pageGroupId);
                });
        
        // Auto-generate slug if not provided
        String slug = pageGroupRequest.getPageGroupSlug();
        if (slug == null || slug.isBlank()) {
            slug = Utils.generateSlug(pageGroupRequest.getPageGroupName());
            logger.info("Auto-generated slug for page group update: {}", slug);
        }
        
        // Check if slug is changing and new slug already exists
        if (!pageGroup.getPageGroupSlug().equals(slug)) {
            if (pageGroupRepository.existsByPageGroupSlug(slug)) {
                logger.warn("New page group slug already exists: {}", slug);
                throw new IllegalArgumentException("Page group slug already exists: " + slug);
            }
        }
        
        // Update entity
        PageMapper.updatePageGroupEntityFromRequest(pageGroup, pageGroupRequest);
        pageGroup.setPageGroupSlug(slug);
        
        PageGroup updated = pageGroupRepository.save(pageGroup);
        logger.info("Page group updated successfully with ID: {}", updated.getPageGroupId());
        
        return PageMapper.toPageGroupDto(updated);
    }
    
    @Override
    public PageGroupDto updatePageGroupStatus(Long pageGroupId, Boolean enabled) {
        logger.info("Updating page group status - ID: {}, Enabled: {}", pageGroupId, enabled);
        
        PageGroup pageGroup = pageGroupRepository.findById(pageGroupId)
                .orElseThrow(() -> {
                    logger.error("Page group not found with ID: {}", pageGroupId);
                    return new IllegalArgumentException("Page group not found with ID: " + pageGroupId);
                });
        
        pageGroup.setPageGroupEnabled(enabled);
        PageGroup updated = pageGroupRepository.save(pageGroup);
        
        logger.info("Page group status updated successfully with ID: {}", pageGroupId);
        return PageMapper.toPageGroupDto(updated);
    }
}