package org.ArtAndDecor.utils;

import org.ArtAndDecor.dto.UserDto;
import org.ArtAndDecor.dto.UserProviderDto;
import org.ArtAndDecor.dto.UserRoleDto;
import org.ArtAndDecor.model.User;
import org.ArtAndDecor.model.UserProvider;
import org.ArtAndDecor.model.UserRole;
import org.springframework.stereotype.Component;

/**
 * Utility class for mapping User entities to UserDto objects
 * Supports clean architecture pattern with nested DTOs
 */
@Component
public class UserMapperUtil {
    
    // =============================================
    // USER MAPPING METHODS
    // =============================================
    
    /**
     * Convert User entity to UserDto with foreign keys only (for basic operations)
     * @param user User entity
     * @return UserDto with only basic fields and foreign key IDs
     */
    public static UserDto toBasicDto(User user) {
        if (user == null) return null;
        
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUserEnabled(user.getUserEnabled());
        dto.setUserName(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setEmail(user.getEmail());
        dto.setImageAvatarName(user.getImageAvatarName());
        dto.setSocialMedia(user.getSocialMedia());
        dto.setLastLoginDt(user.getLastLoginDt());
        dto.setCreatedDt(user.getCreatedDt());
        dto.setModifiedDt(user.getModifiedDt());
        
        return dto;
    }
    
    /**
     * Convert User entity to UserDto with related entity IDs and nested DTOs
     * @param user User entity
     * @return UserDto with foreign keys and nested DTO objects
     */
    public static UserDto toDetailedDto(User user) {
        if (user == null) return null;
        
        UserDto dto = toBasicDto(user);
        
        // Set nested DTO objects
        dto.setUserProvider(toProviderDto(user.getUserProvider()));
        dto.setUserRole(toRoleDto(user.getUserRole()));
        
        // Set computed fields
        dto.setFullName(dto.getFullNameValue());
        
        return dto;
    }
    
    // =============================================
    // USER PROVIDER MAPPING METHODS
    // =============================================
    
    /**
     * Convert UserProvider entity to UserProviderDto (auxiliary class - only table fields)
     */
    public static UserProviderDto toProviderDto(UserProvider provider) {
        if (provider == null) return null;
        
        UserProviderDto dto = new UserProviderDto();
        dto.setUserProviderId(provider.getUserProviderId());
        dto.setUserProviderName(provider.getUserProviderName());
        dto.setUserProviderRemark(provider.getUserProviderRemark());
        dto.setUserProviderEnabled(provider.getUserProviderEnabled());
        
        return dto;
    }
    
    // =============================================
    // USER ROLE MAPPING METHODS
    // =============================================
    
    /**
     * Convert UserRole entity to UserRoleDto (auxiliary class - only table fields)
     */
    public static UserRoleDto toRoleDto(UserRole role) {
        if (role == null) return null;
        
        UserRoleDto dto = new UserRoleDto();
        dto.setUserRoleId(role.getUserRoleId());
        dto.setUserRoleName(role.getUserRoleName());
        dto.setUserRoleRemark(role.getUserRoleRemark());
        dto.setUserRoleEnabled(role.getUserRoleEnabled());
        
        return dto;
    }
    
    // =============================================
    // VALIDATION HELPER METHODS
    // =============================================
    
    /**
     * Check if User entity has complete related data
     */
    public static boolean hasCompleteData(User user) {
        return user != null && 
               user.getUserProvider() != null && 
               user.getUserRole() != null;
    }
    
    /**
     * Check if User entity has admin role
     */
    public static boolean hasAdminRole(User user) {
        return user != null && 
               user.getUserRole() != null && 
               "ADMIN".equalsIgnoreCase(user.getUserRole().getUserRoleName());
    }
    
    // =============================================
    // HELPER METHODS TO GET FOREIGN KEY IDs
    // =============================================
    
    /**
     * Get userProviderId from UserDto nested object
     */
    public static Long getUserProviderId(UserDto userDto) {
        return userDto != null && userDto.getUserProvider() != null ? 
               userDto.getUserProvider().getUserProviderId() : null;
    }
    
    /**
     * Get userRoleId from UserDto nested object
     */
    public static Long getUserRoleId(UserDto userDto) {
        return userDto != null && userDto.getUserRole() != null ? 
               userDto.getUserRole().getUserRoleId() : null;
    }
}