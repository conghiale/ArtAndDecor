package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductDto;
import org.artanddecor.dto.ProductRequestDto;
import org.artanddecor.dto.ProductVariantRequestDto;
import org.artanddecor.dto.ProductVariantDto;
import org.artanddecor.dto.ProductImageDto;
import org.artanddecor.dto.SeoMetaRequestDto;
import org.artanddecor.dto.SeoMetaDto;
import org.artanddecor.dto.SimilarImageSearchResponseDto;
import org.artanddecor.dto.SimilarImageResultDto;
import org.artanddecor.dto.PolicyDto;
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
import org.artanddecor.services.ProductVariantService;
import org.artanddecor.services.SeoMetaService;
import org.artanddecor.services.PolicyService;
import org.artanddecor.utils.ProductMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.ByteArrayResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Arrays;
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
    private final ProductVariantService productVariantService;
    private final SeoMetaService seoMetaService;
    private final PolicyService policyService;
    private final RestTemplate restTemplate;

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
                                                Boolean featured, Boolean highlighted, String productCategorySlug, String productTypeSlug, Pageable pageable) {
        logger.debug("Getting products with criteria - textSearch: {}, enabled: {}, categoryId: {}, typeId: {}, stateId: {}, featured: {}, highlighted: {}, productCategorySlug: {}, productTypeSlug: {}", 
                    textSearch, enabled, categoryId, typeId, stateId, featured, highlighted, productCategorySlug, productTypeSlug);
        
        Page<Product> productPage = productRepository.findProductsByCriteriaPaginated(
            textSearch, enabled, categoryId, typeId, stateId, minPrice, maxPrice, inStock, productCode, 
            featured, highlighted, productCategorySlug, productTypeSlug, pageable);
        
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

    // =============================================
    // CRUD OPERATIONS - Unified ProductRequestDto approach
    // =============================================


    @Override
    @Transactional
    public ProductDto updateProduct(Long productId, ProductRequestDto productRequestDto) {
        logger.info("Updating product ID: {} with request DTO and {} images and {} variants", productId, 
                   productRequestDto.getImageIds() != null ? productRequestDto.getImageIds().size() : 0,
                   productRequestDto.getProductVariants() != null ? productRequestDto.getProductVariants().size() : 0);
        
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
        
        // Validate product variants if provided
        validateProductVariants(productRequestDto.getProductVariants());
        
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
        
        // Update variants if provided - NEW functionality
        if (productRequestDto.getProductVariants() != null) {
            updateProductVariants(updatedProduct, productRequestDto.getProductVariants());
        }
        
        return convertToDto(updatedProduct);
    }

    // =============================================
    @Override
    @Transactional
    public ProductDto createProduct(ProductRequestDto productRequestDto) {
        logger.info("Creating new product from request DTO: {} with {} images and {} variants", 
                   productRequestDto.getProductName(), 
                   productRequestDto.getImageIds() != null ? productRequestDto.getImageIds().size() : 0,
                   productRequestDto.getProductVariants() != null ? productRequestDto.getProductVariants().size() : 0);
        
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
        
        // Validate product variants if provided
        validateProductVariants(productRequestDto.getProductVariants());
        
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
        
        // Associate variants if provided - NEW functionality 
        if (productRequestDto.getProductVariants() != null && !productRequestDto.getProductVariants().isEmpty()) {
            associateVariantsToProduct(savedProduct, productRequestDto.getProductVariants());
        }
        
        return convertToDto(savedProduct);
    }

    // =============================================
    // PRODUCT IMAGE OPERATIONS
    // =============================================

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
    public Page<ProductDto> getFeaturedProducts(Pageable pageable) {
        logger.debug("Getting featured products with pagination");
        
        // First try to get featured products
        Page<ProductDto> featuredProducts = getProductsByCriteria(null, true, null, null, null, null, null, null, null, true, null, null, null, pageable);
        
        // If no featured products found, fallback to all enabled products
        if (featuredProducts.getTotalElements() == 0) {
            logger.debug("No featured products found, falling back to all enabled products");
            return getProductsByCriteria(null, true, null, null, null, null, null, null, null, null, null, null, null, pageable);
        }
        
        return featuredProducts;
    }

    @Override
    public Page<ProductDto> getHighlightedProducts(Pageable pageable) {
        logger.debug("Getting highlighted products with pagination");
        
        // First try to get highlighted products
        Page<ProductDto> highlightedProducts = getProductsByCriteria(null, true, null, null, null, null, null, null, null, null, true, null, null, pageable);
        
        // If no highlighted products found, fallback to all enabled products
        if (highlightedProducts.getTotalElements() == 0) {
            logger.debug("No highlighted products found, falling back to all enabled products");
            return getProductsByCriteria(null, true, null, null, null, null, null, null, null, null, null, null, null, pageable);
        }
        
        return highlightedProducts;
    }

    @Override
    public Page<ProductDto> getLatestProducts(Pageable pageable) {
        logger.debug("Getting latest products with pagination");
        
        // Get all enabled products sorted by creation date (this should always have results if any products exist)
        Page<ProductDto> latestProducts = getProductsByCriteria(null, true, null, null, null, null, null, null, null, null, null, null, null, pageable);
        
        // If somehow no enabled products found, this fallback won't help much, but log the issue
        if (latestProducts.getTotalElements() == 0) {
            logger.warn("No enabled products found in the system");
        }
        
        return latestProducts;
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
     * Validate that all provided product variant data is correct
     * @param productVariants List of product variants to validate  
     * @throws IllegalArgumentException if any productAttributeId doesn't exist or data is invalid
     */
    private void validateProductVariants(List<ProductVariantRequestDto> productVariants) {
        if (productVariants == null || productVariants.isEmpty()) {
            return;
        }
        
        for (ProductVariantRequestDto variantRequest : productVariants) {
            // Validate productAttributeId exists
            if (!productAttributeRepository.existsById(variantRequest.getProductAttributeId())) {
                throw new IllegalArgumentException("Product attribute not found with ID: " + variantRequest.getProductAttributeId());
            }
            
            // Validate quantity is not negative
            if (variantRequest.getProductVariantStock() < 0) {
                throw new IllegalArgumentException("Product variant quantity cannot be negative");
            }
        }
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
     * Associate product variants to a product after creation
     * @param product Product entity
     * @param productVariants List of product variants to associate
     */
    private void associateVariantsToProduct(Product product, List<ProductVariantRequestDto> productVariants) {
        logger.debug("Associating {} variants to product {}", productVariants.size(), product.getProductId());
        
        for (ProductVariantRequestDto variantRequest : productVariants) {
            // Set productId for the variant request
            variantRequest.setProductId(product.getProductId());
            
            // Create the variant using ProductVariantService
            productVariantService.createProductVariant(variantRequest);
        }
        
        logger.debug("Successfully associated {} variants to product {}", productVariants.size(), product.getProductId());
    }
    
    /**
     * Update product variants using differential approach
     * Removes existing variants and recreates them based on new data
     * @param product Product entity
     * @param productVariants New list of product variants
     */
    private void updateProductVariants(Product product, List<ProductVariantRequestDto> productVariants) {
        logger.debug("Updating variants for product {} - {} variants requested", 
                    product.getProductId(), productVariants.size());
        
        Long productId = product.getProductId();
        
        // 1. Remove all existing variants for this product
        List<ProductVariantDto> existingVariants = productVariantService.findVariantsByProductId(productId);
        for (ProductVariantDto existingVariant : existingVariants) {
            productVariantService.deleteProductVariant(existingVariant.getProductVariantId());
        }
        logger.debug("Removed {} existing variants for product {}", existingVariants.size(), productId);
        
        // 2. Create new variants
        if (!productVariants.isEmpty()) {
            for (ProductVariantRequestDto variantRequest : productVariants) {
                // Set productId for the variant request
                variantRequest.setProductId(productId);
                
                // Create the variant using ProductVariantService
                productVariantService.createProductVariant(variantRequest);
            }
            logger.debug("Created {} new variants for product {}", productVariants.size(), productId);
        }
    }
    
    /**
     * Convert SeoMetaRequestDto to SeoMetaDto with consistent field mapping
     * @param seoMetaRequest Request DTO containing SEO metadata
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

    // =============================================
    // AI SIMILAR IMAGE SEARCH OPERATIONS
    // =============================================

    @Override
    public Page<ProductDto> searchProductsBySimilarImage(MultipartFile imageFile, Boolean isSelling, Pageable pageable) throws Exception {
        logger.info("Searching products by similar image, isSelling: {}, file size: {} bytes", isSelling, imageFile.getSize());
        
        // 1. Get AI service configuration from policy
        PolicyDto aiConfig = policyService.findPolicyByName("SIMILAR_IMAGE_CONFIG")
                .orElseThrow(() -> new IllegalArgumentException("SIMILAR_IMAGE_CONFIG policy not found"));
        
        if (!aiConfig.getPolicyEnabled()) {
            throw new IllegalArgumentException("SIMILAR_IMAGE_CONFIG policy is disabled");
        }
        
        // 2. Parse configuration properties
        Map<String, String> configProperties = parseConfigProperties(aiConfig.getPolicyValue());
        String host = configProperties.get("host");
        String thresholdStr = configProperties.get("threshold");
        String topKStr = configProperties.get("top_k");
        
        if (host == null || thresholdStr == null || topKStr == null) {
            throw new IllegalArgumentException("Invalid SIMILAR_IMAGE_CONFIG: missing required properties (host, threshold, top_k)");
        }
        
        double threshold = Double.parseDouble(thresholdStr);
        int topK = Integer.parseInt(topKStr);
        
        // 3. Call AI service
        SimilarImageSearchResponseDto aiResponse = callAiImageSearchService(host, imageFile, threshold, topK);
        
        if (aiResponse.getResults() == null || aiResponse.getResults().isEmpty()) {
            logger.info("No similar images found from AI service");
            return Page.empty(pageable);
        }
        
        // 4. Extract image IDs from AI response
        List<Long> imageIds = aiResponse.getResults().stream()
                .map(SimilarImageResultDto::getId)
                .collect(Collectors.toList());
        
        if (imageIds.isEmpty()) {
            logger.info("No image IDs extracted from AI response");
            return Page.empty(pageable);
        }
        
        logger.info("Found {} similar images from AI service, searching products...", imageIds.size());
        
        // 5. Find products by image IDs with selling filter
        Page<Product> productPage = productRepository.findProductsByImageIdsWithSellingFilter(imageIds, isSelling, pageable);
        
        // 6. Convert to DTOs and return
        return productPage.map(this::convertToDto);
    }
    
    /**
     * Parse configuration properties from policy value
     * Expected format: key1=value1\nkey2=value2\n...
     * @param policyValue Policy value string
     * @return Map of configuration properties
     */
    private Map<String, String> parseConfigProperties(String policyValue) {
        return Arrays.stream(policyValue.split("\\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty() && line.contains("="))
                .map(line -> line.split("=", 2))
                .collect(Collectors.toMap(
                    parts -> parts[0].trim(),
                    parts -> parts.length > 1 ? parts[1].trim() : ""
                ));
    }
    
    /**
     * Call AI service for image search
     * @param host AI service host URL
     * @param imageFile Image file to search
     * @param threshold Similarity threshold
     * @param topK Number of top results to return
     * @return AI service response
     * @throws Exception if call fails
     */
    private SimilarImageSearchResponseDto callAiImageSearchService(String host, MultipartFile imageFile, double threshold, int topK) throws Exception {
        String url = host + "/api/v1/search";
        
        // Prepare request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        // Prepare multipart request body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(imageFile.getBytes()) {
            @Override
            public String getFilename() {
                return imageFile.getOriginalFilename();
            }
        });
        body.add("threshold", threshold);
        body.add("top_k", topK);
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        try {
            logger.debug("Calling AI service at: {} with threshold: {}, top_k: {}", url, threshold, topK);
            ResponseEntity<SimilarImageSearchResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                SimilarImageSearchResponseDto.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                response.getBody();
                logger.debug("AI service call successful, found {} results",
                        response.getBody().getResults() != null ?
                        response.getBody().getResults().size() : 0);
                return response.getBody();
            } else {
                throw new Exception("AI service call failed with status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Failed to call AI service at {}: {}", url, e.getMessage());
            throw new Exception("Failed to call AI service: " + e.getMessage(), e);
        }
    }
}