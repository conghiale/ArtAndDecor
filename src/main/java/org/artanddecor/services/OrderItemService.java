package org.artanddecor.services;

import org.artanddecor.dto.OrderItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * OrderItem Service Interface for business logic operations
 */
public interface OrderItemService {

    /**
     * Get all order items with pagination
     * @param pageable Pagination information
     * @return Page of order items
     */
    Page<OrderItemDto> getAllOrderItems(Pageable pageable);

    /**
     * Get order item by ID
     * @param orderItemId Order item ID
     * @return OrderItemDto if found
     */
    OrderItemDto getOrderItemById(Long orderItemId);

    /**
     * Get order items by order ID (alias for compatibility)
     * @param orderId Order ID
     * @return List of order items for specific order
     */
    List<OrderItemDto> getOrderItems(Long orderId);

    /**
     * Get order items by order ID
     * @param orderId Order ID
     * @return List of order items for specific order
     */
    List<OrderItemDto> getOrderItemsByOrderId(Long orderId);

    /**
     * Add new order item to existing order
     * @param orderId Order ID
     * @param productId Product ID
     * @param quantity Item quantity
     * @return Created order item
     */
    OrderItemDto addOrderItem(Long orderId, Long productId, Integer quantity);

    /**
     * Get order items by product ID
     * @param productId Product ID
     * @return List of order items for specific product
     */
    List<OrderItemDto> getOrderItemsByProductId(Long productId);

    /**
     * Create new order item
     * @param orderItemDto Order item data
     * @return Created order item
     */
    OrderItemDto createOrderItem(OrderItemDto orderItemDto);

    /**
     * Create multiple order items for an order
     * @param orderItemDtos List of order item data
     * @return List of created order items
     */
    List<OrderItemDto> createOrderItems(List<OrderItemDto> orderItemDtos);

    /**
     * Update existing order item
     * @param orderItemId Order item ID
     * @param orderItemDto Updated order item data
     * @return Updated order item
     */
    OrderItemDto updateOrderItem(Long orderItemId, OrderItemDto orderItemDto);

    /**
     * Update order item quantity
     * @param orderItemId Order item ID
     * @param newQuantity New quantity
     * @return Updated order item
     */
    OrderItemDto updateOrderItemQuantity(Long orderItemId, Integer newQuantity);

    /**
     * Update order item price
     * @param orderItemId Order item ID
     * @param newUnitPrice New unit price
     * @return Updated order item
     */
    OrderItemDto updateOrderItemPrice(Long orderItemId, BigDecimal newUnitPrice);

    /**
     * Delete order item
     * @param orderItemId Order item ID
     */
    void deleteOrderItem(Long orderItemId);

    /**
     * Delete all order items for specific order
     * @param orderId Order ID
     */
    void deleteOrderItemsByOrderId(Long orderId);

    /**
     * Calculate total amount for order item
     * @param orderItemId Order item ID
     * @return Total amount (quantity * unit price)
     */
    BigDecimal calculateOrderItemTotalAmount(Long orderItemId);

    /**
     * Calculate total amount for order (sum of all order items)
     * @param orderId Order ID
     * @return Total order amount
     */
    BigDecimal calculateOrderTotalAmount(Long orderId);

    /**
     * Calculate total quantity sold for specific product
     * @param productId Product ID
     * @return Total quantity sold
     */
    Integer calculateTotalQuantitySoldForProduct(Long productId);

    /**
     * Calculate total revenue for specific product
     * @param productId Product ID
     * @return Total revenue for product
     */
    BigDecimal calculateTotalRevenueForProduct(Long productId);



    /**
     * Count order items for specific order
     * @param orderId Order ID
     * @return Number of items in order
     */
    Long countOrderItemsByOrderId(Long orderId);



    /**
     * Validate order item quantity against product stock
     * @param productId Product ID
     * @param requestedQuantity Requested quantity
     * @return true if quantity is available, false otherwise
     */
    boolean validateOrderItemQuantity(Long productId, Integer requestedQuantity);

    /**
     * Search order items by multiple criteria (simplified signature for controller)
     * @param orderId Filter by order ID (optional)
     * @param productId Filter by product ID (optional)
     * @param minQuantity Filter by minimum quantity (optional)
     * @param maxQuantity Filter by maximum quantity (optional)
     * @param minUnitPrice Filter by minimum unit price (optional)
     * @param maxUnitPrice Filter by maximum unit price (optional)
     * @param minTotalPrice Filter by minimum total price (optional)
     * @param maxTotalPrice Filter by maximum total price (optional)
     * @param textSearch Search text in product name or code (optional)
     * @param pageable Pagination information
     * @return Page of order items matching criteria
     */
    Page<OrderItemDto> searchOrderItems(
            List<Long> orderIds,
            Long productId,
            Integer minQuantity,
            Integer maxQuantity,
            BigDecimal minUnitPrice,
            BigDecimal maxUnitPrice,
            BigDecimal minTotalPrice,
            BigDecimal maxTotalPrice,
            String textSearch,
            Pageable pageable);

    /**
     * Validate if order item exists
     * @param orderItemId Order item ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long orderItemId);

    /**
     * Get order summary for specific order (total items, total quantity, total amount)
     * @param orderId Order ID
     * @return Order summary data
     */
    Object[] getOrderSummary(Long orderId);
}