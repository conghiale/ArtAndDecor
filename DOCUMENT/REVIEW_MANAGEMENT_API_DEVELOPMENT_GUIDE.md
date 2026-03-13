# REVIEW MANAGEMENT API DEVELOPMENT GUIDE

## Overview

Review Management API provides comprehensive endpoints for managing product reviews and review likes in the Art & Decor e-commerce system. This API supports full CRUD operations with advanced filtering, pagination, and role-based access control for both `REVIEW` and `PRODUCT_REVIEW_LIKE` entities.

**Base URL:** `/api/reviews`

**API Version:** v2.0 - Enhanced with OpenAPI 3.0 documentation and role-based security

**Database Version:** v1.3 - Removed `CREATED_BY_ROLE_ID` column from REVIEW table

## Security & Authentication

### Authentication Methods
- **JWT Bearer Token**: Required for protected endpoints
- **Public Access**: Available for read-only review display endpoints

### Role Requirements
- **Public Access**: Review browsing, statistics, like counts
- **Authenticated Users**: Personal like management, detailed analytics
- **Admin**: Full access to all review and like management features

### Security Headers
```http
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

---

## API Overview

### Review Management APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/reviews` | GET | PUBLIC | Get all product reviews with advanced filtering |
| `/api/reviews/{reviewId}` | GET | PUBLIC | Get specific review by ID |
| `/api/reviews/product/{productId}` | GET | PUBLIC | Get all reviews for specific product |
| `/api/reviews/product/{productId}/top-level` | GET | PUBLIC | Get main reviews (no replies) for product |
| `/api/reviews/{parentReviewId}/replies` | GET | PUBLIC | Get reply reviews for parent review |
| `/api/reviews/product/{productId}/recent` | GET | PUBLIC | Get recent reviews for product |
| `/api/reviews/user/{userId}` | GET | PUBLIC | Get all reviews by specific user |
| `/api/reviews/statistics/product/{productId}` | GET | PUBLIC | Get review statistics for product |
| `/api/reviews` | POST | USER | Create new product review |
| `/api/reviews/reply` | POST | USER | Create reply to existing review |
| `/api/reviews/{reviewId}` | PUT | USER/ADMIN | Update review content |
| `/api/reviews/{reviewId}` | DELETE | USER/ADMIN | Delete review (soft delete) |
| `/api/reviews/{reviewId}/toggle-visibility` | PATCH | ADMIN | Toggle review visibility |

### Review Like Management APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/review-likes` | GET | ADMIN | Get all review likes with filtering |
| `/api/review-likes/review/{reviewId}` | GET | PUBLIC | Get likes for specific review |
| `/api/review-likes/user/{userId}` | GET | USER | Get user's review likes |
| `/api/reviews/{reviewId}/like` | POST | USER | Like a review |
| `/api/reviews/{reviewId}/unlike` | DELETE | USER | Unlike a review |
| `/api/reviews/{reviewId}/like-status` | GET | USER | Check if user liked review |

### Review Analytics APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/reviews/analytics/summary` | GET | ADMIN | Get overall review analytics |
| `/api/reviews/analytics/ratings` | GET | ADMIN | Get rating distribution analytics |
| `/api/reviews/analytics/trends` | GET | ADMIN | Get review trends over time |

### Key Features

- **Multi-Level Reviews:** Support for main reviews and nested replies
- **Rating System:** 1-5 star ratings with aggregation
- **Like System:** Users can like/unlike reviews with tracking
- **Content Moderation:** Visibility controls and soft delete
- **Advanced Filtering:** Search by user, product, rating, content, and more
- **Analytics:** Comprehensive reporting and statistics
- **Security:** Role-based access with user ownership validation
- **Performance:** Optimized queries with pagination support

---

## REVIEW API ENDPOINTS

### 1. Get All Product Reviews with Advanced Filtering

**Endpoint:** `GET /api/reviews`

