package org.artanddecor.repository;

import org.artanddecor.model.ProductAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ProductAttribute Repository for database operations
 */
@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    // =============================================
    // FIND OPERATIONS
    // =============================================

    /**
     * Find product attributes by product ID
     */
    @Query("SELECT pa FROM ProductAttribute pa " +
           "WHERE pa.product.productId = :productId " +
           "AND pa.productAttributeEnabled = true " +
           "ORDER BY pa.productAttr.productAttrName ASC")
    List<ProductAttribute> findByProductId(@Param("productId") Long productId);

    /**
     * Find product attributes by attribute ID
     */
    @Query("SELECT pa FROM ProductAttribute pa " +
           "WHERE pa.productAttr.productAttrId = :attrId " +
           "AND pa.productAttributeEnabled = true " +
           "ORDER BY pa.product.productName ASC")
    List<ProductAttribute> findByProductAttrId(@Param("attrId") Long attrId);

    /**
     * Find specific product attribute by product and attribute ID
     */
    @Query("SELECT pa FROM ProductAttribute pa " +
           "WHERE pa.product.productId = :productId " +
           "AND pa.productAttr.productAttrId = :attrId")
    Optional<ProductAttribute> findByProductIdAndAttrId(@Param("productId") Long productId, @Param("attrId") Long attrId);

    /**
     * Check if product-attribute combination exists
     */
    boolean existsByProductProductIdAndProductAttrProductAttrId(Long productId, Long attrId);

    // =============================================
    // SEARCH OPERATIONS
    // =============================================

    /**
     * Search product attributes by multiple criteria with pagination
     */
    @Query("SELECT pa FROM ProductAttribute pa " +
           "WHERE (:productId IS NULL OR pa.product.productId = :productId) " +
           "AND (:attrId IS NULL OR pa.productAttr.productAttrId = :attrId) " +
           "AND (:enabled IS NULL OR pa.productAttributeEnabled = :enabled) " +
           "AND (:attrValue IS NULL OR LOWER(pa.productAttributeValue) LIKE LOWER(CONCAT('%', :attrValue, '%'))) " +
           "ORDER BY pa.product.productName ASC, pa.productAttr.productAttrName ASC")
    Page<ProductAttribute> findProductAttributesByCriteriaPaginated(
        @Param("productId") Long productId,
        @Param("attrId") Long attrId,
        @Param("enabled") Boolean enabled,
        @Param("attrValue") String attrValue,
        Pageable pageable
    );

    /**
     * Count attributes for product
     */
    @Query("SELECT COUNT(pa) FROM ProductAttribute pa " +
           "WHERE pa.product.productId = :productId " +
           "AND pa.productAttributeEnabled = true")
    Long countByProductId(@Param("productId") Long productId);

    /**
     * Delete by product ID and attribute ID
     */
    void deleteByProductProductIdAndProductAttrProductAttrId(Long productId, Long attrId);

    /**
     * Delete all attributes for product
     */
    void deleteByProductProductId(Long productId);
}