-- =============================================
-- Database Migration Script
-- Purpose: Add IMAGE_EMBEDDING_STATUS column to IMAGE_EMBEDDING table
-- Author: System Update
-- Date: May 05, 2026
-- Version: 1.0
-- Description: Adds status tracking for image embedding processing
-- =============================================

USE ART_AND_DECOR;

-- Add IMAGE_EMBEDDING_STATUS column to IMAGE_EMBEDDING table
ALTER TABLE IMAGE_EMBEDDING
ADD COLUMN IMAGE_EMBEDDING_STATUS VARCHAR(50) NOT NULL DEFAULT 'PENDING'
AFTER EMBEDDING;

-- Update existing records to COMPLETED status (assuming they are already processed)
UPDATE IMAGE_EMBEDDING
SET IMAGE_EMBEDDING_STATUS = 'COMPLETED'
WHERE IMAGE_EMBEDDING_STATUS = 'PENDING';

-- Verify the changes
SELECT 
    COUNT(*) as total_records,
    SUM(CASE WHEN IMAGE_EMBEDDING_STATUS = 'PENDING' THEN 1 ELSE 0 END) as pending_count,
    SUM(CASE WHEN IMAGE_EMBEDDING_STATUS = 'COMPLETED' THEN 1 ELSE 0 END) as completed_count,
    SUM(CASE WHEN IMAGE_EMBEDDING_STATUS = 'FAILED' THEN 1 ELSE 0 END) as failed_count
FROM IMAGE_EMBEDDING;

-- Show table structure to confirm changes
DESCRIBE IMAGE_EMBEDDING;