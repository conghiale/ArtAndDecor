package org.artanddecor.utils;

import org.artanddecor.dto.CartItemAttributeDto;
import org.artanddecor.model.CartItemAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CartItemAttribute Mapper Utility
 * Handles conversion between entities and DTOs for cart item attributes
 */
public class CartItemAttributeMapperUtil {

    private static final Logger logger = LoggerFactory.getLogger(CartItemAttributeMapperUtil.class);

    /**
     * Convert CartItemAttribute entity to CartItemAttributeDto
     * @param entity CartItemAttribute entity
     * @return CartItemAttributeDto
     */
    public static CartItemAttributeDto toDto(CartItemAttribute entity) {
        if (entity == null) {
            return null;
        }

        try {
            CartItemAttributeDto.CartItemAttributeDtoBuilder builder = CartItemAttributeDto.builder()
                    .cartItemAttributeId(entity.getCartItemAttributeId())
                    .createdDt(entity.getCreatedDt())
                    .modifiedDt(entity.getModifiedDt());

            // Set cart item ID
            if (entity.getCartItem() != null) {
                builder.cartItemId(entity.getCartItem().getCartItemId());
            }

            // Set product attribute ID and details
            if (entity.getProductAttribute() != null) {
                builder.productAttributeId(entity.getProductAttribute().getProductAttributeId());
                builder.attributeValue(entity.getProductAttribute().getProductAttributeValue());
                
                // Set attribute name from ProductAttr
                if (entity.getProductAttribute().getProductAttr() != null) {
                    builder.attributeName(entity.getProductAttribute().getProductAttr().getProductAttrName());
                    builder.attributeDisplayName(entity.getProductAttribute().getProductAttr().getProductAttrDisplayName());
                }
            }

            return builder.build();
        } catch (Exception e) {
            logger.error("Error mapping CartItemAttribute to DTO: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Convert list of CartItemAttribute entities to DTOs
     * @param entities List of CartItemAttribute entities
     * @return List of CartItemAttributeDto
     */
    public static List<CartItemAttributeDto> toDtoList(List<CartItemAttribute> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(CartItemAttributeMapperUtil::toDto)
                .filter(dto -> dto != null && dto.isValid())  // Filter out null and invalid results
                .collect(Collectors.toList());
    }
}