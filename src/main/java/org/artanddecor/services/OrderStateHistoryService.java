package org.artanddecor.services;

import org.artanddecor.dto.OrderStateHistoryDto;

import java.util.List;

/**
 * OrderStateHistory Service Interface for business logic operations
 */
public interface OrderStateHistoryService {

    /**
     * Get order state history for specific order (used by OrderController API 9)
     * @param orderId Order ID
     * @return List of order state history for specific order
     */
    List<OrderStateHistoryDto> getOrderStateHistory(Long orderId);

    /**
     * Create new order state history record
     * @param orderId Order ID
     * @param oldOrderStateId Previous order state ID
     * @param newOrderStateId New order state ID
     * @param changedByUserId User ID who changed the state
     * @return Created order state history DTO
     */
    OrderStateHistoryDto createOrderStateHistory(
        Long orderId,
        Long oldOrderStateId, 
        Long newOrderStateId,
        Long changedByUserId);
}