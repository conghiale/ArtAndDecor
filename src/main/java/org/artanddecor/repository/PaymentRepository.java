package org.artanddecor.repository;

import org.artanddecor.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Payment Repository
 * Data access layer for PAYMENT table
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find by payment slug
     */
    Optional<Payment> findByPaymentSlug(String paymentSlug);

    /**
     * Check if payment slug exists
     */
    boolean existsByPaymentSlug(String paymentSlug);

    /**
     * Find by transaction ID
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Check if transaction ID exists
     */
    boolean existsByTransactionId(String transactionId);

    /**
     * Find payments by order ID
     */
    List<Payment> findByOrderOrderId(Long orderId);

    /**
     * Find payments by payment method ID
     */
    List<Payment> findByPaymentMethodPaymentMethodId(Long paymentMethodId);

    /**
     * Find payments by payment state ID
     */
    List<Payment> findByPaymentStatePaymentStateId(Long paymentStateId);

    /**
     * Search payments by comprehensive criteria with pagination
     */
    @Query("SELECT p FROM Payment p " +
           "LEFT JOIN p.order o " +
           "LEFT JOIN p.paymentMethod pm " +
           "LEFT JOIN p.paymentState ps " +
           "WHERE " +
           "(:paymentId IS NULL OR p.paymentId = :paymentId) AND " +
           "(:orderId IS NULL OR o.orderId = :orderId) AND " +
           "(:paymentSlug IS NULL OR LOWER(p.paymentSlug) LIKE LOWER(CONCAT('%', :paymentSlug, '%'))) AND " +
           "(:paymentMethodId IS NULL OR pm.paymentMethodId = :paymentMethodId) AND " +
           "(:paymentStateId IS NULL OR ps.paymentStateId = :paymentStateId) AND " +
           "(:transactionId IS NULL OR LOWER(p.transactionId) LIKE LOWER(CONCAT('%', :transactionId, '%'))) AND " +
           "(:minAmount IS NULL OR p.amount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR p.amount <= :maxAmount) AND " +
           "(:textSearch IS NULL OR " +
           "    LOWER(p.paymentSlug) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "    LOWER(p.transactionId) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "    LOWER(p.paymentRemark) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "    LOWER(pm.paymentMethodName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "    LOWER(ps.paymentStateName) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<Payment> findByCriteria(
            @Param("paymentId") Long paymentId,
            @Param("orderId") Long orderId,
            @Param("paymentSlug") String paymentSlug,
            @Param("paymentMethodId") Long paymentMethodId,
            @Param("paymentStateId") Long paymentStateId,
            @Param("transactionId") String transactionId,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("textSearch") String textSearch,
            Pageable pageable
    );

    /**
     * Get total count of payments
     */
    @Query("SELECT COUNT(p) FROM Payment p")
    long getTotalPaymentCount();

    /**
     * Get payment statistics by payment state
     */
    @Query("SELECT ps.paymentStateName, COUNT(p) FROM Payment p " +
           "JOIN p.paymentState ps " +
           "GROUP BY ps.paymentStateName")
    List<Object[]> getPaymentStatsByState();

    /**
     * Get payment statistics by payment method
     */
    @Query("SELECT pm.paymentMethodName, COUNT(p) FROM Payment p " +
           "JOIN p.paymentMethod pm " +
           "GROUP BY pm.paymentMethodName")
    List<Object[]> getPaymentStatsByMethod();

    /**
     * Calculate total payment amount by state
     */
    @Query("SELECT ps.paymentStateName, SUM(p.amount) FROM Payment p " +
           "JOIN p.paymentState ps " +
           "GROUP BY ps.paymentStateName")
    List<Object[]> getTotalAmountByState();
}