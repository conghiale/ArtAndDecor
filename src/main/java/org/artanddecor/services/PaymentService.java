package org.artanddecor.services;

import org.artanddecor.dto.PaymentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Payment Service Interface
 * Business logic for payment management operations
 * Note: No create method - payments are created automatically during order checkout
 */
public interface PaymentService {

    /**
     * Get all payments with pagination
     */
    Page<PaymentDto> getAllPayments(Pageable pageable);

    /**
     * Get payment by ID
     */
    PaymentDto getPaymentById(Long paymentId);

    /**
     * Get payment by slug
     */
    PaymentDto getPaymentBySlug(String paymentSlug);

    /**
     * Get payment by transaction ID
     */
    PaymentDto getPaymentByTransactionId(String transactionId);

    /**
     * Get payments by order ID
     */
    List<PaymentDto> getPaymentsByOrderId(Long orderId);

    /**
     * Update payment (Admin only - for status changes, remarks, etc.)
     */
    PaymentDto updatePayment(Long paymentId, PaymentDto paymentDto);

    /**
     * Search payments by comprehensive criteria with pagination
     */
    Page<PaymentDto> searchPaymentsByCriteria(
            Long paymentId, Long orderId, String paymentSlug,
            Long paymentMethodId, Long paymentStateId, String transactionId,
            BigDecimal minAmount, BigDecimal maxAmount, String textSearch,
            Pageable pageable
    );

    /**
     * Get total payment count
     */
    long getTotalPaymentCount();

    /**
     * Get payment statistics by payment state
     */
    Map<String, Long> getPaymentStatsByState();

    /**
     * Get payment statistics by payment method
     */
    Map<String, Long> getPaymentStatsByMethod();

    /**
     * Get total payment amount by state
     */
    Map<String, BigDecimal> getTotalAmountByState();
}