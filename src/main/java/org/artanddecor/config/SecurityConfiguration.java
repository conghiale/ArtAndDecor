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

                        // Product endpoints - structured by functionality and access control
                        // Public product read access (customer-facing)
                        .requestMatchers(HttpMethod.GET, "/products/slug/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/search", "/products/in-stock").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/top-selling", "/products/featured").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/highlighted", "/products/latest").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/{productId:[\\d+]}/images").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/types/**", "/products/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/states", "/products/attrs").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/attributes/{attributeId:[\\d+]}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/{productId:[\\d+]}").permitAll()
                        
                        // Admin-only product read access (management operations)
                        .requestMatchers(HttpMethod.GET, "/products/stats/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/products/attributes").hasAnyRole("ADMIN", "MANAGER")
                        
                        // Product management operations (Admin/Manager only)
                        .requestMatchers(HttpMethod.POST, "/products").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/products/{productId:[\\d+]}/images/{imageId:[\\d+]}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/products/attributes").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/products/types").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/products/categories").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/products/attrs").hasAnyRole("ADMIN", "MANAGER")
                        
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/products/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole("ADMIN", "MANAGER")

                        // Cart endpoints - structured by functionality
                        // Public cart access (support both logged-in and guest users)
                        .requestMatchers(HttpMethod.GET, "/carts/current").permitAll()
                        .requestMatchers(HttpMethod.POST, "/carts/items").permitAll()
                        .requestMatchers(HttpMethod.GET, "/carts/items/count").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/carts/items/*").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/carts/items/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/carts/items").permitAll()
                        .requestMatchers(HttpMethod.GET, "/carts/states", "/carts/item-states").permitAll()
                        
                        // Admin-only cart operations
                        .requestMatchers(HttpMethod.GET, "/carts").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/carts/admin/guest/items").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/carts/states/{cartStateId}", "/carts/item-states/{cartItemStateId}").hasRole("ADMIN")

                        // Contact endpoints - public read by slug/search, admin for management
                        .requestMatchers(HttpMethod.GET, "/contacts/slug/**", "/contacts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/contacts/names", "/contacts/stats/**").hasRole("ADMIN")
                        .requestMatchers("/contacts/**").hasRole("ADMIN")

                        // Image endpoints - public read and upload, file serving endpoints
                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/file/**", "/images/download/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/images/upload").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/images/*/upload").permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/stats/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/images/**").hasAnyRole("ADMIN", "MANAGER")

                        // Order endpoints - restructured according to new API requirements
                        // Order preview and creation - permitAll (accessible by both authenticated and guest users)
                        .requestMatchers(HttpMethod.POST, "/orders/preview").permitAll()
                        .requestMatchers(HttpMethod.POST, "/orders/create").permitAll()
                        .requestMatchers(HttpMethod.POST, "/orders/checkout").permitAll()
                        
                        // Admin-only order operations  
                        .requestMatchers(HttpMethod.GET, "/orders/*").hasRole("ADMIN") // GET /orders/{orderId} - Admin only
                        .requestMatchers(HttpMethod.PATCH, "/orders/*/status").hasAnyRole("ADMIN", "MANAGER")
                        
                        // Customer order operations - permitAll (no authentication required)
                        .requestMatchers(HttpMethod.GET, "/orders/my-orders", "/orders/my-orders/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/orders/my-orders/*/cancel").permitAll()
                        
                        // Order read operations - permitAll (accessible by both ADMIN and CUSTOMER)
                        .requestMatchers(HttpMethod.GET, "/orders").permitAll()
                        .requestMatchers(HttpMethod.GET, "/orders/states").permitAll()
                        .requestMatchers(HttpMethod.GET, "/orders/state-history").permitAll()
                        .requestMatchers(HttpMethod.GET, "/orders/items").permitAll()

                        // Policy endpoints - public read access for all GET operations, admin for management
                        .requestMatchers(HttpMethod.GET, "/policies/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/policies").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/policies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/policies/**").hasRole("ADMIN")

                        // Review endpoints - public read access, authenticated for write/admin operations
                        .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/reviews").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/reviews/**").hasAnyRole("ADMIN", "MANAGER")

                        // Shipment endpoints - only API endpoints that actually exist in ShipmentController
                        // Public read access for shipping configuration (customer needs to see shipping options)
                        .requestMatchers(HttpMethod.GET, "/shipments/fee-types").permitAll()
                        .requestMatchers(HttpMethod.GET, "/shipments/fees").permitAll()
                        .requestMatchers(HttpMethod.GET, "/shipments/states").permitAll() 
                        .requestMatchers(HttpMethod.GET, "/shipments").permitAll()
                        
                        // Admin-only shipment management operations (exact API endpoints)
                        .requestMatchers(HttpMethod.POST, "/shipments/fee-types").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/shipments/fee-types/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/shipments/fees").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/shipments/fees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/shipments/states").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/shipments/states/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/shipments/**").hasRole("ADMIN")

                        // Payment endpoints - only API endpoints that actually exist in PaymentController
                        // Public read access for payment configuration (customer needs to see payment options)
                        .requestMatchers(HttpMethod.GET, "/payments/methods").permitAll()
                        .requestMatchers(HttpMethod.GET, "/payments/states").permitAll()
                        .requestMatchers(HttpMethod.GET, "/payments").permitAll()
                        
                        // Admin-only payment management operations (exact API endpoints)
                        .requestMatchers(HttpMethod.POST, "/payments/methods").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/payments/methods/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/payments/states").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/payments/states/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/payments/**").hasRole("ADMIN")
                        
                        // Admin-only payment statistics endpoints
                        .requestMatchers(HttpMethod.GET, "/payments/stats/**").hasRole("ADMIN")

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

                        // Blog endpoints - structured by functionality and access control
                        // Public blog read access (customer-facing)
                        .requestMatchers(HttpMethod.GET, "/blogs/types").permitAll()
                        .requestMatchers(HttpMethod.GET, "/blogs/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/blogs").permitAll()
                        .requestMatchers(HttpMethod.GET, "/blogs/slug/**").permitAll()
                        
                        // Admin-only blog operations
                        .requestMatchers(HttpMethod.GET, "/blogs/types/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/blogs/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/blogs/{blogId:[\\d+]}").hasRole("ADMIN")
                        
                        .requestMatchers(HttpMethod.POST, "/blogs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/blogs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/blogs/**").hasRole("ADMIN")

                        // Page endpoints - structured by functionality and access control
                        // Public page read access (customer-facing)
                        .requestMatchers(HttpMethod.GET, "/pages").permitAll() // Search pages with criteria
                        .requestMatchers(HttpMethod.GET, "/pages/slug/**").permitAll() // Get page by slug
                        
                        // Admin-only page operations
                        .requestMatchers(HttpMethod.GET, "/pages/{pageId:[\\d+]}").hasRole("ADMIN") // Get page by ID
                        .requestMatchers(HttpMethod.POST, "/pages").hasRole("ADMIN") // Create new page
                        .requestMatchers(HttpMethod.PUT, "/pages/**").hasRole("ADMIN") // Update page
                        .requestMatchers(HttpMethod.PATCH, "/pages/**").hasRole("ADMIN") // Update page status

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
                "https://maisonart.vn"
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