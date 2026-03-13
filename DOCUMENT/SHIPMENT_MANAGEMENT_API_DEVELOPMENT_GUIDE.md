# SHIPMENT MANAGEMENT API DEVELOPMENT GUIDE

**Project:** Art & Decor E-commerce Platform  
**Date:** March 06, 2026  
**Author:** Development Team  
**Version:** 1.0  
**Features:** Complete Shipment Management APIs with Tracking, State Management, Fee Calculation, and Comprehensive Filtering  

---

## Overview

The Shipment Management API provides comprehensive functionality for handling shipping operations including shipment lifecycle management, state tracking, shipping fee configuration, and delivery management. All shipment operations are designed with:

- **Multi-Entity Management:** Complete CRUD operations for Shipments, Shipment States, Shipping Fees, and Shipping Fee Types
- **Advanced Tracking:** Real-time shipment status tracking with shipped/delivered timestamps and state history
- **Smart Fee Calculation:** Automatic shipping fee calculation based on order amount and configurable pricing tiers
- **Comprehensive Filtering:** Multi-criteria search with date ranges, location filters, state filters, and text search
- **Address Management:** Structured address handling with separate fields for line, city, district, ward, and country
- **Role-Based Security:** Tiered access control with different permissions for customers, managers, and administrators
- **OpenAPI Documentation:** Complete API documentation with interactive testing capabilities
- **Business Intelligence:** Statistics and analytics for shipment performance monitoring

---

## API Overview

The Shipment Management API provides comprehensive endpoints for managing the complete shipment lifecycle, fee configurations, and tracking operations.

