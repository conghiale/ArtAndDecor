package org.artanddecor.services.impl;

import org.artanddecor.dto.DiscountTypeDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.DiscountType;
import org.artanddecor.repository.DiscountTypeRepository;
import org.artanddecor.services.DiscountTypeService;
import org.artanddecor.utils.DiscountTypeMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DiscountType Service Implementation for business logic operations
 */
@Service
@Transactional
public class DiscountTypeServiceImpl implements DiscountTypeService {

    @Autowired
    private DiscountTypeRepository discountTypeRepository;

    @Autowired
    private DiscountTypeMapperUtil discountTypeMapperUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<DiscountTypeDto> getAllDiscountTypes(Pageable pageable) {
        Page<DiscountType> discountTypesPage = discountTypeRepository.findAll(pageable);
        return discountTypesPage.map(discountTypeMapperUtil::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountTypeDto getDiscountTypeById(Long discountTypeId) {
        DiscountType discountType = discountTypeRepository.findById(discountTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount Type not found with ID: " + discountTypeId));
        return discountTypeMapperUtil.mapToDto(discountType);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountTypeDto getDiscountTypeByName(String discountTypeName) {
        DiscountType discountType = discountTypeRepository.findByDiscountTypeName(discountTypeName)
                .orElseThrow(() -> new ResourceNotFoundException("Discount Type not found with name: " + discountTypeName));
        return discountTypeMapperUtil.mapToDto(discountType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountTypeDto> getAllEnabledDiscountTypes() {
        List<DiscountType> enabledDiscountTypes = discountTypeRepository.findByDiscountTypeEnabledOrderByDiscountTypeName(true);
        return enabledDiscountTypes.stream()
                .map(discountTypeMapperUtil::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DiscountTypeDto createDiscountType(DiscountTypeDto discountTypeDto) {
        // Validate uniqueness
        if (!isDiscountTypeNameUnique(discountTypeDto.getDiscountTypeName(), null)) {
            throw new IllegalArgumentException("Discount Type name already exists: " + discountTypeDto.getDiscountTypeName());
        }

        DiscountType discountType = discountTypeMapperUtil.mapToEntity(discountTypeDto);
        discountType.setCreatedDt(LocalDateTime.now());
        discountType.setModifiedDt(LocalDateTime.now());
        
        DiscountType savedDiscountType = discountTypeRepository.save(discountType);
        return discountTypeMapperUtil.mapToDto(savedDiscountType);
    }

    @Override
    public DiscountTypeDto updateDiscountType(Long discountTypeId, DiscountTypeDto discountTypeDto) {
        DiscountType existingDiscountType = discountTypeRepository.findById(discountTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount Type not found with ID: " + discountTypeId));

        // Validate uniqueness (excluding current record)
        if (!isDiscountTypeNameUnique(discountTypeDto.getDiscountTypeName(), discountTypeId)) {
            throw new IllegalArgumentException("Discount Type name already exists: " + discountTypeDto.getDiscountTypeName());
        }

        // Update fields
        existingDiscountType.setDiscountTypeName(discountTypeDto.getDiscountTypeName());
        existingDiscountType.setDiscountTypeDisplayName(discountTypeDto.getDiscountTypeDisplayName());
        existingDiscountType.setDiscountTypeRemark(discountTypeDto.getDiscountTypeDescription() != null ? 
            discountTypeDto.getDiscountTypeDescription() : discountTypeDto.getDiscountTypeRemark());
        existingDiscountType.setDiscountTypeEnabled(discountTypeDto.getDiscountTypeEnabled());
        existingDiscountType.setModifiedDt(LocalDateTime.now());

        DiscountType updatedDiscountType = discountTypeRepository.save(existingDiscountType);
        return discountTypeMapperUtil.mapToDto(updatedDiscountType);
    }

    @Override
    public void deleteDiscountType(Long discountTypeId) {
        if (!discountTypeRepository.existsById(discountTypeId)) {
            throw new ResourceNotFoundException("Discount Type not found with ID: " + discountTypeId);
        }
        // Note: Check if discount type is used in discounts before deletion
        discountTypeRepository.deleteById(discountTypeId);
    }

    @Override
    public DiscountTypeDto toggleDiscountTypeEnabled(Long discountTypeId) {
        DiscountType discountType = discountTypeRepository.findById(discountTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount Type not found with ID: " + discountTypeId));

        discountType.setDiscountTypeEnabled(!discountType.getDiscountTypeEnabled());
        discountType.setModifiedDt(LocalDateTime.now());

        DiscountType updatedDiscountType = discountTypeRepository.save(discountType);
        return discountTypeMapperUtil.mapToDto(updatedDiscountType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DiscountTypeDto> searchDiscountTypesByCriteria(
            Long discountTypeId,
            String discountTypeName,
            Boolean discountTypeEnabled,
            String textSearch,
            Pageable pageable) {
        
        Page<DiscountType> discountTypesPage = discountTypeRepository.findDiscountTypesByCriteria(
                discountTypeId, discountTypeName, discountTypeEnabled, textSearch, pageable);
        
        return discountTypesPage.map(discountTypeMapperUtil::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllEnabledDiscountTypeNames() {
        return discountTypeRepository.findAllEnabledDiscountTypeNames();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long discountTypeId) {
        return discountTypeRepository.existsById(discountTypeId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDiscountTypeNameUnique(String discountTypeName, Long excludeId) {
        return discountTypeRepository.findByDiscountTypeName(discountTypeName)
                .map(existingDiscountType -> excludeId != null && existingDiscountType.getDiscountTypeId().equals(excludeId))
                .orElse(true);
    }
}