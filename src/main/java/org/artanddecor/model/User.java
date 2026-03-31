package org.artanddecor.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User Entity
 * Represents system users with authentication and profile information
 */
@Entity
@Table(name = "USER")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    
    private static final Logger logger = LoggerFactory.getLogger(User.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_PROVIDER_ID", nullable = false)
    private UserProvider userProvider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ROLE_ID", nullable = false)
    private UserRole userRole;

    @Column(name = "USER_ENABLED", nullable = false)
    @Builder.Default
    private Boolean userEnabled = true;

    @Column(name = "USER_NAME", nullable = false, unique = true, length = 64)
    private String userName;

    @Column(name = "PASSWORD", length = 150)
    private String password;

    @Column(name = "FIRST_NAME", length = 50)
    private String firstName;

    @Column(name = "LAST_NAME", length = 50)
    private String lastName;

    @Column(name = "PHONE_NUMBER", length = 15)
    private String phoneNumber;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "IMAGE_AVATAR_NAME", length = 150)
    private String imageAvatarName;

    @Column(name = "SOCIAL_MEDIA", columnDefinition = "TEXT")
    private String socialMedia;

    @Column(name = "LAST_LOGIN_DT")
    private LocalDateTime lastLoginDt;

    @CreationTimestamp
    @Column(name = "CREATED_DT", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @UpdateTimestamp
    @Column(name = "MODIFIED_DT", nullable = false)
    private LocalDateTime modifiedDt;

    // One-to-Many relationships
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ProductReviewLike> productReviewLikes;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Cart> carts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Order> orders;

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating new User: {}", userName);
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.modifiedDt = now;
        if (this.userEnabled == null) {
            this.userEnabled = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        logger.debug("Updating User: {}", userName);
        this.modifiedDt = LocalDateTime.now();
    }

    /**
     * Get full name combining first and last name
     * @return Full name or empty string if both are null
     */
    public String getFullName() {
        if (firstName == null && lastName == null) {
            return "";
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    /**
     * Get full name value (alias for getFullName for consistent API)
     * @return Full name or empty string if both are null
     */
    public String getFullNameValue() {
        return getFullName();
    }

    // =============================================
    // UserDetails interface implementation
    // =============================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userRole != null && userRole.getUserRoleName() != null) {
            String roleName = "ROLE_" + userRole.getUserRoleName().toUpperCase();
            logger.debug("User {} has role: {}", userName, roleName);
            return Collections.singletonList(new SimpleGrantedAuthority(roleName));
        }
        // Default to CUSTOMER role if no role specified
        logger.debug("User {} defaulting to ROLE_CUSTOMER", userName);
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Can be customized based on business logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Can be customized based on business logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Can be customized based on business logic
    }

    @Override
    public boolean isEnabled() {
        return userEnabled != null ? userEnabled : true;
    }
}