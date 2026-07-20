# Prompt cho Copilot Agent - Tạo Stored Procedure xoá dữ liệu và check dữ liệu liên quan

Bạn là Copilot Agent. Hãy dựa vào file database schema hiện tại `CREATE_DB_ART_AND_DECOR.sql` để tạo các MySQL Stored Procedure phục vụ xoá dữ liệu an toàn theo từng bảng cụ thể.

Tôi có 2 nhóm yêu cầu chính:

1. Tạo Stored Procedure xoá record cho từng bảng.
2. Tạo Stored Procedure check tương ứng để kiểm tra record liên quan trước/sau khi xoá.

Không tạo API Java. Không tạo Unit Test. Không tạo Integration Test. Chỉ tạo file SQL Stored Procedure.

---

## 1. Vị trí lưu file

Tất cả các file Stored Procedure phải được lưu tại thư mục:

```text
\DATABASE\STORE_OPERATION
```

Nếu thư mục chưa tồn tại, hãy tạo thư mục này trước.

Cấu trúc mong muốn:

```text
\DATABASE
  \STORE_OPERATION
    SP_DELETE_USER_SAFE.sql
    SP_DELETE_PRODUCT_TYPE_SAFE.sql
    SP_DELETE_PRODUCT_CATEGORY_SAFE.sql
    SP_DELETE_PRODUCT_SAFE.sql
    SP_DELETE_PRODUCT_ATTR_SAFE.sql
    SP_DELETE_PRODUCT_ATTRIBUTE_SAFE.sql
    SP_DELETE_PRODUCT_VARIANT_SAFE.sql
    SP_DELETE_BLOG_TYPE_SAFE.sql
    SP_DELETE_BLOG_CATEGORY_SAFE.sql
    SP_DELETE_BLOG_SAFE.sql

    SP_CHECK_DELETE_USER_SAFE.sql
    SP_CHECK_DELETE_PRODUCT_TYPE_SAFE.sql
    SP_CHECK_DELETE_PRODUCT_CATEGORY_SAFE.sql
    SP_CHECK_DELETE_PRODUCT_SAFE.sql
    SP_CHECK_DELETE_PRODUCT_ATTR_SAFE.sql
    SP_CHECK_DELETE_PRODUCT_ATTRIBUTE_SAFE.sql
    SP_CHECK_DELETE_PRODUCT_VARIANT_SAFE.sql
    SP_CHECK_DELETE_BLOG_TYPE_SAFE.sql
    SP_CHECK_DELETE_BLOG_CATEGORY_SAFE.sql
    SP_CHECK_DELETE_BLOG_SAFE.sql
```

Không lưu các file store rải rác ở thư mục khác.

---

## 2. Mục tiêu tổng thể

Tôi cần các Stored Procedure riêng cho từng bảng sau:

### 2.1. Store xoá dữ liệu

Tạo các file SQL riêng trong thư mục `\DATABASE\STORE_OPERATION`:

```text
SP_DELETE_USER_SAFE.sql
SP_DELETE_PRODUCT_TYPE_SAFE.sql
SP_DELETE_PRODUCT_CATEGORY_SAFE.sql
SP_DELETE_PRODUCT_SAFE.sql
SP_DELETE_PRODUCT_ATTR_SAFE.sql
SP_DELETE_PRODUCT_ATTRIBUTE_SAFE.sql
SP_DELETE_PRODUCT_VARIANT_SAFE.sql
SP_DELETE_BLOG_TYPE_SAFE.sql
SP_DELETE_BLOG_CATEGORY_SAFE.sql
SP_DELETE_BLOG_SAFE.sql
```

Mỗi file chứa:

```sql
DROP PROCEDURE IF EXISTS ...
CREATE PROCEDURE ...
```

### 2.2. Store check dữ liệu liên quan

Tạo thêm các file SQL check riêng trong thư mục `\DATABASE\STORE_OPERATION`:

```text
SP_CHECK_DELETE_USER_SAFE.sql
SP_CHECK_DELETE_PRODUCT_TYPE_SAFE.sql
SP_CHECK_DELETE_PRODUCT_CATEGORY_SAFE.sql
SP_CHECK_DELETE_PRODUCT_SAFE.sql
SP_CHECK_DELETE_PRODUCT_ATTR_SAFE.sql
SP_CHECK_DELETE_PRODUCT_ATTRIBUTE_SAFE.sql
SP_CHECK_DELETE_PRODUCT_VARIANT_SAFE.sql
SP_CHECK_DELETE_BLOG_TYPE_SAFE.sql
SP_CHECK_DELETE_BLOG_CATEGORY_SAFE.sql
SP_CHECK_DELETE_BLOG_SAFE.sql
```

Mỗi store check dùng để kiểm tra:

```text
Record chính còn tồn tại hay không.
Các bảng con/liên quan còn record hay không.
Mỗi bảng liên quan còn bao nhiêu record.
SEO_META tương ứng còn tồn tại hay không nếu record chính có SEO_META_ID.
```

