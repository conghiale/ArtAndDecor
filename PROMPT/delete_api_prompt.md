# Prompt cho Copilot Agent - Implement API xoá dữ liệu an toàn, không tạo Unit Test

Bạn là Copilot coding agent. Hãy phân tích source code Java/Spring hiện tại và triển khai API xoá dữ liệu theo đúng yêu cầu bên dưới.

## 1. Mục tiêu

Implement API xoá cho các entity:

1. `USER`
2. `PRODUCT_TYPE`
3. `PRODUCT_CATEGORY`
4. `PRODUCT_ATTR`
5. `PRODUCT_ATTRIBUTE`
6. `PRODUCT_VARIANT`
7. `BLOG_TYPE`
8. `BLOG_CATEGORY`
9. `BLOG`

Yêu cầu quan trọng nhất:

- Chỉ được xoá khi record không còn được sử dụng bởi bất kỳ record nào khác.
- Không được để database tự xoá dây chuyền bằng `ON DELETE CASCADE`.
- Không được để database tự `SET NULL` nếu nghiệp vụ yêu cầu phải chặn xoá khi còn record liên quan.
- Không được dựa vào lỗi foreign key từ database làm cơ chế kiểm tra chính.
- Phải chủ động query/check ràng buộc trước khi xoá.
- Nếu còn ràng buộc thì trả lỗi và không xoá.
- Nếu lỗi ở bất kỳ bước nào thì rollback toàn bộ transaction.
- Không thay đổi request/response và logic của các API cũ.
- Không tạo Unit Test hoặc Integration Test. Tôi sẽ tự test thủ công qua API.

---

## 2. File bắt buộc phải phân tích trước khi code

Trước khi chỉnh sửa code, hãy đọc kỹ:

1. `CREATE_DB_ART_AND_DECOR.sql`

2. `UserController.java` và toàn bộ file liên quan:
   - Service
   - Repository/Mapper/DAO
   - Entity
   - DTO
   - Response model
   - Exception handler
   - Logger pattern
   - Security/current user context nếu API hiện tại có dùng

3. `ProductController.java` và toàn bộ file liên quan.

4. `BlogController.java` và toàn bộ file liên quan.

Không được phỏng đoán tên bảng, tên field, tên entity hoặc quan hệ dữ liệu. Mọi rule xoá phải dựa trên `CREATE_DB_ART_AND_DECOR.sql` và source code thực tế.

---

## 3. Nguyên tắc triển khai chung

Các API delete phải tuân thủ pattern hiện tại của project:

- Giữ nguyên convention URL nếu project đã có pattern sẵn.
- Giữ nguyên format request/response hiện tại.
- Giữ nguyên cách xử lý success/error hiện tại.
- Không làm thay đổi request/response của các API đang tồn tại.
- Không làm thay đổi logic của các API đang tồn tại.
- Không tự ý refactor lớn nếu không cần thiết.
- Hạn chế tạo thêm method/class mới nếu có thể tái sử dụng method/class hiện tại.
- Nếu bắt buộc tạo helper method thì helper phải nhỏ, rõ nghĩa, dễ trace và không làm phình source code không cần thiết.
- Controller chỉ nên nhận request, validate request cơ bản và trả response.
- Business logic xoá, kiểm tra ràng buộc và transaction phải đặt ở service layer nếu project hiện tại có service layer.
- Message response trả về client phải là tiếng Việt.
- Log phải ghi bằng tiếng Anh để dễ vận hành/debug.

---

## 4. Hiểu đúng cấu trúc database cần áp dụng

## 4.1. USER

Bảng `USER` có khoá chính:

```sql
USER.USER_ID
```

Trước khi xoá `USER`, phải kiểm tra tất cả bảng đang tham chiếu `USER_ID`, bao gồm tối thiểu:

```text
REVIEW.USER_ID
PRODUCT_REVIEW_LIKE.USER_ID
WISHLIST.USER_ID
CART.USER_ID
ORDERS.USER_ID
ORDER_STATE_HISTORY.CHANGED_BY_USER_ID
```

