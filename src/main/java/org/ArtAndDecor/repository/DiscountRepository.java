package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.Discount;
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
 * Discount Repository for database operations
 */
@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    /**
     * Find discount by code
     * @param discountCode Discount code
     * @return Optional Discount
     */
    Optional<Discount> findByDiscountCode(String discountCode);

    /**
     * Find active discounts (enabled and within date range)
     * @param currentDate Current date
     * @return List of active discounts
     */
    @Query("SELECT d FROM Discount d WHERE d.isActive = true " +
           "AND (d.startAt IS NULL OR d.startAt <= :currentDate) " +
           "AND (d.endAt IS NULL OR d.endAt >= :currentDate)")
    List<Discount> findActiveDiscounts(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find enabled discounts by discount type
     * @param discountTypeId Discount type ID
     * @param enabled Enabled status
     * @return List of discounts by type
     */
    @Query("SELECT d FROM Discount d WHERE d.discountType.discountTypeId = :discountTypeId " +
           "AND d.isActive = :enabled ORDER BY d.discountName")
    List<Discount> findByDiscountTypeIdAndDiscountEnabled(
        @Param("discountTypeId") Long discountTypeId, 
        @Param("enabled") Boolean enabled);

    /**
     * Find valid discount by code (active and within usage limit)
     * @param discountCode Discount code
     * @param currentDate Current date
     * @return Optional valid discount
     */
    @Query("SELECT d FROM Discount d WHERE d.discountCode = :discountCode " +
           "AND d.isActive = true " +
           "AND (d.startAt IS NULL OR d.startAt <= :currentDate) " +
           "AND (d.endAt IS NULL OR d.endAt >= :currentDate) " +
           "AND (d.totalUsageLimit IS NULL OR d.usedCount < d.totalUsageLimit)")
    Optional<Discount> findValidDiscountByCode(
        @Param("discountCode") String discountCode, 
        @Param("currentDate") LocalDateTime currentDate);

    /**
     * Find discounts by multiple criteria
     * @param discountId Filter by discount ID (optional)
     * @param discountCode Filter by discount code (optional)
     * @param discountName Filter by discount name (optional)
     * @param discountTypeId Filter by discount type ID (optional)
     * @param isActive Filter by enabled status (optional)
     * @param dateActive Filter by active status (optional, based on date and enabled)
     * @param minValue Filter by minimum value (optional)
     * @param maxValue Filter by maximum value (optional)
     * @param minPercentage Filter by minimum percentage (optional)
     * @param maxPercentage Filter by maximum percentage (optional)
     * @param startDateFrom Filter by start date from (optional)
     * @param startDateTo Filter by start date to (optional)
     * @param endDateFrom Filter by end date from (optional)
     * @param endDateTo Filter by end date to (optional)
     * @param textSearch Search text in code, name, description (optional)
     * @param currentDate Current date for active filter
     * @param pageable Pagination information
     * @return Page of discounts matching criteria
     */
    @Query("SELECT d FROM Discount d LEFT JOIN d.discountType dt WHERE " +
           "(:discountId IS NULL OR d.discountId = :discountId) AND " +
           "(:discountCode IS NULL OR LOWER(d.discountCode) LIKE LOWER(CONCAT('%', :discountCode, '%'))) AND " +
           "(:discountName IS NULL OR LOWER(d.discountName) LIKE LOWER(CONCAT('%', :discountName, '%'))) AND " +
           "(:discountTypeId IS NULL OR dt.discountTypeId = :discountTypeId) AND " +
           "(:isActive IS NULL OR d.isActive = :isActive) AND " +
           "(:dateActive IS NULL OR (:dateActive = true AND d.isActive = true " +
           " AND (d.startAt IS NULL OR d.startAt <= :currentDate) " +
           " AND (d.endAt IS NULL OR d.endAt >= :currentDate)) " +
           " OR (:dateActive = false AND (d.isActive = false " +
           " OR d.startAt > :currentDate OR d.endAt < :currentDate))) AND " +
           "(:minValue IS NULL OR d.discountValue >= :minValue) AND " +
           "(:maxValue IS NULL OR d.discountValue <= :maxValue) AND " +
           "(:startDateFrom IS NULL OR d.startAt >= :startDateFrom) AND " +
           "(:startDateTo IS NULL OR d.startAt <= :startDateTo) AND " +
           "(:endDateFrom IS NULL OR d.endAt >= :endDateFrom) AND " +
           "(:endDateTo IS NULL OR d.endAt <= :endDateTo) AND " +
           "(:textSearch IS NULL OR :textSearch = '' OR " +
           " LOWER(d.discountCode) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(d.discountName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(d.discountRemark) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<Discount> findDiscountsByCriteria(
        @Param("discountId") Long discountId,
        @Param("discountCode") String discountCode,
        @Param("discountName") String discountName,
        @Param("discountTypeId") Long discountTypeId,
        @Param("isActive") Boolean isActive,
        @Param("dateActive") Boolean dateActive,
        @Param("minValue") BigDecimal minValue,
        @Param("maxValue") BigDecimal maxValue,
        @Param("startDateFrom") LocalDateTime startDateFrom,
        @Param("startDateTo") LocalDateTime startDateTo,
        @Param("endDateFrom") LocalDateTime endDateFrom,
        @Param("endDateTo") LocalDateTime endDateTo,
        @Param("textSearch") String textSearch,
        @Param("currentDate") LocalDateTime currentDate,
        Pageable pageable);

    /**
     * Find all discount codes for validation usage
     * @return List of all discount codes
     */
    @Query("SELECT d.discountCode FROM Discount d WHERE d.discountCode IS NOT NULL")
    List<String> findAllDiscountCodes();
}