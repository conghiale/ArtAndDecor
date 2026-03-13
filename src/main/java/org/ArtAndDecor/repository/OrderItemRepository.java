package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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