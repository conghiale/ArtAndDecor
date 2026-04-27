package org.artanddecor.controllers;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.*;
import org.artanddecor.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import java.time.LocalDate;
import java.util.List;

/**
 * Order Management REST Controller - Refactored Version 8.0
 * Core APIs for Order Management System
 * Business focus: Order State, Order History, Order Items, Orders
 * DISCOUNT APIs removed as per requirements
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Order Management APIs - Essential endpoints for Order system")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final OrderStateService orderStateService;
    private final OrderStateHistoryService orderStateHistoryService;
    private final OrderItemService orderItemService;

    // ===== CUSTOMER ORDER APIs =====

    /**
     * NEW API: Preview Order Checkout
     * Role: permitAll - Preview order without authentication for guest users
     * Business Flow: Client sends cartId + selected cart item IDs → Validate cart exists → Validate items belong to cart → Calculate shipping & discount → Return preview without creating order
     * SECURITY: cartId required to ensure all selected items belong to the same cart and prevent unauthorized access
     */
    @PostMapping("/preview")
    @Operation(
        summary = "Preview order checkout calculation for selected cart items",
        description = "Preview order totals, shipping, and discount for specific cart items from a validated cart. Includes security validation to ensure all selected items belong to the specified cart.",
        tags = {"Customer Order Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Order preview calculated successfully for selected items",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request - Cart not found, selected items not found, or items don't belong to specified cart"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Security violation - Selected items don't belong to specified cart"
        )
    })
    public ResponseEntity<BaseResponseDto<PreviewOrderResponse>> previewOrder(
            @Parameter(
                description = "Preview request containing cart ID and selected cart item IDs for validation and preview",
                required = true,
                example = "{\"cartId\": 123, \"selectedCartItemIds\": [1, 2, 3]}"
            )
            @Valid @RequestBody PreviewOrderRequest request) {
        
        try {
            logger.info("Preview order request for cart {} with {} selected items", 
                       request.getCartId(),
                       request.getSelectedCartItemIds() != null ? request.getSelectedCartItemIds().size() : 0);
            
            PreviewOrderResponse preview = orderService.previewOrder(request);
            
            return ResponseEntity.ok(BaseResponseDto.success("Order preview calculated successfully", preview));
        } catch (SecurityException e) {
            logger.error("Security violation in order preview: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(BaseResponseDto.badRequest("Security violation: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error previewing order: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to preview order: " + e.getMessage()));
        }
    }

    /**
     * NEW API: Create Order from Selected Cart Items
     * Role: permitAll - Both authenticated users and guest users can create orders
     * Business Flow: Validate selection → Create order with customer/receiver info → Remove cart items
     * IMPORTANT: userId parameter ensures Order.USER_ID is properly set for order ownership tracking
     */
    @PostMapping("/create")
    @Operation(
        summary = "Create order from selected cart items",
        description = "Create a new order using selected cart items with complete order information. The userId parameter ensures the Order record has proper USER_ID set to identify order ownership.",
        tags = {"Customer Order Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Order created successfully from selected cart items",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request - Missing required information or cart items not found"
        )
    })
    public ResponseEntity<BaseResponseDto<OrderDto>> createOrderFromSelectedItems(
            @Parameter(
                description = "Order creation request with selected cart items and complete order details (cartId required)",
                required = true
            )
            @Valid @RequestBody CheckoutCartRequest request) {
        
        try {
            logger.info("Creating order from selected cart items for cartId: {}", request.getCartId());
            
            // Get userId from cartId for Order.USER_ID assignment
            Long userId = orderService.getUserIdFromCart(request.getCartId());
            logger.info("Retrieved userId: {} for Order.USER_ID assignment", userId != null ? userId : "NULL (Guest Order)");

            // Create order using CheckoutCartRequest with userId for ownership tracking
            OrderDto createdOrder = orderService.checkoutSelectedCartItems(request, userId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Order created successfully from selected cart items", createdOrder));
        } catch (Exception e) {
            logger.error("Failed to create order from selected cart items: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to create order: " + e.getMessage()));
        }
    }

    /**
     * NEW API: Update Order Status
     * Role: ADMIN - Update order status with special handling for DELIVERED status
     * Business Flow: Update order status → Create history → Handle special cases (DELIVERED affects shipment)
     */
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update order status",
        description = "Update order status with automatic shipment status handling. When order status is set to DELIVERED, associated shipments are automatically marked as DELIVERED. Only ADMIN role can update order status.",
        tags = {"Admin Order Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Order status updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request - Order or status not found"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Access denied - ADMIN role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<OrderDto>> updateOrderStatus(
            @Parameter(description = "Order ID", required = true, example = "123")
            @PathVariable Long orderId,
            
            @Parameter(
                description = "Status update request with new order status",
                required = true,
                example = "{\"newOrderStateId\": 4, \"statusNote\": \"Order delivered successfully\"}"
            )
            @Valid @RequestBody UpdateOrderStatusRequest request,
            
            @Parameter(description = "User ID of admin making the change", required = true, example = "123")
            @RequestParam Long userId) {
        
        try {
            logger.info("Update order status request - Order: {}, New Status: {}, User: {}", 
                       orderId, request.getNewOrderStateId(), userId);
            
            // Set the user who made the change if not provided
            if (request.getChangedByUserId() == null) {
                request.setChangedByUserId(userId);
            }
            
            OrderDto updatedOrder = orderService.updateOrderStatusWithSpecialHandling(
                    orderId, request.getNewOrderStateId(), request.getChangedByUserId(), request.getStatusNote());
            
            return ResponseEntity.ok(BaseResponseDto.success("Order status updated successfully", updatedOrder));
        } catch (Exception e) {
            logger.error("Error updating order status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to update order status: " + e.getMessage()));
        }
    }

    /**
     * API 1: Checkout Cart → Create Order
     * Role: permitAll - Both ADMIN and GUEST can create orders
     * Business Flow: Cart validation → Product inventory check → Auto-select best shipping & discount → Order creation → Cart clearing
     * CHECKOUT ENTIRE CART - All items in user's cart will be processed
     */
    @PostMapping("/checkout")
    // permitAll - Both ADMIN and GUEST users can checkout
    @Operation(
        summary = "Checkout entire cart to create order",
        description = "Customer checkout their entire cart to create a new order. This process validates all cart items, calculates shipping, and creates the order with complete customer details. ALL ITEMS in the cart will be processed.",
        tags = {"Customer Order Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Order created successfully from entire cart",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request - Cart not found, insufficient inventory, or missing required fields",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request - UserId or sessionId required for guest users"
        )
    })
    public ResponseEntity<BaseResponseDto<OrderDto>> checkoutCart(
            @Parameter(
                description = "Checkout request with complete order details (cartId required for cart validation)",
                required = true,
                example = "{\"customerName\": \"John Doe\", \"paymentMethodId\": 1, \"cartId\": 456}"
            )
            @Valid @RequestBody CheckoutCartRequest request) {
        
        try {
            logger.info("Checkout entire cart request for cartId: {}", request.getCartId());
            
            // Get userId from cartId for Order.USER_ID assignment
            Long userId = orderService.getUserIdFromCart(request.getCartId());
            logger.info("Retrieved userId: {} for Order.USER_ID assignment", userId != null ? userId : "NULL (Guest Order)");

            // Use checkoutEntireCart method - processes ALL cart items
            OrderDto createdOrder = orderService.checkoutEntireCart(request, userId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Order created successfully from entire cart", createdOrder));

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid checkout request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Invalid request: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Failed to create order: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to create order: " + e.getMessage()));
        }
    }

    /**
     * API 2: Get My Orders
     * Role: permitAll - Customer views their own order history with filtering and pagination
     */
    @GetMapping("/my-orders")
    @Operation(
        summary = "Get customer's order history",
        description = "Customer retrieves their own orders with optional filtering by order state and date range. Supports pagination for large result sets.",
        tags = {"Customer Order Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Orders retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        )
    })
    public ResponseEntity<BaseResponseDto<Page<OrderDto>>> getMyOrders(
            @Parameter(description = "Filter by order state name (optional)", example = "PENDING")
            @RequestParam(required = false) String state,
            
            @Parameter(description = "Filter orders from this date (optional). Format: YYYY-MM-DD", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            
            @Parameter(description = "Filter orders to this date (optional). Format: YYYY-MM-DD", example = "2026-03-08")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            
            @Parameter(description = "User ID for logged-in users (required for security)", example = "123")
            @RequestParam Long userId,
            
            @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        try {
            logger.info("Get my orders request from user: {}", userId);
            
            Page<OrderDto> orders = orderService.getMyOrders(userId, state, fromDate, toDate, pageable);
            
            return ResponseEntity.ok(BaseResponseDto.success("Orders retrieved successfully", orders));
        } catch (Exception e) {
            logger.error("Error getting my orders: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get orders: " + e.getMessage()));
        }
    }

    /**
     * API 3: Get My Order Detail
     * Role: permitAll - Customer views specific order details
     */
    @GetMapping("/my-orders/{orderId}")
    @Operation(
        summary = "Get customer's specific order details",
        description = "Customer retrieves detailed information about a specific order including order items and order history.",
        tags = {"Customer Order Management"}
    )
    public ResponseEntity<BaseResponseDto<OrderDto>> getMyOrderDetail(
            @Parameter(description = "Order ID", required = true, example = "123")
            @PathVariable Long orderId,
            
            @Parameter(description = "User ID for logged-in users (required for security)", example = "123")
            @RequestParam Long userId) {
        
        try {
            logger.info("Get my order detail request from user: {}, orderId: {}", userId, orderId);
            
            OrderDto order = orderService.getMyOrderDetail(userId, orderId);
            
            return ResponseEntity.ok(BaseResponseDto.success("Order detail retrieved successfully", order));
        } catch (Exception e) {
            logger.error("Error getting order detail: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get order detail: " + e.getMessage()));
        }
    }

    /**
     * API 4: Cancel My Order
     * Role: permitAll - Customer cancels their own pending order
     */
    @PostMapping("/my-orders/{orderId}/cancel")
    @Operation(
        summary = "Cancel customer's order",
        description = "Customer cancels their own order if it's in a cancellable state (PENDING, PROCESSING). Updates order state and creates history record.",
        tags = {"Customer Order Management"}
    )
    public ResponseEntity<BaseResponseDto<OrderDto>> cancelMyOrder(
            @Parameter(description = "Order ID to cancel", required = true, example = "123")
            @PathVariable Long orderId,
            
            @Parameter(description = "User ID for logged-in users (required for security)", example = "123")
            @RequestParam Long userId) {
        
        try {
            logger.info("Cancel my order request from user: {}, orderId: {}", userId, orderId);
            
            OrderDto cancelledOrder = orderService.cancelMyOrder(userId, orderId);
            
            return ResponseEntity.ok(BaseResponseDto.success("Order cancelled successfully", cancelledOrder));
        } catch (Exception e) {
            logger.error("Error cancelling order: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to cancel order: " + e.getMessage()));
        }
    }

    // ===== ORDER MANAGEMENT APIs =====

    /**
     * API: Get Order by ID (Admin)
     * Role: ADMIN - Get complete order information with all related data
     * Business Analysis: Admin needs full access to any order details for management
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get order by ID (Admin)",
        description = "Admin retrieves complete order information by ID including all order items, customer details, and order history. Full access to any order in the system.",
        tags = {"Admin Order Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Access denied - ADMIN role required"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Order not found"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<OrderDto>> getOrderById(
            @Parameter(description = "Order ID", required = true, example = "123")
            @PathVariable Long orderId) {
        
        try {
            logger.info("Admin get order by ID request: {}", orderId);
            
            OrderDto order = orderService.getOrderById(orderId);
            
            return ResponseEntity.ok(BaseResponseDto.success("Order retrieved successfully", order));
        } catch (IllegalArgumentException e) {
            logger.warn("Order not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponseDto.notFound("Order not found with ID: " + orderId));
        } catch (Exception e) {
            logger.error("Error getting order by ID: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get order: " + e.getMessage()));
        }
    }

    /**
     * API 5: Get Orders
     * Role: permitAll - Get orders with comprehensive filtering and pagination
     * Business Analysis: Both ADMIN and CUSTOMER need to view orders with different access levels
     */
    @GetMapping
    @Operation(
        summary = "Get orders with filtering",
        description = "Retrieve orders with comprehensive filtering options including customer, state, date range, and amount range. Supports pagination.",
        tags = {"Order Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Orders retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        )
    })
    public ResponseEntity<BaseResponseDto<Page<OrderDto>>> getOrders(
            @Parameter(description = "Filter by user ID (optional)", example = "456")
            @RequestParam(required = false) Long userId,
            
            @Parameter(description = "Filter by session ID for guest orders (optional)", example = "guest-session-12345")
            @RequestParam(required = false) String sessionId,
            
            @Parameter(description = "Filter by order code (optional)", example = "ORD-20260115-001")
            @RequestParam(required = false) String orderCode,
            
            @Parameter(description = "Filter by order state name (optional)", example = "PENDING")
            @RequestParam(required = false) String state,
            
            @Parameter(description = "Filter orders from this date (optional). Format: YYYY-MM-DD", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            
            @Parameter(description = "Filter orders to this date (optional). Format: YYYY-MM-DD", example = "2026-03-08")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            
            @Parameter(description = "Filter by minimum order amount (optional)", example = "100000")
            @RequestParam(required = false) BigDecimal minAmount,
            
            @Parameter(description = "Filter by maximum order amount (optional)", example = "1000000")
            @RequestParam(required = false) BigDecimal maxAmount,
            
            @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        try {
            logger.info("Get orders request with filters - userId: {}, sessionId: {}, orderCode: {}", userId, sessionId, orderCode);
            
            Page<OrderDto> orders = orderService.searchOrders(
                    null, userId, sessionId, orderCode, state, fromDate, toDate, minAmount, maxAmount, pageable);
            
            return ResponseEntity.ok(BaseResponseDto.success("Orders retrieved successfully", orders));
        } catch (Exception e) {
            logger.error("Error getting orders: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get orders: " + e.getMessage()));
        }
    }

    // ===== ORDER MANAGEMENT APIs =====

    // ===== ORDER STATE APIs =====

    /**
     * API 8: Get Order States
     * Role: permitAll - Both ADMIN and CUSTOMER can view order states
     * Business Analysis: Filter by ID, name, enabled status with pagination
     */
    @GetMapping("/states")
    @Operation(
        summary = "Get order states with filtering",
        description = "Retrieve order states with optional filtering by ID, name, and enabled status. Used for dropdowns and state management.",
        tags = {"Order State Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order states retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        )
    })
    public ResponseEntity<BaseResponseDto<Page<OrderStateDto>>> getOrderStates(
            @Parameter(description = "Filter by order state ID (optional)", example = "1")
            @RequestParam(required = false) Long orderStateId,
            
            @Parameter(description = "Filter by order state name (optional)", example = "PENDING")
            @RequestParam(required = false) String orderStateName,
            
            @Parameter(description = "Filter by enabled status (optional)", example = "true")
            @RequestParam(required = false) Boolean enabled,
            
            @PageableDefault(page = 0, size = 10, sort = "orderStateName", direction = Sort.Direction.ASC) Pageable pageable) {
        
        try {
            logger.info("Get order states request with filters");
            
            Page<OrderStateDto> orderStates = orderStateService.getOrderStates(
                    orderStateId, orderStateName, enabled, pageable);
            
            return ResponseEntity.ok(BaseResponseDto.success("Order states retrieved successfully", orderStates));
        } catch (Exception e) {
            logger.error("Error getting order states: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get order states: " + e.getMessage()));
        }
    }



    // ===== ORDER STATE HISTORY APIs =====

    /**
     * API 11: Get Order State History
     * Role: permitAll - Both ADMIN and CUSTOMER can view order state history
     * Business Analysis: Filter by order ID, date range, state changes with pagination
     */
    @GetMapping("/state-history")
    @Operation(
        summary = "Get order state history with filtering",
        description = "Retrieve order state change history with optional filtering by order ID, date range, and state transitions. Records are created automatically when order states change.",
        tags = {"Order State History Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order state history retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        )
    })
    public ResponseEntity<BaseResponseDto<Page<OrderStateHistoryDto>>> getOrderStateHistory(
            @Parameter(description = "Filter by order ID (optional)", example = "123")
            @RequestParam(required = false) Long orderId,
            
            @Parameter(description = "Filter state changes from this date (optional). Format: YYYY-MM-DD", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            
            @Parameter(description = "Filter state changes to this date (optional). Format: YYYY-MM-DD", example = "2026-03-08")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            
            @Parameter(description = "Filter by old state ID (optional)", example = "1")
            @RequestParam(required = false) Long oldStateId,
            
            @Parameter(description = "Filter by new state ID (optional)", example = "2")
            @RequestParam(required = false) Long newStateId,
            
            @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        try {
            logger.info("Get order state history request with filters");
            
            Page<OrderStateHistoryDto> history = orderStateHistoryService.getOrderStateHistory(
                    orderId, fromDate, toDate, oldStateId, newStateId, pageable);
            
            return ResponseEntity.ok(BaseResponseDto.success("Order state history retrieved successfully", history));
        } catch (Exception e) {
            logger.error("Error getting order state history: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get order state history: " + e.getMessage()));
        }
    }

    // ===== ORDER ITEM APIs =====

    /**
     * API 12: Get Order Items
     * Role: permitAll - Both ADMIN and CUSTOMER can view order items
     * Business Analysis: Filter by order ID, product ID, quantity range, price range with pagination
     */
    @GetMapping("/items")
    @Operation(
        summary = "Get order items with filtering",
        description = "Retrieve order items with comprehensive filtering options including order ID, product ID, quantity range, and price range. Items are created automatically during order checkout.",
        tags = {"Order Item Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order items retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        )
    })
    public ResponseEntity<BaseResponseDto<Page<OrderItemDto>>> getOrderItems(
            @Parameter(description = "Filter by order IDs (optional)", example = "123,456,789")
            @RequestParam(required = false) List<Long> orderIds,
            
            @Parameter(description = "Filter by product ID (optional)", example = "456")
            @RequestParam(required = false) Long productId,
            
            @Parameter(description = "Filter by minimum quantity (optional)", example = "1")
            @RequestParam(required = false) Integer minQuantity,
            
            @Parameter(description = "Filter by maximum quantity (optional)", example = "10")
            @RequestParam(required = false) Integer maxQuantity,
            
            @Parameter(description = "Filter by minimum unit price (optional)", example = "100000")
            @RequestParam(required = false) BigDecimal minUnitPrice,
            
            @Parameter(description = "Filter by maximum unit price (optional)", example = "1000000")
            @RequestParam(required = false) BigDecimal maxUnitPrice,
            
            @Parameter(description = "Filter by minimum total price (optional)", example = "500000")
            @RequestParam(required = false) BigDecimal minTotalPrice,
            
            @Parameter(description = "Filter by maximum total price (optional)", example = "5000000")
            @RequestParam(required = false) BigDecimal maxTotalPrice,
            
            @Parameter(description = "Search text in product name or code (optional)", example = "sofa")
            @RequestParam(required = false) String textSearch,
            
            @PageableDefault(page = 0, size = 10, sort = "orderItemId", direction = Sort.Direction.DESC) Pageable pageable) {
        
        try {
            logger.info("Get order items request with filters");
            
            Page<OrderItemDto> orderItems = orderItemService.searchOrderItems(
                    orderIds, productId, minQuantity, maxQuantity, minUnitPrice, maxUnitPrice, 
                    minTotalPrice, maxTotalPrice, textSearch, pageable);
            
            return ResponseEntity.ok(BaseResponseDto.success("Order items retrieved successfully", orderItems));
        } catch (Exception e) {
            logger.error("Error getting order items: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get order items: " + e.getMessage()));
        }
    }
}
