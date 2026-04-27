# Art & Decor API Changes Documentation

## 📅 **Latest Change Date**: April 12, 2026
## 🎯 **Latest Scope**: API Filtering Enhancement + Status Update APIs

---

## 🆕 **LATEST UPDATES - April 12, 2026**

### 📋 **Summary**: Enhanced Filtering & Status Management APIs + Contact SEO Integration
- **Product Types/Categories**: Added slug filtering parameters
- **Payment/Shipment**: Added dedicated status update APIs 
- **Contact System**: Enhanced with integrated SEO meta management
- **Security**: Updated access control for new endpoints

---

## 🔄 **1. ENHANCED FILTERING APIs**

### 1.1 **Product Types Filtering Enhancement**

#### **URL**: `GET /products/types` *(unchanged)*
#### **Role**: PUBLIC *(unchanged)*
#### **Authentication**: Not Required *(unchanged)*

#### **🆕 NEW PARAMETER** - `productTypeSlug`:

**BEFORE**:
```bash
GET /products/types?textSearch=art&enabled=true
```

**NOW**:
```bash
GET /products/types?textSearch=art&enabled=true&productTypeSlug=wall-art
```

**🟢 Enhancement**: 
- **Parameter**: `productTypeSlug` (optional)
- **Purpose**: Filter by exact product type slug for precision searches
- **Use Case**: Client có thể filter chính xác theo slug từ URL routing

---

### 1.2 **Product Categories Filtering Enhancement**

#### **URL**: `GET /products/categories` *(unchanged)*
#### **Role**: PUBLIC *(unchanged)*  
#### **Authentication**: Not Required *(unchanged)*

#### **🆕 NEW PARAMETER** - `productCategorySlug`:

**BEFORE**:
```bash
GET /products/categories?textSearch=art&productTypeId=1&rootOnly=true
```

**NOW**:
```bash
GET /products/categories?textSearch=art&productTypeId=1&rootOnly=true&productCategorySlug=modern-art
```

**🟢 Enhancement**: 
- **Parameter**: `productCategorySlug` (optional)
- **Purpose**: Filter by exact product category slug
- **Use Case**: SEO-friendly URLs và precise category filtering

---

## 🆕 **2. NEW STATUS UPDATE APIs**

### 2.1 **Payment Status Update API** - NEW

#### **URL**: `PATCH /payments/{paymentId}/status` *(brand new)*
#### **Role**: ADMIN only
#### **Authentication**: Required

**Purpose**: Update payment status without full payment entity update

**Request**:
```bash
PATCH /payments/123/status?paymentStateId=2
```

**Response**:
```json
{
  "success": true,
  "message": "Payment status updated successfully",
  "data": {
    "paymentId": 123,
    "paymentStateId": 2,
    "paymentStateName": "COMPLETED",
    "updatedAt": "2026-04-12T10:30:00Z"
  }
}
```

**🎯 Business Logic**: 
- Admin có thể thay đổi trạng thái payment nhanh chóng
- Không cần update toàn bộ payment entity
- Audit trail cho payment status changes

---

### 2.2 **Shipment Status Update API** - NEW  

#### **URL**: `PATCH /shipments/{shipmentId}/status` *(brand new)*
#### **Role**: ADMIN only
#### **Authentication**: Required

**Purpose**: Update shipment status without full shipment entity update

**Request**:
```bash
PATCH /shipments/456/status?shipmentStateId=3
```

**Response**:
```json
{
  "success": true,
  "message": "Shipment status updated successfully", 
  "data": {
    "shipmentId": 456,
    "shipmentStateId": 3,
    "shipmentStateName": "DELIVERED",
    "updatedAt": "2026-04-12T10:30:00Z"
  }
}
```

**🎯 Business Logic**:
- Admin có thể track shipment progress efficiently  
- Reuses existing `ShipmentService.updateShipmentState()` method
- Automatic remark: "Status updated by admin"

---

### 2.3 **Order Status Update API** - EXISTING

#### **URL**: `PATCH /orders/{orderId}/status` *(already exists)*
#### **Role**: ADMIN only *(unchanged)*
#### **Authentication**: Required *(unchanged)*

