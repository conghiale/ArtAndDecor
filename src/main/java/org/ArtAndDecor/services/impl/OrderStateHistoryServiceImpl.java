package org.ArtAndDecor.services.impl;

import org.ArtAndDecor.dto.OrderStateHistoryDto;
import org.ArtAndDecor.model.Order;
import org.ArtAndDecor.model.OrderState;
import org.ArtAndDecor.model.OrderStateHistory;
import org.ArtAndDecor.model.User;
import org.ArtAndDecor.repository.OrderRepository;
import org.ArtAndDecor.repository.OrderStateHistoryRepository;
import org.ArtAndDecor.repository.OrderStateRepository;
import org.ArtAndDecor.repository.UserRepository;
import org.ArtAndDecor.services.OrderStateHistoryService;
import org.ArtAndDecor.utils.OrderStateHistoryMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderStateHistory Service Implementation for business logic operations
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
    private OrderStateHistoryMapperUtil orderStateHistoryMapperUtil;

    @Override
    @Transactional(readOnly = true)
    public List<OrderStateHistoryDto> getOrderStateHistory(Long orderId) {
        List<OrderStateHistory> historyList = orderStateHistoryRepository.findByOrderIdOrderByStateChangeDateDesc(orderId);
        return historyList.stream()
                .map(orderStateHistoryMapperUtil::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderStateHistoryDto createOrderStateHistory(
            Long orderId,
            Long oldOrderStateId, 
            Long newOrderStateId,
            Long changedByUserId) {
        
        // Get the related entities
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        OrderState oldState = orderStateRepository.findById(oldOrderStateId)
                .orElseThrow(() -> new RuntimeException("Old Order State not found with ID: " + oldOrderStateId));
        
        OrderState newState = orderStateRepository.findById(newOrderStateId)
                .orElseThrow(() -> new RuntimeException("New Order State not found with ID: " + newOrderStateId));
        
        User changedByUser = userRepository.findById(changedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + changedByUserId));
        
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
        return orderStateHistoryMapperUtil.mapToDto(savedHistory);
    }
}