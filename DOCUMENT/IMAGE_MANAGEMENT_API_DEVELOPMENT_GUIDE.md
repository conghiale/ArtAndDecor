# IMAGE MANAGEMENT API DEVELOPMENT GUIDE

**Project:** Art & Decor E-commerce Platform  
**Date:** February 3, 2026  
**Author:** Development Team  
**Version:** 1.0  
**Features:** Complete Image Management APIs with Batch Upload, Update, and File Hashing  

---

## Overview

The Image Management API provides comprehensive functionality for handling image uploads, downloads, updates, and retrieval. All uploaded images are processed with:

- **File Security:** SHA-256 hashing of file content + timestamp for unique filenames
- **Image Analysis:** Automatic dimension detection (width x height)
- **Batch Processing:** Support for uploading multiple images in a single request
- **Error Handling:** Detailed error reporting for failed uploads with recovery information

---

## Image File Storage Strategy

### File Naming Convention
- **Format:** `{SHA256_HASH}{TIMESTAMP}.{EXTENSION}`
- **Example:** `a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6.jpg`
- **Hash Source:** SHA-256(file_content + current_timestamp_milliseconds)
- **Purpose:** Ensures unique filenames and prevents collisions

### Image Metadata Storage
- **IMAGE_NAME:** Hashed filename (a1b2c3d4e5f6...jpg)
- **IMAGE_DISPLAY_NAME:** User-friendly name (e.g., "Product Photo")
- **IMAGE_SLUG:** URL-friendly identifier (e.g., "product-photo")
- **IMAGE_SIZE:** Dimensions in format "widthxheight" (e.g., "2048x1024")
- **IMAGE_REMARK_EN:** English description/remarks (optional)
- **IMAGE_REMARK:** Vietnamese description/remarks (optional)

### Storage Location
- **Configuration:** Defined in Policy table (STORAGE_PATH)
- **Default Fallback:** `{user.dir}/uploads/images/`
- **File Organization:** Flat directory structure with hashed filenames

---

## Image Constraints & Validation

### File Size Limits
| Type | Maximum Size | Description |
|------|-------------|-------------|
| Single Image | 50 MB | Per file size limit |
| Batch Upload | 50 MB × N files | Each file individually limited |

### Supported Image Formats
- **JPEG** (.jpg, .jpeg)
- **PNG** (.png)
- **GIF** (.gif)
- **BMP** (.bmp)
- **WebP** (.webp)
- **TIFF** (.tiff, .tif)

**Validation:** Checked via MIME type (must start with "image/")

### Image Dimension Requirements
- **Minimum:** 100 x 100 pixels
- **Maximum:** No strict limit (OS/system dependent)
- **Recommended:** 
  - Thumbnails: 200 x 200 pixels
  - Product Images: 1024 x 1024 pixels
  - Banner Images: 1920 x 1080 pixels or higher

---

## API Endpoints

