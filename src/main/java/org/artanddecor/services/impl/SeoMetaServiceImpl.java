package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.SeoMetaDto;
import org.artanddecor.dto.SeoMetaRequestDto;
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
        if (seoMetaDto.getSeoMetaTitle() != null && !seoMetaDto.getSeoMetaTitle().isBlank()
                && existsByTitle(seoMetaDto.getSeoMetaTitle())) {
            throw new IllegalArgumentException("SEO meta title already exists: " + seoMetaDto.getSeoMetaTitle());
        }
        
        if (seoMetaDto.getSeoMetaCanonicalUrl() != null && !seoMetaDto.getSeoMetaCanonicalUrl().isBlank()
                && existsByCanonicalUrl(seoMetaDto.getSeoMetaCanonicalUrl())) {
            throw new IllegalArgumentException("SEO meta canonical URL already exists: " + seoMetaDto.getSeoMetaCanonicalUrl());
        }
        
        SeoMeta seoMeta = SeoMetaMapperUtil.toSeoMetaEntity(seoMetaDto);
        SeoMeta savedSeoMeta = seoMetaRepository.save(seoMeta);
        
        logger.info("Successfully created SEO meta with ID: {}", savedSeoMeta.getSeoMetaId());
        return SeoMetaMapperUtil.toSeoMetaDto(savedSeoMeta);
    }

    @Override
    @Transactional
    public SeoMetaDto createSeoMetaFromRequest(SeoMetaRequestDto seoMetaRequestDto) {
        logger.info("Creating new SEO meta from request: {}", seoMetaRequestDto.getSeoMetaTitle());
        
        // Validation
        if (seoMetaRequestDto.getSeoMetaTitle() != null && !seoMetaRequestDto.getSeoMetaTitle().isBlank()
                && existsByTitle(seoMetaRequestDto.getSeoMetaTitle())) {
            throw new IllegalArgumentException("SEO meta title already exists: " + seoMetaRequestDto.getSeoMetaTitle());
        }
        
        if (seoMetaRequestDto.getSeoMetaCanonicalUrl() != null && !seoMetaRequestDto.getSeoMetaCanonicalUrl().isBlank()
                && existsByCanonicalUrl(seoMetaRequestDto.getSeoMetaCanonicalUrl())) {
            throw new IllegalArgumentException("SEO meta canonical URL already exists: " + seoMetaRequestDto.getSeoMetaCanonicalUrl());
        }
        
        SeoMeta seoMeta = SeoMetaMapperUtil.toSeoMetaEntityFromRequest(seoMetaRequestDto);
        SeoMeta savedSeoMeta = seoMetaRepository.save(seoMeta);
        
        logger.info("Successfully created SEO meta from request with ID: {}", savedSeoMeta.getSeoMetaId());
        return SeoMetaMapperUtil.toSeoMetaDto(savedSeoMeta);
    }

    @Override
    @Transactional
    public SeoMetaDto updateSeoMeta(Long id, SeoMetaDto seoMetaDto) {
        logger.info("Updating SEO meta ID: {}", id);
        
        SeoMeta existingSeoMeta = seoMetaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SEO meta not found with ID: " + id));
        
        // Validation - check if title/canonical URL exists for other records (null-safe)
        String newTitle = seoMetaDto.getSeoMetaTitle();
        String existingTitle = existingSeoMeta.getSeoMetaTitle();
        if (newTitle != null && !newTitle.isBlank() && !newTitle.equals(existingTitle)
                && existsByTitle(newTitle)) {
            throw new IllegalArgumentException("SEO meta title already exists: " + newTitle);
        }

        String newUrl = seoMetaDto.getSeoMetaCanonicalUrl();
        String existingUrl = existingSeoMeta.getSeoMetaCanonicalUrl();
        if (newUrl != null && !newUrl.isBlank() && !newUrl.equals(existingUrl)
                && existsByCanonicalUrl(newUrl)) {
            throw new IllegalArgumentException("SEO meta canonical URL already exists: " + newUrl);
        }
        
        // Update fields
        SeoMetaMapperUtil.updateSeoMetaEntity(existingSeoMeta, seoMetaDto);
        SeoMeta updatedSeoMeta = seoMetaRepository.save(existingSeoMeta);
        
        logger.info("Successfully updated SEO meta ID: {}", id);
        return SeoMetaMapperUtil.toSeoMetaDto(updatedSeoMeta);
    }

    @Override
    @Transactional
    public SeoMetaDto updateSeoMetaFromRequest(Long id, SeoMetaRequestDto seoMetaRequestDto) {
        logger.info("Updating SEO meta ID: {} from request", id);
        
        SeoMeta existingSeoMeta = seoMetaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SEO meta not found with ID: " + id));
        
        // Validation - check if title/canonical URL exists for other records (null-safe)
        String newTitle = seoMetaRequestDto.getSeoMetaTitle();
        String existingTitle = existingSeoMeta.getSeoMetaTitle();
        if (newTitle != null && !newTitle.isBlank() && !newTitle.equals(existingTitle)
                && existsByTitle(newTitle)) {
            throw new IllegalArgumentException("SEO meta title already exists: " + newTitle);
        }

        String newUrl = seoMetaRequestDto.getSeoMetaCanonicalUrl();
        String existingUrl = existingSeoMeta.getSeoMetaCanonicalUrl();
        if (newUrl != null && !newUrl.isBlank() && !newUrl.equals(existingUrl)
                && existsByCanonicalUrl(newUrl)) {
            throw new IllegalArgumentException("SEO meta canonical URL already exists: " + newUrl);
        }
        
        // Update fields directly — null values are saved as-is (no forced defaults)
        existingSeoMeta.setSeoMetaTitle(seoMetaRequestDto.getSeoMetaTitle());
        existingSeoMeta.setSeoMetaDescription(seoMetaRequestDto.getSeoMetaDescription());
        existingSeoMeta.setSeoMetaKeywords(seoMetaRequestDto.getSeoMetaKeywords());
        existingSeoMeta.setSeoMetaIndex(seoMetaRequestDto.getSeoMetaIndex());
        existingSeoMeta.setSeoMetaFollow(seoMetaRequestDto.getSeoMetaFollow());
        existingSeoMeta.setSeoMetaCanonicalUrl(seoMetaRequestDto.getSeoMetaCanonicalUrl());
        existingSeoMeta.setSeoMetaSchemaType(seoMetaRequestDto.getSeoMetaSchemaType());
        existingSeoMeta.setSeoMetaCustomJson(seoMetaRequestDto.getSeoMetaCustomJson());
        existingSeoMeta.setSeoMetaEnabled(seoMetaRequestDto.getSeoMetaEnabled());
        
        SeoMeta updatedSeoMeta = seoMetaRepository.save(existingSeoMeta);
        
        logger.info("Successfully updated SEO meta ID: {} from request", id);
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