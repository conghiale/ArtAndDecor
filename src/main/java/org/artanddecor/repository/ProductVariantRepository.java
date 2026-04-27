package org.artanddecor.repository;

import org.artanddecor.model.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ProductVariant entity
 * Manages product-attribute mappings with stock information
 */
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    // =============================================
    // FIND OPERATIONS
    // =============================================

    /**
     * Find product variants by product ID
     */
    @Query("SELECT pv FROM ProductVariant pv " +
           "WHERE pv.product.productId = :productId " +
           "ORDER BY pv.productAttribute.productAttr.productAttrName ASC, pv.productAttribute.productAttributeValue ASC")
    List<ProductVariant> findByProductId(@Param("productId") Long productId);

    /**
     * Find enabled product variants by product ID
     */
    @Query("SELECT pv FROM ProductVariant pv " +
           "WHERE pv.product.productId = :productId " +
           "AND pv.productVariantEnabled = true " +
           "AND pv.productAttribute.productAttributeEnabled = true " +
           "ORDER BY pv.productAttribute.productAttr.productAttrName ASC, pv.productAttribute.productAttributeValue ASC")
    List<ProductVariant> findEnabledByProductId(@Param("productId") Long productId);

    /**
     * Find product variants by product attribute ID
     */
    @Query("SELECT pv FROM ProductVariant pv " +
           "WHERE pv.productAttribute.productAttributeId = :productAttributeId " +
           "ORDER BY pv.product.productName ASC")
    List<ProductVariant> findByProductAttributeId(@Param("productAttributeId") Long productAttributeId);

    /**
     * Find specific product variant by product and attribute
     */
    @Query("SELECT pv FROM ProductVariant pv " +
           "WHERE pv.product.productId = :productId " +
           "AND pv.productAttribute.productAttributeId = :productAttributeId")
    Optional<ProductVariant> findByProductIdAndProductAttributeId(@Param("productId") Long productId, 
                                                                  @Param("productAttributeId") Long productAttributeId);

    /**
     * Find product variants with stock > 0
     */
    @Query("SELECT pv FROM ProductVariant pv " +
           "WHERE pv.productVariantStock > 0 " +
           "AND pv.productVariantEnabled = true " +
           "AND pv.productAttribute.productAttributeEnabled = true " +
           "ORDER BY pv.product.productName ASC, pv.productAttribute.productAttr.productAttrName ASC")
    List<ProductVariant> findVariantsWithStock();

    /**
     * Find product variants by criteria with pagination
     */
    @Query("SELECT pv FROM ProductVariant pv " +
           "WHERE (:productId IS NULL OR pv.product.productId = :productId) " +
           "AND (:productAttributeId IS NULL OR pv.productAttribute.productAttributeId = :productAttributeId) " +
           "AND (:enabled IS NULL OR pv.productVariantEnabled = :enabled) " +
           "AND (:hasStock IS NULL OR " +
           "     (:hasStock = true AND pv.productVariantStock > 0) OR " +
           "     (:hasStock = false AND pv.productVariantStock <= 0)) " +
           "ORDER BY pv.product.productName ASC, pv.productAttribute.productAttr.productAttrName ASC")
    Page<ProductVariant> findProductVariantsByCriteria(@Param("productId") Long productId,
                                                      @Param("productAttributeId") Long productAttributeId,
                                                      @Param("enabled") Boolean enabled,
                                                      @Param("hasStock") Boolean hasStock,
                                                      Pageable pageable);

    // =============================================
    // STOCK OPERATIONS
    // =============================================

    /**
     * Update product variant stock
     */
    @Modifying
    @Query("UPDATE ProductVariant pv SET pv.productVariantStock = :stock " +
           "WHERE pv.productVariantId = :productVariantId")
    int updateStock(@Param("productVariantId") Long productVariantId, @Param("stock") Integer stock);

    /**
     * Decrease product variant stock
     */
    @Modifying
    @Query("UPDATE ProductVariant pv SET pv.productVariantStock = pv.productVariantStock - :quantity " +
           "WHERE pv.productVariantId = :productVariantId AND pv.productVariantStock >= :quantity")
    int decreaseStock(@Param("productVariantId") Long productVariantId, @Param("quantity") Integer quantity);

    /**
     * Increase product variant stock
     */
    @Modifying
    @Query("UPDATE ProductVariant pv SET pv.productVariantStock = pv.productVariantStock + :quantity " +
           "WHERE pv.productVariantId = :productVariantId")
    int increaseStock(@Param("productVariantId") Long productVariantId, @Param("quantity") Integer quantity);

    // =============================================
    // VALIDATION & CONSTRAINTS
    // =============================================

    /**
     * Check if product-attribute combination exists
     */
    @Query("SELECT COUNT(pv) > 0 FROM ProductVariant pv " +
           "WHERE pv.product.productId = :productId " +
           "AND pv.productAttribute.productAttributeId = :productAttributeId")
    boolean existsByProductIdAndProductAttributeId(@Param("productId") Long productId, 
                                                  @Param("productAttributeId") Long productAttributeId);

    /**
     * Get total stock for a product (sum of all variants)
     */
    @Query("SELECT COALESCE(SUM(pv.productVariantStock), 0) FROM ProductVariant pv " +
           "WHERE pv.product.productId = :productId " +
           "AND pv.productVariantEnabled = true " +
           "AND pv.productAttribute.productAttributeEnabled = true")
    Long getTotalStockByProductId(@Param("productId") Long productId);

    /**
     * Count variants by product ID
     */
    @Query("SELECT COUNT(pv) FROM ProductVariant pv " +
           "WHERE pv.product.productId = :productId")
    Long countByProductId(@Param("productId") Long productId);

    /**
     * Count enabled variants by product ID
     */
    @Query("SELECT COUNT(pv) FROM ProductVariant pv " +
           "WHERE pv.product.productId = :productId " +
           "AND pv.productVariantEnabled = true " +
           "AND pv.productAttribute.productAttributeEnabled = true")
    Long countEnabledByProductId(@Param("productId") Long productId);
}