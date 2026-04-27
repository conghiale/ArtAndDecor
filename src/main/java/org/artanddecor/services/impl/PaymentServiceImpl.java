package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.PaymentDto;
import org.artanddecor.dto.PaymentQRRequestDto;
import org.artanddecor.dto.PolicyDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.Payment;
import org.artanddecor.model.PaymentMethod;
import org.artanddecor.model.PaymentState;
import org.artanddecor.repository.PaymentMethodRepository;
import org.artanddecor.repository.PaymentRepository;
import org.artanddecor.repository.PaymentStateRepository;
import org.artanddecor.services.PaymentService;
import org.artanddecor.services.PolicyService;
import org.artanddecor.utils.PaymentMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Implementation of PaymentService interface
 * Business logic for payment management operations
 * Note: No create method - payments are created automatically during order checkout
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentStateRepository paymentStateRepository;
    private final PaymentMapperUtil paymentMapperUtil;
    private final PolicyService policyService;
    private final RestTemplate restTemplate;

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDto> getAllPayments(Pageable pageable) {
        logger.debug("Getting all payments with pagination: {}", pageable);
        Page<Payment> paymentPage = paymentRepository.findAll(pageable);
        return paymentPage.map(paymentMapperUtil::mapPaymentToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentById(Long paymentId) {
        logger.debug("Getting payment by ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        return paymentMapperUtil.mapPaymentToDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentBySlug(String paymentSlug) {
        logger.debug("Getting payment by slug: {}", paymentSlug);
        Payment payment = paymentRepository.findByPaymentSlug(paymentSlug)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with slug: " + paymentSlug));
        return paymentMapperUtil.mapPaymentToDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByTransactionId(String transactionId) {
        logger.debug("Getting payment by transaction ID: {}", transactionId);
        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with transaction ID: " + transactionId));
        return paymentMapperUtil.mapPaymentToDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByOrderId(Long orderId) {
        logger.debug("Getting payments by order ID: {}", orderId);
        List<Payment> payments = paymentRepository.findByOrderOrderId(orderId);
        return payments.stream()
            .map(paymentMapperUtil::mapPaymentToDto)
            .toList();
    }

    @Override
    public PaymentDto updatePayment(Long paymentId, PaymentDto paymentDto) {
        logger.debug("Updating payment with ID: {}", paymentId);
        
        Payment existingPayment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        // Validate and set new payment method if provided
        if (paymentDto.getPaymentMethod() != null && paymentDto.getPaymentMethod().getPaymentMethodId() != null) {
            PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentDto.getPaymentMethod().getPaymentMethodId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));
            existingPayment.setPaymentMethod(paymentMethod);
        }

        // Validate and set new payment state if provided
        if (paymentDto.getPaymentState() != null && paymentDto.getPaymentState().getPaymentStateId() != null) {
            PaymentState paymentState = paymentStateRepository.findById(paymentDto.getPaymentState().getPaymentStateId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment state not found"));
            existingPayment.setPaymentState(paymentState);
        }

        // Update other fields
        if (paymentDto.getAmount() != null) {
            existingPayment.setAmount(paymentDto.getAmount());
        }
        if (paymentDto.getTransactionId() != null) {
            existingPayment.setTransactionId(paymentDto.getTransactionId());
        }
        if (paymentDto.getPaymentRemark() != null) {
            existingPayment.setPaymentRemark(paymentDto.getPaymentRemark());
        }

        Payment updatedPayment = paymentRepository.save(existingPayment);
        logger.info("Updated payment with ID: {}", updatedPayment.getPaymentId());
        
        return paymentMapperUtil.mapPaymentToDto(updatedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDto> searchPaymentsByCriteria(
            Long paymentId, Long orderId, String paymentSlug,
            Long paymentMethodId, Long paymentStateId, String transactionId,
            BigDecimal minAmount, BigDecimal maxAmount, String textSearch,
            Pageable pageable) {
        
        logger.debug("Searching payments with criteria");
        
        Page<Payment> paymentPage = paymentRepository.findByCriteria(
            paymentId, orderId, paymentSlug, paymentMethodId, paymentStateId,
            transactionId, minAmount, maxAmount, textSearch, pageable
        );
        
        return paymentPage.map(paymentMapperUtil::mapPaymentToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalPaymentCount() {
        logger.debug("Getting total payment count");
        return paymentRepository.getTotalPaymentCount();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getPaymentStatsByState() {
        logger.debug("Getting payment statistics by state");
        List<Object[]> results = paymentRepository.getPaymentStatsByState();
        Map<String, Long> stats = new HashMap<>();
        for (Object[] result : results) {
            stats.put((String) result[0], (Long) result[1]);
        }
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getPaymentStatsByMethod() {
        logger.debug("Getting payment statistics by method");
        List<Object[]> results = paymentRepository.getPaymentStatsByMethod();
        Map<String, Long> stats = new HashMap<>();
        for (Object[] result : results) {
            stats.put((String) result[0], (Long) result[1]);
        }
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getTotalAmountByState() {
        logger.debug("Getting total payment amounts by state");
        List<Object[]> results = paymentRepository.getTotalAmountByState();
        Map<String, BigDecimal> amounts = new HashMap<>();
        for (Object[] result : results) {
            amounts.put((String) result[0], (BigDecimal) result[1]);
        }
        return amounts;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generatePaymentQRCode(PaymentQRRequestDto request) {
        logger.debug("Generating QR code for order: {} with amount: {}", request.getOrderCode(), request.getAmount());
        
        try {
            // Get bank information from policy by POLICY_NAME
            PolicyDto bankPolicy = policyService.findPolicyByName("PAYMENT_BANK_INFO")
                .orElseThrow(() -> new ResourceNotFoundException("Payment bank information not configured"));
            
            // Parse bank properties from policy
            Properties bankProps = new Properties();
            bankProps.load(new StringReader(bankPolicy.getPolicyValue()));
            
            String bankName = bankProps.getProperty("bank.name", "");
            String accountNumber = bankProps.getProperty("bank.account.number", "");
            String noteTemplate = bankProps.getProperty("bank.note.template", "Thanh toan don hang {orderId}");
            
            // Validate required fields
            if (bankName.isEmpty() || accountNumber.isEmpty()) {
                throw new IllegalStateException("Bank name and account number are required in policy configuration");
            }
            
            // Generate payment description from template
            String description = noteTemplate.replace("{orderId}", request.getOrderCode());
            
            // Build SEPAY QR code URL
            StringBuilder qrUrlBuilder = new StringBuilder("https://qr.sepay.vn/img");
            qrUrlBuilder.append("?acc=").append(accountNumber);
            qrUrlBuilder.append("&bank=").append(URLEncoder.encode(bankName, StandardCharsets.UTF_8));
            qrUrlBuilder.append("&amount=").append(request.getAmount().longValue());
            qrUrlBuilder.append("&des=").append(URLEncoder.encode(description, StandardCharsets.UTF_8));
            
            String qrCodeUrl = qrUrlBuilder.toString();
            logger.debug("Generated QR URL: {}", qrCodeUrl);
            
            // Call SEPAY API to get QR code as byte array
            byte[] qrCodeBytes = restTemplate.getForObject(qrCodeUrl, byte[].class);
            
            if (qrCodeBytes == null || qrCodeBytes.length == 0) {
                throw new RuntimeException("Failed to retrieve QR code from SEPAY service");
            }
            
            logger.info("Generated QR code successfully for order: {}, size: {} bytes", request.getOrderCode(), qrCodeBytes.length);
            return qrCodeBytes;
            
        } catch (Exception e) {
            logger.error("Error generating QR code for order {}: {}", request.getOrderCode(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public PaymentDto updatePaymentStatus(Long paymentId, Long paymentStateId) {
        logger.info("Updating payment status - paymentId: {}, newStateId: {}", paymentId, paymentStateId);
        
        // Find payment
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        
        // Validate payment state exists
        PaymentState paymentState = paymentStateRepository.findById(paymentStateId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment state not found with ID: " + paymentStateId));
        
        // Update payment state
        payment.setPaymentState(paymentState);
        
        // Save updated payment
        Payment updatedPayment = paymentRepository.save(payment);
        
        logger.info("Payment status updated successfully - paymentId: {}, newState: {}", 
                   paymentId, paymentState.getPaymentStateName());
        
        return paymentMapperUtil.mapPaymentToDto(updatedPayment);
    }
}