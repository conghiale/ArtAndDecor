package org.artanddecor.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for testing enhanced image format detection
 * Demonstrates the new format detection capabilities
 */
public class ImageFormatTestUtils {

    private static final Logger logger = LoggerFactory.getLogger(ImageFormatTestUtils.class);

    /**
     * Test image format detection and dimension extraction
     * Can be used for manual testing during development
     * 
     * @param imagePath Path to image file for testing
     * @return Test result summary
     */
    public static String testImageFormat(String imagePath) {
        try {
            byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
            
            // Test format detection
            String detectedFormat = detectFormatBySignature(imageBytes);
            
            // Test dimension extraction
            String dimensions = Utils.getImageDimensions(imageBytes);
            
            return String.format("File: %s | Format: %s | Dimensions: %s", 
                               imagePath, detectedFormat, dimensions);
                               
        } catch (IOException e) {
            return String.format("Error testing %s: %s", imagePath, e.getMessage());
        }
    }

    /**
     * Quick format detection using binary signatures
     * Demonstrates the detection logic
     * 
     * @param imageBytes Image file content
     * @return Detected format or "unknown"
     */
    private static String detectFormatBySignature(byte[] imageBytes) {
        if (imageBytes.length < 8) {
            return "unknown";
        }

        // JPG: FF D8 FF
        if (imageBytes[0] == (byte) 0xFF && 
            imageBytes[1] == (byte) 0xD8 && 
            imageBytes[2] == (byte) 0xFF) {
            return "JPG";
        }

        // PNG: 89 50 4E 47
        if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == 0x50 && 
            imageBytes[2] == 0x4E && imageBytes[3] == 0x47) {
            return "PNG";
        }

        // WebP: RIFF...WEBP
        if (imageBytes.length >= 12 &&
            imageBytes[0] == 'R' && imageBytes[1] == 'I' && 
            imageBytes[2] == 'F' && imageBytes[3] == 'F' &&
            imageBytes[8] == 'W' && imageBytes[9] == 'E' && 
            imageBytes[10] == 'B' && imageBytes[11] == 'P') {
            return "WEBP";
        }

        // HEIC: ftyp...heic
        if (imageBytes.length >= 12 &&
            imageBytes[4] == 'f' && imageBytes[5] == 't' && 
            imageBytes[6] == 'y' && imageBytes[7] == 'p' &&
            imageBytes[8] == 'h' && imageBytes[9] == 'e' && 
            imageBytes[10] == 'i' && imageBytes[11] == 'c') {
            return "HEIC";
        }

        return "unknown";
    }

    /**
     * Print supported formats for reference
     * 
     * @return List of supported formats
     */
    public static String getSupportedFormats() {
        return "Supported formats: JPG/JPEG (ImageIO), PNG (ImageIO), WEBP (Enhanced ImageIO + Manual), HEIC (Enhanced ImageIO + Manual)";
    }

    /**
     * Demo method - can be called from controller for testing
     * 
     * @return Demo information
     */
    public static String getDemoInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Enhanced Image Format Support:\n");
        sb.append("- JPG/JPEG: Standard ImageIO decoder\n");
        sb.append("- PNG: Standard ImageIO decoder\n");
        sb.append("- WEBP: Enhanced ImageIO with fallback manual parsing\n");
        sb.append("- HEIC: Enhanced ImageIO with fallback manual parsing\n");
        sb.append("- Binary signature detection for accurate format identification\n");
        sb.append("- Strict format validation in upload process\n");
        sb.append("- Returns 'unknown' only when all decoders fail\n");
        return sb.toString();
    }
}