Store check chỉ được SELECT, không được DELETE/UPDATE/INSERT.

---

## 3. Input mong muốn

Input của tôi phải đơn giản.

Mỗi Stored Procedure xoá chỉ nhận:

```sql
IN p_id BIGINT
```

Ví dụ:

```sql
CALL SP_DELETE_PRODUCT_SAFE(10);
CALL SP_DELETE_USER_SAFE(5);
CALL SP_DELETE_BLOG_SAFE(7);
```

Mỗi Stored Procedure check cũng chỉ nhận:

```sql
IN p_id BIGINT
```

Ví dụ:

```sql
CALL SP_CHECK_DELETE_PRODUCT_SAFE(10);
CALL SP_CHECK_DELETE_USER_SAFE(5);
CALL SP_CHECK_DELETE_BLOG_SAFE(7);
```

Không thêm các input như:

```text
p_deleted_by
p_delete_mode
p_dry_run
p_allow_business_delete
p_remark
```

Nếu cần comment thì ghi trong file SQL, không thêm input.

---

## 4. Nguyên tắc xoá dữ liệu

Mong muốn của tôi:

```text
Khi truyền ID record cần xoá, store sẽ xoá record đó và các record liên quan theo đúng quan hệ database.
Mục tiêu là clear database, không để dữ liệu mồ côi, không làm mất toàn vẹn dữ liệu.
```

Cần tận dụng các FK hiện tại:

```text
ON DELETE CASCADE
ON DELETE SET NULL
ON DELETE RESTRICT
```

Nhưng phải hiểu đúng:

1. Với `ON DELETE CASCADE`:
   - Có thể tận dụng DB để tự xoá record con.
   - Tuy nhiên nếu muốn trả về số record bị xoá thì phải COUNT trước khi DELETE.

2. Với `ON DELETE SET NULL`:
   - DB sẽ không xoá record con, chỉ set FK về NULL.
   - Nếu nghiệp vụ của store cần clear dữ liệu liên quan thì phải chủ động xoá record liên quan trước, không chỉ để DB set NULL.
   - Nếu đó là dữ liệu không nên xoá, có thể để DB SET NULL, nhưng phải ghi rõ trong result set.

3. Với `ON DELETE RESTRICT`:
   - DB sẽ chặn xoá nếu còn record con.
   - Nếu mong muốn xoá sạch dữ liệu liên quan, store phải chủ động xoá record con trước theo đúng thứ tự child -> parent.
   - Nếu record con là dữ liệu master/shared không nên xoá thì không xoá ngược lên parent.

---

## 5. Quy tắc xử lý SEO_META

Rất quan trọng:

```text
Trong hệ thống này, 1 record SEO_META chỉ được sử dụng bởi 1 record nghiệp vụ.
```

Vì vậy, khi xoá record chính có `SEO_META_ID`, store phải xoá luôn record `SEO_META` tương ứng.

Các bảng có `SEO_META_ID` cần xử lý xoá kèm:

```text
PRODUCT_TYPE.SEO_META_ID
PRODUCT_CATEGORY.SEO_META_ID
PRODUCT.SEO_META_ID
BLOG_TYPE.SEO_META_ID
BLOG_CATEGORY.SEO_META_ID
BLOG.SEO_META_ID
```

Quy trình bắt buộc trong store xoá:

```text
1. Validate p_id.
2. Kiểm tra record chính tồn tại.
3. Lấy SEO_META_ID của record chính và lưu vào biến local.
4. COUNT các bảng liên quan trước khi xoá để trả result set.
5. Xoá dữ liệu liên quan theo đúng thứ tự.
6. Xoá record chính.
7. Xoá SEO_META tương ứng nếu SEO_META_ID không null.
8. Commit transaction.
```

Không xoá `IMAGE` theo cách tương tự. Nếu record có `IMAGE_ID`, không tự xoá `IMAGE`, vì `IMAGE` có thể liên quan đến file vật lý hoặc được dùng bởi nghiệp vụ khác.

---

## 6. Result set mong muốn

Kết quả trả về không cần quá chi tiết, nhưng phải chuyên nghiệp và đủ để biết store đã làm gì.

Mỗi store xoá trả về 1 result set tổng quan:

```sql
SELECT
    TRUE/FALSE AS success,
    'COMMITTED | ROLLED_BACK | VALIDATION_FAILED | NOT_FOUND' AS transaction_status,
    'Tên bảng chính' AS target_table,
    p_id AS target_id,
    v_deleted_main_count AS deleted_main_count,
    v_deleted_related_count AS deleted_related_count,
    v_deleted_seo_meta_count AS deleted_seo_meta_count,
    v_total_deleted_count AS total_deleted_count,
    'Message tiếng Việt' AS message;
```

Không cần trả quá nhiều result set nhỏ nếu không cần.

Nếu lỗi:

