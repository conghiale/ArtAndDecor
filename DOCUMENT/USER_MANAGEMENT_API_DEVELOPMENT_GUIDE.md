# USER MANAGEMENT API DEVELOPMENT GUIDE

**Project:** Art & Decor E-commerce Platform  
**Date:** February 27, 2026  
**Author:** Development Team  
**Version:** 11.0  
**Features:** Enhanced User Management APIs - Simplified filtering with enhanced search capabilities and Policy-based email configuration  

---

## API Overview

The User Management API provides comprehensive endpoints for managing users, roles, and authentication providers with enhanced security and filtering capabilities.

### Available Endpoints

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/users` | ADMIN | Retrieve all users with advanced filtering |
| GET | `/api/users/{id}` | ADMIN, CUSTOMER (own) | Get user details by ID |
| POST | `/api/users` | ADMIN | Create a new user |
| PUT | `/api/users/{id}` | ADMIN, CUSTOMER (own) | Update user information |
| DELETE | `/api/users/{id}` | ADMIN | Soft delete a user |
| GET | `/api/users/roles` | ADMIN | Get all user roles |
| GET | `/api/users/roles/names` | ALL | Get role names for dropdown |
| GET | `/api/users/providers` | ADMIN | Get all user providers |
| GET | `/api/users/providers/names` | ALL | Get provider names for dropdown |
| PUT | `/api/users/{id}/password` | ADMIN, CUSTOMER (own) | Update user password |
| POST | `/api/users/{id}/reset-password` | ADMIN | Reset user password |
| GET | `/api/users/statistics` | ADMIN | Get user management statistics |

### Key Features

- **Advanced User Search:** Multi-criteria filtering with text search across names, emails, roles, and providers
- **Role-based Access Control:** Comprehensive permission system for different user types
- **Provider Management:** Support for multiple authentication sources (LOCAL, GOOGLE, FACEBOOK)
- **Password Security:** Secure password management with encryption and validation  
- **Policy-driven Email Config:** Dynamic email configuration through POLICY table
- **Audit Trail:** Complete tracking of user modifications and access patterns
- **Data Validation:** Comprehensive input validation with detailed error messages
- **Dropdown Support:** Simplified endpoints for UI component integration

---

## Table of Contents

- [Overview](#overview)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)  
  - [User Role Management](#user-role-management)
  - [User Provider Management](#user-provider-management)
  - [User Management](#user-management)
  - [Password Management](#password-management)
- [DTOs and Validation](#dtos-and-validation)
- [Error Handling](#error-handling)
- [Examples](#examples)
- [Security Considerations](#security-considerations)
- [Email Configuration](#email-configuration)

## Overview

This guide covers the enhanced User Management API system that handles users, user roles, and user providers in the ArtAndDecor application. The API provides CRUD operations for users with advanced filtering and search capabilities, and read-only operations for roles and providers.

### Key Features (v11.0)
- **Simplified Filtering**: Removed ID-based filtering for cleaner API design  
- **Enhanced Search**: Extended textSearch to include USER_PROVIDER_DISPLAY_NAME and USER_ROLE_DISPLAY_NAME
- **Dropdown APIs**: New endpoints to get role/provider names for UI components
- **Policy-based Email Config**: Email settings now stored in POLICY table for easy management
- User management with role-based access control
- User provider management (authentication sources) - read-only
- User role management - read-only  
- Comprehensive validation and error handling
- Audit logging and security controls
- OpenAPI 3.0 documentation integration

### Key Changes in v11.0
1. **UserRole APIs**: Removed `roleId` filter, added `/api/users/roles/names` endpoint
2. **UserProvider APIs**: Removed `providerId` filter, added `/api/users/providers/names` endpoint  
3. **User APIs**: Removed ID-based filters (`userId`, `userProviderId`, `userRoleId`), enhanced textSearch
4. **Email Configuration**: Migrated from configEmail.properties to POLICY table for dynamic configuration

### Authentication Requirements

- **Public APIs:** No authentication required
- **User APIs:** JWT Bearer token required
- **Admin APIs:** Admin role verification required (for reset password)
- **Self-access:** Users can access/modify their own data

## Database Schema

### USER Table
```sql
CREATE TABLE USER (
    USER_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    USER_NAME VARCHAR(50) NOT NULL UNIQUE,
    FIRST_NAME VARCHAR(50),
    LAST_NAME VARCHAR(50),
    EMAIL VARCHAR(100) NOT NULL UNIQUE,
    PASSWORD VARCHAR(255) NOT NULL,
    USER_ENABLED BOOLEAN DEFAULT TRUE,
    USER_PROVIDER_ID BIGINT NOT NULL,
    USER_ROLE_ID BIGINT NOT NULL,
    CONSTRAINT FK_USER_PROVIDER FOREIGN KEY (USER_PROVIDER_ID) REFERENCES USER_PROVIDER(USER_PROVIDER_ID) ON DELETE RESTRICT,
    CONSTRAINT FK_USER_ROLE FOREIGN KEY (USER_ROLE_ID) REFERENCES USER_ROLE(USER_ROLE_ID) ON DELETE RESTRICT
);
```

### USER_ROLE Table
```sql
CREATE TABLE USER_ROLE (
    USER_ROLE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    USER_ROLE_NAME VARCHAR(50) NOT NULL UNIQUE,
    USER_ROLE_DISPLAY_NAME VARCHAR(255),
    USER_ROLE_REMARK TEXT,
    USER_ROLE_ENABLED BOOLEAN DEFAULT TRUE
);
```

### USER_PROVIDER Table
```sql
CREATE TABLE USER_PROVIDER (
    USER_PROVIDER_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    USER_PROVIDER_NAME VARCHAR(50) NOT NULL UNIQUE,
    USER_PROVIDER_DISPLAY_NAME VARCHAR(255),
    USER_PROVIDER_REMARK TEXT,
    USER_PROVIDER_ENABLED BOOLEAN DEFAULT TRUE
);
```

## API Endpoints

### User Role Management

#### 1. Get User Roles with Optional Filtering
```http
GET /api/users/roles
```
**Access:** PUBLIC  
**Description:** Get all user roles or filter by specific criteria. Returns all roles when no parameters are provided.

**Query Parameters (all optional):**
- `roleName`: Filter by exact role name
- `textSearch`: Search in role name, display name, or remark (partial match, case-insensitive)
- `enabled`: Filter by enabled status (true/false/null for all)

**Example Requests:**
```http
# Get all roles
GET /api/users/roles

