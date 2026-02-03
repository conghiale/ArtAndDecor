package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Policy Repository
 * Handles database operations for Policy configuration table
 */
@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    /**
     * Find policy by name
     * @param policyName Policy name
     * @return Policy if found
     */
    Optional<Policy> findByPolicyName(String policyName);

    /**
     * Check if policy exists by name
     * @param policyName Policy name
     * @return true if exists
     */
    boolean existsByPolicyName(String policyName);
}
