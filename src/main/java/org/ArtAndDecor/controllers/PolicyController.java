package org.ArtAndDecor.controllers;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.BaseResponseDto;
import org.ArtAndDecor.dto.PolicyDto;
import org.ArtAndDecor.services.PolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class PolicyController {
    
    private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);
    private final PolicyService policyService;
    
    // =============================================
    // ADMIN ENDPOINTS - Policy Management (All require ADMIN role)
    // =============================================
    
    /**
     * Get policy by name (admin access)
     * Used by admin to retrieve configuration values
     */
    @GetMapping("/name/{policyName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PolicyDto>> getPolicyByName(@PathVariable String policyName) {
        logger.debug("Requesting policy by name: {}", policyName);
        try {
            Optional<PolicyDto> policy = policyService.findPolicyByName(policyName);
            return policy.map(policyDto -> ResponseEntity.ok(BaseResponseDto.success(
                            "Policy retrieved successfully",
                            policyDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            BaseResponseDto.notFound("Policy not found with name: " + policyName)));
        } catch (Exception e) {
            logger.error("Error retrieving policy by name {}: {}", policyName, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve policy: " + e.getMessage()));
        }
    }
    
    /**
     * Get policy by slug (admin access)
     * Used for URL-friendly policy lookup
     */
    @GetMapping("/slug/{policySlug}")
    public ResponseEntity<BaseResponseDto<PolicyDto>> getPolicyBySlug(@PathVariable String policySlug) {
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
     * Get all enabled policies (admin access)
     * Returns configuration that is available
     */
    @GetMapping("/public")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<List<PolicyDto>>> getPublicPolicies() {
        logger.debug("Requesting all enabled policies");
        try {
            List<PolicyDto> policies = policyService.findAllEnabledPolicies();
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Retrieved %d enabled policies", policies.size()),
                    policies));
        } catch (Exception e) {
            logger.error("Error retrieving public policies: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve policies: " + e.getMessage()));
        }
    }
    
    /**
     * Get policy by ID (admin/management lookup)
     * Requires ADMIN role
     */
    @GetMapping("/{policyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PolicyDto>> getPolicyById(@PathVariable Long policyId) {
        logger.info("Admin requesting policy by ID: {}", policyId);
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
     * Search policies by name pattern (admin)
     * Requires ADMIN role
     */
    @GetMapping("/admin/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<List<PolicyDto>>> searchPoliciesByName(
            @RequestParam(name = "q", required = false) String searchTerm) {
        
        logger.info("Admin searching policies with term: {}", searchTerm);
        try {
            if (searchTerm == null || searchTerm.isBlank()) {
                return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                        "Search term is required"));
            }
            
            List<PolicyDto> results = policyService.searchPoliciesByName(searchTerm);
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Found %d matching policy/policies", results.size()),
                    results));
        } catch (Exception e) {
            logger.error("Error searching policies: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to search policies: " + e.getMessage()));
        }
    }
    
    /**
     * Create new policy
     * Requires ADMIN role only
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PolicyDto>> createPolicy(@Valid @RequestBody PolicyDto policyDto) {
        logger.info("Creating new policy: {}", policyDto.getPolicyName());
        try {
            PolicyDto created = policyService.createPolicy(policyDto);
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
    @PutMapping("/{policyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PolicyDto>> updatePolicy(
            @PathVariable Long policyId,
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
    @PatchMapping("/{policyId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PolicyDto>> updatePolicyStatus(
            @PathVariable Long policyId,
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
    @PatchMapping("/{policyId}/value")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<PolicyDto>> updatePolicyValue(
            @PathVariable Long policyId,
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
     * Delete policy
     * Requires ADMIN role only
     */
    @DeleteMapping("/{policyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<Void>> deletePolicy(@PathVariable Long policyId) {
        logger.info("Deleting policy with ID: {}", policyId);
        try {
            policyService.deletePolicy(policyId);
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Policy deleted successfully",
                    null));
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting policy {}: {}", policyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting policy {}: {}", policyId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to delete policy: " + e.getMessage()));
        }
    }
    
    /**
     * Get total policy count (admin dashboard)
     * Requires ADMIN role
     */
    @GetMapping("/admin/total-count")
    @PreAuthorize("hasRole('ADMIN')")
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
}
