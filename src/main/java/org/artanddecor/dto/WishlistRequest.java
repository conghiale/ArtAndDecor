package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Wishlist Request DTO for adding items to wishlist
 * Supports both authenticated and anonymous users
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistRequest {
    
    @NotNull(message = "ID sản phẩm là bắt buộc")
    private Long productId;
    
    // For authenticated users - will be extracted from JWT token
    private Long userId;
    
    // For anonymous users - session ID from frontend
    @Size(max = 128, message = "ID phiên không được vượt quá 128 ký tự")
    private String sessionId;
}