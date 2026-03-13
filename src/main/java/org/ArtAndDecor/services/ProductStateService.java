package org.ArtAndDecor.services;

import org.ArtAndDecor.dto.ProductStateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * ProductState Service Interface
 * Defines business operations for product state management
 */
public interface ProductStateService {

    // =============================================
    // CUSTOMER-FOCUSED OPERATIONS (name > ID priority)
    // =============================================

    /**
     * Find product state by name for customer view
     */
    Optional<ProductStateDto> findProductStateByName(String productStateName);

    /**
     * Get product states by multiple criteria with pagination (all parameters optional)
     * @param textSearch Text search in name, displayName, remark (partial match, case-insensitive)
     * @param enabled Filter by enabled status
     * @param pageable Pagination and sorting information
     * @return Page of ProductStateDto matching criteria
     */
    Page<ProductStateDto> getProductStatesByCriteria(String textSearch, Boolean enabled, Pageable pageable);

    // =============================================
    // ADMIN-FOCUSED OPERATIONS (ID > name priority)
    // =============================================

    /**
     * Find product state by ID for admin management
     */
    Optional<ProductStateDto> findProductStateById(Long productStateId);

    // =============================================
    // CRUD OPERATIONS
    // =============================================

    /**
     * Create new product state
     */
    ProductStateDto createProductState(ProductStateDto productStateDto);

    /**
     * Update existing product state
     */
    ProductStateDto updateProductState(Long productStateId, ProductStateDto productStateDto);

    /**
     * Delete product state by ID
     */
    void deleteProductStateById(Long productStateId);

    // =============================================
    // UTILITY OPERATIONS
    // =============================================

    /**
     * Get total count of product states
     */
    long getTotalProductStateCount();

    /**
     * Check if name exists (for validation)
     */
    boolean existsByName(String productStateName);
}