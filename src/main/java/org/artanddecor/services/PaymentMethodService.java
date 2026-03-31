package org.artanddecor.services;

import org.artanddecor.dto.PaymentMethodDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * PaymentMethod Service Interface
 * Business logic for payment method management operations
 */
public interface PaymentMethodService {

    /**
     * Get all payment methods with pagination
     */
    Page<PaymentMethodDto> getAllPaymentMethods(Pageable pageable);

    /**
     * Get payment method by ID
     */
    PaymentMethodDto getPaymentMethodById(Long paymentMethodId);

    /**
     * Get payment method by name
     */
    PaymentMethodDto getPaymentMethodByName(String paymentMethodName);

    /**
     * Get all enabled payment methods
     */
    List<PaymentMethodDto> getAllEnabledPaymentMethods();

    /**
     * Get all enabled payment method names
     */
    List<String> getAllEnabledPaymentMethodNames();

    /**
     * Create new payment method
     */
    PaymentMethodDto createPaymentMethod(PaymentMethodDto paymentMethodDto);

    /**
     * Update payment method
     */
    PaymentMethodDto updatePaymentMethod(Long paymentMethodId, PaymentMethodDto paymentMethodDto);

    /**
     * Toggle payment method enabled status
     */
    PaymentMethodDto togglePaymentMethodEnabled(Long paymentMethodId);

    /**
     * Delete payment method
     */
    void deletePaymentMethod(Long paymentMethodId);

    /**
     * Search payment methods by criteria with pagination
     */
    Page<PaymentMethodDto> searchPaymentMethodsByCriteria(
            Long paymentMethodId, String paymentMethodName, Boolean paymentMethodEnabled,
            String textSearch, Pageable pageable
    );
}