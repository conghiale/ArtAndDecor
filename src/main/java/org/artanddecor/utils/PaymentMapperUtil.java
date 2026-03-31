package org.artanddecor.utils;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.PaymentDto;
import org.artanddecor.dto.PaymentMethodDto;
import org.artanddecor.dto.PaymentStateDto;
import org.artanddecor.model.Payment;
import org.artanddecor.model.PaymentMethod;
import org.artanddecor.model.PaymentState;
import org.springframework.stereotype.Component;

/**
 * Payment Mapper Utility - Consolidated mapper for all payment-related entities
 * Handles all payment-related mappings: Payment, PaymentMethod, PaymentState
 */
@Component
@RequiredArgsConstructor
public class PaymentMapperUtil {
    
    // =============================================
    // PAYMENT_METHOD MAPPING METHODS
    // =============================================
    
    /**
     * Map PaymentMethod entity to PaymentMethodDto
     * @param paymentMethod PaymentMethod entity
     * @return PaymentMethodDto
     */
    public PaymentMethodDto mapPaymentMethodToDto(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return null;
        }

        PaymentMethodDto dto = new PaymentMethodDto();
        dto.setPaymentMethodId(paymentMethod.getPaymentMethodId());
        dto.setPaymentMethodName(paymentMethod.getPaymentMethodName());
        dto.setPaymentMethodDisplayName(paymentMethod.getPaymentMethodDisplayName());
        dto.setPaymentMethodRemark(paymentMethod.getPaymentMethodRemark());
        dto.setPaymentMethodEnabled(paymentMethod.getPaymentMethodEnabled());
        dto.setCreatedDt(paymentMethod.getCreatedDt());
        dto.setModifiedDt(paymentMethod.getModifiedDt());

        return dto;
    }

    /**
     * Map PaymentMethodDto to PaymentMethod entity
     * @param dto PaymentMethodDto
     * @return PaymentMethod entity
     */
    public PaymentMethod mapPaymentMethodToEntity(PaymentMethodDto dto) {
        if (dto == null) {
            return null;
        }

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodId(dto.getPaymentMethodId());
        paymentMethod.setPaymentMethodName(dto.getPaymentMethodName());
        paymentMethod.setPaymentMethodDisplayName(dto.getPaymentMethodDisplayName());
        paymentMethod.setPaymentMethodRemark(dto.getPaymentMethodRemark());
        paymentMethod.setPaymentMethodEnabled(dto.getPaymentMethodEnabled());
        paymentMethod.setCreatedDt(dto.getCreatedDt());
        paymentMethod.setModifiedDt(dto.getModifiedDt());

        return paymentMethod;
    }

    // =============================================
    // PAYMENT_STATE MAPPING METHODS
    // =============================================
    
    /**
     * Map PaymentState entity to PaymentStateDto
     * @param paymentState PaymentState entity
     * @return PaymentStateDto
     */
    public PaymentStateDto mapPaymentStateToDto(PaymentState paymentState) {
        if (paymentState == null) {
            return null;
        }

        PaymentStateDto dto = new PaymentStateDto();
        dto.setPaymentStateId(paymentState.getPaymentStateId());
        dto.setPaymentStateName(paymentState.getPaymentStateName());
        dto.setPaymentStateDisplayName(paymentState.getPaymentStateDisplayName());
        dto.setPaymentStateRemark(paymentState.getPaymentStateRemark());
        dto.setPaymentStateEnabled(paymentState.getPaymentStateEnabled());
        dto.setCreatedDt(paymentState.getCreatedDt());
        dto.setModifiedDt(paymentState.getModifiedDt());

        return dto;
    }

    /**
     * Map PaymentStateDto to PaymentState entity
     * @param dto PaymentStateDto
     * @return PaymentState entity
     */
    public PaymentState mapPaymentStateToEntity(PaymentStateDto dto) {
        if (dto == null) {
            return null;
        }

        PaymentState paymentState = new PaymentState();
        paymentState.setPaymentStateId(dto.getPaymentStateId());
        paymentState.setPaymentStateName(dto.getPaymentStateName());
        paymentState.setPaymentStateDisplayName(dto.getPaymentStateDisplayName());
        paymentState.setPaymentStateRemark(dto.getPaymentStateRemark());
        paymentState.setPaymentStateEnabled(dto.getPaymentStateEnabled());
        paymentState.setCreatedDt(dto.getCreatedDt());
        paymentState.setModifiedDt(dto.getModifiedDt());

        return paymentState;
    }

    // =============================================
    // PAYMENT MAPPING METHODS
    // =============================================
    
    /**
     * Map Payment entity to PaymentDto
     * @param payment Payment entity
     * @return PaymentDto
     */
    public PaymentDto mapPaymentToDto(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setPaymentSlug(payment.getPaymentSlug());
        dto.setAmount(payment.getAmount());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentRemark(payment.getPaymentRemark());
        dto.setCreatedDt(payment.getCreatedDt());
        dto.setModifiedDt(payment.getModifiedDt());

        // Map nested entities
        if (payment.getPaymentMethod() != null) {
            dto.setPaymentMethod(mapPaymentMethodToDto(payment.getPaymentMethod()));
        }

        if (payment.getPaymentState() != null) {
            dto.setPaymentState(mapPaymentStateToDto(payment.getPaymentState()));
        }

        // Note: Order relationship should be handled separately in service layer to avoid circular references

        return dto;
    }

    /**
     * Map PaymentDto to Payment entity (for updates)
     * Note: Order, PaymentMethod, and PaymentState relationships should be set separately in service layer
     * @param dto PaymentDto
     * @return Payment entity
     */
    public Payment mapPaymentToEntity(PaymentDto dto) {
        if (dto == null) {
            return null;
        }

        Payment payment = new Payment();
        payment.setPaymentId(dto.getPaymentId());
        payment.setPaymentSlug(dto.getPaymentSlug());
        payment.setAmount(dto.getAmount());
        payment.setTransactionId(dto.getTransactionId());
        payment.setPaymentRemark(dto.getPaymentRemark());
        payment.setCreatedDt(dto.getCreatedDt());
        payment.setModifiedDt(dto.getModifiedDt());

        // Note: Related entities (Order, PaymentMethod, PaymentState) should be set separately in service layer

        return payment;
    }
}