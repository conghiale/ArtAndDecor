package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.BlogTypeDto;
import org.artanddecor.dto.BlogTypeRequest;
import org.artanddecor.dto.SeoMetaDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.BlogType;
import org.artanddecor.model.Image;
import org.artanddecor.model.SeoMeta;
import org.artanddecor.repository.BlogTypeRepository;
import org.artanddecor.repository.BlogCategoryRepository;
import org.artanddecor.repository.ImageRepository;
import org.artanddecor.repository.SeoMetaRepository;
import org.artanddecor.services.BlogTypeService;
import org.artanddecor.services.SeoMetaService;
import org.artanddecor.utils.BlogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * BlogType Service Implementation
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BlogTypeServiceImpl implements BlogTypeService {

    private static final Logger logger = LoggerFactory.getLogger(BlogTypeServiceImpl.class);

    private final BlogTypeRepository blogTypeRepository;
    private final BlogCategoryRepository blogCategoryRepository;
    private final ImageRepository imageRepository;
    private final SeoMetaRepository seoMetaRepository;
    private final SeoMetaService seoMetaService;
    private final BlogMapper blogMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<BlogTypeDto> getBlogTypes(String blogTypeName, Boolean blogTypeEnabled, String blogTypeSlug, Pageable pageable) {
        logger.info("Getting blog types with filters - name: {}, enabled: {}, slug: {}", blogTypeName, blogTypeEnabled, blogTypeSlug);
        
        Page<BlogType> blogTypes = blogTypeRepository.findWithFilters(blogTypeName, blogTypeEnabled, blogTypeSlug, pageable);
        return blogTypes.map(blogMapper::toBlogTypeDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogTypeDto getBlogTypeById(Long blogTypeId) {
        logger.info("Getting blog type by ID: {}", blogTypeId);
        
        BlogType blogType = blogTypeRepository.findById(blogTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog type not found with ID: " + blogTypeId));
        
        return blogMapper.toBlogTypeDto(blogType);
    }

    @Override
    public BlogTypeDto updateBlogTypeStatus(Long blogTypeId, Boolean enabled) {
        logger.info("Updating blog type status - ID: {}, enabled: {}", blogTypeId, enabled);
        
        BlogType blogType = blogTypeRepository.findById(blogTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog type not found with ID: " + blogTypeId));
        
        blogType.setBlogTypeEnabled(enabled);
        blogType.setModifiedDt(LocalDateTime.now());
        
        BlogType savedBlogType = blogTypeRepository.save(blogType);
        return blogMapper.toBlogTypeDto(savedBlogType);
    }

    @Override
    public BlogTypeDto updateBlogType(Long blogTypeId, BlogTypeRequest request) {
        logger.info("Updating blog type {} with common request", blogTypeId);

        BlogType existingBlogType = blogTypeRepository.findById(blogTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("BlogType not found with ID: " + blogTypeId));

        // Check unique constraints
        if (!existingBlogType.getBlogTypeName().equals(request.getBlogTypeName()) &&
            blogTypeRepository.findByBlogTypeName(request.getBlogTypeName()).isPresent()) {
            throw new IllegalArgumentException("Blog type name already exists: " + request.getBlogTypeName());
        }

        if (!existingBlogType.getBlogTypeSlug().equals(request.getBlogTypeSlug()) &&
            blogTypeRepository.findByBlogTypeSlug(request.getBlogTypeSlug()).isPresent()) {
            throw new IllegalArgumentException("Blog type slug already exists: " + request.getBlogTypeSlug());
        }

        blogMapper.updateBlogTypeFromRequest(existingBlogType, request);
        existingBlogType.setModifiedDt(LocalDateTime.now());

        // Handle image relationship
        if (request.getImageId() != null) {
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + request.getImageId()));
            existingBlogType.setImage(image);
        } else {
            existingBlogType.setImage(null);
        }

        // Handle SeoMeta relationship
        if (request.getSeoMeta() != null) {
            if (existingBlogType.getSeoMetaId() != null) {
                // BlogType already has SEO meta → update it (avoid false duplicate error)
                seoMetaService.updateSeoMetaFromRequest(existingBlogType.getSeoMetaId(), request.getSeoMeta());
            } else {
                // BlogType has no SEO meta yet → create new
                SeoMetaDto createdSeoMeta = seoMetaService.createSeoMetaFromRequest(request.getSeoMeta());
                existingBlogType.setSeoMetaId(createdSeoMeta.getSeoMetaId());
            }
        } else {
            // Request has no SEO meta → remove SEO meta and delete orphan
            if (existingBlogType.getSeoMetaId() != null) {
                Long oldSeoMetaId = existingBlogType.getSeoMetaId();
                existingBlogType.setSeoMetaId(null);
                seoMetaService.deleteSeoMeta(oldSeoMetaId);
            }
        }

        BlogType savedBlogType = blogTypeRepository.save(existingBlogType);
        return blogMapper.toBlogTypeDto(savedBlogType);
    }

    @Override
    public BlogTypeDto createBlogType(BlogTypeRequest request) {
        logger.info("Creating new blog type with common request: {}", request.getBlogTypeName());

        // Check unique constraints
        if (blogTypeRepository.findByBlogTypeName(request.getBlogTypeName()).isPresent()) {
            throw new IllegalArgumentException("Blog type name already exists: " + request.getBlogTypeName());
        }
        
        if (blogTypeRepository.findByBlogTypeSlug(request.getBlogTypeSlug()).isPresent()) {
            throw new IllegalArgumentException("Blog type slug already exists: " + request.getBlogTypeSlug());
        }

        BlogType blogType = blogMapper.toBlogTypeEntity(request);
        blogType.setCreatedDt(LocalDateTime.now());
        blogType.setModifiedDt(LocalDateTime.now());

        // Handle image relationship
        if (request.getImageId() != null) {
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + request.getImageId()));
            blogType.setImage(image);
        }

        // Handle SeoMeta relationship
        if (request.getSeoMeta() != null) {
            SeoMetaDto createdSeoMeta = seoMetaService.createSeoMetaFromRequest(request.getSeoMeta());
            blogType.setSeoMetaId(createdSeoMeta.getSeoMetaId());
        }

        BlogType savedBlogType = blogTypeRepository.save(blogType);
        return blogMapper.toBlogTypeDto(savedBlogType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBlogType(Long blogTypeId) {
        logger.info("Start delete blogType, id={}", blogTypeId);

        if (blogTypeId == null || blogTypeId <= 0) {
            logger.warn("Invalid blogType id for delete, id={}", blogTypeId);
            throw new IllegalArgumentException("ID loại bài viết không hợp lệ.");
        }

        BlogType blogType = blogTypeRepository.findById(blogTypeId)
                .orElseThrow(() -> {
                    logger.warn("BlogType not found for delete, id={}", blogTypeId);
                    return new IllegalArgumentException("Không tìm thấy loại bài viết cần xoá.");
                });

        Long seoMetaId = blogType.getSeoMetaId();

        logger.debug("Checking references before deleting blogType, id={}", blogTypeId);
        Long referencedCount = blogCategoryRepository.countByBlogType_BlogTypeId(blogTypeId);
        Long sampleReferencedId = blogCategoryRepository.findSampleBlogCategoryIdByBlogTypeId(blogTypeId);
        if (referencedCount != null && referencedCount > 0) {
            logger.warn(
                    "Cannot delete BLOG_TYPE, id={}, referencedTable={}, referencedColumn={}, referencedCount={}, sampleReferencedId={}",
                    blogTypeId, "BLOG_CATEGORY", "BLOG_TYPE_ID", referencedCount, sampleReferencedId);
            String referenceDetail = String.format(
                "Cannot delete BLOG_TYPE, id=%s, referencedTable=%s, referencedColumn=%s, referencedCount=%s, sampleReferencedId=%s",
                blogTypeId, "BLOG_CATEGORY", "BLOG_TYPE_ID", referencedCount, sampleReferencedId);
            throw new IllegalStateException(referenceDetail);
        }

        blogTypeRepository.delete(blogType);

        if (seoMetaId != null) {
            seoMetaService.deleteSeoMeta(seoMetaId);
            logger.info("Deleted associated SEO meta for blogType, blogTypeId={}, seoMetaId={}", blogTypeId, seoMetaId);
        }

        logger.info("Deleted blogType successfully, id={}", blogTypeId);
    }
}