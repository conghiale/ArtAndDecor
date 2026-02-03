# ART AND DECOR DATABASE DEVELOPMENT GUIDE

## Tổng quan

Database **ART_AND_DECOR** là hệ thống quản lý e-commerce cho cửa hàng bán tranh nghệ thuật và đồ trang trí. Database được thiết kế với **31 bảng chính** (giảm từ 33 do loại bỏ IMAGE_FORMAT và IMAGE_CATEGORY), hỗ trợ đầy đủ các tính năng như quản lý người dùng, sản phẩm, giỏ hàng, đơn hàng, thanh toán, vận chuyển, quản lý trang và SEO.

**Phiên bản hiện tại:** v1.1 (Cập nhật Phase 2 - Xóa IMAGE_FORMAT)

**Thứ tự tạo bảng đã được tối ưu hóa:** Các bảng SEO được tạo trước các bảng khác để tránh lỗi foreign key constraint.

**Thay đổi từ phiên bản trước:**
- **IMAGE_FORMAT table**: ❌ Đã xóa - Định dạng hình ảnh được lưu trực tiếp trong trường `IMAGE_SIZE` của bảng IMAGE
- **IMAGE_CATEGORY table**: ❌ Đã xóa từ trước
- **IMAGE table**: ✅ Cập nhật - Loại bỏ cột `IMAGE_CATEGORY_ID` và `IMAGE_FORMAT_ID`, giữ lại các cột chính

**Lợi ích của thay đổi:**
- Giảm độ phức tạp của schema
- Loại bỏ JOIN không cần thiết giúp tăng hiệu suất
- Linh hoạt hơn trong lưu trữ thông tin định dạng

---

## USER MANAGEMENT TABLES

### 1. USER_PROVIDER (VIEW)
**Chức năng:** Quản lý các nhà cung cấp đăng nhập (local, Google, Facebook, GitHub)

**Dữ liệu mẫu:**
- Name: LOCAL - Description: Local registration (Đăng ký trực tiếp trên hệ thống)
- Name: GOOGLE - Description: Google OAuth (Đăng nhập qua Google)
- Name: FACEBOOK - Description: Facebook OAuth (Đăng nhập qua Facebook)
- Name: GITHUB - Description: GitHub OAuth (Đăng nhập qua GitHub)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| USER_PROVIDER_ID | BIGINT | YES | ID duy nhất của nhà cung cấp |
| USER_PROVIDER_NAME | VARCHAR(50) | YES | Tên nhà cung cấp đăng nhập |
| USER_PROVIDER_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| USER_PROVIDER_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| USER_PROVIDER_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 2. USER_ROLE (VIEW)
**Chức năng:** Quản lý vai trò người dùng trong hệ thống

**Dữ liệu mẫu:**
- Name: ADMIN - Description: System Administrator (Quản trị viên hệ thống)
- Name: MANAGER - Description: Store Manager (Quản lý cửa hàng)
- Name: STAFF - Description: Store Staff (Nhân viên cửa hàng)
- Name: CUSTOMER - Description: Customer (Khách hàng)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| USER_ROLE_ID | BIGINT | YES | ID duy nhất của vai trò |
| USER_ROLE_NAME | VARCHAR(64) | YES | Tên vai trò |
| USER_ROLE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| USER_ROLE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| USER_ROLE_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 3. USER
**Chức năng:** Lưu trữ thông tin người dùng của hệ thống

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| USER_ID | BIGINT | YES | ID duy nhất của người dùng |
| USER_PROVIDER_ID | BIGINT | YES | ID nhà cung cấp đăng nhập |
| USER_ROLE_ID | BIGINT | YES | ID vai trò người dùng |
| USER_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| USER_NAME | VARCHAR(64) | NO | Tên đăng nhập |
| PASSWORD | VARCHAR(150) | NO | Mật khẩu đã mã hóa |
| FIRST_NAME | VARCHAR(50) | NO | Tên |
| LAST_NAME | VARCHAR(50) | NO | Họ |
| PHONE_NUMBER | VARCHAR(15) | NO | Số điện thoại |
| EMAIL | VARCHAR(100) | NO | Email |
| IMAGE_AVATAR_NAME | VARCHAR(150) | NO | Tên file ảnh đại diện |
| SOCIAL_MEDIA | TEXT | NO | Thông tin mạng xã hội |
| LAST_LOGIN_DT | DATETIME | NO | Thời gian đăng nhập gần nhất |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

---

## IMAGE MANAGEMENT TABLES

**Lưu ý:** Bảng IMAGE_CATEGORY đã loại bỏ ở version trước. Bảng IMAGE_FORMAT đã loại bỏ từ Phase 2 để đơn giản hóa schema. Thông tin định dạng hình ảnh được lưu trực tiếp trong trường IMAGE_SIZE.

### 5. IMAGE
**Chức năng:** Lưu trữ thông tin chi tiết của từng hình ảnh

