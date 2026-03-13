package org.ArtAndDecor.services.impl;

import org.ArtAndDecor.dto.OrderItemDto;
import org.ArtAndDecor.exception.ResourceNotFoundException;
import org.ArtAndDecor.model.OrderItem;
import org.ArtAndDecor.model.Order;
import org.ArtAndDecor.model.Product;
import org.ArtAndDecor.repository.OrderItemRepository;
import org.ArtAndDecor.repository.OrderRepository;
import org.ArtAndDecor.repository.ProductRepository;
import org.ArtAndDecor.services.OrderItemService;
import org.ArtAndDecor.utils.OrderItemMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderItem Service Implementation for business logic operations
 */
@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired 
    private OrderItemMapperUtil orderItemMapperUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemDto> getAllOrderItems(Pageable pageable) {
        Page<OrderItem> orderItemsPage = orderItemRepository.findAll(pageable);
        return orderItemsPage.map(orderItemMapperUtil::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemDto getOrderItemById(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Item not found with ID: " + orderItemId));
        return orderItemMapperUtil.mapToDto(orderItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDto> getOrderItemsByOrderId(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(orderId);
        return orderItems.stream()
                .map(orderItemMapperUtil::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDto> getOrderItemsByProductId(Long productId) {
        List<OrderItem> orderItems = orderItemRepository.findByProductProductId(productId);
        return orderItems.stream()
                .map(orderItemMapperUtil::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderItemDto createOrderItem(OrderItemDto orderItemDto) {
        // Validate order exists
        Order order = null;
        if (orderItemDto.getOrderId() != null) {
            order = orderRepository.findById(orderItemDto.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderItemDto.getOrderId()));
        }

        // Validate product exists and get product details
        Product product = productRepository.findById(orderItemDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + orderItemDto.getProductId()));
        
        // Set product details if not provided
        if (orderItemDto.getProductName() == null) {
            orderItemDto.setProductName(product.getProductName());
        }
        if (orderItemDto.getProductCode() == null) {
            orderItemDto.setProductCode(product.getProductCode());
        }
        if (orderItemDto.getUnitPrice() == null) {
            orderItemDto.setUnitPrice(product.getProductPrice());
        }

        OrderItem orderItem = orderItemMapperUtil.mapToEntity(orderItemDto);
        orderItem.setOrder(order);

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return orderItemMapperUtil.mapToDto(savedOrderItem);
    }

    @Override
    public List<OrderItemDto> createOrderItems(List<OrderItemDto> orderItemDtos) {
        List<OrderItemDto> createdItems = orderItemDtos.stream()
                .map(this::createOrderItem)
                .collect(Collectors.toList());

        // Recalculate order totals if needed
        if (!createdItems.isEmpty() && createdItems.get(0).getOrderId() != null) {
            // Trigger order total recalculation
            recalculateOrderTotals(createdItems.get(0).getOrderId());
        }

        return createdItems;
    }

    @Override
    public OrderItemDto updateOrderItem(Long orderItemId, OrderItemDto orderItemDto) {
        OrderItem existingOrderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Item not found with ID: " + orderItemId));

        // Update fields
        orderItemMapperUtil.updateEntityFromDto(existingOrderItem, orderItemDto);

        OrderItem updatedOrderItem = orderItemRepository.save(existingOrderItem);
        return orderItemMapperUtil.mapToDto(updatedOrderItem);
    }

    @Override
    public OrderItemDto updateOrderItemQuantity(Long orderItemId, Integer newQuantity) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Item not found with ID: " + orderItemId));

        orderItem.setQuantity(newQuantity);

        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        return orderItemMapperUtil.mapToDto(updatedOrderItem);
    }

    @Override
    public OrderItemDto updateOrderItemPrice(Long orderItemId, BigDecimal newUnitPrice) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Item not found with ID: " + orderItemId));

        orderItem.setUnitPrice(newUnitPrice);

        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        return orderItemMapperUtil.mapToDto(updatedOrderItem);
    }

    @Override
    public void deleteOrderItem(Long orderItemId) {
        if (!orderItemRepository.existsById(orderItemId)) {
            throw new ResourceNotFoundException("Order Item not found with ID: " + orderItemId);
        }
        orderItemRepository.deleteById(orderItemId);
    }

    @Override
    public void deleteOrderItemsByOrderId(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(orderId);
        if (!orderItems.isEmpty()) {
            orderItemRepository.deleteAll(orderItems);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateOrderItemTotalAmount(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Item not found with ID: " + orderItemId));

        return BigDecimal.valueOf(orderItem.getQuantity()).multiply(orderItem.getUnitPrice());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateOrderTotalAmount(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(orderId);
        
        return orderItems.stream()
                .map(item -> BigDecimal.valueOf(item.getQuantity()).multiply(item.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer calculateTotalQuantitySoldForProduct(Long productId) {
        List<OrderItem> orderItems = orderItemRepository.findByProductProductId(productId);
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalRevenueForProduct(Long productId) {
        List<OrderItem> orderItems = orderItemRepository.findByProductProductId(productId);
        return orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Object[]> getTopSellingProductsByQuantity(Pageable pageable) {
        // Return empty page since this is not used
        return Page.empty(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Object[]> getTopRevenueGeneratingProducts(Pageable pageable) {
        // Return empty page since this is not used
        return Page.empty(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countOrderItemsByOrderId(Long orderId) {
        return (long) orderItemRepository.findByOrderOrderId(orderId).size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDto> getHighQuantityOrderItems() {
        // Return empty list since this is not used
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateOrderItemQuantity(Long productId, Integer requestedQuantity) {
        // Implement product stock validation
        if (requestedQuantity == null || requestedQuantity <= 0) {
            return false;
        }
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        
        // Check if product is enabled and has sufficient stock
        if (!product.getProductEnabled()) {
            return false;
        }
        
        return product.getStockQuantity() >= requestedQuantity;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemDto> searchOrderItemsByCriteria(
            Long orderItemId,
            Long orderId,
            Long productId,
            String productName,
            Integer minQuantity,
            Integer maxQuantity,
            BigDecimal minUnitPrice,
            BigDecimal maxUnitPrice,
            BigDecimal minTotalPrice,
            BigDecimal maxTotalPrice,
            String textSearch,
            Pageable pageable) {

        // Simple implementation - return all order items with pagination
        return getAllOrderItems(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long orderItemId) {
        return orderItemRepository.existsById(orderItemId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductUsedInOrders(Long productId) {
        return !orderItemRepository.findByProductProductId(productId).isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public Object[] getOrderSummary(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(orderId);
        
        Long totalItems = (long) orderItems.size();
        Integer totalQuantity = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
        BigDecimal totalAmount = orderItems.stream()
                .map(item -> BigDecimal.valueOf(item.getQuantity()).multiply(item.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Object[]{totalItems, totalQuantity, totalAmount};
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDto> getOrderItems(Long orderId) {
        return getOrderItemsByOrderId(orderId);
    }

    @Override
    public OrderItemDto addOrderItem(Long orderId, Long productId, Integer quantity) {
        // Implement full order item addition with product validation
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        
        // Validate quantity
        if (!validateOrderItemQuantity(productId, quantity)) {
            throw new IllegalArgumentException("Invalid quantity or insufficient stock for product ID: " + productId);
        }
        
        // Create order item
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setProductName(product.getProductName());
        orderItem.setProductCode(product.getProductCode());
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(product.getProductPrice());
        orderItem.setCreatedDt(LocalDateTime.now());
        orderItem.setModifiedDt(LocalDateTime.now());
        
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        
        // Recalculate order totals
        recalculateOrderTotals(orderId);
        
        return orderItemMapperUtil.mapToDtoWithoutOrder(savedOrderItem);
    }

    /**
     * Recalculate order totals based on order items
     */
    private void recalculateOrderTotals(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(orderId);
        
        BigDecimal subtotalAmount = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        order.setSubtotalAmount(subtotalAmount);
        
        // Keep existing discount amount or set to zero if null
        BigDecimal discountAmount = order.getDiscountAmount() != null ? 
            order.getDiscountAmount() : BigDecimal.ZERO;
        
        BigDecimal totalAmount = subtotalAmount.subtract(discountAmount);
        order.setTotalAmount(totalAmount);
        
        order.setModifiedDt(LocalDateTime.now());

        orderRepository.save(order);
    }

    @Override
    public OrderItemDto updateOrderItem(Long orderItemId, Integer quantity) {
        return updateOrderItemQuantity(orderItemId, quantity);
    }
}