**✅ Status**: Already implemented with full functionality
- **Special handling**: When order status = DELIVERED → auto-update shipments to DELIVERED
- **History tracking**: Auto-creates ORDER_STATE_HISTORY records
- **Complete**: No changes needed

---

## 🆕 **3. Contact SEO Integration Enhancement** - NEW

### 3.1 **Contact Creation API** - ENHANCED

#### **URL**: `POST /contacts` *(unchanged)*
#### **Role**: ADMIN only *(unchanged)*
#### **Authentication**: Required *(unchanged)*

**Purpose**: Enhanced contact creation with integrated SEO meta management

#### **🔄 INPUT STRUCTURE CHANGE**:

**BEFORE**:
```json
{
  "contactName": "Art Store Hanoi",
  "contactSlug": "art-store-hanoi",
  "contactAddress": "123 Hang Bac Street, Hanoi",
  "contactEmail": "info@artstore-hanoi.com",
  "contactPhone": "+84-24-1234-5678",
  "contactEnabled": true,
  "seoMetaId": 456  // ← OLD: Reference existing SEO meta by ID
}
```

**NOW**:
```json
{
  "contactName": "Art Store Hanoi", 
  "contactSlug": "art-store-hanoi",
  "contactAddress": "123 Hang Bac Street, Hanoi",
  "contactEmail": "info@artstore-hanoi.com", 
  "contactPhone": "+84-24-1234-5678",
  "contactEnabled": true,
  "seoMeta": {  // ← NEW: Embedded SEO meta object
    "seoMetaTitle": "Art Store Hanoi - Premium Vietnamese Art Gallery",
    "seoMetaDescription": "Discover authentic Vietnamese art at Art Store Hanoi. Premium gallery featuring traditional and contemporary artworks in the heart of Hanoi's Old Quarter.",
    "seoMetaKeywords": "Vietnamese art, Hanoi gallery, traditional art, contemporary art",
    "seoMetaCanonicalUrl": "https://artdecor.com/contacts/art-store-hanoi",
    "seoMetaIndex": true,
    "seoMetaFollow": true,
    "seoMetaEnabled": true
  }
}
```

**🟢 Enhancement Benefits**:
- **Integrated Workflow**: Create contact + SEO in single request
- **Better UX**: No need to pre-create SEO meta separately  
- **Validation**: Full validation on embedded SEO data
- **Backward Compatible**: `seoMeta` field is optional

---

### 3.2 **Contact Update API** - ENHANCED

#### **URL**: `PUT /contacts/{contactId}` *(unchanged)*
#### **Role**: ADMIN only *(unchanged)*
#### **Authentication**: Required *(unchanged)*

**Purpose**: Enhanced contact update with SEO meta management

#### **🎯 SEO Meta Handling Logic**:

**Scenario 1 - Create new SEO meta**:
```json
{
  "contactName": "Updated Contact Name",
  "seoMeta": {  // Contact has no existing SEO → CREATE new
    "seoMetaTitle": "New SEO Title"
  }
}
```

**Scenario 2 - Update existing SEO meta**:
```json  
{
  "contactName": "Updated Contact Name",
  "seoMeta": {  // Contact has existing SEO → UPDATE existing  
    "seoMetaTitle": "Updated SEO Title"
  }
}
```

**Scenario 3 - Keep existing SEO meta**:
```json
{
  "contactName": "Updated Contact Name"
  // seoMeta omitted → Keep existing SEO meta unchanged
}
```

**🎯 Business Logic**:
- **Smart Detection**: Auto-detects create vs update based on existing SEO meta ID
- **Partial Updates**: Only provided SEO fields are updated
- **Preserve Data**: Omitting seoMeta preserves existing SEO data
- **Transaction Safety**: SEO operations wrapped in transaction with contact updates

---

## 🔐 **4. SECURITY CONFIGURATION UPDATES**

### 4.1 **New Security Rules Added**

**File**: `SecurityConfiguration.java`

