package org.artanddecor.services.impl;

import org.artanddecor.dto.DiscountDto;
import org.artanddecor.dto.DiscountTypeDto;
import org.artanddecor.dto.DiscountValidationResult;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.Discount;
import org.artanddecor.model.DiscountType;
import org.artanddecor.repository.DiscountRepository;
import org.artanddecor.repository.DiscountTypeRepository;
import org.artanddecor.services.DiscountService;
import org.artanddecor.utils.DiscountMapperUtil;
import org.artanddecor.utils.DiscountTypeMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Discount Service Implementation for business logic operations
 */
@Service
@Transactional
public class DiscountServiceImpl implements DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private DiscountTypeRepository discountTypeRepository;

    @Autowired
    private DiscountMapperUtil discountMapperUtil;

    @Autowired
    private DiscountTypeMapperUtil discountTypeMapperUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<DiscountDto> getAllDiscounts(Pageable pageable) {
        Page<Discount> discountsPage = discountRepository.findAll(pageable);
        return discountsPage.map(discountMapperUtil::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountDto getDiscountById(Long discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found with ID: " + discountId));
        return discountMapperUtil.mapToDto(discount);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountDto getDiscountByCode(String discountCode) {
        Discount discount = discountRepository.findByDiscountCode(discountCode)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found with code: " + discountCode));
        return discountMapperUtil.mapToDto(discount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountDto> getAllActiveDiscounts() {
        LocalDateTime now = LocalDateTime.now();
        List<Discount> activeDiscounts = discountRepository.findActiveDiscounts(now);
        return activeDiscounts.stream()
                .map(discountMapperUtil::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountDto> getDiscountsByTypeId(Long discountTypeId) {
        List<Discount> discountsByType = discountRepository.findByDiscountTypeIdAndDiscountEnabled(discountTypeId, true);
        return discountsByType.stream()
                .map(discountMapperUtil::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountDto getValidDiscountByCode(String discountCode) {
        LocalDateTime now = LocalDateTime.now();
        Discount discount = discountRepository.findValidDiscountByCode(discountCode, now)
                .orElseThrow(() -> new ResourceNotFoundException("Valid discount not found with code: " + discountCode));
        return discountMapperUtil.mapToDto(discount);
    }

    @Override
    public DiscountDto createDiscount(DiscountDto discountDto) {
        // Validate uniqueness
        if (discountDto.getDiscountCode() != null && !isDiscountCodeUnique(discountDto.getDiscountCode(), null)) {
            throw new IllegalArgumentException("Discount code already exists: " + discountDto.getDiscountCode());
        }

        // Validate discount type exists
        if (discountDto.getDiscountType() != null && discountDto.getDiscountType().getDiscountTypeId() != null) {
            DiscountType discountType = discountTypeRepository.findById(discountDto.getDiscountType().getDiscountTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Discount Type not found with ID: " + discountDto.getDiscountType().getDiscountTypeId()));
        }

        Discount discount = discountMapperUtil.mapToEntity(discountDto);
        discount.setCreatedDt(LocalDateTime.now());
        discount.setModifiedDt(LocalDateTime.now());
        
        Discount savedDiscount = discountRepository.save(discount);
        return discountMapperUtil.mapToDto(savedDiscount);
    }

    @Override
    public DiscountDto updateDiscount(Long discountId, DiscountDto discountDto) {
        Discount existingDiscount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found with ID: " + discountId));

        // Validate uniqueness (excluding current record)
        if (discountDto.getDiscountCode() != null && !isDiscountCodeUnique(discountDto.getDiscountCode(), discountId)) {
            throw new IllegalArgumentException("Discount code already exists: " + discountDto.getDiscountCode());
        }

        // Update fields
        if (discountDto.getDiscountCode() != null) {
            existingDiscount.setDiscountCode(discountDto.getDiscountCode());
        }
        if (discountDto.getDiscountName() != null) {
            existingDiscount.setDiscountName(discountDto.getDiscountName());
        }
        if (discountDto.getDiscountRemark() != null) {
            existingDiscount.setDiscountRemark(discountDto.getDiscountRemark());
        }
        if (discountDto.getDiscountValue() != null) {
            existingDiscount.setDiscountValue(discountDto.getDiscountValue());
        }
        if (discountDto.getTotalUsageLimit() != null) {
            existingDiscount.setTotalUsageLimit(discountDto.getTotalUsageLimit());
        }
        if (discountDto.getStartAt() != null) {
            existingDiscount.setStartAt(discountDto.getStartAt());
        }
        if (discountDto.getEndAt() != null) {
            existingDiscount.setEndAt(discountDto.getEndAt());
        }
        if (discountDto.getIsActive() != null) {
            existingDiscount.setIsActive(discountDto.getIsActive());
        }

        existingDiscount.setModifiedDt(LocalDateTime.now());

        Discount updatedDiscount = discountRepository.save(existingDiscount);
        return discountMapperUtil.mapToDto(updatedDiscount);
    }

    @Override
    public void deleteDiscount(Long discountId) {
        if (!discountRepository.existsById(discountId)) {
            throw new ResourceNotFoundException("Discount not found with ID: " + discountId);
        }
        // Note: Check if discount is used in orders before deletion
        discountRepository.deleteById(discountId);
    }

    @Override
    public DiscountDto toggleDiscountEnabled(Long discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found with ID: " + discountId));

        discount.setIsActive(!discount.getIsActive());
        discount.setModifiedDt(LocalDateTime.now());

        Discount updatedDiscount = discountRepository.save(discount);
        return discountMapperUtil.mapToDto(updatedDiscount);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDiscountAmount(Long discountId, BigDecimal originalAmount) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found with ID: " + discountId));

        return calculateDiscount(discount, originalAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDiscountAmountByCode(String discountCode, BigDecimal originalAmount) {
        LocalDateTime now = LocalDateTime.now();
        Discount discount = discountRepository.findValidDiscountByCode(discountCode, now)
                .orElseThrow(() -> new ResourceNotFoundException("Valid discount not found with code: " + discountCode));

        return calculateDiscount(discount, originalAmount);
    }

    private BigDecimal calculateDiscount(Discount discount, BigDecimal originalAmount) {
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (discount.getDiscountValue() != null && discount.getDiscountValue().compareTo(BigDecimal.ZERO) > 0) {
            // Check discount type to determine if it's percentage or fixed amount
            if (discount.getDiscountType() != null) {
                String typeName = discount.getDiscountType().getDiscountTypeName();
                if ("PERCENTAGE".equalsIgnoreCase(typeName)) {
                    // Percentage discount
                    discountAmount = originalAmount.multiply(discount.getDiscountValue()).divide(new BigDecimal(100));
                } else {
                    // Fixed amount discount
                    discountAmount = discount.getDiscountValue();
                }
            } else {
                // Default to fixed amount if no type specified
                discountAmount = discount.getDiscountValue();
            }
        }

        // Ensure discount doesn't exceed original amount and max discount limit
        BigDecimal maxDiscount = discount.getMaxDiscountAmount() != null ? 
            discount.getMaxDiscountAmount() : originalAmount;
        return discountAmount.min(originalAmount).min(maxDiscount);
    }

    @Override
    public void incrementDiscountUsage(Long discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found with ID: " + discountId));

        discount.setUsedCount(discount.getUsedCount() + 1);
        discount.setModifiedDt(LocalDateTime.now());
        discountRepository.save(discount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUseDiscount(Long discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found with ID: " + discountId));

        return discount.getTotalUsageLimit() == null || 
               discount.getUsedCount() < discount.getTotalUsageLimit();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DiscountDto> searchDiscountsByCriteria(
            Long discountId,
            String discountCode,
            String discountName,
            Long discountTypeId,
            Boolean discountEnabled,
            Boolean isActive,
            BigDecimal minValue,
            BigDecimal maxValue,
            LocalDateTime startDateFrom,
            LocalDateTime startDateTo,
            LocalDateTime endDateFrom,
            LocalDateTime endDateTo,
            String textSearch,
            Pageable pageable) {

        LocalDateTime currentDate = LocalDateTime.now();
        
        Page<Discount> discountsPage = discountRepository.findDiscountsByCriteria(
                discountId, discountCode, discountName, discountTypeId, discountEnabled, isActive,
                minValue, maxValue, startDateFrom, startDateTo,
                endDateFrom, endDateTo, textSearch, currentDate, pageable);
        
        return discountsPage.map(discountMapperUtil::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllDiscountCodes() {
        return discountRepository.findAllDiscountCodes();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long discountId) {
        return discountRepository.existsById(discountId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDiscountCodeUnique(String discountCode, Long excludeId) {
        return discountRepository.findByDiscountCode(discountCode)
                .map(existingDiscount -> excludeId != null && existingDiscount.getDiscountId().equals(excludeId))
                .orElse(true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountDto> getDiscountsWithFilters(
            String code, 
            Boolean active, 
            Boolean expired, 
            String discountType, 
            LocalDate fromDate, 
            LocalDate toDate) {
        
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;
        LocalDateTime currentDate = LocalDateTime.now();
        
        // Convert discountType name to discountTypeId if needed
        Long discountTypeId = null;
        if (discountType != null && !discountType.trim().isEmpty()) {
            try {
                DiscountType foundDiscountType = discountTypeRepository.findByDiscountTypeName(discountType)
                        .orElse(null);
                if (foundDiscountType != null) {
                    discountTypeId = foundDiscountType.getDiscountTypeId();
                }
            } catch (Exception e) {
                // Ignore and continue with null discountTypeId
            }
        }
        
        Page<Discount> discounts = discountRepository.findDiscountsByCriteria(
                null, // discountId
                code, // discountCode
                null, // discountName
                discountTypeId, // discountTypeId
                active, // discountEnabled
                !Boolean.TRUE.equals(expired), // isActive (opposite of expired)
                null, // minValue
                null, // maxValue
                fromDateTime, // startDateFrom
                toDateTime, // startDateTo
                fromDateTime, // endDateFrom  
                toDateTime, // endDateTo
                null, // textSearch
                currentDate, // currentDate
                PageRequest.of(0, Integer.MAX_VALUE)
        );
        
        return discounts.getContent().stream()
                .map(discountMapperUtil::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountTypeDto> getAllDiscountTypes() {
        return discountTypeRepository.findByDiscountTypeEnabledOrderByDiscountTypeName(true)
                .stream()
                .map(discountTypeMapperUtil::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountValidationResult validateDiscountCode(String code, BigDecimal cartAmount, List<Long> productIds) {
        DiscountValidationResult result = new DiscountValidationResult();
        result.setValid(false);
        result.setDiscountCode(code);
        
        if (code == null || code.trim().isEmpty()) {
            result.setMessage("Discount code is required");
            return result;
        }
        
        try {
            // Get valid discount
            DiscountDto discount = getValidDiscountByCode(code);
            
            if (discount == null) {
                result.setMessage("Invalid or expired discount code");
                return result;
            }
            
            // Check if discount can be used (usage limits)
            if (!canUseDiscount(discount.getDiscountId())) {
                result.setMessage("Discount code has reached its usage limit");
                return result;
            }
            
            // Calculate discount amount
            BigDecimal discountAmount = calculateDiscountAmountByCode(code, cartAmount);
            BigDecimal finalAmount = cartAmount.subtract(discountAmount);
            
            result.setValid(true);
            result.setDiscountAmount(discountAmount);
            result.setDiscountValue(discount.getDiscountValue());
            result.setDiscountType(discount.getDiscountType() != null ? discount.getDiscountType().getDiscountTypeName() : null);
            result.setFinalAmount(finalAmount);
            result.setRemainingUsage(discount.getTotalUsageLimit() != null ? 
                (discount.getTotalUsageLimit() - discount.getUsedCount()) : null);
            result.setMessage("Discount code is valid");
            
        } catch (Exception e) {
            result.setMessage("Error validating discount code: " + e.getMessage());
        }
        
        return result;
    }
}