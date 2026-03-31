package org.artanddecor.controllers;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.BaseResponseDto;
import org.artanddecor.dto.CartDto;
import org.artanddecor.dto.CartItemDto;
import org.artanddecor.dto.CartItemRequestDto;
import org.artanddecor.services.CartService;
import org.artanddecor.services.CartItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// Removed SecurityContextHolder and PreAuthorize imports - using SecurityConfiguration instead
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for Cart Management
 * Optimized for specific workflow requirements
 */
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Tag(name = "Cart Management", description = "APIs for shopping cart operations")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;
    private final CartItemService cartItemService;

    // =============================================
    // CLIENT APIS - WORKFLOW OPTIMIZED
    // =============================================

    @Operation(
        summary = "Get cart with merge support",
        description = "Get cart with enhanced priority lookup and merge support: cartId (highest) -> merge scenario (userId + sessionId) -> userId -> sessionId (lowest). " +
                     "When both userId and sessionId are provided, guest cart items will be merged into user cart and guest cart will be cleared."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cart retrieved or created successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/current") 
    public ResponseEntity<BaseResponseDto<CartDto>> getCart(
            @Parameter(description = "Cart ID (highest priority)", example = "1")
            @RequestParam(required = false) Long cartId,
            
            @Parameter(description = "User ID for logged in users (medium priority)", example = "1")
            @RequestParam(required = false) Long userId,
            
            @Parameter(description = "Session ID for guest users (lowest priority)")
            @RequestParam(required = false) String sessionId,
            
            @Parameter(description = "Cart item state ID filter (optional, null = all states)", example = "1")
            @RequestParam(required = false) Long cartItemStateId) {
        
        logger.info("GET /carts/current - cartId: {}, userId: {}, sessionId: {}, cartItemStateId: {}", cartId, userId, sessionId, cartItemStateId);

        try {
            // Enhanced logic with merge support: cartId -> merge scenario (userId + sessionId) -> userId -> sessionId
            CartDto cart = cartService.getCartWithMergeSupport(cartId, userId, sessionId);
            
            // Load cart items with state filter
            List<CartItemDto> cartItems = cartItemService.getCartItemsByCartId(cart.getCartId(), cartItemStateId);
            cart.setCartItems(cartItems);
            
            String message = (userId != null && sessionId != null && cartId == null) ? 
                "Cart retrieved with guest cart merged successfully" : "Cart retrieved successfully";
            
            return ResponseEntity.ok(BaseResponseDto.success(message, cart));
            
        } catch (Exception e) {
            logger.error("Error getting cart: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get cart: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Add product to cart",
        description = "Add a product to cart with optional attributes. Supports multiple cart identification methods: direct cartId, userId (finds/creates active cart), or sessionId (guest cart). Handles product attribute selection for comprehensive product configuration."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product added to cart successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid product attributes or cart not found"),
        @ApiResponse(responseCode = "404", description = "Product or attributes not found")
    })
    @PostMapping("/items")
    public ResponseEntity<BaseResponseDto<CartItemDto>> addProductToCart(
            @Valid @RequestBody CartItemRequestDto request) {
        
        logger.info("POST /carts/items - request: {}", request);

        try {
            // Validate the request
            if (!request.isValidForAdd()) {
                return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Invalid request: missing required fields"));
            }

            // Add product to cart (with or without attributes)
            CartItemDto cartItem = cartItemService.addProductToCart(request);
            
            String message = request.hasSelectedAttributes() ? 
                "Product with attributes added to cart successfully" : 
                "Product added to cart successfully";
            
            return ResponseEntity.ok(BaseResponseDto.success(message, cartItem));
            
        } catch (IllegalArgumentException e) {
            logger.error("Validation error adding product to cart: {}", e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Validation error: " + e.getMessage()));
            
        } catch (Exception e) {
            logger.error("Error adding product to cart: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to add product to cart: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Remove cart item",
        description = "Remove an item from cart (set state to REMOVED). Works for both logged-in users and guest sessions."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cart item removed successfully"),
        @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @PutMapping("/items/{cartItemId}/remove")
    public ResponseEntity<BaseResponseDto<CartItemDto>> removeCartItem(
            @Parameter(description = "Cart item ID", required = true)
            @PathVariable Long cartItemId) {
        
        logger.info("PUT /carts/items/{}/remove", cartItemId);

        try {
            CartItemDto cartItem = cartItemService.removeCartItem(cartItemId);
            return ResponseEntity.ok(BaseResponseDto.success("Cart item removed successfully", cartItem));
            
        } catch (Exception e) {
            logger.error("Error removing cart item {}: {}", cartItemId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to remove cart item: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Get cart items count",
        description = "Get count of cart items in a cart. Cart is identified by cartId, userId, or sessionId (in priority order)."
    )
    @GetMapping("/items/count")
    public ResponseEntity<BaseResponseDto<Long>> getCartItemsCount(
            @Parameter(description = "Cart ID (highest priority)", example = "1")
            @RequestParam(required = false) Long cartId,
            
            @Parameter(description = "User ID for logged in users (medium priority)", example = "1")
            @RequestParam(required = false) Long userId,
            
            @Parameter(description = "Session ID for guest users (lowest priority)")
            @RequestParam(required = false) String sessionId,
            
            @Parameter(description = "Cart item state ID filter (optional, defaults to 1=ACTIVE)", example = "1")
            @RequestParam(required = false) Long cartItemStateId) {
        
        logger.info("GET /carts/items/count - cartId: {}, userId: {}, sessionId: {}, cartItemStateId: {}", cartId, userId, sessionId, cartItemStateId);

        try {
            // Default cartItemStateId to 1 (ACTIVE) if null or 0
            if (cartItemStateId == null || cartItemStateId.equals(0L)) {
                cartItemStateId = 1L; // ACTIVE state
            }
            
            // If no parameters provided, return 0
            if (cartId == null && userId == null && sessionId == null) {
                return ResponseEntity.ok(BaseResponseDto.success("Cart items count retrieved", 0L));
            }

            Long count = cartItemService.getCartItemsCount(cartId, userId, sessionId, cartItemStateId);
            return ResponseEntity.ok(BaseResponseDto.success("Cart items count retrieved successfully", count));
            
        } catch (Exception e) {
            logger.error("Error getting cart items count: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get cart items count: " + e.getMessage()));
        }
    }

    // =============================================
    // ADMIN APIS
    // =============================================

    @Operation(
        summary = "Admin: Get carts with filters",
        description = "Get paginated list of carts with flexible filtering. Admin access required.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carts retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping
    public ResponseEntity<BaseResponseDto<Page<CartDto>>> getCarts(
            @Parameter(description = "Filter by cart ID", example = "1")
            @RequestParam(required = false) Long cartId,
            
            @Parameter(description = "Filter by user ID", example = "1")
            @RequestParam(required = false) Long userId,
            
            @Parameter(description = "Filter by session ID", example = "session123")
            @RequestParam(required = false) String sessionId,
            
            @Parameter(description = "Filter by cart state ID", example = "1")
            @RequestParam(required = false) Long cartStateId,
            
            @Parameter(description = "Filter by enabled status", example = "true")
            @RequestParam(required = false) Boolean cartEnabled,
            
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort field", example = "createdDt")
            @RequestParam(defaultValue = "createdDt") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        logger.info("GET /carts - admin filters applied");

        try {
            Page<CartDto> carts = cartService.getCartsByCriteria(cartId, userId, sessionId, 
                cartStateId, null, cartEnabled, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(BaseResponseDto.success("Carts retrieved successfully", carts));
            
        } catch (Exception e) {
            logger.error("Error retrieving carts: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve carts: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Admin: Add product to guest cart",
        description = "Create a new guest cart and add a product with optional attributes. Admin access required.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/admin/guest/items")
    public ResponseEntity<BaseResponseDto<CartItemDto>> addProductToGuestCart(
            @Valid @RequestBody CartItemRequestDto request) {
        
        logger.info("POST /carts/admin/guest/items - request: {}", request);

        try {
            // Validate that essential fields are provided
            if (request.getProductId() == null || request.getQuantity() == null) {
                return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Product ID and quantity are required"));
            }
            
            // Ensure sessionId is provided or generate if not (for guest cart)
            if (request.getSessionId() == null || request.getSessionId().trim().isEmpty()) {
                request.setSessionId(cartService.generateSessionId());
                logger.info("Generated session ID for guest cart: {}", request.getSessionId());
            }

            CartItemDto cartItem = cartItemService.addProductToCart(request);
            return ResponseEntity.ok(BaseResponseDto.success("Product added to guest cart successfully", cartItem));
            
        } catch (Exception e) {
            logger.error("Error adding product to guest cart: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to add product to guest cart: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Update cart item",
        description = "Update cart item details including quantity and attributes. If quantity is 0, item state will be set to REMOVED. Admin access required.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<BaseResponseDto<CartItemDto>> updateCartItem(
            @Parameter(description = "Cart item ID", required = true)
            @PathVariable Long cartItemId,
            
            @Valid @RequestBody CartItemRequestDto request) {
        
        logger.info("PUT /carts/items/{} - update request: {}", cartItemId, request);

        try {
            // Validate request for update operations
            if (!request.isValidForUpdate()) {
                return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Invalid update request"));
            }

            // Use unified service method with quantity and attribute handling
            CartItemDto updatedCartItem = cartItemService.updateCartItem(cartItemId, request);
            
            String message = request.hasSelectedAttributes() ? 
                "Cart item and attributes updated successfully" : 
                "Cart item updated successfully";
                
            return ResponseEntity.ok(BaseResponseDto.success(message, updatedCartItem));
            
        } catch (Exception e) {
            logger.error("Error updating cart item {}: {}", cartItemId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update cart item: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Get cart items",
        description = "Get all items in a cart with optional state filter. Supports both logged-in users and guest sessions. Lookup priority: cartId -> userId -> sessionId"
    )
    @GetMapping("/items")
    public ResponseEntity<BaseResponseDto<List<CartItemDto>>> getCartItems(
            @Parameter(description = "Cart ID (optional, highest priority)")
            @RequestParam(required = false) Long cartId,
            
            @Parameter(description = "User ID for logged in users (optional, medium priority)", example = "1")
            @RequestParam(required = false) Long userId,
            
            @Parameter(description = "Session ID for guest users (optional, lowest priority)")
            @RequestParam(required = false) String sessionId,
            
            @Parameter(description = "Filter by cart item state ID (optional, defaults to 1=ACTIVE)", example = "1")
            @RequestParam(required = false) Long cartItemStateId) {

        logger.info("GET /carts/items - cartId: {}, userId: {}, sessionId: {}, cartItemStateId: {}", cartId, userId, sessionId, cartItemStateId);

        try {
            // Default cartItemStateId to 1 (ACTIVE) if null or 0
            if (cartItemStateId == null || cartItemStateId.equals(0L)) {
                cartItemStateId = 1L; // ACTIVE state
            }
            
            // Priority logic: cartId -> userId -> sessionId
            if (cartId != null) {
                // Direct cart access - validate ownership for logged-in users only
                if (userId != null) {
                    CartDto cartDto = cartService.getCartById(cartId);
                    if (cartDto.getUser() != null && !userId.equals(cartDto.getUser().getUserId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(BaseResponseDto.forbidden("You can only access your own cart items"));
                    }
                }
                List<CartItemDto> cartItems = cartItemService.getCartItemsByCartId(cartId, cartItemStateId);
                return ResponseEntity.ok(BaseResponseDto.success("Cart items retrieved successfully", cartItems));
                
            } else if (userId != null) {
                // Get active cart by user
                CartDto userCart = cartService.getActiveCartByUser(userId);
                List<CartItemDto> cartItems = cartItemService.getCartItemsByCartId(userCart.getCartId(), cartItemStateId);
                return ResponseEntity.ok(BaseResponseDto.success("Cart items retrieved successfully", cartItems));
                
            } else if (sessionId != null) {
                // Get active cart by session
                CartDto sessionCart = cartService.createOrGetActiveCartForSession(sessionId);
                List<CartItemDto> cartItems = cartItemService.getCartItemsByCartId(sessionCart.getCartId(), cartItemStateId);
                return ResponseEntity.ok(BaseResponseDto.success("Cart items retrieved successfully", cartItems));
                
            } else {
                return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("At least one parameter (cartId, userId, or sessionId) is required"));
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving cart items: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve cart items: " + e.getMessage()));
        }
    }

}