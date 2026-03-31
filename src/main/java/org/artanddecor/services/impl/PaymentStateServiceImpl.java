package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.PaymentStateDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.PaymentState;
import org.artanddecor.repository.PaymentStateRepository;
import org.artanddecor.services.PaymentStateService;
import org.artanddecor.utils.PaymentMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of PaymentStateService interface
 * Business logic for payment state management operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentStateServiceImpl implements PaymentStateService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentStateServiceImpl.class);

    private final PaymentStateRepository paymentStateRepository;
    private final PaymentMapperUtil paymentMapperUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentStateDto> getAllPaymentStates(Pageable pageable) {
        logger.debug("Getting all payment states with pagination: {}", pageable);
        Page<PaymentState> statePage = paymentStateRepository.findAll(pageable);
        return statePage.map(paymentMapperUtil::mapPaymentStateToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStateDto getPaymentStateById(Long paymentStateId) {
        logger.debug("Getting payment state by ID: {}", paymentStateId);
        PaymentState state = paymentStateRepository.findById(paymentStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment state not found with ID: " + paymentStateId));
        return paymentMapperUtil.mapPaymentStateToDto(state);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStateDto getPaymentStateByName(String paymentStateName) {
        logger.debug("Getting payment state by name: {}", paymentStateName);
        PaymentState state = paymentStateRepository.findByPaymentStateName(paymentStateName)
            .orElseThrow(() -> new ResourceNotFoundException("Payment state not found with name: " + paymentStateName));
        return paymentMapperUtil.mapPaymentStateToDto(state);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentStateDto> getAllEnabledPaymentStates() {
        logger.debug("Getting all enabled payment states");
        List<PaymentState> states = paymentStateRepository.findByPaymentStateEnabledTrueOrderByPaymentStateName();
        return states.stream()
            .map(paymentMapperUtil::mapPaymentStateToDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllEnabledPaymentStateNames() {
        logger.debug("Getting all enabled payment state names");
        return paymentStateRepository.findAllEnabledPaymentStateNames();
    }

    @Override
    public PaymentStateDto createPaymentState(PaymentStateDto paymentStateDto) {
        logger.debug("Creating new payment state: {}", paymentStateDto.getPaymentStateName());
        
        // Check if name already exists
        if (paymentStateRepository.existsByPaymentStateName(paymentStateDto.getPaymentStateName())) {
            throw new IllegalArgumentException("Payment state name already exists: " + paymentStateDto.getPaymentStateName());
        }

        PaymentState state = paymentMapperUtil.mapPaymentStateToEntity(paymentStateDto);
        PaymentState savedState = paymentStateRepository.save(state);
        logger.info("Created payment state with ID: {} and name: {}", 
                   savedState.getPaymentStateId(), savedState.getPaymentStateName());
        
        return paymentMapperUtil.mapPaymentStateToDto(savedState);
    }

    @Override
    public PaymentStateDto updatePaymentState(Long paymentStateId, PaymentStateDto paymentStateDto) {
        logger.debug("Updating payment state with ID: {}", paymentStateId);
        
        PaymentState existingState = paymentStateRepository.findById(paymentStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment state not found with ID: " + paymentStateId));

        // Check if new name conflicts with existing (excluding current record)
        if (!existingState.getPaymentStateName().equals(paymentStateDto.getPaymentStateName()) &&
            paymentStateRepository.existsByPaymentStateName(paymentStateDto.getPaymentStateName())) {
            throw new IllegalArgumentException("Payment state name already exists: " + paymentStateDto.getPaymentStateName());
        }

        // Update fields
        existingState.setPaymentStateName(paymentStateDto.getPaymentStateName());
        existingState.setPaymentStateDisplayName(paymentStateDto.getPaymentStateDisplayName());
        existingState.setPaymentStateRemark(paymentStateDto.getPaymentStateRemark());
        existingState.setPaymentStateEnabled(paymentStateDto.getPaymentStateEnabled());

        PaymentState updatedState = paymentStateRepository.save(existingState);
        logger.info("Updated payment state with ID: {}", updatedState.getPaymentStateId());
        
        return paymentMapperUtil.mapPaymentStateToDto(updatedState);
    }

    @Override
    public PaymentStateDto togglePaymentStateEnabled(Long paymentStateId) {
        logger.debug("Toggling enabled status for payment state ID: {}", paymentStateId);
        
        PaymentState state = paymentStateRepository.findById(paymentStateId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment state not found with ID: " + paymentStateId));

        state.setPaymentStateEnabled(!state.getPaymentStateEnabled());
        PaymentState updatedState = paymentStateRepository.save(state);
        logger.info("Toggled enabled status for payment state ID: {} to: {}", 
                   paymentStateId, updatedState.getPaymentStateEnabled());
        
        return paymentMapperUtil.mapPaymentStateToDto(updatedState);
    }

    @Override
    public void deletePaymentState(Long paymentStateId) {
        logger.debug("Deleting payment state with ID: {}", paymentStateId);
        
        if (!paymentStateRepository.existsById(paymentStateId)) {
            throw new ResourceNotFoundException("Payment state not found with ID: " + paymentStateId);
        }
        
        paymentStateRepository.deleteById(paymentStateId);
        logger.info("Deleted payment state with ID: {}", paymentStateId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentStateDto> searchPaymentStatesByCriteria(
            Long paymentStateId, String paymentStateName, Boolean paymentStateEnabled,
            String textSearch, Pageable pageable) {
        
        logger.debug("Searching payment states with criteria");
        
        Page<PaymentState> statePage = paymentStateRepository.findByCriteria(
            paymentStateId, paymentStateName, paymentStateEnabled, textSearch, pageable
        );
        
        return statePage.map(paymentMapperUtil::mapPaymentStateToDto);
    }
}