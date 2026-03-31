package org.artanddecor.utils;

import org.artanddecor.dto.DiscountDto;
import org.artanddecor.dto.DiscountTypeDto;
import org.artanddecor.model.Discount;
import org.artanddecor.model.DiscountType;
import org.springframework.stereotype.Component;

/**
 * Consolidated Discount Mapper Utility for converting between Entity and DTO
 * Handles both Discount and DiscountType mapping operations
 */
@Component
public class DiscountMapperUtil {

    // =================================================================
    // DISCOUNT MAPPING OPERATIONS
    // =================================================================

    /**
     * Map Discount entity to DiscountDto
     * @param discount Discount entity
     * @return DiscountDto
     */
    public DiscountDto mapDiscountToDto(Discount discount) {
        if (discount == null) {
            return null;
        }

        DiscountDto dto = new DiscountDto();
        dto.setDiscountId(discount.getDiscountId());
        dto.setDiscountCode(discount.getDiscountCode());
        dto.setDiscountName(discount.getDiscountName());
        dto.setDiscountRemark(discount.getDiscountRemark());
        dto.setDiscountValue(discount.getDiscountValue());
        dto.setMaxDiscountAmount(discount.getMaxDiscountAmount());
        dto.setMinOrderAmount(discount.getMinOrderAmount());
        dto.setTotalUsageLimit(discount.getTotalUsageLimit());
        dto.setUsedCount(discount.getUsedCount());
        dto.setStartAt(discount.getStartAt());
        dto.setEndAt(discount.getEndAt());
        dto.setIsActive(discount.getIsActive());
        dto.setDiscountDisplayName(discount.getDiscountDisplayName());
        dto.setCreatedDt(discount.getCreatedDt());
        dto.setModifiedDt(discount.getModifiedDt());

        // Map nested DiscountType
        if (discount.getDiscountType() != null) {
            dto.setDiscountType(mapDiscountTypeToDto(discount.getDiscountType()));
        }

        return dto;
    }

    /**
     * Map DiscountDto to Discount entity
     * @param discountDto DiscountDto
     * @return Discount entity
     */
    public Discount mapDiscountToEntity(DiscountDto discountDto) {
        if (discountDto == null) {
            return null;
        }

        Discount entity = new Discount();
        entity.setDiscountId(discountDto.getDiscountId());
        entity.setDiscountCode(discountDto.getDiscountCode());
        entity.setDiscountName(discountDto.getDiscountName());
        entity.setDiscountRemark(discountDto.getDiscountRemark());
        entity.setDiscountValue(discountDto.getDiscountValue());
        entity.setMaxDiscountAmount(discountDto.getMaxDiscountAmount());
        entity.setMinOrderAmount(discountDto.getMinOrderAmount());
        entity.setTotalUsageLimit(discountDto.getTotalUsageLimit());
        entity.setUsedCount(discountDto.getUsedCount() != null ? discountDto.getUsedCount() : 0);
        entity.setStartAt(discountDto.getStartAt());
        entity.setEndAt(discountDto.getEndAt());
        entity.setIsActive(discountDto.getIsActive() != null ? discountDto.getIsActive() : true);
        entity.setDiscountDisplayName(discountDto.getDiscountDisplayName());
        entity.setCreatedDt(discountDto.getCreatedDt());
        entity.setModifiedDt(discountDto.getModifiedDt());

        // Map nested DiscountType
        if (discountDto.getDiscountType() != null) {
            entity.setDiscountType(mapDiscountTypeToEntity(discountDto.getDiscountType()));
        }

        return entity;
    }

    /**
     * Update existing Discount entity with data from DTO
     * @param existingEntity Existing Discount entity
     * @param dto DiscountDto with updated data
     * @return Updated Discount entity
     */
    public Discount updateDiscountEntityFromDto(Discount existingEntity, DiscountDto dto) {
        if (existingEntity == null || dto == null) {
            return existingEntity;
        }

        if (dto.getDiscountCode() != null) {
            existingEntity.setDiscountCode(dto.getDiscountCode());
        }
        if (dto.getDiscountName() != null) {
            existingEntity.setDiscountName(dto.getDiscountName());
        }
        if (dto.getDiscountRemark() != null) {
            existingEntity.setDiscountRemark(dto.getDiscountRemark());
        }
        if (dto.getDiscountValue() != null) {
            existingEntity.setDiscountValue(dto.getDiscountValue());
        }
        if (dto.getMaxDiscountAmount() != null) {
            existingEntity.setMaxDiscountAmount(dto.getMaxDiscountAmount());
        }
        if (dto.getMinOrderAmount() != null) {
            existingEntity.setMinOrderAmount(dto.getMinOrderAmount());
        }
        if (dto.getTotalUsageLimit() != null) {
            existingEntity.setTotalUsageLimit(dto.getTotalUsageLimit());
        }
        if (dto.getUsedCount() != null) {
            existingEntity.setUsedCount(dto.getUsedCount());
        }
        if (dto.getStartAt() != null) {
            existingEntity.setStartAt(dto.getStartAt());
        }
        if (dto.getEndAt() != null) {
            existingEntity.setEndAt(dto.getEndAt());
        }
        if (dto.getIsActive() != null) {
            existingEntity.setIsActive(dto.getIsActive());
        }
        if (dto.getDiscountDisplayName() != null) {
            existingEntity.setDiscountDisplayName(dto.getDiscountDisplayName());
        }

        // Update DiscountType if provided
        if (dto.getDiscountType() != null && existingEntity.getDiscountType() != null) {
            updateDiscountTypeEntityFromDto(existingEntity.getDiscountType(), dto.getDiscountType());
        } else if (dto.getDiscountType() != null) {
            existingEntity.setDiscountType(mapDiscountTypeToEntity(dto.getDiscountType()));
        }

        return existingEntity;
    }

    // =================================================================
    // DISCOUNT TYPE MAPPING OPERATIONS
    // =================================================================

    /**
     * Map DiscountType entity to DiscountTypeDto
     * @param discountType DiscountType entity
     * @return DiscountTypeDto
     */
    public DiscountTypeDto mapDiscountTypeToDto(DiscountType discountType) {
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
    public DiscountType mapDiscountTypeToEntity(DiscountTypeDto discountTypeDto) {
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
    public DiscountType updateDiscountTypeEntityFromDto(DiscountType existingEntity, DiscountTypeDto dto) {
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