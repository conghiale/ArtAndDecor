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
    
    @Min(value = 0, message = "Cấp độ đánh giá không được là số âm")
    @Builder.Default
    private Integer reviewLevel = 0;
    
    @NotNull(message = "Điểm đánh giá là bắt buộc")
    @Min(value = 1, message = "Điểm đánh giá phải lớn hơn hoặc bằng 1")
    @Max(value = 5, message = "Điểm đánh giá không được vượt quá 5")
    private int rating;
    
    @NotBlank(message = "Nội dung đánh giá là bắt buộc")
    @Size(max = 65535, message = "Nội dung đánh giá không được vượt quá 65535 ký tự")
    private String reviewContent;
    
    @Min(value = 0, message = "Số lượt thích không được là số âm")
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
            return "Người dùng không xác định";
        }
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String userName = user.getUserName();
        
        if (firstName == null && lastName == null) {
            return userName != null ? userName : "Người dùng không xác định";
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