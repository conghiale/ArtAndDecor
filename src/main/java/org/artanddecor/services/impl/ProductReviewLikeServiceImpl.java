package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductReviewLikeDto;
import org.artanddecor.dto.ProductReviewLikeRequestDto;
import org.artanddecor.model.ProductReviewLike;
import org.artanddecor.model.Review;
import org.artanddecor.model.User;
import org.artanddecor.repository.ProductReviewLikeRepository;
import org.artanddecor.repository.ReviewRepository;
import org.artanddecor.repository.UserRepository;
import org.artanddecor.services.ProductReviewLikeService;
import org.artanddecor.utils.ProductReviewLikeMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * ProductReviewLike Service Implementation
 * Business logic for ProductReviewLike management
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProductReviewLikeServiceImpl implements ProductReviewLikeService {

    private static final Logger logger = LoggerFactory.getLogger(ProductReviewLikeServiceImpl.class);

    private final ProductReviewLikeRepository productReviewLikeRepository;
    private final ProductReviewLikeMapperUtil productReviewLikeMapperUtil;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    /**
     * Get all product review likes with pagination
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewLikeDto> getAllProductReviewLikes(int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting all product review likes - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                    page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductReviewLike> likePage = productReviewLikeRepository.findAll(pageable);
        return likePage.map(productReviewLikeMapperUtil::toDto);
    }

    /**
     * Get product review like by ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductReviewLikeDto> getProductReviewLikeById(Long likeId) {
        logger.debug("Getting product review like by ID: {}", likeId);
        
        Optional<ProductReviewLike> like = productReviewLikeRepository.findById(likeId);
        return like.map(productReviewLikeMapperUtil::toDto);
    }

    /**
     * Get product review likes with filters
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewLikeDto> getProductReviewLikesWithFilters(
            Long userId, Long reviewId, int page, int size, String sortBy, String sortDir) {
        
        logger.debug("Getting product review likes with filters - userId: {}, reviewId: {}", userId, reviewId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductReviewLike> likePage = productReviewLikeRepository.findLikesWithFilters(userId, reviewId, pageable);
        return likePage.map(productReviewLikeMapperUtil::toDto);
    }

    /**
     * Get product review likes by user ID
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewLikeDto> getProductReviewLikesByUserId(Long userId, int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting product review likes for user ID: {}", userId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductReviewLike> likePage = productReviewLikeRepository.findByUser_UserId(userId, pageable);
        return likePage.map(productReviewLikeMapperUtil::toDto);
    }

    /**
     * Get product review likes by review ID
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewLikeDto> getProductReviewLikesByReviewId(Long reviewId, int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting product review likes for review ID: {}", reviewId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductReviewLike> likePage = productReviewLikeRepository.findByReview_ReviewId(reviewId, pageable);
        return likePage.map(productReviewLikeMapperUtil::toDto);
    }

    /**
     * Get specific product review like by user and review
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductReviewLikeDto> getProductReviewLikeByUserAndReview(Long userId, Long reviewId) {
        logger.debug("Getting product review like for user ID: {} and review ID: {}", userId, reviewId);
        
        Optional<ProductReviewLike> like = productReviewLikeRepository.findByUser_UserIdAndReview_ReviewId(userId, reviewId);
        return like.map(productReviewLikeMapperUtil::toDto);
    }

    /**
     * Check if user has liked a review
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserLikedReview(Long userId, Long reviewId) {
        logger.debug("Checking if user ID: {} has liked review ID: {}", userId, reviewId);
        
        return productReviewLikeRepository.existsByUser_UserIdAndReview_ReviewId(userId, reviewId);
    }

    /**
     * Get likes count for a review
     */
    @Override
    @Transactional(readOnly = true)
    public Long getLikesCountByReviewId(Long reviewId) {
        logger.debug("Getting likes count for review ID: {}", reviewId);
        
        return productReviewLikeRepository.countByReview_ReviewId(reviewId);
    }

    /**
     * Get total likes count by a user
     */
    @Override
    @Transactional(readOnly = true)
    public Long getLikesCountByUserId(Long userId) {
        logger.debug("Getting total likes count for user ID: {}", userId);
        
        return productReviewLikeRepository.countByUser_UserId(userId);
    }

    /**
     * Get likes by product ID
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewLikeDto> getProductReviewLikesByProductId(Long productId, int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting product review likes for product ID: {}", productId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductReviewLike> likePage = productReviewLikeRepository.findByProductId(productId, pageable);
        return likePage.map(productReviewLikeMapperUtil::toDto);
    }

    /**
     * Get likes by user for a specific product
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewLikeDto> getProductReviewLikesByUserIdAndProductId(
            Long userId, Long productId, int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting product review likes for user ID: {} and product ID: {}", userId, productId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductReviewLike> likePage = productReviewLikeRepository.findByUserIdAndProductId(userId, productId, pageable);
        return likePage.map(productReviewLikeMapperUtil::toDto);
    }

    // =============================================
    // CRUD OPERATIONS FOR USER LIKES
    // =============================================

    /**
     * Create new product review like (permitAll - accessible to all users)
     */
    @Override
    public ProductReviewLikeDto createProductReviewLike(ProductReviewLikeRequestDto requestDto) {
        logger.debug("Creating product review like for review ID: {} by user ID: {}", 
                    requestDto.getReviewId(), requestDto.getUserId());

        // Check if like already exists
        if (productReviewLikeRepository.existsByUser_UserIdAndReview_ReviewId(requestDto.getUserId(), requestDto.getReviewId())) {
            throw new IllegalArgumentException("User has already liked this review");
        }

        // Find related entities
        Review review = reviewRepository.findById(requestDto.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + requestDto.getReviewId()));

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + requestDto.getUserId()));

        // Create like entity
        ProductReviewLike like = new ProductReviewLike();
        like.setReview(review);
        like.setUser(user);
        like.setCreatedDt(LocalDateTime.now());
        like.setModifiedDt(LocalDateTime.now());

        // Save like
        ProductReviewLike savedLike = productReviewLikeRepository.save(like);

        // Update review like count
        Long newLikeCount = productReviewLikeRepository.countByReview_ReviewId(requestDto.getReviewId());
        review.setCountLike(newLikeCount.intValue());
        reviewRepository.save(review);
        
        logger.info("Successfully created product review like with ID: {}", savedLike.getProductReviewLikeId());
        return productReviewLikeMapperUtil.toDto(savedLike);
    }

    /**
     * Delete product review like (permitAll - accessible to all users)
     */
    @Override
    public void deleteProductReviewLike(Long userId, Long reviewId) {
        logger.debug("Deleting product review like for review ID: {} by user ID: {}", reviewId, userId);

        // Check if like exists
        if (!productReviewLikeRepository.existsByUser_UserIdAndReview_ReviewId(userId, reviewId)) {
            throw new IllegalArgumentException("Like not found for user ID: " + userId + " and review ID: " + reviewId);
        }

        // Delete like (hard delete)
        productReviewLikeRepository.deleteByUser_UserIdAndReview_ReviewId(userId, reviewId);

        // Update review like count
        Long newLikeCount = productReviewLikeRepository.countByReview_ReviewId(reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));
        review.setCountLike(newLikeCount.intValue());
        reviewRepository.save(review);
        
        logger.info("Successfully deleted product review like for review ID: {} by user ID: {}", reviewId, userId);
    }
}