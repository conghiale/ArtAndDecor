package org.artanddecor.controllers;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.*;
import org.artanddecor.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Product Management REST Controller
 * Comprehensive API for managing products, categories, types, states, attributes and images
 * Follows the same pattern as ImageController with customer and admin endpoints
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Comprehensive APIs for product management including products, categories, types, states, attributes and images")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final ProductTypeService productTypeService;
    private final ProductCategoryService productCategoryService;
    private final ProductStateService productStateService;
    private final ProductAttrService productAttrService;
    private final ProductAttributeService productAttributeService;

    /*=============================================
     PRODUCT ENDPOINTS
     =============================================*/

    /**
     * Get product by slug (Customer-friendly endpoint)
     */
    @GetMapping("/slug/{productSlug}")
    @Operation(
        summary = "Get product by URL-friendly slug",
        description = "Retrieve detailed product information using URL-friendly slug. This is a customer-facing endpoint that provides comprehensive product details including images, attributes, and reviews. No authentication required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product retrieved successfully", 
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
        @ApiResponse(responseCode = "404", description = "Product not found with the specified slug"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving product")
    })
    public ResponseEntity<BaseResponseDto<ProductDto>> getProductBySlug(
            @Parameter(description = "URL-friendly product identifier (e.g., 'modern-abstract-art-001')", 
                      example = "modern-abstract-art-001") 
            @PathVariable String productSlug) {
        logger.info("Requesting product by slug: {}", productSlug);
        try {
            Optional<ProductDto> product = productService.findProductBySlug(productSlug);
            return product.map(productDto -> ResponseEntity.ok(BaseResponseDto.success("Product retrieved successfully", productDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound("Product not found with slug: " + productSlug)));
        } catch (Exception e) {
            logger.error("Error retrieving product by slug {}: {}", productSlug, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve product: " + e.getMessage()));
        }
    }

    /**
     * Get product by ID (Admin/System reference)
     */
    @GetMapping("/{productId}")
    @Operation(
        summary = "Get product by database ID",
        description = "Retrieve detailed product information using database ID. This is an admin/system endpoint used for management operations. Accessible by authenticated users."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
        @ApiResponse(responseCode = "404", description = "Product not found with the specified ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving product")
    })
    public ResponseEntity<BaseResponseDto<ProductDto>> getProductById(
            @Parameter(description = "Database product identifier (positive integer)", 
                      example = "1") 
            @PathVariable Long productId) {
        logger.info("Admin requesting product by ID: {}", productId);
        try {
            Optional<ProductDto> product = productService.findProductById(productId);
            return product.map(productDto -> ResponseEntity.ok(BaseResponseDto.success("Product retrieved successfully", productDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound("Product not found with ID: " + productId)));
        } catch (Exception e) {
            logger.error("Error retrieving product by ID {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve product: " + e.getMessage()));
        }
    }

    /**
     * Get products with advanced filtering and pagination
     */
    @GetMapping("/search")
    @Operation(
        summary = "Get products with advanced filtering and pagination",
        description = "Retrieve products with comprehensive filtering options including text search, price range, stock status, category, state, and special flags (featured/highlighted). Returns paginated results with sorting options. Use enabled=true to get all enabled products."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully with pagination info"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters or pagination settings"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while searching products")
    })
    public ResponseEntity<BaseResponseDto<Page<ProductDto>>> getProductsByCriteria(
            @Parameter(description = "Search text across product fields (name, code, description, remark)", 
                      example = "abstract art") 
            @RequestParam(value = "textSearch", required = false) String textSearch,
            
            @Parameter(description = "Filter by enabled status. Use enabled=true to get all enabled products", 
                      example = "true") 
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            
            @Parameter(description = "Filter by category ID from PRODUCT_CATEGORY table", 
                      example = "1") 
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            
            @Parameter(description = "Filter by type ID from PRODUCT_TYPE table", 
                      example = "1") 
            @RequestParam(value = "typeId", required = false) Long typeId,
            
            @Parameter(description = "Filter by state ID from PRODUCT_STATE table (1=Active, 2=Inactive, etc.)", 
                      example = "1") 
            @RequestParam(value = "stateId", required = false) Long stateId,
            
            @Parameter(description = "Minimum price filter in VND (Vietnamese Dong)", 
                      example = "100000") 
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            
            @Parameter(description = "Maximum price filter in VND (Vietnamese Dong)", 
                      example = "5000000") 
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            
            @Parameter(description = "Filter by stock availability (true=in stock, false=out of stock)", 
                      example = "true") 
            @RequestParam(value = "inStock", required = false) Boolean inStock,
            
            @Parameter(description = "Filter by product code (partial match supported)", 
                      example = "ART-001") 
            @RequestParam(value = "productCode", required = false) String productCode,
            
            @Parameter(description = "Filter by featured products (true=featured only, false=non-featured only)", 
                      example = "true") 
            @RequestParam(value = "featured", required = false) Boolean featured,
            
            @Parameter(description = "Filter by highlighted products (true=highlighted only, false=non-highlighted only)", 
                      example = "true") 
            @RequestParam(value = "highlighted", required = false) Boolean highlighted,
            
            @Parameter(description = "Pagination settings: page number (0-based), size, sort field, and direction") 
            @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        logger.info("Getting products with criteria - textSearch: {}, enabled: {}, categoryId: {}, typeId: {}, stateId: {}, featured: {}, highlighted: {}", 
                   textSearch, enabled, categoryId, typeId, stateId, featured, highlighted);
        
        try {
            Page<ProductDto> productsPage = productService.getProductsByCriteria(
                textSearch, enabled, categoryId, typeId, stateId, minPrice, maxPrice, inStock, productCode, 
                featured, highlighted, pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Products retrieved successfully", productsPage));
        } catch (Exception e) {
            logger.error("Error getting products: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get products: " + e.getMessage()));
        }
    }

    /**
     * Create new product (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Create new product with images", 
        description = "Create a new product in the system using simplified input with IDs. Supports uploading multiple images by providing imageIds array. Client should upload images first and include the received image IDs. This operation is restricted to users with ADMIN or MANAGER roles."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully with images",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid product data, image IDs, or validation errors"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN or MANAGER role required"),
        @ApiResponse(responseCode = "409", description = "Product with same name, slug, or code already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while creating product")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ProductDto>> createProduct(
            @Parameter(description = "Product data to create with IDs for foreign keys and optional imageIds array for associating images") 
            @Valid @RequestBody ProductRequestDto productRequestDto) {
        logger.info("Creating new product: {} with {} images", productRequestDto.getProductName(), 
                   productRequestDto.getImageIds() != null ? productRequestDto.getImageIds().size() : 0);
        try {
            ProductDto createdProduct = productService.createProduct(productRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.success(
                "Product created successfully" + (productRequestDto.getImageIds() != null && !productRequestDto.getImageIds().isEmpty() ? 
                " with " + productRequestDto.getImageIds().size() + " images" : ""), 
                createdProduct));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating product: {}", e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating product: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to create product: " + e.getMessage()));
        }
    }

    /**
     * Update existing product with images (Admin only)
     */
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Update existing product with images", 
        description = "Update product information using simplified DTO with IDs. Supports updating associated images by providing imageIds array. Client should upload new images first if needed. Admin/Manager access required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully with images",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid product data, image IDs, or validation errors"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN or MANAGER role required"),
        @ApiResponse(responseCode = "404", description = "Product not found with specified ID"),
        @ApiResponse(responseCode = "409", description = "Product with same name, slug, or code already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while updating product")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ProductDto>> updateProduct(
            @Parameter(description = "Product database ID", example = "1") 
            @PathVariable Long productId, 
            @Parameter(description = "Product data to update with IDs for foreign keys and optional imageIds array for updating associated images") 
            @Valid @RequestBody ProductRequestDto productRequestDto) {
        logger.info("Updating product ID: {} with {} images", productId, 
                   productRequestDto.getImageIds() != null ? productRequestDto.getImageIds().size() : 0);
        try {
            ProductDto updatedProduct = productService.updateProduct(productId, productRequestDto);
            return ResponseEntity.ok(BaseResponseDto.success(
                "Product updated successfully" + (productRequestDto.getImageIds() != null && !productRequestDto.getImageIds().isEmpty() ? 
                " with " + productRequestDto.getImageIds().size() + " images" : ""), 
                updatedProduct));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating product {}: {}", productId, e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating product {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update product: " + e.getMessage()));
        }
    }
    
    /**
     * Get total product count (Admin dashboard)
     */
    @GetMapping("/stats/count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get total product count", description = "Get total number of products for admin dashboard. Admin/Manager access required.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Long>> getTotalProductCount() {
        logger.info("Getting total product count");
        try {
            long count = productService.getTotalProductCount();
            return ResponseEntity.ok(BaseResponseDto.success("Total product count retrieved successfully", count));
        } catch (Exception e) {
            logger.error("Error getting total product count: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get total product count: " + e.getMessage()));
        }
    }

    /**
     * Get products in stock
     */
    @GetMapping("/in-stock")
    @Operation(summary = "Get products in stock", description = "Get all products that are currently in stock")
    public ResponseEntity<BaseResponseDto<List<ProductDto>>> getProductsInStock() {
        logger.info("Getting products in stock");
        try {
            List<ProductDto> products = productService.getProductsInStock();
            return ResponseEntity.ok(BaseResponseDto.success("In-stock products retrieved successfully", products));
        } catch (Exception e) {
            logger.error("Error getting in-stock products: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get in-stock products: " + e.getMessage()));
        }
    }

    /**
     * Get top selling products
     */
    @GetMapping("/top-selling")
    @Operation(
        summary = "Get top selling products with pagination",
        description = "Retrieve products sorted by sales volume (soldQuantity) in descending order. Products with higher sales are listed first. Products with same sales count are sorted by creation date (newest first). Returns enabled products only. No authentication required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top selling products retrieved successfully with pagination info"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),  
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving top selling products")
    })
    public ResponseEntity<BaseResponseDto<Page<ProductDto>>> getTopSellingProducts(
            @Parameter(description = "Pagination settings: page number (0-based), size, sort field will be ignored as sorting is by soldQuantity DESC") 
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        logger.info("Getting top selling products");
        try {
            Page<ProductDto> topProducts = productService.getTopSellingProducts(pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Top selling products retrieved successfully", topProducts));
        } catch (Exception e) {
            logger.error("Error getting top selling products: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get top selling products: " + e.getMessage()));
        }
    }

    /**
     * Get featured products with pagination
     */
    @GetMapping("/featured")
    @Operation(
        summary = "Get featured products with pagination",
        description = "Retrieve all enabled products that are marked as featured. Featured products are highlighted products that are promoted or recommended. If no featured products are found, returns all enabled products as fallback. No authentication required. Returns products with pagination support."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Featured products retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving featured products")
    })
    public ResponseEntity<BaseResponseDto<Page<ProductDto>>> getFeaturedProducts(
            @Parameter(description = "Pagination settings: page number (0-based), size, sort field, and direction") 
            @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        logger.info("Getting featured products with pagination");
        try {
            Page<ProductDto> products = productService.getFeaturedProducts(pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Featured products retrieved successfully", products));
        } catch (Exception e) {
            logger.error("Error retrieving featured products: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve featured products: " + e.getMessage()));
        }
    }

    /**
     * Get highlighted products with pagination
     */
    @GetMapping("/highlighted")
    @Operation(
        summary = "Get highlighted products with pagination",
        description = "Retrieve all enabled products that are marked as highlighted. Highlighted products are special products that deserve extra attention on the website. If no highlighted products are found, returns all enabled products as fallback. No authentication required. Returns products with pagination support."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Highlighted products retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving highlighted products")
    })
    public ResponseEntity<BaseResponseDto<Page<ProductDto>>> getHighlightedProducts(
            @Parameter(description = "Pagination settings: page number (0-based), size, sort field, and direction") 
            @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        logger.info("Getting highlighted products with pagination");
        try {
            Page<ProductDto> products = productService.getHighlightedProducts(pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Highlighted products retrieved successfully", products));
        } catch (Exception e) {
            logger.error("Error retrieving highlighted products: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve highlighted products: " + e.getMessage()));
        }
    }

    /**
     * Get latest products
     */
    @GetMapping("/latest")
    @Operation(
        summary = "Get latest products",
        description = "Retrieve the most recently added products. Returns enabled products ordered by creation date in descending order. No authentication required. Supports pagination to control the number of results."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Latest products retrieved successfully with pagination info"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving latest products")
    })
    public ResponseEntity<BaseResponseDto<Page<ProductDto>>> getLatestProducts(
            @Parameter(description = "Pagination settings: page number (0-based), size, sort field, and direction") 
            @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        logger.info("Getting latest products");
        try {
            Page<ProductDto> products = productService.getLatestProducts(pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Latest products retrieved successfully", products));
        } catch (Exception e) {
            logger.error("Error retrieving latest products: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve latest products: " + e.getMessage()));
        }
    }

    /*=============================================
     PRODUCT IMAGE ENDPOINTS  
     =============================================*/

    /**
     * Get product images
     */
    @GetMapping("/{productId}/images")
    @Operation(summary = "Get product images", description = "Get all images for a specific product")
    public ResponseEntity<BaseResponseDto<List<ProductImageDto>>> getProductImages(@PathVariable Long productId) {
        logger.info("Getting images for product ID: {}", productId);
        try {
            List<ProductImageDto> images = productService.getProductImages(productId);
            return ResponseEntity.ok(BaseResponseDto.success("Product images retrieved successfully", images));
        } catch (Exception e) {
            logger.error("Error getting product images for {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get product images: " + e.getMessage()));
        }
    }

    /**
     * Add image to product (Admin only)
     */
    @PostMapping("/{productId}/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Add image to product", description = "Associate an image with a product. Admin/Manager access required.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ProductImageDto>> addImageToProduct(
            @PathVariable Long productId, @PathVariable Long imageId,
            @RequestParam(value = "isPrimary", defaultValue = "false") Boolean isPrimary) {
        logger.info("Adding image {} to product {}, isPrimary: {}", imageId, productId, isPrimary);
        try {
            ProductImageDto productImage = productService.addImageToProduct(productId, imageId, isPrimary);
            return ResponseEntity.ok(BaseResponseDto.success("Image added to product successfully", productImage));
        } catch (Exception e) {
            logger.error("Error adding image to product: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to add image to product: " + e.getMessage()));
        }
    }

    /**
     * Remove image from product (Admin only)
     */
    @DeleteMapping("/{productId}/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Remove image from product", description = "Remove image association from product. Admin/Manager access required.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Void>> removeImageFromProduct(
            @PathVariable Long productId, @PathVariable Long imageId) {
        logger.info("Removing image {} from product {}", imageId, productId);
        try {
            productService.removeImageFromProduct(productId, imageId);
            return ResponseEntity.ok(BaseResponseDto.success("Image removed from product successfully", null));
        } catch (Exception e) {
            logger.error("Error removing image from product: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to remove image from product: " + e.getMessage()));
        }
    }

    /**
     * Set primary image for product (Admin only)
     */
    @PutMapping("/{productId}/images/{imageId}/primary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Set primary image", description = "Set an image as primary for the product. Admin/Manager access required.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ProductImageDto>> setPrimaryImage(
            @PathVariable Long productId, @PathVariable Long imageId) {
        logger.info("Setting primary image {} for product {}", imageId, productId);
        try {
            ProductImageDto primaryImage = productService.setPrimaryImage(productId, imageId);
            return ResponseEntity.ok(BaseResponseDto.success("Primary image set successfully", primaryImage));
        } catch (Exception e) {
            logger.error("Error setting primary image: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to set primary image: " + e.getMessage()));
        }
    }

    /*=============================================
     PRODUCT ATTRIBUTE ENDPOINTS
     =============================================*/

    /**
     * Update product attribute (update entire PRODUCT_ATTRIBUTE by PRODUCT_ATTRIBUTE_ID)
     */
    @PutMapping("/attributes/{productAttributeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update product attribute association", description = "Update entire product attribute association by PRODUCT_ATTRIBUTE_ID. Admin/Manager access required.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ProductAttributeDto>> updateProductAttribute(
            @PathVariable Long productAttributeId, @Valid @RequestBody ProductAttributeDto productAttributeDto) {
        logger.info("Updating product attribute association ID: {}", productAttributeId);
        try {
            ProductAttributeDto updatedProductAttribute = productAttributeService.updateProductAttribute(productAttributeId, productAttributeDto);
            return ResponseEntity.ok(BaseResponseDto.success("Product attribute association updated successfully", updatedProductAttribute));
        } catch (Exception e) {
            logger.error("Error updating product attribute association: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update product attribute association: " + e.getMessage()));
        }
    }

    /**
     * Get product attribute by ID (Admin/System reference)
     */
    @GetMapping("/attributes/{productAttributeId}")
    @Operation(
        summary = "Get product attribute by ID",
        description = "Retrieve detailed information about a specific product attribute association by its database ID. Admin/System endpoint for management operations."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product attribute retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductAttributeDto.class))),
        @ApiResponse(responseCode = "404", description = "Product attribute not found with the specified ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving product attribute")
    })
    public ResponseEntity<BaseResponseDto<ProductAttributeDto>> getProductAttributeById(
            @Parameter(description = "Database product attribute identifier", example = "1")
            @PathVariable Long productAttributeId) {
        
        logger.info("Getting product attribute by ID: {}", productAttributeId);
        
        try {
            return productAttributeService.findProductAttributeById(productAttributeId)
                    .map(dto -> ResponseEntity.ok(BaseResponseDto.success("Product attribute retrieved successfully", dto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(BaseResponseDto.notFound("Product attribute not found with ID: " + productAttributeId)));
        } catch (Exception e) {
            logger.error("Error getting product attribute by ID {}: {}", productAttributeId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get product attribute: " + e.getMessage()));
        }
    }

    /**
     * Get all product attributes with filtering and pagination (Admin dashboard)
     */
    @GetMapping("/attributes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Get all product attributes with filtering and pagination",
        description = "Retrieve product attributes with comprehensive filtering options. Supports filtering by product ID, attribute ID, enabled status, and attribute value. Returns paginated results for admin dashboard. Admin/Manager access required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product attributes retrieved successfully with pagination info"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters or pagination settings"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN or MANAGER role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while searching product attributes")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Page<ProductAttributeDto>>> getAllProductAttributesByCriteria(
            @Parameter(description = "Filter by product ID", example = "1")
            @RequestParam(value = "productId", required = false) Long productId,
            
            @Parameter(description = "Filter by attribute ID from PRODUCT_ATTR table", example = "2")
            @RequestParam(value = "productAttrId", required = false) Long productAttrId,
            
            @Parameter(description = "Filter by enabled status", example = "true")
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            
            @Parameter(description = "Search by attribute value (partial match)", example = "Red")
            @RequestParam(value = "attributeValue", required = false) String attributeValue,
            
            @Parameter(description = "Pagination settings: page number (0-based), size, sort field, and direction")
            @PageableDefault(page = 0, size = 20, sort = "createdDt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        logger.info("Getting all product attributes with criteria - productId: {}, attrId: {}, enabled: {}, value: {}", 
                   productId, productAttrId, enabled, attributeValue);
        
        try {
            Page<ProductAttributeDto> productAttributesPage = productAttributeService.getProductAttributesByCriteria(
                    productId, productAttrId, enabled, attributeValue, pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Product attributes retrieved successfully", productAttributesPage));
        } catch (Exception e) {
            logger.error("Error getting product attributes: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get product attributes: " + e.getMessage()));
        }
    }

    /**
     * Update product attribute quantity only (Admin only)
     */
    @PatchMapping("/attributes/{productAttributeId}/quantity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Update product attribute quantity",
        description = "Update only the quantity field of a product attribute. Useful for stock management operations. Admin/Manager access required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product attribute quantity updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid quantity value or validation errors"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN or MANAGER role required"),
        @ApiResponse(responseCode = "404", description = "Product attribute not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while updating quantity")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ProductAttributeDto>> updateProductAttributeQuantity(
            @Parameter(description = "Database product attribute identifier", example = "1")
            @PathVariable Long productAttributeId,
            
            @Parameter(description = "New quantity value (must be >= 0)", example = "25")
            @RequestParam Integer quantity) {
        
        logger.info("Updating product attribute quantity: ID={}, newQuantity={}", productAttributeId, quantity);
        
        try {
            ProductAttributeDto updatedProductAttribute = productAttributeService.updateProductAttributeQuantity(
                    productAttributeId, quantity);
            return ResponseEntity.ok(BaseResponseDto.success("Product attribute quantity updated successfully", updatedProductAttribute));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating quantity: {}", e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating product attribute quantity {}: {}", productAttributeId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update quantity: " + e.getMessage()));
        }
    }

    /**
     * Delete product attribute by ID (Admin only)
     */
    @DeleteMapping("/attributes/{productAttributeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Delete product attribute association",
        description = "Delete a product attribute association by its ID. This action cannot be undone. Admin/Manager access required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product attribute deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN or MANAGER role required"),
        @ApiResponse(responseCode = "404", description = "Product attribute not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while deleting product attribute")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Void>> deleteProductAttributeById(
            @Parameter(description = "Database product attribute identifier", example = "1")
            @PathVariable Long productAttributeId) {
        
        logger.info("Deleting product attribute by ID: {}", productAttributeId);
        
        try {
            productAttributeService.deleteProductAttribute(productAttributeId);
            return ResponseEntity.ok(BaseResponseDto.success("Product attribute deleted successfully", null));
        } catch (IllegalArgumentException e) {
            logger.error("Product attribute not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting product attribute {}: {}", productAttributeId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to delete product attribute: " + e.getMessage()));
        }
    }

    /**
     * Create new product attribute (Admin only)
     */
    @PostMapping("/attributes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Create new product attribute",
        description = "Create a new product attribute association. Links a product with an attribute and specifies the value and quantity. Admin/Manager access required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product attribute created successfully",
                    content = @Content(schema = @Schema(implementation = ProductAttributeDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data or validation errors"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN or MANAGER role required"),
        @ApiResponse(responseCode = "404", description = "Product or attribute not found"),
        @ApiResponse(responseCode = "409", description = "Product attribute combination already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while creating product attribute")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ProductAttributeDto>> createProductAttribute(
            @Parameter(description = "Product attribute data to create")
            @Valid @RequestBody AddProductAttributeRequestDto request) {
        
        logger.info("Creating product attribute: productId={}, attrId={}, value={}, quantity={}", 
                   request.getProductId(), request.getProductAttrId(), request.getAttrValue(), request.getQuantity());
        
        try {
            ProductAttributeDto productAttribute = productAttributeService.createProductAttribute(
                request.getProductId(), request.getProductAttrId(), request.getAttrValue(), request.getQuantity());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Product attribute created successfully", productAttribute));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating product attribute: {}", e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating product attribute: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to create product attribute: " + e.getMessage()));
        }
    }

    /*=============================================
     PRODUCT TYPE ENDPOINTS
     =============================================*/

    /**
     * Get product type by ID
     */
    @GetMapping("/types/{productTypeId}")
    @Operation(summary = "Get product type by ID", description = "Get product type information by database ID")
    public ResponseEntity<BaseResponseDto<ProductTypeDto>> getProductTypeById(@PathVariable Long productTypeId) {
        logger.info("Requesting product type by ID: {}", productTypeId);
        try {
            Optional<ProductTypeDto> productType = productTypeService.findProductTypeById(productTypeId);
            return productType.map(dto -> ResponseEntity.ok(BaseResponseDto.success("Product type retrieved successfully", dto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound("Product type not found with ID: " + productTypeId)));
        } catch (Exception e) {
            logger.error("Error retrieving product type by ID: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve product type: " + e.getMessage()));
        }
    }

    /**
     * Get product types with filtering and pagination
     */
    @GetMapping("/types")
    @Operation(summary = "Get product types with filtering", description = "Get all product types with optional filtering and pagination")
    public ResponseEntity<BaseResponseDto<Page<ProductTypeDto>>> getProductTypesByCriteria(
            @RequestParam(value = "textSearch", required = false) String textSearch,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        logger.info("Getting product types with criteria - textSearch: {}, enabled: {}", textSearch, enabled);
        
        try {
            Page<ProductTypeDto> productTypesPage = productTypeService.getProductTypesByCriteria(textSearch, enabled, pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Product types retrieved successfully", productTypesPage));
        } catch (Exception e) {
            logger.error("Error getting product types: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get product types: " + e.getMessage()));
        }
    }



    /**
     * Create new product type (Admin only)
     */
    @PostMapping("/types")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Create new product type", description = "Create a new product type. Admin/Manager access required.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ProductTypeDto>> createProductType(@Valid @RequestBody ProductTypeDto productTypeDto) {
        logger.info("Creating new product type: {}", productTypeDto.getProductTypeName());
        try {
            ProductTypeDto createdProductType = productTypeService.createProductType(productTypeDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.success("Product type created successfully", createdProductType));
        } catch (Exception e) {
            logger.error("Error creating product type: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to create product type: " + e.getMessage()));
        }
    }

    /**
     * Update existing product type (Admin/Manager only)
     */
    @PutMapping("/types/{productTypeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update product type", description = "Update existing product type by ID. Admin/Manager access required.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ProductTypeDto>> updateProductType(
            @PathVariable Long productTypeId, 
            @Valid @RequestBody ProductTypeDto productTypeDto) {
        logger.info("Updating product type ID: {} with data: {}", productTypeId, productTypeDto.getProductTypeName());
        try {
            ProductTypeDto updatedProductType = productTypeService.updateProductType(productTypeId, productTypeDto);
            return ResponseEntity.ok(BaseResponseDto.success("Product type updated successfully", updatedProductType));
        } catch (Exception e) {
            logger.error("Error updating product type {}: {}", productTypeId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update product type: " + e.getMessage()));
        }
    }

    /*=============================================
     PRODUCT CATEGORY ENDPOINTS
     =============================================*/

    /**
     * Get product category by ID
     */
    @GetMapping("/categories/{productCategoryId}")
    @Operation(summary = "Get product category by ID", description = "Get product category information by database ID")
    public ResponseEntity<BaseResponseDto<ProductCategoryDto>> getProductCategoryById(@PathVariable Long productCategoryId) {
        logger.info("Requesting product category by ID: {}", productCategoryId);
        try {
            Optional<ProductCategoryDto> productCategory = productCategoryService.findProductCategoryById(productCategoryId);
            return productCategory.map(dto -> ResponseEntity.ok(BaseResponseDto.success("Product category retrieved successfully", dto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound("Product category not found with ID: " + productCategoryId)));
        } catch (Exception e) {
            logger.error("Error retrieving product category by ID: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve product category: " + e.getMessage()));
        }
    }

    /**
     * Get product categories with filtering and pagination
     */
    @GetMapping("/categories")
    @Operation(
        summary = "Get product categories with advanced filtering", 
        description = "Get all product categories with comprehensive filtering options including parent category filters and pagination. Supports filtering by root categories or specific parent category ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product categories retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving categories")
    })
    public ResponseEntity<BaseResponseDto<Page<ProductCategoryDto>>> getProductCategoriesByCriteria(
            @Parameter(description = "Search text across category fields (name, slug, displayName, remark)", 
                      example = "art")
            @RequestParam(value = "textSearch", required = false) String textSearch,
            
            @Parameter(description = "Filter by enabled status", example = "true")
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            
            @Parameter(description = "Filter by visible status", example = "true")
            @RequestParam(value = "visible", required = false) Boolean visible,
            
            @Parameter(description = "Filter by product type ID", example = "1")
            @RequestParam(value = "productTypeId", required = false) Long productTypeId,
            
            @Parameter(description = "Filter by parent category ID (null for all, specific ID for children of that category)", 
                      example = "1")
            @RequestParam(value = "parentCategoryId", required = false) Long parentCategoryId,
            
            @Parameter(description = "Filter only root categories (categories with no parent). Cannot be used with parentCategoryId", 
                      example = "true")
            @RequestParam(value = "rootOnly", required = false) Boolean rootOnly,
            
            @Parameter(description = "Pagination settings")
            @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        logger.info("Getting product categories with criteria - textSearch: {}, productTypeId: {}, parentCategoryId: {}, rootOnly: {}", 
                   textSearch, productTypeId, parentCategoryId, rootOnly);
        
        try {
            // Validation: cannot use both parentCategoryId and rootOnly
            if (parentCategoryId != null && Boolean.TRUE.equals(rootOnly)) {
                return ResponseEntity.badRequest().body(
                    BaseResponseDto.badRequest("Cannot use both parentCategoryId and rootOnly filters simultaneously"));
            }
            
            Page<ProductCategoryDto> productCategoriesPage = productCategoryService.getProductCategoriesByCriteria(
                textSearch, enabled, visible, productTypeId, parentCategoryId, rootOnly, pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Product categories retrieved successfully", productCategoriesPage));
        } catch (Exception e) {
            logger.error("Error getting product categories: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get product categories: " + e.getMessage()));
        }
    }

    /**
     * Create new product category (Admin/Manager only)
     */
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Create new product category", description = "Create a new product category. Admin/Manager access required.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ProductCategoryDto>> createProductCategory(@Valid @RequestBody ProductCategoryDto productCategoryDto) {
        logger.info("Creating new product category: {}", productCategoryDto.getProductCategoryName());
        try {
            ProductCategoryDto createdProductCategory = productCategoryService.createProductCategory(productCategoryDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.success("Product category created successfully", createdProductCategory));
        } catch (Exception e) {
            logger.error("Error creating product category: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to create product category: " + e.getMessage()));
        }
    }

    /**
     * Update existing product category (Admin/Manager only)
     */
    @PutMapping("/categories/{productCategoryId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update product category", description = "Update existing product category by ID. Admin/Manager access required.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ProductCategoryDto>> updateProductCategory(
            @PathVariable Long productCategoryId, 
            @Valid @RequestBody ProductCategoryDto productCategoryDto) {
        logger.info("Updating product category ID: {} with data: {}", productCategoryId, productCategoryDto.getProductCategoryName());
        try {
            ProductCategoryDto updatedProductCategory = productCategoryService.updateProductCategory(productCategoryId, productCategoryDto);
            return ResponseEntity.ok(BaseResponseDto.success("Product category updated successfully", updatedProductCategory));
        } catch (Exception e) {
            logger.error("Error updating product category {}: {}", productCategoryId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update product category: " + e.getMessage()));
        }
    }

    /*=============================================
     PRODUCT STATE ENDPOINTS
     =============================================*/

    /**
     * Get product states with filtering and pagination
     */
    @GetMapping("/states")
    @Operation(summary = "Get product states with filtering", description = "Get all product states with optional filtering and pagination")
    public ResponseEntity<BaseResponseDto<Page<ProductStateDto>>> getProductStatesByCriteria(
            @RequestParam(value = "textSearch", required = false) String textSearch,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        logger.info("Getting product states with criteria - textSearch: {}, enabled: {}", textSearch, enabled);
        
        try {
            Page<ProductStateDto> productStatesPage = productStateService.getProductStatesByCriteria(
                textSearch, enabled, pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Product states retrieved successfully", productStatesPage));
        } catch (Exception e) {
            logger.error("Error getting product states: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get product states: " + e.getMessage()));
        }
    }

    /*=============================================
     PRODUCT ATTR MANAGEMENT ENDPOINTS
     =============================================*/

    /**
     * Get product attributes with filtering and pagination (similar to getProductStatesByCriteria)
     */
    @GetMapping("/attrs")
    @Operation(summary = "Get product attributes with filtering", description = "Get all product attributes with optional filtering and pagination")
    public ResponseEntity<BaseResponseDto<Page<ProductAttrDto>>> getProductAttrsByCriteria(
            @RequestParam(value = "textSearch", required = false) String textSearch,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        logger.info("Getting product attributes with criteria - textSearch: {}, enabled: {}", textSearch, enabled);
        
        try {
            Page<ProductAttrDto> productAttrsPage = productAttrService.getProductAttrsByCriteria(
                textSearch, enabled, pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Product attributes retrieved successfully", productAttrsPage));
        } catch (Exception e) {
            logger.error("Error getting product attributes: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get product attributes: " + e.getMessage()));
        }
    }

    /**
     * Create new product attribute (Admin/Manager only)
     */
    @PostMapping("/attrs")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Create new product attribute", description = "Create a new product attribute definition. Admin/Manager access required.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ProductAttrDto>> createProductAttr(@Valid @RequestBody ProductAttrDto productAttrDto) {
        logger.info("Creating new product attribute: {}", productAttrDto.getProductAttrName());
        try {
            ProductAttrDto createdProductAttr = productAttrService.createProductAttr(productAttrDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.success("Product attribute created successfully", createdProductAttr));
        } catch (Exception e) {
            logger.error("Error creating product attribute: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to create product attribute: " + e.getMessage()));
        }
    }

    /**
     * Update existing product attribute (Admin/Manager only)
     */
    @PutMapping("/attrs/{productAttrId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update existing product attribute", description = "Update product attribute definition. Admin/Manager access required.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ProductAttrDto>> updateProductAttr(
            @PathVariable Long productAttrId, @Valid @RequestBody ProductAttrDto productAttrDto) {
        logger.info("Updating product attribute ID: {}", productAttrId);
        try {
            ProductAttrDto updatedProductAttr = productAttrService.updateProductAttr(productAttrId, productAttrDto);
            return ResponseEntity.ok(BaseResponseDto.success("Product attribute updated successfully", updatedProductAttr));
        } catch (Exception e) {
            logger.error("Error updating product attribute: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update product attribute: " + e.getMessage()));
        }
    }
}