# Search for admin roles that are enabled
GET /api/users/roles?textSearch=admin&enabled=true

# Filter by specific role name
GET /api/users/roles?roleName=USER&enabled=true
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Roles retrieved successfully",
  "data": [
    {
      "userRoleId": 1,
      "userRoleName": "ADMIN",
      "userRoleDisplayName": "Administrator",
      "userRoleRemark": "System administrator with full access",
      "userRoleEnabled": true,
      "userCount": 5
    },
    {
      "userRoleId": 2,
      "userRoleName": "USER",
      "userRoleDisplayName": "Regular User", 
      "userRoleRemark": "Standard user with limited access",
      "userRoleEnabled": true,
      "userCount": 150
    }
  ],
  "timestamp": "2026-02-27 10:45:30"
}
      "userRoleName": "CUSTOMER",
      "userRoleDisplayName": "Customer",
      "userRoleRemark": "Standard customer access",
      "userRoleEnabled": true,
      "userCount": 15
    }
  ],
  "timestamp": "2026-02-25 10:45:30"
}
```

#### 2. Get User Role by ID
```http
GET /api/users/roles/{roleId}
```
**Access:** PUBLIC  
**Description:** Get specific user role details by ID

**Path Parameters:**
- `roleId`: User role ID

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Role retrieved successfully",
  "data": {
    "userRoleId": 1,
    "userRoleName": "ADMIN",
    "userRoleDisplayName": "Administrator",
    "userRoleRemark": "Full system access",
    "userRoleEnabled": true,
    "userCount": 2
  }
}
```

