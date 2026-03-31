package org.artanddecor.repository;

import org.artanddecor.model.CartItemAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CartItemAttribute Repository for database operations
 * Simplified mapping between cart items and their selected product attributes
 */
@Repository
public interface CartItemAttributeRepository extends JpaRepository<CartItemAttribute, Long> {

    /**
     * Find cart item attributes by cart item ID
     * @param cartItemId Cart item ID
     * @return List of cart item attributes
     */
    List<CartItemAttribute> findByCartItemCartItemId(Long cartItemId);

    /**
     * Find cart item attributes by product attribute ID
     * @param productAttributeId Product attribute ID
     * @return List of cart item attributes
     */
    List<CartItemAttribute> findByProductAttributeProductAttributeId(Long productAttributeId);

    /**
     * Delete all cart item attributes by cart item ID
     * @param cartItemId Cart item ID
     */
    @Modifying
    @Query("DELETE FROM CartItemAttribute cia WHERE cia.cartItem.cartItemId = :cartItemId")
    void deleteByCartItemId(@Param("cartItemId") Long cartItemId);

    /**
     * Delete a specific cart item attribute by cart item ID and product attribute ID
     * @param cartItemId Cart item ID
     * @param productAttributeId Product attribute ID
     */
    @Modifying
    @Query("DELETE FROM CartItemAttribute cia WHERE cia.cartItem.cartItemId = :cartItemId " +
           "AND cia.productAttribute.productAttributeId = :productAttributeId")
    void deleteByCartItemIdAndProductAttributeId(
            @Param("cartItemId") Long cartItemId, 
            @Param("productAttributeId") Long productAttributeId);

    /**
     * Check if cart item attribute exists by cart item ID and product attribute ID
     * @param cartItemId Cart item ID
     * @param productAttributeId Product attribute ID
     * @return true if exists, false otherwise
     */
    boolean existsByCartItemCartItemIdAndProductAttributeProductAttributeId(
            Long cartItemId, Long productAttributeId);

    /**
     * Count cart item attributes by cart item ID
     * @param cartItemId Cart item ID
     * @return Number of attributes for the cart item
     */
    long countByCartItemCartItemId(Long cartItemId);

    /**
     * Find cart item attributes with product attribute details for efficient loading
     * @param cartItemId Cart item ID
     * @return List of cart item attributes with joined product attribute data
     */
    @Query("SELECT cia FROM CartItemAttribute cia " +
           "JOIN FETCH cia.productAttribute pa " +
           "JOIN FETCH pa.productAttr attr " +
           "WHERE cia.cartItem.cartItemId = :cartItemId " +
           "ORDER BY attr.productAttrName, pa.productAttributeValue")
    List<CartItemAttribute> findByCartItemIdWithDetails(@Param("cartItemId") Long cartItemId);

    /**
     * Find cart item attributes by multiple cart item IDs
     * @param cartItemIds List of cart item IDs
     * @return List of cart item attributes
     */
    @Query("SELECT cia FROM CartItemAttribute cia " +
           "JOIN FETCH cia.productAttribute pa " +
           "JOIN FETCH pa.productAttr attr " +
           "WHERE cia.cartItem.cartItemId IN :cartItemIds " +
           "ORDER BY cia.cartItem.cartItemId, attr.productAttrName, pa.productAttributeValue")
    List<CartItemAttribute> findByCartItemIdsWithDetails(@Param("cartItemIds") List<Long> cartItemIds);
}