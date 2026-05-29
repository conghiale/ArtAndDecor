package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Entity
@Table(name = "TESTIMONIAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Testimonial {

    private static final Logger logger = LoggerFactory.getLogger(Testimonial.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TESTIMONIAL_ID")
    private Long testimonialId;

    @Column(name = "TESTIMONIAL_NAME", length = 150, nullable = false)
    private String testimonialName;

    @Column(name = "TESTIMONIAL_QUOTE", columnDefinition = "TEXT", nullable = false)
    private String testimonialQuote;

    @Column(name = "TESTIMONIAL_ENABLED", nullable = false)
    private Boolean testimonialEnabled = true;

    @Column(name = "TESTIMONIAL_DISPLAY_ORDER")
    private Integer testimonialDisplayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID")
    private Image image;

    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new Testimonial: {}", testimonialName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating Testimonial: {}", testimonialName);
        this.modifiedDt = LocalDateTime.now();
    }
}