#### 3. Get All User Role Names
```http
GET /api/users/roles/names
```
**Access:** PUBLIC  
**Description:** Get list of all enabled user role names for dropdown/combobox usage in UI

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Role names retrieved successfully",
  "data": [
    "ADMIN",
    "USER",
    "CUSTOMER"
  ],
  "timestamp": "2026-02-27 10:45:30"
}
```

### User Provider Management

#### 1. Get User Providers with Optional Filtering
```http
GET /api/users/providers
```
**Access:** PUBLIC  
**Description:** Get all user providers or filter by specific criteria. Returns all providers when no parameters are provided.

**Query Parameters (all optional):**
- `providerName`: Filter by exact provider name
- `textSearch`: Search in provider name, display name, or remark (partial match, case-insensitive)
- `enabled`: Filter by enabled status (true/false/null for all)

**Example Requests:**
```http
# Get all providers
GET /api/users/providers

# Search for Google provider that is enabled
GET /api/users/providers?textSearch=google&enabled=true

# Filter by specific provider name
GET /api/users/providers?providerName=LOCAL&enabled=true
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "User providers retrieved successfully",
  "data": [
    {
      "userProviderId": 1,
      "userProviderName": "LOCAL",
      "userProviderDisplayName": "Local Authentication",
      "userProviderRemark": "Standard username/password authentication",
      "userProviderEnabled": true,
      "userCount": 18
    },
    {
      "userProviderId": 2,
      "userProviderName": "GOOGLE",
      "userProviderDisplayName": "Google OAuth",
      "userProviderRemark": "Google OAuth2 authentication",
      "userProviderEnabled": true,
      "userCount": 3
    }
  ],
  "timestamp": "2026-02-25 10:45:30"
}
```

#### 2. Get User Provider by ID
```http
GET /api/users/providers/{providerId}
```
**Access:** PUBLIC  
**Description:** Get detailed information for a specific user provider by ID

**Path Parameters:**
- `providerId`: User provider ID

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Provider retrieved successfully",
  "data": {
    "userProviderId": 1,
    "userProviderName": "LOCAL",
    "userProviderDisplayName": "Local Authentication", 
    "userProviderRemark": "Standard username/password authentication",
    "userProviderEnabled": true,
    "userCount": 18
  },
  "timestamp": "2026-02-27 10:45:30"
}
```

