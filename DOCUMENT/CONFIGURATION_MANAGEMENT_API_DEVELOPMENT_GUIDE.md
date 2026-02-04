# CONFIGURATION MANAGEMENT API DEVELOPMENT GUIDE

**Project:** Art & Decor E-commerce Platform  
**Date:** February 3, 2026  
**Author:** Development Team  
**Version:** 1.0  
**Features:** Complete Configuration Management APIs for CONTACT and POLICY tables  

---

## Overview

The Configuration Management API provides complete functionality for managing system configuration (Policy) and contact information. These APIs support:

- **Policy Management:** System-wide configuration settings (storage paths, website text, UI settings)
- **Contact Management:** Business contact information and location details
- **Access Control:** Role-based access (public read, admin full CRUD)
- **Validation:** Unique constraints on names, slugs, and emails

---

## Table of Contents

1. [Policy Management](#policy-management)
2. [Contact Management](#contact-management)
3. [Common Response Structure](#common-response-structure)
4. [Database Schema Reference](#database-schema-reference)
5. [Troubleshooting Guide](#troubleshooting-guide)

---

## POLICY MANAGEMENT

### Overview

Policy table stores system-wide configuration settings used throughout the application. Examples include:
- STORAGE_PATH: Directory for file uploads
- WEBSITE_LOGO_ALT_TEXT: Website branding text
- HERO_SECTION_TITLE: Homepage hero section title
- FOOTER_COPYRIGHT_TEXT: Footer copyright notice

### API Endpoints - Policy

#### 1. Get Policy by Name (Admin)
**Endpoint:** `GET /policies/name/{policyName}`  
**Method:** GET  
**Access:** ADMIN  
**Description:** Retrieve system configuration by policy name (admin management)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `policyName` | String | Yes | Path | Unique policy name (e.g., "STORAGE_PATH", "FOOTER_COPYRIGHT_TEXT") |

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `policyId` | Long | Unique policy identifier |
| `policyName` | String | Unique policy name (configuration key) |
| `policySlug` | String | URL-friendly policy identifier (optional) |
| `policyValue` | String | Configuration value (path, text, etc.) |
| `policyRemarkEn` | String | English description of policy |
| `policyRemark` | String | Vietnamese description of policy |
| `policyEnabled` | Boolean | Whether policy is active/enabled |
| `createdDt` | String | Policy creation timestamp |
| `modifiedDt` | String | Last modification timestamp |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/policies/name/STORAGE_PATH" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}"
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
    "policyRemarkEn": "Storage path",
    "policyRemark": "Đường dẫn lưu trữ",
    "policyEnabled": true,
    "createdDt": "2026-01-15 10:30:45",
    "modifiedDt": "2026-01-15 10:30:45"
  },
  "timestamp": "2026-02-03 10:35:20"
}
```

**Example Response (Not Found - 404):**
```json
{
  "code": 404,
  "message": "Policy not found with name: INVALID_POLICY",
  "data": null,
  "timestamp": "2026-02-03 10:35:20"
}
```

---

#### 2. Get Policy by Slug (Admin)
**Endpoint:** `GET /policies/slug/{policySlug}`  
**Method:** GET  
**Access:** CUSTOMER  
**Description:** Retrieve policy by URL-friendly slug

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `policySlug` | String | Yes | Path | URL-friendly policy identifier |

**Response Fields:** Same as Get Policy by Name

**Example Request:**
```bash
curl -X GET "http://localhost:8080/policies/slug/storage-path" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}"
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
    "policyRemarkEn": "Storage path",
    "policyRemark": "Đường dẫn lưu trữ",
    "policyEnabled": true,
    "createdDt": "2026-01-15 10:30:45",
    "modifiedDt": "2026-01-15 10:30:45"
  },
  "timestamp": "2026-02-03 10:35:20"
}
```

---

#### 3. Get All Enabled Policies (Admin)
**Endpoint:** `GET /policies/public`  
**Method:** GET  
**Access:** ADMIN  
**Description:** Retrieve all active/enabled system policies

**Response Type:** Array of PolicyDto

**Example Request:**
```bash
curl -X GET "http://localhost:8080/policies/public" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Retrieved 16 enabled policies",
  "data": [
    {
      "policyId": 1,
      "policyName": "FAVICON",
      "policySlug": "favicon",
      "policyValue": "favicon.ico",
      "policyRemarkEn": "Favicon file name",
      "policyRemark": "Tên file favicon",
      "policyEnabled": true,
      "createdDt": "2026-01-15 10:30:45",
      "modifiedDt": "2026-01-15 10:30:45"
    },
    {
      "policyId": 2,
      "policyName": "STORAGE_PATH",
      "policySlug": "storage-path",
      "policyValue": "/storage",
      "policyRemarkEn": "Storage path",
      "policyRemark": "Đường dẫn lưu trữ",
      "policyEnabled": true,
      "createdDt": "2026-01-15 10:30:45",
      "modifiedDt": "2026-01-15 10:30:45"
    }
  ],
  "timestamp": "2026-02-03 10:35:20"
}
```

---

#### 4. Get Policy by ID (Admin)
**Endpoint:** `GET /policies/{policyId}`  
**Method:** GET  
**Access:** ADMIN  
**Description:** Retrieve policy by ID (admin/system reference)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `policyId` | Long | Yes | Path | Unique policy identifier in database |

**Response Fields:** Same as Get Policy by Name

**Example Request:**
```bash
curl -X GET "http://localhost:8080/policies/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Policy retrieved successfully",
  "data": {
    "policyId": 1,
    "policyName": "FAVICON",
    "policySlug": "favicon",
    "policyValue": "favicon.ico",
    "policyRemarkEn": "Favicon file name",
    "policyRemark": "Tên file favicon",
    "policyEnabled": true,
    "createdDt": "2026-01-15 10:30:45",
    "modifiedDt": "2026-01-15 10:30:45"
  },
  "timestamp": "2026-02-03 10:35:20"
}
```

---

#### 5. Search Policies by Name (Admin)
**Endpoint:** `GET /policies/admin/search`  
**Method:** GET  
**Access:** ADMIN  
**Description:** Search policies by name pattern (case-insensitive)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `q` | String | Yes | Query | Search term (policy name pattern) |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/policies/admin/search?q=STORAGE" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Found 1 matching policy/policies",
  "data": [
    {
      "policyId": 1,
      "policyName": "STORAGE_PATH",
      "policySlug": "storage-path",
      "policyValue": "/storage",
      "policyRemarkEn": "Storage path",
      "policyRemark": "Đường dẫn lưu trữ",
      "policyEnabled": true,
      "createdDt": "2026-01-15 10:30:45",
      "modifiedDt": "2026-01-15 10:30:45"
    }
  ],
  "timestamp": "2026-02-03 10:35:20"
}
```