```java
// Payment & Shipment status endpoints - ADMIN/MANAGER only
.requestMatchers(HttpMethod.PATCH, "/payments/*/status").hasAnyRole("ADMIN", "MANAGER")
.requestMatchers(HttpMethod.PATCH, "/shipments/*/status").hasAnyRole("ADMIN", "MANAGER")

// Contact management - ADMIN only (existing, documented for reference)
.requestMatchers(HttpMethod.POST, "/contacts").hasRole("ADMIN")
.requestMatchers(HttpMethod.PUT, "/contacts/**").hasRole("ADMIN")
```

### 4.2 **Access Control Matrix**

| **API Endpoint** | **Method** | **Required Role** | **Business Justification** |
|------------------|------------|-------------------|---------------------------|
| `/products` (filtered) | GET | Public | Products browseable by category/type |
| `/payments/{id}/status` | PATCH | ADMIN/MANAGER | Order processing authority |
| `/shipments/{id}/status` | PATCH | ADMIN/MANAGER | Logistics management |
| `/contacts` (with SEO) | POST/PUT | ADMIN | Content management control |

---

## 📋 **5. API CONSISTENCY PATTERNS**

### 5.1 **Status Update Pattern**
All status update APIs follow consistent PATCH pattern:
```
PATCH /{entity}/{id}/status
```

**Examples**:
- `PATCH /payments/123/status`
- `PATCH /shipments/456/status`  
- `PATCH /orders/789/status` (existing)

### 5.2 **Filtering Enhancement Pattern**
Product filtering APIs enhanced with slug parameters:
```
GET /{entity}?...&{entityType}Slug={slug}
```

**Examples**:
- `GET /products?productTypeSlug=paintings&productCategorySlug=modern`
- Backward compatible: old parameters still work

### 5.3 **SEO Integration Pattern**
Contact and Product APIs follow consistent SEO meta integration:
```
POST|PUT /{entity} with embedded seoMeta object
```

**Examples**:
- `POST /products` with `seoMeta: {...}`
- `POST /contacts` with `seoMeta: {...}`
- `PUT /products/{id}` with `seoMeta: {...}`
- `PUT /contacts/{id}` with `seoMeta: {...}`

---

## 📊 **6. TESTING GUIDE - New APIs**

### 6.1 **Enhanced Filtering Tests**

```bash
# Product Type filtering by slug
GET /api/products?productTypeSlug=canvas-art&size=20&page=0

# Product Category filtering by slug  
GET /api/products?productCategorySlug=modern-art&size=20&page=0

# Combined filtering (backward compatible)
GET /api/products?productTypeId=1&productTypeSlug=canvas-art&productCategorySlug=modern

# Response should include filtered products with matching slugs
```

### 6.2 **Status Update Tests**

```bash
# Payment status update (Admin/Manager required)
PATCH /api/payments/123/status
Authorization: Bearer {admin-jwt-token}
Content-Type: application/json
{
  "newStatus": "COMPLETED"
}

# Shipment status update (Admin/Manager required)  
PATCH /api/shipments/456/status
Authorization: Bearer {admin-jwt-token}
Content-Type: application/json
{
  "newStatus": "SHIPPED"
}

# Expected response: 200 OK with updated entity
```

### 6.3 **Contact SEO Integration Tests**

```bash
# Contact creation with SEO (Admin required)
POST /api/contacts
Authorization: Bearer {admin-jwt-token}
Content-Type: application/json
{
  "contactName": "Test Art Store",
  "contactSlug": "test-art-store", 
  "contactEmail": "test@artstore.com",
  "contactEnabled": true,
  "seoMeta": {
    "seoMetaTitle": "Test Art Store - Premium Gallery",
    "seoMetaDescription": "Test description for art store",
    "seoMetaEnabled": true
  }
}

# Contact update with SEO modification (Admin required)
PUT /api/contacts/123
Authorization: Bearer {admin-jwt-token} 
Content-Type: application/json
{
  "contactName": "Updated Art Store Name",
  "seoMeta": {
    "seoMetaTitle": "Updated SEO Title"
  }
}

# Contact update without SEO changes (preserves existing SEO)
PUT /api/contacts/123
Authorization: Bearer {admin-jwt-token}
Content-Type: application/json
{
  "contactName": "Updated Name Only"
  // seoMeta omitted - existing SEO preserved
}
```

