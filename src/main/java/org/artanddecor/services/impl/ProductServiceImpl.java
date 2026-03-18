package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductDto;
import org.artanddecor.dto.ProductCreateDto;
import org.artanddecor.dto.ProductImageDto;
import org.artanddecor.dto.ProductAttributeDto;
import org.artanddecor.model.Product;
import org.artanddecor.repository.ProductRepository;
import org.artanddecor.services.ProductService;
import org.artanddecor.utils.ProductMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    public ProductDto createProduct(ProductCreateDto productCreateDto) {
        logger.info("Creating new product from create DTO: {}", productCreateDto.getProductName());
        
        // Validation
        if (existsBySlug(productCreateDto.getProductSlug())) {
            throw new IllegalArgumentException("Product slug already exists: " + productCreateDto.getProductSlug());
        }
        if (existsByName(productCreateDto.getProductName())) {
            throw new IllegalArgumentException("Product name already exists: " + productCreateDto.getProductName());
        }
        if (existsByCode(productCreateDto.getProductCode())) {
            throw new IllegalArgumentException("Product code already exists: " + productCreateDto.getProductCode());
        }
        
        Product product = ProductMapperUtil.toProductEntityFromCreateDto(productCreateDto);
        Product savedProduct = productRepository.save(product);
        
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
        // Implementation to be added based on ProductImageService
        throw new UnsupportedOperationException("Method not yet implemented");
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
    // PRODUCT ATTRIBUTE OPERATIONS
    // =============================================

    @Override
    public ProductAttributeDto addAttributeToProduct(Long productId, Long attrId, String attrValue) {
        // Implementation to be added based on ProductAttributeService
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public void removeAttributeFromProduct(Long productId, Long attrId) {
        // Implementation to be added based on ProductAttributeService
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public List<ProductAttributeDto> getProductAttributes(Long productId) {
        // Implementation to be added based on ProductAttributeService
        throw new UnsupportedOperationException("Method not yet implemented");
    }

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
}