---

#### 6. Create Policy (Admin)
**Endpoint:** `POST /policies`  
**Method:** POST  
**Access:** ADMIN only  
**Description:** Create new system policy configuration

**Request Body (JSON):**

| Field | Type | Required | Max Length | Description |
|-------|------|----------|------------|-------------|
| `policyName` | String | Yes | 64 | Unique policy name (configuration key) |
| `policySlug` | String | No | 64 | URL-friendly identifier (optional) |
| `policyValue` | String | Yes | - | Configuration value (path, text, number, etc.) |
| `policyRemarkEn` | String | No | 256 | English description |
| `policyRemark` | String | Yes | 256 | Vietnamese description |
| `policyEnabled` | Boolean | No | - | Default: true |

**Validation Rules:**

| Rule | Condition | Error |
|------|-----------|-------|
| **Name Required** | policyName must not be blank | 400 Bad Request |
| **Name Unique** | policyName must be unique in database | 400 Bad Request |
| **Value Required** | policyValue must not be blank | 400 Bad Request |
| **Slug Unique** | If provided, policySlug must be unique | 400 Bad Request |
| **Remark Required** | policyRemark (Vietnamese) must not be blank | 400 Bad Request |

**Example Request:**
```bash
curl -X POST "http://localhost:8080/policies" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {admin_jwt_token}" \
  -d '{
    "policyName": "NEW_SETTING",
    "policySlug": "new-setting",
    "policyValue": "some_configuration_value",
    "policyRemarkEn": "New system setting",
    "policyRemark": "Cài đặt hệ thống mới",
    "policyEnabled": true
  }'
```

