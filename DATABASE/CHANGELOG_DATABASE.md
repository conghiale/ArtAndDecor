
# Thay đổi cấu trúc database ART_AND_DECOR (v1.0)

## Tóm tắt thay đổi

- **Xóa bảng IMAGE_CATEGORY**
  - Loại bỏ hoàn toàn bảng IMAGE_CATEGORY và các dữ liệu mẫu liên quan.

- **Bổ sung phân cấp cho PRODUCT_CATEGORY**
  - Thêm cột PRODUCT_CATEGORY_PARENT_ID để hỗ trợ category cha-con.

- **Liên kết PRODUCT_CATEGORY với PRODUCT_TYPE**
  - Thêm cột PRODUCT_TYPE_ID (bắt buộc) vào PRODUCT_CATEGORY để mỗi category thuộc về một loại sản phẩm cụ thể.

- **Thêm cột ảnh đại diện (image name)**
  - Thêm cột PRODUCT_CATEGORY_IMAGE_NAME (tùy chọn) vào PRODUCT_CATEGORY.
  - Thêm cột PRODUCT_TYPE_IMAGE_NAME (tùy chọn) vào PRODUCT_TYPE.

- **Cập nhật lại các lệnh INSERT mẫu**
  - Điều chỉnh dữ liệu mẫu cho các bảng liên quan để phù hợp với cấu trúc mới.
  
- **Xoá bảng SEO_META_CATEGORY**
  - Loại bỏ hoàn toàn bảng SEO_META_CATEGORY và các dữ liệu mẫu liên quan.
  
- **Xoá bảng IMAGE_FORMAT**
  - Loại bỏ hoàn toàn bảng IMAGE_FORMAT và các dữ liệu mẫu liên quan.

- **Xoá bảng IMAGE_FORMAT**
  - Loại bỏ cột SEO_META và các dữ liệu mẫu liên quan trong bảng IMAGE.

## Lưu ý
- Các thay đổi này giúp quản lý phân loại sản phẩm linh hoạt hơn, hỗ trợ hiển thị hình ảnh đại diện và phân cấp danh mục rõ ràng.
- Các truy vấn, API liên quan đến category/type cần cập nhật lại cho đúng cấu trúc mới.

# Thay đổi cấu trúc database ART_AND_DECOR (v1.1)
- **Xoá bảng IMAGE_FORMAT**
  - Thêm cột PRODUCT_IMAGE_PRIMARY trong bảng PRODUCT_IMAGE ("Mỗi sản phẩm có thể có nhiều hình ảnh, nhưng chỉ một hình ảnh được đánh dấu là hình ảnh chính (PRIMARY=TRUE). Hình ảnh chính sẽ được hiển thị đầu tiên trên trang chi tiết sản phẩm.")

# Thay đổi cấu trúc database ART_AND_DECOR (v1.3)
- **Bảng ORDER**
  - Thêm cột `DISCOUNT_CODE` VARCHAR(50) NULL (Snapshot mã giảm giá tại thời điểm đặt hàng)
  - Thêm cột `DISCOUNT_TYPE` VARCHAR(100) NULL (Snapshot loại giảm giá: PERCENTAGE hoặc FIXED_AMOUNT)
  - Thêm cột `DISCOUNT_VALUE` DECIMAL(15,2) NULL (Snapshot giá trị giảm giá tại thời điểm đặt hàng)
  - **Mục đích**: Lưu trữ snapshot thông tin discount tại thời điểm đặt hàng để đảm bảo tính nhất quán dữ liệu, tránh trường hợp discount bị thay đổi hoặc xóa sau này ảnh hưởng đến dữ liệu đơn hàng lịch sử
  - **Cập nhật**: Đồng bộ UPDATE INSERT_SAMPLE_DATA.sql và Java code tương ứng (Order.java, OrderDto.java, OrderMapperUtil.java)

# Thay đổi cấu trúc database ART_AND_DECOR (v1.2)
- **Bảng PRODUCT**
  - Thêm cột `PRODUCT_CODE` VARCHAR(64) NOT NULL UNIQUE
  - Thêm cột `PRODUCT_FEATURED` BOOLEAN NOT NULL DEFAULT FALSE (Sản phẩm nổi bật)
  - Thêm cột `PRODUCT_HIGHLIGHTED` BOOLEAN NOT NULL DEFAULT FALSE (Sản phẩm tiêu biểu)
