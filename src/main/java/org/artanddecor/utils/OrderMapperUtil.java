package org.artanddecor.utils;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.OrderDto;
import org.artanddecor.model.Order;
import org.springframework.stereotype.Component;

/**
 * Order Mapper Utility for converting between Entity and DTO
 */
@Component
@RequiredArgsConstructor
public class OrderMapperUtil {

    private final OrderStateMapperUtil orderStateMapperUtil;

    private final DiscountMapperUtil discountMapperUtil;

    private final OrderItemMapperUtil orderItemMapperUtil;

    /**
     * Map Order entity to OrderDto
     * @param order Order entity
     * @return OrderDto
     */
    public OrderDto mapToDto(Order order) {
        if (order == null) {
            return null;
        }

        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getOrderId());
        dto.setOrderCode(order.getOrderCode());
        dto.setOrderSlug(order.getOrderSlug());
        dto.setUserId(order.getUser() != null ? order.getUser().getUserId() : null);
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerPhoneNumber(order.getCustomerPhoneNumber());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setCustomerAddress(order.getCustomerAddress());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setReceiverEmail(order.getReceiverEmail());
        dto.setReceiverAddress(order.getReceiverAddress());
        dto.setSubtotalAmount(order.getSubtotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setShippingFeeAmount(order.getShippingFeeAmount());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderNote(order.getOrderNote());
        dto.setCreatedDt(order.getCreatedDt());
        dto.setModifiedDt(order.getModifiedDt());

        // Map nested OrderState
        if (order.getOrderState() != null) {
            dto.setOrderState(orderStateMapperUtil.mapToDto(order.getOrderState()));
            dto.setOrderStateId(order.getOrderState().getOrderStateId());
            dto.setOrderStateName(order.getOrderState().getOrderStateName());
        }

        // Map nested Discount
        if (order.getDiscount() != null) {
            dto.setDiscount(discountMapperUtil.mapToDto(order.getDiscount()));
            dto.setDiscountId(order.getDiscount().getDiscountId());
            dto.setDiscountCode(order.getDiscount().getDiscountCode());
        }
        
        // Map discount snapshot data
        dto.setDiscountType(order.getDiscountType());
        dto.setDiscountValue(order.getDiscountValue());

        // Map OrderItems if loaded
        if (order.getOrderItems() != null) {
            dto.setOrderItems(order.getOrderItems().stream()
                    .map(orderItemMapperUtil::mapToDto)
                    .collect(java.util.stream.Collectors.toList()));
        }

        return dto;
    }

    /**
     * Map OrderDto to Order entity
     * @param orderDto OrderDto
     * @return Order entity
     */
    public Order mapToEntity(OrderDto orderDto) {
        if (orderDto == null) {
            return null;
        }

        Order entity = new Order();
        entity.setOrderId(orderDto.getOrderId());
        entity.setOrderCode(orderDto.getOrderCode());
        entity.setOrderSlug(orderDto.getOrderSlug());
        entity.setCustomerName(orderDto.getCustomerName());
        entity.setCustomerPhoneNumber(orderDto.getCustomerPhoneNumber());
        entity.setCustomerEmail(orderDto.getCustomerEmail());
        entity.setCustomerAddress(orderDto.getCustomerAddress());
        entity.setReceiverName(orderDto.getReceiverName());
        entity.setReceiverPhone(orderDto.getReceiverPhone());
        entity.setReceiverEmail(orderDto.getReceiverEmail());
        entity.setReceiverAddress(orderDto.getReceiverAddress());
        entity.setSubtotalAmount(orderDto.getSubtotalAmount());
        entity.setDiscountAmount(orderDto.getDiscountAmount());
        entity.setShippingFeeAmount(orderDto.getShippingFeeAmount());
        entity.setTotalAmount(orderDto.getTotalAmount());
        entity.setOrderNote(orderDto.getOrderNote());
        entity.setCreatedDt(orderDto.getCreatedDt());
        entity.setModifiedDt(orderDto.getModifiedDt());

        // Map nested OrderState
        if (orderDto.getOrderState() != null) {
            entity.setOrderState(orderStateMapperUtil.mapToEntity(orderDto.getOrderState()));
        }

        // Map nested Discount
        if (orderDto.getDiscount() != null) {
            entity.setDiscount(discountMapperUtil.mapToEntity(orderDto.getDiscount()));
        }
        
        // Map discount snapshot data
        entity.setDiscountCode(orderDto.getDiscountCode());
        entity.setDiscountType(orderDto.getDiscountType());
        entity.setDiscountValue(orderDto.getDiscountValue());

        // Map OrderItems
        if (orderDto.getOrderItems() != null) {
            entity.setOrderItems(orderDto.getOrderItems().stream()
                    .map(orderItemMapperUtil::mapToEntity)
                    .collect(java.util.stream.Collectors.toList()));
        }

        return entity;
    }

