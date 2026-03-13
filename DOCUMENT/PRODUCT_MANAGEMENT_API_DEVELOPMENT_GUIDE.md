# PRODUCT MANAGEMENT API DEVELOPMENT GUIDE

**Project:** Art & Decor E-commerce Platform  
**Date:** March 02, 2026  
**Author:** Development Team  
**Version:** 3.0  
**Features:** Enhanced Product Management APIs with Unified Pagination, OpenAPI Documentation, and Streamlined Product Discovery Endpoints  

---

## Overview

The Product Management API provides comprehensive functionality for handling product catalog operations including product CRUD operations, filtering, categorization, and search capabilities. All product operations are designed with:

- **Unified Pagination:** All listing endpoints (featured, highlighted, latest, top-selling) support comprehensive pagination with sorting options
- **Advanced Filtering:** Multi-criteria search including text search, price range, stock status, category, state, and special flags
- **Path-Based Routing:** RESTful URLs using PathVariable for category and type filtering (`/category/{slug}`, `/type/{slug}`)
- **Consistent Data Response:** All endpoints ensure non-empty results by falling back to enabled products when specific filters return no results
- **Rich Product Data:** Comprehensive product information including descriptions, pricing, inventory, and attributes
- **OpenAPI Documentation:** Complete API documentation with parameter descriptions, response schemas, and interactive testing capabilities
- **Enhanced Performance:** Optimized with pagination-first approach and efficient database queries

---

## Database Structure

### Core Product Management Tables

#### PRODUCT_TYPE Table
- **PURPOSE:** Product type categorization (e.g., Furniture, Decoration, Lighting)
- **KEY FIELDS:** PRODUCT_TYPE_ID, PRODUCT_TYPE_NAME, PRODUCT_TYPE_SLUG, PRODUCT_TYPE_ENABLED
- **INDEXING:** Primary key on PRODUCT_TYPE_ID, unique index on PRODUCT_TYPE_SLUG

#### PRODUCT_CATEGORY Table  
- **PURPOSE:** Product category classification (e.g., Sofa, Table, Wall Art)
- **KEY FIELDS:** PRODUCT_CATEGORY_ID, PRODUCT_CATEGORY_NAME, PRODUCT_CATEGORY_SLUG, PRODUCT_CATEGORY_ENABLED
- **INDEXING:** Primary key on PRODUCT_CATEGORY_ID, unique index on PRODUCT_CATEGORY_SLUG

#### PRODUCT_STATE Table
- **PURPOSE:** Product lifecycle states (e.g., Active, Inactive, Discontinued)
- **KEY FIELDS:** PRODUCT_STATE_ID, PRODUCT_STATE_NAME, PRODUCT_STATE_ENABLED
- **INDEXING:** Primary key on PRODUCT_STATE_ID

#### PRODUCT Table (Main Entity)
- **PRODUCT_ID:** Primary key (AUTO_INCREMENT BIGINT)
- **PRODUCT_NAME:** Display name (VARCHAR(255), NOT NULL)
- **PRODUCT_SLUG:** URL-friendly identifier (VARCHAR(255), UNIQUE, NOT NULL)
- **PRODUCT_CODE:** Unique product code (VARCHAR(50), UNIQUE, NOT NULL)
- **PRODUCT_DESCRIPTION:** Detailed description (TEXT)
- **PRODUCT_REMARK:** Additional remarks (VARCHAR(256))
- **PRODUCT_PRICE:** Current price (DECIMAL(15,2), NOT NULL)
- **PRODUCT_STOCK_QUANTITY:** Inventory count (INT, DEFAULT 0)
- **PRODUCT_ENABLED:** Active status (BOOLEAN, DEFAULT TRUE)
- **PRODUCT_FEATURED:** Featured product flag (BOOLEAN, DEFAULT FALSE)
- **PRODUCT_HIGHLIGHTED:** Highlighted product flag (BOOLEAN, DEFAULT FALSE)
- **PRODUCT_TYPE_ID:** Foreign key to PRODUCT_TYPE
- **PRODUCT_CATEGORY_ID:** Foreign key to PRODUCT_CATEGORY
- **PRODUCT_STATE_ID:** Foreign key to PRODUCT_STATE
- **CREATED_DT, MODIFIED_DT:** Timestamps

#### Supporting Tables
- **PRODUCT_ATTR:** Attribute definitions (size, color, material, etc.)
- **PRODUCT_ATTRIBUTE:** Product-specific attribute values
- **PRODUCT_IMAGE:** Product image associations

---

## API Overview

**Documentation:** All endpoints are fully documented with OpenAPI 3.0 annotations, providing:
- Comprehensive parameter descriptions with examples
- Response schema definitions
- Error code explanations
- Interactive API testing capabilities via Swagger UI
- Security requirement specifications

