package org.ArtAndDecor.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security Configuration
 * Configures Spring Security with JWT authentication
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring Security Filter Chain");
        
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Static resources
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/images/**",
                                "/favicon.ico", "/assets/**").permitAll()
                        
                        // Authentication endpoints
                        .requestMatchers("/auth/**").permitAll()
                        
                        // Public read-only endpoints
                        .requestMatchers(HttpMethod.GET,
                                "/users/check-username", "/users/check-email",
                                "/products/**", "/categories/**",
                                "/blogs/**", "/images/**").permitAll()
                        
                        // Health and actuator endpoints
                        .requestMatchers("/actuator/**", "/health").permitAll()
                        
                        // User search by name endpoint - requires authentication
                        .requestMatchers(HttpMethod.GET, "/users/search-by-name").authenticated()
                        
                        // User search endpoint - requires ADMIN or MANAGER role
                        .requestMatchers(HttpMethod.GET, "/users/search").hasAnyRole("ADMIN", "MANAGER")
                        
                        // User endpoints by ID - requires ADMIN or MANAGER role for viewing
                        .requestMatchers(HttpMethod.GET, "/users/{userId}").hasAnyRole("ADMIN", "MANAGER")
                        
                        // Get all users - requires ADMIN or MANAGER role
                        .requestMatchers(HttpMethod.GET, "/users").hasAnyRole("ADMIN", "MANAGER")
                        
                        // Create user - requires ADMIN or MANAGER role
                        .requestMatchers(HttpMethod.POST, "/users").hasAnyRole("ADMIN", "MANAGER")
                        
                        // Update user - requires ADMIN or MANAGER role
                        .requestMatchers(HttpMethod.PUT, "/users/{userId}").hasAnyRole("ADMIN", "MANAGER")
                        
                        // Update user status - requires ADMIN role only
                        .requestMatchers(HttpMethod.PATCH, "/users/{userId}/status").hasRole("ADMIN")
                        
                        // Delete user - requires ADMIN role only
                        .requestMatchers(HttpMethod.DELETE, "/users/{userId}").hasRole("ADMIN")
                        
                        // Change own password - requires authentication
                        .requestMatchers(HttpMethod.PUT, "/users/change-password").authenticated()
                        
                        // Change password by username - requires authentication (self-service)
                        .requestMatchers(HttpMethod.PUT, "/users/username/{userName}/change-password").authenticated()
                        
                        // Admin reset password - requires ADMIN role
                        .requestMatchers(HttpMethod.PUT, "/users/{userId}/reset-password").hasRole("ADMIN")
                        
                        // Admin endpoints - require ADMIN role
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        
                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("Configuring CORS");
        
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:3001", 
                "http://localhost:5173",
                "http://localhost:8080",
                "https://art-and-decor.com",
                "https://www.art-and-decor.com"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}