package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductAttrDto;
import org.artanddecor.dto.ProductAttrRequestDto;
import org.artanddecor.model.ProductAttr;
import org.artanddecor.repository.ProductAttrRepository;
import org.artanddecor.services.ProductAttrService;
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
 * ProductAttr Service Implementation
 * Handles business logic for product attribute management
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductAttrServiceImpl implements ProductAttrService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductAttrServiceImpl.class);
    
    private final ProductAttrRepository productAttrRepository;

    // =============================================
    // ADMIN-FOCUSED OPERATIONS
    // =============================================

    @Override
    public Optional<ProductAttrDto> findProductAttrById(Long productAttrId) {
        logger.debug("Finding product attribute by ID: {}", productAttrId);
        return productAttrRepository.findById(productAttrId)
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
    // CRUD OPERATIONS
    // =============================================

    @Override
    @Transactional
    public ProductAttrDto createProductAttr(ProductAttrRequestDto requestDto) {
        logger.info("Creating new product attribute from request: {}", requestDto.getProductAttrName());
        
        // Validation - check name uniqueness using repository query
        if (productAttrRepository.existsByProductAttrName(requestDto.getProductAttrName())) {
            throw new IllegalArgumentException("Product attribute name already exists: " + requestDto.getProductAttrName());
        }
        
        ProductAttr productAttr = new ProductAttr();
        productAttr.setProductAttrName(requestDto.getProductAttrName());
        productAttr.setProductAttrDisplayName(requestDto.getProductAttrDisplayName());
        productAttr.setProductAttrRemark(requestDto.getProductAttrRemark());
        productAttr.setProductAttrEnabled(requestDto.getProductAttrEnabled() != null ? requestDto.getProductAttrEnabled() : true);
        
        ProductAttr savedProductAttr = productAttrRepository.save(productAttr);
        logger.info("Product attribute created successfully with ID: {}", savedProductAttr.getProductAttrId());
        
        return convertToDto(savedProductAttr);
    }

    @Override
    @Transactional
    public ProductAttrDto updateProductAttr(Long productAttrId, ProductAttrRequestDto requestDto) {
        logger.info("Updating product attribute ID: {} from request", productAttrId);
        
        ProductAttr existingProductAttr = productAttrRepository.findById(productAttrId)
                .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + productAttrId));
        
        // Validation - check if name exists for other records
        if (!existingProductAttr.getProductAttrName().equals(requestDto.getProductAttrName()) && 
            productAttrRepository.existsByProductAttrName(requestDto.getProductAttrName())) {
            throw new IllegalArgumentException("Product attribute name already exists: " + requestDto.getProductAttrName());
        }
        
        // Update fields
        existingProductAttr.setProductAttrName(requestDto.getProductAttrName());
        existingProductAttr.setProductAttrDisplayName(requestDto.getProductAttrDisplayName());
        existingProductAttr.setProductAttrRemark(requestDto.getProductAttrRemark());
        if (requestDto.getProductAttrEnabled() != null) {
            existingProductAttr.setProductAttrEnabled(requestDto.getProductAttrEnabled());
        }
        existingProductAttr.setModifiedDt(LocalDateTime.now());
        
        ProductAttr savedProductAttr = productAttrRepository.save(existingProductAttr);
        logger.info("Product attribute updated successfully with ID: {}", savedProductAttr.getProductAttrId());
        
        return convertToDto(savedProductAttr);
    }

    // =============================================
    // HELPER METHODS
    // =============================================

    private ProductAttrDto convertToDto(ProductAttr productAttr) {
        return ProductMapperUtil.toProductAttrDto(productAttr);
    }
}