```sql
SELECT
    FALSE AS success,
    'ROLLED_BACK' AS transaction_status,
    'PRODUCT' AS target_table,
    p_id AS target_id,
    0 AS deleted_main_count,
    0 AS deleted_related_count,
    0 AS deleted_seo_meta_count,
    0 AS total_deleted_count,
    'Có lỗi xảy ra trong quá trình xoá. Toàn bộ dữ liệu đã được rollback.' AS message;
```

Mỗi store check trả về result set dạng:

```sql
SELECT
    'TABLE_NAME' AS table_name,
    'COLUMN_NAME' AS reference_column,
    COUNT(*) AS record_count
FROM ...
```

Hoặc trả nhiều dòng bằng `UNION ALL`, ví dụ:

```sql
SELECT 'USER' AS table_name, 'USER_ID' AS reference_column, COUNT(*) AS record_count FROM USER WHERE USER_ID = p_id
UNION ALL
SELECT 'REVIEW', 'USER_ID', COUNT(*) FROM REVIEW WHERE USER_ID = p_id
UNION ALL
SELECT 'WISHLIST', 'USER_ID', COUNT(*) FROM WISHLIST WHERE USER_ID = p_id;
```

---

## 7. Validate input bắt buộc

Mỗi store xoá và store check phải validate:

```text
p_id không được NULL.
p_id phải > 0.
Record chính phải tồn tại nếu là store xoá.
```

Nếu `p_id` invalid:

```text
Không xoá gì.
Không update gì.
Không xoá SEO_META.
Trả result set báo lỗi.
```

---

## 8. Transaction và rollback

Mỗi store xoá phải chạy trong transaction:

```sql
START TRANSACTION;
...
COMMIT;
```

Bắt buộc có handler:

```sql
DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
    ROLLBACK;
    SELECT
        FALSE AS success,
        'ROLLED_BACK' AS transaction_status,
        '<TARGET_TABLE>' AS target_table,
        p_id AS target_id,
        0 AS deleted_main_count,
        0 AS deleted_related_count,
        0 AS deleted_seo_meta_count,
        0 AS total_deleted_count,
        'Có lỗi xảy ra trong quá trình xoá. Toàn bộ dữ liệu đã được rollback.' AS message;
END;
```

Yêu cầu:

```text
Nếu xoá child thành công nhưng xoá parent lỗi thì rollback toàn bộ.
Nếu xoá parent thành công nhưng xoá SEO_META lỗi thì rollback toàn bộ.
Không để xoá một phần dữ liệu.
Không để database mất toàn vẹn.
```

---

## 9. Yêu cầu performance

Hạn chế query hoặc xử lý logic ảnh hưởng performance.

Yêu cầu:

```text
Không dùng cursor nếu không cần.
Không dùng vòng lặp phức tạp nếu có thể xoá theo set-based DELETE.
Không dùng dynamic SQL.
Không dùng INFORMATION_SCHEMA trong runtime nếu không cần.
Không SELECT *.
Không load dữ liệu lớn.
Chỉ dùng COUNT(*) theo các cột FK đã có index hoặc nên có index.
Đếm trước khi xoá để trả result set.
Xoá theo điều kiện FK cụ thể.
```

Ưu tiên:

```sql
DELETE FROM CHILD_TABLE WHERE FK_ID = p_id;
```

Không làm:

```sql
SELECT * FROM CHILD_TABLE WHERE FK_ID = p_id;
```

---

## 10. Không được làm

Không được:

```text
Không tạo store generic xoá mọi table.
Không dùng dynamic SQL xoá table bất kỳ.
Không xoá IMAGE.
Không xoá file vật lý.
Không xoá ngược lên các bảng master/shared không liên quan.
Không xoá PRODUCT_TYPE khi xoá PRODUCT_CATEGORY.
Không xoá PRODUCT_CATEGORY khi xoá PRODUCT.
Không xoá PRODUCT_ATTR khi xoá PRODUCT_ATTRIBUTE.
Không xoá BLOG_TYPE khi xoá BLOG_CATEGORY.
Không xoá BLOG_CATEGORY khi xoá BLOG.
Không xoá USER_ROLE khi xoá USER.
Không xoá USER_PROVIDER khi xoá USER.
Không xoá PRODUCT_STATE khi xoá PRODUCT.
Không xoá CART_STATE/CART_ITEM_STATE/ORDER_STATE/PAYMENT_STATE/PAYMENT_METHOD.
Không bỏ qua rollback.
Không xoá một phần rồi báo success.
Không tạo API Java.
Không tạo Unit Test.
Không tạo Integration Test.
```

---

## 11. Stored Procedure xoá theo từng bảng

### 11.1. `SP_DELETE_USER_SAFE.sql`

Procedure:

```sql
CALL SP_DELETE_USER_SAFE(p_id);
```

Bảng chính:

```text
USER
```

Cần xoá/handle các bảng liên quan đến `USER_ID`:

