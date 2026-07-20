-- =============================================
-- File: RUN_ALL_DATABASE_SCRIPTS.sql
-- Description: Master script to execute all database scripts in correct order
-- Author: Generated for ArtAndDecor Project
-- Date: January 19, 2026
-- =============================================

-- =============================================
-- INSTRUCTIONS FOR EXECUTION
-- =============================================
-- 1. Make sure MySQL server is running
-- 2. Connect to MySQL with sufficient privileges (root user recommended)
-- 3. Navigate to the DATABASE folder in your MySQL client
-- 4. Execute this script: SOURCE RUN_ALL_DATABASE_SCRIPTS.sql
-- 
-- Or execute via command line:
-- mysql -u root -p < RUN_ALL_DATABASE_SCRIPTS.sql
-- =============================================

-- Display start message
SELECT '=====================================================' AS '';
SELECT 'STARTING ART AND DECOR DATABASE SETUP' AS '';
SELECT '=====================================================' AS '';
SELECT CONCAT('Start Time: ', NOW()) AS '';
SELECT '' AS '';

-- =============================================
-- STEP 1: CREATE DATABASE AND SCHEMA
-- =============================================
SELECT '=====================================================' AS '';
SELECT 'STEP 1: Creating database schema and initial data...' AS '';
SELECT '=====================================================' AS '';

-- Execute the main database creation script
SOURCE CREATE_DB_ART_AND_DECOR.sql;

SELECT 'Database schema created successfully!' AS '';
SELECT '' AS '';

-- =============================================
-- STEP 2: INSERT SAMPLE DATA (OPTIONAL)
-- =============================================
SELECT '=====================================================' AS '';
SELECT 'STEP 2: Inserting sample data...' AS '';
SELECT '=====================================================' AS '';

-- Execute the sample data insertion script
SOURCE INSERT_SAMPLE_DATA.sql;

SELECT 'Sample data inserted successfully!' AS '';
SELECT '' AS '';

-- =============================================
-- OPTIONAL: CLEAR SAMPLE DATA (UNCOMMENT IF NEEDED)
-- =============================================
-- SELECT '=====================================================' AS '';
-- SELECT 'STEP 3: Clearing sample data...' AS '';
-- SELECT '=====================================================' AS '';
-- 
-- -- Execute the clear sample data script
-- SOURCE CLEAR_SAMPLE_DATA.sql;
-- 
-- SELECT 'Sample data cleared successfully!' AS '';
-- SELECT '' AS '';

-- =============================================
-- FINAL VERIFICATION
-- =============================================
SELECT '=====================================================' AS '';
SELECT 'FINAL VERIFICATION' AS '';
SELECT '=====================================================' AS '';

-- Show all tables in the database
USE `ART_AND_DECOR`;
SHOW TABLES;

-- Count total tables
SELECT CONCAT('total_tables: ', COUNT(*)) AS ''
FROM information_schema.tables
WHERE table_schema = 'ART_AND_DECOR';

-- Count records in key tables
SELECT 'Table record counts:' AS '';

SELECT 
    'USER' AS table_name,
    COUNT(*) AS record_count
FROM `USER`
UNION ALL
SELECT 
    'PRODUCT',
    COUNT(*)
FROM `PRODUCT`
UNION ALL
SELECT 
    'CART',
    COUNT(*)
FROM `CART`
UNION ALL
SELECT 
    'ORDERS',
    COUNT(*)
FROM `ORDERS`;

-- =============================================
-- COMPLETION MESSAGE
-- =============================================
SELECT '' AS '';
SELECT '=====================================================' AS '';
SELECT 'DATABASE SETUP COMPLETED SUCCESSFULLY!' AS '';
SELECT '=====================================================' AS '';
SELECT CONCAT('Completion Time: ', NOW()) AS '';
SELECT '' AS '';
SELECT 'Database: ART_AND_DECOR is ready for use.' AS '';
SELECT 'Spring Boot application can now connect to the database.' AS '';
SELECT '' AS '';
SELECT 'Next steps:' AS '';
SELECT '1. Update application.properties with correct database credentials' AS '';
SELECT '2. Start the Spring Boot application' AS '';
SELECT '3. Test the API endpoints' AS '';
SELECT '' AS '';
SELECT 'To clear sample data later, run: SOURCE CLEAR_SAMPLE_DATA.sql' AS '';
SELECT '=====================================================' AS '';