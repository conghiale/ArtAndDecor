package org.artanddecor.services;

import org.artanddecor.dto.ShipmentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Shipment business operations
 */
public interface ShipmentService {

    /**
     * Get all shipments with pagination
     */
    Page<ShipmentDto> getAllShipments(Pageable pageable);

    /**
     * Get shipment by ID
     */
    ShipmentDto getShipmentById(Long shipmentId);

    /**
     * Get shipment by shipment code
     */
    ShipmentDto getShipmentByCode(String shipmentCode);

    /**
     * Get shipments by order ID
     */
    List<ShipmentDto> getShipmentsByOrderId(Long orderId);

    /**
     * Create new shipment
     */
    ShipmentDto createShipment(ShipmentDto shipmentDto);

    /**
     * Update existing shipment
     */
    ShipmentDto updateShipment(Long shipmentId, ShipmentDto shipmentDto);

    /**
     * Delete shipment by ID
     */
    void deleteShipment(Long shipmentId);

    /**
     * Search shipments by criteria with advanced filtering
     */
    Page<ShipmentDto> searchShipmentsByCriteria(
            Long shipmentId,
            Long orderId,
            String shipmentCode,
            Long shipmentStateId,
            String receiverName,
            String receiverPhone,
            String city,
            String country,
            BigDecimal minShippingFee,
            BigDecimal maxShippingFee,
            LocalDateTime shippedAfter,
            LocalDateTime shippedBefore,
            LocalDateTime deliveredAfter,
            LocalDateTime deliveredBefore,
            String textSearch,
            Pageable pageable);

    /**
     * Update shipment state
     */
    ShipmentDto updateShipmentState(Long shipmentId, Long shipmentStateId, String remark);

    /**
     * Mark shipment as shipped
     */
    ShipmentDto markAsShipped(Long shipmentId, LocalDateTime shippedAt, String remark);

    /**
     * Mark shipment as delivered
     */
    ShipmentDto markAsDelivered(Long shipmentId, LocalDateTime deliveredAt, String remark);

    /**
     * Get shipments by state
     */
    Page<ShipmentDto> getShipmentsByState(Long shipmentStateId, Pageable pageable);

    /**
     * Check if shipment code is unique
     */
    boolean isShipmentCodeUnique(String shipmentCode, Long excludeId);

    /**
     * Generate unique shipment code
     */
    String generateShipmentCode();

    /**
     * Get shipment statistics
     */
    Object getShipmentStatistics();
}