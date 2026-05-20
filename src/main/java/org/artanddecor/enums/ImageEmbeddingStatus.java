package org.artanddecor.enums;

/**
 * Enum for Image Embedding Status
 * Represents the processing status of image embeddings for AI search functionality
 */
public enum ImageEmbeddingStatus {
    PENDING("Pending processing"),
    COMPLETED("Processing completed successfully"),
    FAILED("Processing failed");

    private final String description;

    ImageEmbeddingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return this.name();
    }
}