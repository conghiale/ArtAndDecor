package org.artanddecor.services;

import org.artanddecor.dto.BlogTypeDto;
import org.artanddecor.dto.BlogTypeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * BlogType Service Interface
 */
public interface BlogTypeService {

    /**
     * Get blog types with filtering and pagination
     */
    Page<BlogTypeDto> getBlogTypes(String blogTypeName, Boolean blogTypeEnabled, String blogTypeSlug, Pageable pageable);

    /**
     * Get blog type by ID
     */
    BlogTypeDto getBlogTypeById(Long blogTypeId);

    /**
     * Update blog type status (enabled/disabled)
     */
    BlogTypeDto updateBlogTypeStatus(Long blogTypeId, Boolean enabled);

    /**
     * Update blog type
     */
    BlogTypeDto updateBlogType(Long blogTypeId, BlogTypeRequest request);

    /**
     * Create new blog type
     */
    BlogTypeDto createBlogType(BlogTypeRequest request);
}