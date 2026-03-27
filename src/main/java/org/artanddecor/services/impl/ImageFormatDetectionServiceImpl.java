package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.exception.UnsupportedImageFormatException;
import org.artanddecor.services.ImageFormatDetectionService;
import org.artanddecor.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

/**
 * Implementation of ImageFormatDetectionService
 * Detects image formats using binary signatures (magic bytes) and file extensions
 */
@Service
@RequiredArgsConstructor
public class ImageFormatDetectionServiceImpl implements ImageFormatDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(ImageFormatDetectionServiceImpl.class);

    // Supported formats
    private static final Set<String> SUPPORTED_FORMATS = Set.of("jpg", "jpeg", "png", "webp", "heic");

    // Binary signatures (magic bytes) for image formats
    private static final byte[] JPG_SIGNATURE = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] WEBP_SIGNATURE = {'R', 'I', 'F', 'F'};
    private static final byte[] WEBP_FORMAT_SIGNATURE = {'W', 'E', 'B', 'P'};
    private static final byte[] HEIC_SIGNATURE = {'f', 't', 'y', 'p'};
    private static final byte[] HEIC_BRAND = {'h', 'e', 'i', 'c'};

    @Override
    public String detectFormat(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is null or empty");
        }

        byte[] fileBytes = file.getBytes();
        String filename = file.getOriginalFilename();
        return detectFormat(fileBytes, filename);
    }

    @Override
    public String detectFormat(byte[] fileBytes, String filename) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new UnsupportedImageFormatException("File content is empty", null, filename);
        }

        logger.debug("Detecting format for file: {} (size: {} bytes)", filename, fileBytes.length);

        // Detect by binary signature first (most reliable)
        String detectedFormat = detectByBinarySignature(fileBytes);
        
        if (detectedFormat != null) {
            logger.debug("Format detected by binary signature: {}", detectedFormat);
            return detectedFormat;
        }

        // Fallback to file extension
        detectedFormat = detectByFileExtension(filename);
        if (detectedFormat != null) {
            logger.debug("Format detected by file extension: {}", detectedFormat);
            return detectedFormat;
        }

        // If nothing detected, throw exception
        throw new UnsupportedImageFormatException("Unsupported image format", "unknown", filename);
    }

    @Override
    public boolean isSupportedFormat(String format) {
        if (Utils.isNullOrEmpty(format)) {
            return false;
        }
        return SUPPORTED_FORMATS.contains(format.toLowerCase().trim());
    }

    @Override
    public void validateSupportedFormat(MultipartFile file) throws IOException {
        String detectedFormat = detectFormat(file);
        if (!isSupportedFormat(detectedFormat)) {
            throw new UnsupportedImageFormatException(
                "Unsupported image format. Only JPG, PNG, WEBP, HEIC are allowed",
                detectedFormat,
                file.getOriginalFilename()
            );
        }
    }

    /**
     * Detect format by analyzing binary signature (magic bytes)
     * This is the most reliable method
     */
    private String detectByBinarySignature(byte[] fileBytes) {
        if (fileBytes.length < 8) {
            return null; // Not enough bytes to analyze
        }

        // Check JPG signature (FF D8 FF)
        if (matchesSignature(fileBytes, JPG_SIGNATURE, 0)) {
            return "jpg";
        }

        // Check PNG signature (89 50 4E 47 0D 0A 1A 0A)
        if (matchesSignature(fileBytes, PNG_SIGNATURE, 0)) {
            return "png";
        }

        // Check WebP signature (RIFF + WEBP at bytes 8-11)
        if (matchesSignature(fileBytes, WEBP_SIGNATURE, 0) && fileBytes.length >= 12) {
            if (matchesSignature(fileBytes, WEBP_FORMAT_SIGNATURE, 8)) {
                return "webp";
            }
        }

        // Check HEIC signature (ftyp + heic at bytes 4-7, 8-11)
        if (fileBytes.length >= 12 && matchesSignature(fileBytes, HEIC_SIGNATURE, 4)) {
            if (matchesSignature(fileBytes, HEIC_BRAND, 8)) {
                return "heic";
            }
        }

        return null;
    }

    /**
     * Detect format by file extension as fallback
     */
    private String detectByFileExtension(String filename) {
        if (Utils.isNullOrEmpty(filename) || !filename.contains(".")) {
            return null;
        }

        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase().trim();
        
        // Normalize extensions
        return switch (extension) {
            case "jpg", "jpeg" -> "jpg";
            case "png" -> "png";
            case "webp" -> "webp";
            case "heic", "heif" -> "heic";
            default -> null;
        };
    }

    /**
     * Check if byte array matches signature at specific offset
     */
    private boolean matchesSignature(byte[] fileBytes, byte[] signature, int offset) {
        if (fileBytes.length < offset + signature.length) {
            return false;
        }

        for (int i = 0; i < signature.length; i++) {
            if (fileBytes[offset + i] != signature[i]) {
                return false;
            }
        }

        return true;
    }
}