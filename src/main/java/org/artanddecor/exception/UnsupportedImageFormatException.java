package org.artanddecor.exception;

/**
 * Exception thrown when trying to process an unsupported image format
 * Used specifically for image dimension detection and format validation
 */
public class UnsupportedImageFormatException extends RuntimeException {

    private final String detectedFormat;
    private final String filename;

    public UnsupportedImageFormatException(String message) {
        super(message);
        this.detectedFormat = null;
        this.filename = null;
    }

    public UnsupportedImageFormatException(String message, String detectedFormat, String filename) {
        super(String.format("%s (detected format: %s, filename: %s)", message, detectedFormat, filename));
        this.detectedFormat = detectedFormat;
        this.filename = filename;
    }

    public UnsupportedImageFormatException(String message, Throwable cause) {
        super(message, cause);
        this.detectedFormat = null;
        this.filename = null;
    }

    public String getDetectedFormat() {
        return detectedFormat;
    }

    public String getFilename() {
        return filename;
    }
}