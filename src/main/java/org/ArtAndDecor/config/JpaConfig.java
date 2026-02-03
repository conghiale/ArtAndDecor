package org.ArtAndDecor.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA Configuration
 */
@Configuration
@EnableJpaRepositories(basePackages = "org.ArtAndDecor.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
    
    private static final Logger logger = LogManager.getLogger(JpaConfig.class);

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    public JpaConfig() {
        logger.info("Initializing JPA configuration");
    }

    /**
     * Log database configuration on startup
     */
    public void logDatabaseConfig() {
        logger.info("Database URL: {}", databaseUrl);
        logger.info("Database Username: {}", databaseUsername);
        logger.info("JPA repositories enabled for package: org.ArtAndDecor.repository");
        logger.info("JPA auditing enabled");
        logger.info("Transaction management enabled");
    }
}