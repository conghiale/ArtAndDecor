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
 * ShipmentState Entity
 * Represents shipment states (PREPARING, SHIPPED, IN_TRANSIT, DELIVERED, FAILED_DELIVERY)
 */
@Entity
@Table(name = "SHIPMENT_STATE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentState {
    
    private static final Logger logger = LoggerFactory.getLogger(ShipmentState.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SHIPMENT_STATE_ID")
    private Long shipmentStateId;

    @Column(name = "SHIPMENT_STATE_NAME", nullable = false, unique = true, length = 64)
    private String shipmentStateName;

    @Column(name = "SHIPMENT_STATE_DISPLAY_NAME", length = 256)
    private String shipmentStateDisplayName;

    @Column(name = "SHIPMENT_STATE_REMARK", nullable = false, length = 256)
    private String shipmentStateRemark;

    @Column(name = "SHIPMENT_STATE_ENABLED", nullable = false)
    private Boolean shipmentStateEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with Shipment
    @OneToMany(mappedBy = "shipmentState", fetch = FetchType.LAZY)
    private List<Shipment> shipments;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new ShipmentState: {}", shipmentStateName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating ShipmentState: {}", shipmentStateName);
        this.modifiedDt = LocalDateTime.now();
    }
}