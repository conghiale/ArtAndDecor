package org.artanddecor.services;

import org.artanddecor.dto.ChangePasswordRequest;
import org.artanddecor.dto.UserDto;
import org.artanddecor.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * Enable or disable user
     * @param userId User ID
     * @param enabled Enabled status
     * @return Updated user DTO
     */
    UserDto updateUserStatus(Long userId, Boolean enabled);





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
     * Find users by multiple criteria with pagination (all parameters optional)
     * Enhanced textSearch includes USER_PROVIDER_DISPLAY_NAME and USER_ROLE_DISPLAY_NAME
     * @param userProviderName Provider name filter
     * @param userProviderDisplayName Provider display name filter
     * @param userRoleName Role name filter
     * @param userRoleDisplayName Role display name filter
     * @param textSearch Text search in userName, firstName, lastName, phoneNumber, email, USER_PROVIDER_DISPLAY_NAME, USER_ROLE_DISPLAY_NAME (contains, case-insensitive)
     * @param userName Username filter
     * @param userEnabled User enabled status filter
     * @param pageable Pagination and sorting information
     * @return Page of UserDto matching criteria
     */
    Page<UserDto> findUsersByCriteria(String userProviderName, String userProviderDisplayName, 
                                     String userRoleName, String userRoleDisplayName, 
                                     String textSearch, String userName, Boolean userEnabled, 
                                     Pageable pageable);





    /**
     * Change password for authenticated user (self-service)
     * @param username Current user's username
     * @param request Password change request
     * @return Updated user DTO
     */
    UserDto changePassword(String username, ChangePasswordRequest request);

    /**
     * Admin reset password for any user (username-based) with email notification
     * Generates random password and sends email notification to user
     * @param userName Username to reset password for
     * @return Updated user DTO
     */
    UserDto resetPassword(String userName);


}