package org.artanddecor.utils;

import org.artanddecor.dto.*;
import org.artanddecor.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for mapping entities to DTOs for Product Management
 * Provides comprehensive mapping methods with different levels of detail
 */
@Component
public class ProductMapperUtil {

    // =============================================
    // PRODUCT TYPE MAPPING METHODS
    // =============================================

    /**
     * Convert ProductTypeDto to ProductType entity
     * @param productTypeDto Product type DTO
     * @return ProductType entity
     */
    public static ProductType toProductTypeEntity(ProductTypeDto productTypeDto) {
        if (productTypeDto == null) return null;
        
        ProductType productType = new ProductType();
        productType.setProductTypeId(productTypeDto.getProductTypeId());
        productType.setProductTypeName(productTypeDto.getProductTypeName());
        productType.setProductTypeSlug(productTypeDto.getProductTypeSlug());
        productType.setProductTypeDisplayName(productTypeDto.getProductTypeDisplayName());
        productType.setProductTypeRemark(productTypeDto.getProductTypeRemark());
        productType.setProductTypeEnabled(productTypeDto.getProductTypeEnabled());
        productType.setSeoMetaId(productTypeDto.getSeoMetaId());
        productType.setCreatedDt(productTypeDto.getCreatedDt());
        productType.setModifiedDt(productTypeDto.getModifiedDt());
        
        return productType;
    }

    /**
     * Convert ProductType entity to ProductTypeDto
     * @param productType Product type entity
     * @return ProductTypeDto
     */

    public static ProductTypeDto toProductTypeDto(ProductType productType) {
        if (productType == null) return null;
        
        return ProductTypeDto.builder()
                .productTypeId(productType.getProductTypeId())
                .productTypeName(productType.getProductTypeName())
                .productTypeSlug(productType.getProductTypeSlug())
                .productTypeDisplayName(productType.getProductTypeDisplayName())
                .productTypeRemark(productType.getProductTypeRemark())
                .productTypeEnabled(productType.getProductTypeEnabled())
                .seoMetaId(productType.getSeoMetaId())
                .image(ImageMapperUtil.toBasicDto(productType.getImage()))
                .createdDt(productType.getCreatedDt())
                .modifiedDt(productType.getModifiedDt())
                .build();
    }

    // =============================================
    // PRODUCT CATEGORY MAPPING - DTO only (Entity mapping removed) 
    // =============================================

    public static ProductCategoryDto toProductCategoryDto(ProductCategory productCategory) {
        if (productCategory == null) return null;
        
        return ProductCategoryDto.builder()
                .productCategoryId(productCategory.getProductCategoryId())
                .productCategoryName(productCategory.getProductCategoryName())
                .productCategorySlug(productCategory.getProductCategorySlug())
                .productCategoryDisplayName(productCategory.getProductCategoryDisplayName())
                .productCategoryRemark(productCategory.getProductCategoryRemark())
                .productCategoryEnabled(productCategory.getProductCategoryEnabled())
                .productCategoryVisible(productCategory.getProductCategoryVisible())
                .seoMetaId(productCategory.getSeoMetaId())
                .productTypeId(productCategory.getProductType() != null ? productCategory.getProductType().getProductTypeId() : null)
                .parentCategoryId(productCategory.getParentCategory() != null ? productCategory.getParentCategory().getProductCategoryId() : null)
                .productType(toProductTypeDto(productCategory.getProductType()))
                .image(ImageMapperUtil.toBasicDto(productCategory.getImage()))
                .createdDt(productCategory.getCreatedDt())
                .modifiedDt(productCategory.getModifiedDt())
                .build();
    }

    // =============================================
    // PRODUCT STATE MAPPING METHODS
    // =============================================

