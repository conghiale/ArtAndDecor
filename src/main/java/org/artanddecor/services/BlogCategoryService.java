package org.artanddecor.services;

import org.artanddecor.dto.BlogCategoryDto;
import org.artanddecor.dto.BlogCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * BlogCategory Service Interface
 */
public interface BlogCategoryService {

    /**
     * Get blog categories with filtering and pagination
     */
    Page<BlogCategoryDto> getBlogCategories(String blogCategoryName, Boolean blogCategoryEnabled, 
                                          String blogCategorySlug, Long blogTypeId, Pageable pageable);

    /**
     * Get blog category by ID
     */
    BlogCategoryDto getBlogCategoryById(Long blogCategoryId);

    /**
     * Update blog category status (enabled/disabled)
     */
    BlogCategoryDto updateBlogCategoryStatus(Long blogCategoryId, Boolean enabled);

    /**
     * Update blog category
     */
    BlogCategoryDto updateBlogCategory(Long blogCategoryId, BlogCategoryRequest request);

    /**
     * Create new blog category
     */
    BlogCategoryDto createBlogCategory(BlogCategoryRequest request);
}