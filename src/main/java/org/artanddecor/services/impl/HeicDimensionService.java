package org.artanddecor.services.impl;

import org.artanddecor.services.ImageDimensionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

/**
 * HEIC dimension service using enhanced ImageIO with HEIC plugin
 * Requires nightmonkeys-imageio dependency for HEIC format support
 */
@Service
public class HeicDimensionService implements ImageDimensionService {

    private static final Logger logger = LoggerFactory.getLogger(HeicDimensionService.class);

    @Override
    public String getDimensions(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is null or empty");
        }

        try {
            byte[] imageBytes = file.getBytes();
            return getDimensions(imageBytes);
        } catch (IOException e) {
            logger.warn("Failed to read HEIC dimensions from file: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public String getDimensions(byte[] imageBytes) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IOException("Image bytes are null or empty");
        }

        try {
            // First try with enhanced ImageIO (with HEIC plugin)
            return getDimensionsWithImageIO(imageBytes);
            
        } catch (IOException e) {
            logger.debug("ImageIO HEIC failed, trying manual parsing: {}", e.getMessage());
            
            // Fallback to manual HEIC header parsing
            try {
                return getDimensionsFromHeicHeader(imageBytes);
            } catch (IOException headerException) {
                logger.warn("Both ImageIO and header parsing failed for HEIC: {}", headerException.getMessage());
                throw new IOException("Unable to read HEIC dimensions: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean supportsFormat(String format) {
        if (format == null) {
            return false;
        }
        String lowerFormat = format.toLowerCase().trim();
        return "heic".equals(lowerFormat) || "heif".equals(lowerFormat);
    }

    /**
     * Use enhanced ImageIO with HEIC plugin to read dimensions
     */
    private String getDimensionsWithImageIO(byte[] imageBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
             ImageInputStream iis = ImageIO.createImageInputStream(bis)) {
            
            // Get HEIC ImageReader
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("heic");
            if (!readers.hasNext()) {
                // Try alternative format names
                readers = ImageIO.getImageReadersByFormatName("heif");
            }
            
            if (!readers.hasNext()) {
                throw new IOException("No HEIC ImageReader found");
            }
            
            ImageReader reader = readers.next();
            try {
                reader.setInput(iis, true, true);
                
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                String dimensions = width + "x" + height;
                
                logger.debug("HEIC ImageIO detected dimensions: {}", dimensions);
                return dimensions;
                
            } finally {
                reader.dispose();
            }
        }
    }

    /**
     * Fallback: Parse HEIC header manually to extract dimensions
     * HEIC format is based on ISO Base Media File Format (MP4 container)
     * Structure: [size][type][data]...
     */
    private String getDimensionsFromHeicHeader(byte[] imageBytes) throws IOException {
        if (imageBytes.length < 32) {
            throw new IOException("HEIC file too small to contain valid header");
        }

        // Verify ftyp box at the beginning
        if (!isValidHeicFile(imageBytes)) {
            throw new IOException("Not a valid HEIC file");
        }

        // Parse boxes to find 'meta' box and then 'iprp' (item properties)
        int offset = 0;
        while (offset < imageBytes.length - 8) {
            // Read box size (4 bytes, big endian)
            int boxSize = readBigEndianInt(imageBytes, offset);
            
            // Read box type (4 bytes)
            String boxType = new String(imageBytes, offset + 4, 4);
            
            logger.debug("Found HEIC box: {} at offset {} with size {}", boxType, offset, boxSize);
            
            if ("meta".equals(boxType)) {
                // Found meta box, look for image dimensions inside
                int[] dimensions = parseMetaBox(imageBytes, offset + 8, boxSize - 8);
                if (dimensions != null) {
                    String result = dimensions[0] + "x" + dimensions[1];
                    logger.debug("HEIC header parsing detected dimensions: {}", result);
                    return result;
                }
            }
            
            if (boxSize <= 8) {
                break; // Invalid box size
            }
            
            offset += boxSize;
        }

        throw new IOException("Could not find image dimensions in HEIC file");
    }

    /**
     * Check if byte array contains valid HEIC file signature
     */
    private boolean isValidHeicFile(byte[] imageBytes) {
        if (imageBytes.length < 12) {
            return false;
        }

        // Check ftyp box type at offset 4
        if (!(imageBytes[4] == 'f' && imageBytes[5] == 't' && 
              imageBytes[6] == 'y' && imageBytes[7] == 'p')) {
            return false;
        }

        // Check brand at offset 8 (heic, mif1, etc.)
        String brand = new String(imageBytes, 8, 4);
        return "heic".equals(brand) || "mif1".equals(brand);
    }

    /**
     * Parse meta box to find image dimensions
     * This is a simplified parser for common HEIC structures
     */
    private int[] parseMetaBox(byte[] imageBytes, int metaOffset, int metaSize) {
        try {
            // Skip meta box version/flags (4 bytes)
            int offset = metaOffset + 4;
            int endOffset = metaOffset + metaSize;
            
            while (offset < endOffset - 8) {
                int boxSize = readBigEndianInt(imageBytes, offset);
                String boxType = new String(imageBytes, offset + 4, 4);
                
                if ("iprp".equals(boxType)) {
                    // Item properties box found
                    return parseItemPropertiesBox(imageBytes, offset + 8, boxSize - 8);
                } else if ("pitm".equals(boxType)) {
                    // Primary item box - contains reference to main image
                    logger.debug("Found primary item box");
                }
                
                if (boxSize <= 8) {
                    break;
                }
                
                offset += boxSize;
            }
        } catch (Exception e) {
            logger.debug("Error parsing HEIC meta box: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * Parse item properties box to find image configuration
     */
    private int[] parseItemPropertiesBox(byte[] imageBytes, int iprpOffset, int iprpSize) {
        try {
            int offset = iprpOffset;
            int endOffset = iprpOffset + iprpSize;
            
            while (offset < endOffset - 8) {
                int boxSize = readBigEndianInt(imageBytes, offset);
                String boxType = new String(imageBytes, offset + 4, 4);
                
                if ("ispe".equals(boxType)) {
                    // Image Spatial Extents box - contains width and height
                    if (boxSize >= 20) {
                        int width = readBigEndianInt(imageBytes, offset + 12);
                        int height = readBigEndianInt(imageBytes, offset + 16);
                        return new int[]{width, height};
                    }
                }
                
                if (boxSize <= 8) {
                    break;
                }
                
                offset += boxSize;
            }
        } catch (Exception e) {
            logger.debug("Error parsing HEIC item properties box: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * Read 4-byte big endian integer from byte array
     */
    private int readBigEndianInt(byte[] bytes, int offset) {
        return ByteBuffer.wrap(bytes, offset, 4).order(ByteOrder.BIG_ENDIAN).getInt();
    }
}