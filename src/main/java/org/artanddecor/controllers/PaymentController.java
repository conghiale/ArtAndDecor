package org.artanddecor.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.*;
import org.artanddecor.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Payment Management REST Controller
 * Provides streamlined endpoints for payment system management
 * Focus on essential CRUD operations with proper role-based access control
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Payment Management", description = "Streamlined payment system APIs")
@CrossOrigin(origins = "*")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final PaymentMethodService paymentMethodService;
    private final PaymentStateService paymentStateService;

    // =============================================
    // PAYMENT_METHOD ENDPOINTS
    // =============================================

    /**
     * Get payment methods with filtering and pagination
     * Role: permitAll (both ADMIN and CUSTOMER can access)
     */
    @GetMapping("/methods")
    @Operation(
        summary = "Get payment methods with filtering",
        description = "Retrieve payment methods with optional filtering and pagination. No authentication required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment methods retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<Page<PaymentMethodDto>>> getPaymentMethods(
            @Parameter(description = "Filter by payment method ID") 
            @RequestParam(required = false) Long paymentMethodId,
            
            @Parameter(description = "Filter by payment method name") 
            @RequestParam(required = false) String paymentMethodName,
            
            @Parameter(description = "Filter by enabled status") 
            @RequestParam(required = false) Boolean paymentMethodEnabled,
            
            @Parameter(description = "Text search across name, display name, and remark") 
            @RequestParam(required = false) String textSearch,
            
            @Parameter(description = "Pagination settings")
            @PageableDefault(page = 0, size = 10, sort = "paymentMethodName") Pageable pageable) {

        logger.info("Getting payment methods with criteria - methodId: {}, name: {}, enabled: {}, textSearch: {}",
                   paymentMethodId, paymentMethodName, paymentMethodEnabled, textSearch);

        try {
            Page<PaymentMethodDto> methodsPage = paymentMethodService.searchPaymentMethodsByCriteria(
                    paymentMethodId, paymentMethodName, paymentMethodEnabled, textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Payment methods retrieved successfully", methodsPage));
        } catch (Exception e) {
            logger.error("Error retrieving payment methods: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve payment methods: " + e.getMessage()));
        }
    }

    /**
     * Create new payment method
     * Role: ADMIN only
     */
    @PostMapping("/methods")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create payment method",
        description = "Create a new payment method configuration. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<PaymentMethodDto>> createPaymentMethod(
            @Parameter(description = "Payment method data to create")
            @Valid @RequestBody PaymentMethodDto paymentMethodDto) {

        logger.info("Creating new payment method: {}", paymentMethodDto.getPaymentMethodName());

        try {
            PaymentMethodDto createdMethod = paymentMethodService.createPaymentMethod(paymentMethodDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Payment method created successfully", createdMethod));
        } catch (Exception e) {
            logger.error("Error creating payment method: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to create payment method: " + e.getMessage()));
        }
    }

    /**
     * Update payment method
     * Role: ADMIN only
     */
    @PutMapping("/methods/{paymentMethodId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update payment method",
        description = "Update existing payment method configuration. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<PaymentMethodDto>> updatePaymentMethod(
            @Parameter(description = "Payment method ID")
            @PathVariable Long paymentMethodId,
            
            @Parameter(description = "Updated payment method data")
            @Valid @RequestBody PaymentMethodDto paymentMethodDto) {

        logger.info("Updating payment method ID: {}", paymentMethodId);

        try {
            PaymentMethodDto updatedMethod = paymentMethodService.updatePaymentMethod(paymentMethodId, paymentMethodDto);
            return ResponseEntity.ok(BaseResponseDto.success("Payment method updated successfully", updatedMethod));
        } catch (Exception e) {
            logger.error("Error updating payment method {}: {}", paymentMethodId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update payment method: " + e.getMessage()));
        }
    }

    // =============================================
    // PAYMENT_STATE ENDPOINTS  
    // =============================================

    /**
     * Get payment states with filtering and pagination
     * Role: permitAll (both ADMIN and CUSTOMER can access)
     */
    @GetMapping("/states")
    @Operation(
        summary = "Get payment states with filtering",
        description = "Retrieve payment states with optional filtering and pagination. No authentication required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment states retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })  
    public ResponseEntity<BaseResponseDto<Page<PaymentStateDto>>> getPaymentStates(
            @Parameter(description = "Filter by payment state ID")
            @RequestParam(required = false) Long paymentStateId,
            
            @Parameter(description = "Filter by payment state name")
            @RequestParam(required = false) String paymentStateName,
            
            @Parameter(description = "Filter by enabled status")
            @RequestParam(required = false) Boolean paymentStateEnabled,
            
            @Parameter(description = "Text search across name, display name, and remark")
            @RequestParam(required = false) String textSearch,
            
            @Parameter(description = "Pagination settings")
            @PageableDefault(page = 0, size = 10, sort = "paymentStateName") Pageable pageable) {

        logger.info("Getting payment states with criteria - stateId: {}, name: {}, enabled: {}", 
                   paymentStateId, paymentStateName, paymentStateEnabled);

        try {
            Page<PaymentStateDto> statesPage = paymentStateService.searchPaymentStatesByCriteria(
                    paymentStateId, paymentStateName, paymentStateEnabled, textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Payment states retrieved successfully", statesPage));
        } catch (Exception e) {
            logger.error("Error retrieving payment states: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve payment states: " + e.getMessage()));
        }
    }

    /**
     * Create new payment state
     * Role: ADMIN only
     */
    @PostMapping("/states")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create payment state",
        description = "Create a new payment state configuration. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<PaymentStateDto>> createPaymentState(
            @Parameter(description = "Payment state data to create")
            @Valid @RequestBody PaymentStateDto paymentStateDto) {

        logger.info("Creating new payment state: {}", paymentStateDto.getPaymentStateName());

        try {
            PaymentStateDto createdState = paymentStateService.createPaymentState(paymentStateDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Payment state created successfully", createdState));
        } catch (Exception e) {
            logger.error("Error creating payment state: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to create payment state: " + e.getMessage()));
        }
    }

    /**
     * Update payment state
     * Role: ADMIN only
     */
    @PutMapping("/states/{paymentStateId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update payment state",
        description = "Update existing payment state configuration. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<PaymentStateDto>> updatePaymentState(
            @Parameter(description = "Payment state ID")
            @PathVariable Long paymentStateId,
            
            @Parameter(description = "Updated payment state data")
            @Valid @RequestBody PaymentStateDto paymentStateDto) {

        logger.info("Updating payment state ID: {}", paymentStateId);

        try {
            PaymentStateDto updatedState = paymentStateService.updatePaymentState(paymentStateId, paymentStateDto);
            return ResponseEntity.ok(BaseResponseDto.success("Payment state updated successfully", updatedState));
        } catch (Exception e) {
            logger.error("Error updating payment state {}: {}", paymentStateId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update payment state: " + e.getMessage()));
        }
    }

    // =============================================
    // PAYMENT ENDPOINTS (No CREATE - created during order checkout)
    // =============================================

    /**
     * Get payments with filtering and pagination
     * Role: permitAll (both ADMIN and CUSTOMER can access)
     */
    @GetMapping
    @Operation(
        summary = "Get payments with filtering",
        description = "Retrieve payments with comprehensive filtering and pagination. No authentication required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<Page<PaymentDto>>> getPayments(
            @Parameter(description = "Filter by payment ID")
            @RequestParam(required = false) Long paymentId,
            
            @Parameter(description = "Filter by order ID")
            @RequestParam(required = false) Long orderId,
            
            @Parameter(description = "Filter by payment slug")
            @RequestParam(required = false) String paymentSlug,
            
            @Parameter(description = "Filter by payment method ID")
            @RequestParam(required = false) Long paymentMethodId,
            
            @Parameter(description = "Filter by payment state ID")
            @RequestParam(required = false) Long paymentStateId,
            
            @Parameter(description = "Filter by transaction ID")
            @RequestParam(required = false) String transactionId,
            
            @Parameter(description = "Filter by minimum payment amount")
            @RequestParam(required = false) BigDecimal minAmount,
            
            @Parameter(description = "Filter by maximum payment amount")
            @RequestParam(required = false) BigDecimal maxAmount,
            
            @Parameter(description = "Text search across multiple fields")
            @RequestParam(required = false) String textSearch,
            
            @Parameter(description = "Pagination settings")
            @PageableDefault(page = 0, size = 10, sort = "createdDt",
                           direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

        logger.info("Getting payments with criteria - paymentId: {}, orderId: {}, stateId: {}",
                   paymentId, orderId, paymentStateId);

        try {
            Page<PaymentDto> paymentsPage = paymentService.searchPaymentsByCriteria(
                    paymentId, orderId, paymentSlug, paymentMethodId, paymentStateId,
                    transactionId, minAmount, maxAmount, textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Payments retrieved successfully", paymentsPage));
        } catch (Exception e) {
            logger.error("Error retrieving payments: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve payments: " + e.getMessage()));
        }
    }

    /**
     * Update payment
     * Role: ADMIN only
     */
    @PutMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update payment",
        description = "Update existing payment information. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<PaymentDto>> updatePayment(
            @Parameter(description = "Payment ID")
            @PathVariable Long paymentId,
            
            @Parameter(description = "Updated payment data")
            @Valid @RequestBody PaymentDto paymentDto) {

        logger.info("Updating payment ID: {}", paymentId);

        try {
            PaymentDto updatedPayment = paymentService.updatePayment(paymentId, paymentDto);
            return ResponseEntity.ok(BaseResponseDto.success("Payment updated successfully", updatedPayment));
        } catch (Exception e) {
            logger.error("Error updating payment {}: {}", paymentId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update payment: " + e.getMessage()));
        }
    }

    // =============================================
    // PAYMENT STATISTICS ENDPOINTS (Admin only)
    // =============================================

    /**
     * Get payment statistics by state (Admin only)
     */
    @GetMapping("/stats/by-state")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get payment statistics by state",
        description = "Get payment count statistics by payment state. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Map<String, Long>>> getPaymentStatsByState() {
        logger.info("Getting payment statistics by state");

        try {
            Map<String, Long> stats = paymentService.getPaymentStatsByState();
            return ResponseEntity.ok(BaseResponseDto.success("Payment statistics by state retrieved successfully", stats));
        } catch (Exception e) {
            logger.error("Error retrieving payment statistics by state: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve payment statistics: " + e.getMessage()));
        }
    }

    /**
     * Get payment statistics by method (Admin only)
     */
    @GetMapping("/stats/by-method")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get payment statistics by method",
        description = "Get payment count statistics by payment method. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Map<String, Long>>> getPaymentStatsByMethod() {
        logger.info("Getting payment statistics by method");

        try {
            Map<String, Long> stats = paymentService.getPaymentStatsByMethod();
            return ResponseEntity.ok(BaseResponseDto.success("Payment statistics by method retrieved successfully", stats));
        } catch (Exception e) {
            logger.error("Error retrieving payment statistics by method: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve payment statistics: " + e.getMessage()));
        }
    }

    /**
     * Get total payment amounts by state (Admin only)
     */
    @GetMapping("/stats/amounts-by-state")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get total payment amounts by state",
        description = "Get total payment amounts grouped by payment state. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Map<String, BigDecimal>>> getTotalAmountsByState() {
        logger.info("Getting total payment amounts by state");

        try {
            Map<String, BigDecimal> amounts = paymentService.getTotalAmountByState();
            return ResponseEntity.ok(BaseResponseDto.success("Total payment amounts by state retrieved successfully", amounts));
        } catch (Exception e) {
            logger.error("Error retrieving total payment amounts by state: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve payment amounts: " + e.getMessage()));
        }
    }
}