-- =============================================
-- Script: CREATE_IMAGE_EMBEDDING_TABLE.sql
-- Description: Tạo bảng IMAGE_EMBEDDING để lưu trữ embedding vector của hình ảnh
-- Author: Generated for ArtAndDecor Project
-- Date: April 13, 2026
-- Usage: Chạy script này trên database ART_AND_DECOR đang hoạt động
-- =============================================

-- Kiểm tra nếu bảng đã tồn tại thì xóa (tùy chọn - uncomment nếu cần)
-- DROP TABLE IF EXISTS `IMAGE_EMBEDDING`;

-- Tạo bảng IMAGE_EMBEDDING
CREATE TABLE `IMAGE_EMBEDDING` (
    `IMAGE_EMBEDDING_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `IMAGE_ID` BIGINT NOT NULL,
    `EMBEDDING` VARBINARY(8000) NOT NULL,
    `CREATED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `MODIFIED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`IMAGE_ID`) REFERENCES `IMAGE`(`IMAGE_ID`) ON DELETE CASCADE,
    INDEX `idx_image_embedding_image_id` (`IMAGE_ID`)
);

-- Xác nhận bảng đã được tạo thành công
SELECT 'Bảng IMAGE_EMBEDDING đã được tạo thành công!' as Status;

-- Kiểm tra cấu trúc bảng vừa tạo
DESCRIBE `IMAGE_EMBEDDING`;