-- ================================
-- 1. DROP TABLE IF EXISTS (optional)
-- ================================
DROP TABLE IF EXISTS `WISHLIST`;

-- ================================
-- 2. CREATE TABLE
-- ================================
CREATE TABLE `WISHLIST` (
    `WISHLIST_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `USER_ID` BIGINT NULL,
    `SESSION_ID` VARCHAR(100) NULL,
    `PRODUCT_ID` BIGINT NOT NULL,
    `CREATED_DT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `UPDATED_AT` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT `fk_wishlist_user`
        FOREIGN KEY (`USER_ID`)
        REFERENCES `USER`(`USER_ID`)
        ON DELETE CASCADE,

    CONSTRAINT `fk_wishlist_product`
        FOREIGN KEY (`PRODUCT_ID`)
        REFERENCES `PRODUCT`(`PRODUCT_ID`)
        ON DELETE CASCADE,

    -- Indexes
    INDEX `idx_wishlist_user` (`USER_ID`),
    INDEX `idx_wishlist_session` (`SESSION_ID`),
    INDEX `idx_wishlist_product` (`PRODUCT_ID`),
    INDEX `idx_wishlist_created` (`CREATED_DT`),

    -- Unique constraints (IMPORTANT NOTE BELOW)
    UNIQUE KEY `uk_user_product` (`USER_ID`, `PRODUCT_ID`),
    UNIQUE KEY `uk_session_product` (`SESSION_ID`, `PRODUCT_ID`)
);

-- ================================
-- 3. NOTE QUAN TRỌNG
-- ================================
-- MySQL cho phép nhiều NULL trong UNIQUE INDEX
-- => Không bị conflict khi USER_ID NULL hoặc SESSION_ID NULL
-- => Behavior đúng cho cả guest & user

-- ================================
-- 4. INSERT DATA - USER LOGIN
-- ================================
INSERT INTO `WISHLIST` (`USER_ID`, `SESSION_ID`, `PRODUCT_ID`) VALUES
-- Customer 1
(2, NULL, 1),
(2, NULL, 3),
(2, NULL, 5),
(2, NULL, 9),
(2, NULL, 11),

-- Customer 2
(3, NULL, 2),
(3, NULL, 6),
(3, NULL, 13),

-- Customer 3
(4, NULL, 1),
(4, NULL, 4),
(4, NULL, 7),
(4, NULL, 15),

-- Customer 4
(5, NULL, 3),
(5, NULL, 8),
(5, NULL, 12),

-- Customer 5
(6, NULL, 10),
(6, NULL, 14);

-- ================================
-- 5. INSERT DATA - GUEST (SESSION)
-- ================================
INSERT INTO `WISHLIST` (`USER_ID`, `SESSION_ID`, `PRODUCT_ID`) VALUES
-- Session 1
(NULL, 'session_4fd0459a143f46f4bf4bc57cdde992cb', 1),
(NULL, 'session_4fd0459a143f46f4bf4bc57cdde992cb', 5),
(NULL, 'session_4fd0459a143f46f4bf4bc57cdde992cb', 9),

-- Session 2
(NULL, 'session_a2f5b3a1546a480e932f5f83ce9321d1', 2),
(NULL, 'session_a2f5b3a1546a480e932f5f83ce9321d1', 6),

-- Session 3
(NULL, 'session_5f610ae951bc485fb473427c108a425b', 3),
(NULL, 'session_5f610ae951bc485fb473427c108a425b', 11);

-- ================================
-- 6. TEST QUERY (OPTIONAL)
-- ================================

-- Lấy wishlist theo user
-- SELECT * FROM WISHLIST WHERE USER_ID = 2;

-- Lấy wishlist theo session (guest)
-- SELECT * FROM WISHLIST WHERE SESSION_ID = 'sess_anon_001_xxx';

-- Check duplicate protection
-- INSERT INTO WISHLIST (USER_ID, PRODUCT_ID) VALUES (2, 1); -- ERROR expected