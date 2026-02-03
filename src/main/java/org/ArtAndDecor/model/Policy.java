package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

@Entity
@Table(name = "POLICY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Policy {

    private static final Logger logger = LogManager.getLogger(Policy.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POLICY_ID")
    private Long policyId;

    @Column(name = "POLICY_NAME", length = 64, nullable = false, unique = true)
    private String policyName;

    @Column(name = "POLICY_SLUG", length = 64)
    private String policySlug;

    @Column(name = "POLICY_VALUE", columnDefinition = "TEXT", nullable = false)
    private String policyValue;

    @Column(name = "POLICY_REMARK_EN", length = 256)
    private String policyRemarkEn;

    @Column(name = "POLICY_REMARK", length = 256, nullable = false)
    private String policyRemark;

    @Column(name = "POLICY_ENABLED", nullable = false)
    private Boolean policyEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new Policy: {}", policyName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating Policy: {}", policyName);
        this.modifiedDt = LocalDateTime.now();
    }
}