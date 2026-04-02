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
import org.artanddecor.dto.BaseResponseDto;
import org.artanddecor.dto.DiscountDto;
import org.artanddecor.dto.DiscountTypeDto;
import org.artanddecor.services.DiscountService;
import org.artanddecor.services.DiscountTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Discount Management Controller providing REST APIs for DISCOUNT and DISCOUNT_TYPE operations
 * 
 * Separated from OrderController for clean architecture and dedicated discount management.
 * Provides comprehensive CRUD operations for discount campaigns and discount types with 
 * proper role-based security and extensive filtering capabilities.
 * 
 * Security Model:
 * - ADMIN/MANAGER: Full CRUD access to discounts and discount types
 * - All endpoints require authentication except where noted
 * 
 * API Groups:
 * - Discount Type Management (3 endpoints): GET, POST, PUT
 * - Discount Management (3 endpoints): GET, POST, PUT
 * 
 * Total: 6 REST endpoints for comprehensive discount system management
 */
@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
@Tag(name = "Discount Management", description = "REST APIs for managing discount campaigns and discount types")
public class DiscountController {

    private static final Logger logger = LoggerFactory.getLogger(DiscountController.class);
    
    private final DiscountService discountService;
    private final DiscountTypeService discountTypeService;

    // =================================================================
    // DISCOUNT TYPE APIS (3 endpoints)
    // =================================================================

