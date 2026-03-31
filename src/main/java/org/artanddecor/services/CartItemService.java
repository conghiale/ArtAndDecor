package org.artanddecor.services;

import org.artanddecor.dto.CartItemDto;
import org.artanddecor.dto.CartItemRequestDto;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Cart Item Service Interface
 * Defines business logic for cart item management
 */
public interface CartItemService {

    /**
     * Get cart item by ID
     * @param cartItemId Cart item ID
     * @return CartItemDto
     */
    CartItemDto getCartItemById(Long cartItemId);

    /**
     * Get active cart items by user
     * @param userId User ID
     * @return List of active cart items
     */
    List<CartItemDto> getActiveCartItemsByUser(Long userId);

    /**
     * Add product to cart with optional attributes
     * Unified method supporting all add scenarios:
     * - Direct cart ID
     * - User ID (finds/creates active cart)
     * - Session ID (finds/creates guest cart)
     * - With or without product attributes
     * @param request Cart item request with product and cart identification
     * @return CartItemDto
     */
    CartItemDto addProductToCart(CartItemRequestDto request);

    /**
     * Update cart item
     * @param cartItemId Cart item ID
     * @param request Updated cart item data
     * @return Updated CartItemDto
     */
    CartItemDto updateCartItem(Long cartItemId, CartItemRequestDto request);

    /**
     * Remove cart item (set state to REMOVED)
     * @param cartItemId Cart item ID
     * @return Updated CartItemDto
     */
    CartItemDto removeCartItem(Long cartItemId);

    /**
     * Clear all items from cart
     * @param cartId Cart ID
     */
    void clearCart(Long cartId);

    /**
     * Get cart items count with filters - priority lookup by cartId, userId, or sessionId
     * @param cartId Cart ID (highest priority)
     * @param userId User ID (medium priority, optional)
     * @param sessionId Session ID (lowest priority, optional) 
     * @param cartItemStateId Cart item state ID (optional)
     * @return Cart items count
     */
    Long getCartItemsCount(Long cartId, Long userId, String sessionId, Long cartItemStateId);

    /**
     * Get cart items by cart ID with state filter
     * @param cartId Cart ID
     * @param cartItemStateId Cart item state ID filter (optional)
     * @return List of CartItemDto
     */
    List<CartItemDto> getCartItemsByCartId(Long cartId, Long cartItemStateId);

    /**
     * Get active cart items by cart ID (for CUSTOMER role)
     * @param cartId Cart ID
     * @return List of active CartItemDto
     */
    List<CartItemDto> getActiveCartItemsByCartId(Long cartId);
}