### 1. Get Image by Slug
**Endpoint:** `GET /images/slug/{imageSlug}`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**Description:** Retrieve image metadata by URL-friendly slug (customer-friendly endpoint)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `imageSlug` | String | Yes | Path | URL-friendly image identifier (e.g., "product-photo") |

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `imageId` | Long | Unique image identifier |
| `imageName` | String | Hashed filename stored on disk |
| `imageDisplayName` | String | User-friendly display name |
| `imageSlug` | String | URL-friendly slug identifier |
| `imageSize` | String | Image dimensions (widthxheight format) |
| `imageRemarkEn` | String | English description/remarks |
| `imageRemark` | String | Vietnamese description/remarks |
| `createdDt` | String | Image upload timestamp |
| `modifiedDt` | String | Last modification timestamp |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/images/slug/premium-sofa-photo" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Image retrieved successfully",
  "data": {
    "imageId": 1,
    "imageName": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6.jpg",
    "imageDisplayName": "Premium Sofa Photo",
    "imageSlug": "premium-sofa-photo",
    "imageSize": "2048x1024",
    "imageRemarkEn": "High-quality product photograph",
    "imageRemark": "Ảnh chất lượng cao của sản phẩm",
    "createdDt": "2026-02-03 10:30:45",
    "modifiedDt": "2026-02-03 10:30:45"
  },
  "timestamp": "2026-02-03 10:35:20"
}
```

**Example Response (Not Found - 404):**
```json
{
  "code": 404,
  "message": "Image not found with slug: invalid-slug",
  "data": null,
  "timestamp": "2026-02-03 10:35:20"
}
```

---

### 2. Get Image by ID
**Endpoint:** `GET /images/{imageId}`  
**Method:** GET  
**Access:** PUBLIC (No authentication required)  
**Description:** Retrieve image metadata by image ID (admin/system reference)

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `imageId` | Long | Yes | Path | Unique image identifier in database |

**Response Fields:** Same as Get Image by Slug

**Example Request:**
```bash
curl -X GET "http://localhost:8080/images/1" \
  -H "Content-Type: application/json"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Image retrieved successfully",
  "data": {
    "imageId": 1,
    "imageName": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6.jpg",
    "imageDisplayName": "Premium Sofa Photo",
    "imageSlug": "premium-sofa-photo",
    "imageSize": "2048x1024",
    "imageRemarkEn": "High-quality product photograph",
    "imageRemark": "Ảnh chất lượng cao của sản phẩm",
    "createdDt": "2026-02-03 10:30:45",
    "modifiedDt": "2026-02-03 10:30:45"
  },
  "timestamp": "2026-02-03 10:35:20"
}
```

**Example Response (Not Found - 404):**
```json
{
  "code": 404,
  "message": "Image not found with ID: 999",
  "data": null,
  "timestamp": "2026-02-03 10:35:20"
}
```

---

### 3. Upload Multiple Images (Batch)
**Endpoint:** `POST /images/upload`  
**Method:** POST  
**Content-Type:** `multipart/form-data`  
**Access:** PUBLIC (No authentication required)  
**Description:** Upload multiple images with optional metadata (batch processing)

**Request Parameters (Form Data):**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `imageFiles` | File[] | Yes | Array of image files to upload |
| `imageDisplayNames` | String[] | No | Display names for each image (parallel array) |
| `imageSizes` | String[] | No | Custom image sizes (e.g., "1024x768") - if not provided, auto-detected |
| `imageRemarksEn` | String[] | No | English remarks for each image (parallel array) |
| `imageRemarks` | String[] | No | Vietnamese remarks for each image (parallel array) |
| `imageSlugs` | String[] | No | Custom slugs for each image - if not provided, auto-generated from displayName |

**Parallel Array Processing Notes:**
- All optional string arrays should have length matching `imageFiles` length
- Elements at index `i` correspond to image file at index `i`
- If array is shorter than file count, missing elements are treated as empty/null
- If array is longer, extra elements are ignored

**Response Structure:**

| Field | Type | Description |
|-------|------|-------------|
| `uploadedImages` | ImageDto[] | Array of successfully uploaded images |
| `failedImages` | ImageUploadErrorDto[] | Array of failed uploads with error details |
| `successCount` | Integer | Count of successfully uploaded images |
| `failureCount` | Integer | Count of failed uploads |
| `success` | Boolean | `true` if all uploads succeeded, `false` if any failed |
| `uploadedAt` | String | Batch upload timestamp |
| `message` | String | Summary message (e.g., "Successfully uploaded 3 image(s), 1 failed") |

**ImageUploadErrorDto Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `fileIndex` | Integer | Index of the failed file in the upload array (0-based) |
| `displayName` | String | Display name provided for the failed file |
| `originalFilename` | String | Original filename from the uploaded file |
| `errorMessage` | String | Detailed error message explaining the failure |
| `errorCode` | String | Machine-readable error code (e.g., "UPLOAD_FAILED") |

**Validation Rules:**

| Rule | Condition | Error |
|------|-----------|-------|
| **Required Files** | At least 1 file must be provided | 400 Bad Request |
| **File Empty** | Each file must not be empty | File-specific error in failedImages |
| **File Size** | Maximum 50 MB per file | File-specific error |
| **MIME Type** | Must start with "image/" | File-specific error |
| **File Type** | Must be supported image format | File-specific error |

**Example Request (cURL with 3 images):**
```bash
curl -X POST "http://localhost:8080/images/upload" \
  -H "Accept: application/json" \
  -F "imageFiles=@photo1.jpg" \
  -F "imageFiles=@photo2.png" \
  -F "imageFiles=@photo3.jpg" \
  -F "imageDisplayNames=Sofa Front View" \
  -F "imageDisplayNames=Sofa Side View" \
  -F "imageDisplayNames=Sofa Detail" \
  -F "imageSizes=2048x1024" \
  -F "imageSizes=1920x1080" \
  -F "imageRemarksEn=Front angle of premium sofa" \
  -F "imageRemarksEn=Side angle showing depth" \
  -F "imageRemarks=Góc nhìn phía trước" \
  -F "imageRemarks=Góc nhìn bên cạnh"