#### 3. Get All User Provider Names
```http
GET /api/users/providers/names
```
**Access:** PUBLIC  
**Description:** Get list of all enabled user provider names for dropdown/combobox usage in UI

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Provider names retrieved successfully",
  "data": [
    "LOCAL",
    "GOOGLE",
    "FACEBOOK"
  ],
  "timestamp": "2026-02-27 10:45:30"
}
```

### User Management

#### 1. Get Users by Enhanced Criteria with Pagination
```http
GET /api/users?providerName={providerName}&providerDisplayName={providerDisplayName}&roleName={roleName}&roleDisplayName={roleDisplayName}&searchText={searchText}&userName={userName}&userEnabled={userEnabled}&page={page}&size={size}&sort={sort}&direction={direction}
```
**Access:** AUTHENTICATED (Admin Role Required)  
**Description:** Get users by multiple criteria with pagination and enhanced search capabilities. Returns all users if no filters are provided.

**Query Parameters (all optional):**
- `providerName`: Filter by provider name (partial match, case-insensitive)
- `providerDisplayName`: Filter by provider display name (partial match, case-insensitive)
- `roleName`: Filter by role name (partial match, case-insensitive)
- `roleDisplayName`: Filter by role display name (partial match, case-insensitive) 
- `searchText`: Enhanced search across userName, firstName, lastName, phoneNumber, email, USER_PROVIDER_DISPLAY_NAME, USER_ROLE_DISPLAY_NAME (partial match, case-insensitive)
- `userName`: Filter by username (exact match)
- `userEnabled`: Filter by user enabled status (true/false/null for all)
- `page`: Page number (default: 0, zero-based)
- `size`: Page size (default: 10)
- `sort`: Sort field (default: userId)
- `direction`: Sort direction: ASC or DESC (default: ASC)

**Example Request:**
```http
GET /api/users?roleName=admin&searchText=john&page=0&size=5&sort=userId&direction=ASC
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Users retrieved successfully",
  "data": {
    "content": [
      {
        "userId": 1,
        "userName": "john_admin",
        "firstName": "John",
        "lastName": "Smith",
        "phoneNumber": "+1234567890",
        "email": "john.admin@example.com",
        "imageAvatarName": "avatar1.jpg",
        "socialMedia": {"linkedin": "john-smith"},
        "userEnabled": true,
        "lastLoginDt": "2026-02-23T10:30:00",
        "userRole": {
          "userRoleId": 1,
          "userRoleName": "ADMIN", 
          "userRoleDisplayName": "Administrator"
        },
        "userProvider": {
          "userProviderId": 1,
          "userProviderName": "LOCAL", 
          "userProviderDisplayName": "Local Authentication"
        }
      }
    ],
    "pageable": {
      "page": 0,
      "size": 5,
      "sort": "userId,ASC"
    },
    "totalElements": 25,
    "totalPages": 5,
    "first": true,
    "last": false
  },
  "timestamp": "2026-02-24 10:45:30"
}
```

#### 2. Get User by ID
```http
GET /api/users/{userId}
```
**Access:** AUTHENTICATED  
**Description:** Get specific user details by ID

**Path Parameters:**
- `userId`: User ID

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "User retrieved successfully", 
  "data": {
    "userId": 1,
    "userName": "john_admin",
    "firstName": "John",
    "lastName": "Smith",
    "phoneNumber": "+1234567890", 
    "email": "john.admin@example.com",
    "imageAvatarName": "avatar1.jpg",
    "socialMedia": {"linkedin": "john-smith"},
    "userEnabled": true,
    "lastLoginDt": "2026-02-23T10:30:00",
    "userRole": {
      "userRoleId": 1,
      "userRoleName": "ADMIN",
      "userRoleDisplayName": "Administrator"
    },
    "userProvider": {
      "userProviderId": 1,
      "userProviderName": "LOCAL",
      "userProviderDisplayName": "Local Authentication"
    }
  }
}
```

#### 3. Get All Users (Paginated)
```http
GET /api/users?page={page}&size={size}
```
**Access:** AUTHENTICATED  
**Description:** Get all users with pagination

