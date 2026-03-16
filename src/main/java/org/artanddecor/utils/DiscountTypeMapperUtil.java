package org.artanddecor.utils;

import org.artanddecor.dto.DiscountTypeDto;
import org.artanddecor.model.DiscountType;
import org.springframework.stereotype.Component;

/**
 * DiscountType Mapper Utility for converting between Entity and DTO
 */
@Component
public class DiscountTypeMapperUtil {

    /**
     * Map DiscountType entity to DiscountTypeDto
     * @param discountType DiscountType entity
     * @return DiscountTypeDto
     */
    public DiscountTypeDto mapToDto(DiscountType discountType) {
        if (discountType == null) {
            return null;
        }

        DiscountTypeDto dto = new DiscountTypeDto();
        dto.setDiscountTypeId(discountType.getDiscountTypeId());
        dto.setDiscountTypeName(discountType.getDiscountTypeName());
        dto.setDiscountTypeDisplayName(discountType.getDiscountTypeDisplayName());
        dto.setDiscountTypeDescription(discountType.getDiscountTypeRemark()); // Map remark to description
        dto.setDiscountTypeRemark(discountType.getDiscountTypeRemark()); 
        dto.setDiscountTypeEnabled(discountType.getDiscountTypeEnabled());
        dto.setDiscountTypeCreatedDate(discountType.getCreatedDt());
        dto.setDiscountTypeModifiedDate(discountType.getModifiedDt());

        return dto;
    }

    /**
     * Map DiscountTypeDto to DiscountType entity
     * @param discountTypeDto DiscountTypeDto
     * @return DiscountType entity
     */
    public DiscountType mapToEntity(DiscountTypeDto discountTypeDto) {
        if (discountTypeDto == null) {
            return null;
        }

        DiscountType entity = new DiscountType();
        entity.setDiscountTypeId(discountTypeDto.getDiscountTypeId());
        entity.setDiscountTypeName(discountTypeDto.getDiscountTypeName());
        entity.setDiscountTypeDisplayName(discountTypeDto.getDiscountTypeDisplayName());
        entity.setDiscountTypeRemark(discountTypeDto.getDiscountTypeDescription() != null ? 
            discountTypeDto.getDiscountTypeDescription() : discountTypeDto.getDiscountTypeRemark()); // Map description to remark
        entity.setDiscountTypeEnabled(discountTypeDto.getDiscountTypeEnabled() != null ? discountTypeDto.getDiscountTypeEnabled() : true);
        entity.setCreatedDt(discountTypeDto.getDiscountTypeCreatedDate());
        entity.setModifiedDt(discountTypeDto.getDiscountTypeModifiedDate());

        return entity;
    }

    /**
     * Update existing DiscountType entity with data from DTO
     * @param existingEntity Existing DiscountType entity
     * @param dto DiscountTypeDto with updated data
     * @return Updated DiscountType entity
     */
    public DiscountType updateEntityFromDto(DiscountType existingEntity, DiscountTypeDto dto) {
        if (existingEntity == null || dto == null) {
            return existingEntity;
        }

        if (dto.getDiscountTypeName() != null) {
            existingEntity.setDiscountTypeName(dto.getDiscountTypeName());
        }
        if (dto.getDiscountTypeDisplayName() != null) {
            existingEntity.setDiscountTypeDisplayName(dto.getDiscountTypeDisplayName());
        }
        if (dto.getDiscountTypeDescription() != null) {
            existingEntity.setDiscountTypeRemark(dto.getDiscountTypeDescription()); // Map description to remark
        }
        if (dto.getDiscountTypeRemark() != null) {
            existingEntity.setDiscountTypeRemark(dto.getDiscountTypeRemark());
        }
        if (dto.getDiscountTypeEnabled() != null) {
            existingEntity.setDiscountTypeEnabled(dto.getDiscountTypeEnabled());
        }

        return existingEntity;
    }
}