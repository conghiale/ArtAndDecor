package org.artanddecor.utils;

import org.artanddecor.dto.OrderStateDto;
import org.artanddecor.model.OrderState;
import org.springframework.stereotype.Component;

/**
 * OrderState Mapper Utility for converting between Entity and DTO
 */
@Component
public class OrderStateMapperUtil {

    /**
     * Map OrderState entity to OrderStateDto
     * @param orderState OrderState entity
     * @return OrderStateDto
     */
    public OrderStateDto mapToDto(OrderState orderState) {
        if (orderState == null) {
            return null;
        }

        OrderStateDto dto = new OrderStateDto();
        dto.setOrderStateId(orderState.getOrderStateId());
        dto.setOrderStateName(orderState.getOrderStateName());
        dto.setOrderStateDisplayName(orderState.getOrderStateDisplayName());
        dto.setOrderStateRemark(orderState.getOrderStateRemark());
        dto.setOrderStateEnabled(orderState.getOrderStateEnabled());
        dto.setOrderStateCreatedDate(orderState.getCreatedDt());
        dto.setOrderStateModifiedDate(orderState.getModifiedDt());

        return dto;
    }

    /**
     * Map OrderStateDto to OrderState entity
     * @param orderStateDto OrderStateDto
     * @return OrderState entity
     */
    public OrderState mapToEntity(OrderStateDto orderStateDto) {
        if (orderStateDto == null) {
            return null;
        }

        OrderState entity = new OrderState();
        entity.setOrderStateId(orderStateDto.getOrderStateId());
        entity.setOrderStateName(orderStateDto.getOrderStateName());
        entity.setOrderStateDisplayName(orderStateDto.getOrderStateDisplayName());
        entity.setOrderStateRemark(orderStateDto.getOrderStateRemark());
        entity.setOrderStateEnabled(orderStateDto.getOrderStateEnabled() != null ? orderStateDto.getOrderStateEnabled() : true);
        entity.setCreatedDt(orderStateDto.getOrderStateCreatedDate());
        entity.setModifiedDt(orderStateDto.getOrderStateModifiedDate());

        return entity;
    }

    /**
     * Update existing OrderState entity with data from DTO
     * @param existingEntity Existing OrderState entity
     * @param dto OrderStateDto with updated data
     * @return Updated OrderState entity
     */
    public OrderState updateEntityFromDto(OrderState existingEntity, OrderStateDto dto) {
        if (existingEntity == null || dto == null) {
            return existingEntity;
        }

        if (dto.getOrderStateName() != null) {
            existingEntity.setOrderStateName(dto.getOrderStateName());
        }
        if (dto.getOrderStateDisplayName() != null) {
            existingEntity.setOrderStateDisplayName(dto.getOrderStateDisplayName());
        }
        if (dto.getOrderStateRemark() != null) {
            existingEntity.setOrderStateRemark(dto.getOrderStateRemark());
        }
        if (dto.getOrderStateEnabled() != null) {
            existingEntity.setOrderStateEnabled(dto.getOrderStateEnabled());
        }

        return existingEntity;
    }
}