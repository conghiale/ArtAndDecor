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
     * Get cart by slug
     * @param cartSlug Cart slug
     * @return CartDto
     */
    CartDto getCartBySlug(String cartSlug);

    /**
     * Get active cart by user
     * @param userId User ID
     * @return CartDto
     */
    CartDto getActiveCartByUser(Long userId);

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
}