**Security:** 🌍 **Public Access** - No authentication required

**Function:** Retrieve a paginated list of product reviews with comprehensive filtering options including user, product, rating, content search, and visibility filters.

**Use Cases:**
- Product review display on e-commerce pages
- Admin review management and analytics  
- Customer review browsing and search
- Review moderation and quality control

**Parameters:**
- `userId` (Long, optional) - Filter reviews by user ID who wrote them
- `productId` (Long, optional) - Filter reviews by specific product ID  
- `parentReviewId` (Long, optional) - Filter reply reviews by parent review ID
- `rootReviewId` (Long, optional) - Filter by root review ID in conversation thread
- `rating` (Byte, optional) - Filter by star rating (1-5 stars)
- `minCountLike` (Integer, optional) - Filter reviews with minimum number of likes
- `isVisible` (Boolean, optional) - Filter by visibility status (admin use)
- `isDeleted` (Boolean, optional) - Filter by deletion status (admin use)  
- `searchText` (String, optional) - Search text in review content (case-insensitive)
- `page` (Integer, default=0) - Page number (zero-based)
- `size` (Integer, default=10) - Page size (1-100)
- `sortBy` (String, default="createdDt") - Sort field name
- `sortDir` (String, default="desc") - Sort direction (asc/desc)

**Response Format:**
```json
{
    "code": 200,
    "message": "Reviews retrieved successfully",
    "timestamp": "2026-03-04 15:30:00",
    "data": {
        "content": [
            {
                "reviewId": 1,
                "parentReviewId": null,
                "rootReviewId": null,
                "reviewLevel": 0,
                "rating": 5,
                "reviewContent": "Excellent product, high quality materials",
                "countLike": 15,
                "isVisible": true,
                "isDeleted": false,
                "createdDt": "2026-03-01 10:30:00",
                "modifiedDt": "2026-03-01 10:30:00",
                "user": {
                    "userId": 123,
                    "userName": "customer1",
                    "firstName": "John",
                    "lastName": "Smith"
                },
                "product": {
                    "productId": 456,
                    "productName": "Modern Abstract Art Canvas"
                }
            }
        ],
        "pageable": {
            "pageNumber": 0,
            "pageSize": 10
        },
        "totalElements": 127,
        "totalPages": 13
    }
}
```

**Usage Examples:**
```bash
# Get all reviews with pagination
GET /api/reviews?page=0&size=10

# Filter reviews by product
GET /api/reviews?productId=456&page=0&size=10

# Filter by user and rating
GET /api/reviews?userId=123&rating=5

# Search in review content
GET /api/reviews?searchText=excellent%20product

# Get reviews with minimum 5 likes
GET /api/reviews?minCountLike=5

# Complex filtering for admin
GET /api/reviews?productId=456&rating=5&isVisible=true&sortBy=countLike&sortDir=desc
```

### 2. Get Review by ID

**Endpoint:** `GET /api/reviews/{reviewId}`

**Security:** 🌍 **Public Access** - No authentication required

**Function:** Retrieve detailed information about a specific review using its unique database identifier.

**Path Parameters:**
- `reviewId` (Long, required) - Unique review identifier (positive integer)

**Use Cases:**
- Display detailed review information  
- Direct review linking and sharing
- Admin review moderation
- Review analytics and reporting

**Response Format:**
```json
{
    "code": 200,
    "message": "Review retrieved successfully", 
    "timestamp": "2026-03-04 15:30:00",
    "data": {
        "reviewId": 1,
        "parentReviewId": null,
        "rootReviewId": null,
        "reviewLevel": 0,
        "rating": 5,
        "reviewContent": "Absolutely love this piece! Perfect for my living room.",
        "countLike": 23,
        "isVisible": true,
        "isDeleted": false,
        "createdDt": "2026-03-01 10:30:00",
        "modifiedDt": "2026-03-01 10:30:00",
        "user": {
            "userId": 123,
            "userName": "artlover2024", 
            "firstName": "Sarah",
            "lastName": "Johnson"
        },
        "product": {
            "productId": 456,
            "productName": "Vintage Landscape Oil Painting"
        }
    }
}
```

