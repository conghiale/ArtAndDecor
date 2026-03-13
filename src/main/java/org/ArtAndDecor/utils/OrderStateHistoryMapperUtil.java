package org.ArtAndDecor.utils;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.OrderStateHistoryDto;
import org.ArtAndDecor.dto.OrderStateDto;
import org.ArtAndDecor.model.OrderStateHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * OrderStateHistory Mapper Utility for converting between Entity and DTO
 * Matches database schema: ORDER_STATE_HISTORY_ID, ORDER_ID, OLD_STATE_ID, NEW_STATE_ID, CHANGED_BY_USER_ID, CREATED_DT
 */
@Component
@RequiredArgsConstructor
public class OrderStateHistoryMapperUtil {

    private final OrderStateMapperUtil orderStateMapperUtil;

    /**
     * Map OrderStateHistory entity to OrderStateHistoryDto
     * @param orderStateHistory OrderStateHistory entity
     * @return OrderStateHistoryDto
     */
    public OrderStateHistoryDto mapToDto(OrderStateHistory orderStateHistory) {
        if (orderStateHistory == null) {
            return null;
        }

        OrderStateHistoryDto dto = new OrderStateHistoryDto();
        dto.setHistoryId(orderStateHistory.getOrderStateHistoryId());
        dto.setOrderStateHistoryId(orderStateHistory.getOrderStateHistoryId()); // Keep for compatibility
        dto.setStateChangeDate(orderStateHistory.getCreatedDt());
        dto.setCreatedDt(orderStateHistory.getCreatedDt()); // Keep for compatibility
        dto.setChangedByUserId(orderStateHistory.getChangedByUserId());
        dto.setChangedByUserName(orderStateHistory.getChangedByUserName());

        // Set order information
        if (orderStateHistory.getOrder() != null) {
            dto.setOrderId(orderStateHistory.getOrder().getOrderId());
            dto.setOrderCode(orderStateHistory.getOrder().getOrderCode());
        }

        // Map old and new states
        if (orderStateHistory.getOldState() != null) {
            dto.setOldState(orderStateMapperUtil.mapToDto(orderStateHistory.getOldState()));
        }
        
        if (orderStateHistory.getNewState() != null) {
            dto.setNewState(orderStateMapperUtil.mapToDto(orderStateHistory.getNewState()));
        }

        // Map changed by user
        if (orderStateHistory.getChangedByUser() != null) {
            dto.setChangedByUser(UserMapperUtil.toBasicDto(orderStateHistory.getChangedByUser()));
        }

        // Set transition description
        dto.setTransitionDescription(dto.getTransitionDescriptionValue());

        return dto;
    }

    /**
     * Map OrderStateHistoryDto to OrderStateHistory entity
     * @param orderStateHistoryDto OrderStateHistoryDto
     * @return OrderStateHistory entity
     */
    public OrderStateHistory mapToEntity(OrderStateHistoryDto orderStateHistoryDto) {
        if (orderStateHistoryDto == null) {
            return null;
        }

        OrderStateHistory entity = new OrderStateHistory();
        entity.setOrderStateHistoryId(orderStateHistoryDto.getHistoryId() != null ? 
            orderStateHistoryDto.getHistoryId() : orderStateHistoryDto.getOrderStateHistoryId());
        entity.setCreatedDt(orderStateHistoryDto.getStateChangeDate() != null ? 
            orderStateHistoryDto.getStateChangeDate() : orderStateHistoryDto.getCreatedDt());

        // Map old and new states
        if (orderStateHistoryDto.getOldState() != null) {
            entity.setOldState(orderStateMapperUtil.mapToEntity(orderStateHistoryDto.getOldState()));
        }
        
        if (orderStateHistoryDto.getNewState() != null) {
            entity.setNewState(orderStateMapperUtil.mapToEntity(orderStateHistoryDto.getNewState()));
        }

        // Map changed by user
        if (orderStateHistoryDto.getChangedByUser() != null) {
            // Note: We'll set the relationship in the service layer to avoid circular dependencies
            // entity.setChangedByUser(user from service);
        }

        // Note: Order relationship will be set by the service layer

        return entity;
    }

    /**
     * Map OrderStateHistory entity to OrderStateHistoryDto without nested objects (for performance)
     * @param orderStateHistory OrderStateHistory entity
     * @return OrderStateHistoryDto without nested objects
     */
    public OrderStateHistoryDto mapToDtoWithoutNested(OrderStateHistory orderStateHistory) {
        if (orderStateHistory == null) {
            return null;
        }

        OrderStateHistoryDto dto = new OrderStateHistoryDto();
        dto.setHistoryId(orderStateHistory.getOrderStateHistoryId());
        dto.setOrderStateHistoryId(orderStateHistory.getOrderStateHistoryId());
        dto.setStateChangeDate(orderStateHistory.getCreatedDt());
        dto.setCreatedDt(orderStateHistory.getCreatedDt());
        dto.setChangedByUserId(orderStateHistory.getChangedByUserId());
        dto.setChangedByUserName(orderStateHistory.getChangedByUserName());

        // Set only IDs for relationships
        if (orderStateHistory.getOrder() != null) {
            dto.setOrderId(orderStateHistory.getOrder().getOrderId());
            dto.setOrderCode(orderStateHistory.getOrder().getOrderCode());
        }

        // Create minimal state DTOs
        if (orderStateHistory.getOldState() != null) {
            OrderStateDto oldStateDto = new OrderStateDto();
            oldStateDto.setOrderStateId(orderStateHistory.getOldState().getOrderStateId());
            oldStateDto.setOrderStateName(orderStateHistory.getOldState().getOrderStateName());
            dto.setOldState(oldStateDto);
        }
        
        if (orderStateHistory.getNewState() != null) {
            OrderStateDto newStateDto = new OrderStateDto();
            newStateDto.setOrderStateId(orderStateHistory.getNewState().getOrderStateId());
            newStateDto.setOrderStateName(orderStateHistory.getNewState().getOrderStateName());
            dto.setNewState(newStateDto);
        }

        // Set transition description
        dto.setTransitionDescription(dto.getTransitionDescriptionValue());

        return dto;
    }

    /**
     * Update existing OrderStateHistory entity with data from DTO
     * @param existingEntity Existing OrderStateHistory entity
     * @param dto OrderStateHistoryDto with updated data
     * @return Updated OrderStateHistory entity
     */
    public OrderStateHistory updateEntityFromDto(OrderStateHistory existingEntity, OrderStateHistoryDto dto) {
        if (existingEntity == null || dto == null) {
            return existingEntity;
        }

        // Only update the created date if provided
        if (dto.getStateChangeDate() != null) {
            existingEntity.setCreatedDt(dto.getStateChangeDate());
        } else if (dto.getCreatedDt() != null) {
            existingEntity.setCreatedDt(dto.getCreatedDt());
        }

        // Note: Order, old state, new state, and changed by user relationships 
        // should not be modified after creation

        return existingEntity;
    }
}