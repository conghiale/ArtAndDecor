package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.PaymentMethodDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.PaymentMethod;
import org.artanddecor.repository.PaymentMethodRepository;
import org.artanddecor.services.PaymentMethodService;
import org.artanddecor.utils.PaymentMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of PaymentMethodService interface
 * Business logic for payment method management operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentMethodServiceImpl.class);

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMapperUtil paymentMapperUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentMethodDto> getAllPaymentMethods(Pageable pageable) {
        logger.debug("Getting all payment methods with pagination: {}", pageable);
        Page<PaymentMethod> methodPage = paymentMethodRepository.findAll(pageable);
        return methodPage.map(paymentMapperUtil::mapPaymentMethodToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentMethodDto getPaymentMethodById(Long paymentMethodId) {
        logger.debug("Getting payment method by ID: {}", paymentMethodId);
        PaymentMethod method = paymentMethodRepository.findById(paymentMethodId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with ID: " + paymentMethodId));
        return paymentMapperUtil.mapPaymentMethodToDto(method);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentMethodDto getPaymentMethodByName(String paymentMethodName) {
        logger.debug("Getting payment method by name: {}", paymentMethodName);
        PaymentMethod method = paymentMethodRepository.findByPaymentMethodName(paymentMethodName)
            .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with name: " + paymentMethodName));
        return paymentMapperUtil.mapPaymentMethodToDto(method);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethodDto> getAllEnabledPaymentMethods() {
        logger.debug("Getting all enabled payment methods");
        List<PaymentMethod> methods = paymentMethodRepository.findByPaymentMethodEnabledTrueOrderByPaymentMethodName();
        return methods.stream()
            .map(paymentMapperUtil::mapPaymentMethodToDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllEnabledPaymentMethodNames() {
        logger.debug("Getting all enabled payment method names");
        return paymentMethodRepository.findAllEnabledPaymentMethodNames();
    }

    @Override
    public PaymentMethodDto createPaymentMethod(PaymentMethodDto paymentMethodDto) {
        logger.debug("Creating new payment method: {}", paymentMethodDto.getPaymentMethodName());
        
        // Check if name already exists
        if (paymentMethodRepository.existsByPaymentMethodName(paymentMethodDto.getPaymentMethodName())) {
            throw new IllegalArgumentException("Payment method name already exists: " + paymentMethodDto.getPaymentMethodName());
        }

        PaymentMethod method = paymentMapperUtil.mapPaymentMethodToEntity(paymentMethodDto);
        PaymentMethod savedMethod = paymentMethodRepository.save(method);
        logger.info("Created payment method with ID: {} and name: {}", 
                   savedMethod.getPaymentMethodId(), savedMethod.getPaymentMethodName());
        
        return paymentMapperUtil.mapPaymentMethodToDto(savedMethod);
    }

    @Override
    public PaymentMethodDto updatePaymentMethod(Long paymentMethodId, PaymentMethodDto paymentMethodDto) {
        logger.debug("Updating payment method with ID: {}", paymentMethodId);
        
        PaymentMethod existingMethod = paymentMethodRepository.findById(paymentMethodId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with ID: " + paymentMethodId));

        // Check if new name conflicts with existing (excluding current record)
        if (!existingMethod.getPaymentMethodName().equals(paymentMethodDto.getPaymentMethodName()) &&
            paymentMethodRepository.existsByPaymentMethodName(paymentMethodDto.getPaymentMethodName())) {
            throw new IllegalArgumentException("Payment method name already exists: " + paymentMethodDto.getPaymentMethodName());
        }

        // Update fields
        existingMethod.setPaymentMethodName(paymentMethodDto.getPaymentMethodName());
        existingMethod.setPaymentMethodDisplayName(paymentMethodDto.getPaymentMethodDisplayName());
        existingMethod.setPaymentMethodRemark(paymentMethodDto.getPaymentMethodRemark());
        existingMethod.setPaymentMethodEnabled(paymentMethodDto.getPaymentMethodEnabled());

        PaymentMethod updatedMethod = paymentMethodRepository.save(existingMethod);
        logger.info("Updated payment method with ID: {}", updatedMethod.getPaymentMethodId());
        
        return paymentMapperUtil.mapPaymentMethodToDto(updatedMethod);
    }

    @Override
    public PaymentMethodDto togglePaymentMethodEnabled(Long paymentMethodId) {
        logger.debug("Toggling enabled status for payment method ID: {}", paymentMethodId);
        
        PaymentMethod method = paymentMethodRepository.findById(paymentMethodId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with ID: " + paymentMethodId));

        method.setPaymentMethodEnabled(!method.getPaymentMethodEnabled());
        PaymentMethod updatedMethod = paymentMethodRepository.save(method);
        logger.info("Toggled enabled status for payment method ID: {} to: {}", 
                   paymentMethodId, updatedMethod.getPaymentMethodEnabled());
        
        return paymentMapperUtil.mapPaymentMethodToDto(updatedMethod);
    }

    @Override
    public void deletePaymentMethod(Long paymentMethodId) {
        logger.debug("Deleting payment method with ID: {}", paymentMethodId);
        
        if (!paymentMethodRepository.existsById(paymentMethodId)) {
            throw new ResourceNotFoundException("Payment method not found with ID: " + paymentMethodId);
        }
        
        paymentMethodRepository.deleteById(paymentMethodId);
        logger.info("Deleted payment method with ID: {}", paymentMethodId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentMethodDto> searchPaymentMethodsByCriteria(
            Long paymentMethodId, String paymentMethodName, Boolean paymentMethodEnabled,
            String textSearch, Pageable pageable) {
        
        logger.debug("Searching payment methods with criteria");
        
        Page<PaymentMethod> methodPage = paymentMethodRepository.findByCriteria(
            paymentMethodId, paymentMethodName, paymentMethodEnabled, textSearch, pageable
        );
        
        return methodPage.map(paymentMapperUtil::mapPaymentMethodToDto);
    }
}