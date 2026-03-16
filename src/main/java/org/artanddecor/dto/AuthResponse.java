package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Authentication response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String message;
    private Boolean success;
    private TokenPair tokenPair;
    private UserDto user;
    private LocalDateTime timestamp;
    
    public static AuthResponse success(String message, TokenPair tokenPair, UserDto user) {
        return AuthResponse.builder()
                .message(message)
                .success(true)
                .tokenPair(tokenPair)
                .user(user)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static AuthResponse success(String message, TokenPair tokenPair) {
        return AuthResponse.builder()
                .message(message)
                .success(true)
                .tokenPair(tokenPair)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static AuthResponse error(String message) {
        return AuthResponse.builder()
                .message(message)
                .success(false)
                .timestamp(LocalDateTime.now())
                .build();
    }
}