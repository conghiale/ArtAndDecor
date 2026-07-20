-- =============================================
-- MIGRATION: Add index for PRODUCT_CATEGORY_PARENT_ID
-- Purpose : Optimize category hierarchy lookup
--           and product filtering by parent category
-- Date    : 2026-06-14
-- =============================================

ALTER TABLE `PRODUCT_CATEGORY`
    ADD INDEX `idx_product_category_parent`
    (`PRODUCT_CATEGORY_PARENT_ID`);