```text
PRODUCT_REVIEW_LIKE.USER_ID
REVIEW.USER_ID
WISHLIST.USER_ID
CART.USER_ID
ORDERS.USER_ID
ORDER_STATE_HISTORY.CHANGED_BY_USER_ID
```

Yêu cầu xoá:

```text
Xoá dữ liệu liên quan để clear database, không chỉ để DB SET NULL nếu có thể xoá an toàn.
Phải xử lý theo thứ tự child -> parent.
```

Gợi ý thứ tự:

```text
1. Xoá PRODUCT_REVIEW_LIKE theo USER_ID.
2. Xoá PRODUCT_REVIEW_LIKE theo các REVIEW của USER nếu cần.
3. Xoá REVIEW con/like liên quan nếu REVIEW.USER_ID = p_id.
4. Xoá WISHLIST theo USER_ID.
5. Xoá CART liên quan đến USER_ID nếu muốn clear cart.
   - CART_ITEM_ATTRIBUTE sẽ tự xoá theo CART_ITEM nếu CART_ITEM bị xoá.
   - CART_ITEM sẽ tự xoá theo CART nếu FK CASCADE.
6. Với ORDERS liên quan USER_ID:
   - Cần xử lý PAYMENT trước vì PAYMENT.ORDER_ID là RESTRICT.
   - Sau đó xoá ORDERS.
   - ORDER_ITEM, ORDER_STATE_HISTORY, SHIPMENT sẽ tự xoá theo ORDERS nếu FK CASCADE.
7. Xoá hoặc xử lý ORDER_STATE_HISTORY.CHANGED_BY_USER_ID nếu còn.
8. Xoá USER.
```

Không xoá:

```text
USER_PROVIDER
USER_ROLE
ORDER_STATE
PAYMENT_METHOD
PAYMENT_STATE
SHIPMENT_STATE
```

Result count cần có:

```text
deleted_user_count
deleted_review_count
deleted_product_review_like_count
deleted_wishlist_count
deleted_cart_count
deleted_order_count
deleted_payment_count
deleted_shipment_count
deleted_order_item_count
deleted_order_state_history_count
total_deleted_count
```

---

### 11.2. `SP_DELETE_PRODUCT_TYPE_SAFE.sql`

Procedure:

```sql
CALL SP_DELETE_PRODUCT_TYPE_SAFE(p_id);
```

Bảng chính:

```text
PRODUCT_TYPE
```

Có `SEO_META_ID`, cần xoá SEO_META tương ứng sau khi xoá `PRODUCT_TYPE`.

Quan hệ chính:

```text
PRODUCT_CATEGORY.PRODUCT_TYPE_ID
```

Mong muốn:

```text
Xoá PRODUCT_TYPE và các PRODUCT_CATEGORY liên quan.
Nếu PRODUCT_CATEGORY có PRODUCT thì xử lý tiếp theo logic xoá PRODUCT.
Tận dụng các FK CASCADE khi xoá PRODUCT hoặc PRODUCT_CATEGORY.
```

Gợi ý thứ tự:

```text
1. Lấy SEO_META_ID của PRODUCT_TYPE.
2. Tìm toàn bộ PRODUCT_CATEGORY thuộc PRODUCT_TYPE.
3. Với từng PRODUCT_CATEGORY:
   - Xoá các PRODUCT thuộc category theo logic SP_DELETE_PRODUCT_SAFE.
   - Xoá category con nếu có.
   - Xoá PRODUCT_CATEGORY.
   - Xoá SEO_META của PRODUCT_CATEGORY nếu có.
4. Xoá PRODUCT_TYPE.
5. Xoá SEO_META của PRODUCT_TYPE.
```

Không xoá:

```text
IMAGE
PRODUCT_STATE
PRODUCT_ATTR
PRODUCT_ATTRIBUTE nếu không liên quan qua PRODUCT_VARIANT
```

Result count cần có:

```text
deleted_product_type_count
deleted_product_category_count
deleted_product_count
deleted_product_image_count
deleted_product_variant_count
deleted_review_count
deleted_wishlist_count
deleted_seo_meta_count
total_deleted_count
```

---

### 11.3. `SP_DELETE_PRODUCT_CATEGORY_SAFE.sql`

Procedure:

```sql
CALL SP_DELETE_PRODUCT_CATEGORY_SAFE(p_id);
```

Bảng chính:

```text
PRODUCT_CATEGORY
```

Có `SEO_META_ID`, cần xoá SEO_META tương ứng sau khi xoá `PRODUCT_CATEGORY`.

Quan hệ chính:

```text
PRODUCT.PRODUCT_CATEGORY_ID
PRODUCT_CATEGORY.PRODUCT_CATEGORY_PARENT_ID
```

Mong muốn:

```text
Xoá PRODUCT_CATEGORY.
Xoá category con liên quan.
Xoá PRODUCT thuộc category đó.
Xoá dữ liệu liên quan của PRODUCT theo logic SP_DELETE_PRODUCT_SAFE.
```