### Product Management APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/products/search` | GET | PUBLIC | Get products with advanced filtering and pagination |
| `/api/products/category/{categorySlug}` | GET | PUBLIC | Get enabled products by category slug |
| `/api/products/type/{typeSlug}` | GET | PUBLIC | Get enabled products by product type slug |
| `/api/products/{productId}` | GET | PUBLIC | Get specific product details by ID |
| `/api/products/slug/{productSlug}` | GET | PUBLIC | Get product details by URL-friendly slug |
| `/api/products/code/{productCode}` | GET | PUBLIC | Get product details by unique product code |
| `/api/products/featured` | GET | PUBLIC | Get featured products with pagination |
| `/api/products/highlighted` | GET | PUBLIC | Get highlighted products with pagination |
| `/api/products/latest` | GET | PUBLIC | Get latest products with pagination |
| `/api/products/top-selling` | GET | PUBLIC | Get top-selling products with pagination |
| `/api/products` | POST | ADMIN | Create new product |
| `/api/products/{productId}` | PUT | ADMIN | Update existing product |
| `/api/products/{productId}` | DELETE | ADMIN | Delete product |
| `/api/products/{productId}/toggle-status` | PATCH | ADMIN | Toggle product enabled status |

### Product Category Management APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/products/categories` | GET | PUBLIC | Get product categories with filtering |
| `/api/products/categories/{categoryId}` | GET | PUBLIC | Get specific product category |
| `/api/products/categories/enabled` | GET | PUBLIC | Get enabled product categories |
| `/api/products/categories` | POST | ADMIN | Create new product category |
| `/api/products/categories/{categoryId}` | PUT | ADMIN | Update product category |
| `/api/products/categories/{categoryId}` | DELETE | ADMIN | Delete product category |

### Product Type Management APIs

| Endpoint | Method | Access | Description |  
|----------|--------|--------|-------------|
| `/api/products/types` | GET | PUBLIC | Get product types with filtering |
| `/api/products/types/{typeId}` | GET | PUBLIC | Get specific product type |
| `/api/products/types/enabled` | GET | PUBLIC | Get enabled product types |
| `/api/products/types` | POST | ADMIN | Create new product type |
| `/api/products/types/{typeId}` | PUT | ADMIN | Update product type |
| `/api/products/types/{typeId}` | DELETE | ADMIN | Delete product type |

### Product State Management APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/products/states` | GET | PUBLIC | Get product states with filtering |
| `/api/products/states/{stateId}` | GET | PUBLIC | Get specific product state |
| `/api/products/states/enabled` | GET | PUBLIC | Get enabled product states |
| `/api/products/states` | POST | ADMIN | Create new product state |
| `/api/products/states/{stateId}` | PUT | ADMIN | Update product state |
| `/api/products/states/{stateId}` | DELETE | ADMIN | Delete product state |

---

## API Endpoints

**Documentation:** All endpoints are fully documented with OpenAPI 3.0 annotations, providing:
- Comprehensive parameter descriptions with examples
- Response schema definitions
- Error code explanations  
- Interactive API testing capabilities via Swagger UI
- Security requirement specifications

### 1. Get Products with Advanced Filtering (Search Endpoint)
**Endpoint:** `GET /api/products/search`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**OpenAPI Summary:** "Get products with advanced filtering and pagination"
**Description:** Retrieve products with comprehensive filtering options. Use `enabled=true` to get all enabled products (replaces the deprecated `/enabled` endpoint). Returns all products when no filters provided.

**Query Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `textSearch` | String | No | Search across product fields (name, code, description) | `"sofa"` |
| `enabled` | Boolean | No | Filter by enabled status (use `true` for all enabled products) | `true` |
| `categoryId` | Long | No | Filter by category ID | `1` |
| `stateId` | Long | No | Filter by state ID | `1` |
| `minPrice` | BigDecimal | No | Minimum price filter | `100.00` |
| `maxPrice` | BigDecimal | No | Maximum price filter | `5000.00` |
| `inStock` | Boolean | No | Filter by stock availability | `true` |
| `productCode` | String | No | Filter by product code | `"PRD001"` |
| `page` | Integer | No | Page number (default: 0, zero-based) | `0` |
| `size` | Integer | No | Page size (default: 10, max: 100) | `20` |
| `sort` | String | No | Sort field (default: createdDt) | `"productName"` |
| `direction` | String | No | Sort direction: ASC or DESC (default: DESC) | `"ASC"` |

**Example Request - Get All Enabled Products:**
```bash
curl -X GET "http://localhost:8080/products/search?enabled=true&page=0&size=10" \
  -H "Content-Type: application/json"
```