    /**
     * API 1: Get All Discount Types
     * Role: ADMIN/MANAGER - View all discount types with filtering and pagination
     * Business Flow: Apply filters → Retrieve discount types → Return paginated results
     */
    @GetMapping("/types")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Get all discount types with advanced filtering",
        description = "Admin and Managers retrieve all discount types in the system with comprehensive filtering options including type name, enabled status, and text search. Supports pagination and sorting for large datasets.",
        tags = {"Discount Type Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Discount types retrieved successfully with applied filters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required - Valid JWT token required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - ADMIN or MANAGER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Page<DiscountTypeDto>>> getAllDiscountTypes(
            @Parameter(description = "Filter by discount type ID (optional)", example = "1")
            @RequestParam(required = false) Long typeId,
            
            @Parameter(description = "Filter by discount type name (partial match, optional)", example = "PERCENTAGE")
            @RequestParam(required = false) String typeName,
            
            @Parameter(description = "Filter by enabled status (optional). True for enabled types", example = "true")
            @RequestParam(required = false) Boolean enabled,
            
            @Parameter(description = "Text search across type name and description (optional)", example = "percentage")
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Page number for pagination (0-based, optional)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of records per page (optional)", example = "20")
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Sort by field (optional). Available: discountTypeName, createdDt", example = "discountTypeName")
            @RequestParam(defaultValue = "discountTypeName") String sortBy,
            
            @Parameter(description = "Sort direction (optional). ASC or DESC", example = "ASC")
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        try {
            logger.info("Get all discount types with filters - typeId: {}, typeName: {}, enabled: {}, search: {}", 
                        typeId, typeName, enabled, search);
            
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<DiscountTypeDto> discountTypesPage = discountTypeService.searchDiscountTypesByCriteria(
                    typeId, typeName, enabled, search, pageable);
            
            return ResponseEntity.ok(BaseResponseDto.success(
                "Discount types retrieved successfully", discountTypesPage));
        } catch (Exception e) {
            logger.error("Error getting discount types: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get discount types: " + e.getMessage()));
        }
    }

    /**
     * API 2: Create New Discount Type
     * Role: ADMIN/MANAGER - Create new discount type categories
     * Business Flow: Validation (name uniqueness) → Creation → Return created type
     */
    @PostMapping("/types")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Create new discount type category",
        description = "Admin and Managers create new discount type categories (e.g., PERCENTAGE, FIXED_AMOUNT, BUY_ONE_GET_ONE) with unique names and proper configuration for discount campaigns.",
        tags = {"Discount Type Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Discount type created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid discount type data - Duplicate name or validation errors"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required - Valid JWT token required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - ADMIN or MANAGER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<DiscountTypeDto>> createDiscountType(
            @Parameter(
                description = "Discount type creation data",
                required = true,
                example = "{\"discountTypeName\": \"PERCENTAGE\", \"discountTypeDisplayName\": \"Percentage Discount\", \"discountTypeDescription\": \"Discount based on percentage of total amount\", \"discountTypeEnabled\": true}"
            )
            @Valid @RequestBody DiscountTypeDto discountTypeDto) {
        
        try {
            logger.info("Creating new discount type: {}", discountTypeDto.getDiscountTypeName());
            
            DiscountTypeDto createdType = discountTypeService.createDiscountType(discountTypeDto);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Discount type created successfully", createdType));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid discount type data: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Invalid discount type data: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating discount type: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to create discount type: " + e.getMessage()));
        }
    }

    /**
     * API 3: Update Discount Type
     * Role: ADMIN/MANAGER - Update existing discount type configuration
     * Business Flow: Validation (exists, name uniqueness) → Update → Return updated type
     */
    @PutMapping("/types/{typeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Update existing discount type configuration",
        description = "Admin and Managers update existing discount type categories with validation for uniqueness and proper configuration. Can modify name, display name, description, and enabled status.",
        tags = {"Discount Type Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Discount type updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid discount type data - Duplicate name or validation errors"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required - Valid JWT token required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - ADMIN or MANAGER role required"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Discount type not found with specified ID"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<DiscountTypeDto>> updateDiscountType(
            @Parameter(description = "Discount type ID to update", required = true, example = "1")
            @PathVariable Long typeId,
            
            @Parameter(
                description = "Updated discount type data",
                required = true,
                example = "{\"discountTypeName\": \"PERCENTAGE\", \"discountTypeDisplayName\": \"Percentage Discount Updated\", \"discountTypeDescription\": \"Updated description for percentage discounts\", \"discountTypeEnabled\": true}"
            )
            @Valid @RequestBody DiscountTypeDto discountTypeDto) {
        
        try {
            logger.info("Updating discount type ID: {} with data: {}", typeId, discountTypeDto.getDiscountTypeName());
            
            DiscountTypeDto updatedType = discountTypeService.updateDiscountType(typeId, discountTypeDto);
            
            return ResponseEntity.ok(BaseResponseDto.success("Discount type updated successfully", updatedType));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid discount type data: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Invalid discount type data: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating discount type: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to update discount type: " + e.getMessage()));
        }
    }

    // =================================================================
    // DISCOUNT APIS (3 endpoints)
    // =================================================================

    /**
     * API 4: Get All Discounts
     * Role: ADMIN/MANAGER - View all discounts with comprehensive filtering
     * Business Flow: Apply filters → Retrieve discount campaigns → Return paginated results with statistics
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Get all discount campaigns with advanced filtering",
        description = "Admin and Managers retrieve all discount campaigns in the system with comprehensive filtering options including code, active status, discount type, date range, and usage statistics. Supports pagination and sorting for efficient data management.",
        tags = {"Discount Campaign Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Discount campaigns retrieved successfully with applied filters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required - Valid JWT token required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - ADMIN or MANAGER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Page<DiscountDto>>> getAllDiscounts(
            @Parameter(description = "Filter by discount ID (optional)", example = "1")
            @RequestParam(required = false) Long discountId,
            
            @Parameter(description = "Filter by discount code (partial match, optional)", example = "WELCOME")
            @RequestParam(required = false) String code,
            
            @Parameter(description = "Filter by discount name (partial match, optional)", example = "Welcome")
            @RequestParam(required = false) String name,
            
            @Parameter(description = "Filter by discount type ID (optional)", example = "1")
            @RequestParam(required = false) Long typeId,
            
            @Parameter(description = "Filter by enabled status (optional). True for enabled discounts", example = "true")
            @RequestParam(required = false) Boolean enabled,
            
            @Parameter(description = "Filter by active status (optional). True for currently active discounts", example = "true")
            @RequestParam(required = false) Boolean active,
            
            @Parameter(description = "Text search across code, name, and description (optional)", example = "welcome")
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Page number for pagination (0-based, optional)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of records per page (optional)", example = "20")
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Sort by field (optional). Available: discountCode, discountName, createdDt", example = "discountCode")
            @RequestParam(defaultValue = "discountCode") String sortBy,
            
            @Parameter(description = "Sort direction (optional). ASC or DESC", example = "ASC")
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        try {
            logger.info("Get all discounts with filters - discountId: {}, code: {}, name: {}, typeId: {}, enabled: {}, active: {}, search: {}", 
                        discountId, code, name, typeId, enabled, active, search);
            
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<DiscountDto> discountsPage = discountService.searchDiscountsByCriteria(
                    discountId, code, name, typeId, enabled, active, 
                    null, null, null, null, null, null, search, pageable);
            
            return ResponseEntity.ok(BaseResponseDto.success(
                "Discount campaigns retrieved successfully", discountsPage));
        } catch (Exception e) {
            logger.error("Error getting discount campaigns: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get discount campaigns: " + e.getMessage()));
        }
    }

    /**
     * API 5: Create New Discount Campaign
     * Role: ADMIN/MANAGER - Create new discount campaigns with business rules validation
     * Business Flow: Validation (code uniqueness, date ranges, amounts, type) → Creation → Return created discount
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Create new discount campaign",
        description = "Admin and Managers create new discount campaigns with comprehensive validation including code uniqueness, valid date ranges, appropriate discount values, usage limits, and discount type assignment. Supports both percentage and fixed amount discounts.",
        tags = {"Discount Campaign Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Discount campaign created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid discount data - Duplicate code, invalid date range, invalid discount values, or missing discount type"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required - Valid JWT token required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - ADMIN or MANAGER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<DiscountDto>> createDiscount(
            @Parameter(
                description = "Discount campaign creation data with all required fields and validation rules",
                required = true,
                example = "{\"discountCode\": \"WELCOME2026\", \"discountName\": \"Welcome Discount\", \"discountType\": {\"discountTypeId\": 1}, \"discountValue\": 15.0, \"minOrderAmount\": 100000, \"maxDiscountAmount\": 50000, \"totalUsageLimit\": 1000, \"startAt\": \"2026-01-01T00:00:00\", \"endAt\": \"2026-12-31T23:59:59\", \"isActive\": true}"
            )
            @Valid @RequestBody DiscountDto discountDto) {
        
        try {
            logger.info("Creating new discount campaign: {}", discountDto.getDiscountCode());
            
            DiscountDto createdDiscount = discountService.createDiscount(discountDto);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Discount campaign created successfully", createdDiscount));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid discount campaign data: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Invalid discount campaign data: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating discount campaign: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to create discount campaign: " + e.getMessage()));
        }
    }

    /**
     * API 6: Update Discount Campaign
     * Role: ADMIN/MANAGER - Update existing discount campaigns with business rules validation
     * Business Flow: Validation (exists, code uniqueness, date ranges, amounts) → Update → Return updated discount
     */
    @PutMapping("/{discountId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Update existing discount campaign",
        description = "Admin and Managers update existing discount campaigns with comprehensive validation for code uniqueness, date ranges, discount values, usage limits, and business rules. Preserves usage statistics while allowing configuration updates.",
        tags = {"Discount Campaign Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Discount campaign updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid discount data - Duplicate code, invalid date range, invalid discount values, or business rule violations"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required - Valid JWT token required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - ADMIN or MANAGER role required"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Discount campaign not found with specified ID"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<DiscountDto>> updateDiscount(
            @Parameter(description = "Discount campaign ID to update", required = true, example = "1")
            @PathVariable Long discountId,
            
            @Parameter(
                description = "Updated discount campaign data with validation rules",
                required = true,
                example = "{\"discountCode\": \"WELCOME2026\", \"discountName\": \"Welcome Discount Updated\", \"discountValue\": 20.0, \"minOrderAmount\": 150000, \"maxDiscountAmount\": 75000, \"totalUsageLimit\": 2000, \"startAt\": \"2026-01-01T00:00:00\", \"endAt\": \"2026-12-31T23:59:59\", \"isActive\": true}"
            )
            @Valid @RequestBody DiscountDto discountDto) {
        
        try {
            logger.info("Updating discount campaign ID: {} with code: {}", discountId, discountDto.getDiscountCode());
            
            DiscountDto updatedDiscount = discountService.updateDiscount(discountId, discountDto);
            
            return ResponseEntity.ok(BaseResponseDto.success("Discount campaign updated successfully", updatedDiscount));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid discount campaign data: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Invalid discount campaign data: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating discount campaign: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to update discount campaign: " + e.getMessage()));
        }
    }
}