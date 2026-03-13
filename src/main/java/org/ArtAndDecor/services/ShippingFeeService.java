package org.ArtAndDecor.services;

import org.ArtAndDecor.dto.ShippingFeeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for ShippingFee business operations
 */
public interface ShippingFeeService {

    /**
     * Get all shipping fees with pagination
     */
    Page<ShippingFeeDto> getAllShippingFees(Pageable pageable);

    /**
     * Get shipping fee by ID
     */
    ShippingFeeDto getShippingFeeById(Long shippingFeeId);

    /**
     * Get shipping fees by type ID
     */
    List<ShippingFeeDto> getShippingFeesByTypeId(Long shippingFeeTypeId);

    /**
     * Get all enabled shipping fees
     */
    List<ShippingFeeDto> getAllEnabledShippingFees();

    /**
     * Create new shipping fee
     */
    ShippingFeeDto createShippingFee(ShippingFeeDto shippingFeeDto);

    /**
     * Update existing shipping fee
     */
    ShippingFeeDto updateShippingFee(Long shippingFeeId, ShippingFeeDto shippingFeeDto);

    /**
     * Delete shipping fee by ID
     */
    void deleteShippingFee(Long shippingFeeId);

    /**
     * Toggle shipping fee enabled status
     */
    ShippingFeeDto toggleShippingFeeEnabled(Long shippingFeeId);

    /**
     * Search shipping fees by criteria
     */
    Page<ShippingFeeDto> searchShippingFeesByCriteria(
            Long shippingFeeId,
            Long shippingFeeTypeId,
            BigDecimal minOrderPrice,
            BigDecimal maxOrderPrice,
            BigDecimal minShippingFeeValue,
            BigDecimal maxShippingFeeValue,
            Boolean shippingFeeEnabled,
            String textSearch,
            Pageable pageable);

    /**
     * Calculate shipping fee for order amount
     */
    ShippingFeeDto calculateShippingFee(BigDecimal orderAmount);

    /**
     * Get applicable shipping fees for order amount
     */
    List<ShippingFeeDto> getApplicableShippingFees(BigDecimal orderAmount);

    /**
     * Check if shipping fee exists by ID
     */
    boolean existsById(Long shippingFeeId);

    /**
     * Check if price ranges overlap for same type
     */
    boolean isPriceRangeOverlapping(Long shippingFeeTypeId, BigDecimal minPrice, BigDecimal maxPrice, Long excludeId);
}