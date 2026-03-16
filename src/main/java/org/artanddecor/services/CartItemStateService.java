package org.artanddecor.services;

import org.artanddecor.dto.CartItemStateDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Cart Item State Service Interface
 * Defines business logic for cart item state management
 */
public interface CartItemStateService {

    /**
     * Get cart item state by ID
     * @param cartItemStateId Cart item state ID
     * @return CartItemStateDto
     */
    CartItemStateDto getCartItemStateById(Long cartItemStateId);

    /**
     * Get cart item state by name
     * @param cartItemStateName Cart item state name
     * @return CartItemStateDto
     */
    CartItemStateDto getCartItemStateByName(String cartItemStateName);

    /**
     * Search cart item states by keyword
     * @param keyword Search keyword
     * @param page Page number
     * @param size Page size
     * @return Page of CartItemStateDto
     */
    Page<CartItemStateDto> searchCartItemStates(String keyword, int page, int size);

    /**
     * Get cart item states with cart item count
     * @param page Page number
     * @param size Page size
     * @return Page of CartItemStateDto with cart item count
     */
    Page<CartItemStateDto> getCartItemStatesWithCartItemCount(int page, int size);

    /**
     * Create new cart item state
     * @param cartItemStateDto Cart item state data
     * @return Created CartItemStateDto
     */
    CartItemStateDto createCartItemState(CartItemStateDto cartItemStateDto);

    /**
     * Update cart item state
     * @param cartItemStateId Cart item state ID
     * @param cartItemStateDto Updated cart item state data
     * @return Updated CartItemStateDto
     */
    CartItemStateDto updateCartItemState(Long cartItemStateId, CartItemStateDto cartItemStateDto);

    /**
     * Delete cart item state by ID
     * @param cartItemStateId Cart item state ID
     */
    void deleteCartItemState(Long cartItemStateId);

    /**
     * Enable/Disable cart item state
     * @param cartItemStateId Cart item state ID
     * @param enabled Enabled status
     * @return Updated CartItemStateDto
     */
    CartItemStateDto toggleCartItemStateEnabled(Long cartItemStateId, boolean enabled);

    /**
     * Get active cart item state
     * @return Active CartItemStateDto
     */
    CartItemStateDto getActiveCartItemState();

    /**
     * Get ordered cart item state
     * @return Ordered CartItemStateDto
     */
    CartItemStateDto getOrderedCartItemState();

    /**
     * Get cart item statistics
     * @return List of cart item statistics
     */
    List<Object[]> getCartItemStatistics();

    /**
     * Convert CartItemState entity to DTO
     * @param cartItemState CartItemState entity
     * @return CartItemStateDto
     */
    CartItemStateDto convertToDto(org.artanddecor.model.CartItemState cartItemState);

    /**
     * Get cart item states by various criteria with flexible filtering
     * @param cartItemStateId Filter by cart item state ID (optional)
     * @param cartItemStateName Filter by cart item state name (optional)
     * @param cartItemStateEnabled Filter by enabled status (optional)
     * @param textSearch Text search in name, display name, and remark (optional)
     * @return List of CartItemStateDto matching criteria (no pagination)
     */
    List<CartItemStateDto> getCartItemStatesByCriteria(Long cartItemStateId, String cartItemStateName, 
                                                      Boolean cartItemStateEnabled, String textSearch);
}