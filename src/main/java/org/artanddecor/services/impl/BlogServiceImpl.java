package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.BlogDto;
import org.artanddecor.dto.BlogRequest;
import org.artanddecor.dto.SeoMetaDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.Blog;
import org.artanddecor.model.BlogCategory;
import org.artanddecor.model.SeoMeta;
import org.artanddecor.model.Image;
import org.artanddecor.repository.BlogRepository;
import org.artanddecor.repository.BlogCategoryRepository;
import org.artanddecor.repository.SeoMetaRepository;
import org.artanddecor.repository.ImageRepository;
import org.artanddecor.services.BlogService;
import org.artanddecor.services.SeoMetaService;
import org.artanddecor.utils.BlogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Blog Service Implementation
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BlogServiceImpl implements BlogService {

    private static final Logger logger = LoggerFactory.getLogger(BlogServiceImpl.class);

    private final BlogRepository blogRepository;
    private final BlogCategoryRepository blogCategoryRepository;
    private final SeoMetaRepository seoMetaRepository;
    private final SeoMetaService seoMetaService;
    private final ImageRepository imageRepository;
    private final BlogMapper blogMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<BlogDto> getBlogs(String blogTitle, Boolean blogEnabled, Long blogCategoryId, 
                                 LocalDate fromDate, LocalDate toDate, String blogCategorySlug, String blogTypeSlug, Pageable pageable) {
        logger.info("Getting blogs with filters - title: {}, enabled: {}, categoryId: {}, fromDate: {}, toDate: {}, blogCategorySlug: {}, blogTypeSlug: {}", 
                   blogTitle, blogEnabled, blogCategoryId, fromDate, toDate, blogCategorySlug, blogTypeSlug);
        
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;
        
        Page<Blog> blogs = blogRepository.findWithFilters(
            blogTitle, blogEnabled, blogCategoryId, fromDateTime, toDateTime, blogCategorySlug, blogTypeSlug, pageable);
        return blogs.map(blogMapper::toBlogDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogDto getBlogById(Long blogId) {
        logger.info("Getting blog by ID: {}", blogId);
        
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with ID: " + blogId));
        
        return blogMapper.toBlogDto(blog);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogDto getBlogBySlug(String blogSlug) {
        logger.info("Getting blog by slug: {}", blogSlug);
        
        Blog blog = blogRepository.findByBlogSlug(blogSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with slug: " + blogSlug));
        
        return blogMapper.toBlogDto(blog);
    }

    @Override
    public BlogDto updateBlogStatus(Long blogId, Boolean enabled) {
        logger.info("Updating blog status - ID: {}, enabled: {}", blogId, enabled);
        
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with ID: " + blogId));
        
        blog.setBlogEnabled(enabled);
        blog.setModifiedDt(LocalDateTime.now());
        
        Blog savedBlog = blogRepository.save(blog);
        return blogMapper.toBlogDto(savedBlog);
    }

    @Override
    public BlogDto updateBlog(Long blogId, BlogRequest request) {
        logger.info("Updating blog {} with common request", blogId);

        Blog existingBlog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with ID: " + blogId));

        // Check unique constraints
        if (!existingBlog.getBlogSlug().equals(request.getBlogSlug()) &&
            blogRepository.findByBlogSlug(request.getBlogSlug()).isPresent()) {
            throw new IllegalArgumentException("Blog slug already exists: " + request.getBlogSlug());
        }

        blogMapper.updateBlogFromRequest(existingBlog, request);
        existingBlog.setModifiedDt(LocalDateTime.now());

        // Handle BlogCategory relationship
        BlogCategory blogCategory = blogCategoryRepository.findById(request.getBlogCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("BlogCategory not found with ID: " + request.getBlogCategoryId()));
        existingBlog.setBlogCategory(blogCategory);

        // Handle SeoMeta relationship
        if (request.getSeoMeta() != null) {
            SeoMetaDto createdSeoMeta = seoMetaService.createSeoMetaFromRequest(request.getSeoMeta());
            existingBlog.setSeoMetaId(createdSeoMeta.getSeoMetaId());
        } else {
            existingBlog.setSeoMetaId(null);
        }

        // Handle Image relationship
        if (request.getImageId() != null) {
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + request.getImageId()));
            existingBlog.setImageId(image.getImageId());
        } else {
            existingBlog.setImageId(null);
        }

        Blog savedBlog = blogRepository.save(existingBlog);
        return blogMapper.toBlogDto(savedBlog);
    }

    @Override
    public BlogDto createBlog(BlogRequest request) {
        logger.info("Creating new blog with common request: {}", request.getBlogTitle());

        // Check unique constraints
        if (blogRepository.findByBlogSlug(request.getBlogSlug()).isPresent()) {
            throw new IllegalArgumentException("Blog slug already exists: " + request.getBlogSlug());
        }

        Blog blog = blogMapper.toBlogEntity(request);
        blog.setCreatedDt(LocalDateTime.now());
        blog.setModifiedDt(LocalDateTime.now());

        // Handle BlogCategory relationship
        BlogCategory blogCategory = blogCategoryRepository.findById(request.getBlogCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("BlogCategory not found with ID: " + request.getBlogCategoryId()));
        blog.setBlogCategory(blogCategory);

        // Handle SeoMeta relationship
        if (request.getSeoMeta() != null) {
            SeoMetaDto createdSeoMeta = seoMetaService.createSeoMetaFromRequest(request.getSeoMeta());
            blog.setSeoMetaId(createdSeoMeta.getSeoMetaId());
        }

        // Handle Image relationship
        if (request.getImageId() != null) {
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + request.getImageId()));
            blog.setImageId(image.getImageId());
        }

        Blog savedBlog = blogRepository.save(blog);
        return blogMapper.toBlogDto(savedBlog);
    }
}