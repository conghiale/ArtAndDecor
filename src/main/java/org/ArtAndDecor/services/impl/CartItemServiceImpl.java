package org.ArtAndDecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.CartItemDto;
import org.ArtAndDecor.model.Cart;
import org.ArtAndDecor.model.CartItem;
import org.ArtAndDecor.model.CartItemState;
import org.ArtAndDecor.model.Product;
import org.ArtAndDecor.repository.CartItemRepository;
import org.ArtAndDecor.repository.CartRepository;
import org.ArtAndDecor.repository.CartItemStateRepository;
import org.ArtAndDecor.repository.ProductRepository;
import org.ArtAndDecor.services.CartItemService;
import org.ArtAndDecor.exception.ResourceNotFoundException;
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
 * Handles business logic for shopping cart item management
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

    /**
     * Get cart item by ID
     * @param cartItemId Cart item ID
     * @return CartItemDto
     * @throws ResourceNotFoundException if cart item not found
     */
    @Override
    @Transactional(readOnly = true)
    public CartItemDto getCartItemById(Long cartItemId) {
        logger.info("Fetching cart item with ID: {}", cartItemId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartItemId));

        return convertToDto(cartItem);
    }

    /**
     * Get cart items by cart ID
     * @param cartId Cart ID
     * @return List of CartItemDto
     */
    @Override
    @Transactional(readOnly = true)
    public List<CartItemDto> getCartItemsByCartId(Long cartId) {
        logger.info("Fetching cart items for cart ID: {}", cartId);

        List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cartId);
        return cartItems.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Get active cart items by user
     * @param userId User ID
     * @return List of active CartItemDto
     */    @Override    @Transactional(readOnly = true)
    public List<CartItemDto> getActiveCartItemsByUser(Long userId) {
        logger.info("Fetching active cart items for user ID: {}", userId);

        List<CartItem> cartItems = cartItemRepository.findActiveCartItemsByUser(userId);
        return cartItems.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Get cart items by quantity range
     * @param minQuantity Minimum quantity
     * @param maxQuantity Maximum quantity
     * @param page Page number
     * @param size Page size
     * @return Page of CartItemDto
     */
    @Transactional(readOnly = true)
    public Page<CartItemDto> getCartItemsByQuantityRange(Integer minQuantity, Integer maxQuantity, int page, int size) {
        logger.info("Fetching cart items with quantity range: {} - {}", minQuantity, maxQuantity);

        Pageable pageable = PageRequest.of(page, size);
        Page<CartItem> cartItemPage = cartItemRepository.findByQuantityRange(minQuantity, maxQuantity, pageable);

        return cartItemPage.map(this::convertToDto);
    }

    /**
     * Get cart items by date range
     * @param startDate Start date
     * @param endDate End date
     * @param page Page number
     * @param size Page size
     * @return Page of CartItemDto
     */
    @Transactional(readOnly = true)
    public Page<CartItemDto> getCartItemsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        logger.info("Fetching cart items between {} and {}", startDate, endDate);

        Pageable pageable = PageRequest.of(page, size);
        Page<CartItem> cartItemPage = cartItemRepository.findByDateRange(startDate, endDate, pageable);

        return cartItemPage.map(this::convertToDto);
    }

    /**
     * Add item to cart
     * @param cartId Cart ID
     * @param productId Product ID
     * @param quantity Item quantity
     * @return Created or updated CartItemDto
     */
    public CartItemDto addItemToCart(Long cartId, Long productId, Integer quantity) {
        logger.info("Adding item to cart - cartId: {}, productId: {}, quantity: {}", cartId, productId, quantity);

        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        // Check if item already exists in cart
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId);

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            // Update quantity if item already exists
            cartItem = existingCartItem.get();
            cartItem.setCartItemQuantity(cartItem.getCartItemQuantity() + quantity);
            cartItem.calculateTotalPrice();
            logger.info("Updated existing cart item with new quantity: {}", cartItem.getCartItemQuantity());
        } else {
            // Create new cart item
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setCartItemQuantity(quantity);
            
            // Set active cart item state
            CartItemState activeState = cartItemStateRepository.findActiveCartItemState()
                .orElseThrow(() -> new ResourceNotFoundException("Active cart item state not found"));
            cartItem.setCartItemState(activeState);
            
            cartItem.calculateTotalPrice();
            logger.info("Created new cart item");
        }

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        
        // Update cart total quantity
        updateCartTotalQuantity(cartId);

        return convertToDto(savedCartItem);
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
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        cartItem.setCartItemQuantity(quantity);
        cartItem.calculateTotalPrice();

        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        
        // Update cart total quantity
        updateCartTotalQuantity(cartItem.getCart().getCartId());

        logger.info("Cart item quantity updated successfully");
        return convertToDto(updatedCartItem);
    }

    /**
     * Remove item from cart
     * @param cartItemId Cart item ID
     */
    public void removeCartItem(Long cartItemId) {
        logger.info("Removing cart item with ID: {}", cartItemId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartItemId));

        Long cartId = cartItem.getCart().getCartId();
        cartItemRepository.delete(cartItem);
        
        // Update cart total quantity
        updateCartTotalQuantity(cartId);

        logger.info("Cart item removed successfully");
    }

    /**
     * Clear all items from cart
     * @param cartId Cart ID
     */
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
     */    @Override    @Transactional(readOnly = true)
    public Integer getCartTotalQuantity(Long cartId) {
        logger.info("Calculating total quantity for cart ID: {}", cartId);
        return cartItemRepository.getCartTotalQuantity(cartId);
    }

    /**
     * Get cart item statistics by category
     * @return List of cart item statistics by category
     */
    @Transactional(readOnly = true)
    public List<Object[]> getCartItemStatisticsByCategory() {
        logger.info("Fetching cart item statistics by category");
        return cartItemRepository.getCartItemStatisticsByCategory();
    }

    /**
     * Update cart total quantity
     * @param cartId Cart ID
     */
    private void updateCartTotalQuantity(Long cartId) {
        Integer totalQuantity = cartItemRepository.getCartTotalQuantity(cartId);
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart != null) {
            cart.setTotalQuantity(totalQuantity);
            cartRepository.save(cart);
        }
    }

    /**
     * Convert CartItem entity to DTO
     * @param cartItem CartItem entity
     * @return CartItemDto
     */
    @Override
    public CartItemDto convertToDto(CartItem cartItem) {
        CartItemDto dto = new CartItemDto();
        dto.setCartItemId(cartItem.getCartItemId());
        dto.setQuantity(cartItem.getCartItemQuantity());
        dto.setTotalPrice(cartItem.getCartItemTotalPrice());
        dto.setCreatedDt(cartItem.getCreatedDt());
        dto.setModifiedDt(cartItem.getModifiedDt());

        // Set unit price from product
        if (cartItem.getProduct() != null && cartItem.getProduct().getProductPrice() != null) {
            dto.setUnitPrice(cartItem.getProduct().getProductPrice());
        }

        // Set nested DTOs (implement based on your existing services)
        // dto.setCart(cartService.convertToDto(cartItem.getCart()));
        // dto.setProduct(productService.convertToDto(cartItem.getProduct()));
        // dto.setCartItemState(cartItemStateService.convertToDto(cartItem.getCartItemState()));

        return dto;
    }

    /**
     * Convert CartItemDto to CartItem entity
     * @param dto CartItemDto
     * @return CartItem entity
     */
    private CartItem convertToEntity(CartItemDto dto) {
        CartItem cartItem = new CartItem();
        cartItem.setCartItemId(dto.getCartItemId());
        cartItem.setCartItemQuantity(dto.getQuantity());
        cartItem.setCartItemTotalPrice(dto.getTotalPrice());

        // Set related entities based on IDs in nested DTOs
        // Implementation depends on your DTO structure

        return cartItem;
    }

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
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param sortBy Sort field
     * @param sortDirection Sort direction
     * @return Page of CartItemDto matching criteria
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CartItemDto> getCartItemsByCriteria(Long cartItemId, Long cartId, Long productId, 
                                                   Long userId, BigDecimal minPrice, BigDecimal maxPrice,
                                                   Integer minQuantity, Integer maxQuantity, 
                                                   Long cartItemStateId, int page, int size, 
                                                   String sortBy, String sortDirection) {
        logger.info("Fetching cart items by criteria - cartItemId: {}, cartId: {}, productId: {}, userId: {}, " +
                   "minPrice: {}, maxPrice: {}, minQuantity: {}, maxQuantity: {}, cartItemStateId: {}",
                   cartItemId, cartId, productId, userId, minPrice, maxPrice, 
                   minQuantity, maxQuantity, cartItemStateId);

        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CartItem> cartItemsPage = cartItemRepository.findCartItemsByCriteria(
            cartItemId, cartId, productId, userId, minPrice, maxPrice,
            minQuantity, maxQuantity, cartItemStateId, pageable);

        return cartItemsPage.map(this::convertToDto);
    }

    /**
     * Get active cart items by cart ID (for CUSTOMER role)
     * @param cartId Cart ID
     * @return List of active CartItemDto
     */
    @Transactional(readOnly = true)
    public List<CartItemDto> getActiveCartItemsByCartId(Long cartId) {
        logger.info("Fetching active cart items for cart ID: {}", cartId);
        
        List<CartItem> cartItems = cartItemRepository.findActiveCartItemsByCartId(cartId);
        return cartItems.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}