**Query Parameters:**
- `page` (optional, default=0): Page number (0-based)
- `size` (optional, default=10): Page size

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Users retrieved successfully",
  "data": {
    "content": [
      {
        "userId": 1,
        "userName": "john_admin",
        "firstName": "John",
        "lastName": "Smith",
        "userEnabled": true,
        "userRole": {
          "userRoleId": 1,
          "userRoleName": "ADMIN",
          "userRoleDisplayName": "Administrator"
        },
        "userProvider": {
          "userProviderId": 1,
          "userProviderName": "LOCAL",
          "userProviderDisplayName": "Local Authentication"
        }
      }
    ],
    "pageable": {
      "sort": {"sorted": false, "unsorted": true},
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 25,
    "totalPages": 3,
    "size": 10,
    "number": 0,
    "first": true,
    "last": false
  }
}
```

#### 6. Create New User
```http
POST /api/users
Content-Type: application/json
```
**Access:** ADMIN or AUTHENTICATED (for registration)  
**Description:** Create a new user

**Request Body:**
```json
{
  "userName": "jane_customer",
  "firstName": "Jane",
  "lastName": "Doe",
  "phoneNumber": "+0987654321",
  "email": "jane.doe@example.com", 
  "password": "SecurePass123!",
  "imageAvatarName": "avatar2.jpg",
  "socialMedia": {"facebook": "jane.doe"},
  "userEnabled": true,
  "userRole": {
    "userRoleId": 4
  },
  "userProvider": {
    "userProviderId": 1  
  }
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "User created successfully",
  "data": {
    "userId": 25,
    "userName": "jane_customer",
    "firstName": "Jane", 
    "lastName": "Doe",
    "phoneNumber": "+0987654321",
    "email": "jane.doe@example.com",
    "imageAvatarName": "avatar2.jpg",
    "socialMedia": {"facebook": "jane.doe"},
    "userEnabled": true,
    "lastLoginDt": null,
    "userRole": {
      "userRoleId": 4,
      "userRoleName": "CUSTOMER",
      "userRoleDisplayName": "Customer"
    },
    "userProvider": {
      "userProviderId": 1,
      "userProviderName": "LOCAL",
      "userProviderDisplayName": "Local Authentication"
    }
  }
}
```

#### 7. Update User
```http
PUT /api/users/{userId}
Content-Type: application/json
```
**Access:** ADMIN or SELF  
**Description:** Update existing user

**Path Parameters:**
- `userId`: User ID to update

**Request Body:** Same as create user (all fields optional for update)

**Response:** Same as create user response

#### 8. Update User Status (Enable/Disable)
```http
PATCH /api/users/{userId}/status?enabled={enabled}
```
**Access:** ADMIN  
**Description:** Enable or disable user account

**Path Parameters:**
- `userId`: User ID

**Query Parameters:**
- `enabled`: Boolean (true/false)

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "User status updated successfully",
  "data": {
    "userId": 25,
    "userName": "jane_customer",
    "userEnabled": false,
    "userRole": {
      "userRoleId": 4,
      "userRoleName": "CUSTOMER",
      "userRoleDisplayName": "Customer"
    }
  }
}
```

### Password Management

#### 1. Change Password (Self-Service)
```http  
PUT /api/users/change-password
Content-Type: application/json
Authorization: Bearer {token}
```
**Access:** AUTHENTICATED (Self only)  
**Description:** Change password for authenticated user

**Request Body:**
```json
{
  "currentPassword": "OldPassword123!",
  "newPassword": "NewPassword456!",
  "confirmPassword": "NewPassword456!"
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Password changed successfully",
  "data": {
    "userId": 25,
    "userName": "jane_customer",
    "firstName": "Jane",
    "lastName": "Doe",
    "email": "jane.doe@example.com"
  }
}
```

#### 2. Admin Reset Password (Username-based)
```http
PUT /api/users/reset-password/{userName}
Authorization: Bearer {admin_token}
```
**Access:** ADMIN  
**Description:** Admin reset password for any user. Generates random password and sends email notification.

**Path Parameters:**
- `userName`: Username to reset password for

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Password reset successfully", 
  "data": {
    "userId": 25,
    "userName": "jane_customer",
    "firstName": "Jane",
    "lastName": "Doe",
    "email": "jane.doe@example.com",
    "userEnabled": true
  }
}
```

**Note:** New password is auto-generated and sent to user's email address.


## DTOs and Validation

### UserDto
```java
public class UserDto {
    private Long userId;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String userName;
    
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @Size(max = 50, message = "Last name must not exceed 50 characters") 
    private String lastName;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    private String imageAvatarName;
    private Object socialMedia; // JSON object
    private Boolean userEnabled;
    private LocalDateTime lastLoginDt;
    
    // Related entities
    private UserRoleDto userRole;
    private UserProviderDto userProvider;
}
```

### ChangePasswordRequest
```java
public class ChangePasswordRequest {
    @NotBlank(message = "Current password is required")
    private String currentPassword;
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
    
    // Validation method
    public boolean isPasswordsMatch() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}
