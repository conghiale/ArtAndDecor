package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Wishlist DTO for API responses
 * Contains wishlist item information with user and product DTOs to avoid circular references
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistDto {
    
    private Long wishlistId;
    private UserDto user;
    private String sessionId;
    private ProductDto product;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}