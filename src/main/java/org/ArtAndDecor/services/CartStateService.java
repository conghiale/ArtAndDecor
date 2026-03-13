package org.ArtAndDecor.services;

import org.ArtAndDecor.dto.CartStateDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Cart State Service Interface
 * Defines business logic for cart state management
 */
public interface CartStateService {

    /**
     * Get cart state by ID
     * @param cartStateId Cart state ID
     * @return CartStateDto
     */
    CartStateDto getCartStateById(Long cartStateId);

    /**
     * Get cart state by name
     * @param cartStateName Cart state name
     * @return CartStateDto
     */
    CartStateDto getCartStateByName(String cartStateName);

    /**
     * Search cart states by keyword
     * @param keyword Search keyword
     * @param page Page number
     * @param size Page size
     * @return Page of CartStateDto
     */
    Page<CartStateDto> searchCartStates(String keyword, int page, int size);

    /**
     * Get cart states with cart count
     * @param page Page number
     * @param size Page size
     * @return Page of CartStateDto with cart count
     */
    Page<CartStateDto> getCartStatesWithCartCount(int page, int size);

    /**
     * Create new cart state
     * @param cartStateDto Cart state data
     * @return Created CartStateDto
     */
    CartStateDto createCartState(CartStateDto cartStateDto);

    /**
     * Update cart state
     * @param cartStateId Cart state ID
     * @param cartStateDto Updated cart state data
     * @return Updated CartStateDto
     */
    CartStateDto updateCartState(Long cartStateId, CartStateDto cartStateDto);

    /**
     * Delete cart state by ID
     * @param cartStateId Cart state ID
     */
    void deleteCartState(Long cartStateId);

    /**
     * Enable/Disable cart state
     * @param cartStateId Cart state ID
     * @param enabled Enabled status
     * @return Updated CartStateDto
     */
    CartStateDto toggleCartStateEnabled(Long cartStateId, boolean enabled);

    /**
     * Get active cart state
     * @return Active CartStateDto
     */
    CartStateDto getActiveCartState();

    /**
     * Get cart states by various criteria with flexible filtering
     * @param cartStateId Filter by cart state ID (optional)
     * @param cartStateName Filter by cart state name (optional)
     * @param cartStateEnabled Filter by enabled status (optional)
     * @param textSearch Text search in name, display name, and remark (optional)
     * @return List of CartStateDto matching criteria (no pagination)
     */
    List<CartStateDto> getCartStatesByCriteria(Long cartStateId, String cartStateName, 
                                             Boolean cartStateEnabled, String textSearch);

    /**
     * Convert CartState entity to DTO
     * @param cartState CartState entity
     * @return CartStateDto
     */
    CartStateDto convertToDto(org.ArtAndDecor.model.CartState cartState);
}