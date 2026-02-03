# USER MANAGEMENT API DEVELOPMENT GUIDE

**Project:** Art & Decor E-commerce Platform  
**Date:** January 27, 2026  
**Author:** Development Team  
**Version:** 6.0  
**Features:** Complete User Management APIs with JWT Authentication  

---

## User Roles & API Access

| Role ID | Role Name | Description | API Access Level |
|---------|-----------|-------------|------------------|
| 1 | ADMIN | System Administrator | Full access to all APIs |
| 2 | MANAGER | Store Manager | User management (except delete/status) |
| 3 | STAFF | Store Staff | Read-only access to user data |
| 4 | CUSTOMER | Customer | Access to own profile and public APIs only |

## Authentication Requirements

- **Public APIs:** No authentication required
- **User APIs:** JWT Bearer token required
- **Admin APIs:** Admin role verification required
- **Self-access:** Users can access/modify their own data

---

## API Endpoints

### 1. Search Users by Criteria
**Endpoint:** `GET /api/users/search`  
**Method:** GET  
**Access:** ADMIN, MANAGER  
**Description:** Get users filtered by multiple criteria (all parameters optional)

**Request Parameters:**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `userId` | Long | No | - | User ID filter |
| `userProviderId` | Long | No | - | Provider ID (1=LOCAL, 2=GOOGLE, 3=FACEBOOK, 4=GITHUB) |
| `userRoleId` | Long | No | - | Role ID (1=CUSTOMER, 2=ADMIN, 3=MANAGER, 4=STAFF) |
| `userEnabled` | Boolean | No | - | User enabled status |
| `userName` | String | No | - | Username filter (exact match) |

**Example Request:**
```http
GET /api/users/search?userRoleId=4&userEnabled=true
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Example Response:**
```json
{
  "code": 200,
  "message": "Users retrieved successfully",
  "data": [
    {
      "userId": 4,
      "userProviderId": 1,
      "userRoleId": 4,
      "userEnabled": true,
      "userName": "customer01",
      "password": null,
      "firstName": "Nguyen",
      "lastName": "Van A",
      "phoneNumber": "0904567890",
      "emailAddress": "customer1@gmail.com",
      "description": null,
      "userAvatarLink": "D4E5F6789012345678901234567890ABCDEF12",
      "address": null,
      "lastLoginDt": "2026-01-19 16:20:00",
      "createdDt": "2026-01-15 09:00:00",
      "modifiedDt": "2026-01-19 16:20:00",
      "userProviderName": "LOCAL",
      "userProviderRemarkEn": "Local authentication",
      "userProviderRemark": "Xác thực nội bộ",
      "userRoleName": "CUSTOMER",
      "userRoleRemarkEn": "Customer",
      "userRoleRemark": "Khách hàng"
    }
  ],
  "timestamp": "2026-01-26 14:30:00"
}
```

---

### 2. Get User by ID
**Endpoint:** `GET /api/users/{userId}`  
**Method:** GET  
**Access:** ADMIN, MANAGER, STAFF, SELF  
**Description:** Get detailed user information by ID

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `userId` | Long | Yes | Path | User ID to retrieve |

**Example Request:**
```http
GET /api/users/4
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Example Response:**
```json
{
  "code": 200,
  "message": "User retrieved successfully",
  "data": {
    "userId": 4,
    "userProviderId": 1,
    "userRoleId": 4,
    "userEnabled": true,
    "userName": "customer01",
    "password": null,
    "firstName": "Nguyen",
    "lastName": "Van A",
    "phoneNumber": "0904567890",
    "emailAddress": "customer1@gmail.com",
    "description": null,
    "userAvatarLink": "D4E5F6789012345678901234567890ABCDEF12",
    "address": null,
    "lastLoginDt": "2026-01-19 16:20:00",
    "createdDt": "2026-01-15 09:00:00",
    "modifiedDt": "2026-01-19 16:20:00",
    "userProviderName": "LOCAL",
    "userProviderRemarkEn": "Local authentication",
    "userProviderRemark": "Xác thực nội bộ",
    "userRoleName": "CUSTOMER",
    "userRoleRemarkEn": "Customer",
    "userRoleRemark": "Khách hàng"
  },
  "timestamp": "2026-01-26 14:30:00"
}
```

---

### 3. Get All Users (Paginated)
**Endpoint:** `GET /api/users`  
**Method:** GET  
**Access:** ADMIN, MANAGER  
**Description:** Get paginated list of all users

