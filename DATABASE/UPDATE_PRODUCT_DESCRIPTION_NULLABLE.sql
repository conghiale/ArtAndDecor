-- =============================================
-- Database Migration Script
-- Purpose: Update PRODUCT table to allow NULL for PRODUCT_DESCRIPTION
-- Author: System Update
-- Date: April 27, 2026
-- Version: 1.0
-- =============================================

USE `ART_AND_DECOR`;

-- Update PRODUCT table structure
ALTER TABLE `PRODUCT` 
MODIFY COLUMN `PRODUCT_DESCRIPTION` TEXT NULL;

-- Verify the change
SELECT 
    COLUMN_NAME,
    IS_NULLABLE,
    DATA_TYPE,
    COLUMN_TYPE
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'ART_AND_DECOR' 
AND TABLE_NAME = 'PRODUCT' 
AND COLUMN_NAME = 'PRODUCT_DESCRIPTION';

-- =============================================
-- MIGRATION NOTES:
-- 1. This change allows PRODUCT_DESCRIPTION to be NULL
-- 2. Existing data will remain unchanged
-- 3. New products can be created without description
-- 4. Application logic has been updated to handle nullable description
-- =============================================