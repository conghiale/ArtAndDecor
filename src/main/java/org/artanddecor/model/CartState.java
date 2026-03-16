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
 * CartState Entity
 * Represents cart states (ACTIVE, CHECKED_OUT, CHECKED_OUT_PART, ABANDONED)
 */
@Entity
@Table(name = "CART_STATE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartState {
    
    private static final Logger logger = LoggerFactory.getLogger(CartState.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_STATE_ID")
    private Long cartStateId;

    @Column(name = "CART_STATE_NAME", nullable = false, unique = true, length = 64)
    private String cartStateName;

    @Column(name = "CART_STATE_DISPLAY_NAME", length = 256)
    private String cartStateDisplayName;

    @Column(name = "CART_STATE_REMARK", nullable = false, length = 256)
    private String cartStateRemark;

    @Column(name = "CART_STATE_ENABLED", nullable = false)
    private Boolean cartStateEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with Cart
    @OneToMany(mappedBy = "cartState", fetch = FetchType.LAZY)
    private List<Cart> carts;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new CartState: {}", cartStateName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating CartState: {}", cartStateName);
        this.modifiedDt = LocalDateTime.now();
    }
}