package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.CartItemDto;
import org.artanddecor.dto.CartItemRequestDto;
import org.artanddecor.model.Cart;
import org.artanddecor.model.CartItem;
import org.artanddecor.model.CartItemAttribute;
import org.artanddecor.model.CartItemState;
import org.artanddecor.model.Product;
import org.artanddecor.model.ProductAttribute;
import org.artanddecor.repository.CartItemAttributeRepository;
import org.artanddecor.repository.CartItemRepository;
import org.artanddecor.repository.CartRepository;
import org.artanddecor.repository.CartItemStateRepository;
import org.artanddecor.repository.ProductAttributeRepository;
import org.artanddecor.repository.ProductRepository;
import org.artanddecor.services.CartItemService;
import org.artanddecor.services.CartService;
import org.artanddecor.utils.CartMapper;
import org.artanddecor.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Cart Item Service Implementation
 * Handles business logic for shopping cart item management optimized for workflow
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartItemServiceImpl implements CartItemService {

    private static final Logger logger = LoggerFactory.getLogger(CartItemServiceImpl.class);

    private final CartItemRepository cartItemRepository;
    private final CartItemAttributeRepository cartItemAttributeRepository;
    private final CartRepository cartRepository;
    private final CartItemStateRepository cartItemStateRepository;
    private final ProductRepository productRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final CartService cartService;
    private final CartMapper cartMapper;

    /**
     * Get cart item by ID
     * @param cartItemId Cart item ID
     * @return CartItemDto
     */
    @Override
    @Transactional(readOnly = true)
    public CartItemDto getCartItemById(Long cartItemId) {
        logger.info("Fetching cart item with ID: {}", cartItemId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartItemId));

        return cartMapper.toDto(cartItem);
    }

    /**
     * Get cart items by cart ID with state filter
     * @param cartId Cart ID
     * @param cartItemStateId Cart item state ID filter (optional)
     * @return List of CartItemDto
     */
    @Override
    @Transactional(readOnly = true)
    public List<CartItemDto> getCartItemsByCartId(Long cartId, Long cartItemStateId) {
        logger.info("Fetching cart items for cart ID: {}, state filter: {}", cartId, cartItemStateId);

        List<CartItem> cartItems;
        if (cartItemStateId != null) {
            cartItems = cartItemRepository.findByCartIdAndCartItemStateId(cartId, cartItemStateId);
        } else {
            cartItems = cartItemRepository.findByCart_CartId(cartId);
        }
        
        return cartMapper.toCartItemDto(cartItems);
    }

    /**
     * Get active cart items by user
     * @param userId User ID
     * @return List of active CartItemDto
     */
    @Override
    @Transactional(readOnly = true)
    public List<CartItemDto> getActiveCartItemsByUser(Long userId) {
        logger.info("Fetching active cart items for user ID: {}", userId);

        List<CartItem> cartItems = cartItemRepository.findActiveCartItemsByUser(userId);
        return cartMapper.toCartItemDto(cartItems);
    }

    /**
     * Get active cart items by cart ID (for CUSTOMER role)
     * @param cartId Cart ID
     * @return List of active CartItemDto
     */
    @Override
    @Transactional(readOnly = true)
    public List<CartItemDto> getActiveCartItemsByCartId(Long cartId) {
        logger.info("Fetching active cart items for cart ID: {}", cartId);
        
        List<CartItem> cartItems = cartItemRepository.findActiveCartItemsByCartId(cartId);
        return cartMapper.toCartItemDto(cartItems);
    }

    /**
     * Get cart items count with filters - priority lookup by cartId, userId, or sessionId
     * @param cartId Cart ID (highest priority)
     * @param userId User ID (medium priority, optional)
     * @param sessionId Session ID (lowest priority, optional) 
     * @param cartItemStateId Cart item state ID (optional)
     * @return Cart items count
     */
    @Override
    @Transactional(readOnly = true)
    public Long getCartItemsCount(Long cartId, Long userId, String sessionId, Long cartItemStateId) {
        logger.info("Getting cart items count - cartId: {}, userId: {}, sessionId: {}, cartItemStateId: {}", 
                   cartId, userId, sessionId, cartItemStateId);

        try {
            // Priority logic: cartId -> userId -> sessionId
            Long targetCartId = null;
            
            if (cartId != null) {
                targetCartId = cartId;
            } else if (userId != null) {
                // Get active cart for user
                Optional<Cart> activeCart = cartRepository.findActiveCartByUser(userId);
                if (activeCart.isPresent()) {
                    targetCartId = activeCart.get().getCartId();
                }
            } else if (sessionId != null) {
                // Get active cart for session
                Optional<Cart> sessionCart = cartRepository.findActiveCartBySession(sessionId);
                if (sessionCart.isPresent()) {
                    targetCartId = sessionCart.get().getCartId();
                }
            }

            if (targetCartId == null) {
                return 0L;
            }

            // Count items with optional state filter
            return cartItemRepository.countCartItems(targetCartId, cartItemStateId);
            
        } catch (Exception e) {
            logger.error("Error getting cart items count: {}", e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * Add product to cart with optional attributes
     * Unified method supporting all add scenarios
     * @param request Cart item request with product and cart identification
     * @return CartItemDto
     */
    @Override
    public CartItemDto addProductToCart(CartItemRequestDto request) {
        logger.info("Adding product to cart - request: {}", request);

        // Validate request
        if (!request.isValidForAdd()) {
            throw new IllegalArgumentException("Invalid add to cart request");
        }

        // Determine target cart ID based on priority: cartId > userId > sessionId
        Long targetCartId = resolveCartId(request);
        
        Cart cart = cartRepository.findById(targetCartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + targetCartId));

        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId()));

        // Get active and removed states
        CartItemState activeState = cartItemStateRepository.findActiveCartItemState()
            .orElseThrow(() -> new ResourceNotFoundException("Active cart item state not found"));
        
        Optional<CartItemState> removedState = cartItemStateRepository.findByCartItemStateName("REMOVED");

        // Check if item already exists in cart (any state)
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(targetCartId, request.getProductId());

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            
            // Check current state
            if (removedState.isPresent() && 
                cartItem.getCartItemState().getCartItemStateId().equals(removedState.get().getCartItemStateId())) {
                // Item was removed, now reactivate with new quantity
                cartItem.setCartItemQuantity(request.getQuantity());
                cartItem.setCartItemState(activeState);
                logger.info("Reactivated removed cart item with quantity {}", request.getQuantity());
            } else {
                // Item is active, increase quantity
                cartItem.setCartItemQuantity(cartItem.getCartItemQuantity() + request.getQuantity());
                logger.info("Updated existing cart item quantity to: {}", cartItem.getCartItemQuantity());
            }
            cartItem.calculateTotalPrice();
        } else {
            // Create new cart item
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setCartItemQuantity(request.getQuantity());
            cartItem.setCartItemState(activeState);
            cartItem.calculateTotalPrice();
            logger.info("Created new cart item");
        }

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        
        // Add attributes if provided
        if (request.hasSelectedAttributes()) {
            addAttributesToCartItem(savedCartItem.getCartItemId(), request.getSelectedAttributeIds());
            logger.info("Added {} attributes to cart item", request.getSelectedAttributesCount());
        }
        
        // Update cart total quantity
        updateCartTotalQuantity(targetCartId);

        return cartMapper.toDto(savedCartItem);
    }

    /**
     * Update cart item
     * @param cartItemId Cart item ID
     * @param request Updated cart item data
     * @return Updated CartItemDto
     */
    @Override
    public CartItemDto updateCartItem(Long cartItemId, CartItemRequestDto request) {
        logger.info("Updating cart item ID: {} with request: {}", cartItemId, request);

        CartItem existingCartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartItemId));

        // Handle quantity updates
        if (request.getQuantity() != null) {
            if (request.getQuantity() == 0) {
                // Quantity is 0, set state to REMOVED
                logger.info("Quantity is 0, setting cart item state to REMOVED");
                CartItemState removedState = cartItemStateRepository.findByCartItemStateName("REMOVED")
                    .orElseThrow(() -> new ResourceNotFoundException("REMOVED cart item state not found"));
                existingCartItem.setCartItemState(removedState);
                existingCartItem.setCartItemQuantity(0);
            } else if (request.getQuantity() > 0) {
                existingCartItem.setCartItemQuantity(request.getQuantity());
                // If setting quantity > 0, ensure state is ACTIVE if currently REMOVED
                if (existingCartItem.getCartItemState().getCartItemStateName().equals("REMOVED")) {
                    CartItemState activeState = cartItemStateRepository.findActiveCartItemState()
                        .orElseThrow(() -> new ResourceNotFoundException("ACTIVE cart item state not found"));
                    existingCartItem.setCartItemState(activeState);
                }
            } else {
                throw new IllegalArgumentException("Quantity cannot be negative");
            }
        }

        // Handle attribute updates if provided
        if (request.hasSelectedAttributes()) {
            addAttributesToCartItem(cartItemId, request.getSelectedAttributeIds());
            logger.info("Updated {} attributes for cart item", request.getSelectedAttributesCount());
        }

        // Recalculate total price if quantity changed and > 0
        if (request.getQuantity() != null && request.getQuantity() > 0) {
            existingCartItem.calculateTotalPrice();
        }

        CartItem updatedCartItem = cartItemRepository.save(existingCartItem);
        
        // Update cart total quantity
        updateCartTotalQuantity(existingCartItem.getCart().getCartId());

        logger.info("Cart item updated successfully");
        return cartMapper.toDto(updatedCartItem);
    }

    /**
     * Remove cart item (set state to REMOVED)
     * @param cartItemId Cart item ID
     * @return Updated CartItemDto
     */
    @Override
    public CartItemDto removeCartItem(Long cartItemId) {
        logger.info("Removing cart item with ID: {}", cartItemId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartItemId));

        // Get REMOVED state
        CartItemState removedState = cartItemStateRepository.findByCartItemStateName("REMOVED")
            .orElseThrow(() -> new ResourceNotFoundException("REMOVED cart item state not found"));

        cartItem.setCartItemState(removedState);
        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        
        // Update cart total quantity
        updateCartTotalQuantity(cartItem.getCart().getCartId());

        logger.info("Cart item removed successfully (state changed to REMOVED)");
        return cartMapper.toDto(updatedCartItem);
    }

    /**
     * Clear all items from cart
     * @param cartId Cart ID
     */
    @Override
    public void clearCart(Long cartId) {
        logger.info("Clearing all items from cart ID: {}", cartId);

        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));

        cartItemRepository.deleteByCartId(cartId);
        
        // Reset cart total quantity
        cart.setTotalQuantity(0);
        cartRepository.save(cart);

        logger.info("Cart cleared successfully");
    }

    /**
     * Resolve cart ID from request based on priority: cartId > userId > sessionId
     * @param request CartItemRequestDto
     * @return Cart ID
     */
    private Long resolveCartId(CartItemRequestDto request) {
        if (request.getCartId() != null) {
            logger.debug("Using provided cart ID: {}", request.getCartId());
            return request.getCartId();
        }
        
        if (request.getUserId() != null) {
            logger.debug("Resolving cart for user ID: {}", request.getUserId());
            var cartDto = cartService.createOrGetActiveCart(request.getUserId());
            return cartDto.getCartId();
        }
        
        if (request.getSessionId() != null) {
            logger.debug("Resolving cart for session ID: {}", request.getSessionId());
            var cartDto = cartService.createOrGetActiveCartForSession(request.getSessionId());
            return cartDto.getCartId();
        }
        
        throw new IllegalArgumentException("No cart identification provided (cartId, userId, or sessionId required)");
    }

    /**
     * Add attributes to an existing cart item
     * @param cartItemId Cart item ID
     * @param attributeIds List of product attribute IDs
     */
    private void addAttributesToCartItem(Long cartItemId, List<Long> attributeIds) {
        logger.debug("Adding attributes to cart item ID: {}, attributes: {}", cartItemId, attributeIds);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartItemId));

        // Clear existing attributes for this cart item
        cartItemAttributeRepository.deleteByCartItemId(cartItemId);
        logger.debug("Cleared existing attributes for cart item");

        // Add new attributes
        for (Long attributeId : attributeIds) {
            ProductAttribute productAttribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new ResourceNotFoundException("Product attribute not found with ID: " + attributeId));

            // Verify this attribute belongs to the same product
            if (!productAttribute.getProduct().getProductId().equals(cartItem.getProduct().getProductId())) {
                throw new IllegalArgumentException("Product attribute ID " + attributeId + 
                    " does not belong to product ID " + cartItem.getProduct().getProductId());
            }

            // Create cart item attribute
            CartItemAttribute cartItemAttribute = new CartItemAttribute();
            cartItemAttribute.setCartItem(cartItem);
            cartItemAttribute.setProductAttribute(productAttribute);
            
            cartItemAttributeRepository.save(cartItemAttribute);
            logger.debug("Added attribute ID {} to cart item", attributeId);
        }
    }

    /**
     * Update cart total quantity
     * @param cartId Cart ID
     */
    private void updateCartTotalQuantity(Long cartId) {
        Integer totalQuantity = cartItemRepository.getActiveCartItemsTotalQuantity(cartId);
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart != null) {
            cart.setTotalQuantity(totalQuantity != null ? totalQuantity : 0);
            cartRepository.save(cart);
        }
    }
}