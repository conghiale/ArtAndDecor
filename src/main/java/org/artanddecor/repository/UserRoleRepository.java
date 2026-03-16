package org.artanddecor.repository;

import org.artanddecor.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRole Repository for database operations
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    /**
     * Find user role by name
     * @param roleName Role name to search
     * @return Optional UserRole
     */
    Optional<UserRole> findByUserRoleName(String roleName);


    
    /**
     * Find user roles by multiple criteria with text search
     * All parameters are optional (nullable)
     * @param userRoleName Role name filter (exact match)
     * @param textSearch Text search in name, display name, or remark
     * @param userRoleEnabled Enabled status filter
     * @return List of roles matching criteria
     */
    @Query("SELECT ur FROM UserRole ur " +
           "WHERE (:userRoleName IS NULL OR ur.userRoleName = :userRoleName) " +
           "AND (:userRoleEnabled IS NULL OR ur.userRoleEnabled = :userRoleEnabled) " +
           "AND (:textSearch IS NULL OR " +
           "     LOWER(ur.userRoleName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(ur.userRoleDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(ur.userRoleRemark) LIKE LOWER(CONCAT('%', :textSearch, '%'))) " +
           "ORDER BY ur.userRoleName")
    List<UserRole> findRolesByCriteria(
        @Param("userRoleName") String userRoleName,
        @Param("textSearch") String textSearch,
        @Param("userRoleEnabled") Boolean userRoleEnabled
    );
    
    /**
     * Count total users by role
     * @param userRoleId Role ID
     * @return Count of users with this role
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.userRole.userRoleId = :userRoleId")
    Long countUsersByRole(@Param("userRoleId") Long userRoleId);
    
    /**
     * Get all role names for dropdown/combobox
     * @return List of role names ordered by name
     */
    @Query("SELECT DISTINCT ur.userRoleName FROM UserRole ur WHERE ur.userRoleEnabled = true ORDER BY ur.userRoleName")
    List<String> findAllRoleNames();
}