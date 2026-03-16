package org.artanddecor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

    public WebMvcConfig() {
        logger.info("Initializing Web MVC configuration");
    }

    /**
     * Configure CORS mappings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("Configuring CORS mappings");

        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        logger.debug("CORS configured for /api/** endpoints");
    }
}