**Example Response (Success - 201):**
```json
{
  "code": 201,
  "message": "Policy created successfully",
  "data": {
    "policyId": 21,
    "policyName": "NEW_SETTING",
    "policySlug": "new-setting",
    "policyValue": "some_configuration_value",
    "policyRemarkEn": "New system setting",
    "policyRemark": "Cài đặt hệ thống mới",
    "policyEnabled": true,
    "createdDt": "2026-02-03 10:45:30",
    "modifiedDt": "2026-02-03 10:45:30"
  },
  "timestamp": "2026-02-03 10:45:35"
}
```

**Example Response (Duplicate Name - 400):**
```json
{
  "code": 400,
  "message": "Validation error: Policy name already exists: STORAGE_PATH",
  "data": null,
  "timestamp": "2026-02-03 10:45:35"
}
```

---

#### 7. Update Policy (Admin)
**Endpoint:** `PUT /policies/{policyId}`  
**Method:** PUT  
**Access:** ADMIN only  
**Description:** Update policy configuration (full update)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `policyId` | Long | Yes | Path | Policy ID to update |

**Request Body:** Same as Create Policy (all fields optional but validated)

**Example Request:**
```bash
curl -X PUT "http://localhost:8080/policies/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {admin_jwt_token}" \
  -d '{
    "policyName": "STORAGE_PATH",
    "policySlug": "storage-path",
    "policyValue": "/uploads/storage",
    "policyRemarkEn": "Updated storage path",
    "policyRemark": "Đường dẫn lưu trữ đã cập nhật",
    "policyEnabled": true
  }'
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Policy updated successfully",
  "data": {
    "policyId": 1,
    "policyName": "STORAGE_PATH",
    "policySlug": "storage-path",
    "policyValue": "/uploads/storage",
    "policyRemarkEn": "Updated storage path",
    "policyRemark": "Đường dẫn lưu trữ đã cập nhật",
    "policyEnabled": true,
    "createdDt": "2026-01-15 10:30:45",
    "modifiedDt": "2026-02-03 11:00:20"
  },
  "timestamp": "2026-02-03 11:00:25"
}
```

---

#### 8. Update Policy Status (Admin)
**Endpoint:** `PATCH /policies/{policyId}/status`  
**Method:** PATCH  
**Access:** ADMIN  
**Description:** Enable or disable policy (quick status update)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `policyId` | Long | Yes | Path | Policy ID to update |
| `enabled` | Boolean | Yes | Query | New enabled status (true/false) |

**Example Request:**
```bash
curl -X PATCH "http://localhost:8080/policies/20/status?enabled=true" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Policy status updated successfully",
  "data": {
    "policyId": 20,
    "policyName": "ARCHIVED_SETTING_01",
    "policySlug": null,
    "policyValue": "deprecated_value",
    "policyRemarkEn": "Old configuration",
    "policyRemark": "Cấu hình cũ",
    "policyEnabled": true,
    "createdDt": "2026-01-15 10:30:45",
    "modifiedDt": "2026-02-03 11:05:10"
  },
  "timestamp": "2026-02-03 11:05:15"
}
```

---

#### 9. Update Policy Value Only (Admin)
**Endpoint:** `PATCH /policies/{policyId}/value`  
**Method:** PATCH  
**Access:** ADMIN  
**Description:** Quick update of configuration value only (common operation)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `policyId` | Long | Yes | Path | Policy ID to update |
| `value` | String | Yes | Query | New configuration value |

**Example Request:**
```bash
curl -X PATCH "http://localhost:8080/policies/1/value?value=/new/storage/path" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Policy value updated successfully",
  "data": {
    "policyId": 1,
    "policyName": "STORAGE_PATH",
    "policySlug": "storage-path",
    "policyValue": "/new/storage/path",
    "policyRemarkEn": "Storage path",
    "policyRemark": "Đường dẫn lưu trữ",
    "policyEnabled": true,
    "createdDt": "2026-01-15 10:30:45",
    "modifiedDt": "2026-02-03 11:10:05"
  },
  "timestamp": "2026-02-03 11:10:10"
}
```

---

#### 10. Delete Policy (Admin)
**Endpoint:** `DELETE /policies/{policyId}`  
**Method:** DELETE  
**Access:** ADMIN only  
**Description:** Delete system policy (permanent action)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `policyId` | Long | Yes | Path | Policy ID to delete |