    /**
     * Convert ProductStateDto to ProductState entity
     * @param productStateDto Product state DTO
     * @return ProductState entity
     */
    public static ProductState toProductStateEntity(ProductStateDto productStateDto) {
        if (productStateDto == null) return null;
        
        ProductState productState = new ProductState();
        productState.setProductStateId(productStateDto.getProductStateId());
        productState.setProductStateName(productStateDto.getProductStateName());
        productState.setProductStateEnabled(productStateDto.getProductStateEnabled());
        productState.setProductStateDisplayName(productStateDto.getProductStateDisplayName());
        productState.setProductStateRemark(productStateDto.getProductStateRemark());
        productState.setCreatedDt(productStateDto.getCreatedDt());
        productState.setModifiedDt(productStateDto.getModifiedDt());
        
        return productState;
    }

    /**
     * Convert ProductState entity to ProductStateDto
     * @param productState Product state entity
     * @return ProductStateDto
     */

    public static ProductStateDto toProductStateDto(ProductState productState) {
        if (productState == null) return null;
        
        return ProductStateDto.builder()
                .productStateId(productState.getProductStateId())
                .productStateName(productState.getProductStateName())
                .productStateEnabled(productState.getProductStateEnabled())
                .productStateDisplayName(productState.getProductStateDisplayName())
                .productStateRemark(productState.getProductStateRemark())
                .createdDt(productState.getCreatedDt())
                .modifiedDt(productState.getModifiedDt())
                .build();
    }

    // =============================================
    // PRODUCT ATTR MAPPING METHODS
    // =============================================

    /**
     * Convert ProductAttrDto to ProductAttr entity
     * @param productAttrDto Product attribute DTO
     * @return ProductAttr entity
     */
    public static ProductAttr toProductAttrEntity(ProductAttrDto productAttrDto) {
        if (productAttrDto == null) return null;
        
        ProductAttr productAttr = new ProductAttr();
        productAttr.setProductAttrId(productAttrDto.getProductAttrId());
        productAttr.setProductAttrName(productAttrDto.getProductAttrName());
        productAttr.setProductAttrEnabled(productAttrDto.getProductAttrEnabled());
        productAttr.setProductAttrDisplayName(productAttrDto.getProductAttrDisplayName());
        productAttr.setProductAttrRemark(productAttrDto.getProductAttrRemark());
        productAttr.setCreatedDt(productAttrDto.getCreatedDt());
        productAttr.setModifiedDt(productAttrDto.getModifiedDt());
        
        return productAttr;
    }

    /**
     * Convert ProductAttr entity to ProductAttrDto
     * @param productAttr Product attribute entity
     * @return ProductAttrDto
     */

    public static ProductAttrDto toProductAttrDto(ProductAttr productAttr) {
        if (productAttr == null) return null;
        
        return ProductAttrDto.builder()
                .productAttrId(productAttr.getProductAttrId())
                .productAttrName(productAttr.getProductAttrName())
                .productAttrEnabled(productAttr.getProductAttrEnabled())
                .productAttrDisplayName(productAttr.getProductAttrDisplayName())
                .productAttrRemark(productAttr.getProductAttrRemark())
                .createdDt(productAttr.getCreatedDt())
                .modifiedDt(productAttr.getModifiedDt())
                .build();
    }

    // =============================================
    // PRODUCT MAPPING METHODS - Core functionality only
    // =============================================

