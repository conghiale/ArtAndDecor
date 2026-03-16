package org.artanddecor.services;

import org.artanddecor.dto.DiscountDto;
import org.artanddecor.dto.DiscountTypeDto;
import org.artanddecor.dto.DiscountValidationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Discount Service Interface for business logic operations
 */
public interface DiscountService {

    /**
     * Get all discounts with pagination
     * @param pageable Pagination information
     * @return Page of discounts
     */
    Page<DiscountDto> getAllDiscounts(Pageable pageable);

    /**
     * Get discount by ID
     * @param discountId Discount ID
     * @return DiscountDto if found
     */
    DiscountDto getDiscountById(Long discountId);

    /**
     * Get discount by code
     * @param discountCode Discount code
     * @return DiscountDto if found
     */
    DiscountDto getDiscountByCode(String discountCode);

    /**
     * Get all active discounts (enabled and within date range)
     * @return List of active discounts
     */
    List<DiscountDto> getAllActiveDiscounts();

    /**
     * Get enabled discounts by discount type
     * @param discountTypeId Discount type ID
     * @return List of discounts by type
     */
    List<DiscountDto> getDiscountsByTypeId(Long discountTypeId);

    /**
     * Validate and get valid discount by code
     * @param discountCode Discount code
     * @return Valid discount if available
     */
    DiscountDto getValidDiscountByCode(String discountCode);

    /**
     * Create new discount
     * @param discountDto Discount data
     * @return Created discount
     */
    DiscountDto createDiscount(DiscountDto discountDto);

    /**
     * Update existing discount
     * @param discountId Discount ID
     * @param discountDto Updated discount data
     * @return Updated discount
     */
    DiscountDto updateDiscount(Long discountId, DiscountDto discountDto);

    /**
     * Delete discount
     * @param discountId Discount ID
     */
    void deleteDiscount(Long discountId);

    /**
     * Toggle enabled status of discount
     * @param discountId Discount ID
     * @return Updated discount
     */
    DiscountDto toggleDiscountEnabled(Long discountId);

    /**
     * Apply discount to order amount
     * @param discountId Discount ID
     * @param originalAmount Original order amount
     * @return Calculated discount amount
     */
    BigDecimal calculateDiscountAmount(Long discountId, BigDecimal originalAmount);

    /**
     * Apply discount by code to order amount
     * @param discountCode Discount code
     * @param originalAmount Original order amount
     * @return Calculated discount amount
     */
    BigDecimal calculateDiscountAmountByCode(String discountCode, BigDecimal originalAmount);

    /**
     * Increment discount usage count
     * @param discountId Discount ID
     */
    void incrementDiscountUsage(Long discountId);

    /**
     * Check if discount can be used (not exceeded max usage)
     * @param discountId Discount ID
     * @return true if can be used, false otherwise
     */
    boolean canUseDiscount(Long discountId);

    /**
     * Search discounts by multiple criteria
     * @param discountId Filter by discount ID (optional)
     * @param discountCode Filter by discount code (optional)
     * @param discountName Filter by discount name (optional)
     * @param discountTypeId Filter by discount type ID (optional)
     * @param discountEnabled Filter by enabled status (optional)
     * @param isActive Filter by active status (optional, based on date and enabled)
     * @param minValue Filter by minimum value (optional)
     * @param maxValue Filter by maximum value (optional)
     * @param minPercentage Filter by minimum percentage (optional)
     * @param maxPercentage Filter by maximum percentage (optional)
     * @param startDateFrom Filter by start date from (optional)
     * @param startDateTo Filter by start date to (optional)
     * @param endDateFrom Filter by end date from (optional)
     * @param endDateTo Filter by end date to (optional)
     * @param textSearch Search text in code, name, description (optional)
     * @param pageable Pagination information
     * @return Page of discounts matching criteria
     */
    Page<DiscountDto> searchDiscountsByCriteria(
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
            Pageable pageable);

    /**
     * Get all discount codes for validation usage
     * @return List of all discount codes
     */
    List<String> getAllDiscountCodes();

    /**
     * Validate if discount exists
     * @param discountId Discount ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long discountId);

    /**
     * Validate if discount code is unique
     * @param discountCode Discount code
     * @param excludeId ID to exclude from uniqueness check (for updates)
     * @return true if unique, false otherwise
     */
    boolean isDiscountCodeUnique(String discountCode, Long excludeId);
    
    /**
     * Validate discount code with cart details (API 11)
     * @param code Discount code
     * @param cartAmount Cart amount
     * @param productIds List of product IDs
     * @return Discount validation result
     */
    DiscountValidationResult validateDiscountCode(String code, BigDecimal cartAmount, List<Long> productIds);
    
    /**
     * Get discounts with filters (API 12)
     * @param code Filter by code (optional)
     * @param active Filter by active status (optional)
     * @param expired Filter by expired status (optional)
     * @param discountType Filter by discount type (optional)
     * @param fromDate Filter from date (optional)
     * @param toDate Filter to date (optional)
     * @return List of filtered discounts
     */
    List<DiscountDto> getDiscountsWithFilters(
            String code, 
            Boolean active, 
            Boolean expired, 
            String discountType, 
            LocalDate fromDate, 
            LocalDate toDate);
    
    /**
     * Get all discount types (API 15)
     * @return List of all discount types
     */
    List<DiscountTypeDto> getAllDiscountTypes();
}