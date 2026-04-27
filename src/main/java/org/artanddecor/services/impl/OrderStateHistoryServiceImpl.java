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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

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
        
        List<OrderStateHistory> allHistory = orderStateHistoryRepository.findAll();
        
        // Apply filters
        List<OrderStateHistory> filteredHistory = allHistory.stream()
                .filter(history -> {
                    // Filter by order ID
                    if (orderId != null && !history.getOrder().getOrderId().equals(orderId)) {
                        return false;
                    }
                    
                    // Filter by date range
                    LocalDateTime historyDate = history.getCreatedDt();
                    if (fromDate != null && historyDate.isBefore(fromDate.atStartOfDay())) {
                        return false;
                    }
                    if (toDate != null && historyDate.isAfter(toDate.atTime(LocalTime.MAX))) {
                        return false;
                    }
                    
                    // Filter by old state ID
                    if (oldStateId != null && !history.getOldState().getOrderStateId().equals(oldStateId)) {
                        return false;
                    }
                    
                    // Filter by new state ID
                    if (newStateId != null && !history.getNewState().getOrderStateId().equals(newStateId)) {
                        return false;
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
        
        // Convert to DTO
        List<OrderStateHistoryDto> dtoList = filteredHistory.stream()
                .map(orderMapperUtil::mapToDto)
                .collect(Collectors.toList());
        
        // Apply pagination manually since we're filtering in memory
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtoList.size());
        
        List<OrderStateHistoryDto> pageContent = dtoList.subList(start, end);
        return new PageImpl<>(pageContent, pageable, dtoList.size());
    }
}