Lưu ý:

- Dù một số FK trong DB đang là `ON DELETE CASCADE` hoặc `ON DELETE SET NULL`, vẫn không được để DB tự cascade/set null khi gọi API xoá user.
- Nếu user còn review, like, wishlist, cart, order, order state history hoặc bảng khác tham chiếu trong schema thực tế thì không cho xoá.
- Log phải ghi rõ user đang bị ràng buộc bởi bảng nào, column nào, count bao nhiêu và sample ID nếu lấy được.

---

## 4.2. PRODUCT_TYPE

Quan hệ chính:

```text
PRODUCT_CATEGORY.PRODUCT_TYPE_ID -> PRODUCT_TYPE.PRODUCT_TYPE_ID
```

Trước khi xoá `PRODUCT_TYPE`, phải kiểm tra:

```text
PRODUCT_CATEGORY.PRODUCT_TYPE_ID
```

Nếu còn `PRODUCT_CATEGORY` thuộc product type đó thì không cho xoá.

Không được xoá dây chuyền `PRODUCT_CATEGORY`.

---

## 4.3. PRODUCT_CATEGORY

Quan hệ chính:

```text
PRODUCT.PRODUCT_CATEGORY_ID -> PRODUCT_CATEGORY.PRODUCT_CATEGORY_ID
PRODUCT_CATEGORY.PRODUCT_CATEGORY_PARENT_ID -> PRODUCT_CATEGORY.PRODUCT_CATEGORY_ID
```

Trước khi xoá `PRODUCT_CATEGORY`, phải kiểm tra:

```text
PRODUCT.PRODUCT_CATEGORY_ID
PRODUCT_CATEGORY.PRODUCT_CATEGORY_PARENT_ID
```

Lưu ý cực kỳ quan trọng:

- DB đang có `PRODUCT_CATEGORY_PARENT_ID ON DELETE CASCADE`.
- Tuy nhiên nghiệp vụ yêu cầu không được xoá dây chuyền category con.
- Vì vậy nếu category đang có category con thì phải báo lỗi và không xoá.
- Nếu category đang có product thì phải báo lỗi và không xoá.

---

## 4.4. PRODUCT_ATTR

Quan hệ chính:

```text
PRODUCT_ATTRIBUTE.PRODUCT_ATTR_ID -> PRODUCT_ATTR.PRODUCT_ATTR_ID
```

Trước khi xoá `PRODUCT_ATTR`, phải kiểm tra:

```text
PRODUCT_ATTRIBUTE.PRODUCT_ATTR_ID
```

Nếu còn `PRODUCT_ATTRIBUTE` thuộc attr đó thì không cho xoá.

Không được xoá dây chuyền `PRODUCT_ATTRIBUTE`.

---

## 4.5. PRODUCT_ATTRIBUTE

Quan hệ chính:

```text
PRODUCT_VARIANT.PRODUCT_ATTRIBUTE_ID -> PRODUCT_ATTRIBUTE.PRODUCT_ATTRIBUTE_ID
CART_ITEM_ATTRIBUTE.PRODUCT_ATTRIBUTE_ID -> PRODUCT_ATTRIBUTE.PRODUCT_ATTRIBUTE_ID
```

Trước khi xoá `PRODUCT_ATTRIBUTE`, phải kiểm tra tối thiểu:

```text
PRODUCT_VARIANT.PRODUCT_ATTRIBUTE_ID
CART_ITEM_ATTRIBUTE.PRODUCT_ATTRIBUTE_ID
```

Lưu ý:

- DB đang có `PRODUCT_VARIANT.PRODUCT_ATTRIBUTE_ID ON DELETE CASCADE`.
- DB cũng có `CART_ITEM_ATTRIBUTE.PRODUCT_ATTRIBUTE_ID ON DELETE CASCADE`.
- Tuy nhiên nghiệp vụ yêu cầu không được xoá dây chuyền.
- Nếu attribute đang được product variant hoặc cart item attribute sử dụng thì không cho xoá.

---

## 4.6. PRODUCT_VARIANT