**Thay đổi Phase 2:**
- ❌ Loại bỏ: `IMAGE_CATEGORY_ID` - Danh mục hình ảnh
- ❌ Loại bỏ: `IMAGE_FORMAT_ID` - Tham chiếu đến bảng IMAGE_FORMAT  
- ✅ Giữ lại: `IMAGE_SIZE` - Bây giờ dùng để lưu thông tin định dạng (ví dụ: "JPEG", "PNG", "1920x1080", v.v.)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| IMAGE_ID | BIGINT | YES | ID duy nhất của hình ảnh |
| IMAGE_NAME | VARCHAR(150) | YES | Tên file hình ảnh (UUID hoặc hash SHA-256) |
| IMAGE_DISPLAY_NAME | VARCHAR(64) | YES | Tên hiển thị của hình ảnh |
| IMAGE_SLUG | VARCHAR(64) | YES | URL slug cho SEO |
| IMAGE_SIZE | VARCHAR(64) | YES | Kích thước/định dạng file (ví dụ: "JPEG", "PNG", "1920x1080") |
| IMAGE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| IMAGE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| SEO_META_ID | BIGINT | NO | ID metadata SEO |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

**Indices:**
- `idx_image_slug` - Tối ưu hóa truy vấn theo slug
- `idx_image_display_name` - Tối ưu hóa truy vấn theo display name
- `idx_image_name` - Tối ưu hóa truy vấn theo tên file
- `idx_image_seo_meta` - Tối ưu hóa JOIN với bảng SEO_META

**Ví dụ dữ liệu:**
```sql
INSERT INTO IMAGE VALUES
(1, 'sunset_seascape_001_hash.jpg', 'Hoàng hôn biển', 'sunset-seascape-001', 'JPEG', 'Bầu trời đẹp...', 'Beautiful sky...', 25, NOW(), NOW()),
(2, 'mountain_landscape_002_hash.jpg', 'Núi non', 'mountain-landscape-002', 'PNG', 'Núi cao...', 'High mountain...', 26, NOW(), NOW()),
(3, 'abstract_modern_001_hash.jpg', 'Nghệ thuật trừu tượng', 'modern-abstract-001', '1920x1200', 'Hiệu ứng...', 'Effects...', 27, NOW(), NOW());
```

---

## PRODUCT MANAGEMENT TABLES

### 6. PRODUCT_TYPE
**Chức năng:** Phân loại sản phẩm theo loại. Có thể đính kèm ảnh đại diện cho từng loại.

**Lưu ý:**
- Có thêm cột PRODUCT_TYPE_IMAGE_NAME (tùy chọn) để lưu tên file ảnh đại diện cho loại sản phẩm.

| Column Name             | Type         | Require | Description                         |
|------------------------|--------------|---------|-------------------------------------|
| PRODUCT_TYPE_ID        | BIGINT       | YES     | ID duy nhất của loại                |
| PRODUCT_TYPE_SLUG      | VARCHAR(64)  | YES     | URL slug cho SEO                    |
| PRODUCT_TYPE_NAME      | VARCHAR(64)  | YES     | Tên loại sản phẩm                   |
| PRODUCT_TYPE_REMARK_EN | VARCHAR(256) | NO      | Ghi chú tiếng Anh                   |
| PRODUCT_TYPE_REMARK    | VARCHAR(256) | YES     | Ghi chú tiếng Việt                  |
| PRODUCT_TYPE_ENABLED   | BOOLEAN      | YES     | Trạng thái kích hoạt                |
| PRODUCT_TYPE_DISPLAY   | BOOLEAN      | YES     | Hiển thị trên giao diện             |
| SEO_META_ID            | BIGINT       | NO      | ID metadata SEO                     |
| PRODUCT_TYPE_IMAGE_NAME| VARCHAR(255) | NO      | Tên file ảnh đại diện loại sản phẩm |
| CREATED_DT             | DATETIME     | YES     | Ngày tạo                            |
| MODIFIED_DT            | DATETIME     | YES     | Ngày cập nhật                       |

**Dữ liệu mẫu:**
- PRODUCT_TYPE_ID=1: image (Sản phẩm hình ảnh)
- PRODUCT_TYPE_ID=2: decor (Sản phẩm trang trí)
- PRODUCT_TYPE_ID=3: tools (Dụng cụ vẽ)

### 7. PRODUCT_CATEGORY
**Chức năng:** Phân loại sản phẩm theo danh mục, hỗ trợ phân cấp (category cha-con) và liên kết với loại sản phẩm (type).

**Lưu ý:**
- Mỗi category liên kết với một PRODUCT_TYPE_ID (bắt buộc).
- Hỗ trợ phân cấp category qua PRODUCT_CATEGORY_PARENT_ID (có thể null).
- Có thể đính kèm ảnh đại diện qua PRODUCT_CATEGORY_IMAGE_NAME (tùy chọn).

| Column Name | Type | Require | Description |
|--------------------------|--------|---------|---------------------------------------------|
| PRODUCT_CATEGORY_ID      | BIGINT | YES     | ID duy nhất của danh mục                    |
| PRODUCT_CATEGORY_SLUG    | VARCHAR(64) | YES | URL slug cho SEO                           |
| PRODUCT_CATEGORY_NAME    | VARCHAR(64) | YES | Tên danh mục sản phẩm                      |
| PRODUCT_CATEGORY_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh                        |
| PRODUCT_CATEGORY_REMARK  | VARCHAR(256) | YES | Ghi chú tiếng Việt                         |
| PRODUCT_CATEGORY_ENABLED | BOOLEAN | YES     | Trạng thái kích hoạt                        |
| PRODUCT_CATEGORY_DISPLAY | BOOLEAN | YES     | Hiển thị trên giao diện                     |
| SEO_META_ID              | BIGINT | NO      | ID metadata SEO                             |
| PRODUCT_TYPE_ID          | BIGINT | YES     | ID loại sản phẩm liên kết                   |
| PRODUCT_CATEGORY_PARENT_ID | BIGINT | NO    | ID danh mục cha (nếu là category con)       |
| PRODUCT_CATEGORY_IMAGE_NAME | VARCHAR(255) | NO | Tên file ảnh đại diện category           |
| CREATED_DT               | DATETIME | YES   | Ngày tạo                                    |
| MODIFIED_DT              | DATETIME | YES   | Ngày cập nhật                               |

