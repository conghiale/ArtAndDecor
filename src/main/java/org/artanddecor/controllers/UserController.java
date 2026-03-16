package org.artanddecor.controllers;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.UserRoleDto;
import org.artanddecor.dto.UserProviderDto;
import org.artanddecor.dto.BaseResponseDto;
import org.artanddecor.dto.ChangePasswordRequest;
import org.artanddecor.dto.UserDto;
import org.artanddecor.services.UserService;
import org.artanddecor.services.UserRoleService;
import org.artanddecor.services.UserProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * User Management REST Controller
 * Handles all USER MANAGEMENT API operations with role-based access control
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "APIs for managing users, user roles, and user providers with advanced filtering and search capabilities")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final UserRoleService userRoleService;

    private final UserProviderService userProviderService;

     /*=============================================
     USER ROLE MANAGEMENT APIs
     =============================================*/

    /**
     * Get user roles with optional filtering
     * Returns all roles if no filters are provided, or filtered results based on criteria
     * 
     * @param userRoleName Role name filter (optional) 
     * @param textSearch Text search in role name, display name, or remark (optional)
     * @param userRoleEnabled Enabled status filter (optional)
     * @return List of roles matching criteria
     */
    @GetMapping("/roles")
    @Operation(summary = "Retrieve user roles with optional filtering",
               description = "Get all user roles or filter by specific criteria. Returns all roles when no parameters are provided. Supports text search across role name, display name, and remark fields.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<List<UserRoleDto>>> getRolesByCriteria(
            @Parameter(description = "Filter by exact role name") @RequestParam(value = "roleName", required = false) String userRoleName,
            @Parameter(description = "Search text in role name, display name, or remark (case-insensitive)")
            @RequestParam(value = "textSearch", required = false) String textSearch,
            @Parameter(description = "Filter by enabled status (true/false/null for all)") @RequestParam(value = "enabled", required = false) Boolean userRoleEnabled) {
        
        logger.info("Getting roles by criteria - roleName: {}, textSearch: {}, enabled: {}", 
                   userRoleName, textSearch, userRoleEnabled);
        
        try {
            List<UserRoleDto> roles = userRoleService.findRolesByCriteria(userRoleName, textSearch, userRoleEnabled);
            
            logger.info("Found {} roles matching criteria", roles.size());
            return ResponseEntity.ok(BaseResponseDto.success("Roles retrieved successfully", roles));
            
        } catch (Exception e) {
            logger.error("Error getting roles by criteria: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve roles: " + e.getMessage()));
        }
    }

    /**
     * Get detailed information for a specific user role
     * 
     * @param roleId Role ID to retrieve
     * @return Role details with complete information
     */
    @GetMapping("/roles/{roleId}")
    @Operation(summary = "Get user role by ID",
               description = "Retrieve detailed information for a specific user role including role name, display name, remark, and enabled status.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found with provided ID"),
        @ApiResponse(responseCode = "400", description = "Invalid role ID format"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<UserRoleDto>> getRoleById(
            @Parameter(description = "Unique identifier of the role to retrieve")
            @PathVariable Long roleId) {
        logger.info("Getting role by ID: {}", roleId);
        
        try {
            Optional<UserRoleDto> roleOpt = userRoleService.findRoleById(roleId);

            return roleOpt.map(userRoleDto -> ResponseEntity.ok(BaseResponseDto.success("Role retrieved successfully", userRoleDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound("Role not found with ID: " + roleId)));

        } catch (Exception e) {
            logger.error("Error getting role by ID {}: {}", roleId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve role: " + e.getMessage()));
        }
    }

    /**
     * Get all user role names for dropdown/combobox
     * 
     * @return List of role names
     */
    @GetMapping("/roles/names")
    @Operation(summary = "Get all user role names",
               description = "Retrieve list of all enabled user role names for dropdown/combobox usage in UI.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role names retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<List<String>>> getAllRoleNames() {
        logger.info("Getting all role names");
        
        try {
            List<String> roleNames = userRoleService.getAllRoleNames();
            
            logger.info("Found {} role names", roleNames.size());
            return ResponseEntity.ok(BaseResponseDto.success("Role names retrieved successfully", roleNames));
            
        } catch (Exception e) {
            logger.error("Error getting all role names: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponseDto.serverError("Failed to retrieve role names: " + e.getMessage()));
        }
    }

    /* =============================================
     USER PROVIDER MANAGEMENT APIs
     =============================================*/

    /**
     * Get user providers with optional filtering
     * Returns all providers if no filters are provided, or filtered results based on criteria
     * 
     * @param userProviderName Provider name filter (optional) 
     * @param textSearch Text search in provider name, display name, or remark (optional)
     * @param userProviderEnabled Enabled status filter (optional)
     * @return List of providers matching criteria
     */
    @GetMapping("/providers")
    @Operation(summary = "Retrieve user providers with optional filtering",
               description = "Get all user providers or filter by specific criteria. Returns all providers when no parameters are provided. Supports text search across provider name, display name, and remark fields.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Providers retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<List<UserProviderDto>>> getProvidersByCriteria(
            @Parameter(description = "Filter by exact provider name") @RequestParam(value = "providerName", required = false) String userProviderName,
            @Parameter(description = "Search text in provider name, display name, or remark (case-insensitive)")
            @RequestParam(value = "textSearch", required = false) String textSearch,
            @Parameter(description = "Filter by enabled status (true/false/null for all)") @RequestParam(value = "enabled", required = false) Boolean userProviderEnabled) {
        
        logger.info("Getting providers by criteria - providerName: {}, textSearch: {}, enabled: {}", 
                   userProviderName, textSearch, userProviderEnabled);
        
        try {
            List<UserProviderDto> providers = userProviderService.findProvidersByCriteria(userProviderName, textSearch, userProviderEnabled);
            
            logger.info("Found {} providers matching criteria", providers.size());
            return ResponseEntity.ok(BaseResponseDto.success("Providers retrieved successfully", providers));
            
        } catch (Exception e) {
            logger.error("Error getting providers by criteria: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve providers: " + e.getMessage()));
        }
    }

    /**
     * Get detailed information for a specific user provider
     * 
     * @param providerId Provider ID to retrieve
     * @return Provider details with complete information
     */
    @GetMapping("/providers/{providerId}")
    @Operation(summary = "Get user provider by ID",
               description = "Retrieve detailed information for a specific user provider including provider name, display name, remark, and enabled status.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Provider retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Provider not found with provided ID"),
        @ApiResponse(responseCode = "400", description = "Invalid provider ID format"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<UserProviderDto>> getProviderById(
            @Parameter(description = "Unique identifier of the provider to retrieve")
            @PathVariable Long providerId) {
        logger.info("Getting provider by ID: {}", providerId);
        
        try {
            Optional<UserProviderDto> providerOpt = userProviderService.findProviderById(providerId);

            return providerOpt.map(userProviderDto -> ResponseEntity.ok(BaseResponseDto.success("Provider retrieved successfully", userProviderDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound("Provider not found with ID: " + providerId)));

        } catch (Exception e) {
            logger.error("Error getting provider by ID {}: {}", providerId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve provider: " + e.getMessage()));
        }
    }

    /**
     * Get all user provider names for dropdown/combobox
     * 
     * @return List of provider names
     */
    @GetMapping("/providers/names")
    @Operation(summary = "Get all user provider names",
               description = "Retrieve list of all enabled user provider names for dropdown/combobox usage in UI.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Provider names retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<List<String>>> getAllProviderNames() {
        logger.info("Getting all provider names");
        
        try {
            List<String> providerNames = userProviderService.getAllProviderNames();
            
            logger.info("Found {} provider names", providerNames.size());
            return ResponseEntity.ok(BaseResponseDto.success("Provider names retrieved successfully", providerNames));
            
        } catch (Exception e) {
            logger.error("Error getting all provider names: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponseDto.serverError("Failed to retrieve provider names: " + e.getMessage()));
        }
    }

    /* =============================================
     USER MANAGEMENT APIs
     =============================================*/

    /**
     * Get users with advanced filtering and pagination
     * Returns complete user information including USER_ROLE and USER_PROVIDER details
     * Returns all users if no search criteria are provided
     * Enhanced textSearch includes USER_PROVIDER_DISPLAY_NAME and USER_ROLE_DISPLAY_NAME
     * 
     * @param providerName Provider name filter (optional)
     * @param providerDisplayName Provider display name filter (optional)
     * @param roleName Role name filter (optional) 
     * @param roleDisplayName Role display name filter (optional)
     * @param searchText Text search across multiple fields including user details and provider/role display names (optional)
     * @param userName Username filter (optional)
     * @param userEnabled User enabled status filter (optional)
     * @param pageable Pagination and sorting information
     * @return Paginated list of users matching criteria with full details
     */
    @GetMapping
    @Operation(summary = "Get users with advanced filtering and pagination",
               description = "Retrieve users by multiple criteria with pagination support. Returns all users when no filters are provided. Enhanced textSearch includes USER_PROVIDER_DISPLAY_NAME and USER_ROLE_DISPLAY_NAME for comprehensive searching.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Page<UserDto>>> getUsersByCriteria(
            @Parameter(description = "Filter by provider name (partial match, case-insensitive)") @RequestParam(value = "providerName", required = false) String providerName,
            @Parameter(description = "Filter by provider display name (partial match, case-insensitive)") @RequestParam(value = "providerDisplayName", required = false) String providerDisplayName,
            @Parameter(description = "Filter by role name (partial match, case-insensitive)") @RequestParam(value = "roleName", required = false) String roleName,
            @Parameter(description = "Filter by role display name (partial match, case-insensitive)") @RequestParam(value = "roleDisplayName", required = false) String roleDisplayName,
            @Parameter(description = "Enhanced search text across username, first name, last name, phone number, email, provider display name, and role display name (partial match, case-insensitive)") @RequestParam(value = "searchText", required = false) String searchText,
            @Parameter(description = "Filter by exact username match") @RequestParam(value = "userName", required = false) String userName,
            @Parameter(description = "Filter by user enabled status (true/false/null for all)") @RequestParam(value = "userEnabled", required = false) Boolean userEnabled,
            @Parameter(description = "Pagination parameters - page number (0-based), size, sort field, and direction")
            @PageableDefault(page = 0, size = 10, sort = "userId",
                            direction = org.springframework.data.domain.Sort.Direction.ASC)
            Pageable pageable) {
        
        logger.info("Getting users by enhanced criteria - providerName: {}, roleName: {}, searchText: {}, page: {}, size: {}", 
                   providerName, roleName, searchText, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<UserDto> users = userService.findUsersByCriteria(
                providerName, providerDisplayName, roleName, roleDisplayName, 
                searchText, userName, userEnabled, pageable);
            
            if (users.isEmpty()) {
                logger.info("No users found matching criteria");
                return ResponseEntity.ok(BaseResponseDto.success("No users found matching criteria", users));
            }
            
            logger.info("Found {} users matching criteria on page {}", users.getContent().size(), pageable.getPageNumber());
            return ResponseEntity.ok(BaseResponseDto.success("Users retrieved successfully", users));
            
        } catch (Exception e) {
            logger.error("Error getting users by criteria: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve users: " + e.getMessage()));
        }
    }

    /**
     * Get detailed information for a specific user
     * 
     * @param userId User ID to retrieve
     * @return User details including provider and role information
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID",
               description = "Retrieve detailed information for a specific user including user profile, role details, provider information, and account status.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found with provided ID"),
        @ApiResponse(responseCode = "400", description = "Invalid user ID format"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<UserDto>> getUserById(
            @Parameter(description = "Unique identifier of the user to retrieve")
            @PathVariable Long userId) {
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
     * Create a new user account
     * 
     * @param userDto User data for creation
     * @return Created user with generated ID
     */
    @PostMapping
    @Operation(summary = "Create new user",
               description = "Create a new user account with provided information. Default provider and role will be assigned if not specified. Password will be encoded before storage.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user data or validation errors"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "409", description = "User with username or email already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<UserDto>> createUser(
            @Parameter(description = "User data including username, email, personal information, role, and provider")
            @Valid @RequestBody UserDto userDto) {
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
     * Update an existing user account
     * 
     * @param userId User ID to update
     * @param userDto Updated user data
     * @return Updated user information
     */
    @PutMapping("/{userId}")
    @Operation(summary = "Update existing user",
               description = "Update user information including personal details, role, provider, and account settings. Only provided fields will be updated.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user data or validation errors"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "User not found with provided ID"),
        @ApiResponse(responseCode = "409", description = "Username or email conflicts with existing user"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<UserDto>> updateUser(
            @Parameter(description = "Unique identifier of the user to update") @PathVariable Long userId,
            @Parameter(description = "Updated user data with new information") @Valid @RequestBody UserDto userDto) {
        
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
     * Update user account status (enable/disable)
     * 
     * @param userId User ID to update status for
     * @param enabled New enabled status (true to enable, false to disable)
     * @return Updated user with new status
     */
    @PatchMapping("/{userId}/status")
    @Operation(summary = "Update user account status",
               description = "Enable or disable a user account. Disabled accounts cannot authenticate or access the system.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "User not found with provided ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<UserDto>> updateUserStatus(
            @Parameter(description = "Unique identifier of the user to update status") @PathVariable Long userId,
            @Parameter(description = "New enabled status (true to enable, false to disable)") @RequestParam Boolean enabled) {
        
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

    /* =============================================
     PASSWORD MANAGEMENT APIs
     =============================================*/

    /**
     * Change password for the currently authenticated user (self-service)
     * 
     * @param request Password change request containing current and new passwords
     * @param auth Authentication object containing current user information
     * @return Updated user response confirming password change
     */
    @PutMapping("/change-password")
    @Operation(summary = "Change password for authenticated user",
               description = "Allow authenticated users to change their own password by providing current password and new password. Current password is validated before change.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid password data or validation errors (current password incorrect, passwords don't match, etc.)"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<UserDto>> changePassword(
            @Parameter(description = "Password change request with current password, new password, and confirmation")
            @Valid @RequestBody ChangePasswordRequest request,
            @Parameter(hidden = true) Authentication auth) {
        
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
     * Admin reset password for any user (generates random password with email notification)
     * Generates a secure random password automatically and sends email notification to user
     * 
     * @param userName Username of the account to reset password for
     * @param auth Authentication object containing admin user information
     * @return Updated user response confirming password reset
     */
    @PutMapping("/reset-password/{userName}")
    @Operation(summary = "Admin reset password for any user",
               description = "Administrative function to reset any user's password. Generates a secure random password and sends email notification to the user with new credentials.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully with email notification sent"),
        @ApiResponse(responseCode = "400", description = "Invalid username or user not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error or email sending failure")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<UserDto>> resetPassword(
            @Parameter(description = "Username of the account to reset password for") @PathVariable String userName,
            @Parameter(hidden = true) Authentication auth) {
        
        logger.info("Admin password reset request for username: {} by admin: {}", userName, auth.getName());
        
        try {
            UserDto updatedUser = userService.resetPassword(userName);
            logger.info("Password reset successfully for username: {} by admin: {}", userName, auth.getName());
            return ResponseEntity.ok(BaseResponseDto.success("Password reset successfully with email notification sent", updatedUser));
            
        } catch (IllegalArgumentException e) {
            logger.error("Password reset validation error for user {}: {}", userName, e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error resetting password for user {}: {}", userName, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.serverError("Failed to reset password"));
        }
    }
}