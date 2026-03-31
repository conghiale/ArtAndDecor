package org.artanddecor.services;


import org.artanddecor.dto.OrderStateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * OrderState Service Interface for business logic operations
 * Updated to support new API requirements
 */
public interface OrderStateService {

    /**
     * Get all order states
     * @return List of all order states
     */
    List<OrderStateDto> getAllOrderStates();

    /**
     * Get all enabled order states
     * @return List of enabled order states
     */
    List<OrderStateDto> getAllEnabledOrderStates();
    
    /**
     * Get order states with filtering and pagination
     * @param orderStateId Filter by order state ID (optional)
     * @param orderStateName Filter by order state name (optional)
     * @param enabled Filter by enabled status (optional)
     * @param pageable Pagination information
     * @return Page of order states matching criteria
     */
    Page<OrderStateDto> getOrderStates(Long orderStateId, String orderStateName, Boolean enabled, Pageable pageable);
    
    /**
     * Create new order state
     * @param request Order state creation request
     * @return Created order state DTO
     */

    
    /**
     * Update existing order state
     * @param orderStateId Order state ID to update
     * @param request Order state update request
     * @return Updated order state DTO
     */

}