### 3. Get Reviews by Product ID

**Endpoint:** `GET /api/reviews/product/{productId}`

**Security:** 🌍 **Public Access** - No authentication required

**Function:** Retrieve all reviews associated with a specific product with pagination and sorting.

**Path Parameters:**
- `productId` (Long, required) - Product identifier to get reviews for

**Query Parameters:**
- `page` (Integer, default=0) - Page number (zero-based)
- `size` (Integer, default=10) - Page size (1-100)  
- `sortBy` (String, default="createdDt") - Sort field name
- `sortDir` (String, default="desc") - Sort direction (asc/desc)

**Use Cases:**
- Product detail page review sections
- Customer review browsing
- Product quality assessment
- Purchase decision support

### 4. Get Top-Level Reviews for Product

**Endpoint:** `GET /api/reviews/product/{productId}/top-level`

**Security:** 🌍 **Public Access** - No authentication required

**Function:** Retrieve only the main reviews for a product (excludes reply reviews).

**Use Cases:** 
- Display primary product reviews without conversation threads
- Main review listing on product pages
- Review summary displays

### 5. Get Reply Reviews for Parent Review

**Endpoint:** `GET /api/reviews/{parentReviewId}/replies`

**Security:** 🌍 **Public Access** - No authentication required

**Function:** Retrieve all reply reviews (comments) for a specific parent review.

**Path Parameters:**
- `parentReviewId` (Long, required) - Parent review identifier

**Use Cases:**
- Build conversation threads under main reviews
- Display review discussions
- Customer Q&A sections

### 6. Get Recent Reviews for Product

**Endpoint:** `GET /api/reviews/product/{productId}/recent`

**Security:** 🌍 **Public Access** - No authentication required

**Function:** Retrieve the 10 most recent reviews for a product (no pagination).

**Use Cases:**
- Show latest customer feedback
- Recent review widgets
- Product freshness indicators

**Mô tả:** Lấy 10 đánh giá gần đây nhất của sản phẩm.

**Response:** Trả về List thay vì Page (không phân trang).

### 7. Lấy thống kê đánh giá

**Endpoint:** `GET /api/reviews/product/{productId}/statistics`

**Mô tả:** Lấy thống kê đánh giá của sản phẩm (số lượng, điểm trung bình).

**Response Format:**
```json
{
    "success": true,
    "message": "Review statistics retrieved successfully",
    "data": {
        "productId": 456,
        "totalReviews": 150,
        "averageRating": 4.5
    }
}
```

---

## PRODUCT REVIEW LIKE API ENDPOINTS

### 8. Lấy danh sách tất cả lượt thích với filter

**Endpoint:** `GET /api/reviews/likes`

**Mô tả:** Lấy danh sách lượt thích đánh giá với các bộ lọc.

**Parameters:**
- `userId` (Long, optional) - Lọc theo ID người thích
- `reviewId` (Long, optional) - Lọc theo ID đánh giá
- `page` (Integer, default=0) - Số trang
- `size` (Integer, default=10) - Kích thước trang
- `sortBy` (String, default="createdDt") - Trường sắp xếp
- `sortDir` (String, default="desc") - Hướng sắp xếp

**Response Format:**
```json
{
    "success": true,
    "message": "Product review likes retrieved successfully",
    "data": {
        "content": [
            {
                "productReviewLikeId": 1,
                "reviewId": 123,
                "userId": 456,
                "createdDt": "2026-03-01 10:30:00",
                "modifiedDt": "2026-03-01 10:30:00",
                "user": {
                    "userId": 456,
                    "userName": "user123"
                },
                "review": {
                    "reviewId": 123,
                    "reviewContent": "Great product!"
                }
            }
        ],
        "totalElements": 25,
        "totalPages": 3
    }
}
```

