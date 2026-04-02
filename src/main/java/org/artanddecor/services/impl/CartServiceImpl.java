package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.CartDto;
import org.artanddecor.model.Cart;
import org.artanddecor.model.CartItem;
import org.artanddecor.model.CartItemState;
import org.artanddecor.model.CartState;
import org.artanddecor.model.User;
import org.artanddecor.repository.CartRepository;
import org.artanddecor.repository.CartItemRepository;
import org.artanddecor.repository.CartItemStateRepository;
import org.artanddecor.repository.CartStateRepository;
import org.artanddecor.repository.UserRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cart Service Implementation
 * Handles business logic for cart management optimized for workflow requirements
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartStateRepository cartStateRepository;
    private final CartItemStateRepository cartItemStateRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

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

        return cartMapper.toDto(cart);
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

        return cartMapper.toDto(cart);
    }

    /**
     * Get current cart (for authenticated user or guest session)
     * Handles both authenticated users and guest sessions
     * @param userId User ID (optional, null for guest)
     * @param sessionId Session ID (optional, generates new if null)
     * @return Current active cart DTO
     */
    @Override
    public CartDto getCurrentCart(Long userId, String sessionId) {
        logger.info("Getting current cart - userId: {}, sessionId: {}", userId, sessionId);

        if (userId != null) {
            // User is authenticated - get or create active cart
            return createOrGetActiveCart(userId);
        } else {
            // Guest user - handle session-based cart
            if (sessionId == null) {
                sessionId = generateSessionId();
                logger.info("Generated new session ID: {}", sessionId);
            }
            return createOrGetActiveCartForSession(sessionId);
        }
    }

    /**
     * Create or get active cart for user
     * @param userId User ID
     * @return Active cart DTO
     */
    @Override
    public CartDto createOrGetActiveCart(Long userId) {
        logger.info("Creating or getting active cart for user ID: {}", userId);

        // Try to find existing active cart
        Cart existingCart = cartRepository.findActiveCartByUser(userId).orElse(null);
        
        if (existingCart != null) {
            logger.info("Found existing active cart ID: {}", existingCart.getCartId());
            return cartMapper.toDto(existingCart);
        }

        // Create new cart
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        CartState activeState = cartStateRepository.findActiveCartState()
            .orElseThrow(() -> new ResourceNotFoundException("Active cart state not found"));

        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setCartState(activeState);
        newCart.setCartSlug(generateCartSlug(userId));
        newCart.setTotalQuantity(0);
        newCart.setCartEnabled(true);

        Cart savedCart = cartRepository.save(newCart);
        logger.info("Created new active cart ID: {} for user ID: {}", savedCart.getCartId(), userId);

        return cartMapper.toDto(savedCart);
    }

    /**
     * Create or get active cart for session
     * @param sessionId Session ID
     * @return Active cart DTO
     */
    @Override
    public CartDto createOrGetActiveCartForSession(String sessionId) {
        logger.info("Creating or getting active cart for session ID: {}", sessionId);

        // Try to find existing active cart for session
        Cart existingCart = cartRepository.findActiveCartBySession(sessionId).orElse(null);
        
        if (existingCart != null) {
            logger.info("Found existing active cart ID: {} for session: {}", existingCart.getCartId(), sessionId);
            return cartMapper.toDto(existingCart);
        }

        // Create new cart for session
        CartState activeState = cartStateRepository.findActiveCartState()
            .orElseThrow(() -> new ResourceNotFoundException("Active cart state not found"));

        Cart newCart = new Cart();
        newCart.setSessionId(sessionId);
        newCart.setCartState(activeState);
        newCart.setCartSlug(generateCartSlugForSession(sessionId));
        newCart.setTotalQuantity(0);
        newCart.setCartEnabled(true);

        Cart savedCart = cartRepository.save(newCart);
        logger.info("Created new active cart ID: {} for session ID: {}", savedCart.getCartId(), sessionId);

        return cartMapper.toDto(savedCart);
    }

    /**
     * Generate unique session ID for guest users
     * @return Generated session ID
     */
    @Override
    public String generateSessionId() {
        return "session_" + UUID.randomUUID().toString().replace("-", "");
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

        return cartsPage.map(cartMapper::toDto);
    }

    /**
     * Generate unique cart slug for user
     * @param userId User ID
     * @return Generated cart slug
     */
    private String generateCartSlug(Long userId) {
        String baseSlug = "cart_user_" + userId + "_" + System.currentTimeMillis();
        
        // Ensure uniqueness
        while (cartRepository.existsByCartSlug(baseSlug)) {
            baseSlug = "cart_user_" + userId + "_" + System.currentTimeMillis() + "_" + 
                      (int)(Math.random() * 1000);
        }
        
        return baseSlug;
    }

    /**
     * Generate unique cart slug for session
     * @param sessionId Session ID
     * @return Generated cart slug
     */
    private String generateCartSlugForSession(String sessionId) {
        String baseSlug = "cart_session_" + sessionId.hashCode() + "_" + System.currentTimeMillis();
        
        // Ensure uniqueness
        while (cartRepository.existsByCartSlug(baseSlug)) {
            baseSlug = "cart_session_" + sessionId.hashCode() + "_" + System.currentTimeMillis() + "_" + 
                      (int)(Math.random() * 1000);
        }
        
        return baseSlug;
    }

    /**
     * Merge guest cart items to user cart
     * Transfers all active cart items from guest cart to user cart
     * @param userId User ID (target cart owner)
     * @param sessionId Session ID (source guest cart)
     * @return Merged user cart DTO
     */
    @Override
    public CartDto mergeGuestCartToUserCart(Long userId, String sessionId) {
        logger.info("Merging guest cart to user cart - userId: {}, sessionId: {}", userId, sessionId);

        // Get or create user cart
        CartDto userCart = createOrGetActiveCart(userId);
        
        // Find guest cart
        Cart guestCart = cartRepository.findActiveCartBySession(sessionId).orElse(null);
        if (guestCart == null) {
            logger.info("No active guest cart found for session: {}, returning user cart", sessionId);
            return userCart;
        }

        // Get active cart items from guest cart
        List<CartItem> guestCartItems = cartItemRepository.findActiveCartItemsByCartId(guestCart.getCartId());
        
        if (guestCartItems.isEmpty()) {
            logger.info("No active items in guest cart, returning user cart");
            return userCart;
        }

        // Get user cart entity
        Cart userCartEntity = cartRepository.findById(userCart.getCartId())
            .orElseThrow(() -> new ResourceNotFoundException("User cart not found with ID: " + userCart.getCartId()));

        logger.info("Merging {} active items from guest cart to user cart", guestCartItems.size());

        // Merge each guest cart item
        for (CartItem guestItem : guestCartItems) {
            mergeCartItem(userCartEntity, guestItem);
        }

        // Update user cart total quantity
        updateCartTotalQuantity(userCartEntity);
        
        // Clear guest cart by setting items to inactive/removed state
        clearGuestCartItems(guestCart.getCartId());

        logger.info("Cart merge completed successfully");
        
        // Return updated user cart
        return cartMapper.toDto(cartRepository.findById(userCartEntity.getCartId()).get());
    }

    /**
     * Get or create cart with merge support
     * Priority: cartId -> merge scenario (userId + sessionId) -> userId -> sessionId
     * @param cartId Cart ID (highest priority)
     * @param userId User ID 
     * @param sessionId Session ID
     * @return Cart DTO with merged items if applicable
     */
    @Override
    public CartDto getCartWithMergeSupport(Long cartId, Long userId, String sessionId) {
        logger.info("Getting cart with merge support - cartId: {}, userId: {}, sessionId: {}", cartId, userId, sessionId);

        // Priority 1: cartId
        if (cartId != null) {
            logger.info("Using cartId priority: {}", cartId);
            return getCartById(cartId);
        }

        // Priority 2: merge scenario (both userId and sessionId present)
        if (userId != null && sessionId != null) {
            logger.info("Merge scenario detected - merging guest cart to user cart");
            return mergeGuestCartToUserCart(userId, sessionId);
        }

        // Priority 3: userId only
        if (userId != null) {
            logger.info("Using userId priority: {}", userId);
            return getCurrentCart(userId, null);
        }

        // Priority 4: sessionId only  
        if (sessionId != null) {
            logger.info("Using sessionId priority: {}", sessionId);
            return getCurrentCart(null, sessionId);
        }

        // No identifiers provided - return new guest cart
        return getCurrentCart(null, null);
    }

    /**
     * Merge a single cart item from guest cart to user cart
     * @param userCart Target user cart
     * @param guestItem Source guest cart item
     */
    private void mergeCartItem(Cart userCart, CartItem guestItem) {
        logger.debug("Merging cart item - product: {}, quantity: {}", 
                    guestItem.getProduct().getProductId(), guestItem.getCartItemQuantity());

        // Check if user cart already has this product
        Optional<CartItem> existingUserItem = cartItemRepository.findByCartIdAndProductId(
            userCart.getCartId(), guestItem.getProduct().getProductId());

        if (existingUserItem.isPresent()) {
            // Product exists - merge quantities
            CartItem userItem = existingUserItem.get();
            
            // Only merge if user item is not in REMOVED state
            if (!"REMOVED".equals(userItem.getCartItemState().getCartItemStateName())) {
                int newQuantity = userItem.getCartItemQuantity() + guestItem.getCartItemQuantity();
                userItem.setCartItemQuantity(newQuantity);
                userItem.calculateTotalPrice();
                
                // Ensure user item is in ACTIVE state
                CartItemState activeState = cartItemStateRepository.findActiveCartItemState()
                    .orElseThrow(() -> new ResourceNotFoundException("Active cart item state not found"));
                userItem.setCartItemState(activeState);
                
                cartItemRepository.save(userItem);
                logger.debug("Merged quantities - new total: {}", newQuantity);
            }
        } else {
            // Product doesn't exist in user cart - transfer the item
            guestItem.setCart(userCart);
            cartItemRepository.save(guestItem);
            logger.debug("Transferred cart item to user cart");
        }
    }

    /**
     * Clear guest cart items by setting them to REMOVED state
     * @param guestCartId Guest cart ID
     */
    private void clearGuestCartItems(Long guestCartId) {
        logger.debug("Clearing guest cart items for cart ID: {}", guestCartId);
        
        List<CartItem> guestItems = cartItemRepository.findActiveCartItemsByCartId(guestCartId);
        
        if (!guestItems.isEmpty()) {
            CartItemState removedState = cartItemStateRepository.findByCartItemStateName("REMOVED")
                .orElseThrow(() -> new ResourceNotFoundException("REMOVED cart item state not found"));
                
            for (CartItem item : guestItems) {
                item.setCartItemState(removedState);
            }
            
            cartItemRepository.saveAll(guestItems);
            logger.info("Cleared {} items from guest cart", guestItems.size());
        }
    }

    /**
     * Update cart total quantity
     * @param cart Cart entity
     */
    private void updateCartTotalQuantity(Cart cart) {
        Integer totalQuantity = cartItemRepository.getActiveCartItemsTotalQuantity(cart.getCartId());
        cart.setTotalQuantity(totalQuantity != null ? totalQuantity : 0);
        cartRepository.save(cart);
        logger.debug("Updated cart total quantity: {}", totalQuantity);
    }
}