package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order Entity 
 * Represents customer orders
 */
@Entity
@Table(name = "ORDER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    private static final Logger logger = LoggerFactory.getLogger(Order.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CART_ID")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_STATE_ID", nullable = false)
    private OrderState orderState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISCOUNT_ID")
    private Discount discount;

    // Discount information snapshot (lưu trữ thông tin discount tại thời điểm đặt hàng)
    @Column(name = "DISCOUNT_CODE", length = 50)
    private String discountCode;
    
    @Column(name = "DISCOUNT_TYPE", length = 100)
    private String discountType;
    
    @Column(name = "DISCOUNT_VALUE", precision = 15, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "ORDER_CODE", nullable = false, unique = true, length = 50)
    private String orderCode;

    @Column(name = "ORDER_SLUG", length = 64, nullable = false, unique = true)
    private String orderSlug;

    // Customer information snapshot (người đặt hàng)
    @Column(name = "CUSTOMER_NAME", length = 150)
    private String customerName;
    
    @Column(name = "CUSTOMER_PHONE_NUMBER", length = 15)
    private String customerPhoneNumber;
    
    @Column(name = "CUSTOMER_EMAIL", length = 100)
    private String customerEmail;
    
    @Column(name = "CUSTOMER_ADDRESS", columnDefinition = "TEXT")
    private String customerAddress;
    
    // Receiver information snapshot (người nhận)
    @Column(name = "RECEIVER_NAME", length = 150)
    private String receiverName;
    
    @Column(name = "RECEIVER_PHONE", length = 20)
    private String receiverPhone;
    
    @Column(name = "RECEIVER_EMAIL", length = 150)
    private String receiverEmail;
    
    // Receiver address details (địa chỉ người nhận chi tiết)
    @Column(name = "ADDRESS_LINE", length = 255)
    private String addressLine;
    
    @Column(name = "CITY", length = 100)
    private String city;
    
    @Column(name = "WARD", length = 100)
    private String ward;
    
    @Column(name = "COUNTRY", length = 100)
    private String country;

    // Financial breakdown
    @Column(name = "SUBTOTAL_AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotalAmount;
    
    @Column(name = "DISCOUNT_AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "SHIPPING_FEE_AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal shippingFeeAmount = BigDecimal.ZERO;

    @Column(name = "TOTAL_AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "ORDER_NOTE", columnDefinition = "TEXT")
    private String orderNote;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationships
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<OrderStateHistory> orderStateHistories;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Payment> payments;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Shipment> shipments;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new Order: {}, User ID: {}", orderCode, 
                    user != null ? user.getUserId() : null);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating Order: {}", orderCode);
        this.modifiedDt = LocalDateTime.now();
    }

    /**
     * Check if order has discount applied
     * @return true if discount is applied
     */
    public boolean hasDiscount() {
        return discount != null;
    }
}