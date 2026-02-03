# JWT SECURITY CONFIGURATION GUIDE

**Project:** Art & Decor E-commerce Platform  
**Date:** January 27, 2026  
**Author:** Development Team  
**Version:** 1.0  
**Features:** Complete JWT Security Setup with Spring Security FilterChain Configuration  

---

## Overview

This guide provides JWT security configuration information for client developers working with the Art & Decor platform. It covers authentication requirements, security rules, endpoint access control, and CORS configuration.

### Security Architecture

- **Framework:** Spring Security 6.x with Spring Boot 3.x
- **Authentication:** JWT (JSON Web Tokens) - Stateless
- **Authorization:** Role-based access control (RBAC)
- **Session Management:** Stateless (no server sessions)
- **CORS:** Configured for multiple frontend origins

---

## Security Filter Chain

### Filter Processing Order
1. **CORS Filter** - Handle cross-origin requests
2. **CSRF Disabled** - Not needed for stateless JWT authentication
3. **JWT Authentication Filter** - Validate JWT tokens  
4. **Authorization Filter** - Role-based access control
5. **Session Management** - Stateless configuration

---

## Endpoint Access Control

### Public Endpoints (No Authentication Required)

| Category | Endpoint Pattern | Methods | Description |
|----------|------------------|---------|-------------|
| **Static Resources** | `/`, `/index.html`, `/css/**`, `/js/**`, `/images/**`, `/favicon.ico`, `/assets/**` | ALL | Static web assets |
| **Authentication** | `/api/auth/**` | ALL | Authentication endpoints (login, register, refresh) |
| **Public Checks** | `/api/users/check-username`, `/api/users/check-email` | GET | Username/email availability checks |
| **Public Content** | `/api/products/**`, `/api/categories/**`, `/api/blogs/**`, `/api/images/**` | GET | Public read-only content |
| **Health** | `/actuator/**`, `/health` | ALL | Health and monitoring endpoints |

### Authenticated Endpoints (JWT Token Required)

| Category | Endpoint Pattern | Methods | Access Level | Description |
|----------|------------------|---------|--------------|-------------|
| **User Search** | `/api/users/search-by-name` | GET | Authenticated users | Search users by name |
| **Password Management** | `/api/users/change-password` | PUT | Self-service | Change own password |
| **Password by Username** | `/api/users/username/{userName}/change-password` | PUT | Self-service | Change password by username |

### Role-Based Access Control

#### ADMIN + MANAGER Access Required

| Endpoint Pattern | Methods | Description |
|------------------|---------|-------------|
| `/api/users/search` | GET | Search users by criteria |
| `/api/users/{userId}` | GET | Get user by ID |
| `/api/users` | GET | Get all users (paginated) |
| `/api/users` | POST | Create new user |
| `/api/users/{userId}` | PUT | Update user information |

#### ADMIN Only Access

| Endpoint Pattern | Methods | Description |
|------------------|---------|-------------|
| `/api/users/{userId}/status` | PATCH | Update user status (enable/disable) |
| `/api/users/{userId}` | DELETE | Delete user account |
| `/api/users/{userId}/reset-password` | PUT | Admin reset user password |
| `/api/admin/**` | ALL | All admin-specific endpoints |

---

## CORS Configuration

### Allowed Origins
```java
List.of(
    "http://localhost:3000",      // React development server
    "http://localhost:3001",      // Alternative React port
    "http://localhost:5173",      // Vite development server
    "http://localhost:8080",      // Spring Boot server
    "https://art-and-decor.com",  // Production domain
    "https://www.art-and-decor.com" // Production www domain
)
```

### CORS Settings

| Setting | Value | Description |
|---------|-------|-------------|
| **Allowed Methods** | `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS` | HTTP methods |
| **Allowed Headers** | `*` | All headers allowed |
| **Allow Credentials** | `true` | Support cookies/auth headers |
| **Configuration Path** | `/**` | Applied to all endpoints |

---

## JWT Authentication Requirements

### Token Usage

| Scenario | Authorization Header | Required |
|----------|---------------------|----------|
| **Public Endpoints** | None | No |
| **Authenticated Endpoints** | `Bearer <access_token>` | Yes |
| **Role-based Endpoints** | `Bearer <access_token>` with valid role | Yes |

### Authorization Header Format
```http
Authorization: Bearer <jwt_access_token>
```

