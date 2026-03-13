package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * ProductImage DTO for API requests and responses
 * Contains PRODUCT_IMAGE table data with nested related entities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {
    
    private Long productImageId;

    @Builder.Default
    private Boolean productImagePrimary = false;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;

    // Nested related entities following clean architecture
    private ProductDto product;
    private ImageDto image;
}