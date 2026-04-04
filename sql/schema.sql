-- ============================================
-- Showcase Pay Database Schema
-- Database: MySQL 8.0+
-- ============================================

-- Create database
CREATE DATABASE IF NOT EXISTS `showcase_pay` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `showcase_pay`;

-- ============================================
-- Payment Table
-- ============================================
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `payment_no` VARCHAR(64) NOT NULL COMMENT 'Payment Number',
  `order_no` VARCHAR(64) NOT NULL COMMENT 'Order Number',
  `user_id` BIGINT NOT NULL COMMENT 'User ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT 'Payment Amount',
  `currency` VARCHAR(10) NOT NULL DEFAULT 'CNY' COMMENT 'Currency',
  `payment_method` VARCHAR(32) NOT NULL COMMENT 'Payment Method: ALIPAY, WECHAT, CARD',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT 'Status: PENDING, PROCESSING, SUCCESS, FAILED, CLOSED',
  `transaction_id` VARCHAR(128) DEFAULT NULL COMMENT 'Third-party Transaction ID',
  `subject` VARCHAR(256) DEFAULT NULL COMMENT 'Payment Subject',
  `description` TEXT COMMENT 'Payment Description',
  `pay_time` DATETIME DEFAULT NULL COMMENT 'Payment Time',
  `expire_time` DATETIME DEFAULT NULL COMMENT 'Expiration Time',
  `callback_url` VARCHAR(512) DEFAULT NULL COMMENT 'Callback URL',
  `extra_data` TEXT COMMENT 'Extra Data (JSON)',
  `error_message` TEXT COMMENT 'Error Message',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  `create_by` VARCHAR(64) DEFAULT 'system' COMMENT 'Created By',
  `update_by` VARCHAR(64) DEFAULT 'system' COMMENT 'Updated By',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Logical Delete: 0=No, 1=Yes',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_transaction_id` (`transaction_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Payment Record Table';

-- ============================================
-- Orders Table
-- ============================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `order_no` VARCHAR(64) NOT NULL COMMENT 'Order Number',
  `user_id` BIGINT NOT NULL COMMENT 'User ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT 'Order Amount',
  `currency` VARCHAR(10) NOT NULL DEFAULT 'CNY' COMMENT 'Currency',
  `status` VARCHAR(32) NOT NULL DEFAULT 'CREATED' COMMENT 'Status: CREATED, PENDING_PAYMENT, PAID, SHIPPED, COMPLETED, CANCELLED, REFUNDED',
  `subject` VARCHAR(256) NOT NULL COMMENT 'Order Subject',
  `description` TEXT COMMENT 'Order Description',
  `pay_time` DATETIME DEFAULT NULL COMMENT 'Payment Time',
  `expire_time` DATETIME DEFAULT NULL COMMENT 'Expiration Time',
  `extra_data` TEXT COMMENT 'Extra Data (JSON)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  `create_by` VARCHAR(64) DEFAULT 'system' COMMENT 'Created By',
  `update_by` VARCHAR(64) DEFAULT 'system' COMMENT 'Updated By',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Logical Delete: 0=No, 1=Yes',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Order Table';

-- ============================================
-- Payment Channel Config Table
-- ============================================
DROP TABLE IF EXISTS `payment_channel_config`;
CREATE TABLE `payment_channel_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `channel_code` VARCHAR(32) NOT NULL COMMENT 'Channel Code: ALIPAY, WECHAT, etc.',
  `channel_name` VARCHAR(64) NOT NULL COMMENT 'Channel Name',
  `merchant_id` VARCHAR(128) NOT NULL COMMENT 'Merchant ID',
  `merchant_key` VARCHAR(512) NOT NULL COMMENT 'Merchant Key (Encrypted)',
  `app_id` VARCHAR(128) DEFAULT NULL COMMENT 'App ID',
  `private_key` TEXT COMMENT 'Private Key (Encrypted)',
  `public_key` TEXT COMMENT 'Public Key',
  `notify_url` VARCHAR(512) DEFAULT NULL COMMENT 'Notify URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0=Disabled, 1=Enabled',
  `extra_config` TEXT COMMENT 'Extra Config (JSON)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  `create_by` VARCHAR(64) DEFAULT 'system' COMMENT 'Created By',
  `update_by` VARCHAR(64) DEFAULT 'system' COMMENT 'Updated By',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Logical Delete: 0=No, 1=Yes',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_channel_code` (`channel_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Payment Channel Configuration Table';

-- ============================================
-- Payment Log Table
-- ============================================
DROP TABLE IF EXISTS `payment_log`;
CREATE TABLE `payment_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `payment_no` VARCHAR(64) NOT NULL COMMENT 'Payment Number',
  `operation_type` VARCHAR(32) NOT NULL COMMENT 'Operation Type: CREATE, PAY, CALLBACK, CANCEL, CLOSE, REFUND',
  `request_data` TEXT COMMENT 'Request Data (JSON)',
  `response_data` TEXT COMMENT 'Response Data (JSON)',
  `status` VARCHAR(32) NOT NULL COMMENT 'Status: SUCCESS, FAILED',
  `error_message` TEXT COMMENT 'Error Message',
  `operator` VARCHAR(64) DEFAULT 'system' COMMENT 'Operator',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  PRIMARY KEY (`id`),
  KEY `idx_payment_no` (`payment_no`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Payment Operation Log Table';

-- ============================================
-- Insert sample data
-- ============================================
INSERT INTO `payment_channel_config` (`channel_code`, `channel_name`, `merchant_id`, `merchant_key`, `app_id`, `status`, `extra_config`) VALUES
('ALIPAY', 'Alipay', '2088000000000000', 'encrypted_key_here', 'app_id_here', 1, '{"gateway_url":"https://openapi.alipay.com/gateway.do"}'),
('WECHAT', 'WeChat Pay', '10000000', 'encrypted_key_here', 'app_id_here', 1, '{"gateway_url":"https://api.mch.weixin.qq.com"}');
