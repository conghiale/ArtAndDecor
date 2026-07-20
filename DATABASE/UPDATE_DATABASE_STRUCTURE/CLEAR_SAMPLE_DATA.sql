-- =============================================
-- File: CLEAR_SAMPLE_DATA.sql
-- Description: Delete sample data from 17 specific tables in Art & Decor database
-- Author: Generated for ArtAndDecor Project
-- Date: January 18, 2026
-- Note: This script removes sample data while preserving table structure and reference data
-- =============================================

USE `ART_AND_DECOR`;

-- =============================================
-- DISABLE FOREIGN KEY CHECKS FOR DELETION
-- =============================================
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- DELETE DATA FROM TABLES (ORDER MATTERS DUE TO DEPENDENCIES)
-- =============================================

-- Delete order related data first
DELETE FROM `ORDER_STATE_HISTORY`;
DELETE FROM `PAYMENT`;
DELETE FROM `SHIPMENT`;

-- Delete order data
DELETE FROM `ORDERS`;

-- Delete cart related data
DELETE FROM `CART_ITEM`;
DELETE FROM `CART`;

-- Delete review related data
DELETE FROM `PRODUCT_REVIEW_LIKE`;
DELETE FROM `REVIEW`;

-- Delete product related data
DELETE FROM `PRODUCT_ATTRIBUTE`;
DELETE FROM `PRODUCT_IMAGE`;
DELETE FROM `PRODUCT`;

-- Delete image data
DELETE FROM `IMAGE`;

-- Delete user data
DELETE FROM `USER`;

-- Delete blog data
DELETE FROM `BLOG`;

-- Delete discount data
DELETE FROM `DISCOUNT`;

-- Delete contact data
DELETE FROM `CONTACT`;

-- Delete policy data
DELETE FROM `POLICY`;

-- =============================================
-- RESET AUTO INCREMENT VALUES
-- =============================================

-- Reset AUTO_INCREMENT for all affected tables
ALTER TABLE `ORDER_STATE_HISTORY` AUTO_INCREMENT = 1;
ALTER TABLE `PAYMENT` AUTO_INCREMENT = 1;
ALTER TABLE `SHIPMENT` AUTO_INCREMENT = 1;
ALTER TABLE `ORDERS` AUTO_INCREMENT = 1;
ALTER TABLE `CART_ITEM` AUTO_INCREMENT = 1;
ALTER TABLE `CART` AUTO_INCREMENT = 1;
ALTER TABLE `PRODUCT_REVIEW_LIKE` AUTO_INCREMENT = 1;
ALTER TABLE `REVIEW` AUTO_INCREMENT = 1;
ALTER TABLE `PRODUCT_ATTRIBUTE` AUTO_INCREMENT = 1;
ALTER TABLE `PRODUCT_IMAGE` AUTO_INCREMENT = 1;
ALTER TABLE `PRODUCT` AUTO_INCREMENT = 1;
ALTER TABLE `IMAGE` AUTO_INCREMENT = 1;
ALTER TABLE `USER` AUTO_INCREMENT = 1;
ALTER TABLE `BLOG` AUTO_INCREMENT = 1;
ALTER TABLE `DISCOUNT` AUTO_INCREMENT = 1;
ALTER TABLE `CONTACT` AUTO_INCREMENT = 1;
ALTER TABLE `POLICY` AUTO_INCREMENT = 1;

-- =============================================
-- RE-ENABLE FOREIGN KEY CHECKS
-- =============================================
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- VERIFICATION
-- =============================================

-- Check if tables are empty
SELECT 
    'USER' AS table_name, 
    COUNT(*) AS record_count 
FROM `USER`
UNION ALL
SELECT 
    'IMAGE' AS table_name, 
    COUNT(*) AS record_count 
FROM `IMAGE`
UNION ALL
SELECT 
    'PRODUCT' AS table_name, 
    COUNT(*) AS record_count 
FROM `PRODUCT`
UNION ALL
SELECT 
    'REVIEW' AS table_name, 
    COUNT(*) AS record_count 
FROM `REVIEW`
UNION ALL
SELECT 
    'PRODUCT_REVIEW_LIKE' AS table_name, 
    COUNT(*) AS record_count 
FROM `PRODUCT_REVIEW_LIKE`
UNION ALL
SELECT 
    'PRODUCT_IMAGE' AS table_name, 
    COUNT(*) AS record_count 
FROM `PRODUCT_IMAGE`
UNION ALL
SELECT 
    'PRODUCT_ATTRIBUTE' AS table_name, 
    COUNT(*) AS record_count 
FROM `PRODUCT_ATTRIBUTE`
UNION ALL
SELECT 
    'CART' AS table_name, 
    COUNT(*) AS record_count 
FROM `CART`
UNION ALL
SELECT 
    'CART_ITEM' AS table_name, 
    COUNT(*) AS record_count 
FROM `CART_ITEM`
UNION ALL
SELECT 
    'ORDERS' AS table_name, 
    COUNT(*) AS record_count 
FROM `ORDERS`
UNION ALL
SELECT 
    'ORDER_STATE_HISTORY' AS table_name, 
    COUNT(*) AS record_count 
FROM `ORDER_STATE_HISTORY`
UNION ALL
SELECT 
    'PAYMENT' AS table_name, 
    COUNT(*) AS record_count 
FROM `PAYMENT`
UNION ALL
SELECT 
    'SHIPMENT' AS table_name, 
    COUNT(*) AS record_count 
FROM `SHIPMENT`
UNION ALL
SELECT 
    'CONTACT' AS table_name, 
    COUNT(*) AS record_count 
FROM `CONTACT`
UNION ALL
SELECT 
    'BLOG' AS table_name, 
    COUNT(*) AS record_count 
FROM `BLOG`
UNION ALL
SELECT 
    'DISCOUNT' AS table_name, 
    COUNT(*) AS record_count 
FROM `DISCOUNT`
UNION ALL
SELECT 
    'POLICY' AS table_name, 
    COUNT(*) AS record_count 
FROM `POLICY`;

-- =============================================
-- SUMMARY
-- =============================================
SELECT 'Sample data cleared successfully! 17 tables are now empty but structure is preserved.' AS Status;