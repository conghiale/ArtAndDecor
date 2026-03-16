package org.artanddecor.utils;

import org.artanddecor.dto.ShipmentStateDto;
import org.artanddecor.model.ShipmentState;
import org.springframework.stereotype.Component;

/**
 * ShipmentState Mapper Utility for converting between Entity and DTO
 */
@Component
public class ShipmentStateMapperUtil {

    /**
     * Map ShipmentState entity to ShipmentStateDto
     * @param shipmentState ShipmentState entity
     * @return ShipmentStateDto
     */
    public ShipmentStateDto mapToDto(ShipmentState shipmentState) {
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
    public ShipmentState mapToEntity(ShipmentStateDto dto) {
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
}