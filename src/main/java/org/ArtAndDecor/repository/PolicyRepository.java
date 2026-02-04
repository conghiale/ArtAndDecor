package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Policy Repository
 * Handles database operations for Policy configuration table
 */
@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    /**
     * Find policy by name (unique)
     * @param policyName Policy name
     * @return Policy if found
     */
    Optional<Policy> findByPolicyName(String policyName);

    /**
     * Find policy by slug
     * @param policySlug the policy slug
     * @return Optional containing Policy if found
     */
    Optional<Policy> findByPolicySlug(String policySlug);

    /**
     * Check if policy exists by name
     * @param policyName Policy name
     * @return true if exists
     */
    boolean existsByPolicyName(String policyName);
    

    
    /**
     * Find all enabled policies
     * @return List of enabled policies
     */
    List<Policy> findByPolicyEnabledTrue();
    
    /**
     * Find policies by enabled status
     * @param enabled the enabled status
     * @return List of policies with specified enabled status
     */
    List<Policy> findByPolicyEnabled(Boolean enabled);
    
    /**
     * Find policies by name pattern (case-insensitive)
     * @param namePattern the name pattern to search
     * @return List of matching policies
     */
    @Query("SELECT p FROM Policy p WHERE LOWER(p.policyName) LIKE LOWER(CONCAT('%', :namePattern, '%'))")
    List<Policy> findByPolicyNameContainingIgnoreCase(@Param("namePattern") String namePattern);
    
    /**
     * Find policies by value pattern (for configuration search)
     * @param valuePattern the value pattern to search
     * @return List of matching policies
     */
    @Query("SELECT p FROM Policy p WHERE p.policyValue LIKE CONCAT('%', :valuePattern, '%')")
    List<Policy> findByPolicyValueContaining(@Param("valuePattern") String valuePattern);
}
