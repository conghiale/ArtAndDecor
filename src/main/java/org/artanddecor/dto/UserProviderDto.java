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
    
    @NotBlank(message = "Tên nhà cung cấp là bắt buộc")
    @Size(max = 50, message = "Tên nhà cung cấp không được vượt quá 50 ký tự")
    private String userProviderName;
    
    @Size(max = 256, message = "Tên hiển thị nhà cung cấp không được vượt quá 256 ký tự")
    private String userProviderDisplayName;
    
    @NotBlank(message = "Ghi chú nhà cung cấp là bắt buộc")
    @Size(max = 256, message = "Ghi chú nhà cung cấp không được vượt quá 256 ký tự")
    private String userProviderRemark;
    
    @NotNull(message = "Cờ kích hoạt nhà cung cấp là bắt buộc")
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