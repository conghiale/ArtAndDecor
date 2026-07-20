package org.artanddecor.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.artanddecor.dto.*;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.*;
import org.artanddecor.repository.*;
import org.artanddecor.services.*;
import org.artanddecor.config.MailConfiguration;
import org.artanddecor.utils.OrderMapperUtil;
import org.artanddecor.utils.ProductMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Orders Service Implementation for business logic operations
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    // Default shipping fee configuration - can be moved to application.properties later
    private static final BigDecimal DEFAULT_SHIPPING_FEE = BigDecimal.valueOf(0); // 0 VND

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStateRepository orderStateRepository;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private ShippingFeeService shippingFeeService;

    @Autowired
    private OrderMapperUtil orderMapperUtil;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderStateHistoryService orderStateHistoryService;
    
    @Autowired
    private DiscountService discountService;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private PaymentMethodService paymentMethodService;
    
    @Autowired 
    private PaymentMethodRepository paymentMethodRepository;
    
    @Autowired
    private PaymentStateRepository paymentStateRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentStateRepository shipmentStateRepository;
    
    @Autowired
    private ContactService contactService;
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MailConfiguration mailConfiguration;

    @Autowired
    private PolicyService policyService;

    // ===== NEW APIS =====
    
    @Override
    @Transactional(readOnly = true)
    public PreviewOrderResponse previewOrder(PreviewOrderRequest request) {
            
        Long cartId = request.getCartId();
        List<Long> selectedCartItemIds = request.getSelectedCartItemIds();
        logger.info("Previewing order for cart {} with {} selected items", 
                   cartId, selectedCartItemIds != null ? selectedCartItemIds.size() : 0);
        
        // Validation
        if (cartId == null) {
            throw new IllegalArgumentException("Cart ID is required for preview validation");
        }
        
        if (selectedCartItemIds == null || selectedCartItemIds.isEmpty()) {
            throw new IllegalArgumentException("Selected cart item IDs are required for preview");
        }
        
        // Validate cart exists and is ACTIVE
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));
        
        if (!"ACTIVE".equals(cart.getCartState().getCartStateName())) {
            throw new IllegalArgumentException("Cart is not active, cannot preview. CartId: " + cartId);
        }
        
        // Get selected cart items and validate they belong to the specified cart
        List<CartItem> cartItems = cartItemRepository.findAllById(selectedCartItemIds);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new ResourceNotFoundException("No cart items found for provided IDs");
        }
        
        // Validate all requested items were found
        if (cartItems.size() != selectedCartItemIds.size()) {
            throw new ResourceNotFoundException(
                String.format("Some cart items not found. Requested: %d, Found: %d", 
                             selectedCartItemIds.size(), cartItems.size()));
        }
        
        // Critical security validation: ensure all selected items belong to the specified cart
        for (CartItem item : cartItems) {
            if (!item.getCart().getCartId().equals(cartId)) {
                throw new SecurityException(
                    String.format("Cart item %d does not belong to cart %d", 
                                 item.getCartItemId(), cartId));
            }
        }
        
        // Calculate subtotal using cart item's unit price logic (considers attributes)
        BigDecimal subtotalAmount = cartItems.stream()
                .map(item -> item.calculateUnitPrice().multiply(new BigDecimal(item.getCartItemQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Initialize collections for warnings/errors
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        // Validate inventory and collect warnings
        for (CartItem item : cartItems) {
            validateCartItemInventory(item, errors);
        }
        
        // Calculate discount using the same rules as checkout
        DiscountCalculationResult discountResult = calculateOrderDiscount(cartItems, subtotalAmount, request.getDiscountCode());
        BigDecimal discountAmount = discountResult.discountAmount;
        DiscountDto appliedDiscount = discountResult.appliedDiscount;
        String discountMessage = discountResult.discountMessage;


        // AUTO-CALCULATE OPTIMAL SHIPPING FEE (phí ship tối ưu cho khách hàng)
        ShippingFeeCalculationResult shippingResult = calculateOrderShippingFee(subtotalAmount);
        BigDecimal shippingFeeAmount = shippingResult.shippingFeeAmount;
        ShippingFeeDto appliedShippingFee = shippingResult.appliedShippingFee;
        String shippingMessage = shippingResult.shippingMessage;

        /*try {
            // Calculate shipping fee based on subtotal amount only
            appliedShippingFee = shippingFeeService.calculateShippingFee(subtotalAmount);
            
            shippingMessage = "Shipping calculated based on order amount";
            
            if (appliedShippingFee != null && appliedShippingFee.getShippingFeeEnabled() && appliedShippingFee.getShippingFeeValue() != null) {
                shippingFeeAmount = appliedShippingFee.getShippingFeeValue();
            } else {
                // Use default shipping fee
                shippingFeeAmount = DEFAULT_SHIPPING_FEE;
                shippingMessage = "Standard shipping fee applied";
            }
        } catch (Exception e) {
            logger.warn("Failed to calculate shipping fee, using default: {}", e.getMessage());
            shippingFeeAmount = DEFAULT_SHIPPING_FEE;
            shippingMessage = "Standard shipping fee applied";
        }*/

        // Calculate final total
        BigDecimal totalAmount = subtotalAmount.subtract(discountAmount).add(shippingFeeAmount);
        
        // Convert cartItems to CartItemDto list for response
        List<CartItemDto> selectedCartItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            CartItemDto cartItemDto = new CartItemDto();
            cartItemDto.setCartItemId(item.getCartItemId());
            cartItemDto.setProduct(ProductMapperUtil.toProductDto(item.getProduct()));
            cartItemDto.setQuantity(item.getCartItemQuantity());
            
            // Use calculated unit price based on selected attributes
            BigDecimal unitPrice = item.calculateUnitPrice();
            cartItemDto.setUnitPrice(unitPrice);
            cartItemDto.setTotalPrice(unitPrice.multiply(new BigDecimal(item.getCartItemQuantity())));
            
            selectedCartItems.add(cartItemDto);
        }
        
        // Calculate summary statistics
        Integer totalItems = selectedCartItems.size();
        Integer totalQuantity = selectedCartItems.stream()
                .mapToInt(CartItemDto::getQuantity)
                .sum();
        
        return PreviewOrderResponse.builder()
                .selectedCartItems(selectedCartItems)
                .subtotalAmount(subtotalAmount)
                .discountAmount(discountAmount)
                .shippingFeeAmount(shippingFeeAmount)
                .totalAmount(totalAmount)
                .appliedDiscount(appliedDiscount)
                .discountMessage(discountMessage)
                .appliedShippingFee(appliedShippingFee)
                .shippingMessage(shippingMessage)
                .totalItems(totalItems)
                .totalQuantity(totalQuantity)
                .warnings(warnings.isEmpty() ? null : warnings)
                .errors(errors.isEmpty() ? null : errors)
                .build();
    }
    
    @Override
    public OrderDto checkoutEntireCart(CheckoutCartRequest request, Long userId) {
        logger.info("Checkout entire cart for cartId: {}, userId: {}", request.getCartId(), userId);
        
        // Validation
        if (!request.hasCompleteOrderInfo()) {
            throw new IllegalArgumentException("Complete order information is required");
        }

        if (request.getCartId() == null) {
            throw new IllegalArgumentException("Cart ID is required");
        }

        // Validate cart exists and is ACTIVE
        Cart cart = cartRepository.findById(request.getCartId())
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + request.getCartId()));
            
        if (!"ACTIVE".equals(cart.getCartState().getCartStateName())) {
            throw new IllegalArgumentException("Cart is not active, cannot checkout. CartId: " + request.getCartId());
        }

        // Get all cart items for this cart
        List<CartItem> cartItems = cartItemRepository.findByCart_CartId(request.getCartId());
        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("No items found in cart: " + request.getCartId());
        }

        return createOrderFromCartItems(cartItems, request, true, userId); // Clear entire cart
    }
    
    @Override
    public OrderDto checkoutSelectedCartItems(CheckoutCartRequest request, Long userId) {
        logger.info("Creating order from selected cart items for cartId: {}, userId: {}", request.getCartId(), userId);
        
        // Validation
        if (request == null || request.getSelectedCartItemIds() == null || request.getSelectedCartItemIds().isEmpty()) {
            throw new IllegalArgumentException("Selected cart item IDs are required");
        }
        
        if (request.getCartId() == null) {
            throw new IllegalArgumentException("Cart ID is required");
        }
        
        if (!request.hasCompleteOrderInfo()) {
            throw new IllegalArgumentException("Complete order information is required");
        }
        
        // Validate cart exists and is ACTIVE
        Cart cart = cartRepository.findById(request.getCartId())
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + request.getCartId()));
            
        if (!"ACTIVE".equals(cart.getCartState().getCartStateName())) {
            throw new IllegalArgumentException("Cart is not active, cannot checkout. CartId: " + request.getCartId());
        }
        
        // Get selected cart items and validate they belong to the specified cart
        List<CartItem> cartItems = cartItemRepository.findAllById(request.getSelectedCartItemIds());
        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("No cart items found with provided IDs");
        }
        
        // Validate all requested items were found
        if (cartItems.size() != request.getSelectedCartItemIds().size()) {
            throw new ResourceNotFoundException("Some cart items were not found. Requested: " + 
                request.getSelectedCartItemIds().size() + ", Found: " + cartItems.size());
        }
        
        // Critical security validation: ensure all selected items belong to the specified cart
        for (CartItem item : cartItems) {
            if (!item.getCart().getCartId().equals(request.getCartId())) {
                throw new SecurityException(
                    "Security violation: Cart item " + item.getCartItemId() + " does not belong to cart " + request.getCartId());
            }
        }
        
        return createOrderFromCartItems(cartItems, request, false, userId); // Clear only selected items
    }
    
    /**
     * Helper method to create order from cart items (shared logic)
     * Enhanced to support userId parameter for order ownership tracking
     */
    private OrderDto createOrderFromCartItems(List<CartItem> cartItems, CheckoutCartRequest request, boolean clearEntireCart, Long userId) {
        
        // Get user from cart or userId parameter for order ownership tracking
        User user = null;
        Cart cart = cartItems.get(0).getCart(); // All cart items belong to same cart (validated above)
        
        // Priority 1: Use userId parameter if provided (from authenticated user)
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                logger.info("Order will be assigned to userId: {} (from request parameter)", userId);
            }
        }
        
        // Priority 2: Fallback to cart's associated user
        if (user == null && cart.getUser() != null) {
            user = cart.getUser();
            logger.info("Order will be assigned to userId: {} (from cart user relationship)", user.getUserId());
        }
        
        // Priority 3: Guest order (user remains null)
        if (user == null) {
            logger.info("Order will be created as GUEST order (USER_ID will be null)");
        }
        
        // Calculate amounts using cart item's unit price logic (considers attributes)
        BigDecimal subtotalAmount = cartItems.stream()
                .map(item -> item.calculateUnitPrice().multiply(new BigDecimal(item.getCartItemQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<String> inventoryErrors = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            validateCartItemInventory(cartItem, inventoryErrors);
        }
        if (!inventoryErrors.isEmpty()) {
            throw new IllegalStateException(String.join("; ", inventoryErrors));
        }
        
        Long cartId = cart.getCartId();
        
        DiscountCalculationResult discountResult = calculateOrderDiscount(cartItems, subtotalAmount, request.getDiscountCode());
        BigDecimal discountAmount = discountResult.discountAmount;
        DiscountDto appliedDiscount = discountResult.appliedDiscount;

        ShippingFeeCalculationResult shippingResult = calculateOrderShippingFee(subtotalAmount);
        BigDecimal shippingFeeAmount = shippingResult.shippingFeeAmount;
        
        // Create order
        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setOrderSlug(generateOrderSlug());
        order.setUser(user); // Can be null for guest orders
        
        // Set sessionId for guest orders (from cart sessionId)
        if (user == null && cart.getSessionId() != null) {
            order.setSessionId(cart.getSessionId());
            logger.info("Order will use sessionId: {} (from cart for guest order)", cart.getSessionId());
        }
        
        // Set order state to PENDING (initial state)
        OrderState newState = orderStateRepository.findByOrderStateName("PENDING")
                .orElseGet(() -> {
                    // Fallback to first available state if PENDING not found
                    List<OrderState> allStates = orderStateRepository.findAll();
                    if (allStates.isEmpty()) {
                        throw new RuntimeException("No order states found in database");
                    }
                    logger.warn("PENDING state not found, using first available state: {}", allStates.get(0).getOrderStateName());
                    return allStates.get(0);
                });
        order.setOrderState(newState);
        
        // Set customer information
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhoneNumber(request.getCustomerPhoneNumber());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerAddress(request.getCustomerAddress());
        
        // Set receiver information
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverEmail(request.getReceiverEmail());
        
        // Set delivery address
        order.setAddressLine(request.getAddressLine());
        order.setCity(request.getCity());
        order.setWard(request.getWard());
        order.setCountry(request.getCountry());
        
        // Set financial information
        order.setSubtotalAmount(subtotalAmount);
        order.setDiscountAmount(discountAmount);
        order.setShippingFeeAmount(shippingFeeAmount);
        order.setTotalAmount(subtotalAmount.subtract(discountAmount).add(shippingFeeAmount));
        
        // Set discount information (snapshot data)
        if (appliedDiscount != null) {
            order.setDiscountCode(appliedDiscount.getDiscountCode());
            order.setDiscountType(appliedDiscount.getDiscountType() != null ? 
                    appliedDiscount.getDiscountType().getDiscountTypeName() : null);
            order.setDiscountValue(appliedDiscount.getDiscountValue());
        } else if (request.getDiscountCode() != null && !request.getDiscountCode().trim().isEmpty()) {
            // Fallback: if we have discount code but no applied discount, just store the code
            order.setDiscountCode(request.getDiscountCode());
        }
        
        // Set optional information
        order.setOrderNote(request.getOrderNote());
        
        // Set timestamps
        order.setCreatedDt(LocalDateTime.now());
        order.setModifiedDt(LocalDateTime.now());
        
        // Save order first to get ID
        Order savedOrder = orderRepository.save(order);
        logger.info("Order saved successfully with ID: {}, USER_ID: {}, Order Type: {}", 
                   savedOrder.getOrderId(), 
                   savedOrder.getUser() != null ? savedOrder.getUser().getUserId() : "NULL (Guest Order)",
                   savedOrder.getUser() != null ? "USER ORDER" : "GUEST ORDER");
        
        // Create initial payment record with PENDING state for order tracking
        try {
            Payment initialPayment = new Payment();
            initialPayment.setOrder(savedOrder);
            initialPayment.setAmount(savedOrder.getTotalAmount());
            
            // Set payment method from request
            if (request.getPaymentMethodId() != null) {
                try {
                    PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                            .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with ID: " + request.getPaymentMethodId()));
                    initialPayment.setPaymentMethod(paymentMethod);
                    logger.info("Payment method resolved for order {}: ID={}, Name={}", 
                               savedOrder.getOrderId(), request.getPaymentMethodId(), paymentMethod.getPaymentMethodName());
                } catch (Exception e) {
                    logger.warn("Failed to resolve payment method ID: {}, will use default. Error: {}", 
                               request.getPaymentMethodId(), e.getMessage());
                    // Use default payment method (COD)
                    PaymentMethod defaultPaymentMethod = paymentMethodRepository.findByPaymentMethodName("COD")
                            .orElseThrow(() -> new ResourceNotFoundException("Default COD payment method not found"));
                    initialPayment.setPaymentMethod(defaultPaymentMethod);
                }
            } else {
                // Use default payment method (COD) if not specified
                PaymentMethod defaultPaymentMethod = paymentMethodRepository.findByPaymentMethodName("COD")
                        .orElseThrow(() -> new ResourceNotFoundException("Default COD payment method not found"));
                initialPayment.setPaymentMethod(defaultPaymentMethod);
            }
            
            // Set default PENDING payment state
            PaymentState pendingState = paymentStateRepository.findByPaymentStateName("PENDING")
                    .orElseThrow(() -> new ResourceNotFoundException("PENDING payment state not found"));
            initialPayment.setPaymentState(pendingState);
            
            // Generate payment slug and transaction ID for tracking
            String paymentSlug = "PAY-" + savedOrder.getOrderCode() + "-" + System.currentTimeMillis();
            initialPayment.setPaymentSlug(paymentSlug);
            
            String transactionId = "TXN-" + savedOrder.getOrderCode() + "-" + System.currentTimeMillis();
            initialPayment.setTransactionId(transactionId);
            initialPayment.setPaymentRemark("Initial payment record for order " + savedOrder.getOrderCode());
            
            // Save payment record
            paymentRepository.save(initialPayment);
            logger.info("Initial payment record created for order {} with payment ID: {}", 
                       savedOrder.getOrderId(), initialPayment.getPaymentId());

            // Update in-memory payments collection to avoid Hibernate L1 cache stale issue.
            // findById() within the same transaction returns the cached entity (savedOrder),
            // which still has payments=null because JPA only updates the owning side (Payment.order).
            // Without this, applyLatestPaymentSnapshot() sees no payments → paymentMethod is null in email.
            if (savedOrder.getPayments() == null) {
                savedOrder.setPayments(new java.util.ArrayList<>());
            }
            savedOrder.getPayments().add(initialPayment);

        } catch (Exception e) {
            logger.error("Failed to create initial payment record for order {}: {}", savedOrder.getOrderId(), e.getMessage());
            // Don't fail order creation due to payment record creation failure
        }
        
        // Create order items
        List<OrderItem> orderItemsToSave = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            
            // Set snapshot product information
            orderItem.setProductName(product.getProductName());
            orderItem.setProductCode(product.getProductCode());
            orderItem.setProductCategoryName(product.getProductCategory().getProductCategoryName());
            
            // Set product type name from relationship: Product -> ProductCategory -> ProductType
            String productTypeName = "GENERAL"; // Default value
            if (product.getProductCategory() != null && product.getProductCategory().getProductType() != null) {
                productTypeName = product.getProductCategory().getProductType().getProductTypeName();
            }
            orderItem.setProductTypeName(productTypeName);
            
            // Set item details
            orderItem.setQuantity(cartItem.getCartItemQuantity());
            
            // Use CartItem's calculateUnitPrice logic which considers selected attributes
            BigDecimal unitPrice = cartItem.calculateUnitPrice();
            orderItem.setUnitPrice(unitPrice);
            
            BigDecimal totalPrice = unitPrice.multiply(new BigDecimal(cartItem.getCartItemQuantity()));
            orderItem.setTotalPrice(totalPrice);
            
            // Set attributes as JSON (snapshot of selected product attributes)
            if (cartItem.getCartItemAttributes() != null && !cartItem.getCartItemAttributes().isEmpty()) {
                try {
                    // Build JSON array with selected attributes info for order snapshot
                    StringBuilder jsonBuilder = new StringBuilder("[");
                    boolean first = true;
                    
                    for (CartItemAttribute cartItemAttr : cartItem.getCartItemAttributes()) {
                        if (cartItemAttr.getProductAttribute() != null) {
                            if (!first) {
                                jsonBuilder.append(",");
                            }
                            
                            ProductAttribute productAttr = cartItemAttr.getProductAttribute();
                            jsonBuilder.append("{");
                            
                            // Add attribute name
                            if (productAttr.getProductAttr() != null && productAttr.getProductAttr().getProductAttrDisplayName() != null) {
                                jsonBuilder.append("\"attributeName\":\"")
                                          .append(escapeJsonString(productAttr.getProductAttr().getProductAttrDisplayName()))
                                          .append("\",");
                            } else if (productAttr.getProductAttr() != null && productAttr.getProductAttr().getProductAttrName() != null) {
                                jsonBuilder.append("\"attributeName\":\"")
                                          .append(escapeJsonString(productAttr.getProductAttr().getProductAttrName()))
                                          .append("\",");
                            }
                            
                            // Add attribute value
                            if (productAttr.getProductAttributeDisplayName() != null) {
                                jsonBuilder.append("\"attributeValue\":\"")
                                          .append(escapeJsonString(productAttr.getProductAttributeDisplayName()))
                                          .append("\",");
                            } else if (productAttr.getProductAttributeValue() != null) {
                                jsonBuilder.append("\"attributeValue\":\"")
                                          .append(escapeJsonString(productAttr.getProductAttributeValue()))
                                          .append("\",");
                            }
                            
                            // Add attribute price (important for order snapshot)
                            jsonBuilder.append("\"productAttributePrice\":");
                            if (productAttr.getProductAttributePrice() != null) {
                                jsonBuilder.append(productAttr.getProductAttributePrice());
                            } else {
                                jsonBuilder.append("null");
                            }
                            
                            jsonBuilder.append("}");
                            first = false;
                        }
                    }
                    
                    jsonBuilder.append("]");
                    orderItem.setProductAttrJson(jsonBuilder.toString());
                    
                    logger.debug("Saved {} attributes as JSON for order item: {}", 
                               cartItem.getCartItemAttributes().size(), jsonBuilder.toString());
                } catch (Exception e) {
                    logger.error("Failed to convert cart item attributes to JSON for order item: {}", e.getMessage());
                    orderItem.setProductAttrJson("[]"); // Empty array as fallback
                }
            } else {
                orderItem.setProductAttrJson("[]"); // Empty array for no attributes
            }
            
            orderItem.setCreatedDt(LocalDateTime.now());
            orderItem.setModifiedDt(LocalDateTime.now());

            orderItemsToSave.add(orderItem);
        }
        orderItemRepository.saveAll(orderItemsToSave);

        // Clear cart after successful checkout (hard delete)
        try {
            if (clearEntireCart) {
                // Clear entire cart for checkoutCart API
                cartItemService.clearCart(cartId);
                logger.info("Cart cleared entirely after checkout - cartId: {}, orderId: {}", 
                        cartId, savedOrder.getOrderId());
            } else {
                // Clear only selected items for createOrder API
                List<Long> cartItemIds = cartItems.stream()
                        .map(CartItem::getCartItemId)
                        .collect(Collectors.toList());
                cartItemService.clearSelectedCartItems(cartItemIds);
                logger.info("Selected cart items cleared after checkout - cartItemIds: {}, orderId: {}", 
                        cartItemIds, savedOrder.getOrderId());
            }
        } catch (Exception e) {
            // Cart clearing failure should not affect the order creation
            logger.error("Failed to clear cart after checkout - cartId: {}, orderId: {}, clearEntireCart: {}, error: {}", 
                    cartId, savedOrder.getOrderId(), clearEntireCart, e.getMessage());
        }
        
        logger.info("Order created successfully - ID: {}, Code: {}", savedOrder.getOrderId(), savedOrder.getOrderCode());
        
        // Reload order from database to ensure Payment relationship is loaded for proper snapshot
        Order finalOrder = orderRepository.findById(savedOrder.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found after creation: " + savedOrder.getOrderId()));
        
        logger.debug("Order reloaded with {} payments for proper snapshot mapping", 
                    finalOrder.getPayments() != null ? finalOrder.getPayments().size() : 0);
        
        // Convert to DTO before sending notification email
        OrderDto orderDto = orderMapperUtil.mapToDto(finalOrder);
        
        // Send order notification email to admin contacts
        try {
            sendOrderNotificationEmail(orderDto);
        } catch (Exception e) {
            // Email failure should not affect order creation success
            logger.error("Failed to send order notification email for order {}: {}", 
                        orderDto.getOrderId(), e.getMessage());
        }
        
        return orderDto;
    }
    
    // REMOVED: Legacy checkoutSelectedCartItems method with single userId parameter
    // This method is not used by OrderController - all checkout functionality uses the unified methods

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
        logger.info("Order state updated successfully for order ID: {}", orderId);

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
        OrderState canceledState = orderStateRepository.findByOrderStateName("CANCELLED")
                .orElseThrow(() -> new ResourceNotFoundException("CANCELLED order state not found"));

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
        
        // Try to ensure uniqueness, but limit attempts to avoid infinite loop
        int maxAttempts = 100;
        int attempts = 0;
        
        while (attempts < maxAttempts && !isOrderCodeUnique(orderCode, null)) {
            sequence++;
            orderCode = baseCode + "-" + String.format("%03d", sequence);
            attempts++;
        }
        
        if (attempts >= maxAttempts) {
            // If we can't verify uniqueness after many attempts, add timestamp for uniqueness
            String millisPrefix = now.format(DateTimeFormatter.ofPattern("SSS"));
            orderCode = baseCode + "-" + millisPrefix + "-" + String.format("%03d", sequence);
            logger.warn("Generated order code after max attempts: {}", orderCode);
        }
        
        return orderCode;
    }
    
    private boolean isOrderCodeUnique(String orderCode, Long excludeId) {
        try {
            return orderRepository.findByOrderCode(orderCode)
                    .map(existingOrder -> {
                        // If we're excluding an ID (update case), check if found order has different ID
                        if (excludeId != null) {
                            return !existingOrder.getOrderId().equals(excludeId); // Return false if different ID found
                        }
                        // For create case, any existing order found means code is not unique
                        return false;
                    })
                    .orElse(true); // No existing order found, code is unique
        } catch (Exception e) {
            // Handle SQL exception due to ORDER table name being a reserved keyword
            logger.error("Error checking order code uniqueness for code '{}': {}", orderCode, e.getMessage());
            // Return true to allow order creation to continue with a different code
            // The generateOrderCode method will try with a different sequence number
            return true;
        }
    }
    
    // REMOVED: Legacy checkoutCart methods with deprecated parameters
    // These methods were not used by OrderController and contained deprecated shippingAddressId parameter
    // All checkout functionality is now handled by checkoutEntireCart() and checkoutSelectedCartItems()
    
    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getMyOrders(
            Long userId, 
            String state, 
            LocalDate fromDate, 
            LocalDate toDate, 
            Pageable pageable) {
        
        // Convert LocalDate to LocalDateTime for repository call
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;
        
        // Use database-level filtering and pagination for better performance
        Page<Order> ordersPage = orderRepository.findUserOrdersWithFiltering(
                userId, state, fromDateTime, toDateTime, pageable);
        
        return ordersPage.map(orderMapperUtil::mapToDto);
    }
    
    // REMOVED: adminSearchOrders method - not used by OrderController
    // All admin search functionality is handled by searchOrders method
        
    @Override
    public Page<OrderDto> searchOrders(
            Long orderId,
            Long userId,
            String sessionId,
            String orderCode,
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
        
        // Use repository method to search orders with proper parameters
        Page<Order> ordersPage = orderRepository.findOrdersByCriteria(
                orderId,           // orderId
                orderCode,         // orderCode  
                userId,            // userId (formerly customerId)
                sessionId,         // sessionId for guest orders
                null,              // customerName
                null,              // customerPhone
                null,              // customerEmail
                stateId,           // orderStateId
                minAmount,         // minTotalAmount
                maxAmount,         // maxTotalAmount
                null,              // minOriginalAmount
                null,              // maxOriginalAmount
                null,              // minDiscountAmount
                null,              // maxDiscountAmount
                fromDateTime,      // orderDateFrom
                toDateTime,        // orderDateTo
                null,              // requiredDateFrom
                null,              // requiredDateTo
                null,              // shippedDateFrom
                null,              // shippedDateTo
                null,              // textSearch
                pageable);         // pageable

        return ordersPage.map(orderMapperUtil::mapToDto);
    }
    
    @Override
    public boolean isOrderOwner(Long orderId, Long userId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getUser() != null && order.getUser().getUserId().equals(userId))
                .orElse(false);
    }
    
    @Override
    public OrderDto updateOrderStatusWithSpecialHandling(Long orderId, Long newOrderStateId, Long changedByUserId, String statusNote) {
        logger.info("Updating order status with special handling - Order: {}, New Status: {}, User: {}", 
                   orderId, newOrderStateId, changedByUserId);
        
        // First, perform normal order state update
        OrderDto updatedOrder = updateOrderState(orderId, newOrderStateId, changedByUserId);
        
        // Check for special handling using already-loaded state name from DTO — avoids extra DB call
        try {
            if (updatedOrder.isDelivered()) {
                logger.info("Order {} marked as DELIVERED, applying shipment/payment completion workflow", orderId);

                // 1) Mark all shipments of this order as DELIVERED
                try {
                    Optional<ShipmentState> deliveredShipmentStateOpt = shipmentStateRepository.findByShipmentStateName("DELIVERED");
                    if (deliveredShipmentStateOpt.isEmpty()) {
                        logger.warn("DELIVERED shipment state not found, skip shipment status update for order {}", orderId);
                    } else {
                        ShipmentState deliveredShipmentState = deliveredShipmentStateOpt.get();
                        List<Shipment> shipments = shipmentRepository.findByOrderOrderIdOrderByCreatedDtDesc(orderId);

                        String shipAutoRemark = "Auto-updated to DELIVERED when order was marked DELIVERED" +
                                (statusNote != null && !statusNote.trim().isEmpty() ? " | Note: " + statusNote.trim() : "");
                        for (Shipment shipment : shipments) {
                            shipment.setShipmentState(deliveredShipmentState);
                            if (shipment.getDeliveredAt() == null) {
                                shipment.setDeliveredAt(LocalDateTime.now());
                            }
                            shipment.setShipmentRemark(shipAutoRemark);
                        }
                        if (!shipments.isEmpty()) {
                            shipmentRepository.saveAll(shipments);
                        }
                        logger.info("Updated {} shipments to DELIVERED for order {}",
                                   shipments.size(), orderId);
                    }
                } catch (Exception e) {
                    logger.error("Error updating shipments to DELIVERED for order {}: {}", orderId, e.getMessage(), e);
                    // Do not fail order status update due to shipment update failure
                }

                // 2) Mark all payments of this order as COMPLETED
                try {
                    Optional<PaymentState> completedPaymentStateOpt = paymentStateRepository.findByPaymentStateName("COMPLETED");
                    if (completedPaymentStateOpt.isEmpty()) {
                        logger.warn("COMPLETED payment state not found, skip payment status update for order {}", orderId);
                    } else {
                        PaymentState completedPaymentState = completedPaymentStateOpt.get();
                        List<Payment> payments = paymentRepository.findByOrderOrderId(orderId);

                        List<Payment> paymentsToUpdate = new ArrayList<>();
                        String payAutoRemark = "Auto-updated to COMPLETED when order was marked DELIVERED";
                        for (Payment payment : payments) {
                            if (payment.getPaymentState() == null
                                    || !"COMPLETED".equalsIgnoreCase(payment.getPaymentState().getPaymentStateName())) {
                                payment.setPaymentState(completedPaymentState);
                                String existingRemark = payment.getPaymentRemark();
                                if (existingRemark == null || existingRemark.trim().isEmpty()) {
                                    payment.setPaymentRemark(payAutoRemark);
                                } else if (!existingRemark.contains(payAutoRemark)) {
                                    payment.setPaymentRemark(existingRemark + " | " + payAutoRemark);
                                }
                                paymentsToUpdate.add(payment);
                            }
                        }
                        if (!paymentsToUpdate.isEmpty()) {
                            paymentRepository.saveAll(paymentsToUpdate);
                        }
                        logger.info("Updated {}/{} payments to COMPLETED for order {}",
                                   paymentsToUpdate.size(), payments.size(), orderId);
                    }
                } catch (Exception e) {
                    logger.error("Error updating payments to COMPLETED for order {}: {}", orderId, e.getMessage(), e);
                    // Do not fail order status update due to payment update failure
                }
            }
            
            // Add status note to order state history if provided
            if (statusNote != null && !statusNote.trim().isEmpty()) {
                // You can extend OrderStateHistory to include notes if needed
                logger.info("Status note for order {}: {}", orderId, statusNote);
            }
            
        } catch (Exception e) {
            logger.error("Error in special handling for order status update: {}", e.getMessage());
            // Don't fail the order update if special handling fails
        }
        
        return updatedOrder;
    }

    /**
     * Auto-select and apply best discount for the given order amount
     * Uses same logic as previewOrder for consistency
     * @param orderAmount The order amount to calculate discount for
     * @return AutoDiscountResult containing applied discount and calculated amount
     */
    private AutoDiscountResult applyBestAvailableDiscount(BigDecimal orderAmount) {
        AutoDiscountResult result = new AutoDiscountResult();
        
        DiscountDto bestDiscount = findBestDiscountForOrder(orderAmount);
        if (bestDiscount != null) {
            try {
                result.appliedDiscount = bestDiscount;
                result.discountAmount = bestDiscount.calculateDiscountAmount(orderAmount);
                result.success = true;
                logger.info("Auto-applied best discount {} with amount {} for order amount {}", 
                        bestDiscount.getDiscountCode(), result.discountAmount, orderAmount);
            } catch (Exception e) {
                logger.warn("Failed to apply auto discount: {}", e.getMessage());
                result.success = false;
                result.errorMessage = e.getMessage();
                // Keep discount amount as 0 and appliedDiscount as null
            }
        } else {
            logger.debug("No applicable discount found for order amount: {}", orderAmount);
        }
        
        return result;
    }
    
    /**
     * Inner class to hold auto discount processing result
     */
    private static class AutoDiscountResult {
        DiscountDto appliedDiscount = null;
        BigDecimal discountAmount = BigDecimal.ZERO;
        boolean success = false;
        String errorMessage = null;
    }

    /**
     * Shared discount calculation for preview and checkout flows.
     */
    private DiscountCalculationResult calculateOrderDiscount(List<CartItem> cartItems, BigDecimal subtotalAmount, String discountCode) {
        DiscountCalculationResult result = new DiscountCalculationResult();

        if (discountCode != null && !discountCode.trim().isEmpty()) {
            try {
                List<Long> productIds = cartItems.stream()
                        .map(item -> item.getProduct().getProductId())
                        .collect(Collectors.toList());

                DiscountValidationResult validationResult = discountService.validateDiscountCode(
                        discountCode, subtotalAmount, productIds);

                if (validationResult != null && validationResult.isValid()) {
                    result.appliedDiscount = discountService.getDiscountByCode(validationResult.getDiscountCode());
                    result.discountAmount = validationResult.getDiscountAmount();
                    result.discountMessage = "Mã giảm giá " + validationResult.getDiscountCode() + " đã được áp dụng";
                    if (result.appliedDiscount != null) {
                        logger.info("Manual discount applied - Code: {}, Amount: {}", result.appliedDiscount.getDiscountCode(), result.discountAmount);
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to apply manual discount code: {}", e.getMessage());
            }

            return result;
        }

        AutoDiscountResult autoDiscountResult = applyBestAvailableDiscount(subtotalAmount);
        result.appliedDiscount = autoDiscountResult.appliedDiscount;
        result.discountAmount = autoDiscountResult.discountAmount;
        result.discountMessage = result.appliedDiscount != null
                ? "Đã áp dụng mã giảm giá tự động: " + result.appliedDiscount.getDiscountCode()
                : "Không có mã giảm giá nào được áp dụng";
        return result;
    }

    /**
     * Shared shipping fee calculation for preview and checkout flows.
     */
    private ShippingFeeCalculationResult calculateOrderShippingFee(BigDecimal subtotalAmount) {
        ShippingFeeCalculationResult result = new ShippingFeeCalculationResult();

        try {
            ShippingFeeDto shippingFee = shippingFeeService.calculateShippingFee(subtotalAmount);
            if (shippingFee != null && shippingFee.getShippingFeeValue() != null) {
                result.appliedShippingFee = shippingFee;
                result.shippingFeeAmount = shippingFee.getShippingFeeValue();
                result.shippingMessage = shippingFee.getShippingFeeDisplayName() != null ? shippingFee.getShippingFeeDisplayName() : "Standard shipping";
                logger.info("Calculated shipping fee: {} ({})", result.shippingFeeAmount, result.shippingMessage);
            } else {
                result.shippingFeeAmount = DEFAULT_SHIPPING_FEE;
                result.shippingMessage = "Miễn phí vận chuyển";
                logger.info("No specific shipping fee found, using default: {}", result.shippingFeeAmount);
            }
        } catch (Exception e) {
            logger.error("Error calculating shipping fee for subtotal {}: {}", subtotalAmount, e.getMessage());
            result.shippingFeeAmount = DEFAULT_SHIPPING_FEE;
            result.shippingMessage = "Miễn phí vận chuyển";
        }

        return result;
    }

    /**
     * Validate whether a cart item can still be purchased based on the current product state and stock.
     * Uses product-level stock because the cart item schema does not store a product-variant identifier.
     */
    private void validateCartItemInventory(CartItem cartItem, List<String> errors) {
        if (cartItem == null || cartItem.getProduct() == null) {
            errors.add("A cart item is missing product information");
            return;
        }

        Product product = cartItem.getProduct();
        Integer requestedQuantity = cartItem.getCartItemQuantity() != null ? cartItem.getCartItemQuantity() : 0;
        Integer availableStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

        if (!Boolean.TRUE.equals(product.getProductEnabled())) {
            errors.add("Product '" + product.getProductName() + "' is no longer available");
            return;
        }

        if (product.getProductState() != null && product.getProductState().getProductStateName() != null
                && !"ACTIVE".equalsIgnoreCase(product.getProductState().getProductStateName())) {
            errors.add("Product '" + product.getProductName() + "' is not active");
            return;
        }

        if (availableStock <= 0) {
            errors.add("Product '" + product.getProductName() + "' is out of stock");
            return;
        }

        if (requestedQuantity > availableStock) {
            errors.add("Product '" + product.getProductName() + "' only has " + availableStock + " item(s) in stock, but " + requestedQuantity + " were requested");
        }
    }

    /**
     * Shared result for discount calculation.
     */
    private static class DiscountCalculationResult {
        private DiscountDto appliedDiscount;
        private BigDecimal discountAmount = BigDecimal.ZERO;
        private String discountMessage = "Không có mã giảm giá nào được áp dụng";
    }

    /**
     * Shared result for shipping fee calculation.
     */
    private static class ShippingFeeCalculationResult {
        private ShippingFeeDto appliedShippingFee;
        private BigDecimal shippingFeeAmount = BigDecimal.ZERO;
        private String shippingMessage = "Free shipping applied";
    }
    
    /**
     * Find the best discount for given order amount
     * Selects discount that provides maximum savings while meeting eligibility criteria
     * @param orderAmount Total order amount
     * @return Best applicable discount or null if none found
     */
    private DiscountDto findBestDiscountForOrder(BigDecimal orderAmount) {
        try {
            // Get all active and enabled discounts
            List<DiscountDto> activeDiscounts = discountService.getAllActiveDiscounts();
            if (activeDiscounts == null || activeDiscounts.isEmpty()) {
                return null;
            }
            
            DiscountDto bestDiscount = null;
            BigDecimal maxSavings = BigDecimal.ZERO;
            
            for (DiscountDto discount : activeDiscounts) {
                // Check if discount is enabled and meets minimum order requirement
                if (!discount.getIsActive()) {
                    continue;
                }
                
                // Check minimum order amount requirement
                if (discount.getMinOrderAmount() != null && 
                    orderAmount.compareTo(discount.getMinOrderAmount()) < 0) {
                    continue;
                }
                
                // Check if discount has remaining usage
                if (discount.getTotalUsageLimit() != null && discount.getUsedCount() != null &&
                    discount.getUsedCount().intValue() >= discount.getTotalUsageLimit().intValue()) {
                    continue;
                }
                
                // Calculate potential savings using DTO data — avoids extra DB call per discount
                BigDecimal discountAmount = discount.calculateDiscountAmount(orderAmount);
                if (discountAmount.compareTo(maxSavings) > 0) {
                    maxSavings = discountAmount;
                    bestDiscount = discount;
                }
            }
            
            return bestDiscount;
        } catch (Exception e) {
            logger.error("Error finding best discount for order amount {}: {}", orderAmount, e.getMessage());
            return null;
        }
    }
    
    /**
     * Generate unique order slug
     * @return Generated order slug
     */
    private String generateOrderSlug() {
        LocalDateTime now = LocalDateTime.now();
        String datePrefix = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timePrefix = now.format(DateTimeFormatter.ofPattern("HHmmssSSS"));
        
        // Generate slug: order-YYYYMMDD-HHMMSSSSSS-random
        String randomSuffix = String.valueOf((int)(Math.random() * 1000));
        return "order-" + datePrefix + "-" + timePrefix + "-" + randomSuffix;
    }
    
    @Override
    public Long getUserIdFromCart(Long cartId) {
        if (cartId == null) {
            logger.warn("CartId is null, cannot retrieve userId");
            return null;
        }
        
        try {
            Cart cart = cartRepository.findById(cartId).orElse(null);
            if (cart != null && cart.getUser() != null) {
                Long userId = cart.getUser().getUserId();
                logger.info("Retrieved userId: {} for Order.USER_ID assignment from cartId: {}", userId, cartId);
                return userId;
            } else {
                logger.info("Cart {} has no associated user (guest cart), userId will be null", cartId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error retrieving userId from cartId: {}, error: {}", cartId, e.getMessage());
            return null;
        }
    }
    
    /**
     * Helper method to escape special characters in JSON strings
     * @param input Input string to escape
     * @return Escaped string safe for JSON
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        
        return input.replace("\\", "\\\\")  // Escape backslashes first
                   .replace("\"", "\\\"")   // Escape double quotes
                   .replace("\n", "\\n")    // Escape newlines
                   .replace("\r", "\\r")    // Escape carriage returns
                   .replace("\t", "\\t");   // Escape tabs
    }
    
    /**
     * Send order notification email to admin contacts
     * @param order Order details to include in notification
     */
    private void sendOrderNotificationEmail(OrderDto order) {
        logger.info("Sending order notification email for order: {} - {}",
                order.getOrderId(), order.getOrderCode());

        try {
            // Gửi cho admin/contact như cũ
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE); // Get all contacts
            Page<ContactDto> enabledContacts = contactService.findContactsByCriteria(null, true, null, pageable);
            String subjectAdmin = buildOrderEmailSubject(order);
            String contentAdmin = buildOrderEmailContent(order);
            int successCount = 0;
            int totalCount = 0;
            if (!enabledContacts.isEmpty()) {
                for (ContactDto contact : enabledContacts.getContent()) {
                    if (contact.getContactEmail() != null && !contact.getContactEmail().trim().isEmpty()) {
                        totalCount++;
                        try {
                            emailService.sendNotificationEmail(contact.getContactEmail(), subjectAdmin, contentAdmin);
                            successCount++;
                            logger.debug("Sent order notification email to: {}", contact.getContactEmail());
                        } catch (Exception e) {
                            logger.error("Failed to send order notification email to {}: {}", contact.getContactEmail(), e.getMessage());
                        }
                    }
                }
                if (successCount == 0) {
                    logger.warn("Failed to send order notification email to any contact addresses");
                } else {
                    logger.info("Order notification email sent successfully to {}/{} contact addresses", successCount, totalCount);
                }
            } else {
                logger.warn("No enabled contacts found to send order notification email");
            }

            // Gửi cho khách hàng đặt hàng (nếu có email)
            if (order.getCustomerEmail() != null && !order.getCustomerEmail().trim().isEmpty()) {
                String subjectCustomer = buildOrderCustomerEmailSubject(order);
                String contentCustomer = buildOrderCustomerEmailContent(order);
                try {
                    emailService.sendNotificationEmail(order.getCustomerEmail(), subjectCustomer, contentCustomer);
                    logger.info("Order notification email sent to customer: {}", order.getCustomerEmail());
                } catch (Exception e) {
                    logger.error("Failed to send order notification email to customer {}: {}", order.getCustomerEmail(), e.getMessage());
                }
            } else {
                logger.warn("No customer email found to send order notification email");
            }
        } catch (Exception e) {
            logger.error("Unexpected error while sending order notification email: {}", e.getMessage(), e);
        }
    }

    /**
     * Build email subject for customer notification
     */
    private String buildOrderCustomerEmailSubject(OrderDto order) {
        return String.format("Đơn hàng tại Maison Art đã được đặt! - %s", order.getOrderCode());
    }

    /**
     * Build email content for customer notification (HTML template)
     */
    private String buildOrderCustomerEmailContent(OrderDto order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String orderDate = order.getCreatedDt().format(formatter);

        // Determine payment method
        String paymentMethod = order.getPaymentMethod() != null ? order.getPaymentMethod() : "";
        boolean isBankTransfer = "BANK_TRANSFER".equalsIgnoreCase(paymentMethod);
        boolean isCOD = "COD".equalsIgnoreCase(paymentMethod);

        String greeting = order.getCustomerName() != null ? order.getCustomerName() : "Quý khách";

        // Conditional intro paragraph
        String introText;
        if (isBankTransfer) {
            introText = "Chúng tôi đã nhận được đơn hàng của bạn. Vui lòng chuyển khoản theo thông tin bên dưới để xác nhận thanh toán. "
                    + "Sau khi thanh toán được xác nhận, chúng tôi sẽ đóng gói và giao hàng đến bạn trong thời gian sớm nhất.";
        } else if (isCOD) {
            introText = "Chúng tôi đã nhận được đơn hàng của bạn. "
                    + "Đơn hàng sẽ được chuẩn bị và giao đến địa chỉ của bạn. Bạn thanh toán khi nhận được hàng.";
        } else {
            introText = "Chúng tôi đã nhận được đơn hàng của bạn và đang tiến hành xử lý. "
                    + "Sau khi xác nhận, chúng tôi sẽ đóng gói và sắp xếp giao hàng đến bạn trong thời gian sớm nhất.";
        }

        // Payment summary values
        BigDecimal subtotal = order.getSubtotalAmount()    != null ? order.getSubtotalAmount()    : BigDecimal.ZERO;
        BigDecimal discount = order.getDiscountAmount()    != null ? order.getDiscountAmount()    : BigDecimal.ZERO;
        BigDecimal shipping = order.getShippingFeeAmount() != null ? order.getShippingFeeAmount() : BigDecimal.ZERO;
        BigDecimal total    = order.getTotalAmount()       != null ? order.getTotalAmount()       : BigDecimal.ZERO;

        String discountCode = (order.getDiscountCode() != null && !order.getDiscountCode().isBlank())
                ? " (" + order.getDiscountCode() + ")" : "";
        String discountLabel = discount.compareTo(BigDecimal.ZERO) > 0
                ? "-" + formatCurrency(discount) + discountCode
                : "Không có";

        // Payment method display name
        String paymentMethodDisplay;
        if (isBankTransfer) {
            paymentMethodDisplay = "Chuyển khoản qua ngân hàng";
        } else if (isCOD) {
            paymentMethodDisplay = "Thanh toán khi nhận hàng (COD)";
        } else {
            paymentMethodDisplay = paymentMethod;
        }

        // Order state display
        String orderStateName = (order.getOrderState() != null && order.getOrderState().getOrderStateDisplayName() != null)
                ? order.getOrderState().getOrderStateDisplayName()
                : (order.getOrderStateName() != null ? order.getOrderStateName() : "Đang xử lý");

        // Delivery info
        String receiverName  = order.getReceiverName()  != null ? order.getReceiverName()  : (order.getCustomerName() != null ? order.getCustomerName() : "");
        String receiverPhone = order.getReceiverPhone() != null ? order.getReceiverPhone() : (order.getCustomerPhoneNumber() != null ? order.getCustomerPhoneNumber() : "");
        String receiverEmail = order.getReceiverEmail() != null ? order.getReceiverEmail() : "";
        List<String> addrParts = new ArrayList<>();
        if (order.getAddressLine() != null && !order.getAddressLine().isBlank()) addrParts.add(order.getAddressLine());
        if (order.getWard()        != null && !order.getWard().isBlank())        addrParts.add(order.getWard());
        if (order.getCity()        != null && !order.getCity().isBlank())        addrParts.add(order.getCity());
        if (order.getCountry()     != null && !order.getCountry().isBlank())     addrParts.add(order.getCountry());
        String fullAddress = String.join(", ", addrParts);

        // Bank transfer section (only for BANK_TRANSFER)
        String bankTransferSection = "";
        if (isBankTransfer) {
            String bankAccountName   = "HOANG DINH HA";
            String bankName          = "Vietcombank";
            String bankAccountNumber = "9963879962";
            String bankBranch        = "";
            try {
                java.util.Optional<PolicyDto> bankPolicyOpt = policyService.findPolicyByName("PAYMENT_BANK_INFO");
                if (bankPolicyOpt.isPresent() && bankPolicyOpt.get().getPolicyValue() != null) {
                    java.util.Properties bankProps = new java.util.Properties();
                    bankProps.load(new java.io.StringReader(bankPolicyOpt.get().getPolicyValue()));
                    if (bankProps.getProperty("bank.account.name") != null)   bankAccountName   = bankProps.getProperty("bank.account.name");
                    if (bankProps.getProperty("bank.name") != null)           bankName          = bankProps.getProperty("bank.name");
                    if (bankProps.getProperty("bank.account.number") != null) bankAccountNumber = bankProps.getProperty("bank.account.number");
                    if (bankProps.getProperty("bank.branch") != null)         bankBranch        = bankProps.getProperty("bank.branch");
                }
            } catch (Exception e) {
                logger.debug("Could not load PAYMENT_BANK_INFO policy: {}", e.getMessage());
            }
            String transferNote = "Thanh toan " + order.getOrderCode();
            String branchRow = !bankBranch.isBlank()
                    ? "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Chi nhánh:</td>"
                        + "<td style=\"padding:6px 0;font-size:14px;\"><strong>" + bankBranch + "</strong></td></tr>"
                    : "";
            bankTransferSection =
                    "<hr style=\"margin:30px 0;border:none;border-top:1px solid #e5e5e5;\">"
                    + "<h2 style=\"font-size:18px;font-weight:bold;margin:0 0 16px;\">Thông tin chuyển khoản</h2>"
                    + "<table cellpadding=\"0\" cellspacing=\"0\">"
                    + "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Chủ tài khoản:</td>"
                        + "<td style=\"padding:6px 0;font-size:14px;\"><strong>" + bankAccountName + "</strong></td></tr>"
                    + "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Ngân hàng:</td>"
                        + "<td style=\"padding:6px 0;font-size:14px;\"><strong>" + bankName + "</strong></td></tr>"
                    + "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Số tài khoản:</td>"
                        + "<td style=\"padding:6px 0;font-size:14px;\"><strong>" + bankAccountNumber + "</strong></td></tr>"
                    + branchRow
                    + "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Nội dung CK:</td>"
                        + "<td style=\"padding:6px 0;font-size:14px;\"><strong>" + transferNote + "</strong></td></tr>"
                    + "</table>";
        }

        // Order note section (optional)
        String noteSection = "";
        if (order.getOrderNote() != null && !order.getOrderNote().isBlank()) {
            noteSection =
                    "<hr style=\"margin:30px 0;border:none;border-top:1px solid #e5e5e5;\">"
                    + "<h2 style=\"font-size:18px;font-weight:bold;margin:0 0 12px;\">Ghi chú đơn hàng</h2>"
                    + "<p style=\"font-size:14px;line-height:1.8;margin:0;\">" + escapeHtml(order.getOrderNote()) + "</p>";
        }

        String receiverEmailRow = !receiverEmail.isBlank()
                ? "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Email:</td>"
                    + "<td style=\"padding:6px 0;font-size:14px;\">" + escapeHtml(receiverEmail) + "</td></tr>"
                : "";
        String receiverAddressRow = !fullAddress.isBlank()
                ? "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Địa chỉ:</td>"
                    + "<td style=\"padding:6px 0;font-size:14px;\">" + escapeHtml(fullAddress) + "</td></tr>"
                : "";

        String bodyIntro = "<p style=\"font-size:15px;margin:0 0 10px;\">Xin chào " + escapeHtml(greeting) + ",</p>"
                + "<p style=\"line-height:1.8;font-size:14px;margin:0 0 16px;color:#555;\">" + escapeHtml(introText) + "</p>";

        String deliverySection = "<hr style=\"margin:30px 0;border:none;border-top:1px solid #e5e5e5;\">"
                + "<h2 style=\"font-size:18px;font-weight:bold;margin:0 0 16px;\">Thông tin giao hàng</h2>"
                + "<table cellpadding=\"0\" cellspacing=\"0\">"
                + "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Người nhận:</td>"
                    + "<td style=\"padding:6px 0;font-size:14px;\"><strong>" + escapeHtml(receiverName) + "</strong></td></tr>"
                + "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Số điện thoại:</td>"
                    + "<td style=\"padding:6px 0;font-size:14px;\">" + escapeHtml(receiverPhone) + "</td></tr>"
                + receiverEmailRow
                + receiverAddressRow
                + "</table>";

        return buildOrderEmailTemplate(
                "Xác nhận đơn hàng - Maison Art",
                "Cảm ơn bạn đã đặt hàng",
                bodyIntro,
                order,
                orderDate,
                orderStateName,
                paymentMethodDisplay,
                subtotal,
                discountLabel,
                shipping,
                total,
                bankTransferSection,
                deliverySection,
                noteSection,
                "<div style=\"margin-top:40px;text-align:center;line-height:1.8;color:#888;font-size:13px;\">"
                        + "<p style=\"margin:0 0 6px;\">Cảm ơn bạn đã mua hàng tại <strong style=\"color:#333;\">Maison Art</strong>!</p>"
                        + "<p style=\"margin:0;\">Cần hỗ trợ? Liên hệ: "
                        + "<a href=\"mailto:dinhha.hrc@gmail.com\" style=\"color:#333;text-decoration:underline;\">dinhha.hrc@gmail.com</a>"
                        + "</p>"
                        + "</div>");
    }

    /**
     * Build email subject for order notification
     * @param order Order details
     * @return Email subject
     */
    private String buildOrderEmailSubject(OrderDto order) {
        String orderType = (order.getUserId() != null) ? "KHÁCH HÀNG" : "KHÁCH VÃNG LAI";
        return String.format("[ĐƠN HÀNG MỚI] %s - %s - %s", 
                           order.getOrderCode(), 
                           orderType,
                           formatCurrency(order.getTotalAmount()));
    }
    
    /**
     * Build email content for order notification
     * @param order Order details
     * @return Email content
     */
    private String buildOrderEmailContent(OrderDto order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String orderTime = order.getCreatedDt().format(formatter);
        String orderType = (order.getUserId() != null) ? "Khách hàng đăng ký" : "Khách vãng lai";

        String paymentMethod = order.getPaymentMethod() != null ? order.getPaymentMethod() : "";
        String paymentMethodDisplay;
        if ("BANK_TRANSFER".equalsIgnoreCase(paymentMethod)) {
            paymentMethodDisplay = "Chuyển khoản qua ngân hàng";
        } else if ("COD".equalsIgnoreCase(paymentMethod)) {
            paymentMethodDisplay = "Thanh toán khi nhận hàng (COD)";
        } else {
            paymentMethodDisplay = paymentMethod;
        }

        BigDecimal subtotal = order.getSubtotalAmount() != null ? order.getSubtotalAmount() : BigDecimal.ZERO;
        BigDecimal discount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal shipping = order.getShippingFeeAmount() != null ? order.getShippingFeeAmount() : BigDecimal.ZERO;
        BigDecimal total = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        String discountCode = (order.getDiscountCode() != null && !order.getDiscountCode().isBlank())
                ? " (" + order.getDiscountCode() + ")" : "";
        String discountLabel = discount.compareTo(BigDecimal.ZERO) > 0
                ? "-" + formatCurrency(discount) + discountCode
                : "Không có";

        String orderStateName = (order.getOrderState() != null && order.getOrderState().getOrderStateDisplayName() != null)
                ? order.getOrderState().getOrderStateDisplayName()
                : (order.getOrderStateName() != null ? order.getOrderStateName() : "Đang xử lý");

        String receiverName = order.getReceiverName() != null ? order.getReceiverName() : "Chưa cập nhật";
        String receiverPhone = order.getReceiverPhone() != null ? order.getReceiverPhone() : "Chưa cập nhật";
        String receiverEmail = order.getReceiverEmail() != null ? order.getReceiverEmail() : "";

        List<String> addrParts = new ArrayList<>();
        if (order.getAddressLine() != null && !order.getAddressLine().isBlank()) addrParts.add(order.getAddressLine());
        if (order.getWard() != null && !order.getWard().isBlank()) addrParts.add(order.getWard());
        if (order.getCity() != null && !order.getCity().isBlank()) addrParts.add(order.getCity());
        if (order.getCountry() != null && !order.getCountry().isBlank()) addrParts.add(order.getCountry());
        String deliveryAddress = addrParts.isEmpty() ? "Chưa cập nhật" : String.join(", ", addrParts);

        String sessionInfo = order.getSessionId() != null && !order.getSessionId().isBlank()
                ? "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Session ID:</td>"
                    + "<td style=\"padding:6px 0;font-size:14px;\">" + escapeHtml(order.getSessionId()) + "</td></tr>"
                : "";

        String adminIntro = "<p style=\"font-size:15px;margin:0 0 10px;\">Một đơn hàng mới vừa được tạo trên hệ thống Maison Art.</p>"
                + "<p style=\"line-height:1.8;font-size:14px;margin:0 0 16px;color:#555;\">"
                + "Vui lòng kiểm tra thông tin thanh toán, xác nhận đơn hàng và tiếp tục quy trình xử lý giao hàng cho khách."
                + "</p>";

        String adminInfoSection = "<hr style=\"margin:30px 0;border:none;border-top:1px solid #e5e5e5;\">"
                + "<h2 style=\"font-size:18px;font-weight:bold;margin:0 0 16px;\">Thông tin cần xử lý</h2>"
                + "<table cellpadding=\"0\" cellspacing=\"0\">"
                + "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Loại khách:</td>"
                    + "<td style=\"padding:6px 0;font-size:14px;\"><strong>" + escapeHtml(orderType) + "</strong></td></tr>"
                + "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Khách đặt hàng:</td>"
                    + "<td style=\"padding:6px 0;font-size:14px;\">" + escapeHtml(defaultString(order.getCustomerName(), "Chưa cập nhật")) + "</td></tr>"
                + "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Email:</td>"
                    + "<td style=\"padding:6px 0;font-size:14px;\">" + escapeHtml(defaultString(order.getCustomerEmail(), "Chưa cập nhật")) + "</td></tr>"
                + "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Số điện thoại:</td>"
                    + "<td style=\"padding:6px 0;font-size:14px;\">" + escapeHtml(defaultString(order.getCustomerPhoneNumber(), "Chưa cập nhật")) + "</td></tr>"
                + sessionInfo
                + "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Người nhận:</td>"
                    + "<td style=\"padding:6px 0;font-size:14px;\">" + escapeHtml(receiverName) + "</td></tr>"
                + "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">SĐT nhận hàng:</td>"
                    + "<td style=\"padding:6px 0;font-size:14px;\">" + escapeHtml(receiverPhone) + "</td></tr>"
                + (!receiverEmail.isBlank()
                    ? "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Email nhận:</td>"
                        + "<td style=\"padding:6px 0;font-size:14px;\">" + escapeHtml(receiverEmail) + "</td></tr>"
                    : "")
                + "<tr><td style=\"padding:6px 16px 6px 0;color:#888;font-size:14px;\">Địa chỉ giao hàng:</td>"
                    + "<td style=\"padding:6px 0;font-size:14px;\">" + escapeHtml(deliveryAddress) + "</td></tr>"
                + "</table>";

        String noteSection = "";
        if (order.getOrderNote() != null && !order.getOrderNote().trim().isEmpty()) {
            noteSection = "<hr style=\"margin:30px 0;border:none;border-top:1px solid #e5e5e5;\">"
                    + "<h2 style=\"font-size:18px;font-weight:bold;margin:0 0 12px;\">Ghi chú của khách</h2>"
                    + "<p style=\"font-size:14px;line-height:1.8;margin:0;\">" + escapeHtml(order.getOrderNote()) + "</p>";
        }

        return buildOrderEmailTemplate(
                "Đơn hàng mới - Maison Art",
                "Đơn hàng mới cần xử lý",
                adminIntro,
                order,
                orderTime,
                orderStateName,
                paymentMethodDisplay,
                subtotal,
                discountLabel,
                shipping,
                total,
                "",
                adminInfoSection,
                noteSection,
                "<div style=\"margin-top:40px;font-size:13px;color:#888;line-height:1.8;\">"
                        + "Email này được gửi tự động từ hệ thống Maison Art."
                        + "</div>");
    }

    private String buildOrderEmailTemplate(
            String title,
            String heading,
            String introHtml,
            OrderDto order,
            String orderDate,
            String orderStateName,
            String paymentMethodDisplay,
            BigDecimal subtotal,
            String discountLabel,
            BigDecimal shipping,
            BigDecimal total,
            String extraTopSection,
            String infoSection,
            String noteSection,
            String footerHtml) {
        String safePaymentMethod = paymentMethodDisplay == null || paymentMethodDisplay.isBlank()
                ? "Chưa cập nhật"
                : paymentMethodDisplay;

        return "<!DOCTYPE html>"
                + "<html lang=\"vi\">"
                + "<head><meta charset=\"UTF-8\"><title>" + escapeHtml(title) + "</title></head>"
                + "<body style=\"margin:0;padding:0;background:#f7f7f7;font-family:Arial,Helvetica,sans-serif;color:#333333;\">"
                + "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#f7f7f7;padding:40px 0;\">"
                + "<tr><td align=\"center\">"
                + "<table width=\"620\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#ffffff;border-radius:6px;padding:40px;\">"
                + "<tr><td>"
                + "<p style=\"font-size:13px;margin:0 0 14px;color:#888;\">Maison Art</p>"
                + "<h1 style=\"font-size:26px;font-weight:bold;margin:0 0 18px;color:#222;\">" + escapeHtml(heading) + "</h1>"
                + introHtml
                + "<p style=\"font-size:13px;color:#888;margin:0 0 4px;\">"
                    + "Đơn hàng: <strong style=\"color:#333;\">#" + escapeHtml(order.getOrderCode()) + "</strong>"
                    + " &nbsp;|&nbsp; " + escapeHtml(orderDate)
                    + " &nbsp;|&nbsp; " + escapeHtml(orderStateName)
                + "</p>"
                + defaultString(extraTopSection, "")
                + "<hr style=\"margin:30px 0;border:none;border-top:1px solid #e5e5e5;\">"
                + "<h2 style=\"font-size:18px;font-weight:bold;margin:0 0 16px;\">Chi tiết sản phẩm</h2>"
                + buildOrderItemsHtml(order)
                + "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin-top:16px;\">"
                + "<tr>"
                    + "<td style=\"padding:6px 0;font-size:14px;color:#888;\">Tạm tính:</td>"
                    + "<td align=\"right\" style=\"padding:6px 0;font-size:14px;\">" + formatCurrency(subtotal) + "</td>"
                + "</tr>"
                + "<tr>"
                    + "<td style=\"padding:6px 0;font-size:14px;color:#888;\">Giảm giá:</td>"
                    + "<td align=\"right\" style=\"padding:6px 0;font-size:14px;\">" + discountLabel + "</td>"
                + "</tr>"
                + "<tr>"
                    + "<td style=\"padding:6px 0;font-size:14px;color:#888;\">Phí vận chuyển:</td>"
                    + "<td align=\"right\" style=\"padding:6px 0;font-size:14px;\">" + formatCurrency(shipping) + "</td>"
                + "</tr>"
                + "<tr><td colspan=\"2\" style=\"padding:4px 0;\">"
                    + "<hr style=\"border:none;border-top:1px solid #e5e5e5;margin:6px 0;\">"
                + "</td></tr>"
                + "<tr>"
                    + "<td style=\"padding:6px 0;font-size:16px;font-weight:bold;\">Tổng cộng:</td>"
                    + "<td align=\"right\" style=\"padding:6px 0;font-size:16px;font-weight:bold;\">" + formatCurrency(total) + "</td>"
                + "</tr>"
                + "<tr>"
                    + "<td colspan=\"2\" style=\"padding:8px 0 0;font-size:13px;color:#888;\">"
                        + "Phương thức thanh toán: <strong style=\"color:#333;\">" + escapeHtml(safePaymentMethod) + "</strong>"
                    + "</td>"
                + "</tr>"
                + "</table>"
                + defaultString(infoSection, "")
                + defaultString(noteSection, "")
                + defaultString(footerHtml, "")
                + "</td></tr></table></td></tr></table></body></html>";
    }

    private String buildOrderItemsHtml(OrderDto order) {
        List<OrderItemDto> itemList = getOrderItemsForEmail(order);
        String apiBase = mailConfiguration.getSystemWebsite().replaceAll("/$", "");
        String storagePath = policyService.findPolicyByName("STORAGE_PATH")
                .map(p -> p.getPolicyValue() != null ? p.getPolicyValue().replaceAll("/$", "") : "")
                .orElse("");

        StringBuilder itemsHtml = new StringBuilder();
        itemsHtml.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed;border-collapse:collapse;\">");
        itemsHtml.append("<tr>")
                .append("<th style=\"text-align:left;padding:8px 0;border-bottom:2px solid #e5e5e5;font-size:12px;color:#888;width:70px;\"></th>")
                .append("<th style=\"text-align:left;padding:8px 12px 8px 0;border-bottom:2px solid #e5e5e5;font-size:12px;color:#888;width:56%;\">Sản phẩm</th>")
                .append("<th style=\"text-align:center;padding:8px 0;border-bottom:2px solid #e5e5e5;font-size:12px;color:#888;width:12%;white-space:nowrap;\">SL</th>")
                .append("<th style=\"text-align:right;padding:8px 0;border-bottom:2px solid #e5e5e5;font-size:12px;color:#888;width:22%;white-space:nowrap;\">Thành tiền</th>")
                .append("</tr>");

        if (itemList != null && !itemList.isEmpty()) {
            for (OrderItemDto item : itemList) {
                BigDecimal rowTotal = (item.getUnitPrice() != null && item.getQuantity() != null)
                        ? item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                        : BigDecimal.ZERO;

                String imgUrl = resolvePrimaryProductImageUrl(item.getProductId(), apiBase, storagePath);
                String imgCell = imgUrl != null
                        ? "<td style=\"padding:12px 10px 12px 0;border-bottom:1px solid #e5e5e5;width:70px;vertical-align:middle;\">"
                            + "<img src=\"" + imgUrl + "\" alt=\"\" width=\"60\" height=\"60\" style=\"border-radius:4px;display:block;object-fit:cover;\"></td>"
                        : "<td style=\"padding:12px 10px 12px 0;border-bottom:1px solid #e5e5e5;width:70px;\"></td>";

                String productName = escapeHtml(defaultString(item.getProductName(), "Sản phẩm"));
                String attributesHtml = buildOrderItemAttributesHtml(item);

                itemsHtml.append("<tr>")
                        .append(imgCell)
                        .append("<td style=\"padding:12px 12px 12px 0;border-bottom:1px solid #e5e5e5;font-size:14px;vertical-align:top;\">")
                        .append("<div style=\"max-width:320px;line-height:1.45;word-break:break-word;overflow-wrap:anywhere;\">")
                        .append(productName)
                        .append(attributesHtml)
                        .append("</div>")
                        .append("</td>")
                        .append("<td align=\"center\" style=\"padding:12px 0;border-bottom:1px solid #e5e5e5;font-size:14px;white-space:nowrap;\">&times;")
                        .append(item.getQuantity() != null ? item.getQuantity() : 0)
                        .append("</td>")
                        .append("<td align=\"right\" style=\"padding:12px 0;border-bottom:1px solid #e5e5e5;font-size:14px;white-space:nowrap;\">")
                        .append(formatCurrency(rowTotal))
                        .append("</td>")
                        .append("</tr>");
            }
        } else {
            itemsHtml.append("<tr><td colspan=\"4\" style=\"padding:15px 0;border-bottom:1px solid #e5e5e5;color:#888;font-size:14px;\">Thông tin sản phẩm đang được cập nhật</td></tr>");
        }

        itemsHtml.append("</table>");
        return itemsHtml.toString();
    }

    private List<OrderItemDto> getOrderItemsForEmail(OrderDto order) {
        List<OrderItemDto> itemList = order.getOrderItems();
        if ((itemList == null || itemList.isEmpty()) && order.getOrderId() != null) {
            try {
                List<OrderItem> rawItems = orderItemRepository.findByOrderOrderId(order.getOrderId());
                if (!rawItems.isEmpty()) {
                    itemList = rawItems.stream()
                            .map(orderMapperUtil::mapToDto)
                            .collect(Collectors.toList());
                }
            } catch (Exception e) {
                logger.debug("Could not load order items for order {}: {}", order.getOrderId(), e.getMessage());
            }
        }
        return itemList;
    }

    private String resolvePrimaryProductImageUrl(Long productId, String apiBase, String storagePath) {
        if (productId == null) {
            return null;
        }

        try {
            Product prod = productRepository.findById(productId).orElse(null);
            if (prod != null && prod.getProductImages() != null) {
                for (ProductImage productImage : prod.getProductImages()) {
                    if (Boolean.TRUE.equals(productImage.getProductImagePrimary())
                            && productImage.getImage() != null
                            && productImage.getImage().getPathFile() != null) {
                        return apiBase + "/api/images/file" + storagePath + productImage.getImage().getPathFile();
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Could not load product image for product ID {}: {}", productId, e.getMessage());
        }

        return null;
    }

    private String buildOrderItemAttributesHtml(OrderItemDto item) {
        List<Map<String, Object>> attributes = parseOrderItemAttributes(item.getProductAttrJson());
        if (attributes.isEmpty()) {
            return "";
        }

        StringBuilder html = new StringBuilder();
        html.append("<div style=\"margin-top:6px;font-size:12px;color:#666;line-height:1.6;\">");
        for (Map<String, Object> attribute : attributes) {
            String attributeName = stringValue(attribute.get("attributeName"));
            String attributeValue = stringValue(attribute.get("attributeValue"));
            if (attributeName.isBlank() && attributeValue.isBlank()) {
                continue;
            }

            html.append("<div>");
            if (!attributeName.isBlank()) {
                html.append("<span style=\"color:#888;\">")
                        .append(escapeHtml(attributeName))
                        .append(":</span> ");
            }
            html.append("<span>")
                    .append(escapeHtml(attributeValue))
                    .append("</span></div>");
        }
        html.append("</div>");
        return html.toString();
    }

    private List<Map<String, Object>> parseOrderItemAttributes(String productAttrJson) {
        if (productAttrJson == null || productAttrJson.isBlank() || "[]".equals(productAttrJson)) {
            return Collections.emptyList();
        }

        try {
            return OBJECT_MAPPER.readValue(productAttrJson, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            logger.debug("Could not parse order item attribute JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private String defaultString(String value, String fallback) {
        return value != null ? value : fallback;
    }

    private String stringValue(Object value) {
        return value != null ? String.valueOf(value) : "";
    }

    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }

        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
    
    /**
     * Format currency amount for display in email
     * @param amount Amount to format
     * @return Formatted currency string
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0₫";
        }
        return String.format("%,d₫", amount.longValue());
    }
}
