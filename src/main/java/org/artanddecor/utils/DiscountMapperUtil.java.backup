package org.artanddecor.utils;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.DiscountDto;
import org.artanddecor.model.Discount;
import org.springframework.stereotype.Component;

/**
 * Discount Mapper Utility for converting between Entity and DTO
 */
@Component
@RequiredArgsConstructor
public class DiscountMapperUtil {

    private final DiscountTypeMapperUtil discountTypeMapperUtil;

    /**
     * Map Discount entity to DiscountDto
     * @param discount Discount entity
     * @return DiscountDto
     */
    public DiscountDto mapToDto(Discount discount) {
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
            dto.setDiscountType(discountTypeMapperUtil.mapToDto(discount.getDiscountType()));
        }

        return dto;
    }

    /**
     * Map DiscountDto to Discount entity
     * @param discountDto DiscountDto
     * @return Discount entity
     */
    public Discount mapToEntity(DiscountDto discountDto) {
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
            entity.setDiscountType(discountTypeMapperUtil.mapToEntity(discountDto.getDiscountType()));
        }

        return entity;
    }

    /**
     * Update existing Discount entity with data from DTO
     * @param existingEntity Existing Discount entity
     * @param dto DiscountDto with updated data
     * @return Updated Discount entity
     */
    public Discount updateEntityFromDto(Discount existingEntity, DiscountDto dto) {
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

        return existingEntity;
    }
}