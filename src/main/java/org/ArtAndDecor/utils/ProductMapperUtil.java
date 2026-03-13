package org.ArtAndDecor.utils;

import org.ArtAndDecor.dto.*;
import org.ArtAndDecor.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Utility class for mapping entities to DTOs for Product Management
 * Provides comprehensive mapping methods with different levels of detail
 */
@Component
public class ProductMapperUtil {

    // =============================================
    // PRODUCT TYPE MAPPING METHODS
    // =============================================

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
                .image(toImageDto(productType.getImage()))
                .createdDt(productType.getCreatedDt())
                .modifiedDt(productType.getModifiedDt())
                .build();
    }

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
        
        if (productTypeDto.getCreatedDt() == null) {
            productType.setCreatedDt(LocalDateTime.now());
        } else {
            productType.setCreatedDt(productTypeDto.getCreatedDt());
        }
        productType.setModifiedDt(LocalDateTime.now());
        
        return productType;
    }

    // =============================================
    // PRODUCT CATEGORY MAPPING METHODS
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
                .image(toImageDto(productCategory.getImage()))
                .createdDt(productCategory.getCreatedDt())
                .modifiedDt(productCategory.getModifiedDt())
                .build();
    }

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
        
        if (productCategoryDto.getCreatedDt() == null) {
            productCategory.setCreatedDt(LocalDateTime.now());
        } else {
            productCategory.setCreatedDt(productCategoryDto.getCreatedDt());
        }
        productCategory.setModifiedDt(LocalDateTime.now());
        
        return productCategory;
    }

    // =============================================
    // PRODUCT STATE MAPPING METHODS
    // =============================================

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

    public static ProductState toProductStateEntity(ProductStateDto productStateDto) {
        if (productStateDto == null) return null;
        
        ProductState productState = new ProductState();
        productState.setProductStateId(productStateDto.getProductStateId());
        productState.setProductStateName(productStateDto.getProductStateName());
        productState.setProductStateEnabled(productStateDto.getProductStateEnabled());
        productState.setProductStateDisplayName(productStateDto.getProductStateDisplayName());
        productState.setProductStateRemark(productStateDto.getProductStateRemark());
        
        if (productStateDto.getCreatedDt() == null) {
            productState.setCreatedDt(LocalDateTime.now());
        } else {
            productState.setCreatedDt(productStateDto.getCreatedDt());
        }
        productState.setModifiedDt(LocalDateTime.now());
        
        return productState;
    }

    // =============================================
    // PRODUCT ATTR MAPPING METHODS
    // =============================================

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

    public static ProductAttr toProductAttrEntity(ProductAttrDto productAttrDto) {
        if (productAttrDto == null) return null;
        
        ProductAttr productAttr = new ProductAttr();
        productAttr.setProductAttrId(productAttrDto.getProductAttrId());
        productAttr.setProductAttrName(productAttrDto.getProductAttrName());
        productAttr.setProductAttrEnabled(productAttrDto.getProductAttrEnabled());
        productAttr.setProductAttrDisplayName(productAttrDto.getProductAttrDisplayName());
        productAttr.setProductAttrRemark(productAttrDto.getProductAttrRemark());
        
        if (productAttrDto.getCreatedDt() == null) {
            productAttr.setCreatedDt(LocalDateTime.now());
        } else {
            productAttr.setCreatedDt(productAttrDto.getCreatedDt());
        }
        productAttr.setModifiedDt(LocalDateTime.now());
        
        return productAttr;
    }

    // =============================================
    // PRODUCT MAPPING METHODS
    // =============================================

    public static ProductDto toProductDto(Product product) {
        if (product == null) return null;
        
        return ProductDto.builder()
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
                .createdDt(product.getCreatedDt())
                .modifiedDt(product.getModifiedDt())
                .build();
    }

    public static Product toProductEntity(ProductDto productDto) {
        if (productDto == null) return null;
        
        Product product = new Product();
        product.setProductId(productDto.getProductId());
        product.setProductName(productDto.getProductName());
        product.setProductSlug(productDto.getProductSlug());
        product.setProductCode(productDto.getProductCode());
        product.setSoldQuantity(productDto.getSoldQuantity());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setProductDescription(productDto.getProductDescription());
        product.setProductPrice(productDto.getProductPrice());
        product.setProductEnabled(productDto.getProductEnabled());
        
        if (productDto.getCreatedDt() == null) {
            product.setCreatedDt(LocalDateTime.now());
        } else {
            product.setCreatedDt(productDto.getCreatedDt());
        }
        product.setModifiedDt(LocalDateTime.now());
        
        return product;
    }

    // =============================================
    // PRODUCT IMAGE MAPPING METHODS
    // =============================================

    public static ProductImageDto toProductImageDto(ProductImage productImage) {
        if (productImage == null) return null;
        
        return ProductImageDto.builder()
                .productImageId(productImage.getProductImageId())
                .productImagePrimary(productImage.getProductImagePrimary())
                .product(toProductDto(productImage.getProduct()))
                .image(toImageDto(productImage.getImage()))
                .createdDt(productImage.getCreatedDt())
                .modifiedDt(productImage.getModifiedDt())
                .build();
    }

    // =============================================
    // PRODUCT ATTRIBUTE MAPPING METHODS
    // =============================================

    public static ProductAttributeDto toProductAttributeDto(ProductAttribute productAttribute) {
        if (productAttribute == null) return null;
        
        return ProductAttributeDto.builder()
                .productAttributeId(productAttribute.getProductAttributeId())
                .productAttributeValue(productAttribute.getProductAttributeValue())
                .productAttr(toProductAttrDto(productAttribute.getProductAttr()))
                .createdDt(productAttribute.getCreatedDt())
                .modifiedDt(productAttribute.getModifiedDt())
                .build();
    }

    // =============================================
    // HELPER METHODS
    // =============================================

    private static ImageDto toImageDto(Image image) {
        if (image == null) return null;
        // Use existing ImageMapperUtil if available, or create basic mapping
        return ImageDto.builder()
                .imageId(image.getImageId())
                .imageName(image.getImageName())
                .imageDisplayName(image.getImageDisplayName())
                .imageSlug(image.getImageSlug())
                .imageSize(image.getImageSize())
                .imageFormat(image.getImageFormat())
                .imageRemark(image.getImageRemark())
                .createdDt(image.getCreatedDt())
                .modifiedDt(image.getModifiedDt())
                .build();
    }
}