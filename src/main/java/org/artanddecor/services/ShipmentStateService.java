package org.artanddecor.services;

import org.artanddecor.dto.ShipmentStateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for ShipmentState business operations
 */
public interface ShipmentStateService {

    /**
     * Get all shipment states with pagination
     */
    Page<ShipmentStateDto> getAllShipmentStates(Pageable pageable);

    /**
     * Get shipment state by ID
     */
    ShipmentStateDto getShipmentStateById(Long shipmentStateId);

    /**
     * Get shipment state by name
     */
    ShipmentStateDto getShipmentStateByName(String shipmentStateName);

    /**
     * Get all enabled shipment states
     */
    List<ShipmentStateDto> getAllEnabledShipmentStates();

    /**
     * Create new shipment state
     */
    ShipmentStateDto createShipmentState(ShipmentStateDto shipmentStateDto);

    /**
     * Update existing shipment state
     */
    ShipmentStateDto updateShipmentState(Long shipmentStateId, ShipmentStateDto shipmentStateDto);

    /**
     * Delete shipment state by ID
     */
    void deleteShipmentState(Long shipmentStateId);

    /**
     * Toggle shipment state enabled status
     */
    ShipmentStateDto toggleShipmentStateEnabled(Long shipmentStateId);

    /**
     * Search shipment states by criteria
     */
    Page<ShipmentStateDto> searchShipmentStatesByCriteria(
            Long shipmentStateId,
            String shipmentStateName,
            Boolean shipmentStateEnabled,
            String textSearch,
            Pageable pageable);

    /**
     * Get all enabled shipment state names
     */
    List<String> getAllEnabledShipmentStateNames();

    /**
     * Check if shipment state exists by ID
     */
    boolean existsById(Long shipmentStateId);

    /**
     * Check if shipment state name is unique
     */
    boolean isShipmentStateNameUnique(String shipmentStateName, Long excludeId);
}