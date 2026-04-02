package org.artanddecor.services;

import org.artanddecor.dto.ProductStateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * ProductState Service Interface
 * Defines business operations for product state management
 */
public interface ProductStateService {

    // =============================================
    // ADMIN-FOCUSED OPERATIONS
    // =============================================

    /**
     * Find product state by ID for admin management
     */
    Optional<ProductStateDto> findProductStateById(Long productStateId);

    /**
     * Get product states by multiple criteria with pagination (all parameters optional)
     * @param textSearch Text search in name, displayName, remark (partial match, case-insensitive)
     * @param enabled Filter by enabled status
     * @param pageable Pagination and sorting information
     * @return Page of ProductStateDto matching criteria
     */
    Page<ProductStateDto> getProductStatesByCriteria(String textSearch, Boolean enabled, Pageable pageable);
}