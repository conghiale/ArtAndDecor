package org.artanddecor.repository;

import org.artanddecor.enums.ImageEmbeddingStatus;
import org.artanddecor.model.ImageEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ImageEmbedding Repository
 * Handles database operations for ImageEmbedding table
 */
@Repository
public interface ImageEmbeddingRepository extends JpaRepository<ImageEmbedding, Long> {

    /**
     * Find embedding by image ID
     * @param imageId the image ID
     * @return Optional containing ImageEmbedding if found
     */
    Optional<ImageEmbedding> findByImageId(Long imageId);

    /**
     * Check if embedding exists for image ID
     * @param imageId the image ID
     * @return true if embedding exists, false otherwise
     */
    boolean existsByImageId(Long imageId);

    /**
     * Delete embedding by image ID
     * @param imageId the image ID
     */
    void deleteByImageId(Long imageId);

    /**
     * Find embedding by image ID with non-null embedding data
     * @param imageId the image ID
     * @return Optional containing ImageEmbedding if found with valid embedding data
     */
    @Query("SELECT ie FROM ImageEmbedding ie WHERE ie.imageId = :imageId AND ie.embedding IS NOT NULL")
    Optional<ImageEmbedding> findByImageIdWithEmbedding(@Param("imageId") Long imageId);

    /**
     * Find image IDs by embedding status.
     * @param status embedding status to filter by
     * @return list of image IDs having the given status
     */
    @Query("SELECT ie.imageId FROM ImageEmbedding ie WHERE ie.imageEmbeddingStatus = :status")
    List<Long> findImageIdsByEmbeddingStatus(@Param("status") ImageEmbeddingStatus status);

    /**
     * Update embedding status by image ID (targeted update — avoids full entity merge)
     * @param imageId the image ID
     * @param status new status
     * @param now updated timestamp
     * @return number of rows updated
     */
    @Modifying
    @Transactional
    @Query("UPDATE ImageEmbedding ie SET ie.imageEmbeddingStatus = :status, ie.modifiedDt = :now WHERE ie.imageId = :imageId")
    int updateStatusByImageId(@Param("imageId") Long imageId, @Param("status") ImageEmbeddingStatus status, @Param("now") LocalDateTime now);
}