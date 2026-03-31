package org.artanddecor.repository;

import org.artanddecor.model.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PaymentMethod Repository
 * Data access layer for PAYMENT_METHOD table
 */
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    /**
     * Find by payment method name
     */
    Optional<PaymentMethod> findByPaymentMethodName(String paymentMethodName);

    /**
     * Check if payment method name exists
     */
    boolean existsByPaymentMethodName(String paymentMethodName);

    /**
     * Get all enabled payment methods
     */
    List<PaymentMethod> findByPaymentMethodEnabledTrueOrderByPaymentMethodName();

    /**
     * Get all enabled payment method names
     */
    @Query("SELECT pm.paymentMethodName FROM PaymentMethod pm WHERE pm.paymentMethodEnabled = true ORDER BY pm.paymentMethodName")
    List<String> findAllEnabledPaymentMethodNames();

    /**
     * Search payment methods by criteria with pagination
     */
    @Query("SELECT pm FROM PaymentMethod pm WHERE " +
           "(:paymentMethodId IS NULL OR pm.paymentMethodId = :paymentMethodId) AND " +
           "(:paymentMethodName IS NULL OR LOWER(pm.paymentMethodName) LIKE LOWER(CONCAT('%', :paymentMethodName, '%'))) AND " +
           "(:paymentMethodEnabled IS NULL OR pm.paymentMethodEnabled = :paymentMethodEnabled) AND " +
           "(:textSearch IS NULL OR LOWER(pm.paymentMethodName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "                       LOWER(pm.paymentMethodDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "                       LOWER(pm.paymentMethodRemark) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<PaymentMethod> findByCriteria(
            @Param("paymentMethodId") Long paymentMethodId,
            @Param("paymentMethodName") String paymentMethodName,
            @Param("paymentMethodEnabled") Boolean paymentMethodEnabled,
            @Param("textSearch") String textSearch,
            Pageable pageable
    );
}