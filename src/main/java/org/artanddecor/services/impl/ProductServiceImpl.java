package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductDto;
import org.artanddecor.dto.ProductRequestDto;
import org.artanddecor.dto.ProductImageDto;
import org.artanddecor.dto.ProductAttributeDto;
import org.artanddecor.model.Product;
import org.artanddecor.model.ProductImage;
import org.artanddecor.model.Image;
import org.artanddecor.repository.ProductRepository;
import org.artanddecor.repository.ProductImageRepository;
import org.artanddecor.repository.ImageRepository;
import org.artanddecor.services.ProductService;
import org.artanddecor.services.ProductAttributeService;
import org.artanddecor.utils.ProductMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final ImageRepository imageRepository;
    private final ProductAttributeService productAttributeService;

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
    // CRUD OPERATIONS
    // =============================================

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        logger.info("Creating new product: {}", productDto.getProductName());
        
        // Validation
        if (existsBySlug(productDto.getProductSlug())) {
            throw new IllegalArgumentException("Product slug already exists: " + productDto.getProductSlug());
        }
        if (existsByName(productDto.getProductName())) {
            throw new IllegalArgumentException("Product name already exists: " + productDto.getProductName());
        }
        if (existsByCode(productDto.getProductCode())) {
            throw new IllegalArgumentException("Product code already exists: " + productDto.getProductCode());
        }
        
        Product product = convertToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        
        return convertToDto(savedProduct);
    }
    @Override
    @Transactional
    public ProductDto updateProduct(Long productId, ProductRequestDto productRequestDto) {
        logger.info("Updating product ID: {} with request DTO and {} images", productId, 
                   productRequestDto.getImageIds() != null ? productRequestDto.getImageIds().size() : 0);
        
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
        
        // Update product fields
        ProductMapperUtil.updateProductEntityFromRequestDto(existingProduct, productRequestDto);
        Product updatedProduct = productRepository.save(existingProduct);
        
        // Update images if provided
        if (productRequestDto.getImageIds() != null) {
            // Remove existing images
            productImageRepository.deleteByProductProductId(productId);
            
            // Associate new images if list is not empty
            if (!productRequestDto.getImageIds().isEmpty()) {
                associateImagesToProduct(updatedProduct, productRequestDto.getImageIds(), productRequestDto.getPrimaryImageId());
            }
        }
        
        return convertToDto(updatedProduct);
    }    
    @Override
    @Transactional
    public ProductDto createProduct(ProductRequestDto productRequestDto) {
        logger.info("Creating new product from request DTO: {} with {} images", 
                   productRequestDto.getProductName(), 
                   productRequestDto.getImageIds() != null ? productRequestDto.getImageIds().size() : 0);
        
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
        
        Product product = ProductMapperUtil.toProductEntityFromRequestDto(productRequestDto);
        Product savedProduct = productRepository.save(product);
        
        // Associate images if provided
        if (productRequestDto.getImageIds() != null && !productRequestDto.getImageIds().isEmpty()) {
            associateImagesToProduct(savedProduct, productRequestDto.getImageIds(), productRequestDto.getPrimaryImageId());
        }
        
        return convertToDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        logger.info("Updating product ID: {}", productId);
        
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        
        // Validation - check if slug/name/code exists for other records
        if (!existingProduct.getProductSlug().equals(productDto.getProductSlug()) && 
            existsBySlug(productDto.getProductSlug())) {
            throw new IllegalArgumentException("Product slug already exists: " + productDto.getProductSlug());
        }
        if (!existingProduct.getProductName().equals(productDto.getProductName()) && 
            existsByName(productDto.getProductName())) {
            throw new IllegalArgumentException("Product name already exists: " + productDto.getProductName());
        }
        if (!existingProduct.getProductCode().equals(productDto.getProductCode()) && 
            existsByCode(productDto.getProductCode())) {
            throw new IllegalArgumentException("Product code already exists: " + productDto.getProductCode());
        }
        
        // Update fields
        existingProduct.setProductName(productDto.getProductName());
        existingProduct.setProductSlug(productDto.getProductSlug());
        existingProduct.setProductCode(productDto.getProductCode());
        existingProduct.setProductDescription(productDto.getProductDescription());
        existingProduct.setProductPrice(productDto.getProductPrice());
        existingProduct.setStockQuantity(productDto.getStockQuantity());
        existingProduct.setSoldQuantity(productDto.getSoldQuantity());
        existingProduct.setProductEnabled(productDto.getProductEnabled());
        existingProduct.setModifiedDt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(existingProduct);
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

    private Product convertToEntity(ProductDto productDto) {
        return ProductMapperUtil.toProductEntity(productDto);
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
}