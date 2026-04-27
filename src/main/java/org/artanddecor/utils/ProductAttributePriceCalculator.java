package org.artanddecor.utils;

import org.artanddecor.model.CartItemAttribute;
import org.artanddecor.model.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for calculating product attribute prices based on policy mapping
 */
@Component
public class ProductAttributePriceCalculator {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductAttributePriceCalculator.class);
    
    /**
     * Calculate price based on Policy PRODUCT_ATTRIBUTE_PRICE_MAPPING
     * @param cartItemAttributes Selected cart item attributes
     * @param policy Policy containing price mapping
     * @return Policy-based price or BigDecimal.ZERO if no match found
     */
    public BigDecimal calculatePriceFromPolicy(List<CartItemAttribute> cartItemAttributes, Policy policy) {
        try {
            // Get attribute values of selected attributes
            if (cartItemAttributes == null || cartItemAttributes.isEmpty()) {
                logger.debug("No cart item attributes provided");
                return BigDecimal.ZERO;
            }
            
            Set<String> selectedAttributeValues = cartItemAttributes.stream()
                .map(CartItemAttribute::getAttributeValue)
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toSet());
            
            if (selectedAttributeValues.isEmpty()) {
                logger.debug("No valid attribute values found");
                return BigDecimal.ZERO;
            }
            
            if (policy == null || policy.getPolicyValue() == null) {
                logger.debug("Policy or policy value is null");
                return BigDecimal.ZERO;
            }
            
            // Parse policy value (format: "attribute1,attribute2=price" per line)
            String[] mappingLines = policy.getPolicyValue().split("\n");
            
            for (String line : mappingLines) {
                line = line.trim();
                if (line.isEmpty() || !line.contains("=")) {
                    continue;
                }
                
                String[] parts = line.split("=", 2);
                if (parts.length != 2) {
                    continue;
                }
                
                String attributesCombination = parts[0].trim();
                String priceStr = parts[1].trim();
                
                try {
                    BigDecimal price = new BigDecimal(priceStr);
                    
                    // Parse required attributes for this price
                    Set<String> requiredAttributes = Arrays.stream(attributesCombination.split(","))
                        .map(String::trim)
                        .filter(attr -> !attr.isEmpty())
                        .collect(Collectors.toSet());
                    
                    // Check if selected attributes exactly match required attributes
                    if (attributesExactlyMatch(selectedAttributeValues, requiredAttributes)) {
                        logger.info("Found matching price configuration: {} = {}", attributesCombination, price);
                        return price;
                    }
                    
                } catch (NumberFormatException e) {
                    logger.warn("Invalid price format in policy mapping: {}", priceStr);
                }
            }
            
            logger.debug("No matching attribute combination found in policy for attributes: {}", selectedAttributeValues);
            return BigDecimal.ZERO;
            
        } catch (Exception e) {
            logger.error("Error calculating policy-based price: {}", e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Check if selected attributes exactly match required attributes (no more, no less)
     * Order doesn't matter: {30x40, vang} matches {vang, 30x40}
     * @param selectedAttributes Set of selected attribute values
     * @param requiredAttributes Set of required attribute values
     * @return true if sets are exactly equal, false otherwise
     */
    private boolean attributesExactlyMatch(Set<String> selectedAttributes, Set<String> requiredAttributes) {
        // Check case-insensitive exact match
        Set<String> selectedLower = selectedAttributes.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
            
        Set<String> requiredLower = requiredAttributes.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
        
        boolean matches = selectedLower.equals(requiredLower);
        
        if (matches) {
            logger.debug("Attributes match: selected={}, required={}", selectedAttributes, requiredAttributes);
        } else {
            logger.debug("Attributes don't match: selected={}, required={}", selectedAttributes, requiredAttributes);
        }
        
        return matches;
    }
    
    /**
     * Get the highest price from individual product attributes (fallback logic)
     * @param cartItemAttributes Selected cart item attributes
     * @return Maximum attribute price or BigDecimal.ZERO if no valid prices found
     */
    public BigDecimal getMaxAttributePrice(List<CartItemAttribute> cartItemAttributes) {
        if (cartItemAttributes == null || cartItemAttributes.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal maxPrice = BigDecimal.ZERO;
        
        for (CartItemAttribute cartItemAttribute : cartItemAttributes) {
            if (cartItemAttribute.getProductAttribute() != null) {
                BigDecimal attributePrice = cartItemAttribute.getProductAttribute().getProductAttributePrice();
                if (attributePrice != null && attributePrice.compareTo(BigDecimal.ZERO) > 0) {
                    if (maxPrice.compareTo(attributePrice) < 0) {
                        maxPrice = attributePrice;
                    }
                }
            }
        }
        
        logger.debug("Max attribute price found: {}", maxPrice);
        return maxPrice;
    }
}