-- =============================================
-- Database Migration Script
-- Purpose: Update PRODUCT table to allow NULL for PRODUCT_DESCRIPTION
-- Author: System Update
-- Date: May 04, 2026
-- Version: 1.0
-- =============================================

USE ART_AND_DECOR;

-- Check for duplicate data before adding unique entries.
SELECT IMAGE_ID, COUNT(*)
FROM IMAGE_EMBEDDING
GROUP BY IMAGE_ID
HAVING COUNT(*) > 1;

-- If there is no duplicate data, run the following command.
ALTER TABLE IMAGE_EMBEDDING
    ADD CONSTRAINT UK_IMAGE_EMBEDDING_IMAGE_ID UNIQUE (IMAGE_ID);