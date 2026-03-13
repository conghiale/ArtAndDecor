package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.DiscountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DiscountType Repository for database operations
 */
@Repository
public interface DiscountTypeRepository extends JpaRepository<DiscountType, Long> {

    /**
     * Find discount type by name
     * @param discountTypeName Discount type name
     * @return Optional DiscountType
     */
    Optional<DiscountType> findByDiscountTypeName(String discountTypeName);

    /**
     * Find enabled discount types
     * @param enabled Enabled status
     * @return List of enabled discount types
     */
    List<DiscountType> findByDiscountTypeEnabledOrderByDiscountTypeName(Boolean enabled);

    /**
     * Find discount types by multiple criteria
     * @param discountTypeId Filter by discount type ID (optional)
     * @param discountTypeName Filter by discount type name (optional)
     * @param discountTypeEnabled Filter by enabled status (optional)
     * @param textSearch Search text in name, display name, description (optional)
     * @param pageable Pagination information
     * @return Page of discount types matching criteria
     */
    @Query("SELECT dt FROM DiscountType dt WHERE " +
           "(:discountTypeId IS NULL OR dt.discountTypeId = :discountTypeId) AND " +
           "(:discountTypeName IS NULL OR LOWER(dt.discountTypeName) LIKE LOWER(CONCAT('%', :discountTypeName, '%'))) AND " +
           "(:discountTypeEnabled IS NULL OR dt.discountTypeEnabled = :discountTypeEnabled) AND " +
           "(:textSearch IS NULL OR :textSearch = '' OR " +
           " LOWER(dt.discountTypeName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(dt.discountTypeDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(dt.discountTypeRemark) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<DiscountType> findDiscountTypesByCriteria(
        @Param("discountTypeId") Long discountTypeId,
        @Param("discountTypeName") String discountTypeName,
        @Param("discountTypeEnabled") Boolean discountTypeEnabled,
        @Param("textSearch") String textSearch,
        Pageable pageable);

    /**
     * Find all discount type names for dropdown/combobox usage
     * @return List of enabled discount type names
     */
    @Query("SELECT dt.discountTypeName FROM DiscountType dt WHERE dt.discountTypeEnabled = true ORDER BY dt.discountTypeName")
    List<String> findAllEnabledDiscountTypeNames();
}