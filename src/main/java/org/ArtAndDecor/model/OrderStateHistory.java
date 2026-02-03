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
    private Orders order;

    @Column(name = "ORDER_STATE_HISTORY_SLUG", nullable = false, unique = true, length = 64)
    private String orderStateHistorySlug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OLD_STATE_ID", nullable = false)
    private OrderState oldState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEW_STATE_ID", nullable = false)
    private OrderState newState;

    @Column(name = "ORDER_STATE_HISTORY_NOTE", columnDefinition = "TEXT")
    private String orderStateHistoryNote;

    @Column(name = "ORDER_STATE_HISTORY_REMARK_EN", length = 256)
    private String orderStateHistoryRemarkEn;

    @Column(name = "ORDER_STATE_HISTORY_REMARK", nullable = false, length = 256)
    private String orderStateHistoryRemark;

    @Column(name = "SEO_META_ID")
    private Long seoMetaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEO_META_ID", referencedColumnName = "SEO_META_ID", insertable = false, updatable = false)
    private SeoMeta seoMeta;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new OrderStateHistory for order ID: {}, old state: {}, new state: {}", 
                    order != null ? order.getOrderId() : null,
                    oldState != null ? oldState.getOrderStateName() : null,
                    newState != null ? newState.getOrderStateName() : null);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating OrderStateHistory ID: {}", orderStateHistoryId);
        this.modifiedDt = LocalDateTime.now();
    }
}