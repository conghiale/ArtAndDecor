package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

/**
 * OrderStateHistory Entity
 * Represents history of order state changes
 * Matches database schema exactly
 */
@Entity
@Table(name = "ORDER_STATE_HISTORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStateHistory {
    
    private static final Logger logger = LogManager.getLogger(OrderStateHistory.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_STATE_HISTORY_ID")
    private Long orderStateHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OLD_STATE_ID", nullable = false)
    private OrderState oldState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEW_STATE_ID", nullable = false)
    private OrderState newState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHANGED_BY_USER_ID")
    private User changedByUser;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new OrderStateHistory for order ID: {}, old state: {}, new state: {}", 
                    order != null ? order.getOrderId() : null,
                    oldState != null ? oldState.getOrderStateName() : null,
                    newState != null ? newState.getOrderStateName() : null);
        this.createdDt = LocalDateTime.now();
    }

    // Helper methods for backward compatibility with service layer
    public LocalDateTime getStateChangeDate() {
        return this.createdDt;
    }

    public void setStateChangeDate(LocalDateTime stateChangeDate) {
        this.createdDt = stateChangeDate;
    }

    public Long getChangedByUserId() {
        return changedByUser != null ? changedByUser.getUserId() : null;
    }

    public String getChangedByUserName() {
        return changedByUser != null ? 
            (changedByUser.getFirstName() != null ? changedByUser.getFirstName() + " " + changedByUser.getLastName() : changedByUser.getUsername()) 
            : null;
    }
}