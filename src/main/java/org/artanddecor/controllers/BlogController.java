package org.artanddecor.controllers;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.*;
import org.artanddecor.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Map;

/**
 * Blog Management REST Controller
 * Handles BlogType, BlogCategory, and Blog operations
 */
@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
@Tag(name = "Blog Management", description = "Blog Management APIs")
public class BlogController {

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

    private final BlogTypeService blogTypeService;
    private final BlogCategoryService blogCategoryService;
    private final BlogService blogService;

    // ===== BLOG TYPE APIs =====

    /**
     * Get Blog Types with filtering and pagination
     * Role: permitAll - Both ADMIN and CUSTOMER can view blog types
     */
    @GetMapping("/types")
    @Operation(
        summary = "Get blog types with filtering",
        description = "Retrieve blog types with optional filtering by name, enabled status, and slug. Supports pagination.",
        tags = {"Blog Type Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Blog types retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        )
    })
    public ResponseEntity<BaseResponseDto<Page<BlogTypeDto>>> getBlogTypes(
            @Parameter(description = "Filter by blog type name (optional)")
            @RequestParam(required = false) String blogTypeName,
            
            @Parameter(description = "Filter by enabled status (optional)")
            @RequestParam(required = false) Boolean blogTypeEnabled,
            
            @Parameter(description = "Filter by blog type slug (optional)")
            @RequestParam(required = false) String blogTypeSlug,
            
            @PageableDefault(page = 0, size = 10, sort = "blogTypeName", direction = Sort.Direction.ASC) Pageable pageable) {
        
        try {
            Page<BlogTypeDto> blogTypes = blogTypeService.getBlogTypes(
                blogTypeName, blogTypeEnabled, blogTypeSlug, pageable);
            
            return ResponseEntity.ok(BaseResponseDto.success("Blog types retrieved successfully", blogTypes));
        } catch (Exception e) {
            logger.error("Error getting blog types: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get blog types: " + e.getMessage()));
        }
    }

    /**
     * Get Blog Type by ID
     * Role: ADMIN only
     */
    @GetMapping("/types/{blogTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get blog type by ID",
        description = "Retrieve a specific blog type by its ID.",
        tags = {"Blog Type Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<BlogTypeDto>> getBlogTypeById(
            @Parameter(description = "Blog Type ID", required = true)
            @PathVariable Long blogTypeId) {
        
        try {
            BlogTypeDto blogType = blogTypeService.getBlogTypeById(blogTypeId);
            return ResponseEntity.ok(BaseResponseDto.success("Blog type retrieved successfully", blogType));
        } catch (Exception e) {
            logger.error("Error getting blog type by ID: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get blog type: " + e.getMessage()));
        }
    }

    /**
     * Update Blog Type status
     * Role: ADMIN only
     */
    @PatchMapping("/types/{blogTypeId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update blog type status",
        description = "Update the enabled status of a blog type.",
        tags = {"Blog Type Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<BlogTypeDto>> updateBlogTypeStatus(
            @Parameter(description = "Blog Type ID", required = true)
            @PathVariable Long blogTypeId,
            
            @Parameter(description = "Status update request", required = true)
            @RequestBody Map<String, Boolean> statusRequest) {
        
        try {
            Boolean enabled = statusRequest.get("enabled");
            if (enabled == null) {
                return ResponseEntity.badRequest()
                        .body(BaseResponseDto.badRequest("'enabled' field is required"));
            }
            
            BlogTypeDto updatedBlogType = blogTypeService.updateBlogTypeStatus(blogTypeId, enabled);
            return ResponseEntity.ok(BaseResponseDto.success("Blog type status updated successfully", updatedBlogType));
        } catch (Exception e) {
            logger.error("Error updating blog type status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to update blog type status: " + e.getMessage()));
        }
    }

    /**
     * Update Blog Type
     * Role: ADMIN only
     */
    @PutMapping("/types/{blogTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update blog type",
        description = "Update blog type information.",
        tags = {"Blog Type Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<BlogTypeDto>> updateBlogType(
            @Parameter(description = "Blog Type ID", required = true)
            @PathVariable Long blogTypeId,
            
            @Parameter(description = "Blog type update data", required = true)
            @Valid @RequestBody BlogTypeRequest request) {
        
        try {
            BlogTypeDto updatedBlogType = blogTypeService.updateBlogType(blogTypeId, request);
            return ResponseEntity.ok(BaseResponseDto.success("Blog type updated successfully", updatedBlogType));
        } catch (Exception e) {
            logger.error("Error updating blog type: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to update blog type: " + e.getMessage()));
        }
    }

    /**
     * Create new Blog Type
     * Role: ADMIN only
     */
    @PostMapping("/types")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create new blog type",
        description = "Create a new blog type.",
        tags = {"Blog Type Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<BlogTypeDto>> createBlogType(
            @Parameter(description = "Blog type creation data", required = true)
            @Valid @RequestBody BlogTypeRequest request) {
        
        try {
            BlogTypeDto newBlogType = blogTypeService.createBlogType(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Blog type created successfully", newBlogType));
        } catch (Exception e) {
            logger.error("Error creating blog type: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to create blog type: " + e.getMessage()));
        }
    }

    // ===== BLOG CATEGORY APIs =====

    /**
     * Get Blog Categories with filtering and pagination
     * Role: permitAll - Both ADMIN and CUSTOMER can view blog categories
     */
    @GetMapping("/categories")
    @Operation(
        summary = "Get blog categories with filtering",
        description = "Retrieve blog categories with optional filtering by name, enabled status, slug, and blog type. Supports pagination.",
        tags = {"Blog Category Management"}
    )
    public ResponseEntity<BaseResponseDto<Page<BlogCategoryDto>>> getBlogCategories(
            @Parameter(description = "Filter by blog category name (optional)")
            @RequestParam(required = false) String blogCategoryName,
            
            @Parameter(description = "Filter by enabled status (optional)")
            @RequestParam(required = false) Boolean blogCategoryEnabled,
            
            @Parameter(description = "Filter by blog category slug (optional)")
            @RequestParam(required = false) String blogCategorySlug,
            
            @Parameter(description = "Filter by blog type ID (optional)")
            @RequestParam(required = false) Long blogTypeId,
            
            @PageableDefault(page = 0, size = 10, sort = "blogCategoryName", direction = Sort.Direction.ASC) Pageable pageable) {
        
        try {
            Page<BlogCategoryDto> blogCategories = blogCategoryService.getBlogCategories(
                blogCategoryName, blogCategoryEnabled, blogCategorySlug, blogTypeId, pageable);
            
            return ResponseEntity.ok(BaseResponseDto.success("Blog categories retrieved successfully", blogCategories));
        } catch (Exception e) {
            logger.error("Error getting blog categories: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get blog categories: " + e.getMessage()));
        }
    }

    /**
     * Get Blog Category by ID
     * Role: ADMIN only
     */
    @GetMapping("/categories/{blogCategoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get blog category by ID",
        description = "Retrieve a specific blog category by its ID.",
        tags = {"Blog Category Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<BlogCategoryDto>> getBlogCategoryById(
            @Parameter(description = "Blog Category ID", required = true)
            @PathVariable Long blogCategoryId) {
        
        try {
            BlogCategoryDto blogCategory = blogCategoryService.getBlogCategoryById(blogCategoryId);
            return ResponseEntity.ok(BaseResponseDto.success("Blog category retrieved successfully", blogCategory));
        } catch (Exception e) {
            logger.error("Error getting blog category by ID: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get blog category: " + e.getMessage()));
        }
    }

    /**
     * Update Blog Category status
     * Role: ADMIN only
     */
    @PatchMapping("/categories/{blogCategoryId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update blog category status",
        description = "Update the enabled status of a blog category.",
        tags = {"Blog Category Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<BlogCategoryDto>> updateBlogCategoryStatus(
            @Parameter(description = "Blog Category ID", required = true)
            @PathVariable Long blogCategoryId,
            
            @Parameter(description = "Status update request", required = true)
            @RequestBody Map<String, Boolean> statusRequest) {
        
        try {
            Boolean enabled = statusRequest.get("enabled");
            if (enabled == null) {
                return ResponseEntity.badRequest()
                        .body(BaseResponseDto.badRequest("'enabled' field is required"));
            }
            
            BlogCategoryDto updatedBlogCategory = blogCategoryService.updateBlogCategoryStatus(blogCategoryId, enabled);
            return ResponseEntity.ok(BaseResponseDto.success("Blog category status updated successfully", updatedBlogCategory));
        } catch (Exception e) {
            logger.error("Error updating blog category status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to update blog category status: " + e.getMessage()));
        }
    }

    /**
     * Update Blog Category
     * Role: ADMIN only
     */
    @PutMapping("/categories/{blogCategoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update blog category",
        description = "Update blog category information.",
        tags = {"Blog Category Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<BlogCategoryDto>> updateBlogCategory(
            @Parameter(description = "Blog Category ID", required = true)
            @PathVariable Long blogCategoryId,
            
            @Parameter(description = "Blog category update data", required = true)
            @Valid @RequestBody BlogCategoryRequest request) {
        
        try {
            BlogCategoryDto updatedBlogCategory = blogCategoryService.updateBlogCategory(blogCategoryId, request);
            return ResponseEntity.ok(BaseResponseDto.success("Blog category updated successfully", updatedBlogCategory));
        } catch (Exception e) {
            logger.error("Error updating blog category: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to update blog category: " + e.getMessage()));
        }
    }

    /**
     * Create new Blog Category
     * Role: ADMIN only
     */
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create new blog category",
        description = "Create a new blog category.",
        tags = {"Blog Category Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<BlogCategoryDto>> createBlogCategory(
            @Parameter(description = "Blog category creation data", required = true)
            @Valid @RequestBody BlogCategoryRequest request) {
        
        try {
            BlogCategoryDto newBlogCategory = blogCategoryService.createBlogCategory(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Blog category created successfully", newBlogCategory));
        } catch (Exception e) {
            logger.error("Error creating blog category: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to create blog category: " + e.getMessage()));
        }
    }

    // ===== BLOG APIs =====

    /**
     * Get Blogs with filtering and pagination
     * Role: permitAll - Both ADMIN and CUSTOMER can view blogs
     */
    @GetMapping
    @Operation(
        summary = "Get blogs with filtering",
        description = "Retrieve blogs with optional filtering by title, enabled status, category, and date range. Supports pagination.",
        tags = {"Blog Management"}
    )
    public ResponseEntity<BaseResponseDto<Page<BlogDto>>> getBlogs(
            @Parameter(description = "Filter by blog title (optional)")
            @RequestParam(required = false) String blogTitle,
            
            @Parameter(description = "Filter by enabled status (optional)")
            @RequestParam(required = false) Boolean blogEnabled,
            
            @Parameter(description = "Filter by blog category ID (optional)")
            @RequestParam(required = false) Long blogCategoryId,
            
            @Parameter(description = "Filter blogs from this date (optional). Format: YYYY-MM-DD")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            
            @Parameter(description = "Filter blogs to this date (optional). Format: YYYY-MM-DD")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            
            @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        try {
            Page<BlogDto> blogs = blogService.getBlogs(
                blogTitle, blogEnabled, blogCategoryId, fromDate, toDate, pageable);
            
            return ResponseEntity.ok(BaseResponseDto.success("Blogs retrieved successfully", blogs));
        } catch (Exception e) {
            logger.error("Error getting blogs: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get blogs: " + e.getMessage()));
        }
    }

    /**
     * Get Blog by ID
     * Role: ADMIN only
     */
    @GetMapping("/{blogId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get blog by ID",
        description = "Retrieve a specific blog by its ID.",
        tags = {"Blog Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<BlogDto>> getBlogById(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable Long blogId) {
        
        try {
            BlogDto blog = blogService.getBlogById(blogId);
            return ResponseEntity.ok(BaseResponseDto.success("Blog retrieved successfully", blog));
        } catch (Exception e) {
            logger.error("Error getting blog by ID: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get blog: " + e.getMessage()));
        }
    }

    /**
     * Get Blog by slug
     * Role: permitAll - Both ADMIN and CUSTOMER can view blogs by slug
     */
    @GetMapping("/slug/{blogSlug}")
    @Operation(
        summary = "Get blog by slug",
        description = "Retrieve a specific blog by its slug for public viewing.",
        tags = {"Blog Management"}
    )
    public ResponseEntity<BaseResponseDto<BlogDto>> getBlogBySlug(
            @Parameter(description = "Blog slug", required = true)
            @PathVariable String blogSlug) {
        
        try {
            BlogDto blog = blogService.getBlogBySlug(blogSlug);
            return ResponseEntity.ok(BaseResponseDto.success("Blog retrieved successfully", blog));
        } catch (Exception e) {
            logger.error("Error getting blog by slug: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get blog: " + e.getMessage()));
        }
    }

    /**
     * Update Blog status
     * Role: ADMIN only
     */
    @PatchMapping("/{blogId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update blog status",
        description = "Update the enabled status of a blog.",
        tags = {"Blog Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<BlogDto>> updateBlogStatus(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable Long blogId,
            
            @Parameter(description = "Status update request", required = true)
            @RequestBody Map<String, Boolean> statusRequest) {
        
        try {
            Boolean enabled = statusRequest.get("enabled");
            if (enabled == null) {
                return ResponseEntity.badRequest()
                        .body(BaseResponseDto.badRequest("'enabled' field is required"));
            }
            
            BlogDto updatedBlog = blogService.updateBlogStatus(blogId, enabled);
            return ResponseEntity.ok(BaseResponseDto.success("Blog status updated successfully", updatedBlog));
        } catch (Exception e) {
            logger.error("Error updating blog status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to update blog status: " + e.getMessage()));
        }
    }

    /**
     * Update Blog
     * Role: ADMIN only
     */
    @PutMapping("/{blogId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update blog",
        description = "Update blog information.",
        tags = {"Blog Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<BlogDto>> updateBlog(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable Long blogId,
            
            @Parameter(description = "Blog update data", required = true)
            @Valid @RequestBody BlogRequest request) {
        
        try {
            BlogDto updatedBlog = blogService.updateBlog(blogId, request);
            return ResponseEntity.ok(BaseResponseDto.success("Blog updated successfully", updatedBlog));
        } catch (Exception e) {
            logger.error("Error updating blog: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to update blog: " + e.getMessage()));
        }
    }

    /**
     * Create new Blog
     * Role: ADMIN only
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create new blog",
        description = "Create a new blog post.",
        tags = {"Blog Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<BlogDto>> createBlog(
            @Parameter(description = "Blog creation data", required = true)
            @Valid @RequestBody BlogRequest request) {
        
        try {
            BlogDto newBlog = blogService.createBlog(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Blog created successfully", newBlog));
        } catch (Exception e) {
            logger.error("Error creating blog: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to create blog: " + e.getMessage()));
        }
    }
}