- **Bảng ORDER_ITEM**
  - Thêm cột `PRODUCT_CODE` VARCHAR(64) NOT NULL (Snapshot)
  - Thêm cột `PRODUCT_CATEGORY_NAME` VARCHAR(100) NOT NULL (Snapshot)
  - Thêm cột `PRODUCT_TYPE_NAME` VARCHAR(100) NOT NULL (Snapshot)
  - Thêm cột `PRODUCT_ATTR_JSON` JSON NULL (Snapshot thuộc tính sản phẩm)
- **Bảng CART**
  - Chỉnh sửa tên cột TOTAL_AMOUNT thành TOTAL_QUANTITY
- **Bảng USER**
  - Thêm ràng buộc cột EMAIL thành NOT NULL UNIQUE, USER_NAME NOT NULL UNIQUE
- **Bảng PRODUCT_ATTRIBUTE**
  - Thêm ràng buộc cột PRODUCT_ATTRIBUTE_VALUE thành NOT NULL
- **Bảng REVIEW**
  - Bỏ cột `CREATED_BY_ROLE_ID` BIGINT NOT NULL
- **Bảng PAYMENT**
  - Bỏ cột `SEO_META_ID` BIGINT NULL

# Thay đổi cấu trúc database ART_AND_DECOR (v1.3)
- **Bảng PRODUCT**
  - Xóa cột `PRODUCT_REMARK` TEXT NULL
- **Bảng ORDER**  
  - Thêm cột `CUSTOMER_NAME` VARCHAR(150) NULL (Snapshot tên khách hàng từ bảng USER)
  - Thêm cột `CUSTOMER_ADDRESS` TEXT NULL (Snapshot địa chỉ khách hàng)
  - Đổi tên cột `CUSTOMER_NAME` thành `RECEIVER_NAME` VARCHAR(150) NULL
  - Đổi tên cột `CUSTOMER_PHONE` thành `RECEIVER_PHONE` VARCHAR(20) NULL  
  - Đổi tên cột `CUSTOMER_EMAIL` thành `RECEIVER_EMAIL` VARCHAR(150) NULL
  - Đổi tên cột `CUSTOMER_ADDRESS` thành `RECEIVER_ADDRESS` TEXT NULL
  - Đổi tên cột `SHIPPING_FEE` thành `SHIPPING_FEE_AMOUNT` DECIMAL(15,2) NOT NULL DEFAULT 0
- **Cập nhật dữ liệu mẫu**
  - Điều chỉnh lệnh INSERT INTO `ORDER` trong INSERT_SAMPLE_DATA.sql để phù hợp với cấu trúc mới
  
- **Bảng ORDER**
  - Thêm cột `CUSTOMER_NAME` VARCHAR(150) NULL (Snapshot tên khách hàng từ bảng USER)
- **Cập nhật dữ liệu mẫu**
  - Điều chỉnh lệnh INSERT INTO `ORDER` trong INSERT_SAMPLE_DATA.sql để bao gồm cột CUSTOMER_NAME

# Thay đổi cấu trúc database ART_AND_DECOR (v1.4)
- **Loại bỏ bảng RESPONSE_CODE**
  - Xóa hoàn toàn bảng RESPONSE_CODE và dữ liệu mẫu liên quan từ CREATE_DB_ART_AND_DECOR.sql.
  - Lý do: Bảng này không được sử dụng trong business logic, chỉ dùng cho logging và đã có fallback mechanism.

# Thay đổi cấu trúc database ART_AND_DECOR (v1.5)

## Tóm tắt thay đổi

### **Cấu trúc bảng (CREATE_DB_ART_AND_DECOR.sql)**

- **Bảng ORDER**
  - ✅ Thêm cột `CUSTOMER_PHONE_NUMBER` VARCHAR(15) NULL (Snapshot số điện thoại khách hàng từ bảng USER)
  - ✅ Thêm cột `CUSTOMER_EMAIL` VARCHAR(100) NULL (Snapshot email khách hàng từ bảng USER)  
  - ✅ Thêm cột `CUSTOMER_ADDRESS` TEXT NULL (Snapshot địa chỉ khách hàng từ bảng USER)

