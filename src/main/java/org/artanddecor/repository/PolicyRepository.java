package org.artanddecor.repository;

import org.artanddecor.model.Policy;
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
     * Find policy by slug
     * @param policySlug the policy slug
     * @return Optional containing Policy if found
     */
    Optional<Policy> findByPolicySlug(String policySlug);

    /**
     * Find policy by name
     * @param policyName Policy name
     * @return Optional containing policy if found
     */
    Optional<Policy> findByPolicyName(String policyName);

    /**
     * Check if policy exists by name
     * @param policyName Policy name
     * @return true if exists
     */
    boolean existsByPolicyName(String policyName);

    /**
     * Find policies by multiple criteria
     * @param policyName Filter by policy name (exact match)
     * @param policyEnabled Filter by enabled status
     * @param textSearch Search text in name, slug, value, display name, remark
     * @return List of matching policies
     */
    @Query("SELECT p FROM Policy p WHERE " +
           "(:policyName IS NULL OR p.policyName = :policyName) AND " +
           "(:policyEnabled IS NULL OR p.policyEnabled = :policyEnabled) AND " +
           "(:textSearch IS NULL OR :textSearch = '' OR " +
           " LOWER(p.policyName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(p.policySlug) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(p.policyValue) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(p.policyDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(p.policyRemark) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    List<Policy> findPoliciesByCriteria(
        @Param("policyName") String policyName,
        @Param("policyEnabled") Boolean policyEnabled,
        @Param("textSearch") String textSearch);
    
    /**
     * Get all distinct policy names for dropdown/combobox
     * @return List of policy names
     */
    @Query("SELECT DISTINCT p.policyName FROM Policy p ORDER BY p.policyName")
    List<String> findAllPolicyNames();
}
