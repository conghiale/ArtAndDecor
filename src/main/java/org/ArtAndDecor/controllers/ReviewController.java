package org.ArtAndDecor.controllers;

import org.ArtAndDecor.dto.BaseResponseDto;
import org.ArtAndDecor.dto.ProductReviewLikeDto;
import org.ArtAndDecor.dto.ReviewDto;
import org.ArtAndDecor.services.ProductReviewLikeService;
import org.ArtAndDecor.services.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

/**
 * Review Management REST Controller
 * Comprehensive API for managing product reviews and review likes
 * Supports full CRUD operations with advanced filtering, pagination and security
 */
@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "*")
@Tag(name = "Review Management", description = "Comprehensive APIs for managing product reviews and review likes with role-based access control")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductReviewLikeService productReviewLikeService;

    // =============================================
    // REVIEW API ENDPOINTS
    // =============================================

    /**
     * Get all reviews with comprehensive filtering and pagination
     */
    @GetMapping
    @Operation(
        summary = "Get all product reviews with advanced filtering",
        description = "Retrieve a paginated list of product reviews with comprehensive filtering options including user, product, rating, content search, and visibility filters. Public endpoint accessible to all users.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully with pagination info",
                    content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters or pagination settings"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving reviews")
    })
    public ResponseEntity<BaseResponseDto<Page<ReviewDto>>> getAllReviews(
            @Parameter(description = "Filter by user ID who wrote the review", example = "123") 
            @RequestParam(value = "userId", required = false) Long userId,
            
            @Parameter(description = "Filter by product ID being reviewed", example = "456")
            @RequestParam(value = "productId", required = false) Long productId,
            
            @Parameter(description = "Filter by parent review ID (for reply reviews)", example = "789") 
            @RequestParam(value = "parentReviewId", required = false) Long parentReviewId,
            
            @Parameter(description = "Filter by root review ID (top-level review in thread)", example = "101")
            @RequestParam(value = "rootReviewId", required = false) Long rootReviewId,
            
            @Parameter(description = "Filter by star rating (1-5 stars)", example = "5")
            @RequestParam(value = "rating", required = false) Byte rating,
            
            @Parameter(description = "Filter reviews with minimum number of likes", example = "10")
            @RequestParam(value = "minCountLike", required = false) Integer minCountLike,
            
            @Parameter(description = "Filter by visibility status", example = "true")
            @RequestParam(value = "isVisible", required = false) Boolean isVisible,
            
            @Parameter(description = "Filter by deletion status", example = "false")
            @RequestParam(value = "isDeleted", required = false) Boolean isDeleted,
            
            @Parameter(description = "Search text in review content (case-insensitive)", example = "excellent product")
            @RequestParam(value = "searchText", required = false) String searchText,
            
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size (1-100)", example = "10")
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size,
            
            @Parameter(description = "Sort field name", example = "createdDt")
            @RequestParam(value = "sortBy", defaultValue = "createdDt") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        try {
            logger.info("GET /api/reviews - Getting reviews with filters: userId={}, productId={}, rating={}, searchText={}", 
                       userId, productId, rating, searchText);

            Page<ReviewDto> reviews;
            
            // Check if any filters are provided
            boolean hasFilters = userId != null || productId != null || parentReviewId != null || 
                               rootReviewId != null || rating != null || minCountLike != null ||
                               isVisible != null || isDeleted != null || searchText != null;
            
            if (hasFilters) {
                reviews = reviewService.getReviewsWithFilters(
                    userId, productId, parentReviewId, rootReviewId,
                    rating, minCountLike, isVisible, isDeleted, searchText,
                    page, size, sortBy, sortDir);
            } else {
                reviews = reviewService.getAllReviews(page, size, sortBy, sortDir);
            }

            BaseResponseDto<Page<ReviewDto>> response = BaseResponseDto.success(
                "Reviews retrieved successfully", reviews);
            
            logger.info("Successfully retrieved {} reviews", reviews.getTotalElements());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting reviews: {}", e.getMessage(), e);
            BaseResponseDto<Page<ReviewDto>> response = BaseResponseDto.serverError(
                "Failed to retrieve reviews: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get a specific review by its unique ID
     */
    @GetMapping("/{reviewId}")
    @Operation(
        summary = "Get review by database ID", 
        description = "Retrieve detailed information about a specific review using its unique database identifier. Public endpoint accessible to all users.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ReviewDto.class))),
        @ApiResponse(responseCode = "404", description = "Review not found with the specified ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving review")
    })
    public ResponseEntity<BaseResponseDto<ReviewDto>> getReviewById(
            @Parameter(description = "Unique review identifier (positive integer)", example = "123", required = true)
            @PathVariable Long reviewId) {
        try {
            logger.info("GET /api/reviews/{} - Getting review by ID", reviewId);

            Optional<ReviewDto> review = reviewService.getReviewById(reviewId);
            
            if (review.isPresent()) {
                BaseResponseDto<ReviewDto> response = BaseResponseDto.success(
                    "Review retrieved successfully", review.get());
                return ResponseEntity.ok(response);
            } else {
                BaseResponseDto<ReviewDto> response = BaseResponseDto.notFound(
                    "Review not found with ID: " + reviewId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error getting review by ID {}: {}", reviewId, e.getMessage(), e);
            BaseResponseDto<ReviewDto> response = BaseResponseDto.serverError(
                "Failed to retrieve review: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all reviews for a specific product with pagination
     */
    @GetMapping("/product/{productId}")
    @Operation(
        summary = "Get all reviews for a specific product",
        description = "Retrieve all reviews associated with a specific product. This endpoint supports pagination and sorting. Most commonly used for displaying product review sections. Public endpoint accessible to all users.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product reviews retrieved successfully with pagination info"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving product reviews")
    })
    public ResponseEntity<BaseResponseDto<Page<ReviewDto>>> getReviewsByProductId(
            @Parameter(description = "Product identifier to get reviews for", example = "456", required = true)
            @PathVariable Long productId,
            
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size (1-100)", example = "10") 
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size,
            
            @Parameter(description = "Sort field name", example = "createdDt")
            @RequestParam(value = "sortBy", defaultValue = "createdDt") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        try {
            logger.info("GET /api/reviews/product/{} - Getting reviews by product ID", productId);

            Page<ReviewDto> reviews = reviewService.getReviewsByProductId(productId, page, size, sortBy, sortDir);

            BaseResponseDto<Page<ReviewDto>> response = BaseResponseDto.success(
                "Product reviews retrieved successfully", reviews);
            
            logger.info("Successfully retrieved {} reviews for product {}", reviews.getTotalElements(), productId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting reviews for product {}: {}", productId, e.getMessage(), e);
            BaseResponseDto<Page<ReviewDto>> response = BaseResponseDto.serverError(
                "Failed to retrieve product reviews: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get top-level reviews for a product (no parent review)
     */
    @GetMapping("/product/{productId}/top-level")
    @Operation(
        summary = "Get top-level reviews for a product",
        description = "Retrieve only the main reviews for a product (excludes reply reviews). Perfect for showing primary product reviews without the conversation threads. Public endpoint accessible to all users.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top-level reviews retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving top-level reviews")
    })
    public ResponseEntity<BaseResponseDto<Page<ReviewDto>>> getTopLevelReviewsByProductId(
            @Parameter(description = "Product identifier to get top-level reviews for", example = "456", required = true)
            @PathVariable Long productId,
            
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size (1-100)", example = "10")
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size,
            
            @Parameter(description = "Sort field name", example = "createdDt")
            @RequestParam(value = "sortBy", defaultValue = "createdDt") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        try {
            logger.info("GET /api/reviews/product/{}/top-level - Getting top-level reviews", productId);

            Page<ReviewDto> reviews = reviewService.getTopLevelReviewsByProductId(productId, page, size, sortBy, sortDir);

            BaseResponseDto<Page<ReviewDto>> response = BaseResponseDto.success(
                "Top-level reviews retrieved successfully", reviews);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting top-level reviews for product {}: {}", productId, e.getMessage(), e);
            BaseResponseDto<Page<ReviewDto>> response = BaseResponseDto.serverError(
                "Failed to retrieve top-level reviews: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get reply reviews for a parent review
     */
    @GetMapping("/{parentReviewId}/replies")
    @Operation(
        summary = "Get reply reviews for a parent review",
        description = "Retrieve all reply reviews (comments) for a specific parent review. Used to build conversation threads under main reviews. Public endpoint accessible to all users.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reply reviews retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Parent review not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving reply reviews")
    })
    public ResponseEntity<BaseResponseDto<Page<ReviewDto>>> getReplyReviews(
            @Parameter(description = "Parent review identifier to get replies for", example = "123", required = true)
            @PathVariable Long parentReviewId,
            
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size (1-100)", example = "10")
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {
        
        try {
            logger.info("GET /api/reviews/{}/replies - Getting reply reviews", parentReviewId);

            Page<ReviewDto> reviews = reviewService.getReplyReviews(parentReviewId, page, size);

            BaseResponseDto<Page<ReviewDto>> response = BaseResponseDto.success(
                "Reply reviews retrieved successfully", reviews);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting replies for review {}: {}", parentReviewId, e.getMessage(), e);
            BaseResponseDto<Page<ReviewDto>> response = BaseResponseDto.serverError(
                "Failed to retrieve reply reviews: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get recent reviews for a product
     */
    @GetMapping("/product/{productId}/recent")
    @Operation(
        summary = "Get recent reviews for a product",
        description = "Retrieve the 10 most recent reviews for a product. Perfect for showing latest customer feedback. Returns a list without pagination. Public endpoint accessible to all users.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent reviews retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving recent reviews")
    })
    public ResponseEntity<BaseResponseDto<List<ReviewDto>>> getRecentReviewsByProductId(
            @Parameter(description = "Product identifier to get recent reviews for", example = "456", required = true)
            @PathVariable Long productId) {
        try {
            logger.info("GET /api/reviews/product/{}/recent - Getting recent reviews", productId);

            List<ReviewDto> reviews = reviewService.getRecentReviewsByProductId(productId);

            BaseResponseDto<List<ReviewDto>> response = BaseResponseDto.success(
                "Recent reviews retrieved successfully", reviews);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting recent reviews for product {}: {}", productId, e.getMessage(), e);
            BaseResponseDto<List<ReviewDto>> response = BaseResponseDto.serverError(
                "Failed to retrieve recent reviews: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get review statistics for a product
     */
    @GetMapping("/product/{productId}/statistics")
    @Operation(
        summary = "Get review statistics for a product",
        description = "Retrieve comprehensive review statistics for a product including total count and average rating. Essential for product overview and rating displays. Public endpoint accessible to all users.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review statistics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while calculating review statistics")
    })
    public ResponseEntity<BaseResponseDto<ReviewService.ReviewStatisticsDto>> getReviewStatistics(
            @Parameter(description = "Product identifier to get statistics for", example = "456", required = true)
            @PathVariable Long productId) {
        try {
            logger.info("GET /api/reviews/product/{}/statistics - Getting review statistics", productId);

            ReviewService.ReviewStatisticsDto statistics = reviewService.getReviewStatistics(productId);

            BaseResponseDto<ReviewService.ReviewStatisticsDto> response = BaseResponseDto.success(
                "Review statistics retrieved successfully", statistics);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting review statistics for product {}: {}", productId, e.getMessage(), e);
            BaseResponseDto<ReviewService.ReviewStatisticsDto> response = BaseResponseDto.serverError(
                "Failed to retrieve review statistics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // =============================================
    // PRODUCT REVIEW LIKE API ENDPOINTS
    // =============================================

    /**
     * Get all product review likes with pagination and optional filters
     */
    @GetMapping("/likes")
    @Operation(
        summary = "Get all product review likes with filtering",
        description = "Retrieve a paginated list of product review likes with optional filtering by user and review. Useful for admin analytics and user activity tracking. Requires authentication.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product review likes retrieved successfully with pagination info"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters or pagination settings"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving likes")
    })
    public ResponseEntity<BaseResponseDto<Page<ProductReviewLikeDto>>> getAllProductReviewLikes(
            @Parameter(description = "Filter by user ID who liked reviews", example = "123")
            @RequestParam(value = "userId", required = false) Long userId,
            
            @Parameter(description = "Filter by specific review ID", example = "456")
            @RequestParam(value = "reviewId", required = false) Long reviewId,
            
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size (1-100)", example = "10")
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size,
            
            @Parameter(description = "Sort field name", example = "createdDt")
            @RequestParam(value = "sortBy", defaultValue = "createdDt") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        try {
            logger.info("GET /api/reviews/likes - Getting product review likes with filters: userId={}, reviewId={}", 
                       userId, reviewId);

            Page<ProductReviewLikeDto> likes;
            
            // Check if any filters are provided
            if (userId != null || reviewId != null) {
                likes = productReviewLikeService.getProductReviewLikesWithFilters(
                    userId, reviewId, page, size, sortBy, sortDir);
            } else {
                likes = productReviewLikeService.getAllProductReviewLikes(page, size, sortBy, sortDir);
            }

            BaseResponseDto<Page<ProductReviewLikeDto>> response = BaseResponseDto.success(
                "Product review likes retrieved successfully", likes);
            
            logger.info("Successfully retrieved {} product review likes", likes.getTotalElements());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting product review likes: {}", e.getMessage(), e);
            BaseResponseDto<Page<ProductReviewLikeDto>> response = BaseResponseDto.serverError(
                "Failed to retrieve product review likes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get product review like by unique ID
     */
    @GetMapping("/likes/{likeId}")
    @Operation(
        summary = "Get product review like by ID",
        description = "Retrieve detailed information about a specific review like using its unique identifier. Requires authentication.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product review like retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Product review like not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving like")
    })
    public ResponseEntity<BaseResponseDto<ProductReviewLikeDto>> getProductReviewLikeById(
            @Parameter(description = "Unique like identifier", example = "123", required = true)
            @PathVariable Long likeId) {
        try {
            logger.info("GET /api/reviews/likes/{} - Getting product review like by ID", likeId);

            Optional<ProductReviewLikeDto> like = productReviewLikeService.getProductReviewLikeById(likeId);
            
            if (like.isPresent()) {
                BaseResponseDto<ProductReviewLikeDto> response = BaseResponseDto.success(
                    "Product review like retrieved successfully", like.get());
                return ResponseEntity.ok(response);
            } else {
                BaseResponseDto<ProductReviewLikeDto> response = BaseResponseDto.notFound(
                    "Product review like not found with ID: " + likeId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error getting product review like by ID {}: {}", likeId, e.getMessage(), e);
            BaseResponseDto<ProductReviewLikeDto> response = BaseResponseDto.serverError(
                "Failed to retrieve product review like: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get likes by review ID
     */
    @GetMapping("/{reviewId}/likes")
    @Operation(
        summary = "Get all likes for a specific review",
        description = "Retrieve all users who liked a specific review with pagination. Public endpoint accessible to all users.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review likes retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Review not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving likes")
    })
    public ResponseEntity<BaseResponseDto<Page<ProductReviewLikeDto>>> getProductReviewLikesByReviewId(
            @Parameter(description = "Review identifier to get likes for", example = "456", required = true)
            @PathVariable Long reviewId,
            
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size (1-100)", example = "10")
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size,
            
            @Parameter(description = "Sort field name", example = "createdDt")
            @RequestParam(value = "sortBy", defaultValue = "createdDt") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        try {
            logger.info("GET /api/reviews/{}/likes - Getting likes by review ID", reviewId);

            Page<ProductReviewLikeDto> likes = productReviewLikeService.getProductReviewLikesByReviewId(
                reviewId, page, size, sortBy, sortDir);

            BaseResponseDto<Page<ProductReviewLikeDto>> response = BaseResponseDto.success(
                "Review likes retrieved successfully", likes);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting likes for review {}: {}", reviewId, e.getMessage(), e);
            BaseResponseDto<Page<ProductReviewLikeDto>> response = BaseResponseDto.serverError(
                "Failed to retrieve review likes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get likes count for a review
     */
    @GetMapping("/{reviewId}/likes/count")
    @Operation(
        summary = "Get total likes count for a review",
        description = "Retrieve the total number of likes for a specific review. Essential for displaying like counts in UI. Public endpoint accessible to all users.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Likes count retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Review not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while counting likes")
    })
    public ResponseEntity<BaseResponseDto<Long>> getLikesCountByReviewId(
            @Parameter(description = "Review identifier to count likes for", example = "456", required = true)
            @PathVariable Long reviewId) {
        try {
            logger.info("GET /api/reviews/{}/likes/count - Getting likes count", reviewId);

            Long count = productReviewLikeService.getLikesCountByReviewId(reviewId);

            BaseResponseDto<Long> response = BaseResponseDto.success(
                "Likes count retrieved successfully", count);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting likes count for review {}: {}", reviewId, e.getMessage(), e);
            BaseResponseDto<Long> response = BaseResponseDto.serverError(
                "Failed to retrieve likes count: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Check if user has liked a review
     */
    @GetMapping("/{reviewId}/likes/user/{userId}/exists")
    @Operation(
        summary = "Check if user has liked a specific review",
        description = "Verify whether a specific user has already liked a review. Essential for showing correct like button states in UI. Public endpoint accessible to all users.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User like status retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Review or user not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while checking like status")
    })
    public ResponseEntity<BaseResponseDto<Boolean>> hasUserLikedReview(
            @Parameter(description = "Review identifier to check", example = "456", required = true)
            @PathVariable Long reviewId,
            
            @Parameter(description = "User identifier to check", example = "123", required = true)
            @PathVariable Long userId) {
        try {
            logger.info("GET /api/reviews/{}/likes/user/{}/exists - Checking if user liked review", reviewId, userId);

            boolean hasLiked = productReviewLikeService.hasUserLikedReview(userId, reviewId);

            BaseResponseDto<Boolean> response = BaseResponseDto.success(
                "User like status retrieved successfully", hasLiked);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error checking if user {} liked review {}: {}", userId, reviewId, e.getMessage(), e);
            BaseResponseDto<Boolean> response = BaseResponseDto.serverError(
                "Failed to check user like status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get likes by user for a specific product
     */
    @GetMapping("/likes/user/{userId}/product/{productId}")
    @Operation(
        summary = "Get user's likes for a specific product's reviews",
        description = "Retrieve all review likes made by a specific user for reviews of a specific product. Useful for user activity tracking and personalization. Requires authentication.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User product likes retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "User or product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving user likes")
    })
    public ResponseEntity<BaseResponseDto<Page<ProductReviewLikeDto>>> getProductReviewLikesByUserAndProduct(
            @Parameter(description = "User identifier to get likes from", example = "123", required = true)
            @PathVariable Long userId,
            
            @Parameter(description = "Product identifier to filter reviews by", example = "456", required = true)
            @PathVariable Long productId,
            
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size (1-100)", example = "10")
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size,
            
            @Parameter(description = "Sort field name", example = "createdDt")
            @RequestParam(value = "sortBy", defaultValue = "createdDt") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        try {
            logger.info("GET /api/reviews/likes/user/{}/product/{} - Getting likes by user and product", userId, productId);

            Page<ProductReviewLikeDto> likes = productReviewLikeService.getProductReviewLikesByUserIdAndProductId(
                userId, productId, page, size, sortBy, sortDir);

            BaseResponseDto<Page<ProductReviewLikeDto>> response = BaseResponseDto.success(
                "User product likes retrieved successfully", likes);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting likes for user {} and product {}: {}", userId, productId, e.getMessage(), e);
            BaseResponseDto<Page<ProductReviewLikeDto>> response = BaseResponseDto.serverError(
                "Failed to retrieve user product likes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}