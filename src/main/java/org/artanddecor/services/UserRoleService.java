package org.artanddecor.services;

import org.artanddecor.dto.UserRoleDto;
import org.artanddecor.model.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * UserRole Service Interface
 * Handles all user role-related business logic
 */
public interface UserRoleService {

    /**
     * Find all user roles with optional filters (not filtering by enabled status)
     * 
     * @param userRoleName Role name filter (optional)
     * @param textSearch Text search in name, display name, or remark (optional)
     * @param userRoleEnabled Enabled status filter (optional)
     * @return List of user roles matching criteria
     */
    List<UserRoleDto> findRolesByCriteria(String userRoleName, String textSearch, Boolean userRoleEnabled);

    /**
     * Find user role by ID
     * 
     * @param userRoleId Role ID
     * @return UserRole details with user count
     */
    Optional<UserRoleDto> findRoleById(Long userRoleId);

    /**
     * Find user role by name
     * 
     * @param roleName Role name
     * @return UserRole details
     */
    Optional<UserRoleDto> findRoleByName(String roleName);

    /**
     * Get all user roles (no filtering by enabled status)
     * 
     * @return List of all roles ordered by name
     */
    List<UserRoleDto> getAllRoles();

    /**
     * Get all user role names for dropdown/combobox
     * 
     * @return List of role names
     */
    List<String> getAllRoleNames();

    /**
     * Convert UserRole entity to DTO
     * 
     * @param userRole Entity to convert
     * @return UserRoleDto
     */
    UserRoleDto convertToDto(UserRole userRole);

    /**
     * Convert UserRoleDto to entity
     * 
     * @param userRoleDto DTO to convert
     * @return UserRole entity
     */
    UserRole convertToEntity(UserRoleDto userRoleDto);
}