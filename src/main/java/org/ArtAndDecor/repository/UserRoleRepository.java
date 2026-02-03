package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.UserRole;
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
     * Find all enabled roles ordered by name
     * @return List of enabled roles
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.userRoleEnabled = true ORDER BY ur.userRoleName")
    List<UserRole> findAllEnabledOrderByName();
}