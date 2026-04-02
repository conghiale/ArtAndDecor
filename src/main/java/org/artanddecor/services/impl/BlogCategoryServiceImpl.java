package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.BlogCategoryDto;
import org.artanddecor.dto.BlogCategoryRequest;
import org.artanddecor.dto.SeoMetaDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.BlogCategory;
import org.artanddecor.model.BlogType;
import org.artanddecor.model.Image;
import org.artanddecor.model.SeoMeta;
import org.artanddecor.repository.BlogCategoryRepository;
import org.artanddecor.repository.BlogTypeRepository;
import org.artanddecor.repository.ImageRepository;
import org.artanddecor.repository.SeoMetaRepository;
import org.artanddecor.services.BlogCategoryService;
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
 * BlogCategory Service Implementation
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BlogCategoryServiceImpl implements BlogCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(BlogCategoryServiceImpl.class);

    private final BlogCategoryRepository blogCategoryRepository;
    private final BlogTypeRepository blogTypeRepository;
    private final ImageRepository imageRepository;
    private final SeoMetaRepository seoMetaRepository;
    private final SeoMetaService seoMetaService;
    private final BlogMapper blogMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<BlogCategoryDto> getBlogCategories(String blogCategoryName, Boolean blogCategoryEnabled, 
                                                 String blogCategorySlug, Long blogTypeId, Pageable pageable) {
        logger.info("Getting blog categories with filters - name: {}, enabled: {}, slug: {}, typeId: {}", 
                   blogCategoryName, blogCategoryEnabled, blogCategorySlug, blogTypeId);
        
        Page<BlogCategory> blogCategories = blogCategoryRepository.findWithFilters(
            blogCategoryName, blogCategoryEnabled, blogCategorySlug, blogTypeId, pageable);
        return blogCategories.map(blogMapper::toBlogCategoryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogCategoryDto getBlogCategoryById(Long blogCategoryId) {
        logger.info("Getting blog category by ID: {}", blogCategoryId);
        
        BlogCategory blogCategory = blogCategoryRepository.findById(blogCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog category not found with ID: " + blogCategoryId));
        
        return blogMapper.toBlogCategoryDto(blogCategory);
    }

    @Override
    public BlogCategoryDto updateBlogCategoryStatus(Long blogCategoryId, Boolean enabled) {
        logger.info("Updating blog category status - ID: {}, enabled: {}", blogCategoryId, enabled);
        
        BlogCategory blogCategory = blogCategoryRepository.findById(blogCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog category not found with ID: " + blogCategoryId));
        
        blogCategory.setBlogCategoryEnabled(enabled);
        blogCategory.setModifiedDt(LocalDateTime.now());
        
        BlogCategory savedBlogCategory = blogCategoryRepository.save(blogCategory);
        return blogMapper.toBlogCategoryDto(savedBlogCategory);
    }

    @Override
    public BlogCategoryDto updateBlogCategory(Long blogCategoryId, BlogCategoryRequest request) {
        logger.info("Updating blog category {} with common request", blogCategoryId);

        BlogCategory existingBlogCategory = blogCategoryRepository.findById(blogCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("BlogCategory not found with ID: " + blogCategoryId));

        // Check unique constraints
        if (!existingBlogCategory.getBlogCategoryName().equals(request.getBlogCategoryName()) &&
            blogCategoryRepository.findByBlogCategoryName(request.getBlogCategoryName()).isPresent()) {
            throw new IllegalArgumentException("Blog category name already exists: " + request.getBlogCategoryName());
        }

        if (!existingBlogCategory.getBlogCategorySlug().equals(request.getBlogCategorySlug()) &&
            blogCategoryRepository.findByBlogCategorySlug(request.getBlogCategorySlug()).isPresent()) {
            throw new IllegalArgumentException("Blog category slug already exists: " + request.getBlogCategorySlug());
        }

        blogMapper.updateBlogCategoryFromRequest(existingBlogCategory, request);
        existingBlogCategory.setModifiedDt(LocalDateTime.now());

        // Handle BlogType relationship
        BlogType blogType = blogTypeRepository.findById(request.getBlogTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("BlogType not found with ID: " + request.getBlogTypeId()));
        existingBlogCategory.setBlogType(blogType);

        // Handle image relationship
        if (request.getImageId() != null) {
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + request.getImageId()));
            existingBlogCategory.setImage(image);
        } else {
            existingBlogCategory.setImage(null);
        }

        // Handle SeoMeta relationship
        if (request.getSeoMeta() != null) {
            SeoMetaDto createdSeoMeta = seoMetaService.createSeoMetaFromRequest(request.getSeoMeta());
            existingBlogCategory.setSeoMetaId(createdSeoMeta.getSeoMetaId());
        } else {
            existingBlogCategory.setSeoMetaId(null);
        }

        BlogCategory savedBlogCategory = blogCategoryRepository.save(existingBlogCategory);
        return blogMapper.toBlogCategoryDto(savedBlogCategory);
    }

    @Override
    public BlogCategoryDto createBlogCategory(BlogCategoryRequest request) {
        logger.info("Creating new blog category with common request: {}", request.getBlogCategoryName());

        // Check unique constraints
        if (blogCategoryRepository.findByBlogCategoryName(request.getBlogCategoryName()).isPresent()) {
            throw new IllegalArgumentException("Blog category name already exists: " + request.getBlogCategoryName());
        }
        
        if (blogCategoryRepository.findByBlogCategorySlug(request.getBlogCategorySlug()).isPresent()) {
            throw new IllegalArgumentException("Blog category slug already exists: " + request.getBlogCategorySlug());
        }

        BlogCategory blogCategory = blogMapper.toBlogCategoryEntity(request);
        blogCategory.setCreatedDt(LocalDateTime.now());
        blogCategory.setModifiedDt(LocalDateTime.now());

        // Handle BlogType relationship
        BlogType blogType = blogTypeRepository.findById(request.getBlogTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("BlogType not found with ID: " + request.getBlogTypeId()));
        blogCategory.setBlogType(blogType);

        // Handle image relationship
        if (request.getImageId() != null) {
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + request.getImageId()));
            blogCategory.setImage(image);
        }

        // Handle SeoMeta relationship
        if (request.getSeoMeta() != null) {
            SeoMetaDto createdSeoMeta = seoMetaService.createSeoMetaFromRequest(request.getSeoMeta());
            blogCategory.setSeoMetaId(createdSeoMeta.getSeoMetaId());
        }

        BlogCategory savedBlogCategory = blogCategoryRepository.save(blogCategory);
        return blogMapper.toBlogCategoryDto(savedBlogCategory);
    }
}