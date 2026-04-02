package org.artanddecor.services;

import org.artanddecor.dto.CartDto;
import org.springframework.data.domain.Page;

/**
 * Cart Service Interface
 * Defines business logic for cart management
 */
public interface CartService {

    /**
     * Get cart by ID
     * @param cartId Cart ID
     * @return CartDto
     */
    CartDto getCartById(Long cartId);

    /**
     * Get active cart by user
     * @param userId User ID
     * @return CartDto
     */
    CartDto getActiveCartByUser(Long userId);

    /**
     * Get current cart (for authenticated user or guest session)
     * @param userId User ID (optional, null for guest)
     * @param sessionId Session ID (optional, generates new if null)  
     * @return Current active cart DTO
     */
    CartDto getCurrentCart(Long userId, String sessionId);

    /**
     * Create or get active cart for user
     * @param userId User ID
     * @return Active cart DTO
     */
    CartDto createOrGetActiveCart(Long userId);

    /**
     * Create or get active cart for session
     * @param sessionId Session ID
     * @return Active cart DTO
     */
    CartDto createOrGetActiveCartForSession(String sessionId);

    /**
     * Generate unique session ID for guest users
     * @return Generated session ID
     */
    String generateSessionId();

    /**
     * Merge guest cart items to user cart
     * @param userId User ID (target cart owner)
     * @param sessionId Session ID (source guest cart)
     * @return Merged user cart DTO
     */
    CartDto mergeGuestCartToUserCart(Long userId, String sessionId);

    /**
     * Get carts by various criteria with flexible filtering
     * @param cartId Filter by cart ID (optional)
     * @param userId Filter by user ID (optional)
     * @param sessionId Filter by session ID (optional)
     * @param cartStateId Filter by cart state ID (optional)
     * @param cartSlug Filter by cart slug (optional)
     * @param cartEnabled Filter by enabled status (optional)
     * @param page Page number
     * @param size Page size
     * @param sortBy Sort field
     * @param sortDirection Sort direction
     * @return Page of carts matching criteria
     */
    Page<CartDto> getCartsByCriteria(Long cartId, Long userId, String sessionId, 
                                   Long cartStateId, String cartSlug, Boolean cartEnabled,
                                   int page, int size, String sortBy, String sortDirection);

    /**
     * Get or create cart with merge support
     * Priority: cartId -> merge scenario (userId + sessionId) -> userId -> sessionId
     * @param cartId Cart ID (highest priority)
     * @param userId User ID 
     * @param sessionId Session ID
     * @return Cart DTO with merged items if applicable
     */
    CartDto getCartWithMergeSupport(Long cartId, Long userId, String sessionId);
}