Quan hệ chính:

```text
PRODUCT_VARIANT.PRODUCT_ID -> PRODUCT.PRODUCT_ID
PRODUCT_VARIANT.PRODUCT_ATTRIBUTE_ID -> PRODUCT_ATTRIBUTE.PRODUCT_ATTRIBUTE_ID
```

Hiện trong schema được cung cấp chưa thấy bảng khác tham chiếu trực tiếp đến `PRODUCT_VARIANT_ID`.

Trước khi xoá `PRODUCT_VARIANT`, phải:

1. Kiểm tra `PRODUCT_VARIANT` có tồn tại không.
2. Search toàn bộ `CREATE_DB_ART_AND_DECOR.sql` để xác nhận có bảng nào tham chiếu `PRODUCT_VARIANT_ID` không.
3. Nếu có bảng tham chiếu `PRODUCT_VARIANT_ID` thì phải check các bảng đó trước khi xoá.
4. Nếu không có bảng nào tham chiếu trực tiếp `PRODUCT_VARIANT_ID` thì có thể xoá variant sau khi validate tồn tại.

Lưu ý:

- Bảng `PRODUCT_VARIANT` có trigger sau delete để cập nhật lại `PRODUCT.STOCK_QUANTITY`.
- Vì vậy API xoá `PRODUCT_VARIANT` phải chạy trong transaction.
- Sau khi xoá variant thành công, stock của product sẽ được DB trigger cập nhật.
- Không tự update stock thủ công nếu trigger hiện tại đã đảm nhiệm việc này, trừ khi source code hiện tại đang có rule riêng.

---

## 4.7. BLOG_TYPE

Quan hệ chính:

```text
BLOG_CATEGORY.BLOG_TYPE_ID -> BLOG_TYPE.BLOG_TYPE_ID
```

Trước khi xoá `BLOG_TYPE`, phải kiểm tra:

```text
BLOG_CATEGORY.BLOG_TYPE_ID
```

Lưu ý:

- DB đang khai báo `BLOG_CATEGORY.BLOG_TYPE_ID ON DELETE SET NULL`.
- Nhưng nghiệp vụ yêu cầu không tự set null nếu còn category đang dùng blog type.
- Nếu còn `BLOG_CATEGORY` tham chiếu đến `BLOG_TYPE`, không cho xoá.

---

## 4.8. BLOG_CATEGORY

Quan hệ chính:

```text
BLOG.BLOG_CATEGORY_ID -> BLOG_CATEGORY.BLOG_CATEGORY_ID
```

Trước khi xoá `BLOG_CATEGORY`, phải kiểm tra:

```text
BLOG.BLOG_CATEGORY_ID
```

Nếu còn blog thuộc category đó thì không cho xoá.

---

## 4.9. BLOG

Trước khi xoá `BLOG`, phải:

1. Kiểm tra `BLOG` có tồn tại không.
2. Search toàn bộ schema để xác định có bảng nào tham chiếu `BLOG.BLOG_ID` không.
3. Nếu có bảng tham chiếu blog, ví dụ blog image, blog comment, blog tag mapping, relation table hoặc bảng tương tự, phải check trước.
4. Nếu không có bảng nào tham chiếu `BLOG_ID` trong schema hiện tại thì có thể xoá sau khi validate tồn tại.

Không được xoá dây chuyền dữ liệu liên quan nếu schema thực tế có thêm bảng liên quan đến blog.

---

## 5. Workflow bắt buộc cho mỗi API xoá

Với mỗi API xoá, workflow bắt buộc:

