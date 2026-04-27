-- =====================================================
-- ADD CART_ITEM_UNIT_PRICE COLUMN TO CART_ITEM TABLE
-- =====================================================
-- Purpose: Add unit price storage capability to cart items
-- This allows storing frontend-calculated prices instead of 
-- relying solely on product/attribute maximum prices
-- 
-- Date: 2026-04-18
-- Author: Cart Enhancement Implementation
-- =====================================================

-- Check if column already exists before adding
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'CART_ITEM'
    AND COLUMN_NAME = 'CART_ITEM_UNIT_PRICE'
);

-- Add column only if it doesn't exist
SET @sql = IF(@column_exists = 0,
    'ALTER TABLE CART_ITEM ADD COLUMN CART_ITEM_UNIT_PRICE DECIMAL(15,2) NULL COMMENT "Unit price for cart item - can be set from frontend or calculated via policy"',
    'SELECT "Column CART_ITEM_UNIT_PRICE already exists" as message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verify the column was added successfully
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
AND TABLE_NAME = 'CART_ITEM'
AND COLUMN_NAME = 'CART_ITEM_UNIT_PRICE';

-- Optional: Update existing cart items with calculated unit prices
-- (This can be run separately if needed)
/*
UPDATE CART_ITEM ci
LEFT JOIN PRODUCT p ON ci.PRODUCT_ID = p.PRODUCT_ID
SET ci.CART_ITEM_UNIT_PRICE = COALESCE(
    -- Use product price as fallback for existing items
    p.PRODUCT_PRICE,
    0.00
)
WHERE ci.CART_ITEM_UNIT_PRICE IS NULL;
*/

-- Show completion message
SELECT 
    'CART_ITEM_UNIT_PRICE column migration completed successfully!' as status,
    NOW() as completed_at;