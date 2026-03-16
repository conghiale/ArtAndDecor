package org.artanddecor.utils;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductReviewLikeDto;
import org.artanddecor.model.ProductReviewLike;
import org.springframework.stereotype.Component;

/**
 * Utility class for mapping ProductReviewLike entities to ProductReviewLikeDto objects
 * Supports clean architecture pattern with nested DTOs
 */
@Component
@RequiredArgsConstructor
public class ProductReviewLikeMapperUtil {

    private final UserMapperUtil userMapperUtil;

    private final ReviewMapperUtil reviewMapperUtil;
    
    // =============================================
    // PRODUCT REVIEW LIKE MAPPING METHODS
    // =============================================
    
    /**
     * Convert ProductReviewLike entity to ProductReviewLikeDto with foreign keys only (for basic operations)
     * @param productReviewLike ProductReviewLike entity
     * @return ProductReviewLikeDto with only basic fields and foreign key IDs
     */
    public ProductReviewLikeDto toBasicDto(ProductReviewLike productReviewLike) {
        if (productReviewLike == null) return null;
        
        ProductReviewLikeDto dto = new ProductReviewLikeDto();
        dto.setProductReviewLikeId(productReviewLike.getProductReviewLikeId());
        dto.setReviewId(productReviewLike.getReview() != null ? productReviewLike.getReview().getReviewId() : null);
        dto.setUserId(productReviewLike.getUser() != null ? productReviewLike.getUser().getUserId() : null);
        dto.setCreatedDt(productReviewLike.getCreatedDt());
        dto.setModifiedDt(productReviewLike.getModifiedDt());
        
        return dto;
    }
    
    /**
     * Convert ProductReviewLike entity to ProductReviewLikeDto with nested DTOs
     * @param productReviewLike ProductReviewLike entity
     * @return ProductReviewLikeDto with foreign keys and nested DTO objects
     */
    public ProductReviewLikeDto toDto(ProductReviewLike productReviewLike) {
        if (productReviewLike == null) return null;
        
        ProductReviewLikeDto dto = toBasicDto(productReviewLike);
        
        // Add nested DTOs for related entities
        if (productReviewLike.getUser() != null) {
            dto.setUser(userMapperUtil.toBasicDto(productReviewLike.getUser()));
        }
        
        if (productReviewLike.getReview() != null) {
            // Use basic DTO to avoid circular reference (Review -> ProductReviewLike -> Review)
            dto.setReview(reviewMapperUtil.toBasicDto(productReviewLike.getReview()));
        }
        
        return dto;
    }

    /**
     * Convert ProductReviewLike entity to ProductReviewLikeDto with full nested DTOs
     * @param productReviewLike ProductReviewLike entity
     * @return ProductReviewLikeDto with comprehensive nested data
     */
    public ProductReviewLikeDto toFullDto(ProductReviewLike productReviewLike) {
        if (productReviewLike == null) return null;
        
        ProductReviewLikeDto dto = toBasicDto(productReviewLike);
        
        // Add nested DTOs with full details but avoid circular references
        if (productReviewLike.getUser() != null) {
            dto.setUser(userMapperUtil.toBasicDto(productReviewLike.getUser()));
        }
        
        if (productReviewLike.getReview() != null) {
            // Use basic DTO to prevent circular reference
            dto.setReview(reviewMapperUtil.toBasicDto(productReviewLike.getReview()));
        }
        
        return dto;
    }

    /**
     * Convert ProductReviewLikeDto to ProductReviewLike entity (for save operations)
     * @param productReviewLikeDto ProductReviewLikeDto object
     * @return ProductReviewLike entity
     */
    public ProductReviewLike toEntity(ProductReviewLikeDto productReviewLikeDto) {
        if (productReviewLikeDto == null) return null;
        
        ProductReviewLike productReviewLike = new ProductReviewLike();
        productReviewLike.setProductReviewLikeId(productReviewLikeDto.getProductReviewLikeId());
        productReviewLike.setCreatedDt(productReviewLikeDto.getCreatedDt());
        productReviewLike.setModifiedDt(productReviewLikeDto.getModifiedDt());
        
        // Note: Related entities (User, Review) should be set separately
        // in the service layer to avoid potential issues with entity management
        
        return productReviewLike;
    }

    /**
     * Update ProductReviewLike entity from ProductReviewLikeDto (for update operations)
     * @param productReviewLike Existing ProductReviewLike entity
     * @param productReviewLikeDto ProductReviewLikeDto with updated data
     */
    public void updateEntityFromDto(ProductReviewLike productReviewLike, ProductReviewLikeDto productReviewLikeDto) {
        if (productReviewLike == null || productReviewLikeDto == null) return;
        
        // ProductReviewLike is typically immutable after creation (like/unlike operations)
        // Only modifiedDt might be updated, but this is usually handled automatically
        
        // Note: User and Review associations should not be changed after creation
        // If needed, delete the old like and create a new one
    }
}