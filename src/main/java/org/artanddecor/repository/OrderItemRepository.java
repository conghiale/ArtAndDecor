package org.artanddecor.repository;

import org.artanddecor.model.OrderItem;
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

    /**
     * Find order items with comprehensive filtering (unified method)
     * @param orderIds List of order IDs to filter by (optional)
     * @param productId Filter by product ID (optional)
     * @param minQuantity Filter by minimum quantity (optional)
     * @param maxQuantity Filter by maximum quantity (optional)
     * @param minUnitPrice Filter by minimum unit price (optional)
     * @param maxUnitPrice Filter by maximum unit price (optional)
     * @param minTotalPrice Filter by minimum total price (optional)
     * @param maxTotalPrice Filter by maximum total price (optional) 
     * @param textSearch Search text in product name or code (optional)
     * @param pageable Pagination information
     * @return Page of order items matching criteria
     */
    @Query("SELECT oi FROM OrderItem oi LEFT JOIN oi.product p WHERE " +
           "(COALESCE(:orderIds, null) IS NULL OR oi.order.orderId IN :orderIds) AND " +
           "(:productId IS NULL OR p.productId = :productId) AND " +
           "(:minQuantity IS NULL OR oi.quantity >= :minQuantity) AND " +
           "(:maxQuantity IS NULL OR oi.quantity <= :maxQuantity) AND " +
           "(:minUnitPrice IS NULL OR oi.unitPrice >= :minUnitPrice) AND " +
           "(:maxUnitPrice IS NULL OR oi.unitPrice <= :maxUnitPrice) AND " +
           "(:minTotalPrice IS NULL OR oi.totalPrice >= :minTotalPrice) AND " +
           "(:maxTotalPrice IS NULL OR oi.totalPrice <= :maxTotalPrice) AND " +
           "(:textSearch IS NULL OR :textSearch = '' OR " +
           " LOWER(p.productName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(p.productCode) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(oi.productName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(oi.productCode) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<OrderItem> findWithFilters(
            @Param("orderIds") List<Long> orderIds,
            @Param("productId") Long productId,
            @Param("minQuantity") Integer minQuantity,
            @Param("maxQuantity") Integer maxQuantity,
            @Param("minUnitPrice") BigDecimal minUnitPrice,
            @Param("maxUnitPrice") BigDecimal maxUnitPrice,
            @Param("minTotalPrice") BigDecimal minTotalPrice,
            @Param("maxTotalPrice") BigDecimal maxTotalPrice,
            @Param("textSearch") String textSearch,
            Pageable pageable);

}