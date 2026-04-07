package org.artanddecor.utils;

import org.artanddecor.dto.WishlistDto;
import org.artanddecor.dto.WishlistRequest;
import org.artanddecor.dto.ProductDto;
import org.artanddecor.dto.UserDto;
import org.artanddecor.model.Product;
import org.artanddecor.model.User;
import org.artanddecor.model.Wishlist;

/**
 * Wishlist Mapper utility for converting between DTOs and entities
 * Handles mapping for wishlist operations with proper DTO conversion
 */
public class WishlistMapper {
    
    /**
     * Convert Wishlist entity to WishlistDto with proper DTO conversion
     * @param wishlist Wishlist entity with fetched relationships
     * @return WishlistDto
     */
    public static WishlistDto toDto(Wishlist wishlist) {
        if (wishlist == null) {
            return null;
        }
        
        // Convert User entity to UserDto if present
        UserDto userDto = null;
        if (wishlist.getUser() != null) {
            userDto = UserMapperUtil.toDetailedDto(wishlist.getUser());
        }
        
        // Convert Product entity to ProductDto
        ProductDto productDto = null;
        if (wishlist.getProduct() != null) {
            productDto = ProductMapperUtil.toProductDto(wishlist.getProduct());
        }
        
        return WishlistDto.builder()
                .wishlistId(wishlist.getWishlistId())
                .user(userDto)
                .sessionId(wishlist.getSessionId())
                .product(productDto)
                .createdDt(wishlist.getCreatedDt())
                .updatedAt(wishlist.getUpdatedAt())
                .build();
    }
    
    /**
     * Convert WishlistRequest to new Wishlist entity
     * @param request WishlistRequest
     * @param user User entity (can be null for anonymous)
     * @param product Product entity (required)
     * @return new Wishlist entity
     */
    public static Wishlist toEntity(WishlistRequest request, User user, Product product) {
        if (request == null || product == null) {
            return null;
        }
        
        return Wishlist.builder()
                .user(user)
                .sessionId(user == null ? request.getSessionId() : null) // Only set sessionId if user is null
                .product(product)
                .build();
    }
}