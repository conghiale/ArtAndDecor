package org.artanddecor.services.impl;

import org.artanddecor.dto.OrderItemDto;
import org.artanddecor.model.OrderItem;
import org.artanddecor.repository.OrderItemRepository;
import org.artanddecor.services.OrderItemService;
import org.artanddecor.utils.OrderMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * OrderItem Service Implementation for business logic operations
 * Cleaned up version - contains only methods used by controllers
 */
@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired 
    private OrderMapperUtil orderMapperUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemDto> searchOrderItems(
            List<Long> orderIds,
            Long productId,
            Integer minQuantity,
            Integer maxQuantity,
            BigDecimal minUnitPrice,
            BigDecimal maxUnitPrice,
            BigDecimal minTotalPrice,
            BigDecimal maxTotalPrice,
            String textSearch,
            Pageable pageable) {

        // Optimized single repository call with unified filtering method
        Page<OrderItem> orderItemsPage = orderItemRepository.findWithFilters(
                orderIds, productId, minQuantity, maxQuantity, 
                minUnitPrice, maxUnitPrice, minTotalPrice, maxTotalPrice, 
                textSearch, pageable);
        return orderItemsPage.map(orderMapperUtil::mapToDto);
    }
}