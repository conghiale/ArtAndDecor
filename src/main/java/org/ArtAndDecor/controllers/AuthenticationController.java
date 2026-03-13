package org.ArtAndDecor.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.*;
import org.ArtAndDecor.services.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST Controller
 * Handles user registration, authentication, and token management
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    /**
     * Register a new user
     * 
     * @param request Registration request containing user details
     * @return Authentication response with token and user info
     */
    @PostMapping("/register")
    public ResponseEntity<BaseResponseDto<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration request for username: {}", request.getUserName());
        
        try {
            AuthResponse response = authenticationService.register(request);
            
            if (response.getSuccess()) {
                logger.info("User registration successful: {}", request.getUserName());
                return ResponseEntity.ok(BaseResponseDto.success("User registered successfully", response));
            } else {
                logger.warn("User registration failed: {}", response.getMessage());
                return ResponseEntity.badRequest()
                        .body(BaseResponseDto.badRequest(response.getMessage()));
            }
            
        } catch (Exception e) {
            logger.error("Error during registration for user {}: {}", request.getUserName(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(BaseResponseDto.serverError("Registration failed: " + e.getMessage()));
        }
    }

    /**
     * Authenticate user login
     * 
     * @param request Authentication request containing username/email and password
     * @return Authentication response with token and user info
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponseDto<AuthResponse>> authenticate(@Valid @RequestBody AuthRequest request) {
        logger.info("Authentication request for user: {}", request.getUsernameOrEmail());
        
        try {
            AuthResponse response = authenticationService.authenticate(request);
            
            if (response.getSuccess()) {
                logger.info("User authentication successful: {}", request.getUsernameOrEmail());
                return ResponseEntity.ok(BaseResponseDto.success("Authentication successful", response));
            } else {
                logger.error("User authentication failed: {}", response.getMessage());
                return ResponseEntity.badRequest()
                        .body(BaseResponseDto.badRequest(response.getMessage()));
            }
            
        } catch (Exception e) {
            logger.error("Error during authentication for user {}: {}", request.getUsernameOrEmail(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(BaseResponseDto.serverError("Authentication failed: " + e.getMessage()));
        }
    }

    /**
     * Alternative login endpoint for compatibility
     */
    @PostMapping("/authenticate")
    public ResponseEntity<BaseResponseDto<AuthResponse>> authenticateAlternative(@Valid @RequestBody AuthRequest request) {
        return authenticate(request);
    }

    /**
     * Refresh access token using refresh token
     * 
     * @param request Refresh token request
     * @return Authentication response with new token pair
     */
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponseDto<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        logger.info("Token refresh request received");
        
        try {
            AuthResponse response = authenticationService.refreshToken(request);
            
            if (response.getSuccess()) {
                logger.info("Token refresh successful");
                return ResponseEntity.ok(BaseResponseDto.success("Token refreshed successfully", response));
            } else {
                logger.warn("Token refresh failed: {}", response.getMessage());
                return ResponseEntity.badRequest()
                        .body(BaseResponseDto.badRequest(response.getMessage()));
            }
            
        } catch (Exception e) {
            logger.error("Error during token refresh: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(BaseResponseDto.serverError("Token refresh failed: " + e.getMessage()));
        }
    }

    /**
     * Alternative refresh endpoint for compatibility (matches example format)
     */
    @PostMapping("/get_token_pair")
    public ResponseEntity<BaseResponseDto<AuthResponse>> getTokenPair(@Valid @RequestBody RefreshTokenRequest request) {
        return refreshToken(request);
    }

    /**
     * Health check endpoint for authentication service
     */
    @GetMapping("/health")
    public ResponseEntity<BaseResponseDto<String>> health() {
        logger.debug("Authentication service health check");
        return ResponseEntity.ok(BaseResponseDto.success("Authentication service is running"));
    }

    /**
     * Get current authentication info (requires valid token)
     */
    @GetMapping("/me")
    public ResponseEntity<BaseResponseDto<String>> getCurrentUser() {
        logger.debug("Current user info request");
        return ResponseEntity.ok(BaseResponseDto.success("Authentication required to access this endpoint"));
    }
}