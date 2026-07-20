package org.artanddecor.utils;

import org.artanddecor.dto.DiscountDto;
import org.artanddecor.dto.DiscountRequestDto;
import org.artanddecor.dto.DiscountTypeDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.Discount;
import org.artanddecor.model.DiscountType;
import org.artanddecor.repository.DiscountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Consolidated Discount Mapper Utility for converting between Entity and DTO
 * Handles both Discount and DiscountType mapping operations
 */
@Component
public class DiscountMapperUtil {

    @Autowired
    private DiscountTypeRepository discountTypeRepository;

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
     * Map DiscountDto to Discount entity
     * @param discountRequestDto DiscountDto
     * @return Discount entity
     */
    public Discount mapDiscountToEntity(DiscountRequestDto discountRequestDto) {
        if (discountRequestDto == null) {
            return null;
        }

        Discount entity = new Discount();

        if (discountRequestDto.getDiscountId() != null && discountRequestDto.getDiscountId() > 0) {
            entity.setDiscountId(discountRequestDto.getDiscountId());
        }

        entity.setDiscountCode(discountRequestDto.getDiscountCode());
        entity.setDiscountName(discountRequestDto.getDiscountName());
        entity.setDiscountRemark(discountRequestDto.getDiscountRemark());
        entity.setDiscountValue(discountRequestDto.getDiscountValue());
        entity.setMaxDiscountAmount(discountRequestDto.getMaxDiscountAmount());
        entity.setMinOrderAmount(discountRequestDto.getMinOrderAmount());
        entity.setTotalUsageLimit(discountRequestDto.getTotalUsageLimit());
        entity.setUsedCount(discountRequestDto.getUsedCount() != null ? discountRequestDto.getUsedCount() : 0);
        entity.setStartAt(discountRequestDto.getStartAt());
        entity.setEndAt(discountRequestDto.getEndAt());
        entity.setIsActive(discountRequestDto.getIsActive() != null ? discountRequestDto.getIsActive() : true);
        entity.setDiscountDisplayName(discountRequestDto.getDiscountDisplayName());
        entity.setCreatedDt(discountRequestDto.getCreatedDt());
        entity.setModifiedDt(discountRequestDto.getModifiedDt());

        // Map nested DiscountType
        if (discountRequestDto.getDiscountTypeId() != null && discountRequestDto.getDiscountTypeId() > 0) {
            DiscountType discountType = discountTypeRepository.findById(discountRequestDto.getDiscountTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Discount Type not found with ID: " + discountRequestDto.getDiscountTypeId()));
            entity.setDiscountType(discountType);
        }

        return entity;
    }

    /**
     * Update existing Discount entity with data from DTO
     * @param existingEntity Existing Discount entity
     * @param discountRequestDto DiscountDto with updated data
     * @return Updated Discount entity
     */
    public Discount updateDiscountEntityFromDto(Discount existingEntity, DiscountRequestDto discountRequestDto) {
        if (existingEntity == null || discountRequestDto == null) {
            return existingEntity;
        }

        existingEntity.setDiscountCode(discountRequestDto.getDiscountCode());
        existingEntity.setDiscountName(discountRequestDto.getDiscountName());
        existingEntity.setDiscountRemark(discountRequestDto.getDiscountRemark());
        existingEntity.setDiscountValue(discountRequestDto.getDiscountValue());
        existingEntity.setMaxDiscountAmount(discountRequestDto.getMaxDiscountAmount());
        existingEntity.setMinOrderAmount(discountRequestDto.getMinOrderAmount());
        existingEntity.setTotalUsageLimit(discountRequestDto.getTotalUsageLimit());
        existingEntity.setUsedCount(discountRequestDto.getUsedCount() != null ? discountRequestDto.getUsedCount() : 0);
        existingEntity.setStartAt(discountRequestDto.getStartAt());
        existingEntity.setEndAt(discountRequestDto.getEndAt());
        existingEntity.setIsActive(discountRequestDto.getIsActive() != null ? discountRequestDto.getIsActive() : true);
        existingEntity.setDiscountDisplayName(discountRequestDto.getDiscountDisplayName());

        if (discountRequestDto.getCreatedDt() != null)
            existingEntity.setCreatedDt(discountRequestDto.getCreatedDt());

        // Map nested DiscountType
        if (discountRequestDto.getDiscountTypeId() != null && discountRequestDto.getDiscountTypeId() > 0) {
            DiscountType discountType = discountTypeRepository.findById(discountRequestDto.getDiscountTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Discount Type not found with ID: " + discountRequestDto.getDiscountTypeId()));
            existingEntity.setDiscountType(discountType);
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
}