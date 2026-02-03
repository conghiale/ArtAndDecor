package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User Repository for database operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find enabled user by username (for authentication)
     * @param userName Username to search
     * @return Optional User
     */
    Optional<User> findByUserNameAndUserEnabled(String userName, Boolean userEnabled);

    /**
     * Find enabled user by username with eager-loaded relationships (for authentication)
     * Uses JOIN FETCH to load userRole and userProvider to avoid LazyInitializationException
     * @param userName Username to search
     * @param userEnabled Enabled status
     * @return Optional User with role and provider eager-loaded
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.userRole " +
           "LEFT JOIN FETCH u.userProvider " +
           "WHERE u.userName = :userName AND u.userEnabled = :userEnabled")
    Optional<User> findByUserNameAndUserEnabledWithDetails(@Param("userName") String userName, @Param("userEnabled") Boolean userEnabled);

    /**
     * Find enabled user by email address (for authentication)
     * @param email Email to search
     * @return Optional User
     */
    Optional<User> findByEmailAndUserEnabled(String email, Boolean userEnabled);

    /**
     * Find enabled user by email address with eager-loaded relationships (for authentication)
     * Uses JOIN FETCH to load userRole and userProvider to avoid LazyInitializationException
     * @param email Email to search
     * @param userEnabled Enabled status
     * @return Optional User with role and provider eager-loaded
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.userRole " +
           "LEFT JOIN FETCH u.userProvider " +
           "WHERE u.email = :email AND u.userEnabled = :userEnabled")
    Optional<User> findByEmailAndUserEnabledWithDetails(@Param("email") String email, @Param("userEnabled") Boolean userEnabled);

    /**
     * Check if username exists
     * @param userName Username to check
     * @return true if exists
     */
    boolean existsByUserName(String userName);

    /**
     * Check if email exists
     * @param email Email to check
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Find users by enabled status
     * @param userEnabled Enabled status
     * @return List of users
     */
    List<User> findByUserEnabled(Boolean userEnabled);

    /**
     * Search users by name (first name or last name contains search term)
     * @param searchTerm Search term
     * @return List of users
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Find users by multiple criteria with JOIN FETCH for performance
     * All parameters are optional (nullable)
     * @param userId User ID filter
     * @param userProviderId Provider ID filter  
     * @param userRoleId Role ID filter
     * @param userEnabled Enabled status filter
     * @param userName Username filter (exact match)
     * @return List of users with eagerly loaded provider and role
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.userProvider up " +
           "LEFT JOIN FETCH u.userRole ur " +
           "WHERE (:userId IS NULL OR u.userId = :userId) " +
           "AND (:userProviderId IS NULL OR u.userProvider.userProviderId = :userProviderId) " +
           "AND (:userRoleId IS NULL OR u.userRole.userRoleId = :userRoleId) " +
           "AND (:userEnabled IS NULL OR u.userEnabled = :userEnabled) " +
           "AND (:userName IS NULL OR u.userName = :userName)")
    List<User> findUsersByCriteria(
        @Param("userId") Long userId,
        @Param("userProviderId") Long userProviderId,
        @Param("userRoleId") Long userRoleId,
        @Param("userEnabled") Boolean userEnabled,
        @Param("userName") String userName
    );

    /**
     * Find single user by ID with related entities
     * @param userId User ID
     * @return Optional User with provider and role
     */
    @Query("SELECT u FROM User u " +
           "LEFT JOIN FETCH u.userProvider " +
           "LEFT JOIN FETCH u.userRole " +
           "WHERE u.userId = :userId")
    Optional<User> findByIdWithDetails(@Param("userId") Long userId);

    /**
     * Find all users with eagerly loaded provider and role (for pagination)
     * @return List of users with all related entities
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.userProvider " +
           "LEFT JOIN FETCH u.userRole")
    List<User> findAllWithDetails();

    /**
     * Search users by name with eagerly loaded related entities
     * @param searchTerm Search term for first or last name
     * @return List of users with provider and role data
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.userProvider " +
           "LEFT JOIN FETCH u.userRole " +
           "WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchByNameWithDetails(@Param("searchTerm") String searchTerm);

    /**
     * Find user by ID with details (alias for backward compatibility)
     * @param userId User ID
     * @return Optional User with all related entities eager loaded
     */
    @Query("SELECT u FROM User u " +
           "LEFT JOIN FETCH u.userProvider " +
           "LEFT JOIN FETCH u.userRole " +
           "WHERE u.userId = :userId")
    Optional<User> findByUserIdWithDetails(@Param("userId") Long userId);
}