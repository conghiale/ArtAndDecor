package org.artanddecor.controllers;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.BaseResponseDto;
import org.artanddecor.dto.WishlistDto;
import org.artanddecor.dto.WishlistRequest;
import org.artanddecor.services.WishlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;

/**
 * Wishlist Management REST Controller
 * Provides APIs for managing user wishlists with support for both authenticated and anonymous users
 */
@RestController
@RequestMapping("/wishlists")
@RequiredArgsConstructor
@Tag(name = "Wishlist Management", description = "APIs for managing user wishlists including adding, viewing, and removing products")
public class WishlistController {

    private static final Logger logger = LoggerFactory.getLogger(WishlistController.class);

    private final WishlistService wishlistService;

    /**
     * Get wishlist items with optional filtering and pagination
     * Supports filtering by userId, sessionId, or productId - all parameters optional
     */
    @Operation(
        summary = "Get wishlist items with filtering", 
        description = "Retrieve wishlist items with optional filtering by userId (authenticated users), sessionId (anonymous users), or productId. All filters are optional. Supports pagination."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Wishlist items retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<BaseResponseDto<Page<WishlistDto>>> getWishlistItems(
        @Parameter(description = "Filter by user ID (for authenticated users)", example = "1")
        @RequestParam(name = "userId", required = false) Long userId,
        @Parameter(description = "Filter by session ID (for anonymous users)")
        @RequestParam(name = "sessionId", required = false) String sessionId,
        @Parameter(description = "Filter by specific product ID", example = "5")
        @RequestParam(name = "productId", required = false) Long productId,
        @PageableDefault(size = 10, sort = "createdDt") Pageable pageable) {
        
        logger.info("Getting wishlist items - userId: {}, sessionId: {}, productId: {}, page: {}", 
                   userId, sessionId != null ? "***" : null, productId, pageable.getPageNumber());
        
        try {
            Page<WishlistDto> results = wishlistService.findWishlistByCriteria(userId, sessionId, productId, pageable);
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Found %d wishlist item(s) - Page %d of %d", 
                                 results.getTotalElements(), results.getNumber() + 1, results.getTotalPages()),
                    results));
        } catch (Exception e) {
            logger.error("Error getting wishlist items: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve wishlist items: " + e.getMessage()));
        }
    }

    /**
     * Add product to wishlist
     * Handles both authenticated and anonymous users
     */
    @Operation(
        summary = "Add product to wishlist", 
        description = "Add a product to user's wishlist. Supports both authenticated users (via userId) and anonymous users (via sessionId). Prevents duplicate entries."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product added to wishlist successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data or product already in wishlist",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Product or user not found",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<BaseResponseDto<WishlistDto>> addToWishlist(
        @Parameter(description = "Wishlist request with product and user/session information", required = true)
        @Valid @RequestBody WishlistRequest request) {
        
        logger.info("Adding product {} to wishlist", request.getProductId());
        
        try {
            WishlistDto created = wishlistService.addToWishlist(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.success(
                    "Product added to wishlist successfully",
                    created));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for adding to wishlist: {}", e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    e.getMessage()));
        } catch (Exception e) {
            logger.error("Error adding product to wishlist: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to add product to wishlist: " + e.getMessage()));
        }
    }

    /**
     * Remove product from wishlist by wishlist ID
     * Hard delete operation
     */
    @Operation(
        summary = "Remove product from wishlist", 
        description = "Remove a specific item from wishlist using wishlist ID. Performs hard delete operation."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product removed from wishlist successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Wishlist item not found",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "System error occurred",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<BaseResponseDto<Void>> removeFromWishlist(
        @Parameter(description = "Wishlist item ID to remove", example = "1", required = true)
        @PathVariable Long wishlistId) {
        
        logger.info("Removing wishlist item with ID: {}", wishlistId);
        
        try {
            wishlistService.removeFromWishlist(wishlistId);
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Product removed from wishlist successfully",
                    null));
        } catch (RuntimeException e) {
            logger.warn("Wishlist item not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.error(
                    404, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error removing product from wishlist: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to remove product from wishlist: " + e.getMessage()));
        }
    }
}