    /**
     * Convert Product entity to ProductDto with full relationship mapping
     * @param product Product entity
     * @return ProductDto with computed fields and relationships
     */
    public static ProductDto toProductDto(Product product) {
        if (product == null) return null;
        
        // Map product images (without circular reference)
        List<ProductImageDto> productImagesDto = null;
        if (product.getProductImages() != null) {
            productImagesDto = product.getProductImages().stream()
                    .map(ProductMapperUtil::toProductImageDtoWithoutProduct)
                    .collect(Collectors.toList());
        }
        
        // Map product attributes (grouped by ProductAttr, only quantity > 0)
        List<ProductAttrWithAttributesDto> productAttributeGroupsDto = null;
        if (product.getProductAttributes() != null) {
            productAttributeGroupsDto = toProductAttrWithAttributesDtoList(product.getProductAttributes());
        }
        
        // Map reviews (simplified version without circular reference)
        List<ReviewDto> reviewsDto = null;
        if (product.getReviews() != null) {
            reviewsDto = product.getReviews().stream()
                    .map(ProductMapperUtil::toReviewDtoSimplified)
                    .collect(Collectors.toList());
        }
        
        ProductDto productDto = ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productSlug(product.getProductSlug())
                .productCode(product.getProductCode())
                .soldQuantity(product.getSoldQuantity())
                .stockQuantity(product.getStockQuantity())
                .productDescription(product.getProductDescription())
                .productPrice(product.getProductPrice())
                .productEnabled(product.getProductEnabled())
                .productFeatured(product.getProductFeatured())
                .productHighlighted(product.getProductHighlighted())
                .productCategory(toProductCategoryDto(product.getProductCategory()))
                .productState(toProductStateDto(product.getProductState()))
                .seoMeta(SeoMetaMapperUtil.toSeoMetaDto(product.getSeoMeta()))
                .productImages(productImagesDto)
                .productAttributeGroups(productAttributeGroupsDto)
                .reviews(reviewsDto)
                .createdDt(product.getCreatedDt())
                .modifiedDt(product.getModifiedDt())
                .build();
        
        // Set computed fields
        productDto.setInStock(product.isInStock());
        productDto.setTotalStockValue(product.getTotalStockValue());
        
        // Calculate average rating and review statistics
        if (reviewsDto != null && !reviewsDto.isEmpty()) {
            double avgRating = reviewsDto.stream()
                    .mapToInt(review -> review.getRating())
                    .average().orElse(0.0);
            productDto.setAverageRating(avgRating);
            productDto.setTotalReviews(reviewsDto.size());
            
            int totalLikes = reviewsDto.stream()
                    .mapToInt(review -> review.getCountLike() != null ? review.getCountLike() : 0)
                    .sum();
            productDto.setTotalReviewLikes(totalLikes);
        } else {
            productDto.setAverageRating(0.0);
            productDto.setTotalReviews(0);
            productDto.setTotalReviewLikes(0);
        }
        
        return productDto;
    }

    /**
     * Main method: Create Product entity from ProductRequestDto with optional SEO Meta handling
     * Unified method that handles both create and update scenarios
     * @param productRequestDto Product request DTO
     * @param seoMetaId Optional SEO Meta ID from SEO service (null for new products without SEO)
     * @return Product entity
     */
    public static Product toProductEntityFromRequestDto(ProductRequestDto productRequestDto, Long seoMetaId) {
        if (productRequestDto == null) return null;
        
        Product product = new Product();
        product.setProductName(productRequestDto.getProductName());
        product.setProductSlug(productRequestDto.getProductSlug());
        product.setProductCode(productRequestDto.getProductCode());
        product.setSoldQuantity(productRequestDto.getSoldQuantity());
        product.setStockQuantity(productRequestDto.getStockQuantity());
        product.setProductDescription(productRequestDto.getProductDescription());
        product.setProductPrice(productRequestDto.getProductPrice());
        product.setProductEnabled(productRequestDto.getProductEnabled());
        product.setProductFeatured(productRequestDto.getProductFeatured());
        product.setProductHighlighted(productRequestDto.getProductHighlighted());
        product.setSeoMetaId(seoMetaId); // Handled via parameter
        
        // Set ProductCategory reference
        if (productRequestDto.getProductCategoryId() != null) {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProductCategoryId(productRequestDto.getProductCategoryId());
            product.setProductCategory(productCategory);
        }
        
        // Set ProductState reference
        if (productRequestDto.getProductStateId() != null) {
            ProductState productState = new ProductState();
            productState.setProductStateId(productRequestDto.getProductStateId());
            product.setProductState(productState);
        }
        
        product.setCreatedDt(LocalDateTime.now());
        product.setModifiedDt(LocalDateTime.now());
        
        return product;
    }