### 8. PRODUCT_STATE
**Chức năng:** Quản lý trạng thái của sản phẩm

**Dữ liệu mẫu:**
- Name: ACTIVE - Description: Product is active (Sản phẩm đang hoạt động)
- Name: INACTIVE - Description: Product is inactive (Sản phẩm tạm ngưng)
- Name: OUT_OF_STOCK - Description: Product is out of stock (Sản phẩm hết hàng)
- Name: DISCONTINUED - Description: Product is discontinued (Sản phẩm ngưng kinh doanh)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| PRODUCT_STATE_ID | BIGINT | YES | ID duy nhất của trạng thái |
| PRODUCT_STATE_NAME | VARCHAR(64) | YES | Tên trạng thái sản phẩm |
| PRODUCT_STATE_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| PRODUCT_STATE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| PRODUCT_STATE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 9. PRODUCT_ATTR
**Chức năng:** Định nghĩa các thuộc tính của sản phẩm

**Dữ liệu mẫu:**
- Name: SIZE - Description: Product size (Kích thước sản phẩm)
- Name: COLOR - Description: Product color (Màu sắc sản phẩm)
- Name: MATERIAL - Description: Product material (Chất liệu sản phẩm)
- Name: BRAND - Description: Product brand (Thương hiệu sản phẩm)
- Name: WEIGHT - Description: Product weight (Trọng lượng sản phẩm)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| PRODUCT_ATTR_ID | BIGINT | YES | ID duy nhất của thuộc tính |
| PRODUCT_ATTR_NAME | VARCHAR(64) | YES | Tên thuộc tính |
| PRODUCT_ATTR_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| PRODUCT_ATTR_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| PRODUCT_ATTR_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 10. PRODUCT
**Chức năng:** Lưu trữ thông tin chi tiết của sản phẩm

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| PRODUCT_ID | BIGINT | YES | ID duy nhất của sản phẩm |
| PRODUCT_NAME | VARCHAR(100) | YES | Tên sản phẩm |
| PRODUCT_SLUG | VARCHAR(64) | YES | URL slug cho SEO |
| PRODUCT_CATEGORY_ID | BIGINT | YES | ID danh mục sản phẩm |
| PRODUCT_STATE_ID | BIGINT | YES | ID trạng thái sản phẩm |
| PRODUCT_TYPE_ID | BIGINT | YES | ID loại sản phẩm |
| SOLD_QUANTITY | INT | YES | Số lượng đã bán |
| STOCK_QUANTITY | INT | YES | Số lượng tồn kho |
| PRODUCT_DESCRIPTION | TEXT | YES | Mô tả sản phẩm |
| PRODUCT_PRICE | DECIMAL(15,2) | YES | Giá sản phẩm |
| PRODUCT_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| PRODUCT_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| PRODUCT_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| SEO_META_ID | BIGINT | NO | ID metadata SEO |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 11. PRODUCT_IMAGE
**Chức năng:** Liên kết sản phẩm với hình ảnh

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| PRODUCT_IMAGE_ID | BIGINT | YES | ID duy nhất của liên kết |
| PRODUCT_ID | BIGINT | YES | ID sản phẩm |
| IMAGE_ID | BIGINT | YES | ID hình ảnh |
| PRODUCT_IMAGE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| PRODUCT_IMAGE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 12. PRODUCT_ATTRIBUTE
**Chức năng:** Liên kết sản phẩm với thuộc tính

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| PRODUCT_ATTRIBUTE_ID | BIGINT | YES | ID duy nhất của liên kết |
| PRODUCT_ID | BIGINT | YES | ID sản phẩm |
| PRODUCT_ATTR_ID | BIGINT | YES | ID thuộc tính |
| PRODUCT_ATTRIBUTE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| PRODUCT_ATTRIBUTE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| PRODUCT_ATTRIBUTE_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

---

## REVIEW SYSTEM TABLES

### 13. REVIEW
**Chức năng:** Quản lý đánh giá sản phẩm từ khách hàng

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| REVIEW_ID | BIGINT | YES | ID duy nhất của đánh giá |
| USER_ID | BIGINT | YES | ID người dùng đánh giá |
| PRODUCT_ID | BIGINT | YES | ID sản phẩm được đánh giá |
| PARENT_REVIEW_ID | BIGINT | NO | ID đánh giá cha (cho reply) |
| ROOT_REVIEW_ID | BIGINT | NO | ID đánh giá gốc |
| REVIEW_LEVEL | INT | YES | Cấp độ đánh giá (0=root) |
| RATING | TINYINT | YES | Điểm đánh giá (1-5) |
| REVIEW_CONTENT | TEXT | YES | Nội dung đánh giá |
| COUNT_LIKE | INT | YES | Số lượt like |
| IS_VISIBLE | BOOLEAN | YES | Hiển thị công khai |
| IS_DELETED | BOOLEAN | YES | Trạng thái xóa |
| CREATED_BY_ROLE_ID | BIGINT | YES | ID vai trò người tạo |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 14. PRODUCT_REVIEW_LIKE
**Chức năng:** Quản lý lượt like đánh giá sản phẩm

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| PRODUCT_REVIEW_LIKE_ID | BIGINT | YES | ID duy nhất của like |
| REVIEW_ID | BIGINT | YES | ID đánh giá |
| USER_ID | BIGINT | YES | ID người dùng like |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

