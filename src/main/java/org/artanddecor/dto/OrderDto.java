package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order DTO for API requests and responses
 * Contains comprehensive information from ORDERS, ORDER_STATE, DISCOUNT, USER tables and related data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    
    private Long orderId;
    
    @NotBlank(message = "Order code is required")
    @Size(max = 50, message = "Order code must not exceed 50 characters")
    private String orderCode;
    
    @NotBlank(message = "Order slug is required")
    @Size(max = 64, message = "Order slug must not exceed 64 characters")
    private String orderSlug;
    
    // User reference for easy API usage
    private Long userId;
    
    // Order state reference for easy API usage
    private Long orderStateId;
    private String orderStateName;
    
    // Discount information snapshot (lưu thông tin discount tại thời điểm đặt hàng)
    private String discountCode;
    private String discountType;
    private BigDecimal discountValue;
    
    // Customer information snapshot (from USER table or CART.SESSION_ID)
    // Maps to USER.USER_NAME if USER_ID is not null, otherwise CART.SESSION_ID
    @Size(max = 150, message = "Customer name must not exceed 150 characters")
    private String customerName;
    
    // Maps to USER.PHONE_NUMBER or manual input
    @Size(max = 15, message = "Customer phone number must not exceed 15 characters")
    private String customerPhoneNumber;
    
    // Maps to USER.EMAIL or manual input
    @Email(message = "Invalid customer email format")
    @Size(max = 100, message = "Customer email must not exceed 100 characters")
    private String customerEmail;
    
    // Customer address (manual input or from USER address)
    private String customerAddress;
    
    // Receiver information snapshot (from SHIPMENT table)
    // Maps to SHIPMENT.RECEIVER_NAME
    @Size(max = 150, message = "Receiver name must not exceed 150 characters")
    private String receiverName;
    
    // Maps to SHIPMENT.RECEIVER_PHONE
    @Size(max = 20, message = "Receiver phone must not exceed 20 characters")
    private String receiverPhone;
    
    // Maps to SHIPMENT.RECEIVER_EMAIL
    @Email(message = "Invalid receiver email format")
    @Size(max = 150, message = "Receiver email must not exceed 150 characters")
    private String receiverEmail;
    
    // Receiver address details (from SHIPMENT table fields)
    @Size(max = 255, message = "Address line must not exceed 255 characters")
    private String addressLine;
    
    @Size(max = 100, message = "City must not exceed 100 characters") 
    private String city;
    
    @Size(max = 100, message = "Ward must not exceed 100 characters")
    private String ward;
    
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
    
    // Financial breakdown (ORDER table fields)
    // Maps to ORDER.SUBTOTAL_AMOUNT - original order amount before any adjustments
    @NotNull(message = "Subtotal amount is required")
    @DecimalMin(value = "0.0", message = "Subtotal amount must not be negative")
    private BigDecimal subtotalAmount;
    
    // Maps to ORDER.DISCOUNT_AMOUNT - snapshot from DISCOUNT calculation
    @DecimalMin(value = "0.0", message = "Discount amount must not be negative")
    private BigDecimal discountAmount;
    
    // Maps to ORDER.SHIPPING_FEE_AMOUNT - snapshot from SHIPMENT.SHIPPING_FEE_AMOUNT
    @DecimalMin(value = "0.0", message = "Shipping fee amount must not be negative")
    private BigDecimal shippingFeeAmount;
    
    // Maps to ORDER.TOTAL_AMOUNT - final amount = SUBTOTAL_AMOUNT + SHIPPING_FEE_AMOUNT - DISCOUNT_AMOUNT
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", message = "Total amount must not be negative")
    private BigDecimal totalAmount;
    
    @Size(max = 1000, message = "Order note must not exceed 1000 characters")
    private String orderNote;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // =============================================
    // NESTED DTOs (complete related entity data)
    // =============================================
    private UserDto user;
    private OrderStateDto orderState;
    
    // Related data
    private List<OrderItemDto> orderItems;
    private List<PaymentDto> payments;
    private List<ShipmentDto> shipments;
    
    // Computed fields
    private Integer totalItems;
    private Boolean hasValidDiscount;
    private String currentOrderStatus;
    private BigDecimal savedAmount;
    
    /**
     * Generate full name from user object
     */
    public String generateFullName() {
        if (user == null) {
            return "Unknown User";
        }
        return user.getFullNameValue();
    }
    
    /**
     * Check if order is pending
     */
    public boolean isPending() {
        return orderState != null && "PENDING".equalsIgnoreCase(orderState.getOrderStateName());
    }
    
    /**
     * Check if order is confirmed
     */
    public boolean isConfirmed() {
        return orderState != null && "CONFIRMED".equalsIgnoreCase(orderState.getOrderStateName());
    }
    
    /**
     * Check if order is processing
     */
    public boolean isProcessing() {
        return orderState != null && "PROCESSING".equalsIgnoreCase(orderState.getOrderStateName());
    }
    
    /**
     * Check if order is shipped
     */
    public boolean isShipped() {
        return orderState != null && "SHIPPED".equalsIgnoreCase(orderState.getOrderStateName());
    }
    
    /**
     * Check if order is delivered
     */
    public boolean isDelivered() {
        return orderState != null && "DELIVERED".equalsIgnoreCase(orderState.getOrderStateName());
    }
    
    /**
     * Check if order is cancelled
     */
    public boolean isCancelled() {
        return orderState != null && "CANCELLED".equalsIgnoreCase(orderState.getOrderStateName());
    }
    
    /**
     * Calculate final amount based on database schema
     * TOTAL_AMOUNT = SUBTOTAL_AMOUNT + SHIPPING_FEE_AMOUNT - DISCOUNT_AMOUNT
     */
    public BigDecimal calculateFinalAmount() {
        BigDecimal subtotal = subtotalAmount != null ? subtotalAmount : BigDecimal.ZERO;
        BigDecimal shipping = shippingFeeAmount != null ? shippingFeeAmount : BigDecimal.ZERO;
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        
        return subtotal.add(shipping).subtract(discount);
    }
    
    /**
     * Verify if calculated total matches stored total amount
     */
    public boolean isTotalAmountValid() {
        if (totalAmount == null) return false;
        return totalAmount.compareTo(calculateFinalAmount()) == 0;
    }
    
    /**
     * Generate customer name from user or session
     * Should use USER.USER_NAME if USER_ID exists, otherwise use customerName field
     */
    public String getEffectiveCustomerName() {
        if (customerName != null && !customerName.trim().isEmpty()) {
            return customerName;
        }
        if (user != null && user.getUserName() != null) {
            return user.getUserName();
        }
        return "Guest Customer";
    }
    
    /**
     * Generate full receiver address from current order address fields
     * or from shipment data as fallback
     */
    public String generateReceiverAddress(ShipmentDto shipment) {
        // Try to use order's address fields first
        StringBuilder orderAddress = new StringBuilder();
        if (addressLine != null) orderAddress.append(addressLine);
        if (ward != null) orderAddress.append(", ").append(ward);
        if (city != null) orderAddress.append(", ").append(city);
        if (country != null) orderAddress.append(", ").append(country);
        
        // If order has address info, use it
        if (orderAddress.length() > 0) {
            return orderAddress.toString();
        }
        
        // Otherwise use shipment address as fallback
        if (shipment == null) return "";
        
        StringBuilder address = new StringBuilder();
        if (shipment.getAddressLine() != null) address.append(shipment.getAddressLine());
        if (shipment.getWard() != null) address.append(", ").append(shipment.getWard());
        if (shipment.getCity() != null) address.append(", ").append(shipment.getCity());
        if (shipment.getCountry() != null) address.append(", ").append(shipment.getCountry());
        
        return address.toString();
    }
}