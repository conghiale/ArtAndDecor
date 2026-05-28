-- =============================================
-- Migration: Add OTP columns to USER table
-- Purpose : Support the forgot-password OTP workflow
--           (sendForgotPasswordOtp / verifyOtp / resetPasswordWithOtp)
-- Run on  : Existing databases (will fail gracefully if columns already exist)
-- Date    : 2025
-- =============================================

USE `art_and_decor`;

ALTER TABLE `USER`
    ADD COLUMN `OTP_CODE`       VARCHAR(6)  NULL AFTER `LAST_LOGIN_DT`,
    ADD COLUMN `OTP_EXPIRED_DT` DATETIME    NULL AFTER `OTP_CODE`;
