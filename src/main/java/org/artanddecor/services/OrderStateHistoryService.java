package org.artanddecor.services;

import org.artanddecor.dto.OrderStateHistoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * OrderStateHistory Service Interface for business logic operations
 * Updated to support new API requirements
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
        
    /**
     * Get order state history with filtering and pagination
     * @param orderId Filter by order ID (optional)
     * @param fromDate Filter state changes from this date (optional)
     * @param toDate Filter state changes to this date (optional)
     * @param oldStateId Filter by old state ID (optional)
     * @param newStateId Filter by new state ID (optional)
     * @param pageable Pagination information
     * @return Page of order state history matching criteria
     */
    Page<OrderStateHistoryDto> getOrderStateHistory(
            Long orderId,
            LocalDate fromDate,
            LocalDate toDate, 
            Long oldStateId,
            Long newStateId,
            Pageable pageable);
}