**Request Parameters:**

| Parameter | Type | Required | Default | Location | Description |
|-----------|------|----------|---------|----------|-------------|
| `page` | Integer | No | 0 | Query | Page number (0-based) |
| `size` | Integer | No | 10 | Query | Page size (max 100) |

**Example Request:**
```http
GET /api/users?page=0&size=5
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Example Response:**
```json
{
  "code": 200,
  "message": "Users retrieved successfully",
  "data": [
    {
      "userId": 1,
      "userName": "admin",
      "firstName": "Admin",
      "lastName": "System",
      "emailAddress": "admin@artdecor.com",
      "userRoleName": "ADMIN",
      "userEnabled": true
    },
    {
      "userId": 2,
      "userName": "manager01",
      "firstName": "John",
      "lastName": "Manager",
      "emailAddress": "manager@artdecor.com",
      "userRoleName": "MANAGER",
      "userEnabled": true
    }
  ],
  "timestamp": "2026-01-26 14:30:00"
}
```

---

### 4. Create User
**Endpoint:** `POST /api/users`  
**Method:** POST  
**Access:** ADMIN, MANAGER  
**Description:** Create new user account

**Request Body (JSON):**

| Field | Type | Required | Default | Max Length | Description |
|-------|------|----------|---------|------------|-------------|
| `userProviderId` | Long | No | 1 | - | Provider ID (1=LOCAL) |
| `userRoleId` | Long | No | 4 | - | Role ID (4=CUSTOMER) |
| `userEnabled` | Boolean | No | true | - | User enabled status |
| `userName` | String | Yes | - | 64 | Username (unique) |
| `password` | String | Yes | - | 150 | Password (min 8 chars) |
| `firstName` | String | Yes | - | 50 | User first name |
| `lastName` | String | Yes | - | 50 | User last name |
| `phoneNumber` | String | No | - | 15 | Phone number |
| `emailAddress` | String | Yes | - | 100 | Email address (unique) |
| `description` | String | No | - | - | User description |
| `userAvatarLink` | String | No | - | 150 | Avatar image link |
| `address` | String | No | - | - | User address |

**Example Request:**
```http
POST /api/users
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "userProviderId": 1,
  "userRoleId": 4,
  "userEnabled": true,
  "userName": "newcustomer",
  "password": "password123",
  "firstName": "Jane",
  "lastName": "Doe",
  "phoneNumber": "0901234567",
  "emailAddress": "jane.doe@example.com",
  "description": "New customer account",
  "address": "123 Main Street, Ho Chi Minh City"
}
```

**Example Response:**
```json
{
  "code": 200,
  "message": "User created successfully",
  "data": {
    "userId": 25,
    "userProviderId": 1,
    "userRoleId": 4,
    "userEnabled": true,
    "userName": "newcustomer",
    "password": null,
    "firstName": "Jane",
    "lastName": "Doe",
    "phoneNumber": "0901234567",
    "emailAddress": "jane.doe@example.com",
    "description": "New customer account",
    "userAvatarLink": null,
    "address": "123 Main Street, Ho Chi Minh City",
    "lastLoginDt": null,
    "createdDt": "2026-01-26 14:30:00",
    "modifiedDt": "2026-01-26 14:30:00",
    "userProviderName": "LOCAL",
    "userRoleName": "CUSTOMER"
  },
  "timestamp": "2026-01-26 14:30:00"
}
```

---

### 5. Update User
**Endpoint:** `PUT /api/users/{userId}`  
**Method:** PUT  
**Access:** ADMIN, MANAGER, SELF  
**Description:** Update existing user information

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `userId` | Long | Yes | Path | User ID to update |

**Request Body:** Same as Create User (all fields optional)

**Example Request:**
```http
PUT /api/users/4
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "firstName": "Nguyen Updated",
  "lastName": "Van A Updated",
  "phoneNumber": "0999888777",
  "address": "456 New Street, Ho Chi Minh City"
}
```

**Example Response:**
```json
{
  "code": 200,
  "message": "User updated successfully",
  "data": {
    "userId": 4,
    "userName": "customer01",
    "firstName": "Nguyen Updated",
    "lastName": "Van A Updated",
    "phoneNumber": "0999888777",
    "address": "456 New Street, Ho Chi Minh City",
    "modifiedDt": "2026-01-26 14:35:00"
  },
  "timestamp": "2026-01-26 14:35:00"
}
```

---

### 6. Update User Status
**Endpoint:** `PATCH /api/users/{userId}/status`  
**Method:** PATCH  
**Access:** ADMIN, MANAGER  
**Description:** Enable or disable user account

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `userId` | Long | Yes | Path | User ID to update |
| `enabled` | Boolean | Yes | Query | New enabled status |

**Example Request:**
```http
PATCH /api/users/4/status?enabled=false
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Example Response:**
```json
{
  "code": 200,
  "message": "User status updated successfully",
  "data": {
    "userId": 4,
    "userName": "customer01",
    "userEnabled": false,
    "modifiedDt": "2026-01-26 14:40:00"
  },
  "timestamp": "2026-01-26 14:40:00"
}
```