---

## CART SYSTEM TABLES

### 15. CART_STATE (VIEW)
**Chức năng:** Quản lý trạng thái giỏ hàng

**Dữ liệu mẫu:**
- Name: ACTIVE - Giỏ hàng đang hoạt động
- Name: CHECKED_OUT - Đã đặt hàng toàn bộ sản phẩm
- Name: CHECKED_OUT_PART - Đã đặt hàng một phần sản phẩm
- Name: ABANDONED - Giỏ hàng đã hết hạn hoặc bị bỏ rơi

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| CART_STATE_ID | BIGINT | YES | ID duy nhất của trạng thái |
| CART_STATE_NAME | VARCHAR(64) | YES | Tên trạng thái giỏ hàng |
| CART_STATE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| CART_STATE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| CART_STATE_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 16. CART
**Chức năng:** Lưu trữ thông tin giỏ hàng của người dùng

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| CART_ID | BIGINT | YES | ID duy nhất của giỏ hàng |
| USER_ID | BIGINT | YES | ID người dùng sở hữu |
| SESSION_ID | VARCHAR(100) | NO | ID phiên làm việc |
| CART_SLUG | VARCHAR(64) | YES | URL slug cho SEO |
| CART_STATE_ID | BIGINT | YES | ID trạng thái giỏ hàng |
| TOTAL_AMOUNT | INT | YES | Tổng số lượng sản phẩm |
| CART_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| CART_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| CART_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| SEO_META_ID | BIGINT | NO | ID metadata SEO |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 17. CART_ITEM_STATE (VIEW)
**Chức năng:** Quản lý trạng thái item trong giỏ hàng

**Dữ liệu mẫu:**
- Name: ACTIVE - Description: Active cart item (Sản phẩm trong giỏ hàng đang hoạt động)
- Name: ORDERED - Description: Cart item ordered (Sản phẩm đã được đặt hàng)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| CART_ITEM_STATE_ID | BIGINT | YES | ID duy nhất của trạng thái |
| CART_ITEM_STATE_NAME | VARCHAR(64) | YES | Tên trạng thái item |
| CART_ITEM_STATE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| CART_ITEM_STATE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| CART_ITEM_STATE_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 18. CART_ITEM
**Chức năng:** Lưu trữ các sản phẩm trong giỏ hàng

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| CART_ITEM_ID | BIGINT | YES | ID duy nhất của item |
| CART_ID | BIGINT | YES | ID giỏ hàng |
| PRODUCT_ID | BIGINT | YES | ID sản phẩm |
| CART_ITEM_QUANTITY | INT | YES | Số lượng sản phẩm |
| CART_ITEM_TOTAL_PRICE | DECIMAL(15,2) | YES | Tổng giá tiền |
| CART_ITEM_STATE_ID | BIGINT | YES | ID trạng thái item |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

---

## ORDER MANAGEMENT TABLES

### 19. ORDER_STATE (VIEW)
**Chức năng:** Quản lý trạng thái đơn hàng

**Dữ liệu mẫu:**
- Name: PENDING - Description: Order is pending (Đơn hàng đang chờ xử lý)
- Name: CONFIRMED - Description: Order is confirmed (Đơn hàng đã xác nhận)
- Name: PROCESSING - Description: Order is being processed (Đơn hàng đang xử lý)
- Name: SHIPPED - Description: Order has been shipped (Đơn hàng đã giao cho vận chuyển)
- Name: DELIVERED - Description: Order has been delivered (Đơn hàng đã giao thành công)
- Name: CANCELLED - Description: Order has been cancelled (Đơn hàng đã hủy)
- Name: RETURNED - Description: Order has been returned (Đơn hàng đã trả lại)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| ORDER_STATE_ID | BIGINT | YES | ID duy nhất của trạng thái |
| ORDER_STATE_NAME | VARCHAR(64) | YES | Tên trạng thái đơn hàng |
| ORDER_STATE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| ORDER_STATE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| ORDER_STATE_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 20. DISCOUNT_TYPE (VIEW)
**Chức năng:** Phân loại các kiểu giảm giá

