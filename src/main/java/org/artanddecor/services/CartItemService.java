package org.artanddecor.services;

import org.artanddecor.dto.CartItemDto;
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
     * Get cart items by cart ID
     * @param cartId Cart ID
     * @return List of CartItemDto
     */
    List<CartItemDto> getCartItemsByCartId(Long cartId);

    /**
     * Get active cart items by user
     * @param userId User ID
     * @return List of active cart items
     */
    List<CartItemDto> getActiveCartItemsByUser(Long userId);

    /**
     * Get cart items by quantity range
     * @param minQuantity Minimum quantity
     * @param maxQuantity Maximum quantity
     * @param page Page number
     * @param size Page size
     * @return Page of cart items
     */
    Page<CartItemDto> getCartItemsByQuantityRange(Integer minQuantity, Integer maxQuantity, int page, int size);

    /**
     * Get cart items by date range
     * @param startDate Start date
     * @param endDate End date
     * @param page Page number
     * @param size Page size
     * @return Page of cart items
     */
    Page<CartItemDto> getCartItemsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size);

    /**
     * Add item to cart
     * @param cartId Cart ID
     * @param productId Product ID
     * @param quantity Quantity
     * @return CartItemDto
     */
    CartItemDto addItemToCart(Long cartId, Long productId, Integer quantity);

    /**
     * Update cart item quantity
     * @param cartItemId Cart item ID
     * @param quantity New quantity
     * @return Updated CartItemDto
     */
    CartItemDto updateCartItemQuantity(Long cartItemId, Integer quantity);

    /**
     * Remove cart item
     * @param cartItemId Cart item ID
     */
    void removeCartItem(Long cartItemId);

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
     * Get cart item statistics by category
     * @return List of statistics
     */
    List<Object[]> getCartItemStatisticsByCategory();

    /**
     * Convert CartItem entity to DTO
     * @param cartItem CartItem entity
     * @return CartItemDto
     */
    CartItemDto convertToDto(org.artanddecor.model.CartItem cartItem);

    /**
     * Get cart items by various criteria with flexible filtering
     * @param cartItemId Filter by cart item ID (optional)
     * @param cartId Filter by cart ID (optional)
     * @param productId Filter by product ID (optional)
     * @param userId Filter by user ID (optional)
     * @param minPrice Filter by minimum price (optional)
     * @param maxPrice Filter by maximum price (optional)
     * @param minQuantity Filter by minimum quantity (optional)
     * @param maxQuantity Filter by maximum quantity (optional)
     * @param cartItemStateId Filter by cart item state ID (optional)
     * @param page Page number
     * @param size Page size
     * @param sortBy Sort field
     * @param sortDirection Sort direction
     * @return Page of cart items matching criteria
     */
    Page<CartItemDto> getCartItemsByCriteria(Long cartItemId, Long cartId, Long productId, 
                                           Long userId, BigDecimal minPrice, BigDecimal maxPrice,
                                           Integer minQuantity, Integer maxQuantity, 
                                           Long cartItemStateId, int page, int size, 
                                           String sortBy, String sortDirection);

    /**
     * Get active cart items by cart ID (for CUSTOMER role)
     * @param cartId Cart ID
     * @return List of active CartItemDto
     */
    List<CartItemDto> getActiveCartItemsByCartId(Long cartId);
}