package org.artanddecor.services.impl;

import org.artanddecor.dto.OrderStateDto;
import org.artanddecor.model.OrderState;
import org.artanddecor.repository.OrderStateRepository;
import org.artanddecor.services.OrderStateService;
import org.artanddecor.utils.OrderStateMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderState Service Implementation for business logic operations
 */
@Service
@Transactional
public class OrderStateServiceImpl implements OrderStateService {

    @Autowired
    private OrderStateRepository orderStateRepository;

    @Autowired
    private OrderStateMapperUtil orderStateMapperUtil;

    @Override
    @Transactional(readOnly = true)
    public List<OrderStateDto> getAllOrderStates() {
        List<OrderState> orderStates = orderStateRepository.findAll();
        return orderStates.stream()
                .map(orderStateMapperUtil::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderStateDto> getAllEnabledOrderStates() {
        List<OrderState> enabledOrderStates = orderStateRepository.findByOrderStateEnabledOrderByOrderStateName(true);
        return enabledOrderStates.stream()
                .map(orderStateMapperUtil::mapToDto)
                .collect(Collectors.toList());
    }
}