**Dữ liệu mẫu:**
- Name: PERCENTAGE - Description: Percentage discount (Giảm giá theo phần trăm)
- Name: FIXED_AMOUNT - Description: Fixed amount discount (Giảm giá số tiền cố định)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| DISCOUNT_TYPE_ID | BIGINT | YES | ID duy nhất của loại giảm giá |
| DISCOUNT_TYPE_NAME | VARCHAR(64) | YES | Tên loại giảm giá |
| DISCOUNT_TYPE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| DISCOUNT_TYPE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| DISCOUNT_TYPE_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 21. DISCOUNT
**Chức năng:** Quản lý các mã giảm giá

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| DISCOUNT_ID | BIGINT | YES | ID duy nhất của mã giảm giá |
| DISCOUNT_CODE | VARCHAR(100) | YES | Mã giảm giá |
| DISCOUNT_NAME | VARCHAR(64) | YES | Tên chương trình giảm giá |
| DISCOUNT_TYPE_ID | BIGINT | YES | ID loại giảm giá |
| DISCOUNT_VALUE | DECIMAL(15,2) | YES | Giá trị giảm giá |
| MAX_DISCOUNT_AMOUNT | DECIMAL(15,2) | YES | Số tiền giảm tối đa |
| MIN_ORDER_AMOUNT | DECIMAL(15,2) | YES | Giá trị đơn hàng tối thiểu |
| START_AT | DATETIME | YES | Thời gian bắt đầu |
| END_AT | DATETIME | YES | Thời gian kết thúc |
| TOTAL_USAGE_LIMIT | INT | YES | Giới hạn số lần sử dụng |
| USED_COUNT | INT | YES | Số lần đã sử dụng |
| IS_ACTIVE | BOOLEAN | YES | Trạng thái kích hoạt |
| DISCOUNT_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| DISCOUNT_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 22. ORDER
**Chức năng:** Lưu trữ thông tin đơn hàng

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| ORDER_ID | BIGINT | YES | ID duy nhất của đơn hàng |
| USER_ID | BIGINT | YES | ID người đặt hàng |
| ORDER_CODE | VARCHAR(50) | YES | Mã đơn hàng |
| ORDER_SLUG | VARCHAR(64) | YES | URL slug cho SEO |
| CART_ID | BIGINT | YES | ID giỏ hàng gốc |
| ORDER_STATE_ID | BIGINT | YES | ID trạng thái đơn hàng |
| DISCOUNT_ID | BIGINT | NO | ID mã giảm giá (nếu có) |
| TOTAL_AMOUNT | DECIMAL(15,2) | YES | Tổng tiền đơn hàng |
| ORDER_NOTE | TEXT | NO | Ghi chú của khách hàng |
| ORDER_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| ORDER_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| SEO_META_ID | BIGINT | NO | ID metadata SEO |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 23. ORDER_STATE_HISTORY
**Chức năng:** Lưu trữ lịch sử thay đổi trạng thái đơn hàng

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| ORDER_STATE_HISTORY_ID | BIGINT | YES | ID duy nhất của lịch sử |
| ORDER_ID | BIGINT | YES | ID đơn hàng |
| ORDER_STATE_HISTORY_SLUG | VARCHAR(64) | YES | URL slug cho SEO |
| OLD_STATE_ID | BIGINT | YES | ID trạng thái cũ |
| NEW_STATE_ID | BIGINT | YES | ID trạng thái mới |
| ORDER_STATE_HISTORY_NOTE | TEXT | NO | Ghi chú thay đổi |
| ORDER_STATE_HISTORY_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| ORDER_STATE_HISTORY_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| SEO_META_ID | BIGINT | NO | ID metadata SEO |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 24. ORDER_ITEM
**Chức năng:** Lưu trữ chi tiết các sản phẩm trong mỗi đơn hàng

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| ORDER_ITEM_ID | BIGINT | YES | ID duy nhất của item đơn hàng |
| ORDER_ID | BIGINT | YES | ID đơn hàng |
| PRODUCT_ID | BIGINT | YES | ID sản phẩm |
| UNIT_PRICE | DECIMAL(15,2) | YES | Giá đơn vị tại thời điểm đặt hàng |
| ORDER_ITEM_QUANTITY | INT | YES | Số lượng sản phẩm |
| ORDER_ITEM_TOTAL_PRICE | DECIMAL(15,2) | YES | Tổng tiền của item |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

---

## PAYMENT SYSTEM TABLES

### 25. PAYMENT_METHOD (VIEW)
**Chức năng:** Quản lý các phương thức thanh toán

**Dữ liệu mẫu:**
- Name: COD - Description: Cash on Delivery (Thanh toán khi nhận hàng)
- Name: BANK_TRANSFER - Description: Bank Transfer (Chuyển khoản ngân hàng)
- Name: MOMO - Description: MoMo E-wallet (Ví điện tử MoMo)
- Name: ZALOPAY - Description: ZaloPay E-wallet (Ví điện tử ZaloPay)
- Name: VNPAY - Description: VNPay Gateway (Cổng thanh toán VNPay)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| PAYMENT_METHOD_ID | BIGINT | YES | ID duy nhất của phương thức |
| PAYMENT_METHOD_NAME | VARCHAR(64) | YES | Tên phương thức thanh toán |
| PAYMENT_METHOD_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| PAYMENT_METHOD_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| PAYMENT_METHOD_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 26. PAYMENT_STATE (VIEW)
**Chức năng:** Quản lý trạng thái thanh toán

**Dữ liệu mẫu:**
- Name: PENDING - Description: Payment is pending (Thanh toán đang chờ xử lý)
- Name: COMPLETED - Description: Payment completed (Thanh toán thành công)
- Name: FAILED - Description: Payment failed (Thanh toán thất bại)
- Name: REFUNDED - Description: Payment refunded (Thanh toán đã hoàn lại)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| PAYMENT_STATE_ID | BIGINT | YES | ID duy nhất của trạng thái |
| PAYMENT_STATE_NAME | VARCHAR(64) | YES | Tên trạng thái thanh toán |
| PAYMENT_STATE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| PAYMENT_STATE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| PAYMENT_STATE_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 27. PAYMENT
**Chức năng:** Lưu trữ thông tin giao dịch thanh toán

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| PAYMENT_ID | BIGINT | YES | ID duy nhất của giao dịch |
| ORDER_ID | BIGINT | YES | ID đơn hàng |
| PAYMENT_SLUG | VARCHAR(64) | YES | URL slug cho SEO |
| PAYMENT_METHOD_ID | BIGINT | YES | ID phương thức thanh toán |
| PAYMENT_STATE_ID | BIGINT | YES | ID trạng thái thanh toán |
| AMOUNT | DECIMAL(15,2) | YES | Số tiền thanh toán |
| TRANSACTION_ID | VARCHAR(100) | YES | Mã giao dịch |
| PAYMENT_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| PAYMENT_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| SEO_META_ID | BIGINT | NO | ID metadata SEO |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

