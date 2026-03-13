package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.ShippingFeeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingFeeTypeRepository extends JpaRepository<ShippingFeeType, Long> {

    Optional<ShippingFeeType> findByShippingFeeTypeName(String shippingFeeTypeName);

    List<ShippingFeeType> findByShippingFeeTypeEnabledTrueOrderByShippingFeeTypeName();

    boolean existsByShippingFeeTypeNameAndShippingFeeTypeIdNot(String shippingFeeTypeName, Long shippingFeeTypeId);

    boolean existsByShippingFeeTypeName(String shippingFeeTypeName);

    @Query("SELECT sft FROM ShippingFeeType sft WHERE " +
           "(:shippingFeeTypeId IS NULL OR sft.shippingFeeTypeId = :shippingFeeTypeId) AND " +
           "(:shippingFeeTypeName IS NULL OR LOWER(sft.shippingFeeTypeName) LIKE LOWER(CONCAT('%', :shippingFeeTypeName, '%'))) AND " +
           "(:shippingFeeTypeEnabled IS NULL OR sft.shippingFeeTypeEnabled = :shippingFeeTypeEnabled) AND " +
           "(:textSearch IS NULL OR " +
           "  LOWER(sft.shippingFeeTypeName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "  LOWER(sft.shippingFeeTypeDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "  LOWER(sft.shippingFeeTypeRemark) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<ShippingFeeType> findByCriteria(
            @Param("shippingFeeTypeId") Long shippingFeeTypeId,
            @Param("shippingFeeTypeName") String shippingFeeTypeName,
            @Param("shippingFeeTypeEnabled") Boolean shippingFeeTypeEnabled,
            @Param("textSearch") String textSearch,
            Pageable pageable);

    @Query("SELECT sft.shippingFeeTypeName FROM ShippingFeeType sft WHERE sft.shippingFeeTypeEnabled = true ORDER BY sft.shippingFeeTypeName")
    List<String> findAllEnabledShippingFeeTypeNames();
}