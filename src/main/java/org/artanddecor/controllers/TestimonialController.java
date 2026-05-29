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
import org.artanddecor.dto.BaseResponseDto;
import org.artanddecor.dto.TestimonialDto;
import org.artanddecor.dto.TestimonialRequest;
import org.artanddecor.services.TestimonialService;
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
 * Testimonial Management REST Controller
 * - PUBLIC: GET all/filter testimonials
 * - ADMIN: Create, update, get by ID
 */
@RestController
@RequestMapping("/testimonials")
@RequiredArgsConstructor
@Tag(name = "Testimonial Management", description = "APIs for managing customer testimonials displayed on the website")
public class TestimonialController {

    private static final Logger logger = LoggerFactory.getLogger(TestimonialController.class);
    private final TestimonialService testimonialService;

    // =============================================
    // PUBLIC ENDPOINTS
    // =============================================

    @Operation(
        summary = "Get testimonials with optional filtering",
        description = "Retrieve testimonials with optional filters. If no parameters provided, returns all testimonials."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Testimonials retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<BaseResponseDto<Page<TestimonialDto>>> getTestimonials(
        @Parameter(description = "Filter by enabled status (true/false)")
        @RequestParam(required = false) Boolean testimonialEnabled,
        @Parameter(description = "Search text in name and quote fields", example = "beautiful")
        @RequestParam(required = false) String textSearch,
        @PageableDefault(size = 10, sort = "testimonialDisplayOrder", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.info("Getting testimonials - enabled: {}, textSearch: {}", testimonialEnabled, textSearch);
        try {
            Page<TestimonialDto> results = testimonialService.getTestimonialsByCriteria(testimonialEnabled, textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Found %d testimonial(s)", results.getTotalElements()),
                    results));
        } catch (Exception e) {
            logger.error("Error getting testimonials: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve testimonials: " + e.getMessage()));
        }
    }

    // =============================================
    // ADMIN ENDPOINTS
    // =============================================

    @Operation(
        summary = "Get testimonial by ID (Admin)",
        description = "Retrieve a single testimonial by its ID. Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Testimonial found successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Testimonial not found",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping("/{testimonialId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<TestimonialDto>> getTestimonialById(
        @Parameter(description = "Testimonial ID", example = "1")
        @PathVariable Long testimonialId) {

        logger.debug("Getting testimonial by ID: {}", testimonialId);
        try {
            TestimonialDto dto = testimonialService.getTestimonialById(testimonialId);
            return ResponseEntity.ok(BaseResponseDto.success("Testimonial retrieved successfully", dto));
        } catch (IllegalArgumentException | org.artanddecor.exception.ResourceNotFoundException e) {
            logger.warn("Testimonial not found with ID: {}", testimonialId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound("Testimonial not found with ID: " + testimonialId));
        } catch (Exception e) {
            logger.error("Error getting testimonial {}: {}", testimonialId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve testimonial: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Create new testimonial (Admin)",
        description = "Create a new customer testimonial. Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Testimonial created successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<TestimonialDto>> createTestimonial(
        @Valid @RequestBody TestimonialRequest request) {

        logger.info("Creating testimonial: {}", request.getTestimonialName());
        try {
            TestimonialDto created = testimonialService.createTestimonial(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    BaseResponseDto.success("Testimonial created successfully", created));
        } catch (Exception e) {
            logger.error("Error creating testimonial: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to create testimonial: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Update testimonial (Admin)",
        description = "Update an existing testimonial. Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Testimonial updated successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Testimonial not found",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @PutMapping("/{testimonialId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<TestimonialDto>> updateTestimonial(
        @Parameter(description = "Testimonial ID", example = "1")
        @PathVariable Long testimonialId,
        @Valid @RequestBody TestimonialRequest request) {

        logger.info("Updating testimonial with ID: {}", testimonialId);
        try {
            TestimonialDto updated = testimonialService.updateTestimonial(testimonialId, request);
            return ResponseEntity.ok(BaseResponseDto.success("Testimonial updated successfully", updated));
        } catch (IllegalArgumentException | org.artanddecor.exception.ResourceNotFoundException e) {
            logger.warn("Testimonial not found with ID: {}", testimonialId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound("Testimonial not found with ID: " + testimonialId));
        } catch (Exception e) {
            logger.error("Error updating testimonial {}: {}", testimonialId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to update testimonial: " + e.getMessage()));
        }
    }
}
