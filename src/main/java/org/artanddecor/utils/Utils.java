package org.artanddecor.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Common utility methods for the application
 */
public class Utils {
    
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    
    // Email validation pattern
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    // Phone number pattern (Vietnamese format)
    private static final String PHONE_PATTERN = 
        "^(\\+84|0)(3[2-9]|5[689]|7[06-9]|8[1-689]|9[0-46-9])[0-9]{7}$";
    
    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    private static final Pattern phonePattern = Pattern.compile(PHONE_PATTERN);

    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * Check if string is null or empty
     * @param str String to check
     * @return true if null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Check if number is null or zero
     * @param num Number to check
     * @return true if null or zero
     */
    public static boolean isNullOrZero(Number num) {
        if (num == null) return true;
        if (num instanceof Integer) return num.intValue() == 0;
        if (num instanceof Long) return num.longValue() == 0L;
        if (num instanceof Double) return num.doubleValue() == 0.0;
        if (num instanceof Float) return num.floatValue() == 0.0f;
        if (num instanceof BigDecimal) return ((BigDecimal) num).compareTo(BigDecimal.ZERO) == 0;
        return false;
    }

    /**
     * Check if collection is null or empty
     * @param collection Collection to check
     * @return true if null or empty
     */
    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Check if map is null or empty
     * @param map Map to check
     * @return true if null or empty
     */
    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid email format
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) {
            logger.debug("Email validation failed: email is null or empty");
            return false;
        }
        boolean isValid = emailPattern.matcher(email.trim()).matches();
        logger.debug("Email validation for '{}': {}", email, isValid);
        return isValid;
    }

    /**
     * Validate Vietnamese phone number format
     * @param phone Phone number to validate
     * @return true if valid phone format
     */
    public static boolean isValidPhone(String phone) {
        if (isNullOrEmpty(phone)) {
            logger.debug("Phone validation failed: phone is null or empty");
            return false;
        }
        boolean isValid = phonePattern.matcher(phone.trim()).matches();
        logger.debug("Phone validation for '{}': {}", phone, isValid);
        return isValid;
    }

    /**
     * Hash string using SHA-1
     * @param input String to hash
     * @return SHA-1 hash or null if error
     */
    public static String hashSHA1(String input) {
        if (isNullOrEmpty(input)) {
            logger.warn("Cannot hash null or empty string");
            return null;
        }
        
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = sha1.digest(input.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            logger.debug("Successfully hashed string with SHA-1");
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-1 algorithm not available: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Hash string using SHA-256
     * @param input String to hash
     * @return SHA-256 hash or null if error
     */
    public static String hashSHA256(String input) {
        if (isNullOrEmpty(input)) {
            logger.warn("Cannot hash null or empty string");
            return null;
        }
        
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = sha256.digest(input.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            logger.debug("Successfully hashed string with SHA-256");
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256 algorithm not available: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Generate slug from Vietnamese text
     * @param text Text to convert to slug
     * @return URL-friendly slug
     */
    public static String generateSlug(String text) {
        if (isNullOrEmpty(text)) {
            return "";
        }

        // Normalize and remove Vietnamese diacritics
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        
        // Convert to lowercase and replace special characters
        String slug = withoutDiacritics.toLowerCase()
            .replaceAll("đ", "d")
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");

        logger.debug("Generated slug '{}' from text '{}'", slug, text);
        return slug;
    }

    /**
     * Safe string comparison (null-safe)
     * @param str1 First string
     * @param str2 Second string
     * @return true if both strings are equal (including both null)
     */
    public static boolean safeEquals(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equals(str2);
    }

    /**
     * Trim string safely
     * @param str String to trim
     * @return Trimmed string or null if input is null
     */
    public static String safeTrim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * Get default value if string is null or empty
     * @param str String to check
     * @param defaultValue Default value
     * @return Original string if not null/empty, otherwise default value
     */
    public static String defaultIfEmpty(String str, String defaultValue) {
        return isNullOrEmpty(str) ? defaultValue : str;
    }

    /**
     * Check if string contains only digits
     * @param str String to check
     * @return true if contains only digits
     */
    public static boolean isNumeric(String str) {
        if (isNullOrEmpty(str)) return false;
        return str.matches("\\d+");
    }

    /**
     * Safe parsing of integer from string
     * @param str String to parse
     * @param defaultValue Default value if parsing fails
     * @return Parsed integer or default value
     */
    public static Integer safeParseInt(String str, Integer defaultValue) {
        if (isNullOrEmpty(str)) return defaultValue;
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            logger.warn("Failed to parse integer from '{}': {}", str, e.getMessage());
            return defaultValue;
        }
    }

    /**
     * Safe parsing of long from string
     * @param str String to parse
     * @param defaultValue Default value if parsing fails
     * @return Parsed long or default value
     */
    public static Long safeParseLong(String str, Long defaultValue) {
        if (isNullOrEmpty(str)) return defaultValue;
        try {
            return Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
            logger.warn("Failed to parse long from '{}': {}", str, e.getMessage());
            return defaultValue;
        }
    }

    // =====================================================
    // FILE & IMAGE UTILITIES
    // =====================================================

    /**
     * Extract filename without extension from original filename
     * Example: "image-photo.jpg" -> "image-photo"
     * Generic utility for any file operation
     * 
     * @param filename Original filename (e.g., "document.pdf", "photo.jpg")
     * @return Filename without extension, or "file" if input is null/empty
     */
    public static String extractFileNameWithoutExtension(String filename) {
        if (isNullOrEmpty(filename)) {
            logger.debug("Filename is null or empty, returning default 'file'");
            return "file";
        }
        
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            String result = filename.substring(0, lastDotIndex);
            logger.debug("Extracted filename '{}' from '{}'", result, filename);
            return result;
        }
        
        logger.debug("No extension found in '{}', returning as is", filename);
        return filename;
    }

    /**
     * Hash file content using SHA-256 algorithm only (no timestamp)
     * Used for file deduplication - same file content produces same hash
     * 
     * @param fileContentBytes File content as byte array
     * @return SHA-256 hash hex string (no extension)
     */
    public static String hashFileContent(byte[] fileContentBytes) {
        if (fileContentBytes == null || fileContentBytes.length == 0) {
            logger.warn("File content bytes are null or empty");
            return "file_" + System.currentTimeMillis();
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            
            // Hash: SHA-256(file_content only)
            digest.update(fileContentBytes);
            byte[] hash = digest.digest();
            
            // Convert to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);  
            }
            
            String hashedContent = hexString.toString();
            logger.debug("File content hashed (content only): {}", hashedContent);
            return hashedContent;
            
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256 algorithm not available: {}", e.getMessage());
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Hash file content with current timestamp
     * Format: SHA-256(file_content_bytes + current_timestamp_millis)
     * Result: HexFormat lowercase
     * Suitable for generating unique file identifiers
     * 
     * Generic utility - can be used by any file upload service
     * 
     * @param fileContentBytes Raw bytes of file content
     * @param fileExtension File extension with dot (e.g., ".jpg", ".pdf")
     * @return Hashed filename with extension (e.g., "a1b2c3d4e5f6....jpg")
     */
    public static String hashFileContentWithTimestamp(byte[] fileContentBytes, String fileExtension) {
        if (fileContentBytes == null || fileContentBytes.length == 0) {
            logger.warn("File content bytes are null or empty");
            return "file_" + System.currentTimeMillis() + fileExtension;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            
//            Get current timestamp in milliseconds
            long currentTimeMillis = System.currentTimeMillis();
            String timestampStr = String.valueOf(currentTimeMillis);
            
//            Hash: SHA-256(file_content + timestamp_millis)
            digest.update(fileContentBytes);
            digest.update(timestampStr.getBytes(StandardCharsets.UTF_8));
            byte[] hash = digest.digest();
            
//            Convert to hex (lowercase)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            String extension = isNullOrEmpty(fileExtension) ? "" : fileExtension;
            String hashedName = hexString + extension;
            logger.debug("File content hashed with timestamp: {}", hashedName);
            return hashedName;
            
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256 algorithm not available: {}", e.getMessage());
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Get image dimensions (width x height) from image byte data
     * Uses format-specific decoders for accurate dimension detection
     * Supports: JPG/JPEG, PNG, WEBP, HEIC formats
     * Example return: "2048x1024"
     * Returns "unknown" only if all format-specific decoders fail
     * 
     * Strategy pattern approach:
     * 1. Detect format from binary signature
     * 2. Use appropriate decoder for each format:
     *    - JPG/PNG: Standard ImageIO
     *    - WEBP: Enhanced ImageIO with WebP plugin + manual parsing fallback
     *    - HEIC: Enhanced ImageIO with HEIC plugin + manual parsing fallback
     * 3. Return "unknown" only as last resort
     * 
     * @param imageDataBytes Raw bytes of image file
     * @return Image dimensions as "widthxheight" string, or "unknown" if cannot read
     */
    public static String getImageDimensions(byte[] imageDataBytes) {
        if (imageDataBytes == null || imageDataBytes.length == 0) {
            logger.warn("Image data bytes are null or empty");
            return "unknown";
        }

        try {
            // 1. Detect format using binary signatures
            String format = detectImageFormat(imageDataBytes);
            logger.debug("Detected image format: {}", format);
            
            // 2. Use format-specific decoder
            return switch (format) {
                case "jpg", "jpeg", "png" -> getImageDimensionsWithImageIO(imageDataBytes);
                case "webp" -> getImageDimensionsWebP(imageDataBytes);
                case "heic" -> getImageDimensionsHEIC(imageDataBytes);
                default -> {
                    logger.warn("Unsupported image format: {}", format);
                    yield "unknown";
                }
            };
            
        } catch (Exception e) {
            logger.warn("Failed to read image dimensions: {}", e.getMessage());
            return "unknown";
        }
    }

    /**
     * Detect image format from binary signature (magic bytes)
     * More reliable than file extension detection
     */
    private static String detectImageFormat(byte[] imageDataBytes) {
        if (imageDataBytes.length < 8) {
            return "unknown";
        }

        // JPG signature: FF D8 FF
        if (imageDataBytes[0] == (byte) 0xFF && 
            imageDataBytes[1] == (byte) 0xD8 && 
            imageDataBytes[2] == (byte) 0xFF) {
            return "jpg";
        }

        // PNG signature: 89 50 4E 47 0D 0A 1A 0A
        if (imageDataBytes[0] == (byte) 0x89 && imageDataBytes[1] == 0x50 && 
            imageDataBytes[2] == 0x4E && imageDataBytes[3] == 0x47 &&
            imageDataBytes[4] == 0x0D && imageDataBytes[5] == 0x0A &&
            imageDataBytes[6] == 0x1A && imageDataBytes[7] == 0x0A) {
            return "png";
        }

        // WebP signature: RIFF...WEBP
        if (imageDataBytes.length >= 12 &&
            imageDataBytes[0] == 'R' && imageDataBytes[1] == 'I' && 
            imageDataBytes[2] == 'F' && imageDataBytes[3] == 'F' &&
            imageDataBytes[8] == 'W' && imageDataBytes[9] == 'E' && 
            imageDataBytes[10] == 'B' && imageDataBytes[11] == 'P') {
            return "webp";
        }

        // HEIC signature: ftyp + heic brand
        if (imageDataBytes.length >= 12 &&
            imageDataBytes[4] == 'f' && imageDataBytes[5] == 't' && 
            imageDataBytes[6] == 'y' && imageDataBytes[7] == 'p' &&
            imageDataBytes[8] == 'h' && imageDataBytes[9] == 'e' && 
            imageDataBytes[10] == 'i' && imageDataBytes[11] == 'c') {
            return "heic";
        }

        return "unknown";
    }

    /**
     * Get image dimensions using standard ImageIO (JPG/PNG)
     */
    private static String getImageDimensionsWithImageIO(byte[] imageDataBytes) {
        try {
            javax.imageio.ImageIO.setUseCache(false);
            java.awt.image.BufferedImage bufferedImage = javax.imageio.ImageIO.read(
                    new java.io.ByteArrayInputStream(imageDataBytes)
            );
            
            if (bufferedImage == null) {
                logger.warn("ImageIO could not decode the image");
                return "unknown";
            }
            
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            String dimensions = width + "x" + height;
            
            logger.debug("ImageIO detected dimensions: {}", dimensions);
            return dimensions;
            
        } catch (Exception e) {
            logger.warn("Failed to read image dimensions with ImageIO: {}", e.getMessage());
            return "unknown";
        }
    }

    /**
     * Get WebP image dimensions using multiple strategies
     */
    private static String getImageDimensionsWebP(byte[] imageDataBytes) {
        try {
            // Strategy 1: Try enhanced ImageIO with WebP plugin
            try {
                javax.imageio.ImageIO.setUseCache(false);
                java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(imageDataBytes);
                java.awt.image.BufferedImage bufferedImage = javax.imageio.ImageIO.read(bis);
                
                if (bufferedImage != null) {
                    int width = bufferedImage.getWidth();
                    int height = bufferedImage.getHeight();
                    String dimensions = width + "x" + height;
                    logger.debug("WebP ImageIO detected dimensions: {}", dimensions);
                    return dimensions;
                }
            } catch (Exception e) {
                logger.debug("WebP ImageIO failed: {}", e.getMessage());
            }

            // Strategy 2: Manual WebP header parsing
            return parseWebPDimensions(imageDataBytes);
            
        } catch (Exception e) {
            logger.warn("Failed to read WebP dimensions: {}", e.getMessage());
            return "unknown";
        }
    }

    /**
     * Get HEIC image dimensions using multiple strategies
     */
    private static String getImageDimensionsHEIC(byte[] imageDataBytes) {
        try {
            // Strategy 1: Try enhanced ImageIO with HEIC plugin
            try {
                javax.imageio.ImageIO.setUseCache(false);
                java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(imageDataBytes);
                java.awt.image.BufferedImage bufferedImage = javax.imageio.ImageIO.read(bis);
                
                if (bufferedImage != null) {
                    int width = bufferedImage.getWidth();
                    int height = bufferedImage.getHeight();
                    String dimensions = width + "x" + height;
                    logger.debug("HEIC ImageIO detected dimensions: {}", dimensions);
                    return dimensions;
                }
            } catch (Exception e) {
                logger.debug("HEIC ImageIO failed: {}", e.getMessage());
            }

            // Strategy 2: Manual HEIC header parsing (simplified)
            return parseHEICDimensions(imageDataBytes);
            
        } catch (Exception e) {
            logger.warn("Failed to read HEIC dimensions: {}", e.getMessage());
            return "unknown";
        }
    }

    /**
     * Parse WebP dimensions from header manually
     * Supports VP8, VP8L, and VP8X formats
     */
    private static String parseWebPDimensions(byte[] imageDataBytes) {
        if (imageDataBytes.length < 30) {
            throw new RuntimeException("WebP file too small");
        }

        // Get format type at offset 12
        String formatType = new String(imageDataBytes, 12, 4);
        
        int width, height;
        
        if ("VP8 ".equals(formatType)) {
            // Simple lossy WebP
            width = ((imageDataBytes[27] & 0xFF) << 8) | (imageDataBytes[26] & 0xFF);
            height = ((imageDataBytes[29] & 0xFF) << 8) | (imageDataBytes[28] & 0xFF);
            
        } else if ("VP8X".equals(formatType)) {
            // Extended WebP
            width = ((imageDataBytes[26] & 0xFF) << 16) | ((imageDataBytes[25] & 0xFF) << 8) | (imageDataBytes[24] & 0xFF);
            height = ((imageDataBytes[29] & 0xFF) << 16) | ((imageDataBytes[28] & 0xFF) << 8) | (imageDataBytes[27] & 0xFF);
            width += 1;
            height += 1;
            
        } else {
            throw new RuntimeException("Unsupported WebP format: " + formatType);
        }

        String dimensions = width + "x" + height;
        logger.debug("WebP manual parsing detected dimensions: {}", dimensions);
        return dimensions;
    }

    /**
     * Parse HEIC dimensions from header manually (simplified parser)
     * Looks for 'ispe' box containing image spatial extents
     */
    private static String parseHEICDimensions(byte[] imageDataBytes) {
        // This is a very simplified HEIC parser
        // In a full implementation, you'd parse the complete box structure
        
        // Look for 'ispe' box signature in the file
        for (int i = 0; i < imageDataBytes.length - 20; i++) {
            if (imageDataBytes[i] == 'i' && imageDataBytes[i+1] == 's' && 
                imageDataBytes[i+2] == 'p' && imageDataBytes[i+3] == 'e') {
                
                // Found ispe box, extract dimensions (big endian)
                try {
                    int width = ((imageDataBytes[i+12] & 0xFF) << 24) |
                               ((imageDataBytes[i+13] & 0xFF) << 16) |
                               ((imageDataBytes[i+14] & 0xFF) << 8) |
                               (imageDataBytes[i+15] & 0xFF);
                    
                    int height = ((imageDataBytes[i+16] & 0xFF) << 24) |
                                ((imageDataBytes[i+17] & 0xFF) << 16) |
                                ((imageDataBytes[i+18] & 0xFF) << 8) |
                                (imageDataBytes[i+19] & 0xFF);
                    
                    if (width > 0 && height > 0 && width < 100000 && height < 100000) {
                        String dimensions = width + "x" + height;
                        logger.debug("HEIC manual parsing detected dimensions: {}", dimensions);
                        return dimensions;
                    }
                } catch (Exception e) {
                    logger.debug("Error parsing HEIC dimensions at offset {}: {}", i, e.getMessage());
                }
            }
        }
        
        throw new RuntimeException("Could not find HEIC dimensions");
    }
}