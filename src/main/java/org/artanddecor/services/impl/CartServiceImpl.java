package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.CartDto;
import org.artanddecor.model.Cart;
import org.artanddecor.repository.CartRepository;
import org.artanddecor.services.CartService;
import org.artanddecor.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cart Service Implementation
 * Handles business logic for cart management
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;

    /**
     * Get cart by ID
     * @param cartId Cart ID
     * @return CartDto
     */
    @Override
    @Transactional(readOnly = true)
    public CartDto getCartById(Long cartId) {
        logger.info("Fetching cart with ID: {}", cartId);

        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));

        return convertToDto(cart);
    }

    /**
     * Get cart by slug
     * @param cartSlug Cart slug
     * @return CartDto
     */
    @Override
    @Transactional(readOnly = true)
    public CartDto getCartBySlug(String cartSlug) {
        logger.info("Fetching cart with slug: {}", cartSlug);

        Cart cart = cartRepository.findByCartSlug(cartSlug)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found with slug: " + cartSlug));

        return convertToDto(cart);
    }

    /**
     * Get active cart by user
     * @param userId User ID
     * @return CartDto
     */
    @Override
    @Transactional(readOnly = true)
    public CartDto getActiveCartByUser(Long userId) {
        logger.info("Fetching active cart for user ID: {}", userId);

        Cart cart = cartRepository.findActiveCartByUser(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Active cart not found for user ID: " + userId));

        return convertToDto(cart);
    }

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
    @Override
    @Transactional(readOnly = true)
    public Page<CartDto> getCartsByCriteria(Long cartId, Long userId, String sessionId, 
                                          Long cartStateId, String cartSlug, Boolean cartEnabled,
                                          int page, int size, String sortBy, String sortDirection) {
        logger.info("Fetching carts by criteria - cartId: {}, userId: {}, sessionId: {}, cartStateId: {}, cartSlug: {}, cartEnabled: {}",
                   cartId, userId, sessionId, cartStateId, cartSlug, cartEnabled);

        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Cart> cartsPage = cartRepository.findCartsByCriteria(
            cartId, userId, sessionId, cartStateId, cartSlug, cartEnabled, pageable);

        return cartsPage.map(this::convertToDto);
    }

    /**
     * Convert Cart entity to DTO
     * @param cart Cart entity
     * @return CartDto
     */
    private CartDto convertToDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setCartId(cart.getCartId());
        dto.setCartSlug(cart.getCartSlug());
        dto.setSessionId(cart.getSessionId());
        dto.setCartEnabled(cart.getCartEnabled());
        
        // Set user info if available
        if (cart.getUser() != null) {
            dto.setUserId(cart.getUser().getUserId());
        }
        
        // Set cart state info if available
        if (cart.getCartState() != null) {
            dto.setCartStateId(cart.getCartState().getCartStateId());
            dto.setCartStateName(cart.getCartState().getCartStateName());
        }
        
        // Set timestamps if available
        if (cart.getCreatedDt() != null) {
            dto.setCreatedDt(cart.getCreatedDt());
        }
        if (cart.getModifiedDt() != null) {
            dto.setModifiedDt(cart.getModifiedDt());
        }
        
        return dto;
    }
}