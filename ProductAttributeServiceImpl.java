package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.GroupedProductAttributeDto;
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

import java.math.BigDecimal;
import java.util.*;

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
        logger.info("Creating product attribute from DTO: productId={}, attrId={}, value={}, quantity={}, checkUnique={}", 
                   requestDto.getProductId(), requestDto.getProductAttrId(), 
                   requestDto.getProductAttributeValue(), requestDto.getProductAttributeQuantity(), requestDto.getCheckUnique());
        
        // Validate product exists if productId is provided
        Product product = null;
        if (requestDto.getProductId() != null) {
            product = productRepository.findById(requestDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + requestDto.getProductId()));
        }
        
        // Validate product attribute exists
        ProductAttr productAttr = productAttrRepository.findById(requestDto.getProductAttrId())
                .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + requestDto.getProductAttrId()));
        
        // Check unique combination of PRODUCT_ATTRIBUTE_VALUE and PRODUCT_ATTRIBUTE_PRICE if checkUnique is true
        if (requestDto.getCheckUnique() != null && requestDto.getCheckUnique() && 
            requestDto.getProductAttributeValue() != null && requestDto.getProductAttributePrice() != null) {
            boolean exists = productAttributeRepository.existsByValueAndPrice(
                requestDto.getProductAttributeValue(), requestDto.getProductAttributePrice());
            if (exists) {
                throw new IllegalArgumentException(
                    String.format("Product attribute combination already exists: value=%s, price=%s", 
                                 requestDto.getProductAttributeValue(), requestDto.getProductAttributePrice()));
            }
        }
        
        // Check if combination already exists using repository query (only if productId is provided) - Keep existing logic as fallback
        if (requestDto.getProductId() != null && 
            productAttributeRepository.findByProductId(requestDto.getProductId())
                .stream()
                .anyMatch(pa -> pa.getProductAttr().getProductAttrId().equals(requestDto.getProductAttrId()) && 
                               pa.getProductAttributeValue().equals(requestDto.getProductAttributeValue()))) {
            throw new IllegalArgumentException(
                String.format("Product attribute combination already exists: product=%d, attr=%d, value=%s", 
                             requestDto.getProductId(), requestDto.getProductAttrId(), requestDto.getProductAttributeValue()));
        }
        
        // Create new product attribute association
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setProduct(product); // Can be null for global attributes
        productAttribute.setProductAttr(productAttr);
        productAttribute.setProductAttributeValue(requestDto.getProductAttributeValue());
        productAttribute.setProductAttributeQuantity(requestDto.getProductAttributeQuantity() != null ? requestDto.getProductAttributeQuantity() : 0);
        productAttribute.setProductAttributePrice(requestDto.getProductAttributePrice());
        productAttribute.setProductAttributeEnabled(requestDto.getProductAttributeEnabled() != null ? requestDto.getProductAttributeEnabled() : true);
        
        ProductAttribute savedProductAttribute = productAttributeRepository.save(productAttribute);
        logger.info("Product attribute created successfully with ID: {}", savedProductAttribute.getProductAttributeId());
        
        return ProductMapperUtil.toProductAttributeDto(savedProductAttribute);
    }

    @Override
    @Transactional
    public ProductAttributeDto updateProductAttribute(Long productAttributeId, ProductAttributeRequestDto requestDto) {
        logger.info("Updating product attribute ID: {} from request DTO, checkUnique={}", productAttributeId, requestDto.getCheckUnique());
        
        ProductAttribute existingProductAttribute = productAttributeRepository.findById(productAttributeId)
                .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + productAttributeId));
        
        // Check unique combination of PRODUCT_ATTRIBUTE_VALUE and PRODUCT_ATTRIBUTE_PRICE if checkUnique is true
        // Only check if at least one of value or price is being updated
        if (requestDto.getCheckUnique() != null && requestDto.getCheckUnique()) {
            String newValue = requestDto.getProductAttributeValue() != null ? requestDto.getProductAttributeValue() : existingProductAttribute.getProductAttributeValue();
            BigDecimal newPrice = requestDto.getProductAttributePrice() != null ? requestDto.getProductAttributePrice() : existingProductAttribute.getProductAttributePrice();
            
            if (newValue != null && newPrice != null) {
                boolean exists = productAttributeRepository.existsByValueAndPriceExcludingId(
                    newValue, newPrice, productAttributeId);
                if (exists) {
                    throw new IllegalArgumentException(
                        String.format("Product attribute combination already exists: value=%s, price=%s", 
                                     newValue, newPrice));
                }
            }
        }
        
        // Validate and update product if changed (can be null)
        if (requestDto.getProductId() != null && 
            (existingProductAttribute.getProduct() == null || 
             !requestDto.getProductId().equals(existingProductAttribute.getProduct().getProductId()))) {
            Product newProduct = productRepository.findById(requestDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + requestDto.getProductId()));
            existingProductAttribute.setProduct(newProduct);
        } else if (requestDto.getProductId() == null) {
            // Set product to null if explicitly requesting to remove product association
            existingProductAttribute.setProduct(null);
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
        if (requestDto.getProductAttributePrice() != null) {
            existingProductAttribute.setProductAttributePrice(requestDto.getProductAttributePrice());
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
    
    // =============================================
    // CUSTOM OPERATIONS
    // =============================================
    
    @Override
    public List<GroupedProductAttributeDto> getGroupedProductAttributes(Long productId, Long productAttrId, Boolean enabled) {
        logger.debug("Getting grouped product attributes with filters - productId: {}, productAttrId: {}, enabled: {}", 
                    productId, productAttrId, enabled);
        
        List<ProductAttribute> allAttributes = productAttributeRepository.findGroupedProductAttributes(
                productId, productAttrId, enabled);
        
        // Group by value and price, keep one sample per unique combination
        Map<String, GroupedProductAttributeDto> groupedMap = new LinkedHashMap<>();
        
        for (ProductAttribute attribute : allAttributes) {
            String key = attribute.getProductAttributeValue() + "_" + attribute.getProductAttributePrice();
            
            if (!groupedMap.containsKey(key)) {
                GroupedProductAttributeDto groupedDto = GroupedProductAttributeDto.builder()
                        .productAttributeId(attribute.getProductAttributeId())
                        .productId(attribute.getProduct().getProductId())
                        .productAttrId(attribute.getProductAttr().getProductAttrId())
                        .productAttributeValue(attribute.getProductAttributeValue())
                        .productAttributePrice(attribute.getProductAttributePrice())
                        .productAttributeEnabled(attribute.getProductAttributeEnabled())
                        .productAttributeQuantity(attribute.getProductAttributeQuantity())
                        .modifiedDt(attribute.getModifiedDt())
                        .build();
                
                groupedMap.put(key, groupedDto);
            }
        }
        
        List<GroupedProductAttributeDto> result = new ArrayList<>(groupedMap.values());
        logger.debug("Found {} unique combinations from {} total attributes", result.size(), allAttributes.size());
        
        return result;
    }
    
    @Override
    @Transactional
    public int updatePricesByAttributeValues(List<String> productAttributeValues, BigDecimal productAttributePrice) {
        logger.info("Updating product attribute price to {} for values: {}", productAttributePrice, productAttributeValues);
        
        if (productAttributeValues == null || productAttributeValues.isEmpty()) {
            throw new IllegalArgumentException("Product attribute values list cannot be null or empty");
        }
        
        if (productAttributePrice == null || productAttributePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product attribute price must be positive");
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
        
        // Validate product exists if productId is provided
        if (requestDto.getProductId() != null && !productRepository.existsById(requestDto.getProductId())) {
            throw new IllegalArgumentException("Product not found with ID: " + requestDto.getProductId());
        }
        
        // Validate product attribute exists if productAttrId is provided
        if (requestDto.getProductAttrId() != null && !productAttrRepository.existsById(requestDto.getProductAttrId())) {
            throw new IllegalArgumentException("Product attribute not found with ID: " + requestDto.getProductAttrId());
        }
        
        int updatedCount = productAttributeRepository.updateByAttributeValues(
                productAttributeValues,
                requestDto.getProductId(),
                requestDto.getProductAttrId(),
                requestDto.getProductAttributeQuantity(),
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
}