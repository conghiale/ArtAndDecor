package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ReviewDto;
import org.artanddecor.dto.ReviewRequestDto;
import org.artanddecor.model.Review;
import org.artanddecor.model.Product;
import org.artanddecor.model.User;
import org.artanddecor.repository.ReviewRepository;
import org.artanddecor.repository.ProductRepository;
import org.artanddecor.repository.UserRepository;
import org.artanddecor.services.ReviewService;
import org.artanddecor.utils.ReviewMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Review Service Implementation
 * Business logic for Review management
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository reviewRepository;
    private final ReviewMapperUtil reviewMapperUtil;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Get all reviews with pagination
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDto> getAllReviews(int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting all reviews - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Review> reviewPage = reviewRepository.findAll(pageable);
        return reviewPage.map(reviewMapperUtil::toDto);
    }

    /**
     * Get review by ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ReviewDto> getReviewById(Long reviewId) {
        logger.debug("Getting review by ID: {}", reviewId);
        
        Optional<Review> review = reviewRepository.findById(reviewId);
        return review.map(reviewMapperUtil::toDto);
    }

    /**
     * Get reviews with filters
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDto> getReviewsWithFilters(
            Long userId, Long productId, Long parentReviewId, Long rootReviewId,
            Byte rating, Integer minCountLike, Boolean isVisible, Boolean isDeleted,
            String searchText, int page, int size, String sortBy, String sortDir) {
        
        logger.debug("Getting reviews with filters - userId: {}, productId: {}, rating: {}, searchText: {}", 
                    userId, productId, rating, searchText);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Review> reviewPage = reviewRepository.findReviewsWithFilters(
            userId, productId, parentReviewId, rootReviewId, 
            rating, minCountLike, isVisible, isDeleted, searchText, pageable);
            
        return reviewPage.map(reviewMapperUtil::toDto);
    }

    /**
     * Get reviews by product ID
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDto> getReviewsByProductId(Long productId, int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting reviews for product ID: {}", productId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Review> reviewPage = reviewRepository.findByProduct_ProductId(productId, pageable);
        return reviewPage.map(reviewMapperUtil::toDto);
    }

    /**
     * Get reviews by user ID
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDto> getReviewsByUserId(Long userId, int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting reviews for user ID: {}", userId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Review> reviewPage = reviewRepository.findByUser_UserId(userId, pageable);
        return reviewPage.map(reviewMapperUtil::toDto);
    }

    /**
     * Get top-level reviews for a product (no parent)
     */
    @Override
    @Transactional(readOnly = true) 
    public Page<ReviewDto> getTopLevelReviewsByProductId(Long productId, int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting top-level reviews for product ID: {}", productId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Review> reviewPage = reviewRepository.findByProduct_ProductIdAndParentReviewIsNull(productId, pageable);
        return reviewPage.map(reviewMapperUtil::toDto);
    }

    /**
     * Get reply reviews for a parent review
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDto> getReplyReviews(Long parentReviewId, int page, int size) {
        logger.debug("Getting reply reviews for parent review ID: {}", parentReviewId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findByParentReview_ReviewIdOrderByCreatedDtAsc(parentReviewId, pageable);
        return reviewPage.map(reviewMapperUtil::toDto);
    }

    /**
     * Get recent reviews for a product
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> getRecentReviewsByProductId(Long productId) {
        logger.debug("Getting recent reviews for product ID: {}", productId);
        
        List<Review> reviews = reviewRepository.findTop10ByProduct_ProductIdAndIsDeletedFalseAndIsVisibleTrueOrderByCreatedDtDesc(productId);
        return reviews.stream()
                     .map(reviewMapperUtil::toDto)
                     .collect(Collectors.toList());
    }

    /**
     * Get review statistics for a product
     */
    @Override
    @Transactional(readOnly = true)
    public ReviewService.ReviewStatisticsDto getReviewStatistics(Long productId) {
        logger.debug("Getting review statistics for product ID: {}", productId);
        
        Long totalReviews = reviewRepository.countByProduct_ProductId(productId);
        Double averageRating = reviewRepository.findAverageRatingByProductId(productId);
        
        return ReviewService.ReviewStatisticsDto.builder()
                .productId(productId)
                .totalReviews(totalReviews)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .build();
    }

    // =============================================
    // CRUD OPERATIONS FOR ADMIN
    // =============================================

    /**
     * Create new review (Customer & Admin access)
     */
    @Override
    public ReviewDto createReview(ReviewRequestDto requestDto) {
        logger.debug("Creating new review for product ID: {} by user ID: {}", 
                    requestDto.getProductId(), requestDto.getUserId());

        // Find related entities
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + requestDto.getProductId()));

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + requestDto.getUserId()));

        Review parentReview = null;
        Review rootReview = null;
        int reviewLevel = 0;

        // Handle parent review relationship
        if (requestDto.getParentReviewId() != null) {
            parentReview = reviewRepository.findById(requestDto.getParentReviewId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent review not found with ID: " + requestDto.getParentReviewId()));
            
            // Set root review and calculate level
            rootReview = parentReview.getRootReview() != null ? parentReview.getRootReview() : parentReview;
            reviewLevel = parentReview.getReviewLevel() + 1;
        }

        // Create review entity
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setParentReview(parentReview);
        review.setRootReview(rootReview);
        review.setReviewLevel(reviewLevel);
        review.setRating(requestDto.getRating().byteValue());
        review.setReviewContent(requestDto.getReviewContent());
        review.setCountLike(0);
        review.setIsVisible(requestDto.getEffectiveIsVisible());
        review.setIsDeleted(false);
        review.setCreatedDt(LocalDateTime.now());
        review.setModifiedDt(LocalDateTime.now());

        // Save review
        Review savedReview = reviewRepository.save(review);
        
        logger.info("Successfully created review with ID: {}", savedReview.getReviewId());
        return reviewMapperUtil.toDto(savedReview);
    }

    /**
     * Update existing review (Admin only)
     */
    @Override
    public ReviewDto updateReview(Long reviewId, ReviewRequestDto requestDto) {
        logger.debug("Updating review with ID: {}", reviewId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));

        // Update modifiable fields
        review.setRating(requestDto.getRating().byteValue());
        review.setReviewContent(requestDto.getReviewContent());
        review.setIsVisible(requestDto.getEffectiveIsVisible());
        review.setModifiedDt(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);
        
        logger.info("Successfully updated review with ID: {}", reviewId);
        return reviewMapperUtil.toDto(updatedReview);
    }

    /**
     * Update review visibility status (Admin only)
     */
    @Override
    public ReviewDto updateVisibilityStatus(Long reviewId, boolean isVisible) {
        logger.debug("Updating visibility status for review ID: {} to: {}", reviewId, isVisible);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));

        review.setIsVisible(isVisible);
        review.setModifiedDt(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);
        
        logger.info("Successfully updated visibility status for review ID: {} to: {}", reviewId, isVisible);
        return reviewMapperUtil.toDto(updatedReview);
    }

    /**
     * Soft delete review (Admin only)
     */
    @Override
    public ReviewDto softDeleteReview(Long reviewId) {
        logger.debug("Soft deleting review with ID: {}", reviewId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));

        review.setIsDeleted(true);
        review.setModifiedDt(LocalDateTime.now());

        Review deletedReview = reviewRepository.save(review);
        
        logger.info("Successfully soft deleted review with ID: {}", reviewId);
        return reviewMapperUtil.toDto(deletedReview);
    }
}