---

### 7. Delete User
**Endpoint:** `DELETE /api/users/{userId}`  
**Method:** DELETE  
**Access:** ADMIN only  
**Description:** Delete user account (permanent action)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `userId` | Long | Yes | Path | User ID to delete |

**Example Request:**
```http
DELETE /api/users/25
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Example Response:**
```json
{
  "code": 200,
  "message": "User deleted successfully",
  "data": null,
  "timestamp": "2026-01-26 14:45:00"
}
```

---

### 8. Search Users by Name
**Endpoint:** `GET /api/users/search-by-name`  
**Method:** GET  
**Access:** ADMIN, MANAGER, STAFF  
**Description:** Search users by first name or last name

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `searchTerm` | String | Yes | Query | Search term for names (min 2 chars) |

**Example Request:**
```http
GET /api/users/search-by-name?searchTerm=Nguyen
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Example Response:**
```json
{
  "code": 200,
  "message": "Users found successfully",
  "data": [
    {
      "userId": 4,
      "userName": "customer01",
      "firstName": "Nguyen",
      "lastName": "Van A",
      "emailAddress": "customer1@gmail.com",
      "userRoleName": "CUSTOMER",
      "userEnabled": true
    }
  ],
  "timestamp": "2026-01-26 14:50:00"
}
```

---

### 9. Check Username Availability
**Endpoint:** `GET /api/users/check-username`  
**Method:** GET  
**Access:** PUBLIC (no authentication required)  
**Description:** Check if username is available for registration

**Request Parameters:**

| Parameter | Type | Required | Location | Min Length | Max Length | Description |
|-----------|------|----------|----------|------------|------------|-------------|
| `userName` | String | Yes | Query | 3 | 64 | Username to check availability |

**Example Request:**
```http
GET /api/users/check-username?userName=testuser123
Host: localhost:8080
Content-Type: application/json
```

**Example Response:**
```json
{
  "code": 200,
  "message": "Username check completed",
  "data": false,
  "timestamp": "2026-01-26 14:55:00"
}
```
*Note: `false` means username is available, `true` means already exists*

---

### 10. Check Email Availability
**Endpoint:** `GET /api/users/check-email`  
**Method:** GET  
**Access:** PUBLIC (no authentication required)  
**Description:** Check if email is available for registration

**Request Parameters:**

| Parameter | Type | Required | Location | Max Length | Description |
|-----------|------|----------|----------|------------|-------------|
| `email` | String | Yes | Query | 100 | Email address to check availability |

**Example Request:**
```http
GET /api/users/check-email?email=test@example.com
Host: localhost:8080
Content-Type: application/json
```

**Example Response:**
```json
{
  "code": 200,
  "message": "Email check completed",
  "data": false,
  "timestamp": "2026-01-26 14:55:00"
}
```
*Note: `false` means email is available, `true` means already exists*

---

## Password Management APIs

### 11. Change Password (Self-Service)
**Endpoint:** `PUT /api/users/change-password`  
**Method:** PUT  
**Access:** CUSTOMER, STAFF, MANAGER, ADMIN (Self-service only)  
**Description:** Authenticated user changes their own password

**Request Body (JSON):**

| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| `currentPassword` | String | Yes | Not blank | Current password for verification |
| `newPassword` | String | Yes | 8-150 chars, complexity rules | New password |
| `confirmPassword` | String | Yes | Must match newPassword | Password confirmation |

**Password Complexity Rules:**
- Minimum 8 characters, maximum 150 characters
- Must contain at least one uppercase letter (A-Z)
- Must contain at least one lowercase letter (a-z)
- Must contain at least one number (0-9)

