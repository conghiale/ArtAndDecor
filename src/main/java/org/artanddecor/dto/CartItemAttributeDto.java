package org.artanddecor.dto;

import lombok.*;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
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

    @DecimalMin(value = "0.0", inclusive = true, message = "Product attribute price must not be negative")
    @Digits(integer = 13, fraction = 2, message = "Product attribute price must have at most 13 integer digits and 2 decimal places")
    private BigDecimal productAttributePrice;

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