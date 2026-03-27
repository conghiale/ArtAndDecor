package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductAttributeDto;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    // CRUD OPERATIONS  
    // =============================================
    
    @Override
    @Transactional
    public ProductAttributeDto createProductAttribute(Long productId, Long productAttrId, String attributeValue, Integer quantity) {
        logger.info("Creating product attribute: productId={}, attrId={}, value={}, quantity={}", 
                   productId, productAttrId, attributeValue, quantity);
        
        // Validate product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        
        // Validate product attribute exists
        ProductAttr productAttr = productAttrRepository.findById(productAttrId)
                .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + productAttrId));
        
        // Check if combination already exists
        if (existsByCombo(productId, productAttrId, attributeValue)) {
            throw new IllegalArgumentException(
                String.format("Product attribute combination already exists: product=%d, attr=%d, value=%s", 
                             productId, productAttrId, attributeValue));
        }
        
        // Create new product attribute association
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setProduct(product);
        productAttribute.setProductAttr(productAttr);
        productAttribute.setProductAttributeValue(attributeValue);
        productAttribute.setProductAttributeQuantity(quantity != null ? quantity : 0);
        productAttribute.setProductAttributeEnabled(true);
        
        ProductAttribute savedProductAttribute = productAttributeRepository.save(productAttribute);
        logger.info("Product attribute created successfully with ID: {}", savedProductAttribute.getProductAttributeId());
        
        return ProductMapperUtil.toProductAttributeDto(savedProductAttribute);
    }
    
    @Override
    @Transactional
    public ProductAttributeDto updateProductAttribute(Long productAttributeId, ProductAttributeDto productAttributeDto) {
        logger.info("Updating product attribute ID: {}", productAttributeId);
        
        ProductAttribute existingProductAttribute = productAttributeRepository.findById(productAttributeId)
                .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + productAttributeId));
        
        // Update fields
        if (productAttributeDto.getProductAttributeValue() != null) {
            existingProductAttribute.setProductAttributeValue(productAttributeDto.getProductAttributeValue());
        }
        if (productAttributeDto.getProductAttributeQuantity() != null) {
            existingProductAttribute.setProductAttributeQuantity(productAttributeDto.getProductAttributeQuantity());
        }
        if (productAttributeDto.getProductAttributeEnabled() != null) {
            existingProductAttribute.setProductAttributeEnabled(productAttributeDto.getProductAttributeEnabled());
        }
        
        ProductAttribute updatedProductAttribute = productAttributeRepository.save(existingProductAttribute);
        logger.info("Product attribute updated successfully with ID: {}", updatedProductAttribute.getProductAttributeId());
        
        return ProductMapperUtil.toProductAttributeDto(updatedProductAttribute);
    }
    
    @Override
    @Transactional
    public ProductAttributeDto updateProductAttributeQuantity(Long productAttributeId, Integer newQuantity) {
        logger.info("Updating product attribute quantity: ID={}, newQuantity={}", productAttributeId, newQuantity);
        
        ProductAttribute productAttribute = productAttributeRepository.findById(productAttributeId)
                .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + productAttributeId));
        
        productAttribute.setProductAttributeQuantity(newQuantity != null ? newQuantity : 0);
        
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
    
    @Override
    @Transactional
    public void deleteProductAttribute(Long productId, Long productAttrId, String attributeValue) {
        logger.info("Deleting product attribute by combination: productId={}, attrId={}, value={}", 
                   productId, productAttrId, attributeValue);
        
        Optional<ProductAttribute> productAttribute = findProductAttributeEntityByCombo(productId, productAttrId, attributeValue);
        if (productAttribute.isEmpty()) {
            throw new IllegalArgumentException(
                String.format("Product attribute not found: product=%d, attr=%d, value=%s", 
                             productId, productAttrId, attributeValue));
        }
        
        productAttributeRepository.delete(productAttribute.get());
        logger.info("Product attribute deleted successfully");
    }

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
    public List<ProductAttributeDto> findProductAttributesByProductId(Long productId) {
        logger.debug("Finding product attributes by product ID: {}", productId);
        return productAttributeRepository.findByProductId(productId)
                .stream()
                .map(ProductMapperUtil::toProductAttributeDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductAttributeDto> findProductAttributesByAttrId(Long productAttrId) {
        logger.debug("Finding product attributes by attr ID: {}", productAttrId);
        return productAttributeRepository.findByProductAttrId(productAttrId)
                .stream()
                .map(ProductMapperUtil::toProductAttributeDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<ProductAttributeDto> findProductAttributeByCombo(Long productId, Long productAttrId, String attributeValue) {
        logger.debug("Finding product attribute by combo: productId={}, attrId={}, value={}", 
                    productId, productAttrId, attributeValue);
        return findProductAttributeEntityByCombo(productId, productAttrId, attributeValue)
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
    // UTILITY OPERATIONS
    // =============================================
    
    @Override
    public boolean existsByCombo(Long productId, Long productAttrId, String attributeValue) {
        logger.debug("Checking if product attribute exists by combo: productId={}, attrId={}, value={}", 
                    productId, productAttrId, attributeValue);
        return findProductAttributeEntityByCombo(productId, productAttrId, attributeValue).isPresent();
    }
    
    @Override
    public long countByProductId(Long productId) {
        logger.debug("Counting product attributes by product ID: {}", productId);
        return productAttributeRepository.countByProductId(productId);
    }
    
    @Override
    public long getTotalCount() {
        logger.debug("Getting total product attributes count");
        return productAttributeRepository.count();
    }
    
    // =============================================
    // HELPER METHODS
    // =============================================
    
    private Optional<ProductAttribute> findProductAttributeEntityByCombo(Long productId, Long productAttrId, String attributeValue) {
        return productAttributeRepository.findByProductId(productId)
                .stream()
                .filter(pa -> pa.getProductAttr().getProductAttrId().equals(productAttrId) && 
                             pa.getProductAttributeValue().equals(attributeValue))
                .findFirst();
    }
}