---

## 🎯 **7. BUSINESS BENEFITS - April 12 Updates**

### 7.1 **Enhanced User Experience**
- **Faster Product Discovery**: Slug-based filtering for cleaner URLs and better UX
- **Real-time Status Updates**: Instant payment/shipment status visibility
- **Backward Compatibility**: All existing filtering still works

### 7.2 **Improved Admin Operations**  
- **Streamlined Workflow**: Single API calls for status updates
- **Consistent Patterns**: All status updates follow PATCH /{entity}/{id}/status
- **Role-based Security**: Proper access controls for sensitive operations

### 7.3 **System Architecture Benefits**
- **Clean URL Structure**: SEO-friendly slug filtering parameters
- **Consistent API Design**: Uniform patterns across all entities  
- **Maintainability**: Reused existing service methods where possible

### 7.4 **SEO & Content Management Benefits** - NEW
- **Integrated Workflow**: Contact + SEO management in single operation
- **Consistent Pattern**: Same SEO integration approach as Product system
- **Better Content Quality**: Easier SEO optimization for contact pages
- **Admin Efficiency**: No need for separate SEO meta management workflow

---

## 🚨 **8. MIGRATION NOTES - April 12**

### **For Frontend Developers**:

1. ✅ **Enhanced Filtering**: Optionally use slug parameters for cleaner URLs
   ```javascript
   // Enhanced filtering (recommended)
   const url = `/api/products?productTypeSlug=canvas-art&productCategorySlug=modern`;
   
   // Old filtering (still works)  
   const url = `/api/products?productTypeId=1&productCategoryId=2`;
   ```

2. ✅ **Status Update APIs**: Implement PATCH endpoints for workflow management
   ```javascript
   // Payment status update
   await fetch(`/api/payments/${paymentId}/status`, {
     method: 'PATCH',
     headers: { 'Authorization': `Bearer ${token}` },
     body: JSON.stringify({ newStatus: 'COMPLETED' })
   });
   ```

3. ✅ **Contact SEO Enhancement**: Optionally integrate SEO meta management
   ```javascript
   // Enhanced contact creation (backward compatible)
   const contactData = {
     contactName: "Art Store Name",
     contactEmail: "info@artstore.com", 
     contactEnabled: true,
     seoMeta: {  // NEW optional embedded SEO object  
       seoMetaTitle: "SEO Title",
       seoMetaDescription: "SEO Description", 
       seoMetaEnabled: true
     }
   };
   
   // Old format still works (backward compatible)
   const oldContactData = {
     contactName: "Art Store Name",
     contactEnabled: true
     // No seoMeta - still works fine
   };
   ```

### **For Backend Developers**:

1. ✅ **Repository Updates**: Enhanced @Query methods support additional slug parameters  
2. ✅ **Service Layer**: Updated interfaces with new method signatures
3. ✅ **Contact Service Updates**: Update service implementations for new SEO parameters
4. ✅ **Database**: Ensure Contact-SeoMeta relationships are properly configured

---

### 3.3 **API Response Structure** *(unchanged)*

All Contact API responses remain the same structure - no breaking changes to response format.

---

## 🔐 **4. SECURITY CONFIGURATION UPDATES**

### 4.1 **New Security Rules Added**

```java
// Payment status update - Admin only
.requestMatchers(HttpMethod.PATCH, "/payments/*/status").hasRole("ADMIN")

// Shipment status update - Admin only  
.requestMatchers(HttpMethod.PATCH, "/shipments/*/status").hasRole("ADMIN")

// Order status update - Already configured
.requestMatchers(HttpMethod.PATCH, "/orders/*/status").hasRole("ADMIN")
```

### 4.2 **Access Control Matrix**

| **Entity** | **Read API** | **Full Update** | **Status Update** | **Role** |
|------------|--------------|-----------------|-------------------|----------|
| **Order** | `GET /orders` | ❌ | `PATCH /orders/{id}/status` | ADMIN |
| **Payment** | `GET /payments` | `PUT /payments/{id}` | `PATCH /payments/{id}/status` | ADMIN |
| **Shipment** | `GET /shipments` | `PUT /shipments/{id}` | `PATCH /shipments/{id}/status` | ADMIN |
| **ProductType** | `GET /products/types` | `PUT /products/types/{id}` | N/A | PUBLIC/ADMIN |
| **ProductCategory** | `GET /products/categories` | `PUT /products/categories/{id}` | N/A | PUBLIC/ADMIN |

