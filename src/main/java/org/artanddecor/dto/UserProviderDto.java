package org.artanddecor.dto;

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
    
    @Size(max = 256, message = "User provider display name must not exceed 256 characters")
    private String userProviderDisplayName;
    
    @NotBlank(message = "User provider remark is required")
    @Size(max = 256, message = "User provider remark must not exceed 256 characters")
    private String userProviderRemark;
    
    @NotNull(message = "User provider enabled flag is required")
    private Boolean userProviderEnabled;
    
    // Additional information for reporting
    private Long userCount;
    
    /**
     * Get display text for the provider
     */
    public String getDisplayText() {
        return userProviderDisplayName != null ? userProviderDisplayName : userProviderName;
    }
    
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