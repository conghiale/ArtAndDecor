package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CartItemState Entity
 * Represents cart item states (ACTIVE, ORDERED)
 */
@Entity
@Table(name = "CART_ITEM_STATE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemState {
    
    private static final Logger logger = LogManager.getLogger(CartItemState.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_ITEM_STATE_ID")
    private Long cartItemStateId;

    @Column(name = "CART_ITEM_STATE_NAME", nullable = false, unique = true, length = 64)
    private String cartItemStateName;

    @Column(name = "CART_ITEM_STATE_DISPLAY_NAME", length = 256)
    private String cartItemStateDisplayName;

    @Column(name = "CART_ITEM_STATE_REMARK", nullable = false, length = 256)
    private String cartItemStateRemark;

    @Column(name = "CART_ITEM_STATE_ENABLED", nullable = false)
    private Boolean cartItemStateEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with CartItem
    @OneToMany(mappedBy = "cartItemState", fetch = FetchType.LAZY)
    private List<CartItem> cartItems;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new CartItemState: {}", cartItemStateName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating CartItemState: {}", cartItemStateName);
        this.modifiedDt = LocalDateTime.now();
    }
}