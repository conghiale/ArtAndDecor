package org.ArtAndDecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.CartStateDto;
import org.ArtAndDecor.model.CartState;
import org.ArtAndDecor.repository.CartStateRepository;
import org.ArtAndDecor.services.CartStateService;
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
 * Cart State Service Implementation
 * Handles business logic for cart state management
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartStateServiceImpl implements CartStateService {

    private static final Logger logger = LoggerFactory.getLogger(CartStateServiceImpl.class);

    private final CartStateRepository cartStateRepository;

    /**
     * Get cart state by ID
     * @param cartStateId Cart state ID
     * @return CartStateDto
     * @throws ResourceNotFoundException if cart state not found
     */
    @Override
    @Transactional(readOnly = true)
    public CartStateDto getCartStateById(Long cartStateId) {
        logger.info("Fetching cart state with ID: {}", cartStateId);

        CartState cartState = cartStateRepository.findById(cartStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart state not found with ID: " + cartStateId));

        return convertToDto(cartState);
    }

    /**
     * Get cart state by name
     * @param cartStateName Cart state name
     * @return CartStateDto
     * @throws ResourceNotFoundException if cart state not found
     */
    @Override
    @Transactional(readOnly = true)
    public CartStateDto getCartStateByName(String cartStateName) {
        logger.info("Fetching cart state with name: {}", cartStateName);

        CartState cartState = cartStateRepository.findByCartStateName(cartStateName)
            .orElseThrow(() -> new ResourceNotFoundException("Cart state not found with name: " + cartStateName));

        return convertToDto(cartState);
    }

    /**
     * Search cart states by keyword
     * @param keyword Search keyword
     * @param page Page number
     * @param size Page size
     * @return Page of CartStateDto
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CartStateDto> searchCartStates(String keyword, int page, int size) {
        logger.info("Searching cart states with keyword: {}", keyword);

        Pageable pageable = PageRequest.of(page, size);
        Page<CartState> cartStatePage = cartStateRepository.findByCartStateNameContainingIgnoreCase(keyword, pageable);

        return cartStatePage.map(this::convertToDto);
    }

    /**
     * Get cart states with cart count
     * @param page Page number
     * @param size Page size
     * @return Page of CartStateDto with cart count
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CartStateDto> getCartStatesWithCartCount(int page, int size) {
        logger.info("Fetching cart states with cart count");

        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> cartStateWithCountPage = cartStateRepository.findCartStatesWithCartCount(pageable);

        return cartStateWithCountPage.map(objects -> {
            CartState cartState = (CartState) objects[0];
            Long cartCount = (Long) objects[1];
            
            CartStateDto dto = convertToDto(cartState);
            dto.setCartCount(cartCount);
            return dto;
        });
    }

    /**
     * Create new cart state
     * @param cartStateDto Cart state data
     * @return Created CartStateDto
     */
    @Override
    public CartStateDto createCartState(CartStateDto cartStateDto) {
        logger.info("Creating new cart state: {}", cartStateDto.getCartStateName());

        // Check if cart state name already exists
        if (cartStateRepository.existsByCartStateName(cartStateDto.getCartStateName())) {
            throw new IllegalArgumentException("Cart state name already exists: " + cartStateDto.getCartStateName());
        }

        CartState cartState = convertToEntity(cartStateDto);
        cartState.setCartStateEnabled(true);

        CartState savedCartState = cartStateRepository.save(cartState);
        logger.info("Cart state created successfully with ID: {}", savedCartState.getCartStateId());

        return convertToDto(savedCartState);
    }

    /**
     * Update cart state
     * @param cartStateId Cart state ID
     * @param cartStateDto Updated cart state data
     * @return Updated CartStateDto
     * @throws ResourceNotFoundException if cart state not found
     */
    @Override
    public CartStateDto updateCartState(Long cartStateId, CartStateDto cartStateDto) {
        logger.info("Updating cart state with ID: {}", cartStateId);

        CartState existingCartState = cartStateRepository.findById(cartStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart state not found with ID: " + cartStateId));

        // Update fields
        if (cartStateDto.getCartStateName() != null) {
            // Check if new name already exists (exclude current record)
            if (!existingCartState.getCartStateName().equals(cartStateDto.getCartStateName()) &&
                cartStateRepository.existsByCartStateName(cartStateDto.getCartStateName())) {
                throw new IllegalArgumentException("Cart state name already exists: " + cartStateDto.getCartStateName());
            }
            existingCartState.setCartStateName(cartStateDto.getCartStateName());
        }
        if (cartStateDto.getCartStateDisplayName() != null) {
            existingCartState.setCartStateDisplayName(cartStateDto.getCartStateDisplayName());
        }
        if (cartStateDto.getCartStateRemark() != null) {
            existingCartState.setCartStateRemark(cartStateDto.getCartStateRemark());
        }
        if (cartStateDto.getCartStateEnabled() != null) {
            existingCartState.setCartStateEnabled(cartStateDto.getCartStateEnabled());
        }

        CartState updatedCartState = cartStateRepository.save(existingCartState);
        logger.info("Cart state updated successfully with ID: {}", updatedCartState.getCartStateId());

        return convertToDto(updatedCartState);
    }

    /**
     * Delete cart state by ID
     * @param cartStateId Cart state ID
     * @throws ResourceNotFoundException if cart state not found
     */
    @Override
    public void deleteCartState(Long cartStateId) {
        logger.info("Deleting cart state with ID: {}", cartStateId);

        CartState cartState = cartStateRepository.findById(cartStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart state not found with ID: " + cartStateId));

        // Check if cart state is being used by any carts
        Long cartCount = cartStateRepository.getCartCountByState(cartStateId);
        if (cartCount > 0) {
            throw new IllegalStateException("Cannot delete cart state. It is being used by " + cartCount + " cart(s).");
        }

        cartStateRepository.delete(cartState);
        logger.info("Cart state deleted successfully with ID: {}", cartStateId);
    }

    /**
     * Enable/Disable cart state
     * @param cartStateId Cart state ID
     * @param enabled Enabled status
     * @return Updated CartStateDto
     */
    @Override
    public CartStateDto toggleCartStateEnabled(Long cartStateId, boolean enabled) {
        logger.info("Setting cart state {} enabled status to: {}", cartStateId, enabled);

        CartState cartState = cartStateRepository.findById(cartStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart state not found with ID: " + cartStateId));

        cartState.setCartStateEnabled(enabled);
        CartState updatedCartState = cartStateRepository.save(cartState);

        return convertToDto(updatedCartState);
    }

    /**
     * Get active cart state
     * @return Active CartStateDto
     * @throws ResourceNotFoundException if active cart state not found
     */
    @Override
    @Transactional(readOnly = true)
    public CartStateDto getActiveCartState() {
        logger.info("Fetching active cart state");

        CartState cartState = cartStateRepository.findActiveCartState()
            .orElseThrow(() -> new ResourceNotFoundException("Active cart state not found"));

        return convertToDto(cartState);
    }

    /**
     * Convert CartState entity to DTO
     * @param cartState CartState entity
     * @return CartStateDto
     */
    @Override
    public CartStateDto convertToDto(CartState cartState) {
        CartStateDto dto = new CartStateDto();
        dto.setCartStateId(cartState.getCartStateId());
        dto.setCartStateName(cartState.getCartStateName());
        dto.setCartStateDisplayName(cartState.getCartStateDisplayName());
        dto.setCartStateRemark(cartState.getCartStateRemark());
        dto.setCartStateEnabled(cartState.getCartStateEnabled());
        return dto;
    }

    /**
     * Convert CartStateDto to CartState entity
     * @param dto CartStateDto
     * @return CartState entity
     */
    private CartState convertToEntity(CartStateDto dto) {
        CartState cartState = new CartState();
        cartState.setCartStateId(dto.getCartStateId());
        cartState.setCartStateName(dto.getCartStateName());
        cartState.setCartStateDisplayName(dto.getCartStateDisplayName());
        cartState.setCartStateRemark(dto.getCartStateRemark());
        cartState.setCartStateEnabled(dto.getCartStateEnabled());
        return cartState;
    }

    /**
     * Get cart states by various criteria with flexible filtering
     * @param cartStateId Filter by cart state ID (optional)
     * @param cartStateName Filter by cart state name (optional)
     * @param cartStateEnabled Filter by enabled status (optional)
     * @param textSearch Text search in name, display name, and remark (optional)
     * @return List of CartStateDto matching criteria (no pagination)
     */
    @Override
    @Transactional(readOnly = true)
    public List<CartStateDto> getCartStatesByCriteria(Long cartStateId, String cartStateName, 
                                                     Boolean cartStateEnabled, String textSearch) {
        logger.info("Fetching cart states by criteria - cartStateId: {}, cartStateName: {}, cartStateEnabled: {}, textSearch: {}", 
                   cartStateId, cartStateName, cartStateEnabled, textSearch);

        List<CartState> cartStates = cartStateRepository.findCartStatesByCriteria(
            cartStateId, cartStateName, cartStateEnabled, textSearch);
        
        return cartStates.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}