**Example Request:**
```http
PUT /api/users/change-password
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "currentPassword": "oldPassword123",
  "newPassword": "NewSecurePass456",
  "confirmPassword": "NewSecurePass456"
}
```

**Example Response:**
```json
{
  "code": 200,
  "message": "Password changed successfully",
  "data": {
    "userId": 4,
    "userName": "customer01",
    "firstName": "Nguyen",
    "lastName": "Van A",
    "modifiedDt": "2026-01-26 15:30:00",
    "password": null
  },
  "timestamp": "2026-01-26 15:30:00"
}
```

---

### 12. Reset Password (Admin)
**Endpoint:** `PUT /api/users/{userId}/reset-password`  
**Method:** PUT  
**Access:** ADMIN, MANAGER  
**Description:** Admin resets password for any user (ID-based priority for admin efficiency)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `userId` | Long | Yes | Path | User ID to reset password |

**Request Body (JSON):**

| Field | Type | Required | Default | Description |
|-------|------|----------|---------|-------------|
| `newPassword` | String | Yes | - | New password (complexity rules apply) |
| `forceChangeOnLogin` | Boolean | No | true | Force user to change password on next login |

**Example Request:**
```http
PUT /api/users/4/reset-password
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <admin_jwt_token>

{
  "newPassword": "TempPassword789",
  "forceChangeOnLogin": true
}
```

**Example Response:**
```json
{
  "code": 200,
  "message": "Password reset successfully",
  "data": {
    "userId": 4,
    "userName": "customer01",
    "firstName": "Nguyen",
    "lastName": "Van A",
    "modifiedDt": "2026-01-26 15:35:00",
    "password": null
  },
  "timestamp": "2026-01-26 15:35:00"
}
```

---

### 13. Change Password by Username (Customer-Friendly)
**Endpoint:** `PUT /api/users/username/{userName}/change-password`  
**Method:** PUT  
**Access:** CUSTOMER (Self-service), ADMIN, MANAGER (with validation)  
**Description:** Change password using username (customer-friendly identification)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `userName` | String | Yes | Path | Username for password change |

**Request Body (JSON):** Same as Change Password (Self-Service)

**Security Note:** 
- **Customer Access:** Can only change own password (JWT userName must match path userName)
- **Admin Access:** Can change any user's password using username

**Example Request:**
```http
PUT /api/users/username/customer01/change-password
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "currentPassword": "currentPass123",
  "newPassword": "CustomerNewPass456",
  "confirmPassword": "CustomerNewPass456"
}
```

**Example Response:** Same as Change Password (Self-Service)

---

## Database Query Strategy Analysis

### Current Implementation Status

**All database operations currently use JPA Repository pattern:**
- ✅ **100% JPA Implementation** - UserRepository interface with JPA queries
- ❌ **0% Stored Procedures** - No stored procedures currently used in code
- ✅ **Current Performance** - Adequate for small to medium user base (<10,000 users)
- ✅ **Development Speed** - Fast development and easy testing

### JPA vs Stored Procedures Decision Matrix

| Operation Type | Current Implementation | Recommendation | Performance Impact | When to Migrate |
|----------------|----------------------|----------------|-------------------|------------------|
| **User Authentication** | JPA with JOIN FETCH | Keep JPA | Low impact | >1000 auth/hour |
| **User CRUD Operations** | JPA Repository | Keep JPA | Very low | Never (unless >10K users) |
| **User Search/Filter** | JPA with @Query | Keep JPA | Low impact | >5000 searches/hour |
| **Password Operations** | JPA (BCrypt in service) | Keep JPA | Very low | Keep JPA permanently |
| **Bulk User Operations** | Not implemented | Use JPA initially | Medium impact | >100 users/batch |
| **User Reports/Analytics** | Not implemented | Consider SP for future | High impact | When reporting needed |

### Performance Thresholds for Migration

| Metric | Current JPA Performance | Stored Procedure Threshold | Expected Improvement |
|--------|------------------------|---------------------------|----------------------|
| **User Login** | 5-15ms average | When >50ms or >1000/hour | 30-50% faster |
| **User Registration** | 10-25ms average | When >100ms | 20-40% faster |
| **User Search** | 15-40ms average | When >100ms or >5000/hour | 50-70% faster |
| **User Updates** | 5-20ms average | Rarely needed | 20-30% faster |

### Migration Strategy (Future Planning)

