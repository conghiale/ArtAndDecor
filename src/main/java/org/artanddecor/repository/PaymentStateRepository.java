package org.artanddecor.repository;

import org.artanddecor.model.PaymentState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PaymentState Repository
 * Data access layer for PAYMENT_STATE table
 */
@Repository
public interface PaymentStateRepository extends JpaRepository<PaymentState, Long> {

    /**
     * Find by payment state name
     */
    Optional<PaymentState> findByPaymentStateName(String paymentStateName);

    /**
     * Check if payment state name exists
     */
    boolean existsByPaymentStateName(String paymentStateName);

    /**
     * Get all enabled payment states
     */
    List<PaymentState> findByPaymentStateEnabledTrueOrderByPaymentStateName();

    /**
     * Get all enabled payment state names
     */
    @Query("SELECT ps.paymentStateName FROM PaymentState ps WHERE ps.paymentStateEnabled = true ORDER BY ps.paymentStateName")
    List<String> findAllEnabledPaymentStateNames();

    /**
     * Search payment states by criteria with pagination
     */
    @Query("SELECT ps FROM PaymentState ps WHERE " +
           "(:paymentStateId IS NULL OR ps.paymentStateId = :paymentStateId) AND " +
           "(:paymentStateName IS NULL OR LOWER(ps.paymentStateName) LIKE LOWER(CONCAT('%', :paymentStateName, '%'))) AND " +
           "(:paymentStateEnabled IS NULL OR ps.paymentStateEnabled = :paymentStateEnabled) AND " +
           "(:textSearch IS NULL OR LOWER(ps.paymentStateName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "                       LOWER(ps.paymentStateDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "                       LOWER(ps.paymentStateRemark) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<PaymentState> findByCriteria(
            @Param("paymentStateId") Long paymentStateId,
            @Param("paymentStateName") String paymentStateName,
            @Param("paymentStateEnabled") Boolean paymentStateEnabled,
            @Param("textSearch") String textSearch,
            Pageable pageable
    );
}