package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ShipmentDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.Order;
import org.artanddecor.model.Shipment;
import org.artanddecor.model.ShipmentState;
import org.artanddecor.repository.OrderRepository;
import org.artanddecor.repository.ShipmentRepository;
import org.artanddecor.repository.ShipmentStateRepository;
import org.artanddecor.services.ShipmentService;
import org.artanddecor.utils.ShipmentMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * Implementation of ShipmentService interface
 * Business logic for shipment management operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentServiceImpl.class);
    private static final Random random = new Random();

    private final ShipmentRepository shipmentRepository;
    private final ShipmentStateRepository shipmentStateRepository;
    private final OrderRepository orderRepository;
    private final ShipmentMapperUtil shipmentMapperUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentDto> getAllShipments(Pageable pageable) {
        logger.debug("Getting all shipments with pagination: {}", pageable);
        Page<Shipment> shipmentPage = shipmentRepository.findAll(pageable);
        return shipmentPage.map(shipmentMapperUtil::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentDto getShipmentById(Long shipmentId) {
        logger.debug("Getting shipment by ID: {}", shipmentId);
        Shipment shipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));
        return shipmentMapperUtil.mapToDto(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentDto getShipmentByCode(String shipmentCode) {
        logger.debug("Getting shipment by code: {}", shipmentCode);
        Shipment shipment = shipmentRepository.findByShipmentCode(shipmentCode)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with code: " + shipmentCode));
        return shipmentMapperUtil.mapToDto(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentDto> getShipmentsByOrderId(Long orderId) {
        logger.debug("Getting shipments for order ID: {}", orderId);
        List<Shipment> shipments = shipmentRepository.findByOrderOrderIdOrderByCreatedDtDesc(orderId);
        return shipments.stream()
            .map(shipmentMapperUtil::mapToDto)
            .toList();
    }

    @Override
    public ShipmentDto createShipment(ShipmentDto shipmentDto) {
        logger.debug("Creating new shipment for order: {}", shipmentDto.getOrderId());
        
        // Validate order exists
        Order order = orderRepository.findById(shipmentDto.getOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + shipmentDto.getOrderId()));
        
        // Validate shipment state
        ShipmentState shipmentState = shipmentStateRepository.findById(shipmentDto.getShipmentState().getShipmentStateId())
            .orElseThrow(() -> new ResourceNotFoundException("Shipment state not found"));

        // Generate unique shipment code if not provided
        if (shipmentDto.getShipmentCode() == null || shipmentDto.getShipmentCode().trim().isEmpty()) {
            shipmentDto.setShipmentCode(generateShipmentCode());
        }

        // Validate shipment code uniqueness
        if (!isShipmentCodeUnique(shipmentDto.getShipmentCode(), null)) {
            throw new IllegalArgumentException("Shipment code already exists: " + shipmentDto.getShipmentCode());
        }

        Shipment shipment = shipmentMapperUtil.mapToEntity(shipmentDto);
        shipment.setOrder(order);
        shipment.setShipmentState(shipmentState);

        Shipment savedShipment = shipmentRepository.save(shipment);
        logger.info("Created shipment with ID: {} and code: {}", savedShipment.getShipmentId(), savedShipment.getShipmentCode());
        
        return shipmentMapperUtil.mapToDto(savedShipment);
    }

    @Override
    public ShipmentDto updateShipment(Long shipmentId, ShipmentDto shipmentDto) {
        logger.debug("Updating shipment with ID: {}", shipmentId);
        
        Shipment existingShipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        // Validate shipment code uniqueness if changed
        if (!existingShipment.getShipmentCode().equals(shipmentDto.getShipmentCode()) &&
            !isShipmentCodeUnique(shipmentDto.getShipmentCode(), shipmentId)) {
            throw new IllegalArgumentException("Shipment code already exists: " + shipmentDto.getShipmentCode());
        }

        // Update fields
        existingShipment.setShipmentCode(shipmentDto.getShipmentCode());
        existingShipment.setReceiverName(shipmentDto.getReceiverName());
        existingShipment.setReceiverPhone(shipmentDto.getReceiverPhone());
        existingShipment.setReceiverEmail(shipmentDto.getReceiverEmail());
        existingShipment.setAddressLine(shipmentDto.getAddressLine());
        existingShipment.setCity(shipmentDto.getCity());
        existingShipment.setWard(shipmentDto.getWard());
        existingShipment.setCountry(shipmentDto.getCountry());
        existingShipment.setShippingFeeAmount(shipmentDto.getShippingFeeAmount());
        existingShipment.setShipmentRemark(shipmentDto.getShipmentRemark());

        // Update shipment state if provided
        if (shipmentDto.getShipmentState() != null && shipmentDto.getShipmentState().getShipmentStateId() != null) {
            ShipmentState shipmentState = shipmentStateRepository.findById(shipmentDto.getShipmentState().getShipmentStateId())
                .orElseThrow(() -> new ResourceNotFoundException("Shipment state not found"));
            existingShipment.setShipmentState(shipmentState);
        }

        Shipment updatedShipment = shipmentRepository.save(existingShipment);
        logger.info("Updated shipment with ID: {}", updatedShipment.getShipmentId());
        
        return shipmentMapperUtil.mapToDto(updatedShipment);
    }

    @Override
    public void deleteShipment(Long shipmentId) {
        logger.debug("Deleting shipment with ID: {}", shipmentId);
        
        if (!shipmentRepository.existsById(shipmentId)) {
            throw new ResourceNotFoundException("Shipment not found with ID: " + shipmentId);
        }

        shipmentRepository.deleteById(shipmentId);
        logger.info("Deleted shipment with ID: {}", shipmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentDto> searchShipmentsByCriteria(
            Long shipmentId, Long orderId, String shipmentCode, Long shipmentStateId,
            String receiverName, String receiverPhone, String city, String country,
            BigDecimal minShippingFee, BigDecimal maxShippingFee,
            LocalDateTime shippedAfter, LocalDateTime shippedBefore,
            LocalDateTime deliveredAfter, LocalDateTime deliveredBefore,
            String textSearch, Pageable pageable) {
        
        logger.debug("Searching shipments with criteria");
        
        Page<Shipment> shipmentPage = shipmentRepository.findByCriteria(
            shipmentId, orderId, shipmentCode, shipmentStateId,
            receiverName, receiverPhone, city, country,
            minShippingFee, maxShippingFee,
            shippedAfter, shippedBefore, deliveredAfter, deliveredBefore,
            textSearch, pageable
        );
        
        return shipmentPage.map(shipmentMapperUtil::mapToDto);
    }

    @Override
    public ShipmentDto updateShipmentState(Long shipmentId, Long shipmentStateId, String remark) {
        logger.debug("Updating shipment state for shipment ID: {} to state ID: {}", shipmentId, shipmentStateId);
        
        Shipment shipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));
            
        ShipmentState shipmentState = shipmentStateRepository.findById(shipmentStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment state not found with ID: " + shipmentStateId));

        shipment.setShipmentState(shipmentState);
        if (remark != null) {
            shipment.setShipmentRemark(remark);
        }

        Shipment updatedShipment = shipmentRepository.save(shipment);
        logger.info("Updated shipment state for ID: {} to: {}", shipmentId, shipmentState.getShipmentStateName());
        
        return shipmentMapperUtil.mapToDto(updatedShipment);
    }

    @Override
    public ShipmentDto markAsShipped(Long shipmentId, LocalDateTime shippedAt, String remark) {
        logger.debug("Marking shipment as shipped: {}", shipmentId);
        
        Shipment shipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        shipment.setShippedAt(shippedAt != null ? shippedAt : LocalDateTime.now());
        if (remark != null) {
            shipment.setShipmentRemark(remark);
        }

        // Update to SHIPPED state if exists
        shipmentStateRepository.findByShipmentStateName("SHIPPED")
            .ifPresent(shipment::setShipmentState);

        Shipment updatedShipment = shipmentRepository.save(shipment);
        logger.info("Marked shipment as shipped: {}", shipmentId);
        
        return shipmentMapperUtil.mapToDto(updatedShipment);
    }

    @Override
    public ShipmentDto markAsDelivered(Long shipmentId, LocalDateTime deliveredAt, String remark) {
        logger.debug("Marking shipment as delivered: {}", shipmentId);
        
        Shipment shipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        shipment.setDeliveredAt(deliveredAt != null ? deliveredAt : LocalDateTime.now());
        if (remark != null) {
            shipment.setShipmentRemark(remark);
        }

        // Update to DELIVERED state if exists
        shipmentStateRepository.findByShipmentStateName("DELIVERED")
            .ifPresent(shipment::setShipmentState);

        Shipment updatedShipment = shipmentRepository.save(shipment);
        logger.info("Marked shipment as delivered: {}", shipmentId);
        
        return shipmentMapperUtil.mapToDto(updatedShipment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentDto> getShipmentsByState(Long shipmentStateId, Pageable pageable) {
        logger.debug("Getting shipments by state ID: {}", shipmentStateId);
        Page<Shipment> shipmentPage = shipmentRepository.findByShipmentStateShipmentStateId(shipmentStateId, pageable);
        return shipmentPage.map(shipmentMapperUtil::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isShipmentCodeUnique(String shipmentCode, Long excludeId) {
        if (excludeId != null) {
            return !shipmentRepository.existsByShipmentCodeAndShipmentIdNot(shipmentCode, excludeId);
        }
        return !shipmentRepository.existsByShipmentCode(shipmentCode);
    }

    @Override
    public String generateShipmentCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = String.format("%04d", random.nextInt(10000));
        String shipmentCode = "SH" + timestamp + randomSuffix;
        
        // Ensure uniqueness
        while (!isShipmentCodeUnique(shipmentCode, null)) {
            randomSuffix = String.format("%04d", random.nextInt(10000));
            shipmentCode = "SH" + timestamp + randomSuffix;
        }
        
        return shipmentCode;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getShipmentStatistics() {
        logger.debug("Getting shipment statistics");
        
        try {
            // Get total number of shipments
            long totalShipments = shipmentRepository.count();
            
            // Get count by states
            java.util.Map<String, Long> shipmentsByState = new java.util.HashMap<>();
            List<ShipmentState> allStates = shipmentStateRepository.findAll();
            
            for (ShipmentState state : allStates) {
                long count = shipmentRepository.countByShipmentStateShipmentStateId(state.getShipmentStateId());
                shipmentsByState.put(state.getShipmentStateName(), count);
            }
            
            // Calculate total shipping fees
            BigDecimal totalShippingFees = shipmentRepository.sumAllShippingFees();
            if (totalShippingFees == null) {
                totalShippingFees = BigDecimal.ZERO;
            }
            
            // Get recent shipments count (last 7 days)
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            long recentShipments = shipmentRepository.countByCreatedDtAfter(sevenDaysAgo);
            
            // Build statistics object
            java.util.Map<String, Object> statistics = new java.util.HashMap<>();
            statistics.put("totalShipments", totalShipments);
            statistics.put("shipmentsByState", shipmentsByState);
            statistics.put("totalShippingFees", totalShippingFees);
            statistics.put("recentShipments", recentShipments);
            statistics.put("statisticsGeneratedAt", LocalDateTime.now());
            
            logger.debug("Generated shipment statistics successfully");
            return statistics;
            
        } catch (Exception e) {
            logger.error("Error generating shipment statistics", e);
            throw new RuntimeException("Failed to generate shipment statistics", e);
        }
    }
}