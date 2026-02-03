package org.ArtAndDecor.config;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application Configuration for Authentication
 * Configures UserDetailsService, AuthenticationProvider, and PasswordEncoder
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    
    private final UserRepository userRepository;

    /**
     * UserDetailsService bean for loading user details
     * Only loads enabled users for authentication
     * Uses eager-loaded queries to avoid LazyInitializationException
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return usernameOrEmail -> {
            logger.debug("Loading user details for: {}", usernameOrEmail);
            
            // Try to find enabled user by username first with eager-loaded relationships
            return userRepository.findByUserNameAndUserEnabledWithDetails(usernameOrEmail, true)
                    .or(() -> {
                        logger.debug("User not found by username, trying email: {}", usernameOrEmail);
                        // Try by email with eager-loaded relationships
                        return userRepository.findByEmailAndUserEnabledWithDetails(usernameOrEmail, true);
                    })
                    .orElseThrow(() -> {
                        logger.warn("Enabled user not found with username or email: {}", usernameOrEmail);
                        return new UsernameNotFoundException("User not found or not enabled: " + usernameOrEmail);
                    });
        };
    }

    /**
     * Authentication Provider bean using DAO
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        logger.info("Configuring DaoAuthenticationProvider");
        
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Authentication Manager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        logger.info("Configuring AuthenticationManager");
        return config.getAuthenticationManager();
    }

    /**
     * Password Encoder bean using BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Configuring BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }
}