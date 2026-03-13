package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Orders Repository for database operations
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find order by order code
     * @param orderCode Order code
     * @return Optional Order
     */
    Optional<Order> findByOrderCode(String orderCode);

    /**
     * Find orders by user ID
     * @param userId User ID
     * @return List of orders for specific user
     */
    List<Order> findByUser_UserIdOrderByCreatedDtDesc(Long userId);



    /**
     * Find orders by multiple criteria
     * @param orderId Filter by order ID (optional)
     * @param orderCode Filter by order code (optional)
     * @param userId Filter by user ID (optional)
     * @param customerName Filter by customer name (optional)
     * @param customerPhone Filter by customer phone (optional)
     * @param customerEmail Filter by customer email (optional)
     * @param orderStateId Filter by order state ID (optional)
     * @param discountId Filter by discount ID (optional)
     * @param minTotalAmount Filter by minimum total amount (optional)
     * @param maxTotalAmount Filter by maximum total amount (optional)
     * @param minOriginalAmount Filter by minimum original amount (optional)
     * @param maxOriginalAmount Filter by maximum original amount (optional)
     * @param minDiscountAmount Filter by minimum discount amount (optional)
     * @param maxDiscountAmount Filter by maximum discount amount (optional)
     * @param orderDateFrom Filter by order date from (optional)
     * @param orderDateTo Filter by order date to (optional)
     * @param requiredDateFrom Filter by required date from (optional)
     * @param requiredDateTo Filter by required date to (optional)
     * @param shippedDateFrom Filter by shipped date from (optional)
     * @param shippedDateTo Filter by shipped date to (optional)
     * @param textSearch Search text in code, customer info, address (optional)
     * @param pageable Pagination information
     * @return Page of orders matching criteria
     */
    @Query("SELECT o FROM Order o LEFT JOIN o.orderState os LEFT JOIN o.discount d LEFT JOIN o.user u WHERE " +
           "(:orderId IS NULL OR o.orderId = :orderId) AND " +
           "(:orderCode IS NULL OR LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :orderCode, '%'))) AND " +
           "(:userId IS NULL OR u.userId = :userId) AND " +
           "(:customerName IS NULL OR LOWER(o.receiverName) LIKE LOWER(CONCAT('%', :customerName, '%'))) AND " +
           "(:customerPhone IS NULL OR LOWER(o.receiverPhone) LIKE LOWER(CONCAT('%', :customerPhone, '%'))) AND " +
           "(:customerEmail IS NULL OR LOWER(o.receiverEmail) LIKE LOWER(CONCAT('%', :customerEmail, '%'))) AND " +
           "(:orderStateId IS NULL OR os.orderStateId = :orderStateId) AND " +
           "(:discountId IS NULL OR d.discountId = :discountId) AND " +
           "(:minTotalAmount IS NULL OR o.totalAmount >= :minTotalAmount) AND " +
           "(:maxTotalAmount IS NULL OR o.totalAmount <= :maxTotalAmount) AND " +
           "(:minOriginalAmount IS NULL OR o.subtotalAmount >= :minOriginalAmount) AND " +
           "(:maxOriginalAmount IS NULL OR o.subtotalAmount <= :maxOriginalAmount) AND " +
           "(:minDiscountAmount IS NULL OR o.discountAmount >= :minDiscountAmount) AND " +
           "(:maxDiscountAmount IS NULL OR o.discountAmount <= :maxDiscountAmount) AND " +
           "(:orderDateFrom IS NULL OR o.createdDt >= :orderDateFrom) AND " +
           "(:orderDateTo IS NULL OR o.createdDt <= :orderDateTo) AND " +
           "(:requiredDateFrom IS NULL OR o.createdDt >= :requiredDateFrom) AND " +
           "(:requiredDateTo IS NULL OR o.createdDt <= :requiredDateTo) AND " +
           "(:shippedDateFrom IS NULL OR o.modifiedDt >= :shippedDateFrom) AND " +
           "(:shippedDateTo IS NULL OR o.modifiedDt <= :shippedDateTo) AND " +
           "(:textSearch IS NULL OR :textSearch = '' OR " +
           " LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(o.receiverName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(o.receiverPhone) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(o.receiverEmail) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(o.receiverAddress) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<Order> findOrdersByCriteria(
        @Param("orderId") Long orderId,
        @Param("orderCode") String orderCode,
        @Param("userId") Long userId,
        @Param("customerName") String customerName,
        @Param("customerPhone") String customerPhone,
        @Param("customerEmail") String customerEmail,
        @Param("orderStateId") Long orderStateId,
        @Param("discountId") Long discountId,
        @Param("minTotalAmount") BigDecimal minTotalAmount,
        @Param("maxTotalAmount") BigDecimal maxTotalAmount,
        @Param("minOriginalAmount") BigDecimal minOriginalAmount,
        @Param("maxOriginalAmount") BigDecimal maxOriginalAmount,
        @Param("minDiscountAmount") BigDecimal minDiscountAmount,
        @Param("maxDiscountAmount") BigDecimal maxDiscountAmount,
        @Param("orderDateFrom") LocalDateTime orderDateFrom,
        @Param("orderDateTo") LocalDateTime orderDateTo,
        @Param("requiredDateFrom") LocalDateTime requiredDateFrom,
        @Param("requiredDateTo") LocalDateTime requiredDateTo,
        @Param("shippedDateFrom") LocalDateTime shippedDateFrom,
        @Param("shippedDateTo") LocalDateTime shippedDateTo,
        @Param("textSearch") String textSearch,
        Pageable pageable);


}