package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ShippingFeeTypeDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.ShippingFeeType;
import org.artanddecor.repository.ShippingFeeTypeRepository;
import org.artanddecor.services.ShippingFeeTypeService;
import org.artanddecor.utils.ShipmentMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of ShippingFeeTypeService interface
 * Business logic for shipping fee type management operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ShippingFeeTypeServiceImpl implements ShippingFeeTypeService {

    private static final Logger logger = LoggerFactory.getLogger(ShippingFeeTypeServiceImpl.class);

    private final ShippingFeeTypeRepository shippingFeeTypeRepository;
    private final ShipmentMapperUtil shipmentMapperUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<ShippingFeeTypeDto> getAllShippingFeeTypes(Pageable pageable) {
        logger.debug("Getting all shipping fee types with pagination: {}", pageable);
        Page<ShippingFeeType> typePage = shippingFeeTypeRepository.findAll(pageable);
        return typePage.map(shipmentMapperUtil::mapShippingFeeTypeToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingFeeTypeDto getShippingFeeTypeById(Long shippingFeeTypeId) {
        logger.debug("Getting shipping fee type by ID: {}", shippingFeeTypeId);
        ShippingFeeType feeType = shippingFeeTypeRepository.findById(shippingFeeTypeId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipping fee type not found with ID: " + shippingFeeTypeId));
        return shipmentMapperUtil.mapShippingFeeTypeToDto(feeType);
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingFeeTypeDto getShippingFeeTypeByName(String shippingFeeTypeName) {
        logger.debug("Getting shipping fee type by name: {}", shippingFeeTypeName);
        ShippingFeeType feeType = shippingFeeTypeRepository.findByShippingFeeTypeName(shippingFeeTypeName)
            .orElseThrow(() -> new ResourceNotFoundException("Shipping fee type not found with name: " + shippingFeeTypeName));
        return shipmentMapperUtil.mapShippingFeeTypeToDto(feeType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingFeeTypeDto> getAllEnabledShippingFeeTypes() {
        logger.debug("Getting all enabled shipping fee types");
        List<ShippingFeeType> types = shippingFeeTypeRepository.findByShippingFeeTypeEnabledTrueOrderByShippingFeeTypeName();
        return types.stream()
            .map(shipmentMapperUtil::mapShippingFeeTypeToDto)
            .toList();
    }

    @Override
    public ShippingFeeTypeDto createShippingFeeType(ShippingFeeTypeDto shippingFeeTypeDto) {
        logger.debug("Creating new shipping fee type: {}", shippingFeeTypeDto.getShippingFeeTypeName());
        
        // Check if name already exists
        if (shippingFeeTypeRepository.existsByShippingFeeTypeName(shippingFeeTypeDto.getShippingFeeTypeName())) {
            throw new IllegalArgumentException("Shipping fee type name already exists: " + shippingFeeTypeDto.getShippingFeeTypeName());
        }

        ShippingFeeType feeType = shipmentMapperUtil.mapShippingFeeTypeToEntity(shippingFeeTypeDto);
        ShippingFeeType savedType = shippingFeeTypeRepository.save(feeType);
        logger.info("Created shipping fee type with ID: {} and name: {}", 
                   savedType.getShippingFeeTypeId(), savedType.getShippingFeeTypeName());
        
        return shipmentMapperUtil.mapShippingFeeTypeToDto(savedType);
    }

    @Override
    public ShippingFeeTypeDto updateShippingFeeType(Long shippingFeeTypeId, ShippingFeeTypeDto shippingFeeTypeDto) {
        logger.debug("Updating shipping fee type with ID: {}", shippingFeeTypeId);
        
        ShippingFeeType existingType = shippingFeeTypeRepository.findById(shippingFeeTypeId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipping fee type not found with ID: " + shippingFeeTypeId));

        // Check if name already exists (excluding current record)
        if (!existingType.getShippingFeeTypeName().equals(shippingFeeTypeDto.getShippingFeeTypeName()) &&
            shippingFeeTypeRepository.existsByShippingFeeTypeNameAndShippingFeeTypeIdNot(
                shippingFeeTypeDto.getShippingFeeTypeName(), shippingFeeTypeId)) {
            throw new IllegalArgumentException("Shipping fee type name already exists: " + shippingFeeTypeDto.getShippingFeeTypeName());
        }

        // Update fields
        existingType.setShippingFeeTypeName(shippingFeeTypeDto.getShippingFeeTypeName());
        existingType.setShippingFeeTypeDisplayName(shippingFeeTypeDto.getShippingFeeTypeDisplayName());
        existingType.setShippingFeeTypeRemark(shippingFeeTypeDto.getShippingFeeTypeRemark());
        existingType.setShippingFeeTypeEnabled(shippingFeeTypeDto.getShippingFeeTypeEnabled());

        ShippingFeeType updatedType = shippingFeeTypeRepository.save(existingType);
        logger.info("Updated shipping fee type with ID: {}", updatedType.getShippingFeeTypeId());
        
        return shipmentMapperUtil.mapShippingFeeTypeToDto(updatedType);
    }

    @Override
    public void deleteShippingFeeType(Long shippingFeeTypeId) {
        logger.debug("Deleting shipping fee type with ID: {}", shippingFeeTypeId);
        
        if (!shippingFeeTypeRepository.existsById(shippingFeeTypeId)) {
            throw new ResourceNotFoundException("Shipping fee type not found with ID: " + shippingFeeTypeId);
        }

        shippingFeeTypeRepository.deleteById(shippingFeeTypeId);
        logger.info("Deleted shipping fee type with ID: {}", shippingFeeTypeId);
    }

    @Override
    public ShippingFeeTypeDto toggleShippingFeeTypeEnabled(Long shippingFeeTypeId) {
        logger.debug("Toggling enabled status for shipping fee type ID: {}", shippingFeeTypeId);
        
        ShippingFeeType feeType = shippingFeeTypeRepository.findById(shippingFeeTypeId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipping fee type not found with ID: " + shippingFeeTypeId));

        feeType.setShippingFeeTypeEnabled(!feeType.getShippingFeeTypeEnabled());
        ShippingFeeType updatedType = shippingFeeTypeRepository.save(feeType);
        logger.info("Toggled enabled status for shipping fee type ID: {} to: {}", 
                   shippingFeeTypeId, updatedType.getShippingFeeTypeEnabled());
        
        return shipmentMapperUtil.mapShippingFeeTypeToDto(updatedType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShippingFeeTypeDto> searchShippingFeeTypesByCriteria(
            Long shippingFeeTypeId, String shippingFeeTypeName, Boolean shippingFeeTypeEnabled,
            String textSearch, Pageable pageable) {
        
        logger.debug("Searching shipping fee types with criteria");
        
        Page<ShippingFeeType> typePage = shippingFeeTypeRepository.findByCriteria(
            shippingFeeTypeId, shippingFeeTypeName, shippingFeeTypeEnabled, textSearch, pageable
        );
        
        return typePage.map(shipmentMapperUtil::mapShippingFeeTypeToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllEnabledShippingFeeTypeNames() {
        logger.debug("Getting all enabled shipping fee type names");
        return shippingFeeTypeRepository.findAllEnabledShippingFeeTypeNames();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long shippingFeeTypeId) {
        return shippingFeeTypeRepository.existsById(shippingFeeTypeId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isShippingFeeTypeNameUnique(String shippingFeeTypeName, Long excludeId) {
        if (excludeId != null) {
            return !shippingFeeTypeRepository.existsByShippingFeeTypeNameAndShippingFeeTypeIdNot(shippingFeeTypeName, excludeId);
        }
        return !shippingFeeTypeRepository.existsByShippingFeeTypeName(shippingFeeTypeName);
    }
}