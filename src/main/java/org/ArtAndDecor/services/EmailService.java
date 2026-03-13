package org.ArtAndDecor.services;

/**
 * Email Service Interface
 * Handles email sending operations
 */
public interface EmailService {

    /**
     * Send password reset email to user
     * 
     * @param toEmail Recipient email address
     * @param userName User name
     * @param newPassword New generated password
     */
    void sendPasswordResetEmail(String toEmail, String userName, String newPassword);

    /**
     * Send general notification email
     * 
     * @param toEmail Recipient email address
     * @param subject Email subject
     * @param content Email content
     */
    void sendNotificationEmail(String toEmail, String subject, String content);
}