---

## SHIPPING SYSTEM TABLES

### 28. SHIPPING_FEE_TYPE (VIEW)
**Chức năng:** Phân loại các kiểu phí vận chuyển

**Dữ liệu mẫu:**
- Name: PERCENTAGE - Description: Percentage-based shipping fee (Phí vận chuyển tính theo phần trăm)
- Name: FIXED_AMOUNT - Description: Fixed amount shipping fee (Phí vận chuyển số tiền cố định)
- Name: FREE_SHIPPING - Description: Free shipping (Miễn phí vận chuyển)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| SHIPPING_FEE_TYPE_ID | BIGINT | YES | ID duy nhất của loại phí |
| SHIPPING_FEE_TYPE_NAME | VARCHAR(64) | YES | Tên loại phí vận chuyển |
| SHIPPING_FEE_TYPE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| SHIPPING_FEE_TYPE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| SHIPPING_FEE_TYPE_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 29. SHIPPING_FEE
**Chức năng:** Cấu hình phí vận chuyển theo khoảng giá

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| SHIPPING_FEE_ID | BIGINT | YES | ID duy nhất của cấu hình |
| SHIPPING_FEE_TYPE_ID | BIGINT | YES | ID loại phí vận chuyển |
| MIN_ORDER_PRICE | DECIMAL(15,2) | YES | Giá đơn hàng tối thiểu |
| MAX_ORDER_PRICE | DECIMAL(15,2) | YES | Giá đơn hàng tối đa |
| SHIPPING_FEE_VALUE | DECIMAL(15,2) | YES | Giá trị phí vận chuyển |
| SHIPPING_FEE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| SHIPPING_FEE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| SHIPPING_FEE_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 30. SHIPMENT_STATE (VIEW)
**Chức năng:** Quản lý trạng thái vận chuyển

**Dữ liệu mẫu:**
- Name: PREPARING - Description: Package is being prepared (Đang chuẩn bị hàng)
- Name: SHIPPED - Description: Package has been shipped (Đã giao cho đơn vị vận chuyển)
- Name: IN_TRANSIT - Description: Package is in transit (Đang vận chuyển)
- Name: DELIVERED - Description: Package delivered (Đã giao hàng thành công)
- Name: FAILED_DELIVERY - Description: Delivery failed (Giao hàng thất bại)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| SHIPMENT_STATE_ID | BIGINT | YES | ID duy nhất của trạng thái |
| SHIPMENT_STATE_NAME | VARCHAR(64) | YES | Tên trạng thái vận chuyển |
| SHIPMENT_STATE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| SHIPMENT_STATE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| SHIPMENT_STATE_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 31. SHIPMENT
**Chức năng:** Quản lý thông tin vận chuyển đơn hàng

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| SHIPMENT_ID | BIGINT | YES | ID duy nhất của lô hàng |
| ORDER_ID | BIGINT | YES | ID đơn hàng |
| SHIPMENT_SLUG | VARCHAR(64) | YES | URL slug cho SEO |
| SHIPPING_FEE_ID | BIGINT | YES | ID phí vận chuyển |
| SHIPMENT_STATE_ID | BIGINT | YES | ID trạng thái vận chuyển |
| PHONE | VARCHAR(20) | YES | Số điện thoại nhận hàng |
| ADDRESS | VARCHAR(256) | YES | Địa chỉ nhận hàng |
| SHIPMENT_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| SHIPMENT_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| SEO_META_ID | BIGINT | NO | ID metadata SEO |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

---

## CONTENT MANAGEMENT TABLES

### 32. BLOG_CATEGORY
**Chức năng:** Phân loại bài viết blog

**Dữ liệu mẫu:**
- Name: Tin tức - Description: News (Tin tức về nghệ thuật và trang trí)
- Name: Hướng dẫn - Description: Tutorials (Hướng dẫn vẽ và trang trí)
- Name: Sự kiện - Description: Events (Các sự kiện nghệ thuật)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| BLOG_CATEGORY_ID | BIGINT | YES | ID duy nhất của danh mục |
| BLOG_CATEGORY_SLUG | VARCHAR(64) | YES | URL slug cho SEO |
| BLOG_CATEGORY_NAME | VARCHAR(64) | YES | Tên danh mục blog |
| BLOG_CATEGORY_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| BLOG_CATEGORY_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| BLOG_CATEGORY_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| SEO_META_ID | BIGINT | NO | ID metadata SEO |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 33. BLOG_TYPE
**Chức năng:** Phân loại loại nội dung blog

