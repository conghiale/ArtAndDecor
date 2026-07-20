package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductTypeDto;
import org.artanddecor.dto.ProductTypeRequestDto;
import org.artanddecor.dto.SeoMetaDto;
import org.artanddecor.model.Image;
import org.artanddecor.model.ProductType;
import org.artanddecor.repository.ImageRepository;
import org.artanddecor.repository.ProductTypeRepository;
import org.artanddecor.repository.ProductCategoryRepository;
import org.artanddecor.services.ProductTypeService;
import org.artanddecor.services.SeoMetaService;
import org.artanddecor.utils.ProductMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * ProductType Service Implementation
 * Handles business logic for product type management
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductTypeServiceImpl implements ProductTypeService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductTypeServiceImpl.class);
    
    private final ProductTypeRepository productTypeRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ImageRepository imageRepository;
    private final SeoMetaService seoMetaService;

    // =============================================
    // ADMIN-FOCUSED OPERATIONS
    // =============================================

    @Override
    public Optional<ProductTypeDto> findProductTypeById(Long productTypeId) {
        logger.debug("Finding product type by ID: {}", productTypeId);
        return productTypeRepository.findById(productTypeId)
                .map(this::convertToDto);
    }

    @Override
    public Page<ProductTypeDto> getProductTypesByCriteria(String textSearch, Boolean enabled, String productTypeSlug, Pageable pageable) {
        logger.debug("Getting product types with criteria - textSearch: {}, enabled: {}, productTypeSlug: {}", textSearch, enabled, productTypeSlug);
        
        Page<ProductType> productTypePage = productTypeRepository.findProductTypesByCriteriaPaginated(
            textSearch, enabled, productTypeSlug, pageable);
        
        return productTypePage.map(this::convertToDto);
    }

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    @Override
    @Transactional
    public ProductTypeDto createProductType(ProductTypeRequestDto requestDto) {
        logger.info("Creating new product type from request: {}", requestDto.getProductTypeName());
        
        // Validation
        if (existsBySlug(requestDto.getProductTypeSlug())) {
            throw new IllegalArgumentException("Product type with slug '" + requestDto.getProductTypeSlug() + "' already exists");
        }
        if (existsByName(requestDto.getProductTypeName())) {
            throw new IllegalArgumentException("Product type with name '" + requestDto.getProductTypeName() + "' already exists");
        }
        
        ProductType productType = new ProductType();
        productType.setProductTypeName(requestDto.getProductTypeName());
        productType.setProductTypeSlug(requestDto.getProductTypeSlug());
        productType.setProductTypeDisplayName(requestDto.getProductTypeDisplayName());
        productType.setProductTypeContent(requestDto.getProductTypeContent());
        productType.setProductTypeRemark(requestDto.getProductTypeRemark());
        productType.setProductTypeEnabled(requestDto.getProductTypeEnabled() != null ? requestDto.getProductTypeEnabled() : true);
        productType.setProductTypeDisplayOrder(requestDto.getProductTypeDisplayOrder());
        
        // Handle image
        if (requestDto.getImageId() != null) {
            Image image = imageRepository.findById(requestDto.getImageId())
                    .orElseThrow(() -> new IllegalArgumentException("Image not found with ID: " + requestDto.getImageId()));
            productType.setImage(image);
        }
        
        // Handle SEO meta
        if (requestDto.getSeoMeta() != null) {
            SeoMetaDto createdSeoMeta = seoMetaService.createSeoMetaFromRequest(requestDto.getSeoMeta());
            productType.setSeoMetaId(createdSeoMeta.getSeoMetaId());
        }
        
        ProductType savedProductType = productTypeRepository.save(productType);
        logger.info("Product type created successfully with ID: {}", savedProductType.getProductTypeId());
        
        return convertToDto(savedProductType);
    }

    @Override
    @Transactional
    public ProductTypeDto updateProductType(Long productTypeId, ProductTypeRequestDto requestDto) {
        logger.info("Updating product type ID: {} from request", productTypeId);
        
        ProductType existingProductType = productTypeRepository.findById(productTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Product type not found with ID: " + productTypeId));
        
        // Validation - check if slug/name exists for other records
        if (!existingProductType.getProductTypeSlug().equals(requestDto.getProductTypeSlug()) && 
            existsBySlug(requestDto.getProductTypeSlug())) {
            throw new IllegalArgumentException("Product type with slug '" + requestDto.getProductTypeSlug() + "' already exists");
        }
        if (!existingProductType.getProductTypeName().equals(requestDto.getProductTypeName()) && 
            existsByName(requestDto.getProductTypeName())) {
            throw new IllegalArgumentException("Product type with name '" + requestDto.getProductTypeName() + "' already exists");
        }
        
        // Update basic fields
        existingProductType.setProductTypeName(requestDto.getProductTypeName());
        existingProductType.setProductTypeSlug(requestDto.getProductTypeSlug());
        existingProductType.setProductTypeDisplayName(requestDto.getProductTypeDisplayName());
        existingProductType.setProductTypeContent(requestDto.getProductTypeContent());
        existingProductType.setProductTypeRemark(requestDto.getProductTypeRemark());
        existingProductType.setProductTypeDisplayOrder(requestDto.getProductTypeDisplayOrder());
        if (requestDto.getProductTypeEnabled() != null) {
            existingProductType.setProductTypeEnabled(requestDto.getProductTypeEnabled());
        }
        
        // Handle image
        if (requestDto.getImageId() != null) {
            Image image = imageRepository.findById(requestDto.getImageId())
                    .orElseThrow(() -> new IllegalArgumentException("Image not found with ID: " + requestDto.getImageId()));
            existingProductType.setImage(image);
        }
        
        // Handle SEO meta
        if (requestDto.getSeoMeta() != null) {
            if (existingProductType.getSeoMetaId() != null) {
                // Update existing SEO meta
                seoMetaService.updateSeoMetaFromRequest(existingProductType.getSeoMetaId(), requestDto.getSeoMeta());
            } else {
                // Create new SEO meta
                SeoMetaDto createdSeoMeta = seoMetaService.createSeoMetaFromRequest(requestDto.getSeoMeta());
                existingProductType.setSeoMetaId(createdSeoMeta.getSeoMetaId());
            }
        }
        
        existingProductType.setModifiedDt(LocalDateTime.now());
        
        ProductType savedProductType = productTypeRepository.save(existingProductType);
        logger.info("Product type updated successfully with ID: {}", savedProductType.getProductTypeId());
        
        return convertToDto(savedProductType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductType(Long productTypeId) {
        logger.info("Start delete productType, id={}", productTypeId);

        if (productTypeId == null || productTypeId <= 0) {
            logger.warn("Invalid productType id for delete, id={}", productTypeId);
            throw new IllegalArgumentException("ID loại sản phẩm không hợp lệ.");
        }

        ProductType productType = productTypeRepository.findById(productTypeId)
                .orElseThrow(() -> {
                    logger.warn("ProductType not found for delete, id={}", productTypeId);
                    return new IllegalArgumentException("Không tìm thấy loại sản phẩm cần xoá.");
                });

        Long seoMetaId = productType.getSeoMetaId();

        logger.debug("Checking references before deleting productType, id={}", productTypeId);
        Long referencedCount = productCategoryRepository.countByProductType_ProductTypeId(productTypeId);
        Long sampleReferencedId = productCategoryRepository.findSampleCategoryIdByProductTypeId(productTypeId);
        if (referencedCount != null && referencedCount > 0) {
            logger.warn(
                    "Cannot delete PRODUCT_TYPE, id={}, referencedTable={}, referencedColumn={}, referencedCount={}, sampleReferencedId={}",
                    productTypeId, "PRODUCT_CATEGORY", "PRODUCT_TYPE_ID", referencedCount, sampleReferencedId);
            String referenceDetail = String.format(
                "Cannot delete PRODUCT_TYPE, id=%s, referencedTable=%s, referencedColumn=%s, referencedCount=%s, sampleReferencedId=%s",
                productTypeId, "PRODUCT_CATEGORY", "PRODUCT_TYPE_ID", referencedCount, sampleReferencedId);
            throw new IllegalStateException(referenceDetail);
        }

        productTypeRepository.delete(productType);

        if (seoMetaId != null) {
            seoMetaService.deleteSeoMeta(seoMetaId);
            logger.info("Deleted associated SEO meta for productType, productTypeId={}, seoMetaId={}", productTypeId, seoMetaId);
        }

        logger.info("Deleted productType successfully, id={}", productTypeId);
    }

    // =============================================
    // HELPER METHODS
    // =============================================

    private ProductTypeDto convertToDto(ProductType productType) {
        return ProductMapperUtil.toProductTypeDto(productType);
    }

    private boolean existsBySlug(String productTypeSlug) {
        return productTypeRepository.existsByProductTypeSlug(productTypeSlug);
    }

    private boolean existsByName(String productTypeName) {
        return productTypeRepository.existsByProductTypeName(productTypeName);
    }
}