```

**Example Request (JavaScript/Fetch):**
```javascript
const formData = new FormData();

// Add image files
formData.append('imageFiles', document.getElementById('file1').files[0]);
formData.append('imageFiles', document.getElementById('file2').files[0]);
formData.append('imageFiles', document.getElementById('file3').files[0]);

// Add display names (parallel array)
formData.append('imageDisplayNames', 'Sofa Front View');
formData.append('imageDisplayNames', 'Sofa Side View');
formData.append('imageDisplayNames', 'Sofa Detail');

// Add custom sizes (optional)
formData.append('imageSizes', '2048x1024');
formData.append('imageSizes', '1920x1080');

// Add remarks
formData.append('imageRemarksEn', 'Front angle of premium sofa');
formData.append('imageRemarksEn', 'Side angle showing depth');

formData.append('imageRemarks', 'Góc nhìn phía trước');
formData.append('imageRemarks', 'Góc nhìn bên cạnh');

fetch('http://localhost:8080/images/upload', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => console.log(data));
```

**Example Response (All Success - 200):**
```json
{
  "code": 200,
  "message": "Upload completed: 3 succeeded, 0 failed",
  "data": {
    "uploadedImages": [
      {
        "imageId": 5,
        "imageName": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6.jpg",
        "imageDisplayName": "Sofa Front View",
        "imageSlug": "sofa-front-view",
        "imageSize": "2048x1024",
        "imageRemarkEn": "Front angle of premium sofa",
        "imageRemark": "Góc nhìn phía trước",
        "createdDt": "2026-02-03 10:45:30",
        "modifiedDt": "2026-02-03 10:45:30"
      },
      {
        "imageId": 6,
        "imageName": "b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7.png",
        "imageDisplayName": "Sofa Side View",
        "imageSlug": "sofa-side-view",
        "imageSize": "1920x1080",
        "imageRemarkEn": "Side angle showing depth",
        "imageRemark": "Góc nhìn bên cạnh",
        "createdDt": "2026-02-03 10:45:30",
        "modifiedDt": "2026-02-03 10:45:30"
      },
      {
        "imageId": 7,
        "imageName": "c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8.jpg",
        "imageDisplayName": "Sofa Detail",
        "imageSlug": "sofa-detail",
        "imageSize": "1024x1024",
        "imageRemarkEn": null,
        "imageRemark": null,
        "createdDt": "2026-02-03 10:45:30",
        "modifiedDt": "2026-02-03 10:45:30"
      }
    ],
    "failedImages": [],
    "successCount": 3,
    "failureCount": 0,
    "success": true,
    "uploadedAt": "2026-02-03 10:45:30"
  },
  "timestamp": "2026-02-03 10:45:35"
}
```

**Example Response (Partial Success - 200):**
```json
{
  "code": 200,
  "message": "Upload completed: 2 succeeded, 1 failed",
  "data": {
    "uploadedImages": [
      {
        "imageId": 8,
        "imageName": "d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9.jpg",
        "imageDisplayName": "Chair Front",
        "imageSlug": "chair-front",
        "imageSize": "1920x1080",
        "imageRemarkEn": "Front view",
        "imageRemark": "Góc phía trước",
        "createdDt": "2026-02-03 11:00:15",
        "modifiedDt": "2026-02-03 11:00:15"
      },
      {
        "imageId": 9,
        "imageName": "e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0.png",
        "imageDisplayName": "Chair Side",
        "imageSlug": "chair-side",
        "imageSize": "1024x768",
        "imageRemarkEn": "Side view",
        "imageRemark": "Góc bên cạnh",
        "createdDt": "2026-02-03 11:00:15",
        "modifiedDt": "2026-02-03 11:00:15"
      }
    ],
    "failedImages": [
      {
        "fileIndex": 2,
        "displayName": "Chair Back",
        "originalFilename": "chair_back.txt",
        "errorMessage": "Invalid file type. Only image files are allowed: text/plain",
        "errorCode": "UPLOAD_FAILED"
      }
    ],
    "successCount": 2,
    "failureCount": 1,
    "success": false,
    "uploadedAt": "2026-02-03 11:00:15"
  },
  "timestamp": "2026-02-03 11:00:20"
}
```

**Example Response (No Files Provided - 400):**
```json
{
  "code": 400,
  "message": "No image files provided",
  "data": null,
  "timestamp": "2026-02-03 11:05:00"
}
```

---

### 4. Update Image with New File
**Endpoint:** `POST /images/{imageId}/upload`  
**Method:** POST  
**Content-Type:** `multipart/form-data`  
**Access:** PUBLIC (No authentication required)  
**Description:** Replace existing image file and optionally update metadata

**Request Parameters:**

| Parameter | Type | Required | Location | Description |
|-----------|------|----------|----------|-------------|
| `imageId` | Long | Yes | Path | Image ID to update |
| `imageFiles` | File | Yes | Form | New image file (first file used if multiple provided) |
| `imageDisplayNames` | String | No | Form | New display name for the image |

**Behavior:**
- Old file is deleted from disk after successful upload
- New file hash is generated with current timestamp
- Image dimensions are automatically re-detected
- All other metadata remains unchanged unless specifically updated
- If displayName not provided, extracted from new filename

**Response Fields:** Same as ImageDto

**Example Request (cURL):**
```bash
curl -X POST "http://localhost:8080/images/5/upload" \
  -H "Accept: application/json" \
  -F "imageFiles=@new_sofa_photo.jpg" \
  -F "imageDisplayNames=Updated Sofa Front View"
