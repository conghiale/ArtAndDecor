package org.ArtAndDecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.ProductStateDto;
import org.ArtAndDecor.model.ProductState;
import org.ArtAndDecor.repository.ProductStateRepository;
import org.ArtAndDecor.services.ProductStateService;
import org.ArtAndDecor.utils.ProductMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * ProductState Service Implementation
 * Handles business logic for product state management
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductStateServiceImpl implements ProductStateService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductStateServiceImpl.class);
    
    private final ProductStateRepository productStateRepository;

    // =============================================
    // CUSTOMER-FOCUSED OPERATIONS
    // =============================================

    @Override
    public Optional<ProductStateDto> findProductStateByName(String productStateName) {
        logger.debug("Finding product state by name: {}", productStateName);
        return productStateRepository.findByProductStateName(productStateName)
                .map(this::convertToDto);
    }

    @Override
    public Page<ProductStateDto> getProductStatesByCriteria(String textSearch, Boolean enabled, Pageable pageable) {
        logger.debug("Getting product states with criteria - textSearch: {}, enabled: {}", textSearch, enabled);
        
        Page<ProductState> productStatePage = productStateRepository.findProductStatesByCriteriaPaginated(
            textSearch, enabled, pageable);
        
        return productStatePage.map(this::convertToDto);
    }

    // =============================================
    // ADMIN-FOCUSED OPERATIONS
    // =============================================

    @Override
    public Optional<ProductStateDto> findProductStateById(Long productStateId) {
        logger.debug("Finding product state by ID: {}", productStateId);
        return productStateRepository.findById(productStateId)
                .map(this::convertToDto);
    }

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    @Override
    @Transactional
    public ProductStateDto createProductState(ProductStateDto productStateDto) {
        logger.info("Creating new product state: {}", productStateDto.getProductStateName());
        
        // Validation
        if (existsByName(productStateDto.getProductStateName())) {
            throw new IllegalArgumentException("Product state name already exists: " + productStateDto.getProductStateName());
        }
        
        ProductState productState = convertToEntity(productStateDto);
        ProductState savedProductState = productStateRepository.save(productState);
        
        return convertToDto(savedProductState);
    }

    @Override
    @Transactional
    public ProductStateDto updateProductState(Long productStateId, ProductStateDto productStateDto) {
        logger.info("Updating product state ID: {}", productStateId);
        
        ProductState existingProductState = productStateRepository.findById(productStateId)
                .orElseThrow(() -> new IllegalArgumentException("Product state not found with ID: " + productStateId));
        
        // Validation - check if name exists for other records
        if (!existingProductState.getProductStateName().equals(productStateDto.getProductStateName()) && 
            existsByName(productStateDto.getProductStateName())) {
            throw new IllegalArgumentException("Product state name already exists: " + productStateDto.getProductStateName());
        }
        
        // Update fields
        existingProductState.setProductStateName(productStateDto.getProductStateName());
        existingProductState.setProductStateDisplayName(productStateDto.getProductStateDisplayName());
        existingProductState.setProductStateRemark(productStateDto.getProductStateRemark());
        existingProductState.setProductStateEnabled(productStateDto.getProductStateEnabled());
        existingProductState.setModifiedDt(LocalDateTime.now());
        
        ProductState savedProductState = productStateRepository.save(existingProductState);
        return convertToDto(savedProductState);
    }

    @Override
    @Transactional
    public void deleteProductStateById(Long productStateId) {
        logger.info("Deleting product state ID: {}", productStateId);
        
        if (!productStateRepository.existsById(productStateId)) {
            throw new IllegalArgumentException("Product state not found with ID: " + productStateId);
        }
        
        productStateRepository.deleteById(productStateId);
    }

    // =============================================
    // UTILITY OPERATIONS
    // =============================================

    @Override
    public long getTotalProductStateCount() {
        return productStateRepository.count();
    }

    @Override
    public boolean existsByName(String productStateName) {
        return productStateRepository.existsByProductStateName(productStateName);
    }

    // =============================================
    // HELPER METHODS
    // =============================================

    private ProductStateDto convertToDto(ProductState productState) {
        return ProductMapperUtil.toProductStateDto(productState);
    }

    private ProductState convertToEntity(ProductStateDto productStateDto) {
        return ProductMapperUtil.toProductStateEntity(productStateDto);
    }
}