```text
1. Log start delete.
2. Validate id.
3. Nếu id null, id <= 0 hoặc invalid:
   - Trả lỗi tiếng Việt theo format hiện tại.
   - Log warn.
   - Không thao tác database ngoài các bước cần thiết.
4. Kiểm tra record tồn tại.
5. Nếu không tồn tại:
   - Log warn.
   - Trả lỗi tiếng Việt theo format hiện tại.
   - Không xoá.
6. Kiểm tra tất cả bảng đang tham chiếu record cần xoá.
7. Nếu còn ràng buộc:
   - Log warn rõ table/column/count/sampleId.
   - Trả lỗi tiếng Việt theo format hiện tại.
   - Không xoá.
8. Re-check ràng buộc ngay trước khi xoá nếu workflow có nhiều bước hoặc có nguy cơ dữ liệu thay đổi trong transaction.
9. Nếu không còn ràng buộc:
   - Thực hiện xoá.
10. Log delete success.
11. Return success theo format hiện tại.
12. Nếu phát sinh exception:
   - Log error.
   - Rollback transaction.
   - Trả lỗi theo exception handler hiện tại.
```

Không được dùng cascade delete để xoá dữ liệu con.

Không được viết code kiểu:

```java
repository.deleteById(id);
```

mà chưa kiểm tra đầy đủ record tồn tại và ràng buộc trước đó.

---

## 6. Transaction, rollback và tính toàn vẹn dữ liệu

Toàn bộ logic xoá phải chạy trong transaction ở service layer.

Nếu project dùng Spring, ưu tiên:

```java
@Transactional(rollbackFor = Exception.class)
```

hoặc theo pattern transaction hiện tại của project.

Yêu cầu rollback:

- Nếu lỗi validate ràng buộc thì không có thay đổi DB nào được commit.
- Nếu lỗi trong lúc xoá thì rollback.
- Nếu lỗi runtime bất thường thì rollback.
- Nếu xảy ra `DataIntegrityViolationException` hoặc lỗi FK bất ngờ do race condition/concurrent request, phải rollback và trả lỗi rõ ràng theo format hiện tại.
- Không được catch exception rồi return lỗi làm mất rollback.
- Nếu cần catch để log thì phải throw lại exception phù hợp.
- Controller không nên chứa business logic phức tạp. Controller chỉ gọi service và trả response.

Yêu cầu xử lý race condition tối thiểu:

- Phải chủ động check ràng buộc trước khi xoá.
- Nếu project hỗ trợ lock/pessimistic lock theo pattern hiện tại, cân nhắc lock record chính khi xoá để tránh concurrent delete/update bất thường.
- Không bắt buộc refactor lớn để thêm lock nếu project chưa dùng pattern này.
- Dù đã check trước, vẫn phải handle lỗi DB ở bước delete như tuyến phòng thủ cuối cùng.
- Khi lỗi DB xảy ra, không được để dữ liệu xoá một phần.

---

## 7. Logging

Log bằng tiếng Anh.

Response message trả về client bằng tiếng Việt.

Bổ sung log vừa đủ, không spam.

Cần log được các case:

```text
Start delete
Invalid id
Record not found
Checking references
Referenced table found
Delete success
Delete failed
Unexpected exception
Rollback reason
```

Log format mong muốn:

```java
log.info("Start delete productCategory, id={}", id);

log.debug("Checking references before deleting productCategory, id={}", id);

log.warn(
    "Cannot delete productCategory, id={}, referencedTable={}, referencedColumn={}, referencedCount={}, sampleReferencedId={}",
    id,
    "PRODUCT",
    "PRODUCT_CATEGORY_ID",
    count,
    sampleId
);

log.info("Deleted productCategory successfully, id={}", id);

log.error("Failed to delete productCategory, id={}, reason={}", id, ex.getMessage(), ex);
```

Khi bị ràng buộc, log phải ghi rõ:

```text
entityName
id cần xoá
referencedTable
referencedColumn
referencedCount
sampleReferencedId nếu lấy được
```

Ví dụ:

