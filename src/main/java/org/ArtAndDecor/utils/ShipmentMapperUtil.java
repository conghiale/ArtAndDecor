package org.ArtAndDecor.utils;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.ShipmentDto;
import org.ArtAndDecor.model.Shipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Shipment Mapper Utility for converting between Entity and DTO
 */
@Component
@RequiredArgsConstructor
public class ShipmentMapperUtil {

    private final ShipmentStateMapperUtil shipmentStateMapperUtil;

    private final OrderMapperUtil orderMapperUtil;

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
        dto.setDistrict(shipment.getDistrict());
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
            dto.setShipmentState(shipmentStateMapperUtil.mapToDto(shipment.getShipmentState()));
        }

        // Set computed fields
        dto.setFullAddress(shipment.getFullAddress());
        dto.setDeliveryDurationInDays(dto.getDeliveryDurationInDays());

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
        shipment.setDistrict(dto.getDistrict());
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
}