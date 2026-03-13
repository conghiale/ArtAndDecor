package org.ArtAndDecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.ProductCategoryDto;
import org.ArtAndDecor.model.ProductCategory;
import org.ArtAndDecor.repository.ProductCategoryRepository;
import org.ArtAndDecor.services.ProductCategoryService;
import org.ArtAndDecor.utils.ProductMapperUtil;
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

    // =============================================
    // CUSTOMER-FOCUSED OPERATIONS
    // =============================================

    @Override
    public Optional<ProductCategoryDto> findProductCategoryBySlug(String productCategorySlug) {
        logger.debug("Finding product category by slug: {}", productCategorySlug);
        return productCategoryRepository.findByProductCategorySlug(productCategorySlug)
                .map(this::convertToDto);
    }

    @Override
    public Page<ProductCategoryDto> getProductCategoriesByCriteria(String textSearch, Boolean enabled, Boolean visible, Long productTypeId, Pageable pageable) {
        logger.debug("Getting product categories with criteria - textSearch: {}, enabled: {}, visible: {}, productTypeId: {}", 
                    textSearch, enabled, visible, productTypeId);
        
        Page<ProductCategory> productCategoryPage = productCategoryRepository.findProductCategoriesByCriteriaPaginated(
            textSearch, enabled, visible, productTypeId, pageable);
        
        return productCategoryPage.map(this::convertToDto);
    }

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
    public Optional<ProductCategoryDto> findProductCategoryByName(String productCategoryName) {
        logger.debug("Finding product category by name: {}", productCategoryName);
        return productCategoryRepository.findByProductCategoryName(productCategoryName)
                .map(this::convertToDto);
    }

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    @Override
    @Transactional
    public ProductCategoryDto createProductCategory(ProductCategoryDto productCategoryDto) {
        logger.info("Creating new product category: {}", productCategoryDto.getProductCategoryName());
        
        // Validation
        if (existsBySlug(productCategoryDto.getProductCategorySlug())) {
            throw new IllegalArgumentException("Product category slug already exists: " + productCategoryDto.getProductCategorySlug());
        }
        if (existsByName(productCategoryDto.getProductCategoryName())) {
            throw new IllegalArgumentException("Product category name already exists: " + productCategoryDto.getProductCategoryName());
        }
        
        ProductCategory productCategory = convertToEntity(productCategoryDto);
        ProductCategory savedProductCategory = productCategoryRepository.save(productCategory);
        
        return convertToDto(savedProductCategory);
    }

    @Override
    @Transactional
    public ProductCategoryDto updateProductCategory(Long productCategoryId, ProductCategoryDto productCategoryDto) {
        logger.info("Updating product category ID: {}", productCategoryId);
        
        ProductCategory existingProductCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("Product category not found with ID: " + productCategoryId));
        
        // Validation - check if slug/name exists for other records
        if (!existingProductCategory.getProductCategorySlug().equals(productCategoryDto.getProductCategorySlug()) && 
            existsBySlug(productCategoryDto.getProductCategorySlug())) {
            throw new IllegalArgumentException("Product category slug already exists: " + productCategoryDto.getProductCategorySlug());
        }
        if (!existingProductCategory.getProductCategoryName().equals(productCategoryDto.getProductCategoryName()) && 
            existsByName(productCategoryDto.getProductCategoryName())) {
            throw new IllegalArgumentException("Product category name already exists: " + productCategoryDto.getProductCategoryName());
        }
        
        // Update fields
        existingProductCategory.setProductCategoryName(productCategoryDto.getProductCategoryName());
        existingProductCategory.setProductCategorySlug(productCategoryDto.getProductCategorySlug());
        existingProductCategory.setProductCategoryDisplayName(productCategoryDto.getProductCategoryDisplayName());
        existingProductCategory.setProductCategoryRemark(productCategoryDto.getProductCategoryRemark());
        existingProductCategory.setProductCategoryEnabled(productCategoryDto.getProductCategoryEnabled());
        existingProductCategory.setProductCategoryVisible(productCategoryDto.getProductCategoryVisible());
        existingProductCategory.setSeoMetaId(productCategoryDto.getSeoMetaId());
        existingProductCategory.setModifiedDt(LocalDateTime.now());
        
        ProductCategory savedProductCategory = productCategoryRepository.save(existingProductCategory);
        return convertToDto(savedProductCategory);
    }

    @Override
    @Transactional
    public void deleteProductCategoryById(Long productCategoryId) {
        logger.info("Deleting product category ID: {}", productCategoryId);
        
        if (!productCategoryRepository.existsById(productCategoryId)) {
            throw new IllegalArgumentException("Product category not found with ID: " + productCategoryId);
        }
        
        productCategoryRepository.deleteById(productCategoryId);
    }

    // =============================================
    // UTILITY OPERATIONS
    // =============================================

    @Override
    public long getTotalProductCategoryCount() {
        return productCategoryRepository.count();
    }

    @Override
    public List<ProductCategoryDto> getCategoriesByProductTypeId(Long productTypeId) {
        logger.debug("Getting categories by product type ID: {}", productTypeId);
        return productCategoryRepository.findByProductTypeId(productTypeId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategoryDto> getRootCategories() {
        logger.debug("Getting root categories");
        return productCategoryRepository.findRootCategories()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategoryDto> getChildCategoriesByParentId(Long parentId) {
        logger.debug("Getting child categories by parent ID: {}", parentId);
        return productCategoryRepository.findByParentCategoryId(parentId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsBySlug(String productCategorySlug) {
        return productCategoryRepository.existsByProductCategorySlug(productCategorySlug);
    }

    @Override
    public boolean existsByName(String productCategoryName) {
        return productCategoryRepository.existsByProductCategoryName(productCategoryName);
    }

    // =============================================
    // HELPER METHODS
    // =============================================

    private ProductCategoryDto convertToDto(ProductCategory productCategory) {
        return ProductMapperUtil.toProductCategoryDto(productCategory);
    }

    private ProductCategory convertToEntity(ProductCategoryDto productCategoryDto) {
        return ProductMapperUtil.toProductCategoryEntity(productCategoryDto);
    }
}