**Dữ liệu mẫu:**
- Name: Bài viết - Description: Article (Bài viết thông thường)
- Name: Video - Description: Video content (Nội dung video)
- Name: Hình ảnh - Description: Image gallery (Thư viện hình ảnh)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| BLOG_TYPE_ID | BIGINT | YES | ID duy nhất của loại |
| BLOG_TYPE_SLUG | VARCHAR(64) | YES | URL slug cho SEO |
| BLOG_TYPE_NAME | VARCHAR(64) | YES | Tên loại blog |
| BLOG_TYPE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| BLOG_TYPE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| BLOG_TYPE_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| SEO_META_ID | BIGINT | NO | ID metadata SEO |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 34. BLOG
**Chức năng:** Lưu trữ nội dung bài viết blog

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| BLOG_ID | BIGINT | YES | ID duy nhất của bài viết |
| BLOG_CATEGORY_ID | BIGINT | YES | ID danh mục blog |
| BLOG_TYPE_ID | BIGINT | YES | ID loại blog |
| BLOG_TITLE | VARCHAR(256) | YES | Tiêu đề bài viết |
| BLOG_SLUG | VARCHAR(64) | YES | URL slug cho SEO |
| BLOG_CONTENT | LONGTEXT | YES | Nội dung bài viết |
| BLOG_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| BLOG_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| BLOG_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| SEO_META_ID | BIGINT | NO | ID metadata SEO |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

---

## SEO MANAGEMENT TABLES

### 35. SEO_META
**Chức năng:** Lưu trữ metadata SEO cho các đối tượng

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| SEO_META_ID | BIGINT | YES | ID duy nhất của metadata |
| SEO_META_TITLE | VARCHAR(150) | YES | Tiêu đề SEO |
| SEO_META_DESCRIPTION | VARCHAR(500) | YES | Mô tả SEO |
| SEO_META_KEYWORDS | VARCHAR(300) | NO | Từ khóa SEO |
| SEO_META_INDEX | BOOLEAN | YES | Cho phép index bởi search engine |
| SEO_META_FOLLOW | BOOLEAN | YES | Cho phép follow link |
| SEO_META_CANONICAL_URL | VARCHAR(500) | NO | URL canonical |
| SEO_META_IMAGE_NAME | VARCHAR(150) | NO | Tên file hình ảnh SEO |
| SEO_META_SCHEMA_TYPE | VARCHAR(50) | NO | Loại Schema markup |
| SEO_META_CUSTOM_JSON | JSON | NO | Dữ liệu Schema JSON |
| SEO_META_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

---

## CONFIGURATION TABLES

### 36. CONTACT
**Chức năng:** Quản lý thông tin liên hệ của cửa hàng

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| CONTACT_ID | BIGINT | YES | ID duy nhất của liên hệ |
| CONTACT_NAME | VARCHAR(64) | YES | Tên liên hệ |
| CONTACT_SLUG | VARCHAR(64) | YES | URL slug cho SEO |
| CONTACT_ADDRESS | VARCHAR(256) | YES | Địa chỉ |
| CONTACT_EMAIL | VARCHAR(64) | YES | Email liên hệ |
| CONTACT_PHONE | VARCHAR(15) | YES | Số điện thoại |
| CONTACT_FANPAGE | VARCHAR(256) | NO | Link fanpage |
| CONTACT_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CONTACT_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| CONTACT_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| SEO_META_ID | BIGINT | NO | ID metadata SEO |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 37. POLICY
**Chức năng:** Lưu trữ các chính sách và cấu hình nội dung động của website

**Dữ liệu mẫu:**
- POLICY_NAME: MENU_HEADER_TEXT_01 - POLICY_VALUE: Trang chủ (Menu header text 01)
- POLICY_NAME: MENU_HEADER_TEXT_02 - POLICY_VALUE: Tranh vẽ (Menu header text 02)
- POLICY_NAME: MENU_HEADER_TEXT_03 - POLICY_VALUE: Shops (Menu header text 03)
- POLICY_NAME: MENU_HEADER_TEXT_04 - POLICY_VALUE: Blog (Menu header text 04)
- POLICY_NAME: MENU_HEADER_TEXT_05 - POLICY_VALUE: Liên hệ (Menu header text 05)
- POLICY_NAME: HERO_SECTION_TITLE - POLICY_VALUE: Lorem Ipsum (Hero section title)
- POLICY_NAME: HERO_SECTION_SUBTITLE - POLICY_VALUE: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Faucibus in libero risus semper habitant arcu eget. (Hero section subtitle)
- POLICY_NAME: SECTION_TITLE - POLICY_VALUE: LOREM (General section title)
- POLICY_NAME: SECTION_SUBTITLE - POLICY_VALUE: LOREM IPSUM DOLOR SIT AMET. (General section subtitle)
- POLICY_NAME: SECTION_IMAGE_01 - POLICY_VALUE: pho-co-hoi-an.jpg (First section image)
- POLICY_NAME: SECTION_IMAGE_02 - POLICY_VALUE: nui-phu-si.jpg (Second section image)
- POLICY_NAME: SECTION_DESCRIPTION_01 - POLICY_VALUE: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Faucibus in libero risus 01... (First section description)
- POLICY_NAME: SECTION_DESCRIPTION_02 - POLICY_VALUE: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Faucibus in libero risus 02... (Second section description)
- POLICY_NAME: WEBSITE_LOGO_ALT_TEXT - POLICY_VALUE: Maison Art (Website logo alt text)
- POLICY_NAME: FOOTER_ABOUT_TEXT - POLICY_VALUE: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean commodo ligula eget dolor. (Footer about text)
- POLICY_NAME: FOOTER_COPYRIGHT_TEXT - POLICY_VALUE: © 2025 Palette & Co. Giữ toàn quyền. (Footer copyright text)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| POLICY_ID | BIGINT | YES | ID duy nhất của chính sách |
| POLICY_NAME | VARCHAR(64) | YES | Tên chính sách |
| POLICY_SLUG | VARCHAR(64) | NO | URL slug cho SEO |
| POLICY_VALUE | TEXT | YES | Nội dung chính sách |
| POLICY_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| POLICY_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| POLICY_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

