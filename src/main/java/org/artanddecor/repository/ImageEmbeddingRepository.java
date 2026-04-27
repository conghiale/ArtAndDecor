package org.artanddecor.repository;

import org.artanddecor.model.ImageEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}