package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.SeoMetaDto;
import org.artanddecor.model.SeoMeta;
import org.artanddecor.repository.SeoMetaRepository;
import org.artanddecor.services.SeoMetaService;
import org.artanddecor.utils.SeoMetaMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * SeoMeta Service Implementation
 * Handles essential business logic for SEO metadata management
 * Only implements methods actually used in the application
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeoMetaServiceImpl implements SeoMetaService {
    
    private static final Logger logger = LoggerFactory.getLogger(SeoMetaServiceImpl.class);
    
    private final SeoMetaRepository seoMetaRepository;

    // =============================================
    // BASIC FIND OPERATIONS
    // =============================================

    @Override
    public Optional<SeoMetaDto> findById(Long id) {
        logger.debug("Finding SEO meta by ID: {}", id);
        return seoMetaRepository.findById(id)
                .map(SeoMetaMapperUtil::toSeoMetaDto);
    }

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    @Override
    @Transactional
    public SeoMetaDto createSeoMeta(SeoMetaDto seoMetaDto) {
        logger.info("Creating new SEO meta: {}", seoMetaDto.getSeoMetaTitle());
        
        // Validation
        if (existsByTitle(seoMetaDto.getSeoMetaTitle())) {
            throw new IllegalArgumentException("SEO meta title already exists: " + seoMetaDto.getSeoMetaTitle());
        }
        
        if (seoMetaDto.getSeoMetaCanonicalUrl() != null && 
            existsByCanonicalUrl(seoMetaDto.getSeoMetaCanonicalUrl())) {
            throw new IllegalArgumentException("SEO meta canonical URL already exists: " + seoMetaDto.getSeoMetaCanonicalUrl());
        }
        
        SeoMeta seoMeta = SeoMetaMapperUtil.toSeoMetaEntity(seoMetaDto);
        SeoMeta savedSeoMeta = seoMetaRepository.save(seoMeta);
        
        logger.info("Successfully created SEO meta with ID: {}", savedSeoMeta.getSeoMetaId());
        return SeoMetaMapperUtil.toSeoMetaDto(savedSeoMeta);
    }

    @Override
    @Transactional
    public SeoMetaDto updateSeoMeta(Long id, SeoMetaDto seoMetaDto) {
        logger.info("Updating SEO meta ID: {}", id);
        
        SeoMeta existingSeoMeta = seoMetaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SEO meta not found with ID: " + id));
        
        // Validation - check if title/canonical URL exists for other records
        if (!existingSeoMeta.getSeoMetaTitle().equals(seoMetaDto.getSeoMetaTitle()) && 
            existsByTitle(seoMetaDto.getSeoMetaTitle())) {
            throw new IllegalArgumentException("SEO meta title already exists: " + seoMetaDto.getSeoMetaTitle());
        }
        
        if (seoMetaDto.getSeoMetaCanonicalUrl() != null && 
            !seoMetaDto.getSeoMetaCanonicalUrl().equals(existingSeoMeta.getSeoMetaCanonicalUrl()) &&
            existsByCanonicalUrl(seoMetaDto.getSeoMetaCanonicalUrl())) {
            throw new IllegalArgumentException("SEO meta canonical URL already exists: " + seoMetaDto.getSeoMetaCanonicalUrl());
        }
        
        // Update fields
        SeoMetaMapperUtil.updateSeoMetaEntity(existingSeoMeta, seoMetaDto);
        SeoMeta updatedSeoMeta = seoMetaRepository.save(existingSeoMeta);
        
        logger.info("Successfully updated SEO meta ID: {}", id);
        return SeoMetaMapperUtil.toSeoMetaDto(updatedSeoMeta);
    }

    @Override
    @Transactional
    public void deleteSeoMeta(Long id) {
        logger.info("Deleting SEO meta ID: {}", id);
        
        if (!seoMetaRepository.existsById(id)) {
            throw new IllegalArgumentException("SEO meta not found with ID: " + id);
        }
        
        seoMetaRepository.deleteById(id);
        logger.info("Successfully deleted SEO meta ID: {}", id);
    }

    // =============================================
    // VALIDATION OPERATIONS
    // =============================================

    @Override
    public boolean existsByTitle(String title) {
        return seoMetaRepository.existsBySeoMetaTitle(title);
    }

    @Override
    public boolean existsByCanonicalUrl(String canonicalUrl) {
        return seoMetaRepository.existsBySeoMetaCanonicalUrl(canonicalUrl);
    }
}