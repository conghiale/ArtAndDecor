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
 * UserRole Entity
 * Represents user roles (ADMIN, MANAGER, STAFF, CUSTOMER)
 */
@Entity
@Table(name = "USER_ROLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {
    
    private static final Logger logger = LogManager.getLogger(UserRole.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ROLE_ID")
    private Long userRoleId;

    @Column(name = "USER_ROLE_NAME", nullable = false, unique = true, length = 64)
    private String userRoleName;

    @Column(name = "USER_ROLE_REMARK_EN", length = 256)
    private String userRoleRemarkEn;

    @Column(name = "USER_ROLE_REMARK", nullable = false, length = 256)
    private String userRoleRemark;

    @Column(name = "USER_ROLE_ENABLED", nullable = false)
    private Boolean userRoleEnabled = true;

    @CreationTimestamp
    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @UpdateTimestamp
    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationship with User
    @OneToMany(mappedBy = "userRole", fetch = FetchType.LAZY)
    private List<User> users;

    // Business methods
    public boolean isEnabled() {
        return this.userRoleEnabled != null && this.userRoleEnabled;
    }

    public void enable() {
        this.userRoleEnabled = true;
    }

    public void disable() {
        this.userRoleEnabled = false;
    }
}