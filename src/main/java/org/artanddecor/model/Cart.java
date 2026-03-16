package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Cart Entity
 * Represents shopping carts for users
 */
@Entity
@Table(name = "CART")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    
    private static final Logger logger = LoggerFactory.getLogger(Cart.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_ID")
    private Long cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CART_STATE_ID", nullable = false)
    private CartState cartState;

    @Column(name = "SESSION_ID", length = 100)
    private String sessionId;

    @Column(name = "CART_SLUG", length = 64, nullable = false, unique = true)
    private String cartSlug;

    @Column(name = "TOTAL_QUANTITY", nullable = false)
    private Integer totalQuantity = 0;

    @Column(name = "CART_ENABLED", nullable = false)
    private Boolean cartEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with CartItem
    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new Cart for user ID: {}, session ID: {}", 
                    user != null ? user.getUserId() : null, sessionId);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating Cart ID: {}", cartId);
        this.modifiedDt = LocalDateTime.now();
    }

    /**
     * Get total number of items in cart
     * @return Total item count
     */
    public int getTotalItemCount() {
        if (cartItems == null || cartItems.isEmpty()) {
            return 0;
        }
        return cartItems.stream()
                .mapToInt(CartItem::getCartItemQuantity)
                .sum();
    }
}