**Example Request - Advanced Filtering:**
```bash
curl -X GET "http://localhost:8080/products/search?textSearch=sofa&categoryId=1&minPrice=500&maxPrice=3000&inStock=true&page=0&size=5" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Products retrieved successfully",
  "data": {
    "content": [
      {
        "productId": 1,
        "productName": "Premium Leather Sofa",
        "productSlug": "premium-leather-sofa",
        "productCode": "PRD001",
        "productDescription": "Luxurious 3-seater leather sofa with premium Italian leather and solid wood frame",
        "productPrice": 2499.99,
        "productStockQuantity": 15,
        "productEnabled": true,
        "productType": {
          "productTypeId": 1,
          "productTypeName": "Furniture",
          "productTypeSlug": "furniture"
        },
        "productCategory": {
          "productCategoryId": 1,
          "productCategoryName": "Sofa",
          "productCategorySlug": "sofa"
        },
        "productState": {
          "productStateId": 1,
          "productStateName": "Active"
        },
        "createdDt": "2026-02-25 09:30:00",
        "modifiedDt": "2026-02-26 10:15:00"
      }
    ],
    "pageable": {
      "page": 0,
      "size": 10,
      "sort": "createdDt,DESC"
    },
    "totalElements": 45,
    "totalPages": 5,
    "first": true,
    "last": false
  },
  "timestamp": "2026-02-26 14:30:00"
}
```

### 2. Get Products by Category Slug
**Endpoint:** `GET /api/products/category/{categorySlug}`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**OpenAPI Summary:** "Get enabled products by category slug"
**Description:** Retrieve all enabled products belonging to a specific category using the URL-friendly category slug. This endpoint automatically filters for enabled products only.

**Path Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `categorySlug` | String | Yes | URL-friendly category identifier | `"sofa"`, `"dining-table"` |

**Query Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `page` | Integer | No | Page number (default: 0) | `0` |
| `size` | Integer | No | Page size (default: 10) | `10` |
| `sort` | String | No | Sort field (default: createdDt) | `"productPrice"` |
| `direction` | String | No | Sort direction (default: DESC) | `"ASC"` |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/products/category/sofa?page=0&size=5&sort=productPrice&direction=ASC" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Products retrieved successfully",
  "data": {
    "content": [
      {
        "productId": 1,
        "productName": "Premium Leather Sofa",
        "productSlug": "premium-leather-sofa",
        "productCode": "PRD001",
        "productDescription": "Luxurious 3-seater leather sofa with premium Italian leather",
        "productPrice": 2499.99,
        "productStockQuantity": 15,
        "productEnabled": true,
        "productType": {
          "productTypeId": 1,
          "productTypeName": "Furniture",
          "productTypeSlug": "furniture"
        },
        "productCategory": {
          "productCategoryId": 1,
          "productCategoryName": "Sofa",
          "productCategorySlug": "sofa"
        },
        "productState": {
          "productStateId": 1,
          "productStateName": "Active"
        },
        "createdDt": "2026-02-25 09:30:00",
        "modifiedDt": "2026-02-26 10:15:00"
      }
    ],
    "pageable": {
      "page": 0,
      "size": 5,
      "sort": "productPrice,ASC"
    },
    "totalElements": 12,
    "totalPages": 3,
    "first": true,
    "last": false
  },
  "timestamp": "2026-02-26 14:30:00"
}
```

### 3. Get Products by Type Slug
**Endpoint:** `GET /api/products/type/{typeSlug}`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**OpenAPI Summary:** "Get enabled products by type slug"
**Description:** Retrieve all enabled products belonging to a specific type using the URL-friendly type slug. This endpoint automatically filters for enabled products only.

**Path Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `typeSlug` | String | Yes | URL-friendly type identifier | `"furniture"`, `"decoration"` |

**Query Parameters:** (Same as category endpoint)

**Example Request:**
```bash
curl -X GET "http://localhost:8080/products/type/furniture?page=0&size=10" \
  -H "Content-Type: application/json"
```

### 4. Get Product by ID
**Endpoint:** `GET /api/products/{productId}`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**OpenAPI Summary:** "Get product by ID"
**Description:** Retrieve detailed product information by product ID.

**Path Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `productId` | Long | Yes | Product ID | `1` |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/products/1" \
  -H "Content-Type: application/json"
```

### 5. Get Product by Slug
**Endpoint:** `GET /api/products/slug/{productSlug}`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**OpenAPI Summary:** "Get product by URL-friendly slug"
**Description:** Retrieve detailed product information using the URL-friendly slug identifier.

**Path Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `productSlug` | String | Yes | URL-friendly product identifier | `"premium-leather-sofa"` |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/products/slug/premium-leather-sofa" \
  -H "Content-Type: application/json"
