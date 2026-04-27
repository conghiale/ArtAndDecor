package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * ImageEmbedding Entity
 * Represents image embeddings for AI search functionality
 * Maps to IMAGE_EMBEDDING table
 */
@Entity
@Table(name = "IMAGE_EMBEDDING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageEmbedding {
    
    private static final Logger logger = LoggerFactory.getLogger(ImageEmbedding.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_EMBEDDING_ID")
    private Long imageEmbeddingId;

    @Column(name = "IMAGE_ID", nullable = false)
    private Long imageId;

    @Column(name = "EMBEDDING", columnDefinition = "VARBINARY(8000)")
    private byte[] embedding;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // Many-to-One relationship with Image
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID", insertable = false, updatable = false)
    private Image image;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new ImageEmbedding for image ID: {}", imageId);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating ImageEmbedding for image ID: {}", imageId);
        this.modifiedDt = LocalDateTime.now();
    }
}