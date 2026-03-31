package org.artanddecor.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * CartItemAttribute DTO for API responses
 * Simplified representation of selected product attributes for cart items
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemAttributeDto {

    private Long cartItemAttributeId;

    private Long cartItemId;

    private Long productAttributeId;

    private String attributeName;

    private String attributeValue;

    private String attributeDisplayName;

    private LocalDateTime createdDt;

    private LocalDateTime modifiedDt;

    /**
     * Get formatted attribute display text
     * @return Formatted string like "Size: 30x40cm"
     */
    public String getFormattedDisplay() {
        if (attributeName != null && attributeValue != null) {
            return attributeName + ": " + attributeValue;
        }
        return "";
    }

    /**
     * Check if this attribute has valid name and value
     * @return true if has both name and value, false otherwise
     */
    public boolean isValid() {
        return attributeName != null && !attributeName.trim().isEmpty() &&
               attributeValue != null && !attributeValue.trim().isEmpty();
    }
}