```

### 6. Create Product (Admin Only)
**Endpoint:** `POST /api/products`  
**Method:** POST  
**Access:** ADMIN (Requires ADMIN role)  
**OpenAPI Summary:** "Create a new product"
**Description:** Create a new product with comprehensive information.

**Request Body (ProductDto):**
```json
{
  "productName": "Premium Leather Sofa",
  "productSlug": "premium-leather-sofa",
  "productCode": "PRD001",
  "productDescription": "Luxurious 3-seater leather sofa with premium Italian leather and solid wood frame",
  "productPrice": 2499.99,
  "productStockQuantity": 15,
  "productEnabled": true,
  "productTypeId": 1,
  "productCategoryId": 1,
  "productStateId": 1
}
```

**Example Request:**
```bash
curl -X POST "http://localhost:8080/products" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}" \
  -d '{
    "productName": "Premium Leather Sofa",
    "productSlug": "premium-leather-sofa",
    "productCode": "PRD001",
    "productDescription": "Luxurious 3-seater leather sofa",
    "productPrice": 2499.99,
    "productStockQuantity": 15,
    "productEnabled": true,
    "productTypeId": 1,
    "productCategoryId": 1,
    "productStateId": 1
  }'
```

### 7. Update Product (Admin Only)
**Endpoint:** `PUT /api/products/{productId}`  
**Method:** PUT  
**Access:** ADMIN (Requires ADMIN role)  
**OpenAPI Summary:** "Update an existing product"
**Description:** Update product information by product ID.

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `productId` | Long | Yes | Product ID to update |

**Request Body:** Same as Create Product

### 8. Update Product Status (Admin Only)
**Endpoint:** `PATCH /api/products/{productId}/status`  
**Method:** PATCH  
**Access:** ADMIN (Requires ADMIN role)  
**OpenAPI Summary:** "Update product enabled status"
**Description:** Enable or disable a product without modifying other fields.

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `productId` | Long | Yes | Product ID to update |

**Query Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `enabled` | Boolean | Yes | New enabled status | `true` or `false` |

---

## Enhanced Product Discovery Endpoints

### 9. Get Featured Products with Pagination
**Endpoint:** `GET /api/products/featured`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**OpenAPI Summary:** "Get featured products with pagination"
**Description:** Retrieve all enabled products marked as featured with comprehensive pagination support. Featured products are promoted items that receive special attention and higher visibility on the website. **Smart Fallback:** If no featured products are found, automatically returns all enabled products to ensure users always see content. Returns products in paginated format with sorting options.

**Query Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `page` | Integer | No | Page number (default: 0, zero-based) | `0` |
| `size` | Integer | No | Page size (default: 10) | `10` |
| `sort` | String | No | Sort field (default: createdDt) | `"productName"` |
| `direction` | String | No | Sort direction: ASC or DESC (default: DESC) | `"ASC"` |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/products/featured?page=0&size=5&sort=productPrice&direction=ASC" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Featured products retrieved successfully",
  "data": [
    {
      "productId": 1,
      "productName": "Tranh phong cảnh hoàng hôn trên biển",
      "productSlug": "tranh-phong-canh-hoang-hon-bien",
      "productCode": "ART-SUNSET-001",
      "productPrice": 450000.00,
      "productFeatured": true,
      "productHighlighted": false,
      "stockQuantity": 50,
      "soldQuantity": 25,
      "productEnabled": true,
      "inStock": true,
      "createdDt": "2026-01-15 10:00:00"
    }
  ],
  "timestamp": "2026-03-02 14:30:00"
}
```

### 10. Get Highlighted Products with Pagination
**Endpoint:** `GET /api/products/highlighted`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**OpenAPI Summary:** "Get highlighted products with pagination"
**Description:** Retrieve all enabled products marked as highlighted with comprehensive pagination support. Highlighted products are special items that deserve extra attention and are usually displayed in special sections of the website. **Smart Fallback:** If no highlighted products are found, automatically returns all enabled products to ensure users always see content.

