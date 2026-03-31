package org.artanddecor.services.impl;

import org.artanddecor.dto.OrderStateDto;
import org.artanddecor.model.OrderState;
import org.artanddecor.repository.OrderStateRepository;
import org.artanddecor.services.OrderStateService;
import org.artanddecor.utils.OrderMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderState Service Implementation for business logic operations
 * Updated to support new API requirements
 */
@Service
@Transactional
public class OrderStateServiceImpl implements OrderStateService {

    @Autowired
    private OrderStateRepository orderStateRepository;

    @Autowired
    private OrderMapperUtil orderMapperUtil; // Using consolidated mapper

    @Override
    @Transactional(readOnly = true)
    public List<OrderStateDto> getAllOrderStates() {
        List<OrderState> orderStates = orderStateRepository.findAll();
        return orderStates.stream()
                .map(orderMapperUtil::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderStateDto> getAllEnabledOrderStates() {
        List<OrderState> enabledOrderStates = orderStateRepository.findByOrderStateEnabledOrderByOrderStateName(true);
        return enabledOrderStates.stream()
                .map(orderMapperUtil::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<OrderStateDto> getOrderStates(Long orderStateId, String orderStateName, Boolean enabled, Pageable pageable) {
        List<OrderState> allOrderStates = orderStateRepository.findAll();
        
        // Apply filters
        List<OrderState> filteredOrderStates = allOrderStates.stream()
                .filter(orderState -> {
                    if (orderStateId != null && !orderState.getOrderStateId().equals(orderStateId)) {
                        return false;
                    }
                    if (orderStateName != null && !orderState.getOrderStateName().toLowerCase()
                            .contains(orderStateName.toLowerCase())) {
                        return false;
                    }
                    if (enabled != null && !orderState.getOrderStateEnabled().equals(enabled)) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        
        // Convert to DTO
        List<OrderStateDto> dtoList = filteredOrderStates.stream()
                .map(orderMapperUtil::mapToDto)
                .collect(Collectors.toList());
        
        // Apply pagination manually since we're filtering in memory
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtoList.size());
        
        List<OrderStateDto> pageContent = dtoList.subList(start, end);
        return new PageImpl<>(pageContent, pageable, dtoList.size());
    }
}