```text
Cannot delete PRODUCT_TYPE id=1 because it is referenced by PRODUCT_CATEGORY.PRODUCT_TYPE_ID, referencedCount=3, sampleReferencedId=10
Cannot delete PRODUCT_CATEGORY id=2 because it is referenced by PRODUCT.PRODUCT_CATEGORY_ID, referencedCount=5, sampleReferencedId=100
Cannot delete PRODUCT_CATEGORY id=2 because it is referenced by PRODUCT_CATEGORY.PRODUCT_CATEGORY_PARENT_ID, referencedCount=2, sampleReferencedId=8
Cannot delete PRODUCT_ATTR id=4 because it is referenced by PRODUCT_ATTRIBUTE.PRODUCT_ATTR_ID, referencedCount=6, sampleReferencedId=11
Cannot delete PRODUCT_ATTRIBUTE id=7 because it is referenced by PRODUCT_VARIANT.PRODUCT_ATTRIBUTE_ID, referencedCount=9, sampleReferencedId=20
Cannot delete PRODUCT_ATTRIBUTE id=7 because it is referenced by CART_ITEM_ATTRIBUTE.PRODUCT_ATTRIBUTE_ID, referencedCount=2, sampleReferencedId=15
Cannot delete BLOG_TYPE id=3 because it is referenced by BLOG_CATEGORY.BLOG_TYPE_ID, referencedCount=4, sampleReferencedId=12
Cannot delete BLOG_CATEGORY id=5 because it is referenced by BLOG.BLOG_CATEGORY_ID, referencedCount=10, sampleReferencedId=100
```

Không cần log dữ liệu nhạy cảm không liên quan.

Không trả table/column kỹ thuật ra client nếu project hiện tại không làm vậy. Nhưng log bắt buộc phải có table/column để debug.

---

## 8. Response message

Message trả về client phải là tiếng Việt.

Không đổi format response hiện tại của project.

Không đổi field name response hiện tại.

Nếu project có enum/code/message convention thì tái sử dụng.

Ví dụ message:

```text
Không tìm thấy người dùng cần xoá.
Không thể xoá người dùng vì đang được sử dụng bởi dữ liệu khác.
Xoá người dùng thành công.

Không tìm thấy loại sản phẩm cần xoá.
Không thể xoá loại sản phẩm vì đang có danh mục sản phẩm sử dụng.
Xoá loại sản phẩm thành công.

Không tìm thấy danh mục sản phẩm cần xoá.
Không thể xoá danh mục sản phẩm vì đang có sản phẩm sử dụng.
Không thể xoá danh mục sản phẩm vì đang có danh mục con.
Xoá danh mục sản phẩm thành công.

Không tìm thấy nhóm thuộc tính sản phẩm cần xoá.
Không thể xoá nhóm thuộc tính sản phẩm vì đang có thuộc tính sản phẩm sử dụng.
Xoá nhóm thuộc tính sản phẩm thành công.

Không tìm thấy thuộc tính sản phẩm cần xoá.
Không thể xoá thuộc tính sản phẩm vì đang được sử dụng bởi biến thể sản phẩm hoặc giỏ hàng.
Xoá thuộc tính sản phẩm thành công.

Không tìm thấy biến thể sản phẩm cần xoá.
Không thể xoá biến thể sản phẩm vì đang được dữ liệu khác sử dụng.
Xoá biến thể sản phẩm thành công.

Không tìm thấy loại bài viết cần xoá.
Không thể xoá loại bài viết vì đang có danh mục bài viết sử dụng.
Xoá loại bài viết thành công.

Không tìm thấy danh mục bài viết cần xoá.
Không thể xoá danh mục bài viết vì đang có bài viết sử dụng.
Xoá danh mục bài viết thành công.

Không tìm thấy bài viết cần xoá.
Không thể xoá bài viết vì đang được dữ liệu khác sử dụng.
Xoá bài viết thành công.
```

Nếu project đang dùng HTTP status cụ thể:

- Not found: giữ theo convention hiện tại.
- Business conflict/ràng buộc: dùng convention hiện tại, ví dụ `400 Bad Request` hoặc `409 Conflict`.
- Success: giữ theo convention hiện tại.

Không tự đổi chuẩn response/status nếu project đã có chuẩn sẵn.

---

## 9. Query kiểm tra ràng buộc

Ưu tiên dùng pattern repository/mapper/DAO hiện tại.

Nếu project dùng Spring Data JPA, có thể dùng các method dạng:

```java
long countByProductTypeId(Long productTypeId);
Optional<Long> findFirstIdByProductTypeId(Long productTypeId);
```