**Query Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `page` | Integer | No | Page number (default: 0, zero-based) | `0` |
| `size` | Integer | No | Page size (default: 10) | `10` |
| `sort` | String | No | Sort field (default: createdDt) | `"productName"` |
| `direction` | String | No | Sort direction: ASC or DESC (default: DESC) | `"DESC"` |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/products/highlighted?page=0&size=8&sort=createdDt&direction=DESC" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Highlighted products retrieved successfully",
  "data": [
    {
      "productId": 2,
      "productName": "Tranh núi non hùng vĩ",
      "productSlug": "tranh-nui-non-hung-vi",
      "productCode": "ART-MOUNTAIN-002",
      "productPrice": 520000.00,
      "productFeatured": false,
      "productHighlighted": true,
      "stockQuantity": 35,
      "soldQuantity": 18,
      "productEnabled": true,
      "inStock": true,
      "createdDt": "2026-01-20 14:00:00"
    }
  ],
  "timestamp": "2026-03-02 14:31:00"
}
```

### 11. Get Latest Products with Pagination
**Endpoint:** `GET /api/products/latest`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**OpenAPI Summary:** "Get latest products with pagination"
**Description:** Retrieve the most recently added products with comprehensive pagination support. Returns enabled products ordered by creation date in descending order (newest first). **Data Guarantee:** Always returns enabled products if any exist in the system, providing consistent content for users. Includes comprehensive product information and metadata.

**Query Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `page` | Integer | No | Page number (default: 0, zero-based) | `0` |
| `size` | Integer | No | Page size (default: 10) | `10` |
| `sort` | String | No | Sort field (default: createdDt) | `"createdDt"` |
| `direction` | String | No | Sort direction (default: DESC) | `"DESC"` |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/products/latest?page=0&size=5&sort=createdDt&direction=DESC" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Latest products retrieved successfully",
  "data": {
    "content": [
      {
        "productId": 12,
        "productName": "Giấy vẽ canvas 100% cotton A3",
        "productSlug": "giay-ve-canvas-100-cotton-a3", 
        "productCode": "TOOL-PAPER-012",
        "productPrice": 220000.00,
        "productDescription": "Giấy vẽ canvas 100% cotton, chất lượng cao",
        "stockQuantity": 50,
        "soldQuantity": 25,
        "productEnabled": true,
        "productFeatured": false,
        "productHighlighted": false,
        "inStock": true,
        "createdDt": "2026-03-02 15:00:00"
      }
    ],
    "pageable": {
      "page": 0,
      "size": 5,
      "sort": "createdDt,DESC"
    },
    "totalElements": 15,
    "totalPages": 3,
    "first": true,
    "last": false
  },
  "timestamp": "2026-03-02 15:30:00"
}
```

### 12. Get Top Selling Products with Pagination
**Endpoint:** `GET /api/products/top-selling`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**OpenAPI Summary:** "Get top selling products with pagination"
**Description:** Retrieve enabled products ordered by sold quantity (best sellers first) with comprehensive pagination support. **Data Guarantee:** Always returns enabled products if any exist in the system, ensuring users see content even when sales data is limited. Returns only enabled products with comprehensive pagination info and detailed product information.

**Query Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `page` | Integer | No | Page number (default: 0, zero-based) | `0` |
| `size` | Integer | No | Page size (default: 10) | `10` |
| `sort` | String | No | Sort field (default: soldQuantity) | `"soldQuantity"` |
| `direction` | String | No | Sort direction (default: DESC) | `"DESC"` |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/products/top-selling?page=0&size=5&sort=soldQuantity&direction=DESC" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Top selling products retrieved successfully",
  "data": {
    "content": [
      {
        "productId": 3,
        "productName": "Tranh sơn mài truyền thống Việt Nam",
        "productSlug": "tranh-son-mai-truyen-thong-vietnam",
        "productCode": "ART-LACQUER-003",
        "productPrice": 1250000.00,
        "productDescription": "Tranh sơn mài truyền thống của Việt Nam",
        "stockQuantity": 15,
        "soldQuantity": 85,
        "productEnabled": true,
        "productFeatured": true,
        "productHighlighted": false,
        "inStock": true,
        "createdDt": "2026-01-10 09:00:00"
      }
    ],
    "pageable": {
      "page": 0,
      "size": 5,
      "sort": "soldQuantity,DESC"
    },
    "totalElements": 25,
    "totalPages": 5,
    "first": true,
    "last": false
  },
  "timestamp": "2026-03-02 15:45:00"
}
```

---

## API Performance & Data Consistency

### Pagination Strategy
All listing endpoints now use a **pagination-first approach** to ensure:
- Consistent performance regardless of dataset size  
- Predictable memory usage and response times
- Optimal database query performance with indexed fields
- Enhanced user experience with manageable data chunks

### Smart Fallback Logic & Data Availability Guarantee  
**Non-Empty Response Policy:** All product listing endpoints implement intelligent fallback mechanisms to ensure users always receive meaningful content:

#### Fallback Implementation Strategy

| Endpoint | Primary Filter | Fallback Logic | Guarantee |
|----------|---------------|----------------|-----------|
| **Featured Products** | `featured=true + enabled=true` | → `enabled=true` only | Always returns enabled products |
| **Highlighted Products** | `highlighted=true + enabled=true` | → `enabled=true` only | Always returns enabled products |
| **Latest Products** | `enabled=true` (sorted by createdDt) | N/A | Always returns enabled products |
| **Top Selling Products** | `enabled=true` (sorted by soldQuantity) | N/A | Always returns enabled products |

#### Business Logic Benefits
1. **Improved User Experience:** Users never encounter empty product listings
2. **Content Continuity:** Website sections always display relevant products
3. **Administrative Flexibility:** Administrators can safely adjust featured/highlighted flags without causing empty pages
4. **Performance Efficiency:** Fallback uses the same optimized `getProductsByCriteria` method
5. **SEO Benefits:** Product listing pages always contain indexable content

#### Technical Implementation
```java
// Example: Featured Products with Smart Fallback
Page<ProductDto> featuredProducts = getProductsByCriteria(null, true, null, null, null, null, null, null, true, null, pageable);

