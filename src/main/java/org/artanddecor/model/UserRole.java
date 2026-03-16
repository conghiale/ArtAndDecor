package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    private static final Logger logger = LoggerFactory.getLogger(UserRole.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ROLE_ID")
    private Long userRoleId;

    @Column(name = "USER_ROLE_NAME", nullable = false, unique = true, length = 64)
    private String userRoleName;

    @Column(name = "USER_ROLE_DISPLAY_NAME", length = 256)
    private String userRoleDisplayName;

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