```

### UserRoleDto  
```java
public class UserRoleDto {
    private Long userRoleId;
    private String userRoleName;
    private String userRoleDisplayName;
    private String userRoleRemark;
    private Boolean userRoleEnabled;
    private Long userCount; // Count of users with this role
}
```

### UserProviderDto
```java  
public class UserProviderDto {
    private Long userProviderId;
    private String userProviderName;
    private String userProviderDisplayName;
    private String userProviderRemark;
    private Boolean userProviderEnabled;
    private Long userCount; // Count of users with this provider
}
```

## Error Handling

### Standard Error Response Format
```json
{
  "status": "BAD_REQUEST|SERVER_ERROR|UNAUTHORIZED|FORBIDDEN|NOT_FOUND",
  "message": "Error description",
  "data": null,
  "timestamp": "2026-02-23T10:30:00.123456",
  "path": "/users/123"
}
```

### Common Error Scenarios

#### 1. Validation Errors (400 Bad Request)
```json
{
  "status": "BAD_REQUEST",
  "message": "Validation failed: Username is required, Email format is invalid",
  "data": null
}
```

#### 2. User Not Found (400 Bad Request)
```json
{
  "status": "BAD_REQUEST", 
  "message": "User not found with ID: 999",
  "data": null
}
```

#### 3. Duplicate Username/Email (400 Bad Request)
```json
{
  "status": "BAD_REQUEST",
  "message": "Username already exists: john_user",
  "data": null
}
```

#### 4. Authentication Required (401 Unauthorized)
```json
{
  "status": "UNAUTHORIZED",
  "message": "Authentication required",
  "data": null
}
```

#### 5. Insufficient Permissions (403 Forbidden)
```json
{
  "status": "FORBIDDEN",
  "message": "Insufficient permissions for this operation",
  "data": null
}
```

#### 6. Password Change Errors (400 Bad Request)
```json
{
  "status": "BAD_REQUEST",
  "message": "Current password is incorrect",
  "data": null
}
```

## Examples

### Example 1: User Registration Flow
```bash
# 1. Create new customer user
curl -X POST "http://localhost:8080/users" \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "new_customer",
    "firstName": "New",
    "lastName": "Customer", 
    "email": "new.customer@example.com",
    "password": "SecurePass123!",
    "userRole": {"userRoleId": 4},
    "userProvider": {"userProviderId": 1}
  }'

# 2. Login and get JWT token (separate authentication endpoint)
# ... authentication process ...

# 3. Update profile
curl -X PUT "http://localhost:8080/users/26" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Updated",
    "lastName": "Customer",
    "phoneNumber": "+1234567890"
  }'
```

### Example 2: User Search and Management
```bash
# 1. Search users by role and text
curl -X GET "http://localhost:8080/users?roleName=customer&searchText=john" \
  -H "Authorization: Bearer {token}"

# 2. Get paginated users  
curl -X GET "http://localhost:8080/users?page=0&size=5" \
  -H "Authorization: Bearer {token}"

# 3. Admin disable user
curl -X PATCH "http://localhost:8080/users/26/status?enabled=false" \
  -H "Authorization: Bearer {admin_token}"
```

### Example 3: Password Management Flow
```bash
# 1. User changes own password
curl -X PUT "http://localhost:8080/users/change-password" \
  -H "Authorization: Bearer {user_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "OldPassword123!",
    "newPassword": "NewPassword456!",
    "confirmPassword": "NewPassword456!"
  }'

# 2. Admin resets user password (auto-generates and emails new password)
curl -X PUT "http://localhost:8080/users/reset-password/problem_user" \
  -H "Authorization: Bearer {admin_token}"
