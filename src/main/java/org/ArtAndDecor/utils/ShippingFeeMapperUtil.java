package org.ArtAndDecor.utils;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.ShippingFeeDto;
import org.ArtAndDecor.model.ShippingFee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ShippingFee Mapper Utility for converting between Entity and DTO
 */
@Component
@RequiredArgsConstructor
public class ShippingFeeMapperUtil {

    private final ShippingFeeTypeMapperUtil shippingFeeTypeMapperUtil;

    /**
     * Map ShippingFee entity to ShippingFeeDto
     * @param shippingFee ShippingFee entity
     * @return ShippingFeeDto
     */
    public ShippingFeeDto mapToDto(ShippingFee shippingFee) {
        if (shippingFee == null) {
            return null;
        }

        ShippingFeeDto dto = new ShippingFeeDto();
        dto.setShippingFeeId(shippingFee.getShippingFeeId());
        dto.setShippingFeeTypeId(shippingFee.getShippingFeeType() != null ? 
                                shippingFee.getShippingFeeType().getShippingFeeTypeId() : null);
        dto.setMinOrderPrice(shippingFee.getMinOrderPrice());
        dto.setMaxOrderPrice(shippingFee.getMaxOrderPrice());
        dto.setShippingFeeValue(shippingFee.getShippingFeeValue());
        dto.setShippingFeeDisplayName(shippingFee.getShippingFeeDisplayName());
        dto.setShippingFeeRemark(shippingFee.getShippingFeeRemark());
        dto.setShippingFeeEnabled(shippingFee.getShippingFeeEnabled());
        dto.setCreatedDt(shippingFee.getCreatedDt());
        dto.setModifiedDt(shippingFee.getModifiedDt());

        // Set related data from ShippingFeeType
        if (shippingFee.getShippingFeeType() != null) {
            dto.setShippingFeeTypeName(shippingFee.getShippingFeeType().getShippingFeeTypeName());
            dto.setShippingFeeTypeRemark(shippingFee.getShippingFeeType().getShippingFeeTypeRemark());
        }

        return dto;
    }

    /**
     * Map ShippingFeeDto to ShippingFee entity
     * @param dto ShippingFeeDto
     * @return ShippingFee entity
     */
    public ShippingFee mapToEntity(ShippingFeeDto dto) {
        if (dto == null) {
            return null;
        }

        ShippingFee shippingFee = new ShippingFee();
        shippingFee.setShippingFeeId(dto.getShippingFeeId());
        shippingFee.setMinOrderPrice(dto.getMinOrderPrice());
        shippingFee.setMaxOrderPrice(dto.getMaxOrderPrice());
        shippingFee.setShippingFeeValue(dto.getShippingFeeValue());
        shippingFee.setShippingFeeDisplayName(dto.getShippingFeeDisplayName());
        shippingFee.setShippingFeeRemark(dto.getShippingFeeRemark());
        shippingFee.setShippingFeeEnabled(dto.getShippingFeeEnabled());

        // Note: ShippingFeeType relationship should be set separately in service layer
        // to avoid circular dependencies during mapping

        return shippingFee;
    }
}