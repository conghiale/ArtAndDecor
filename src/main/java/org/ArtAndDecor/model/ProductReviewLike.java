package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * ProductReviewLike Entity
 * Represents likes on product reviews by users
 */
@Entity
@Table(name = "PRODUCT_REVIEW_LIKE", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"USER_ID", "REVIEW_ID"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewLike {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductReviewLike.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_REVIEW_LIKE_ID")
    private Long productReviewLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEW_ID", nullable = false)
    private Review review;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new ProductReviewLike for review ID: {}, user ID: {}", 
                    review != null ? review.getReviewId() : null,
                    user != null ? user.getUserId() : null);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating ProductReviewLike ID: {}", productReviewLikeId);
        this.modifiedDt = LocalDateTime.now();
    }
}