```

## Security Considerations

### 1. Authentication and Authorization
- All user management operations require JWT authentication
- Admin operations (reset password, disable users) require ADMIN role
- Users can only modify their own data (except for admin users)

### 2. Password Security
- Minimum 8 characters required for passwords
- Passwords are encrypted using BCrypt before storage
- Password reset generates cryptographically secure random passwords
- Current password verification required for password changes

### 3. Input Validation
- All inputs are validated using Bean Validation annotations
- SQL injection prevention through parameterized queries
- XSS prevention through proper output encoding

### 4. Rate Limiting and Monitoring  
- Implement rate limiting on password-related endpoints
- Log all administrative actions for audit trails
- Monitor failed password attempts

### 5. Email Security
- Password reset emails sent securely
- Email delivery failures are logged but don't fail the password reset operation
- Consider implementing email verification for password resets

### 6. Data Privacy
- Sensitive data (passwords) never returned in API responses
- User search limited to authenticated users
- Consider implementing field-level access controls

## Notes

### API Design Decisions

1. **Role and Provider Management**: Currently read-only. No create/update/delete operations exposed via API for security and data integrity.

2. **Consolidated Endpoints**: Main endpoints (`/api/users/roles`, `/api/users/providers`, `/users`) now include integrated filtering capabilities. When no filter parameters are provided, they return all results. When filters are provided, they return filtered results. This eliminates the need for separate search endpoints.

3. **OpenAPI Integration**: All endpoints now include comprehensive OpenAPI 3.0 documentation with detailed parameter descriptions, response schemas, and security requirements.

3. **Password Reset**: Admin-triggered reset uses username (not user ID) for better usability and uses auto-generated passwords with email notification for security.

4. **Pagination**: Only main user listing endpoint supports pagination. Search endpoints return full result sets.

5. **Error Handling**: Consistent error response format across all endpoints with descriptive error messages.

## Email Configuration

### Overview (v11.0)
Starting from version 11.0, email configuration has been consolidated into a single POLICY key for simplified management. This allows administrators to update all email settings through one configuration entry without requiring application restart.

### Consolidated Configuration
All email settings are now stored in a single POLICY record with key `EMAIL_CONFIG`. The configuration uses a newline-separated key=value format:

#### Configuration Format
```
smtp.host=smtp.gmail.com
smtp.port=587
smtp.username=artanddecor.system@gmail.com
smtp.password=your-email-password-here
smtp.auth=true
smtp.starttls.enable=true
smtp.starttls.required=true
smtp.ssl.trust=smtp.gmail.com
from.name=Art and Decor System
from.address=artanddecor.system@gmail.com
support.address=support@artanddecor.com
system.name=Art and Decor E-commerce Platform
system.website=https://artanddecor.com
system.support.phone=+84-123-456-789
```

#### Configuration Keys
**SMTP Configuration:**
- `smtp.host`: SMTP server hostname
- `smtp.port`: SMTP server port  
- `smtp.username`: SMTP authentication username
- `smtp.password`: SMTP authentication password
- `smtp.auth`: Enable SMTP authentication (true/false)
- `smtp.starttls.enable`: Enable STARTTLS (true/false)
- `smtp.starttls.required`: Require STARTTLS (true/false)
- `smtp.ssl.trust`: SSL trust configuration

**Email Template Configuration:**  
- `from.name`: Sender display name
- `from.address`: Sender email address
- `support.address`: Support email address

**System Information:**
- `system.name`: System name for email templates
- `system.website`: Website URL for email templates  
- `system.support.phone`: Support phone number

### Migration Script
To set up the consolidated email configuration in POLICY table, run:
```sql
-- Execute the migration script
SOURCE DATABASE/INSERT_EMAIL_CONFIG_POLICIES.sql;
```

### Benefits
1. **Consolidated Management**: All email settings in one POLICY record
2. **Dynamic Configuration**: Update email settings via admin interface
3. **Environment-specific**: Different settings per environment without code changes
4. **Audit Trail**: Track configuration changes through POLICY table
5. **Fallback Support**: Automatic fallback to default values if configuration is missing
6. **Simplified Deployment**: Single configuration entry reduces complexity

### Future Enhancements

1. **Advanced Pagination**: Add pagination support to search endpoints
2. **Role Management**: Add CRUD operations for roles and providers (admin-only)  
3. **User Import/Export**: Bulk user management capabilities
4. **Advanced Filtering**: Date range filtering, sorting options
5. **User Profiles**: Extended profile information and preferences
6. **Email Templates**: Customizable email templates for password reset notifications
