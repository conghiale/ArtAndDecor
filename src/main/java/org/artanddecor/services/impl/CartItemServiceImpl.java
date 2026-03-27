package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.CartItemDto;
import org.artanddecor.dto.CartItemUpdateRequestDto;
import org.artanddecor.model.Cart;
import org.artanddecor.model.CartItem;
import org.artanddecor.model.CartItemState;
import org.artanddecor.model.Product;
import org.artanddecor.repository.CartItemRepository;
import org.artanddecor.repository.CartRepository;
import org.artanddecor.repository.CartItemStateRepository;
import org.artanddecor.repository.ProductRepository;
import org.artanddecor.services.CartItemService;
import org.artanddecor.services.CartService;
import org.artanddecor.utils.CartMapper;
import org.artanddecor.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final CartRepository cartRepository;
    private final CartItemStateRepository cartItemStateRepository;
    private final ProductRepository productRepository;
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
     * Add item to cart
     * @param cartId Cart ID
     * @param productId Product ID
     * @param quantity Item quantity
     * @return Created or updated CartItemDto
     */
    @Override
    public CartItemDto addItemToCart(Long cartId, Long productId, Integer quantity) {
        logger.info("Adding item to cart - cartId: {}, productId: {}, quantity: {}", cartId, productId, quantity);

        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        // Get active and removed states
        CartItemState activeState = cartItemStateRepository.findActiveCartItemState()
            .orElseThrow(() -> new ResourceNotFoundException("Active cart item state not found"));
        
        Optional<CartItemState> removedState = cartItemStateRepository.findByCartItemStateName("REMOVED");

        // Check if item already exists in cart (any state)
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId);

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            
            // Check current state
            if (removedState.isPresent() && 
                cartItem.getCartItemState().getCartItemStateId().equals(removedState.get().getCartItemStateId())) {
                // Item was removed, now reactivate with quantity 1
                cartItem.setCartItemQuantity(1);
                cartItem.setCartItemState(activeState);
                logger.info("Reactivated removed cart item with quantity 1");
            } else {
                // Item is active, increase quantity
                cartItem.setCartItemQuantity(cartItem.getCartItemQuantity() + quantity);
                logger.info("Updated existing cart item quantity to: {}", cartItem.getCartItemQuantity());
            }
            cartItem.calculateTotalPrice();
        } else {
            // Create new cart item
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setCartItemQuantity(quantity);
            cartItem.setCartItemState(activeState);
            cartItem.calculateTotalPrice();
            logger.info("Created new cart item");
        }

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        
        // Update cart total quantity
        updateCartTotalQuantity(cartId);

        return cartMapper.toDto(savedCartItem);
    }

    /**
     * Add product to guest cart (creates cart if needed)
     * @param sessionId Session ID (creates new if null)
     * @param productId Product ID
     * @param quantity Quantity
     * @return CartItemDto
     */
    @Override
    public CartItemDto addProductToGuestCart(String sessionId, Long productId, Integer quantity) {
        logger.info("Adding product to guest cart - sessionId: {}, productId: {}, quantity: {}", 
                   sessionId, productId, quantity);

        // Get or create cart for session
        var cartDto = cartService.createOrGetActiveCartForSession(sessionId);
        
        // Add item to cart
        return addItemToCart(cartDto.getCartId(), productId, quantity);
    }

    /**
     * Update cart item quantity
     * @param cartItemId Cart item ID
     * @param quantity New quantity
     * @return Updated CartItemDto
     */
    @Override
    public CartItemDto updateCartItemQuantity(Long cartItemId, Integer quantity) {
        logger.info("Updating cart item quantity - cartItemId: {}, quantity: {}", cartItemId, quantity);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartItemId));

        if (quantity <= 0) {
            // Quantity is 0 or negative, set state to REMOVED
            return removeCartItem(cartItemId);
        }

        cartItem.setCartItemQuantity(quantity);
        cartItem.calculateTotalPrice();

        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        
        // Update cart total quantity
        updateCartTotalQuantity(cartItem.getCart().getCartId());

        logger.info("Cart item quantity updated successfully");
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
     * Update cart item using request DTO (for admin)
     * @param cartItemId Cart item ID
     * @param request Updated cart item data from request DTO
     * @return Updated CartItemDto
     */
    @Override
    public CartItemDto updateCartItemByRequest(Long cartItemId, CartItemUpdateRequestDto request) {
        logger.info("Admin updating cart item with request DTO: {}", cartItemId);

        CartItem existingCartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartItemId));

        // Special logic: quantity = 0 should automatically set state to REMOVED (ID=3)
        if (request.getQuantity() != null) {
            if (request.getQuantity() == 0) {
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

        // Update prices if provided
        if (request.getTotalPrice() != null) {
            existingCartItem.setCartItemTotalPrice(request.getTotalPrice());
        }

        // Update foreign key references if provided
        if (request.getCartId() != null) {
            Cart newCart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + request.getCartId()));
            existingCartItem.setCart(newCart);
        }

        if (request.getProductId() != null) {
            Product newProduct = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId()));
            existingCartItem.setProduct(newProduct);
        }

        if (request.getCartItemStateId() != null) {
            CartItemState newState = cartItemStateRepository.findById(request.getCartItemStateId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item state not found with ID: " + request.getCartItemStateId()));
            existingCartItem.setCartItemState(newState);
        }

        // Recalculate total price if not explicitly provided and quantity > 0
        if (request.getTotalPrice() == null && request.getQuantity() != null && request.getQuantity() > 0) {
            existingCartItem.calculateTotalPrice();
        }

        CartItem updatedCartItem = cartItemRepository.save(existingCartItem);
        
        // Update cart total quantity
        updateCartTotalQuantity(existingCartItem.getCart().getCartId());

        logger.info("Cart item updated successfully with quantity logic");
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
     * Get cart total value
     * @param cartId Cart ID
     * @return Total cart value
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCartTotalValue(Long cartId) {
        logger.info("Calculating total value for cart ID: {}", cartId);
        return cartItemRepository.getCartTotalValue(cartId);
    }

    /**
     * Get cart total quantity
     * @param cartId Cart ID
     * @return Total cart quantity
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getCartTotalQuantity(Long cartId) {
        logger.info("Calculating total quantity for cart ID: {}", cartId);
        return cartItemRepository.getCartTotalQuantity(cartId);
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
     * Convert CartItem entity to DTO
     * @param cartItem CartItem entity
     * @return CartItemDto
     */
    @Override
    public CartItemDto convertToDto(CartItem cartItem) {
        return cartMapper.toDto(cartItem);
    }
}