```

**Example Request (JavaScript/Fetch):**
```javascript
const formData = new FormData();
formData.append('imageFiles', document.getElementById('imageFile').files[0]);
formData.append('imageDisplayNames', 'Updated Product Photo');

fetch('http://localhost:8080/images/5/upload', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => console.log(data));
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Image file updated successfully",
  "data": {
    "imageId": 5,
    "imageName": "f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1.jpg",
    "imageDisplayName": "Updated Sofa Front View",
    "imageSlug": "sofa-front-view",
    "imageSize": "1920x1440",
    "imageRemarkEn": "Front angle of premium sofa",
    "imageRemark": "Góc nhìn phía trước",
    "createdDt": "2026-02-03 10:45:30",
    "modifiedDt": "2026-02-03 11:15:45"
  },
  "timestamp": "2026-02-03 11:15:50"
}
```

**Example Response (File Empty - 400):**
```json
{
  "code": 400,
  "message": "Image file cannot be empty",
  "data": null,
  "timestamp": "2026-02-03 11:20:00"
}
```

**Example Response (Image Not Found - 404):**
```json
{
  "code": 404,
  "message": "Image not found with ID: 999",
  "data": null,
  "timestamp": "2026-02-03 11:20:00"
}
```

---

### 5. Get Total Image Count (Admin Dashboard)
**Endpoint:** `GET /images/admin/total-count`  
**Method:** GET  
**Access:** ADMIN, MANAGER (Role-based access control)  
**Description:** Get total count of images in the system (for dashboard/analytics)

**Security Requirements:**
- JWT authentication required
- User must have ADMIN or MANAGER role
- Other roles will receive 403 Forbidden response

**Response Type:** Long (single value)

**Example Request:**
```bash
curl -X GET "http://localhost:8080/images/admin/total-count" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {jwt_token}"
```

**Example Response (Success - 200):**
```json
{
  "code": 200,
  "message": "Total count retrieved successfully",
  "data": 1256,
  "timestamp": "2026-02-03 11:25:30"
}
```

**Example Response (No Authentication - 401):**
```json
{
  "code": 401,
  "message": "Unauthorized - JWT token missing or invalid",
  "data": null,
  "timestamp": "2026-02-03 11:25:30"
}
```

**Example Response (Insufficient Permission - 403):**
```json
{
  "code": 403,
  "message": "Forbidden - User does not have required role (ADMIN or MANAGER)",
  "data": null,
  "timestamp": "2026-02-03 11:25:30"
}
```

---

## Image File Processing Flow

### Upload Processing Steps

```
1. File Reception
   ├─ Validate file exists and not empty
   ├─ Check file size (≤ 50 MB)
   └─ Verify MIME type starts with "image/"

