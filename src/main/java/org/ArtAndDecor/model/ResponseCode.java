package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

/**
 * ResponseCode entity for managing API response codes
 */
@Entity
@Table(name = "RESPONSE_CODE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCode {

    private static final Logger logger = LogManager.getLogger(ResponseCode.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESPONSE_CODE_ID")
    private Long responseCodeId;

    @Column(name = "RESPONSE_CODE_NAME", nullable = false, unique = true, length = 64)
    private String responseCodeName;

    @Column(name = "RESPONSE_CODE_REMARK_EN", length = 256)
    private String responseCodeRemarkEn;

    @Column(name = "RESPONSE_CODE_REMARK", nullable = false, length = 256)
    private String responseCodeRemark;

    @Column(name = "RESPONSE_CODE_ENABLED", nullable = false)
    private Boolean responseCodeEnabled = true;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new ResponseCode: {}", responseCodeName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating ResponseCode: {}", responseCodeName);
        this.modifiedDt = LocalDateTime.now();
    }
}