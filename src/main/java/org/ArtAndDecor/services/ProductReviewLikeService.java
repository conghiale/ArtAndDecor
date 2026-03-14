package org.ArtAndDecor.services;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.ProductReviewLikeDto;
import org.ArtAndDecor.model.ProductReviewLike;
import org.ArtAndDecor.repository.ProductReviewLikeRepository;
import org.ArtAndDecor.utils.ProductReviewLikeMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * ProductReviewLike Service
 * Business logic for ProductReviewLike management
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProductReviewLikeService {

    private static final Logger logger = LoggerFactory.getLogger(ProductReviewLikeService.class);

    private final ProductReviewLikeRepository productReviewLikeRepository;

    private final ProductReviewLikeMapperUtil productReviewLikeMapperUtil;

    /**
     * Get all product review likes with pagination
     */
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
    @Transactional(readOnly = true)
    public Optional<ProductReviewLikeDto> getProductReviewLikeById(Long likeId) {
        logger.debug("Getting product review like by ID: {}", likeId);
        
        Optional<ProductReviewLike> like = productReviewLikeRepository.findById(likeId);
        return like.map(productReviewLikeMapperUtil::toDto);
    }

    /**
     * Get product review likes with filters
     */
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
    @Transactional(readOnly = true)
    public Optional<ProductReviewLikeDto> getProductReviewLikeByUserAndReview(Long userId, Long reviewId) {
        logger.debug("Getting product review like for user ID: {} and review ID: {}", userId, reviewId);
        
        Optional<ProductReviewLike> like = productReviewLikeRepository.findByUser_UserIdAndReview_ReviewId(userId, reviewId);
        return like.map(productReviewLikeMapperUtil::toDto);
    }

    /**
     * Check if user has liked a review
     */
    @Transactional(readOnly = true)
    public boolean hasUserLikedReview(Long userId, Long reviewId) {
        logger.debug("Checking if user ID: {} has liked review ID: {}", userId, reviewId);
        
        return productReviewLikeRepository.existsByUser_UserIdAndReview_ReviewId(userId, reviewId);
    }

    /**
     * Get likes count for a review
     */
    @Transactional(readOnly = true)
    public Long getLikesCountByReviewId(Long reviewId) {
        logger.debug("Getting likes count for review ID: {}", reviewId);
        
        return productReviewLikeRepository.countByReview_ReviewId(reviewId);
    }

    /**
     * Get total likes count by a user
     */
    @Transactional(readOnly = true)
    public Long getLikesCountByUserId(Long userId) {
        logger.debug("Getting total likes count for user ID: {}", userId);
        
        return productReviewLikeRepository.countByUser_UserId(userId);
    }

    /**
     * Get likes by product ID
     */
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
}