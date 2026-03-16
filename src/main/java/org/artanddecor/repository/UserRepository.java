package org.artanddecor.repository;

import org.artanddecor.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
     * Check if user exists by username
     * @param userName Username to check
     * @return true if exists
     */
    boolean existsByUserName(String userName);

    /**
     * Check if user exists by email
     * @param email Email to check
     * @return true if exists
     */
    boolean existsByEmail(String email);

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
     * Find users by multiple criteria with JOIN FETCH for performance
     * All parameters are optional (nullable)
     * @param userId User ID filter
     * @param userProviderId Provider ID filter
     * @param userProviderName Provider name filter
     * @param userProviderDisplayName Provider display name filter
     * @param userRoleId Role ID filter
     * @param userRoleName Role name filter
     * @param userRoleDisplayName Role display name filter
     * @param textSearch Text search in userName, firstName, lastName, phoneNumber, email (contains, case-insensitive)
     * @param userName Username filter (exact match)
     * @param userEnabled User enabled status filter
     * @return List of users with eagerly loaded provider and role
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.userProvider up " +
           "LEFT JOIN FETCH u.userRole ur " +
           "WHERE (:userId IS NULL OR u.userId = :userId) " +
           "AND (:userProviderId IS NULL OR u.userProvider.userProviderId = :userProviderId) " +
           "AND (:userProviderName IS NULL OR LOWER(u.userProvider.userProviderName) LIKE LOWER(CONCAT('%', :userProviderName, '%'))) " +
           "AND (:userProviderDisplayName IS NULL OR LOWER(u.userProvider.userProviderDisplayName) LIKE LOWER(CONCAT('%', :userProviderDisplayName, '%'))) " +
           "AND (:userRoleId IS NULL OR u.userRole.userRoleId = :userRoleId) " +
           "AND (:userRoleName IS NULL OR LOWER(u.userRole.userRoleName) LIKE LOWER(CONCAT('%', :userRoleName, '%'))) " +
           "AND (:userRoleDisplayName IS NULL OR LOWER(u.userRole.userRoleDisplayName) LIKE LOWER(CONCAT('%', :userRoleDisplayName, '%'))) " +
           "AND (:userName IS NULL OR u.userName = :userName) " +
           "AND (:userEnabled IS NULL OR u.userEnabled = :userEnabled) " +
           "AND (:textSearch IS NULL OR (" +
           "     LOWER(u.userName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(u.firstName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(u.lastName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(u.email) LIKE LOWER(CONCAT('%', :textSearch, '%'))" +
           "))")
    List<User> findUsersAdvancedCriteria(
        @Param("userId") Long userId,
        @Param("userProviderId") Long userProviderId,
        @Param("userProviderName") String userProviderName,
        @Param("userProviderDisplayName") String userProviderDisplayName,
        @Param("userRoleId") Long userRoleId,
        @Param("userRoleName") String userRoleName,
        @Param("userRoleDisplayName") String userRoleDisplayName,
        @Param("textSearch") String textSearch,
        @Param("userName") String userName,
        @Param("userEnabled") Boolean userEnabled
    );

    /**
     * Find users by multiple criteria with pagination
     * Enhanced textSearch includes USER_PROVIDER_DISPLAY_NAME and USER_ROLE_DISPLAY_NAME
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.userProvider up " +
           "LEFT JOIN FETCH u.userRole ur " +
           "WHERE (:userProviderName IS NULL OR LOWER(u.userProvider.userProviderName) LIKE LOWER(CONCAT('%', :userProviderName, '%'))) " +
           "AND (:userProviderDisplayName IS NULL OR LOWER(u.userProvider.userProviderDisplayName) LIKE LOWER(CONCAT('%', :userProviderDisplayName, '%'))) " +
           "AND (:userRoleName IS NULL OR LOWER(u.userRole.userRoleName) LIKE LOWER(CONCAT('%', :userRoleName, '%'))) " +
           "AND (:userRoleDisplayName IS NULL OR LOWER(u.userRole.userRoleDisplayName) LIKE LOWER(CONCAT('%', :userRoleDisplayName, '%'))) " +
           "AND (:userName IS NULL OR u.userName = :userName) " +
           "AND (:userEnabled IS NULL OR u.userEnabled = :userEnabled) " +
           "AND (:textSearch IS NULL OR (" +
           "     LOWER(u.userName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(u.firstName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(u.lastName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(u.email) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(u.userProvider.userProviderDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(u.userRole.userRoleDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%'))" +
           "))")
    Page<User> findUsersAdvancedCriteriaPaginated(
        @Param("userProviderName") String userProviderName,
        @Param("userProviderDisplayName") String userProviderDisplayName,
        @Param("userRoleName") String userRoleName,
        @Param("userRoleDisplayName") String userRoleDisplayName,
        @Param("textSearch") String textSearch,
        @Param("userName") String userName,
        @Param("userEnabled") Boolean userEnabled,
        Pageable pageable
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




}