if (featuredProducts.getTotalElements() == 0) {
    logger.debug("No featured products found, falling back to all enabled products");
    return getProductsByCriteria(null, true, null, null, null, null, null, null, null, null, pageable);
}
```

### Caching & Performance
- All `enabled=true` queries benefit from database query optimization
- Pagination parameters are validated to prevent excessive load
- Sort fields are restricted to indexed columns for optimal performance  
- Response times are optimized for typical e-commerce usage patterns

---

## Product Attribute Management

### 13. Get Product Attributes
**Endpoint:** `GET /api/products/{productId}/attributes`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**Description:** Get all attributes for a specific product.

### 14. Set Product Attributes (Admin Only)
**Endpoint:** `POST /api/products/{productId}/attributes`  
**Method:** POST  
**Access:** ADMIN (Requires ADMIN role)  
**Description:** Set multiple attributes for a product.

---

## Supporting Endpoints

### Product Types
- `GET /api/products/types` - Get all product types
- `GET /api/products/types/{typeId}` - Get product type by ID
- `GET /api/products/types/slug/{typeSlug}` - Get product type by slug

### Product Categories  
- `GET /api/products/categories` - Get all product categories
- `GET /api/products/categories/{categoryId}` - Get product category by ID
- `GET /api/products/categories/slug/{categorySlug}` - Get product category by slug

### Product States
- `GET /api/products/states` - Get all product states
- `GET /api/products/states/{stateId}` - Get product state by ID

---

## Security & Access Control

### Public Access (No Authentication Required)
**Customer-Facing Endpoints:** All product retrieval endpoints are publicly accessible to support e-commerce functionality:

- **Product Discovery:** `GET /api/products/featured`, `/api/products/highlighted`, `/api/products/latest`, `/api/products/top-selling`
- **Product Details:** `GET /api/products/{productId}`, `/api/products/slug/{productSlug}`
- **Product Search:** `GET /api/products/search` (with all filtering options)
- **Category/Type Browsing:** `GET /api/products/category/{categorySlug}`, `/api/products/type/{typeSlug}`
- **Supporting Data:** `GET /api/products/types`, `/api/products/categories`, `/api/products/states`

### Administrative Access (Admin/Manager Roles Required)
**Product Management Operations:** Restricted to users with ADMIN or MANAGER roles:

- **Product CRUD:** `POST /api/products`, `PUT /api/products/{productId}`, `PATCH /api/products/{productId}/status`
- **Product Images:** `POST /api/products/{productId}/images/{imageId}`, `PUT /api/products/{productId}/images/{imageId}/primary`, `DELETE /api/products/{productId}/images/{imageId}`
- **Product Attributes:** `POST /api/products/{productId}/attributes/{attrId}`, `PUT /api/products/attributes/{productAttributeId}`, `DELETE /api/products/{productId}/attributes/{attrId}`
- **Type Management:** `POST /api/products/types`, `PUT /api/products/types/{productTypeId}`  
- **Category Management:** `POST /api/products/categories`, `PUT /api/products/categories/{productCategoryId}`
- **Attribute Management:** `POST /api/products/attrs`, `PUT /api/products/attrs/{productAttrId}`

### Authentication Implementation
**JWT Bearer Token:** Administrative endpoints require valid JWT token in Authorization header:
```bash
curl -X POST "http://localhost:8080/products" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productName": "New Product", ...}'
```

**Role Validation:** Spring Security automatically validates user roles using `@PreAuthorize` annotations:
```java
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
```

### CORS Configuration
**Cross-Origin Support:** Configured for development and production environments:
- Development: `localhost:3000`, `localhost:5173`, `localhost:8080`  
- Production: `https://art-and-decor.com`, `https://www.art-and-decor.com`
- Methods: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS`
- Credentials: Enabled for authenticated requests

---

## Error Handling

### Common HTTP Status Codes

| Status Code | Description | Example Scenario |
|-------------|-------------|------------------|
| 200 | OK | Successful retrieval |
| 201 | Created | Product successfully created |
| 400 | Bad Request | Invalid request parameters or validation errors |
| 401 | Unauthorized | Missing or invalid JWT token |
| 403 | Forbidden | User lacks required ADMIN role |
| 404 | Not Found | Product, category, or type not found |
| 409 | Conflict | Duplicate product code or slug |
| 500 | Internal Server Error | System error |

### Error Response Format
```json
{
  "code": 404,
  "message": "Product not found with ID: 999",
  "data": null,
  "timestamp": "2026-02-26 14:30:00"
}
```

### Validation Errors
```json
{
  "code": 400,
  "message": "Validation failed",
  "data": {
    "productName": "Product name is required",
    "productPrice": "Price must be greater than 0",
    "productCode": "Product code must be unique"
  },
  "timestamp": "2026-02-26 14:30:00"
}
```

---

## Security Configuration

### Authentication Requirements
- **PUBLIC Endpoints:** All GET operations for product retrieval
- **ADMIN Endpoints:** All POST, PUT, PATCH operations for product management

### JWT Token Format
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsInJvbGUiOiJBRE1JTiIsImV4cCI6MTY0MDk5NTIwMH0...
```

