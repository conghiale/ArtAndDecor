package org.ArtAndDecor.services;

import org.ArtAndDecor.dto.PolicyDto;
import java.util.List;
import java.util.Optional;

/**
 * Policy Service Interface
 * Business logic operations for Policy management (system configuration)
 */
public interface PolicyService {
    
    /**
     * Get policy by name (exact match, most common lookup)
     * @param policyName the policy name
     * @return Optional containing PolicyDto if found
     */
    Optional<PolicyDto> findPolicyByName(String policyName);
    
    /**
     * Get policy by ID
     * @param policyId the policy ID
     * @return Optional containing PolicyDto if found
     */
    Optional<PolicyDto> findPolicyById(Long policyId);
    
    /**
     * Get policy by slug
     * @param policySlug the policy slug
     * @return Optional containing PolicyDto if found
     */
    Optional<PolicyDto> findPolicyBySlug(String policySlug);
    
    /**
     * Get all enabled policies
     * @return List of enabled PolicyDto objects
     */
    List<PolicyDto> findAllEnabledPolicies();
    
    /**
     * Search policies by name pattern (case-insensitive)
     * @param namePattern the name pattern to search
     * @return List of matching PolicyDto objects
     */
    List<PolicyDto> searchPoliciesByName(String namePattern);
    
    /**
     * Search policies by value pattern (for configuration lookup)
     * @param valuePattern the value pattern to search
     * @return List of matching PolicyDto objects
     */
    List<PolicyDto> searchPoliciesByValue(String valuePattern);
    
    /**
     * Create new policy
     * @param policyDto the policy DTO with data
     * @return created PolicyDto with ID
     * @throws IllegalArgumentException if validation fails or policy name already exists
     */
    PolicyDto createPolicy(PolicyDto policyDto);
    
    /**
     * Update existing policy
     * @param policyId the policy ID to update
     * @param policyDto the policy DTO with updated data
     * @return updated PolicyDto
     * @throws IllegalArgumentException if policy not found or validation fails
     */
    PolicyDto updatePolicy(Long policyId, PolicyDto policyDto);
    
    /**
     * Update policy status (enable/disable)
     * @param policyId the policy ID to update
     * @param enabled the new enabled status
     * @return updated PolicyDto
     * @throws IllegalArgumentException if policy not found
     */
    PolicyDto updatePolicyStatus(Long policyId, Boolean enabled);
    
    /**
     * Update policy value only (for configuration updates)
     * @param policyId the policy ID to update
     * @param newValue the new policy value
     * @return updated PolicyDto
     * @throws IllegalArgumentException if policy not found
     */
    PolicyDto updatePolicyValue(Long policyId, String newValue);
    
    /**
     * Delete policy by ID
     * @param policyId the policy ID to delete
     * @throws IllegalArgumentException if policy not found
     */
    void deletePolicy(Long policyId);
    
    /**
     * Get total count of policies
     * @return total count of policies in database
     */
    long getTotalPolicyCount();
}
