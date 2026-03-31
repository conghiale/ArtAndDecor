package org.artanddecor.utils;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ShipmentDto;
import org.artanddecor.dto.ShipmentStateDto;
import org.artanddecor.dto.ShippingFeeDto;
import org.artanddecor.dto.ShippingFeeTypeDto;
import org.artanddecor.model.Shipment;
import org.artanddecor.model.ShipmentState;
import org.artanddecor.model.ShippingFee;
import org.artanddecor.model.ShippingFeeType;
import org.springframework.stereotype.Component;

/**
 * Consolidated Shipment-related Mapper Utility for converting between Entities and DTOs
 * Handles all shipping-related mappings: Shipment, ShipmentState, ShippingFee, ShippingFeeType
 */
@Component
@RequiredArgsConstructor
public class ShipmentMapperUtil {

    private final OrderMapperUtil orderMapperUtil;

    // =============================================
    // SHIPMENT MAPPING METHODS
    // =============================================

    /**
     * Map Shipment entity to ShipmentDto
     * @param shipment Shipment entity
     * @return ShipmentDto
     */
    public ShipmentDto mapToDto(Shipment shipment) {
        if (shipment == null) {
            return null;
        }

        ShipmentDto dto = new ShipmentDto();
        dto.setShipmentId(shipment.getShipmentId());
        dto.setOrderId(shipment.getOrder() != null ? shipment.getOrder().getOrderId() : null);
        dto.setShipmentCode(shipment.getShipmentCode());
        dto.setReceiverName(shipment.getReceiverName());
        dto.setReceiverPhone(shipment.getReceiverPhone());
        dto.setReceiverEmail(shipment.getReceiverEmail());
        dto.setAddressLine(shipment.getAddressLine());
        dto.setCity(shipment.getCity());
        dto.setWard(shipment.getWard());
        dto.setCountry(shipment.getCountry());
        dto.setShippingFeeAmount(shipment.getShippingFeeAmount());
        dto.setShipmentRemark(shipment.getShipmentRemark());
        dto.setCreatedDt(shipment.getCreatedDt());
        dto.setModifiedDt(shipment.getModifiedDt());
        dto.setShippedAt(shipment.getShippedAt());
        dto.setDeliveredAt(shipment.getDeliveredAt());

        // Map related entities
        if (shipment.getOrder() != null) {
            dto.setOrder(orderMapperUtil.mapToDto(shipment.getOrder()));
        }

        if (shipment.getShipmentState() != null) {
            dto.setShipmentState(mapShipmentStateToDto(shipment.getShipmentState()));
        }

        // Set computed fields
        dto.setFullAddress(shipment.getFullAddress());

        return dto;
    }

    /**
     * Map ShipmentDto to Shipment entity
     * @param dto ShipmentDto
     * @return Shipment entity
     */
    public Shipment mapToEntity(ShipmentDto dto) {
        if (dto == null) {
            return null;
        }

        Shipment shipment = new Shipment();
        shipment.setShipmentId(dto.getShipmentId());
        shipment.setShipmentCode(dto.getShipmentCode());
        shipment.setReceiverName(dto.getReceiverName());
        shipment.setReceiverPhone(dto.getReceiverPhone());
        shipment.setReceiverEmail(dto.getReceiverEmail());
        shipment.setAddressLine(dto.getAddressLine());
        shipment.setCity(dto.getCity());
        shipment.setWard(dto.getWard());
        shipment.setCountry(dto.getCountry());
        shipment.setShippingFeeAmount(dto.getShippingFeeAmount());
        shipment.setShipmentRemark(dto.getShipmentRemark());
        shipment.setShippedAt(dto.getShippedAt());
        shipment.setDeliveredAt(dto.getDeliveredAt());

        // Note: Order and ShipmentState relationships should be set separately in service layer
        // to avoid circular dependencies during mapping

        return shipment;
    }

    // =============================================
    // SHIPMENT STATE MAPPING METHODS
    // =============================================

    /**
     * Map ShipmentState entity to ShipmentStateDto
     * @param shipmentState ShipmentState entity
     * @return ShipmentStateDto
     */
    public ShipmentStateDto mapShipmentStateToDto(ShipmentState shipmentState) {
        if (shipmentState == null) {
            return null;
        }

        ShipmentStateDto dto = new ShipmentStateDto();
        dto.setShipmentStateId(shipmentState.getShipmentStateId());
        dto.setShipmentStateName(shipmentState.getShipmentStateName());
        dto.setShipmentStateDisplayName(shipmentState.getShipmentStateDisplayName());
        dto.setShipmentStateRemark(shipmentState.getShipmentStateRemark());
        dto.setShipmentStateEnabled(shipmentState.getShipmentStateEnabled());
        dto.setCreatedDt(shipmentState.getCreatedDt());
        dto.setModifiedDt(shipmentState.getModifiedDt());

        return dto;
    }

    /**
     * Map ShipmentStateDto to ShipmentState entity
     * @param dto ShipmentStateDto
     * @return ShipmentState entity
     */
    public ShipmentState mapShipmentStateToEntity(ShipmentStateDto dto) {
        if (dto == null) {
            return null;
        }

        ShipmentState shipmentState = new ShipmentState();
        shipmentState.setShipmentStateId(dto.getShipmentStateId());
        shipmentState.setShipmentStateName(dto.getShipmentStateName());
        shipmentState.setShipmentStateDisplayName(dto.getShipmentStateDisplayName());
        shipmentState.setShipmentStateRemark(dto.getShipmentStateRemark());
        shipmentState.setShipmentStateEnabled(dto.getShipmentStateEnabled());

        return shipmentState;
    }