### Required Role for Admin Operations
- **Role:** ADMIN
- **Operations:** Create, Update, Delete products and related entities

---

## Performance Optimization

### Database Indexing
- Primary keys on all ID fields
- Unique indexes on SLUG and CODE fields
- Composite indexes on frequently filtered fields (enabled, category_id, type_id)
- Full-text indexes on searchable text fields

### Query Optimization
- Use PathVariable routing for better caching (`/category/{slug}` vs `/search?categoryId=1`)
- Implement pagination for all listing endpoints
- Lazy loading for related entities where appropriate

### Caching Strategy
- Cache frequently accessed categories and types
- Implement Redis caching for product listings
- Use ETags for conditional requests

---

## API Usage Examples

### Frontend Integration Examples

#### Get All Enabled Products (Home Page)
```javascript
// Replace deprecated /products/enabled with search endpoint
fetch('/api/products/search?enabled=true&page=0&size=12&sort=createdDt&direction=DESC')
  .then(response => response.json())
  .then(data => {
    // Handle paginated product list
    console.log('Total products:', data.data.totalElements);
    console.log('Products:', data.data.content);
  });
```

#### Get Products by Category (Category Page)
```javascript
// New PathVariable approach for better SEO
fetch('/api/products/category/sofa?page=0&size=8&sort=productPrice&direction=ASC')
  .then(response => response.json())
  .then(data => {
    // Handle category-specific products
    displayProducts(data.data.content);
  });
```

#### Product Search with Filters
```javascript
const searchParams = new URLSearchParams({
  textSearch: 'sofa',
  categoryId: '1',
  minPrice: '500',
  maxPrice: '3000',
  inStock: 'true',
  page: '0',
  size: '10'
});

fetch(`/api/products/search?${searchParams}`)
  .then(response => response.json())
  .then(data => {
    // Handle filtered search results
    updateSearchResults(data.data);
  });
```

#### Get Single Product Details
```javascript
// Using slug for SEO-friendly URLs
fetch('/api/products/slug/premium-leather-sofa')
  .then(response => response.json())
  .then(data => {
    // Display detailed product information
    displayProductDetails(data.data);
  });
```

---

## Migration Notes

### API Changes from Previous Version
1. **Deprecated Endpoint:** `GET /api/products/enabled` → Use `GET /api/products/search?enabled=true`
2. **New PathVariable Routing:**
   - `GET /api/products/by-category?categorySlug=sofa` → `GET /api/products/category/sofa`
   - `GET /api/products/by-type?typeSlug=furniture` → `GET /api/products/type/furniture`
3. **Enhanced Search:** Single `/search` endpoint replaces multiple specialized endpoints

### Database Schema Updates
- Added `PRODUCT_DESCRIPTION TEXT` field
- Added `PRODUCT_REMARK VARCHAR(256)` field
- Updated DTO validation to include new fields

### Security Configuration Updates
- PathVariable routes automatically covered by existing `/api/products/**` security rules
- No additional security configuration required for new endpoints

---

## New Featured & Highlighted Product Endpoints

### 9. Get Featured Products
**Endpoint:** `GET /api/products/featured`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**Role:** Customer/Public  
**Description:** Retrieve all enabled products marked as featured. Featured products are promoted items that deserve special attention.

**Parameters:** None

**Example Request:**
```bash
curl -X GET "http://localhost:8080/products/featured" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Featured products retrieved successfully",
  "data": [
    {
      "productId": 1,
      "productName": "Tranh phong cảnh hoàng hôn trên biển",
      "productSlug": "tranh-phong-canh-hoang-hon-bien",
      "productCode": "ART-SUNSET-001",
      "productPrice": 450000.00,
      "productFeatured": true,
      "productHighlighted": false,
      "stockQuantity": 50,
      "soldQuantity": 25,
      "productEnabled": true,
      "inStock": true,
      "createdDt": "2026-01-15 10:00:00"
    }
  ],
  "timestamp": "2026-03-01 14:30:00"
}
```

