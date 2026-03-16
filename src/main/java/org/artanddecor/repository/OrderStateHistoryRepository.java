package org.artanddecor.repository;

import org.artanddecor.model.OrderStateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderStateHistory Repository for database operations
 */
@Repository
public interface OrderStateHistoryRepository extends JpaRepository<OrderStateHistory, Long> {

    /**
     * Find order state history by order ID
     * @param orderId Order ID
     * @return List of order state history for specific order
     */
    @Query("SELECT osh FROM OrderStateHistory osh WHERE osh.order.orderId = :orderId ORDER BY osh.createdDt DESC")
    List<OrderStateHistory> findByOrderIdOrderByStateChangeDateDesc(@Param("orderId") Long orderId);




}