-- ============================================
-- Showcase Pay Database Initialization Script
-- Database: MySQL 8.0+
-- ============================================
-- Usage: mysql -u root -p < init.sql
-- ============================================

-- Create database
CREATE DATABASE IF NOT EXISTS `showcase_pay`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `showcase_pay`;

-- ============================================
-- Table: t_order
-- ============================================
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
    `id`          BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    `order_no`    VARCHAR(64)     NOT NULL COMMENT 'Order Number (business unique identifier)',
    `user_id`     BIGINT          NOT NULL COMMENT 'User ID',
    `amount`      DECIMAL(12, 2)  NOT NULL COMMENT 'Order Amount',
    `currency`    VARCHAR(10)     NOT NULL DEFAULT 'CNY' COMMENT 'Currency Code (ISO 4217)',
    `status`      VARCHAR(32)     NOT NULL DEFAULT 'CREATED' COMMENT 'Order Status: CREATED, PENDING_PAYMENT, PAID, SHIPPED, COMPLETED, CANCELLED, REFUNDED',
    `subject`     VARCHAR(256)    NOT NULL COMMENT 'Order Subject / Title',
    `description` TEXT                     DEFAULT NULL COMMENT 'Order Description',
    `pay_time`    DATETIME                 DEFAULT NULL COMMENT 'Payment Completion Time',
    `expire_time` DATETIME                 DEFAULT NULL COMMENT 'Order Expiration Time',
    `extra_data`  JSON                     DEFAULT NULL COMMENT 'Extra Data (JSON format)',
    `version`     INT             NOT NULL DEFAULT 0 COMMENT 'Optimistic Lock Version',
    `created_at`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
    `updated_at`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Update Time',
    `deleted`     TINYINT         NOT NULL DEFAULT 0 COMMENT 'Logical Delete Flag: 0=Active, 1=Deleted',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Order Table';

-- ============================================
-- Table: t_payment_record
-- ============================================
DROP TABLE IF EXISTS `t_payment_record`;
CREATE TABLE `t_payment_record` (
    `id`               BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    `payment_no`       VARCHAR(64)     NOT NULL COMMENT 'Payment Number (business unique identifier)',
    `order_no`         VARCHAR(64)     NOT NULL COMMENT 'Associated Order Number',
    `user_id`          BIGINT          NOT NULL COMMENT 'User ID',
    `payment_method`   VARCHAR(32)     NOT NULL COMMENT 'Payment Method: ALIPAY, WECHAT, CARD, CREDIT, BALANCE, APPLE_PAY',
    `amount`           DECIMAL(12, 2)  NOT NULL COMMENT 'Payment Amount',
    `currency`         VARCHAR(10)     NOT NULL DEFAULT 'CNY' COMMENT 'Currency Code (ISO 4217)',
    `status`           VARCHAR(32)     NOT NULL DEFAULT 'PENDING' COMMENT 'Payment Status: PENDING, PROCESSING, SUCCESS, FAILED, CANCELLED, REFUNDED, TIMEOUT',
    `transaction_id`   VARCHAR(128)             DEFAULT NULL COMMENT 'Third-party Transaction ID (from payment gateway)',
    `gateway_response` JSON                     DEFAULT NULL COMMENT 'Payment Gateway Response (JSON format)',
    `callback_time`    DATETIME                 DEFAULT NULL COMMENT 'Callback Notification Time',
    `version`          INT             NOT NULL DEFAULT 0 COMMENT 'Optimistic Lock Version',
    `created_at`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
    `updated_at`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Update Time',
    `deleted`          TINYINT         NOT NULL DEFAULT 0 COMMENT 'Logical Delete Flag: 0=Active, 1=Deleted',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_transaction_id` (`transaction_id`),
    KEY `idx_status` (`status`),
    KEY `idx_payment_method` (`payment_method`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_callback_time` (`callback_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Payment Record Table';

-- ============================================
-- Table: t_payment_channel
-- ============================================
DROP TABLE IF EXISTS `t_payment_channel`;
CREATE TABLE `t_payment_channel` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
    `channel_code` VARCHAR(32)  NOT NULL COMMENT 'Channel Code: ALIPAY, WECHAT, CARD',
    `channel_name` VARCHAR(64)  NOT NULL COMMENT 'Channel Display Name',
    `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT 'Channel Status: 0=Disabled, 1=Enabled',
    `config`       JSON                  DEFAULT NULL COMMENT 'Channel Configuration (JSON: merchant_id, app_id, keys, gateway URLs, etc.)',
    `version`      INT          NOT NULL DEFAULT 0 COMMENT 'Optimistic Lock Version',
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Update Time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_channel_code` (`channel_code`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Payment Channel Configuration Table';

-- ============================================
-- Sample Data: Payment Channels
-- ============================================
INSERT INTO `t_payment_channel` (`channel_code`, `channel_name`, `status`, `config`) VALUES
('ALIPAY', 'Alipay', 1, JSON_OBJECT(
    'gateway_url', 'https://openapi.alipay.com/gateway.do',
    'app_id', 'your_alipay_app_id',
    'merchant_id', '2088000000000000',
    'sign_type', 'RSA2',
    'notify_url', 'https://your-domain.com/api/pay/callback/alipay'
)),
('WECHAT', 'WeChat Pay', 1, JSON_OBJECT(
    'gateway_url', 'https://api.mch.weixin.qq.com',
    'app_id', 'your_wechat_app_id',
    'merchant_id', '10000000',
    'notify_url', 'https://your-domain.com/api/pay/callback/wechat'
)),
('CARD', 'Bank Card', 1, JSON_OBJECT(
    'gateway_url', 'https://your-bank-gateway.com/api',
    'merchant_id', 'your_bank_merchant_id',
    'notify_url', 'https://your-domain.com/api/pay/callback/card'
));

-- ============================================
-- Sample Data: Orders (for testing)
-- ============================================
INSERT INTO `t_order` (`order_no`, `user_id`, `amount`, `currency`, `status`, `subject`, `description`, `expire_time`) VALUES
('ORD20260404000001', 1001, 99.90, 'CNY', 'CREATED', 'Test Order - Premium Subscription', 'Monthly premium subscription plan', DATE_ADD(NOW(), INTERVAL 30 MINUTE)),
('ORD20260404000002', 1002, 199.00, 'CNY', 'CREATED', 'Test Order - Annual Membership', 'Annual VIP membership', DATE_ADD(NOW(), INTERVAL 30 MINUTE));

-- ============================================
-- Verify Initialization
-- ============================================
SELECT 'Database initialization completed successfully!' AS result;
SELECT COUNT(*) AS total_orders FROM `t_order` WHERE `deleted` = 0;
SELECT COUNT(*) AS total_payment_records FROM `t_payment_record` WHERE `deleted` = 0;
SELECT `channel_code`, `channel_name`, `status` FROM `t_payment_channel`;
