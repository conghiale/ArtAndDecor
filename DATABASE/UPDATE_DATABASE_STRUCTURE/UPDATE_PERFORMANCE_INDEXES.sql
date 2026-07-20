-- =============================================
-- Performance Index Migration
-- Description: Add missing indexes to improve query performance for
--              product listing/filtering and cart item lookup.
-- Run once on existing production databases.
-- Date: 2026
-- =============================================

USE `ART_AND_DECOR`;

-- PRODUCT table: indexes for boolean/enum filter columns used in catalog queries
ALTER TABLE `PRODUCT`
    ADD INDEX `idx_product_enabled`       (`PRODUCT_ENABLED`),
    ADD INDEX `idx_product_featured`      (`PRODUCT_FEATURED`),
    ADD INDEX `idx_product_highlighted`   (`PRODUCT_HIGHLIGHTED`),
    ADD INDEX `idx_product_sold_quantity` (`SOLD_QUANTITY`);

-- CART_ITEM table: composite index for the common pattern
-- "get active cart items by cart" (CART_ID + CART_ITEM_STATE_ID)
ALTER TABLE `CART_ITEM`
    ADD INDEX `idx_cart_item_cart_state` (`CART_ID`, `CART_ITEM_STATE_ID`);