Nếu project dùng MyBatis/Mapper/DAO, viết query count/sampleId theo pattern hiện tại.

Mỗi check ràng buộc nên lấy tối thiểu:

```text
referencedCount
sampleReferencedId
```

Có thể dùng 2 query riêng hoặc 1 query tuỳ pattern hiện tại.

Không query quá nặng:

- Nếu chỉ cần kiểm tra tồn tại thì có thể dùng `exists`.
- Vì log cần count/sampleId, ưu tiên count + sampleId nếu phù hợp.
- Không load toàn bộ list entity chỉ để check ràng buộc.
- Không query dữ liệu nhạy cảm không cần thiết.

---

## 10. Yêu cầu riêng cho từng API

## 10.1. Delete USER

Implement delete USER trong đúng controller/service hiện tại.

Check tối thiểu:

```text
REVIEW.USER_ID
PRODUCT_REVIEW_LIKE.USER_ID
WISHLIST.USER_ID
CART.USER_ID
ORDERS.USER_ID
ORDER_STATE_HISTORY.CHANGED_BY_USER_ID
```

Nếu schema/source code còn bảng khác tham chiếu `USER_ID`, phải check thêm.

Nếu còn bất kỳ dữ liệu liên quan nào thì không cho xoá user.

---

## 10.2. Delete PRODUCT_TYPE

Check tối thiểu:

```text
PRODUCT_CATEGORY.PRODUCT_TYPE_ID
```

Nếu còn category thì không xoá.

---

## 10.3. Delete PRODUCT_CATEGORY

Check tối thiểu:

```text
PRODUCT.PRODUCT_CATEGORY_ID
PRODUCT_CATEGORY.PRODUCT_CATEGORY_PARENT_ID
```

Nếu còn product hoặc category con thì không xoá.

Đặc biệt phải chặn case category con để tránh DB cascade xoá dây chuyền.

---

## 10.4. Delete PRODUCT_ATTR

Check tối thiểu:

```text
PRODUCT_ATTRIBUTE.PRODUCT_ATTR_ID
```

Nếu còn product attribute thì không xoá.

---

## 10.5. Delete PRODUCT_ATTRIBUTE

Check tối thiểu:

```text
PRODUCT_VARIANT.PRODUCT_ATTRIBUTE_ID
CART_ITEM_ATTRIBUTE.PRODUCT_ATTRIBUTE_ID
```

Nếu còn product variant hoặc cart item attribute thì không xoá.

Đặc biệt phải chặn trước để tránh DB cascade xoá `PRODUCT_VARIANT` hoặc `CART_ITEM_ATTRIBUTE`.

---

## 10.6. Delete PRODUCT_VARIANT

Check:

```text
PRODUCT_VARIANT tồn tại
Bất kỳ bảng nào trong schema thực tế tham chiếu PRODUCT_VARIANT_ID
```

Nếu không có bảng nào tham chiếu `PRODUCT_VARIANT_ID`, cho phép xoá variant sau khi validate tồn tại.

Sau khi xoá, không tự update `PRODUCT.STOCK_QUANTITY` nếu DB trigger hiện tại đã xử lý. Chỉ làm khác nếu source code hiện tại đã có rule rõ ràng.

---

## 10.7. Delete BLOG_TYPE

Check tối thiểu:

```text
BLOG_CATEGORY.BLOG_TYPE_ID
```

Nếu còn blog category thì không xoá.

Đặc biệt phải chặn trước để tránh DB tự `SET NULL` `BLOG_CATEGORY.BLOG_TYPE_ID`.

---

## 10.8. Delete BLOG_CATEGORY

Check tối thiểu:

```text
BLOG.BLOG_CATEGORY_ID
```

Nếu còn blog thì không xoá.

---

## 10.9. Delete BLOG

Check:

```text
BLOG tồn tại
Bất kỳ bảng nào trong schema thực tế tham chiếu BLOG_ID
```

Nếu không có bảng nào tham chiếu `BLOG_ID`, cho phép xoá blog sau khi validate tồn tại.

