package org.artanddecor.repository;

import org.artanddecor.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderItem Repository for database operations
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Find order items by order ID
     * @param orderId Order ID
     * @return List of order items
     */
    List<OrderItem> findByOrderOrderId(Long orderId);

    /**
     * Find order items by product ID
     * @param productId Product ID
     * @return List of order items
     */
    List<OrderItem> findByProductProductId(Long productId);

}