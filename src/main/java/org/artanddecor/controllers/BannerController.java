package org.artanddecor.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.BannerDto;
import org.artanddecor.dto.BannerRequest;
import org.artanddecor.dto.BaseResponseDto;
import org.artanddecor.services.BannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Banner Management REST Controller
 * - PUBLIC: GET all/filter banners
 * - ADMIN: Create, update, get by ID
 */
@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
@Tag(name = "Banner Management", description = "APIs for managing banners displayed on the website")
public class BannerController {

    private static final Logger logger = LoggerFactory.getLogger(BannerController.class);
    private final BannerService bannerService;

    // =============================================
    // PUBLIC ENDPOINTS
    // =============================================

    @Operation(
        summary = "Get banners with optional filtering",
        description = "Retrieve banners with optional filters. If no parameters provided, returns all banners."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Banners retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<BaseResponseDto<Page<BannerDto>>> getBanners(
        @Parameter(description = "Filter by enabled status (true/false)")
        @RequestParam(required = false) Boolean bannerEnabled,
        @Parameter(description = "Search text in title and link fields", example = "summer")
        @RequestParam(required = false) String textSearch,
        @PageableDefault(size = 10, sort = "bannerDisplayOrder", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.info("Getting banners - enabled: {}, textSearch: {}", bannerEnabled, textSearch);
        try {
            Page<BannerDto> results = bannerService.getBannersByCriteria(bannerEnabled, textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Found %d banner(s)", results.getTotalElements()),
                    results));
        } catch (Exception e) {
            logger.error("Error getting banners: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve banners: " + e.getMessage()));
        }
    }

    // =============================================
    // ADMIN ENDPOINTS
    // =============================================

    @Operation(
        summary = "Get banner by ID (Admin)",
        description = "Retrieve a single banner by its ID including all associated images. Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Banner found successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Banner not found",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping("/{bannerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<BannerDto>> getBannerById(
        @Parameter(description = "Banner ID", example = "1")
        @PathVariable Long bannerId) {

        logger.debug("Getting banner by ID: {}", bannerId);
        try {
            BannerDto dto = bannerService.getBannerById(bannerId);
            return ResponseEntity.ok(BaseResponseDto.success("Banner retrieved successfully", dto));
        } catch (IllegalArgumentException | org.artanddecor.exception.ResourceNotFoundException e) {
            logger.warn("Banner not found with ID: {}", bannerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound("Banner not found with ID: " + bannerId));
        } catch (Exception e) {
            logger.error("Error getting banner {}: {}", bannerId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve banner: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Create new banner (Admin)",
        description = "Create a new banner with associated images. imageIds order determines display order. Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Banner created successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<BannerDto>> createBanner(
        @Valid @RequestBody BannerRequest request) {

        logger.info("Creating banner: {}", request.getBannerTitle());
        try {
            BannerDto created = bannerService.createBanner(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    BaseResponseDto.success("Banner created successfully", created));
        } catch (Exception e) {
            logger.error("Error creating banner: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to create banner: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Update banner (Admin)",
        description = "Update an existing banner and replace its image associations. imageIds order determines display order. Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Banner updated successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Banner not found",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @PutMapping("/{bannerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<BannerDto>> updateBanner(
        @Parameter(description = "Banner ID", example = "1")
        @PathVariable Long bannerId,
        @Valid @RequestBody BannerRequest request) {

        logger.info("Updating banner with ID: {}", bannerId);
        try {
            BannerDto updated = bannerService.updateBanner(bannerId, request);
            return ResponseEntity.ok(BaseResponseDto.success("Banner updated successfully", updated));
        } catch (IllegalArgumentException | org.artanddecor.exception.ResourceNotFoundException e) {
            logger.warn("Banner not found with ID: {}", bannerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound("Banner not found with ID: " + bannerId));
        } catch (Exception e) {
            logger.error("Error updating banner {}: {}", bannerId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to update banner: " + e.getMessage()));
        }
    }
}
