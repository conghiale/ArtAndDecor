package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductAttributeDto;
import org.artanddecor.dto.ProductAttributeRequestDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.ProductAttr;
import org.artanddecor.model.ProductAttribute;
import org.artanddecor.repository.ProductAttrRepository;
import org.artanddecor.repository.ProductAttributeRepository;
import org.artanddecor.services.ProductAttributeService;
import org.artanddecor.utils.ProductMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ProductAttributeService Implementation 
 * Manages master attribute definitions with pricing
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductAttributeServiceImpl implements ProductAttributeService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductAttributeServiceImpl.class);
    
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductAttrRepository productAttrRepository;

    // =============================================
    // FIND OPERATIONS
    // =============================================
    
    @Override
    public Optional<ProductAttributeDto> findProductAttributeById(Long productAttributeId) {
        logger.debug("Finding product attribute by ID: {}", productAttributeId);
        return productAttributeRepository.findById(productAttributeId)
                .map(ProductMapperUtil::toProductAttributeDto);
    }

    @Override
    public List<ProductAttributeDto> findAttributesByAttrId(Long attrId) {
        logger.debug("Finding attributes by attr ID: {}", attrId);
        return productAttributeRepository.findByProductAttrId(attrId)
                .stream()
                .map(ProductMapperUtil::toProductAttributeDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductAttributeDto> findAttributesByValue(String attributeValue) {
        logger.debug("Finding attributes by value: {}", attributeValue);
        return productAttributeRepository.findByProductAttributeValue(attributeValue)
                .stream()
                .map(ProductMapperUtil::toProductAttributeDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductAttributeDto> findByAttrIdValueAndPrice(Long attrId, String value, BigDecimal price) {
        logger.debug("Finding attribute by attrId: {}, value: {}, price: {}", attrId, value, price);
        return productAttributeRepository.findByAttrIdValueAndPrice(attrId, value, price)
                .map(ProductMapperUtil::toProductAttributeDto);
    }
    
    @Override
    public Page<ProductAttributeDto> getProductAttributesByCriteria(
            Long attrId, Boolean enabled, String attributeValue, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        logger.debug("Getting product attributes with criteria - attrId: {}, enabled: {}, value: {}, priceRange: [{}, {}]",
                    attrId, enabled, attributeValue, minPrice, maxPrice);
        
        Page<ProductAttribute> productAttributePage = productAttributeRepository.findProductAttributesByCriteria(
                attrId, enabled, attributeValue, minPrice, maxPrice, pageable);
        
        return productAttributePage.map(ProductMapperUtil::toProductAttributeDto);
    }

    @Override
    public List<String> getDistinctValuesByAttrId(Long attrId) {
        logger.debug("Getting distinct values for attr ID: {}", attrId);
        return productAttributeRepository.findDistinctValuesByAttrId(attrId);
    }

    @Override
    public BigDecimal[] getPriceRangeByAttrId(Long attrId) {
        logger.debug("Getting price range for attr ID: {}", attrId);
        List<Object[]> results = productAttributeRepository.getPriceRangeByAttrId(attrId);
        
        if (results.isEmpty() || results.get(0)[0] == null) {
            return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO};
        }
        
        Object[] result = results.get(0);
        BigDecimal minPrice = (BigDecimal) result[0];
        BigDecimal maxPrice = (BigDecimal) result[1];
        
        return new BigDecimal[]{minPrice != null ? minPrice : BigDecimal.ZERO, 
                               maxPrice != null ? maxPrice : BigDecimal.ZERO};
    }

    // =============================================
    // CRUD OPERATIONS  
    // =============================================
    
    @Override
    @Transactional
    public ProductAttributeDto createProductAttribute(ProductAttributeRequestDto requestDto) {
        logger.info("Creating product attribute: attrId={}, value={}, displayName={}, price={}", 
                   requestDto.getProductAttrId(), requestDto.getProductAttributeValue(), 
                   requestDto.getProductAttributeDisplayName(), requestDto.getProductAttributePrice());
        
        // Validate product attribute exists
        ProductAttr productAttr = productAttrRepository.findById(requestDto.getProductAttrId())
                .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + requestDto.getProductAttrId()));
        
        // Check unique combination of PRODUCT_ATTR_ID, PRODUCT_ATTRIBUTE_VALUE, PRODUCT_ATTRIBUTE_PRICE
        if (productAttributeRepository.existsByValueAttrIdAndPrice(
                requestDto.getProductAttributeValue(), requestDto.getProductAttrId(), requestDto.getProductAttributePrice())) {
            throw new IllegalArgumentException(
                String.format("Product attribute combination already exists: attrId=%d, value=%s, price=%s", 
                             requestDto.getProductAttrId(), requestDto.getProductAttributeValue(), requestDto.getProductAttributePrice()));
        }
        
        // Create new product attribute
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setProductAttr(productAttr);
        productAttribute.setProductAttributeValue(requestDto.getProductAttributeValue());
        productAttribute.setProductAttributeDisplayName(requestDto.getProductAttributeDisplayName());
        productAttribute.setProductAttributePrice(requestDto.getProductAttributePrice());
        productAttribute.setProductAttributeEnabled(requestDto.getProductAttributeEnabled() != null ? requestDto.getProductAttributeEnabled() : true);
        
        ProductAttribute savedProductAttribute = productAttributeRepository.save(productAttribute);
        logger.info("Product attribute created successfully with ID: {}", savedProductAttribute.getProductAttributeId());
        
        return ProductMapperUtil.toProductAttributeDto(savedProductAttribute);
    }
    
    @Override
    @Transactional
    public ProductAttributeDto updateProductAttribute(Long productAttributeId, ProductAttributeRequestDto requestDto) {
        logger.info("Updating product attribute ID: {}", productAttributeId);
        
        ProductAttribute existingProductAttribute = productAttributeRepository.findById(productAttributeId)
                .orElseThrow(() -> new ResourceNotFoundException("Product attribute not found with ID: " + productAttributeId));
        
        // Check unique combination if critical fields are being updated
        Long newAttrId = requestDto.getProductAttrId() != null ? requestDto.getProductAttrId() : existingProductAttribute.getProductAttr().getProductAttrId();
        String newValue = requestDto.getProductAttributeValue() != null ? requestDto.getProductAttributeValue() : existingProductAttribute.getProductAttributeValue();
        BigDecimal newPrice = requestDto.getProductAttributePrice() != null ? requestDto.getProductAttributePrice() : existingProductAttribute.getProductAttributePrice();
        
        if (productAttributeRepository.existsByValueAttrIdAndPriceExcludingId(
                newValue, newAttrId, newPrice, productAttributeId)) {
            throw new IllegalArgumentException(
                String.format("Product attribute combination already exists: attrId=%d, value=%s, price=%s", 
                             newAttrId, newValue, newPrice));
        }
        
        // Validate and update product attr if changed
        if (requestDto.getProductAttrId() != null && 
            !requestDto.getProductAttrId().equals(existingProductAttribute.getProductAttr().getProductAttrId())) {
            ProductAttr newProductAttr = productAttrRepository.findById(requestDto.getProductAttrId())
                    .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + requestDto.getProductAttrId()));
            existingProductAttribute.setProductAttr(newProductAttr);
        }
        
        // Update other fields
        if (requestDto.getProductAttributeValue() != null) {
            existingProductAttribute.setProductAttributeValue(requestDto.getProductAttributeValue());
        }
        if (requestDto.getProductAttributeDisplayName() != null) {
            existingProductAttribute.setProductAttributeDisplayName(requestDto.getProductAttributeDisplayName());
        }
        if (requestDto.getProductAttributePrice() != null) {
            existingProductAttribute.setProductAttributePrice(requestDto.getProductAttributePrice());
        }
        if (requestDto.getProductAttributeEnabled() != null) {
            existingProductAttribute.setProductAttributeEnabled(requestDto.getProductAttributeEnabled());
        }
        
        ProductAttribute savedProductAttribute = productAttributeRepository.save(existingProductAttribute);
        logger.info("Product attribute updated successfully");
        
        return ProductMapperUtil.toProductAttributeDto(savedProductAttribute);
    }
    
    @Override
    @Transactional
    public void deleteProductAttribute(Long productAttributeId) {
        logger.info("Deleting product attribute ID: {}", productAttributeId);
        
        if (!productAttributeRepository.existsById(productAttributeId)) {
            throw new ResourceNotFoundException("Product attribute not found with ID: " + productAttributeId);
        }
        
        productAttributeRepository.deleteById(productAttributeId);
        logger.info("Product attribute deleted successfully");
    }

    // =============================================
    // BATCH OPERATIONS
    // =============================================
    
    @Override
    @Transactional
    public int updatePricesByAttributeValues(List<String> productAttributeValues, BigDecimal productAttributePrice) {
        logger.info("Updating product attribute price to {} for values: {}", productAttributePrice, productAttributeValues);
        
        if (productAttributeValues == null || productAttributeValues.isEmpty()) {
            throw new IllegalArgumentException("Product attribute values list cannot be null or empty");
        }
        
        if (productAttributePrice == null || productAttributePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product attribute price must be non-negative");
        }
        
        int updatedCount = productAttributeRepository.updatePricesByAttributeValues(productAttributeValues, productAttributePrice);
        logger.info("Updated {} product attribute records with new price: {}", updatedCount, productAttributePrice);
        
        return updatedCount;
    }
    
    @Override
    @Transactional
    public int updateByAttributeValues(List<String> productAttributeValues, ProductAttributeRequestDto requestDto) {
        logger.info("Updating product attributes by values: {} with data: {}", productAttributeValues, requestDto);
        
        if (productAttributeValues == null || productAttributeValues.isEmpty()) {
            throw new IllegalArgumentException("Product attribute values list cannot be null or empty");
        }
        
        if (requestDto == null) {
            throw new IllegalArgumentException("Request DTO cannot be null");
        }
        
        // Validate product attribute exists if productAttrId is provided
        if (requestDto.getProductAttrId() != null && !productAttrRepository.existsById(requestDto.getProductAttrId())) {
            throw new IllegalArgumentException("Product attribute not found with ID: " + requestDto.getProductAttrId());
        }
        
        int updatedCount = productAttributeRepository.updateByAttributeValues(
                productAttributeValues,
                requestDto.getProductAttrId(),
                requestDto.getProductAttributePrice(),
                requestDto.getProductAttributeEnabled()
        );
        
        logger.info("Updated {} product attribute records by values: {}", updatedCount, productAttributeValues);
        return updatedCount;
    }
    
    @Override
    @Transactional
    public int deleteByAttributeValues(List<String> productAttributeValues) {
        logger.info("Deleting product attributes by values: {}", productAttributeValues);
        
        if (productAttributeValues == null || productAttributeValues.isEmpty()) {
            throw new IllegalArgumentException("Product attribute values list cannot be null or empty");
        }
        
        int deletedCount = productAttributeRepository.deleteByAttributeValues(productAttributeValues);
        logger.info("Deleted {} product attribute records by values: {}", deletedCount, productAttributeValues);
        
        return deletedCount;
    }
    
    @Override
    @Transactional
    public int deleteByProductAttrId(Long productAttrId) {
        logger.info("Deleting product attributes by product attr ID: {}", productAttrId);
        
        if (productAttrId == null) {
            throw new IllegalArgumentException("Product attribute ID cannot be null");
        }
        
        int deletedCount = productAttributeRepository.deleteByProductAttrId(productAttrId);
        logger.info("Deleted {} product attribute records by product attr ID: {}", deletedCount, productAttrId);
        
        return deletedCount;
    }

    // =============================================
    // UTILITY OPERATIONS
    // =============================================

    @Override
    public boolean existsAttributeCombination(Long attrId, String value, BigDecimal price) {
        return productAttributeRepository.existsByValueAttrIdAndPrice(value, attrId, price);
    }

    @Override
    public Long countByProductAttrId(Long attrId) {
        return productAttributeRepository.countByProductAttrId(attrId);
    }

    @Override
    public List<ProductAttributeDto> findByAttributeValues(List<String> values) {
        logger.debug("Finding attributes by values: {}", values);
        return productAttributeRepository.findByAttributeValues(values)
                .stream()
                .map(ProductMapperUtil::toProductAttributeDto)
                .collect(Collectors.toList());
    }
}