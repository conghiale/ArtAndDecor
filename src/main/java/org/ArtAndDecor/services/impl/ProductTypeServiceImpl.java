package org.ArtAndDecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.ProductTypeDto;
import org.ArtAndDecor.model.ProductType;
import org.ArtAndDecor.repository.ProductTypeRepository;
import org.ArtAndDecor.services.ProductTypeService;
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
 * ProductType Service Implementation
 * Handles business logic for product type management
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductTypeServiceImpl implements ProductTypeService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductTypeServiceImpl.class);
    
    private final ProductTypeRepository productTypeRepository;

    // =============================================
    // CUSTOMER-FOCUSED OPERATIONS
    // =============================================

    @Override
    public Optional<ProductTypeDto> findProductTypeBySlug(String productTypeSlug) {
        logger.debug("Finding product type by slug: {}", productTypeSlug);
        return productTypeRepository.findByProductTypeSlug(productTypeSlug)
                .map(this::convertToDto);
    }

    @Override
    public Page<ProductTypeDto> getProductTypesByCriteria(String textSearch, Boolean enabled, Pageable pageable) {
        logger.debug("Getting product types with criteria - textSearch: {}, enabled: {}", textSearch, enabled);
        
        Page<ProductType> productTypePage = productTypeRepository.findProductTypesByCriteriaPaginated(
            textSearch, enabled, pageable);
        
        return productTypePage.map(this::convertToDto);
    }

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
    public Optional<ProductTypeDto> findProductTypeByName(String productTypeName) {
        logger.debug("Finding product type by name: {}", productTypeName);
        return productTypeRepository.findByProductTypeName(productTypeName)
                .map(this::convertToDto);
    }

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    @Override
    @Transactional
    public ProductTypeDto createProductType(ProductTypeDto productTypeDto) {
        logger.info("Creating new product type: {}", productTypeDto.getProductTypeName());
        
        // Validation
        if (existsBySlug(productTypeDto.getProductTypeSlug())) {
            throw new IllegalArgumentException("Product type with slug '" + productTypeDto.getProductTypeSlug() + "' already exists");
        }
        if (existsByName(productTypeDto.getProductTypeName())) {
            throw new IllegalArgumentException("Product type with name '" + productTypeDto.getProductTypeName() + "' already exists");
        }
        
        ProductType productType = convertToEntity(productTypeDto);
        ProductType savedProductType = productTypeRepository.save(productType);
        
        return convertToDto(savedProductType);
    }

    @Override
    @Transactional
    public ProductTypeDto updateProductType(Long productTypeId, ProductTypeDto productTypeDto) {
        logger.info("Updating product type ID: {}", productTypeId);
        
        ProductType existingProductType = productTypeRepository.findById(productTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Product type not found with ID: " + productTypeId));
        
        // Validation - check if slug/name exists for other records
        if (!existingProductType.getProductTypeSlug().equals(productTypeDto.getProductTypeSlug()) && 
            existsBySlug(productTypeDto.getProductTypeSlug())) {
            throw new IllegalArgumentException("Product type with slug '" + productTypeDto.getProductTypeSlug() + "' already exists");
        }
        if (!existingProductType.getProductTypeName().equals(productTypeDto.getProductTypeName()) && 
            existsByName(productTypeDto.getProductTypeName())) {
            throw new IllegalArgumentException("Product type with name '" + productTypeDto.getProductTypeName() + "' already exists");
        }
        
        // Update fields
        existingProductType.setProductTypeName(productTypeDto.getProductTypeName());
        existingProductType.setProductTypeSlug(productTypeDto.getProductTypeSlug());
        existingProductType.setProductTypeDisplayName(productTypeDto.getProductTypeDisplayName());
        existingProductType.setProductTypeRemark(productTypeDto.getProductTypeRemark());
        existingProductType.setProductTypeEnabled(productTypeDto.getProductTypeEnabled());
        existingProductType.setSeoMetaId(productTypeDto.getSeoMetaId());
        existingProductType.setModifiedDt(LocalDateTime.now());
        
        ProductType savedProductType = productTypeRepository.save(existingProductType);
        return convertToDto(savedProductType);
    }

    @Override
    @Transactional
    public void deleteProductTypeById(Long productTypeId) {
        logger.info("Deleting product type ID: {}", productTypeId);
        
        if (!productTypeRepository.existsById(productTypeId)) {
            throw new IllegalArgumentException("Product type not found with ID: " + productTypeId);
        }
        
        productTypeRepository.deleteById(productTypeId);
    }

    // =============================================
    // UTILITY OPERATIONS
    // =============================================

    @Override
    public long getTotalProductTypeCount() {
        return productTypeRepository.count();
    }

    @Override
    public boolean existsBySlug(String productTypeSlug) {
        return productTypeRepository.existsByProductTypeSlug(productTypeSlug);
    }

    @Override
    public boolean existsByName(String productTypeName) {
        return productTypeRepository.existsByProductTypeName(productTypeName);
    }

    // =============================================
    // HELPER METHODS
    // =============================================

    private ProductTypeDto convertToDto(ProductType productType) {
        return ProductMapperUtil.toProductTypeDto(productType);
    }

    private ProductType convertToEntity(ProductTypeDto productTypeDto) {
        return ProductMapperUtil.toProductTypeEntity(productTypeDto);
    }
}