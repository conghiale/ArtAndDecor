package org.ArtAndDecor.services;

import org.ArtAndDecor.dto.ChangePasswordRequest;
import org.ArtAndDecor.dto.ResetPasswordRequest;
import org.ArtAndDecor.dto.UserDto;
import org.ArtAndDecor.model.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * User Service Interface
 */
public interface UserService {

    /**
     * Create new user
     * @param userDto User data
     * @return Created user DTO
     */
    UserDto createUser(UserDto userDto);

    /**
     * Update existing user
     * @param userId User ID
     * @param userDto User data
     * @return Updated user DTO
     */
    UserDto updateUser(Long userId, UserDto userDto);

    /**
     * Find user by ID
     * @param userId User ID
     * @return User DTO if found
     */
    Optional<UserDto> findUserById(Long userId);

    /**
     * Get all users with pagination
     * @param page Page number
     * @param size Page size
     * @return Page of user DTOs
     */
    Page<UserDto> getAllUsers(int page, int size);

    /**
     * Search users by name
     * @param searchTerm Search term
     * @return List of user DTOs
     */
    List<UserDto> searchUsersByName(String searchTerm);

    /**
     * Enable or disable user
     * @param userId User ID
     * @param enabled Enabled status
     * @return Updated user DTO
     */
    UserDto updateUserStatus(Long userId, Boolean enabled);

    /**
     * Delete user
     * @param userId User ID
     */
    void deleteUser(Long userId);

    /**
     * Check if username exists
     * @param userName Username to check
     * @return true if exists
     */
    boolean existsByUserName(String userName);

    /**
     * Check if email exists
     * @param emailAddress Email to check
     * @return true if exists
     */
    boolean existsByEmail(String emailAddress);

    /**
     * Convert User entity to DTO
     * @param user User entity
     * @return User DTO
     */
    UserDto convertToDto(User user);

    /**
     * Convert User DTO to entity
     * @param userDto User DTO
     * @return User entity
     */
    User convertToEntity(UserDto userDto);

    /**
     * Find users by multiple criteria (all parameters optional)
     * @param userId User ID filter
     * @param userProviderId Provider ID filter
     * @param userRoleId Role ID filter
     * @param userEnabled Enabled status filter
     * @param userName Username filter
     * @return List of UserDto matching criteria
     */
    List<UserDto> findUsersByCriteria(Long userId, Long userProviderId, Long userRoleId, 
                                     Boolean userEnabled, String userName);

    /**
     * Change password for authenticated user (self-service)
     * @param username Current user's username
     * @param request Password change request
     * @return Updated user DTO
     */
    UserDto changePassword(String username, ChangePasswordRequest request);

    /**
     * Admin reset password for any user (ID-based)
     * @param userId User ID to reset password
     * @param request Password reset request
     * @return Updated user DTO
     */
    UserDto resetPassword(Long userId, ResetPasswordRequest request);

    /**
     * Customer change password by username (customer-friendly)
     * @param username Username to change password for
     * @param request Password change request
     * @return Updated user DTO
     */
    UserDto changePasswordByUsername(String username, ChangePasswordRequest request);
}