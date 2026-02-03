package org.ArtAndDecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * UserProvider DTO for API requests and responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProviderDto {
    
    private Long userProviderId;
    
    @NotBlank(message = "User provider name is required")
    @Size(max = 50, message = "User provider name must not exceed 50 characters")
    private String userProviderName;
    
    private String userProviderRemark;
    private String userProviderRemarkEn;
    
    @NotNull(message = "User provider enabled flag is required")
    private Boolean userProviderEnabled;
    
    // Additional information for reporting
    private Long userCount;
    
    /**
     * Check if this is a local provider
     */
    public boolean isLocalProvider() {
        return "LOCAL".equalsIgnoreCase(userProviderName) || 
               "SYSTEM".equalsIgnoreCase(userProviderName);
    }
    
    /**
     * Check if this is a Google provider
     */
    public boolean isGoogleProvider() {
        return "GOOGLE".equalsIgnoreCase(userProviderName);
    }
    
    /**
     * Check if this is a Facebook provider
     */
    public boolean isFacebookProvider() {
        return "FACEBOOK".equalsIgnoreCase(userProviderName);
    }
    
    /**
     * Check if this is a social provider
     */
    public boolean isSocialProvider() {
        return isGoogleProvider() || isFacebookProvider() || 
               "GITHUB".equalsIgnoreCase(userProviderName) ||
               "TWITTER".equalsIgnoreCase(userProviderName);
    }
}