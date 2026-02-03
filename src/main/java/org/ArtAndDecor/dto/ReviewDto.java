package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Review DTO for API requests and responses
 * Contains information from REVIEW table and related USER, PRODUCT information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    
    private Long reviewId;
    
    private Long parentReviewId;
    
    @Size(max = 255, message = "Review title must not exceed 255 characters")
    private String reviewTitle;
    
    private String reviewContent;
    
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;
    
    private Boolean isVisible = true;
    
    private Integer likeCount = 0;
    
    private Integer replyCount = 0;
    
    private Boolean reviewEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // =============================================
    // NESTED DTOs (complete related entity data)
    // =============================================
    private UserDto user;
    private ProductDto product;
    private ReviewDto parentReview;
    
    /**
     * Generate full name from user data
     */
    public String generateFullName() {
        if (user == null) {
            return "Unknown User";
        }
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String userName = user.getUserName();
        
        if (firstName == null && lastName == null) {
            return userName != null ? userName : "Unknown User";
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }
    
    /**
     * Check if this is a reply to another review
     */
    public boolean isReply() {
        return parentReviewId != null;
    }
    
    /**
     * Check if this review has a valid rating
     */
    public boolean hasValidRating() {
        return rating != null && rating >= 1 && rating <= 5;
    }
}