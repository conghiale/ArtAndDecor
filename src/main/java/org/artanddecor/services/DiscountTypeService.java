package org.artanddecor.services;

import org.artanddecor.dto.DiscountTypeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * DiscountType Service Interface for business logic operations
 */
public interface DiscountTypeService {

    /**
     * Get all discount types with pagination
     * @param pageable Pagination information
     * @return Page of discount types
     */
    Page<DiscountTypeDto> getAllDiscountTypes(Pageable pageable);

    /**
     * Get discount type by ID
     * @param discountTypeId Discount type ID
     * @return DiscountTypeDto if found
     */
    DiscountTypeDto getDiscountTypeById(Long discountTypeId);

    /**
     * Get discount type by name
     * @param discountTypeName Discount type name
     * @return DiscountTypeDto if found
     */
    DiscountTypeDto getDiscountTypeByName(String discountTypeName);

    /**
     * Get all enabled discount types
     * @return List of enabled discount types
     */
    List<DiscountTypeDto> getAllEnabledDiscountTypes();

    /**
     * Create new discount type
     * @param discountTypeDto Discount type data
     * @return Created discount type
     */
    DiscountTypeDto createDiscountType(DiscountTypeDto discountTypeDto);

    /**
     * Update existing discount type
     * @param discountTypeId Discount type ID
     * @param discountTypeDto Updated discount type data
     * @return Updated discount type
     */
    DiscountTypeDto updateDiscountType(Long discountTypeId, DiscountTypeDto discountTypeDto);

    /**
     * Delete discount type
     * @param discountTypeId Discount type ID
     */
    void deleteDiscountType(Long discountTypeId);

    /**
     * Toggle enabled status of discount type
     * @param discountTypeId Discount type ID
     * @return Updated discount type
     */
    DiscountTypeDto toggleDiscountTypeEnabled(Long discountTypeId);

    /**
     * Search discount types by multiple criteria
     * @param discountTypeId Filter by discount type ID (optional)
     * @param discountTypeName Filter by discount type name (optional)
     * @param discountTypeEnabled Filter by enabled status (optional)
     * @param textSearch Search text in name, display name, description (optional)
     * @param pageable Pagination information
     * @return Page of discount types matching criteria
     */
    Page<DiscountTypeDto> searchDiscountTypesByCriteria(
            Long discountTypeId,
            String discountTypeName,
            Boolean discountTypeEnabled,
            String textSearch,
            Pageable pageable);

    /**
     * Get all discount type names for dropdown/combobox usage
     * @return List of enabled discount type names
     */
    List<String> getAllEnabledDiscountTypeNames();

    /**
     * Validate if discount type exists
     * @param discountTypeId Discount type ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long discountTypeId);

    /**
     * Validate if discount type name is unique
     * @param discountTypeName Discount type name
     * @param excludeId ID to exclude from uniqueness check (for updates)
     * @return true if unique, false otherwise
     */
    boolean isDiscountTypeNameUnique(String discountTypeName, Long excludeId);
}