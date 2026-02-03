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
     * Find all enabled providers ordered by name
     * @return List of enabled providers
     */
    @Query("SELECT up FROM UserProvider up WHERE up.userProviderEnabled = true ORDER BY up.userProviderName")
    List<UserProvider> findAllEnabledOrderByName();
}