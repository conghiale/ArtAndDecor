package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductAttributeDto;
import org.artanddecor.dto.ProductAttributeRequestDto;
import org.artanddecor.model.Product;
import org.artanddecor.model.ProductAttr;
import org.artanddecor.model.ProductAttribute;
import org.artanddecor.repository.ProductRepository;
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

import java.util.Optional;

/**
 * ProductAttributeService Implementation 
 * Handles business logic for product attribute associations (PRODUCT_ATTRIBUTE table)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductAttributeServiceImpl implements ProductAttributeService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductAttributeServiceImpl.class);
    
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductRepository productRepository;
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
    public Page<ProductAttributeDto> getProductAttributesByCriteria(
            Long productId, Long productAttrId, Boolean enabled, String attributeValue, Pageable pageable) {
        logger.debug("Getting product attributes with criteria");
        
        Page<ProductAttribute> productAttributePage = productAttributeRepository.findProductAttributesByCriteriaPaginated(
                productId, productAttrId, enabled, attributeValue, pageable);
        
        return productAttributePage.map(ProductMapperUtil::toProductAttributeDto);
    }

    // =============================================
    // CRUD OPERATIONS  
    // =============================================
    
    @Override
    @Transactional
    public ProductAttributeDto createProductAttribute(ProductAttributeRequestDto requestDto) {
        logger.info("Creating product attribute from DTO: productId={}, attrId={}, value={}, quantity={}", 
                   requestDto.getProductId(), requestDto.getProductAttrId(), 
                   requestDto.getProductAttributeValue(), requestDto.getProductAttributeQuantity());
        
        // Validate product exists
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + requestDto.getProductId()));
        
        // Validate product attribute exists
        ProductAttr productAttr = productAttrRepository.findById(requestDto.getProductAttrId())
                .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + requestDto.getProductAttrId()));
        
        // Check if combination already exists using repository query
        if (productAttributeRepository.findByProductId(requestDto.getProductId())
                .stream()
                .anyMatch(pa -> pa.getProductAttr().getProductAttrId().equals(requestDto.getProductAttrId()) && 
                               pa.getProductAttributeValue().equals(requestDto.getProductAttributeValue()))) {
            throw new IllegalArgumentException(
                String.format("Product attribute combination already exists: product=%d, attr=%d, value=%s", 
                             requestDto.getProductId(), requestDto.getProductAttrId(), requestDto.getProductAttributeValue()));
        }
        
        // Create new product attribute association
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setProduct(product);
        productAttribute.setProductAttr(productAttr);
        productAttribute.setProductAttributeValue(requestDto.getProductAttributeValue());
        productAttribute.setProductAttributeQuantity(requestDto.getProductAttributeQuantity() != null ? requestDto.getProductAttributeQuantity() : 0);
        productAttribute.setProductAttributeEnabled(requestDto.getProductAttributeEnabled() != null ? requestDto.getProductAttributeEnabled() : true);
        
        ProductAttribute savedProductAttribute = productAttributeRepository.save(productAttribute);
        logger.info("Product attribute created successfully with ID: {}", savedProductAttribute.getProductAttributeId());
        
        return ProductMapperUtil.toProductAttributeDto(savedProductAttribute);
    }

    @Override
    @Transactional
    public ProductAttributeDto updateProductAttribute(Long productAttributeId, ProductAttributeRequestDto requestDto) {
        logger.info("Updating product attribute ID: {} from request DTO", productAttributeId);
        
        ProductAttribute existingProductAttribute = productAttributeRepository.findById(productAttributeId)
                .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + productAttributeId));
        
        // Validate and update product if changed
        if (requestDto.getProductId() != null && !requestDto.getProductId().equals(existingProductAttribute.getProduct().getProductId())) {
            Product newProduct = productRepository.findById(requestDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + requestDto.getProductId()));
            existingProductAttribute.setProduct(newProduct);
        }
        
        // Validate and update product attr if changed
        if (requestDto.getProductAttrId() != null && !requestDto.getProductAttrId().equals(existingProductAttribute.getProductAttr().getProductAttrId())) {
            ProductAttr newProductAttr = productAttrRepository.findById(requestDto.getProductAttrId())
                    .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + requestDto.getProductAttrId()));
            existingProductAttribute.setProductAttr(newProductAttr);
        }
        
        // Update fields
        if (requestDto.getProductAttributeValue() != null) {
            existingProductAttribute.setProductAttributeValue(requestDto.getProductAttributeValue());
        }
        if (requestDto.getProductAttributeQuantity() != null) {
            existingProductAttribute.setProductAttributeQuantity(requestDto.getProductAttributeQuantity());
        }
        if (requestDto.getProductAttributeEnabled() != null) {
            existingProductAttribute.setProductAttributeEnabled(requestDto.getProductAttributeEnabled());
        }
        
        ProductAttribute updatedProductAttribute = productAttributeRepository.save(existingProductAttribute);
        logger.info("Product attribute updated successfully with ID: {}", updatedProductAttribute.getProductAttributeId());
        
        return ProductMapperUtil.toProductAttributeDto(updatedProductAttribute);
    }
    
    @Override
    @Transactional
    public ProductAttributeDto updateProductAttributeQuantity(Long productAttributeId, Integer quantity) {
        logger.info("Updating product attribute quantity: ID={}, newQuantity={}", productAttributeId, quantity);
        
        ProductAttribute productAttribute = productAttributeRepository.findById(productAttributeId)
                .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + productAttributeId));
        
        productAttribute.setProductAttributeQuantity(quantity != null ? quantity : 0);
        
        ProductAttribute updatedProductAttribute = productAttributeRepository.save(productAttribute);
        logger.info("Product attribute quantity updated successfully");
        
        return ProductMapperUtil.toProductAttributeDto(updatedProductAttribute);
    }
    
    @Override
    @Transactional
    public void deleteProductAttribute(Long productAttributeId) {
        logger.info("Deleting product attribute by ID: {}", productAttributeId);
        
        if (!productAttributeRepository.existsById(productAttributeId)) {
            throw new IllegalArgumentException("Product attribute not found with ID: " + productAttributeId);
        }
        
        productAttributeRepository.deleteById(productAttributeId);
        logger.info("Product attribute deleted successfully");
    }
}