    // =============================================
    // SHIPPING FEE MAPPING METHODS
    // =============================================

    /**
     * Map ShippingFee entity to ShippingFeeDto
     * @param shippingFee ShippingFee entity
     * @return ShippingFeeDto
     */
    public ShippingFeeDto mapShippingFeeToDto(ShippingFee shippingFee) {
        if (shippingFee == null) {
            return null;
        }

        ShippingFeeDto dto = new ShippingFeeDto();
        dto.setShippingFeeId(shippingFee.getShippingFeeId());
        dto.setShippingFeeTypeId(shippingFee.getShippingFeeType() != null ? 
                                shippingFee.getShippingFeeType().getShippingFeeTypeId() : null);
        dto.setMinOrderPrice(shippingFee.getMinOrderPrice());
        dto.setMaxOrderPrice(shippingFee.getMaxOrderPrice());
        dto.setShippingFeeValue(shippingFee.getShippingFeeValue());
        dto.setShippingFeeDisplayName(shippingFee.getShippingFeeDisplayName());
        dto.setShippingFeeRemark(shippingFee.getShippingFeeRemark());
        dto.setShippingFeeEnabled(shippingFee.getShippingFeeEnabled());
        dto.setCreatedDt(shippingFee.getCreatedDt());
        dto.setModifiedDt(shippingFee.getModifiedDt());

        // Set related data from ShippingFeeType
        if (shippingFee.getShippingFeeType() != null) {
            dto.setShippingFeeTypeName(shippingFee.getShippingFeeType().getShippingFeeTypeName());
            dto.setShippingFeeTypeRemark(shippingFee.getShippingFeeType().getShippingFeeTypeRemark());
        }

        return dto;
    }

    /**
     * Map ShippingFeeDto to ShippingFee entity
     * @param dto ShippingFeeDto
     * @return ShippingFee entity
     */
    public ShippingFee mapShippingFeeToEntity(ShippingFeeDto dto) {
        if (dto == null) {
            return null;
        }

        ShippingFee shippingFee = new ShippingFee();
        shippingFee.setShippingFeeId(dto.getShippingFeeId());
        shippingFee.setMinOrderPrice(dto.getMinOrderPrice());
        shippingFee.setMaxOrderPrice(dto.getMaxOrderPrice());
        shippingFee.setShippingFeeValue(dto.getShippingFeeValue());
        shippingFee.setShippingFeeDisplayName(dto.getShippingFeeDisplayName());
        shippingFee.setShippingFeeRemark(dto.getShippingFeeRemark());
        shippingFee.setShippingFeeEnabled(dto.getShippingFeeEnabled());

        // Note: ShippingFeeType relationship should be set separately in service layer
        // to avoid circular dependencies during mapping

        return shippingFee;
    }

    // =============================================
    // SHIPPING FEE TYPE MAPPING METHODS
    // =============================================

    /**
     * Map ShippingFeeType entity to ShippingFeeTypeDto
     * @param shippingFeeType ShippingFeeType entity
     * @return ShippingFeeTypeDto
     */
    public ShippingFeeTypeDto mapShippingFeeTypeToDto(ShippingFeeType shippingFeeType) {
        if (shippingFeeType == null) {
            return null;
        }

        ShippingFeeTypeDto dto = new ShippingFeeTypeDto();
        dto.setShippingFeeTypeId(shippingFeeType.getShippingFeeTypeId());
        dto.setShippingFeeTypeName(shippingFeeType.getShippingFeeTypeName());
        dto.setShippingFeeTypeDisplayName(shippingFeeType.getShippingFeeTypeDisplayName());
        dto.setShippingFeeTypeRemark(shippingFeeType.getShippingFeeTypeRemark());
        dto.setShippingFeeTypeEnabled(shippingFeeType.getShippingFeeTypeEnabled());
        dto.setCreatedDt(shippingFeeType.getCreatedDt());
        dto.setModifiedDt(shippingFeeType.getModifiedDt());

        return dto;
    }

    /**
     * Map ShippingFeeTypeDto to ShippingFeeType entity
     * @param dto ShippingFeeTypeDto
     * @return ShippingFeeType entity
     */
    public ShippingFeeType mapShippingFeeTypeToEntity(ShippingFeeTypeDto dto) {
        if (dto == null) {
            return null;
        }

        ShippingFeeType shippingFeeType = new ShippingFeeType();
        shippingFeeType.setShippingFeeTypeId(dto.getShippingFeeTypeId());
        shippingFeeType.setShippingFeeTypeName(dto.getShippingFeeTypeName());
        shippingFeeType.setShippingFeeTypeDisplayName(dto.getShippingFeeTypeDisplayName());
        shippingFeeType.setShippingFeeTypeRemark(dto.getShippingFeeTypeRemark());
        shippingFeeType.setShippingFeeTypeEnabled(dto.getShippingFeeTypeEnabled());

        return shippingFeeType;
    }
}