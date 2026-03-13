# CONFIGURATION MANAGEMENT API DEVELOPMENT GUIDE

**Project:** Art & Decor E-commerce Platform  
**Date:** February 26, 2026  
**Author:** Development Team  
**Version:** 2.0  
**Features:** Updated Configuration Management APIs for CONTACT and POLICY tables  

---

## Overview

The Configuration Management API provides complete functionality for managing system configuration (Policy) and contact information. These APIs support:

- **Policy Management:** System-wide configuration settings (storage paths, website text, UI settings)
- **Contact Management:** Business contact information and location details
- **Access Control:** Role-based access (public read, admin full CRUD)
- **Validation:** Unique constraints on names, slugs, and emails
- **Enhanced Search:** Criteria-based filtering and search functionality

---

## Table of Contents

1. [Policy Management](#policy-management)
2. [Contact Management](#contact-management)
3. [Common Response Structure](#common-response-structure)
4. [Database Schema Reference](#database-schema-reference)
5. [Authentication Requirements](#authentication-requirements)
6. [Troubleshooting Guide](#troubleshooting-guide)

---

## POLICY MANAGEMENT

### Overview

Policy table stores system-wide configuration settings used throughout the application. Examples include:
- STORAGE_PATH: Directory for file uploads
- WEBSITE_LOGO_ALT_TEXT: Website branding text  
- HERO_SECTION_TITLE: Homepage hero section title
- FOOTER_COPYRIGHT_TEXT: Footer copyright notice

**Database Schema Changes (v2.0):**
- Added: `POLICY_DISPLAY_NAME` (VARCHAR 256) - User-friendly display name
- Removed: `POLICY_REMARK_EN` - English remark field
- Updated: `POLICY_REMARK` - Now required (NOT NULL)

---

## API Overview

### Policy Management APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/policies/slug/{policySlug}` | GET | PUBLIC | Get policy by URL-friendly slug (public access) |
| `/api/policies` | GET | ADMIN | Search policies with multiple criteria and filtering |
| `/api/policies/{policyId}` | GET | ADMIN | Get specific policy by database ID |
| `/api/policies` | POST | ADMIN | Create new system policy |
| `/api/policies/{policyId}` | PUT | ADMIN | Update existing policy |
| `/api/policies/{policyId}` | DELETE | ADMIN | Delete policy (with validation) |
| `/api/policies/{policyId}/toggle-status` | PATCH | ADMIN | Toggle policy enabled/disabled status |

### Contact Management APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/contacts/slug/{contactSlug}` | GET | PUBLIC | Get contact by URL-friendly slug (public access) |
| `/api/contacts` | GET | ADMIN | Search contacts with filtering options |
| `/api/contacts/{contactId}` | GET | ADMIN | Get specific contact by ID |
| `/api/contacts` | POST | ADMIN | Create new contact information |
| `/api/contacts/{contactId}` | PUT | ADMIN | Update contact information |
| `/api/contacts/{contactId}` | DELETE | ADMIN | Delete contact |
| `/api/contacts/{contactId}/toggle-status` | PATCH | ADMIN | Toggle contact enabled status |

### Key Features

- **Public Configuration Access:** Essential settings available without authentication
- **Comprehensive Search:** Advanced filtering with text search across multiple fields
- **Input Validation:** Unique constraints on names, slugs, emails, and phone numbers
- **Status Management:** Enable/disable toggle for policies and contacts
- **Admin Controls:** Full CRUD operations with proper access control
- **SEO-Friendly:** URL slug support for public-facing configuration data
- **Data Integrity:** Validation rules and database constraints ensure data quality

---

### API Endpoints - Policy

#### 1. Get Policy by Slug (Public)
**Endpoint:** `GET /api/policies/slug/{policySlug}`  
**Method:** GET  
**Access:** PUBLIC  
**Description:** Retrieve policy by URL-friendly slug (public access)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `policySlug` | String | Yes | Path | URL-friendly policy identifier |

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `policyId` | Long | Unique policy identifier |
| `policyName` | String | Unique policy name (configuration key) |
| `policySlug` | String | URL-friendly policy identifier |
| `policyValue` | String | Configuration value (path, text, etc.) |
| `policyDisplayName` | String | User-friendly display name |
| `policyRemark` | String | Policy description/remark (required) |
| `policyEnabled` | Boolean | Whether policy is active/enabled |
| `createdDt` | String | Policy creation timestamp |
| `modifiedDt` | String | Last modification timestamp |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/policies/slug/storage-path" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Policy retrieved successfully",
  "data": {
    "policyId": 1,
    "policyName": "STORAGE_PATH",
    "policySlug": "storage-path", 
    "policyValue": "/storage",
    "policyDisplayName": "Storage Directory Path",
    "policyRemark": "Đường dẫn thư mục lưu trữ file",
    "policyEnabled": true,
    "createdDt": "2026-01-15 10:30:45",
    "modifiedDt": "2026-01-15 10:30:45"
  },
  "timestamp": "2026-02-26 10:35:20"
}
```

---

#### 2. Search Policies with Criteria (Admin)
**Endpoint:** `GET /api/policies`  
**Method:** GET  
**Access:** ADMIN  
**Description:** Filter and search policies by multiple criteria. Returns all policies if no filters applied.

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `policyName` | String | No | Filter by exact policy name (e.g., "STORAGE_PATH") |
| `policyEnabled` | Boolean | No | Filter by enabled status (true/false) |
| `textSearch` | String | No | Search text in name, slug, value, display name, remark |

**Response Type:** Array of PolicyDto

**Example Request:**
```bash
# Get all policies
curl -X GET "http://localhost:8080/policies" \
  -H "Authorization: Bearer {jwt_token}"

# Search by text
curl -X GET "http://localhost:8080/policies?textSearch=storage" \
  -H "Authorization: Bearer {jwt_token}"

# Filter by enabled status
curl -X GET "http://localhost:8080/policies?policyEnabled=true" \
  -H "Authorization: Bearer {jwt_token}"

# Combined filters
curl -X GET "http://localhost:8080/policies?policyEnabled=true&textSearch=footer" \
  -H "Authorization: Bearer {jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Found 3 matching policy(ies)",
  "data": [
    {
      "policyId": 1,
      "policyName": "STORAGE_PATH",
      "policySlug": "storage-path",
      "policyValue": "/storage",
      "policyDisplayName": "Storage Directory Path", 
      "policyRemark": "Đường dẫn thư mục lưu trữ file",
      "policyEnabled": true,
      "createdDt": "2026-01-15 10:30:45",
      "modifiedDt": "2026-01-15 10:30:45"
    },
    {
      "policyId": 2,
      "policyName": "FAVICON",
      "policySlug": "favicon",
      "policyValue": "favicon.ico",
      "policyDisplayName": "Website Favicon",
      "policyRemark": "Tên file favicon website",
      "policyEnabled": true,
      "createdDt": "2026-01-15 10:30:45",
      "modifiedDt": "2026-01-15 10:30:45"
    }
  ],
  "timestamp": "2026-02-26 10:35:20"
}
```

---

#### 3. Get Policy by ID (Admin)
**Endpoint:** `GET /api/policies/{policyId}`  
**Method:** GET  
**Access:** ADMIN  
**Description:** Retrieve policy by database ID (admin management)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `policyId` | Long | Yes | Path | Policy database ID |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/policies/1" \
  -H "Authorization: Bearer {jwt_token}"
```

**Response:** Same structure as Get Policy by Slug

---

#### 4. Create Policy (Admin)
**Endpoint:** `POST /api/policies`  
**Method:** POST  
**Access:** ADMIN  
**Description:** Create new system policy

**Request Body (PolicyDto):**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `policyName` | String | Yes | Unique policy name (max 64 chars) |
| `policySlug` | String | No | URL slug (auto-generated if not provided) |
| `policyValue` | String | Yes | Policy configuration value |
| `policyDisplayName` | String | No | User-friendly display name (max 256 chars) |
| `policyRemark` | String | Yes | Policy description (max 256 chars) |
| `policyEnabled` | Boolean | No | Enable status (defaults to true) |

**Example Request:**
```bash
curl -X POST "http://localhost:8080/policies" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}" \
  -d '{
    "policyName": "CONTACT_EMAIL", 
    "policyValue": "contact@artstore.com",
    "policyDisplayName": "Contact Email Address",
    "policyRemark": "Email liên hệ chính của cửa hàng",
    "policyEnabled": true
  }'
```

**Example Response (Success - 201):**
```json
{
  "code": 201,
  "message": "Policy created successfully",
  "data": {
    "policyId": 17,
    "policyName": "CONTACT_EMAIL",
    "policySlug": "contact-email",
    "policyValue": "contact@artstore.com", 
    "policyDisplayName": "Contact Email Address",
    "policyRemark": "Email liên hệ chính của cửa hàng",
    "policyEnabled": true,
    "createdDt": "2026-02-26 10:35:20",
    "modifiedDt": "2026-02-26 10:35:20"
  },
  "timestamp": "2026-02-26 10:35:20"
}
```

---

#### 5. Update Policy (Admin)
**Endpoint:** `PUT /api/policies/{policyId}`  
**Method:** PUT  
**Access:** ADMIN  
**Description:** Update existing policy

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `policyId` | Long | Yes | Path | Policy ID to update |

**Request Body:** Same as Create Policy

**Example Request:**
```bash
curl -X PUT "http://localhost:8080/policies/17" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}" \
  -d '{
    "policyName": "CONTACT_EMAIL",
    "policySlug": "contact-email", 
    "policyValue": "info@artstore.com",
    "policyDisplayName": "Contact Email Address",
    "policyRemark": "Email liên hệ chính của cửa hàng - đã cập nhật",
    "policyEnabled": true
  }'
```

---

#### 6. Update Policy Status (Admin)
**Endpoint:** `PATCH /api/policies/{policyId}/status`  
**Method:** PATCH  
**Access:** ADMIN  
**Description:** Enable or disable policy

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `policyId` | Long | Yes | Path | Policy ID to update |
| `enabled` | Boolean | Yes | Query | New enabled status |

**Example Request:**
```bash
curl -X PATCH "http://localhost:8080/policies/17/status?enabled=false" \
  -H "Authorization: Bearer {jwt_token}"
```

---

#### 7. Update Policy Value (Admin)
**Endpoint:** `PATCH /api/policies/{policyId}/value`  
**Method:** PATCH  
**Access:** ADMIN  
**Description:** Update only the value of a policy (quick config change)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `policyId` | Long | Yes | Path | Policy ID to update |
| `value` | String | Yes | Query | New policy value |

**Example Request:**
```bash
curl -X PATCH "http://localhost:8080/policies/17/value?value=support@artstore.com" \
  -H "Authorization: Bearer {jwt_token}"
```

---

#### 8. Get Total Policy Count (Admin)
**Endpoint:** `GET /api/policies/admin/total-count`  
**Method:** GET  
**Access:** ADMIN  
**Description:** Get total number of policies for dashboard statistics

**Example Request:**
```bash
curl -X GET "http://localhost:8080/policies/admin/total-count" \
  -H "Authorization: Bearer {jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Total count retrieved successfully",
  "data": 16,
  "timestamp": "2026-02-26 10:35:20"
}
```

---

#### 9. Get All Policy Names (Admin)
**Endpoint:** `GET /api/policies/admin/names`  
**Method:** GET  
**Access:** ADMIN  
**Description:** Get list of all policy names for dropdown/combobox UI elements

**Example Request:**
```bash
curl -X GET "http://localhost:8080/policies/admin/names" \
  -H "Authorization: Bearer {jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Retrieved 16 policy names",
  "data": [
    "CONTACT_EMAIL",
    "FAVICON", 
    "FOOTER_COPYRIGHT_TEXT",
    "HERO_SECTION_TITLE",
    "STORAGE_PATH",
    "WEBSITE_LOGO_ALT_TEXT"
  ],
  "timestamp": "2026-02-26 10:35:20"
}
```

---

## CONTACT MANAGEMENT

### Overview

Contact table stores business contact information including addresses, phone numbers, emails, and social media links. Used for:
- Multiple business locations/stores  
- Customer service contact points
- Regional office information
- Partner/vendor contact details

**Database Schema Changes (v2.0):**
- Removed: `CONTACT_REMARK_EN` - English remark field  
- Updated: `CONTACT_REMARK` - Now required (NOT NULL)

### API Endpoints - Contact

#### 1. Get Contact by Slug (Public)
**Endpoint:** `GET /api/contacts/slug/{contactSlug}`  
**Method:** GET  
**Access:** PUBLIC  
**Description:** Retrieve contact by URL-friendly slug (customer-facing)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `contactSlug` | String | Yes | Path | URL-friendly contact identifier |

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `contactId` | Long | Unique contact identifier |
| `contactName` | String | Contact/location name |
| `contactSlug` | String | URL-friendly contact identifier |
| `contactAddress` | String | Full contact address |
| `contactEmail` | String | Contact email address |
| `contactPhone` | String | Contact phone number |
| `contactFanpage` | String | Social media/fanpage URL (optional) |
| `contactEnabled` | Boolean | Whether contact is active/visible |
| `contactRemark` | String | Contact description/notes (required) |
| `seoMetaId` | Long | Associated SEO metadata ID |
| `createdDt` | String | Contact creation timestamp |
| `modifiedDt` | String | Last modification timestamp |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/contacts/slug/art-store-hanoi" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Contact retrieved successfully",
  "data": {
    "contactId": 1,
    "contactName": "Art Store Hanoi",
    "contactSlug": "art-store-hanoi",
    "contactAddress": "123 Hoàn Kiếm, Hà Nội, Việt Nam",
    "contactEmail": "hanoi@artstore.com",
    "contactPhone": "+84-24-1234567",
    "contactFanpage": "https://facebook.com/artstore.hanoi",
    "contactEnabled": true,
    "contactRemark": "Cửa hàng chính tại Hà Nội - chuyên tranh và đồ trang trí",
    "seoMetaId": 70,
    "createdDt": "2026-01-15 10:30:45",
    "modifiedDt": "2026-01-15 10:30:45"
  },
  "timestamp": "2026-02-26 10:35:20"
}
```

---

#### 2. Search Contacts with Criteria (Public)
**Endpoint:** `GET /api/contacts`  
**Method:** GET  
**Access:** PUBLIC  
**Description:** Filter and search contacts by multiple criteria. Returns all contacts if no filters applied.

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `contactName` | String | No | Filter by exact contact name |
| `contactEnabled` | Boolean | No | Filter by enabled status (true/false) |
| `textSearch` | String | No | Search text in name, slug, address, email, phone, fanpage, remark |

**Response Type:** Array of ContactDto

**Example Request:**
```bash
# Get all contacts  
curl -X GET "http://localhost:8080/contacts"

# Search by text
curl -X GET "http://localhost:8080/contacts?textSearch=hanoi"

# Filter by enabled status
curl -X GET "http://localhost:8080/contacts?contactEnabled=true"

# Combined filters
curl -X GET "http://localhost:8080/contacts?contactEnabled=true&textSearch=store"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Found 2 matching contact(s)",
  "data": [
    {
      "contactId": 1,
      "contactName": "Art Store Hanoi",
      "contactSlug": "art-store-hanoi", 
      "contactAddress": "123 Hoàn Kiếm, Hà Nội, Việt Nam",
      "contactEmail": "hanoi@artstore.com",
      "contactPhone": "+84-24-1234567",
      "contactFanpage": "https://facebook.com/artstore.hanoi",
      "contactEnabled": true,
      "contactRemark": "Cửa hàng chính tại Hà Nội - chuyên tranh và đồ trang trí",
      "seoMetaId": 70,
      "createdDt": "2026-01-15 10:30:45",
      "modifiedDt": "2026-01-15 10:30:45"
    },
    {
      "contactId": 2,
      "contactName": "Art Store Ho Chi Minh",
      "contactSlug": "art-store-hcm",
      "contactAddress": "456 Nguyễn Huệ, Quận 1, TP.HCM, Việt Nam", 
      "contactEmail": "hcm@artstore.com",
      "contactPhone": "+84-28-7654321",
      "contactFanpage": "https://facebook.com/artstore.hcm",
      "contactEnabled": true,
      "contactRemark": "Chi nhánh TP.HCM - gallery tranh nghệ thuật",
      "seoMetaId": 71,
      "createdDt": "2026-01-15 10:30:45",
      "modifiedDt": "2026-01-15 10:30:45"
    }
  ],
  "timestamp": "2026-02-26 10:35:20"
}
```

---

#### 3. Get Contact by ID (Admin)
**Endpoint:** `GET /api/contacts/{contactId}`  
**Method:** GET  
**Access:** ADMIN  
**Description:** Retrieve contact by database ID (admin management)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `contactId` | Long | Yes | Path | Contact database ID |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/contacts/1" \
  -H "Authorization: Bearer {jwt_token}"
```

**Response:** Same structure as Get Contact by Slug

---

#### 4. Create Contact (Admin)
**Endpoint:** `POST /api/contacts`  
**Method:** POST  
**Access:** ADMIN  
**Description:** Create new contact information

**Request Body (ContactDto):**

| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| `contactName` | String | Yes | Max 64 chars | Contact/location name |
| `contactSlug` | String | No | Max 64 chars | URL slug (auto-generated if not provided) |
| `contactAddress` | String | Yes | Max 256 chars | Full contact address |
| `contactEmail` | String | Yes | Valid email, Max 64 chars | Contact email address |
| `contactPhone` | String | Yes | Vietnamese phone format, Max 15 chars | Contact phone number |
| `contactFanpage` | String | No | Max 256 chars | Social media/fanpage URL |
| `contactRemark` | String | Yes | Max 256 chars | Contact description/notes |
| `contactEnabled` | Boolean | No | - | Enable status (defaults to true) |
| `seoMetaId` | Long | No | - | Associated SEO metadata ID |

**Example Request:**
```bash
curl -X POST "http://localhost:8080/contacts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}" \
  -d '{
    "contactName": "Art Store Da Nang",
    "contactAddress": "789 Bạch Đằng, Hải Châu, Đà Nẵng, Việt Nam",
    "contactEmail": "danang@artstore.com", 
    "contactPhone": "+84-236-8888999",
    "contactFanpage": "https://facebook.com/artstore.danang",
    "contactRemark": "Chi nhánh Đà Nẵng - tranh phong cảnh biển",
    "contactEnabled": true
  }'
```

**Example Response (Success - 201):**
```json
{
  "code": 201,
  "message": "Contact created successfully",
  "data": {
    "contactId": 3,
    "contactName": "Art Store Da Nang",
    "contactSlug": "art-store-da-nang",
    "contactAddress": "789 Bạch Đằng, Hải Châu, Đà Nẵng, Việt Nam",
    "contactEmail": "danang@artstore.com",
    "contactPhone": "+84-236-8888999", 
    "contactFanpage": "https://facebook.com/artstore.danang",
    "contactEnabled": true,
    "contactRemark": "Chi nhánh Đà Nẵng - tranh phong cảnh biển",
    "seoMetaId": null,
    "createdDt": "2026-02-26 10:35:20",
    "modifiedDt": "2026-02-26 10:35:20"
  },
  "timestamp": "2026-02-26 10:35:20"
}
```

---

#### 5. Update Contact (Admin)
**Endpoint:** `PUT /api/contacts/{contactId}`  
**Method:** PUT  
**Access:** ADMIN  
**Description:** Update existing contact information

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `contactId` | Long | Yes | Path | Contact ID to update |

**Request Body:** Same as Create Contact

**Example Request:**
```bash
curl -X PUT "http://localhost:8080/contacts/3" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}" \
  -d '{
    "contactName": "Art Store Da Nang",
    "contactSlug": "art-store-da-nang",
    "contactAddress": "789 Bạch Đằng, Hải Châu, Đà Nẵng, Việt Nam - Tầng 2",
    "contactEmail": "danang@artstore.com",
    "contactPhone": "+84-236-8888999",
    "contactFanpage": "https://facebook.com/artstore.danang", 
    "contactRemark": "Chi nhánh Đà Nẵng - tranh phong cảnh biển - đã mở rộng",
    "contactEnabled": true
  }'
```

---

#### 6. Update Contact Status (Admin)
**Endpoint:** `PATCH /api/contacts/{contactId}/status`  
**Method:** PATCH  
**Access:** ADMIN  
**Description:** Enable or disable contact

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `contactId` | Long | Yes | Path | Contact ID to update |
| `enabled` | Boolean | Yes | Query | New enabled status |

**Example Request:**
```bash
curl -X PATCH "http://localhost:8080/contacts/3/status?enabled=false" \
  -H "Authorization: Bearer {jwt_token}"
```

---

#### 7. Delete Contact (Admin)
**Endpoint:** `DELETE /api/contacts/{contactId}`  
**Method:** DELETE  
**Access:** ADMIN  
**Description:** Permanently delete contact (cannot be undone)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `contactId` | Long | Yes | Path | Contact ID to delete |

**Example Request:**
```bash
curl -X DELETE "http://localhost:8080/contacts/3" \
  -H "Authorization: Bearer {jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Contact deleted successfully",
  "data": null,
  "timestamp": "2026-02-26 10:35:20"
}
```

---

#### 8. Get Total Contact Count (Admin)
**Endpoint:** `GET /api/contacts/admin/total-count`  
**Method:** GET  
**Access:** ADMIN  
**Description:** Get total number of contacts for dashboard statistics

**Example Request:**
```bash
curl -X GET "http://localhost:8080/contacts/admin/total-count" \
  -H "Authorization: Bearer {jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Total count retrieved successfully", 
  "data": 3,
  "timestamp": "2026-02-26 10:35:20"
}
```

---

#### 9. Get All Contact Names (Admin)
**Endpoint:** `GET /api/contacts/admin/names`  
**Method:** GET  
**Access:** ADMIN  
**Description:** Get list of all contact names for dropdown/combobox UI elements

**Example Request:**
```bash
curl -X GET "http://localhost:8080/contacts/admin/names" \
  -H "Authorization: Bearer {jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Retrieved 3 contact names",
  "data": [
    "Art Store Da Nang",
    "Art Store Hanoi", 
    "Art Store Ho Chi Minh"
  ],
  "timestamp": "2026-02-26 10:35:20"
}
```

---

## COMMON RESPONSE STRUCTURE

All APIs follow the consistent response format:

### Success Response Structure

```json
{
  "code": 200,
  "message": "Operation successful message",
  "data": "Response data (object, array, or primitive)",
  "timestamp": "2026-02-26 10:35:20"
}
```

### Error Response Structure

```json
{
  "code": 400,
  "message": "Error description",
  "data": null,
  "timestamp": "2026-02-26 10:35:20"
}
```

### Common HTTP Status Codes

| Code | Description | Usage |
|------|-------------|-------|
| 200 | OK | Successful GET, PUT, PATCH operations |
| 201 | Created | Successful POST operations |
| 400 | Bad Request | Validation errors, invalid request data |
| 401 | Unauthorized | Missing or invalid authentication token |
| 403 | Forbidden | Insufficient permissions (requires ADMIN role) |
| 404 | Not Found | Resource not found by ID/slug/name |
| 500 | Internal Server Error | Unexpected server errors |

---

## DATABASE SCHEMA REFERENCE

### Policy Table (POLICY)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `POLICY_ID` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| `POLICY_NAME` | VARCHAR(64) | NOT NULL, UNIQUE | Configuration key name |
| `POLICY_SLUG` | VARCHAR(64) | | URL-friendly identifier |
| `POLICY_VALUE` | TEXT | NOT NULL | Configuration value |
| `POLICY_DISPLAY_NAME` | VARCHAR(256) | | User-friendly display name |
| `POLICY_REMARK` | VARCHAR(256) | NOT NULL | Policy description |
| `POLICY_ENABLED` | BOOLEAN | NOT NULL, DEFAULT TRUE | Active status |
| `CREATED_DT` | DATETIME | NOT NULL, AUTO_TIMESTAMP | Creation time |
| `MODIFIED_DT` | DATETIME | NOT NULL, AUTO_UPDATE | Last modification time |

### Contact Table (CONTACT)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `CONTACT_ID` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| `CONTACT_NAME` | VARCHAR(64) | NOT NULL, UNIQUE | Contact/location name |
| `CONTACT_SLUG` | VARCHAR(64) | NOT NULL, UNIQUE | URL-friendly identifier |
| `CONTACT_ADDRESS` | VARCHAR(256) | NOT NULL | Full address |
| `CONTACT_EMAIL` | VARCHAR(64) | NOT NULL | Email address |
| `CONTACT_PHONE` | VARCHAR(15) | NOT NULL | Phone number |
| `CONTACT_FANPAGE` | VARCHAR(256) | | Social media URL |
| `CONTACT_ENABLED` | BOOLEAN | NOT NULL, DEFAULT TRUE | Active status |
| `CONTACT_REMARK` | VARCHAR(256) | NOT NULL | Contact description |
| `SEO_META_ID` | BIGINT | FOREIGN KEY | SEO metadata reference |
| `CREATED_DT` | DATETIME | NOT NULL, AUTO_TIMESTAMP | Creation time |
| `MODIFIED_DT` | DATETIME | NOT NULL, AUTO_UPDATE | Last modification time |

---

## AUTHENTICATION REQUIREMENTS

### JWT Token Authentication

Most endpoints require Bearer token authentication:

```bash
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Public Endpoints (No Authentication Required)

- `GET /api/contacts/slug/{contactSlug}` - Public contact access
- `GET /api/contacts` - Public contact search
- `GET /api/policies/slug/{policySlug}` - Public policy access

### Admin Endpoints (ADMIN Role Required) 

- All `POST`, `PUT`, `PATCH`, `DELETE` operations
- All `/admin/*` endpoints  
- `GET /api/contacts/{contactId}` - Admin contact access
- `GET /api/policies` - Admin policy search
- `GET /api/policies/{policyId}` - Admin policy access

---

## TROUBLESHOOTING GUIDE

### Common Issues

#### 1. 401 Unauthorized Error

**Problem:** Missing or invalid JWT token
**Solution:** 
- Ensure Authorization header is included
- Check token expiration
- Verify token format: `Bearer {token}`

#### 2. 403 Forbidden Error

**Problem:** Insufficient role permissions
**Solution:** 
- Verify user has ADMIN role for admin endpoints
- Check endpoint access requirements

#### 3. 404 Not Found Error

**Problem:** Resource not found by ID/slug
**Solution:** 
- Verify the ID/slug exists in database
- Check for typos in URL parameters
- Ensure resource is not soft-deleted/disabled

#### 4. 400 Bad Request - Validation Errors

**Common validation issues:**

| Field | Issue | Solution |
|-------|-------|----------|
| `contactEmail` | Invalid email format | Use valid email format |
| `contactPhone` | Invalid Vietnamese phone | Use format: `+84-xx-xxxxxxx` or `0xxxxxxxxx` |
| `policyName` | Name already exists | Use unique policy name |
| `contactName` | Name already exists | Use unique contact name |
| Required fields | Missing required data | Include all required fields |

#### 5. Auto-Generated Slugs

Both Contact and Policy entities auto-generate slugs if not provided:
- Converts name to lowercase
- Replaces spaces with hyphens  
- Removes special characters
- Example: "Art Store Hanoi" → "art-store-hanoi"

### Performance Considerations

1. **Search Performance:**
   - Use specific filters to reduce result sets
   - Text search performs case-insensitive LIKE queries
   - Consider pagination for large datasets

2. **Caching:**
   - Public endpoints may be cached
   - Admin endpoints provide real-time data

3. **Rate Limiting:**
   - Public endpoints may have rate limits
   - Admin endpoints have higher limits

---

## Version History

### Version 2.0 (February 26, 2026)
- **BREAKING CHANGES:**
  - Removed deprecated endpoints: `/api/policies/name/{name}`, `/api/policies/public`, `/api/policies/admin/search`, `/api/contacts/public`, `/api/contacts/search`
  - Removed database fields: `POLICY_REMARK_EN`, `CONTACT_REMARK_EN`
  - Made `POLICY_REMARK` and `CONTACT_REMARK` required (NOT NULL)
  - Added `POLICY_DISPLAY_NAME` field

- **NEW FEATURES:**
  - Enhanced search with `GET /api/policies` and `GET /api/contacts` with multiple criteria
  - Added `/admin/names` endpoints for dropdown/combobox data
  - Comprehensive OpenAPI documentation
  - Better security configuration

- **IMPROVEMENTS:**
  - More flexible search capabilities
  - Consistent API patterns
  - Enhanced validation and error handling
  - Updated authentication requirements

### Version 1.0 (February 3, 2026)
- Initial Configuration Management API
- Basic CRUD operations for Policy and Contact
- Role-based access control
- Slug-based public access

---

**For technical support or questions, contact the development team.**
