package org.ArtAndDecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UserProvider Entity
 * Represents authentication providers (LOCAL, GOOGLE, FACEBOOK, etc.)
 */
@Entity
@Table(name = "USER_PROVIDER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProvider {
    
    private static final Logger logger = LogManager.getLogger(UserProvider.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_PROVIDER_ID")
    private Long userProviderId;

    @Column(name = "USER_PROVIDER_NAME", nullable = false, unique = true, length = 50)
    private String userProviderName;

    @Column(name = "USER_PROVIDER_REMARK_EN", length = 256)
    private String userProviderRemarkEn;

    @Column(name = "USER_PROVIDER_REMARK", nullable = false, length = 256)
    private String userProviderRemark;

    @Column(name = "USER_PROVIDER_ENABLED", nullable = false)
    private Boolean userProviderEnabled = true;

    @CreationTimestamp
    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @UpdateTimestamp
    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with User
    @OneToMany(mappedBy = "userProvider", fetch = FetchType.LAZY)
    private List<User> users;

    // Business methods
    public boolean isEnabled() {
        return this.userProviderEnabled != null && this.userProviderEnabled;
    }

    public void enable() {
        this.userProviderEnabled = true;
    }

    public void disable() {
        this.userProviderEnabled = false;
    }
}