package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductVariantDto;
import org.artanddecor.dto.ProductVariantRequestDto;
import org.artanddecor.dto.ProductAttrWithVariantsDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.Product;
import org.artanddecor.model.ProductAttribute;
import org.artanddecor.model.ProductVariant;
import org.artanddecor.repository.ProductRepository;
import org.artanddecor.repository.ProductAttributeRepository;
import org.artanddecor.repository.ProductVariantRepository;
import org.artanddecor.services.ProductVariantService;
import org.artanddecor.utils.ProductMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ProductVariantService Implementation 
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private static final Logger logger = LoggerFactory.getLogger(ProductVariantServiceImpl.class);

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ProductAttributeRepository productAttributeRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductVariantDto> findProductVariantById(Long productVariantId) {
        logger.debug("Finding product variant by ID: {}", productVariantId);
        return productVariantRepository.findById(productVariantId)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantDto> findVariantsByProductId(Long productId) {
        logger.debug("Finding variants by product ID: {}", productId);
        return productVariantRepository.findByProductId(productId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantDto> findEnabledVariantsByProductId(Long productId) {
        logger.debug("Finding enabled variants by product ID: {}", productId);
        return productVariantRepository.findEnabledByProductId(productId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantDto> findVariantsByProductAttributeId(Long productAttributeId) {
        logger.debug("Finding variants by product attribute ID: {}", productAttributeId);
        return productVariantRepository.findByProductAttributeId(productAttributeId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductVariantDto> findVariantByProductAndAttribute(Long productId, Long productAttributeId) {
        logger.debug("Finding variant by product ID: {} and attribute ID: {}", productId, productAttributeId);
        return productVariantRepository.findByProductIdAndProductAttributeId(productId, productAttributeId)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductVariantDto> getProductVariantsByCriteria(Long productId, Long productAttributeId, 
                                                               Boolean enabled, Boolean hasStock, Pageable pageable) {
        logger.debug("Getting variants by criteria - productId: {}, attrId: {}, enabled: {}, hasStock: {}", 
                    productId, productAttributeId, enabled, hasStock);
        return productVariantRepository.findProductVariantsByCriteria(productId, productAttributeId, enabled, hasStock, pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantDto> findVariantsWithStock() {
        logger.debug("Finding variants with stock > 0");
        return productVariantRepository.findVariantsWithStock()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttrWithVariantsDto> getProductAttributesWithVariants(Long productId) {
        logger.debug("Getting product attributes with variants for product ID: {}", productId);
        
        List<ProductVariant> variants = productVariantRepository.findEnabledByProductId(productId);
        
        // Group variants by ProductAttr
        Map<Long, List<ProductVariant>> variantsByAttr = variants.stream()
                .collect(Collectors.groupingBy(
                    variant -> variant.getProductAttribute().getProductAttr().getProductAttrId()
                ));
        
        List<ProductAttrWithVariantsDto> result = new ArrayList<>();
        
        for (Map.Entry<Long, List<ProductVariant>> entry : variantsByAttr.entrySet()) {
            List<ProductVariant> attrVariants = entry.getValue();
            if (!attrVariants.isEmpty()) {
                // Get ProductAttr info from first variant
                var productAttr = attrVariants.get(0).getProductAttribute().getProductAttr();
                
                // Build attribute variants list
                List<ProductVariantDto> variantDtos = attrVariants.stream()
                        .map(variant -> ProductMapperUtil.toProductVariantDto(variant))
                        .collect(Collectors.toList());
                
                // Build final DTO using new structure
                ProductAttrWithVariantsDto attrWithVariants = ProductAttrWithVariantsDto.builder()
                        .productAttr(ProductMapperUtil.toProductAttrDto(productAttr))
                        .variants(variantDtos)
                        .build();
                
                // Calculate computed fields
                attrWithVariants.setTotalQuantity(attrWithVariants.calculateTotalQuantity());
                attrWithVariants.setVariantCount(attrWithVariants.calculateVariantCount());
                attrWithVariants.setHasStock(attrWithVariants.calculateHasStock());
                
                result.add(attrWithVariants);
            }
        }
        
        // Sort by attribute name
        result.sort(Comparator.comparing(dto -> dto.getProductAttr().getProductAttrName()));
        
        logger.debug("Found {} attribute types with variants for product {}", result.size(), productId);
        return result;
    }

    @Override
    public ProductVariantDto createProductVariant(ProductVariantRequestDto requestDto) {
        logger.info("Creating product variant: productId={}, attributeId={}, stock={}", 
                   requestDto.getProductId(), requestDto.getProductAttributeId(), requestDto.getProductVariantStock());
        
        // Validate product exists
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + requestDto.getProductId()));
        
        // Validate product attribute exists
        ProductAttribute productAttribute = productAttributeRepository.findById(requestDto.getProductAttributeId())
                .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + requestDto.getProductAttributeId()));
        
        // Check if combination already exists
        if (productVariantRepository.existsByProductIdAndProductAttributeId(
                requestDto.getProductId(), requestDto.getProductAttributeId())) {
            throw new IllegalArgumentException(
                String.format("Product variant already exists for product %d and attribute %d", 
                             requestDto.getProductId(), requestDto.getProductAttributeId()));
        }
        
        // Create new product variant
        ProductVariant productVariant = new ProductVariant();
        productVariant.setProduct(product);
        productVariant.setProductAttribute(productAttribute);
        productVariant.setProductVariantStock(requestDto.getProductVariantStock());
        productVariant.setProductVariantEnabled(requestDto.getProductVariantEnabled() != null ? requestDto.getProductVariantEnabled() : true);
        
        ProductVariant savedVariant = productVariantRepository.save(productVariant);
        logger.info("Product variant created successfully with ID: {}", savedVariant.getProductVariantId());
        
        return convertToDto(savedVariant);
    }

    @Override
    public ProductVariantDto updateProductVariant(Long productVariantId, ProductVariantRequestDto requestDto) {
        logger.info("Updating product variant ID: {}", productVariantId);
        
        ProductVariant existingVariant = productVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with ID: " + productVariantId));
        
        // Update product if changed
        if (requestDto.getProductId() != null && !requestDto.getProductId().equals(existingVariant.getProduct().getProductId())) {
            Product newProduct = productRepository.findById(requestDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + requestDto.getProductId()));
            existingVariant.setProduct(newProduct);
        }
        
        // Update product attribute if changed
        if (requestDto.getProductAttributeId() != null && 
            !requestDto.getProductAttributeId().equals(existingVariant.getProductAttribute().getProductAttributeId())) {
            ProductAttribute newAttribute = productAttributeRepository.findById(requestDto.getProductAttributeId())
                    .orElseThrow(() -> new IllegalArgumentException("Product attribute not found with ID: " + requestDto.getProductAttributeId()));
            existingVariant.setProductAttribute(newAttribute);
            
            // Check if new combination would create duplicate
            if (productVariantRepository.existsByProductIdAndProductAttributeId(
                    existingVariant.getProduct().getProductId(), requestDto.getProductAttributeId())) {
                throw new IllegalArgumentException("Product variant already exists for this product-attribute combination");
            }
        }
        
        // Update other fields
        if (requestDto.getProductVariantStock() != null) {
            existingVariant.setProductVariantStock(requestDto.getProductVariantStock());
        }
        if (requestDto.getProductVariantEnabled() != null) {
            existingVariant.setProductVariantEnabled(requestDto.getProductVariantEnabled());
        }
        
        ProductVariant updatedVariant = productVariantRepository.save(existingVariant);
        logger.info("Product variant updated successfully");
        
        return convertToDto(updatedVariant);
    }

    @Override
    public void deleteProductVariant(Long productVariantId) {
        logger.info("Deleting product variant ID: {}", productVariantId);
        
        if (!productVariantRepository.existsById(productVariantId)) {
            throw new ResourceNotFoundException("Product variant not found with ID: " + productVariantId);
        }
        
        productVariantRepository.deleteById(productVariantId);
        logger.info("Product variant deleted successfully");
    }

    @Override
    public ProductVariantDto updateProductVariantStock(Long productVariantId, Integer stock) {
        logger.info("Updating stock for variant ID: {}, new stock: {}", productVariantId, stock);
        
        int updatedRows = productVariantRepository.updateStock(productVariantId, stock);
        if (updatedRows == 0) {
            throw new ResourceNotFoundException("Product variant not found with ID: " + productVariantId);
        }
        
        return findProductVariantById(productVariantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with ID: " + productVariantId));
    }

    @Override
    public boolean decreaseProductVariantStock(Long productVariantId, Integer quantity) {
        logger.info("Decreasing stock for variant ID: {}, quantity: {}", productVariantId, quantity);
        
        int updatedRows = productVariantRepository.decreaseStock(productVariantId, quantity);
        boolean success = updatedRows > 0;
        
        if (success) {
            logger.info("Stock decreased successfully for variant ID: {}", productVariantId);
        } else {
            logger.warn("Failed to decrease stock for variant ID: {} - insufficient stock or variant not found", productVariantId);
        }
        
        return success;
    }

    @Override
    public ProductVariantDto increaseProductVariantStock(Long productVariantId, Integer quantity) {
        logger.info("Increasing stock for variant ID: {}, quantity: {}", productVariantId, quantity);
        
        int updatedRows = productVariantRepository.increaseStock(productVariantId, quantity);
        if (updatedRows == 0) {
            throw new ResourceNotFoundException("Product variant not found with ID: " + productVariantId);
        }
        
        return findProductVariantById(productVariantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with ID: " + productVariantId));
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalStockByProductId(Long productId) {
        logger.debug("Getting total stock for product ID: {}", productId);
        return productVariantRepository.getTotalStockByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsProductVariant(Long productId, Long productAttributeId) {
        return productVariantRepository.existsByProductIdAndProductAttributeId(productId, productAttributeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countVariantsByProductId(Long productId) {
        return productVariantRepository.countByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countEnabledVariantsByProductId(Long productId) {
        return productVariantRepository.countEnabledByProductId(productId);
    }

    // =============================================
    // HELPER METHODS
    // =============================================

    private ProductVariantDto convertToDto(ProductVariant productVariant) {
        return ProductMapperUtil.toProductVariantDto(productVariant);
    }
}