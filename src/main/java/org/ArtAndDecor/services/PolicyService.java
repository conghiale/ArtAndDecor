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
     * Find policies by criteria (with filters)
     * @param policyName Filter by policy name (exact match)
     * @param policyEnabled Filter by enabled status
     * @param textSearch Search text in multiple fields
     * @return List of matching PolicyDto objects
     */
    List<PolicyDto> findPoliciesByCriteria(String policyName, Boolean policyEnabled, String textSearch);
    
    /**
     * Get all policy names for dropdown/combobox
     * @return List of all policy names
     */
    List<String> getAllPolicyNames();
    
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
     * Get total count of policies
     * @return total count of policies in database
     */
    long getTotalPolicyCount();
}
