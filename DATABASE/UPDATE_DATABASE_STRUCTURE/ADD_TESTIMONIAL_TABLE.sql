-- =============================================
-- Migration: Add TESTIMONIAL table
-- Database: ART_AND_DECOR
-- Date: 2026-05-29
-- Description: Creates TESTIMONIAL table for storing customer testimonials
--              displayed on the website (name, quote, image, enabled status)
-- =============================================

USE `ART_AND_DECOR`;

CREATE TABLE IF NOT EXISTS `TESTIMONIAL` (
    `TESTIMONIAL_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `TESTIMONIAL_NAME` VARCHAR(150) NOT NULL,
    `TESTIMONIAL_QUOTE` TEXT NOT NULL,
    `TESTIMONIAL_ENABLED` BOOLEAN NOT NULL DEFAULT TRUE,
    `TESTIMONIAL_DISPLAY_ORDER` INT DEFAULT NULL,
    `IMAGE_ID` BIGINT NULL,
    `CREATED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `MODIFIED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`IMAGE_ID`) REFERENCES `IMAGE`(`IMAGE_ID`) ON DELETE SET NULL,
    INDEX `idx_testimonial_name` (`TESTIMONIAL_NAME`),
    INDEX `idx_testimonial_enabled` (`TESTIMONIAL_ENABLED`),
    INDEX `idx_testimonial_display_order` (`TESTIMONIAL_DISPLAY_ORDER`),
    INDEX `idx_testimonial_image` (`IMAGE_ID`)
);