### 9. Lấy lượt thích theo ID

**Endpoint:** `GET /api/reviews/likes/{likeId}`

**Mô tả:** Lấy thông tin chi tiết của một lượt thích.

### 10. Lấy lượt thích theo đánh giá

**Endpoint:** `GET /api/reviews/{reviewId}/likes`

**Mô tả:** Lấy tất cả lượt thích của một đánh giá cụ thể.

### 11. Đếm số lượt thích của đánh giá

**Endpoint:** `GET /api/reviews/{reviewId}/likes/count`

**Mô tả:** Lấy số lượng lượt thích của đánh giá.

**Response Format:**
```json
{
    "success": true,
    "message": "Likes count retrieved successfully",
    "data": 15
}
```

### 12. Kiểm tra người dùng đã thích đánh giá chưa

**Endpoint:** `GET /api/reviews/{reviewId}/likes/user/{userId}/exists`

**Mô tả:** Kiểm tra xem người dùng đã thích đánh giá này chưa.

**Response Format:**
```json
{
    "success": true,
    "message": "User like status retrieved successfully",
    "data": true
}
```

### 13. Lấy lượt thích của người dùng cho sản phẩm

**Endpoint:** `GET /api/reviews/likes/user/{userId}/product/{productId}`

**Mô tả:** Lấy tất cả lượt thích của người dùng cho các đánh giá của sản phẩm.

---

## ERROR HANDLING

### HTTP Status Codes

- **200 OK** - Request thành công
- **404 NOT FOUND** - Không tìm thấy resource
- **500 INTERNAL SERVER ERROR** - Lỗi server

### Error Response Format

```json
{
    "success": false,
    "message": "Error description",
    "data": null
}
```

### Common Errors

- **Review not found** - Đánh giá không tồn tại
- **Product review like not found** - Lượt thích không tồn tại  
- **Invalid filter parameters** - Tham số filter không hợp lệ
- **Database connection error** - Lỗi kết nối database

---

## BEST PRACTICES

### 1. Pagination
- Luôn sử dụng phân trang cho các endpoint trả về danh sách
- Default page size = 10, có thể tùy chỉnh tùy theo UI cần thiết
- Sắp xếp mặc định theo `createdDt desc` để hiển thị mới nhất trước

### 2. Filtering
- Combine nhiều filter để tạo query phức tạp
- Sử dụng `searchText` để tìm kiếm full-text trong nội dung đánh giá
- Filter `isDeleted=false` để chỉ lấy đánh giá chưa bị xóa

### 3. Performance
- Sử dụng nested DTO để tránh N+1 query problem
- Limit số lượng kết quả trả về với pagination
- Cache kết quả thống kê cho các sản phẩm hot

### 4. Security  
- Validate tất cả input parameters
- Kiểm tra quyền truy cập trước khi trả về dữ liệu nhạy cảm
- Log tất cả các request để audit

---

## INTEGRATION EXAMPLES

### Frontend Integration

```javascript
// Lấy đánh giá của sản phẩm với filter
async function getProductReviews(productId, filters = {}) {
    const params = new URLSearchParams({
        productId: productId,
        page: filters.page || 0,
        size: filters.size || 10,
        ...filters
    });
    
    const response = await fetch(`/reviews?${params}`);
    return response.json();
}

// Lấy thống kê đánh giá
async function getReviewStatistics(productId) {
    const response = await fetch(`/api/reviews/product/${productId}/statistics`);
    return response.json();
}

// Kiểm tra user đã like review chưa
async function checkUserLiked(reviewId, userId) {
    const response = await fetch(`/api/reviews/${reviewId}/likes/user/${userId}/exists`);
    const result = await response.json();
    return result.data; // boolean
}
```

### Backend Service Integration

