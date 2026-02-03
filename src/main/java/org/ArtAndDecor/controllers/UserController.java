package org.ArtAndDecor.controllers;

import org.ArtAndDecor.dto.UserRoleDto;
import org.ArtAndDecor.dto.UserProviderDto;
import org.ArtAndDecor.model.UserRole;
import org.ArtAndDecor.model.UserProvider;
import org.ArtAndDecor.repository.UserRoleRepository;
import org.ArtAndDecor.repository.UserProviderRepository;

import org.ArtAndDecor.dto.BaseResponseDto;
import org.ArtAndDecor.dto.ChangePasswordRequest;
import org.ArtAndDecor.dto.ResetPasswordRequest;
import org.ArtAndDecor.dto.UserDto;
import org.ArtAndDecor.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * User Management REST Controller
 * Handles all USER MANAGEMENT API operations
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserProviderRepository userProviderRepository;

    /**
     * Get all enabled user roles (USER_ROLE)
     */
    @GetMapping("/roles")
    public ResponseEntity<BaseResponseDto<List<UserRoleDto>>> getAllUserRoles() {
        logger.info("Getting all enabled user roles");
        try {
            List<UserRole> roles = userRoleRepository.findAllEnabledOrderByName();
            List<UserRoleDto> dtos = roles.stream().map(role -> UserRoleDto.builder()
                    .userRoleId(role.getUserRoleId())
                    .userRoleName(role.getUserRoleName())
                    .userRoleRemark(role.getUserRoleRemark())
                    .userRoleRemarkEn(role.getUserRoleRemarkEn())
                    .userRoleEnabled(role.getUserRoleEnabled())
                    .build()).toList();
            return ResponseEntity.ok(BaseResponseDto.success("User roles retrieved successfully", dtos));
        } catch (Exception e) {
            logger.error("Error getting user roles: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve user roles: " + e.getMessage()));
        }
    }

    /**
     * Get all enabled user providers (USER_PROVIDER)
     */
    @GetMapping("/providers")
    public ResponseEntity<BaseResponseDto<List<UserProviderDto>>> getAllUserProviders() {
        logger.info("Getting all enabled user providers");
        try {
            List<UserProvider> providers = userProviderRepository.findAllEnabledOrderByName();
            List<UserProviderDto> dtos = providers.stream().map(provider -> UserProviderDto.builder()
                    .userProviderId(provider.getUserProviderId())
                    .userProviderName(provider.getUserProviderName())
                    .userProviderRemark(provider.getUserProviderRemark())
                    .userProviderRemarkEn(provider.getUserProviderRemarkEn())
                    .userProviderEnabled(provider.getUserProviderEnabled())
                    .build()).toList();
            return ResponseEntity.ok(BaseResponseDto.success("User providers retrieved successfully", dtos));
        } catch (Exception e) {
            logger.error("Error getting user providers: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve user providers: " + e.getMessage()));
        }
    }

    /**
     * Get users by multiple criteria (all parameters optional)
     * Returns complete user information including USER_ROLE and USER_PROVIDER details
     * 
     * @param userId User ID filter (optional)
     * @param userProviderId Provider ID filter (optional)
     * @param userRoleId Role ID filter (optional)
     * @param userEnabled Enabled status filter (optional, default true)
     * @param userName Username filter (optional)
     * @return List of users matching criteria with full details
     */
    @GetMapping("/search")
    public ResponseEntity<BaseResponseDto<List<UserDto>>> getUsersByCriteria(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "userProviderId", required = false) Long userProviderId,
            @RequestParam(value = "userRoleId", required = false) Long userRoleId,
            @RequestParam(value = "userEnabled", required = false) Boolean userEnabled,
            @RequestParam(value = "userName", required = false) String userName) {
        
        logger.info("Getting users by criteria - userId: {}, providerId: {}, roleId: {}, enabled: {}, userName: {}", 
                   userId, userProviderId, userRoleId, userEnabled, userName);
        
        try {
            List<UserDto> users = userService.findUsersByCriteria(userId, userProviderId, userRoleId, userEnabled, userName);
            
            if (users.isEmpty()) {
                logger.info("No users found matching criteria");
                return ResponseEntity.ok(BaseResponseDto.success("No users found matching criteria", users));
            }
            
            logger.info("Found {} users matching criteria", users.size());
            return ResponseEntity.ok(BaseResponseDto.success("Users retrieved successfully", users));
            
        } catch (Exception e) {
            logger.error("Error getting users by criteria: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve users: " + e.getMessage()));
        }
    }

    /**
     * Get user by ID with full details
     * 
     * @param userId User ID
     * @return User details including provider and role information
     */
    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponseDto<UserDto>> getUserById(@PathVariable Long userId) {
        logger.info("Getting user by ID: {}", userId);
        
        try {
            Optional<UserDto> userOpt = userService.findUserById(userId);
            
            if (userOpt.isEmpty()) {
                logger.info("User not found with ID: {}", userId);
                return ResponseEntity.ok(BaseResponseDto.notFound("User not found with ID: " + userId));
            }
            
            logger.info("User found with ID: {}", userId);
            return ResponseEntity.ok(BaseResponseDto.success("User retrieved successfully", userOpt.get()));
            
        } catch (Exception e) {
            logger.error("Error getting user by ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve user: " + e.getMessage()));
        }
    }

    /**
     * Get all users with pagination
     * 
     * @param page Page number (default 0)
     * @param size Page size (default 10)
     * @return Paginated list of users
     */
    @GetMapping
    public ResponseEntity<BaseResponseDto<Page<UserDto>>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        logger.info("Getting all users - page: {}, size: {}", page, size);
        
        try {
            Page<UserDto> users = userService.getAllUsers(page, size);
            logger.info("Retrieved {} users from page {}", users.getContent().size(), page);
            return ResponseEntity.ok(BaseResponseDto.success("Users retrieved successfully", users));
            
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve users: " + e.getMessage()));
        }
    }

    /**
     * Create new user
     * 
     * @param userDto User data
     * @return Created user
     */
    @PostMapping
    public ResponseEntity<BaseResponseDto<UserDto>> createUser(@Valid @RequestBody UserDto userDto) {
        logger.info("Creating new user with username: {}", userDto.getUserName());
        
        try {
            UserDto createdUser = userService.createUser(userDto);
            logger.info("User created successfully with ID: {}", createdUser.getUserId());
            return ResponseEntity.ok(BaseResponseDto.success("User created successfully", createdUser));
            
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.serverError("Failed to create user: " + e.getMessage()));
        }
    }

    /**
     * Update existing user
     * 
     * @param userId User ID to update
     * @param userDto Updated user data
     * @return Updated user
     */
    @PutMapping("/{userId}")
    public ResponseEntity<BaseResponseDto<UserDto>> updateUser(
            @PathVariable Long userId, 
            @Valid @RequestBody UserDto userDto) {
        
        logger.info("Updating user with ID: {}", userId);
        
        try {
            UserDto updatedUser = userService.updateUser(userId, userDto);
            logger.info("User updated successfully with ID: {}", updatedUser.getUserId());
            return ResponseEntity.ok(BaseResponseDto.success("User updated successfully", updatedUser));
            
        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.serverError("Failed to update user: " + e.getMessage()));
        }
    }

    /**
     * Update user status (enable/disable)
     * 
     * @param userId User ID
     * @param enabled New enabled status
     * @return Updated user
     */
    @PatchMapping("/{userId}/status")
    public ResponseEntity<BaseResponseDto<UserDto>> updateUserStatus(
            @PathVariable Long userId, 
            @RequestParam Boolean enabled) {
        
        logger.info("Updating user status for ID: {} to enabled: {}", userId, enabled);
        
        try {
            UserDto updatedUser = userService.updateUserStatus(userId, enabled);
            logger.info("User status updated successfully for ID: {}", updatedUser.getUserId());
            return ResponseEntity.ok(BaseResponseDto.success("User status updated successfully", updatedUser));
            
        } catch (Exception e) {
            logger.error("Error updating user status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.serverError("Failed to update user status: " + e.getMessage()));
        }
    }

    /**
     * Delete user
     * 
     * @param userId User ID to delete
     * @return Success response
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<BaseResponseDto<String>> deleteUser(@PathVariable Long userId) {
        logger.info("Deleting user with ID: {}", userId);
        
        try {
            userService.deleteUser(userId);
            logger.info("User deleted successfully with ID: {}", userId);
            return ResponseEntity.ok(BaseResponseDto.success("User deleted successfully"));
            
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.serverError("Failed to delete user: " + e.getMessage()));
        }
    }

    /**
     * Search users by name
     * 
     * @param searchTerm Search term for first name or last name
     * @return List of users matching search term
     */
    @GetMapping("/search-by-name")
    public ResponseEntity<BaseResponseDto<List<UserDto>>> searchUsersByName(
            @RequestParam String searchTerm) {
        
        logger.info("Searching users by name: {}", searchTerm);
        
        try {
            List<UserDto> users = userService.searchUsersByName(searchTerm);
            logger.info("Found {} users matching search term: {}", users.size(), searchTerm);
            return ResponseEntity.ok(BaseResponseDto.success("Users found successfully", users));
            
        } catch (Exception e) {
            logger.error("Error searching users by name: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to search users: " + e.getMessage()));
        }
    }

    /**
     * Check if username exists
     * 
     * @param userName Username to check
     * @return Boolean response
     */
    @GetMapping("/check-username")
    public ResponseEntity<BaseResponseDto<Boolean>> checkUsernameExists(@RequestParam String userName) {
        logger.debug("Checking if username exists: {}", userName);
        
        try {
            boolean exists = userService.existsByUserName(userName);
            return ResponseEntity.ok(BaseResponseDto.success("Username check completed", exists));
        } catch (Exception e) {
            logger.error("Error checking username: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to check username: " + e.getMessage()));
        }
    }

    /**
     * Check if email exists
     * 
     * @param email Email to check
     * @return Boolean response
     */
    @GetMapping("/check-email")
    public ResponseEntity<BaseResponseDto<Boolean>> checkEmailExists(@RequestParam String email) {
        logger.debug("Checking if email exists: {}", email);
        
        try {
            boolean exists = userService.existsByEmail(email);
            return ResponseEntity.ok(BaseResponseDto.success("Email check completed", exists));
        } catch (Exception e) {
            logger.error("Error checking email: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to check email: " + e.getMessage()));
        }
    }

    // =============================================
    // PASSWORD MANAGEMENT APIs
    // =============================================

    /**
     * Change password for authenticated user (self-service)
     * 
     * @param request Password change request
     * @param auth Authentication object (contains current user info)
     * @return Updated user response
     */
    @PutMapping("/change-password")
    public ResponseEntity<BaseResponseDto<UserDto>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication auth) {
        
        String currentUsername = auth.getName();
        logger.info("Password change request for user: {}", currentUsername);
        
        try {
            UserDto updatedUser = userService.changePassword(currentUsername, request);
            logger.info("Password changed successfully for user: {}", currentUsername);
            return ResponseEntity.ok(BaseResponseDto.success("Password changed successfully", updatedUser));
            
        } catch (IllegalArgumentException e) {
            logger.error("Password change validation error for user {}: {}", currentUsername, e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error changing password for user {}: {}", currentUsername, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.serverError("Failed to change password"));
        }
    }

    /**
     * Admin reset password for any user (ID-based priority for admin efficiency)
     * 
     * @param userId User ID to reset password
     * @param request Password reset request
     * @param auth Authentication object (admin user)
     * @return Updated user response
     */
    @PutMapping("/{userId}/reset-password")
    public ResponseEntity<BaseResponseDto<UserDto>> resetPassword(
            @PathVariable Long userId,
            @Valid @RequestBody ResetPasswordRequest request,
            Authentication auth) {
        
        logger.info("Admin password reset request for user ID: {} by admin: {}", userId, auth.getName());
        
        try {
            UserDto updatedUser = userService.resetPassword(userId, request);
            logger.info("Password reset successfully for user ID: {} by admin: {}", userId, auth.getName());
            return ResponseEntity.ok(BaseResponseDto.success("Password reset successfully", updatedUser));
            
        } catch (IllegalArgumentException e) {
            logger.error("Password reset validation error for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error resetting password for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.serverError("Failed to reset password"));
        }
    }

    /**
     * Customer change password by username (customer-friendly identification)
     * 
     * @param userName Username for password change
     * @param request Password change request
     * @param auth Authentication object (for security validation)
     * @return Updated user response
     */
    @PutMapping("/username/{userName}/change-password")
    public ResponseEntity<BaseResponseDto<UserDto>> changePasswordByUsername(
            @PathVariable String userName,
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication auth) {
        
        logger.info("Username-based password change request for: {}", userName);
        
        // Security check: verify the authenticated user matches the username (for customers)
        if (!auth.getName().equals(userName)) {
            logger.error("Unauthorized password change attempt: {} trying to change password for {}", 
                        auth.getName(), userName);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Cannot change password for other users"));
        }
        
        try {
            UserDto updatedUser = userService.changePasswordByUsername(userName, request);
            logger.info("Password changed successfully for username: {}", userName);
            return ResponseEntity.ok(BaseResponseDto.success("Password changed successfully", updatedUser));
            
        } catch (IllegalArgumentException e) {
            logger.error("Password change validation error for username {}: {}", userName, e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error changing password for username {}: {}", userName, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.serverError("Failed to change password"));
        }
    }
}