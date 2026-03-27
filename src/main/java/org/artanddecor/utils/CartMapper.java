package org.artanddecor.utils;

import org.artanddecor.dto.*;
import org.artanddecor.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper utility class for converting between Cart-related entities and DTOs
 * Provides centralized mapping logic for Cart, CartItem, CartState, and CartItemState
 */
@Component
public class CartMapper {

    /**
     * Convert Cart entity to DTO
     * @param cart Cart entity
     * @return CartDto
     */
    public CartDto toDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartDto dto = new CartDto();
        dto.setCartId(cart.getCartId());
        dto.setSessionId(cart.getSessionId());
        dto.setCartSlug(cart.getCartSlug());
        dto.setTotalQuantity(cart.getTotalQuantity());
        dto.setCartEnabled(cart.getCartEnabled());
        dto.setCreatedDt(cart.getCreatedDt());
        dto.setModifiedDt(cart.getModifiedDt());

        // Set user info
        if (cart.getUser() != null) {
            dto.setUserId(cart.getUser().getUserId());
            // Set nested UserDto if needed (implement UserMapper)
        }

        // Set cart state info
        if (cart.getCartState() != null) {
            dto.setCartStateId(cart.getCartState().getCartStateId());
            dto.setCartStateName(cart.getCartState().getCartStateName());
            dto.setCartState(toDto(cart.getCartState()));
        }

        // Set cart items
        if (cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
            List<CartItemDto> cartItemDtos = cart.getCartItems().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
            dto.setCartItems(cartItemDtos);
        }

        // Calculate totals
        dto.calculateTotals();

        return dto;
    }

    /**
     * Convert CartItem entity to DTO
     * @param cartItem CartItem entity
     * @return CartItemDto
     */
    public CartItemDto toDto(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        CartItemDto dto = new CartItemDto();
        dto.setCartItemId(cartItem.getCartItemId());
        dto.setQuantity(cartItem.getCartItemQuantity());
        dto.setTotalPrice(cartItem.getCartItemTotalPrice());
        dto.setCreatedDt(cartItem.getCreatedDt());
        dto.setModifiedDt(cartItem.getModifiedDt());

        // Set unit price from product
        if (cartItem.getProduct() != null) {
            dto.setUnitPrice(cartItem.getProduct().getProductPrice());
            // Set nested ProductDto if needed (implement ProductMapper)
        }

        // Set cart item state info
        if (cartItem.getCartItemState() != null) {
            dto.setCartItemState(toDto(cartItem.getCartItemState()));
        }

        return dto;
    }

    /**
     * Convert CartState entity to DTO
     * @param cartState CartState entity
     * @return CartStateDto
     */
    public CartStateDto toDto(CartState cartState) {
        if (cartState == null) {
            return null;
        }

        CartStateDto dto = new CartStateDto();
        dto.setCartStateId(cartState.getCartStateId());
        dto.setCartStateName(cartState.getCartStateName());
        dto.setCartStateDisplayName(cartState.getCartStateDisplayName());
        dto.setCartStateRemark(cartState.getCartStateRemark());
        dto.setCartStateEnabled(cartState.getCartStateEnabled());

        return dto;
    }

    /**
     * Convert CartItemState entity to DTO
     * @param cartItemState CartItemState entity
     * @return CartItemStateDto
     */
    public CartItemStateDto toDto(CartItemState cartItemState) {
        if (cartItemState == null) {
            return null;
        }

        CartItemStateDto dto = new CartItemStateDto();
        dto.setCartItemStateId(cartItemState.getCartItemStateId());
        dto.setCartItemStateName(cartItemState.getCartItemStateName());
        dto.setCartItemStateDisplayName(cartItemState.getCartItemStateDisplayName());
        dto.setCartItemStateRemark(cartItemState.getCartItemStateRemark());
        dto.setCartItemStateEnabled(cartItemState.getCartItemStateEnabled());

        return dto;
    }

    /**
     * Convert list of Cart entities to DTOs
     * @param carts List of Cart entities
     * @return List of CartDtos
     */
    public List<CartDto> toDto(List<Cart> carts) {
        if (carts == null || carts.isEmpty()) {
            return List.of();
        }
        return carts.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Convert list of CartItem entities to DTOs
     * @param cartItems List of CartItem entities
     * @return List of CartItemDtos
     */
    public List<CartItemDto> toCartItemDto(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return List.of();
        }
        return cartItems.stream().map(this::toDto).collect(Collectors.toList());
    }
}