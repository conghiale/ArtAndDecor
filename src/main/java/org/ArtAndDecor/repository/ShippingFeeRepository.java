package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.ShippingFee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingFeeRepository extends JpaRepository<ShippingFee, Long> {

    List<ShippingFee> findByShippingFeeTypeShippingFeeTypeIdOrderByMinOrderPrice(Long shippingFeeTypeId);

    List<ShippingFee> findByShippingFeeEnabledTrueOrderByMinOrderPrice();

    @Query("SELECT sf FROM ShippingFee sf WHERE sf.shippingFeeEnabled = true " +
           "AND :orderAmount >= sf.minOrderPrice AND :orderAmount <= sf.maxOrderPrice " +
           "ORDER BY sf.shippingFeeValue")
    List<ShippingFee> findApplicableShippingFees(@Param("orderAmount") BigDecimal orderAmount);

    @Query("SELECT sf FROM ShippingFee sf WHERE sf.shippingFeeEnabled = true " +
           "AND :orderAmount >= sf.minOrderPrice AND :orderAmount <= sf.maxOrderPrice " +
           "ORDER BY sf.shippingFeeValue LIMIT 1")
    Optional<ShippingFee> findCheapestApplicableShippingFee(@Param("orderAmount") BigDecimal orderAmount);

    @Query("SELECT sf FROM ShippingFee sf WHERE " +
           "sf.shippingFeeType.shippingFeeTypeId = :shippingFeeTypeId AND " +
           "sf.shippingFeeId != :excludeId AND " +
           "((sf.minOrderPrice <= :maxPrice AND sf.maxOrderPrice >= :minPrice))")
    List<ShippingFee> findOverlappingPriceRanges(
            @Param("shippingFeeTypeId") Long shippingFeeTypeId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("excludeId") Long excludeId);

    @Query("SELECT sf FROM ShippingFee sf WHERE " +
           "(:shippingFeeId IS NULL OR sf.shippingFeeId = :shippingFeeId) AND " +
           "(:shippingFeeTypeId IS NULL OR sf.shippingFeeType.shippingFeeTypeId = :shippingFeeTypeId) AND " +
           "(:minOrderPrice IS NULL OR sf.minOrderPrice >= :minOrderPrice) AND " +
           "(:maxOrderPrice IS NULL OR sf.maxOrderPrice <= :maxOrderPrice) AND " +
           "(:minShippingFeeValue IS NULL OR sf.shippingFeeValue >= :minShippingFeeValue) AND " +
           "(:maxShippingFeeValue IS NULL OR sf.shippingFeeValue <= :maxShippingFeeValue) AND " +
           "(:shippingFeeEnabled IS NULL OR sf.shippingFeeEnabled = :shippingFeeEnabled) AND " +
           "(:textSearch IS NULL OR " +
           "  LOWER(sf.shippingFeeDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "  LOWER(sf.shippingFeeRemark) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<ShippingFee> findByCriteria(
            @Param("shippingFeeId") Long shippingFeeId,
            @Param("shippingFeeTypeId") Long shippingFeeTypeId,
            @Param("minOrderPrice") BigDecimal minOrderPrice,
            @Param("maxOrderPrice") BigDecimal maxOrderPrice,
            @Param("minShippingFeeValue") BigDecimal minShippingFeeValue,
            @Param("maxShippingFeeValue") BigDecimal maxShippingFeeValue,
            @Param("shippingFeeEnabled") Boolean shippingFeeEnabled,
            @Param("textSearch") String textSearch,
            Pageable pageable);
}