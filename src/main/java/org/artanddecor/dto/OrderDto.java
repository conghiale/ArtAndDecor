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
    
    @NotBlank(message = "Mã đơn hàng là bắt buộc")
    @Size(max = 50, message = "Mã đơn hàng không được vượt quá 50 ký tự")
    private String orderCode;
    
    @NotBlank(message = "Slug đơn hàng là bắt buộc")
    @Size(max = 64, message = "Slug đơn hàng không được vượt quá 64 ký tự")
    private String orderSlug;
    
    // User reference for easy API usage
    private Long userId;
    
    // Session ID for guest users (from cart session)
    @Size(max = 100, message = "ID phiên không được vượt quá 100 ký tự")
    private String sessionId;
    
    // Order state reference for easy API usage
    private Long orderStateId;
    private String orderStateName;
    
    // Discount information snapshot (lưu thông tin discount tại thời điểm đặt hàng)
    private String discountCode;
    private String discountType;
    private BigDecimal discountValue;
    
    // Customer information snapshot (from USER table or CART.SESSION_ID)
    // Maps to USER.USER_NAME if USER_ID is not null, otherwise CART.SESSION_ID
    @Size(max = 150, message = "Tên khách hàng không được vượt quá 150 ký tự")
    private String customerName;
    
    // Maps to USER.PHONE_NUMBER or manual input
    @Size(max = 15, message = "Số điện thoại khách hàng không được vượt quá 15 ký tự")
    private String customerPhoneNumber;
    
    // Maps to USER.EMAIL or manual input
    @Email(message = "Định dạng email khách hàng không hợp lệ")
    @Size(max = 100, message = "Email khách hàng không được vượt quá 100 ký tự")
    private String customerEmail;
    
    // Customer address (manual input or from USER address)
    private String customerAddress;
    
    // Receiver information snapshot (from SHIPMENT table)
    // Maps to SHIPMENT.RECEIVER_NAME
    @Size(max = 150, message = "Tên người nhận không được vượt quá 150 ký tự")
    private String receiverName;
    
    // Maps to SHIPMENT.RECEIVER_PHONE
    @Size(max = 20, message = "Số điện thoại người nhận không được vượt quá 20 ký tự")
    private String receiverPhone;
    
    // Maps to SHIPMENT.RECEIVER_EMAIL
    @Email(message = "Định dạng email người nhận không hợp lệ")
    @Size(max = 150, message = "Email người nhận không được vượt quá 150 ký tự")
    private String receiverEmail;
    
    // Receiver address details (from SHIPMENT table fields)
    @Size(max = 255, message = "Địa chỉ chi tiết không được vượt quá 255 ký tự")
    private String addressLine;
    
    @Size(max = 100, message = "Thành phố không được vượt quá 100 ký tự") 
    private String city;
    
    @Size(max = 100, message = "Phường/Xã không được vượt quá 100 ký tự")
    private String ward;
    
    @Size(max = 100, message = "Quốc gia không được vượt quá 100 ký tự")
    private String country;
    
    // Financial breakdown (ORDER table fields)
    // Maps to ORDER.SUBTOTAL_AMOUNT - original order amount before any adjustments
    @NotNull(message = "Tạm tính là bắt buộc")
    @DecimalMin(value = "0.0", message = "Tạm tính không được là số âm")
    private BigDecimal subtotalAmount;
    
    // Maps to ORDER.DISCOUNT_AMOUNT - snapshot from DISCOUNT calculation
    @DecimalMin(value = "0.0", message = "Số tiền giảm giá không được là số âm")
    private BigDecimal discountAmount;
    
    // Maps to ORDER.SHIPPING_FEE_AMOUNT - snapshot from SHIPMENT.SHIPPING_FEE_AMOUNT
    @DecimalMin(value = "0.0", message = "Phí vận chuyển không được là số âm")
    private BigDecimal shippingFeeAmount;
    
    // Maps to ORDER.TOTAL_AMOUNT - final amount = SUBTOTAL_AMOUNT + SHIPPING_FEE_AMOUNT - DISCOUNT_AMOUNT
    @NotNull(message = "Tổng thanh toán là bắt buộc")
    @DecimalMin(value = "0.0", message = "Tổng thanh toán không được là số âm")
    private BigDecimal totalAmount;
    
    @Size(max = 1000, message = "Ghi chú đơn hàng không được vượt quá 1000 ký tự")
    private String orderNote;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
    
    // Payment information snapshot (từ PAYMENT table tại thời điểm thanh toán)
    @Size(max = 100, message = "Phương thức thanh toán không được vượt quá 100 ký tự")
    private String paymentMethod;
    
    private PaymentStateDto paymentState;
    
    @Size(max = 100, message = "Mã giao dịch không được vượt quá 100 ký tự")
    private String transactionId;
    
    @Size(max = 256, message = "Ghi chú thanh toán không được vượt quá 256 ký tự")
    private String paymentRemark;
    
    // =============================================
    // NESTED DTOs (essential related entity data only)
    // =============================================
    private OrderStateDto orderState;
    
    // Related data - only essential lists
    private List<OrderItemDto> orderItems;
    
    // Computed fields
    private Integer totalItems;
    private Boolean hasValidDiscount;
    private String currentOrderStatus;
    private BigDecimal savedAmount;
    
    /**
     * Check if payment is completed
     */
    public boolean isPaymentCompleted() {
        return paymentState != null && "COMPLETED".equalsIgnoreCase(paymentState.getPaymentStateName());
    }
    
    /**
     * Check if payment is pending
     */
    public boolean isPaymentPending() {
        return paymentState != null && "PENDING".equalsIgnoreCase(paymentState.getPaymentStateName());
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
     * Check if order is returned
     */
    public boolean isReturned() {
        return orderState != null && "RETURNED".equalsIgnoreCase(orderState.getOrderStateName());
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