package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.PagePositionDto;
import org.artanddecor.dto.PagePositionRequest;
import org.artanddecor.model.PagePosition;
import org.artanddecor.repository.PagePositionRepository;
import org.artanddecor.services.PagePositionService;
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
 * PagePosition Service Implementation
 * Implements business logic for PagePosition management
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PagePositionServiceImpl implements PagePositionService {
    
    private static final Logger logger = LoggerFactory.getLogger(PagePositionServiceImpl.class);
    private final PagePositionRepository pagePositionRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Page<PagePositionDto> findPagePositionsByCriteria(String pagePositionName, Boolean pagePositionEnabled, String textSearch, Pageable pageable) {
        logger.debug("Finding page positions by criteria - name: {}, enabled: {}, textSearch: {}, page: {}", 
                    pagePositionName, pagePositionEnabled, textSearch, pageable.getPageNumber());
        return pagePositionRepository.findPagePositionsByCriteria(pagePositionName, pagePositionEnabled, textSearch, pageable)
                .map(PageMapper::toPagePositionDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PagePositionDto> findPagePositionById(Long pagePositionId) {
        logger.debug("Finding page position by ID: {}", pagePositionId);
        return pagePositionRepository.findById(pagePositionId)
                .map(PageMapper::toPagePositionDto);
    }
    
    @Override
    public PagePositionDto createPagePosition(PagePositionRequest pagePositionRequest) {
        logger.info("Creating new page position: {}", pagePositionRequest.getPagePositionName());
        
        // Auto-generate slug if not provided
        String slug = pagePositionRequest.getPagePositionSlug();
        if (slug == null || slug.isBlank()) {
            slug = Utils.generateSlug(pagePositionRequest.getPagePositionName());
            logger.info("Auto-generated slug for page position: {}", slug);
        }
        
        // Check if slug already exists
        if (pagePositionRepository.existsByPagePositionSlug(slug)) {
            logger.warn("Page position slug already exists: {}", slug);
            throw new IllegalArgumentException("Page position slug already exists: " + slug);
        }
        
        // Create entity from request
        PagePosition pagePosition = PageMapper.toPagePositionEntity(pagePositionRequest);
        pagePosition.setPagePositionSlug(slug);
        
        PagePosition saved = pagePositionRepository.save(pagePosition);
        logger.info("Page position created successfully with ID: {}", saved.getPagePositionId());
        
        return PageMapper.toPagePositionDto(saved);
    }
    
    @Override
    public PagePositionDto updatePagePosition(Long pagePositionId, PagePositionRequest pagePositionRequest) {
        logger.info("Updating page position with ID: {}", pagePositionId);
        
        PagePosition pagePosition = pagePositionRepository.findById(pagePositionId)
                .orElseThrow(() -> {
                    logger.error("Page position not found with ID: {}", pagePositionId);
                    return new IllegalArgumentException("Page position not found with ID: " + pagePositionId);
                });
        
        // Auto-generate slug if not provided
        String slug = pagePositionRequest.getPagePositionSlug();
        if (slug == null || slug.isBlank()) {
            slug = Utils.generateSlug(pagePositionRequest.getPagePositionName());
            logger.info("Auto-generated slug for page position update: {}", slug);
        }
        
        // Check if slug is changing and new slug already exists
        if (!pagePosition.getPagePositionSlug().equals(slug)) {
            if (pagePositionRepository.existsByPagePositionSlug(slug)) {
                logger.warn("New page position slug already exists: {}", slug);
                throw new IllegalArgumentException("Page position slug already exists: " + slug);
            }
        }
        
        // Update entity
        PageMapper.updatePagePositionEntityFromRequest(pagePosition, pagePositionRequest);
        pagePosition.setPagePositionSlug(slug);
        
        PagePosition updated = pagePositionRepository.save(pagePosition);
        logger.info("Page position updated successfully with ID: {}", updated.getPagePositionId());
        
        return PageMapper.toPagePositionDto(updated);
    }
    
    @Override
    public PagePositionDto updatePagePositionStatus(Long pagePositionId, Boolean enabled) {
        logger.info("Updating page position status - ID: {}, Enabled: {}", pagePositionId, enabled);
        
        PagePosition pagePosition = pagePositionRepository.findById(pagePositionId)
                .orElseThrow(() -> {
                    logger.error("Page position not found with ID: {}", pagePositionId);
                    return new IllegalArgumentException("Page position not found with ID: " + pagePositionId);
                });
        
        pagePosition.setPagePositionEnabled(enabled);
        PagePosition updated = pagePositionRepository.save(pagePosition);
        
        logger.info("Page position status updated successfully with ID: {}", pagePositionId);
        return PageMapper.toPagePositionDto(updated);
    }
}