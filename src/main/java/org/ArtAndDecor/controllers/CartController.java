package org.ArtAndDecor.controllers;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.BaseResponseDto;
import org.ArtAndDecor.dto.CartDto;
import org.ArtAndDecor.dto.CartStateDto;
import org.ArtAndDecor.dto.CartItemDto;
import org.ArtAndDecor.dto.CartItemStateDto;
import org.ArtAndDecor.services.CartService;
import org.ArtAndDecor.services.CartStateService;
import org.ArtAndDecor.services.CartItemService;
import org.ArtAndDecor.services.CartItemStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for Cart Management
 * Provides endpoints for shopping cart operations including carts, cart states, cart items, and cart item states
 */
@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Cart Management", description = "APIs for managing shopping carts, cart states, cart items, and cart item states")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;
    private final CartStateService cartStateService;
    private final CartItemService cartItemService;
    private final CartItemStateService cartItemStateService;

    // =============================================
    // CART APIS
    // =============================================

    @Operation(
        summary = "Get carts by criteria with flexible filtering",
        description = "Retrieves carts with flexible filtering and pagination. Admin access required. If no parameters provided, returns all carts.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carts retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<Page<CartDto>>> getCartsByCriteria(
            @Parameter(description = "Filter by cart ID", example = "1")
            @RequestParam(required = false) Long cartId,
            
            @Parameter(description = "Filter by user ID", example = "1")
            @RequestParam(required = false) Long userId,
            
            @Parameter(description = "Filter by session ID", example = "session123")
            @RequestParam(required = false) String sessionId,
            
            @Parameter(description = "Filter by cart state ID", example = "1")
            @RequestParam(required = false) Long cartStateId,
            
            @Parameter(description = "Filter by cart slug", example = "cart-abc123")
            @RequestParam(required = false) String cartSlug,
            
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

        logger.info("GET /carts - criteria filters applied");

        try {
            Page<CartDto> carts = cartService.getCartsByCriteria(cartId, userId, sessionId, 
                cartStateId, cartSlug, cartEnabled, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(BaseResponseDto.success("Carts retrieved successfully", carts));
        } catch (Exception e) {
            logger.error("Error retrieving carts by criteria: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve carts: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Get cart by ID",
        description = "Retrieves detailed information about a specific cart by its ID. User can access their own carts or admin can access any cart.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Cart not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{cartId}")
    @PreAuthorize("hasRole('ADMIN') or @cartService.getCartById(#cartId).user.userId == authentication.principal.userId")
    public ResponseEntity<BaseResponseDto<CartDto>> getCartById(
            @Parameter(description = "Cart ID", required = true, example = "1")
            @PathVariable Long cartId) {

        logger.info("GET /carts/{}", cartId);

        try {
            CartDto cart = cartService.getCartById(cartId);
            if (cart != null) {
                return ResponseEntity.ok(BaseResponseDto.success("Cart retrieved successfully", cart));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound("Cart not found with ID: " + cartId));
            }
        } catch (Exception e) {
            logger.error("Error retrieving cart by ID {}: {}", cartId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve cart: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Get cart by slug",
        description = "Retrieves cart information using its unique slug identifier. Public access for active carts.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/slug/{cartSlug}")
    public ResponseEntity<BaseResponseDto<CartDto>> getCartBySlug(
            @Parameter(description = "Cart slug", required = true, example = "cart-abc123")
            @PathVariable String cartSlug) {

        logger.info("GET /carts/slug/{}", cartSlug);

        try {
            CartDto cart = cartService.getCartBySlug(cartSlug);
            if (cart != null) {
                return ResponseEntity.ok(BaseResponseDto.success("Cart retrieved successfully", cart));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound("Cart not found with slug: " + cartSlug));
            }
        } catch (Exception e) {
            logger.error("Error retrieving cart by slug {}: {}", cartSlug, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve cart: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Get user's active cart",
        description = "Retrieves the active cart for a specific user. User can access their own active cart.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    public ResponseEntity<BaseResponseDto<CartDto>> getActiveCartByUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long userId) {

        logger.info("GET /carts/user/{}/active", userId);

        try {
            CartDto cart = cartService.getActiveCartByUser(userId);
            if (cart != null) {
                return ResponseEntity.ok(BaseResponseDto.success("Active cart retrieved successfully", cart));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound("No active cart found for user: " + userId));
            }
        } catch (Exception e) {
            logger.error("Error retrieving active cart for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve active cart: " + e.getMessage()));
        }
    }

    // =============================================
    // CART STATE APIS
    // =============================================

    @Operation(
        summary = "Get cart states by criteria with flexible filtering",
        description = "Retrieves cart states with flexible filtering. No pagination. If no parameters provided, returns all cart states."
    )
    @GetMapping("/states")
    public ResponseEntity<BaseResponseDto<List<CartStateDto>>> getCartStatesByCriteria(
            @Parameter(description = "Filter by cart state ID", example = "1")
            @RequestParam(required = false) Long cartStateId,
            
            @Parameter(description = "Filter by cart state name", example = "ACTIVE")
            @RequestParam(required = false) String cartStateName,
            
            @Parameter(description = "Filter by enabled status", example = "true")
            @RequestParam(required = false) Boolean cartStateEnabled,
            
            @Parameter(description = "Text search in name, display name, and remark", example = "active")
            @RequestParam(required = false) String textSearch) {

        logger.info("GET /carts/states - criteria filters applied");

        try {
            List<CartStateDto> cartStates = cartStateService.getCartStatesByCriteria(
                cartStateId, cartStateName, cartStateEnabled, textSearch);
            return ResponseEntity.ok(BaseResponseDto.success("Cart states retrieved successfully", cartStates));
        } catch (Exception e) {
            logger.error("Error retrieving cart states by criteria: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve cart states: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Get cart state by ID",
        description = "Retrieves a specific cart state by its ID. Admin access required.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/states/{cartStateId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<CartStateDto>> getCartStateById(@PathVariable Long cartStateId) {

        logger.info("GET /carts/states/{}", cartStateId);

        try {
            CartStateDto cartState = cartStateService.getCartStateById(cartStateId);
            if (cartState != null) {
                return ResponseEntity.ok(BaseResponseDto.success("Cart state retrieved successfully", cartState));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound("Cart state not found with ID: " + cartStateId));
            }
        } catch (Exception e) {
            logger.error("Error retrieving cart state by ID {}: {}", cartStateId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve cart state: " + e.getMessage()));
        }
    }

    // =============================================
    // CART ITEM APIS
    // =============================================

    @Operation(
        summary = "Get cart items by criteria with flexible filtering",
        description = "Retrieves cart items with flexible filtering and pagination. Admin access required. If no parameters provided, returns all cart items.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cart items retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/items")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<Page<CartItemDto>>> getCartItemsByCriteria(
            @Parameter(description = "Filter by cart item ID", example = "1")
            @RequestParam(required = false) Long cartItemId,
            
            @Parameter(description = "Filter by cart ID", example = "1")
            @RequestParam(required = false) Long cartId,
            
            @Parameter(description = "Filter by product ID", example = "1")
            @RequestParam(required = false) Long productId,
            
            @Parameter(description = "Filter by user ID", example = "1")
            @RequestParam(required = false) Long userId,
            
            @Parameter(description = "Filter by min price", example = "10.00")
            @RequestParam(required = false) BigDecimal minPrice,
            
            @Parameter(description = "Filter by max price", example = "100.00")
            @RequestParam(required = false) BigDecimal maxPrice,
            
            @Parameter(description = "Filter by min quantity", example = "1")
            @RequestParam(required = false) Integer minQuantity,
            
            @Parameter(description = "Filter by max quantity", example = "10")
            @RequestParam(required = false) Integer maxQuantity,
            
            @Parameter(description = "Filter by cart item state ID", example = "1")
            @RequestParam(required = false) Long cartItemStateId,
            
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort field", example = "createdDt")
            @RequestParam(defaultValue = "createdDt") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        logger.info("GET /carts/items - criteria filters applied");

        try {
            Page<CartItemDto> cartItems = cartItemService.getCartItemsByCriteria(cartItemId, cartId, 
                productId, userId, minPrice, maxPrice, minQuantity, maxQuantity, 
                cartItemStateId, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(BaseResponseDto.success("Cart items retrieved successfully", cartItems));
        } catch (Exception e) {
            logger.error("Error retrieving cart items by criteria: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve cart items: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Get cart items by cart ID",
        description = "Retrieves all items in a specific cart. User can access items in their own carts.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{cartId}/items")
    @PreAuthorize("hasRole('ADMIN') or @cartService.getCartById(#cartId).user.userId == authentication.principal.userId")
    public ResponseEntity<BaseResponseDto<List<CartItemDto>>> getCartItemsByCartId(
            @PathVariable Long cartId) {

        logger.info("GET /carts/{}/items", cartId);

        try {
            List<CartItemDto> cartItems = cartItemService.getCartItemsByCartId(cartId);
            return ResponseEntity.ok(BaseResponseDto.success("Cart items retrieved successfully", cartItems));
        } catch (Exception e) {
            logger.error("Error retrieving cart items for cart {}: {}", cartId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve cart items: " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Get active cart items by cart ID",
        description = "Retrieves active cart items for a specific cart. For CUSTOMER role usage.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{cartId}/items/active")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<List<CartItemDto>>> getActiveCartItemsByCartId(
            @PathVariable Long cartId) {

        logger.info("GET /carts/{}/items/active", cartId);

        try {
            List<CartItemDto> cartItems = cartItemService.getActiveCartItemsByCartId(cartId);
            return ResponseEntity.ok(BaseResponseDto.success("Active cart items retrieved successfully", cartItems));
        } catch (Exception e) {
            logger.error("Error retrieving active cart items for cart {}: {}", cartId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve active cart items: " + e.getMessage()));
        }
    }

    // =============================================
    // CART ITEM STATE APIS  
    // =============================================

    @Operation(
        summary = "Get cart item states by criteria with flexible filtering",
        description = "Retrieves cart item states with flexible filtering. No pagination. If no parameters provided, returns all cart item states."
    )
    @GetMapping("/item-states")
    public ResponseEntity<BaseResponseDto<List<CartItemStateDto>>> getCartItemStatesByCriteria(
            @Parameter(description = "Filter by cart item state ID", example = "1")
            @RequestParam(required = false) Long cartItemStateId,
            
            @Parameter(description = "Filter by cart item state name", example = "ACTIVE")
            @RequestParam(required = false) String cartItemStateName,
            
            @Parameter(description = "Filter by enabled status", example = "true")
            @RequestParam(required = false) Boolean cartItemStateEnabled,
            
            @Parameter(description = "Text search in name, display name, and remark", example = "active")
            @RequestParam(required = false) String textSearch) {

        logger.info("GET /carts/item-states - criteria filters applied");

        try {
            List<CartItemStateDto> cartItemStates = cartItemStateService.getCartItemStatesByCriteria(
                cartItemStateId, cartItemStateName, cartItemStateEnabled, textSearch);
            return ResponseEntity.ok(BaseResponseDto.success("Cart item states retrieved successfully", cartItemStates));
        } catch (Exception e) {
            logger.error("Error retrieving cart item states by criteria: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve cart item states: " + e.getMessage()));
        }
    }
}