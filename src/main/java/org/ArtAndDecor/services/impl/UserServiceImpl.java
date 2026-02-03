package org.ArtAndDecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.ChangePasswordRequest;
import org.ArtAndDecor.dto.ResetPasswordRequest;
import org.ArtAndDecor.dto.UserDto;
import org.ArtAndDecor.model.User;
import org.ArtAndDecor.model.UserProvider;
import org.ArtAndDecor.model.UserRole;
import org.ArtAndDecor.repository.UserRepository;
import org.ArtAndDecor.repository.UserProviderRepository;
import org.ArtAndDecor.repository.UserRoleRepository;
import org.ArtAndDecor.services.UserService;
import org.ArtAndDecor.utils.UserMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        logger.info("Creating new user with username: {}", userDto.getUserName());

        // Validation
        if (userDto.getUserName() != null && userRepository.existsByUserName(userDto.getUserName())) {
            throw new IllegalArgumentException("Username already exists: " + userDto.getUserName());
        }

        if (userDto.getEmail() != null && userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
        }

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

        // Check username uniqueness (if changing)
        if (userDto.getUserName() != null && 
            !userDto.getUserName().equals(existingUser.getUsername()) &&
            userRepository.existsByUserName(userDto.getUserName())) {
            throw new IllegalArgumentException("Username already exists: " + userDto.getUserName());
        }

        // Check email uniqueness (if changing)
        if (userDto.getEmail() != null && 
            !userDto.getEmail().equals(existingUser.getEmail()) &&
            userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
        }

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
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(int page, int size) {
        logger.debug("Getting all users - page: {}, size: {}", page, size);
        
        List<User> allUsers = userRepository.findAllWithDetails();
        
        int start = page * size;
        int end = Math.min(start + size, allUsers.size());
        
        List<UserDto> pageContent;
        if (start >= allUsers.size()) {
            pageContent = List.of();
        } else {
            pageContent = allUsers.subList(start, end).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        }
        
        return new PageImpl<>(pageContent, PageRequest.of(page, size), allUsers.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> searchUsersByName(String searchTerm) {
        logger.debug("Searching users by name: {}", searchTerm);
        
        return userRepository.searchByNameWithDetails(searchTerm).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
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
    public void deleteUser(Long userId) {
        logger.info("Deleting user with ID: {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        
        userRepository.deleteById(userId);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findUsersByCriteria(Long userId, Long userProviderId, Long userRoleId, 
                                           Boolean userEnabled, String userName) {
        logger.debug("Finding users by criteria");
        
        return userRepository.findUsersByCriteria(userId, userProviderId, userRoleId, userEnabled, userName)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
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
    public UserDto resetPassword(Long userId, ResetPasswordRequest request) {
        logger.info("Admin resetting password for user ID: {}", userId);

        // 1. Find user by ID
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        // 2. Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setModifiedDt(LocalDateTime.now());
        // Note: Add password reset timestamp and force change fields to User entity if needed
        
        User updatedUser = userRepository.save(user);
        logger.info("Password reset successfully for user ID: {}", userId);
        
        return convertToDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDto changePasswordByUsername(String username, ChangePasswordRequest request) {
        logger.info("Changing password by username: {}", username);
        
        // Same logic as changePassword but with explicit username parameter
        return changePassword(username, request);
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