**Example Request:**
```bash
curl -X DELETE "http://localhost:8080/policies/21" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {admin_jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Policy deleted successfully",
  "data": null,
  "timestamp": "2026-02-03 11:15:30"
}
```

---

#### 11. Get Total Policy Count (Admin Dashboard)
**Endpoint:** `GET /policies/admin/total-count`  
**Method:** GET  
**Access:** ADMIN, MANAGER  
**Description:** Get total count of policies in system

**Example Request:**
```bash
curl -X GET "http://localhost:8080/policies/admin/total-count" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Total count retrieved successfully",
  "data": 16,
  "timestamp": "2026-02-03 11:20:15"
}
```

---

## CONTACT MANAGEMENT

### Overview

Contact table stores business contact information for the organization. Used for:
- Customer inquiries and support
- Business location information
- Multiple office/branch locations
- Contact form submissions

### API Endpoints - Contact

#### 1. Get Contact by Slug (Public)
**Endpoint:** `GET /contacts/slug/{contactSlug}`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**Description:** Retrieve contact by URL-friendly slug (customer-friendly lookup)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `contactSlug` | String | Yes | Path | URL-friendly contact identifier (e.g., "ho-chi-minh-office") |

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `contactId` | Long | Unique contact identifier |
| `contactName` | String | Contact name (e.g., "Ho Chi Minh Office") |
| `contactSlug` | String | URL-friendly identifier |
| `contactAddress` | String | Full contact address |
| `contactEmail` | String | Contact email address |
| `contactPhone` | String | Contact phone number |
| `contactFanpage` | String | Facebook/social media page URL (optional) |
| `contactEnabled` | Boolean | Whether contact is active/published |
| `contactRemarkEn` | String | English description/remarks |
| `contactRemark` | String | Vietnamese description/remarks |
| `seoMetaId` | Long | Associated SEO metadata ID |
| `createdDt` | String | Contact creation timestamp |
| `modifiedDt` | String | Last modification timestamp |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/contacts/slug/ho-chi-minh-office" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Contact retrieved successfully",
  "data": {
    "contactId": 1,
    "contactName": "Ho Chi Minh Office",
    "contactSlug": "ho-chi-minh-office",
    "contactAddress": "123 Nguyen Hue Boulevard, District 1, HCMC",
    "contactEmail": "hcm@artanddecor.vn",
    "contactPhone": "02838123456",
    "contactFanpage": "https://facebook.com/artanddecor",
    "contactEnabled": true,
    "contactRemarkEn": "Main office location",
    "contactRemark": "Trụ sở chính",
    "seoMetaId": 1,
    "createdDt": "2026-01-15 10:30:45",
    "modifiedDt": "2026-01-15 10:30:45"
  },
  "timestamp": "2026-02-03 10:35:20"
}
```

---

#### 2. Get All Enabled Contacts (Public)
**Endpoint:** `GET /contacts/public`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**Description:** Get all active contact locations (customer view)

**Response Type:** Array of ContactDto

**Example Request:**
```bash
curl -X GET "http://localhost:8080/contacts/public" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Retrieved 2 enabled contacts",
  "data": [
    {
      "contactId": 1,
      "contactName": "Ho Chi Minh Office",
      "contactSlug": "ho-chi-minh-office",
      "contactAddress": "123 Nguyen Hue Boulevard, District 1, HCMC",
      "contactEmail": "hcm@artanddecor.vn",
      "contactPhone": "02838123456",
      "contactFanpage": "https://facebook.com/artanddecor",
      "contactEnabled": true,
      "contactRemarkEn": "Main office location",
      "contactRemark": "Trụ sở chính",
      "seoMetaId": 1,
      "createdDt": "2026-01-15 10:30:45",
      "modifiedDt": "2026-01-15 10:30:45"
    },
    {
      "contactId": 2,
      "contactName": "Hanoi Office",
      "contactSlug": "hanoi-office",
      "contactAddress": "456 Tran Hung Dao Street, Hoan Kiem District, Hanoi",
      "contactEmail": "hanoi@artanddecor.vn",
      "contactPhone": "02439876543",
      "contactFanpage": "https://facebook.com/artanddecor",
      "contactEnabled": true,
      "contactRemarkEn": "Hanoi branch office",
      "contactRemark": "Chi nhánh Hà Nội",
      "seoMetaId": 2,
      "createdDt": "2026-01-16 09:15:20",
      "modifiedDt": "2026-01-16 09:15:20"
    }
  ],
  "timestamp": "2026-02-03 10:35:20"
}
```

---

#### 3. Search Contacts (Public)
**Endpoint:** `GET /contacts/search`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**Description:** Search contacts by name (minimum 2 characters)

**Request Parameters:**

| Parameter | Type | Required | Location | Min Length | Description |
|-----------|------|----------|----------|-----------|-------------|
| `q` | String | Yes | Query | 2 | Search term for contact name |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/contacts/search?q=chi" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Found 1 matching contact(s)",
  "data": [
    {
      "contactId": 2,
      "contactName": "Ho Chi Minh Office",
      "contactSlug": "ho-chi-minh-office",
      "contactAddress": "123 Nguyen Hue Boulevard, District 1, HCMC",
      "contactEmail": "hcm@artanddecor.vn",
      "contactPhone": "02838123456",
      "contactFanpage": "https://facebook.com/artanddecor",
      "contactEnabled": true,
      "contactRemarkEn": "Main office location",
      "contactRemark": "Trụ sở chính",
      "seoMetaId": 1,
      "createdDt": "2026-01-15 10:30:45",
      "modifiedDt": "2026-01-15 10:30:45"
    }
  ],
  "timestamp": "2026-02-03 10:40:15"
}
```

