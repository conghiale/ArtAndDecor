package org.artanddecor.services;

import org.artanddecor.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Orders Service Interface for business logic operations
 * Updated to support new API requirements - DISCOUNT functionality removed
 */
public interface OrderService {

    /**
     * Preview order before checkout - NEW API
     * Calculates shipping and discount based on order subtotal amount only
     * @param request Preview order request with selected cart item IDs
     * @return Preview order response with all calculations
     */
    PreviewOrderResponse previewOrder(PreviewOrderRequest request);

    /**
     * Checkout entire cart to create order - FOR API /checkout
     * Uses cartId from CheckoutCartRequest for cart validation and processing
     * @param request Checkout request with complete order details and cartId
     * @param userId User ID for order ownership tracking (from cart → user relationship)
     * @return Created order
     */
    OrderDto checkoutEntireCart(CheckoutCartRequest request, Long userId);

    /**
     * Checkout selected cart items to create order - FOR API /create  
     * Uses cartId and selectedCartItemIds from CheckoutCartRequest
     * @param request Checkout request with selected items, order details and cartId
     * @param userId User ID for order ownership tracking (from cart → user relationship)
     * @return Created order
     */
    OrderDto checkoutSelectedCartItems(CheckoutCartRequest request, Long userId);

    /**
     * Get order by ID (API 6)
     * @param orderId Order ID
     * @return OrderDto if found
     */
    OrderDto getOrderById(Long orderId);

    /**
     * Get customer's orders with filters (API 2)
     * @param userId Customer ID
     * @param state Order state (optional)
     * @param fromDate From date (optional)
     * @param toDate To date (optional)
     * @param pageable Pagination
     * @return Page of customer orders
     */
    Page<OrderDto> getMyOrders(
            Long userId,
            String state,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable);

    /**
     * Get customer's order detail (API 3)
     * @param userId Customer ID
     * @param orderId Order ID
     * @return Order detail if belongs to customer
     */
    OrderDto getMyOrderDetail(Long userId, Long orderId);

    /**
     * Cancel customer's order (API 4)
     * @param userId Customer ID
     * @param orderId Order ID
     * @return Updated order
     */
    OrderDto cancelMyOrder(Long userId, Long orderId);
    
    /**
     * Search orders with filters (API 5) - Used by both admin and public access
     * @param orderId Filter by order ID (optional)
     * @param userId Filter by user ID (optional)
     * @param sessionId Filter by session ID for guest orders (optional)
     * @param orderCode Filter by order code (optional)  
     * @param state Filter by state (optional)
     * @param fromDate Filter from date (optional)
     * @param toDate Filter to date (optional)
     * @param minAmount Filter by minimum amount (optional)
     * @param maxAmount Filter by maximum amount (optional)
     * @param pageable Pagination information
     * @return Page of orders matching criteria
     */
    Page<OrderDto> searchOrders(
            Long orderId,
            Long userId,
            String sessionId,
            String orderCode,
            String state,
            LocalDate fromDate,
            LocalDate toDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable);

    // REMOVED: adminSearchOrders method - consolidated with searchOrders
    // All search functionality is handled by searchOrders method


            
    /**
     * Admin update order (API 7) - If order state changes, creates history record
     * @param orderId Order ID to update
     * @param request Update request with fields to update
     * @param updatedByUserId Admin user who updated order
     * @return Updated order
     */


    /**
     * Update order state (API 8) - Creates order state history
     * @param orderId Order ID
     * @param newOrderStateId New order state ID
     * @param changedByUserId User who made the change
     * @return Updated order
     */
    OrderDto updateOrderState(Long orderId, Long newOrderStateId, Long changedByUserId);
    
    /**
     * Update order status with special handling for DELIVERED status
     * When order status is DELIVERED, automatically update associated shipment status to DELIVERED
     * @param orderId Order ID
     * @param newOrderStateId New order state ID
     * @param changedByUserId User who made the change
     * @param statusNote Optional note for status change
     * @return Updated order
     */
    OrderDto updateOrderStatusWithSpecialHandling(Long orderId, Long newOrderStateId, Long changedByUserId, String statusNote);

    /**
     * Check if order belongs to user (for security)
     * @param orderId Order ID
     * @param userId User ID
     * @return true if order belongs to user
     */
    boolean isOrderOwner(Long orderId, Long userId);
    
    /**
     * Get userId from cartId for order ownership tracking
     * Business flow: cartId → Cart → User → userId (for login users) or sessionId (for guest users)
     * @param cartId Cart ID
     * @return User ID from cart relationship, or null for guest carts
     */
    Long getUserIdFromCart(Long cartId);
}