**Phase 1: Maintain JPA (Recommended for 2026)**
- Continue with JPA for all current operations
- Monitor query performance using Spring Boot Actuator
- Optimize JPA queries with proper indexing
- Add connection pooling optimization

**Phase 2: Hybrid Approach (When needed)**
- Implement stored procedures for specific high-frequency operations
- Maintain dual implementation (JPA + SP) for A/B testing
- Focus on authentication and search operations first

**Phase 3: Strategic Migration (Enterprise scale)**
- Use stored procedures for complex reporting
- Implement bulk operations with stored procedures
- Keep JPA for simple CRUD operations

**Current Recommendation:** Continue with JPA approach due to:
- Excellent development speed and maintainability
- Easy testing and debugging
- Platform independence
- Current performance is adequate for expected load
- No immediate need for complex operations requiring stored procedures

---

## Common Response Structure

All API responses use `BaseResponseDto` format:

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `code` | Integer | Yes | HTTP status code (200=success, 400=bad request, 404=not found, 500=server error) |
| `message` | String | Yes | Human-readable response message |
| `data` | Object/Array/null | No | Response data (varies by endpoint) |
| `timestamp` | String | Yes | Response timestamp (yyyy-MM-dd HH:mm:ss format) |

**Success Response Codes:**
- `200` - Success
- `201` - Created (for POST operations)

**Error Response Codes:**
- `400` - Bad Request (validation error)
- `401` - Unauthorized (authentication required)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found
- `500` - Internal Server Error

---

## UserDto Fields

| Field | Type | Description |
|-------|------|-------------|
| `userId` | Long | User unique identifier |
| `userProviderId` | Long | User provider ID |
| `userRoleId` | Long | User role ID |
| `userEnabled` | Boolean | User enabled status |
| `userName` | String | Username |
| `password` | String | Always null for security |
| `firstName` | String | User first name |
| `lastName` | String | User last name |
| `phoneNumber` | String | Phone number |
| `emailAddress` | String | Email address |
| `description` | String | User description |
| `userAvatarLink` | String | Avatar image link |
| `address` | String | User address |
| `lastLoginDt` | String | Last login timestamp |
| `createdDt` | String | User creation timestamp |
| `modifiedDt` | String | Last modification timestamp |
| `userProviderName` | String | Provider name (LOCAL, GOOGLE, etc.) |
| `userProviderRemarkEn` | String | Provider English description |
| `userProviderRemark` | String | Provider Vietnamese description |
| `userRoleName` | String | Role name (ADMIN, MANAGER, STAFF, CUSTOMER) |
| `userRoleRemarkEn` | String | Role English description |
| `userRoleRemark` | String | Role Vietnamese description |

---

## Database Schema Reference

**Primary Tables:**

| Table | Description | Key Fields |
|-------|-------------|------------|
| `USER` | Core user information | USER_ID, USER_NAME, EMAIL, USER_PROVIDER_ID, USER_ROLE_ID |
| `USER_PROVIDER` | Authentication provider details | USER_PROVIDER_ID, USER_PROVIDER_NAME |
| `USER_ROLE` | User role information | USER_ROLE_ID, USER_ROLE_NAME |

**Provider Types:**
- `1` - LOCAL (Username/Password authentication)
- `2` - GOOGLE (Google OAuth)
- `3` - FACEBOOK (Facebook OAuth)
- `4` - GITHUB (GitHub OAuth)

**Role Types:**
- `1` - ADMIN (System Administrator)
- `2` - MANAGER (Store Manager)
- `3` - STAFF (Store Staff)
- `4` - CUSTOMER (Regular Customer)

**Password Management Extensions:**
- `PASSWORD_RESET_AT` DATETIME - Timestamp of last password reset
- `FORCE_PASSWORD_CHANGE` BOOLEAN - Flag to force password change on next login
- Password history table (optional) for preventing password reuse

**Implementation Notes:**
- All APIs use JPA queries through UserService (100% JPA implementation)
- No stored procedures currently implemented or needed for current scale
- JWT authentication planned for future implementation
- Password encryption uses BCrypt algorithm with strength 12
- All password management APIs follow security best practices
- Database query performance is adequate for current user base

**Performance Analysis:**
- Current JPA implementation handles user base up to 10,000 users efficiently
- Average query response time: 5-40ms depending on operation complexity
- Stored procedures recommended only when specific performance thresholds are exceeded
- Migration to hybrid approach (JPA + SP) planned for enterprise scale (>10K users)