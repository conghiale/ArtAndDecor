package org.artanddecor.services;

import org.artanddecor.dto.CartItemDto;
import org.artanddecor.dto.CartItemUpdateRequestDto;
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
     * Add item to cart
     * @param cartId Cart ID
     * @param productId Product ID
     * @param quantity Quantity
     * @return CartItemDto
     */
    CartItemDto addItemToCart(Long cartId, Long productId, Integer quantity);

    /**
     * Add product to guest cart (creates cart if needed)
     * @param sessionId Session ID (creates new if null)
     * @param productId Product ID
     * @param quantity Quantity
     * @return CartItemDto
     */
    CartItemDto addProductToGuestCart(String sessionId, Long productId, Integer quantity);

    /**
     * Update cart item quantity
     * @param cartItemId Cart item ID
     * @param quantity New quantity
     * @return Updated CartItemDto
     */
    CartItemDto updateCartItemQuantity(Long cartItemId, Integer quantity);

    /**
     * Remove cart item (set state to REMOVED)
     * @param cartItemId Cart item ID
     * @return Updated CartItemDto
     */
    CartItemDto removeCartItem(Long cartItemId);

    /**
     * Update cart item using request DTO (for admin)
     * @param cartItemId Cart item ID
     * @param request Updated cart item data from request DTO
     * @return Updated CartItemDto
     */
    CartItemDto updateCartItemByRequest(Long cartItemId, CartItemUpdateRequestDto request);

    /**
     * Clear all items from cart
     * @param cartId Cart ID
     */
    void clearCart(Long cartId);

    /**
     * Get cart total value
     * @param cartId Cart ID
     * @return Total value
     */
    BigDecimal getCartTotalValue(Long cartId);

    /**
     * Get cart total quantity
     * @param cartId Cart ID
     * @return Total quantity
     */
    Integer getCartTotalQuantity(Long cartId);

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
     * Convert CartItem entity to DTO
     * @param cartItem CartItem entity
     * @return CartItemDto
     */
    CartItemDto convertToDto(org.artanddecor.model.CartItem cartItem);

    /**
     * Get active cart items by cart ID (for CUSTOMER role)
     * @param cartId Cart ID
     * @return List of active CartItemDto
     */
    List<CartItemDto> getActiveCartItemsByCartId(Long cartId);
}