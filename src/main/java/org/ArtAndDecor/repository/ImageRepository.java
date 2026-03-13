package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Image Repository for database operations
 * Streamlined with only necessary methods used by ImageController and ImageService
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    // =============================================
    // FIND OPERATIONS - Used by service
    // =============================================

    /**
     * Find image by slug for retrieving single image
     */
    Optional<Image> findByImageSlug(String imageSlug);

    /**
     * Check if slug exists (for validation)
     */
    boolean existsByImageSlug(String imageSlug);

    /**
     * Check if display name exists (for validation)
     */
    boolean existsByImageDisplayName(String imageDisplayName);

    // =============================================
    // SEARCH OPERATIONS - Customer and Admin
    // =============================================

    /**
     * Search images by multiple criteria with pagination
     */
    @Query("SELECT i FROM Image i " +
           "WHERE (:imageSize IS NULL OR LOWER(i.imageSize) LIKE LOWER(CONCAT('%', :imageSize, '%'))) " +
           "AND (:imageFormat IS NULL OR LOWER(i.imageFormat) = LOWER(:imageFormat)) " +
           "AND (:textSearch IS NULL OR (" +
           "     LOWER(i.imageName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(i.imageDisplayName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(i.imageSlug) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           "     LOWER(i.imageRemark) LIKE LOWER(CONCAT('%', :textSearch, '%'))" +
           ")) " +
           "ORDER BY i.createdDt DESC")
    Page<Image> findImagesByCriteriaPaginated(
        @Param("imageSize") String imageSize,
        @Param("imageFormat") String imageFormat,
        @Param("textSearch") String textSearch,
        Pageable pageable
    );

    /**
     * Find images by format slug (Customer filtering)  
     */
    @Query("SELECT i FROM Image i " +
           "WHERE LOWER(i.imageSize) LIKE LOWER(CONCAT('%', :formatSlug, '%')) " +
           "ORDER BY i.createdDt DESC")
    List<Image> findByFormatSlug(@Param("formatSlug") String formatSlug);

    /**
     * Search images by name pattern (Customer search)
     */
    @Query("SELECT i FROM Image i " +
           "WHERE LOWER(i.imageName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(i.imageDisplayName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(i.imageSlug) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(i.imageRemark) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY i.createdDt DESC")
    List<Image> findBySearchTerm(@Param("searchTerm") String searchTerm);

    /**
     * Search images by multiple criteria with pagination (Admin search)
     */
    @Query("SELECT i FROM Image i " +
           "WHERE (:imageId IS NULL OR i.imageId = :imageId) " +
           "AND (:imageName IS NULL OR LOWER(i.imageName) LIKE LOWER(CONCAT('%', :imageName, '%'))) " +
           "AND (:imageDisplayName IS NULL OR LOWER(i.imageDisplayName) LIKE LOWER(CONCAT('%', :imageDisplayName, '%'))) " +
           "AND (:formatId IS NULL OR LOWER(i.imageSize) LIKE LOWER(CONCAT('%', :formatId, '%'))) " +
           "ORDER BY i.modifiedDt DESC")
    Page<Image> findByCriteriaPaginated(@Param("imageId") Long imageId,
                                       @Param("imageName") String imageName,
                                       @Param("imageDisplayName") String imageDisplayName,
                                       @Param("formatId") Long formatId,
                                       Pageable pageable);

    // =============================================
    // PAGINATION OPERATIONS
    // =============================================

    /**
     * Find all images with pagination (for customer gallery)
     */
    @Query("SELECT i FROM Image i " +
           "ORDER BY i.modifiedDt DESC")
    Page<Image> findAllWithPagination(Pageable pageable);

    // =============================================
    // ANALYTICS & REPORTING
    // =============================================

    /**
     * Count total images (Dashboard statistics)
     */
    @Query("SELECT COUNT(i) FROM Image i")
    long countTotalImages();

    /**
     * Get all distinct image sizes for combobox
     */
    @Query("SELECT DISTINCT i.imageSize FROM Image i WHERE i.imageSize IS NOT NULL ORDER BY i.imageSize")
    List<String> findDistinctImageSizes();

    /**
     * Get all distinct image formats for combobox
     */
    @Query("SELECT DISTINCT i.imageFormat FROM Image i WHERE i.imageFormat IS NOT NULL ORDER BY i.imageFormat")
    List<String> findDistinctImageFormats();
}