    /**
     * Main method: Update Product entity from ProductRequestDto with optional SEO Meta handling  
     * Unified method that handles field updates without changing timestamps/references unnecessarily
     * @param product Existing Product entity to update
     * @param productRequestDto Product request DTO with updated data
     * @param seoMetaId Optional SEO Meta ID (null to keep existing)
     */
    public static void updateProductEntityFromRequestDto(Product product, ProductRequestDto productRequestDto, Long seoMetaId) {
        if (product == null || productRequestDto == null) return;
        
        product.setProductName(productRequestDto.getProductName());
        product.setProductSlug(productRequestDto.getProductSlug());
        product.setProductCode(productRequestDto.getProductCode());
        product.setSoldQuantity(productRequestDto.getSoldQuantity());
        product.setStockQuantity(productRequestDto.getStockQuantity());
        product.setProductDescription(productRequestDto.getProductDescription());
        product.setProductPrice(productRequestDto.getProductPrice());
        product.setProductEnabled(productRequestDto.getProductEnabled());
        product.setProductFeatured(productRequestDto.getProductFeatured());
        product.setProductHighlighted(productRequestDto.getProductHighlighted());
        
        // Update SEO Meta ID if provided
        if (seoMetaId != null) {
            product.setSeoMetaId(seoMetaId);
        }
        
        // Update ProductCategory reference
        if (productRequestDto.getProductCategoryId() != null) {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProductCategoryId(productRequestDto.getProductCategoryId());
            product.setProductCategory(productCategory);
        }
        
        // Update ProductState reference
        if (productRequestDto.getProductStateId() != null) {
            ProductState productState = new ProductState();
            productState.setProductStateId(productRequestDto.getProductStateId());
            product.setProductState(productState);
        }
        
        product.setModifiedDt(LocalDateTime.now());
    }
    




    // =============================================
    // PRODUCT IMAGE MAPPING METHODS
    // =============================================
    
    /**
     * Map ProductImage to DTO without Product reference to avoid circular dependency
     */
    public static ProductImageDto toProductImageDtoWithoutProduct(ProductImage productImage) {
        if (productImage == null) return null;
        
        return ProductImageDto.builder()
                .productImageId(productImage.getProductImageId())
                .productImagePrimary(productImage.getProductImagePrimary())
                .product(null) // Avoid circular reference
                .image(ImageMapperUtil.toBasicDto(productImage.getImage()))
                .createdDt(productImage.getCreatedDt())
                .modifiedDt(productImage.getModifiedDt())
                .build();
    }

    // =============================================
    // PRODUCT CATEGORY MAPPING METHODS
    // =============================================

    /**
     * Convert ProductCategoryDto to ProductCategory entity
     * @param productCategoryDto Product category DTO
     * @return ProductCategory entity
     */
    public static ProductCategory toProductCategoryEntity(ProductCategoryDto productCategoryDto) {
        if (productCategoryDto == null) return null;
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setProductCategoryId(productCategoryDto.getProductCategoryId());
        productCategory.setProductCategoryName(productCategoryDto.getProductCategoryName());
        productCategory.setProductCategorySlug(productCategoryDto.getProductCategorySlug());
        productCategory.setProductCategoryDisplayName(productCategoryDto.getProductCategoryDisplayName());
        productCategory.setProductCategoryRemark(productCategoryDto.getProductCategoryRemark());
        productCategory.setProductCategoryEnabled(productCategoryDto.getProductCategoryEnabled());
        productCategory.setProductCategoryVisible(productCategoryDto.getProductCategoryVisible());
        productCategory.setSeoMetaId(productCategoryDto.getSeoMetaId());
        productCategory.setCreatedDt(productCategoryDto.getCreatedDt());
        productCategory.setModifiedDt(productCategoryDto.getModifiedDt());
        
        return productCategory;
    }

