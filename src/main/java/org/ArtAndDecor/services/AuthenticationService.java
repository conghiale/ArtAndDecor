package org.ArtAndDecor.services;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.*;
import org.ArtAndDecor.model.User;
import org.ArtAndDecor.model.UserProvider;
import org.ArtAndDecor.model.UserRole;
import org.ArtAndDecor.repository.UserProviderRepository;
import org.ArtAndDecor.repository.UserRepository;
import org.ArtAndDecor.repository.UserRoleRepository;
import org.ArtAndDecor.utils.UserMapperUtil;
import org.ArtAndDecor.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

/**
 * Authentication Service
 * Handles user registration, authentication, and token refresh operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final UserProviderRepository userProviderRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user
     */
    public AuthResponse register(RegisterRequest request) {
        logger.info("Registering new user with username: {}", request.getUserName());
        
        try {
            // Validate username and email uniqueness
            if (userRepository.existsByUserName(request.getUserName())) {
                logger.error("Registration failed: Username already exists: {}", request.getUserName());
                return AuthResponse.error("Username already exists");
            }
            
            if (userRepository.existsByEmail(request.getEmail())) {
                logger.error("Registration failed: Email already exists: {}", request.getEmail());
                return AuthResponse.error("Email already exists");
            }

            // Get user provider (default to LOCAL if not specified)
            UserProvider userProvider = getUserProvider(request.getUserProviderId());
            if (userProvider == null) {
                logger.error("Registration failed: Invalid user provider ID: {}", request.getUserProviderId());
                return AuthResponse.error("Invalid user provider");
            }

            // Get user role (default to CUSTOMER if not specified)
            UserRole userRole = getUserRole(request.getUserRoleId());
            if (userRole == null) {
                logger.error("Registration failed: Invalid user role ID: {}", request.getUserRoleId());
                return AuthResponse.error("Invalid user role");
            }

            // Create new user
            User user = User.builder()
                    .userName(request.getUserName())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .imageAvatarName(request.getImageAvatarName())
                    .socialMedia(request.getSocialMedia())
                    .userProvider(userProvider)
                    .userRole(userRole)
                    .userEnabled(true)
                    .createdDt(LocalDateTime.now())
                    .modifiedDt(LocalDateTime.now())
                    .build();

            // Save user
            User savedUser = userRepository.save(user);
            logger.info("User registered successfully with ID: {}", savedUser.getUserId());

            // Generate token pair
            TokenPair tokenPair = jwtService.generateTokenPair(new HashMap<>(), savedUser);

            // Convert to DTO
            UserDto userDto = convertToUserDto(savedUser);

            return AuthResponse.success("User registered successfully", tokenPair, userDto);

        } catch (Exception e) {
            logger.error("Error registering user {}: {}", request.getUserName(), e.getMessage(), e);
            return AuthResponse.error("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Authenticate user login
     */
    public AuthResponse authenticate(AuthRequest request) {
        logger.info("Authenticating user: {}", request.getUsernameOrEmail());
        
        try {
            // Find enabled user by username or email
            Optional<User> userOpt = userRepository.findByUserNameAndUserEnabled(request.getUsernameOrEmail(), true)
                    .or(() -> userRepository.findByEmailAndUserEnabled(request.getUsernameOrEmail(), true));
            
            if (userOpt.isEmpty()) {
                logger.error("Authentication failed: User not found or not enabled: {}", request.getUsernameOrEmail());
                return AuthResponse.error("Invalid credentials");
            }
            
            User user = userOpt.get();

            // Authenticate using username (not email)
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
            );

            // Update last login time
            user.setLastLoginDt(LocalDateTime.now());
            userRepository.save(user);

            // Generate token pair
            TokenPair tokenPair = jwtService.generateTokenPair(new HashMap<>(), user);
            
            // Convert to DTO
            UserDto userDto = convertToUserDto(user);

            logger.info("User authenticated successfully: {}", user.getUsername());
            return AuthResponse.success("Authentication successful", tokenPair, userDto);

        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user {}: {}", request.getUsernameOrEmail(), e);
            return AuthResponse.error("Invalid credentials");
        } catch (Exception e) {
            logger.error("Error authenticating user {}: {}", request.getUsernameOrEmail(), e.getMessage(), e);
            return AuthResponse.error("Authentication failed");
        }
    }

    /**
     * Refresh access token using refresh token
     */
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        logger.info("Processing token refresh request");
        
        try {
            // Validate refresh token and extract username
            String username = jwtService.validateRefreshToken(request.getRefreshToken());
            if (username == null) {
                logger.error("Token refresh failed: Invalid refresh token");
                return AuthResponse.error("Invalid refresh token");
            }

            // Find user
            Optional<User> userOpt = userRepository.findByUserNameAndUserEnabled(username, true);
            if (userOpt.isEmpty()) {
                logger.error("Token refresh failed: User not found: {}", username);
                return AuthResponse.error("User not found");
            }
            
            User user = userOpt.get();
            
            // Check if user is still enabled
            if (!user.isEnabled()) {
                logger.error("Token refresh failed: User is disabled: {}", username);
                return AuthResponse.error("Account is disabled");
            }

            // Generate new token pair
            TokenPair tokenPair = jwtService.generateTokenPair(new HashMap<>(), user);
            
            // Convert to DTO
            UserDto userDto = convertToUserDto(user);

            logger.info("Token refreshed successfully for user: {}", username);
            return AuthResponse.success("Token refreshed successfully", tokenPair, userDto);

        } catch (Exception e) {
            logger.error("Error refreshing token: {}", e.getMessage(), e);
            return AuthResponse.error("Token refresh failed");
        }
    }

    /**
     * Get user provider by ID, default to LOCAL if not found
     */
    private UserProvider getUserProvider(Long userProviderId) {
        if (userProviderId == null) {
            // Default to LOCAL provider
            return userProviderRepository.findByUserProviderName("LOCAL").orElse(null);
        }
        return userProviderRepository.findById(userProviderId).orElse(null);
    }

    /**
     * Get user role by ID, default to CUSTOMER if not found
     */
    private UserRole getUserRole(Long userRoleId) {
        if (userRoleId == null) {
            // Default to CUSTOMER role
            return userRoleRepository.findByUserRoleName("CUSTOMER").orElse(null);
        }
        return userRoleRepository.findById(userRoleId).orElse(null);
    }

    /**
     * Convert User entity to UserDto
     */
    private UserDto convertToUserDto(User user) {
        return UserMapperUtil.toDetailedDto(user);
    }
}