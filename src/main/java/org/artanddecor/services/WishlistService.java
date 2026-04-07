package org.artanddecor.services;

import org.artanddecor.dto.WishlistDto;
import org.artanddecor.dto.WishlistRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Wishlist Service Interface
 * Defines business operations for wishlist management
 * Supports both authenticated and anonymous users
 */
public interface WishlistService {

    /**
     * Get wishlist items by criteria with pagination
     * All parameters are optional - if null, will retrieve all items
     * @param userId Filter by user ID (for authenticated users)
     * @param sessionId Filter by session ID (for anonymous users)
     * @param productId Filter by specific product ID
     * @param pageable Pagination information
     * @return Page of WishlistDto objects
     */
    Page<WishlistDto> findWishlistByCriteria(Long userId, String sessionId, Long productId, Pageable pageable);

    /**
     * Add product to wishlist
     * Handles both authenticated and anonymous users
     * Prevents duplicate entries
     * @param request WishlistRequest with product and user/session info
     * @return created WishlistDto
     * @throws IllegalArgumentException if product not found or duplicate entry
     */
    WishlistDto addToWishlist(WishlistRequest request);

    /**
     * Remove product from wishlist by wishlist ID
     * Hard delete operation
     * @param wishlistId ID of wishlist item to remove
     * @throws RuntimeException if wishlist item not found
     */
    void removeFromWishlist(Long wishlistId);
}