### Available Endpoints

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/shipments` | MANAGER, ADMIN | Retrieve all shipments with advanced filtering |
| GET | `/api/shipments/{id}` | CUSTOMER, MANAGER, ADMIN | Get shipment details by ID |
| POST | `/api/shipments` | MANAGER, ADMIN | Create a new shipment |
| PUT | `/api/shipments/{id}` | MANAGER, ADMIN | Update shipment information |
| DELETE | `/api/shipments/{id}` | ADMIN | Soft delete a shipment |
| GET | `/api/shipments/states` | ALL | Get all shipment states |
| PUT | `/api/shipments/{id}/state` | MANAGER, ADMIN | Update shipment state |
| GET | `/api/shipments/fees/types` | ALL | Get all shipping fee types |
| GET | `/api/shipments/fees` | MANAGER, ADMIN | Get shipping fee configurations |
| POST | `/api/shipments/fees` | ADMIN | Create shipping fee configuration |
| PUT | `/api/shipments/fees/{id}` | ADMIN | Update shipping fee configuration |
| DELETE | `/api/shipments/fees/{id}` | ADMIN | Delete shipping fee configuration |
| GET | `/api/shipments/calculate-fee` | ALL | Calculate shipping fee for order |
| GET | `/api/shipments/statistics` | MANAGER, ADMIN | Get shipment statistics |

### Key Features

- **Advanced Filtering:** Multi-criteria search with date ranges, location filters, state filters, and full-text search
- **Real-time Tracking:** Live shipment status updates with shipped/delivered timestamps
- **Dynamic Fee Calculation:** Automatic shipping cost calculation based on configurable price tiers
- **State Management:** Complete shipment lifecycle tracking from preparation to delivery
- **Address Validation:** Structured address management with city/district/ward hierarchy
- **Business Analytics:** Comprehensive statistics for shipment performance monitoring
- **Role-based Security:** Tiered access control for different user types
- **Audit Trail:** Complete tracking of shipment modifications and state changes

---

## Database Structure

### Core Shipment Management Tables

#### SHIPPING_FEE_TYPE Table
- **PURPOSE:** Shipping fee calculation types (FIXED_AMOUNT, PERCENTAGE, FREE_SHIPPING)
- **KEY FIELDS:** SHIPPING_FEE_TYPE_ID, SHIPPING_FEE_TYPE_NAME, SHIPPING_FEE_TYPE_DISPLAY_NAME, SHIPPING_FEE_TYPE_ENABLED
- **BUSINESS LOGIC:** Defines how shipping fees are calculated (fixed amount vs percentage of order value)
- **INDEXING:** Primary key on SHIPPING_FEE_TYPE_ID, unique index on SHIPPING_FEE_TYPE_NAME

#### SHIPPING_FEE Table  
- **PURPOSE:** Shipping fee configuration based on order amount ranges
- **KEY FIELDS:** SHIPPING_FEE_ID, SHIPPING_FEE_TYPE_ID, MIN_ORDER_PRICE, MAX_ORDER_PRICE, SHIPPING_FEE_VALUE, SHIPPING_FEE_ENABLED
- **BUSINESS LOGIC:** Price tier management where different order amounts have different shipping costs
- **INDEXING:** Primary key on SHIPPING_FEE_ID, index on price range for efficient lookup

#### SHIPMENT_STATE Table
- **PURPOSE:** Shipment lifecycle states (PREPARING, SHIPPED, IN_TRANSIT, DELIVERED, FAILED_DELIVERY)
- **KEY FIELDS:** SHIPMENT_STATE_ID, SHIPMENT_STATE_NAME, SHIPMENT_STATE_DISPLAY_NAME, SHIPMENT_STATE_ENABLED
- **BUSINESS LOGIC:** State machine for tracking shipment progress from creation to delivery
- **INDEXING:** Primary key on SHIPMENT_STATE_ID, unique index on SHIPMENT_STATE_NAME

#### SHIPMENT Table (Main Entity)
- **SHIPMENT_ID:** Primary key (AUTO_INCREMENT BIGINT)
- **ORDER_ID:** Foreign key to ORDER (CASCADE DELETE)
- **SHIPMENT_CODE:** Unique tracking code (VARCHAR(64), UNIQUE, NOT NULL)
- **SHIPMENT_STATE_ID:** Foreign key to SHIPMENT_STATE (RESTRICT DELETE)
- **RECEIVER_NAME, RECEIVER_PHONE, RECEIVER_EMAIL:** Recipient information snapshot
- **ADDRESS_LINE, CITY, DISTRICT, WARD, COUNTRY:** Structured address fields
- **SHIPPING_FEE_AMOUNT:** Shipping fee snapshot (DECIMAL(15,2), DEFAULT 0)
- **SHIPPED_AT, DELIVERED_AT:** Delivery timeline timestamps
- **SHIPMENT_REMARK:** Optional notes (VARCHAR(256))
- **CREATED_DT, MODIFIED_DT:** Record timestamps

---

## API Endpoints

### 1. Shipment Management Endpoints

#### 1.1 Get All Shipments with Advanced Filtering
**Endpoint:** `GET /api/shipments`  
**Method:** GET  
**Access:** ADMIN, MANAGER  
**OpenAPI Summary:** "Get all shipments with advanced filtering and pagination"
**Description:** Retrieve shipments with comprehensive filtering options including date ranges, location filters, and shipment states.

**Query Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `shipmentId` | Long | No | Filter by specific shipment ID | `1` |
| `orderId` | Long | No | Filter by order ID | `123` |
| `shipmentCode` | String | No | Filter by shipment tracking code | `"SH001"` |
| `shipmentStateId` | Long | No | Filter by shipment state | `2` |
| `receiverName` | String | No | Filter by receiver name | `"John Doe"` |
| `receiverPhone` | String | No | Filter by receiver phone | `"0901234567"` |
| `city` | String | No | Filter by delivery city | `"Ho Chi Minh"` |
| `country` | String | No | Filter by delivery country | `"Vietnam"` |
| `minShippingFee` | BigDecimal | No | Minimum shipping fee filter | `50.00` |
| `maxShippingFee` | BigDecimal | No | Maximum shipping fee filter | `200.00` |
| `shippedAfter` | LocalDateTime | No | Shipped after date (yyyy-MM-dd HH:mm:ss) | `"2026-03-01 00:00:00"` |
| `shippedBefore` | LocalDateTime | No | Shipped before date | `"2026-03-31 23:59:59"` |
| `deliveredAfter` | LocalDateTime | No | Delivered after date | `"2026-03-01 00:00:00"` |
| `deliveredBefore` | LocalDateTime | No | Delivered before date | `"2026-03-31 23:59:59"` |
| `textSearch` | String | No | Search across multiple fields | `"vietnam"` |
| `page` | Integer | No | Page number (0-indexed) | `0` |
| `size` | Integer | No | Page size (max 100) | `10` |
| `sortBy` | String | No | Sort field (default: createdDt) | `"shipmentCode"` |
| `sortDirection` | String | No | Sort direction (asc/desc) | `"desc"` |

**Response Example:**
```json
{
  "success": true,
  "message": "Shipments retrieved successfully",
  "data": {
    "content": [
      {
        "shipmentId": 1,
        "orderId": 123,
        "shipmentCode": "SH20260306001",
        "receiverName": "John Doe",
        "receiverPhone": "0901234567",
        "receiverEmail": "john.doe@example.com",
        "addressLine": "123 Main Street",
        "city": "Ho Chi Minh",
        "district": "District 1",
        "ward": "Ward 1",
        "country": "Vietnam",
        "shippingFeeAmount": 50.00,
        "shippedAt": "2026-03-05T10:30:00",
        "deliveredAt": null,
        "shipmentRemark": "Handle with care",
        "createdDt": "2026-03-05T09:00:00",
        "modifiedDt": "2026-03-05T10:30:00",
        "shipmentState": {
          "shipmentStateId": 2,
          "shipmentStateName": "SHIPPED",
          "shipmentStateDisplayName": "Shipped"
        },
        "fullAddress": "123 Main Street, Ward 1, District 1, Ho Chi Minh, Vietnam"
      }
    ],
    "totalElements": 50,
    "totalPages": 5,
    "size": 10,
    "number": 0
  }
}
```

#### 1.2 Get Shipment by ID
**Endpoint:** `GET /api/shipments/{shipmentId}`  
**Method:** GET  
**Access:** ADMIN, MANAGER, or OWNER  
**Description:** Retrieve detailed shipment information. Users can only access shipments for their own orders.

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `shipmentId` | Long | Yes | Shipment ID |

#### 1.3 Get Shipment by Tracking Code
**Endpoint:** `GET /api/shipments/code/{shipmentCode}`  
**Method:** GET  
**Access:** ADMIN, MANAGER, or OWNER  
**Description:** Track shipment using the unique tracking code.

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `shipmentCode` | String | Yes | Shipment tracking code |

#### 1.4 Get Shipments by Order ID
**Endpoint:** `GET /api/shipments/order/{orderId}`  
**Method:** GET  
**Access:** ADMIN, MANAGER, or OWNER  
**Description:** Retrieve all shipments for a specific order.

#### 1.5 Create New Shipment
**Endpoint:** `POST /api/shipments`  
**Method:** POST  
**Access:** ADMIN, MANAGER  
**Description:** Create a new shipment for an order.

**Request Body Example:**
```json
{
  "orderId": 123,
  "shipmentCode": "SH20260306002",
  "receiverName": "Jane Smith",
  "receiverPhone": "0907654321",
  "receiverEmail": "jane.smith@example.com",
  "addressLine": "456 Oak Avenue",
  "city": "Hanoi",
  "district": "Ba Dinh",
  "ward": "Cong Vi",
  "country": "Vietnam",
  "shippingFeeAmount": 75.00,
  "shipmentRemark": "Fragile items"
}
```

#### 1.6 Update Shipment
**Endpoint:** `PUT /api/shipments/{shipmentId}`  
**Method:** PUT  
**Access:** ADMIN, MANAGER  
**Description:** Update shipment information.

#### 1.7 Update Shipment State
**Endpoint:** `PUT /api/shipments/{shipmentId}/state/{shipmentStateId}`  
**Method:** PUT  
**Access:** ADMIN, MANAGER  
**Description:** Change shipment state with optional remarks.

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `remark` | String | No | Optional state change remark |

#### 1.8 Mark as Shipped
**Endpoint:** `PUT /api/shipments/{shipmentId}/ship`  
**Method:** PUT  
**Access:** ADMIN, MANAGER  
**Description:** Mark shipment as shipped with timestamp.

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `shippedAt` | LocalDateTime | No | Shipped timestamp (defaults to now) |
| `remark` | String | No | Optional remark |

#### 1.9 Mark as Delivered
**Endpoint:** `PUT /api/shipments/{shipmentId}/deliver`  
**Method:** PUT  
**Access:** ADMIN, MANAGER  
**Description:** Mark shipment as delivered with timestamp.

#### 1.10 Get Shipment Statistics
**Endpoint:** `GET /api/shipments/statistics`  
**Method:** GET  
**Access:** ADMIN, MANAGER  
**Description:** Retrieve comprehensive shipment statistics and analytics.

### 2. Shipment State Management Endpoints

#### 2.1 Get All Shipment States
**Endpoint:** `GET /api/shipments/states`  
**Method:** GET  
**Access:** ADMIN, MANAGER  
**Description:** Retrieve all shipment states with filtering and pagination.

#### 2.2 Get Enabled Shipment States
**Endpoint:** `GET /api/shipments/states/enabled`  
**Method:** GET  
**Access:** PUBLIC  
**Description:** Get all active shipment states for UI display.

#### 2.3 Create Shipment State
**Endpoint:** `POST /api/shipments/states`  
**Method:** POST  
**Access:** ADMIN  
**Description:** Create a new shipment state.

**Request Body Example:**
```json
{
  "shipmentStateName": "IN_TRANSIT",
  "shipmentStateDisplayName": "In Transit",
  "shipmentStateRemark": "Package is on the way to destination",
  "shipmentStateEnabled": true
}
```

### 3. Shipping Fee Management Endpoints

#### 3.1 Get All Shipping Fees
**Endpoint:** `GET /api/shipments/fees`  
**Method:** GET  
**Access:** ADMIN, MANAGER  
**Description:** Retrieve shipping fee configurations with filtering.

#### 3.2 Calculate Shipping Fee
**Endpoint:** `GET /api/shipments/fees/calculate`  
**Method:** GET  
**Access:** PUBLIC  
**Description:** Calculate shipping fee for a given order amount.

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `orderAmount` | BigDecimal | Yes | Order total amount |

**Response Example:**
```json
{
  "success": true,
  "message": "Shipping fee calculated successfully",
  "data": {
    "shippingFeeId": 3,
    "shippingFeeValue": 50.00,
    "minOrderPrice": 100.00,
    "maxOrderPrice": 500.00,
    "shippingFeeDisplayName": "Standard Shipping",
    "shippingFeeRemark": "Standard delivery within 3-5 business days"
  }
}
```

#### 3.3 Create Shipping Fee
**Endpoint:** `POST /api/shipments/fees`  
**Method:** POST  
**Access:** ADMIN  
**Description:** Create new shipping fee configuration.

### 4. Shipping Fee Type Management Endpoints

#### 4.1 Get All Shipping Fee Types
**Endpoint:** `GET /api/shipments/fee-types`  
**Method:** GET  
**Access:** ADMIN, MANAGER  

#### 4.2 Get Enabled Fee Types
**Endpoint:** `GET /api/shipments/fee-types/enabled`  
**Method:** GET  
**Access:** PUBLIC  

#### 4.3 Create Fee Type
**Endpoint:** `POST /api/shipments/fee-types`  
**Method:** POST  
**Access:** ADMIN  

---

## Business Logic Implementation

### 1. Shipping Fee Calculation Algorithm
```java
public ShippingFeeDto calculateShippingFee(BigDecimal orderAmount) {
    // Find applicable shipping fees for the order amount
    List<ShippingFee> applicable = repository.findApplicableShippingFees(orderAmount);
    
    // Select the cheapest applicable fee
    return applicable.stream()
        .min(Comparator.comparing(ShippingFee::getShippingFeeValue))
        .map(mapper::mapToDto)
        .orElseThrow(() -> new RuntimeException("No shipping fee available"));
}
```

### 2. Shipment Code Generation
```java
public String generateShipmentCode() {
    String prefix = "SH";
    String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    Long sequence = getNextSequenceNumber();
    return String.format("%s%s%03d", prefix, dateStr, sequence);
}
```

### 3. Address Validation and Formatting
```java
public String getFullAddress() {
    StringBuilder sb = new StringBuilder();
    if (addressLine != null) sb.append(addressLine);
    if (ward != null) sb.append(", ").append(ward);
    if (district != null) sb.append(", ").append(district);
    if (city != null) sb.append(", ").append(city);
    if (country != null) sb.append(", ").append(country);
    return sb.toString();
}
```

---

## Security and Access Control

### Role-Based Access Matrix

| Endpoint | ADMIN | MANAGER | USER | PUBLIC |
|----------|-------|---------|------|--------|
| List All Shipments | ✓ | ✓ | ✗ | ✗ |
| Get Shipment by ID | ✓ | ✓ | Own Only | ✗ |
| Create Shipment | ✓ | ✓ | ✗ | ✗ |
| Update Shipment | ✓ | ✓ | ✗ | ✗ |
| Track by Code | ✓ | ✓ | Own Only | ✗ |
| Calculate Shipping Fee | ✓ | ✓ | ✓ | ✓ |
| Manage States | ✓ | ✗ | ✗ | ✗ |
| Manage Fee Config | ✓ | ✗ | ✗ | ✗ |
| View Statistics | ✓ | ✓ | ✗ | ✗ |

### Authentication Requirements
- **JWT Bearer Token:** Required for all protected endpoints
- **User Context:** `authentication.principal.userId` used for ownership validation
- **Role Validation:** `@PreAuthorize` annotations enforce role-based access

---

## Error Handling

### Common Error Responses

#### 404 - Shipment Not Found
```json
{
  "success": false,
  "message": "Shipment not found with ID: 123",
  "data": null,
  "timestamp": "2026-03-06T10:00:00"
}
```

#### 400 - Invalid Request
```json
{
  "success": false,
  "message": "Invalid order amount: must be positive",
  "data": null,
  "timestamp": "2026-03-06T10:00:00"
}
```

#### 403 - Access Denied
```json
{
  "success": false,
  "message": "Access denied - insufficient permissions",
  "data": null,
  "timestamp": "2026-03-06T10:00:00"
}
```

---

## Testing Guide

### Unit Testing
- **Service Layer:** Test business logic including fee calculation, state transitions
- **Repository Layer:** Test custom queries and data access patterns
- **Controller Layer:** Test endpoint responses and security annotations

### Integration Testing
- **End-to-End Flows:** Complete shipment lifecycle from creation to delivery
- **Security Testing:** Role-based access control validation
- **Performance Testing:** Large dataset pagination and filtering

### Sample Test Cases
```java
@Test
public void testCalculateShippingFee_ValidAmount_ReturnsCorrectFee() {
    // Test fee calculation for various order amounts
}

