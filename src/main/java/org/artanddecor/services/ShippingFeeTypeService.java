package org.artanddecor.services;

import org.artanddecor.dto.ShippingFeeTypeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for ShippingFeeType business operations
 */
public interface ShippingFeeTypeService {

    /**
     * Get all shipping fee types with pagination
     */
    Page<ShippingFeeTypeDto> getAllShippingFeeTypes(Pageable pageable);

    /**
     * Get shipping fee type by ID
     */
    ShippingFeeTypeDto getShippingFeeTypeById(Long shippingFeeTypeId);

    /**
     * Get shipping fee type by name
     */
    ShippingFeeTypeDto getShippingFeeTypeByName(String shippingFeeTypeName);

    /**
     * Get all enabled shipping fee types
     */
    List<ShippingFeeTypeDto> getAllEnabledShippingFeeTypes();

    /**
     * Create new shipping fee type
     */
    ShippingFeeTypeDto createShippingFeeType(ShippingFeeTypeDto shippingFeeTypeDto);

    /**
     * Update existing shipping fee type
     */
    ShippingFeeTypeDto updateShippingFeeType(Long shippingFeeTypeId, ShippingFeeTypeDto shippingFeeTypeDto);

    /**
     * Delete shipping fee type by ID
     */
    void deleteShippingFeeType(Long shippingFeeTypeId);

    /**
     * Toggle shipping fee type enabled status
     */
    ShippingFeeTypeDto toggleShippingFeeTypeEnabled(Long shippingFeeTypeId);

    /**
     * Search shipping fee types by criteria
     */
    Page<ShippingFeeTypeDto> searchShippingFeeTypesByCriteria(
            Long shippingFeeTypeId,
            String shippingFeeTypeName,
            Boolean shippingFeeTypeEnabled,
            String textSearch,
            Pageable pageable);

    /**
     * Get all enabled shipping fee type names
     */
    List<String> getAllEnabledShippingFeeTypeNames();

    /**
     * Check if shipping fee type exists by ID
     */
    boolean existsById(Long shippingFeeTypeId);

    /**
     * Check if shipping fee type name is unique
     */
    boolean isShippingFeeTypeNameUnique(String shippingFeeTypeName, Long excludeId);
}