---

## 📋 **5. API CONSISTENCY PATTERNS**

### 5.1 **Status Update Pattern**
All status update APIs follow consistent pattern:
```
PATCH /{entity}/{entityId}/status?{entityStateId}=newStateId
```

**Examples**:
- `PATCH /payments/123/status?paymentStateId=2`
- `PATCH /shipments/456/status?shipmentStateId=3`  
- `PATCH /orders/789/status` (with RequestBody for additional data)

### 5.2 **Filtering Enhancement Pattern**
All collection APIs support slug filtering:
```
GET /{entity}?textSearch=keyword&enabled=true&{entitySlug}=exact-slug
```

**Examples**:
- `GET /products/types?productTypeSlug=wall-art`
- `GET /products/categories?productCategorySlug=modern-art`

---

## 📋 **6. TESTING GUIDE - New APIs**

### 6.1 **Enhanced Filtering Tests**

```bash
# Product Types filtering
GET /api/products/types?productTypeSlug=wall-art
GET /api/products/types?textSearch=art&productTypeSlug=wall-art

# Product Categories filtering  
GET /api/products/categories?productCategorySlug=modern-art
GET /api/products/categories?rootOnly=true&productCategorySlug=art-collections
```

### 6.2 **Status Update Tests**

```bash
# Payment status update (Admin required)
PATCH /api/payments/123/status?paymentStateId=2
Authorization: Bearer {admin-jwt-token}

# Shipment status update (Admin required)
PATCH /api/shipments/456/status?shipmentStateId=3  
Authorization: Bearer {admin-jwt-token}

# Order status update (Existing - Admin required)
PATCH /api/orders/789/status
Authorization: Bearer {admin-jwt-token}
Content-Type: application/json
{
  "newOrderStateId": 4,
  "statusNote": "Order delivered successfully"
}
```

---

## 🎯 **7. BUSINESS BENEFITS - April 12 Updates**

### 7.1 **Enhanced User Experience**
- **Precise Filtering**: Slug-based filtering cho better search precision
- **SEO-Friendly**: Supports URL-based category/type filtering
- **Performance**: Exact slug matching instead of text search

### 7.2 **Improved Admin Operations** 
- **Quick Status Updates**: Update status without full entity modification
- **Consistent API Design**: Same pattern across Order/Payment/Shipment
- **Audit Trail**: Proper logging cho status changes

### 7.3 **System Architecture Benefits**
- **Clean Code**: Reuses existing service methods (no duplication)
- **Security Consistency**: All status updates require ADMIN role
- **Database Efficiency**: Targeted updates instead of full entity saves

---

## 🚨 **8. MIGRATION NOTES - April 12**

### **For Frontend Developers**:
1. ✅ **Optional Enhancement**: Add slug filtering to product type/category searches
   ```javascript
   // Enhanced filtering (backward compatible)
   const params = {
     textSearch: searchTerm,
     enabled: true,
     productTypeSlug: exactSlug  // NEW optional parameter
   };
   ```

2. ✅ **New Admin Features**: Implement quick status update buttons
   ```javascript
   // Payment status update
   await fetch(`/api/payments/${paymentId}/status?paymentStateId=${newStateId}`, {
     method: 'PATCH',
     headers: { 'Authorization': `Bearer ${adminToken}` }
   });
   ```

### **For Backend Developers**:
1. ✅ **API Testing**: Test all new endpoints with proper role-based access
2. ✅ **Service Layer**: Verify existing service methods integration
3. ✅ **Contact Service Updates**: Update service implementations for new SEO parameters
4. ✅ **Database**: Ensure Contact-SeoMeta relationships are properly configured

---

**🔄 Backward Compatibility**: All changes are backward compatible. Existing clients will continue working without modification.

---

# 📚 **PREVIOUS CHANGES - April 11, 2026**

