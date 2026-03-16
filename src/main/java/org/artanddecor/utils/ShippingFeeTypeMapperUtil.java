package org.artanddecor.utils;

import org.artanddecor.dto.ShippingFeeTypeDto;
import org.artanddecor.model.ShippingFeeType;
import org.springframework.stereotype.Component;

/**
 * ShippingFeeType Mapper Utility for converting between Entity and DTO
 */
@Component
public class ShippingFeeTypeMapperUtil {

    /**
     * Map ShippingFeeType entity to ShippingFeeTypeDto
     * @param shippingFeeType ShippingFeeType entity
     * @return ShippingFeeTypeDto
     */
    public ShippingFeeTypeDto mapToDto(ShippingFeeType shippingFeeType) {
        if (shippingFeeType == null) {
            return null;
        }

        ShippingFeeTypeDto dto = new ShippingFeeTypeDto();
        dto.setShippingFeeTypeId(shippingFeeType.getShippingFeeTypeId());
        dto.setShippingFeeTypeName(shippingFeeType.getShippingFeeTypeName());
        dto.setShippingFeeTypeDisplayName(shippingFeeType.getShippingFeeTypeDisplayName());
        dto.setShippingFeeTypeRemark(shippingFeeType.getShippingFeeTypeRemark());
        dto.setShippingFeeTypeEnabled(shippingFeeType.getShippingFeeTypeEnabled());
        dto.setCreatedDt(shippingFeeType.getCreatedDt());
        dto.setModifiedDt(shippingFeeType.getModifiedDt());

        return dto;
    }

    /**
     * Map ShippingFeeTypeDto to ShippingFeeType entity
     * @param dto ShippingFeeTypeDto
     * @return ShippingFeeType entity
     */
    public ShippingFeeType mapToEntity(ShippingFeeTypeDto dto) {
        if (dto == null) {
            return null;
        }

        ShippingFeeType shippingFeeType = new ShippingFeeType();
        shippingFeeType.setShippingFeeTypeId(dto.getShippingFeeTypeId());
        shippingFeeType.setShippingFeeTypeName(dto.getShippingFeeTypeName());
        shippingFeeType.setShippingFeeTypeDisplayName(dto.getShippingFeeTypeDisplayName());
        shippingFeeType.setShippingFeeTypeRemark(dto.getShippingFeeTypeRemark());
        shippingFeeType.setShippingFeeTypeEnabled(dto.getShippingFeeTypeEnabled());

        return shippingFeeType;
    }
}