package org.artanddecor.services.impl;

import org.artanddecor.dto.OrderDto;
import org.artanddecor.dto.CreateOrderItemRequest;
import org.artanddecor.dto.CartDto;
import org.artanddecor.dto.CartItemDto;
import org.artanddecor.dto.ShippingFeeDto;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.Order;
import org.artanddecor.model.OrderState;
import org.artanddecor.model.OrderItem;
import org.artanddecor.model.Product;
import org.artanddecor.model.Discount;
import org.artanddecor.repository.OrderRepository;
import org.artanddecor.repository.OrderStateRepository;
import org.artanddecor.repository.OrderItemRepository;
import org.artanddecor.repository.ProductRepository;
import org.artanddecor.repository.DiscountRepository;
import org.artanddecor.services.OrderService;
import org.artanddecor.services.OrderStateHistoryService;
import org.artanddecor.services.DiscountService;
import org.artanddecor.services.CartService;
import org.artanddecor.services.CartItemService;
import org.artanddecor.services.ShippingFeeService;
import org.artanddecor.utils.OrderMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Orders Service Implementation for business logic operations
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStateRepository orderStateRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private OrderStateHistoryService orderStateHistoryService;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private ShippingFeeService shippingFeeService;

    @Autowired
    private OrderMapperUtil orderMapperUtil;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return orderMapperUtil.mapToDto(order);
    }



    @Override
    public OrderDto updateOrderState(Long orderId, Long newOrderStateId, Long changedByUserId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        OrderState newOrderState = orderStateRepository.findById(newOrderStateId)
                .orElseThrow(() -> new ResourceNotFoundException("Order State not found with ID: " + newOrderStateId));

        // Get old state before updating
        OrderState oldOrderState = order.getOrderState();
        
        // Update order state
        order.setOrderState(newOrderState);
        order.setModifiedDt(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);

        // Create order state history record
        orderStateHistoryService.createOrderStateHistory(
                orderId,
                oldOrderState.getOrderStateId(),
                newOrderStateId,
                changedByUserId);

        return orderMapperUtil.mapToDto(updatedOrder);
    }



    @Override
    public OrderDto getMyOrderDetail(Long userId, Long orderId) {
        OrderDto order = getOrderById(orderId);
        // Add validation to ensure order belongs to user
        if (!order.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found or access denied for user ID: " + userId);
        }
        return order;
    }

    @Override
    public OrderDto cancelMyOrder(Long userId, Long orderId) {
        // Add validation to ensure order belongs to user and can be cancelled
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        
        // Validate ownership
        if (order.getUser() == null || !order.getUser().getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found or access denied for user ID: " + userId);
        }
        
        // Validate order can be cancelled (not shipped or completed)
        String currentStateName = order.getOrderState().getOrderStateName();
        if ("SHIPPED".equals(currentStateName) || "COMPLETED".equals(currentStateName) || "CANCELLED".equals(currentStateName)) {
            throw new IllegalStateException("Order cannot be cancelled in current state: " + currentStateName);
        }
        
        // Cancel the order by updating its state
        OrderState canceledState = orderStateRepository.findByOrderStateName("CANCELED")
                .orElseThrow(() -> new ResourceNotFoundException("CANCELED order state not found"));

        return updateOrderState(orderId, canceledState.getOrderStateId(), userId);
    }

    private String generateOrderCode() {
        LocalDateTime now = LocalDateTime.now();
        String datePrefix = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timePrefix = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        
        // Generate unique order code: ORD-YYYYMMDD-HHMMSS-XXX
        String baseCode = "ORD-" + datePrefix + "-" + timePrefix;
        
        int sequence = 1;
        String orderCode = baseCode + "-" + String.format("%03d", sequence);
        
        // Ensure uniqueness
        while (!isOrderCodeUnique(orderCode, null)) {
            sequence++;
            orderCode = baseCode + "-" + String.format("%03d", sequence);
        }
        
        return orderCode;
    }
    
    private boolean isOrderCodeUnique(String orderCode, Long excludeId) {
        return orderRepository.findByOrderCode(orderCode)
                .map(existingOrder -> excludeId != null && existingOrder.getOrderId().equals(excludeId))
                .orElse(true);
    }
    
    @Override
    public OrderDto checkoutCart(
            Long userId, 
            Long cartId, 
            Long shippingAddressId, 
            String paymentMethod, 
            String discountCode) {
        
        // Get cart with items
        CartDto cart = cartService.getCartById(cartId);
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart not found or empty");
        }
        
        // Validate cart ownership
        if (!cart.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Cart does not belong to user");
        }
        
        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setCreatedDt(LocalDateTime.now());
        order.setModifiedDt(LocalDateTime.now());
        
        // Set default state to NEW
        OrderState newState = orderStateRepository.findByOrderStateName("NEW")
                .orElse(orderStateRepository.findAll().get(0));
        order.setOrderState(newState);
        
        // Calculate subtotal from cart items
        BigDecimal subtotalAmount = cart.getCartItems().stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Apply discount if provided
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (discountCode != null && !discountCode.trim().isEmpty()) {
            try {
                Discount discount = discountRepository.findValidDiscountByCode(discountCode, LocalDateTime.now())
                        .orElse(null);
                if (discount != null && discountService.canUseDiscount(discount.getDiscountId())) {
                    order.setDiscount(discount);
                    // Store discount snapshot
                    order.setDiscountCode(discount.getDiscountCode());
                    order.setDiscountType(discount.getDiscountType() != null ? 
                            discount.getDiscountType().getDiscountTypeName() : null);
                    order.setDiscountValue(discount.getDiscountValue());
                    
                    // Calculate discount amount
                    discountAmount = discountService.calculateDiscountAmount(
                            discount.getDiscountId(), subtotalAmount);
                }
            } catch (Exception e) {
                // Ignore invalid discount codes
            }
        }
        
        // Calculate shipping fee
        BigDecimal shippingFeeAmount = BigDecimal.ZERO;
        try {
            ShippingFeeDto shippingFee = shippingFeeService.calculateShippingFee(subtotalAmount);
            if (shippingFee != null && shippingFee.getShippingFeeValue() != null) {
                shippingFeeAmount = shippingFee.getShippingFeeValue();
            }
        } catch (Exception e) {
            // Use default shipping fee if calculation fails
            shippingFeeAmount = BigDecimal.valueOf(50000); // Default 50,000 VND
        }
        
        // Set order amounts
        order.setSubtotalAmount(subtotalAmount);
        order.setDiscountAmount(discountAmount);
        order.setShippingFeeAmount(shippingFeeAmount);
        order.setTotalAmount(subtotalAmount.subtract(discountAmount).add(shippingFeeAmount));
        
        // Save order first to get ID
        Order savedOrder = orderRepository.save(order);
        
        // Create order items from cart items
        for (CartItemDto cartItem : cart.getCartItems()) {
            if (cartItem.getProduct() == null) continue;
            
            Product product = productRepository.findById(cartItem.getProduct().getProductId())
                    .orElse(null);
            if (product == null) continue;
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            orderItem.setCreatedDt(LocalDateTime.now());
            orderItem.setModifiedDt(LocalDateTime.now());
            
            orderItemRepository.save(orderItem);
        }
        
        // Create order state history
        orderStateHistoryService.createOrderStateHistory(
                savedOrder.getOrderId(),
                null, // No old state for new order
                newState.getOrderStateId(),
                userId);
        
        // Increment discount usage if applied
        if (order.getDiscount() != null) {
            discountService.incrementDiscountUsage(order.getDiscount().getDiscountId());
        }
        
        // Clear cart after successful checkout
        try {
            cartItemService.clearCart(cartId);
            logger.info("Cart cleared successfully after checkout - cartId: {}, orderId: {}", cartId, savedOrder.getOrderId());
        } catch (Exception e) {
            // Cart clearing failure should not affect the order creation
            logger.error("Failed to clear cart after checkout - cartId: {}, orderId: {}, error: {}", 
                    cartId, savedOrder.getOrderId(), e.getMessage());
        }
        
        return orderMapperUtil.mapToDto(savedOrder);
    }
    
    @Override
    public Page<OrderDto> getMyOrders(
            Long userId, 
            String state, 
            LocalDate fromDate, 
            LocalDate toDate, 
            Pageable pageable) {
        
        // Get user's orders
        List<Order> orders = orderRepository.findByUser_UserIdOrderByCreatedDtDesc(userId);
        
        // Apply state filter
        if (state != null && !state.trim().isEmpty()) {
            orders = orders.stream()
                .filter(order -> state.equalsIgnoreCase(order.getOrderState().getOrderStateName()))
                .collect(Collectors.toList());
        }
        
        // Apply date filters
        if (fromDate != null || toDate != null) {
            orders = orders.stream()
                .filter(order -> {
                    LocalDate orderDate = order.getCreatedDt().toLocalDate();
                    if (fromDate != null && orderDate.isBefore(fromDate)) {
                        return false;
                    }
                    if (toDate != null && orderDate.isAfter(toDate)) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        }
        
        // Convert to DTOs
        List<OrderDto> orderDtos = orders.stream()
                .map(orderMapperUtil::mapToDtoWithoutItems)
                .collect(Collectors.toList());
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), orderDtos.size());
        List<OrderDto> pageContent = orderDtos.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, orderDtos.size());
    }
    
    @Override
    public Page<OrderDto> adminSearchOrders(
            Long orderId,
            Long customerId, 
            String state,
            LocalDate fromDate, 
            LocalDate toDate, 
            BigDecimal minAmount, 
            BigDecimal maxAmount, 
            Pageable pageable) {
        
        // Convert LocalDate to LocalDateTime for repository call
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;
        
        // Get state ID from state name
        Long stateId = null;
        if (state != null && !state.trim().isEmpty()) {
            OrderState orderState = orderStateRepository.findByOrderStateName(state).orElse(null);
            if (orderState != null) {
                stateId = orderState.getOrderStateId();
            }
        }
        
        // Use repository method to search orders
        Page<Order> ordersPage = orderRepository.findOrdersByCriteria(
                orderId, null, customerId, null, null, null,
                stateId, null, minAmount, maxAmount, null, null,
                null, null, fromDateTime, toDateTime, null, null,
                null, null, null, pageable);

        return ordersPage.map(orderMapperUtil::mapToDtoWithoutItems);
    }
    
    @Override
    public OrderDto adminCreateOrder(
            Long customerId, 
            Long shippingAddressId, 
            String discountCode,
            List<CreateOrderItemRequest> orderItems, 
            Long createdByUserId) {
        
        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setCreatedDt(LocalDateTime.now());
        order.setModifiedDt(LocalDateTime.now());
        
        // Set default state to NEW
        OrderState newState = orderStateRepository.findByOrderStateName("NEW")
                .orElse(orderStateRepository.findAll().get(0));
        order.setOrderState(newState);
        
        // Apply discount if provided
        if (discountCode != null && !discountCode.trim().isEmpty()) {
            try {
                Discount discount = discountRepository.findValidDiscountByCode(discountCode, LocalDateTime.now())
                        .orElse(null);
                if (discount != null && discountService.canUseDiscount(discount.getDiscountId())) {
                    order.setDiscount(discount);
                    // Store discount snapshot
                    order.setDiscountCode(discount.getDiscountCode());
                    order.setDiscountType(discount.getDiscountType() != null ? 
                            discount.getDiscountType().getDiscountTypeName() : null);
                    order.setDiscountValue(discount.getDiscountValue());
                }
            } catch (Exception e) {
                // Ignore invalid discount codes
            }
        }
        
        // Save order first to get ID
        Order savedOrder = orderRepository.save(order);
        
        // Create order items and calculate totals
        BigDecimal subtotalAmount = BigDecimal.ZERO;
        
        for (CreateOrderItemRequest itemRequest : orderItems) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemRequest.getProductId()));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            
            BigDecimal unitPrice = product.getProductPrice() != null ? product.getProductPrice() : BigDecimal.ZERO;
            orderItem.setUnitPrice(unitPrice);
            orderItem.setCreatedDt(LocalDateTime.now());
            orderItem.setModifiedDt(LocalDateTime.now());
            
            orderItemRepository.save(orderItem);
            
            // Add to subtotal
            subtotalAmount = subtotalAmount.add(unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }
        
        // Calculate discount amount  
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (order.getDiscount() != null) {
            discountAmount = discountService.calculateDiscountAmount(
                    order.getDiscount().getDiscountId(), subtotalAmount);
        }
        
        // Calculate shipping fee
        BigDecimal shippingFeeAmount = BigDecimal.ZERO;
        try {
            ShippingFeeDto shippingFee = shippingFeeService.calculateShippingFee(subtotalAmount);
            if (shippingFee != null && shippingFee.getShippingFeeValue() != null) {
                shippingFeeAmount = shippingFee.getShippingFeeValue();
            }
        } catch (Exception e) {
            // Use default shipping fee if calculation fails
            shippingFeeAmount = BigDecimal.valueOf(50000); // Default 50,000 VND 
            logger.error("Failed to calculate shipping fee, using default: {}", e.getMessage());
        }
        
        // Update order with calculated amounts
        savedOrder.setSubtotalAmount(subtotalAmount);
        savedOrder.setDiscountAmount(discountAmount);
        savedOrder.setShippingFeeAmount(shippingFeeAmount);
        savedOrder.setTotalAmount(subtotalAmount.subtract(discountAmount).add(shippingFeeAmount));
        savedOrder.setModifiedDt(LocalDateTime.now());
        
        Order finalOrder = orderRepository.save(savedOrder);
        
        // Create order state history
        orderStateHistoryService.createOrderStateHistory(
                finalOrder.getOrderId(),
                null, // No old state for new order
                newState.getOrderStateId(),
                createdByUserId);
        
        // Increment discount usage if applied
        if (order.getDiscount() != null) {
            discountService.incrementDiscountUsage(order.getDiscount().getDiscountId());
        }
        
        return orderMapperUtil.mapToDto(finalOrder);
    }
    
    @Override
    public boolean isOrderOwner(Long orderId, Long userId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getUser() != null && order.getUser().getUserId().equals(userId))
                .orElse(false);
    }
}