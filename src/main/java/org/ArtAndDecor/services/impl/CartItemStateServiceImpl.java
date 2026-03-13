package org.ArtAndDecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.CartItemStateDto;
import org.ArtAndDecor.model.CartItemState;
import org.ArtAndDecor.repository.CartItemStateRepository;
import org.ArtAndDecor.services.CartItemStateService;
import org.ArtAndDecor.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Cart Item State Service Implementation
 * Handles business logic for cart item state management
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartItemStateServiceImpl implements CartItemStateService {

    private static final Logger logger = LoggerFactory.getLogger(CartItemStateServiceImpl.class);

    private final CartItemStateRepository cartItemStateRepository;

    /**
     * Get cart item state by ID
     * @param cartItemStateId Cart item state ID
     * @return CartItemStateDto
     * @throws ResourceNotFoundException if cart item state not found
     */
    @Override
    @Transactional(readOnly = true)
    public CartItemStateDto getCartItemStateById(Long cartItemStateId) {
        logger.info("Fetching cart item state with ID: {}", cartItemStateId);

        CartItemState cartItemState = cartItemStateRepository.findById(cartItemStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item state not found with ID: " + cartItemStateId));

        return convertToDto(cartItemState);
    }

    /**
     * Get cart item state by name
     * @param cartItemStateName Cart item state name
     * @return CartItemStateDto
     * @throws ResourceNotFoundException if cart item state not found
     */
    @Override
    @Transactional(readOnly = true)
    public CartItemStateDto getCartItemStateByName(String cartItemStateName) {
        logger.info("Fetching cart item state with name: {}", cartItemStateName);

        CartItemState cartItemState = cartItemStateRepository.findByCartItemStateName(cartItemStateName)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item state not found with name: " + cartItemStateName));

        return convertToDto(cartItemState);
    }

    /**
     * Search cart item states by keyword
     * @param keyword Search keyword
     * @param page Page number
     * @param size Page size
     * @return Page of CartItemStateDto
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CartItemStateDto> searchCartItemStates(String keyword, int page, int size) {
        logger.info("Searching cart item states with keyword: {}", keyword);

        Pageable pageable = PageRequest.of(page, size);
        Page<CartItemState> cartItemStatePage = cartItemStateRepository.findByCartItemStateNameContainingIgnoreCase(keyword, pageable);

        return cartItemStatePage.map(this::convertToDto);
    }

    /**
     * Get cart item states with cart item count
     * @param page Page number
     * @param size Page size
     * @return Page of CartItemStateDto with cart item count
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CartItemStateDto> getCartItemStatesWithCartItemCount(int page, int size) {
        logger.info("Fetching cart item states with cart item count");

        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> cartItemStateWithCountPage = cartItemStateRepository.findCartItemStatesWithCartItemCount(pageable);

        return cartItemStateWithCountPage.map(objects -> {
            CartItemState cartItemState = (CartItemState) objects[0];
            Long cartItemCount = (Long) objects[1];
            
            CartItemStateDto dto = convertToDto(cartItemState);
            dto.setCartItemCount(cartItemCount);
            return dto;
        });
    }

    /**
     * Create new cart item state
     * @param cartItemStateDto Cart item state data
     * @return Created CartItemStateDto
     */
    @Override
    public CartItemStateDto createCartItemState(CartItemStateDto cartItemStateDto) {
        logger.info("Creating new cart item state: {}", cartItemStateDto.getCartItemStateName());

        // Check if cart item state name already exists
        if (cartItemStateRepository.existsByCartItemStateName(cartItemStateDto.getCartItemStateName())) {
            throw new IllegalArgumentException("Cart item state name already exists: " + cartItemStateDto.getCartItemStateName());
        }

        CartItemState cartItemState = convertToEntity(cartItemStateDto);
        cartItemState.setCartItemStateEnabled(true);

        CartItemState savedCartItemState = cartItemStateRepository.save(cartItemState);
        logger.info("Cart item state created successfully with ID: {}", savedCartItemState.getCartItemStateId());

        return convertToDto(savedCartItemState);
    }

    /**
     * Update cart item state
     * @param cartItemStateId Cart item state ID
     * @param cartItemStateDto Updated cart item state data
     * @return Updated CartItemStateDto
     * @throws ResourceNotFoundException if cart item state not found
     */
    @Override
    public CartItemStateDto updateCartItemState(Long cartItemStateId, CartItemStateDto cartItemStateDto) {
        logger.info("Updating cart item state with ID: {}", cartItemStateId);

        CartItemState existingCartItemState = cartItemStateRepository.findById(cartItemStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item state not found with ID: " + cartItemStateId));

        // Update fields
        if (cartItemStateDto.getCartItemStateName() != null) {
            // Check if new name already exists (exclude current record)
            if (!existingCartItemState.getCartItemStateName().equals(cartItemStateDto.getCartItemStateName()) &&
                cartItemStateRepository.existsByCartItemStateName(cartItemStateDto.getCartItemStateName())) {
                throw new IllegalArgumentException("Cart item state name already exists: " + cartItemStateDto.getCartItemStateName());
            }
            existingCartItemState.setCartItemStateName(cartItemStateDto.getCartItemStateName());
        }
        if (cartItemStateDto.getCartItemStateDisplayName() != null) {
            existingCartItemState.setCartItemStateDisplayName(cartItemStateDto.getCartItemStateDisplayName());
        }
        if (cartItemStateDto.getCartItemStateRemark() != null) {
            existingCartItemState.setCartItemStateRemark(cartItemStateDto.getCartItemStateRemark());
        }
        if (cartItemStateDto.getCartItemStateEnabled() != null) {
            existingCartItemState.setCartItemStateEnabled(cartItemStateDto.getCartItemStateEnabled());
        }

        CartItemState updatedCartItemState = cartItemStateRepository.save(existingCartItemState);
        logger.info("Cart item state updated successfully with ID: {}", updatedCartItemState.getCartItemStateId());

        return convertToDto(updatedCartItemState);
    }

    /**
     * Delete cart item state by ID
     * @param cartItemStateId Cart item state ID
     * @throws ResourceNotFoundException if cart item state not found
     */
    @Override
    public void deleteCartItemState(Long cartItemStateId) {
        logger.info("Deleting cart item state with ID: {}", cartItemStateId);

        CartItemState cartItemState = cartItemStateRepository.findById(cartItemStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item state not found with ID: " + cartItemStateId));

        // Check if cart item state is being used by any cart items
        Long cartItemCount = cartItemStateRepository.getCartItemCountByState(cartItemStateId);
        if (cartItemCount > 0) {
            throw new IllegalStateException("Cannot delete cart item state. It is being used by " + cartItemCount + " cart item(s).");
        }

        cartItemStateRepository.delete(cartItemState);
        logger.info("Cart item state deleted successfully with ID: {}", cartItemStateId);
    }

    /**
     * Enable/Disable cart item state
     * @param cartItemStateId Cart item state ID
     * @param enabled Enabled status
     * @return Updated CartItemStateDto
     */
    @Override
    public CartItemStateDto toggleCartItemStateEnabled(Long cartItemStateId, boolean enabled) {
        logger.info("Setting cart item state {} enabled status to: {}", cartItemStateId, enabled);

        CartItemState cartItemState = cartItemStateRepository.findById(cartItemStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item state not found with ID: " + cartItemStateId));

        cartItemState.setCartItemStateEnabled(enabled);
        CartItemState updatedCartItemState = cartItemStateRepository.save(cartItemState);

        return convertToDto(updatedCartItemState);
    }

    /**
     * Get active cart item state
     * @return Active CartItemStateDto
     * @throws ResourceNotFoundException if active cart item state not found
     */
    @Override
    @Transactional(readOnly = true)
    public CartItemStateDto getActiveCartItemState() {
        logger.info("Fetching active cart item state");

        CartItemState cartItemState = cartItemStateRepository.findActiveCartItemState()
            .orElseThrow(() -> new ResourceNotFoundException("Active cart item state not found"));

        return convertToDto(cartItemState);
    }

    /**
     * Get ordered cart item state
     * @return Ordered CartItemStateDto
     * @throws ResourceNotFoundException if ordered cart item state not found
     */
    @Override
    @Transactional(readOnly = true)
    public CartItemStateDto getOrderedCartItemState() {
        logger.info("Fetching ordered cart item state");

        CartItemState cartItemState = cartItemStateRepository.findOrderedCartItemState()
            .orElseThrow(() -> new ResourceNotFoundException("Ordered cart item state not found"));

        return convertToDto(cartItemState);
    }

    /**
     * Get cart item statistics
     * @return List of cart item statistics by state
     */
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getCartItemStatistics() {
        logger.info("Fetching cart item statistics");
        return cartItemStateRepository.getCartItemStatistics();
    }

    /**
     * Convert CartItemState entity to DTO
     * @param cartItemState CartItemState entity
     * @return CartItemStateDto
     */
    @Override
    public CartItemStateDto convertToDto(CartItemState cartItemState) {
        CartItemStateDto dto = new CartItemStateDto();
        dto.setCartItemStateId(cartItemState.getCartItemStateId());
        dto.setCartItemStateName(cartItemState.getCartItemStateName());
        dto.setCartItemStateDisplayName(cartItemState.getCartItemStateDisplayName());
        dto.setCartItemStateRemark(cartItemState.getCartItemStateRemark());
        dto.setCartItemStateEnabled(cartItemState.getCartItemStateEnabled());
        return dto;
    }

    /**
     * Convert CartItemStateDto to CartItemState entity
     * @param dto CartItemStateDto
     * @return CartItemState entity
     */
    private CartItemState convertToEntity(CartItemStateDto dto) {
        CartItemState cartItemState = new CartItemState();
        cartItemState.setCartItemStateId(dto.getCartItemStateId());
        cartItemState.setCartItemStateName(dto.getCartItemStateName());
        cartItemState.setCartItemStateDisplayName(dto.getCartItemStateDisplayName());
        cartItemState.setCartItemStateRemark(dto.getCartItemStateRemark());
        cartItemState.setCartItemStateEnabled(dto.getCartItemStateEnabled());
        return cartItemState;
    }

    /**
     * Get cart item states by various criteria with flexible filtering
     * @param cartItemStateId Filter by cart item state ID (optional)
     * @param cartItemStateName Filter by cart item state name (optional)
     * @param cartItemStateEnabled Filter by enabled status (optional)
     * @param textSearch Text search in name, display name, and remark (optional)
     * @return List of CartItemStateDto matching criteria (no pagination)
     */
    @Override
    @Transactional(readOnly = true)
    public List<CartItemStateDto> getCartItemStatesByCriteria(Long cartItemStateId, String cartItemStateName, 
                                                             Boolean cartItemStateEnabled, String textSearch) {
        logger.info("Fetching cart item states by criteria - cartItemStateId: {}, cartItemStateName: {}, cartItemStateEnabled: {}, textSearch: {}", 
                   cartItemStateId, cartItemStateName, cartItemStateEnabled, textSearch);

        List<CartItemState> cartItemStates = cartItemStateRepository.findCartItemStatesByCriteria(
            cartItemStateId, cartItemStateName, cartItemStateEnabled, textSearch);
        
        return cartItemStates.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}