---

## 11. Không được làm

Không được:

1. Không refactor lớn ngoài phạm vi yêu cầu.
2. Không đổi format response API cũ.
3. Không đổi logic API cũ.
4. Không đổi tên field request/response hiện tại.
5. Không tạo quá nhiều method/class mới không cần thiết.
6. Không dùng cascade delete để xoá dữ liệu liên quan.
7. Không xoá một phần dữ liệu rồi mới báo lỗi.
8. Không catch exception rồi bỏ qua.
9. Không dùng `repository.deleteById(id)` trực tiếp khi chưa check tồn tại và ràng buộc.
10. Không trả log kỹ thuật quá chi tiết ra client.
11. Không tự ý xoá image, seo meta, product image, review, cart, order, wishlist hoặc dữ liệu nghiệp vụ liên quan nếu yêu cầu không nói rõ.
12. Không tạo Unit Test.
13. Không tạo Integration Test.
14. Không sửa test file nếu không cần thiết cho compile.
15. Không sửa dữ liệu sample chỉ để API delete hoạt động.
16. Không tự ý đổi hard delete sang soft delete hoặc ngược lại nếu project đã có pattern rõ ràng.

---

## 12. Yêu cầu hỗ trợ test thủ công qua API

Tôi sẽ tự test thủ công qua API sau khi bạn implement.

Vì vậy sau khi implement xong, hãy cung cấp rõ:

1. Danh sách endpoint delete đã thêm.
2. HTTP method của từng endpoint.
3. URL/path đầy đủ theo route hiện tại.
4. Input cần truyền:
   - Path variable
   - Request param
   - Request body nếu có
   - Header/token nếu API yêu cầu
5. Ví dụ request cho từng API bằng cURL hoặc Postman format.
6. Ví dụ response thành công.
7. Ví dụ response lỗi không tìm thấy record.
8. Ví dụ response lỗi record đang bị ràng buộc.
9. Các bước setup dữ liệu thủ công để tôi test từng case.

Không cần tạo Unit Test/Integration Test.

---

## 13. Checklist test thủ công tôi cần có sau khi implement

Sau khi implement, hãy liệt kê checklist để tôi tự test qua API.

Checklist tối thiểu:

### 13.1. USER

```text
DELETE USER thành công khi user không còn dữ liệu liên quan.
DELETE USER lỗi khi user không tồn tại.
DELETE USER lỗi khi user còn REVIEW.
DELETE USER lỗi khi user còn PRODUCT_REVIEW_LIKE.
DELETE USER lỗi khi user còn WISHLIST.
DELETE USER lỗi khi user còn CART.
DELETE USER lỗi khi user còn ORDERS.
DELETE USER lỗi khi user còn ORDER_STATE_HISTORY.
Sau lỗi, USER vẫn còn trong database.
```

### 13.2. PRODUCT_TYPE

```text
DELETE PRODUCT_TYPE thành công khi không còn PRODUCT_CATEGORY.
DELETE PRODUCT_TYPE lỗi khi không tồn tại.
DELETE PRODUCT_TYPE lỗi khi còn PRODUCT_CATEGORY.
Sau lỗi, PRODUCT_TYPE vẫn còn trong database.
```

### 13.3. PRODUCT_CATEGORY

```text
DELETE PRODUCT_CATEGORY thành công khi không còn PRODUCT và category con.
DELETE PRODUCT_CATEGORY lỗi khi không tồn tại.
DELETE PRODUCT_CATEGORY lỗi khi còn PRODUCT.
DELETE PRODUCT_CATEGORY lỗi khi còn category con.
Sau lỗi, PRODUCT_CATEGORY cha và category con vẫn còn trong database.
```

### 13.4. PRODUCT_ATTR

```text
DELETE PRODUCT_ATTR thành công khi không còn PRODUCT_ATTRIBUTE.
DELETE PRODUCT_ATTR lỗi khi không tồn tại.
DELETE PRODUCT_ATTR lỗi khi còn PRODUCT_ATTRIBUTE.
Sau lỗi, PRODUCT_ATTR vẫn còn trong database.
```