    /**
     * Map Order entity to OrderDto without nested collections (for performance)
     * @param order Order entity
     * @return OrderDto without nested collections
     */
    public OrderDto mapToDtoWithoutItems(Order order) {
        if (order == null) {
            return null;
        }

        OrderDto dto = mapToDto(order);
        dto.setOrderItems(null); // Remove items for performance

        return dto;
    }

    /**
     * Update existing Order entity with data from DTO
     * @param existingEntity Existing Order entity
     * @param dto OrderDto with updated data
     * @return Updated Order entity
     */
    public Order updateEntityFromDto(Order existingEntity, OrderDto dto) {
        if (existingEntity == null || dto == null) {
            return existingEntity;
        }

        if (dto.getOrderCode() != null) {
            existingEntity.setOrderCode(dto.getOrderCode());
        }
        if (dto.getOrderSlug() != null) {
            existingEntity.setOrderSlug(dto.getOrderSlug());
        }
        if (dto.getCustomerName() != null) {
            existingEntity.setCustomerName(dto.getCustomerName());
        }
        if (dto.getCustomerPhoneNumber() != null) {
            existingEntity.setCustomerPhoneNumber(dto.getCustomerPhoneNumber());
        }
        if (dto.getCustomerEmail() != null) {
            existingEntity.setCustomerEmail(dto.getCustomerEmail());
        }
        if (dto.getCustomerAddress() != null) {
            existingEntity.setCustomerAddress(dto.getCustomerAddress());
        }
        if (dto.getReceiverName() != null) {
            existingEntity.setReceiverName(dto.getReceiverName());
        }
        if (dto.getReceiverPhone() != null) {
            existingEntity.setReceiverPhone(dto.getReceiverPhone());
        }
        if (dto.getReceiverEmail() != null) {
            existingEntity.setReceiverEmail(dto.getReceiverEmail());
        }
        if (dto.getReceiverAddress() != null) {
            existingEntity.setReceiverAddress(dto.getReceiverAddress());
        }
        if (dto.getSubtotalAmount() != null) {
            existingEntity.setSubtotalAmount(dto.getSubtotalAmount());
        }
        if (dto.getDiscountAmount() != null) {
            existingEntity.setDiscountAmount(dto.getDiscountAmount());
        }
        if (dto.getShippingFeeAmount() != null) {
            existingEntity.setShippingFeeAmount(dto.getShippingFeeAmount());
        }
        if (dto.getTotalAmount() != null) {
            existingEntity.setTotalAmount(dto.getTotalAmount());
        }
        if (dto.getOrderNote() != null) {
            existingEntity.setOrderNote(dto.getOrderNote());
        }
        
        // Update discount snapshot data
        if (dto.getDiscountCode() != null) {
            existingEntity.setDiscountCode(dto.getDiscountCode());
        }
        if (dto.getDiscountType() != null) {
            existingEntity.setDiscountType(dto.getDiscountType());
        }
        if (dto.getDiscountValue() != null) {
            existingEntity.setDiscountValue(dto.getDiscountValue());
        }

        return existingEntity;
    }
}