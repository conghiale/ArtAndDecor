package org.artanddecor.services.impl;

import org.artanddecor.services.ImageDimensionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * WebP dimension service using enhanced ImageIO with WebP plugin
 * Requires imageio-webp dependency for WebP format support
 */
@Service
public class WebpDimensionService implements ImageDimensionService {

    private static final Logger logger = LoggerFactory.getLogger(WebpDimensionService.class);

    @Override
    public String getDimensions(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is null or empty");
        }

        try {
            byte[] imageBytes = file.getBytes();
            return getDimensions(imageBytes);
        } catch (IOException e) {
            logger.warn("Failed to read WebP dimensions from file: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public String getDimensions(byte[] imageBytes) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IOException("Image bytes are null or empty");
        }

        try {
            // First try with enhanced ImageIO (with WebP plugin)
            return getDimensionsWithImageIO(imageBytes);
            
        } catch (IOException e) {
            logger.debug("ImageIO WebP failed, trying manual parsing: {}", e.getMessage());
            
            // Fallback to manual WebP header parsing
            try {
                return getDimensionsFromWebpHeader(imageBytes);
            } catch (IOException headerException) {
                logger.warn("Both ImageIO and header parsing failed for WebP: {}", headerException.getMessage());
                throw new IOException("Unable to read WebP dimensions: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean supportsFormat(String format) {
        if (format == null) {
            return false;
        }
        return "webp".equals(format.toLowerCase().trim());
    }

    /**
     * Use enhanced ImageIO with WebP plugin to read dimensions
     */
    private String getDimensionsWithImageIO(byte[] imageBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
             ImageInputStream iis = ImageIO.createImageInputStream(bis)) {
            
            // Get WebP ImageReader
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("webp");
            if (!readers.hasNext()) {
                throw new IOException("No WebP ImageReader found");
            }
            
            ImageReader reader = readers.next();
            try {
                reader.setInput(iis, true, true);
                
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                String dimensions = width + "x" + height;
                
                logger.debug("WebP ImageIO detected dimensions: {}", dimensions);
                return dimensions;
                
            } finally {
                reader.dispose();
            }
        }
    }

    /**
     * Fallback: Parse WebP header manually to extract dimensions
     * WebP format: RIFF + size + WEBP + format + dimensions
     */
    private String getDimensionsFromWebpHeader(byte[] imageBytes) throws IOException {
        if (imageBytes.length < 30) {
            throw new IOException("WebP file too small to contain valid header");
        }

        // Verify RIFF signature
        if (!(imageBytes[0] == 'R' && imageBytes[1] == 'I' && 
              imageBytes[2] == 'F' && imageBytes[3] == 'F')) {
            throw new IOException("Invalid RIFF signature in WebP file");
        }

        // Verify WEBP signature at offset 8
        if (!(imageBytes[8] == 'W' && imageBytes[9] == 'E' && 
              imageBytes[10] == 'B' && imageBytes[11] == 'P')) {
            throw new IOException("Invalid WEBP signature");
        }

        // Check format type at offset 12
        String formatType = new String(imageBytes, 12, 4);
        
        int width, height;
        
        if ("VP8 ".equals(formatType)) {
            // Simple WebP format
            if (imageBytes.length < 30) {
                throw new IOException("WebP VP8 format too small");
            }
            
            // Width and height are at offsets 26-27 and 28-29 (little endian)
            width = ((imageBytes[27] & 0xFF) << 8) | (imageBytes[26] & 0xFF);
            height = ((imageBytes[29] & 0xFF) << 8) | (imageBytes[28] & 0xFF);
            
        } else if ("VP8L".equals(formatType)) {
            // Lossless WebP format
            if (imageBytes.length < 25) {
                throw new IOException("WebP VP8L format too small");
            }
            
            // Parse bitstream to extract width/height (more complex)
            width = parseVP8LDimensions(imageBytes)[0];
            height = parseVP8LDimensions(imageBytes)[1];
            
        } else if ("VP8X".equals(formatType)) {
            // Extended WebP format
            if (imageBytes.length < 30) {
                throw new IOException("WebP VP8X format too small");
            }
            
            // Width: bytes 24-26 (24-bit little endian) + 1
            // Height: bytes 27-29 (24-bit little endian) + 1
            width = ((imageBytes[26] & 0xFF) << 16) | ((imageBytes[25] & 0xFF) << 8) | (imageBytes[24] & 0xFF);
            height = ((imageBytes[29] & 0xFF) << 16) | ((imageBytes[28] & 0xFF) << 8) | (imageBytes[27] & 0xFF);
            width += 1;
            height += 1;
            
        } else {
            throw new IOException("Unsupported WebP format: " + formatType);
        }

        String dimensions = width + "x" + height;
        logger.debug("WebP header parsing detected dimensions: {}", dimensions);
        return dimensions;
    }

    /**
     * Parse VP8L (lossless) format dimensions
     * This is a simplified parser for the most common cases
     */
    private int[] parseVP8LDimensions(byte[] imageBytes) throws IOException {
        if (imageBytes.length < 25) {
            throw new IOException("VP8L format too small");
        }

        // VP8L bitstream starts at offset 21
        // First 5 bytes contain width and height packed in bits
        long bits = 0;
        for (int i = 0; i < 5; i++) {
            bits |= ((long) (imageBytes[21 + i] & 0xFF)) << (i * 8);
        }

        // Width: bits 0-13 (14 bits) + 1
        // Height: bits 14-27 (14 bits) + 1
        int width = (int) ((bits & 0x3FFF) + 1);
        int height = (int) (((bits >> 14) & 0x3FFF) + 1);

        return new int[]{width, height};
    }
}