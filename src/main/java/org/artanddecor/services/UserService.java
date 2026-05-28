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

    // =============================================
    // FORGOT-PASSWORD (OTP) WORKFLOW
    // =============================================

    /**
     * Step 1 — generate a 6-digit OTP and send it to the user's registered email.
     * {@code identifier} can be a USERNAME or EMAIL address.
     * OTP is valid for 10 minutes; requesting again overwrites the previous OTP.
     *
     * @param identifier Username or email
     * @throws IllegalArgumentException if no active user is found
     */
    void sendForgotPasswordOtp(String identifier);

    /**
     * Step 2 — verify that the supplied OTP is correct and has not expired.
     * Throws {@link IllegalArgumentException} if invalid or expired.
     *
     * @param identifier Username or email
     * @param otpCode    6-digit OTP entered by the user
     * @throws IllegalArgumentException if OTP is invalid or expired
     */
    void verifyForgotPasswordOtp(String identifier, String otpCode);

    /**
     * Step 3 — reset the password after OTP verification.
     * The OTP is re-validated for security and then cleared on success.
     *
     * @param identifier      Username or email
     * @param otpCode         6-digit OTP (re-sent by client for security)
     * @param newPassword     New password
     * @param confirmPassword Must match newPassword
     * @return Updated user DTO
     * @throws IllegalArgumentException if OTP is invalid/expired or passwords do not match
     */
    UserDto resetPasswordWithOtp(String identifier, String otpCode, String newPassword, String confirmPassword);


}