package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token pair containing access token and refresh token
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenPair {
    
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long expiresIn; // seconds until access token expires
}