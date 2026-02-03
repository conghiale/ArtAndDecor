package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CartItem Entity
 * Represents items in shopping carts
 */
@Entity
@Table(name = "CART_ITEM")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    
    private static final Logger logger = LogManager.getLogger(CartItem.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_ITEM_ID")
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CART_ID", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CART_ITEM_STATE_ID", nullable = false)
    private CartItemState cartItemState;

    @Column(name = "CART_ITEM_QUANTITY", nullable = false)
    private Integer cartItemQuantity = 1;

    @Column(name = "CART_ITEM_TOTAL_PRICE", nullable = false, precision = 15, scale = 2)
    private BigDecimal cartItemTotalPrice;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new CartItem for cart ID: {}, product ID: {}", 
                    cart != null ? cart.getCartId() : null,
                    product != null ? product.getProductId() : null);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
        if (this.cartItemQuantity == null) {
            this.cartItemQuantity = 1;
        }
        calculateTotalPrice();
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating CartItem ID: {}", cartItemId);
        this.modifiedDt = LocalDateTime.now();
    }

    /**
     * Calculate total price based on quantity and product price
     */
    public void calculateTotalPrice() {
        if (product != null && product.getProductPrice() != null && cartItemQuantity != null) {
            this.cartItemTotalPrice = product.getProductPrice().multiply(new BigDecimal(cartItemQuantity));
        }
    }

    /**
     * Update quantity and recalculate total price
     * @param newQuantity New quantity
     */
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity != null && newQuantity > 0) {
            this.cartItemQuantity = newQuantity;
            calculateTotalPrice();
            logger.debug("Updated CartItem quantity to {} with new total price: {}", newQuantity, cartItemTotalPrice);
        }
    }
}