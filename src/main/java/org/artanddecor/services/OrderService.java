package org.artanddecor.services;

import org.artanddecor.dto.CreateOrderItemRequest;
import org.artanddecor.dto.OrderDto;
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
     * Get order by ID (API 6)
     * @param orderId Order ID
     * @return OrderDto if found
     */
    OrderDto getOrderById(Long orderId);

    /**
     * Checkout cart to create order (API 1) - DISCOUNT removed
     * @param userId User ID
     * @param cartId Cart ID
     * @param shippingAddressId Shipping address ID
     * @param paymentMethod Payment method
     * @param discountCode Discount code (set to null, kept for backward compatibility)
     * @return Created order
     */
    OrderDto checkoutCart(
            Long userId,
            Long cartId,
            Long shippingAddressId,
            String paymentMethod,
            String discountCode);

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
     * @param customerId Filter by customer ID (optional)  
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
            Long customerId,
            String state,
            LocalDate fromDate,
            LocalDate toDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable);

    /**
     * Admin search orders with filters (API 5) - Legacy method, delegates to searchOrders
     * @param orderId Filter by order ID (optional)
     * @param customerId Filter by customer ID (optional)
     * @param state Filter by state (optional)
     * @param fromDate Filter from date (optional)
     * @param toDate Filter to date (optional)
     * @param minAmount Filter by minimum amount (optional)
     * @param maxAmount Filter by maximum amount (optional)
     * @param pageable Pagination information
     * @return Page of orders matching criteria
     */
    Page<OrderDto> adminSearchOrders(
            Long orderId,
            Long customerId,
            String state,
            LocalDate fromDate,
            LocalDate toDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable);

    /**
     * Admin create order (API 6) - DISCOUNT removed
     * @param customerId Customer ID
     * @param shippingAddressId Shipping address ID
     * @param discountCode Discount code (set to null, kept for backward compatibility)
     * @param orderItems List of order items
     * @param createdByUserId Admin user who created order
     * @return Created order
     */
    OrderDto adminCreateOrder(
            Long customerId,
            Long shippingAddressId,
            String discountCode,
            List<CreateOrderItemRequest> orderItems,
            Long createdByUserId);
            
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
     * Check if order belongs to user (for security)
     * @param orderId Order ID
     * @param userId User ID
     * @return true if order belongs to user
     */
    boolean isOrderOwner(Long orderId, Long userId);
}
