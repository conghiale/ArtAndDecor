package org.artanddecor.dto;

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
    
    private Long rootReviewId;
    
    @Min(value = 0, message = "Review level must not be negative")
    @Builder.Default
    private Integer reviewLevel = 0;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private int rating;
    
    @NotBlank(message = "Review content is required")
    @Size(max = 65535, message = "Review content must not exceed 65535 characters")
    private String reviewContent;
    
    @Min(value = 0, message = "Count like must not be negative")
    @Builder.Default
    private Integer countLike = 0;
    
    @Builder.Default
    private Boolean isVisible = true;
    
    @Builder.Default
    private Boolean isDeleted = false;
    
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
}