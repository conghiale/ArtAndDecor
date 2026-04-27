package org.artanddecor.services;

import org.artanddecor.dto.BlogDto;
import org.artanddecor.dto.BlogRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Blog Service Interface
 */
public interface BlogService {

    /**
     * Get blogs with filtering and pagination
     */
    Page<BlogDto> getBlogs(String blogTitle, Boolean blogEnabled, Long blogCategoryId, 
                          LocalDate fromDate, LocalDate toDate, String blogCategorySlug, String blogTypeSlug, Pageable pageable);

    /**
     * Get blog by ID
     */
    BlogDto getBlogById(Long blogId);

    /**
     * Get blog by slug
     */
    BlogDto getBlogBySlug(String blogSlug);

    /**
     * Update blog status (enabled/disabled)
     */
    BlogDto updateBlogStatus(Long blogId, Boolean enabled);

    /**
     * Update blog
     */
    BlogDto updateBlog(Long blogId, BlogRequest request);

    /**
     * Create new blog
     */
    BlogDto createBlog(BlogRequest request);
}