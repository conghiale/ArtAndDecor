package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.config.MailConfiguration;
import org.artanddecor.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Email Service Implementation
 * Handles email sending operations using Spring Mail
 * Uses MailConfiguration for dynamic email settings from POLICY table
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender javaMailSender;
    private final MailConfiguration mailConfiguration;

    @Override
    public void sendPasswordResetEmail(String toEmail, String userName, String newPassword) {
        if (!StringUtils.hasText(toEmail)) {
            logger.warn("Cannot send password reset email: recipient email is empty");
            return;
        }

        try {
            String subject = "Mật khẩu mới cho tài khoản Art and Decor của bạn";
            String content = buildPasswordResetEmailContent(userName, newPassword);
            String fromAddress = mailConfiguration.getSenderAddress();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);

            javaMailSender.send(message);
            
            logger.info("Password reset email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage(), e);
            
            // Auto-debug mail configuration when authentication fails
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("authentication")) {
                logger.warn("⚠️ Authentication failed detected - running mail configuration debug...");
                mailConfiguration.debugMailConfiguration();
            }
            
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Override
    public void sendNotificationEmail(String toEmail, String subject, String content) {
        if (!StringUtils.hasText(toEmail)) {
            logger.warn("Cannot send notification email: recipient email is empty");
            return;
        }

        try {
            String fromAddress = mailConfiguration.getSenderAddress();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);

            javaMailSender.send(message);
            
            logger.info("Notification email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send notification email to {}: {}", toEmail, e.getMessage(), e);
            
            // Auto-debug mail configuration when authentication fails
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("authentication")) {
                logger.warn("⚠️ Authentication failed detected - running mail configuration debug...");
                mailConfiguration.debugMailConfiguration();
            }
            
            throw new RuntimeException("Failed to send notification email", e);
        }
    }

    /**
     * Build password reset email content using MailConfiguration
     */
    private String buildPasswordResetEmailContent(String userName, String newPassword) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String currentTime = LocalDateTime.now().format(formatter);

        // Get configuration from MailConfiguration
        String systemName = mailConfiguration.getSystemName();
        String systemWebsite = mailConfiguration.getSystemWebsite();
        String supportAddress = mailConfiguration.getSupportAddress();
        String supportPhone = mailConfiguration.getSystemSupportPhone();

        return String.format("""
            Kính chào %s,

            Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn trên hệ thống %s.

            Thông tin mật khẩu mới:
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            Tên đăng nhập: %s
            Mật khẩu mới: %s
            Thời gian đặt lại: %s
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

            QUAN TRỌNG:
            • Vui lòng đăng nhập và thay đổi mật khẩu ngay sau khi nhận được email này
            • Mật khẩu này chỉ có hiệu lực trong thời gian giới hạn
            • Không chia sẻ thông tin này với bất kỳ ai
            • Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng liên hệ ngay với bộ phận hỗ trợ

            Cách đăng nhập:
            1. Truy cập: %s
            2. Sử dụng tên đăng nhập và mật khẩu mới ở trên
            3. Thay đổi mật khẩu ngay sau khi đăng nhập thành công

            Để đảm bảo bảo mật tài khoản:
            • Sử dụng mật khẩu mạnh (ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt)
            • Không sử dụng mật khẩu đã từng sử dụng trước đây
            • Không lưu mật khẩu trên trình duyệt công cộng

            Nếu có bất kỳ thắc mắc nào, vui lòng liên hệ:
            • Email: %s
            • Hotline: %s
            • Website: %s

            Cảm ơn bạn đã tin tương và sử dụng dịch vụ của chúng tôi.

            Trân trọng,
            Đội ngũ %s

            ────────────────────────────────────────
            Email này được gửi tự động từ hệ thống. Vui lòng không trả lời email này.
            Nếu bạn cần hỗ trợ, hãy liên hệ qua email: %s
            """,
            StringUtils.hasText(userName) ? userName : "Quý khách",
            systemName,
            userName,
            newPassword,
            currentTime,
            systemWebsite,
            supportAddress,
            supportPhone,
            systemWebsite,
            systemName,
            supportAddress
        );
    }
}