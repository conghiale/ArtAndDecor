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
 * PaymentState Entity
 * Represents payment states (PENDING, COMPLETED, FAILED, REFUNDED)
 */
@Entity
@Table(name = "PAYMENT_STATE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentState {
    
    private static final Logger logger = LogManager.getLogger(PaymentState.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_STATE_ID")
    private Long paymentStateId;

    @Column(name = "PAYMENT_STATE_NAME", nullable = false, unique = true, length = 64)
    private String paymentStateName;

    @Column(name = "PAYMENT_STATE_DISPLAY_NAME", length = 256)
    private String paymentStateDisplayName;

    @Column(name = "PAYMENT_STATE_REMARK", nullable = false, length = 256)
    private String paymentStateRemark;

    @Column(name = "PAYMENT_STATE_ENABLED", nullable = false)
    private Boolean paymentStateEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with Payment
    @OneToMany(mappedBy = "paymentState", fetch = FetchType.LAZY)
    private List<Payment> payments;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new PaymentState: {}", paymentStateName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating PaymentState: {}", paymentStateName);
        this.modifiedDt = LocalDateTime.now();
    }
}