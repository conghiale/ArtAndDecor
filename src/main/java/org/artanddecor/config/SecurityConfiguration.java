package org.artanddecor.config;

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
        logger.info("Configuring Security Filter Chain with context-path /api");

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Swagger and API docs
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Static resources and root endpoints
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/images/**",
                                "/favicon.ico", "/assets/**").permitAll()

                        // Authentication endpoints - public access
                        .requestMatchers("/auth/**").permitAll()

                        // Home endpoint - public access
                        .requestMatchers(HttpMethod.GET, "/").permitAll()

                        // Product endpoints - public read, authenticated write
                        .requestMatchers(HttpMethod.GET, "/products/stats/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/products").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/products/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole("ADMIN", "MANAGER")

                        // Cart endpoints - role-based access
                        .requestMatchers(HttpMethod.GET, "/carts/states", "/carts/item-states").permitAll()
                        .requestMatchers(HttpMethod.GET, "/carts", "/carts/items").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/carts/states/{cartStateId}", "/carts/item-states/{cartItemStateId}").hasRole("ADMIN")
                        .requestMatchers("/carts/**").authenticated()

                        // Contact endpoints - public read by slug/search, admin for management
                        .requestMatchers(HttpMethod.GET, "/contacts/slug/**", "/contacts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/contacts/names", "/contacts/stats/**").hasRole("ADMIN")
                        .requestMatchers("/contacts/**").hasRole("ADMIN")

                        // Image endpoints - public read and upload, file serving endpoints
                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/file/**", "/images/download/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/images/upload", "/images/*/upload").permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/stats/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/images/**").hasAnyRole("ADMIN", "MANAGER")

                        // Order endpoints - structured by functionality
                        // Customer order operations  
                        .requestMatchers(HttpMethod.POST, "/orders/checkout").authenticated()
                        .requestMatchers(HttpMethod.GET, "/orders/my-orders", "/orders/my-orders/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/orders/my-orders/*/cancel").authenticated()
                        
                        // Order management operations (Admin/Manager)
                        .requestMatchers(HttpMethod.GET, "/orders/management", "/orders/management/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/orders/management").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/orders/management/*/state").hasAnyRole("ADMIN", "MANAGER")
                        
                        // Order history - mixed access with @PreAuthorize validation
                        .requestMatchers(HttpMethod.GET, "/orders/*/history").authenticated()
                        
                        // Order states - master data for admin
                        .requestMatchers(HttpMethod.GET, "/orders/states").hasAnyRole("ADMIN", "MANAGER")
                        
                        // Discount operations
                        .requestMatchers(HttpMethod.POST, "/orders/discounts/validate").permitAll()
                        .requestMatchers(HttpMethod.GET, "/orders/discounts", "/orders/discount-types").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/orders/discounts").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/orders/discounts/*").hasAnyRole("ADMIN", "MANAGER")

                        // Policy endpoints - public read by slug, admin for management
                        .requestMatchers(HttpMethod.GET, "/policies/slug/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/policies/names", "/policies/stats/**").hasRole("ADMIN")
                        .requestMatchers("/policies/**").hasRole("ADMIN")

                        // Review endpoints - public read access, authenticated for write/admin operations
                        .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/reviews").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/reviews/**").hasAnyRole("ADMIN", "MANAGER")

                        // Shipment endpoints - structured by functionality
                        .requestMatchers(HttpMethod.GET, "/shipments/my-shipments/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/shipments/track/**", "/shipments/calculate-shipping-fee").permitAll()
                        .requestMatchers(HttpMethod.GET, "/shipments/states").permitAll()
                        .requestMatchers("/shipments/states/management/**").hasRole("ADMIN")
                        .requestMatchers("/shipments/management/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/shipments/**").hasAnyRole("ADMIN", "MANAGER")

                        // User endpoints - role-based access
                        .requestMatchers(HttpMethod.GET, "/users/roles/**", "/users/providers/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/{userId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/change-password").authenticated()

                        // Health endpoints
                        .requestMatchers("/actuator/**", "/health").permitAll()

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
                "http://localhost:5173",
                "http://localhost:8180",
                "https://art-and-decor.com"
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