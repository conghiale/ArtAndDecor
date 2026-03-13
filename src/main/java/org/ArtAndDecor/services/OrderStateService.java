package org.ArtAndDecor.services;

import org.ArtAndDecor.dto.OrderStateDto;

import java.util.List;

/**
 * OrderState Service Interface for business logic operations
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
}