### Example Request with JWT
```http
GET /api/users/search
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIkFETUlOIl0sImV4cCI6MTczNzk4NzAwMH0.signature
```

---

## Role Mapping

### Spring Security Role Names
- **ADMIN** → `ROLE_ADMIN` (Spring Security requires `ROLE_` prefix)
- **MANAGER** → `ROLE_MANAGER`  
- **STAFF** → `ROLE_STAFF`
- **CUSTOMER** → `ROLE_CUSTOMER`

### Role Hierarchy
```
ADMIN (Full Access)
├── All MANAGER permissions
├── All STAFF permissions  
├── All CUSTOMER permissions
└── Exclusive ADMIN operations

MANAGER (Management Access)  
├── All STAFF permissions
├── All CUSTOMER permissions
└── User management operations

STAFF (Limited Access)
├── All CUSTOMER permissions
└── Read-only access to user data

CUSTOMER (Basic Access)
└── Own profile and public content
```

---

## Security Configuration Details

### Session Management
```java
.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```
- **Stateless:** No server-side sessions
- **JWT-based:** All authentication state in token
- **Scalable:** Supports horizontal scaling

### Authentication Provider
```java
.authenticationProvider(authenticationProvider)
```
- **Custom Provider:** Handles JWT token validation
- **User Loading:** Loads user details from database
- **Role Assignment:** Maps database roles to Spring Security authorities

### JWT Filter Integration
```java
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
```
- **Filter Order:** JWT filter runs before standard authentication
- **Token Extraction:** From Authorization header
- **Authentication Setting:** Sets SecurityContext for request

---

## Client Integration Guide

### Frontend Authentication Flow

#### 1. Login Process
```javascript
// 1. Login request
const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        usernameOrEmail: 'username',
        password: 'password'
    })
});

const result = await response.json();
if (result.code === 200) {
    // Store tokens
    sessionStorage.setItem('accessToken', result.data.accessToken);
    localStorage.setItem('refreshToken', result.data.refreshToken);
}
```

#### 2. Authenticated API Calls
```javascript
const makeAuthenticatedRequest = async (url, options = {}) => {
    const token = sessionStorage.getItem('accessToken');
    
    return fetch(url, {
        ...options,
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
            ...options.headers
        }
    });
};
```

#### 3. Error Handling
```javascript
const handleApiResponse = async (response) => {
    if (response.status === 401) {
        // Token expired - try refresh or redirect to login
        const refreshed = await refreshAccessToken();
        if (!refreshed) {
            redirectToLogin();
        }
        return;
    }
    
    if (response.status === 403) {
        // Insufficient permissions
        showErrorMessage('Access denied. Insufficient permissions.');
        return;
    }
    
    return response.json();
};
```

---

## Security Best Practices

### Token Management
1. **Access Token Storage:** Use sessionStorage (memory preferred)
2. **Refresh Token Storage:** Use localStorage or secure HTTP-only cookies  
3. **Token Renewal:** Automatic refresh before expiration
4. **Secure Transmission:** Always use HTTPS in production

### Request Headers
```http
Content-Type: application/json
Authorization: Bearer <access_token>
```

### CORS Compliance
- **Allowed Origins:** Only trusted domains
- **Credentials:** Supported for authentication headers
- **Preflight:** Handled automatically by Spring Security

---

## Environment-Specific Configuration

### Development Environment
```yaml
cors:
  allowed-origins:
    - http://localhost:3000
    - http://localhost:3001
    - http://localhost:5173
  allow-credentials: true
```

### Production Environment  
```yaml
cors:
  allowed-origins:
    - https://art-and-decor.com
    - https://www.art-and-decor.com
  allow-credentials: true
  https-only: true
```

---

## Endpoint Security Summary

### Public Access (No Authentication)
- Authentication endpoints (`/api/auth/**`)
- Static resources
- Public content APIs
- Username/email availability checks

### Authenticated Access (Valid JWT Required)
- User search by name
- Password change (self-service)
- All other user profile operations

### Role-Based Access
- **ADMIN + MANAGER:** User management operations
- **ADMIN Only:** User status updates, deletions, password resets

### Security Headers Required
```javascript
const securityHeaders = {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer <token>',
};
```

This guide provides essential JWT security configuration information for client developers to properly integrate with the Art & Decor platform's authentication system.