---

#### 4. Get Contact by ID (Admin)
**Endpoint:** `GET /contacts/{contactId}`  
**Method:** GET  
**Access:** ADMIN  
**Description:** Retrieve contact by ID (admin/system reference)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `contactId` | Long | Yes | Path | Unique contact identifier in database |

**Response Fields:** Same as Get Contact by Slug

**Example Request:**
```bash
curl -X GET "http://localhost:8080/contacts/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}"
```
---

#### 5. Create Contact (Admin)
**Endpoint:** `POST /contacts`  
**Method:** POST  
**Access:** ADMIN  
**Description:** Create new contact location

**Request Body (JSON):**

| Field | Type | Required | Max Length | Description |
|-------|------|----------|------------|-------------|
| `contactName` | String | Yes | 64 | Contact name (e.g., "Ho Chi Minh Office") |
| `contactSlug` | String | Yes | 64 | URL-friendly identifier (must be unique) |
| `contactAddress` | String | Yes | 256 | Full contact address |
| `contactEmail` | String | Yes | 64 | Email address (must be valid email format) |
| `contactPhone` | String | Yes | 15 | Phone number (Vietnam format: 0XXXXXXXXX or +840XXXXXXXXX) |
| `contactFanpage` | String | No | 256 | Facebook/social media page URL |
| `contactRemarkEn` | String | No | 256 | English description |
| `contactRemark` | String | Yes | 256 | Vietnamese description |
| `contactEnabled` | Boolean | No | - | Default: true |
| `seoMetaId` | Long | No | - | Associated SEO metadata ID |

**Example Request:**
```bash
curl -X POST "http://localhost:8080/contacts" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}" \
  -d '{
    "contactName": "Da Nang Office",
    "contactSlug": "da-nang-office",
    "contactAddress": "789 Bach Dang Street, Hai Chau District, Da Nang",
    "contactEmail": "danang@artanddecor.vn",
    "contactPhone": "02363123456",
    "contactFanpage": "https://facebook.com/artanddecor",
    "contactRemarkEn": "Da Nang branch",
    "contactRemark": "Chi nhánh Đà Nẵng",
    "contactEnabled": true,
    "seoMetaId": 3
  }'
```

**Example Response (Success - 201):**
```json
{
  "code": 201,
  "message": "Contact created successfully",
  "data": {
    "contactId": 3,
    "contactName": "Da Nang Office",
    "contactSlug": "da-nang-office",
    "contactAddress": "789 Bach Dang Street, Hai Chau District, Da Nang",
    "contactEmail": "danang@artanddecor.vn",
    "contactPhone": "02363123456",
    "contactFanpage": "https://facebook.com/artanddecor",
    "contactEnabled": true,
    "contactRemarkEn": "Da Nang branch",
    "contactRemark": "Chi nhánh Đà Nẵng",
    "seoMetaId": 3,
    "createdDt": "2026-02-03 11:00:00",
    "modifiedDt": "2026-02-03 11:00:00"
  },
  "timestamp": "2026-02-03 11:00:05"
}
```

