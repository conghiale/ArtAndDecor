package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.PageDto;
import org.artanddecor.dto.PageRequest;
import org.artanddecor.model.Page;
import org.artanddecor.model.PageGroup;
import org.artanddecor.model.PagePosition;
import org.artanddecor.repository.PageGroupRepository;
import org.artanddecor.repository.PagePositionRepository;
import org.artanddecor.repository.PageRepository;
import org.artanddecor.services.PageService;
import org.artanddecor.utils.PageMapper;
import org.artanddecor.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Page Service Implementation
 * Implements business logic for Page management
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PageServiceImpl implements PageService {
    
    private static final Logger logger = LoggerFactory.getLogger(PageServiceImpl.class);
    private final PageRepository pageRepository;
    private final PagePositionRepository pagePositionRepository;
    private final PageGroupRepository pageGroupRepository;
    
    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<PageDto> findPagesByCriteria(String pageName, Boolean pageEnabled, Long pagePositionId, Long pageGroupId, String textSearch, org.springframework.data.domain.Pageable pageable) {
        logger.debug("Finding pages by criteria - name: {}, enabled: {}, positionId: {}, groupId: {}, textSearch: {}, page: {}", 
                    pageName, pageEnabled, pagePositionId, pageGroupId, textSearch, pageable.getPageNumber());
        return pageRepository.findPagesByCriteria(pageName, pageEnabled, pagePositionId, pageGroupId, textSearch, pageable)
                .map(PageMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PageDto> findPageById(Long pageId) {
        logger.debug("Finding page by ID: {}", pageId);
        return pageRepository.findById(pageId)
                .map(PageMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PageDto> findPageBySlug(String pageSlug) {
        logger.debug("Finding page by slug: {}", pageSlug);
        return pageRepository.findByPageSlug(pageSlug)
                .map(PageMapper::toDto);
    }
    
    @Override
    public PageDto createPage(PageRequest pageRequest) {
        logger.info("Creating new page: {}", pageRequest.getPageName());
        
        // Validate foreign keys
        PagePosition pagePosition = pagePositionRepository.findById(pageRequest.getPagePositionId())
                .orElseThrow(() -> {
                    logger.error("Page position not found with ID: {}", pageRequest.getPagePositionId());
                    return new IllegalArgumentException("Page position not found with ID: " + pageRequest.getPagePositionId());
                });
        
        PageGroup pageGroup = pageGroupRepository.findById(pageRequest.getPageGroupId())
                .orElseThrow(() -> {
                    logger.error("Page group not found with ID: {}", pageRequest.getPageGroupId());
                    return new IllegalArgumentException("Page group not found with ID: " + pageRequest.getPageGroupId());
                });
        
        // Auto-generate slug if not provided
        String slug = pageRequest.getPageSlug();
        if (slug == null || slug.isBlank()) {
            slug = Utils.generateSlug(pageRequest.getPageName());
            logger.info("Auto-generated slug for page: {}", slug);
        }
        
        // Check if slug already exists
        if (pageRepository.existsByPageSlug(slug)) {
            logger.warn("Page slug already exists: {}", slug);
            throw new IllegalArgumentException("Page slug already exists: " + slug);
        }
        
        // Create entity from request
        Page page = PageMapper.toEntity(pageRequest, pagePosition, pageGroup);
        page.setPageSlug(slug);
        
        Page saved = pageRepository.save(page);
        logger.info("Page created successfully with ID: {}", saved.getPageId());
        
        return PageMapper.toDto(saved);
    }
    
    @Override
    public PageDto updatePage(Long pageId, PageRequest pageRequest) {
        logger.info("Updating page with ID: {}", pageId);
        
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> {
                    logger.error("Page not found with ID: {}", pageId);
                    return new IllegalArgumentException("Page not found with ID: " + pageId);
                });
        
        // Validate foreign keys
        PagePosition pagePosition = pagePositionRepository.findById(pageRequest.getPagePositionId())
                .orElseThrow(() -> {
                    logger.error("Page position not found with ID: {}", pageRequest.getPagePositionId());
                    return new IllegalArgumentException("Page position not found with ID: " + pageRequest.getPagePositionId());
                });
        
        PageGroup pageGroup = pageGroupRepository.findById(pageRequest.getPageGroupId())
                .orElseThrow(() -> {
                    logger.error("Page group not found with ID: {}", pageRequest.getPageGroupId());
                    return new IllegalArgumentException("Page group not found with ID: " + pageRequest.getPageGroupId());
                });
        
        // Auto-generate slug if not provided
        String slug = pageRequest.getPageSlug();
        if (slug == null || slug.isBlank()) {
            slug = Utils.generateSlug(pageRequest.getPageName());
            logger.info("Auto-generated slug for page update: {}", slug);
        }
        
        // Check if slug is changing and new slug already exists
        if (!page.getPageSlug().equals(slug)) {
            if (pageRepository.existsByPageSlug(slug)) {
                logger.warn("New page slug already exists: {}", slug);
                throw new IllegalArgumentException("Page slug already exists: " + slug);
            }
        }
        
        // Update entity
        PageMapper.updateEntityFromRequest(page, pageRequest, pagePosition, pageGroup);
        page.setPageSlug(slug);
        
        Page updated = pageRepository.save(page);
        logger.info("Page updated successfully with ID: {}", updated.getPageId());
        
        return PageMapper.toDto(updated);
    }
    
    @Override
    public PageDto updatePageStatus(Long pageId, Boolean enabled) {
        logger.info("Updating page status - ID: {}, Enabled: {}", pageId, enabled);
        
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> {
                    logger.error("Page not found with ID: {}", pageId);
                    return new IllegalArgumentException("Page not found with ID: " + pageId);
                });
        
        page.setPageEnabled(enabled);
        Page updated = pageRepository.save(page);
        
        logger.info("Page status updated successfully with ID: {}", pageId);
        return PageMapper.toDto(updated);
    }
}