-- =============================================
-- File: UPDATE_ORDER_SESSION_SUPPORT.sql
-- Description: Update ORDERS table to support guest user sessions
-- Author: Generated for ArtAndDecor Project
-- Date: April 15, 2026
-- Version: 1.0 - Add SESSION_ID support for guest orders
-- =============================================

-- =============================================
-- UPDATE ORDERS TABLE FOR SESSION SUPPORT
-- =============================================

-- Thêm cột SESSION_ID nếu chưa tồn tại (MySQL 8+)
-- Cột này lưu sessionId từ cart khi guest users đặt hàng
ALTER TABLE `ORDERS`
ADD COLUMN IF NOT EXISTS `SESSION_ID` VARCHAR(100) NULL
COMMENT 'Session ID for guest orders - extracted from cart.sessionId'
AFTER `USER_ID`;

-- Thêm index cho SESSION_ID nếu chưa tồn tại  
-- Index này giúp tối ưu các query tìm kiếm orders theo sessionId
DROP INDEX IF EXISTS `idx_order_session` ON `ORDERS`;
CREATE INDEX `idx_order_session`
ON `ORDERS` (`SESSION_ID`);

-- =============================================
-- VERIFICATION QUERIES
-- =============================================

-- Kiểm tra cấu trúc bảng sau khi update
SHOW CREATE TABLE `ORDERS`;

-- Kiểm tra các indexes trên bảng ORDERS
SHOW INDEX FROM `ORDERS`;

-- Đếm số orders hiện tại theo từng loại
SELECT 
    CASE 
        WHEN USER_ID IS NOT NULL THEN 'USER_ORDER'
        WHEN SESSION_ID IS NOT NULL THEN 'GUEST_ORDER' 
        ELSE 'UNKNOWN'
    END AS ORDER_TYPE,
    COUNT(*) as TOTAL_ORDERS
FROM `ORDERS`
GROUP BY 
    CASE 
        WHEN USER_ID IS NOT NULL THEN 'USER_ORDER'
        WHEN SESSION_ID IS NOT NULL THEN 'GUEST_ORDER'
        ELSE 'UNKNOWN'
    END;

-- =============================================
-- MIGRATION COMPLETE
-- =============================================
-- Database migration completed successfully:
-- 1. Added SESSION_ID column to ORDERS table
-- 2. Added idx_order_session index for performance optimization  
-- 3. Support for both authenticated users (USER_ID) and guest users (SESSION_ID)
-- 4. Backward compatibility maintained - existing orders unchanged
-- =============================================