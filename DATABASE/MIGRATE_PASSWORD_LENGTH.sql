-- =============================================
-- Migration: Increase PASSWORD column length
-- Description: Fix truncated bcrypt password hashes
-- Date: February 1, 2026
-- =============================================

USE `ART_AND_DECOR`;

-- Modify PASSWORD column to support full bcrypt hash (72 characters)
ALTER TABLE `USER` 
MODIFY COLUMN `PASSWORD` VARCHAR(255);

-- Verify the change
DESCRIBE `USER`;
