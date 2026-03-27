package org.artanddecor.services.impl;

import org.artanddecor.services.ImageDimensionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * ImageIO-based dimension service for JPG and PNG formats
 * Uses standard Java ImageIO library for reliable dimension detection
 */
@Service
public class ImageIODimensionService implements ImageDimensionService {

    private static final Logger logger = LoggerFactory.getLogger(ImageIODimensionService.class);

    @Override
    public String getDimensions(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is null or empty");
        }

        try {
            byte[] imageBytes = file.getBytes();
            return getDimensions(imageBytes);
        } catch (IOException e) {
            logger.warn("Failed to read dimensions from file using ImageIO: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public String getDimensions(byte[] imageBytes) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IOException("Image bytes are null or empty");
        }

        try {
            // Disable cache for memory efficiency
            ImageIO.setUseCache(false);
            
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            
            if (bufferedImage == null) {
                throw new IOException("Could not decode image using ImageIO");
            }
            
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            String dimensions = width + "x" + height;
            
            logger.debug("ImageIO detected dimensions: {}", dimensions);
            return dimensions;
            
        } catch (IOException e) {
            logger.warn("Failed to read image dimensions using ImageIO: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean supportsFormat(String format) {
        if (format == null) {
            return false;
        }
        
        String lowerFormat = format.toLowerCase().trim();
        return "jpg".equals(lowerFormat) || 
               "jpeg".equals(lowerFormat) || 
               "png".equals(lowerFormat);
    }
}