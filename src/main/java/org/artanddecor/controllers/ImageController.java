package org.artanddecor.controllers;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.BaseResponseDto;
import org.artanddecor.dto.ImageDto;
import org.artanddecor.dto.ImageUploadDto;
import org.artanddecor.dto.ImageUploadResponseDto;
import org.artanddecor.services.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;

/**
 * Image Management REST Controller
 * Handles HTTP requests for image operations including upload, retrieval, and management
 * with comprehensive file processing, hashing, and metadata storage capabilities
 */
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Tag(name = "Image Management", description = "APIs for comprehensive image management including upload, retrieval, search, and file processing with SHA-256 hashing and automatic dimension detection")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    private final ImageService imageService;

     /*=============================================
     PUBLIC ENDPOINTS - Customer Image Browsing
     =============================================*/

    /**
     * Get image metadata by URL-friendly slug (Customer-friendly endpoint)
     * Public access allowed for image browsing and retrieval
     * 
     * @param imageSlug URL-friendly image identifier
     * @return Complete image metadata including dimensions, format, and file information
     */
    @GetMapping("/slug/{imageSlug}")
    @Operation(summary = "Get image by URL-friendly slug",
               description = "Retrieve detailed image metadata using the URL-friendly slug identifier. This is the primary customer-facing endpoint for image retrieval, providing complete information including file details, dimensions, format, and upload timestamps.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image retrieved successfully",
                     content = @Content(schema = @Schema(implementation = ImageDto.class))),
        @ApiResponse(responseCode = "404", description = "Image not found with provided slug"),
        @ApiResponse(responseCode = "400", description = "Invalid slug format"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<ImageDto>> getImageBySlug(
            @Parameter(description = "URL-friendly identifier for the image (e.g., 'premium-sofa-photo')",
                       example = "premium-sofa-photo")
            @PathVariable String imageSlug) {
        logger.info("Requesting image by slug: {}", imageSlug);
        try {
            Optional<ImageDto> image = imageService.findImageBySlug(imageSlug);
            return image.map(imageDto -> ResponseEntity.ok(BaseResponseDto.success("Image retrieved successfully", imageDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound("Image not found with slug: " + imageSlug)));
        } catch (Exception e) {
            logger.error("Error retrieving image by slug {}: {}", imageSlug, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve image: " + e.getMessage()));
        }
    }

     /*=============================================
     ADMIN ENDPOINTS - Image Management
     =============================================*/

    /**
     * Get image metadata by database ID (Admin/System reference)
     * Public access for system integration and admin purposes
     * 
     * @param imageId Internal database identifier for the image
     * @return Complete image metadata including all stored information
     */
    @GetMapping("/{imageId}")
    @Operation(summary = "Get image by database ID",
               description = "Retrieve detailed image metadata using the internal database identifier. Primarily used for administrative purposes and system integrations that require direct database ID access.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image retrieved successfully",
                     content = @Content(schema = @Schema(implementation = ImageDto.class))),
        @ApiResponse(responseCode = "404", description = "Image not found with provided ID"),
        @ApiResponse(responseCode = "400", description = "Invalid image ID format"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<ImageDto>> getImageById(
            @Parameter(description = "Internal database identifier for the image",
                       example = "1")
            @PathVariable Long imageId) {
        logger.info("Admin requesting image by ID: {}", imageId);
        try {
            Optional<ImageDto> image = imageService.findImageById(imageId);
            return image.map(imageDto -> ResponseEntity.ok(BaseResponseDto.success("Image retrieved successfully", imageDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponseDto.notFound("Image not found with ID: " + imageId)));
        } catch (Exception e) {
            logger.error("Error retrieving image by ID {}: {}", imageId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve image: " + e.getMessage()));
        }
    }

    /**
     * Upload multiple images with comprehensive metadata processing
     * Handles batch image upload with SHA-256 hashing, dimension detection, and database persistence
     * Public endpoint accessible for product and content image uploads
     * 
     * @param imageFiles Array of image files to upload
     * @param imageDisplayNames Optional display names for images
     * @param imageSizes Optional size metadata for images
     * @param imageFormats Optional format metadata for images
     * @param imageRemarks Optional remarks for images
     * @param imageSlugs Optional slugs for images
     * @return Upload response with success/failure details and uploaded image information
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload multiple images with metadata",
               description = "Upload one or more images with optional metadata. Each image is processed with SHA-256 hashing for unique filename generation, automatic dimension detection, format validation, and comprehensive metadata storage. Supports parallel arrays for batch metadata assignment.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upload completed (may include partial failures)",
                     content = @Content(schema = @Schema(implementation = ImageUploadResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data or validation errors"),
        @ApiResponse(responseCode = "413", description = "File size exceeds maximum limit (50MB per file)"),
        @ApiResponse(responseCode = "415", description = "Unsupported file format (only image types allowed)"),
        @ApiResponse(responseCode = "500", description = "Internal server error or file processing failure")
    })
    public ResponseEntity<BaseResponseDto<ImageUploadResponseDto>> uploadImages(
            @Parameter(description = "Image files to upload", required = true)
            @RequestPart("imageFiles") MultipartFile[] imageFiles,
            
            @Parameter(description = "Optional display names for each image")
            @RequestPart(value = "imageDisplayNames", required = false) String[] imageDisplayNames,
            
            @Parameter(description = "Optional size metadata for each image (e.g., '1920x1080')")
            @RequestPart(value = "imageSizes", required = false) String[] imageSizes,
            
            @Parameter(description = "Optional format metadata for each image (e.g., 'JPG', 'PNG')")
            @RequestPart(value = "imageFormats", required = false) String[] imageFormats,
            
            @Parameter(description = "Optional remarks for each image")
            @RequestPart(value = "imageRemarks", required = false) String[] imageRemarks,
            
            @Parameter(description = "Optional URL-friendly identifiers for each image")
            @RequestPart(value = "imageSlugs", required = false) String[] imageSlugs) {
        
        logger.info("Uploading {} images via API", imageFiles != null ? imageFiles.length : 0);
        
        try {
            // Validate required parameters
            if (imageFiles == null || imageFiles.length == 0) {
                return ResponseEntity.badRequest()
                        .body(BaseResponseDto.badRequest("No image files provided"));
            }
            
            // Create ImageUploadDto from parts
            ImageUploadDto imageUploadDto = ImageUploadDto.builder()
                    .imageFiles(imageFiles)
                    .imageDisplayNames(imageDisplayNames)
                    .imageSizes(imageSizes)
                    .imageFormats(imageFormats)
                    .imageRemarks(imageRemarks)
                    .imageSlugs(imageSlugs)
                    .build();
            
            // Process upload
            ImageUploadResponseDto response = imageService.uploadImages(imageUploadDto);
            
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Upload completed: %d succeeded, %d failed", 
                            response.getSuccessCount(), response.getFailureCount()),
                    response
            ));
            
        } catch (IllegalArgumentException e) {
            logger.error("Validation error uploading images: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(BaseResponseDto.badRequest("Validation error: " + e.getMessage()));
        } catch (IOException e) {
            logger.error("IO error uploading images: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponseDto.serverError("File operation failed: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error uploading images: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponseDto.serverError("Failed to upload images: " + e.getMessage()));
        }
    }

    /**
     * Update existing image with new file and metadata
     * Replaces the existing image file while preserving the same ID and updating metadata
     * Public access for content management and image replacement operations
     * 
     * @param imageId Database ID of the image to update
     * @param imageUploadDto Form data containing the new image file and optional metadata
     * @return Updated image information with new file details
     */
    @PostMapping("/{imageId}/upload")
    @Operation(summary = "Update existing image with new file",
               description = "Replace an existing image file while maintaining the same database record. Updates both the file content and associated metadata. The new file undergoes the same processing as new uploads including SHA-256 hashing and dimension detection.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image updated successfully",
                     content = @Content(schema = @Schema(implementation = ImageDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data or exactly one file required for update"),
        @ApiResponse(responseCode = "404", description = "Image not found with provided ID"),
        @ApiResponse(responseCode = "413", description = "File size exceeds maximum limit (50MB)"),
        @ApiResponse(responseCode = "415", description = "Unsupported file format"),
        @ApiResponse(responseCode = "500", description = "Internal server error or file processing failure")
    })
    public ResponseEntity<BaseResponseDto<ImageDto>> updateImage(
            @Parameter(description = "Database ID of the image to update", example = "1")
            @PathVariable Long imageId,
            @Parameter(description = "Form data containing exactly one image file and optional metadata for the update",
                       content = @Content(mediaType = "multipart/form-data"))
            @Valid @ModelAttribute ImageUploadDto imageUploadDto) {
        
        logger.info("Updating image with file - ID: {}", imageId);
        
        try {
            // Validate required parameters
            if (imageUploadDto.getImageFiles() == null || imageUploadDto.getImageFiles().length != 1) {
                return ResponseEntity.badRequest()
                        .body(BaseResponseDto.badRequest("Exactly one file is required for update"));
            }
            
            if (imageUploadDto.getImageFiles()[0].isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(BaseResponseDto.badRequest("Image file cannot be empty"));
            }
            
            // Process update using ImageUploadDto
            ImageDto updatedImage = imageService.updateImage(imageId, imageUploadDto);
            
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Image file updated successfully with metadata", 
                    updatedImage
            ));
            
        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating image {}: {}", imageId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponseDto.notFound(e.getMessage()));
        } catch (IOException e) {
            logger.error("IO error updating image {}: {}", imageId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponseDto.serverError("File operation failed: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating image {}: {}", imageId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponseDto.serverError("Failed to update image: " + e.getMessage()));
        }
    }

    /**
     * Get total count of images in the system (Administrative dashboard)
     * Requires Admin or Manager role for access to system statistics
     * 
     * @return Total number of images stored in the database
     */
    @GetMapping("/stats/count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get total image count for dashboard",
               description = "Retrieve the total number of images stored in the system. Used for administrative dashboards and system monitoring. Requires Admin or Manager role access.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total count retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin or Manager role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Long>> getTotalImageCount() {
        logger.info("Getting total image count");
        try {
            long count = imageService.getTotalImageCount();
            return ResponseEntity.ok(BaseResponseDto.success("Total count retrieved successfully", count));
        } catch (Exception e) {
            logger.error("Error getting total image count: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get total count: " + e.getMessage()));
        }
    }

    /**
     * Get all distinct image sizes available in the system
     * Used for populating UI combobox/dropdown options for image size filtering
     * Public access to support customer filtering functionality
     * 
     * @return List of distinct image sizes available in the database
     */
    @GetMapping("/sizes")
    @Operation(summary = "Get all distinct image sizes",
               description = "Retrieve all distinct image sizes available in the system. This endpoint is typically used to populate dropdown/combobox options in the UI for filtering images by size.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image sizes retrieved successfully",
                content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<List<String>>> getAllImageSizes() {
        logger.info("Getting all distinct image sizes");
        try {
            List<String> sizes = imageService.getAllImageSizes();
            return ResponseEntity.ok(BaseResponseDto.success("Image sizes retrieved successfully", sizes));
        } catch (Exception e) {
            logger.error("Error getting image sizes: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get image sizes: " + e.getMessage()));
        }
    }

    /**
     * Get all distinct image formats available in the system
     * Used for populating UI combobox/dropdown options for image format filtering
     * Public access to support customer filtering functionality
     * 
     * @return List of distinct image formats available in the database
     */
    @GetMapping("/formats")
    @Operation(summary = "Get all distinct image formats",
               description = "Retrieve all distinct image formats available in the system. This endpoint is typically used to populate dropdown/combobox options in the UI for filtering images by format.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image formats retrieved successfully",
                content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<List<String>>> getAllImageFormats() {
        logger.info("Getting all distinct image formats");
        try {
            List<String> formats = imageService.getAllImageFormats();
            return ResponseEntity.ok(BaseResponseDto.success("Image formats retrieved successfully", formats));
        } catch (Exception e) {
            logger.error("Error getting image formats: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get image formats: " + e.getMessage()));
        }
    }

    /**
     * Get images with advanced filtering and pagination
     * Supports comprehensive filtering by size, format, and text search across multiple fields
     * Returns all images when no filters are provided, with configurable pagination and sorting
     * Public access for customer browsing and content discovery
     * 
     * @param imageSize Filter by image dimensions (partial match, case-insensitive)
     * @param imageFormat Filter by image format/extension (exact match, case-insensitive)
     * @param textSearch Search across image name, display name, slug, and remark (partial match, case-insensitive)
     * @param pageable Pagination parameters including page number, size, sort field, and direction
     * @return Paginated list of images matching the specified criteria
     */
    @GetMapping
    @Operation(summary = "Get images with advanced filtering and pagination",
               description = "Retrieve images using comprehensive filtering options including size, format, and text search. Supports pagination and custom sorting. When no filters are provided, returns all images with pagination. Text search operates across image name, display name, slug, and remark fields.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Images retrieved successfully",
                     content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters or pagination settings"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<Page<ImageDto>>> getImagesByCriteria(
            @Parameter(description = "Filter by image dimensions (e.g., '1920x1080', '2048'). Partial match, case-insensitive.",
                       example = "1920x1080")
            @RequestParam(value = "imageSize", required = false) String imageSize,
            @Parameter(description = "Filter by exact image format/extension (e.g., 'JPG', 'PNG', 'WEBP'). Case-insensitive.",
                       example = "JPG")
            @RequestParam(value = "imageFormat", required = false) String imageFormat,
            @Parameter(description = "Search text across image name, display name, slug, and remark fields. Partial match, case-insensitive.",
                       example = "product photo")
            @RequestParam(value = "textSearch", required = false) String textSearch,
            @Parameter(description = "Pagination parameters: page number (0-based), size, sort field (default: createdDt), direction (default: DESC)")
            @PageableDefault(page = 0, size = 10, sort = "createdDt", 
                            direction = org.springframework.data.domain.Sort.Direction.DESC) 
            Pageable pageable) {
        
        logger.info("Getting images with criteria - size: {}, format: {}, textSearch: {}, page: {}, size: {}", 
                   imageSize, imageFormat, textSearch, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<ImageDto> imagesPage = imageService.getImagesByCriteria(
                imageSize, imageFormat, textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Images retrieved successfully", imagesPage));
        } catch (Exception e) {
            logger.error("Error getting images: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to get images: " + e.getMessage()));
        }
    }

    /*=============================================
     FILE SERVING ENDPOINTS - Image Rendering & Download
     =============================================*/

    /**
     * Render image file directly for browser display
     * Serves image content with proper Content-Type headers for browser rendering
     * URL pattern: /images/file/**
     * Example: /images/file/home/masion-art/images/2b/3c/file.png
     * 
     * @param request HttpServletRequest to extract the full path from URI
     * @return Image bytes with appropriate Content-Type header
     */
    @GetMapping("/file/**")
    @Operation(summary = "Render image file for browser display",
               description = "Serve image file content directly with proper MIME type headers. The path is extracted from the URI after /images/file/. Browser will display the image inline.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image rendered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file path or path traversal attempt"),
        @ApiResponse(responseCode = "404", description = "Image file not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error or file read failure")
    })
    public ResponseEntity<byte[]> renderImageFile(HttpServletRequest request) {
        
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath(); // /api
        String basePath = contextPath + "/images/file/";
        
        String path = requestURI.substring(basePath.length());
        String absolutePath = "/" + path;
        
        logger.info("Rendering image file: {}", absolutePath);

        try {
            // Get image content
            byte[] imageContent = imageService.getImageFileContent(absolutePath);
            
            // Determine content type
            String contentType = imageService.getImageContentType(absolutePath);
            
            // Return image with proper headers
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageContent);
                    
        } catch (IOException e) {
            logger.error("File not found or read error: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid file path: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error rendering image file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Download image file with attachment disposition
     * Forces browser to download the file instead of displaying it
     * URL pattern: /images/download/**
     * Example: /images/download/home/masion-art/images/2b/3c/file.png
     * 
     * @param request HttpServletRequest to extract the full path from URI
     * @return Image bytes with Content-Disposition: attachment header
     */
    @GetMapping("/download/**")
    @Operation(summary = "Download image file",
               description = "Download image file with Content-Disposition attachment header. Browser will download the file instead of displaying it. The path is extracted from the URI after /images/download/.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image downloaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file path or path traversal attempt"),
        @ApiResponse(responseCode = "404", description = "Image file not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error or file read failure")
    })
    public ResponseEntity<byte[]> downloadImageFile(HttpServletRequest request) {
        
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String basePath = contextPath + "/images/download/";
        
        String path = requestURI.substring(basePath.length());
        String absolutePath = "/" + path;
        
        logger.info("Downloading image file: {}", absolutePath);

        try {
            // Get image content
            byte[] imageContent = imageService.getImageFileContent(absolutePath);
            
            // Extract filename for download
            String fileName = absolutePath.substring(absolutePath.lastIndexOf('/') + 1);
            
            // Determine content type
            String contentType = imageService.getImageContentType(absolutePath);
            
            // Return image with download headers
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(imageContent);
                    
        } catch (IOException e) {
            logger.error("File not found or read error: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid file path: {}", e.getMessage());
            return ResponseEntity.badRequest().build(); 
        } catch (Exception e) {
            logger.error("Error downloading image file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
