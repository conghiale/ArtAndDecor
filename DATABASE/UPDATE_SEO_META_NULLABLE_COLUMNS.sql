-- =============================================
-- MIGRATION: Make all SEO_META business columns nullable
-- Purpose : Allow partial SEO data; null/empty values saved as-is
-- Affected: SEO_META_TITLE, SEO_META_DESCRIPTION, SEO_META_INDEX,
--           SEO_META_FOLLOW, SEO_META_ENABLED
-- Date    : 2026-05-26
-- =============================================

ALTER TABLE `SEO_META`
    MODIFY COLUMN `SEO_META_TITLE`       VARCHAR(150)   NULL,
    MODIFY COLUMN `SEO_META_DESCRIPTION` VARCHAR(500)   NULL,
    MODIFY COLUMN `SEO_META_INDEX`       BOOLEAN        NULL DEFAULT TRUE,
    MODIFY COLUMN `SEO_META_FOLLOW`      BOOLEAN        NULL DEFAULT TRUE,
    MODIFY COLUMN `SEO_META_ENABLED`     BOOLEAN        NULL DEFAULT TRUE;
