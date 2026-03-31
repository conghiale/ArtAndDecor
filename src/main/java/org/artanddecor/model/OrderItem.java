package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OrderItem Entity
 * Represents items within an order
 */
@Entity
@Table(name = "ORDER_ITEM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private static final Logger logger = LoggerFactory.getLogger(OrderItem.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    // Snapshot product information
    @Column(name = "PRODUCT_NAME", nullable = false, length = 255)
    private String productName;

    @Column(name = "PRODUCT_CODE", nullable = false, length = 64)
    private String productCode;

    @Column(name = "PRODUCT_CATEGORY_NAME", nullable = false, length = 100)
    private String productCategoryName;

    @Column(name = "PRODUCT_TYPE_NAME", nullable = false, length = 100)
    private String productTypeName;

    // JSON snapshot of selected product attributes at order time
    @Column(name = "PRODUCT_ATTR_JSON", columnDefinition = "JSON")
    private String productAttrJson;

    @Column(name = "UNIT_PRICE", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Column(name = "TOTAL_PRICE", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new OrderItem for order ID: {}, product: {} ({})", 
                    order != null ? order.getOrderId() : null,
                    productName, productCode);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating OrderItem ID: {}", orderItemId);
        this.modifiedDt = LocalDateTime.now();
    }

    /**
     * Check if this order item has product attributes in JSON
     * @return true if has attributes, false otherwise
     */
    public boolean hasAttributes() {
        return productAttrJson != null && !productAttrJson.trim().isEmpty() && 
               !productAttrJson.equals("null") && !productAttrJson.equals("{}");
    }
}