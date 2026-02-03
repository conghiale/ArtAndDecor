# AUTHENTICATION API DEVELOPMENT GUIDE

**Project:** Art & Decor E-commerce Platform  
**Date:** January 27, 2026  
**Author:** Development Team  
**Version:** 1.0  
**Features:** Complete JWT Authentication APIs with Registration, Login & Token Management  

---

## Authentication Overview

This guide covers all authentication-related APIs for the Art & Decor platform. The system uses JWT (JSON Web Tokens) for stateless authentication with access tokens and refresh tokens.

### Authentication Flow

1. **Register:** Create new account → Get access + refresh tokens
2. **Login:** Authenticate user → Get access + refresh tokens  
3. **API Calls:** Use access token in Authorization header
4. **Token Refresh:** Use refresh token to get new access token
5. **Health Check:** Monitor authentication service status

---

## API Endpoints

### 1. User Registration
**Endpoint:** `POST /api/auth/register`  
**Method:** POST  
**Access:** PUBLIC (no authentication required)  
**Description:** Register new user account with immediate authentication

**Request Body (JSON):**

| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| `userName` | String | Yes | 3-20 chars, alphanumeric + underscore | Unique username |
| `email` | String | Yes | Valid email format | Unique email address |
| `password` | String | Yes | Min 8 chars, complexity rules | User password |
| `firstName` | String | Yes | 1-50 chars | User first name |
| `lastName` | String | Yes | 1-50 chars | User last name |
| `phoneNumber` | String | No | Valid phone format | Contact number |

**Example Request:**
```http
POST /api/auth/register
Host: localhost:8080
Content-Type: application/json

{
  "userName": "newcustomer01",
  "email": "newcustomer@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "0901234567"
}
```

**Example Success Response:**
```json
{
  "code": 200,
  "message": "User registered successfully",
  "data": {
    "success": true,
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJuZXdjdXN0b21lcjAxIiwiaWF0IjoxNzM3OTgzNDAwLCJleHAiOjE3Mzc5ODcwMDB9.XYZ123...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJuZXdjdXN0b21lcjAxIiwiaWF0IjoxNzM3OTgzNDAwLCJleHAiOjE3NDA1NzU0MDB9.ABC456...",
    "tokenType": "Bearer",
    "user": {
      "userId": 26,
      "userName": "newcustomer01",
      "email": "newcustomer@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "phoneNumber": "0901234567",
      "userRoleId": 4,
      "userRoleName": "CUSTOMER",
      "userEnabled": true
    },
    "message": "Registration successful"
  },
  "timestamp": "2026-01-27 10:30:00"
}
```

**Example Error Response:**
```json
{
  "code": 400,
  "message": "Username already exists",
  "data": null,
  "timestamp": "2026-01-27 10:30:00"
}
```

**Validation Rules:**
- Username: 3-20 characters, alphanumeric + underscore only
- Email: Valid email format, unique in system
- Password: Minimum 8 characters with complexity requirements
- Names: 1-50 characters, no special characters

---

### 2. User Login
**Endpoint:** `POST /api/auth/login`  
**Method:** POST  
**Access:** PUBLIC (no authentication required)  
**Description:** Authenticate user credentials and get JWT tokens

**Request Body (JSON):**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `usernameOrEmail` | String | Yes | Username or email address |
| `password` | String | Yes | User password |

**Example Request:**
```http
POST /api/auth/login
Host: localhost:8080
Content-Type: application/json

{
  "usernameOrEmail": "customer01",
  "password": "userPassword123"
}
```

**Example Success Response:**
```json
{
  "code": 200,
  "message": "Authentication successful",
  "data": {
    "success": true,
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjdXN0b21lcjAxIiwiaWF0IjoxNzM3OTgzNDAwLCJleHAiOjE3Mzc5ODcwMDB9.TOKEN123...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjdXN0b21lcjAxIiwiaWF0IjoxNzM3OTgzNDAwLCJleHAiOjE3NDA1NzU0MDB9.REFRESH456...",
    "tokenType": "Bearer",
    "user": {
      "userId": 4,
      "userName": "customer01",
      "email": "customer1@gmail.com",
      "firstName": "Nguyen",
      "lastName": "Van A",
      "phoneNumber": "0904567890",
      "userRoleId": 4,
      "userRoleName": "CUSTOMER",
      "userEnabled": true
    },
    "message": "Login successful"
  },
  "timestamp": "2026-01-27 10:45:00"
}
```

