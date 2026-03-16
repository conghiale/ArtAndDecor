package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductAttrDto;
import org.artanddecor.dto.ProductAttributeDto;
import org.artanddecor.model.ProductAttr;
import org.artanddecor.model.ProductAttribute;
import org.artanddecor.repository.ProductAttrRepository;
import org.artanddecor.repository.ProductAttributeRepository;
import org.artanddecor.services.ProductAttrService;
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

/**
 * ProductAttr Service Implementation
 * Handles business logic for product attribute management
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductAttrServiceImpl implements ProductAttrService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductAttrServiceImpl.class);
    
    private final ProductAttrRepository productAttrRepository;
    private final ProductAttributeRepository productAttributeRepository;

    // =============================================
    // CUSTOMER-FOCUSED OPERATIONS
    // =============================================

    @Override
    public Optional<ProductAttrDto> findProductAttrByName(String productAttrName) {
        logger.debug("Finding product attribute by name: {}", productAttrName);
        return productAttrRepository.findByProductAttrName(productAttrName)
                .map(this::convertToDto);
    }

    @Override
    public Page<ProductAttrDto> getProductAttrsByCriteria(String textSearch, Boolean enabled, Pageable pageable) {
        logger.debug("Getting product attributes with criteria - textSearch: {}, enabled: {}", textSearch, enabled);
        
        Page<ProductAttr> productAttrPage = productAttrRepository.findProductAttrsByCriteriaPaginated(
            textSearch, enabled, pageable);
        
        return productAttrPage.map(this::convertToDto);
    }

    // =============================================
    // ADMIN-FOCUSED OPERATIONS
    // =============================================

    @Override
    public Optional<ProductAttrDto> findProductAttrById(Long productAttrId) {
        logger.debug("Finding product attribute by ID: {}", productAttrId);
        return productAttrRepository.findById(productAttrId)
                .map(this::convertToDto);
    }

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    @Override
    @Transactional
    public ProductAttrDto createProductAttr(ProductAttrDto productAttrDto) {
        logger.info("Creating new product attribute: {}", productAttrDto.getProductAttrName());
        
        // Validation
        if (existsByName(productAttrDto.getProductAttrName())) {
            throw new IllegalArgumentException("Product attribute name already exists: " + productAttrDto.getProductAttrName());
        }
        
        ProductAttr productAttr = convertToEntity(productAttrDto);
        ProductAttr savedProductAttr = productAttrRepository.save(productAttr);
        
        return convertToDto(savedProductAttr);
    }

    @Override
    @Transactional
    public ProductAttrDto updateProductAttr(Long productAttrId, ProductAttrDto productAttrDto) {
        logger.info("Updating product attribute ID: {}", productAttrId);
        
        ProductAttr existingProductAttr = productAttrRepository.findById(productAttrId)
                .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + productAttrId));
        
        // Validation - check if name exists for other records
        if (!existingProductAttr.getProductAttrName().equals(productAttrDto.getProductAttrName()) && 
            existsByName(productAttrDto.getProductAttrName())) {
            throw new IllegalArgumentException("Product attribute name already exists: " + productAttrDto.getProductAttrName());
        }
        
        // Update fields
        existingProductAttr.setProductAttrName(productAttrDto.getProductAttrName());
        existingProductAttr.setProductAttrDisplayName(productAttrDto.getProductAttrDisplayName());
        existingProductAttr.setProductAttrRemark(productAttrDto.getProductAttrRemark());
        existingProductAttr.setProductAttrEnabled(productAttrDto.getProductAttrEnabled());
        existingProductAttr.setModifiedDt(LocalDateTime.now());
        
        ProductAttr savedProductAttr = productAttrRepository.save(existingProductAttr);
        return convertToDto(savedProductAttr);
    }

    @Override
    @Transactional
    public void deleteProductAttrById(Long productAttrId) {
        logger.info("Deleting product attribute ID: {}", productAttrId);
        
        if (!productAttrRepository.existsById(productAttrId)) {
            throw new IllegalArgumentException("Product attribute not found with ID: " + productAttrId);
        }
        
        productAttrRepository.deleteById(productAttrId);
    }

    // =============================================
    // PRODUCT ATTRIBUTE ASSOCIATION OPERATIONS
    // =============================================

    @Override
    @Transactional
    public ProductAttributeDto updateProductAttribute(Long productAttributeId, ProductAttributeDto productAttributeDto) {
        logger.info("Updating product attribute association ID: {}", productAttributeId);
        
        ProductAttribute existingProductAttribute = productAttributeRepository.findById(productAttributeId)
                .orElseThrow(() -> new IllegalArgumentException("Product attribute association not found with ID: " + productAttributeId));
        
        // Update fields
        existingProductAttribute.setProductAttributeValue(productAttributeDto.getProductAttributeValue());
        existingProductAttribute.setProductAttributeEnabled(true); // Default to enabled
        existingProductAttribute.setModifiedDt(LocalDateTime.now());
        
        ProductAttribute savedProductAttribute = productAttributeRepository.save(existingProductAttribute);
        return convertProductAttributeToDto(savedProductAttribute);
    }

    // =============================================
    // UTILITY OPERATIONS
    // =============================================

    @Override
    public long getTotalProductAttrCount() {
        return productAttrRepository.count();
    }

    @Override
    public List<String> getAllProductAttrNames() {
        logger.debug("Getting all product attribute names");
        return productAttrRepository.findAllProductAttrNames();
    }

    @Override
    public boolean existsByName(String productAttrName) {
        return productAttrRepository.existsByProductAttrName(productAttrName);
    }

    // =============================================
    // HELPER METHODS
    // =============================================

    private ProductAttrDto convertToDto(ProductAttr productAttr) {
        return ProductMapperUtil.toProductAttrDto(productAttr);
    }

    private ProductAttr convertToEntity(ProductAttrDto productAttrDto) {
        return ProductMapperUtil.toProductAttrEntity(productAttrDto);
    }

    private ProductAttributeDto convertProductAttributeToDto(ProductAttribute productAttribute) {
        return ProductMapperUtil.toProductAttributeDto(productAttribute);
    }
}