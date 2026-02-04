-- =============================================
-- File: INSERT_SAMPLE_DATA.sql
-- Description: Insert sample data for 17 specific tables in Art & Decor e-commerce database
-- Author: Generated for ArtAndDecor Project  
-- Date: January 18, 2026
-- =============================================

USE `ART_AND_DECOR`;

-- =============================================
-- INSERT SAMPLE USERS
-- =============================================

INSERT INTO `USER` (`USER_PROVIDER_ID`, `USER_ROLE_ID`, `USER_ENABLED`, `USER_NAME`, `PASSWORD`, `FIRST_NAME`, `LAST_NAME`, `PHONE_NUMBER`, `EMAIL`, `IMAGE_AVATAR_NAME`, `SOCIAL_MEDIA`, `LAST_LOGIN_DT`) VALUES
(1, 2, TRUE, 'admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyPw5TCGb2/nX8stqZqIvK', 'Admin', 'System', '0901234567', 'admin@artdecor.com', 'A1B2C3D4E5F6789012345678901234567890ABCD', '{"facebook": "admin.artdecor", "instagram": "artdecor_admin"}', '2026-01-21 09:15:00'),
(1, 1, TRUE, 'customer01', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyPw5TCGb2/nX8stqZqIvK', 'Nguyen', 'Van A', '0904567890', 'customer1@gmail.com', 'D4E5F6789012345678901234567890ABCDEF12', '{"instagram": "nguyenvana_art"}', '2026-01-19 16:20:00'),
(1, 1, TRUE, 'customer02', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyPw5TCGb2/nX8stqZqIvK', 'Tran', 'Thi B', '0905678901', 'customer2@gmail.com', 'E5F6789012345678901234567890ABCDEF123', NULL, '2026-01-19 11:10:00'),
(2, 1, TRUE, 'google_user01', NULL, 'Alice', 'Johnson', NULL, 'alice.johnson@gmail.com', 'F6789012345678901234567890ABCDEF1234', '{"google": "alice.johnson.art"}', '2026-01-18 20:15:00'),
(3, 1, TRUE, 'facebook_user01', NULL, 'Bob', 'Smith', NULL, 'bob.smith@facebook.com', '789012345678901234567890ABCDEF12345', '{"facebook": "bob.smith.artist"}', '2026-01-18 08:30:00'),
(1, 1, TRUE, 'customer03', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyPw5TCGb2/nX8stqZqIvK', 'Le', 'Van C', '0906789012', 'customer3@yahoo.com', '89012345678901234567890ABCDEF123456', NULL, '2026-01-17 16:25:00');

-- =============================================
-- INSERT SAMPLE IMAGES
-- =============================================

INSERT INTO `IMAGE` (`IMAGE_NAME`, `IMAGE_DISPLAY_NAME`, `IMAGE_SLUG`, `IMAGE_SIZE`, `IMAGE_REMARK`, `IMAGE_REMARK_EN`) VALUES
('1A2B3C4D5E6F789012345678901234567890ABCD', 'Tranh Phông Cảnh Hoàng Hôn Biển', 'sunset-landscape-001', '1920x1080', 'Hình ảnh phong cảnh hoàng hôn trên biển rất đẹp', 'Beautiful sunset landscape image over the sea'),
('2B3C4D5E6F789012345678901234567890ABCDE1', 'Tranh Núi Non Hùng Vĩ', 'mountain-view-002', '1920x1200', 'Cảnh quan núi non hùng vĩ trong sương mù', 'Majestic mountain landscape shrouded in mist'),
('3C4D5E6F789012345678901234567890ABCDEF12', 'Nghệ Thuật Trừa Tượng Hiện Đại', 'abstract-art-001', '1500x1500', 'Tác phẩm nghệ thuật trừa tượng hiện đại', 'Modern abstract art masterpiece'),
('4D5E6F789012345678901234567890ABCDEF123', 'Tranh Nghệ Thuật Hiện Đại', 'modern-art-002', '1600x1200', 'Tranh nghệ thuật hiện đại phong cách độc đáo', 'Unique modern artistic painting style'),
('5E6F789012345678901234567890ABCDEF1234', 'Trang Trí Nội Thất Cao Cấp', 'home-decor-001', '1200x800', 'Sản phẩm trang trí nội thất cao cấp', 'Premium home interior decoration product'),
('6F789012345678901234567890ABCDEF12345', 'Tranh Trang Trí Treo Tường', 'wall-decoration-002', '1400x1000', 'Tranh trang trí để treo tường phòng khách', 'Wall decoration painting for living room'),
('789012345678901234567890ABCDEF123456', 'Chân Dung Người Phụ Nữ Cổ Điển', 'portrait-woman-001', '1000x1200', 'Tranh chân dung người phụ nữ phong cách cổ điển', 'Classical style portrait of a woman'),
('89012345678901234567890ABCDEF1234567', 'Chân Dung Người Đàn Ông Nghệ Thuật', 'portrait-man-002', '1000x1200', 'Tranh chân dung người đàn ông nghệ thuật', 'Artistic portrait of a man'),
('9012345678901234567890ABCDEF12345678', 'Tượng Phật Trang Nghiêm', 'buddha-statue-001', '1200x1600', 'Tượng Phật trang nghiêm mang ý nghĩa tâm linh', 'Solemn Buddha statue with spiritual significance'),
('012345678901234567890ABCDEF123456789', 'Tranh Chúa Giê-su Tâm Linh', 'jesus-painting-002', '1100x1400', 'Tranh Chúa Giê-su mang ý nghĩa tâm linh sâu sắc', 'Jesus painting with deep spiritual meaning'),
('12345678901234567890ABCDEF123456789A', 'Avatar Người Dùng Cá Nhân', 'user-avatar-001', '300x300', 'Ảnh đại diện người dùng cá nhân', 'Personal user avatar image'),
('2345678901234567890ABCDEF123456789AB', 'Ảnh Đại Diện Nghệ Thuật', 'user-avatar-002', '300x300', 'Ảnh đại diện phong cách nghệ thuật', 'Artistic style avatar image'),
('345678901234567890ABCDEF123456789ABC', 'Banner Trang Chủ Gallery', 'banner-homepage-001', '1920x600', 'Banner trang chủ của gallery nghệ thuật', 'Gallery homepage banner'),
('45678901234567890ABCDEF123456789ABCD', 'Banner Khuyến Mãi Nghệ Thuật', 'banner-sale-002', '1920x400', 'Banner quảng cáo khuyến mãi nghệ thuật', 'Art promotion banner advertisement'),
('5678901234567890ABCDEF123456789ABCDE', 'Tranh Rừng Thiên Nhiên Hoang Dã', 'nature-forest-003', '1920x1080', 'Cảnh rừng thiên nhiên hoang dã tuyệt đẹp', 'Beautiful wild forest nature scenery');

-- =============================================
-- INSERT SAMPLE PRODUCTS
-- =============================================

INSERT INTO `PRODUCT` (`PRODUCT_NAME`, `PRODUCT_SLUG`, `PRODUCT_CATEGORY_ID`, `PRODUCT_STATE_ID`, `PRODUCT_TYPE_ID`, `SOLD_QUANTITY`, `STOCK_QUANTITY`, `PRODUCT_DESCRIPTION`, `PRODUCT_PRICE`, `PRODUCT_REMARK`, `SEO_META_ID`) VALUES
('Tranh phong cảnh hoàng hôn trên biển', 'tranh-phong-canh-hoang-hon-bien', 1, 1, 1, 25, 50, 'Tranh phong cảnh tuyệt đẹp mô tả hoàng hôn trên biển với màu sắc ấm áp. Chất liệu canvas cao cấp, in UV bền màu. Kích thước 40x60cm, phù hợp trang trí phòng khách, phòng ngủ.', 450000.00, 'Sản phẩm tranh phong cảnh hoàng hôn chất lượng cao', 35),
('Tranh núi non hùng vĩ', 'tranh-nui-non-hung-vi', 1, 1, 1, 18, 35, 'Tranh mô tả dãy núi hùng vĩ với những đỉnh cao chìm trong sương mù. Tạo cảm giác bình yên và thư thái cho không gian sống. Kích thước 50x70cm.', 520000.00, 'Tranh núi non phong cách hùng vĩ ấn tượng', 36),
('Tranh nghệ thuật trừa tượng hiện đại', 'tranh-nghe-thuat-truu-tuong-hien-dai', 1, 1, 1, 30, 40, 'Tranh nghệ thuật trừa tượng với các đường nét và màu sắc độc đáo. Phong cách hiện đại, phù hợp với nội thất contemporary. Kích thước 60x80cm.', 680000.00, 'Nghệ thuật trừa tượng hiện đại độc đáo', 37),
('Bộ cọ vẽ chuyên nghiệp 24 cây', 'bo-co-ve-chuyen-nghiep-24-cay', 2, 1, 3, 45, 80, 'Bộ cọ vẽ chuyên nghiệp gồm 24 cây cọ các loại: cọ tròn, cọ dẹt, cọ quạt. Lông cọ tự nhiên và nhân tạo chất lượng cao. Phù hợp cho vẽ acrylic, oil, watercolor.', 320000.00, 'Bộ cọ vẽ chuyên nghiệp đầy đủ 24 cây', 38),
('Bảng palette pha màu gỗ tự nhiên', 'bang-palette-pha-mau-go-tu-nhien', 2, 1, 3, 12, 25, 'Bảng palette pha màu làm từ gỗ tự nhiên, bề mặt nhẵn mịn. Kích thước 30x40cm, có lỗ để cầm thuận tiện. Dễ dàng vệ sinh sau khi sử dụng.', 180000.00, 'Bảng pha màu gỗ tự nhiên chất lượng cao', 38),
('Đèn LED trang trí phòng khách', 'den-led-trang-tri-phong-khach', 3, 1, 2, 22, 30, 'Đèn LED trang trí hiện đại với thiết kế độc đáo. Ánh sáng ấm, có thể điều chỉnh độ sáng. Tiết kiệm điện, tuổi thọ cao. Phù hợp trang trí phòng khách, phòng ngủ.', 750000.00, 'Đèn LED trang trí hiện đại tiết kiệm điện', 39),
('Tượng trang trí để bàn nghệ thuật', 'tuong-trang-tri-de-ban-nghe-thuat', 3, 1, 2, 8, 15, 'Tượng trang trí bằng ceramic với thiết kế nghệ thuật tinh tế. Màu sắc hài hòa, kích thước nhỏ gọn phù hợp để bàn làm việc, kệ sách. Cao 25cm.', 280000.00, 'Tượng ceramic nghệ thuật trang trí để bàn', 41),
('Khung tranh gỗ sồi cao cấp 40x60', 'khung-tranh-go-soi-cao-cap-40x60', 4, 1, 2, 35, 60, 'Khung tranh làm từ gỗ sồi tự nhiên, gia công tỉ mỉ. Bề mặt được xử lý chống ẩm, chống mối mọt. Kích thước 40x60cm, độ dày 3cm. Có kính bảo vệ.', 350000.00, 'Khung tranh gỗ sồi cao cấp bền đẹp', 40),
('Khung tranh nhôm hiện đại 50x70', 'khung-tranh-nhom-hien-dai-50x70', 4, 1, 2, 28, 45, 'Khung tranh nhôm với thiết kế hiện đại, mỏng nhẹ. Bề mặt anodized chống oxy hóa. Dễ dàng lắp đặt, phù hợp với tranh nghệ thuật hiện đại. Kích thước 50x70cm.', 420000.00, 'Khung tranh nhôm hiện đại chống oxy hóa', 40),
('Tranh chân dung người phụ nữ cổ điển', 'tranh-chan-dung-nu-co-dien', 1, 1, 1, 15, 20, 'Tranh chân dung phong cách cổ điển châu Âu với kỹ thuật vẽ tinh tế. Thể hiện vẻ đẹp dịu dàng của người phụ nữ. Kích thước 30x40cm, phù hợp trang trí phòng ngủ.', 580000.00, 'Tranh chân dung cổ điển châu Âu tinh tế', 44),
('Màu acrylic cao cấp bộ 12 tuýp', 'mau-acrylic-cao-cap-bo-12-tuyp', 2, 1, 3, 38, 70, 'Bộ màu acrylic cao cấp gồm 12 tuýp 20ml các màu cơ bản. Màu sắc tươi sáng, không phai, khô nhanh. Phù hợp cho cả người mới bắt đầu và họa sĩ chuyên nghiệp.', 450000.00, 'Bộ màu acrylic cao cấp 12 tuýp chuyên nghiệp', 42),
('Giấy vẽ canvas 100% cotton A3', 'giay-ve-canvas-100-cotton-a3', 2, 1, 3, 25, 50, 'Giấy vẽ canvas 100% cotton, chất lượng cao. Bề mặt có texture tự nhiên, thấm màu tốt. Kích thước A3 (297x420mm), gói 20 tờ. Phù hợp vẽ acrylic, oil.', 220000.00, 'Giấy canvas cotton A3 texture tự nhiên', 43);

-- =============================================
-- INSERT PRODUCT IMAGES
-- =============================================

INSERT INTO `PRODUCT_IMAGE` (`PRODUCT_ID`, `IMAGE_ID`, `PRODUCT_IMAGE_REMARK_EN`, `PRODUCT_IMAGE_REMARK`) VALUES
(1, 1, 'Main product image', 'Hình ảnh sản phẩm chính'),
(1, 15, 'Additional view', 'Góc nhìn bổ sung'),
(2, 2, 'Main product image', 'Hình ảnh sản phẩm chính'),
(3, 3, 'Main product image', 'Hình ảnh sản phẩm chính'),
(3, 4, 'Alternative view', 'Góc nhìn khác'),
(4, 5, 'Main product image', 'Hình ảnh sản phẩm chính'),
(5, 6, 'Main product image', 'Hình ảnh sản phẩm chính'),
(6, 5, 'Main product image', 'Hình ảnh sản phẩm chính'),
(7, 6, 'Main product image', 'Hình ảnh sản phẩm chính'),
(8, 1, 'Main product image', 'Hình ảnh sản phẩm chính'),
(9, 2, 'Main product image', 'Hình ảnh sản phẩm chính'),
(10, 7, 'Main product image', 'Hình ảnh sản phẩm chính'),
(11, 3, 'Main product image', 'Hình ảnh sản phẩm chính'),
(12, 4, 'Main product image', 'Hình ảnh sản phẩm chính');

-- =============================================
-- INSERT PRODUCT ATTRIBUTES
-- =============================================

INSERT INTO `PRODUCT_ATTRIBUTE` (`PRODUCT_ID`, `PRODUCT_ATTR_ID`, `PRODUCT_ATTRIBUTE_REMARK_EN`, `PRODUCT_ATTRIBUTE_REMARK`) VALUES
(1, 1, 'Size: 40x60cm', 'Kích thước: 40x60cm'),
(1, 2, 'Color: Multi-color', 'Màu sắc: Đa màu'),
(1, 3, 'Material: Canvas', 'Chất liệu: Canvas'),
(2, 1, 'Size: 50x70cm', 'Kích thước: 50x70cm'),
(2, 3, 'Material: Canvas', 'Chất liệu: Canvas'),
(3, 1, 'Size: 60x80cm', 'Kích thước: 60x80cm'),
(3, 2, 'Color: Abstract colors', 'Màu sắc: Màu trừu tượng'),
(4, 1, 'Size: 24 pieces', 'Kích thước: 24 cây'),
(4, 3, 'Material: Natural & Synthetic bristles', 'Chất liệu: Lông tự nhiên và nhân tạo'),
(5, 1, 'Size: 30x40cm', 'Kích thước: 30x40cm'),
(5, 3, 'Material: Natural wood', 'Chất liệu: Gỗ tự nhiên'),
(6, 2, 'Color: Warm white', 'Màu sắc: Trắng ấm'),
(6, 3, 'Material: LED + Metal', 'Chất liệu: LED + Kim loại'),
(7, 1, 'Size: 25cm height', 'Kích thước: Cao 25cm'),
(7, 3, 'Material: Ceramic', 'Chất liệu: Ceramic'),
(8, 1, 'Size: 40x60cm', 'Kích thước: 40x60cm'),
(8, 3, 'Material: Oak wood', 'Chất liệu: Gỗ sồi'),
(9, 1, 'Size: 50x70cm', 'Kích thước: 50x70cm'),
(9, 3, 'Material: Aluminum', 'Chất liệu: Nhôm'),
(10, 1, 'Size: 30x40cm', 'Kích thước: 30x40cm'),
(10, 2, 'Color: Classical tones', 'Màu sắc: Tông cổ điển'),
(11, 1, 'Size: 12 tubes x 20ml', 'Kích thước: 12 tuýp x 20ml'),
(11, 2, 'Color: 12 basic colors', 'Màu sắc: 12 màu cơ bản'),
(12, 1, 'Size: A3 (297x420mm)', 'Kích thước: A3 (297x420mm)'),
(12, 3, 'Material: 100% Cotton', 'Chất liệu: 100% Cotton');

-- =============================================
-- INSERT SAMPLE REVIEWS
-- =============================================

INSERT INTO `REVIEW` (`USER_ID`, `PRODUCT_ID`, `PARENT_REVIEW_ID`, `ROOT_REVIEW_ID`, `REVIEW_LEVEL`, `RATING`, `REVIEW_CONTENT`, `COUNT_LIKE`, `IS_VISIBLE`, `IS_DELETED`, `CREATED_BY_ROLE_ID`) VALUES
(4, 1, NULL, NULL, 0, 5, 'Tranh rất đẹp, màu sắc sống động. Chất lượng canvas tốt, đóng gói cẩn thận. Sẽ mua thêm những bức khác.', 8, TRUE, FALSE, 4),
(5, 1, NULL, NULL, 0, 4, 'Tranh đẹp nhưng hơi nhỏ so với mong đợi. Màu sắc thực tế đậm hơn ảnh một chút.', 3, TRUE, FALSE, 4),
(6, 1, 2, 2, 1, 1, 'Bạn có thể xem kích thước trong mô tả sản phẩm nhé. Mình thấy vừa vặn với không gian phòng.', 1, TRUE, FALSE, 4),
(8, 2, NULL, NULL, 0, 5, 'Tranh núi non thật hùng vĩ, tạo cảm giác thư thái cho phòng ngủ. Chất lượng in rất tốt.', 12, TRUE, FALSE, 4),
(4, 3, NULL, NULL, 0, 5, 'Nghệ thuật trừu tượng đẹp mắt, phù hợp với nội thất hiện đại. Giao hàng nhanh chóng.', 6, TRUE, FALSE, 4),
(5, 4, NULL, NULL, 0, 4, 'Bộ cọ đa dạng, chất lượng tốt cho tầm giá. Một số cọ nhỏ hơi dễ rụng lông.', 4, TRUE, FALSE, 4),
(6, 4, NULL, NULL, 0, 5, 'Cọ vẽ chất lượng tuyệt vời, đặc biệt là các cọ tròn. Rất đáng tiền.', 7, TRUE, FALSE, 4),
(8, 5, NULL, NULL, 0, 5, 'Bảng pha màu gỗ tự nhiên đẹp, bề mặt nhẵn mịn. Dễ vệ sinh sau khi dùng.', 5, TRUE, FALSE, 4),
(4, 6, NULL, NULL, 0, 4, 'Đèn LED đẹp, ánh sáng ấm cúng. Có thể điều chỉnh độ sáng rất tiện lợi.', 9, TRUE, FALSE, 4),
(5, 7, NULL, NULL, 0, 5, 'Tượng ceramic tinh xảo, chi tiết đẹp. Kích thước vừa phải để bàn làm việc.', 3, TRUE, FALSE, 4),
(6, 8, NULL, NULL, 0, 5, 'Khung gỗ sồi chắc chắn, gia công tỉ mỉ. Kính bảo vệ trong suốt.', 11, TRUE, FALSE, 4),
(8, 9, NULL, NULL, 0, 4, 'Khung nhôm nhẹ, thiết kế hiện đại. Dễ lắp đặt và treo tường.', 2, TRUE, FALSE, 4),
(4, 10, NULL, NULL, 0, 5, 'Tranh chân dung cổ điển rất nghệ thuật. Kỹ thuật vẽ tinh tế, đẹp mắt.', 6, TRUE, FALSE, 4),
(5, 11, NULL, NULL, 0, 5, 'Bộ màu acrylic chất lượng cao, màu sắc tươi sáng. Đóng gói cẩn thận.', 8, TRUE, FALSE, 4),
(6, 12, NULL, NULL, 0, 4, 'Giấy canvas cotton chất lượng tốt, bề mặt có texture tự nhiên. Thấm màu đều.', 4, TRUE, FALSE, 4);

-- =============================================
-- INSERT PRODUCT REVIEW LIKES
-- =============================================

INSERT INTO `PRODUCT_REVIEW_LIKE` (`REVIEW_ID`, `USER_ID`) VALUES
(1, 5), (1, 6), (1, 8), (1, 4), (1, 7), (1, 3), (1, 2), (1, 1),
(2, 4), (2, 6), (2, 8),
(3, 5),
(4, 4), (4, 5), (4, 6), (4, 8), (4, 7), (4, 3), (4, 2), (4, 1),
(5, 5), (5, 6), (5, 8), (5, 7), (5, 3), (5, 2),
(6, 4), (6, 6), (6, 8), (6, 7),
(7, 4), (7, 5), (7, 8), (7, 7), (7, 3), (7, 2), (7, 1),
(8, 4), (8, 5), (8, 6), (8, 7), (8, 3),
(9, 5), (9, 6), (9, 8), (9, 7), (9, 3), (9, 2), (9, 1), (9, 4),
(10, 4), (10, 6), (10, 8),
(11, 4), (11, 5), (11, 8), (11, 7), (11, 3), (11, 2), (11, 1), (11, 6),
(12, 4), (12, 5),
(13, 5), (13, 6), (13, 8), (13, 7), (13, 3), (13, 2),
(14, 4), (14, 5), (14, 6), (14, 7), (14, 3), (14, 2), (14, 1), (14, 8),
(15, 4), (15, 5), (15, 8), (15, 7);

-- =============================================
-- INSERT SAMPLE CARTS
-- =============================================

INSERT INTO `CART` (`USER_ID`, `SESSION_ID`, `CART_SLUG`, `CART_STATE_ID`, `TOTAL_AMOUNT`, `CART_REMARK`, `CART_ENABLED`, `SEO_META_ID`) VALUES
(4, 'session_user4_20260118_001', 'cart-user4-20260118-001', 1, 2, 'Giỏ hàng của khách hàng Nguyễn Văn A', TRUE, 45),
(5, 'session_user5_20260118_002', 'cart-user5-20260118-002', 1, 3, 'Giỏ hàng của khách hàng Trần Thị B', TRUE, 46),
(6, 'session_user6_20260118_003', 'cart-user6-20260118-003', 2, 0, 'Giỏ hàng Google user đã thanh toán', TRUE, 47),
(8, 'session_user8_20260118_004', 'cart-user8-20260118-004', 1, 1, 'Giỏ hàng của khách hàng Lê Văn C', TRUE, 48);

-- =============================================
-- INSERT SAMPLE CART ITEMS
-- =============================================

INSERT INTO `CART_ITEM` (`CART_ID`, `PRODUCT_ID`, `CART_ITEM_QUANTITY`, `CART_ITEM_TOTAL_PRICE`, `CART_ITEM_STATE_ID`) VALUES
(1, 1, 1, 450000.00, 1),
(1, 4, 1, 320000.00, 1),
(2, 2, 1, 520000.00, 1),
(2, 6, 1, 750000.00, 1),
(2, 8, 1, 350000.00, 1),
(4, 3, 1, 680000.00, 1);

-- =============================================
-- INSERT SAMPLE DISCOUNTS
-- =============================================

INSERT INTO `DISCOUNT` (`DISCOUNT_CODE`, `DISCOUNT_NAME`, `DISCOUNT_TYPE_ID`, `DISCOUNT_VALUE`, `MAX_DISCOUNT_AMOUNT`, `MIN_ORDER_AMOUNT`, `START_AT`, `END_AT`, `TOTAL_USAGE_LIMIT`, `USED_COUNT`, `IS_ACTIVE`, `DISCOUNT_REMARK`) VALUES
('WELCOME2026', 'Chào mừng năm mới 2026', 1, 10.00, 100000.00, 500000.00, '2026-01-01 00:00:00', '2026-03-31 23:59:59', 1000, 25, TRUE, 'Mã giảm giá chào mừng năm mới 2026'),
('ARTLOVER50', 'Ưu đãi người yêu nghệ thuật', 2, 50000.00, 50000.00, 300000.00, '2026-01-15 00:00:00', '2026-02-15 23:59:59', 500, 12, TRUE, 'Mã giảm giá dành cho người yêu nghệ thuật'),
('STUDENT15', 'Giảm giá cho sinh viên', 1, 15.00, 150000.00, 200000.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 2000, 8, TRUE, 'Mã giảm giá đặc biệt cho sinh viên'),
('FLASH200K', 'Flash sale cuối tuần', 2, 200000.00, 200000.00, 1000000.00, '2026-01-18 00:00:00', '2026-01-20 23:59:59', 100, 3, TRUE, 'Mã giảm giá flash sale cuối tuần');

-- =============================================
-- INSERT SAMPLE ORDERS
-- =============================================

INSERT INTO `ORDER` (`USER_ID`, `ORDER_CODE`, `ORDER_SLUG`, `CART_ID`, `ORDER_STATE_ID`, `DISCOUNT_ID`, `TOTAL_AMOUNT`, `ORDER_NOTE`, `ORDER_REMARK`, `SEO_META_ID`) VALUES
(4, 'ORD-20260115-001', 'ord-20260115-001', 3, 5, 1, 1170000.00, 'Giao hàng trong giờ hành chính', 'Đơn hàng tranh nghệ thuật đã giao thành công', 50),
(5, 'ORD-20260116-002', 'ord-20260116-002', 2, 3, 2, 1570000.00, 'Gọi điện truớc khi giao', 'Đơn hàng dụng cụ vẽ đang xử lý', 51),
(6, 'ORD-20260117-003', 'ord-20260117-003', 1, 2, NULL, 770000.00, 'Để hàng ở bảo vệ nếu không có người', 'Đơn hàng decor nội thất đã xác nhận', 52);

-- =============================================
-- INSERT ORDER ITEMS
-- =============================================

INSERT INTO `ORDER_ITEM` (`ORDER_ID`, `PRODUCT_ID`, `UNIT_PRICE`, `ORDER_ITEM_QUANTITY`, `ORDER_ITEM_TOTAL_PRICE`) VALUES
(1, 1, 450000.00, 1, 450000.00),
(1, 2, 720000.00, 1, 720000.00),
(2, 3, 850000.00, 1, 850000.00),
(2, 4, 720000.00, 1, 720000.00),
(3, 5, 770000.00, 1, 770000.00);

-- =============================================
-- INSERT ORDER STATE HISTORY
-- =============================================

INSERT INTO `ORDER_STATE_HISTORY` (`ORDER_ID`, `ORDER_STATE_HISTORY_SLUG`, `OLD_STATE_ID`, `NEW_STATE_ID`, `ORDER_STATE_HISTORY_NOTE`, `ORDER_STATE_HISTORY_REMARK`, `SEO_META_ID`) VALUES
(1, 'ord-history-001-pending-confirmed', 1, 2, 'Đơn hàng đã được xác nhận', 'Chuyển từ chờ xử lý sang đã xác nhận', 56),
(1, 'ord-history-001-confirmed-processing', 2, 3, 'Bắt đầu xử lý đơn hàng', 'Chuyển từ xác nhận sang đang xử lý', 57),
(1, 'ord-history-001-processing-shipped', 3, 4, 'Đơn hàng đã giao cho đơn vị vận chuyển', 'Chuyển từ xử lý sang đã gửi đi', 58),
(1, 'ord-history-001-shipped-delivered', 4, 5, 'Giao hàng thành công', 'Chuyển từ đang vận chuyển sang đã giao', 59),
(2, 'ord-history-002-pending-confirmed', 1, 2, 'Đơn hàng đã được xác nhận', 'Xác nhận đơn hàng dụng cụ vẽ', 56),
(2, 'ord-history-002-confirmed-processing', 2, 3, 'Đang chuẩn bị hàng', 'Bắt đầu chuẩn bị sản phẩm', 57),
(3, 'ord-history-003-pending-confirmed', 1, 2, 'Đơn hàng được xác nhận', 'Xác nhận đơn hàng decor nội thất', 56);

-- =============================================
-- INSERT SAMPLE PAYMENTS
-- =============================================

INSERT INTO `PAYMENT` (`ORDER_ID`, `PAYMENT_SLUG`, `PAYMENT_METHOD_ID`, `PAYMENT_STATE_ID`, `AMOUNT`, `TRANSACTION_ID`, `PAYMENT_REMARK`, `SEO_META_ID`) VALUES
(1, 'payment-momo-20260115-001', 3, 2, 1170000.00, 'MOMO_TXN_20260115_001', 'Thanh toán qua MoMo thành công', 61),
(2, 'payment-vnpay-20260116-002', 5, 2, 1570000.00, 'VNPAY_TXN_20260116_002', 'Thanh toán qua VNPay thành công', 62),
(3, 'payment-cod-20260117-003', 1, 1, 770000.00, 'COD_TXN_20260117_003', 'Thanh toán khi nhận hàng', 63);

-- =============================================
-- INSERT SAMPLE SHIPMENTS
-- =============================================

INSERT INTO `SHIPMENT` (`ORDER_ID`, `SHIPMENT_SLUG`, `SHIPPING_FEE_ID`, `SHIPMENT_STATE_ID`, `PHONE`, `ADDRESS`, `SHIPMENT_REMARK`, `SEO_META_ID`) VALUES
(1, 'shipment-20260115-001-hcm', 1, 4, '0904567890', '123 Đường ABC, Phường XYZ, Quận 1, TP.HCM', 'Vận chuyển đến TP.HCM đã giao thành công', 65),
(2, 'shipment-20260116-002-hcm', 2, 3, '0905678901', '456 Đường DEF, Phường UVW, Quận 2, TP.HCM', 'Vận chuyển nhanh đang trên đường', 66),
(3, 'shipment-20260117-003-hcm', 1, 1, '0906789012', '789 Đường GHI, Phường RST, Quận 3, TP.HCM', 'Vận chuyển tiêu chuẩn chuẩn bị gửi đi', 67);

-- =============================================
-- INSERT SAMPLE CONTACT
-- =============================================

INSERT INTO `CONTACT` (`CONTACT_NAME`, `CONTACT_SLUG`, `CONTACT_ADDRESS`, `CONTACT_EMAIL`, `CONTACT_PHONE`, `CONTACT_FANPAGE`, `CONTACT_REMARK`, `CONTACT_ENABLED`, `SEO_META_ID`) VALUES
('Art & Decor Store', 'art-decor-store-hcm', '123 Nguyễn Văn Linh, Quận 7, TP.HCM', 'info@artdecor.com', '0281234567', 'https://facebook.com/artdecorstore', 'Showroom chính tại TP.HCM', TRUE, 71),
('Art & Decor Hà Nội', 'art-decor-hanoi', '456 Trần Hưng Đạo, Hoàn Kiếm, Hà Nội', 'hanoi@artdecor.com', '0241234567', 'https://facebook.com/artdecorhanoi', 'Chi nhánh phía Bắc', TRUE, 72);

-- =============================================
-- INSERT SAMPLE BLOGS
-- =============================================

INSERT INTO `BLOG` (`BLOG_CATEGORY_ID`, `BLOG_TYPE_ID`, `BLOG_TITLE`, `BLOG_SLUG`, `BLOG_CONTENT`, `BLOG_REMARK`, `BLOG_ENABLED`, `SEO_META_ID`) VALUES
(1, 1, 'Xu hướng trang trí nội thất năm 2026', 'xu-huong-trang-tri-noi-that-2026', 'Năm 2026 đánh dấu sự trở lại mạnh mẽ của phong cách trang trí tối giản kết hợp với các yếu tố thiên nhiên. Màu sắc trung tính như be, xám nhạt và trắng kem đang chiếm ưu thế...', 'Bài viết về xu hướng trang trí nội thất mới nhất', TRUE, 73),
(2, 1, 'Hướng dẫn chọn tranh phong cảnh phù hợp', 'huong-dan-chon-tranh-phong-canh', 'Việc chọn tranh phong cảnh phù hợp với không gian sống không chỉ tạo điểm nhấn mà còn thể hiện cá tính của gia chủ. Dưới đây là một số gợi ý để bạn lựa chọn...', 'Bài hướng dẫn chọn tranh phong cảnh chuyên sâu', TRUE, 74),
(3, 1, 'Triển lãm nghệ thuật hiện đại tại TP.HCM', 'trien-lam-nghe-thuat-hien-dai-tphcm', 'Triển lãm "Nghệ thuật hiện đại Việt Nam" sẽ diễn ra từ ngày 25/01 đến 25/02/2026 tại Bảo tàng Mỹ thuật TP.HCM. Triển lãm giới thiệu 50 tác phẩm của các họa sĩ nổi tiếng...', 'Thông tin sự kiện triển lãm nghệ thuật', TRUE, 75),
(2, 2, 'Video hướng dẫn vẽ tranh acrylic cho người mới', 'video-huong-dan-ve-tranh-acrylic', 'Video hướng dẫn chi tiết cách vẽ tranh acrylic từ cơ bản đến nâng cao. Bao gồm cách pha màu, kỹ thuật cọ và tạo hiệu ứng đặc biệt...', 'Nội dung video hướng dẫn vẽ tranh', TRUE, 76),
(1, 3, 'Bộ sưu tập tranh phong cảnh mùa xuân', 'bo-suu-tap-tranh-phong-canh-mua-xuan', 'Khám phá bộ sưu tập tranh phong cảnh mùa xuân với những gam màu tươi sáng, tạo cảm giác tươi mới cho không gian sống...', 'Thư viện hình ảnh bộ sưu tập mùa xuân', TRUE, 77);

-- =============================================
-- SUMMARY
-- =============================================
-- INSERT SAMPLE PAGE POSITIONS
-- =============================================

INSERT INTO `PAGE_POSITION` (`PAGE_POSITION_SLUG`, `PAGE_POSITION_NAME`, `PAGE_POSITION_ENABLED`, `PAGE_POSITION_REMARK_EN`, `PAGE_POSITION_REMARK`) VALUES
('header', 'Header Navigation', TRUE, 'Pages shown in header navigation menu', 'Các trang hiển thị trong menu điều hướng header'),
('footer', 'Footer Links', TRUE, 'Pages shown in footer section', 'Các trang hiển thị trong phần footer'),
('sidebar', 'Sidebar Menu', TRUE, 'Pages shown in sidebar navigation', 'Các trang hiển thị trong menu sidebar'),
('main-menu', 'Main Menu', TRUE, 'Pages shown in main navigation menu', 'Các trang hiển thị trong menu điều hướng chính');

-- =============================================
-- INSERT SAMPLE PAGE GROUPS
-- =============================================

INSERT INTO `PAGE_GROUP` (`PAGE_GROUP_SLUG`, `PAGE_GROUP_NAME`, `PAGE_GROUP_ENABLED`, `PAGE_GROUP_REMARK_EN`, `PAGE_GROUP_REMARK`) VALUES
('shop', 'Cửa hàng', TRUE, 'Shop related pages', 'Các trang liên quan đến cửa hàng'),
('support', 'Hỗ trợ', TRUE, 'Customer support pages', 'Các trang hỗ trợ khách hàng'),
('policy', 'Chính sách', TRUE, 'Company policies and terms', 'Các chính sách và điều khoản của công ty'),
('about', 'Giới thiệu', TRUE, 'About us and company information', 'Thông tin giới thiệu về công ty'),
('service', 'Dịch vụ', TRUE, 'Service related pages', 'Các trang liên quan đến dịch vụ');

-- =============================================
-- INSERT SAMPLE PAGES
-- =============================================

INSERT INTO `PAGE` (`PAGE_POSITION_ID`, `PAGE_GROUP_ID`, `TARGET_URL`, `PAGE_SLUG`, `PAGE_NAME`, `PAGE_CONTENT`, `PAGE_ENABLED`, `PAGE_REMARK_EN`, `PAGE_REMARK`) VALUES
(2, 1, NULL, 'tac-pham-goc', 'Tác phẩm gốc', '<h2>Tác phẩm gốc</h2><p>Chúng tôi cam kết mang đến cho quý khách hàng những tác phẩm nghệ thuật chính hãng, có nguồn gốc rõ ràng. Mỗi tác phẩm đều được xác thực và có giấy chứng nhận từ nghệ sĩ hoặc phòng tranh uy tín.</p>', TRUE, 'Original artworks information page', 'Trang thông tin về tác phẩm gốc'),

(2, 2, NULL, 'giao-hang', 'Giao hàng', '<h2>Chính sách giao hàng</h2><p>Art & Decor cung cấp dịch vụ giao hàng toàn quốc với các phương thức:</p><ul><li>Giao hàng tận nơi (2-3 ngày)</li><li>Giao hàng nhanh (1-2 ngày)</li><li>Giao hàng siêu tốc (trong ngày)</li></ul><p>Miễn phí giao hàng cho đơn hàng từ 500.000đ</p>', TRUE, 'Shipping policy and information', 'Chính sách và thông tin giao hàng'),

(2, 3, NULL, 'chinh-sach-doi-tra', 'Chính sách đổi trả', '<h2>Chính sách đổi trả</h2><p>Quý khách có thể đổi/trả sản phẩm trong vòng 7 ngày kể từ ngày nhận hàng với các điều kiện:</p><ul><li>Sản phẩm còn nguyên vẹn, chưa sử dụng</li><li>Đầy đủ hóa đơn, phụ kiện kèm theo</li><li>Không áp dụng với sản phẩm làm riêng theo yêu cầu</li></ul>', TRUE, 'Return and exchange policy', 'Chính sách đổi trả sản phẩm'),

(2, 3, NULL, 'bao-mat-thong-tin', 'Bảo mật thông tin', '<h2>Chính sách bảo mật thông tin</h2><p>Art & Decor cam kết bảo vệ thông tin cá nhân của khách hàng theo tiêu chuẩn bảo mật cao nhất. Mọi thông tin sẽ được mã hóa và chỉ được sử dụng cho mục đích phục vụ khách hàng.</p>', TRUE, 'Privacy policy information', 'Thông tin chính sách bảo mật'),

(2, 4, NULL, 've-chung-toi', 'Về chúng tôi', '<h2>Về Art & Decor</h2><p>Art & Decor được thành lập từ năm 2020 với sứ mệnh mang nghệ thuật đến gần hơn với cuộc sống hàng ngày. Chúng tôi chuyên cung cấp các tác phẩm nghệ thuật, tranh trang trí và phụ kiện nội thất cao cấp.</p>', TRUE, 'About us page content', 'Nội dung trang giới thiệu'),

(2, 4, NULL, 'lien-he', 'Liên hệ', '<h2>Liên hệ với chúng tôi</h2><p><strong>Địa chỉ:</strong> 123 Nguyễn Văn Linh, Quận 7, TP.HCM</p><p><strong>Điện thoại:</strong> 0281234567</p><p><strong>Email:</strong> info@artdecor.com</p><p><strong>Giờ làm việc:</strong> 8:00 - 18:00 (Thứ 2 - Chủ nhật)</p>', TRUE, 'Contact information page', 'Trang thông tin liên hệ'),

(2, 5, NULL, 'dich-vu-tu-van', 'Dịch vụ tư vấn', '<h2>Dịch vụ tư vấn trang trí</h2><p>Đội ngũ chuyên gia của Art & Decor sẵn sàng tư vấn miễn phí để giúp bạn lựa chọn tác phẩm nghệ thuật phù hợp với không gian sống. Liên hệ ngay để được hỗ trợ!</p>', TRUE, 'Interior decoration consulting service', 'Dịch vụ tư vấn trang trí nội thất'),

(2, 5, NULL, 'dich-vu-dong-khung', 'Dịch vụ đóng khung', '<h2>Dịch vụ đóng khung tranh</h2><p>Chúng tôi cung cấp dịch vụ đóng khung tranh chuyên nghiệp với nhiều kiểu dáng và chất liệu khác nhau. Đảm bảo bảo vệ tác phẩm tốt nhất và tăng tính thẩm mỹ.</p>', TRUE, 'Picture framing service', 'Dịch vụ đóng khung tranh'),

(1, 1, '/san-pham', 'san-pham', 'Sản phẩm', NULL, TRUE, 'Products catalog page link', 'Liên kết trang danh mục sản phẩm'),

(1, 4, '/gioi-thieu', 'gioi-thieu', 'Giới thiệu', NULL, TRUE, 'About us page link', 'Liên kết trang giới thiệu');

-- =============================================

SELECT 'Sample data inserted successfully!' AS Status;