2. File Analysis
   ├─ Generate SHA-256 hash (file_content + timestamp_millis)
   ├─ Extract file extension from original filename
   └─ Determine image dimensions (width x height)

3. Slug Generation
   ├─ Use provided slug OR
   ├─ Generate from displayName using Utils.generateSlug()
   │  ├─ Normalize Vietnamese characters (remove diacritics)
   │  ├─ Convert to lowercase
   │  ├─ Replace spaces/special chars with hyphens
   │  └─ Trim trailing/leading hyphens
   └─ Ensure uniqueness if needed

4. File Storage
   ├─ Get storage path from Policy table (STORAGE_PATH)
   ├─ Create directories if not exist
   ├─ Save file with hashed filename
   └─ Log successful storage

5. Database Persistence
   ├─ Create Image entity with all metadata
   ├─ Save to IMAGE table
   ├─ Return ImageDto to client
   └─ Log transaction details

6. Error Handling
   ├─ Catch any exception during process
   ├─ Add error details to failedImages array
   ├─ Continue processing remaining files (batch mode)
   └─ Return partial success response with error details
```

### Slug Generation Algorithm

```
Input: "Premium Sofa Photo"
↓
1. Normalize vietnamese characters
   "Premium Sofa Photo" (no diacritics to remove)
↓
2. Convert to lowercase
   "premium sofa photo"
↓
3. Replace spaces and special chars with hyphens
   "premium-sofa-photo"
↓
4. Trim trailing/leading hyphens
   "premium-sofa-photo"
↓
Output: "premium-sofa-photo"

