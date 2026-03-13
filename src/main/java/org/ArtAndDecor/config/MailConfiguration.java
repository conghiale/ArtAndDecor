package org.ArtAndDecor.config;

import org.ArtAndDecor.dto.PolicyDto;
import org.ArtAndDecor.services.PolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Mail Configuration
 * Creates JavaMailSender bean dynamically from POLICY table settings
 * Falls back to application.properties if POLICY settings are not available
 */
@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MailConfiguration.class);

    private final MailProperties mailProperties;
    private final PolicyService policyService;

    public MailConfiguration(
            MailProperties mailProperties,
            @Autowired(required = false) PolicyService policyService) {

        this.mailProperties = mailProperties;
        this.policyService = policyService;
    }

    // Configuration keys - Updated to match new format
    private static final String EMAIL_CONFIG_KEY = "EMAIL_CONFIG";
    private static final String SMTP_HOST = "mail.smtp.host";
    private static final String SMTP_PORT = "mail.smtp.port";
    private static final String SMTP_USERNAME = "mail.smtp.username";
    private static final String SMTP_PASSWORD = "mail.smtp.password";
    private static final String SMTP_AUTH = "mail.smtp.auth";
    private static final String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private static final String SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";
    private static final String SMTP_SSL_TRUST = "mail.smtp.ssl.trust";
    private static final String SMTP_SSL_PROTOCOLS = "mail.smtp.ssl.protocols";
    private static final String SMTP_CONNECTIONTIMEOUT = "mail.smtp.connectiontimeout";
    private static final String SMTP_TIMEOUT = "mail.smtp.timeout";
    private static final String SMTP_WRITETIMEOUT = "mail.smtp.writetimeout";
    private static final String SMTP_DEBUG = "mail.smtp.debug";
    private static final String FROM_ADDRESS = "mail.smtp.sendfromaddr";
    private static final String FROM_NAME = "mail.smtp.sendfromname";
    private static final String SUPPORT_ADDRESS = "mail.support.address";
    private static final String SYSTEM_NAME = "system.name";
    private static final String SYSTEM_WEBSITE = "system.website";
    private static final String SYSTEM_SUPPORT_PHONE = "system.support.phone";

    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        try {
            // Try to get configuration from POLICY table
            Properties emailConfig = getPolicyEmailConfiguration();
            
            if (emailConfig != null && !emailConfig.isEmpty()) {
                logger.info("Configuring mail sender from POLICY table settings");
                configureFromPolicy(mailSender, emailConfig);
            } else {
                logger.info("POLICY email configuration not available, using application.properties fallback");
                configureFromProperties(mailSender);
            }
            
        } catch (Exception e) {
            logger.warn("Failed to configure mail from POLICY table, using application.properties: {}", e.getMessage());
            configureFromProperties(mailSender);
        }
        
        return mailSender;
    }

    /**
     * Configure mail sender from POLICY table settings
     */
    private void configureFromPolicy(JavaMailSenderImpl mailSender, Properties config) {
        // Basic settings
        String host = config.getProperty(SMTP_HOST, mailProperties.getHost());
        String portStr = config.getProperty(SMTP_PORT, String.valueOf(mailProperties.getPort()));
        String username = config.getProperty(SMTP_USERNAME, mailProperties.getUsername());
        String password = config.getProperty(SMTP_PASSWORD, mailProperties.getPassword());
        
        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(portStr));
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // Properties - Enhanced with new configuration
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", config.getProperty(SMTP_AUTH, "true"));
        props.put("mail.smtp.starttls.enable", config.getProperty(SMTP_STARTTLS_ENABLE, "true"));
        props.put("mail.smtp.starttls.required", config.getProperty(SMTP_STARTTLS_REQUIRED, "true"));
        props.put("mail.smtp.ssl.trust", config.getProperty(SMTP_SSL_TRUST, "*"));
        props.put("mail.smtp.ssl.protocols", config.getProperty(SMTP_SSL_PROTOCOLS, "TLSv1.2"));
        
        // Timeout configurations
        props.put("mail.smtp.connectiontimeout", config.getProperty(SMTP_CONNECTIONTIMEOUT, "5000"));
        props.put("mail.smtp.timeout", config.getProperty(SMTP_TIMEOUT, "5000"));
        props.put("mail.smtp.writetimeout", config.getProperty(SMTP_WRITETIMEOUT, "5000"));
        
        // Debug configuration
        props.put("mail.debug", config.getProperty(SMTP_DEBUG, "false"));

        logger.info("Mail configured from POLICY - Host: {}, Port: {}, Username: {}, SSL Trust: {}", 
                   host, portStr, username, config.getProperty(SMTP_SSL_TRUST, "*"));
    }

    /**
     * Configure mail sender from application.properties fallback
     */
    private void configureFromProperties(JavaMailSenderImpl mailSender) {
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());
        
        Properties props = mailSender.getJavaMailProperties();
        props.putAll(mailProperties.getProperties());
        
        logger.info("Mail configured from application.properties - Host: {}, Port: {}", 
                   mailProperties.getHost(), mailProperties.getPort());
    }

    /**
     * Get email configuration from POLICY table
     * Using Java Properties for robust parsing of configuration string
     * @return Properties object or null if not available
     */
    private Properties getPolicyEmailConfiguration() {
        if (policyService == null) {
            logger.debug("PolicyService not available during mail configuration initialization");
            return null;
        }
        
        try {
            Optional<PolicyDto> policyOpt = policyService.findPoliciesByCriteria(EMAIL_CONFIG_KEY, true, null)
                    .stream()
                    .findFirst();
            
            if (policyOpt.isPresent()) {
                String configValue = policyOpt.get().getPolicyValue();
                if (StringUtils.hasText(configValue)) {
                    Properties emailProps = parseEmailProperties(configValue);
                    if (emailProps != null) {
                        logger.info("Successfully loaded {} email configuration properties from POLICY table", emailProps.size());
                        
                        // Log essential configuration keys (without sensitive values)
                        if (logger.isDebugEnabled()) {
                            logger.debug("Email config - Host: {}, Port: {}, Auth: {}, SSL Trust: {}",
                                    emailProps.getProperty(SMTP_HOST),
                                    emailProps.getProperty(SMTP_PORT), 
                                    emailProps.getProperty(SMTP_AUTH),
                                    emailProps.getProperty(SMTP_SSL_TRUST));
                        }
                        return emailProps;
                    } else {
                        logger.warn("Failed to parse EMAIL_CONFIG properties from database");
                    }
                } else {
                    logger.warn("EMAIL_CONFIG policy found but value is empty");
                }
            } else {
                logger.info("EMAIL_CONFIG policy not found in database, will use fallback configuration");
            }
            
        } catch (Exception e) {
            logger.error("Failed to load email configuration from POLICY table: {}", e.getMessage(), e);
        }
        
        return null;
    }

    /**
     * Parse email properties from string
     * Based on Java Properties format for robust parsing
     * @param emailProperties Email properties string
     * @return Properties object or null if parsing fails
     */
    private Properties parseEmailProperties(String emailProperties) {
        try {
            Properties props = new Properties();
            Reader reader = new InputStreamReader(
                new ByteArrayInputStream(emailProperties.getBytes()), StandardCharsets.UTF_8);
            props.load(reader);
            
            logger.debug("Parsed email properties: host={}, port={}, auth={}", 
                     props.getProperty("mail.smtp.host"),
                     props.getProperty("mail.smtp.port"),
                     props.getProperty("mail.smtp.auth"));
            
            // Validate essential properties
            validateEssentialProperties(props);
            
            return props;
            
        } catch (Exception e) {
            logger.error("Error parsing email properties: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Validate essential email properties
     * @param props Properties to validate
     */
    private void validateEssentialProperties(Properties props) {
        String[] requiredKeys = {"mail.smtp.host", "mail.smtp.port", "mail.smtp.sendfromaddr"};
        
        for (String key : requiredKeys) {
            String value = props.getProperty(key);
            if (!StringUtils.hasText(value)) {
                logger.warn("Missing or empty required email property: {}", key);
            }
        }
        
        // Validate port number
        String port = props.getProperty("mail.smtp.port");
        if (StringUtils.hasText(port)) {
            try {
                int portNum = Integer.parseInt(port);
                if (portNum <= 0 || portNum > 65535) {
                    logger.warn("Invalid SMTP port number: {} (should be 1-65535)", port);
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid SMTP port format: {}", port);
            }
        }
        
        // Validate timeout values
        validateTimeoutProperty(props, "mail.smtp.connectiontimeout");
        validateTimeoutProperty(props, "mail.smtp.timeout");
        validateTimeoutProperty(props, "mail.smtp.writetimeout");
    }

    /**
     * Validate timeout property values
     * @param props Properties object
     * @param timeoutKey Timeout property key
     */
    private void validateTimeoutProperty(Properties props, String timeoutKey) {
        String timeout = props.getProperty(timeoutKey);
        if (StringUtils.hasText(timeout)) {
            try {
                int timeoutValue = Integer.parseInt(timeout);
                if (timeoutValue < 0) {
                    logger.warn("Invalid timeout value for {}: {} (should be >= 0)", timeoutKey, timeout);
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid timeout format for {}: {}", timeoutKey, timeout);
            }
        }
    }

    /**
     * Public method to get email configuration for other services
     * @return Map of email configuration properties (for backward compatibility)
     */
    public Map<String, String> getEmailConfiguration() {
        Properties props = getPolicyEmailConfiguration();
        Map<String, String> config = new HashMap<>();
        
        if (props != null) {
            for (String key : props.stringPropertyNames()) {
                config.put(key, props.getProperty(key));
            }
        }
        
        return config;
    }

    /**
     * Public method to get email configuration as Properties
     * @return Properties object or empty Properties if not available
     */
    public Properties getEmailProperties() {
        Properties props = getPolicyEmailConfiguration();
        return props != null ? props : new Properties();
    }

    /**
     * Public method to get specific email configuration value
     * @param key Configuration key
     * @param defaultValue Default value if not found  
     * @return Configuration value
     */
    public String getEmailConfigValue(String key, String defaultValue) {
        Properties props = getPolicyEmailConfiguration();
        return props != null ? props.getProperty(key, defaultValue) : defaultValue;
    }

    /**
     * Get sender address from configuration
     * @return Sender email address
     */
    public String getSenderAddress() {
        return getEmailConfigValue(FROM_ADDRESS, DEFAULT_FROM_ADDRESS);
    }

    /**
     * Get sender name from configuration  
     * @return Sender name
     */
    public String getSenderName() {
        return getEmailConfigValue(FROM_NAME, DEFAULT_FROM_NAME);
    }

    /**
     * Get support address from configuration
     * @return Support email address
     */
    public String getSupportAddress() {
        return getEmailConfigValue(SUPPORT_ADDRESS, DEFAULT_SUPPORT_ADDRESS);  
    }

    /**
     * Get system name from configuration
     * @return System name
     */
    public String getSystemName() {
        return getEmailConfigValue(SYSTEM_NAME, DEFAULT_SYSTEM_NAME);
    }

    /**
     * Get system website from configuration
     * @return System website URL
     */
    public String getSystemWebsite() {
        return getEmailConfigValue(SYSTEM_WEBSITE, DEFAULT_SYSTEM_WEBSITE);
    }

    /**
     * Get system support phone from configuration
     * @return Support phone number
     */
    public String getSystemSupportPhone() {
        return getEmailConfigValue(SYSTEM_SUPPORT_PHONE, DEFAULT_SUPPORT_PHONE);
    }

    // Default values constants
    private static final String DEFAULT_FROM_ADDRESS = "artanddecor.system@gmail.com";
    private static final String DEFAULT_FROM_NAME = "Art and Decor System";
    private static final String DEFAULT_SUPPORT_ADDRESS = "support@artanddecor.com";
    private static final String DEFAULT_SYSTEM_NAME = "Art and Decor E-commerce Platform";
    private static final String DEFAULT_SYSTEM_WEBSITE = "https://artanddecor.com";
    private static final String DEFAULT_SUPPORT_PHONE = "+84-123-456-789";
}