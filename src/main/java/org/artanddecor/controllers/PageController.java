package org.artanddecor.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.*;
import org.artanddecor.services.PageGroupService;
import org.artanddecor.services.PagePositionService;
import org.artanddecor.services.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

/**
 * Page Management REST Controller
 * Handles HTTP requests for Page, PagePosition, and PageGroup operations
 * - PUBLIC ENDPOINTS: GET operations for page content viewing
 * - ADMIN ENDPOINTS: CRUD operations for content management
 */
@RestController
@RequestMapping("/pages")
@RequiredArgsConstructor
@Tag(name = "Page Management", description = "APIs for managing pages, positions, and groups")
public class PageController {
    
    private static final Logger logger = LoggerFactory.getLogger(PageController.class);
    private final PagePositionService pagePositionService;
    private final PageGroupService pageGroupService;
    private final PageService pageService;
    
    // =============================================
    // PAGE POSITION ENDPOINTS
    // =============================================
    
    /**
     * Get page positions with criteria-based filtering and search
     * Public access for viewing position configurations
     */
    @Operation(
        summary = "Search page positions with criteria", 
        description = "Filter and search page positions by name, enabled status, and text search across multiple fields. If no parameters provided, returns all page positions. Public access for viewing position configurations."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Page positions retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping("/positions")
    public ResponseEntity<BaseResponseDto<Page<PagePositionDto>>> getPagePositions(
        @Parameter(description = "Filter by exact page position name", example = "HEADER")
        @RequestParam(name = "pagePositionName", required = false) String pagePositionName,
        @Parameter(description = "Filter by enabled status (true/false)")
        @RequestParam(name = "pagePositionEnabled", required = false) Boolean pagePositionEnabled,
        @Parameter(description = "Search text in name, slug, display name, remark fields", example = "header")
        @RequestParam(name = "textSearch", required = false) String textSearch,
        @PageableDefault(size = 10, sort = "pagePositionName") Pageable pageable) {
        
        logger.info("Searching page positions with criteria - name: {}, enabled: {}, textSearch: {}, page: {}", 
                   pagePositionName, pagePositionEnabled, textSearch, pageable.getPageNumber());
        
        try {
            Page<PagePositionDto> results = pagePositionService.findPagePositionsByCriteria(pagePositionName, pagePositionEnabled, textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Found %d matching page position(s) - Page %d of %d", 
                                 results.getTotalElements(), results.getNumber() + 1, results.getTotalPages()),
                    results));
        } catch (Exception e) {
            logger.error("Error searching page positions: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to search page positions: " + e.getMessage()));
        }
    }
    
    /**
     * Get page position by ID (admin access)
     */
    @Operation(
        summary = "Get page position by ID", 
        description = "Retrieve page position information using database ID. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Page position found successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Page position not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "400", description = "Invalid request or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping("/positions/{positionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PagePositionDto>> getPagePositionById(
        @Parameter(description = "Page position database ID", example = "1")
        @PathVariable Long positionId) {
        logger.debug("Admin requesting page position by ID: {}", positionId);
        try {
            Optional<PagePositionDto> pagePosition = pagePositionService.findPagePositionById(positionId);
            return pagePosition.map(pagePositionDto -> ResponseEntity.ok(BaseResponseDto.success(
                            "Page position retrieved successfully",
                            pagePositionDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            BaseResponseDto.notFound("Page position not found with ID: " + positionId)));
        } catch (Exception e) {
            logger.error("Error retrieving page position by ID {}: {}", positionId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve page position: " + e.getMessage()));
        }
    }
    
    /**
     * Create new page position (admin access)
     */
    @Operation(
        summary = "Create new page position", 
        description = "Create a new page position with provided configuration. Auto-generates slug if not provided. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Page position created successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error or page position slug already exists",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/positions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PagePositionDto>> createPagePosition(
        @Parameter(description = "Page position information to create", required = true)
        @Valid @RequestBody PagePositionRequest pagePositionRequest) {
        logger.info("Creating new page position: {}", pagePositionRequest.getPagePositionName());
        try {
            PagePositionDto created = pagePositionService.createPagePosition(pagePositionRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.success(
                    "Page position created successfully",
                    created));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating page position: {}", e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Validation error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating page position: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to create page position: " + e.getMessage()));
        }
    }
    
    /**
     * Update page position (admin access)
     */
    @Operation(
        summary = "Update existing page position", 
        description = "Update page position information by ID. Auto-generates slug if not provided. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Page position updated successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Page position not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error or page position slug already exists",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PutMapping("/positions/{positionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PagePositionDto>> updatePagePosition(
        @Parameter(description = "Page position ID to update", example = "1")
        @PathVariable Long positionId,
        @Parameter(description = "Updated page position information", required = true)
        @Valid @RequestBody PagePositionRequest pagePositionRequest) {
        
        logger.info("Updating page position with ID: {}", positionId);
        try {
            PagePositionDto updated = pagePositionService.updatePagePosition(positionId, pagePositionRequest);
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Page position updated successfully",
                    updated));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating page position {}: {}", positionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating page position {}: {}", positionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to update page position: " + e.getMessage()));
        }
    }
    
    /**
     * Update page position status (admin access)
     */
    @Operation(
        summary = "Update page position status", 
        description = "Enable or disable a page position by changing its status. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Page position status updated successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Page position not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PatchMapping("/positions/{positionId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PagePositionDto>> updatePagePositionStatus(
        @Parameter(description = "Page position ID to update", example = "1")
        @PathVariable Long positionId,
        @Parameter(description = "New enabled status (true/false)", required = true)
        @RequestParam Boolean enabled) {
        
        logger.info("Updating page position status - ID: {}, Enabled: {}", positionId, enabled);
        try {
            PagePositionDto updated = pagePositionService.updatePagePositionStatus(positionId, enabled);
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Page position status updated successfully",
                    updated));
        } catch (IllegalArgumentException e) {
            logger.error("Error updating page position status {}: {}", positionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating page position status {}: {}", positionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to update page position status: " + e.getMessage()));
        }
    }
    
    // =============================================
    // PAGE GROUP ENDPOINTS
    // =============================================
    
    /**
     * Get page groups with criteria-based filtering and search
     * Public access for viewing group configurations
     */
    @Operation(
        summary = "Search page groups with criteria", 
        description = "Filter and search page groups by name, enabled status, and text search across multiple fields. If no parameters provided, returns all page groups. Public access for viewing group configurations."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Page groups retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping("/groups")
    public ResponseEntity<BaseResponseDto<Page<PageGroupDto>>> getPageGroups(
        @Parameter(description = "Filter by exact page group name", example = "NAVIGATION")
        @RequestParam(name = "pageGroupName", required = false) String pageGroupName,
        @Parameter(description = "Filter by enabled status (true/false)")
        @RequestParam(name = "pageGroupEnabled", required = false) Boolean pageGroupEnabled,
        @Parameter(description = "Search text in name, slug, display name, remark fields", example = "navigation")
        @RequestParam(name = "textSearch", required = false) String textSearch,
        @PageableDefault(size = 10, sort = "pageGroupName") Pageable pageable) {
        
        logger.info("Searching page groups with criteria - name: {}, enabled: {}, textSearch: {}, page: {}", 
                   pageGroupName, pageGroupEnabled, textSearch, pageable.getPageNumber());
        
        try {
            Page<PageGroupDto> results = pageGroupService.findPageGroupsByCriteria(pageGroupName, pageGroupEnabled, textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Found %d matching page group(s) - Page %d of %d", 
                                 results.getTotalElements(), results.getNumber() + 1, results.getTotalPages()),
                    results));
        } catch (Exception e) {
            logger.error("Error searching page groups: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to search page groups: " + e.getMessage()));
        }
    }
    
    /**
     * Get page group by ID (admin access)
     */
    @Operation(
        summary = "Get page group by ID", 
        description = "Retrieve page group information using database ID. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Page group found successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Page group not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "400", description = "Invalid request or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping("/groups/{groupId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PageGroupDto>> getPageGroupById(
        @Parameter(description = "Page group database ID", example = "1")
        @PathVariable Long groupId) {
        logger.debug("Admin requesting page group by ID: {}", groupId);
        try {
            Optional<PageGroupDto> pageGroup = pageGroupService.findPageGroupById(groupId);
            return pageGroup.map(pageGroupDto -> ResponseEntity.ok(BaseResponseDto.success(
                            "Page group retrieved successfully",
                            pageGroupDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            BaseResponseDto.notFound("Page group not found with ID: " + groupId)));
        } catch (Exception e) {
            logger.error("Error retrieving page group by ID {}: {}", groupId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve page group: " + e.getMessage()));
        }
    }
    
    /**
     * Create new page group (admin access)
     */
    @Operation(
        summary = "Create new page group", 
        description = "Create a new page group with provided configuration. Auto-generates slug if not provided. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Page group created successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error or page group slug already exists",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/groups")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PageGroupDto>> createPageGroup(
        @Parameter(description = "Page group information to create", required = true)
        @Valid @RequestBody PageGroupRequest pageGroupRequest) {
        logger.info("Creating new page group: {}", pageGroupRequest.getPageGroupName());
        try {
            PageGroupDto created = pageGroupService.createPageGroup(pageGroupRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.success(
                    "Page group created successfully",
                    created));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating page group: {}", e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Validation error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating page group: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to create page group: " + e.getMessage()));
        }
    }
    
    /**
     * Update page group (admin access)
     */
    @Operation(
        summary = "Update existing page group", 
        description = "Update page group information by ID. Auto-generates slug if not provided. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Page group updated successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Page group not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error or page group slug already exists",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PutMapping("/groups/{groupId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PageGroupDto>> updatePageGroup(
        @Parameter(description = "Page group ID to update", example = "1")
        @PathVariable Long groupId,
        @Parameter(description = "Updated page group information", required = true)
        @Valid @RequestBody PageGroupRequest pageGroupRequest) {
        
        logger.info("Updating page group with ID: {}", groupId);
        try {
            PageGroupDto updated = pageGroupService.updatePageGroup(groupId, pageGroupRequest);
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Page group updated successfully",
                    updated));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating page group {}: {}", groupId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating page group {}: {}", groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to update page group: " + e.getMessage()));
        }
    }
    
    /**
     * Update page group status (admin access)
     */
    @Operation(
        summary = "Update page group status", 
        description = "Enable or disable a page group by changing its status. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Page group status updated successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Page group not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PatchMapping("/groups/{groupId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PageGroupDto>> updatePageGroupStatus(
        @Parameter(description = "Page group ID to update", example = "1")
        @PathVariable Long groupId,
        @Parameter(description = "New enabled status (true/false)", required = true)
        @RequestParam Boolean enabled) {
        
        logger.info("Updating page group status - ID: {}, Enabled: {}", groupId, enabled);
        try {
            PageGroupDto updated = pageGroupService.updatePageGroupStatus(groupId, enabled);
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Page group status updated successfully",
                    updated));
        } catch (IllegalArgumentException e) {
            logger.error("Error updating page group status {}: {}", groupId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating page group status {}: {}", groupId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to update page group status: " + e.getMessage()));
        }
    }
    
    // =============================================
    // PAGE ENDPOINTS
    // =============================================
    
    /**
     * Get pages with criteria-based filtering and search
     * Public access for viewing page content
     */
    @Operation(
        summary = "Search pages with criteria", 
        description = "Filter and search pages by name, enabled status, position, group, and text search across multiple fields. If no parameters provided, returns all pages. Public access for viewing page content."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pages retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<BaseResponseDto<Page<PageDto>>> getPages(
        @Parameter(description = "Filter by exact page name", example = "About Us")
        @RequestParam(name = "pageName", required = false) String pageName,
        @Parameter(description = "Filter by enabled status (true/false)")
        @RequestParam(name = "pageEnabled", required = false) Boolean pageEnabled,
        @Parameter(description = "Filter by page position ID")
        @RequestParam(name = "pagePositionId", required = false) Long pagePositionId,
        @Parameter(description = "Filter by page group ID")
        @RequestParam(name = "pageGroupId", required = false) Long pageGroupId,
        @Parameter(description = "Search text in name, slug, display name, remark, content fields", example = "about")
        @RequestParam(name = "textSearch", required = false) String textSearch,
        @PageableDefault(size = 10, sort = "pageName") Pageable pageable) {
        
        logger.info("Searching pages with criteria - name: {}, enabled: {}, positionId: {}, groupId: {}, textSearch: {}, page: {}", 
                   pageName, pageEnabled, pagePositionId, pageGroupId, textSearch, pageable.getPageNumber());
        
        try {
            Page<PageDto> results = pageService.findPagesByCriteria(pageName, pageEnabled, pagePositionId, pageGroupId, textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Found %d matching page(s) - Page %d of %d", 
                                 results.getTotalElements(), results.getNumber() + 1, results.getTotalPages()),
                    results));
        } catch (Exception e) {
            logger.error("Error searching pages: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to search pages: " + e.getMessage()));
        }
    }
    
    /**
     * Get page by slug (public access)
     * Used for URL-friendly page lookup
     */
    @Operation(
        summary = "Get page by slug", 
        description = "Retrieve page information using URL-friendly slug. Public access for page viewing."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Page found successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Page not found with the provided slug",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping("/slug/{pageSlug}")
    public ResponseEntity<BaseResponseDto<PageDto>> getPageBySlug(
        @Parameter(description = "URL-friendly page identifier", example = "about-us")
        @PathVariable String pageSlug) {
        logger.debug("Requesting page by slug: {}", pageSlug);
        try {
            Optional<PageDto> page = pageService.findPageBySlug(pageSlug);
            return page.map(pageDto -> ResponseEntity.ok(BaseResponseDto.success(
                            "Page retrieved successfully",
                            pageDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            BaseResponseDto.notFound("Page not found with slug: " + pageSlug)));
        } catch (Exception e) {
            logger.error("Error retrieving page by slug {}: {}", pageSlug, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve page: " + e.getMessage()));
        }
    }
    
    /**
     * Get page by ID (admin access)
     */
    @Operation(
        summary = "Get page by ID", 
        description = "Retrieve page information using database ID. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Page found successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Page not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "400", description = "Invalid request or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping("/{pageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PageDto>> getPageById(
        @Parameter(description = "Page database ID", example = "1")
        @PathVariable Long pageId) {
        logger.debug("Admin requesting page by ID: {}", pageId);
        try {
            Optional<PageDto> page = pageService.findPageById(pageId);
            return page.map(pageDto -> ResponseEntity.ok(BaseResponseDto.success(
                            "Page retrieved successfully",
                            pageDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            BaseResponseDto.notFound("Page not found with ID: " + pageId)));
        } catch (Exception e) {
            logger.error("Error retrieving page by ID {}: {}", pageId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve page: " + e.getMessage()));
        }
    }
    
    /**
     * Create new page (admin access)
     */
    @Operation(
        summary = "Create new page", 
        description = "Create a new page with provided configuration. Auto-generates slug if not provided. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Page created successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error or page slug already exists",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PageDto>> createPage(
        @Parameter(description = "Page information to create", required = true)
        @Valid @RequestBody PageRequest pageRequest) {
        logger.info("Creating new page: {}", pageRequest.getPageName());
        try {
            PageDto created = pageService.createPage(pageRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.success(
                    "Page created successfully",
                    created));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating page: {}", e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Validation error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating page: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to create page: " + e.getMessage()));
        }
    }
    
    /**
     * Update page (admin access)
     */
    @Operation(
        summary = "Update existing page", 
        description = "Update page information by ID. Auto-generates slug if not provided. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Page updated successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Page not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error or page slug already exists",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PutMapping("/{pageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PageDto>> updatePage(
        @Parameter(description = "Page ID to update", example = "1")
        @PathVariable Long pageId,
        @Parameter(description = "Updated page information", required = true)
        @Valid @RequestBody PageRequest pageRequest) {
        
        logger.info("Updating page with ID: {}", pageId);
        try {
            PageDto updated = pageService.updatePage(pageId, pageRequest);
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Page updated successfully",
                    updated));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating page {}: {}", pageId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating page {}: {}", pageId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to update page: " + e.getMessage()));
        }
    }
    
    /**
     * Update page status (admin access)
     */
    @Operation(
        summary = "Update page status", 
        description = "Enable or disable a page by changing its status. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Page status updated successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Page not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PatchMapping("/{pageId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PageDto>> updatePageStatus(
        @Parameter(description = "Page ID to update", example = "1")
        @PathVariable Long pageId,
        @Parameter(description = "New enabled status (true/false)", required = true)
        @RequestParam Boolean enabled) {
        
        logger.info("Updating page status - ID: {}, Enabled: {}", pageId, enabled);
        try {
            PageDto updated = pageService.updatePageStatus(pageId, enabled);
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Page status updated successfully",
                    updated));
        } catch (IllegalArgumentException e) {
            logger.error("Error updating page status {}: {}", pageId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating page status {}: {}", pageId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to update page status: " + e.getMessage()));
        }
    }
}