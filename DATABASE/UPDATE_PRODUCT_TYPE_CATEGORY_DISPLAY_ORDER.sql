-- Thêm cột DISPLAY_ORDER cho PRODUCT_TYPE nếu chưa tồn tại (tương thích nhiều phiên bản MySQL)
SET @col_type_exists = (
	SELECT COUNT(1)
	FROM INFORMATION_SCHEMA.COLUMNS
	WHERE TABLE_SCHEMA = DATABASE()
	  AND TABLE_NAME = 'PRODUCT_TYPE'
	  AND COLUMN_NAME = 'PRODUCT_TYPE_DISPLAY_ORDER'
);
SET @sql_type_col = IF(
	@col_type_exists = 0,
	'ALTER TABLE PRODUCT_TYPE ADD COLUMN PRODUCT_TYPE_DISPLAY_ORDER INT NULL DEFAULT NULL AFTER PRODUCT_TYPE_ENABLED',
	'SELECT "Column PRODUCT_TYPE_DISPLAY_ORDER already exists"'
);
PREPARE stmt_type_col FROM @sql_type_col;
EXECUTE stmt_type_col;
DEALLOCATE PREPARE stmt_type_col;

-- Thêm cột DISPLAY_ORDER cho PRODUCT_CATEGORY nếu chưa tồn tại (tương thích nhiều phiên bản MySQL)
SET @col_category_exists = (
	SELECT COUNT(1)
	FROM INFORMATION_SCHEMA.COLUMNS
	WHERE TABLE_SCHEMA = DATABASE()
	  AND TABLE_NAME = 'PRODUCT_CATEGORY'
	  AND COLUMN_NAME = 'PRODUCT_CATEGORY_DISPLAY_ORDER'
);
SET @sql_category_col = IF(
	@col_category_exists = 0,
	'ALTER TABLE PRODUCT_CATEGORY ADD COLUMN PRODUCT_CATEGORY_DISPLAY_ORDER INT NULL DEFAULT NULL AFTER PRODUCT_CATEGORY_VISIBLE',
	'SELECT "Column PRODUCT_CATEGORY_DISPLAY_ORDER already exists"'
);
PREPARE stmt_category_col FROM @sql_category_col;
EXECUTE stmt_category_col;
DEALLOCATE PREPARE stmt_category_col;

-- Thêm index hỗ trợ sắp xếp/lọc theo display order (nếu chưa có)
SET @idx_type_exists = (
	SELECT COUNT(1)
	FROM INFORMATION_SCHEMA.STATISTICS
	WHERE TABLE_SCHEMA = DATABASE()
	  AND TABLE_NAME = 'PRODUCT_TYPE'
	  AND INDEX_NAME = 'idx_product_type_display_order'
);
SET @sql_type_idx = IF(
	@idx_type_exists = 0,
	'CREATE INDEX idx_product_type_display_order ON PRODUCT_TYPE (PRODUCT_TYPE_DISPLAY_ORDER)',
	'SELECT "Index idx_product_type_display_order already exists"'
);
PREPARE stmt_type_idx FROM @sql_type_idx;
EXECUTE stmt_type_idx;
DEALLOCATE PREPARE stmt_type_idx;

SET @idx_category_exists = (
	SELECT COUNT(1)
	FROM INFORMATION_SCHEMA.STATISTICS
	WHERE TABLE_SCHEMA = DATABASE()
	  AND TABLE_NAME = 'PRODUCT_CATEGORY'
	  AND INDEX_NAME = 'idx_product_category_display_order'
);
SET @sql_category_idx = IF(
	@idx_category_exists = 0,
	'CREATE INDEX idx_product_category_display_order ON PRODUCT_CATEGORY (PRODUCT_CATEGORY_DISPLAY_ORDER)',
	'SELECT "Index idx_product_category_display_order already exists"'
);
PREPARE stmt_category_idx FROM @sql_category_idx;
EXECUTE stmt_category_idx;
DEALLOCATE PREPARE stmt_category_idx;
