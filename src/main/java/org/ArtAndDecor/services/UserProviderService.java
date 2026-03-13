package org.ArtAndDecor.services;

import org.ArtAndDecor.dto.UserProviderDto;
import org.ArtAndDecor.model.UserProvider;

import java.util.List;
import java.util.Optional;

/**
 * UserProvider Service Interface
 * Handles all user provider-related business logic
 */
public interface UserProviderService {

    /**
     * Find all user providers with optional filters (not filtering by enabled status)
     * 
     * @param userProviderName Provider name filter (optional)
     * @param textSearch Text search in name, display name, or remark (optional)
     * @param userProviderEnabled Enabled status filter (optional)
     * @return List of user providers matching criteria
     */
    List<UserProviderDto> findProvidersByCriteria(String userProviderName, String textSearch, Boolean userProviderEnabled);

    /**
     * Find user provider by ID
     * 
     * @param userProviderId Provider ID
     * @return UserProvider details with user count
     */
    Optional<UserProviderDto> findProviderById(Long userProviderId);

    /**
     * Find user provider by name
     * 
     * @param providerName Provider name
     * @return UserProvider details
     */
    Optional<UserProviderDto> findProviderByName(String providerName);

    /**
     * Get all user providers (no filtering by enabled status)
     * 
     * @return List of all providers ordered by name
     */
    List<UserProviderDto> getAllProviders();

    /**
     * Get all user provider names for dropdown/combobox
     * 
     * @return List of provider names
     */
    List<String> getAllProviderNames();

    /**
     * Convert UserProvider entity to DTO
     * 
     * @param userProvider Entity to convert
     * @return UserProviderDto
     */
    UserProviderDto convertToDto(UserProvider userProvider);

    /**
     * Convert UserProviderDto to entity
     * 
     * @param userProviderDto DTO to convert
     * @return UserProvider entity
     */
    UserProvider convertToEntity(UserProviderDto userProviderDto);
}