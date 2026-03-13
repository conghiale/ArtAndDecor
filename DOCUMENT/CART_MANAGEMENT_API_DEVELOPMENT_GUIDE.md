# Cart Management API Development Guide

## Overview

The Cart Management API provides comprehensive functionality for managing shopping carts, cart states, cart items, and cart item states in the ArtAndDecor e-commerce platform. This system handles the complete cart lifecycle from creation to checkout.

## Database Schema

### Cart System Tables

The cart system consists of four main tables:

#### CART_STATE Table
```sql
CREATE TABLE `CART_STATE` (
    `CART_STATE_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `CART_STATE_NAME` VARCHAR(64) NOT NULL UNIQUE,
    `CART_STATE_DISPLAY_NAME` VARCHAR(256),
    `CART_STATE_REMARK` VARCHAR(256) NOT NULL,
    `CART_STATE_ENABLED` BOOLEAN NOT NULL DEFAULT TRUE,
    `CREATED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `MODIFIED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Common cart states:**
- `ACTIVE` - Cart is active and can be modified
- `CHECKED_OUT` - Cart has been checked out
- `ABANDONED` - Cart has been abandoned by user
- `EXPIRED` - Cart has expired due to inactivity

#### CART Table
```sql
CREATE TABLE `CART` (
    `CART_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `USER_ID` BIGINT NULL,
    `SESSION_ID` VARCHAR(100),
    `CART_SLUG` VARCHAR(64) NOT NULL UNIQUE,
    `CART_STATE_ID` BIGINT NOT NULL,
    `TOTAL_QUANTITY` INT NOT NULL DEFAULT 0,
    `CART_ENABLED` BOOLEAN NOT NULL DEFAULT TRUE,
    `CREATED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `MODIFIED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`USER_ID`) REFERENCES `USER`(`USER_ID`) ON DELETE SET NULL,
    FOREIGN KEY (`CART_STATE_ID`) REFERENCES `CART_STATE`(`CART_STATE_ID`) ON DELETE RESTRICT
);
```

#### CART_ITEM_STATE Table
```sql
CREATE TABLE `CART_ITEM_STATE` (
    `CART_ITEM_STATE_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `CART_ITEM_STATE_NAME` VARCHAR(64) NOT NULL UNIQUE,
    `CART_ITEM_STATE_DISPLAY_NAME` VARCHAR(256),
    `CART_ITEM_STATE_REMARK` VARCHAR(256) NOT NULL,
    `CART_ITEM_STATE_ENABLED` BOOLEAN NOT NULL DEFAULT TRUE,
    `CREATED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `MODIFIED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Common cart item states:**
- `ACTIVE` - Item is active in the cart
- `ORDERED` - Item has been ordered/checked out
- `REMOVED` - Item has been removed from cart

#### CART_ITEM Table
```sql
CREATE TABLE `CART_ITEM` (
    `CART_ITEM_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `CART_ID` BIGINT NOT NULL,
    `PRODUCT_ID` BIGINT NOT NULL,
    `CART_ITEM_QUANTITY` INT NOT NULL DEFAULT 1,
    `CART_ITEM_TOTAL_PRICE` DECIMAL(15,2) NOT NULL,
    `CART_ITEM_STATE_ID` BIGINT NOT NULL,
    `CREATED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `MODIFIED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`CART_ID`) REFERENCES `CART`(`CART_ID`) ON DELETE CASCADE,
    FOREIGN KEY (`PRODUCT_ID`) REFERENCES `PRODUCT`(`PRODUCT_ID`) ON DELETE RESTRICT,
    FOREIGN KEY (`CART_ITEM_STATE_ID`) REFERENCES `CART_ITEM_STATE`(`CART_ITEM_STATE_ID`) ON DELETE RESTRICT,
    UNIQUE KEY `idx_cart_product_unique` (`CART_ID`, `PRODUCT_ID`)
);
```

## API Overview

### Cart Management APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/carts` | GET | ADMIN | Get carts by criteria with pagination and filtering |
| `/api/carts/{cartId}` | GET | ADMIN/OWNER | Get specific cart details by ID |
| `/api/carts/slug/{cartSlug}` | GET | PUBLIC | Get cart by URL-friendly slug |
| `/api/carts/user/{userId}/active` | GET | ADMIN/USER | Get user's current active cart |
| `/api/carts` | POST | USER | Create new cart |
| `/api/carts/{cartId}` | PUT | ADMIN/OWNER | Update cart information |
| `/api/carts/{cartId}` | DELETE | ADMIN/OWNER | Delete cart |
| `/api/carts/{cartId}/checkout` | POST | USER | Checkout cart and create order |

### Cart State Management APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/carts/states` | GET | PUBLIC | Get cart states with filtering |
| `/api/carts/states/{cartStateId}` | GET | ADMIN | Get specific cart state |
| `/api/carts/states` | POST | ADMIN | Create new cart state |
| `/api/carts/states/{cartStateId}` | PUT | ADMIN | Update cart state |
| `/api/carts/states/{cartStateId}` | DELETE | ADMIN | Delete cart state |

### Cart Item Management APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/carts/items` | GET | ADMIN | Get cart items with advanced filtering |
| `/api/carts/{cartId}/items` | GET | ADMIN/OWNER | Get items in specific cart |
| `/api/carts/{cartId}/items/active` | GET | CUSTOMER | Get active items in cart (customer access) |
| `/api/carts/items` | POST | USER | Add item to cart |
| `/api/carts/items/{itemId}` | PUT | USER | Update cart item |
| `/api/carts/items/{itemId}` | DELETE | USER | Remove item from cart |
| `/api/carts/{cartId}/clear` | DELETE | USER | Clear all items from cart |

### Cart Item State Management APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/carts/item-states` | GET | PUBLIC | Get cart item states with filtering |
| `/api/carts/item-states/{stateId}` | GET | ADMIN | Get specific cart item state |
| `/api/carts/item-states` | POST | ADMIN | Create new cart item state |
| `/api/carts/item-states/{stateId}` | PUT | ADMIN | Update cart item state |
| `/api/carts/item-states/{stateId}` | DELETE | ADMIN | Delete cart item state |

### Key Features

- **Multi-User Support:** Both authenticated users and guest sessions
- **State Management:** Complete cart and item lifecycle tracking  
- **Flexible Filtering:** Advanced search and filtering capabilities
- **Security:** Role-based access control with ownership validation
- **Performance:** Optimized queries with pagination support
- **Business Logic:** Automatic total calculation and state transitions
- **Analytics:** Admin reporting and cart abandonment tracking

---

## API Endpoints

### Cart APIs (Consolidated)

#### 1. Get Carts by Criteria (Consolidated Endpoint)
- **Endpoint:** `GET /api/carts`
- **Access:** Admin only
- **Parameters:**
  - `cartId` (Long, optional) - Filter by cart ID
  - `userId` (Long, optional) - Filter by user ID  
  - `sessionId` (String, optional) - Filter by session ID
  - `cartStateId` (Long, optional) - Filter by cart state ID
  - `cartSlug` (String, optional) - Filter by cart slug
  - `cartEnabled` (Boolean, optional) - Filter by enabled status
  - `page` (int, default: 0) - Page number
  - `size` (int, default: 10) - Page size
  - `sortBy` (string, default: "createdDt") - Sort field
  - `sortDirection` (string, default: "desc") - Sort direction
- **Response:** Paginated list of carts matching criteria
- **Use Case:** Admin dashboard with flexible filtering - replaces getAllCarts, searchCarts, getCartsByState, getAbandonedCarts

#### 2. Get Cart by ID
- **Endpoint:** `GET /api/carts/{cartId}`
- **Access:** Admin or cart owner
- **Response:** Detailed cart information
- **Use Case:** View specific cart details

#### 3. Get Cart by Slug
- **Endpoint:** `GET /api/carts/slug/{cartSlug}`
- **Access:** Public (authenticated)
- **Response:** Cart information by slug
- **Use Case:** Retrieve cart using friendly URL identifier

#### 4. Get User's Active Cart
- **Endpoint:** `GET /api/carts/user/{userId}/active`
- **Access:** Admin or user themselves
- **Response:** User's current active cart
- **Use Case:** Display user's shopping cart

### Cart State APIs (Consolidated)

#### 1. Get Cart States by Criteria (Consolidated Endpoint)
- **Endpoint:** `GET /api/carts/states`
- **Access:** Public
- **Parameters:**
  - `cartStateId` (Long, optional) - Filter by cart state ID
  - `cartStateName` (String, optional) - Filter by cart state name
  - `cartStateEnabled` (Boolean, optional) - Filter by enabled status
  - `textSearch` (String, optional) - Text search in name, display name, and remark
- **Response:** List of cart states matching criteria (no pagination)
- **Use Case:** Flexible cart state retrieval - replaces getAllCartStates, getEnabledCartStates

#### 2. Get Cart State by ID
- **Endpoint:** `GET /api/carts/states/{cartStateId}`
- **Access:** Admin only
- **Response:** Specific cart state details
- **Use Case:** Cart state management

### Cart Item APIs (Consolidated)

#### 1. Get Cart Items by Criteria (Consolidated Endpoint)
- **Endpoint:** `GET /api/carts/items`
- **Access:** Admin only
- **Parameters:**
  - `cartItemId` (Long, optional) - Filter by cart item ID
  - `cartId` (Long, optional) - Filter by cart ID
  - `productId` (Long, optional) - Filter by product ID
  - `userId` (Long, optional) - Filter by user ID
  - `minPrice` (BigDecimal, optional) - Filter by minimum price
  - `maxPrice` (BigDecimal, optional) - Filter by maximum price
  - `minQuantity` (Integer, optional) - Filter by minimum quantity
  - `maxQuantity` (Integer, optional) - Filter by maximum quantity
  - `cartItemStateId` (Long, optional) - Filter by cart item state ID
  - `page` (int, default: 0) - Page number
  - `size` (int, default: 10) - Page size
  - `sortBy` (string, default: "createdDt") - Sort field
  - `sortDirection` (string, default: "desc") - Sort direction
- **Response:** Paginated list of cart items matching criteria
- **Use Case:** Admin analysis with flexible filtering - replaces getAllCartItems, searchCartItems, getCartItemsByPriceRange, getCartItemsByUserId

#### 2. Get Cart Items by Cart ID
- **Endpoint:** `GET /api/carts/{cartId}/items`
- **Access:** Admin or cart owner
- **Response:** Items in specific cart
- **Use Case:** Display cart contents

#### 3. Get Active Cart Items by Cart ID (NEW)
- **Endpoint:** `GET /api/carts/{cartId}/items/active`  
- **Access:** Admin or cart owner (CUSTOMER role)
- **Response:** Only active items in specific cart
- **Use Case:** Display active cart items for customers - NEW endpoint for CUSTOMER role access

### Cart Item State APIs (Consolidated)

#### 1. Get Cart Item States by Criteria (Consolidated Endpoint)
- **Endpoint:** `GET /api/carts/item-states`
- **Access:** Public
- **Parameters:**
  - `cartItemStateId` (Long, optional) - Filter by cart item state ID
  - `cartItemStateName` (String, optional) - Filter by cart item state name
  - `cartItemStateEnabled` (Boolean, optional) - Filter by enabled status
  - `textSearch` (String, optional) - Text search in name, display name, and remark
- **Response:** List of cart item states matching criteria (no pagination)
- **Use Case:** Flexible cart item state retrieval - replaces getAllCartItemStates, getEnabledCartItemStates

#### 2. Get Cart Item State by ID
- **Endpoint:** `GET /api/carts/item-states/{cartItemStateId}`
- **Access:** Admin only
- **Response:** Specific cart item state details
- **Use Case:** Cart item state management

## Deprecated/Removed Endpoints

The following endpoints have been **consolidated** into the criteria-based endpoints above:

### Cart APIs (Removed):
- **~~GET /api/carts/search~~** - Consolidated into `GET /api/carts` with query parameters
- **~~GET /api/carts/state/{cartStateName}~~** - Consolidated into `GET /api/carts` with cartStateId parameter
- **~~GET /api/carts/abandoned~~** - Consolidated into `GET /api/carts` with cartStateId and date filtering
- **~~GET /api/carts/statistics~~** - Removed as per requirements

### Cart State APIs (Removed):
- **~~GET /api/carts/states/enabled~~** - Consolidated into `GET /api/carts/states` with cartStateEnabled=true
- **~~GET /api/carts/states (paginated)~~** - Now returns all matching results without pagination

### Cart Item APIs (Removed):
- **~~GET /api/carts/items/user/{userId}~~** - Consolidated into `GET /api/carts/items` with userId parameter
- **~~GET /api/carts/items/search~~** - Consolidated into `GET /api/carts/items` with query parameters
- **~~GET /api/carts/items/price-range~~** - Consolidated into `GET /api/carts/items` with minPrice/maxPrice parameters
- **~~GET /api/carts/items/popular-products~~** - Removed as per requirements

### Cart Item State APIs (Removed):
- **~~GET /api/carts/item-states/enabled~~** - Consolidated into `GET /api/carts/item-states` with cartItemStateEnabled=true
- **~~GET /api/carts/item-states (paginated)~~** - Now returns all matching results without pagination

## Security Configuration

### Updated Access Control Rules

```java
// Cart public endpoints - no authentication required
"/carts/states/enabled" - Public access (deprecated, use /carts/states?cartStateEnabled=true)
"/carts/item-states/enabled" - Public access (deprecated, use /carts/item-states?cartItemStateEnabled=true)

// Cart admin-only endpoints - require ADMIN role
"/carts" - Admin only (consolidated criteria-based endpoint)
"/carts/items" - Admin only (consolidated criteria-based endpoint)
"/carts/states/{cartStateId}" - Admin only
"/carts/item-states/{cartItemStateId}" - Admin only

// Cart authenticated endpoints - general access for authenticated users
"/carts/slug/**" - Authenticated users
"/carts/states" - Public (no pagination, criteria-based)
"/carts/item-states" - Public (no pagination, criteria-based)

// Cart user-specific endpoints with method-level @PreAuthorize
"/carts/{cartId}" - Admin or cart owner
"/carts/user/{userId}/active" - Admin or user themselves
"/carts/{cartId}/items" - Admin or cart owner
"/carts/{cartId}/items/active" - Admin or cart owner (NEW CUSTOMER endpoint)
```

## Data Transfer Objects (DTOs)

### CartDto
```java
public class CartDto {
    private Long cartId;
    private String sessionId;
    private String cartSlug;
    private Boolean cartEnabled;
    private LocalDateTime createdDt;
    private LocalDateTime modifiedDt;
    
    // Foreign key references
    private Long userId;
    private Long cartStateId;
    private String cartStateName;
    
    // Computed fields (when needed)
    private Integer totalItemCount;
    private BigDecimal totalValue;
    private Boolean isEmpty;
}
```

### CartStateDto
```java
public class CartStateDto {
    private Long cartStateId;
    private String cartStateName;
    private String cartStateDisplayName;
    private String cartStateRemark;
    private Boolean cartStateEnabled;
    
    // Additional information (when loaded)
    private Long cartCount;
}
```

### CartItemDto
```java
public class CartItemDto {
    private Long cartItemId;
    private Integer cartItemQuantity;
    private BigDecimal cartItemTotalPrice;
    private LocalDateTime createdDt;
    private LocalDateTime modifiedDt;
    
    // Foreign key references
    private Long cartId;
    private Long productId;
    private Long cartItemStateId;
    
    // Nested DTOs (when needed)
    private CartDto cart;
    private ProductDto product;
    private CartItemStateDto cartItemState;
}
```

### CartItemStateDto
```java
public class CartItemStateDto {
    private Long cartItemStateId;
    private String cartItemStateName;
    private String cartItemStateDisplayName;
    private String cartItemStateRemark;
    private Boolean cartItemStateEnabled;
    
    // Additional information (when loaded)
    private Long cartItemCount;
}
```

## Service Layer Architecture (Interface-Implementation Pattern)

### CartService Interface
- **Interface:** `org.ArtAndDecor.services.CartService`
- **Implementation:** `org.ArtAndDecor.services.impl.CartServiceImpl`
- **Key Methods:**
  - `getCartById(Long cartId)` - Get cart by ID
  - `getCartBySlug(String cartSlug)` - Get cart by slug
  - `getActiveCartByUser(Long userId)` - Get user's active cart
  - `getCartsByCriteria(...)` - **NEW:** Flexible criteria-based retrieval

### CartStateService Interface
- **Interface:** `org.ArtAndDecor.services.CartStateService`
- **Implementation:** `org.ArtAndDecor.services.impl.CartStateServiceImpl`
- **Key Methods:**
  - `getCartStateById(Long cartStateId)` - Get cart state by ID
  - `getCartStateByName(String cartStateName)` - Get by name
  - `getCartStatesByCriteria(...)` - **NEW:** Flexible criteria-based retrieval (no pagination)
  - `searchCartStates(...)` - Search with pagination
  - `createCartState(...)`, `updateCartState(...)`, `deleteCartState(...)` - CRUD operations

### CartItemService Interface
- **Responsibility:** Cart item operations and calculations
- **Key Methods:**
  - `getCartItemsByCartId()` - Get items in cart
  - `addItemToCart()` - Add product to cart
  - `updateCartItemQuantity()` - Update item quantity
  - `removeCartItem()` - Remove item from cart
  - `clearCart()` - Remove all items
  - `getCartTotalValue()` - Calculate total value
  - `getMostPopularProducts()` - Popular product analysis

### CartItemStateService
- **Responsibility:** Cart item state management
- **Key Methods:**
  - `getAllCartItemStates()` - Get all item states
  - `getEnabledCartItemStates()` - Get enabled states
  - `getActiveCartItemState()` - Get active state
  - `getOrderedCartItemState()` - Get ordered state

## Repository Layer

### Advanced Query Features

#### CartRepository
```java
// Custom queries for cart management
Optional<Cart> findActiveCartByUser(Long userId);
Optional<Cart> findActiveCartBySession(String sessionId);
Page<Cart> findAbandonedCarts(LocalDateTime cutoffDate, Pageable pageable);
Page<Cart> searchCarts(String keyword, Pageable pageable);
List<Object[]> getCartStatistics();
```

#### CartItemRepository
```java
// Business-specific queries
Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
List<CartItem> findActiveCartItemsByUser(Long userId);
BigDecimal getCartTotalValue(Long cartId);
Integer getCartTotalQuantity(Long cartId);
Page<Object[]> findMostPopularProductsInCarts(Pageable pageable);
```

## Error Handling

### Common Error Scenarios

1. **Cart Not Found**
   - Status: 404 Not Found
   - Message: "Cart not found with ID: {cartId}"

2. **Access Denied**
   - Status: 403 Forbidden
   - Message: "Access denied - insufficient permissions"

3. **Cart State Not Found**
   - Status: 404 Not Found
   - Message: "Cart state not found: {stateName}"

4. **Duplicate Cart Slug**
   - Status: 409 Conflict
   - Message: "Cart slug already exists: {slug}"

5. **Invalid Quantity**
   - Status: 400 Bad Request
   - Message: "Quantity must be greater than 0"

## Performance Considerations

### Indexing Strategy
```sql
-- Cart table indexes
INDEX `idx_cart_user` (`USER_ID`);
INDEX `idx_cart_session` (`SESSION_ID`);
INDEX `idx_cart_slug` (`CART_SLUG`);

-- Cart item table indexes
UNIQUE KEY `idx_cart_product_unique` (`CART_ID`, `PRODUCT_ID`);
INDEX `idx_cart_item_cart` (`CART_ID`);
INDEX `idx_cart_item_product` (`PRODUCT_ID`);
```

### Optimization Tips

1. **Lazy Loading:** Use `@OneToMany(fetch = FetchType.LAZY)` for cart items
2. **Pagination:** Always use pagination for large datasets
3. **Selective Queries:** Use custom queries with specific fields when full entity data isn't needed
4. **Caching:** Consider caching frequently accessed cart states and item states

### CartItemService Interface
- **Interface:** `org.ArtAndDecor.services.CartItemService`
- **Implementation:** `org.ArtAndDecor.services.impl.CartItemServiceImpl`
- **Key Methods:**
  - `getCartItemById(Long cartItemId)` - Get cart item by ID
  - `getCartItemsByCartId(Long cartId)` - Get items by cart
  - `getActiveCartItemsByUser(Long userId)` - Get active user cart items
  - `getActiveCartItemsByCartId(Long cartId)` - **NEW:** For CUSTOMER role access
  - `getCartItemsByCriteria(...)` - **NEW:** Flexible criteria-based retrieval with pagination
  - `addItemToCart(...)`, `updateCartItemQuantity(...)`, `removeCartItem(...)` - Item management
  - `getCartTotalValue(...)`, `getCartTotalQuantity(...)` - Cart calculations

### CartItemStateService Interface
- **Interface:** `org.ArtAndDecor.services.CartItemStateService`
- **Implementation:** `org.ArtAndDecor.services.impl.CartItemStateServiceImpl`
- **Key Methods:**
  - `getCartItemStateById(Long cartItemStateId)` - Get cart item state by ID
  - `getCartItemStateByName(String cartItemStateName)` - Get by name
  - `getCartItemStatesByCriteria(...)` - **NEW:** Flexible criteria-based retrieval (no pagination)
  - `searchCartItemStates(...)` - Search with pagination
  - `getActiveCartItemState()`, `getOrderedCartItemState()` - Special state getters
  - `createCartItemState(...)`, `updateCartItemState(...)`, `deleteCartItemState(...)` - CRUD operations

## Implementation Benefits

### API Consolidation Benefits
1. **Reduced API Surface:** From 15+ endpoints to 8 core endpoints
2. **Flexible Filtering:** Single endpoints handle multiple use cases
3. **Consistent Interface:** Uniform parameter patterns across all APIs
4. **Better Performance:** Reduced over-fetching with precise filtering
5. **Easier Maintenance:** Fewer endpoints to maintain and test

### Interface-Implementation Pattern Benefits
1. **Loose Coupling:** Controllers depend on interfaces, not implementations
2. **Testability:** Easy to mock services for unit testing
3. **Flexibility:** Can swap implementations without changing controllers
4. **Clean Architecture:** Clear separation of concerns
5. **Scalability:** Easy to add caching, logging, or other cross-cutting concerns

## Testing Strategy

### Unit Tests
- Service layer business logic validation
- Repository layer custom query testing 
- DTO conversion accuracy
- Interface contract compliance

### Integration Tests
- Controller endpoint functionality with new consolidated APIs
- Security configuration verification for new CUSTOMER endpoint
- Database transaction handling with criteria-based queries

### Test Data Setup
```sql
-- Insert test cart states
INSERT INTO CART_STATE (CART_STATE_NAME, CART_STATE_DISPLAY_NAME, CART_STATE_REMARK) VALUES
('ACTIVE', 'Active Cart', 'Cart is active and editable'),
('CHECKED_OUT', 'Checked Out', 'Cart has been checked out'),
('ABANDONED', 'Abandoned Cart', 'Cart has been abandoned');

-- Insert test cart item states  
INSERT INTO CART_ITEM_STATE (CART_ITEM_STATE_NAME, CART_ITEM_STATE_DISPLAY_NAME, CART_ITEM_STATE_REMARK) VALUES
('ACTIVE', 'Active Item', 'Item is active in cart'),
('ORDERED', 'Ordered Item', 'Item has been ordered');
```

## Migration Guide

### From Old APIs to New Consolidated APIs

#### Cart APIs Migration:
```bash
# OLD: Multiple specific endpoints
GET /api/carts/search?keyword=test&page=0&size=10
GET /api/carts/state/ACTIVE?page=0&size=10
GET /api/carts/abandoned?days=7

# NEW: Single consolidated endpoint  
GET /api/carts?textSearch=test&page=0&size=10
GET /api/carts?cartStateId=1&page=0&size=10
GET /api/carts?cartStateId=3&page=0&size=10
```

#### Cart Item APIs Migration:
```bash
# OLD: Multiple specific endpoints
GET /api/carts/items/search?keyword=product&page=0&size=10
GET /api/carts/items/price-range?minPrice=10&maxPrice=100
GET /api/carts/items/user/123

# NEW: Single consolidated endpoint
GET /api/carts/items?textSearch=product&page=0&size=10
GET /api/carts/items?minPrice=10&maxPrice=100&page=0&size=10
GET /api/carts/items?userId=123&page=0&size=10
```

## Deployment Notes

### API Documentation
- OpenAPI 3.0 specifications updated for consolidated endpoints
- Swagger UI available at `/swagger-ui.html` with new parameter documentation
- API documentation auto-generated from enhanced annotations

### Monitoring
- Comprehensive logging at INFO level for business operations
- DEBUG level logging for detailed troubleshooting with criteria parameters
- Performance metrics collection for cart operations and query performance

### Security
- JWT-based authentication required
- Enhanced role-based access control (RBAC) with new CUSTOMER endpoint
- Input validation and sanitization for all criteria parameters
- CORS configuration for web clients

## Future Enhancements

1. **Advanced Filtering:** Add date range filtering, sorting by multiple fields
2. **Caching Strategy:** Implement Redis caching for frequently accessed data
3. **Audit Trail:** Track all cart modifications with full audit log
4. **Bulk Operations:** Add bulk cart item operations
5. **Real-time Updates:** WebSocket support for cart synchronization
6. **Analytics Dashboard:** Enhanced reporting with consolidated data access

---

## Quick Reference

### Environment Setup
1. Ensure database tables are created using `CREATE_DB_ART_AND_DECOR.sql`
2. Configure JWT security settings
3. Set up proper role assignments (USER, ADMIN, CUSTOMER)
4. Test consolidated endpoints using Swagger UI

### API Usage Examples
```bash
# Get all active carts for admin
GET /api/carts?cartStateId=1&page=0&size=20

# Get cart items by price range and quantity
GET /api/carts/items?minPrice=10&maxPrice=100&minQuantity=2&page=0&size=10

# Get cart states matching text search
GET /api/carts/states?textSearch=active

# Get active items in specific cart (CUSTOMER access)  
GET /api/carts/123/items/active
```

### Common Use Cases
- **Customer Shopping:** Get active cart, add/remove items, view active cart items only
- **Admin Management:** Use consolidated endpoints with flexible filtering for analysis
- **Analytics:** Leverage criteria-based filtering for detailed reporting
- **System Maintenance:** Use consolidated state management endpoints
