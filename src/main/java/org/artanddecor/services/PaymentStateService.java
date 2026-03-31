package org.artanddecor.services;

import org.artanddecor.dto.PaymentStateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * PaymentState Service Interface
 * Business logic for payment state management operations
 */
public interface PaymentStateService {

    /**
     * Get all payment states with pagination
     */
    Page<PaymentStateDto> getAllPaymentStates(Pageable pageable);

    /**
     * Get payment state by ID
     */
    PaymentStateDto getPaymentStateById(Long paymentStateId);

    /**
     * Get payment state by name
     */
    PaymentStateDto getPaymentStateByName(String paymentStateName);

    /**
     * Get all enabled payment states
     */
    List<PaymentStateDto> getAllEnabledPaymentStates();

    /**
     * Get all enabled payment state names
     */
    List<String> getAllEnabledPaymentStateNames();

    /**
     * Create new payment state
     */
    PaymentStateDto createPaymentState(PaymentStateDto paymentStateDto);

    /**
     * Update payment state
     */
    PaymentStateDto updatePaymentState(Long paymentStateId, PaymentStateDto paymentStateDto);

    /**
     * Toggle payment state enabled status
     */
    PaymentStateDto togglePaymentStateEnabled(Long paymentStateId);

    /**
     * Delete payment state
     */
    void deletePaymentState(Long paymentStateId);

    /**
     * Search payment states by criteria with pagination
     */
    Page<PaymentStateDto> searchPaymentStatesByCriteria(
            Long paymentStateId, String paymentStateName, Boolean paymentStateEnabled,
            String textSearch, Pageable pageable
    );
}