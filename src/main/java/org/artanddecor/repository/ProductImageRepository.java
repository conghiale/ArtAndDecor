package org.artanddecor.repository;

import org.artanddecor.model.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ProductImage Repository for database operations
 */
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    // =============================================
    // FIND OPERATIONS
    // =============================================

    /**
     * Find product images by product ID
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.productId = :productId ORDER BY pi.productImagePrimary DESC, pi.createdDt ASC")
    List<ProductImage> findByProductId(@Param("productId") Long productId);

    /**
     * Find primary image for product
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.productId = :productId AND pi.productImagePrimary = true")
    Optional<ProductImage> findPrimaryImageByProductId(@Param("productId") Long productId);

    /**
     * Find product images by image ID
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.image.imageId = :imageId")
    List<ProductImage> findByImageId(@Param("imageId") Long imageId);

    /**
     * Check if product-image combination exists
     */
    boolean existsByProductProductIdAndImageImageId(Long productId, Long imageId);

    // =============================================
    // SEARCH OPERATIONS
    // =============================================

    /**
     * Search product images by multiple criteria with pagination
     */
    @Query("SELECT pi FROM ProductImage pi " +
           "WHERE (:productId IS NULL OR pi.product.productId = :productId) " +
           "AND (:isPrimary IS NULL OR pi.productImagePrimary = :isPrimary) " +
           "ORDER BY pi.product.productName ASC, pi.productImagePrimary DESC, pi.createdDt ASC")
    Page<ProductImage> findProductImagesByCriteriaPaginated(
        @Param("productId") Long productId,
        @Param("isPrimary") Boolean isPrimary,
        Pageable pageable
    );

    /**
     * Count images for product
     */
    @Query("SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.productId = :productId")
    Long countByProductId(@Param("productId") Long productId);

    /**
     * Delete by product ID and image ID
     */
    void deleteByProductProductIdAndImageImageId(Long productId, Long imageId);
}