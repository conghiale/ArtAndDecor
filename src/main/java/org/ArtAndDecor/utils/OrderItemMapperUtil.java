package org.ArtAndDecor.utils;

import org.ArtAndDecor.dto.OrderItemDto;
import org.ArtAndDecor.model.OrderItem;
import org.springframework.stereotype.Component;

/**
 * OrderItem Mapper Utility for converting between Entity and DTO
 */
@Component
public class OrderItemMapperUtil {

    /**
     * Map OrderItem entity to OrderItemDto
     * @param orderItem OrderItem entity
     * @return OrderItemDto
     */
    public OrderItemDto mapToDto(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        OrderItemDto dto = new OrderItemDto();
        dto.setOrderItemId(orderItem.getOrderItemId());
        dto.setProductId(orderItem.getProduct() != null ? orderItem.getProduct().getProductId() : null);
        dto.setProductName(orderItem.getProductName());
        dto.setProductCode(orderItem.getProductCode());
        dto.setProductCategoryName(orderItem.getProductCategoryName());
        dto.setProductTypeName(orderItem.getProductTypeName());
        dto.setProductAttrJson(orderItem.getProductAttrJson());
        dto.setQuantity(orderItem.getQuantity());
        dto.setUnitPrice(orderItem.getUnitPrice());
        dto.setTotalPrice(orderItem.getTotalPrice());
        dto.setCreatedDt(orderItem.getCreatedDt());
        dto.setModifiedDt(orderItem.getModifiedDt());

        // Set order information
        if (orderItem.getOrder() != null) {
            dto.setOrderId(orderItem.getOrder().getOrderId());
        }

        return dto;
    }

    /**
     * Map OrderItemDto to OrderItem entity
     * @param orderItemDto OrderItemDto
     * @return OrderItem entity
     */
    public OrderItem mapToEntity(OrderItemDto orderItemDto) {
        if (orderItemDto == null) {
            return null;
        }

        OrderItem entity = new OrderItem();
        entity.setOrderItemId(orderItemDto.getOrderItemId());
        entity.setProductName(orderItemDto.getProductName());
        entity.setProductCode(orderItemDto.getProductCode());
        entity.setProductCategoryName(orderItemDto.getProductCategoryName());
        entity.setProductTypeName(orderItemDto.getProductTypeName());
        entity.setProductAttrJson(orderItemDto.getProductAttrJson());
        entity.setQuantity(orderItemDto.getQuantity());
        entity.setUnitPrice(orderItemDto.getUnitPrice());
        entity.setTotalPrice(orderItemDto.getTotalPrice());
        entity.setCreatedDt(orderItemDto.getCreatedDt());
        entity.setModifiedDt(orderItemDto.getModifiedDt());

        // Note: Order and Product relationships will be set by the service layer

        return entity;
    }

    /**
     * Map OrderItem entity to OrderItemDto without order details (for performance)
     * @param orderItem OrderItem entity
     * @return OrderItemDto without order details
     */
    public OrderItemDto mapToDtoWithoutOrder(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        OrderItemDto dto = new OrderItemDto();
        dto.setOrderItemId(orderItem.getOrderItemId());
        dto.setProductId(orderItem.getProduct() != null ? orderItem.getProduct().getProductId() : null);
        dto.setProductName(orderItem.getProductName());
        dto.setProductCode(orderItem.getProductCode());
        dto.setProductCategoryName(orderItem.getProductCategoryName());
        dto.setProductTypeName(orderItem.getProductTypeName());
        dto.setProductAttrJson(orderItem.getProductAttrJson());
        dto.setQuantity(orderItem.getQuantity());
        dto.setUnitPrice(orderItem.getUnitPrice());
        dto.setTotalPrice(orderItem.getTotalPrice());
        dto.setCreatedDt(orderItem.getCreatedDt());
        dto.setModifiedDt(orderItem.getModifiedDt());

        return dto;
    }

    /**
     * Update existing OrderItem entity with data from DTO
     * @param existingEntity Existing OrderItem entity
     * @param dto OrderItemDto with updated data
     * @return Updated OrderItem entity
     */
    public OrderItem updateEntityFromDto(OrderItem existingEntity, OrderItemDto dto) {
        if (existingEntity == null || dto == null) {
            return existingEntity;
        }

        if (dto.getProductName() != null) {
            existingEntity.setProductName(dto.getProductName());
        }
        if (dto.getProductCode() != null) {
            existingEntity.setProductCode(dto.getProductCode());
        }
        if (dto.getProductCategoryName() != null) {
            existingEntity.setProductCategoryName(dto.getProductCategoryName());
        }
        if (dto.getProductTypeName() != null) {
            existingEntity.setProductTypeName(dto.getProductTypeName());
        }
        if (dto.getProductAttrJson() != null) {
            existingEntity.setProductAttrJson(dto.getProductAttrJson());
        }
        if (dto.getQuantity() != null) {
            existingEntity.setQuantity(dto.getQuantity());
        }
        if (dto.getUnitPrice() != null) {
            existingEntity.setUnitPrice(dto.getUnitPrice());
        }
        if (dto.getTotalPrice() != null) {
            existingEntity.setTotalPrice(dto.getTotalPrice());
        }

        return existingEntity;
    }
}