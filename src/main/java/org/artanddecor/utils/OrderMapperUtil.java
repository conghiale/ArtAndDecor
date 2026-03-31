package org.artanddecor.utils;

import org.artanddecor.dto.OrderDto;
import org.artanddecor.dto.OrderItemDto;
import org.artanddecor.dto.OrderStateDto;
import org.artanddecor.dto.OrderStateHistoryDto;
import org.artanddecor.model.Order;
import org.artanddecor.model.OrderItem;
import org.artanddecor.model.OrderState;
import org.artanddecor.model.OrderStateHistory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Unified Order Mapper Utility for converting between Entity and DTO
 * Consolidates all Order-related mappers into single class for efficiency
 * Version: 8.0 - DISCOUNT functionality removed
 */
@Component
public class OrderMapperUtil {

    // ===== ORDER MAPPING =====

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
        dto.setAddressLine(order.getAddressLine());
        dto.setCity(order.getCity());
        dto.setWard(order.getWard());
        dto.setCountry(order.getCountry());
        dto.setSubtotalAmount(order.getSubtotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setShippingFeeAmount(order.getShippingFeeAmount());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderNote(order.getOrderNote());
        dto.setCreatedDt(order.getCreatedDt());
        dto.setModifiedDt(order.getModifiedDt());

        // Map nested OrderState
        if (order.getOrderState() != null) {
            dto.setOrderState(mapToDto(order.getOrderState()));
            dto.setOrderStateId(order.getOrderState().getOrderStateId());
            dto.setOrderStateName(order.getOrderState().getOrderStateName());
        }

        // DISCOUNT functionality removed - set null values
        dto.setDiscount(null);
        dto.setDiscountId(null);
        dto.setDiscountCode(null);
        dto.setDiscountType(null);
        dto.setDiscountValue(null);

        // Map OrderItems if loaded
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            dto.setOrderItems(order.getOrderItems().stream()
                    .map(this::mapToDto)
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
        entity.setAddressLine(orderDto.getAddressLine());
        entity.setCity(orderDto.getCity());
        entity.setWard(orderDto.getWard());
        entity.setCountry(orderDto.getCountry());
        entity.setSubtotalAmount(orderDto.getSubtotalAmount());
        entity.setDiscountAmount(orderDto.getDiscountAmount());
        entity.setShippingFeeAmount(orderDto.getShippingFeeAmount());
        entity.setTotalAmount(orderDto.getTotalAmount());
        entity.setOrderNote(orderDto.getOrderNote());
        
        // DISCOUNT fields removed but structure maintained for compatibility
        entity.setDiscountType(null);
        entity.setDiscountValue(null);

        return entity;
    }

    /**
     * Map Order entity to OrderDto without loading OrderItems (for performance)
     * @param order Order entity
     * @return OrderDto without order items
     */
    public OrderDto mapToDtoWithoutItems(Order order) {
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
        dto.setAddressLine(order.getAddressLine());
        dto.setCity(order.getCity());
        dto.setWard(order.getWard());
        dto.setCountry(order.getCountry());
        dto.setSubtotalAmount(order.getSubtotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setShippingFeeAmount(order.getShippingFeeAmount());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderNote(order.getOrderNote());
        dto.setCreatedDt(order.getCreatedDt());
        dto.setModifiedDt(order.getModifiedDt());

        // Map nested OrderState
        if (order.getOrderState() != null) {
            dto.setOrderState(mapToDto(order.getOrderState()));
            dto.setOrderStateId(order.getOrderState().getOrderStateId());
            dto.setOrderStateName(order.getOrderState().getOrderStateName());
        }

        // DISCOUNT functionality removed - set null values
        dto.setDiscount(null);
        dto.setDiscountId(null);
        dto.setDiscountCode(null);
        dto.setDiscountType(null);
        dto.setDiscountValue(null);

        // Skip OrderItems loading for performance
        dto.setOrderItems(null);

        return dto;
    }

    // ===== ORDER STATE MAPPING =====

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

        return entity;
    }

    // ===== ORDER ITEM MAPPING =====

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

        // Set product attributes JSON snapshot
        dto.setProductAttrJson(orderItem.getProductAttrJson());

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

        return entity;
    }

    // ===== ORDER STATE HISTORY MAPPING =====

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
            dto.setOldState(mapToDto(orderStateHistory.getOldState()));
        }
        
        if (orderStateHistory.getNewState() != null) {
            dto.setNewState(mapToDto(orderStateHistory.getNewState()));
        }

        return dto;
    }

    /**
     * Map OrderStateHistoryDto to OrderStateHistory entity
     * @param dto OrderStateHistoryDto
     * @return OrderStateHistory entity
     */
    public OrderStateHistory mapToEntity(OrderStateHistoryDto dto) {
        if (dto == null) {
            return null;
        }

        OrderStateHistory entity = new OrderStateHistory();
        entity.setOrderStateHistoryId(dto.getHistoryId());
        // Note: changedByUser will be set by service layer using User entity
        // entity.setChangedByUserId(dto.getChangedByUserId()); // Not available in entity
        // entity.setChangedByUserName(dto.getChangedByUserName()); // Not available in entity

        return entity;
    }
}