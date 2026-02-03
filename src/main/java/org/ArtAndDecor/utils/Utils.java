package org.ArtAndDecor.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    
    private static final Logger logger = LogManager.getLogger(Utils.class);
    
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
     * Example return: "2048x1024"
     * Returns "unknown" if dimensions cannot be determined
     * 
     * Generic utility for any image processing service
     * Can be used in image upload, image processing, or validation services
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
            javax.imageio.ImageIO.setUseCache(false); // Avoid cache issues
            java.awt.image.BufferedImage bufferedImage = javax.imageio.ImageIO.read(
                    new java.io.ByteArrayInputStream(imageDataBytes)
            );
            
            if (bufferedImage == null) {
                logger.warn("Could not read image dimensions - unsupported format or corrupted image data");
                return "unknown";
            }
            
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            String dimensions = width + "x" + height;
            
            logger.debug("Image dimensions read: {}", dimensions);
            return dimensions;
            
        } catch (Exception e) {
            logger.warn("Failed to read image dimensions: {}", e.getMessage());
            return "unknown";
        }
    }
}