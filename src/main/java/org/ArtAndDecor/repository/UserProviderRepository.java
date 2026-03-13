package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserProvider Repository for database operations
 */
@Repository
public interface UserProviderRepository extends JpaRepository<UserProvider, Long> {

    /**
     * Find user provider by name
     * @param providerName Provider name to search
     * @return Optional UserProvider
     */
    Optional<UserProvider> findByUserProviderName(String providerName);


    
    /**
     * Find user providers by multiple criteria with text search
     * All parameters are optional (nullable)
     * @param userProviderName Provider name filter (exact match)
     * @param textSearch Text search in name, display name, or remark
     * @param userProviderEnabled Enabled status filter
     * @return List of providers matching criteria
     */
    @Query("SELECT up FROM UserProvider up " +
           "WHERE (:userProviderName IS NULL OR up.userProviderName = :userProviderName) " +
           "AND (:userProviderEnabled IS NULL OR up.userProviderEnabled = :userProviderEnabled) " +
           "AND (:textSearch IS NULL OR " +
           "     LOWER(up.userProviderName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(up.userProviderDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(up.userProviderRemark) LIKE LOWER(CONCAT('%', :textSearch, '%'))) " +
           "ORDER BY up.userProviderName")
    List<UserProvider> findProvidersByCriteria(
        @Param("userProviderName") String userProviderName,
        @Param("textSearch") String textSearch,
        @Param("userProviderEnabled") Boolean userProviderEnabled
    );
    
    /**
     * Count total users by provider
     * @param userProviderId Provider ID
     * @return Count of users using this provider
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.userProvider.userProviderId = :userProviderId")
    Long countUsersByProvider(@Param("userProviderId") Long userProviderId);
    
    /**
     * Get all provider names for dropdown/combobox
     * @return List of provider names ordered by name
     */
    @Query("SELECT DISTINCT up.userProviderName FROM UserProvider up WHERE up.userProviderEnabled = true ORDER BY up.userProviderName")
    List<String> findAllProviderNames();
}