---

#### 6. Update Contact (Admin)
**Endpoint:** `PUT /contacts/{contactId}`  
**Method:** PUT  
**Access:** ADMIN  
**Description:** Update contact information (full update)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `contactId` | Long | Yes | Path | Contact ID to update |

**Request Body:** Same as Create Contact (all fields optional)

**Example Request:**
```bash
curl -X PUT "http://localhost:8080/contacts/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}" \
  -d '{
    "contactName": "Ho Chi Minh - Main Office",
    "contactSlug": "ho-chi-minh-office",
    "contactAddress": "123 Nguyen Hue Boulevard, District 1, HCMC",
    "contactEmail": "hcm@artanddecor.vn",
    "contactPhone": "02838123456",
    "contactFanpage": "https://facebook.com/artanddecor",
    "contactRemarkEn": "Main office location - Updated",
    "contactRemark": "Trụ sở chính - Đã cập nhật",
    "contactEnabled": true,
    "seoMetaId": 1
  }'
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Contact updated successfully",
  "data": {
    "contactId": 1,
    "contactName": "Ho Chi Minh - Main Office",
    "contactSlug": "ho-chi-minh-office",
    "contactAddress": "123 Nguyen Hue Boulevard, District 1, HCMC",
    "contactEmail": "hcm@artanddecor.vn",
    "contactPhone": "02838123456",
    "contactFanpage": "https://facebook.com/artanddecor",
    "contactEnabled": true,
    "contactRemarkEn": "Main office location - Updated",
    "contactRemark": "Trụ sở chính - Đã cập nhật",
    "seoMetaId": 1,
    "createdDt": "2026-01-15 10:30:45",
    "modifiedDt": "2026-02-03 11:30:10"
  },
  "timestamp": "2026-02-03 11:30:15"
}
```

---

#### 7. Update Contact Status (Admin)
**Endpoint:** `PATCH /contacts/{contactId}/status`  
**Method:** PATCH  
**Access:** ADMIN  
**Description:** Enable or disable contact (publish/unpublish)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `contactId` | Long | Yes | Path | Contact ID to update |
| `enabled` | Boolean | Yes | Query | New enabled status |

**Example Request:**
```bash
curl -X PATCH "http://localhost:8080/contacts/2/status?enabled=false" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}"
```

---

#### 8. Delete Contact (Admin)
**Endpoint:** `DELETE /contacts/{contactId}`  
**Method:** DELETE  
**Access:** ADMIN only  
**Description:** Delete contact permanently

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `contactId` | Long | Yes | Path | Contact ID to delete |

**Example Request:**
```bash
curl -X DELETE "http://localhost:8080/contacts/3" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {admin_jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Contact deleted successfully",
  "data": null,
  "timestamp": "2026-02-03 11:45:30"
}
```

---

#### 9. Get Total Contact Count (Admin Dashboard)
**Endpoint:** `GET /contacts/admin/total-count`  
**Method:** GET  
**Access:** ADMIN, MANAGER  
**Description:** Get total count of contacts (for dashboard/statistics)

**Example Request:**
```bash
curl -X GET "http://localhost:8080/contacts/admin/total-count" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Total count retrieved successfully",
  "data": 3,
  "timestamp": "2026-02-03 11:50:00"
}
```

---

## Common Response Structure

All API responses use `BaseResponseDto` format:

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `code` | Integer | Yes | HTTP status code (200=success, 201=created, 400=bad request, 404=not found, 500=server error) |
| `message` | String | Yes | Human-readable response message |
| `data` | Object/Array/null | No | Response data (varies by endpoint) |
| `timestamp` | String | Yes | Response timestamp (yyyy-MM-dd HH:mm:ss format) |

**Response Codes:**
- `200` - Success (GET, PUT, PATCH, DELETE)
- `201` - Created (POST)
- `400` - Bad Request (validation error)
- `401` - Unauthorized (authentication required)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found (resource not found)
- `500` - Internal Server Error (database error)