Gợi ý thứ tự:

```text
1. Lấy SEO_META_ID của category chính.
2. Tìm category con có PRODUCT_CATEGORY_PARENT_ID = p_id.
3. Xoá PRODUCT trong category con.
4. Xoá category con.
5. Xoá SEO_META của category con nếu có.
6. Xoá PRODUCT trong category chính.
7. Xoá PRODUCT_CATEGORY chính.
8. Xoá SEO_META của category chính.
```

Không xoá:

```text
PRODUCT_TYPE
IMAGE
PRODUCT_STATE
```

Result count cần có:

```text
deleted_product_category_count
deleted_child_category_count
deleted_product_count
deleted_product_image_count
deleted_product_variant_count
deleted_review_count
deleted_wishlist_count
deleted_seo_meta_count
total_deleted_count
```

---

### 11.4. `SP_DELETE_PRODUCT_SAFE.sql`

Procedure:

```sql
CALL SP_DELETE_PRODUCT_SAFE(p_id);
```

Bảng chính:

```text
PRODUCT
```

Có `SEO_META_ID`, cần xoá SEO_META tương ứng sau khi xoá `PRODUCT`.

Quan hệ liên quan:

```text
PRODUCT_IMAGE.PRODUCT_ID
PRODUCT_VARIANT.PRODUCT_ID
REVIEW.PRODUCT_ID
WISHLIST.PRODUCT_ID
CART_ITEM.PRODUCT_ID
ORDER_ITEM.PRODUCT_ID
```

Mong muốn:

```text
Xoá PRODUCT và toàn bộ dữ liệu phụ thuộc/liên quan để clear database.
```

Gợi ý thứ tự:

```text
1. Lấy SEO_META_ID của PRODUCT.
2. Count trước các bảng liên quan.
3. Xoá CART_ITEM_ATTRIBUTE của các CART_ITEM có PRODUCT_ID = p_id.
4. Xoá CART_ITEM có PRODUCT_ID = p_id.
5. Xoá ORDER_ITEM có PRODUCT_ID = p_id.
6. Xoá PRODUCT_REVIEW_LIKE của các REVIEW thuộc PRODUCT.
7. Xoá REVIEW thuộc PRODUCT.
8. Xoá WISHLIST thuộc PRODUCT.
9. Xoá PRODUCT_IMAGE thuộc PRODUCT.
10. Xoá PRODUCT_VARIANT thuộc PRODUCT.
11. Xoá PRODUCT.
12. Xoá SEO_META tương ứng.
```

Không xoá:

```text
PRODUCT_CATEGORY
PRODUCT_TYPE
PRODUCT_STATE
IMAGE
PRODUCT_ATTR
PRODUCT_ATTRIBUTE
ORDER
PAYMENT
SHIPMENT
```

Lưu ý:

```text
Nếu xoá ORDER_ITEM thì không xoá ORDERS.
Nếu xoá CART_ITEM thì không xoá CART.
Nếu xoá REVIEW thì cần xoá PRODUCT_REVIEW_LIKE trước hoặc tận dụng CASCADE từ REVIEW nếu FK cho phép.
```

Result count cần có:

```text
deleted_product_count
deleted_product_image_count
deleted_product_variant_count
deleted_review_count
deleted_product_review_like_count
deleted_wishlist_count
deleted_cart_item_count
deleted_cart_item_attribute_count
deleted_order_item_count
deleted_seo_meta_count
total_deleted_count
```

---

### 11.5. `SP_DELETE_PRODUCT_ATTR_SAFE.sql`

Procedure:

```sql
CALL SP_DELETE_PRODUCT_ATTR_SAFE(p_id);
```

Bảng chính:

```text
PRODUCT_ATTR
```

Không có `SEO_META_ID`.

Quan hệ chính:

```text
PRODUCT_ATTRIBUTE.PRODUCT_ATTR_ID
```

Mong muốn:

```text
Xoá PRODUCT_ATTR và các PRODUCT_ATTRIBUTE liên quan.
Với mỗi PRODUCT_ATTRIBUTE, xoá dữ liệu theo logic SP_DELETE_PRODUCT_ATTRIBUTE_SAFE.
```

Gợi ý thứ tự:

```text
1. Tìm PRODUCT_ATTRIBUTE thuộc PRODUCT_ATTR.
2. Xoá CART_ITEM_ATTRIBUTE liên quan PRODUCT_ATTRIBUTE.
3. Xoá PRODUCT_VARIANT liên quan PRODUCT_ATTRIBUTE.
4. Xoá PRODUCT_ATTRIBUTE.
5. Xoá PRODUCT_ATTR.
```

Không xoá:

```text
PRODUCT
PRODUCT_ATTR khác
```

Result count cần có:

```text
deleted_product_attr_count
deleted_product_attribute_count
deleted_product_variant_count
deleted_cart_item_attribute_count
total_deleted_count
```