The following documentation covers the major Product Attribute/Variant System refactoring from April 11, 2026:

---

## 📅 **Change Date**: April 11, 2026
## 🎯 **Scope**: Product Attribute/Variant System Architecture Refactoring + Display Name Enhancement

---

## 🚫 **1. APIs BỊ XÓA (DEPRECATED/REMOVED)**

### 1.1 Product Attribute Management APIs (Product-Specific)
**Lý do**: Chuyển từ product-specific attributes sang master catalog + product variants

| Endpoint (OLD) | Method | Mô tả |
|---|---|---|
| `/products/{productId}/attributes` | GET | Lấy attributes của 1 product cụ thể |
| `/products/{productId}/attributes` | POST | Thêm attribute cho 1 product cụ thể |
| `/products/{productId}/attributes/{attributeId}` | PUT | Cập nhật attribute của 1 product |
| `/products/{productId}/attributes/{attributeId}` | DELETE | Xóa attribute của 1 product |
| `/products/{productId}/attributes/{attributeId}/quantity` | PATCH | Cập nhật quantity attribute của 1 product |

**🔴 Impact cho Client**: Các client đang sử dụng endpoints này sẽ nhận lỗi 404 hoặc 400. Cần migrate sang Product Variants APIs.

---

## 🔄 **2. APIs ĐÃ THAY ĐỔI**

### 2.1 **Product Creation API**

#### **URL**: `POST /products` *(không đổi)*
#### **Role**: ADMIN, MANAGER *(không đổi)*
#### **Authentication**: Required *(không đổi)*

#### **🔄 THAY ĐỔI INPUT**:

**VERSION CŨ**:
```json
{
  "productName": "Modern Art Piece",
  "productSlug": "modern-art-001",
  "productCode": "ART-001",
  "productAttributes": [  // ← Field cũ
    {
      "productAttrId": 1,
      "productAttributeValue": "40x60cm",
      "productAttributePrice": 1500000.00,
      "productAttributeQuantity": 10  // ← Field cũ
    }
  ]
}
```

**VERSION MỚI**:
```json
{
  "productName": "Modern Art Piece", 
  "productSlug": "modern-art-001",
  "productCode": "ART-001",
  "productVariants": [  // ← Thay đổi từ productAttributes
    {
      "productAttributeId": 1,        // ← Thay đổi từ productAttrId + value + price
      "productVariantStock": 10,       // ← Thay đổi từ productVariantQuantity
      "productVariantEnabled": true   // ← Field mới
    }
  ]
}
```

**🔴 Breaking Changes**:
- `productAttributes` → `productVariants`
- `productAttrId + productAttributeValue + productAttributePrice` → `productAttributeId` (reference to master catalog)
- `productAttributeQuantity` → `productVariantStock`

### 2.2 **Product Get by ID API**

#### **URL**: `GET /products/{productId}` *(không đổi)*
#### **Role**: PUBLIC *(thay đổi từ ADMIN, MANAGER)*
#### **Authentication**: Not Required *(thay đổi từ Required)*

#### **🔄 THAY ĐỔI ACCESS CONTROL**:

**VERSION CŨ**: 
- **Role**: ADMIN, MANAGER only
- **Authentication**: Required
- **Mục đích**: Chỉ admin/manager có thể xem product details

**VERSION MỚI**:
- **Role**: PUBLIC 
- **Authentication**: Not Required
- **Mục đích**: Customer có thể xem product details bằng ID

**🟢 Enhancement**: Giờ customer có thể access product details trực tiếp bằng ID, không chỉ qua slug

### 2.3 **Product Update API**

**🔴 Impact Client**: Cần update request body structure khi create/update products.

---

### 2.4 **Product Attributes APIs - MASTER CATALOG**

#### **URL**: `GET /products/attributes` *(không đổi URL nhưng thay đổi behavior)*
#### **Role**: PUBLIC *(thay đổi từ ADMIN only)*
#### **Authentication**: Not Required *(thay đổi từ Required)*

#### **🔄 THAY ĐỔI OUTPUT**:

