package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Product Create DTO for API create requests
 * Contains minimal required information for creating a product with IDs instead of nested objects
 * This provides a cleaner and simpler interface for client integration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {
    
    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    private String productName;
    
    @NotBlank(message = "Product slug is required")
    @Size(max = 64, message = "Product slug must not exceed 64 characters")
    private String productSlug;
    
    @NotBlank(message = "Product code is required")
    @Size(max = 64, message = "Product code must not exceed 64 characters")
    private String productCode;

    @NotNull(message = "Product category ID is required")
    private Long productCategoryId;

    @NotNull(message = "Product state ID is required")
    private Long productStateId;

    @DecimalMin(value = "0.0", message = "Product price must not be negative")
    @Digits(integer = 13, fraction = 2, message = "Invalid price format")
    private BigDecimal productPrice;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must not be negative")
    private Integer stockQuantity;
    
    @Min(value = 0, message = "Sold quantity must not be negative")
    private Integer soldQuantity = 0;
    
    @NotBlank(message = "Product description is required")
    private String productDescription;
    
    private Boolean productEnabled = true;
    
    private Boolean productFeatured = false;
    
    private Boolean productHighlighted = false;
    
    private Long seoMetaId;
}