---

### 11.6. `SP_DELETE_PRODUCT_ATTRIBUTE_SAFE.sql`

Procedure:

```sql
CALL SP_DELETE_PRODUCT_ATTRIBUTE_SAFE(p_id);
```

Bảng chính:

```text
PRODUCT_ATTRIBUTE
```

Không có `SEO_META_ID`.

Quan hệ chính:

```text
PRODUCT_VARIANT.PRODUCT_ATTRIBUTE_ID
CART_ITEM_ATTRIBUTE.PRODUCT_ATTRIBUTE_ID
```

Mong muốn:

```text
Xoá PRODUCT_ATTRIBUTE và các mapping/liên quan.
```

Gợi ý thứ tự:

```text
1. Count CART_ITEM_ATTRIBUTE liên quan.
2. Count PRODUCT_VARIANT liên quan.
3. Xoá CART_ITEM_ATTRIBUTE.
4. Xoá PRODUCT_VARIANT.
5. Xoá PRODUCT_ATTRIBUTE.
```

Không xoá:

```text
PRODUCT_ATTR
PRODUCT
CART_ITEM
```

Lưu ý:

```text
Xoá PRODUCT_VARIANT sẽ làm trigger cập nhật PRODUCT.STOCK_QUANTITY.
Không tự update PRODUCT.STOCK_QUANTITY thủ công.
```

Result count cần có:

```text
deleted_product_attribute_count
deleted_product_variant_count
deleted_cart_item_attribute_count
total_deleted_count
```

---

### 11.7. `SP_DELETE_PRODUCT_VARIANT_SAFE.sql`

Procedure:

```sql
CALL SP_DELETE_PRODUCT_VARIANT_SAFE(p_id);
```

Bảng chính:

```text
PRODUCT_VARIANT
```

Không có `SEO_META_ID`.

Mong muốn:

```text
Xoá PRODUCT_VARIANT theo ID.
```

Gợi ý:

```text
1. Validate PRODUCT_VARIANT tồn tại.
2. Capture PRODUCT_ID trước khi xoá.
3. Xoá PRODUCT_VARIANT.
4. Không tự update PRODUCT.STOCK_QUANTITY vì trigger DB đã xử lý.
5. Trả thêm PRODUCT_ID bị ảnh hưởng nếu muốn.
```

Result count cần có:

```text
deleted_product_variant_count
affected_product_id
total_deleted_count
```

---

### 11.8. `SP_DELETE_BLOG_TYPE_SAFE.sql`

Procedure:

```sql
CALL SP_DELETE_BLOG_TYPE_SAFE(p_id);
```

Bảng chính:

```text
BLOG_TYPE
```

Có `SEO_META_ID`, cần xoá SEO_META tương ứng sau khi xoá `BLOG_TYPE`.

Quan hệ chính:

```text
BLOG_CATEGORY.BLOG_TYPE_ID
```

Mong muốn:

```text
Xoá BLOG_TYPE.
Xoá BLOG_CATEGORY thuộc BLOG_TYPE.
Xoá BLOG thuộc BLOG_CATEGORY.
Xoá SEO_META tương ứng của BLOG, BLOG_CATEGORY, BLOG_TYPE.
```

Gợi ý thứ tự:

```text
1. Lấy SEO_META_ID của BLOG_TYPE.
2. Tìm BLOG_CATEGORY thuộc BLOG_TYPE.
3. Với từng BLOG_CATEGORY:
   - Xoá BLOG thuộc category.
   - Xoá SEO_META của BLOG nếu có.
   - Xoá BLOG_CATEGORY.
   - Xoá SEO_META của BLOG_CATEGORY nếu có.
4. Xoá BLOG_TYPE.
5. Xoá SEO_META của BLOG_TYPE.
```

Không xoá:

```text
IMAGE
```

Result count cần có:

```text
deleted_blog_type_count
deleted_blog_category_count
deleted_blog_count
deleted_seo_meta_count
total_deleted_count
```

---

### 11.9. `SP_DELETE_BLOG_CATEGORY_SAFE.sql`

Procedure:

```sql
CALL SP_DELETE_BLOG_CATEGORY_SAFE(p_id);
```

Bảng chính:

```text
BLOG_CATEGORY
```

Có `SEO_META_ID`, cần xoá SEO_META tương ứng sau khi xoá `BLOG_CATEGORY`.

Quan hệ chính:

```text
BLOG.BLOG_CATEGORY_ID
```

Mong muốn:

```text
Xoá BLOG_CATEGORY.
Xoá BLOG thuộc BLOG_CATEGORY.
Xoá SEO_META của BLOG và BLOG_CATEGORY.
```

Gợi ý thứ tự:

```text
1. Lấy SEO_META_ID của BLOG_CATEGORY.
2. Tìm BLOG thuộc category.
3. Xoá BLOG.
4. Xoá SEO_META của BLOG nếu có.
5. Xoá BLOG_CATEGORY.
6. Xoá SEO_META của BLOG_CATEGORY.
```