Example 2:
Input: "Ghế Văn Phòng (Premium)"
↓
After normalize: "Ghe Van Phong (Premium)"
↓
After lowercase: "ghe van phong (premium)"
↓
After replace: "ghe-van-phong-premium"
↓
After trim: "ghe-van-phong-premium"
↓
Output: "ghe-van-phong-premium"
```

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

**Error Response Codes:**
- `400` - Bad Request (validation error, no files provided, empty file)
- `401` - Unauthorized (authentication required for protected endpoints)
- `403` - Forbidden (insufficient permissions/roles)
- `404` - Not Found (image not found by ID or slug)
- `500` - Internal Server Error (file operation failed, database error)

---

## ImageDto Fields

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `imageId` | Long | Unique image identifier | 5 |
| `imageName` | String | Hashed filename stored on disk | "a1b2c3d4e5f6...jpg" |
| `imageDisplayName` | String | User-friendly display name | "Premium Sofa Photo" |
| `imageSlug` | String | URL-friendly slug identifier | "premium-sofa-photo" |
| `imageSize` | String | Image dimensions (widthxheight) | "2048x1024" |
| `imageRemarkEn` | String | English description/remarks | "High-quality product photo" |
| `imageRemark` | String | Vietnamese description/remarks | "Ảnh chất lượng cao" |
| `createdDt` | String | Image upload timestamp | "2026-02-03 10:30:45" |
| `modifiedDt` | String | Last modification timestamp | "2026-02-03 10:30:45" |

---

## Database Schema Reference

**Primary Tables:**

| Table | Description | Key Fields |
|-------|-------------|------------|
| `IMAGE` | Core image metadata | IMAGE_ID, IMAGE_NAME, IMAGE_DISPLAY_NAME, IMAGE_SLUG, IMAGE_SIZE |
| `POLICY` | System configuration | POLICY_ID, POLICY_NAME, POLICY_VALUE |

**IMAGE Table Fields:**

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `IMAGE_ID` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique image identifier |
| `IMAGE_NAME` | VARCHAR(255) | NOT NULL, UNIQUE | Hashed filename (hash + timestamp + ext) |
| `IMAGE_DISPLAY_NAME` | VARCHAR(255) | NOT NULL | User-friendly display name |
| `IMAGE_SLUG` | VARCHAR(255) | NOT NULL, UNIQUE | URL-friendly identifier |
| `IMAGE_SIZE` | VARCHAR(50) | NOT NULL | Dimensions format: "widthxheight" |
| `IMAGE_REMARK_EN` | LONGTEXT | NULL | English description/remarks |
| `IMAGE_REMARK` | LONGTEXT | NULL | Vietnamese description/remarks |
| `CREATED_DT` | DATETIME | DEFAULT CURRENT_TIMESTAMP | Upload timestamp |
| `MODIFIED_DT` | DATETIME | DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP | Last update timestamp |

**POLICY Table (Storage Configuration):**

| POLICY_NAME | POLICY_VALUE | Description |
|-------------|-------------|-------------|
| `STORAGE_PATH` | `/path/to/uploads/images` | Directory where image files are stored |

**Default Fallback:** If STORAGE_PATH not found in Policy, uses `{user.dir}/uploads/images/`

---

## File Hashing Implementation Details

### SHA-256 Hashing with Timestamp

**Purpose:** Generate unique, non-sequential filenames to prevent file collision and security issues

**Algorithm:**
```
1. Read file content as bytes: fileContentBytes
2. Get current timestamp: System.currentTimeMillis()
3. Convert timestamp to string: "1707019245000"
4. Hash: SHA256(fileContentBytes + timestampString.getBytes())
5. Convert hash to hex format: "a1b2c3d4e5f6g7h8..."
6. Append extension: "a1b2c3d4e5f6g7h8...jpg"
```

**Example:**
```
File: photo.jpg (50 KB)
Timestamp: 1707019245000 (Feb 3, 2026 10:30:45 UTC)
Hash Input: {file_bytes} + "1707019245000".getBytes()
Hash Output: "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"
Final Filename: "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6.jpg"
```

**Advantages:**
- ✅ **Uniqueness:** Different files generate different hashes; same file uploaded at different times gets different hashes
- ✅ **Security:** Hashed names prevent direct file access by original filename
- ✅ **Collision Prevention:** Timestamp ensures near-zero collision probability
- ✅ **Integrity:** File corruption can be detected by re-hashing
- ✅ **Immutability:** Once stored, filename cannot be guessed or predicted

**Limitations:**
- Cannot restore original filename from stored hash
- Requires maintaining IMAGE_DISPLAY_NAME in database for user reference
- Two identical files uploaded at different times will have different hashes (by design)

---

## Image Dimension Detection

### Automatic Dimension Reading

**Method:** Using Java ImageIO library to read image metadata

**Supported Formats:**
- JPEG (.jpg, .jpeg)
- PNG (.png)
- GIF (.gif)
- BMP (.bmp)
- WebP (.webp)
- TIFF (.tiff, .tif)

**Storage Format:** `"{width}x{height}"` (e.g., "2048x1024")

**Example Dimensions:**
| Image | Dimensions | Use Case |
|-------|-----------|----------|
| Thumbnail | 200x200 | List view thumbnails |
| Square Product | 500x500 | Category/grid view |
| Hero Banner | 1920x1080 | Homepage banner |
| Full Resolution | 3000x2000+ | Original product photo |
| Landscape | 1920x1440 | Landscape products |
| Portrait | 1080x1440 | Portrait products |

**Fallback Behavior:**
- If dimensions cannot be read → stored as "unknown"
- Happens when: unsupported format, corrupted file, or ImageIO errors
- Client should handle "unknown" gracefully in frontend

---

## Error Handling Strategy

### Upload Error Types

| Error Type | Status Code | Example Message | Cause |
|------------|------------|-----------------|-------|
| **No Files** | 400 | "No image files provided" | Missing imageFiles parameter |
| **Empty File** | 400 | "File at index 0 is empty" | File upload error/empty file |
| **File Too Large** | 400 | "File size exceeds maximum limit: 52428800" | File > 50 MB |
| **Invalid Type** | 400 | "Invalid file type. Only image files are allowed: text/plain" | MIME type not "image/*" |
| **Unsupported Format** | 400 | "Unable to read image: Unsupported image type" | Format not recognized |
| **Storage Error** | 500 | "Failed to upload image: IOException details" | Disk write error |
| **DB Error** | 500 | "Database transaction failed" | Insert/update failed |

### Batch Upload Error Recovery

When uploading multiple files:
- If file #1 fails → continue with file #2, #3, etc. (fault tolerance)
- Return success for valid files + error details for failed ones
- Client receives complete report of what succeeded and what failed
- No transaction rollback on partial failures (atomic per file)

---

## Performance Considerations

### Optimization Tips

1. **Image Compression**
   - Consider compressing images before upload on client side
   - Reduces storage and bandwidth usage
   - Trade-off: quality vs. file size

2. **Caching**
   - Store hashed filenames in cache for slug lookups
   - Cache frequently accessed images
   - Invalidate cache on update

3. **Async Processing**
   - For large batches, consider async upload queues
   - Process dimensions detection asynchronously
   - Notify client via webhook/polling

4. **CDN Integration**
   - Serve images through CDN for global distribution
   - Store original in primary storage, cache in CDN
   - Implement cache invalidation strategy

5. **Database Indexing**
   - Index `IMAGE_SLUG` for fast slug lookups
   - Index `IMAGE_NAME` for duplicate detection
   - Index `CREATED_DT` for sorting/filtering

---

## Security Considerations

### File Security

1. **MIME Type Validation**
   - Only accept files with MIME type starting with "image/"
   - Check both upload content-type and file header

2. **File Size Limits**
   - Maximum 50 MB per file
   - Prevent DOS attacks via large files
   - Configurable via application.properties

3. **Filename Hashing**
   - Never expose original filename in storage path
   - Use SHA-256 hash + timestamp for uniqueness
   - Prevents direct file enumeration attacks

4. **Access Control**
   - Public endpoints: GET /images endpoints (read-only)
   - Protected endpoints: Total count requires ADMIN/MANAGER role
   - Private storage: Files served via Java app, not direct HTTP

### Best Practices

- Never store executable files or scripts
- Validate file content (magic bytes) in addition to MIME type
- Implement rate limiting for upload endpoints
- Log all upload/delete operations for audit trail
- Regularly scan uploads for malware
- Implement image thumbnail generation for display

---

## Integration Examples

### HTML Form Upload
```html
<form action="http://localhost:8080/images/upload" method="POST" enctype="multipart/form-data">
  <!-- Image files -->
  <input type="file" name="imageFiles" multiple required accept="image/*">
  
  <!-- Display names -->
  <input type="text" name="imageDisplayNames" placeholder="Display Name 1">
  <input type="text" name="imageDisplayNames" placeholder="Display Name 2">
  
  <!-- Remarks (optional) -->
  <textarea name="imageRemarksEn" placeholder="English remarks"></textarea>
  <textarea name="imageRemarks" placeholder="Vietnamese remarks"></textarea>
  
  <button type="submit">Upload Images</button>
