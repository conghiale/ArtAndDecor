package org.artanddecor.repository;

import org.artanddecor.model.OrderStateHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OrderStateHistory Repository for database operations
 */
@Repository
public interface OrderStateHistoryRepository extends JpaRepository<OrderStateHistory, Long> {

    /**
     * Find order state history by order ID
     */
    @Query("SELECT osh FROM OrderStateHistory osh WHERE osh.order.orderId = :orderId ORDER BY osh.createdDt DESC")
    List<OrderStateHistory> findByOrderIdOrderByStateChangeDateDesc(@Param("orderId") Long orderId);

    /**
     * Find order state history with filtering and database-level pagination.
     * Replaces the previous findAll() + in-memory filter pattern.
     */
    @Query("SELECT osh FROM OrderStateHistory osh " +
           "WHERE (:orderId IS NULL OR osh.order.orderId = :orderId) " +
           "AND (:fromDate IS NULL OR osh.createdDt >= :fromDate) " +
           "AND (:toDate IS NULL OR osh.createdDt <= :toDate) " +
           "AND (:oldStateId IS NULL OR osh.oldState.orderStateId = :oldStateId) " +
           "AND (:newStateId IS NULL OR osh.newState.orderStateId = :newStateId) " +
           "ORDER BY osh.createdDt DESC")
    Page<OrderStateHistory> findByFilters(
            @Param("orderId") Long orderId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("oldStateId") Long oldStateId,
            @Param("newStateId") Long newStateId,
            Pageable pageable);

       /**
        * Count state history records by changed user ID
        */
       Long countByChangedByUser_UserId(Long userId);

       /**
        * Get sample order state history ID referencing changed user
        */
       @Query("SELECT MIN(osh.orderStateHistoryId) FROM OrderStateHistory osh WHERE osh.changedByUser.userId = :userId")
       Long findSampleHistoryIdByChangedByUserId(@Param("userId") Long userId);
}