Không xoá:

```text
BLOG_TYPE
IMAGE
```

Result count cần có:

```text
deleted_blog_category_count
deleted_blog_count
deleted_seo_meta_count
total_deleted_count
```

---

### 11.10. `SP_DELETE_BLOG_SAFE.sql`

Procedure:

```sql
CALL SP_DELETE_BLOG_SAFE(p_id);
```

Bảng chính:

```text
BLOG
```

Có `SEO_META_ID`, cần xoá SEO_META tương ứng sau khi xoá `BLOG`.

Mong muốn:

```text
Xoá BLOG.
Xoá SEO_META tương ứng của BLOG.
```

Gợi ý thứ tự:

```text
1. Validate BLOG tồn tại.
2. Capture SEO_META_ID.
3. Xoá BLOG.
4. Xoá SEO_META nếu có.
```

Không xoá:

```text
BLOG_CATEGORY
BLOG_TYPE
IMAGE
```

Result count cần có:

```text
deleted_blog_count
deleted_seo_meta_count
total_deleted_count
```

---

## 12. Stored Procedure check theo từng bảng

Mỗi store check chỉ SELECT count. Không xoá dữ liệu.

### 12.1. `SP_CHECK_DELETE_USER_SAFE.sql`

Check:

```text
USER.USER_ID
PRODUCT_REVIEW_LIKE.USER_ID
REVIEW.USER_ID
WISHLIST.USER_ID
CART.USER_ID
ORDERS.USER_ID
ORDER_STATE_HISTORY.CHANGED_BY_USER_ID
PAYMENT theo ORDERS của USER nếu có
SHIPMENT theo ORDERS của USER nếu có
ORDER_ITEM theo ORDERS của USER nếu có
CART_ITEM theo CART của USER nếu có
CART_ITEM_ATTRIBUTE theo CART_ITEM của USER nếu có
```

### 12.2. `SP_CHECK_DELETE_PRODUCT_TYPE_SAFE.sql`

Check:

```text
PRODUCT_TYPE.PRODUCT_TYPE_ID
PRODUCT_CATEGORY.PRODUCT_TYPE_ID
PRODUCT theo các PRODUCT_CATEGORY thuộc PRODUCT_TYPE
PRODUCT_IMAGE theo PRODUCT
PRODUCT_VARIANT theo PRODUCT
REVIEW theo PRODUCT
WISHLIST theo PRODUCT
CART_ITEM theo PRODUCT
CART_ITEM_ATTRIBUTE theo CART_ITEM
ORDER_ITEM theo PRODUCT
SEO_META của PRODUCT_TYPE
SEO_META của PRODUCT_CATEGORY
SEO_META của PRODUCT
```

### 12.3. `SP_CHECK_DELETE_PRODUCT_CATEGORY_SAFE.sql`

Check:

```text
PRODUCT_CATEGORY.PRODUCT_CATEGORY_ID
PRODUCT_CATEGORY.PRODUCT_CATEGORY_PARENT_ID
PRODUCT.PRODUCT_CATEGORY_ID
PRODUCT_IMAGE theo PRODUCT
PRODUCT_VARIANT theo PRODUCT
REVIEW theo PRODUCT
WISHLIST theo PRODUCT
CART_ITEM theo PRODUCT
CART_ITEM_ATTRIBUTE theo CART_ITEM
ORDER_ITEM theo PRODUCT
SEO_META của PRODUCT_CATEGORY
SEO_META của PRODUCT
```

### 12.4. `SP_CHECK_DELETE_PRODUCT_SAFE.sql`

Check:

```text
PRODUCT.PRODUCT_ID
PRODUCT_IMAGE.PRODUCT_ID
PRODUCT_VARIANT.PRODUCT_ID
REVIEW.PRODUCT_ID
PRODUCT_REVIEW_LIKE theo REVIEW của PRODUCT
WISHLIST.PRODUCT_ID
CART_ITEM.PRODUCT_ID
CART_ITEM_ATTRIBUTE theo CART_ITEM của PRODUCT
ORDER_ITEM.PRODUCT_ID
SEO_META của PRODUCT
```

### 12.5. `SP_CHECK_DELETE_PRODUCT_ATTR_SAFE.sql`

Check:

```text
PRODUCT_ATTR.PRODUCT_ATTR_ID
PRODUCT_ATTRIBUTE.PRODUCT_ATTR_ID
PRODUCT_VARIANT theo PRODUCT_ATTRIBUTE
CART_ITEM_ATTRIBUTE theo PRODUCT_ATTRIBUTE
```

### 12.6. `SP_CHECK_DELETE_PRODUCT_ATTRIBUTE_SAFE.sql`

Check:

