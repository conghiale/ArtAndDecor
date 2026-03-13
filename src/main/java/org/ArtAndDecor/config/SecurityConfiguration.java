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

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        logger.info("Configuring Security Filter Chain");
//
//        return http
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/swagger-ui.html",
//                                "/swagger-ui/**",
//                                "/v3/api-docs/**"
//                        ).permitAll()
//
//                        // Static resources
//                        .requestMatchers("/", "/art-and-decor", "/index.html", "/css/**", "/js/**", "/images/**",
//                                "/favicon.ico", "/assets/**").permitAll()
//
//                        // Authentication endpoints
//                        .requestMatchers("/api/auth/**").permitAll()
//
//                        // Public read-only endpoints for products
//                        .requestMatchers(HttpMethod.GET,
//                                "/api/products/**").permitAll()
//
//                        // Product creation and management - require ADMIN/MANAGER role
//                        .requestMatchers(HttpMethod.POST, "/api/products").hasAnyRole("ADMIN", "MANAGER")
//                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("ADMIN", "MANAGER")
//                        .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasAnyRole("ADMIN", "MANAGER")
//                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole("ADMIN", "MANAGER")
//
//                        // Other public read-only endpoints
//                        .requestMatchers(HttpMethod.GET,
//                                "/categories/**", "/blogs/**").permitAll()
//
//                        // Cart public endpoints - no authentication required
//                        .requestMatchers(HttpMethod.GET,
//                                "/api/carts/states",
//                                "/api/carts/item-states").permitAll()
//
//                        // Cart admin-only endpoints - require ADMIN role
//                        .requestMatchers(HttpMethod.GET,
//                                "/api/carts",
//                                "/api/carts/items",
//                                "/api/carts/states/{cartStateId:[\\d]+}",
//                                "/api/carts/item-states/{cartItemStateId:[\\d]+}").hasRole("ADMIN")
//
//                        // Cart authenticated endpoints - general access for authenticated users
//                        .requestMatchers(HttpMethod.GET,
//                                "/api/carts/slug/**").authenticated()
//
//                        // Cart user-specific endpoints with specific authorization
//                        // These will use method-level @PreAuthorize for owner/admin checks
//                        .requestMatchers(HttpMethod.GET,
//                                "/api/carts/{cartId:[\\d]+}",
//                                "/api/carts/user/{userId:[\\d]+}/active",
//                                "/api/carts/{cartId:[\\d]+}/items",
//                                "/api/carts/{cartId:[\\d]+}/items/active").authenticated()
//
//                        // All other cart operations require authentication
//                        .requestMatchers("/api/carts/**").authenticated()
//
//                        // Reviews - public read access for review display, authenticated for admin/user-specific endpoints
//                        .requestMatchers(HttpMethod.GET,
//                                "/api/reviews",
//                                "/api/reviews/*/likes/count",
//                                "/api/reviews/*/likes/user/*/exists",
//                                "/api/reviews/product/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/reviews/{reviewId:[\\d]+}").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/reviews/{reviewId:[\\d]+}/replies").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/reviews/{reviewId:[\\d]+}/likes").permitAll()
//                        .requestMatchers("/api/reviews/**").authenticated()
//
//                        // Images - public read access and upload
//                        .requestMatchers(HttpMethod.GET, "/api/images/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/images/upload", "/api/images/*/upload").permitAll()
//
//                        // Contact endpoints - public read by slug and search, admin for others
//                        .requestMatchers(HttpMethod.GET, "/api/contacts/slug/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/contacts").permitAll()
//                        .requestMatchers("/api/contacts/**").hasRole("ADMIN")
//
//                        // Policy endpoints - public read by slug, admin for others
//                        .requestMatchers(HttpMethod.GET, "/api/policies/slug/**").permitAll()
//                        .requestMatchers("/api/policies/**").hasRole("ADMIN")
//
//                        // Health and actuator endpoints
//                        .requestMatchers("/actuator/**", "/health").permitAll()
//
//                        // User role/provider endpoints - public access (includes search functionality and name lists for UI)
//                        .requestMatchers(HttpMethod.GET, "/api/users/roles/**", "/api/users/providers/**").permitAll()
//
//                        // User management endpoints - requires ADMIN role for all operations
//                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.GET, "/api/users/{userId}").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/api/users/{userId}").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PATCH, "/api/users/{userId}/status").hasRole("ADMIN")
//
//                        // Password management endpoints
//                        .requestMatchers(HttpMethod.PUT, "/api/users/change-password").authenticated()
//                        .requestMatchers(HttpMethod.PUT, "/api/users/reset-password/**").hasRole("ADMIN")
//
//                        // ORDER API ENDPOINTS - Updated to match actual OrderController endpoints
//                        // Customer Order endpoints - require CUSTOMER role with @PreAuthorize validation
//                        .requestMatchers(HttpMethod.POST,
//                                "/api/orders/checkout").authenticated()
//                        .requestMatchers(HttpMethod.GET,
//                                "/api/orders/my-orders",
//                                "/api/orders/my-orders/{orderId:[\\d]+}").authenticated()
//                        .requestMatchers(HttpMethod.PUT,
//                                "/api/orders/my-orders/{orderId:[\\d]+}/cancel").authenticated()
//
//                        // Admin/Manager Order endpoints - require ADMIN or MANAGER role
//                        .requestMatchers(HttpMethod.GET,
//                                "/api/admin/orders",
//                                "/api/admin/orders/{orderId:[\\d]+}",
//                                "/api/orders/{orderId:[\\d]+}/history",
//                                "/api/admin/order-states").hasAnyRole("ADMIN", "MANAGER")
//                        .requestMatchers(HttpMethod.POST,
//                                "/api/admin/orders").hasAnyRole("ADMIN", "MANAGER")
//                        .requestMatchers(HttpMethod.PUT,
//                                "/api/admin/orders/{orderId:[\\d]+}/state").hasAnyRole("ADMIN", "MANAGER")
//
//                        // Discount endpoints - Public validation, Admin management
//                        .requestMatchers(HttpMethod.POST,
//                                "/api/discounts/validate").permitAll() // Public discount validation
//                        .requestMatchers(HttpMethod.GET,
//                                "/api/admin/discounts",
//                                "/api/admin/discount-types").hasAnyRole("ADMIN", "MANAGER")
//                        .requestMatchers(HttpMethod.POST,
//                                "/api/admin/discounts").hasAnyRole("ADMIN", "MANAGER")
//                        .requestMatchers(HttpMethod.PUT,
//                                "/api/admin/discounts/{id:[\\d]+}").hasAnyRole("ADMIN", "MANAGER")
//
//                        // Admin endpoints - require ADMIN role
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//
//                        // All other requests need authentication
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .authenticationProvider(authenticationProvider)
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
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