    // =============================================
    // PRODUCT ATTRIBUTE MAPPING METHODS
    // =============================================

    public static ProductAttributeDto toProductAttributeDto(ProductAttribute productAttribute) {
        if (productAttribute == null) return null;
        
        return ProductAttributeDto.builder()
                .productAttributeId(productAttribute.getProductAttributeId())
                .productAttributeValue(productAttribute.getProductAttributeValue())
                .productAttributeQuantity(productAttribute.getProductAttributeQuantity())
                .productAttributeEnabled(productAttribute.getProductAttributeEnabled())
                .productAttr(toProductAttrDto(productAttribute.getProductAttr()))
                .createdDt(productAttribute.getCreatedDt())
                .modifiedDt(productAttribute.getModifiedDt())
                .build();
    }
    
    /**
     * Map List of ProductAttributes to grouped ProductAttrWithAttributesDto
     * Only includes attributes with quantity > 0
     */
    public static List<ProductAttrWithAttributesDto> toProductAttrWithAttributesDtoList(List<ProductAttribute> productAttributes) {
        if (productAttributes == null) return null;
        
        // Filter attributes with quantity > 0 and group by ProductAttr
        Map<Long, List<ProductAttribute>> groupedByAttr = productAttributes.stream()
                .filter(attr -> attr.getProductAttributeQuantity() != null && attr.getProductAttributeQuantity() > 0)
//                .filter(attr -> attr.getProductAttributeEnabled() != null && attr.getProductAttributeEnabled())
                .collect(Collectors.groupingBy(
                    attr -> attr.getProductAttr().getProductAttrId()
                ));
        
        return groupedByAttr.entrySet().stream()
                .map(entry -> {
                    List<ProductAttribute> attributes = entry.getValue();
                    if (attributes.isEmpty()) return null;
                    
                    // Get ProductAttr from first attribute in group
                    ProductAttr productAttr = attributes.get(0).getProductAttr();
                    
                    // Convert attributes to DTOs
                    List<ProductAttributeDto> attributeDtos = attributes.stream()
                            .map(ProductMapperUtil::toProductAttributeDto)
                            .collect(Collectors.toList());
                    
                    ProductAttrWithAttributesDto dto = ProductAttrWithAttributesDto.builder()
                            .productAttr(toProductAttrDto(productAttr))
                            .attributeValues(attributeDtos)
                            .build();
                    
                    // Calculate computed fields
                    dto.setTotalQuantity(dto.calculateTotalQuantity());
                    dto.setVariantCount(dto.calculateVariantCount());
                    dto.setHasStock(dto.calculateHasStock());
                    
                    return dto;
                })
                .filter(dto -> dto != null)
                .sorted((a, b) -> a.getProductAttr().getProductAttrName().compareTo(b.getProductAttr().getProductAttrName()))
                .collect(Collectors.toList());
    }

    // =============================================
    // HELPER METHODS
    // =============================================
    
    /**
     * Map Review to DTO (simplified version without Product reference)
     */
    public static ReviewDto toReviewDtoSimplified(Review review) {
        if (review == null) return null;
        
        return ReviewDto.builder()
                .reviewId(review.getReviewId())
                .rating(review.getRating() != null ? review.getRating().intValue() : 0)
                .reviewContent(review.getReviewContent())
                .countLike(review.getCountLike())
                .isVisible(review.getIsVisible())
                .isDeleted(review.getIsDeleted())
                .reviewLevel(review.getReviewLevel())
                .parentReviewId(review.getParentReview() != null ? review.getParentReview().getReviewId() : null)
                .rootReviewId(review.getRootReview() != null ? review.getRootReview().getReviewId() : null)
                .user(null) // Avoid circular reference - don't map full user object
                .product(null) // Avoid circular reference - don't map full product object
                .parentReview(null) // Avoid circular reference
                .createdDt(review.getCreatedDt())
                .modifiedDt(review.getModifiedDt())
                .build();
    }
    

}