package org.ArtAndDecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.ChangePasswordRequest;
import org.ArtAndDecor.dto.UserDto;
import org.ArtAndDecor.model.User;
import org.ArtAndDecor.model.UserProvider;
import org.ArtAndDecor.model.UserRole;
import org.ArtAndDecor.repository.UserRepository;
import org.ArtAndDecor.repository.UserProviderRepository;
import org.ArtAndDecor.repository.UserRoleRepository;
import org.ArtAndDecor.services.UserService;
import org.ArtAndDecor.services.EmailService;
import org.ArtAndDecor.utils.UserMapperUtil;
import org.ArtAndDecor.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User Service Implementation
 * Handles all user-related business logic with optimized queries
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserProviderRepository userProviderRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        logger.info("Creating new user with username: {}", userDto.getUserName());

        User user = convertToEntity(userDto);
        
        // Set defaults
        if (user.getUserProvider() == null) {
            // Default to LOCAL provider (ID = 1)
            UserProvider localProvider = userProviderRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Default LOCAL provider not found"));
            user.setUserProvider(localProvider);
        }

        if (user.getUserRole() == null) {
            // Default to CUSTOMER role (ID = 4)
            UserRole customerRole = userRoleRepository.findById(4L)
                .orElseThrow(() -> new IllegalArgumentException("Default CUSTOMER role not found"));
            user.setUserRole(customerRole);
        }

        if (user.getUserEnabled() == null) {
            user.setUserEnabled(true);
        }

        // Encode password if provided
        if (userDto.getPassword() != null && !userDto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getUserId());
        
        return convertToDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        logger.info("Updating user with ID: {}", userId);

        User existingUser = userRepository.findByIdWithDetails(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Update fields
        updateUserFields(existingUser, userDto);

        User savedUser = userRepository.save(existingUser);
        logger.info("User updated successfully with ID: {}", savedUser.getUserId());
        
        return convertToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findUserById(Long userId) {
        logger.debug("Finding user by ID: {}", userId);
        
        return userRepository.findByIdWithDetails(userId)
            .map(this::convertToDto);
    }

    @Override
    @Transactional
    public UserDto updateUserStatus(Long userId, Boolean enabled) {
        logger.info("Updating user status for ID: {} to enabled: {}", userId, enabled);
        
        User user = userRepository.findByIdWithDetails(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        user.setUserEnabled(enabled);
        User savedUser = userRepository.save(user);
        
        return convertToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> findUsersByCriteria(String userProviderName, String userProviderDisplayName, 
                                           String userRoleName, String userRoleDisplayName, 
                                           String textSearch, String userName, Boolean userEnabled, 
                                           Pageable pageable) {
        logger.debug("Finding users by enhanced criteria with pagination");
        
        Page<User> usersPage = userRepository.findUsersAdvancedCriteriaPaginated(
                userProviderName, userProviderDisplayName, userRoleName, userRoleDisplayName, 
                textSearch, userName, userEnabled, pageable);
        
        return usersPage.map(this::convertToDto);
    }

    @Override
    public UserDto convertToDto(User user) {
        return UserMapperUtil.toDetailedDto(user);
    }

    @Override
    public User convertToEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        User user = new User();
        
        // Basic user fields
        user.setUserId(userDto.getUserId());
        user.setUserEnabled(userDto.getUserEnabled());
        user.setUserName(userDto.getUserName());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setEmail(userDto.getEmail());
        user.setImageAvatarName(userDto.getImageAvatarName());
        user.setSocialMedia(userDto.getSocialMedia());
        user.setLastLoginDt(userDto.getLastLoginDt());

        // Set provider if provided
        if (UserMapperUtil.getUserProviderId(userDto) != null) {
            UserProvider provider = userProviderRepository.findById(UserMapperUtil.getUserProviderId(userDto))
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + UserMapperUtil.getUserProviderId(userDto)));
            user.setUserProvider(provider);
        }

        // Set role if provided
        if (UserMapperUtil.getUserRoleId(userDto) != null) {
            UserRole role = userRoleRepository.findById(UserMapperUtil.getUserRoleId(userDto))
                .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + UserMapperUtil.getUserRoleId(userDto)));
            user.setUserRole(role);
        }

        return user;
    }

    @Override
    @Transactional
    public UserDto changePassword(String username, ChangePasswordRequest request) {
        logger.info("Changing password for user: {}", username);

        // 1. Validate passwords match
        if (!request.isPasswordsMatch()) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }
        
        // 2. Find user by username using enabled-only method
        User user = userRepository.findByUserNameAndUserEnabled(username, true)
            .orElseThrow(() -> new IllegalArgumentException("User not found or not enabled: " + username));
        
        // 3. Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // 4. Validate new password is different
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }
        
        // 5. Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setModifiedDt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        logger.info("Password changed successfully for user: {}", username);
        
        return convertToDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDto resetPassword(String userName) {
        logger.info("Starting password reset for user: {}", userName);
        
        // 1. Find user by username (enabled or disabled)
        User user = userRepository.findByUserNameAndUserEnabled(userName, true)
            .or(() -> userRepository.findByUserNameAndUserEnabled(userName, false))
            .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + userName));

        // 2. Generate new random password
        String newPassword = PasswordUtils.generateRandomPassword();
        
        // 3. Update user password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setModifiedDt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        logger.info("Password reset successfully for user: {}", userName);

        // 4. Send password reset email notification
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), newPassword);
            logger.info("Password reset email sent successfully to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send password reset email to: {}. Error: {}", user.getEmail(), e.getMessage());
            // Note: We continue execution even if email fails, as password has been reset
        }

        return convertToDto(updatedUser);
    }

    /**
     * Update user fields from DTO
     */
    private void updateUserFields(User user, UserDto userDto) {
        if (userDto.getUserEnabled() != null) {
            user.setUserEnabled(userDto.getUserEnabled());
        }
        if (userDto.getUserName() != null) {
            user.setUserName(userDto.getUserName());
        }
        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }
        if (userDto.getPhoneNumber() != null) {
            user.setPhoneNumber(userDto.getPhoneNumber());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getImageAvatarName() != null) {
            user.setImageAvatarName(userDto.getImageAvatarName());
        }
        if (userDto.getSocialMedia() != null) {
            user.setSocialMedia(userDto.getSocialMedia());
        }

        // Update password if provided
        if (userDto.getPassword() != null && !userDto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // Update provider if provided
        if (UserMapperUtil.getUserProviderId(userDto) != null) {
            UserProvider provider = userProviderRepository.findById(UserMapperUtil.getUserProviderId(userDto))
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + UserMapperUtil.getUserProviderId(userDto)));
            user.setUserProvider(provider);
        }

        // Update role if provided
        if (UserMapperUtil.getUserRoleId(userDto) != null) {
            UserRole role = userRoleRepository.findById(UserMapperUtil.getUserRoleId(userDto))
                .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + UserMapperUtil.getUserRoleId(userDto)));
            user.setUserRole(role);
        }
    }
}