### 10. Get Highlighted Products
**Endpoint:** `GET /api/products/highlighted`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**Role:** Customer/Public  
**Description:** Retrieve all enabled products marked as highlighted. Highlighted products are special items that deserve extra attention.

**Parameters:** None

**Example Request:**
```bash
curl -X GET "http://localhost:8080/products/highlighted" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Highlighted products retrieved successfully",
  "data": [
    {
      "productId": 2,
      "productName": "Tranh núi non hùng vĩ",
      "productSlug": "tranh-nui-non-hung-vi",
      "productCode": "ART-MOUNTAIN-002",
      "productPrice": 520000.00,
      "productFeatured": false,
      "productHighlighted": true,
      "stockQuantity": 35,
      "soldQuantity": 18,
      "productEnabled": true,
      "inStock": true,
      "createdDt": "2026-01-20 14:00:00"
    }
  ],
  "timestamp": "2026-03-01 14:31:00"
}
```

### 11. Get Latest Products
**Endpoint:** `GET /api/products/latest`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**Role:** Customer/Public  
**Description:** Retrieve the most recently added products. Returns enabled products ordered by creation date in descending order. Supports pagination to control the number of results returned.

**Query Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `page` | Integer | No | Page number (default: 0, zero-based) | `0` |
| `size` | Integer | No | Page size (default: 10, max: 100) | `20` |
| `sort` | String | No | Sort field (default: createdDt) | `"createdDt"` |
| `direction` | String | No | Sort direction (default: DESC) | `"DESC"` |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/products/latest?page=0&size=5" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Latest products retrieved successfully",
  "data": {
    "content": [
      {
        "productId": 12,
        "productName": "Giấy vẽ canvas 100% cotton A3",
        "productSlug": "giay-ve-canvas-100-cotton-a3",
        "productCode": "TOOL-PAPER-012",
        "productPrice": 220000.00,
        "productDescription": "Giấy vẽ canvas 100% cotton, chất lượng cao. Bề mặt có texture tự nhiên, thấm màu tốt.",
        "stockQuantity": 50,
        "soldQuantity": 25,
        "productEnabled": true,
        "productFeatured": false,
        "productHighlighted": false,
        "inStock": true,
        "createdDt": "2026-03-01 15:00:00"
      },
      {
        "productId": 11,
        "productName": "Màu acrylic cao cấp bộ 12 tuýp",
        "productSlug": "mau-acrylic-cao-cap-bo-12-tuyp",
        "productCode": "TOOL-PAINT-011",
        "productPrice": 450000.00,
        "productDescription": "Bộ màu acrylic cao cấp gồm 12 tuýp 20ml các màu cơ bản.",
        "stockQuantity": 70,
        "soldQuantity": 38,
        "productEnabled": true,
        "productFeatured": false,
        "productHighlighted": false,
        "inStock": true,
        "createdDt": "2026-03-01 10:30:00"
      }
    ],
    "pageable": {
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "pageNumber": 0,
      "pageSize": 5,
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 12,
    "totalPages": 3,
    "last": false,
    "first": true,
    "numberOfElements": 5,
    "size": 5,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "empty": false
  },
  "timestamp": "2026-03-01 15:30:00"
}
```

---

## Updated Search Endpoint

The `/api/products/search` endpoint now supports additional filters:

**New Parameters:**
- `featured` (Boolean): Filter by featured products (true=featured only, false=non-featured only)
- `highlighted` (Boolean): Filter by highlighted products (true=highlighted only, false=non-highlighted only)

**Example Request with New Filters:**
```bash
curl -X GET "http://localhost:8080/products/search?featured=true&highlighted=true&page=0&size=5" \
  -H "Content-Type: application/json"
```

---

## Testing with Swagger UI

Access the interactive API documentation at: `http://localhost:8080/swagger-ui.html`

### Key Testing Scenarios
1. **Public Access:** Test product retrieval endpoints without authentication
2. **Admin Operations:** Test CRUD operations with valid ADMIN JWT token
3. **Pagination:** Verify page navigation and size limits
4. **Filtering:** Test various filter combinations
5. **Error Handling:** Test invalid parameters and authorization failures

---

**Document Version:** 2.2  
**Last Updated:** March 01, 2026  
**Swagger Documentation:** `http://localhost:8080/swagger-ui.html`
**API Base URL:** `http://localhost:8080/api`  
**Swagger Documentation:** `http://localhost:8080/swagger-ui.html`