### 13.5. PRODUCT_ATTRIBUTE

```text
DELETE PRODUCT_ATTRIBUTE thành công khi không còn PRODUCT_VARIANT và CART_ITEM_ATTRIBUTE.
DELETE PRODUCT_ATTRIBUTE lỗi khi không tồn tại.
DELETE PRODUCT_ATTRIBUTE lỗi khi còn PRODUCT_VARIANT.
DELETE PRODUCT_ATTRIBUTE lỗi khi còn CART_ITEM_ATTRIBUTE.
Sau lỗi, PRODUCT_ATTRIBUTE, PRODUCT_VARIANT và CART_ITEM_ATTRIBUTE vẫn còn trong database.
```

### 13.6. PRODUCT_VARIANT

```text
DELETE PRODUCT_VARIANT thành công khi tồn tại và không có bảng nào tham chiếu PRODUCT_VARIANT_ID.
DELETE PRODUCT_VARIANT lỗi khi không tồn tại.
Nếu schema/source code có bảng khác tham chiếu PRODUCT_VARIANT_ID thì DELETE phải lỗi khi còn ràng buộc.
Sau khi xoá PRODUCT_VARIANT thành công, kiểm tra PRODUCT.STOCK_QUANTITY đã được trigger DB cập nhật lại.
```

### 13.7. BLOG_TYPE

```text
DELETE BLOG_TYPE thành công khi không còn BLOG_CATEGORY.
DELETE BLOG_TYPE lỗi khi không tồn tại.
DELETE BLOG_TYPE lỗi khi còn BLOG_CATEGORY.
Sau lỗi, BLOG_TYPE vẫn còn và BLOG_CATEGORY.BLOG_TYPE_ID không bị set null.
```

### 13.8. BLOG_CATEGORY

```text
DELETE BLOG_CATEGORY thành công khi không còn BLOG.
DELETE BLOG_CATEGORY lỗi khi không tồn tại.
DELETE BLOG_CATEGORY lỗi khi còn BLOG.
Sau lỗi, BLOG_CATEGORY vẫn còn trong database.
```

### 13.9. BLOG

```text
DELETE BLOG thành công khi tồn tại và không có bảng nào tham chiếu BLOG_ID.
DELETE BLOG lỗi khi không tồn tại.
Nếu schema/source code có bảng khác tham chiếu BLOG_ID thì DELETE phải lỗi khi còn ràng buộc.
Sau lỗi, BLOG vẫn còn trong database.
```

---

## 14. Checklist verify log khi test thủ công

Khi tôi gọi API test thủ công, log phải giúp trace được:

```text
API nào được gọi.
Entity nào đang xoá.
ID nào được yêu cầu xoá.
ID không tồn tại hay không.
Đang check ràng buộc ở table nào.
Bị ràng buộc bởi table nào.
Bị ràng buộc bởi column nào.
Có bao nhiêu record đang ràng buộc.
Sample referenced ID là gì nếu lấy được.
Xoá thành công hay thất bại.
Nếu thất bại thì thất bại ở bước nào.
Nếu rollback thì lý do rollback là gì.
```

---

## 15. Output sau khi hoàn thành

Sau khi implement xong, hãy báo cáo:

1. Danh sách file đã chỉnh sửa.
2. Endpoint delete đã thêm cho từng entity.
3. HTTP method và URL của từng endpoint.
4. Input cần truyền cho từng API.
5. Ví dụ cURL/Postman cho từng API.
6. Logic kiểm tra ràng buộc cho từng entity.
7. Danh sách table/column đã check cho từng entity.
8. Transaction rollback đang được đảm bảo ở service/method nào.
9. Các message tiếng Việt đã thêm/tái sử dụng.
10. Các log chính đã thêm.
11. Checklist test thủ công qua API.
12. Rủi ro còn lại nếu có.
13. Điểm nào cần tôi xác nhận thêm nếu schema/source code thực tế khác với phân tích.

Không cần báo cáo danh sách file test vì không tạo Unit Test/Integration Test.
