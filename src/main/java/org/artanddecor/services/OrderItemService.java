package org.artanddecor.services;

import org.artanddecor.dto.OrderItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * OrderItem Service Interface for business logic operations
 * Cleaned up version - contains only methods used by controllers
 */
public interface OrderItemService {

    /**
     * Search order items with comprehensive filtering (used by OrderController)
     * @param orderIds Filter by order IDs (optional)
     * @param productId Filter by product ID (optional)
     * @param minQuantity Filter by minimum quantity (optional)
     * @param maxQuantity Filter by maximum quantity (optional)
     * @param minUnitPrice Filter by minimum unit price (optional)
     * @param maxUnitPrice Filter by maximum unit price (optional)
     * @param minTotalPrice Filter by minimum total price (optional)
     * @param maxTotalPrice Filter by maximum total price (optional)
     * @param textSearch Search text in product name or code (optional)
     * @param pageable Pagination information
     * @return Page of order items matching criteria
     */
    Page<OrderItemDto> searchOrderItems(
            List<Long> orderIds,
            Long productId,
            Integer minQuantity,
            Integer maxQuantity,
            BigDecimal minUnitPrice,
            BigDecimal maxUnitPrice,
            BigDecimal minTotalPrice,
            BigDecimal maxTotalPrice,
            String textSearch,
            Pageable pageable);
}