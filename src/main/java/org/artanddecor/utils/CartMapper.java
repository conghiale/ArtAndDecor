package org.artanddecor.utils;

import org.artanddecor.dto.*;
import org.artanddecor.model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper utility class for converting between Cart-related entities and DTOs
 * Provides centralized mapping logic for Cart, CartItem, CartState, and CartItemState
 * Features:
 * - Complete Product and User mapping in CartItem
 * - Circular reference prevention with lightweight Cart mapping
 * - Computed fields calculation (price changes, availability)
 * - Consistent null-safety checks
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
            dto.setUser(org.artanddecor.utils.UserMapperUtil.toBasicDto(cart.getUser()));
        }

        // Set cart state info
        if (cart.getCartState() != null) {
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

        // Set product info (complete ProductDto)
        if (cartItem.getProduct() != null) {
            dto.setUnitPrice(cartItem.getProduct().getProductPrice());
            dto.setProduct(org.artanddecor.utils.ProductMapperUtil.toProductDto(cartItem.getProduct()));
        }
        
        // Set cart info (lightweight CartDto to avoid circular reference)
        if (cartItem.getCart() != null) {
            dto.setCart(toLightweightCartDto(cartItem.getCart()));
        }

        // Set cart item state info
        if (cartItem.getCartItemState() != null) {
            dto.setCartItemState(toDto(cartItem.getCartItemState()));
        }
        
        // Set computed fields
        setComputedFields(dto, cartItem);

        return dto;
    }
    
    /**
     * Set computed fields for CartItemDto
     * @param dto CartItemDto to update
     * @param cartItem Source CartItem entity
     */
    private void setComputedFields(CartItemDto dto, CartItem cartItem) {
        if (cartItem.getProduct() != null) {
            // Check if product is available (in stock and enabled)
            dto.setIsAvailable(cartItem.getProduct().getStockQuantity() > 0 && 
                              Boolean.TRUE.equals(cartItem.getProduct().getProductEnabled()));
            
            // Check price difference between stored price and current product price
            BigDecimal currentPrice = cartItem.getProduct().getProductPrice();
            BigDecimal storedPrice = dto.getUnitPrice();
            
            if (currentPrice != null && storedPrice != null) {
                dto.setIsPriceChanged(!currentPrice.equals(storedPrice));
                dto.setPriceDifference(currentPrice.subtract(storedPrice));
            } else {
                dto.setIsPriceChanged(false);
                dto.setPriceDifference(BigDecimal.ZERO);
            }
        } else {
            // Product not found - likely deleted
            dto.setIsAvailable(false);
            dto.setIsPriceChanged(false);
            dto.setPriceDifference(BigDecimal.ZERO);
        }
    }

    /**
     * Convert Cart entity to lightweight DTO (without cartItems to avoid circular reference)
     * Used when mapping CartItem -> CartDto to prevent infinite recursion
     * @param cart Cart entity
     * @return Lightweight CartDto without cartItems
     */
    public CartDto toLightweightCartDto(Cart cart) {
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
            dto.setUser(org.artanddecor.utils.UserMapperUtil.toBasicDto(cart.getUser()));
        }

        // Set cart state info
        if (cart.getCartState() != null) {
            dto.setCartState(toDto(cart.getCartState()));
        }

        // Explicitly NOT setting cartItems to avoid circular reference
        // dto.setCartItems(null); // This is the key difference from full toDto()

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