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
 * OrderState Entity
 * Represents order states (PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, RETURNED)
 */
@Entity
@Table(name = "ORDER_STATE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderState {
    
    private static final Logger logger = LogManager.getLogger(OrderState.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_STATE_ID")
    private Long orderStateId;

    @Column(name = "ORDER_STATE_NAME", nullable = false, unique = true, length = 64)
    private String orderStateName;

    @Column(name = "ORDER_STATE_REMARK_EN", length = 256)
    private String orderStateRemarkEn;

    @Column(name = "ORDER_STATE_REMARK", nullable = false, length = 256)
    private String orderStateRemark;

    @Column(name = "ORDER_STATE_ENABLED", nullable = false)
    private Boolean orderStateEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with Order
    @OneToMany(mappedBy = "orderState", fetch = FetchType.LAZY)
    private List<Orders> orders;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new OrderState: {}", orderStateName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating OrderState: {}", orderStateName);
        this.modifiedDt = LocalDateTime.now();
    }
}