**VERSION CŨ** (Product-specific):
```json
{
  "data": [
    {
      "productAttributeId": 1,
      "productAttrId": 1,
      "productAttributeValue": "40x60cm", 
      "productAttributePrice": 1500000.00,
      "productAttributeQuantity": 10,    // ← Stock của product cụ thể
      "productId": 123                   // ← Reference product cụ thể
    }
  ]
}
```

**VERSION MỚI** (Master catalog):
```json
{
  "data": [
    {
      "productAttributeId": 1,
      "productAttrId": 1, 
      "productAttributeValue": "40x60cm",
      "productAttributeDisplayName": "Kích thước 40x60cm",    // ← Field mới
      "productAttributePrice": 1500000.00,
      "productAttributeEnabled": true
      // ← Không có productId, không có quantity (moved to ProductVariant)
    }
  ]
}
```

**🟢 Non-Breaking Enhancement**: Thêm `productAttributeDisplayName` cho UX tốt hơn

---

### 2.5 **Product Attribute Creation API**

#### **URL**: `POST /products/attributes` *(không đổi)*
#### **Role**: ADMIN, MANAGER *(không đổi)*
#### **Authentication**: Required *(không đổi)*

#### **🔄 THAY ĐỔI INPUT**:

**VERSION CŨ**:
```json
{
  "productId": 123,                   // ← Field bị xóa  
  "productAttrId": 1,
  "productAttributeValue": "40x60cm",
  "productAttributePrice": 1500000.00,
  "productAttributeQuantity": 10      // ← Field bị xóa
}
```

**VERSION MỚI**:
```json
{
  "productAttrId": 1,
  "productAttributeValue": "40x60cm",
  "productAttributeDisplayName": "Kích thước 40x60cm",  // ← Field mới
  "productAttributePrice": 1500000.00,
  "productAttributeEnabled": true
  // ← Không có productId, không có quantity
}
```

**🔴 Breaking Changes**: 
- Xóa `productId` - giờ tạo master catalog, không specific product
- Xóa `productAttributeQuantity` - quantity manage ở ProductVariant level
- Thêm `productAttributeDisplayName` - optional field cho UX

**✅ API Update ProductAttribute**: Đã tồn tại `PUT /products/attributes/{productAttributeId}` với đầy đủ chức năng update master catalog.

---

## 🆕 **3. APIs MỚI ĐƯỢC THÊM**

### 3.1 **Product Variants Management**

#### **3.1.1 Get Product Variants**
- **URL**: `GET /products/{productId}/variants`
- **Role**: PUBLIC
- **Authentication**: Not Required
- **Response**: 
```json
{
  "data": [
    {
      "productVariantId": 1,
      "productId": 123,
      "productAttributeId": 1,
      "productVariantStock": 10,
      "productVariantEnabled": true,
      "productAttribute": {
        "productAttributeValue": "40x60cm",
        "productAttributeDisplayName": "Kích thước 40x60cm",
        "productAttributePrice": 1500000.00
      }
    }
  ]
}
```

#### **3.1.2 Create Product Variant**
- **URL**: `POST /products/{productId}/variants`
- **Role**: ADMIN, MANAGER
- **Authentication**: Required
- **Input**:
```json
{
  "productAttributeId": 1,        // Reference to master catalog
  "productVariantStock": 10,
  "productVariantEnabled": true
}
```

#### **3.1.3 Update Variant Stock**  
- **URL**: `PATCH /products/variants/{variantId}/stock`
- **Role**: ADMIN, MANAGER
- **Authentication**: Required
- **Input**:
```json
{
  "productVariantStock": 15
}
```

#### **3.1.4 Delete Product Variant**
- **URL**: `DELETE /products/variants/{variantId}`
- **Role**: ADMIN, MANAGER  
- **Authentication**: Required

---

## 🔐 **4. SECURITY CHANGES**

### 4.1 **Access Control Updates**

#### **PUBLIC ACCESS** (permitAll):
```
GET /products/{productId}                  // ← Thay đổi từ ADMIN/MANAGER only
GET /products/attributes                    // ← Thay đổi từ ADMIN only
GET /products/attributes/{id}              // ← Thay đổi từ ADMIN only  
GET /products/{productId}/variants         // ← API mới
```