```java
// Service layer usage example
@Service
public class ProductDisplayService {
    
    @Autowired
    private ReviewService reviewService;
    
    public ProductPageDto getProductPage(Long productId) {
        // Lấy thống kê đánh giá
        ReviewStatisticsDto stats = reviewService.getReviewStatistics(productId);
        
        // Lấy đánh giá gần đây
        List<ReviewDto> recentReviews = reviewService.getRecentReviewsByProductId(productId);
        
        // Lấy đánh giá cấp cao nhất với phân trang
        Page<ReviewDto> topReviews = reviewService.getTopLevelReviewsByProductId(
            productId, 0, 5, "rating", "desc");
            
        return ProductPageDto.builder()
            .reviewStats(stats)
            .recentReviews(recentReviews)  
            .topReviews(topReviews.getContent())
            .build();
    }
}
```

---

## DATABASE SCHEMA REFERENCE

### REVIEW Table Structure (v1.3)
```sql
CREATE TABLE `REVIEW` (
    `REVIEW_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `USER_ID` BIGINT NOT NULL,
    `PRODUCT_ID` BIGINT NOT NULL,
    `PARENT_REVIEW_ID` BIGINT,
    `ROOT_REVIEW_ID` BIGINT,
    `REVIEW_LEVEL` INT NOT NULL DEFAULT 0,
    `RATING` TINYINT NOT NULL CHECK (`RATING` BETWEEN 1 AND 5),
    `REVIEW_CONTENT` TEXT NOT NULL,
    `COUNT_LIKE` INT NOT NULL DEFAULT 0,
    `IS_VISIBLE` BOOLEAN NOT NULL DEFAULT TRUE,
    `IS_DELETED` BOOLEAN NOT NULL DEFAULT FALSE,
    `CREATED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `MODIFIED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### PRODUCT_REVIEW_LIKE Table Structure (v1.3)
```sql
CREATE TABLE `PRODUCT_REVIEW_LIKE` (
    `PRODUCT_REVIEW_LIKE_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `REVIEW_ID` BIGINT NOT NULL,
    `USER_ID` BIGINT NOT NULL,
    `CREATED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `MODIFIED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `idx_user_review_like_unique` (`USER_ID`, `REVIEW_ID`)
);
```

### Key Changes in v1.3
- **Removed:** `CREATED_BY_ROLE_ID` column from REVIEW table
- **Updated:** Model và DTO classes để phù hợp với schema mới
- **Enhanced:** Filter capabilities cho tất cả các trường trong bảng

---

## TESTING

### Unit Test Examples
```java
@Test
public void testGetReviewsWithFilters() {
    // Test filtering by product and rating
    Page<ReviewDto> result = reviewService.getReviewsWithFilters(
        null, 123L, null, null, (byte)5, null, true, false, 
        null, 0, 10, "createdDt", "desc");
        
    assertThat(result).isNotNull();
    assertThat(result.getContent()).allMatch(r -> 
        r.getProduct().getProductId().equals(123L) && r.getRating() == 5);
}

@Test 
public void testSearchReviews() {
    // Test text search
    Page<ReviewDto> result = reviewService.getReviewsWithFilters(
        null, null, null, null, null, null, null, null,
        "tuyệt vời", 0, 10, "createdDt", "desc");
        
    assertThat(result.getContent()).allMatch(r -> 
        r.getReviewContent().toLowerCase().contains("tuyệt vời"));
}
```

### API Test Examples  
```bash
# Test basic filtering
curl "http://localhost:8080/reviews?productId=123&rating=5"

# Test pagination
curl "http://localhost:8080/reviews?page=1&size=20&sortBy=rating&sortDir=asc"

# Test text search
curl "http://localhost:8080/reviews?searchText=tuy%E1%BA%BFt%20v%E1%BB%9Di"

# Test like functionality
curl "http://localhost:8080/reviews/123/likes/user/456/exists"
```
