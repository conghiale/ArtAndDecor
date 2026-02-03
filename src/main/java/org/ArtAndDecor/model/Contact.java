package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

@Entity
@Table(name = "CONTACT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

    private static final Logger logger = LogManager.getLogger(Contact.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONTACT_ID")
    private Long contactId;

    @Column(name = "CONTACT_NAME", length = 64, nullable = false)
    private String contactName;

    @Column(name = "CONTACT_SLUG", length = 64, nullable = false, unique = true)
    private String contactSlug;

    @Column(name = "CONTACT_ADDRESS", length = 256, nullable = false)
    private String contactAddress;

    @Column(name = "CONTACT_EMAIL", length = 64, nullable = false)
    private String contactEmail;

    @Column(name = "CONTACT_PHONE", length = 15, nullable = false)
    private String contactPhone;

    @Column(name = "CONTACT_FANPAGE", length = 256)
    private String contactFanpage;

    @Column(name = "CONTACT_ENABLED", nullable = false)
    private Boolean contactEnabled = true;

    @Column(name = "CONTACT_REMARK_EN", length = 256)
    private String contactRemarkEn;

    @Column(name = "CONTACT_REMARK", length = 256, nullable = false)
    private String contactRemark;

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
        logger.debug("Creating new Contact: {}", contactName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating Contact: {}", contactName);
        this.modifiedDt = LocalDateTime.now();
    }
}