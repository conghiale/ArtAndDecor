package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ProductStateDto;
import org.artanddecor.model.ProductState;
import org.artanddecor.repository.ProductStateRepository;
import org.artanddecor.services.ProductStateService;
import org.artanddecor.utils.ProductMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * ProductState Service Implementation
 * Handles business logic for product state management
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductStateServiceImpl implements ProductStateService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductStateServiceImpl.class);
    
    private final ProductStateRepository productStateRepository;

    // =============================================
    // ADMIN-FOCUSED OPERATIONS
    // =============================================

    @Override
    public Optional<ProductStateDto> findProductStateById(Long productStateId) {
        logger.debug("Finding product state by ID: {}", productStateId);
        return productStateRepository.findById(productStateId)
                .map(this::convertToDto);
    }

    @Override
    public Page<ProductStateDto> getProductStatesByCriteria(String textSearch, Boolean enabled, Pageable pageable) {
        logger.debug("Getting product states with criteria - textSearch: {}, enabled: {}", textSearch, enabled);
        
        Page<ProductState> productStatePage = productStateRepository.findProductStatesByCriteriaPaginated(
            textSearch, enabled, pageable);
        
        return productStatePage.map(this::convertToDto);
    }

    // =============================================
    // HELPER METHODS
    // =============================================

    private ProductStateDto convertToDto(ProductState productState) {
        return ProductMapperUtil.toProductStateDto(productState);
    }
}