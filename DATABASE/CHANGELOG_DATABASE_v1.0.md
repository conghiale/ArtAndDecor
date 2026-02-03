
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
