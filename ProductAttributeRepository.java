package org.artanddecor.repository;

import org.artanddecor.model.ProductAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    /**
     * Check if product attribute value and price combination exists
     */
    @Query("SELECT COUNT(pa) > 0 FROM ProductAttribute pa " +
           "WHERE pa.productAttributeValue = :value " +
           "AND pa.productAttributePrice = :price")
    boolean existsByValueAndPrice(@Param("value") String productAttributeValue, @Param("price") BigDecimal productAttributePrice);
    
    /**
     * Check if product attribute value and price combination exists excluding specific ID
     */
    @Query("SELECT COUNT(pa) > 0 FROM ProductAttribute pa " +
           "WHERE pa.productAttributeValue = :value " +
           "AND pa.productAttributePrice = :price " +
           "AND pa.productAttributeId != :excludeId")
    boolean existsByValueAndPriceExcludingId(@Param("value") String productAttributeValue, 
                                            @Param("price") BigDecimal productAttributePrice, 
                                            @Param("excludeId") Long productAttributeId);

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
    
    // =============================================
    // CUSTOM OPERATIONS
    // =============================================
    
    /**
     * Get grouped product attributes by value and price with optional filters
     * Returns one sample record per unique combination of value and price
     */
    @Query("SELECT pa FROM ProductAttribute pa " +
           "WHERE (:productId IS NULL OR pa.product.productId = :productId) " +
           "AND (:productAttrId IS NULL OR pa.productAttr.productAttrId = :productAttrId) " +
           "AND (:enabled IS NULL OR pa.productAttributeEnabled = :enabled) " +
           "ORDER BY pa.productAttributeValue ASC, pa.productAttributePrice ASC")
    List<ProductAttribute> findGroupedProductAttributes(@Param("productId") Long productId,
                                                       @Param("productAttrId") Long productAttrId,
                                                       @Param("enabled") Boolean enabled);
    
    /**
     * Update product attribute prices by attribute values
     */
    @Modifying
    @Query("UPDATE ProductAttribute pa " +
           "SET pa.productAttributePrice = :price " +
           "WHERE pa.productAttributeValue IN :values")
    int updatePricesByAttributeValues(@Param("values") List<String> values, @Param("price") BigDecimal price);
    
    /**
     * Update product attributes by attribute values (batch update)
     */
    @Modifying  
    @Query("UPDATE ProductAttribute pa " +
           "SET pa.product = CASE WHEN :productId IS NOT NULL THEN (SELECT p FROM Product p WHERE p.productId = :productId) ELSE pa.product END, " +
           "    pa.productAttr = CASE WHEN :productAttrId IS NOT NULL THEN (SELECT attr FROM ProductAttr attr WHERE attr.productAttrId = :productAttrId) ELSE pa.productAttr END, " +
           "    pa.productAttributeQuantity = CASE WHEN :quantity IS NOT NULL THEN :quantity ELSE pa.productAttributeQuantity END, " +
           "    pa.productAttributePrice = CASE WHEN :price IS NOT NULL THEN :price ELSE pa.productAttributePrice END, " +
           "    pa.productAttributeEnabled = CASE WHEN :enabled IS NOT NULL THEN :enabled ELSE pa.productAttributeEnabled END " +
           "WHERE pa.productAttributeValue IN :values")
    int updateByAttributeValues(@Param("values") List<String> values,
                               @Param("productId") Long productId,
                               @Param("productAttrId") Long productAttrId,
                               @Param("quantity") Integer quantity,
                               @Param("price") BigDecimal price,
                               @Param("enabled") Boolean enabled);
                               
    /**
     * Delete product attributes by attribute values
     */
    @Modifying
    @Query("DELETE FROM ProductAttribute pa WHERE pa.productAttributeValue IN :values")
    int deleteByAttributeValues(@Param("values") List<String> values);
    
    /**
     * Delete product attributes by product attribute ID
     */
    @Modifying
    @Query("DELETE FROM ProductAttribute pa WHERE pa.productAttr.productAttrId = :productAttrId")
    int deleteByProductAttrId(@Param("productAttrId") Long productAttrId);
    
    /**
     * Find product attributes by attribute values
     */
    @Query("SELECT pa FROM ProductAttribute pa WHERE pa.productAttributeValue IN :values")
    List<ProductAttribute> findByAttributeValues(@Param("values") List<String> values);
}