</form>

<script>
  document.querySelector('form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const formData = new FormData(this);
    const response = await fetch(this.action, {
      method: this.method,
      body: formData
    });
    
    const result = await response.json();
    console.log('Upload result:', result);
    
    if (result.success) {
      alert(`Uploaded ${result.data.successCount} images successfully!`);
    } else if (result.data.failureCount > 0) {
      alert(`Uploaded ${result.data.successCount}, failed ${result.data.failureCount}`);
      console.log('Failed images:', result.data.failedImages);
    }
  });
</script>
```

### React Component Example
```jsx
import React, { useState } from 'react';

function ImageUpload() {
  const [files, setFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [result, setResult] = useState(null);

  const handleFileChange = (e) => {
    setFiles(Array.from(e.target.files));
  };

  const handleUpload = async () => {
    if (!files.length) {
      alert('Please select image files');
      return;
    }

    setUploading(true);
    const formData = new FormData();
    
    files.forEach((file) => {
      formData.append('imageFiles', file);
      formData.append('imageDisplayNames', file.name.replace(/\.[^/.]+$/, ''));
    });

    try {
      const response = await fetch('http://localhost:8080/images/upload', {
        method: 'POST',
        body: formData
      });
      
      const data = await response.json();
      setResult(data);
      
      if (data.success) {
        alert(`Successfully uploaded ${data.data.successCount} images!`);
        setFiles([]);
      }
    } catch (error) {
      console.error('Upload error:', error);
      alert('Upload failed: ' + error.message);
    } finally {
      setUploading(false);
    }
  };

  return (
    <div>
      <input 
        type="file" 
        multiple 
        accept="image/*" 
        onChange={handleFileChange}
        disabled={uploading}
      />
      <button onClick={handleUpload} disabled={uploading}>
        {uploading ? 'Uploading...' : 'Upload Images'}
      </button>
      {result && (
        <div>
          <p>Success: {result.data.successCount}, Failed: {result.data.failureCount}</p>
          {result.data.failedImages.map((err, idx) => (
            <p key={idx}>Error: {err.originalFilename} - {err.errorMessage}</p>
          ))}
        </div>
      )}
    </div>
  );
}

export default ImageUpload;
```

---

## Troubleshooting Guide

### Common Issues

| Issue | Cause | Solution |
|-------|-------|----------|
| **404 Storage Path Not Found** | STORAGE_PATH not in Policy table | Add STORAGE_PATH policy to database |
| **File Not Saved** | Permission denied on storage directory | Check directory permissions (755 or higher) |
| **Dimension Returns "unknown"** | Unsupported image format | Use JPEG, PNG, or other standard formats |
| **Slug Already Exists** | Two images with same display name | Ensure unique display names or provide custom slugs |
| **Upload Timeout** | Large file + slow connection | Implement chunked upload or increase timeout |
| **Memory Error (OutOfMemory)** | Very large image file | Reduce image size or increase JVM heap |

### Debug Logging

Check application logs for detailed error information:
```
logger.debug() - File hashing, dimension reading
logger.info() - Upload success, update success
logger.warn() - File deletion issues, fallback paths
logger.error() - Upload failures, database errors
```

Enable debug logging:
```properties
logging.level.org.ArtAndDecor.services.impl.ImageFileServiceImpl=DEBUG
logging.level.org.ArtAndDecor.services.impl.ImageServiceImpl=DEBUG
```

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-02-03 | Initial release with 5 API endpoints, batch upload support, automatic dimension detection |

---

## Future Enhancements (Planned)

- [ ] Image resizing/thumbnailing API
- [ ] Image filtering (crop, rotate, etc.)
- [ ] Bulk image deletion API
- [ ] Image search/filtering capabilities
- [ ] Image tagging system
- [ ] Image usage analytics
- [ ] Direct image download endpoint
- [ ] Image version history
- [ ] CDN integration for global distribution
- [ ] Image optimization/compression service
