package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductCategoryDto;
import org.artanddecor.dto.ProductCategoryRequestDto;
import org.artanddecor.dto.SeoMetaDto;
import org.artanddecor.model.Image;
import org.artanddecor.model.ProductCategory;
import org.artanddecor.model.ProductType;
import org.artanddecor.repository.ImageRepository;
import org.artanddecor.repository.ProductCategoryRepository;
import org.artanddecor.repository.ProductTypeRepository;
import org.artanddecor.services.ProductCategoryService;
import org.artanddecor.services.SeoMetaService;
import org.artanddecor.utils.ProductMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ProductCategory Service Implementation
 * Handles business logic for product category management
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductCategoryServiceImpl implements ProductCategoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryServiceImpl.class);
    
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ImageRepository imageRepository;
    private final SeoMetaService seoMetaService;

    // =============================================
    // ADMIN-FOCUSED OPERATIONS
    // =============================================

    @Override
    public Optional<ProductCategoryDto> findProductCategoryById(Long productCategoryId) {
        logger.debug("Finding product category by ID: {}", productCategoryId);
        return productCategoryRepository.findById(productCategoryId)
                .map(this::convertToDto);
    }

    @Override
    public List<ProductCategoryDto> getRootCategories() {
        logger.debug("Getting all root categories");
        List<ProductCategory> rootCategories = productCategoryRepository.findRootCategories();
        return rootCategories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductCategoryDto> getProductCategoriesByCriteria(String textSearch, Boolean enabled, Boolean visible, 
                                                                 Long productTypeId, Long parentCategoryId, Boolean rootOnly, 
                                                                 Pageable pageable) {
        logger.debug("Getting product categories with enhanced criteria - textSearch: {}, enabled: {}, visible: {}, productTypeId: {}, parentCategoryId: {}, rootOnly: {}", 
                    textSearch, enabled, visible, productTypeId, parentCategoryId, rootOnly);
        
        // Handle rootOnly logic by converting to parentCategoryId = -1
        Long effectiveParentCategoryId = parentCategoryId;
        if (Boolean.TRUE.equals(rootOnly)) {
            effectiveParentCategoryId = -1L; // Special value to indicate root categories
        }
        
        Page<ProductCategory> productCategoryPage = productCategoryRepository.findProductCategoriesByCriteriaPaginated(
            textSearch, enabled, visible, productTypeId, effectiveParentCategoryId, pageable);
        
        return productCategoryPage.map(this::convertToDto);
    }

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    @Override
    @Transactional
    public ProductCategoryDto createProductCategory(ProductCategoryRequestDto requestDto) {
        logger.info("Creating new product category from request: {}", requestDto.getProductCategoryName());
        
        // Validation - check slug uniqueness using repository query
        if (productCategoryRepository.existsByProductCategorySlug(requestDto.getProductCategorySlug())) {
            throw new IllegalArgumentException("Product category slug already exists: " + requestDto.getProductCategorySlug());
        }
        if (productCategoryRepository.existsByProductCategoryName(requestDto.getProductCategoryName())) {
            throw new IllegalArgumentException("Product category name already exists: " + requestDto.getProductCategoryName());
        }
        
        // Validate product type exists
        ProductType productType = productTypeRepository.findById(requestDto.getProductTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Product type not found with ID: " + requestDto.getProductTypeId()));
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setProductCategoryName(requestDto.getProductCategoryName());
        productCategory.setProductCategorySlug(requestDto.getProductCategorySlug());
        productCategory.setProductCategoryDisplayName(requestDto.getProductCategoryDisplayName());
        productCategory.setProductCategoryRemark(requestDto.getProductCategoryRemark());
        productCategory.setProductCategoryEnabled(requestDto.getProductCategoryEnabled() != null ? requestDto.getProductCategoryEnabled() : true);
        productCategory.setProductCategoryVisible(requestDto.getProductCategoryVisible() != null ? requestDto.getProductCategoryVisible() : true);
        productCategory.setProductType(productType);
        
        // Handle parent category
        if (requestDto.getProductCategoryParentId() != null) {
            ProductCategory parentCategory = productCategoryRepository.findById(requestDto.getProductCategoryParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found with ID: " + requestDto.getProductCategoryParentId()));
            productCategory.setParentCategory(parentCategory);
        }
        
        // Handle image
        if (requestDto.getImageId() != null) {
            Image image = imageRepository.findById(requestDto.getImageId())
                    .orElseThrow(() -> new IllegalArgumentException("Image not found with ID: " + requestDto.getImageId()));
            productCategory.setImage(image);
        }
        
        // Handle SEO meta
        if (requestDto.getSeoMeta() != null) {
            SeoMetaDto createdSeoMeta = seoMetaService.createSeoMetaFromRequest(requestDto.getSeoMeta());
            productCategory.setSeoMetaId(createdSeoMeta.getSeoMetaId());
        }
        
        ProductCategory savedProductCategory = productCategoryRepository.save(productCategory);
        logger.info("Product category created successfully with ID: {}", savedProductCategory.getProductCategoryId());
        
        return convertToDto(savedProductCategory);
    }

    @Override
    @Transactional
    public ProductCategoryDto updateProductCategory(Long productCategoryId, ProductCategoryRequestDto requestDto) {
        logger.info("Updating product category ID: {} from request", productCategoryId);
        
        ProductCategory existingProductCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("Product category not found with ID: " + productCategoryId));
        
        // Validation - check if slug/name exists for other records
        if (!existingProductCategory.getProductCategorySlug().equals(requestDto.getProductCategorySlug()) && 
            productCategoryRepository.existsByProductCategorySlug(requestDto.getProductCategorySlug())) {
            throw new IllegalArgumentException("Product category slug already exists: " + requestDto.getProductCategorySlug());
        }
        if (!existingProductCategory.getProductCategoryName().equals(requestDto.getProductCategoryName()) && 
            productCategoryRepository.existsByProductCategoryName(requestDto.getProductCategoryName())) {
            throw new IllegalArgumentException("Product category name already exists: " + requestDto.getProductCategoryName());
        }
        
        // Validate product type exists
        ProductType productType = productTypeRepository.findById(requestDto.getProductTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Product type not found with ID: " + requestDto.getProductTypeId()));
        
        // Update basic fields
        existingProductCategory.setProductCategoryName(requestDto.getProductCategoryName());
        existingProductCategory.setProductCategorySlug(requestDto.getProductCategorySlug());
        existingProductCategory.setProductCategoryDisplayName(requestDto.getProductCategoryDisplayName());
        existingProductCategory.setProductCategoryRemark(requestDto.getProductCategoryRemark());
        if (requestDto.getProductCategoryEnabled() != null) {
            existingProductCategory.setProductCategoryEnabled(requestDto.getProductCategoryEnabled());
        }
        if (requestDto.getProductCategoryVisible() != null) {
            existingProductCategory.setProductCategoryVisible(requestDto.getProductCategoryVisible());
        }
        existingProductCategory.setProductType(productType);
        
        // Handle parent category
        if (requestDto.getProductCategoryParentId() != null) {
            ProductCategory parentCategory = productCategoryRepository.findById(requestDto.getProductCategoryParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found with ID: " + requestDto.getProductCategoryParentId()));
            existingProductCategory.setParentCategory(parentCategory);
        } else {
            existingProductCategory.setParentCategory(null);
        }
        
        // Handle image
        if (requestDto.getImageId() != null) {
            Image image = imageRepository.findById(requestDto.getImageId())
                    .orElseThrow(() -> new IllegalArgumentException("Image not found with ID: " + requestDto.getImageId()));
            existingProductCategory.setImage(image);
        }
        
        // Handle SEO meta
        if (requestDto.getSeoMeta() != null) {
            if (existingProductCategory.getSeoMetaId() != null) {
                // Update existing SEO meta
                seoMetaService.updateSeoMetaFromRequest(existingProductCategory.getSeoMetaId(), requestDto.getSeoMeta());
            } else {
                // Create new SEO meta
                SeoMetaDto createdSeoMeta = seoMetaService.createSeoMetaFromRequest(requestDto.getSeoMeta());
                existingProductCategory.setSeoMetaId(createdSeoMeta.getSeoMetaId());
            }
        }
        
        existingProductCategory.setModifiedDt(LocalDateTime.now());
        
        ProductCategory savedProductCategory = productCategoryRepository.save(existingProductCategory);
        logger.info("Product category updated successfully with ID: {}", savedProductCategory.getProductCategoryId());
        
        return convertToDto(savedProductCategory);
    }

    // =============================================
    // HELPER METHODS
    // =============================================

    private ProductCategoryDto convertToDto(ProductCategory productCategory) {
        return ProductMapperUtil.toProductCategoryDto(productCategory);
    }
}