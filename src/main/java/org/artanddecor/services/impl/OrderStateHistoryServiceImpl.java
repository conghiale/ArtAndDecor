package org.artanddecor.services.impl;

import org.artanddecor.dto.OrderStateHistoryDto;
import org.artanddecor.model.Order;
import org.artanddecor.model.OrderState;
import org.artanddecor.model.OrderStateHistory;
import org.artanddecor.model.User;
import org.artanddecor.repository.OrderRepository;
import org.artanddecor.repository.OrderStateHistoryRepository;
import org.artanddecor.repository.OrderStateRepository;
import org.artanddecor.repository.UserRepository;
import org.artanddecor.services.OrderStateHistoryService;
import org.artanddecor.utils.OrderMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * OrderStateHistory Service Implementation for business logic operations
 * Updated to support new API requirements
 */
@Service
@Transactional
public class OrderStateHistoryServiceImpl implements OrderStateHistoryService {

    @Autowired
    private OrderStateHistoryRepository orderStateHistoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStateRepository orderStateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderMapperUtil orderMapperUtil; // Using consolidated mapper

    @Override
    @Transactional
    public OrderStateHistoryDto createOrderStateHistory(
            Long orderId,
            Long oldOrderStateId, 
            Long newOrderStateId,
            Long changedByUserId) {
        
        // Validate: OrderStateHistory requires oldOrderStateId (database constraint: OLD_STATE_ID NOT NULL)
        // This service is only for tracking state transitions, not for initial state creation
        if (oldOrderStateId == null) {
            throw new IllegalArgumentException("Cannot create OrderStateHistory without oldOrderStateId. " +
                    "OrderStateHistory is only for tracking state transitions, not initial state creation.");
        }
        
        // Get the related entities
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        OrderState oldState = orderStateRepository.findById(oldOrderStateId)
                .orElseThrow(() -> new RuntimeException("Old Order State not found with ID: " + oldOrderStateId));
        
        OrderState newState = orderStateRepository.findById(newOrderStateId)
                .orElseThrow(() -> new RuntimeException("New Order State not found with ID: " + newOrderStateId));
        
        // Handle changed by user - can be null for guest orders
        User changedByUser = null;
        if (changedByUserId != null) {
            changedByUser = userRepository.findById(changedByUserId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + changedByUserId));
        }
        
        // Create the new OrderStateHistory entity
        OrderStateHistory orderStateHistory = new OrderStateHistory();
        orderStateHistory.setOrder(order);
        orderStateHistory.setOldState(oldState);
        orderStateHistory.setNewState(newState);
        orderStateHistory.setChangedByUser(changedByUser);
        // Created date is set automatically by @PrePersist
        
        // Save to repository
        OrderStateHistory savedHistory = orderStateHistoryRepository.save(orderStateHistory);
        
        // Return mapped DTO
        return orderMapperUtil.mapToDto(savedHistory);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<OrderStateHistoryDto> getOrderStateHistory(
            Long orderId,
            LocalDate fromDate,
            LocalDate toDate,
            Long oldStateId,
            Long newStateId,
            Pageable pageable) {
        
        LocalDateTime fromDateTime = (fromDate != null) ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = (toDate != null) ? toDate.atTime(23, 59, 59) : null;

        return orderStateHistoryRepository
                .findByFilters(orderId, fromDateTime, toDateTime, oldStateId, newStateId, pageable)
                .map(orderMapperUtil::mapToDto);
    }
}