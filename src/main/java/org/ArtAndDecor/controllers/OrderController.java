package org.ArtAndDecor.controllers;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.*;
import org.ArtAndDecor.services.*;
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
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order Management REST Controller - Refactored to Specification
 * 15 essential APIs for real e-commerce platform
 * Version: 7.0 - According to ORDER MANAGEMENT API SPEC
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Order Management APIs - 15 essential endpoints per specification")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final OrderStateService orderStateService;
    private final OrderStateHistoryService orderStateHistoryService;
    private final DiscountService discountService;

    // ===== CUSTOMER APIs (4 endpoints) =====

    /**
     * API 1: Checkout Cart → Create Order
     * Role: CUSTOMER - Customer creates order from existing shopping cart
     * Business Flow: Cart validation → Product inventory check → Discount application → Shipping calculation → Order creation → Cart clearing
     */
    @PostMapping("/orders/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
        summary = "Checkout cart to create order",
        description = "Customer checkout their shopping cart to create a new order. This process validates cart items, applies discounts, calculates shipping, and creates the order with all necessary details.",
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
            description = "Invalid request - Cart not found, insufficient inventory, invalid discount code, or missing required fields",
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
                description = "Checkout request containing cart ID, shipping address, payment method, and optional discount code",
                required = true,
                example = "{\"cartId\": 123, \"shippingAddressId\": 456, \"paymentMethod\": \"CREDIT_CARD\", \"discountCode\": \"WELCOME2026\"}"
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
                    request.getDiscountCode()
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
     * Business Flow: User validation → Apply filters (state, date range) → Pagination → Return order list
     */
    @GetMapping("/orders/my-orders")
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
            @Parameter(
                description = "Filter by order state (optional). Available states: NEW, CONFIRMED, PROCESSING, SHIPPING, DELIVERED, COMPLETED, CANCELLED",
                example = "NEW"
            )
            @RequestParam(required = false) String state,
            
            @Parameter(
                description = "Filter orders from this date (optional). Format: YYYY-MM-DD",
                example = "2026-01-01"
            )
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            
            @Parameter(
                description = "Filter orders to this date (optional). Format: YYYY-MM-DD",
                example = "2026-03-08"
            )
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            
            @Parameter(
                description = "Pagination parameters. Default: page=0, size=10, sort by createdDt DESC"
            )
            @PageableDefault(size = 10, sort = "createdDt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        
        try {
            UserDto user = (UserDto) authentication.getPrincipal();
            Long userId = user.getUserId();
            
            logger.info("Get my orders request from user: {}", userId);
            
            Page<OrderDto> orders = orderService.getMyOrders(userId, state, fromDate, toDate, pageable);
            
            return ResponseEntity.ok(BaseResponseDto.success("Orders retrieved successfully", orders));
        } catch (Exception e) {
            logger.error("Error getting customer orders: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get orders: " + e.getMessage()));
        }
    }

    /**
     * API 3: Get My Order Detail
     * Role: CUSTOMER - Customer views detailed information about their specific order
     * Business Flow: Ownership validation → Retrieve order details with items → Return complete order information
     */
    @GetMapping("/orders/my-orders/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
        summary = "Get customer's order detail",
        description = "Customer retrieves detailed information about a specific order including order items, pricing, shipping details, and current status. Customer can only access their own orders.",
        tags = {"Customer Order Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order detail retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found or access denied - Customer can only access their own orders"
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
    public ResponseEntity<BaseResponseDto<OrderDto>> getMyOrderDetail(
            @Parameter(
                description = "Order ID to retrieve details for. Customer can only access their own orders.",
                required = true,
                example = "123"
            )
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
     * Role: CUSTOMER - Customer cancels their order if it's in a cancellable state
     * Business Flow: Ownership validation → State validation → Update order state → Create audit history
     * Allowed States: Orders can only be cancelled when in NEW or CONFIRMED states
     */
    @PutMapping("/orders/my-orders/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
        summary = "Cancel customer's order",
        description = "Customer cancels their order if it's in a cancellable state (NEW or CONFIRMED). Once an order is PROCESSING, SHIPPING, DELIVERED, or COMPLETED, it cannot be cancelled by the customer.",
        tags = {"Customer Order Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order cancelled successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Cannot cancel order - Order is not in cancellable state (only NEW or CONFIRMED orders can be cancelled)"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found or access denied - Customer can only cancel their own orders"
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
    public ResponseEntity<BaseResponseDto<OrderDto>> cancelMyOrder(
            @Parameter(
                description = "Order ID to cancel. Customer can only cancel their own orders in NEW or CONFIRMED states.",
                required = true,
                example = "123"
            )
            @PathVariable Long orderId,
            Authentication authentication) {
        
        try {
            UserDto user = (UserDto) authentication.getPrincipal();
            Long userId = user.getUserId();
            
            logger.info("Cancel order request from user: {}, orderId: {}", userId, orderId);
            
            OrderDto canceledOrder = orderService.cancelMyOrder(userId, orderId);
            
            return ResponseEntity.ok(BaseResponseDto.success("Order canceled successfully", canceledOrder));
        } catch (Exception e) {
            logger.error("Error canceling order: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to cancel order: " + e.getMessage()));
        }
    }

    // ===== ADMIN ORDER MANAGEMENT APIs (4 endpoints) =====

    /**
     * API 5: Admin Search Orders
     * Role: ADMIN/MANAGER - Admin searches and views all orders with advanced filtering
     * Business Flow: Multi-criteria filtering → Pagination → Return comprehensive order list
     */
    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Admin search orders with advanced filters",
        description = "Admin and Managers can search through all orders with comprehensive filtering options including order ID, customer, state, date range, and amount range. Supports pagination for large datasets.",
        tags = {"Admin Order Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Orders retrieved successfully with applied filters",
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
            description = "Access denied - ADMIN or MANAGER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Page<OrderDto>>> adminSearchOrders(
            @Parameter(
                description = "Filter by specific order ID (optional)",
                example = "123"
            )
            @RequestParam(required = false) Long orderId,
            
            @Parameter(
                description = "Filter by customer ID (optional)",
                example = "456"
            )
            @RequestParam(required = false) Long customerId,
            
            @Parameter(
                description = "Filter by order state (optional). Available: NEW, CONFIRMED, PROCESSING, SHIPPING, DELIVERED, COMPLETED, CANCELLED",
                example = "NEW"
            )
            @RequestParam(required = false) String state,
            
            @Parameter(
                description = "Filter orders from this date (optional). Format: YYYY-MM-DD",
                example = "2026-01-01"
            )
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            
            @Parameter(
                description = "Filter orders to this date (optional). Format: YYYY-MM-DD",
                example = "2026-03-08"
            )
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            
            @Parameter(
                description = "Filter by minimum order amount in VND (optional)",
                example = "100000"
            )
            @RequestParam(required = false) BigDecimal minAmount,
            
            @Parameter(
                description = "Filter by maximum order amount in VND (optional)",
                example = "5000000"
            )
            @RequestParam(required = false) BigDecimal maxAmount,
            
            @Parameter(
                description = "Pagination parameters. Default: page=0, size=20, sort by createdDt DESC"
            )
            @PageableDefault(size = 20, sort = "createdDt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        
        try {
            logger.info("Admin search orders request with filters");
            
            Page<OrderDto> orders = orderService.adminSearchOrders(
                    orderId, customerId, state, fromDate, toDate, minAmount, maxAmount, pageable);
            
            return ResponseEntity.ok(BaseResponseDto.success("Orders retrieved successfully", orders));
        } catch (Exception e) {
            logger.error("Error searching orders: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to search orders: " + e.getMessage()));
        }
    }

    /**
     * API 6: Admin Get Order Detail
     * Role: ADMIN/MANAGER - Admin views detailed information about any order in the system
     * Business Flow: Direct order retrieval with full details including customer info and order history
     */
    @GetMapping("/admin/orders/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Admin get any order detail",
        description = "Admin and Managers can view detailed information about any order in the system including complete order details, customer information, order items, and payment status.",
        tags = {"Admin Order Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order detail retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found with the specified ID"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required - Valid JWT token required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - ADMIN or MANAGER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<OrderDto>> adminGetOrderDetail(
            @Parameter(
                description = "Order ID to retrieve details for. Admin can access any order in the system.",
                required = true,
                example = "123"
            )
            @PathVariable Long orderId,
            Authentication authentication) {
        
        try {
            logger.info("Admin get order detail request for orderId: {}", orderId);
            
            OrderDto order = orderService.getOrderById(orderId);
            
            return ResponseEntity.ok(BaseResponseDto.success("Order detail retrieved successfully", order));
        } catch (Exception e) {
            logger.error("Error getting order detail: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get order detail: " + e.getMessage()));
        }
    }

    /**
     * API 7: Admin Create Order
     * Role: ADMIN/MANAGER - Admin creates order manually on behalf of customers
     * Business Flow: Customer validation → Product validation → Inventory check → Discount application → Order creation
     * Use Cases: Phone orders, Facebook orders, Manual orders, Chat orders
     */
    @PostMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Admin create order on behalf of customer",
        description = "Admin and Managers can manually create orders for customers. This is useful for phone orders, social media orders, or any manual order entry. The system validates customer, products, inventory, and calculates totals automatically.",
        tags = {"Admin Order Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Order created successfully by admin",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request - Customer not found, product not available, insufficient inventory, or invalid discount code"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required - Valid JWT token required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - ADMIN or MANAGER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<OrderDto>> adminCreateOrder(
            @Parameter(
                description = "Admin order creation request containing customer ID, shipping address, order items, and optional discount code",
                required = true,
                example = "{\"customerId\": 123, \"shippingAddressId\": 456, \"discountCode\": \"ADMIN2026\", \"orderItems\": [{\"productId\": 789, \"quantity\": 2}]}"
            )
            @Valid @RequestBody AdminCreateOrderRequest request,
            Authentication authentication) {
        
        try {
            UserDto user = (UserDto) authentication.getPrincipal();
            Long createdByUserId = user.getUserId();
            
            logger.info("Admin create order request for customer: {}", request.getCustomerId());
            
            OrderDto newOrder = orderService.adminCreateOrder(
                    request.getCustomerId(),
                    request.getShippingAddressId(),
                    request.getDiscountCode(),
                    request.getOrderItems(),
                    createdByUserId
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
     * API 8: Update Order State
     * Role: ADMIN/MANAGER - Admin updates order state with business rules validation
     * Business Flow: State transition validation → Order update → Audit history creation
     * Allowed Transitions: NEW→CONFIRMED→PROCESSING→SHIPPING→DELIVERED→COMPLETED, ANY→CANCELLED
     */
    @PutMapping("/admin/orders/{orderId}/state")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Update order state with validation",
        description = "Admin and Managers can update order states following business rules. Valid transitions: NEW→CONFIRMED→PROCESSING→SHIPPING→DELIVERED→COMPLETED. Orders can be CANCELLED from any state. All state changes are logged for audit purposes.",
        tags = {"Admin Order Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order state updated successfully with audit trail",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid state transition - The requested state change is not allowed according to business rules"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found with the specified ID"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required - Valid JWT token required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - ADMIN or MANAGER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<OrderDto>> updateOrderState(
            @Parameter(
                description = "Order ID to update state for",
                required = true,
                example = "123"
            )
            @PathVariable Long orderId,
            
            @Parameter(
                description = "Request containing new order state ID and optional remarks",
                required = true,
                example = "{\"newState\": 2, \"remarks\": \"Order confirmed and ready for processing\"}"
            )
            @RequestBody @Valid UpdateOrderStateRequest request,
            Authentication authentication) {
        
        try {
            UserDto user = (UserDto) authentication.getPrincipal();
            Long changedByUserId = user.getUserId();
            
            logger.info("Update order state request for orderId: {}, newState: {}", orderId, request.getNewState());
            
            OrderDto updatedOrder = orderService.updateOrderState(orderId, request.getNewState(), changedByUserId);
            
            return ResponseEntity.ok(BaseResponseDto.success("Order state updated successfully", updatedOrder));
        } catch (Exception e) {
            logger.error("Error updating order state: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to update order state: " + e.getMessage()));
        }
    }

    // ===== ADMIN MANAGEMENT APIs (2 endpoints) =====

    /**
     * API 9: Get Order State History
     * Role: ADMIN/MANAGER/CUSTOMER(own orders) - View order state change audit trail
     * Business Flow: Permission validation → Retrieve complete state change history with timestamps and user info
     * Access Control: Customers can only view their own order history, Admin/Manager can view any order history
     */
    @GetMapping("/orders/{orderId}/history")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or (hasRole('CUSTOMER') and @orderService.isOrderOwner(#orderId, authentication.principal.userId))")
    @Operation(
        summary = "Get order state change history",
        description = "Retrieve complete audit trail of order state changes including timestamps, old/new states, and user who made the change. Customers can only view their own order history, while Admin/Manager can view any order history.",
        tags = {"Order Management", "Audit Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order state history retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found or access denied - Customer can only view their own order history"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required - Valid JWT token required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Insufficient permissions to view this order history"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<List<OrderStateHistoryDto>>> getOrderStateHistory(
            @Parameter(
                description = "Order ID to retrieve state history for. Access control: Customer can only view their own orders.",
                required = true,
                example = "123"
            )
            @PathVariable Long orderId,
            Authentication authentication) {
        try {
            logger.info("Get order state history request for orderId: {}", orderId);
            
            List<OrderStateHistoryDto> history = orderStateHistoryService.getOrderStateHistory(orderId);
            
            return ResponseEntity.ok(BaseResponseDto.success("Order state history retrieved successfully", history));
        } catch (Exception e) {
            logger.error("Error getting order state history: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get order state history: " + e.getMessage()));
        }
    }

    /**
     * API 10: Get Order States
     * Role: ADMIN/MANAGER - Retrieve all available order states for system management
     * Business Flow: Master data retrieval for order state management and UI dropdown population
     */
    @GetMapping("/admin/order-states")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Get all order states for management",
        description = "Admin and Managers retrieve all available order states in the system. Used for order management UI, dropdown population, and state transition validation. Can filter by enabled status.",
        tags = {"Admin Order Management", "Master Data"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order states retrieved successfully",
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
            description = "Access denied - ADMIN or MANAGER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<List<OrderStateDto>>> getOrderStates(
            @Parameter(
                description = "Filter by state code (optional). Used for specific state lookup",
                example = "NEW"
            )
            @RequestParam(required = false) String stateCode,
            
            @Parameter(
                description = "Filter by enabled status (optional). Set to true to get only active states for UI dropdowns",
                example = "true"
            )
            @RequestParam(required = false) Boolean enabled,
            Authentication authentication) {
        
        try {
            logger.info("Get order states request");
            
            List<OrderStateDto> orderStates;
            if (enabled != null && enabled) {
                orderStates = orderStateService.getAllEnabledOrderStates();
            } else {
                orderStates = orderStateService.getAllOrderStates();
            }
            
            return ResponseEntity.ok(BaseResponseDto.success("Order states retrieved successfully", orderStates));
        } catch (Exception e) {
            logger.error("Error getting order states: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get order states: " + e.getMessage()));
        }
    }

    // ===== DISCOUNT APIs (3 endpoints) =====

    /**
     * API 11: Validate Discount Code
     * Role: PUBLIC - Anyone can validate discount codes before checkout
     * Business Flow: Code validation → Eligibility check → Amount calculation → Usage verification
     * Purpose: Frontend validation before checkout to show discount amount and eligibility
     */
    @PostMapping("/discounts/validate")
    @Operation(
        summary = "Validate discount code before checkout",
        description = "Public endpoint to validate discount codes before checkout. Checks if discount code exists, is active, within date range, hasn't exceeded usage limits, and meets minimum order requirements. Returns calculated discount amount.",
        tags = {"Discount Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Discount code validation completed (may be valid or invalid)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request format or missing required parameters"
        )
    })
    public ResponseEntity<BaseResponseDto<DiscountValidationResult>> validateDiscountCode(
            @Parameter(
                description = "Discount validation request containing code, cart amount, and product IDs for validation",
                required = true,
                example = "{\"code\": \"WELCOME2026\", \"cartAmount\": 500000, \"productIds\": [1, 2, 3]}"
            )
            @Valid @RequestBody ValidateDiscountRequest request) {
        
        try {
            logger.info("Validate discount code request: {}", request.getCode());
            
            DiscountValidationResult result = discountService.validateDiscountCode(
                    request.getCode(),
                    request.getCartAmount(),
                    request.getProductIds()
            );
            
            return ResponseEntity.ok(BaseResponseDto.success("Discount validation completed", result));
        } catch (Exception e) {
            logger.error("Error validating discount code: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to validate discount code: " + e.getMessage()));
        }
    }

    /**
     * API 12: Get Discounts
     * Role: ADMIN/MANAGER - Admin views all discounts with comprehensive filtering
     * Business Flow: Apply filters → Retrieve discount list → Return with usage statistics
     */
    @GetMapping("/admin/discounts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Admin get all discounts with filters",
        description = "Admin and Managers retrieve all discounts in the system with comprehensive filtering options including code, active status, expiration status, discount type, and date range. Includes usage statistics and effectiveness data.",
        tags = {"Admin Discount Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Discounts retrieved successfully with applied filters",
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
            description = "Access denied - ADMIN or MANAGER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<List<DiscountDto>>> getDiscounts(
            @Parameter(
                description = "Filter by discount code (partial match, optional)",
                example = "WELCOME"
            )
            @RequestParam(required = false) String code,
            
            @Parameter(
                description = "Filter by active status (optional). True for currently active discounts",
                example = "true"
            )
            @RequestParam(required = false) Boolean active,
            
            @Parameter(
                description = "Filter by expiration status (optional). True for expired discounts",
                example = "false"
            )
            @RequestParam(required = false) Boolean expired,
            
            @Parameter(
                description = "Filter by discount type (optional). Available: PERCENTAGE, FIXED_AMOUNT",
                example = "PERCENTAGE"
            )
            @RequestParam(required = false) String discountType,
            
            @Parameter(
                description = "Filter discounts created from this date (optional). Format: YYYY-MM-DD",
                example = "2026-01-01"
            )
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            
            @Parameter(
                description = "Filter discounts created to this date (optional). Format: YYYY-MM-DD",
                example = "2026-03-08"
            )
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Authentication authentication) {
        
        try {
            logger.info("Get discounts request with filters");
            
            List<DiscountDto> discounts = discountService.getDiscountsWithFilters(
                    code, active, expired, discountType, fromDate, toDate);
            
            return ResponseEntity.ok(BaseResponseDto.success("Discounts retrieved successfully", discounts));
        } catch (Exception e) {
            logger.error("Error getting discounts: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get discounts: " + e.getMessage()));
        }
    }

    /**
     * API 13: Create Discount
     * Role: ADMIN/MANAGER - Admin creates new discount campaigns with business rules validation
     * Business Flow: Validation (code uniqueness, date ranges, amounts) → Creation → Return created discount
     */
    @PostMapping("/admin/discounts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Admin create new discount campaign",
        description = "Admin and Managers create new discount campaigns with comprehensive validation including code uniqueness, valid date ranges, appropriate discount values, and usage limits. Supports both percentage and fixed amount discounts.",
        tags = {"Admin Discount Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Discount created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid discount data - Duplicate code, invalid date range, or invalid discount values"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required - Valid JWT token required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - ADMIN or MANAGER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<DiscountDto>> createDiscount(
            @Parameter(
                description = "Complete discount information including code, name, type, value, date range, and usage limits",
                required = true,
                example = "{\"discountCode\": \"SPRING2026\", \"discountName\": \"Spring Sale 2026\", \"discountType\": {\"discountTypeId\": 1}, \"discountValue\": 15.0, \"startAt\": \"2026-03-01T00:00:00\", \"endAt\": \"2026-04-30T23:59:59\", \"usageLimit\": 100, \"minOrderAmount\": 200000, \"enabled\": true}"
            )
            @Valid @RequestBody DiscountDto request,
            Authentication authentication) {
        
        try {
            logger.info("Create discount request: {}", request.getDiscountCode());
            
            DiscountDto newDiscount = discountService.createDiscount(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Discount created successfully", newDiscount));
        } catch (Exception e) {
            logger.error("Error creating discount: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to create discount: " + e.getMessage()));
        }
    }

    /**
     * API 14: Update Discount
     * Role: ADMIN/MANAGER - Admin updates existing discount campaigns with validation
     * Business Flow: Existence check → Validation → Update → Return updated discount
     * Note: Discounts are not deleted to preserve order history integrity
     */
    @PutMapping("/admin/discounts/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Admin update existing discount campaign",
        description = "Admin and Managers update existing discount campaigns. Validates business rules including date ranges, discount values, and usage limits. Discounts are never deleted to preserve order history integrity, but can be disabled.",
        tags = {"Admin Discount Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Discount updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid discount data - Invalid date range, discount values, or business rule violations"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Discount not found with the specified ID"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required - Valid JWT token required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - ADMIN or MANAGER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<DiscountDto>> updateDiscount(
            @Parameter(
                description = "Discount ID to update",
                required = true,
                example = "123"
            )
            @PathVariable Long id,
            
            @Parameter(
                description = "Updated discount information. All fields are validated against business rules.",
                required = true,
                example = "{\"discountName\": \"Updated Spring Sale 2026\", \"discountValue\": 20.0, \"endAt\": \"2026-05-31T23:59:59\", \"enabled\": false}"
            )
            @Valid @RequestBody DiscountDto request,
            Authentication authentication) {
        
        try {
            logger.info("Update discount request for id: {}", id);
            
            DiscountDto updatedDiscount = discountService.updateDiscount(id, request);
            
            return ResponseEntity.ok(BaseResponseDto.success("Discount updated successfully", updatedDiscount));
        } catch (Exception e) {
            logger.error("Error updating discount: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to update discount: " + e.getMessage()));
        }
    }

    /**
     * API 15: Get Discount Types
     * Role: ADMIN/MANAGER - Retrieve all available discount types for system management
     * Business Flow: Master data retrieval for discount type management and UI dropdown population
     * Available Types: PERCENTAGE (e.g., 15% off), FIXED_AMOUNT (e.g., 50,000 VND off)
     */
    @GetMapping("/admin/discount-types")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Get all discount types for management",
        description = "Admin and Managers retrieve all available discount types in the system. Used for discount creation/editing UI, dropdown population, and validation. Includes both percentage-based and fixed-amount discount types.",
        tags = {"Admin Discount Management", "Master Data"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Discount types retrieved successfully",
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
            description = "Access denied - ADMIN or MANAGER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<List<DiscountTypeDto>>> getDiscountTypes(
            Authentication authentication) {
        
        try {
            logger.info("Get discount types request");
            
            List<DiscountTypeDto> discountTypes = discountService.getAllDiscountTypes();
            
            return ResponseEntity.ok(BaseResponseDto.success("Discount types retrieved successfully", discountTypes));
        } catch (Exception e) {
            logger.error("Error getting discount types: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Failed to get discount types: " + e.getMessage()));
        }
    }
}