---

## PAGE MANAGEMENT TABLES

### 39. PAGE_POSITION
**Chức năng:** Quản lý vị trí hiển thị của các trang

**Dữ liệu mẫu:**
- Name: Header Navigation - Description: Pages shown in header navigation menu (Các trang hiển thị trong menu điều hướng header)
- Name: Footer Links - Description: Pages shown in footer section (Các trang hiển thị trong phần footer)
- Name: Sidebar Menu - Description: Pages shown in sidebar navigation (Các trang hiển thị trong menu sidebar)
- Name: Main Menu - Description: Pages shown in main navigation menu (Các trang hiển thị trong menu điều hướng chính)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| PAGE_POSITION_ID | BIGINT | YES | ID duy nhất của vị trí trang |
| PAGE_POSITION_SLUG | VARCHAR(64) | YES | URL slug cho SEO |
| PAGE_POSITION_NAME | VARCHAR(100) | YES | Tên vị trí hiển thị |
| PAGE_POSITION_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| PAGE_POSITION_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| PAGE_POSITION_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 40. PAGE_GROUP
**Chức năng:** Quản lý nhóm các trang theo chức năng

**Dữ liệu mẫu:**
- Name: Cửa hàng - Description: Shop related pages (Các trang liên quan đến cửa hàng)
- Name: Hỗ trợ - Description: Customer support pages (Các trang hỗ trợ khách hàng)
- Name: Chính sách - Description: Company policies and terms (Các chính sách và điều khoản của công ty)
- Name: Giới thiệu - Description: About us and company information (Thông tin giới thiệu về công ty)
- Name: Dịch vụ - Description: Service related pages (Các trang liên quan đến dịch vụ)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| PAGE_GROUP_ID | BIGINT | YES | ID duy nhất của nhóm trang |
| PAGE_GROUP_SLUG | VARCHAR(64) | YES | URL slug cho SEO |
| PAGE_GROUP_NAME | VARCHAR(100) | YES | Tên nhóm trang |
| PAGE_GROUP_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| PAGE_GROUP_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| PAGE_GROUP_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

### 41. PAGE
**Chức năng:** Lưu trữ thông tin các trang static như footer, policy, về chúng tôi, v.v.

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| PAGE_ID | BIGINT | YES | ID duy nhất của trang |
| PAGE_POSITION_ID | BIGINT | YES | ID vị trí hiển thị |
| PAGE_GROUP_ID | BIGINT | YES | ID nhóm trang |
| TARGET_URL | VARCHAR(256) | NO | URL đích (cho link external) |
| PAGE_SLUG | VARCHAR(64) | YES | URL slug cho SEO |
| PAGE_NAME | VARCHAR(100) | YES | Tên hiển thị của trang |
| PAGE_CONTENT | LONGTEXT | NO | Nội dung HTML của trang |
| PAGE_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| PAGE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| PAGE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

---

## SYSTEM MANAGEMENT TABLES

### 38. RESPONSE_CODE
**Chức năng:** Quản lý mã phản hồi API

**Dữ liệu mẫu:**
- Name: 200 - Description: Success (Thành công)
- Name: 201 - Description: Created successfully (Tạo thành công)
- Name: 400 - Description: Bad request (Yêu cầu không hợp lệ)
- Name: 401 - Description: Unauthorized (Không có quyền truy cập)
- Name: 404 - Description: Not found (Không tìm thấy)
- Name: 500 - Description: Internal server error (Lỗi máy chủ nội bộ)

| Column Name | Type | Require | Description |
|-------------|------|---------|-------------|
| RESPONSE_CODE_ID | BIGINT | YES | ID duy nhất của mã phản hồi |
| RESPONSE_CODE_NAME | VARCHAR(64) | YES | Tên mã phản hồi |
| RESPONSE_CODE_REMARK_EN | VARCHAR(256) | NO | Ghi chú tiếng Anh |
| RESPONSE_CODE_REMARK | VARCHAR(256) | YES | Ghi chú tiếng Việt |
| RESPONSE_CODE_ENABLED | BOOLEAN | YES | Trạng thái kích hoạt |
| CREATED_DT | DATETIME | YES | Ngày tạo |
| MODIFIED_DT | DATETIME | YES | Ngày cập nhật |

---

## DATABASE FEATURES

### Triggers
- **tr_update_review_count_like**: Tự động cập nhật số lượt like khi có like mới
- **tr_decrease_review_count_like**: Tự động giảm số lượt like khi xóa like
- **tr_update_discount_used_count**: Tự động cập nhật số lần sử dụng mã giảm giá

### Indexes
- Primary keys cho tất cả các bảng
- Foreign keys với indexes
- Unique indexes cho các trường duy nhất
- Composite indexes cho các query phổ biến
- Fulltext index cho tìm kiếm SEO

### Constraints
- Foreign key constraints với ON DELETE actions
- Check constraints cho rating (1-5)
- Unique constraints cho business rules
- NOT NULL constraints cho required fields

### SEO Features
- URL slugs cho tất cả content
- Comprehensive SEO metadata
- Schema.org markup support
- Canonical URLs
- Meta tags optimization