package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Review Entity
 * Represents product reviews by users
 */
@Entity
@Table(name = "REVIEW")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    
    private static final Logger logger = LogManager.getLogger(Review.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_REVIEW_ID")
    private Review parentReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOT_REVIEW_ID")
    private Review rootReview;

    @Column(name = "REVIEW_LEVEL", nullable = false)
    private Integer reviewLevel = 0;

    @Column(name = "RATING", nullable = false)
    private Byte rating;

    @Column(name = "REVIEW_CONTENT", columnDefinition = "TEXT", nullable = false)
    private String reviewContent;

    @Column(name = "COUNT_LIKE", nullable = false)
    private Integer countLike = 0;

    @Column(name = "IS_VISIBLE", nullable = false)
    private Boolean isVisible = true;

    @Column(name = "IS_DELETED", nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY_ROLE_ID", nullable = false)
    private UserRole createdByRole;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with ProductReviewLike
    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductReviewLike> productReviewLikes;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new Review for product ID: {}, user ID: {}", 
                    product != null ? product.getProductId() : null,
                    user != null ? user.getUserId() : null);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
        if (this.isVisible == null) {
            this.isVisible = true;
        }
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
        if (this.countLike == null) {
            this.countLike = 0;
        }
        if (this.reviewLevel == null) {
            this.reviewLevel = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating Review ID: {}", reviewId);
        this.modifiedDt = LocalDateTime.now();
    }

    /**
     * Check if this review is a reply to another review
     * @return true if this is a reply
     */
    public boolean isReply() {
        return parentReview != null;
    }

    /**
     * Check if rating is valid (1-5 range)
     * @return true if rating is valid
     */
    public boolean hasValidRating() {
        return rating != null && rating >= 1 && rating <= 5;
    }
}