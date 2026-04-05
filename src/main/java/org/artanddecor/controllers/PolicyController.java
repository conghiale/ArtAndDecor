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
import org.artanddecor.dto.PolicyDto;
import org.artanddecor.dto.PolicyRequest;
import org.artanddecor.services.PolicyService;
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
import java.util.List;
import java.util.Optional;

/**
 * Policy Management REST Controller
 * Handles HTTP requests for system configuration/policy operations
 * - ADMIN ENDPOINTS: CRUD operations for system policies
 * Priority ID > name > slug for admin operations
 */
@RestController
@RequestMapping("/policies")
@RequiredArgsConstructor
@Tag(name = "Policy Management", description = "APIs for managing system policies and configuration settings")
public class PolicyController {
    
    private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);
    private final PolicyService policyService;
    
    // =============================================
    // ADMIN ENDPOINTS - Policy Management (All require ADMIN role)
    // =============================================
    
    /**
     * Get policy by slug (admin access)
     * Used for URL-friendly policy lookup
     */
    @Operation(
        summary = "Get policy by slug", 
        description = "Retrieve policy information using URL-friendly slug. Public access for policy viewing."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Policy found successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Policy not found with the provided slug",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping("/slug/{policySlug}")
    public ResponseEntity<BaseResponseDto<PolicyDto>> getPolicyBySlug(
        @Parameter(description = "URL-friendly policy identifier", example = "contact-email")
        @PathVariable String policySlug) {
        logger.debug("Requesting policy by slug: {}", policySlug);
        try {
            Optional<PolicyDto> policy = policyService.findPolicyBySlug(policySlug);
            return policy.map(policyDto -> ResponseEntity.ok(BaseResponseDto.success(
                            "Policy retrieved successfully",
                            policyDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            BaseResponseDto.notFound("Policy not found with slug: " + policySlug)));
        } catch (Exception e) {
            logger.error("Error retrieving policy by slug {}: {}", policySlug, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve policy: " + e.getMessage()));
        }
    }
    
    /**
     * Get policies with criteria-based filtering and search 
     * Public access for browsing policy configurations
     */
    @Operation(
        summary = "Search policies with criteria", 
        description = "Filter and search policies by name, enabled status, and text search across multiple fields. If no parameters provided, returns all policies. Public access for viewing policy configurations."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Policies retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<BaseResponseDto<Page<PolicyDto>>> getPoliciesByCriteria(
        @Parameter(description = "Filter by exact policy name", example = "CONTACT_EMAIL")
        @RequestParam(name = "policyName", required = false) String policyName,
        @Parameter(description = "Filter by enabled status (true/false)")
        @RequestParam(name = "policyEnabled", required = false) Boolean policyEnabled,
        @Parameter(description = "Search text in name, slug, value, display name, remark fields", example = "email")
        @RequestParam(name = "textSearch", required = false) String textSearch,
        @PageableDefault(size = 10, sort = "policyName") Pageable pageable) {
        
        logger.info("Searching policies with criteria - name: {}, enabled: {}, textSearch: {}, page: {}", 
                   policyName, policyEnabled, textSearch, pageable.getPageNumber());
        
        try {
            Page<PolicyDto> results = policyService.findPoliciesByCriteria(policyName, policyEnabled, textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Found %d matching policy(ies) - Page %d of %d", 
                                 results.getTotalElements(), results.getNumber() + 1, results.getTotalPages()),
                    results));
        } catch (Exception e) {
            logger.error("Error searching policies: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to search policies: " + e.getMessage()));
        }
    }
    
    /**
     * Get policy by ID (public lookup)
     * Public access for policy viewing
     */
    @Operation(
        summary = "Get policy by ID", 
        description = "Retrieve policy information using database ID. Public access for viewing policy configurations."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Policy found successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Policy not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping("/{policyId}")
    public ResponseEntity<BaseResponseDto<PolicyDto>> getPolicyById(
        @Parameter(description = "Policy database ID", example = "1")
        @PathVariable Long policyId) {
        logger.debug("Requesting policy by ID: {}", policyId);
        try {
            Optional<PolicyDto> policy = policyService.findPolicyById(policyId);
            return policy.map(policyDto -> ResponseEntity.ok(BaseResponseDto.success(
                            "Policy retrieved successfully",
                            policyDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            BaseResponseDto.notFound("Policy not found with ID: " + policyId)));
        } catch (Exception e) {
            logger.error("Error retrieving policy by ID {}: {}", policyId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve policy: " + e.getMessage()));
        }
    }
    
    /**
     * Create new policy
     * Requires ADMIN role only
     */
    @Operation(
        summary = "Create new policy", 
        description = "Create a new system policy with provided configuration. Auto-generates slug if not provided. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Policy created successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error or policy name already exists",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PolicyDto>> createPolicy(
        @Parameter(description = "Policy information to create", required = true)
        @Valid @RequestBody PolicyRequest policyRequest) {
        logger.info("Creating new policy: {}", policyRequest.getPolicyName());
        try {
            PolicyDto created = policyService.createPolicy(policyRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.success(
                    "Policy created successfully",
                    created));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating policy: {}", e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Validation error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating policy: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to create policy: " + e.getMessage()));
        }
    }
    
    /**
     * Update policy
     * Requires ADMIN role only
     */
    @Operation(
        summary = "Update existing policy", 
        description = "Update policy information by ID. Auto-generates slug if not provided. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Policy updated successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Policy not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error or policy name already exists",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PutMapping("/{policyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PolicyDto>> updatePolicy(
        @Parameter(description = "Policy ID to update", example = "1")
        @PathVariable Long policyId,
        @Parameter(description = "Updated policy information", required = true)
        @Valid @RequestBody PolicyDto policyDto) {
        
        logger.info("Updating policy with ID: {}", policyId);
        try {
            PolicyDto updated = policyService.updatePolicy(policyId, policyDto);
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Policy updated successfully",
                    updated));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating policy {}: {}", policyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating policy {}: {}", policyId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to update policy: " + e.getMessage()));
        }
    }
    
    /**
     * Update policy status (enable/disable)
     * Requires ADMIN role
     */
    @Operation(
        summary = "Update policy status", 
        description = "Enable or disable a policy by changing its status. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Policy status updated successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Policy not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PatchMapping("/{policyId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PolicyDto>> updatePolicyStatus(
        @Parameter(description = "Policy ID to update", example = "1")
        @PathVariable Long policyId,
        @Parameter(description = "New enabled status (true/false)", required = true)
        @RequestParam Boolean enabled) {
        
        logger.info("Updating policy status - ID: {}, Enabled: {}", policyId, enabled);
        try {
            PolicyDto updated = policyService.updatePolicyStatus(policyId, enabled);
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Policy status updated successfully",
                    updated));
        } catch (IllegalArgumentException e) {
            logger.error("Error updating policy status {}: {}", policyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating policy status {}: {}", policyId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to update policy status: " + e.getMessage()));
        }
    }
    
    /**
     * Update policy value only (quick configuration update)
     * Requires ADMIN role
     */
    @Operation(
        summary = "Update policy value", 
        description = "Update only the value of a policy for quick configuration changes. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Policy value updated successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Policy not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters or empty value",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PatchMapping("/{policyId}/value")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PolicyDto>> updatePolicyValue(
        @Parameter(description = "Policy ID to update", example = "1")
        @PathVariable Long policyId,
        @Parameter(description = "New policy value", required = true, example = "contact@artstore.com")
        @RequestParam String value) {
        
        logger.info("Updating policy value - ID: {}", policyId);
        try {
            if (value == null || value.isBlank()) {
                return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                        "Policy value cannot be empty"));
            }
            
            PolicyDto updated = policyService.updatePolicyValue(policyId, value);
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Policy value updated successfully",
                    updated));
        } catch (IllegalArgumentException e) {
            logger.error("Error updating policy value {}: {}", policyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating policy value {}: {}", policyId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to update policy value: " + e.getMessage()));
        }
    }
    
    /**
     * Get total policy count (public statistics)
     * Public access for viewing policy statistics
     */
    @Operation(
        summary = "Get total policy count", 
        description = "Retrieve the total number of policies in the system for statistics. Public access for viewing policy counts."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total count retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "System error occurred",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping("/stats/count")
    public ResponseEntity<BaseResponseDto<Long>> getTotalPolicyCount() {
        logger.info("Getting total policy count");
        try {
            long count = policyService.getTotalPolicyCount();
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Total count retrieved successfully",
                    count));
        } catch (Exception e) {
            logger.error("Error getting total policy count: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to get total count: " + e.getMessage()));
        }
    }
    
    /**
     * Get all policy names for dropdown/combobox (public access)
     * Public access for viewing policy name options
     */
    @Operation(
        summary = "Get all policy names", 
        description = "Retrieve list of all policy names for use in dropdown/combobox UI elements. Public access for viewing policy name options."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Policy names retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "System error occurred",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping("/names")
    public ResponseEntity<BaseResponseDto<List<String>>> getAllPolicyNames() {
        logger.info("Getting all policy names");
        try {
            List<String> names = policyService.getAllPolicyNames();
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Retrieved %d policy names", names.size()),
                    names));
        } catch (Exception e) {
            logger.error("Error retrieving policy names: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve policy names: " + e.getMessage()));
        }
    }
}
