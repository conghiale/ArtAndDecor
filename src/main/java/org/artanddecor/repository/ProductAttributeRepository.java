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
 * ProductAttribute Repository for master attribute definitions with pricing
 * Manages the catalog of available attribute values and their prices
 */
@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    // =============================================
    // FIND OPERATIONS
    // =============================================

    /**
     * Find product attributes by attribute ID (e.g., all size options)
     */
    @Query("SELECT pa FROM ProductAttribute pa " +
           "WHERE pa.productAttr.productAttrId = :attrId " +
           "AND pa.productAttributeEnabled = true " +
           "ORDER BY pa.productAttributeValue ASC, pa.productAttributePrice ASC")
    List<ProductAttribute> findByProductAttrId(@Param("attrId") Long attrId);

    /**
     * Find product attributes by attribute value (e.g., all "40x60cm" options)
     */
    @Query("SELECT pa FROM ProductAttribute pa " +
           "WHERE pa.productAttributeValue = :value " +
           "AND pa.productAttributeEnabled = true " +
           "ORDER BY pa.productAttr.productAttrName ASC, pa.productAttributePrice ASC")
    List<ProductAttribute> findByProductAttributeValue(@Param("value") String productAttributeValue);

    /**
     * Find specific attribute by attr ID, value and price combination
     */
    @Query("SELECT pa FROM ProductAttribute pa " +
           "WHERE pa.productAttr.productAttrId = :attrId " +
           "AND pa.productAttributeValue = :value " +
           "AND pa.productAttributePrice = :price")
    Optional<ProductAttribute> findByAttrIdValueAndPrice(@Param("attrId") Long attrId, 
                                                        @Param("value") String value, 
                                                        @Param("price") BigDecimal price);

    // =============================================
    // VALIDATION & CONSTRAINTS
    // =============================================

    /**
     * Check if attribute value and attribute ID combination exists (for unique constraint)
     */
    @Query("SELECT COUNT(pa) > 0 FROM ProductAttribute pa " +
           "WHERE pa.productAttributeValue = :value " +
           "AND pa.productAttr.productAttrId = :attrId " +
           "AND pa.productAttributePrice = :price")
    boolean existsByValueAttrIdAndPrice(@Param("value") String productAttributeValue, 
                                       @Param("attrId") Long productAttrId, 
                                       @Param("price") BigDecimal price);
    
    /**
     * Check if combination exists excluding specific ID (for updates)
     */
    @Query("SELECT COUNT(pa) > 0 FROM ProductAttribute pa " +
           "WHERE pa.productAttributeValue = :value " +
           "AND pa.productAttr.productAttrId = :attrId " +
           "AND pa.productAttributePrice = :price " +
           "AND pa.productAttributeId != :excludeId")
    boolean existsByValueAttrIdAndPriceExcludingId(@Param("value") String productAttributeValue, 
                                                  @Param("attrId") Long productAttrId, 
                                                  @Param("price") BigDecimal price,
                                                  @Param("excludeId") Long productAttributeId);

    // =============================================
    // SEARCH OPERATIONS
    // =============================================

    /**
     * Search product attributes by multiple criteria with pagination
     */
    @Query("SELECT pa FROM ProductAttribute pa " +
           "WHERE (:attrId IS NULL OR pa.productAttr.productAttrId = :attrId) " +
           "AND (:enabled IS NULL OR pa.productAttributeEnabled = :enabled) " +
           "AND (:attrValue IS NULL OR LOWER(pa.productAttributeValue) LIKE LOWER(CONCAT('%', :attrValue, '%'))) " +
           "AND (:minPrice IS NULL OR pa.productAttributePrice >= :minPrice) " +
           "AND (:maxPrice IS NULL OR pa.productAttributePrice <= :maxPrice) " +
           "ORDER BY pa.productAttr.productAttrName ASC, pa.productAttributeValue ASC, pa.productAttributePrice ASC")
    Page<ProductAttribute> findProductAttributesByCriteria(
        @Param("attrId") Long attrId,
        @Param("enabled") Boolean enabled,
        @Param("attrValue") String attrValue,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );

    /**
     * Get distinct attribute values for an attribute type
     */
    @Query("SELECT DISTINCT pa.productAttributeValue FROM ProductAttribute pa " +
           "WHERE pa.productAttr.productAttrId = :attrId " +
           "AND pa.productAttributeEnabled = true " +
           "ORDER BY pa.productAttributeValue ASC")
    List<String> findDistinctValuesByAttrId(@Param("attrId") Long attrId);

    /**
     * Get price range for an attribute type
     */
    @Query("SELECT MIN(pa.productAttributePrice), MAX(pa.productAttributePrice) FROM ProductAttribute pa " +
           "WHERE pa.productAttr.productAttrId = :attrId " +
           "AND pa.productAttributeEnabled = true " +
           "AND pa.productAttributePrice IS NOT NULL")
    List<Object[]> getPriceRangeByAttrId(@Param("attrId") Long attrId);

    // =============================================
    // CUSTOM OPERATIONS
    // =============================================
    
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
           "SET pa.productAttr = CASE WHEN :productAttrId IS NOT NULL THEN (SELECT attr FROM ProductAttr attr WHERE attr.productAttrId = :productAttrId) ELSE pa.productAttr END, " +
           "    pa.productAttributePrice = CASE WHEN :price IS NOT NULL THEN :price ELSE pa.productAttributePrice END, " +
           "    pa.productAttributeEnabled = CASE WHEN :enabled IS NOT NULL THEN :enabled ELSE pa.productAttributeEnabled END " +
           "WHERE pa.productAttributeValue IN :values")
    int updateByAttributeValues(@Param("values") List<String> values,
                               @Param("productAttrId") Long productAttrId,
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
    @Query("SELECT pa FROM ProductAttribute pa " +
           "WHERE pa.productAttributeValue IN :values " +
           "ORDER BY pa.productAttr.productAttrName ASC, pa.productAttributeValue ASC")
    List<ProductAttribute> findByAttributeValues(@Param("values") List<String> values);
    
    /**
     * Count attributes by attribute type
     */
    @Query("SELECT COUNT(pa) FROM ProductAttribute pa " +
           "WHERE pa.productAttr.productAttrId = :attrId " +
           "AND pa.productAttributeEnabled = true")
    Long countByProductAttrId(@Param("attrId") Long attrId);
}