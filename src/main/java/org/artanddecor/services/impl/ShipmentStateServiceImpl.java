package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ShipmentStateDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.ShipmentState;
import org.artanddecor.repository.ShipmentStateRepository;
import org.artanddecor.services.ShipmentStateService;
import org.artanddecor.utils.ShipmentMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of ShipmentStateService interface
 * Business logic for shipment state management operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentStateServiceImpl implements ShipmentStateService {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentStateServiceImpl.class);

    private final ShipmentStateRepository shipmentStateRepository;
    private final ShipmentMapperUtil shipmentMapperUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentStateDto> getAllShipmentStates(Pageable pageable) {
        logger.debug("Getting all shipment states with pagination: {}", pageable);
        Page<ShipmentState> statePage = shipmentStateRepository.findAll(pageable);
        return statePage.map(shipmentMapperUtil::mapShipmentStateToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentStateDto getShipmentStateById(Long shipmentStateId) {
        logger.debug("Getting shipment state by ID: {}", shipmentStateId);
        ShipmentState shipmentState = shipmentStateRepository.findById(shipmentStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment state not found with ID: " + shipmentStateId));
        return shipmentMapperUtil.mapShipmentStateToDto(shipmentState);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentStateDto getShipmentStateByName(String shipmentStateName) {
        logger.debug("Getting shipment state by name: {}", shipmentStateName);
        ShipmentState shipmentState = shipmentStateRepository.findByShipmentStateName(shipmentStateName)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment state not found with name: " + shipmentStateName));
        return shipmentMapperUtil.mapShipmentStateToDto(shipmentState);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentStateDto> getAllEnabledShipmentStates() {
        logger.debug("Getting all enabled shipment states");
        List<ShipmentState> states = shipmentStateRepository.findByShipmentStateEnabledTrueOrderByShipmentStateName();
        return states.stream()
            .map(shipmentMapperUtil::mapShipmentStateToDto)
            .toList();
    }

    @Override
    public ShipmentStateDto createShipmentState(ShipmentStateDto shipmentStateDto) {
        logger.debug("Creating new shipment state: {}", shipmentStateDto.getShipmentStateName());
        
        // Check if name already exists
        if (shipmentStateRepository.existsByShipmentStateName(shipmentStateDto.getShipmentStateName())) {
            throw new IllegalArgumentException("Shipment state name already exists: " + shipmentStateDto.getShipmentStateName());
        }

        ShipmentState shipmentState = shipmentMapperUtil.mapShipmentStateToEntity(shipmentStateDto);
        ShipmentState savedState = shipmentStateRepository.save(shipmentState);
        logger.info("Created shipment state with ID: {} and name: {}", savedState.getShipmentStateId(), savedState.getShipmentStateName());
        
        return shipmentMapperUtil.mapShipmentStateToDto(savedState);
    }

    @Override
    public ShipmentStateDto updateShipmentState(Long shipmentStateId, ShipmentStateDto shipmentStateDto) {
        logger.debug("Updating shipment state with ID: {}", shipmentStateId);
        
        ShipmentState existingState = shipmentStateRepository.findById(shipmentStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment state not found with ID: " + shipmentStateId));

        // Check if name already exists (excluding current record)
        if (!existingState.getShipmentStateName().equals(shipmentStateDto.getShipmentStateName()) &&
            shipmentStateRepository.existsByShipmentStateNameAndShipmentStateIdNot(
                shipmentStateDto.getShipmentStateName(), shipmentStateId)) {
            throw new IllegalArgumentException("Shipment state name already exists: " + shipmentStateDto.getShipmentStateName());
        }

        // Update fields
        existingState.setShipmentStateName(shipmentStateDto.getShipmentStateName());
        existingState.setShipmentStateDisplayName(shipmentStateDto.getShipmentStateDisplayName());
        existingState.setShipmentStateRemark(shipmentStateDto.getShipmentStateRemark());
        existingState.setShipmentStateEnabled(shipmentStateDto.getShipmentStateEnabled());

        ShipmentState updatedState = shipmentStateRepository.save(existingState);
        logger.info("Updated shipment state with ID: {}", updatedState.getShipmentStateId());
        
        return shipmentMapperUtil.mapShipmentStateToDto(updatedState);
    }

    @Override
    public void deleteShipmentState(Long shipmentStateId) {
        logger.debug("Deleting shipment state with ID: {}", shipmentStateId);
        
        if (!shipmentStateRepository.existsById(shipmentStateId)) {
            throw new ResourceNotFoundException("Shipment state not found with ID: " + shipmentStateId);
        }

        shipmentStateRepository.deleteById(shipmentStateId);
        logger.info("Deleted shipment state with ID: {}", shipmentStateId);
    }

    @Override
    public ShipmentStateDto toggleShipmentStateEnabled(Long shipmentStateId) {
        logger.debug("Toggling enabled status for shipment state ID: {}", shipmentStateId);
        
        ShipmentState shipmentState = shipmentStateRepository.findById(shipmentStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment state not found with ID: " + shipmentStateId));

        shipmentState.setShipmentStateEnabled(!shipmentState.getShipmentStateEnabled());
        ShipmentState updatedState = shipmentStateRepository.save(shipmentState);
        logger.info("Toggled enabled status for shipment state ID: {} to: {}", 
                   shipmentStateId, updatedState.getShipmentStateEnabled());
        
        return shipmentMapperUtil.mapShipmentStateToDto(updatedState);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentStateDto> searchShipmentStatesByCriteria(
            Long shipmentStateId, String shipmentStateName, Boolean shipmentStateEnabled, 
            String textSearch, Pageable pageable) {
        
        logger.debug("Searching shipment states with criteria");
        
        Page<ShipmentState> statePage = shipmentStateRepository.findByCriteria(
            shipmentStateId, shipmentStateName, shipmentStateEnabled, textSearch, pageable
        );
        
        return statePage.map(shipmentMapperUtil::mapShipmentStateToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllEnabledShipmentStateNames() {
        logger.debug("Getting all enabled shipment state names");
        return shipmentStateRepository.findAllEnabledShipmentStateNames();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long shipmentStateId) {
        return shipmentStateRepository.existsById(shipmentStateId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isShipmentStateNameUnique(String shipmentStateName, Long excludeId) {
        if (excludeId != null) {
            return !shipmentStateRepository.existsByShipmentStateNameAndShipmentStateIdNot(shipmentStateName, excludeId);
        }
        return !shipmentStateRepository.existsByShipmentStateName(shipmentStateName);
    }
}