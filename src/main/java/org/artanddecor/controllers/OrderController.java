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
     * API 1: Checkout Cart → Create Order
     * Role: permitAll - Both ADMIN and GUEST can create orders
     * Business Flow: Cart validation → Product inventory check → Auto-select best shipping & discount → Order creation → Cart clearing
     */
    @PostMapping("/checkout")
    // permitAll - Both ADMIN and GUEST users can checkout
    @Operation(
        summary = "Checkout cart to create order",
        description = "Customer checkout their shopping cart to create a new order. This process validates cart items, calculates shipping, and creates the order with all necessary details.",
        tags = {"Customer Order Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Order created successfully from cart",
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
            responseCode = "401", 
            description = "Authentication required - Valid JWT token required"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Access denied - CUSTOMER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<OrderDto>> checkoutCart(
            @Parameter(
                description = "Checkout request containing cart ID, shipping address, and payment method",
                required = true,
                example = "{\"cartId\": 123, \"shippingAddressId\": 456, \"paymentMethod\": \"CREDIT_CARD\"}"
            )
            @Valid @RequestBody CheckoutCartRequest request,
            Authentication authentication) {
        
        try {
            UserDto user = (UserDto) authentication.getPrincipal();
            Long userId = user.getUserId();
            
            logger.info("Checkout cart request from user: {}, cartId: {}", userId, request.getCartId());
            
            OrderDto newOrder = orderService.checkoutCart(
                    userId,
                    request.getCartId(),
                    request.getShippingAddressId(),
                    request.getPaymentMethod(),
                    null // discountCode removed
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Order created successfully", newOrder));
        } catch (Exception e) {
            logger.error("Error creating order from cart: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to create order: " + e.getMessage()));
        }
    }

    /**
     * API 2: Get My Orders
     * Role: CUSTOMER - Customer views their own order history with filtering and pagination
     */
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
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
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required - Valid JWT token required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - CUSTOMER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Page<OrderDto>>> getMyOrders(
            @Parameter(description = "Filter by order state name (optional)", example = "PENDING")
            @RequestParam(required = false) String state,
            
            @Parameter(description = "Filter orders from this date (optional). Format: YYYY-MM-DD", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            
            @Parameter(description = "Filter orders to this date (optional). Format: YYYY-MM-DD", example = "2026-03-08")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            
            @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        
        try {
            UserDto user = (UserDto) authentication.getPrincipal();
            Long userId = user.getUserId();
            
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
     * Role: CUSTOMER - Customer views specific order details
     */
    @GetMapping("/my-orders/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
        summary = "Get customer's specific order details",
        description = "Customer retrieves detailed information about a specific order including order items and order history.",
        tags = {"Customer Order Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<OrderDto>> getMyOrderDetail(
            @Parameter(description = "Order ID", required = true, example = "123")
            @PathVariable Long orderId,
            Authentication authentication) {
        
        try {
            UserDto user = (UserDto) authentication.getPrincipal();
            Long userId = user.getUserId();
            
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
     * Role: CUSTOMER - Customer cancels their own pending order
     */
    @PostMapping("/my-orders/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
        summary = "Cancel customer's order",
        description = "Customer cancels their own order if it's in a cancellable state (PENDING, PROCESSING). Updates order state and creates history record.",
        tags = {"Customer Order Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<OrderDto>> cancelMyOrder(
            @Parameter(description = "Order ID to cancel", required = true, example = "123")
            @PathVariable Long orderId,
            Authentication authentication) {
        
        try {
            UserDto user = (UserDto) authentication.getPrincipal();
            Long userId = user.getUserId();
            
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
     * API 5: Get Orders (Admin/Manager)
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
            @Parameter(description = "Filter by customer ID (optional)", example = "456")
            @RequestParam(required = false) Long customerId,
            
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
            
            @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        
        try {
            logger.info("Get orders request with filters");
            
            Page<OrderDto> orders = orderService.searchOrders(
                    null, customerId, state, fromDate, toDate, minAmount, maxAmount, pageable);
            
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