---

## Database Schema Reference

### POLICY Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `POLICY_ID` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique policy identifier |
| `POLICY_NAME` | VARCHAR(64) | UNIQUE, NOT NULL | Policy configuration key |
| `POLICY_SLUG` | VARCHAR(64) | NULL | URL-friendly identifier |
| `POLICY_VALUE` | TEXT | NOT NULL | Configuration value |
| `POLICY_REMARK_EN` | VARCHAR(256) | NULL | English description |
| `POLICY_REMARK` | VARCHAR(256) | NOT NULL | Vietnamese description |
| `POLICY_ENABLED` | BOOLEAN | DEFAULT TRUE | Active status |
| `CREATED_DT` | DATETIME | NOT NULL | Creation timestamp |
| `MODIFIED_DT` | DATETIME | NOT NULL | Last modification timestamp |

**Indexes:**
- `idx_policy_name` on POLICY_NAME
- `idx_policy_slug` on POLICY_SLUG

### CONTACT Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `CONTACT_ID` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique contact identifier |
| `CONTACT_NAME` | VARCHAR(64) | NOT NULL | Contact name |
| `CONTACT_SLUG` | VARCHAR(64) | UNIQUE, NOT NULL | URL-friendly identifier |
| `CONTACT_ADDRESS` | VARCHAR(256) | NOT NULL | Full address |
| `CONTACT_EMAIL` | VARCHAR(64) | NOT NULL | Email address |
| `CONTACT_PHONE` | VARCHAR(15) | NOT NULL | Phone number |
| `CONTACT_FANPAGE` | VARCHAR(256) | NULL | Social media URL |
| `CONTACT_ENABLED` | BOOLEAN | DEFAULT TRUE | Active status |
| `CONTACT_REMARK_EN` | VARCHAR(256) | NULL | English description |
| `CONTACT_REMARK` | VARCHAR(256) | NOT NULL | Vietnamese description |
| `SEO_META_ID` | BIGINT | NULL, FOREIGN KEY | SEO metadata reference |
| `CREATED_DT` | DATETIME | NOT NULL | Creation timestamp |
| `MODIFIED_DT` | DATETIME | NOT NULL | Last modification timestamp |

**Indexes:**
- `idx_contact_email` on CONTACT_EMAIL
- `idx_contact_slug` on CONTACT_SLUG
- `idx_contact_seo_meta` on SEO_META_ID

---

## Troubleshooting Guide

### Common Issues

| Issue | Cause | Solution |
|-------|-------|----------|
| **Policy Not Found** | Policy name doesn't exist in database | Check policy name spelling (case-sensitive) or use admin list endpoint |
| **Duplicate Name Error** | Policy name already exists | Use unique policy name or check existing policies with search |
| **Invalid Email Format** | Email doesn't match pattern | Ensure email format: name@domain.com |
| **Invalid Phone Format** | Phone not in Vietnam format | Use format: 0XXXXXXXXX or +840XXXXXXXXX (10-15 digits) |
| **Slug Already Exists** | Contact/Policy slug is not unique | Generate unique slug or append timestamp |
| **401 Unauthorized** | JWT token missing or invalid | Include Authorization header with valid JWT token |
| **403 Forbidden** | User role insufficient | Use account with ADMIN/MANAGER role as needed |

### Debug Logging

Enable debug logging for troubleshooting:
```properties
logging.level.org.ArtAndDecor.controllers.PolicyController=DEBUG
logging.level.org.ArtAndDecor.controllers.ContactController=DEBUG
logging.level.org.ArtAndDecor.services.impl.PolicyServiceImpl=DEBUG
logging.level.org.ArtAndDecor.services.impl.ContactServiceImpl=DEBUG
```

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-02-03 | Initial release with Policy and Contact management APIs |

---

## Future Enhancements (Planned)

- [ ] Bulk policy import/export functionality
- [ ] Policy value validation rules by type (string, integer, boolean, etc.)
- [ ] Contact location filtering by province/city
- [ ] Contact availability/business hours configuration
- [ ] Policy change audit trail and versioning
- [ ] Batch contact creation from CSV
- [ ] Contact rating/review system
- [ ] Policy A/B testing framework
- [ ] Geolocation-based contact auto-selection for customers