```text
PRODUCT_ATTRIBUTE.PRODUCT_ATTRIBUTE_ID
PRODUCT_VARIANT.PRODUCT_ATTRIBUTE_ID
CART_ITEM_ATTRIBUTE.PRODUCT_ATTRIBUTE_ID
```

### 12.7. `SP_CHECK_DELETE_PRODUCT_VARIANT_SAFE.sql`

Check:

```text
PRODUCT_VARIANT.PRODUCT_VARIANT_ID
PRODUCT_ID bị ảnh hưởng
STOCK_QUANTITY hiện tại của PRODUCT nếu cần
```

### 12.8. `SP_CHECK_DELETE_BLOG_TYPE_SAFE.sql`

Check:

```text
BLOG_TYPE.BLOG_TYPE_ID
BLOG_CATEGORY.BLOG_TYPE_ID
BLOG theo BLOG_CATEGORY thuộc BLOG_TYPE
SEO_META của BLOG_TYPE
SEO_META của BLOG_CATEGORY
SEO_META của BLOG
```

### 12.9. `SP_CHECK_DELETE_BLOG_CATEGORY_SAFE.sql`

Check:

```text
BLOG_CATEGORY.BLOG_CATEGORY_ID
BLOG.BLOG_CATEGORY_ID
SEO_META của BLOG_CATEGORY
SEO_META của BLOG
```

### 12.10. `SP_CHECK_DELETE_BLOG_SAFE.sql`

Check:

```text
BLOG.BLOG_ID
SEO_META của BLOG
```

---

## 13. Output file mong muốn

Hãy tạo các file SQL riêng đúng tên trong thư mục `\DATABASE\STORE_OPERATION`:

```text
SP_DELETE_USER_SAFE.sql
SP_DELETE_PRODUCT_TYPE_SAFE.sql
SP_DELETE_PRODUCT_CATEGORY_SAFE.sql
SP_DELETE_PRODUCT_SAFE.sql
SP_DELETE_PRODUCT_ATTR_SAFE.sql
SP_DELETE_PRODUCT_ATTRIBUTE_SAFE.sql
SP_DELETE_PRODUCT_VARIANT_SAFE.sql
SP_DELETE_BLOG_TYPE_SAFE.sql
SP_DELETE_BLOG_CATEGORY_SAFE.sql
SP_DELETE_BLOG_SAFE.sql

SP_CHECK_DELETE_USER_SAFE.sql
SP_CHECK_DELETE_PRODUCT_TYPE_SAFE.sql
SP_CHECK_DELETE_PRODUCT_CATEGORY_SAFE.sql
SP_CHECK_DELETE_PRODUCT_SAFE.sql
SP_CHECK_DELETE_PRODUCT_ATTR_SAFE.sql
SP_CHECK_DELETE_PRODUCT_ATTRIBUTE_SAFE.sql
SP_CHECK_DELETE_PRODUCT_VARIANT_SAFE.sql
SP_CHECK_DELETE_BLOG_TYPE_SAFE.sql
SP_CHECK_DELETE_BLOG_CATEGORY_SAFE.sql
SP_CHECK_DELETE_BLOG_SAFE.sql
```

Mỗi file nên có comment ở đầu file:

```sql
-- Procedure: SP_DELETE_PRODUCT_SAFE
-- Purpose: Delete one PRODUCT record and related records safely.
-- Input: p_id BIGINT
-- Output: summary result set
-- Location: \DATABASE\STORE_OPERATION\SP_DELETE_PRODUCT_SAFE.sql
```

---

## 14. Ví dụ gọi store

```sql
CALL SP_CHECK_DELETE_PRODUCT_SAFE(10);
CALL SP_DELETE_PRODUCT_SAFE(10);

CALL SP_CHECK_DELETE_USER_SAFE(5);
CALL SP_DELETE_USER_SAFE(5);

CALL SP_CHECK_DELETE_BLOG_CATEGORY_SAFE(3);
CALL SP_DELETE_BLOG_CATEGORY_SAFE(3);
```

---

## 15. Báo cáo sau khi hoàn thành

Sau khi tạo xong các file SQL, hãy báo cáo:

1. Danh sách file đã tạo trong thư mục `\DATABASE\STORE_OPERATION`.
2. Danh sách Stored Procedure đã tạo.
3. Input của từng Stored Procedure.
4. Result set của từng Stored Procedure.
5. Với từng delete store:
   - Bảng chính bị xoá.
   - Bảng liên quan bị xoá.
   - Bảng nào tận dụng `ON DELETE CASCADE`.
   - Bảng nào phải xoá thủ công vì `ON DELETE RESTRICT`.
   - Bảng nào bị `ON DELETE SET NULL` nhưng store chủ động xoá để clear database.
   - Có xoá `SEO_META` hay không.
6. Với từng check store:
   - Check những table nào.
   - Check theo column nào.
7. Cách chạy các store.
8. Rủi ro còn lại nếu có.
9. Điểm nào cần tôi xác nhận thêm trước khi chạy trên production.