**Example Error Responses:**
```json
{
  "code": 400,
  "message": "Invalid username or password",
  "data": null,
  "timestamp": "2026-01-27 10:45:00"
}
```

```json
{
  "code": 400,
  "message": "Account is disabled. Please contact administrator.",
  "data": null,
  "timestamp": "2026-01-27 10:45:00"
}
```

**Login Features:**
- Accepts both username and email as login identifier
- Updates lastLoginDt on successful authentication
- Returns user profile information with tokens
- Validates account enabled status

---

### 3. Alternative Login Endpoint
**Endpoint:** `POST /api/auth/authenticate`  
**Method:** POST  
**Access:** PUBLIC (no authentication required)  
**Description:** Alternative authentication endpoint (same functionality as /login)

**Request Body:** Same as `/api/auth/login`  
**Response:** Same as `/api/auth/login`

**Example Request:**
```http
POST /api/auth/authenticate
Host: localhost:8080
Content-Type: application/json

{
  "usernameOrEmail": "customer01",
  "password": "userPassword123"
}
```

---

### 4. Token Refresh
**Endpoint:** `POST /api/auth/refresh`  
**Method:** POST  
**Access:** PUBLIC (requires refresh token)  
**Description:** Get new access token using refresh token

**Request Body (JSON):**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `refreshToken` | String | Yes | Valid refresh token from login/register |

**Example Request:**
```http
POST /api/auth/refresh
Host: localhost:8080
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjdXN0b21lcjAxIiwiaWF0IjoxNzM3OTgzNDAwLCJleHAiOjE3NDA1NzU0MDB9.REFRESH456..."
}
```

**Example Success Response:**
```json
{
  "code": 200,
  "message": "Token refreshed successfully",
  "data": {
    "success": true,
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjdXN0b21lcjAxIiwiaWF0IjoxNzM3OTg3MDAwLCJleHAiOjE3Mzc5OTA2MDB9.NEWTOKEN789...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjdXN0b21lcjAxIiwiaWF0IjoxNzM3OTg3MDAwLCJleHAiOjE3NDA1NzkwMDB9.NEWREFRESH012...",
    "tokenType": "Bearer",
    "user": {
      "userId": 4,
      "userName": "customer01",
      "email": "customer1@gmail.com",
      "firstName": "Nguyen",
      "lastName": "Van A",
      "userRoleId": 4,
      "userRoleName": "CUSTOMER",
      "userEnabled": true
    },
    "message": "Token refresh successful"
  },
  "timestamp": "2026-01-27 10:50:00"
}
```

**Example Error Response:**
```json
{
  "code": 400,
  "message": "Invalid or expired refresh token",
  "data": null,
  "timestamp": "2026-01-27 10:50:00"
}
```

**Refresh Token Notes:**
- Both access and refresh tokens are renewed on each refresh
- Old refresh token becomes invalid after successful refresh
- Refresh tokens expire in 30 days
- Invalid tokens return 400 Bad Request

---

### 5. Alternative Token Refresh
**Endpoint:** `POST /api/auth/get_token_pair`  
**Method:** POST  
**Access:** PUBLIC (requires refresh token)  
**Description:** Alternative token refresh endpoint (same functionality as /refresh)

**Request Body:** Same as `/api/auth/refresh`  
**Response:** Same as `/api/auth/refresh`

**Example Request:**
```http
POST /api/auth/get_token_pair
Host: localhost:8080
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 6. Health Check
**Endpoint:** `GET /api/auth/health`  
**Method:** GET  
**Access:** PUBLIC (no authentication required)  
**Description:** Check authentication service health status

**Example Request:**
```http
GET /api/auth/health
Host: localhost:8080
Content-Type: application/json
```

**Example Response:**
```json
{
  "code": 200,
  "message": "Authentication service is running",
  "data": null,
  "timestamp": "2026-01-27 10:55:00"
}
```

---

### 7. Current User Info
**Endpoint:** `GET /api/auth/me`  
**Method:** GET  
**Access:** Authenticated users only  
**Description:** Get information about authentication requirement (placeholder endpoint)

**Request Headers:**

| Header | Type | Required | Description |
|--------|------|----------|-------------|
| `Authorization` | String | Yes | Bearer <access_token> |

**Example Request:**
```http
GET /api/auth/me
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <access_token>
```

**Example Response:**
```json
{
  "code": 200,
  "message": "Authentication required to access this endpoint",
  "data": null,
  "timestamp": "2026-01-27 11:00:00"
}
```

---

## Using JWT Tokens in API Calls

### Authorization Header Format
```http
Authorization: Bearer <access_token>
```

### Example Protected API Call
```http
GET /api/users/profile
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjdXN0b21lcjAxIiwiaWF0IjoxNzM3OTgzNDAwLCJleHAiOjE3Mzc5ODcwMDB9.TOKEN123...
```

---

## Authentication Workflow Examples

### Complete Registration Flow
```javascript
// 1. Register user
POST /api/auth/register
{
  "userName": "newuser01",
  "email": "newuser@example.com", 
  "password": "SecurePass123!",
  "firstName": "New",
  "lastName": "User"
}

