package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductDto;
import org.artanddecor.dto.ProductRequestDto;
import org.artanddecor.dto.ProductAttributeRequestDto;
import org.artanddecor.dto.ProductImageDto;
import org.artanddecor.dto.SeoMetaRequestDto;
import org.artanddecor.dto.SeoMetaDto;
import org.artanddecor.model.Product;
import org.artanddecor.model.ProductImage;
import org.artanddecor.model.ProductAttribute;
import org.artanddecor.model.ProductAttr;
import org.artanddecor.model.Image;
import org.artanddecor.repository.ProductRepository;
import org.artanddecor.repository.ProductImageRepository;
import org.artanddecor.repository.ProductAttributeRepository;
import org.artanddecor.repository.ProductAttrRepository;
import org.artanddecor.repository.ImageRepository;
import org.artanddecor.services.ProductService;
import org.artanddecor.services.SeoMetaService;
import org.artanddecor.utils.ProductMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

/**
 * Product Service Implementation
 * Handles business logic for product management
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductAttrRepository productAttrRepository;
    private final ImageRepository imageRepository;
    private final SeoMetaService seoMetaService;

    // =============================================
    // CUSTOMER-FOCUSED OPERATIONS
    // =============================================

    @Override
    public Optional<ProductDto> findProductBySlug(String productSlug) {
        logger.debug("Finding product by slug: {}", productSlug);
        return productRepository.findByProductSlug(productSlug)
                .map(this::convertToDto);
    }

    @Override
    public Page<ProductDto> getProductsByCriteria(String textSearch, Boolean enabled, Long categoryId, Long typeId, Long stateId, 
                                                BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, String productCode, 
                                                Boolean featured, Boolean highlighted, Pageable pageable) {
        logger.debug("Getting products with criteria - textSearch: {}, enabled: {}, categoryId: {}, typeId: {}, stateId: {}, featured: {}, highlighted: {}", 
                    textSearch, enabled, categoryId, typeId, stateId, featured, highlighted);
        
        Page<Product> productPage = productRepository.findProductsByCriteriaPaginated(
            textSearch, enabled, categoryId, typeId, stateId, minPrice, maxPrice, inStock, productCode, 
            featured, highlighted, pageable);
        
        return productPage.map(this::convertToDto);
    }

    // =============================================
    // ADMIN-FOCUSED OPERATIONS
    // =============================================

    @Override
    public Optional<ProductDto> findProductById(Long productId) {
        logger.debug("Finding product by ID: {}", productId);
        return productRepository.findById(productId)
                .map(this::convertToDto);
    }

    @Override
    public Optional<ProductDto> findProductByName(String productName) {
        logger.debug("Finding product by name: {}", productName);
        return productRepository.findByProductName(productName)
                .map(this::convertToDto);
    }

    @Override
    public Optional<ProductDto> findProductByCode(String productCode) {
        logger.debug("Finding product by code: {}", productCode);
        return productRepository.findByProductCode(productCode)
                .map(this::convertToDto);
    }

    // =============================================
    // CRUD OPERATIONS - Unified ProductRequestDto approach
    // =============================================


    @Override
    @Transactional
    public ProductDto updateProduct(Long productId, ProductRequestDto productRequestDto) {
        logger.info("Updating product ID: {} with request DTO and {} images and {} attributes", productId, 
                   productRequestDto.getImageIds() != null ? productRequestDto.getImageIds().size() : 0,
                   productRequestDto.getProductAttributes() != null ? productRequestDto.getProductAttributes().size() : 0);
        
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        
        // Validation - check if slug/name/code exists for other records
        if (!existingProduct.getProductSlug().equals(productRequestDto.getProductSlug()) && 
            existsBySlug(productRequestDto.getProductSlug())) {
            throw new IllegalArgumentException("Product slug already exists: " + productRequestDto.getProductSlug());
        }
        if (!existingProduct.getProductName().equals(productRequestDto.getProductName()) && 
            existsByName(productRequestDto.getProductName())) {
            throw new IllegalArgumentException("Product name already exists: " + productRequestDto.getProductName());
        }
        if (!existingProduct.getProductCode().equals(productRequestDto.getProductCode()) && 
            existsByCode(productRequestDto.getProductCode())) {
            throw new IllegalArgumentException("Product code already exists: " + productRequestDto.getProductCode());
        }
        
        // Validate image IDs if provided
        if (productRequestDto.getImageIds() != null && !productRequestDto.getImageIds().isEmpty()) {
            validateImageIds(productRequestDto.getImageIds());
            if (productRequestDto.getPrimaryImageId() != null && !productRequestDto.getImageIds().contains(productRequestDto.getPrimaryImageId())) {
                throw new IllegalArgumentException("Primary image ID must be included in image IDs list");
            }
        }
        
        // Validate product attributes if provided
        validateProductAttributes(productRequestDto.getProductAttributes());
        
        // Handle SEO meta update if provided
        Long seoMetaId = null;
        if (productRequestDto.getSeoMeta() != null) {
            seoMetaId = handleSeoMeta(productRequestDto.getSeoMeta(), existingProduct.getSeoMetaId());
        }
        
        // Update product fields with unified method
        ProductMapperUtil.updateProductEntityFromRequestDto(existingProduct, productRequestDto, seoMetaId);
        Product updatedProduct = productRepository.save(existingProduct);
        
        // Update images if provided
        if (productRequestDto.getImageIds() != null) {
            updateProductImages(updatedProduct, productRequestDto.getImageIds(), productRequestDto.getPrimaryImageId());
        }
        
        // Update attributes if provided
        if (productRequestDto.getProductAttributes() != null) {
            updateProductAttributes(updatedProduct, productRequestDto.getProductAttributes());
        }
        
        return convertToDto(updatedProduct);
    }

    // =============================================
    @Override
    @Transactional
    public ProductDto createProduct(ProductRequestDto productRequestDto) {
        logger.info("Creating new product from request DTO: {} with {} images and {} attributes", 
                   productRequestDto.getProductName(), 
                   productRequestDto.getImageIds() != null ? productRequestDto.getImageIds().size() : 0,
                   productRequestDto.getProductAttributes() != null ? productRequestDto.getProductAttributes().size() : 0);
        
        // Validation
        if (existsBySlug(productRequestDto.getProductSlug())) {
            throw new IllegalArgumentException("Product slug already exists: " + productRequestDto.getProductSlug());
        }
        if (existsByName(productRequestDto.getProductName())) {
            throw new IllegalArgumentException("Product name already exists: " + productRequestDto.getProductName());
        }
        if (existsByCode(productRequestDto.getProductCode())) {
            throw new IllegalArgumentException("Product code already exists: " + productRequestDto.getProductCode());
        }
        
        // Validate image IDs if provided
        if (productRequestDto.getImageIds() != null && !productRequestDto.getImageIds().isEmpty()) {
            validateImageIds(productRequestDto.getImageIds());
            if (productRequestDto.getPrimaryImageId() != null && !productRequestDto.getImageIds().contains(productRequestDto.getPrimaryImageId())) {
                throw new IllegalArgumentException("Primary image ID must be included in image IDs list");
            }
        }
        
        // Validate product attributes if provided
        validateProductAttributes(productRequestDto.getProductAttributes());
        
        // Create SEO meta if provided
        Long seoMetaId = null;
        if (productRequestDto.getSeoMeta() != null) {
            seoMetaId = handleSeoMeta(productRequestDto.getSeoMeta(), null);
        }
        
        Product product = ProductMapperUtil.toProductEntityFromRequestDto(productRequestDto, seoMetaId);
        Product savedProduct = productRepository.save(product);
        
        // Associate images if provided
        if (productRequestDto.getImageIds() != null && !productRequestDto.getImageIds().isEmpty()) {
            associateImagesToProduct(savedProduct, productRequestDto.getImageIds(), productRequestDto.getPrimaryImageId());
        }
        
        // Associate attributes if provided - use direct association for new products
        if (productRequestDto.getProductAttributes() != null && !productRequestDto.getProductAttributes().isEmpty()) {
            associateAttributesToProduct(savedProduct, productRequestDto.getProductAttributes());
        }
        
        return convertToDto(savedProduct);
    }

    // =============================================
    // PRODUCT IMAGE OPERATIONS
    // =============================================

    @Override
    public ProductImageDto addImageToProduct(Long productId, Long imageId, Boolean isPrimary) {
        // Implementation to be added based on ProductImageService
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public void removeImageFromProduct(Long productId, Long imageId) {
        logger.info("Removing image {} from product {}", imageId, productId);
        
        // Validate product exists
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }
        
        // Validate image exists
        if (!imageRepository.existsById(imageId)) {
            throw new IllegalArgumentException("Image not found with ID: " + imageId);
        }
        
        // Check if product-image association exists
        if (!productImageRepository.existsByProductProductIdAndImageImageId(productId, imageId)) {
            logger.warn("Product-image association not found - productId: {}, imageId: {}", productId, imageId);
            return; // Silently ignore if association doesn't exist
        }
        
        // Delete the association
        productImageRepository.deleteByProductProductIdAndImageImageId(productId, imageId);
        logger.info("Successfully removed image {} from product {}", imageId, productId);
    }

    @Override
    public List<ProductImageDto> getProductImages(Long productId) {
        // Implementation to be added based on ProductImageService
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public ProductImageDto setPrimaryImage(Long productId, Long imageId) {
        // Implementation to be added based on ProductImageService
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    // =============================================
    // PRODUCT ATTRIBUTE OPERATIONS (Reserved for future use)
    // =============================

    // =============================================
    // UTILITY OPERATIONS
    // =============================================

    @Override
    public long getTotalProductCount() {
        return productRepository.count();
    }

    @Override
    public List<ProductDto> getAllEnabledProducts() {
        logger.debug("Getting all enabled products");
        return productRepository.findAllEnabledProducts()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getEnabledProductsByCategorySlug(String categorySlug) {
        logger.debug("Getting enabled products by category slug: {}", categorySlug);
        return productRepository.findEnabledProductsByCategorySlug(categorySlug)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDto> getEnabledProductsByCategorySlug(String categorySlug, Pageable pageable) {
        logger.debug("Getting enabled products by category slug with pagination: {}", categorySlug);
        Page<Product> productPage = productRepository.findEnabledProductsByCategorySlugPaginated(categorySlug, pageable);
        return productPage.map(this::convertToDto);
    }

    @Override
    public List<ProductDto> getEnabledProductsByTypeSlug(String typeSlug) {
        logger.debug("Getting enabled products by type slug: {}", typeSlug);
        return productRepository.findEnabledProductsByTypeSlug(typeSlug)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDto> getEnabledProductsByTypeSlug(String typeSlug, Pageable pageable) {
        logger.debug("Getting enabled products by type slug with pagination: {}", typeSlug);
        Page<Product> productPage = productRepository.findEnabledProductsByTypeSlugPaginated(typeSlug, pageable);
        return productPage.map(this::convertToDto);
    }

    @Override
    public Page<ProductDto> getFeaturedProducts(Pageable pageable) {
        logger.debug("Getting featured products with pagination");
        
        // First try to get featured products
        Page<ProductDto> featuredProducts = getProductsByCriteria(null, true, null, null, null, null, null, null, null, true, null, pageable);
        
        // If no featured products found, fallback to all enabled products
        if (featuredProducts.getTotalElements() == 0) {
            logger.debug("No featured products found, falling back to all enabled products");
            return getProductsByCriteria(null, true, null, null, null, null, null, null, null, null, null, pageable);
        }
        
        return featuredProducts;
    }

    @Override
    public Page<ProductDto> getHighlightedProducts(Pageable pageable) {
        logger.debug("Getting highlighted products with pagination");
        
        // First try to get highlighted products
        Page<ProductDto> highlightedProducts = getProductsByCriteria(null, true, null, null, null, null, null, null, null, null, true, pageable);
        
        // If no highlighted products found, fallback to all enabled products
        if (highlightedProducts.getTotalElements() == 0) {
            logger.debug("No highlighted products found, falling back to all enabled products");
            return getProductsByCriteria(null, true, null, null, null, null, null, null, null, null, null, pageable);
        }
        
        return highlightedProducts;
    }

    @Override
    public Page<ProductDto> getLatestProducts(Pageable pageable) {
        logger.debug("Getting latest products with pagination");
        
        // Get all enabled products sorted by creation date (this should always have results if any products exist)
        Page<ProductDto> latestProducts = getProductsByCriteria(null, true, null, null, null, null, null, null, null, null, null, pageable);
        
        // If somehow no enabled products found, this fallback won't help much, but log the issue
        if (latestProducts.getTotalElements() == 0) {
            logger.warn("No enabled products found in the system");
        }
        
        return latestProducts;
    }

    @Override
    public List<ProductDto> getProductsByCategoryId(Long categoryId) {
        logger.debug("Getting products by category ID: {}", categoryId);
        return productRepository.findByProductCategoryId(categoryId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getProductsByStateId(Long stateId) {
        logger.debug("Getting products by state ID: {}", stateId);
        return productRepository.findByProductStateId(stateId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getProductsInStock() {
        logger.debug("Getting products in stock");
        return productRepository.findProductsInStock()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDto> getTopSellingProducts(Pageable pageable) {
        logger.debug("Getting top selling products with pagination");
        
        // Use dedicated repository method to sort by soldQuantity DESC
        Page<Product> topSellingProducts = productRepository.findTopSellingProducts(pageable);
        
        // If no products found, log the issue
        if (topSellingProducts.getTotalElements() == 0) {
            logger.warn("No products found in the system for top selling query");
        }
        
        return topSellingProducts.map(this::convertToDto);
    }

    @Override
    public Long getProductCountByCategoryId(Long categoryId) {
        return productRepository.countByProductCategoryId(categoryId);
    }

    @Override
    public Long getProductCountByStateId(Long stateId) {
        return productRepository.countByProductStateId(stateId);
    }

    @Override
    public boolean existsBySlug(String productSlug) {
        return productRepository.existsByProductSlug(productSlug);
    }

    @Override
    public boolean existsByName(String productName) {
        return productRepository.existsByProductName(productName);
    }

    @Override
    public boolean existsByCode(String productCode) {
        return productRepository.existsByProductCode(productCode);
    }

    // =============================================
    // HELPER METHODS
    // =============================================

    private ProductDto convertToDto(Product product) {
        return ProductMapperUtil.toProductDto(product);
    }
    
    /**
     * Validate that all provided image IDs exist in the database
     * @param imageIds List of image IDs to validate
     * @throws IllegalArgumentException if any image ID doesn't exist
     */
    private void validateImageIds(List<Long> imageIds) {
        for (Long imageId : imageIds) {
            if (!imageRepository.existsById(imageId)) {
                throw new IllegalArgumentException("Image not found with ID: " + imageId);
            }
        }
    }
    
    /**
     * Associate images to a product and set primary image
     * @param product Product entity
     * @param imageIds List of image IDs to associate
     * @param primaryImageId ID of primary image (can be null)
     */
    private void associateImagesToProduct(Product product, List<Long> imageIds, Long primaryImageId) {
        logger.debug("Associating {} images to product {}", imageIds.size(), product.getProductId());
        
        // Determine primary image ID if not specified
        Long actualPrimaryImageId = (primaryImageId != null) ? primaryImageId : imageIds.get(0);
        
        for (Long imageId : imageIds) {
            Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with ID: " + imageId));
            
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setImage(image);
            productImage.setProductImagePrimary(imageId.equals(actualPrimaryImageId));
            
            productImageRepository.save(productImage);
        }
        
        logger.info("Successfully associated {} images to product {} (primary: {})", imageIds.size(), product.getProductId(), actualPrimaryImageId);
    }
    
    /**
     * Validate that all provided product attribute IDs exist in the database
     * @param productAttributes List of product attributes to validate  
     * @throws IllegalArgumentException if any productAttrId doesn't exist
     */
    private void validateProductAttributes(List<ProductAttributeRequestDto> productAttributes) {
        if (productAttributes == null || productAttributes.isEmpty()) {
            return;
        }
        
        for (ProductAttributeRequestDto attrRequest : productAttributes) {
            if (!productAttrRepository.existsById(attrRequest.getProductAttrId())) {
                throw new IllegalArgumentException("Product attribute definition not found with ID: " + attrRequest.getProductAttrId());
            }
        }
    }
    
    /**
     * Associate product attributes to a product
     * @param product Product entity
     * @param productAttributes List of product attributes to associate
     */
    private void associateAttributesToProduct(Product product, List<ProductAttributeRequestDto> productAttributes) {
        if (productAttributes == null || productAttributes.isEmpty()) {
            logger.debug("No attributes to associate with product {}", product.getProductId());
            return;
        }
        
        logger.debug("Associating {} attributes to product {}", productAttributes.size(), product.getProductId());
        
        for (ProductAttributeRequestDto attrRequest : productAttributes) {
            ProductAttr productAttr = productAttrRepository.findById(attrRequest.getProductAttrId())
                .orElseThrow(() -> new IllegalArgumentException("Product attribute definition not found with ID: " + attrRequest.getProductAttrId()));
            
            ProductAttribute productAttribute = new ProductAttribute();
            productAttribute.setProduct(product);
            productAttribute.setProductAttr(productAttr);
            productAttribute.setProductAttributeValue(attrRequest.getProductAttributeValue());
            productAttribute.setProductAttributeQuantity(attrRequest.getProductAttributeQuantity());
            productAttribute.setProductAttributeEnabled(attrRequest.getProductAttributeEnabled());
            
            productAttributeRepository.save(productAttribute);
        }
        
        logger.info("Successfully associated {} attributes to product {}", productAttributes.size(), product.getProductId());
    }
    
    /**
     * Update product images using differential approach to avoid duplicate key errors
     * Compares existing images with requested images and performs selective operations
     * @param product Product entity
     * @param imageIds New list of image IDs
     * @param primaryImageId Primary image ID (can be null)
     */
    private void updateProductImages(Product product, List<Long> imageIds, Long primaryImageId) {
        // Handle null input gracefully
        if (imageIds == null) {
            imageIds = List.of(); // Convert null to empty list
        }
        
        Long productId = product.getProductId();
        logger.debug("Updating product images for product {} - {} images requested", 
                    productId, imageIds.size());
        
        // 1. Get existing product images - handle possible null result
        List<ProductImage> existingImages = productImageRepository.findByProductId(productId);
        if (existingImages == null) {
            existingImages = List.of(); // Convert null to empty list
        }
        
        // 2. Create sets for comparison
        Set<Long> existingImageIds = existingImages.stream()
            .map(pi -> pi.getImage().getImageId())
            .collect(Collectors.toSet());
            
        Set<Long> newImageIds = new HashSet<>(imageIds);
        
        // 3. REMOVE: Delete images that are not in the new list
        for (ProductImage existingImage : existingImages) {
            Long existingImageId = existingImage.getImage().getImageId();
            if (!newImageIds.contains(existingImageId)) {
                productImageRepository.delete(existingImage);
                logger.debug("Removed image {} from product {}", existingImageId, productId);
            }
        }
        
        // 4. ADD: Insert new images that don't exist
        Long actualPrimaryImageId = (primaryImageId != null) ? primaryImageId : 
                                   (!imageIds.isEmpty() ? imageIds.get(0) : null);
        
        for (Long imageId : newImageIds) {
            if (!existingImageIds.contains(imageId)) {
                Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new IllegalArgumentException("Image not found with ID: " + imageId));
                    
                ProductImage productImage = new ProductImage();
                productImage.setProduct(product);
                productImage.setImage(image);
                productImage.setProductImagePrimary(imageId.equals(actualPrimaryImageId));
                
                productImageRepository.save(productImage);
                logger.debug("Added image {} to product {} (primary: {})", imageId, productId, imageId.equals(actualPrimaryImageId));
            }
        }
        
        // 5. UPDATE: Update primary flags for existing images if needed
        if (actualPrimaryImageId != null) {
            List<ProductImage> currentImages = productImageRepository.findByProductId(productId);
            for (ProductImage img : currentImages) {
                boolean shouldBePrimary = img.getImage().getImageId().equals(actualPrimaryImageId);
                if (img.getProductImagePrimary() != shouldBePrimary) {
                    img.setProductImagePrimary(shouldBePrimary);
                    productImageRepository.save(img);
                    logger.debug("Updated primary flag for image {} in product {}: {}", 
                               img.getImage().getImageId(), productId, shouldBePrimary);
                }
            }
        }
        
        logger.info("Successfully updated images for product {} - {} total images", productId, newImageIds.size());
    }
    
    /**
     * Update product attributes using differential approach to avoid duplicate key errors  
     * Compares existing attributes with requested attributes and performs selective operations
     * @param product Product entity
     * @param requestAttributes List of requested product attributes
     */
    private void updateProductAttributes(Product product, List<ProductAttributeRequestDto> requestAttributes) {
        if (requestAttributes == null) {
            requestAttributes = List.of(); // Convert null to empty list for consistent processing
        }
        
        Long productId = product.getProductId();
        logger.debug("Updating product attributes for product {} - {} attributes requested", 
                    productId, requestAttributes.size());
        
        // 1. Get existing attributes from DB - handle possible null result
        List<ProductAttribute> existing = productAttributeRepository.findByProductId(productId);
        if (existing == null) {
            existing = List.of(); // Convert null to empty list
        }
        
        // 2. Map existing by productAttrId for efficient lookup
        Map<Long, ProductAttribute> existingMap = existing.stream()
            .collect(Collectors.toMap(
                pa -> pa.getProductAttr().getProductAttrId(),
                Function.identity()
            ));
        
        // 3. Map request by productAttrId and validate for duplicates
        Map<Long, ProductAttributeRequestDto> requestMap = requestAttributes.stream()
            .collect(Collectors.toMap(
                ProductAttributeRequestDto::getProductAttrId,
                Function.identity(),
                (a, b) -> {
                    throw new IllegalArgumentException(
                        "Duplicate productAttrId in request: " + a.getProductAttrId());
                }
            ));
        
        // 4. REMOVE: Delete attributes that exist in DB but not in request
        for (ProductAttribute existingAttr : existing) {
            Long attrId = existingAttr.getProductAttr().getProductAttrId();
            if (!requestMap.containsKey(attrId)) {
                productAttributeRepository.delete(existingAttr);
                logger.debug("Removed attribute {} from product {}", attrId, productId);
            }
        }
        
        // 5. ADD or UPDATE: Process each requested attribute
        for (Map.Entry<Long, ProductAttributeRequestDto> entry : requestMap.entrySet()) {
            Long attrId = entry.getKey();
            ProductAttributeRequestDto req = entry.getValue();
            
            ProductAttribute existingAttr = existingMap.get(attrId);
            
            if (existingAttr != null) {
                // UPDATE existing attribute
                existingAttr.setProductAttributeValue(req.getProductAttributeValue());
                existingAttr.setProductAttributeQuantity(req.getProductAttributeQuantity());
                existingAttr.setProductAttributeEnabled(req.getProductAttributeEnabled());
                
                productAttributeRepository.save(existingAttr);
                logger.debug("Updated attribute {} for product {}: {} (qty: {})", 
                           attrId, productId, req.getProductAttributeValue(), req.getProductAttributeQuantity());
            } else {
                // INSERT new attribute
                ProductAttr productAttr = productAttrRepository.findById(attrId)
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Product attribute definition not found with ID: " + attrId));
                
                ProductAttribute newAttr = new ProductAttribute();
                newAttr.setProduct(product);
                newAttr.setProductAttr(productAttr);
                newAttr.setProductAttributeValue(req.getProductAttributeValue());
                newAttr.setProductAttributeQuantity(req.getProductAttributeQuantity());
                newAttr.setProductAttributeEnabled(req.getProductAttributeEnabled());
                
                productAttributeRepository.save(newAttr);
                logger.debug("Added new attribute {} to product {}: {} (qty: {})", 
                           attrId, productId, req.getProductAttributeValue(), req.getProductAttributeQuantity());
            }
        }
        
        logger.info("Successfully updated attributes for product {} - {} total attributes", 
                   productId, requestMap.size());
    }
    
    // =============================================
    // SEO META HELPER METHODS
    // =============================================
    
    /**
     * Handle SEO meta creation or update based on existing seoMetaId
     * @param seoMetaRequest SEO meta request data
     * @param existingSeoMetaId Existing SEO meta ID (null for creation)
     * @return SEO meta ID (existing or newly created)
     */
    private Long handleSeoMeta(SeoMetaRequestDto seoMetaRequest, Long existingSeoMetaId) {
        if (existingSeoMetaId != null) {
            // Update existing SEO meta
            logger.debug("Updating existing SEO meta with ID: {}", existingSeoMetaId);
            try {
                SeoMetaDto seoMetaDto = convertSeoMetaRequestToDto(seoMetaRequest);
                seoMetaService.updateSeoMeta(existingSeoMetaId, seoMetaDto);
                logger.info("Successfully updated SEO meta ID: {}", existingSeoMetaId);
                return existingSeoMetaId;
            } catch (Exception e) {
                logger.error("Failed to update SEO meta: {}", e.getMessage(), e);
                throw new IllegalArgumentException("Failed to update SEO meta: " + e.getMessage(), e);
            }
        } else {
            // Create new SEO meta
            logger.debug("Creating new SEO meta with title: {}", seoMetaRequest.getSeoMetaTitle());
            try {
                SeoMetaDto seoMetaDto = convertSeoMetaRequestToDto(seoMetaRequest);
                SeoMetaDto createdSeoMeta = seoMetaService.createSeoMeta(seoMetaDto);
                logger.info("Successfully created SEO meta with ID: {}", createdSeoMeta.getSeoMetaId());
                return createdSeoMeta.getSeoMetaId();
            } catch (Exception e) {
                logger.error("Failed to create SEO meta: {}", e.getMessage(), e);
                throw new IllegalArgumentException("Failed to create SEO meta: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Convert SeoMetaRequestDto to SeoMetaDto with consistent field mapping
     * @param seoMetaRequest Request DTO
     * @return SeoMetaDto for service operations
     */
    private SeoMetaDto convertSeoMetaRequestToDto(SeoMetaRequestDto seoMetaRequest) {
        return SeoMetaDto.builder()
                .seoMetaTitle(seoMetaRequest.getSeoMetaTitle())
                .seoMetaDescription(seoMetaRequest.getSeoMetaDescription())
                .seoMetaKeywords(seoMetaRequest.getSeoMetaKeywords())
                .seoMetaCanonicalUrl(seoMetaRequest.getSeoMetaCanonicalUrl())
                .seoMetaIndex(seoMetaRequest.getSeoMetaIndex())
                .seoMetaFollow(seoMetaRequest.getSeoMetaFollow())
                .seoMetaSchemaType(seoMetaRequest.getSeoMetaSchemaType())
                .seoMetaCustomJson(seoMetaRequest.getSeoMetaCustomJson())
                .seoMetaEnabled(seoMetaRequest.getSeoMetaEnabled())
                .build();
    }
}