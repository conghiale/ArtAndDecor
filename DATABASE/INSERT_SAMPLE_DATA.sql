-- =============================================
-- File: INSERT_SAMPLE_DATA.sql
-- Description: Consolidated INSERT statements for Art & Decor database
-- Author: Generated for ArtAndDecor Project  
-- Date: March 12, 2026
-- Version: 2.0 - Consolidated all INSERT statements from CREATE_DB_ART_AND_DECOR.sql
-- =============================================

USE `ART_AND_DECOR`;

-- =============================================
-- INSERT REFERENCE DATA
-- =============================================

-- Insert Policy Data
INSERT INTO `POLICY` (`POLICY_NAME`, `POLICY_SLUG`, `POLICY_VALUE`, `POLICY_DISPLAY_NAME`, `POLICY_REMARK`, `POLICY_ENABLED`) VALUES
('FAVICON', 'favicon', 'favicon.ico', 'Favicon file name', 'Tên file favicon', TRUE),
('SOCIAL_IMAGE', 'social-image', 'social-image.jpg', 'Social image file name', 'Tên file ảnh mạng xã hội', TRUE),
('MENU_HEADER_TEXT_03', 'menu-header-text-03', 'Shops', 'Shops menu text', 'Text menu cửa hàng', TRUE),
('STORAGE_PATH', 'storage-path', '/home/masion-art/data/images', 'Storage path', 'Đường dẫn lưu trữ', TRUE),
('MENU_HEADER_TEXT_05', 'menu-header-text-05', 'Liên hệ', 'Contact menu text', 'Text menu liên hệ', TRUE),
('HERO_SECTION_TITLE', 'hero-section-title', 'Lorem Ipsum', 'Hero section main title', 'Tiêu đề chính của hero section', TRUE),
('HERO_SECTION_SUBTITLE', 'hero-section-subtitle', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Faucibus in libero risus semper habitant arcu eget.', 'Hero section subtitle', 'Phụ đề của hero section', TRUE),
('SECTION_TITLE', 'section-title', 'LOREM', 'General section title', 'Tiêu đề section chung', TRUE),
('SECTION_SUBTITLE', 'section-subtitle', 'LOREM IPSUM DOLOR SIT AMET.', 'General section subtitle', 'Phụ đề section chung', TRUE),
('SECTION_IMAGE_01', 'section-image-01', 'pho-co-hoi-an.jpg', 'First section image filename', 'Tên file ảnh section thứ nhất', TRUE),
('SECTION_IMAGE_02', 'section-image-02', 'nui-phu-si.jpg', 'Second section image filename', 'Tên file ảnh section thứ hai', TRUE),
('SECTION_DESCRIPTION_01', 'section-description-01', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Faucibus in libero risus 01...', 'First section description', 'Mô tả section thứ nhất', TRUE),
('SECTION_DESCRIPTION_02', 'section-description-02', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Faucibus in libero risus 02...', 'Second section description', 'Mô tả section thứ hai', TRUE),
('WEBSITE_LOGO_ALT_TEXT', 'website-logo-alt-text', 'Maison Art', 'Website logo alt text', 'Alt text cho logo website', TRUE),
('FOOTER_ABOUT_TEXT', 'footer-about-text', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean commodo ligula eget dolor.', 'Footer about text', 'Text giới thiệu ở footer', TRUE),
('FOOTER_COPYRIGHT_TEXT', 'footer-copyright-text', '© 2025 Palette & Co. Giữ toàn quyền.', 'Footer copyright text', 'Text bản quyền ở footer', TRUE),

-- Email Configuration Policy
('EMAIL_CONFIG', 'email-config', 'mail.smtp.ssl.protocols=TLSv1.2
mail.smtp.ssl.trust=*
mail.smtp.starttls.enable=true
mail.smtp.starttls.required=true
mail.smtp.auth=true

mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.smtp.username=artanddecor.system@gmail.com
mail.smtp.password=your-email-password-here

mail.smtp.sendfromaddr=artanddecor.system@gmail.com
mail.smtp.sendfromname=Art and Decor System

mail.smtp.connectiontimeout=5000
mail.smtp.timeout=5000
mail.smtp.writetimeout=5000
mail.smtp.debug=false

mail.support.address=support@artanddecor.com

system.name=Art and Decor E-commerce Platform
system.website=https://artanddecor.com
system.support.phone=+84-123-456-789', 'Email Configuration', 'Consolidated email configuration containing SMTP settings, sender info, and system details. Format: key=value separated by newlines.', TRUE);

-- Insert SEO Meta Data
INSERT INTO `SEO_META`(`SEO_META_TITLE`, `SEO_META_DESCRIPTION`, `SEO_META_KEYWORDS`, `SEO_META_INDEX`, `SEO_META_FOLLOW`, `SEO_META_CANONICAL_URL`, `SEO_META_IMAGE_NAME`, `SEO_META_SCHEMA_TYPE`, `SEO_META_CUSTOM_JSON`, `SEO_META_ENABLED`) VALUES

-- SEO for Image Categories
('Scenery Images - Phong Cảnh Thiên Nhiên', 'Discover beautiful landscape and nature images for decoration. High quality scenery pictures for your home and office.', 'scenery, landscape, nature, images, decoration, phong cảnh, thiên nhiên', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Art Images - Hình Ảnh Nghệ Thuật', 'Explore artistic images and creative artworks. Professional art collection for interior decoration and design.', 'art, artistic, images, artwork, creative, nghệ thuật, sáng tạo', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Decoration Images - Hình Ảnh Trang Trí', 'Beautiful decoration images for interior design. Transform your space with stunning decorative pictures.', 'decoration, interior, design, decorative, pictures, trang trí, nội thất', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Portrait Images - Hình Ảnh Chân Dung', 'Professional portrait images and photography. Capture memorable moments with high-quality portraits.', 'portrait, photography, professional, memorable, chân dung, nhiếp ảnh', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Religious Images - Hình Ảnh Tôn Giáo', 'Spiritual and religious images for meditation and worship. Sacred art for your spiritual space.', 'religious, spiritual, meditation, worship, sacred, tôn giáo, tâm linh', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Avatar Images - Ảnh Đại Diện', 'User avatar images and profile pictures. Personalize your online presence with unique avatars.', 'avatar, profile, pictures, user, online, đại diện, cá nhân', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Website Banners - Banner Trang Web', 'Professional website banners and headers. Eye-catching designs for your digital presence.', 'banner, website, header, professional, digital, trang web, thiết kế', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),

-- SEO for Image Formats
('JPEG Format - Định Dạng JPEG', 'High-quality JPEG image format for photography and digital art. Optimized compression for web and print.', 'jpeg, format, photography, digital, compression, định dạng, chất lượng', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('PNG Format - Định Dạng PNG', 'PNG image format with transparency support. Perfect for logos, icons, and graphic design elements.', 'png, format, transparency, logos, icons, graphic, định dạng, trong suốt', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('WebP Format - Định Dạng WebP', 'Modern WebP image format with superior compression. Faster loading times for web applications.', 'webp, format, compression, modern, web, loading, định dạng, nén', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('SVG Format - Định Dạng SVG', 'Scalable Vector Graphics format. Perfect for logos and illustrations that need to scale perfectly.', 'svg, vector, scalable, graphics, logos, illustrations, định dạng, vector', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),

-- SEO for Product Categories
('Tranh Treo Tường - Wall Paintings', 'Bộ sưu tập tranh treo tường đẹp cho không gian sống. Trang trí nội thất với nghệ thuật chất lượng cao.', 'tranh treo tường, wall paintings, decoration, interior, art, trang trí, nội thất', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Dụng Cụ Vẽ - Drawing Tools', 'Dụng cụ vẽ chuyên nghiệp cho nghệ sĩ và người yêu nghệ thuật. Chất lượng cao, giá tốt nhất.', 'dụng cụ vẽ, drawing tools, art supplies, professional, nghệ thuật, chuyên nghiệp', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Decor Nội Thất - Interior Decoration', 'Sản phẩm decor nội thất hiện đại và sang trọng. Biến đổi không gian sống của bạn.', 'decor nội thất, interior decoration, modern, luxury, trang trí, hiện đại', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Khung Tranh - Picture Frames', 'Khung tranh chất lượng cao, đa dạng mẫu mã. Bảo vệ và làm đẹp cho tác phẩm nghệ thuật của bạn.', 'khung tranh, picture frames, quality, protect, artwork, chất lượng, bảo vệ', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),

-- SEO for Product Types
('Image Products - Sản Phẩm Hình Ảnh', 'Sản phẩm hình ảnh chất lượng cao cho trang trí và nghệ thuật. Đa dạng chủ đề và phong cách.', 'image products, high quality, decoration, art, diverse, chất lượng cao, đa dạng', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Decor Products - Sản Phẩm Trang Trí', 'Sản phẩm trang trí nội thất độc đáo và sáng tạo. Làm đẹp không gian sống của bạn.', 'decor products, interior, unique, creative, beautiful, trang trí, độc đáo', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Tools Products - Sản Phẩm Dụng Cụ', 'Dụng cụ và thiết bị vẽ chuyên nghiệp. Hỗ trợ tối đa cho quá trình sáng tạo nghệ thuật.', 'tools, equipment, professional, art, creative, support, dụng cụ, chuyên nghiệp', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),

-- SEO for Blog Categories
('Tin Tức Nghệ Thuật - Art News', 'Cập nhật tin tức mới nhất về nghệ thuật và trang trí. Khám phá xu hướng và sự kiện nghệ thuật.', 'tin tức, art news, trends, events, nghệ thuật, xu hướng, sự kiện', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Hướng Dẫn Nghệ Thuật - Art Tutorials', 'Hướng dẫn chi tiết về kỹ thuật vẽ và trang trí. Học hỏi từ các chuyên gia nghệ thuật.', 'hướng dẫn, tutorials, techniques, expert, learn, kỹ thuật, chuyên gia', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Sự Kiện Nghệ Thuật - Art Events', 'Thông tin về các sự kiện, triển lãm và hoạt động nghệ thuật. Tham gia cộng đồng yêu nghệ thuật.', 'sự kiện, events, exhibition, community, art, triển lãm, cộng đồng', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),

-- SEO for Blog Types
('Bài Viết Nghệ Thuật - Art Articles', 'Bài viết chuyên sâu về nghệ thuật và trang trí. Kiến thức và kinh nghiệm từ các chuyên gia.', 'bài viết, articles, expert, knowledge, experience, chuyên sâu, kiến thức', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Video Nghệ Thuật - Art Videos', 'Video hướng dẫn và giải trí về nghệ thuật. Học hỏi qua hình ảnh trực quan và sinh động.', 'video, tutorials, visual, learn, entertainment, trực quan, sinh động', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),
('Thư Viện Hình Ảnh - Image Gallery', 'Thư viện hình ảnh nghệ thuật phong phú và đa dạng. Khám phá và lưu giữ những tác phẩm đẹp.', 'thư viện, gallery, diverse, artwork, beautiful, phong phú, tác phẩm', TRUE, TRUE, NULL, NULL, NULL, NULL, TRUE),

-- SEO for specific Images
('Tranh Phong Cảnh Hoàng Hôn Biển - Sunset Seascape Painting', 'Khám phá vẻ đẹp hoàng hôn trên biển với tranh phong cảnh tuyệt đẹp này.', 'tranh hoàng hôn, phong cảnh biển, sunset painting, tranh trang trí, canvas art', TRUE, TRUE, '/images/sunset-seascape-001', '1A2B3C4D5E6F789012345678901234567890ABCD', 'ImageObject', '{"@context":"https://schema.org"}', TRUE),
('Tranh Núi Non Hùng Vĩ - Mountain Landscape Art', 'Cảnh quan núi non hùng vĩ trong sương mù.', 'tranh núi non, mountain landscape, phong cảnh núi', TRUE, TRUE, '/images/mountain-landscape-002', '2B3C4D5E6F789012345678901234567890ABCDE1', 'ImageObject', '{"@context":"https://schema.org"}', TRUE),
('Nghệ Thuật Trừu Tượng Hiện Đại - Modern Abstract Art', 'Tác phẩm nghệ thuật trừu tượng.', 'abstract art, nghệ thuật trừu tượng', TRUE, TRUE, '/images/modern-abstract-001', '3C4D5E6F789012345678901234567890ABCDEF12', 'VisualArtwork', '{"@context":"https://schema.org"}', TRUE),
('Tranh Trang Trí Nội Thất - Home Decoration Art', 'Bộ sưu tập tranh trang trí nội thất.', 'trang trí nội thất, home decoration', TRUE, TRUE, '/images/home-decoration-001', '5E6F789012345678901234567890ABCDEF1234', 'ImageObject', '{"@context":"https://schema.org"}', TRUE),
('Tranh Chân Dung Nghệ Thuật - Portrait Art Collection', 'Bộ sưu tập tranh chân dung nghệ thuật.', 'tranh chân dung, portrait art', TRUE, TRUE, '/images/portrait-art-001', '789012345678901234567890ABCDEF123456', 'VisualArtwork', '{"@context":"https://schema.org"}', TRUE),
('Tranh Tôn Giáo Tâm Linh - Religious Spiritual Art', 'Tranh tôn giáo với ý nghĩa tâm linh.', 'tranh tôn giáo, religious art', TRUE, TRUE, '/images/religious-art-001', '9012345678901234567890ABCDEF12345678', 'VisualArtwork', '{"@context":"https://schema.org"}', TRUE),
('Avatar Nghệ Thuật Cá Nhân - Personal Art Avatar', 'Hình đại diện nghệ thuật.', 'avatar nghệ thuật, art avatar', FALSE, FALSE, '/images/personal-avatar-001', '12345678901234567890ABCDEF123456789A', 'ImageObject', '{"@context":"https://schema.org"}', FALSE),
('Banner Trang Chủ Nghệ Thuật - Art Homepage Banner', 'Banner trang chủ nghệ thuật.', 'banner homepage, art banner', TRUE, TRUE, '/banners/homepage-art-001', '345678901234567890ABCDEF123456789ABC', 'ImageObject', '{"@context":"https://schema.org"}', TRUE),
('Banner Khuyến Mãi Nghệ Thuật - Art Sale Promotion Banner', 'Banner khuyến mãi nghệ thuật.', 'banner sale, promotion banner', TRUE, TRUE, '/banners/art-sale-002', '45678901234567890ABCDEF123456789ABCD', 'PromotionalOffer', '{"@context":"https://schema.org"}', TRUE),
('Tranh Rừng Thiên Nhiên - Nature Forest Painting', 'Cảnh rừng thiên nhiên hoang dã.', 'tranh rừng, nature forest', TRUE, TRUE, '/images/nature-forest-003', '5678901234567890ABCDEF123456789ABCDE', 'VisualArtwork', '{"@context":"https://schema.org"}', TRUE),

-- SEO for Blog Posts
('Xu hướng trang trí nội thất 2026', 'Khám phá xu hướng trang trí nội thất năm 2026.', 'xu hướng, trang trí, nội thất, 2026', TRUE, TRUE, NULL, NULL, 'Article', NULL, TRUE),
('Hướng dẫn chọn tranh phong cảnh', 'Học cách chọn tranh phong cảnh.', 'hướng dẫn, chọn tranh, phong cảnh', TRUE, TRUE, NULL, NULL, 'Article', NULL, TRUE),
('Triển lãm nghệ thuật hiện đại TP.HCM', 'Thông tin về triển lãm nghệ thuật.', 'triển lãm, nghệ thuật, TPHCM', TRUE, TRUE, NULL, NULL, 'Event', NULL, TRUE),
('Video hướng dẫn vẽ tranh acrylic', 'Video hướng dẫn vẽ tranh acrylic.', 'video, hướng dẫn, vẽ tranh', TRUE, TRUE, NULL, NULL, 'VideoObject', NULL, TRUE),
('Tranh phong cảnh mùa xuân', 'Bộ sưu tập tranh phong cảnh mùa xuân.', 'tranh, phong cảnh, mùa xuân', TRUE, TRUE, NULL, NULL, 'Article', NULL, TRUE);

-- Insert User Providers
INSERT INTO `USER_PROVIDER` (`USER_PROVIDER_NAME`, `USER_PROVIDER_DISPLAY_NAME`, `USER_PROVIDER_REMARK`, `USER_PROVIDER_ENABLED`) VALUES
('LOCAL', 'Local registration', 'Đăng ký trực tiếp trên hệ thống', TRUE),
('GOOGLE', 'Google OAuth', 'Đăng nhập qua Google', TRUE),
('FACEBOOK', 'Facebook OAuth', 'Đăng nhập qua Facebook', TRUE),
('GITHUB', 'GitHub OAuth', 'Đăng nhập qua GitHub', TRUE);

-- Insert User Roles
-- Website supports only 2 roles: ADMIN and CUSTOMER
INSERT INTO `USER_ROLE` (`USER_ROLE_NAME`, `USER_ROLE_DISPLAY_NAME`, `USER_ROLE_REMARK`, `USER_ROLE_ENABLED`) VALUES
('CUSTOMER', 'Customer', 'Khách hàng', TRUE),
('ADMIN', 'System Administrator', 'Quản trị viên hệ thống', TRUE);

-- Insert Product Types
INSERT INTO `PRODUCT_TYPE` (`PRODUCT_TYPE_SLUG`, `PRODUCT_TYPE_NAME`, `PRODUCT_TYPE_DISPLAY_NAME`, `PRODUCT_TYPE_REMARK`, `PRODUCT_TYPE_ENABLED`, `SEO_META_ID`, `IMAGE_ID`) VALUES
('image', 'IMAGE', 'Image products', 'Các sản phẩm hình ảnh', TRUE, 16, NULL),
('decor', 'DECOR', 'Decoration products', 'Các sản phẩm trang trí', TRUE, 17, NULL),
('tools', 'TOOLS', 'Drawing tools and equipment', 'Dụng cụ và thiết bị vẽ', TRUE, 18, NULL);

-- Insert Product Categories
INSERT INTO `PRODUCT_CATEGORY` (`PRODUCT_CATEGORY_SLUG`, `PRODUCT_CATEGORY_NAME`, `PRODUCT_CATEGORY_DISPLAY_NAME`, `PRODUCT_CATEGORY_REMARK`, `PRODUCT_CATEGORY_ENABLED`, `PRODUCT_CATEGORY_VISIBLE`, `SEO_META_ID`, `PRODUCT_TYPE_ID`, `PRODUCT_CATEGORY_PARENT_ID`, `IMAGE_ID`) VALUES
('tranh', 'Tranh', 'All paintings', 'Tất cả các loại tranh', TRUE, TRUE, 12, 1, NULL, NULL),
('tranh-treo-tuong', 'Tranh treo tường', 'Wall paintings', 'Các loại tranh dùng để trang trí treo tường', TRUE, TRUE, 12, 1, 1, NULL),
('dung-cu-ve', 'Dụng cụ vẽ', 'Drawing tools', 'Các dụng cụ, thiết bị dùng để vẽ tranh', TRUE, TRUE, 13, 3, NULL, NULL),
('decor-noi-that', 'Decor nội thất', 'Interior decoration', 'Các sản phẩm trang trí nội thất', TRUE, TRUE, 14, 2, NULL, NULL),
('khung-tranh', 'Khung tranh', 'Picture frames', 'Các loại khung để đóng tranh', TRUE, TRUE, 15, 1, 1, NULL);

-- Insert Product States
INSERT INTO `PRODUCT_STATE` (`PRODUCT_STATE_NAME`, `PRODUCT_STATE_ENABLED`, `PRODUCT_STATE_DISPLAY_NAME`, `PRODUCT_STATE_REMARK`) VALUES
('ACTIVE', TRUE, 'Product is active', 'Sản phẩm đang hoạt động'),
('INACTIVE', TRUE, 'Product is inactive', 'Sản phẩm tạm ngưng'),
('OUT_OF_STOCK', TRUE, 'Product is out of stock', 'Sản phẩm hết hàng'),
('DISCONTINUED', TRUE, 'Product is discontinued', 'Sản phẩm ngưng kinh doanh');

-- Insert Product Attributes
INSERT INTO `PRODUCT_ATTR` (`PRODUCT_ATTR_NAME`, `PRODUCT_ATTR_ENABLED`, `PRODUCT_ATTR_DISPLAY_NAME`, `PRODUCT_ATTR_REMARK`) VALUES
('SIZE', TRUE, 'Product size', 'Kích thước sản phẩm'),
('COLOR', TRUE, 'Product color', 'Màu sắc sản phẩm'),
('MATERIAL', TRUE, 'Product material', 'Chất liệu sản phẩm'),
('BRAND', TRUE, 'Product brand', 'Thương hiệu sản phẩm'),
('WEIGHT', TRUE, 'Product weight', 'Trọng lượng sản phẩm');

-- Insert Cart States
INSERT INTO `CART_STATE` (`CART_STATE_NAME`, `CART_STATE_DISPLAY_NAME`, `CART_STATE_REMARK`, `CART_STATE_ENABLED`) VALUES
('ACTIVE', 'Active cart in use', 'Giỏ hàng đang hoạt động', TRUE),
('CHECKED_OUT', 'All items have been ordered', 'Đã đặt hàng toàn bộ sản phẩm', TRUE),
('CHECKED_OUT_PART', 'Some items have been ordered', 'Đã đặt hàng một phần sản phẩm', TRUE),
('ABANDONED', 'Cart has been abandoned or expired', 'Giỏ hàng đã hết hạn hoặc bị bỏ rơi', TRUE);

-- Insert Cart Item States
INSERT INTO `CART_ITEM_STATE` (`CART_ITEM_STATE_NAME`, `CART_ITEM_STATE_DISPLAY_NAME`, `CART_ITEM_STATE_REMARK`, `CART_ITEM_STATE_ENABLED`) VALUES
('ACTIVE', 'Active cart item', 'Sản phẩm trong giỏ hàng đang hoạt động', TRUE),
('ORDERED', 'Cart item ordered', 'Sản phẩm đã được đặt hàng', TRUE);
('REMOVED', 'Cart item removed', 'Sản phẩm đã được xoá', TRUE);

-- Insert Order States
INSERT INTO `ORDER_STATE` (`ORDER_STATE_NAME`, `ORDER_STATE_DISPLAY_NAME`, `ORDER_STATE_REMARK`, `ORDER_STATE_ENABLED`) VALUES
('PENDING', 'Order is pending', 'Đơn hàng đang chờ xử lý', TRUE),
('CONFIRMED', 'Order is confirmed', 'Đơn hàng đã xác nhận', TRUE),
('PROCESSING', 'Order is being processed', 'Đơn hàng đang xử lý', TRUE),
('SHIPPED', 'Order has been shipped', 'Đơn hàng đã giao cho vận chuyển', TRUE),
('DELIVERED', 'Order has been delivered', 'Đơn hàng đã giao thành công', TRUE),
('CANCELLED', 'Order has been cancelled', 'Đơn hàng đã hủy', TRUE),
('RETURNED', 'Order has been returned', 'Đơn hàng đã trả lại', TRUE);

-- Insert Payment Methods
INSERT INTO `PAYMENT_METHOD` (`PAYMENT_METHOD_NAME`, `PAYMENT_METHOD_DISPLAY_NAME`, `PAYMENT_METHOD_REMARK`, `PAYMENT_METHOD_ENABLED`) VALUES
('COD', 'Cash on Delivery', 'Thanh toán khi nhận hàng', TRUE),
('BANK_TRANSFER', 'Bank Transfer', 'Chuyển khoản ngân hàng', TRUE),
('MOMO', 'MoMo E-wallet', 'Ví điện tử MoMo', TRUE),
('ZALOPAY', 'ZaloPay E-wallet', 'Ví điện tử ZaloPay', TRUE),
('VNPAY', 'VNPay Gateway', 'Cổng thanh toán VNPay', TRUE);

-- Insert Payment States
INSERT INTO `PAYMENT_STATE` (`PAYMENT_STATE_NAME`, `PAYMENT_STATE_DISPLAY_NAME`, `PAYMENT_STATE_REMARK`, `PAYMENT_STATE_ENABLED`) VALUES
('PENDING', 'Payment is pending', 'Thanh toán đang chờ xử lý', TRUE),
('COMPLETED', 'Payment completed', 'Thanh toán thành công', TRUE),
('FAILED', 'Payment failed', 'Thanh toán thất bại', TRUE),
('REFUNDED', 'Payment refunded', 'Thanh toán đã hoàn lại', TRUE);

-- Insert Shipment States
INSERT INTO `SHIPMENT_STATE` (`SHIPMENT_STATE_NAME`, `SHIPMENT_STATE_DISPLAY_NAME`, `SHIPMENT_STATE_REMARK`, `SHIPMENT_STATE_ENABLED`) VALUES
('PREPARING', 'Package is being prepared', 'Đang chuẩn bị hàng', TRUE),
('SHIPPED', 'Package has been shipped', 'Đã giao cho đơn vị vận chuyển', TRUE),
('IN_TRANSIT', 'Package is in transit', 'Đang vận chuyển', TRUE),
('DELIVERED', 'Package delivered', 'Đã giao hàng thành công', TRUE),
('FAILED_DELIVERY', 'Delivery failed', 'Giao hàng thất bại', TRUE);

-- Insert Shipping Fee Types
INSERT INTO `SHIPPING_FEE_TYPE` (`SHIPPING_FEE_TYPE_NAME`, `SHIPPING_FEE_TYPE_DISPLAY_NAME`, `SHIPPING_FEE_TYPE_REMARK`, `SHIPPING_FEE_TYPE_ENABLED`) VALUES
('PERCENTAGE', 'Percentage-based shipping fee', 'Phí vận chuyển tính theo phần trăm', TRUE),
('FIXED_AMOUNT', 'Fixed amount shipping fee', 'Phí vận chuyển số tiền cố định', TRUE),
('FREE_SHIPPING', 'Free shipping', 'Miễn phí vận chuyển', TRUE);

-- Insert Discount Types
INSERT INTO `DISCOUNT_TYPE` (`DISCOUNT_TYPE_NAME`, `DISCOUNT_TYPE_DISPLAY_NAME`, `DISCOUNT_TYPE_REMARK`, `DISCOUNT_TYPE_ENABLED`) VALUES
('PERCENTAGE', 'Percentage discount', 'Giảm giá theo phần trăm', TRUE),
('FIXED_AMOUNT', 'Fixed amount discount', 'Giảm giá số tiền cố định', TRUE);

-- Insert Blog Types
INSERT INTO `BLOG_TYPE` (`BLOG_TYPE_SLUG`, `BLOG_TYPE_NAME`, `BLOG_TYPE_DISPLAY_NAME`, `BLOG_TYPE_REMARK`, `BLOG_TYPE_ENABLED`, `IMAGE_ID`, `SEO_META_ID`) VALUES
('bai-viet', 'Bài viết', 'Article', 'Bài viết thông thường', TRUE, NULL, 22),
('video', 'Video', 'Video content', 'Nội dung video', TRUE, NULL, 23),
('hinh-anh', 'Hình ảnh', 'Image gallery', 'Thư viện hình ảnh', TRUE, NULL, 24);

-- Insert Blog Categories
INSERT INTO `BLOG_CATEGORY` (`BLOG_CATEGORY_SLUG`, `BLOG_CATEGORY_NAME`, `BLOG_CATEGORY_DISPLAY_NAME`, `BLOG_CATEGORY_REMARK`, `BLOG_CATEGORY_ENABLED`, `BLOG_TYPE_ID`, `IMAGE_ID`, `SEO_META_ID`) VALUES
('tin-tuc', 'Tin tức', 'News', 'Tin tức về nghệ thuật và trang trí', TRUE, 1, NULL, 19),
('huong-dan', 'Hướng dẫn', 'Tutorials', 'Hướng dẫn vẽ và trang trí', TRUE, 1, NULL, 20),
('su-kien', 'Sự kiện', 'Events', 'Các sự kiện nghệ thuật', TRUE, 1, NULL, 21);

-- INSERT SAMPLE BLOGS
INSERT INTO `BLOG` (`BLOG_CATEGORY_ID`, `BLOG_TITLE`, `BLOG_SLUG`, `BLOG_CONTENT`, `BLOG_ENABLED`, `BLOG_REMARK`, `SEO_META_ID`) VALUES
(1, 'Xu hướng trang trí nội thất năm 2026', 'xu-huong-trang-tri-noi-that-2026', 'Năm 2026 đánh dấu sự trở lại mạnh mẽ của phong cách trang trí tối giản kết hợp với các yếu tố thiên nhiên. Màu sắc trung tính như be, xám nhạt và trắng kem đang chiếm ưu thế trong việc tạo không gian sống hài hòa và thư giãn. Xu hướng này không chỉ mang lại vẻ đẹp thẩm mỹ mà còn tạo cảm giác bình yên, gần gũi với thiên nhiên cho người sử dụng.', TRUE, 'Bài viết về xu hướng trang trí năm 2026', 35),
(2, 'Hướng dẫn chọn tranh phong cảnh phù hợp', 'huong-dan-chon-tranh-phong-canh', 'Việc chọn tranh phong cảnh phù hợp với không gian sống không chỉ tạo điểm nhấn mà còn thể hiện cá tính của gia chủ. Dưới đây là một số gợi ý để bạn lựa chọn tranh phong cảnh hoàn hảo: 1) Xem xét kích thước không gian và tỷ lệ tranh phù hợp, 2) Chú ý đến màu sắc chủ đạo của căn phòng, 3) Lựa chọn chủ đề phù hợp với công năng sử dụng, 4) Xem xét ánh sáng tự nhiên trong phòng.', TRUE, 'Hướng dẫn chọn tranh cho không gian', 36),
(3, 'Triển lãm nghệ thuật hiện đại tại TP.HCM', 'trien-lam-nghe-thuat-hien-dai-tphcm', 'Triển lãm "Nghệ thuật hiện đại Việt Nam" sẽ diễn ra từ ngày 25/01 đến 25/02/2026 tại Bảo tàng Mỹ thuật TP.HCM. Triển lãm giới thiệu 50 tác phẩm của các họa sĩ nổi tiếng trong và ngoài nước, thể hiện sự phát triển của nghệ thuật hiện đại Việt Nam. Đây là cơ hội tuyệt vời để công chúng yêu nghệ thuật có thể chiêm ngưỡng và tìm hiểu về các tác phẩm đặc sắc.', TRUE, 'Thông tin triển lãm nghệ thuật', 37),
(2, 'Video hướng dẫn vẽ tranh acrylic cho người mới', 'video-huong-dan-ve-tranh-acrylic', 'Video hướng dẫn chi tiết cách vẽ tranh acrylic từ cơ bản đến nâng cao. Bao gồm cách pha màu, kỹ thuật cọ và tạo hiệu ứng đặc biệt. Acrylic là loại sơn dễ sử dụng, khô nhanh và có thể tạo ra nhiều hiệu ứng khác nhau. Video sẽ hướng dẫn từng bước một cách chi tiết, phù hợp cho cả người mới bắt đầu.', TRUE, 'Video hướng dẫn vẽ tranh acrylic', 38),
(1, 'Bộ sưu tập tranh phong cảnh mùa xuân', 'bo-suu-tap-tranh-phong-canh-mua-xuan', 'Khám phá bộ sưu tập tranh phong cảnh mùa xuân với những gam màu tươi sáng, tạo cảm giác tươi mới cho không gian sống. Các tác phẩm trong bộ sưu tập này được chọn lọc kỹ càng, thể hiện vẻ đẹp của thiên nhiên trong mùa xuân với sắc hoa đào, hoa mai và không khí trong lành. Đây là lựa chọn hoàn hảo để làm mới không gian nhà bạn trong dịp Tết Nguyên đán.', TRUE, 'Bộ sưu tập tranh mùa xuân', 39);

-- INSERT SAMPLE USERS
INSERT INTO `USER` (`USER_PROVIDER_ID`, `USER_ROLE_ID`, `USER_ENABLED`, `USER_NAME`, `PASSWORD`, `FIRST_NAME`, `LAST_NAME`, `PHONE_NUMBER`, `EMAIL`, `IMAGE_AVATAR_NAME`, `SOCIAL_MEDIA`, `LAST_LOGIN_DT`) VALUES
(1, 2, TRUE, 'admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyPw5TCGb2/nX8stqZqIvK', 'Admin', 'System', '0901234567', 'admin@artdecor.com', 'A1B2C3D4E5F6789012345678901234567890ABCD', '{"facebook": "admin.artdecor", "instagram": "artdecor_admin"}', '2026-01-21 09:15:00'),
(1, 1, TRUE, 'customer01', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyPw5TCGb2/nX8stqZqIvK', 'Nguyen', 'Van A', '0904567890', 'customer1@gmail.com', 'D4E5F6789012345678901234567890ABCDEF12', '{"instagram": "nguyenvana_art"}', '2026-01-19 16:20:00'),
(1, 1, TRUE, 'customer02', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyPw5TCGb2/nX8stqZqIvK', 'Tran', 'Thi B', '0905678901', 'customer2@gmail.com', 'E5F6789012345678901234567890ABCDEF123', NULL, '2026-01-19 11:10:00'),
(2, 1, TRUE, 'google_user01', NULL, 'Alice', 'Johnson', NULL, 'alice.johnson@gmail.com', 'F6789012345678901234567890ABCDEF1234', '{"google": "alice.johnson.art"}', '2026-01-18 20:15:00'),
(3, 1, TRUE, 'facebook_user01', NULL, 'Bob', 'Smith', NULL, 'bob.smith@facebook.com', '789012345678901234567890ABCDEF12345', '{"facebook": "bob.smith.artist"}', '2026-01-18 08:30:00'),
(1, 1, TRUE, 'customer03', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyPw5TCGb2/nX8stqZqIvK', 'Le', 'Van C', '0906789012', 'customer3@yahoo.com', '89012345678901234567890ABCDEF123456', NULL, '2026-01-17 16:25:00');

-- INSERT SAMPLE IMAGES
INSERT INTO `IMAGE` (`IMAGE_NAME`, `IMAGE_DISPLAY_NAME`, `IMAGE_SLUG`, `IMAGE_SIZE`, `IMAGE_FORMAT`, `PATH_FILE`, `IMAGE_REMARK`) VALUES
('1A2B3C4D5E6F789012345678901234567890ABCD.jpg', 'Tranh Phông Cảnh Hoàng Hôn Biển', 'sunset-landscape-001', '1920x1080', 'jpg', '/1a/2b/1A2B3C4D5E6F789012345678901234567890ABCD.jpg', 'Hình ảnh phong cảnh hoàng hôn trên biển rất đẹp'),
('2B3C4D5E6F789012345678901234567890ABCDE1.png', 'Tranh Núi Non Hùng Vĩ', 'mountain-view-002', '1920x1200', 'png', '/2b/3c/2B3C4D5E6F789012345678901234567890ABCDE1.png', 'Cảnh quan núi non hùng vĩ trong sương mù'),
('3C4D5E6F789012345678901234567890ABCDEF12.jpg', 'Nghệ Thuật Trừa Tượng Hiện Đại', 'abstract-art-001', '1500x1500', 'jpg', '/3c/4d/3C4D5E6F789012345678901234567890ABCDEF12.jpg', 'Tác phẩm nghệ thuật trừa tượng hiện đại'),
('4D5E6F789012345678901234567890ABCDEF123.jpg', 'Tranh Nghệ Thuật Hiện Đại', 'modern-art-002', '1600x1200', 'jpg', '/4d/5e/4D5E6F789012345678901234567890ABCDEF123.jpg', 'Tranh nghệ thuật hiện đại phong cách độc đáo'),
('5E6F789012345678901234567890ABCDEF1234.jpg', 'Trang Trí Nội Thất Cao Cấp', 'home-decor-001', '1200x800', 'jpg', '/5e/6f/5E6F789012345678901234567890ABCDEF1234.jpg', 'Sản phẩm trang trí nội thất cao cấp'),
('6F789012345678901234567890ABCDEF12345.png', 'Tranh Trang Trí Treo Tường', 'wall-decoration-002', '1400x1000', 'png', '/6f/78/6F789012345678901234567890ABCDEF12345.png', 'Tranh trang trí để treo tường phòng khách'),
('789012345678901234567890ABCDEF123456.jpg', 'Chân Dung Người Phụ Nữ Cổ Điển', 'portrait-woman-001', '1000x1200', 'jpg', '/78/90/789012345678901234567890ABCDEF123456.jpg', 'Tranh chân dung người phụ nữ phong cách cổ điển'),
('89012345678901234567890ABCDEF1234567.jpg', 'Chân Dung Người Đàn Ông Nghệ Thuật', 'portrait-man-002', '1000x1200', 'jpg', '/89/01/89012345678901234567890ABCDEF1234567.jpg', 'Tranh chân dung người đàn ông nghệ thuật'),
('9012345678901234567890ABCDEF12345678.jpg', 'Tượng Phật Trang Nghiêm', 'buddha-statue-001', '1200x1600', 'jpg', '/90/12/9012345678901234567890ABCDEF12345678.jpg', 'Tượng Phật trang nghiêm mang ý nghĩa tâm linh'),
('012345678901234567890ABCDEF123456789.jpg', 'Tranh Chúa Giê-su Tâm Linh', 'jesus-painting-002', '1100x1400', 'jpg', '/01/23/012345678901234567890ABCDEF123456789.jpg', 'Tranh Chúa Giê-su mang ý nghĩa tâm linh sâu sắc'),
('12345678901234567890ABCDEF123456789A.png', 'Avatar Người Dùng Cá Nhân', 'user-avatar-001', '300x300', 'png', '/12/34/12345678901234567890ABCDEF123456789A.png', 'Ảnh đại diện người dùng cá nhân'),
('2345678901234567890ABCDEF123456789AB.png', 'Ảnh Đại Diện Nghệ Thuật', 'user-avatar-002', '300x300', 'png', '/23/45/2345678901234567890ABCDEF123456789AB.png', 'Ảnh đại diện phong cách nghệ thuật'),
('345678901234567890ABCDEF123456789ABC.jpg', 'Banner Trang Chủ Gallery', 'banner-homepage-001', '1920x600', 'jpg', '/34/56/345678901234567890ABCDEF123456789ABC.jpg', 'Banner trang chủ của gallery nghệ thuật'),
('45678901234567890ABCDEF123456789ABCD.jpg', 'Banner Khuyến Mãi Nghệ Thuật', 'banner-sale-002', '1920x400', 'jpg', '/45/67/45678901234567890ABCDEF123456789ABCD.jpg', 'Banner quảng cáo khuyến mãi nghệ thuật'),
('5678901234567890ABCDEF123456789ABCDE.jpg', 'Tranh Rừng Thiên Nhiên Hoang Dã', 'nature-forest-003', '1920x1080', 'jpg', '/56/78/5678901234567890ABCDEF123456789ABCDE.jpg', 'Cảnh rừng thiên nhiên hoang dã tuyệt đẹp');

-- INSERT SAMPLE PRODUCTS
INSERT INTO `PRODUCT` (`PRODUCT_NAME`, `PRODUCT_SLUG`, `PRODUCT_CODE`, `PRODUCT_CATEGORY_ID`, `PRODUCT_STATE_ID`, `SOLD_QUANTITY`, `STOCK_QUANTITY`, `PRODUCT_DESCRIPTION`, `PRODUCT_PRICE`, `PRODUCT_FEATURED`, `PRODUCT_HIGHLIGHTED`, `SEO_META_ID`) VALUES
('Tranh phong cảnh hoàng hôn trên biển', 'tranh-phong-canh-hoang-hon-bien', 'ART-SUNSET-001', 1, 1, 25, 50, 'Tranh phong cảnh tuyệt đẹp mô tả hoàng hôn trên biển với màu sắc ấm áp. Chất liệu canvas cao cấp, in UV bền màu. Kích thước 40x60cm, phù hợp trang trí phòng khách, phòng ngủ.', 450000.00, TRUE, FALSE, 25),
('Tranh núi non hùng vĩ', 'tranh-nui-non-hung-vi', 'ART-MOUNTAIN-002', 1, 1, 18, 35, 'Tranh mô tả dãy núi hùng vĩ với những đỉnh cao chìm trong sương mù. Tạo cảm giác bình yên và thư thái cho không gian sống. Kích thước 50x70cm.', 520000.00, FALSE, TRUE, 26),
('Tranh nghệ thuật trừu tượng hiện đại', 'tranh-nghe-thuat-truu-tuong-hien-dai', 'ART-ABSTRACT-003', 1, 1, 30, 40, 'Tranh nghệ thuật trừu tượng với các đường nét và màu sắc độc đáo. Phong cách hiện đại, phù hợp với nội thất contemporary. Kích thước 60x80cm.', 680000.00, TRUE, TRUE, 27),
('Tranh nghệ thuật hiện đại', 'tranh-nghe-thuat-hien-dai', 'ART-MODERN-004', 1, 1, 12, 20, 'Tranh nghệ thuật hiện đại với phong cách độc đáo, màu sắc tươi sáng. Phù hợp trang trí văn phòng và không gian sống hiện đại. Kích thước 50x60cm.', 390000.00, FALSE, FALSE, 28),
('Trang trí nội thất cao cấp', 'trang-tri-noi-that-cao-cap', 'DECOR-LUXURY-005', 4, 1, 8, 15, 'Sản phẩm trang trí nội thất cao cấp với thiết kế tinh tế, chất liệu premium. Tạo điểm nhấn sang trọng cho không gian sống. Kích thước đa dạng.', 750000.00, TRUE, FALSE, 29),
('Tranh trang trí treo tường', 'tranh-trang-tri-treo-tuong', 'ART-WALL-006', 2, 1, 33, 60, 'Tranh trang trí treo tường phong cách cổ điển kết hợp hiện đại. Phù hợp mọi không gian từ phòng khách đến phòng ngủ. Kích thước 40x50cm.', 320000.00, FALSE, TRUE, 30),
('Chân dung người phụ nữ cổ điển', 'chan-dung-nguoi-phu-nu-co-dien', 'ART-PORTRAIT-007', 1, 1, 5, 12, 'Tranh chân dung người phụ nữ phong cách cổ điển với kỹ thuật vẽ tinh xảo. Thể hiện vẻ đẹp và sự duyên dáng qua từng nét vẽ. Kích thước 30x40cm.', 580000.00, FALSE, FALSE, 31),
('Chân dung người đàn ông nghệ thuật', 'chan-dung-nguoi-dan-ong-nghe-thuat', 'ART-PORTRAIT-008', 1, 1, 3, 8, 'Tranh chân dung người đàn ông nghệ thuật với phong cách hiện đại. Thể hiện cá tính mạnh mẽ và quyết đoán. Kích thước 30x40cm.', 580000.00, FALSE, FALSE, 32),
('Tượng Phật trang nghiêm', 'tuong-phat-trang-nghiem', 'REL-BUDDHA-009', 1, 1, 15, 25, 'Tượng Phật trang nghiêm mang ý nghĩa tâm linh sâu sắc, tạo không gian thiền định và bình an. Phù hợp cho phòng thờ và không gian tĩnh tâm. Kích thước 40x60cm.', 650000.00, TRUE, FALSE, 33),
('Tranh Chúa Giê-su tâm linh', 'tranh-chua-gie-su-tam-linh', 'REL-JESUS-010', 1, 1, 7, 18, 'Tranh Chúa Giê-su mang ý nghĩa tâm linh sâu sắc, thể hiện tình yêu và lòng từ bi. Phù hợp cho không gian tôn giáo và tĩnh tâm. Kích thước 35x50cm.', 480000.00, FALSE, TRUE, 34),
('Bút vẽ chuyên nghiệp Faber-Castell', 'but-ve-chuyen-nghiep-faber-castell', 'TOOL-PEN-011', 3, 1, 45, 100, 'Bộ bút vẽ chuyên nghiệp Faber-Castell với đa dạng màu sắc và độ mịn cao. Phù hợp cho các họa sĩ chuyên nghiệp và người yêu nghệ thuật.', 250000.00, TRUE, FALSE, NULL),
('Giấy vẽ canvas cao cấp', 'giay-ve-canvas-cao-cap', 'TOOL-CANVAS-012', 3, 1, 28, 80, 'Giấy vẽ canvas cao cấp với chất liệu cotton blend, surface texture tự nhiên. Phù hợp cho tranh sơn dầu và acrylic. Kích thước A3.', 180000.00, FALSE, FALSE, NULL),
('Khung tranh gỗ tự nhiên', 'khung-tranh-go-tu-nhien', 'FRAME-WOOD-013', 5, 1, 22, 50, 'Khung tranh làm từ gỗ tự nhiên với thiết kế tinh tế, bền đẹp theo thời gian. Phù hợp đóng khung các tác phẩm nghệ thuật. Kích thước 40x60cm.', 150000.00, FALSE, TRUE, NULL),
('Khung tranh kim loại hiện đại', 'khung-tranh-kim-loai-hien-dai', 'FRAME-METAL-014', 5, 1, 18, 35, 'Khung tranh kim loại hiện đại với thiết kế mỏng, sang trọng. Phù hợp với tranh nghệ thuật hiện đại và không gian contemporary. Kích thước 50x70cm.', 220000.00, TRUE, FALSE, NULL),
('Đèn trang trí nghệ thuật LED', 'den-trang-tri-nghe-thuat-led', 'DECOR-LIGHT-015', 4, 1, 12, 30, 'Đèn trang trí nghệ thuật LED với thiết kế độc đáo, ánh sáng ấm áp. Tạo không gian lãng mạn và ấm cúng cho ngôi nhà. Công suất 12W.', 420000.00, FALSE, FALSE, NULL);

-- INSERT SAMPLE PRODUCT IMAGES
INSERT INTO `PRODUCT_IMAGE` (`PRODUCT_ID`, `IMAGE_ID`, `PRODUCT_IMAGE_PRIMARY`) VALUES
(1, 1, TRUE),
(1, 14, FALSE),
(2, 2, TRUE),
(3, 3, TRUE),
(3, 4, FALSE),
(4, 5, TRUE),
(5, 6, TRUE),
(6, 5, TRUE),
(7, 6, TRUE),
(8, 1, TRUE),
(9, 2, TRUE),
(10, 7, TRUE),
(11, 3, TRUE),
(12, 4, TRUE);

-- INSERT SAMPLE PRODUCT ATTRIBUTES WITH QUANTITIES
INSERT INTO `PRODUCT_ATTRIBUTE` (`PRODUCT_ID`, `PRODUCT_ATTR_ID`, `PRODUCT_ATTRIBUTE_VALUE`, `PRODUCT_ATTRIBUTE_QUANTITY`) VALUES

-- Product 1: Tranh phong cảnh hoàng hôn trên biển (Total: 50)
(1, 1, '40x60cm', 25),        -- Size 40x60cm: 25 pieces
(1, 1, '30x40cm', 25),        -- Size 30x40cm: 25 pieces  
(1, 2, 'Warm colors', 30),    -- Warm color variant: 30 pieces
(1, 2, 'Cool colors', 20),    -- Cool color variant: 20 pieces
(1, 3, 'Canvas', 50),         -- Material Canvas: 50 pieces

-- Product 2: Tranh núi non hùng vĩ (Total: 35)
(2, 1, '50x70cm', 20),        -- Size 50x70cm: 20 pieces
(2, 1, '40x60cm', 15),        -- Size 40x60cm: 15 pieces
(2, 3, 'Canvas', 35),         -- Material Canvas: 35 pieces

-- Product 3: Tranh nghệ thuật trừu tượng hiện đại (Total: 40)
(3, 1, '60x80cm', 25),        -- Size 60x80cm: 25 pieces
(3, 1, '50x70cm', 15),        -- Size 50x70cm: 15 pieces
(3, 2, 'Colorful', 22),       -- Colorful variant: 22 pieces
(3, 2, 'Monochrome', 18),     -- Monochrome variant: 18 pieces

-- Product 4: Tranh nghệ thuật hiện đại (Total: 20)
(4, 1, '50x60cm', 12),        -- Size 50x60cm: 12 pieces
(4, 1, '40x50cm', 8),         -- Size 40x50cm: 8 pieces
(4, 2, 'Bright colors', 20),  -- Bright colors: 20 pieces

-- Product 5: Trang trí nội thất cao cấp (Total: 15)
(5, 1, 'Large', 8),           -- Large size: 8 pieces
(5, 1, 'Medium', 7),          -- Medium size: 7 pieces
(5, 3, 'Premium material', 15), -- Premium material: 15 pieces

-- Product 6: Tranh trang trí treo tường (Total: 60)
(6, 1, '40x50cm', 35),        -- Size 40x50cm: 35 pieces
(6, 1, '30x40cm', 25),        -- Size 30x40cm: 25 pieces
(6, 2, 'Classic style', 30),  -- Classic style: 30 pieces
(6, 2, 'Modern style', 30),   -- Modern style: 30 pieces

-- Product 7: Chân dung người phụ nữ cổ điển (Total: 12)
(7, 1, '30x40cm', 8),         -- Size 30x40cm: 8 pieces
(7, 1, '25x35cm', 4),         -- Size 25x35cm: 4 pieces
(7, 2, 'Classical tones', 12), -- Classical tones: 12 pieces

-- Product 8: Chân dung người đàn ông nghệ thuật (Total: 8)
(8, 1, '30x40cm', 5),         -- Size 30x40cm: 5 pieces
(8, 1, '25x35cm', 3),         -- Size 25x35cm: 3 pieces
(8, 2, 'Modern style', 8),    -- Modern style: 8 pieces

-- Product 9: Tượng Phật trang nghiêm (Total: 25)
(9, 1, 'Large (40x60cm)', 15), -- Large size: 15 pieces
(9, 1, 'Medium (30x45cm)', 10), -- Medium size: 10 pieces
(9, 3, 'High-grade resin', 25), -- High-grade resin: 25 pieces

-- Product 10: Tranh Chúa Giê-su tâm linh (Total: 18)
(10, 1, '35x50cm', 12),       -- Size 35x50cm: 12 pieces
(10, 1, '25x35cm', 6),        -- Size 25x35cm: 6 pieces
(10, 2, 'Sacred colors', 18), -- Sacred colors: 18 pieces

-- Product 11: Bút vẽ chuyên nghiệp Faber-Castell (Total: 100)
(11, 1, '24-piece set', 60),  -- 24-piece set: 60 sets
(11, 1, '12-piece set', 40),  -- 12-piece set: 40 sets
(11, 2, 'Multi-color', 100),  -- Multi-color: 100 sets
(11, 4, 'Faber-Castell', 100), -- Brand Faber-Castell: 100 sets

-- Product 12: Giấy vẽ canvas cao cấp (Total: 80)
(12, 1, 'A3 (297x420mm)', 60), -- A3 size: 60 packs
(12, 1, 'A4 (210x297mm)', 20), -- A4 size: 20 packs
(12, 3, '100% Cotton', 40),     -- 100% Cotton: 40 packs
(12, 3, 'Cotton blend', 40),    -- Cotton blend: 40 packs

-- Product 13: Khung tranh gỗ tự nhiên (Total: 50)
(13, 1, '40x60cm', 30),       -- Size 40x60cm: 30 pieces
(13, 1, '30x40cm', 20),       -- Size 30x40cm: 20 pieces
(13, 3, 'Natural oak', 25),   -- Natural oak: 25 pieces
(13, 3, 'Pine wood', 25),     -- Pine wood: 25 pieces

-- Product 14: Khung tranh kim loại hiện đại (Total: 35)
(14, 1, '50x70cm', 20),       -- Size 50x70cm: 20 pieces
(14, 1, '40x60cm', 15),       -- Size 40x60cm: 15 pieces
(14, 2, 'Silver', 18),        -- Silver color: 18 pieces
(14, 2, 'Black', 17),         -- Black color: 17 pieces
(14, 3, 'Aluminum', 35),      -- Aluminum material: 35 pieces

-- Product 15: Đèn trang trí nghệ thuật LED (Total: 30)
(15, 1, 'Standard size', 30),  -- Standard size: 30 pieces
(15, 2, 'Warm white', 20),     -- Warm white: 20 pieces
(15, 2, 'Cool white', 10),     -- Cool white: 10 pieces
(15, 3, 'LED + Metal', 30);    -- LED + Metal: 30 pieces

-- INSERT SAMPLE REVIEWS
INSERT INTO `REVIEW` (`USER_ID`, `PRODUCT_ID`, `PARENT_REVIEW_ID`, `ROOT_REVIEW_ID`, `REVIEW_LEVEL`, `RATING`, `REVIEW_CONTENT`, `COUNT_LIKE`, `IS_VISIBLE`, `IS_DELETED`) VALUES
(4, 1, NULL, NULL, 0, 5, 'Tranh rất đẹp, màu sắc sống động. Chất lượng canvas tốt, đóng gói cẩn thận. Sẽ mua thêm những bức khác.', 8, TRUE, FALSE),
(5, 1, NULL, NULL, 0, 4, 'Tranh đẹp nhưng hơi nhỏ so với mong đợi. Màu sắc thực tế đậm hơn ảnh một chút.', 3, TRUE, FALSE),
(6, 1, 2, 2, 1, 1, 'Bạn có thể xem kích thước trong mô tả sản phẩm nhé. Mình thấy vừa vặn với không gian phòng.', 1, TRUE, FALSE),
(2, 2, NULL, NULL, 0, 5, 'Tranh núi non thật hùng vĩ, tạo cảm giác thư thái cho phòng ngủ. Chất lượng in rất tốt.', 12, TRUE, FALSE),
(4, 3, NULL, NULL, 0, 5, 'Nghệ thuật trừu tượng đẹp mắt, phù hợp với nội thất hiện đại. Giao hàng nhanh chóng.', 6, TRUE, FALSE),
(5, 4, NULL, NULL, 0, 4, 'Bộ cọ đa dạng, chất lượng tốt cho tầm giá. Một số cọ nhỏ hơi dễ rụng lông.', 4, TRUE, FALSE),
(6, 4, NULL, NULL, 0, 5, 'Cọ vẽ chất lượng tuyệt vời, đặc biệt là các cọ tròn. Rất đáng tiền.', 7, TRUE, FALSE),
(2, 5, NULL, NULL, 0, 5, 'Bảng pha màu gỗ tự nhiên đẹp, bề mặt nhẵn mịn. Dễ vệ sinh sau khi dùng.', 5, TRUE, FALSE),
(4, 6, NULL, NULL, 0, 4, 'Đèn LED đẹp, ánh sáng ấm cúng. Có thể điều chỉnh độ sáng rất tiện lợi.', 9, TRUE, FALSE),
(5, 7, NULL, NULL, 0, 5, 'Tượng ceramic tinh xảo, chi tiết đẹp. Kích thước vừa phải để bàn làm việc.', 3, TRUE, FALSE),
(6, 8, NULL, NULL, 0, 5, 'Khung gỗ sồi chắc chắn, gia công tỉ mỉ. Kính bảo vệ trong suốt.', 11, TRUE, FALSE),
(2, 9, NULL, NULL, 0, 4, 'Khung nhôm nhẹ, thiết kế hiện đại. Dễ lắp đặt và treo tường.', 2, TRUE, FALSE),
(4, 10, NULL, NULL, 0, 5, 'Tranh chân dung cổ điển rất nghệ thuật. Kỹ thuật vẽ tinh tế, đẹp mắt.', 6, TRUE, FALSE),
(5, 11, NULL, NULL, 0, 5, 'Bộ màu acrylic chất lượng cao, màu sắc tươi sáng. Đóng gói cẩn thận.', 8, TRUE, FALSE),
(6, 12, NULL, NULL, 0, 4, 'Giấy canvas cotton chất lượng tốt, bề mặt có texture tự nhiên. Thấm màu đều.', 4, TRUE, FALSE);

-- INSERT PRODUCT REVIEW LIKES
INSERT INTO `PRODUCT_REVIEW_LIKE` (`REVIEW_ID`, `USER_ID`) VALUES
(1, 5), (1, 6), (1, 4), (1, 3), (1, 2), (1, 1),
(2, 4), (2, 6), (2, 3),
(3, 5),
(4, 4), (4, 5), (4, 6), (4, 3), (4, 2), (4, 1),
(5, 5), (5, 6), (5, 2), (5, 3),
(6, 4), (6, 6), (6, 2), (6, 3),
(7, 4), (7, 5), (7, 2), (7, 3), (7, 1),
(9, 5), (9, 6), (9, 3), (9, 2), (9, 1), (9, 4),
(10, 4), (10, 6), (10, 2),
(11, 4), (11, 5), (11, 2), (11, 3), (11, 1), (11, 6),
(12, 4), (12, 5),
(13, 5), (13, 6), (13, 3), (13, 2),
(14, 4), (14, 5), (14, 6), (14, 3), (14, 2), (14, 1);

-- INSERT SAMPLE CARTS
INSERT INTO `CART` (`USER_ID`, `SESSION_ID`, `CART_SLUG`, `CART_STATE_ID`, `TOTAL_QUANTITY`, `CART_ENABLED`) VALUES
(4, 'session_user4_20260118_001', 'cart-user4-20260118-001', 1, 2, TRUE),
(5, 'session_user5_20260118_002', 'cart-user5-20260118-002', 1, 3, TRUE),
(6, 'session_user6_20260118_003', 'cart-user6-20260118-003', 2, 0, TRUE),
(2, 'session_user2_20260118_004', 'cart-user2-20260118-004', 1, 1, TRUE);

-- INSERT SAMPLE CART ITEMS
INSERT INTO `CART_ITEM` (`CART_ID`, `PRODUCT_ID`, `CART_ITEM_QUANTITY`, `CART_ITEM_TOTAL_PRICE`, `CART_ITEM_STATE_ID`) VALUES
(1, 1, 1, 450000.00, 1),
(1, 4, 1, 320000.00, 1),
(2, 2, 1, 520000.00, 1),
(2, 6, 1, 750000.00, 1),
(2, 8, 1, 350000.00, 1),
(4, 3, 1, 680000.00, 1);

-- INSERT SAMPLE DISCOUNTS
INSERT INTO `DISCOUNT` (`DISCOUNT_CODE`, `DISCOUNT_NAME`, `DISCOUNT_TYPE_ID`, `DISCOUNT_VALUE`, `MAX_DISCOUNT_AMOUNT`, `MIN_ORDER_AMOUNT`, `START_AT`, `END_AT`, `TOTAL_USAGE_LIMIT`, `USED_COUNT`, `IS_ACTIVE`, `DISCOUNT_DISPLAY_NAME`, `DISCOUNT_REMARK`) VALUES
('WELCOME2026', 'Chào mừng năm mới 2026', 1, 10.00, 100000.00, 500000.00, '2026-01-01 00:00:00', '2026-03-31 23:59:59', 1000, 25, TRUE, 'Mã giảm giá chào mừng năm mới 2026', 'Mã giảm giá chào mừng năm mới 2026'),
('ARTLOVER50', 'Ưu đãi người yêu nghệ thuật', 2, 50000.00, 50000.00, 300000.00, '2026-01-15 00:00:00', '2026-02-15 23:59:59', 500, 12, TRUE, 'Mã giảm giá dành cho người yêu nghệ thuật', 'Mã giảm giá dành cho người yêu nghệ thuật'),
('STUDENT15', 'Giảm giá cho sinh viên', 1, 15.00, 150000.00, 200000.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 2000, 8, TRUE, 'Mã giảm giá đặc biệt cho sinh viên', 'Mã giảm giá đặc biệt cho sinh viên'),
('FLASH200K', 'Flash sale cuối tuần', 2, 200000.00, 200000.00, 1000000.00, '2026-01-18 00:00:00', '2026-01-20 23:59:59', 100, 3, TRUE, 'Mã giảm giá flash sale cuối tuần', 'Mã giảm giá flash sale cuối tuần');

-- INSERT SAMPLE SHIPPING FEES
INSERT INTO `SHIPPING_FEE` (`SHIPPING_FEE_TYPE_ID`, `MIN_ORDER_PRICE`, `MAX_ORDER_PRICE`, `SHIPPING_FEE_VALUE`, `SHIPPING_FEE_DISPLAY_NAME`, `SHIPPING_FEE_REMARK`, `SHIPPING_FEE_ENABLED`) VALUES
(3, 500000.00, 999999999.99, 0.00, 'Miễn phí vận chuyển đơn hàng trên 500k', 'Miễn phí vận chuyển cho đơn hàng từ 500k trở lên', TRUE),
(2, 0.00, 499999.99, 30000.00, 'Phí vận chuyển cố định', 'Phí vận chuyển cố định 30k cho đơn hàng dưới 500k', TRUE);

-- INSERT SAMPLE ORDERS
INSERT INTO `ORDER` (`USER_ID`, `ORDER_CODE`, `ORDER_SLUG`, `CART_ID`, `ORDER_STATE_ID`, `DISCOUNT_ID`, `DISCOUNT_CODE`, `DISCOUNT_TYPE`, `DISCOUNT_VALUE`, `CUSTOMER_NAME`, `CUSTOMER_PHONE_NUMBER`, `CUSTOMER_EMAIL`, `CUSTOMER_ADDRESS`, `RECEIVER_NAME`, `RECEIVER_PHONE`, `RECEIVER_EMAIL`, `RECEIVER_ADDRESS`, `SUBTOTAL_AMOUNT`, `DISCOUNT_AMOUNT`, `SHIPPING_FEE_AMOUNT`, `TOTAL_AMOUNT`, `ORDER_NOTE`) VALUES
(4, 'ORD-20260115-001', 'ord-20260115-001', 3, 5, 1, 'WELCOME2026', 'PERCENTAGE', 10.00, 'Nguyen Van A', '0904567890', 'customer1@gmail.com', '789 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 'Alice Johnson', '0904567890', 'alice.johnson@gmail.com', '123 Đường ABC, Phường XYZ, Quận 1, TP.HCM', 1200000.00, 30000.00, 0.00, 1170000.00, 'Giao hàng trong giờ hành chính'),
(5, 'ORD-20260116-002', 'ord-20260116-002', 2, 3, 2, 'ARTLOVER50', 'FIXED_AMOUNT', 50000.00, 'Tran Thi B', '0905678901', 'customer2@gmail.com', '456 Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP.HCM', 'Tran Thi B', '0905678901', 'customer2@gmail.com', '456 Đường DEF, Phường UVW, Quận 2, TP.HCM', 1620000.00, 50000.00, 0.00, 1570000.00, 'Gọi điện truớc khi giao'),
(6, 'ORD-20260117-003', 'ord-20260117-003', 1, 2, NULL, NULL, NULL, NULL, 'Le Van C', '0906789012', 'customer3@yahoo.com', '321 Đường Đồng Khởi, Phường Bến Nghé, Quận 1, TP.HCM', 'Le Van C', '0906789012', 'customer3@yahoo.com', '789 Đường GHI, Phường RST, Quận 3, TP.HCM', 770000.00, 0.00, 0.00, 770000.00, 'Để hàng ở bảo vệ nếu không có người');

-- INSERT ORDER ITEMS
INSERT INTO `ORDER_ITEM` (`ORDER_ID`, `PRODUCT_ID`, `PRODUCT_NAME`, `PRODUCT_CODE`, `PRODUCT_CATEGORY_NAME`, `PRODUCT_TYPE_NAME`, `PRODUCT_ATTR_JSON`, `UNIT_PRICE`, `QUANTITY`, `TOTAL_PRICE`) VALUES
(1, 1, 'Tranh phong cảnh hoàng hôn trên biển', 'ART-SUNSET-001', 'Tranh thiên nhiên', 'Nghệ thuật', '{"material": "Canvas", "size": "40x60cm", "style": "Landscape"}', 450000.00, 1, 450000.00),
(1, 2, 'Tranh núi non hùng vĩ', 'ART-MOUNTAIN-002', 'Tranh thiên nhiên', 'Nghệ thuật', '{"material": "Canvas", "size": "50x70cm", "style": "Landscape"}', 720000.00, 1, 720000.00),
(2, 3, 'Tranh nghệ thuật trữa tượng hiện đại', 'ART-ABSTRACT-003', 'Tranh thiên nhiên', 'Nghệ thuật', '{"material": "Canvas", "size": "60x80cm", "style": "Abstract"}', 850000.00, 1, 850000.00),
(2, 4, 'Bộ cọ vẽ chuyên nghiệp 24 cây', 'TOOL-BRUSH-004', 'Dụng cụ vẽ', 'Dụng cụ nghệ thuật', '{"type": "Brush Set", "count": "24", "material": "Natural & Synthetic"}', 720000.00, 1, 720000.00),
(3, 5, 'Bảng palette pha màu gỗ tự nhiên', 'TOOL-PALETTE-005', 'Dụng cụ vẽ', 'Dụng cụ nghệ thuật', '{"material": "Wood", "size": "30x40cm", "type": "Color Mixing Palette"}', 770000.00, 1, 770000.00);

-- INSERT ORDER STATE HISTORY
INSERT INTO `ORDER_STATE_HISTORY` (`ORDER_ID`, `OLD_STATE_ID`, `NEW_STATE_ID`, `CHANGED_BY_USER_ID`) VALUES
(1, 1, 2, 1),
(1, 2, 3, 1),
(1, 3, 4, 1),
(1, 4, 5, 1),
(2, 1, 2, 1),
(2, 2, 3, 1),
(3, 1, 2, 1);

-- INSERT SAMPLE PAYMENTS
INSERT INTO `PAYMENT` (`ORDER_ID`, `PAYMENT_SLUG`, `PAYMENT_METHOD_ID`, `PAYMENT_STATE_ID`, `AMOUNT`, `TRANSACTION_ID`, `PAYMENT_REMARK`) VALUES
(1, 'payment-momo-20260115-001', 3, 2, 1170000.00, 'MOMO_TXN_20260115_001', 'Thanh toán qua MoMo thành công'),
(2, 'payment-vnpay-20260116-002', 5, 2, 1570000.00, 'VNPAY_TXN_20260116_002', 'Thanh toán qua VNPay thành công'),
(3, 'payment-cod-20260117-003', 1, 1, 770000.00, 'COD_TXN_20260117_003', 'Thanh toán khi nhận hàng');

-- INSERT SAMPLE SHIPMENTS
INSERT INTO `SHIPMENT` (`ORDER_ID`, `SHIPMENT_CODE`, `SHIPMENT_STATE_ID`, `RECEIVER_NAME`, `RECEIVER_PHONE`, `RECEIVER_EMAIL`, `ADDRESS_LINE`, `CITY`, `DISTRICT`, `WARD`, `COUNTRY`, `SHIPPING_FEE_AMOUNT`, `SHIPPED_AT`, `DELIVERED_AT`, `SHIPMENT_REMARK`) VALUES
(1, 'SHIP-20260115-001', 4, 'Alice Johnson', '0904567890', 'alice.johnson@gmail.com', '123 Đường ABC, Phường XYZ', 'TP.HCM', 'Quận 1', 'Phường XYZ', 'Việt Nam', 0.00, '2026-01-16 10:00:00', '2026-01-17 15:30:00', 'Giao hàng thành công'),
(2, 'SHIP-20260116-002', 3, 'Tran Thi B', '0905678901', 'customer2@gmail.com', '456 Đường DEF, Phường UVW', 'TP.HCM', 'Quận 2', 'Phường UVW', 'Việt Nam', 0.00, '2026-01-17 09:00:00', NULL, 'Đang vận chuyển'),
(3, 'SHIP-20260117-003', 1, 'Le Van C', '0906789012', 'customer3@yahoo.com', '789 Đường GHI, Phường RST', 'TP.HCM', 'Quận 3', 'Phường RST', 'Việt Nam', 0.00, NULL, NULL, 'Đang chuẩn bị hàng');

-- INSERT SAMPLE CONTACT
INSERT INTO `CONTACT` (`CONTACT_NAME`, `CONTACT_SLUG`, `CONTACT_ADDRESS`, `CONTACT_EMAIL`, `CONTACT_PHONE`, `CONTACT_FANPAGE`, `CONTACT_ENABLED`, `CONTACT_REMARK`, `SEO_META_ID`) VALUES
('Art & Decor Store', 'art-decor-store-hcm', '123 Nguyễn Văn Linh, Quận 7, TP.HCM', 'info@artdecor.com', '0281234567', 'https://facebook.com/artdecorstore', TRUE, 'Cửa hàng chính tại TP.HCM', NULL),
('Art & Decor Hà Nội', 'art-decor-hanoi', '456 Trần Hưng Đạo, Hoàn Kiếm, Hà Nội', 'hanoi@artdecor.com', '0241234567', 'https://facebook.com/artdecorhanoi', TRUE, 'Chi nhánh tại Hà Nội', NULL);

-- INSERT SAMPLE PAGE_POSITION
INSERT INTO `PAGE_POSITION` (PAGE_POSITION_SLUG, PAGE_POSITION_NAME, PAGE_POSITION_DISPLAY_NAME, PAGE_POSITION_REMARK) VALUES
('header', 'Header Menu', 'Header Navigation', 'Pages displayed in the header navigation'),
('footer', 'Footer Menu', 'Footer Navigation', 'Pages displayed in the footer navigation'),
('sidebar', 'Sidebar Menu', 'Sidebar Navigation', 'Pages displayed in sidebar'),
('landing', 'Landing Page', 'Landing Pages', 'Landing pages of the website');

-- INSERT SAMPLE PAGE_GROUP
INSERT INTO `PAGE_GROUP` (PAGE_GROUP_SLUG, PAGE_GROUP_NAME, PAGE_GROUP_DISPLAY_NAME, PAGE_GROUP_REMARK) VALUES
('general', 'General Pages', 'General Information', 'Common website pages'),
('company', 'Company Pages', 'About Company', 'Pages related to company information'),
('support', 'Support Pages', 'Customer Support', 'Support and help pages'),
('legal', 'Legal Pages', 'Legal Information', 'Legal policy pages');

-- INSERT SAMPLE PAGE
INSERT INTO `PAGE` (PAGE_POSITION_ID, PAGE_GROUP_ID, TARGET_URL, PAGE_SLUG, PAGE_NAME, PAGE_CONTENT, PAGE_DISPLAY_NAME, PAGE_REMARK) VALUES
(4, 1, '/', 'home', 'Home Page', '<h1>Welcome to our website</h1>', 'Home', 'Main landing page'),
(1, 2, '/about', 'about-us', 'About Us', '<p>About our company</p>', 'About Us', 'Company introduction page'),
(1, 3, '/contact', 'contact', 'Contact Page', '<p>Contact information</p>', 'Contact', 'Contact information page'),
(3, 3, '/faq', 'faq', 'FAQ Page', '<p>Frequently asked questions</p>', 'FAQ', 'FAQ support page'),
(2, 4, '/privacy-policy', 'privacy-policy', 'Privacy Policy', '<p>Privacy policy content</p>', 'Privacy Policy', 'Legal privacy policy'),
(2, 4, '/terms-of-service', 'terms-of-service', 'Terms of Service', '<p>Terms and conditions</p>', 'Terms of Service', 'Legal terms page');

-- =============================================
-- CONSOLIDATION COMPLETE
-- Database now has all INSERT statements in a single file
-- Each table has INSERT statements in only one location
-- =============================================