// 2. Store tokens from response and use for API calls
Authorization: Bearer <received_access_token>
```

### Login and API Usage Flow
```javascript
// 1. Login
POST /api/auth/login
{
  "usernameOrEmail": "customer01",
  "password": "userPassword123"
}

// 2. Use access token for API calls
GET /api/users/profile
Authorization: Bearer <access_token>

// 3. Refresh token when needed (before expiry)
POST /api/auth/refresh
{
  "refreshToken": "<refresh_token>"
}
```

---

## Error Handling

### Common Error Responses

| HTTP Status | Error Type | Description |
|-------------|------------|-------------|
| 400 | Bad Request | Invalid request format, missing fields, or validation errors |
| 401 | Unauthorized | Invalid credentials or expired token |
| 403 | Forbidden | Account disabled or insufficient permissions |
| 409 | Conflict | Username or email already exists |
| 422 | Unprocessable Entity | Validation errors |
| 500 | Internal Server Error | Server-side error |

### Validation Error Format
```json
{
  "code": 400,
  "message": "Validation failed: Username already exists",
  "data": null,
  "timestamp": "2026-01-27 11:05:00"
}
```

---

## Integration Examples

### React/JavaScript Example
```javascript
class AuthService {
    constructor() {
        this.baseURL = 'http://localhost:8080/api/auth';
    }
    
    // Login function
    async login(usernameOrEmail, password) {
        const response = await fetch(`${this.baseURL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ usernameOrEmail, password })
        });
        
        const result = await response.json();
        
        if (result.code === 200 && result.data.success) {
            // Store tokens
            sessionStorage.setItem('accessToken', result.data.accessToken);
            localStorage.setItem('refreshToken', result.data.refreshToken);
            return result.data.user;
        } else {
            throw new Error(result.message);
        }
    }
    
    // Register function
    async register(userData) {
        const response = await fetch(`${this.baseURL}/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });
        
        const result = await response.json();
        
        if (result.code === 200 && result.data.success) {
            sessionStorage.setItem('accessToken', result.data.accessToken);
            localStorage.setItem('refreshToken', result.data.refreshToken);
            return result.data.user;
        } else {
            throw new Error(result.message);
        }
    }
    
    // Refresh token function
    async refreshToken() {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) return false;
        
        try {
            const response = await fetch(`${this.baseURL}/refresh`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ refreshToken })
            });
            
            const result = await response.json();
            
            if (result.code === 200 && result.data.success) {
                sessionStorage.setItem('accessToken', result.data.accessToken);
                localStorage.setItem('refreshToken', result.data.refreshToken);
                return true;
            } else {
                this.logout();
                return false;
            }
        } catch (error) {
            this.logout();
            return false;
        }
    }
    
    // Logout function
    logout() {
        sessionStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
    }
}
```

---

## Common Response Structure

All API responses use `BaseResponseDto` format:

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `code` | Integer | Yes | HTTP status code (200=success, 400=bad request, 404=not found, 500=server error) |
| `message` | String | Yes | Human-readable response message |
| `data` | Object/null | No | Response data (AuthResponse object or null for errors) |
| `timestamp` | String | Yes | Response timestamp (yyyy-MM-dd HH:mm:ss format) |

### AuthResponse Object Structure

| Field | Type | Description |
|-------|------|-------------|
| `success` | Boolean | Authentication operation success status |
| `accessToken` | String | JWT access token (1 hour validity) |
| `refreshToken` | String | JWT refresh token (30 days validity) |
| `tokenType` | String | Token type (always "Bearer") |
| `user` | Object | User profile information |
| `message` | String | Operation result message |

This completes the Authentication API Development Guide with comprehensive examples and integration details for client developers.