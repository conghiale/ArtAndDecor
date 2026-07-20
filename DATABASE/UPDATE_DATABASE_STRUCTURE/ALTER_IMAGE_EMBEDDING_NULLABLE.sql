-- =============================================
-- Migration: ALTER IMAGE_EMBEDDING.EMBEDDING to explicitly NULL
-- Description: Modify the EMBEDDING column in IMAGE_EMBEDDING table
--              to explicitly allow NULL values
-- Date: May 26, 2026
-- =============================================

USE `ART_AND_DECOR`;

ALTER TABLE `IMAGE_EMBEDDING`
    MODIFY COLUMN `EMBEDDING` VARBINARY(8000) NULL;

-- Verify change
SELECT 
    COLUMN_NAME,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'ART_AND_DECOR'
  AND TABLE_NAME   = 'IMAGE_EMBEDDING'
  AND COLUMN_NAME  = 'EMBEDDING';
