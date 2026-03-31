package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ShippingFeeDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.ShippingFee;
import org.artanddecor.model.ShippingFeeType;
import org.artanddecor.repository.ShippingFeeRepository;
import org.artanddecor.repository.ShippingFeeTypeRepository;
import org.artanddecor.services.ShippingFeeService;
import org.artanddecor.utils.ShipmentMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of ShippingFeeService interface
 * Business logic for shipping fee management operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ShippingFeeServiceImpl implements ShippingFeeService {

    private static final Logger logger = LoggerFactory.getLogger(ShippingFeeServiceImpl.class);

    private final ShippingFeeRepository shippingFeeRepository;
    private final ShippingFeeTypeRepository shippingFeeTypeRepository;
    private final ShipmentMapperUtil shipmentMapperUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<ShippingFeeDto> getAllShippingFees(Pageable pageable) {
        logger.debug("Getting all shipping fees with pagination: {}", pageable);
        Page<ShippingFee> feePage = shippingFeeRepository.findAll(pageable);
        return feePage.map(shipmentMapperUtil::mapShippingFeeToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingFeeDto getShippingFeeById(Long shippingFeeId) {
        logger.debug("Getting shipping fee by ID: {}", shippingFeeId);
        ShippingFee shippingFee = shippingFeeRepository.findById(shippingFeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipping fee not found with ID: " + shippingFeeId));
        return shipmentMapperUtil.mapShippingFeeToDto(shippingFee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingFeeDto> getShippingFeesByTypeId(Long shippingFeeTypeId) {
        logger.debug("Getting shipping fees by type ID: {}", shippingFeeTypeId);
        List<ShippingFee> fees = shippingFeeRepository.findByShippingFeeTypeShippingFeeTypeIdOrderByMinOrderPrice(shippingFeeTypeId);
        return fees.stream()
            .map(shipmentMapperUtil::mapShippingFeeToDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingFeeDto> getAllEnabledShippingFees() {
        logger.debug("Getting all enabled shipping fees");
        List<ShippingFee> fees = shippingFeeRepository.findByShippingFeeEnabledTrueOrderByMinOrderPrice();
        return fees.stream()
            .map(shipmentMapperUtil::mapShippingFeeToDto)
            .toList();
    }

    @Override
    public ShippingFeeDto createShippingFee(ShippingFeeDto shippingFeeDto) {
        logger.debug("Creating new shipping fee for type ID: {}", shippingFeeDto.getShippingFeeTypeId());
        
        // Validate shipping fee type exists
        ShippingFeeType feeType = shippingFeeTypeRepository.findById(shippingFeeDto.getShippingFeeTypeId())
            .orElseThrow(() -> new ResourceNotFoundException("Shipping fee type not found with ID: " + shippingFeeDto.getShippingFeeTypeId()));

        // Validate price range logic
        if (shippingFeeDto.getMinOrderPrice().compareTo(shippingFeeDto.getMaxOrderPrice()) > 0) {
            throw new IllegalArgumentException("Min order price cannot be greater than max order price");
        }

        // Check for overlapping price ranges
        if (isPriceRangeOverlapping(shippingFeeDto.getShippingFeeTypeId(), 
                                  shippingFeeDto.getMinOrderPrice(), 
                                  shippingFeeDto.getMaxOrderPrice(), null)) {
            throw new IllegalArgumentException("Price range overlaps with existing shipping fee");
        }

        ShippingFee shippingFee = shipmentMapperUtil.mapShippingFeeToEntity(shippingFeeDto);
        shippingFee.setShippingFeeType(feeType);

        ShippingFee savedFee = shippingFeeRepository.save(shippingFee);
        logger.info("Created shipping fee with ID: {}", savedFee.getShippingFeeId());
        
        return shipmentMapperUtil.mapShippingFeeToDto(savedFee);
    }

    @Override
    public ShippingFeeDto updateShippingFee(Long shippingFeeId, ShippingFeeDto shippingFeeDto) {
        logger.debug("Updating shipping fee with ID: {}", shippingFeeId);
        
        ShippingFee existingFee = shippingFeeRepository.findById(shippingFeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipping fee not found with ID: " + shippingFeeId));

        // Validate price range logic
        if (shippingFeeDto.getMinOrderPrice().compareTo(shippingFeeDto.getMaxOrderPrice()) > 0) {
            throw new IllegalArgumentException("Min order price cannot be greater than max order price");
        }

        // Check for overlapping price ranges (excluding current record)
        if (isPriceRangeOverlapping(shippingFeeDto.getShippingFeeTypeId(),
                                  shippingFeeDto.getMinOrderPrice(),
                                  shippingFeeDto.getMaxOrderPrice(), shippingFeeId)) {
            throw new IllegalArgumentException("Price range overlaps with existing shipping fee");
        }

        // Update shipping fee type if changed
        if (!existingFee.getShippingFeeType().getShippingFeeTypeId().equals(shippingFeeDto.getShippingFeeTypeId())) {
            ShippingFeeType feeType = shippingFeeTypeRepository.findById(shippingFeeDto.getShippingFeeTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Shipping fee type not found with ID: " + shippingFeeDto.getShippingFeeTypeId()));
            existingFee.setShippingFeeType(feeType);
        }

        // Update fields
        existingFee.setMinOrderPrice(shippingFeeDto.getMinOrderPrice());
        existingFee.setMaxOrderPrice(shippingFeeDto.getMaxOrderPrice());
        existingFee.setShippingFeeValue(shippingFeeDto.getShippingFeeValue());
        existingFee.setShippingFeeDisplayName(shippingFeeDto.getShippingFeeDisplayName());
        existingFee.setShippingFeeRemark(shippingFeeDto.getShippingFeeRemark());
        existingFee.setShippingFeeEnabled(shippingFeeDto.getShippingFeeEnabled());

        ShippingFee updatedFee = shippingFeeRepository.save(existingFee);
        logger.info("Updated shipping fee with ID: {}", updatedFee.getShippingFeeId());
        
        return shipmentMapperUtil.mapShippingFeeToDto(updatedFee);
    }

    @Override
    public void deleteShippingFee(Long shippingFeeId) {
        logger.debug("Deleting shipping fee with ID: {}", shippingFeeId);
        
        if (!shippingFeeRepository.existsById(shippingFeeId)) {
            throw new ResourceNotFoundException("Shipping fee not found with ID: " + shippingFeeId);
        }

        shippingFeeRepository.deleteById(shippingFeeId);
        logger.info("Deleted shipping fee with ID: {}", shippingFeeId);
    }

    @Override
    public ShippingFeeDto toggleShippingFeeEnabled(Long shippingFeeId) {
        logger.debug("Toggling enabled status for shipping fee ID: {}", shippingFeeId);
        
        ShippingFee shippingFee = shippingFeeRepository.findById(shippingFeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Shipping fee not found with ID: " + shippingFeeId));

        shippingFee.setShippingFeeEnabled(!shippingFee.getShippingFeeEnabled());
        ShippingFee updatedFee = shippingFeeRepository.save(shippingFee);
        logger.info("Toggled enabled status for shipping fee ID: {} to: {}", 
                   shippingFeeId, updatedFee.getShippingFeeEnabled());
        
        return shipmentMapperUtil.mapShippingFeeToDto(updatedFee);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShippingFeeDto> searchShippingFeesByCriteria(
            Long shippingFeeId, Long shippingFeeTypeId, BigDecimal minOrderPrice, BigDecimal maxOrderPrice,
            BigDecimal minShippingFeeValue, BigDecimal maxShippingFeeValue, Boolean shippingFeeEnabled,
            String textSearch, Pageable pageable) {
        
        logger.debug("Searching shipping fees with criteria");
        
        Page<ShippingFee> feePage = shippingFeeRepository.findByCriteria(
            shippingFeeId, shippingFeeTypeId, minOrderPrice, maxOrderPrice,
            minShippingFeeValue, maxShippingFeeValue, shippingFeeEnabled,
            textSearch, pageable
        );
        
        return feePage.map(shipmentMapperUtil::mapShippingFeeToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingFeeDto calculateShippingFee(BigDecimal orderAmount) {
        logger.debug("Calculating shipping fee for order amount: {}", orderAmount);
        
        ShippingFee cheapestFee = shippingFeeRepository.findCheapestApplicableShippingFee(orderAmount)
            .orElse(null);
            
        if (cheapestFee == null) {
            logger.warn("No applicable shipping fee found for order amount: {}", orderAmount);
            return null;
        }
        
        return shipmentMapperUtil.mapShippingFeeToDto(cheapestFee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingFeeDto> getApplicableShippingFees(BigDecimal orderAmount) {
        logger.debug("Getting applicable shipping fees for order amount: {}", orderAmount);
        
        List<ShippingFee> fees = shippingFeeRepository.findApplicableShippingFees(orderAmount);
        return fees.stream()
            .map(shipmentMapperUtil::mapShippingFeeToDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long shippingFeeId) {
        return shippingFeeRepository.existsById(shippingFeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPriceRangeOverlapping(Long shippingFeeTypeId, BigDecimal minPrice, BigDecimal maxPrice, Long excludeId) {
        Long excludeIdValue = excludeId != null ? excludeId : -1L;
        List<ShippingFee> overlappingFees = shippingFeeRepository.findOverlappingPriceRanges(
            shippingFeeTypeId, minPrice, maxPrice, excludeIdValue
        );
        return !overlappingFees.isEmpty();
    }
}