- **Bảng ORDER_STATE_HISTORY**
  - ✅ Thêm cột `MODIFIED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### **Dữ liệu mẫu (INSERT_SAMPLE_DATA.sql)**

- **ORDER table**
  - ✅ Cập nhật INSERT statement với 3 cột mới: `CUSTOMER_PHONE_NUMBER`, `CUSTOMER_EMAIL`, `CUSTOMER_ADDRESS`
  - ✅ Điền dữ liệu snapshot tương ứng từ thông tin USER cho 3 đơn hàng mẫu

### **Documentation (DATABASE_DEVELOPMENT_GUIDE.md)**

- **Tổng quan**
  - ✅ Cập nhật số lượng bảng từ 30 → 39 bảng
  - ✅ Nâng cấp phiên bản từ v1.4 → v1.5

- **Bảng ORDER (section 22)**
  - ✅ Thêm miêu tả đầy đủ 3 cột snapshot mới
  - ✅ Cập nhật lưu ý về snapshot thông tin khách hàng vs người nhận

- **Bảng ORDER_STATE_HISTORY (section 23)**
  - ✅ Thêm cột `MODIFIED_DT` 
  - ❌ Loại bỏ cột `ORDER_STATE_HISTORY_SLUG` (không tồn tại trong SQL)

- **Bảng PAYMENT (section 27)**
  - ✅ Thêm cột `PAYMENT_REMARK` VARCHAR(256) YES

- **Bảng CART (section 16)**
  - ✅ Sửa `USER_ID` từ YES → NO (hỗ trợ guest checkout)
  - ✅ Cải thiện mô tả `SESSION_ID` (for guest checkout)

- **Bảng BLOG_CATEGORY (section 32)**
  - ✅ Thêm cột `BLOG_TYPE_ID` BIGINT NULL
  - ✅ Thêm cột `IMAGE_ID` BIGINT NULL

- **Bảng BLOG_TYPE (section 33)**  
  - ✅ Thêm cột `IMAGE_ID` BIGINT NULL

- **Bảng BLOG (section 34)**
  - ❌ Loại bỏ cột `BLOG_TYPE_ID` (không tồn tại trong SQL)
  - ✅ Thêm cột `BLOG_REMARK` VARCHAR(256) YES
  - ✅ Thêm cột `SEO_META_ID` BIGINT NULL

- **Bảng SHIPMENT (section 31) - CẬP NHẬT HOÀN TOÀN**
  - ❌ Loại bỏ: `SHIPMENT_SLUG`, `SHIPPING_FEE_ID`, `PHONE`, `ADDRESS`
  - ✅ Thêm mới: `SHIPMENT_CODE`, `RECEIVER_NAME`, `RECEIVER_EMAIL`
  - ✅ Thêm mới: `ADDRESS_LINE`, `CITY`, `DISTRICT`, `WARD`, `COUNTRY` 
  - ✅ Thêm mới: `SHIPPING_FEE_AMOUNT`, `SHIPPED_AT`, `DELIVERED_AT`, `SHIPMENT_REMARK`

- **Bảng CONTACT (section 35)**
  - ✅ Thêm cột `CONTACT_REMARK` VARCHAR(256) YES
  - ✅ Thêm cột `SEO_META_ID` BIGINT NULL

## Lợi ích của thay đổi v1.5
- **Snapshot đầy đủ thông tin khách hàng** trong ORDER: tên, phone, email, địa chỉ
- **Tính nhất quán 100%** giữa SQL schema và documentation (39/39 bảng)
- **Lịch sử thay đổi đầy đủ** với MODIFIED_DT trong ORDER_STATE_HISTORY
- **Cấu trúc vận chuyển chi tiết** với thông tin địa chỉ đầy đủ và tracking thời gian
- **Hỗ trợ guest checkout** với USER_ID nullable trong CART
- **SEO optimization** với metadata đầy đủ cho BLOG và CONTACT

---