@Test
public void testUpdateShipmentState_ValidTransition_Success() {
    // Test state transitions
}

@Test
public void testGetShipmentsByUser_OnlyOwnShipments_Success() {
    // Test access control
}
```

---

## Performance Considerations

### Database Optimization
- **Indexing:** Composite indexes on frequently filtered columns
- **Query Optimization:** Use of criteria queries with proper joins
- **Pagination:** Limit large result sets with configurable page sizes

### Caching Strategy
- **Shipping Fee Types:** Cache enabled types for public access
- **State Lists:** Cache active shipment states
- **User Permissions:** Cache role-based access decisions

### API Performance
- **Lazy Loading:** Related entities loaded on demand
- **Projection DTOs:** Return only required fields in responses
- **Bulk Operations:** Batch processing for multiple shipment updates

---

## Deployment and Configuration

### Environment Variables
```properties
# Shipment Configuration
shipment.code.prefix=SH
shipment.default.country=Vietnam
shipment.max.page.size=100

# Fee Configuration  
shipping.fee.default.enabled=true
shipping.fee.cache.ttl=3600

# Security Configuration
security.shipment.owner.access=true
security.admin.bypass=false
```

### Database Migration Scripts
- **V1.0:** Initial shipment tables creation
- **V1.1:** Add tracking timestamps
- **V1.2:** Enhanced address structure
- **V1.3:** Fee calculation optimization

This comprehensive guide provides complete coverage of the Shipment Management API, enabling developers to effectively implement, test, and maintain the shipping functionality within the Art & Decor e-commerce platform.