#### **ADMIN/MANAGER ACCESS** (unchanged):
```
POST /products/attributes
PUT /products/attributes/{id}
DELETE /products/attributes/{id}
POST /products/{productId}/variants        // ← API mới
PATCH /products/variants/{id}/stock       // ← API mới
DELETE /products/variants/{id}             // ← API mới
```

---

## 📋 **5. MIGRATION GUIDE**

### 5.1 **Frontend Changes Required**

#### **Product Creation/Update Forms**:
```javascript
// OLD CODE ❌
const productData = {
  productName: "Art Piece",
  productAttributes: [
    {
      productAttrId: 1,
      productAttributeValue: "40x60cm", 
      productAttributePrice: 1500000,
      productAttributeQuantity: 10
    }
  ]
};

// NEW CODE ✅
const productData = {
  productName: "Art Piece",
  productVariants: [
    {
      productAttributeId: 1,    // Pre-created in master catalog
      productVariantStock: 10,
      productVariantEnabled: true
    }
  ]
};
```

#### **Product Attribute Display**:
```javascript
// OLD CODE ❌
fetch(`/api/products/${productId}/attributes`)

// NEW CODE ✅  
fetch(`/api/products/${productId}/variants`)  // For product-specific data
fetch(`/api/products/attributes`)             // For master catalog

// BONUS: Product by ID now public ✅
fetch(`/api/products/${productId}`)           // No authentication needed
```

### 5.2 **Backend Integration Changes**

#### **Cart/Order Services**:
```java
// OLD CODE ❌
productAttributeRepository.findByProductIdAndAttrId(productId, attrId);

// NEW CODE ✅
productVariantRepository.existsByProductIdAndProductAttributeId(productId, attributeId);
```

---

## ⚠️ **6. TESTING PRIORITIES**

### 6.1 **Critical Test Cases**
1. **Product Creation/Update** với productVariants array
2. **Product Variants CRUD** operations
3. **Master Catalog Access** (public endpoints)  
4. **Stock Management** thông qua ProductVariant
5. **Cart Integration** với ProductVariant validation
6. **Display Name** rendering trong UI

### 6.2 **API Endpoints to Test**
```bash
# Product Access (công khai)
GET /api/products/123                    # ← API mới public
GET /api/products/slug/modern-art-001

# Master Catalog (công khai)
GET /api/products/attributes
GET /api/products/attributes/1

# Product Variants (theo product)
GET /api/products/123/variants
POST /api/products/123/variants  
PATCH /api/products/variants/1/stock

# Product CRUD (updated structure)
POST /api/products
PUT /api/products/123
```

---

## 🎯 **7. BUSINESS BENEFITS**

### 7.1 **Improved Data Architecture**
- **Master Catalog**: Tách biệt attribute definitions khỏi product mappings
- **Flexible Pricing**: Centralized pricing với product-specific overrides
- **Better UX**: Vietnamese display names (`productAttributeDisplayName`)

### 7.2 **Enhanced Performance**
- **Reduced Duplication**: Master catalog tránh duplicate attribute data
- **Efficient Queries**: Proper indexing cho ProductVariant lookups
- **Scalable Stock Management**: Individual tracking per product-attribute combination

---

## 🚨 **8. IMMEDIATE ACTION ITEMS**

### **For Frontend Developers**:
1. ✅ Update product creation/update forms
2. ✅ Change API endpoints từ `/attributes` sang `/variants` cho product-specific data
3. ✅ Add display name rendering for better UX
4. ✅ Update cart integration logic
5. ✅ **NEW**: Remove authentication requirement cho `GET /products/{productId}` endpoint

### **For Backend Developers**:  
1. ✅ Test all updated API endpoints
2. ✅ Verify ProductVariant validation in cart services
3. ✅ Test security access control changes
4. ✅ Update integration tests

### **For Database Team**:
1. ✅ Execute PRODUCT_VARIANT table creation
2. ✅ Add PRODUCT_ATTRIBUTE_DISPLAY_NAME column  
3. ✅ Migrate existing data if needed
4. ✅ Verify foreign key constraints

---

**📞 Support Contact**: Development team for any migration assistance needed.