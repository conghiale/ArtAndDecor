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
 * PaymentMethod Entity
 * Represents payment methods (COD, BANK_TRANSFER, MOMO, etc.)
 */
@Entity
@Table(name = "PAYMENT_METHOD")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentMethod.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_METHOD_ID")
    private Long paymentMethodId;

    @Column(name = "PAYMENT_METHOD_NAME", nullable = false, unique = true, length = 64)
    private String paymentMethodName;

    @Column(name = "PAYMENT_METHOD_DISPLAY_NAME", length = 256)
    private String paymentMethodDisplayName;

    @Column(name = "PAYMENT_METHOD_REMARK", nullable = false, length = 256)
    private String paymentMethodRemark;

    @Column(name = "PAYMENT_METHOD_ENABLED", nullable = false)
    private Boolean paymentMethodEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with Payment
    @OneToMany(mappedBy = "paymentMethod", fetch = FetchType.LAZY)
    private List<Payment> payments;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new PaymentMethod: {}", paymentMethodName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating PaymentMethod: {}", paymentMethodName);
        this.modifiedDt = LocalDateTime.now();
    }
}