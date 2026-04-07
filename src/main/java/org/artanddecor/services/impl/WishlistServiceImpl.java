package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.WishlistDto;
import org.artanddecor.dto.WishlistRequest;
import org.artanddecor.model.Product;
import org.artanddecor.model.User;
import org.artanddecor.model.Wishlist;
import org.artanddecor.repository.ProductRepository;
import org.artanddecor.repository.UserRepository;
import org.artanddecor.repository.WishlistRepository;
import org.artanddecor.services.WishlistService;
import org.artanddecor.utils.WishlistMapper;
import org.artanddecor.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Wishlist Service Implementation
 * Handles business logic for wishlist management
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistServiceImpl implements WishlistService {
    
    private static final Logger logger = LoggerFactory.getLogger(WishlistServiceImpl.class);
    
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public Page<WishlistDto> findWishlistByCriteria(Long userId, String sessionId, Long productId, Pageable pageable) {
        logger.debug("Finding wishlist items with criteria - userId: {}, sessionId: {}, productId: {}, page: {}", 
                    userId, sessionId != null ? "***" : null, productId, pageable.getPageNumber());
        
        return wishlistRepository.findWishlistByCriteria(userId, sessionId, productId, pageable)
                .map(WishlistMapper::toDto);
    }

    @Override
    @Transactional
    public WishlistDto addToWishlist(WishlistRequest request) {
        logger.info("Adding product {} to wishlist - userId: {}, sessionId: {}", 
                   request.getProductId(), request.getUserId(), request.getSessionId() != null ? "***" : null);
        
        // Validate product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + request.getProductId()));
        
        User user = null;
        String effectiveSessionId = null;
        
        // If userId provided, validate user exists
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.getUserId()));
            
            // Check for duplicate entry for authenticated user
            if (wishlistRepository.existsByUserUserIdAndProductProductId(request.getUserId(), request.getProductId())) {
                throw new IllegalArgumentException("Product already exists in user's wishlist");
            }
            
            logger.debug("Processing authenticated user wishlist request for userId: {}", request.getUserId());
        } else {
            // For anonymous users, handle sessionId logic
            if (request.getSessionId() != null && !request.getSessionId().trim().isEmpty()) {
                // Use existing sessionId
                effectiveSessionId = request.getSessionId().trim();
                
                // Check for duplicate entry for anonymous user with existing session
                if (wishlistRepository.existsBySessionIdAndProductProductId(effectiveSessionId, request.getProductId())) {
                    throw new IllegalArgumentException("Product already exists in session's wishlist");
                }
                
                logger.debug("Processing anonymous user wishlist request with existing sessionId");
            } else {
                // Generate new sessionId for anonymous user (both userId and sessionId are null/empty)
                effectiveSessionId = Utils.generateSessionId();
                logger.info("Generated new sessionId for anonymous user: {}...", effectiveSessionId.substring(0, 8));
                // No need to check for duplicates since this is a brand new session
            }
        }
        
        // Create and save wishlist item
        Wishlist wishlist = WishlistMapper.toEntity(request, user, product);
        
        // Override sessionId if we generated a new one for anonymous users
        if (user == null && effectiveSessionId != null) {
            wishlist.setSessionId(effectiveSessionId);
        }
        
        Wishlist saved = wishlistRepository.save(wishlist);
        
        logger.info("Wishlist item created successfully with ID: {}", saved.getWishlistId());
        
        // Load with full relationships for DTO mapping
        return wishlistRepository.findWishlistByCriteria(
                saved.getUser() != null ? saved.getUser().getUserId() : null,
                saved.getSessionId(),
                saved.getProduct().getProductId(),
                Pageable.unpaged()
        ).getContent().stream()
                .findFirst()
                .map(WishlistMapper::toDto)
                .orElse(WishlistMapper.toDto(saved));
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long wishlistId) {
        logger.info("Removing wishlist item with ID: {}", wishlistId);
        
        // Validate wishlist item exists
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found with ID: " + wishlistId));
        
        // Hard delete
        wishlistRepository.delete(wishlist);
        
        logger.info("Wishlist item {} removed successfully", wishlistId);
    }
}