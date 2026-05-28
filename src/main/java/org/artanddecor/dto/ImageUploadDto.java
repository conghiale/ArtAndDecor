package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * Image Upload DTO for handling single file upload with metadata.
 * Used by both upload (POST) and update (PUT) endpoints.
 *
 * For upload (POST): imageFile is required.
 * For update (PUT): imageFile is optional — omit to update metadata only.
 *
 * File name (imageName) is automatically generated via SHA-256 hash of file content.
 * Display name is taken from client for user-friendly identification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadDto {

    /**
     * Image file uploaded via form-data.
     * Required for upload; optional for update (metadata-only update when omitted).
     */
    private MultipartFile imageFile;

    /**
     * User-friendly display name for the image.
     * Optional – extracted from original filename when not provided.
     */
    private String imageDisplayName;

    /**
     * Image dimensions, e.g. "2048x1024", "1920x1080".
     * Optional – auto-detected from file content when not provided.
     */
    private String imageSize;

    /**
     * Image format, e.g. "JPG", "PNG", "WEBP".
     * Optional – extracted from file extension when not provided.
     */
    private String imageFormat;

    /**
     * Optional remark / description for the image.
     */
    private String imageRemark;

    /**
     * URL-friendly slug for the image.
     * Optional – generated from display name when not provided.
     */
    private String imageSlug;
}
