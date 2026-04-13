-- ============================================
-- Nacos Configuration Data for Showcase Pay
-- Database: MySQL 8.0+ (Nacos 2.x embedded MySQL mode)
-- ============================================
-- Prerequisites: Nacos must be configured to use MySQL as its backend storage.
-- Usage: mysql -u root -p nacos_config < nacos_config.sql
-- Note: This script assumes the nacos_config database already exists (created by Nacos init).
-- ============================================

USE `nacos_config`;

-- ============================================
-- Shared Common Configuration
-- data_id: common-config.yaml
-- group: DEFAULT_GROUP
-- ============================================
INSERT INTO `config_info` (
    `data_id`,
    `group_id`,
    `tenant_id`,
    `app_name`,
    `content`,
    `md5`,
    `gmt_create`,
    `gmt_modified`,
    `src_user`,
    `src_ip`,
    `c_desc`,
    `c_use`,
    `effect`,
    `type`,
    `c_schema`,
    `encrypted_data_key`
) VALUES (
    'common-config.yaml',
    'DEFAULT_GROUP',
    '',
    'showcase-pay',
    '# Common Configuration for Showcase Pay Services
# Shared across all microservices

# Redis Configuration (shared)
spring:
  data:
    redis:
      host: ${REDIS_HOST:127.0.0.1}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      database: 0
      timeout: 5000ms
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
          max-wait: 3000ms

# MyBatis Plus Common Settings
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      id-type: assign_id
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# Logging Configuration
logging:
  level:
    com.showcase.pay: DEBUG
    org.springframework: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{traceId}] %-5level %logger{50} - %msg%n"

# Actuator Configuration
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always

# Knife4j Configuration
springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /v3/api-docs
knife4j:
  enable: true
  setting:
    language: en
',
    MD5('# Common Configuration for Showcase Pay Services
# Shared across all microservices

# Redis Configuration (shared)
spring:
  data:
    redis:
      host: ${REDIS_HOST:127.0.0.1}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      database: 0
      timeout: 5000ms
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
          max-wait: 3000ms

# MyBatis Plus Common Settings
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      id-type: assign_id
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# Logging Configuration
logging:
  level:
    com.showcase.pay: DEBUG
    org.springframework: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{traceId}] %-5level %logger{50} - %msg%n"

# Actuator Configuration
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always

# Knife4j Configuration
springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /v3/api-docs
knife4j:
  enable: true
  setting:
    language: en
'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    'Common configuration shared across all Showcase Pay microservices',
    'global',
    'true',
    'yaml',
    '',
    ''
) ON DUPLICATE KEY UPDATE
    `content` = VALUES(`content`),
    `md5` = VALUES(`md5`),
    `gmt_modified` = NOW();

-- ============================================
-- Gateway Service Configuration
-- data_id: showcase-pay-gateway.yaml
-- group: DEFAULT_GROUP
-- ============================================
INSERT INTO `config_info` (
    `data_id`,
    `group_id`,
    `tenant_id`,
    `app_name`,
    `content`,
    `md5`,
    `gmt_create`,
    `gmt_modified`,
    `src_user`,
    `src_ip`,
    `c_desc`,
    `c_use`,
    `effect`,
    `type`,
    `c_schema`,
    `encrypted_data_key`
) VALUES (
    'showcase-pay-gateway.yaml',
    'DEFAULT_GROUP',
    '',
    'showcase-pay-gateway',
    '# Gateway Service Configuration

server:
  port: 8080

spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      routes:
        # Payment Service Route
        - id: showcase-pay-payment
          uri: lb://showcase-pay-payment
          predicates:
            - Path=/api/payments/**
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200

        # Order Service Route
        - id: showcase-pay-order
          uri: lb://showcase-pay-order
          predicates:
            - Path=/api/orders/**
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200

        # Swagger Route for Payment Service
        - id: showcase-pay-payment-swagger
          uri: lb://showcase-pay-payment
          predicates:
            - Path=/payment/swagger-ui/**
          filters:
            - RewritePath=/payment/swagger-ui/(?<segment>.*), /swagger-ui/${segment}

        # Swagger Route for Order Service
        - id: showcase-pay-order-swagger
          uri: lb://showcase-pay-order
          predicates:
            - Path=/order/swagger-ui/**
          filters:
            - RewritePath=/order/swagger-ui/(?<segment>.*), /swagger-ui/${segment}
      globalcors:
        cors-configurations:
          ''[/**]'':
            allowedOriginPatterns: "*"
            allowedMethods: "*"
            allowedHeaders: "*"
            allowCredentials: true
            maxAge: 3600

# Logging Configuration
logging:
  level:
    com.showcase.pay: DEBUG
    org.springframework.cloud.gateway: DEBUG
',
    MD5('# Gateway Service Configuration

server:
  port: 8080

spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      routes:
        # Payment Service Route
        - id: showcase-pay-payment
          uri: lb://showcase-pay-payment
          predicates:
            - Path=/api/payments/**
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200

        # Order Service Route
        - id: showcase-pay-order
          uri: lb://showcase-pay-order
          predicates:
            - Path=/api/orders/**
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200

        # Swagger Route for Payment Service
        - id: showcase-pay-payment-swagger
          uri: lb://showcase-pay-payment
          predicates:
            - Path=/payment/swagger-ui/**
          filters:
            - RewritePath=/payment/swagger-ui/(?<segment>.*), /swagger-ui/${segment}

        # Swagger Route for Order Service
        - id: showcase-pay-order-swagger
          uri: lb://showcase-pay-order
          predicates:
            - Path=/order/swagger-ui/**
          filters:
            - RewritePath=/order/swagger-ui/(?<segment>.*), /swagger-ui/${segment}
      globalcors:
        cors-configurations:
          ''[/**]'':
            allowedOriginPatterns: "*"
            allowedMethods: "*"
            allowedHeaders: "*"
            allowCredentials: true
            maxAge: 3600

# Logging Configuration
logging:
  level:
    com.showcase.pay: DEBUG
    org.springframework.cloud.gateway: DEBUG
'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    'Gateway service configuration including routes and rate limiting',
    'global',
    'true',
    'yaml',
    '',
    ''
) ON DUPLICATE KEY UPDATE
    `content` = VALUES(`content`),
    `md5` = VALUES(`md5`),
    `gmt_modified` = NOW();

-- ============================================
-- Order Service Configuration
-- data_id: showcase-pay-order.yaml
-- group: DEFAULT_GROUP
-- ============================================
INSERT INTO `config_info` (
    `data_id`,
    `group_id`,
    `tenant_id`,
    `app_name`,
    `content`,
    `md5`,
    `gmt_create`,
    `gmt_modified`,
    `src_user`,
    `src_ip`,
    `c_desc`,
    `c_use`,
    `effect`,
    `type`,
    `c_schema`,
    `encrypted_data_key`
) VALUES (
    'showcase-pay-order.yaml',
    'DEFAULT_GROUP',
    '',
    'showcase-pay-order',
    '# Order Service Configuration

server:
  port: 8082

spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:127.0.0.1}:${MYSQL_PORT:3306}/showcase_pay?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 30000
      max-lifetime: 1800000
      connection-timeout: 30000

# MyBatis Plus Configuration
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.showcase.pay.order.entity

# RocketMQ Configuration
rocketmq:
  name-server: ${ROCKETMQ_NAMESRV:127.0.0.1:9876}
  producer:
    group: order-producer-group
    send-message-timeout: 3000
    retry-times-when-send-failed: 3
    retry-times-when-send-async-failed: 3
',
    MD5('# Order Service Configuration

server:
  port: 8082

spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:127.0.0.1}:${MYSQL_PORT:3306}/showcase_pay?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 30000
      max-lifetime: 1800000
      connection-timeout: 30000

# MyBatis Plus Configuration
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.showcase.pay.order.entity

# RocketMQ Configuration
rocketmq:
  name-server: ${ROCKETMQ_NAMESRV:127.0.0.1:9876}
  producer:
    group: order-producer-group
    send-message-timeout: 3000
    retry-times-when-send-failed: 3
    retry-times-when-send-async-failed: 3
'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    'Order service configuration including datasource and RocketMQ',
    'global',
    'true',
    'yaml',
    '',
    ''
) ON DUPLICATE KEY UPDATE
    `content` = VALUES(`content`),
    `md5` = VALUES(`md5`),
    `gmt_modified` = NOW();

-- ============================================
-- Payment Service Configuration
-- data_id: showcase-pay-payment.yaml
-- group: DEFAULT_GROUP
-- ============================================
INSERT INTO `config_info` (
    `data_id`,
    `group_id`,
    `tenant_id`,
    `app_name`,
    `content`,
    `md5`,
    `gmt_create`,
    `gmt_modified`,
    `src_user`,
    `src_ip`,
    `c_desc`,
    `c_use`,
    `effect`,
    `type`,
    `c_schema`,
    `encrypted_data_key`
) VALUES (
    'showcase-pay-payment.yaml',
    'DEFAULT_GROUP',
    '',
    'showcase-pay-payment',
    '# Payment Service Configuration

server:
  port: 8083

spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:127.0.0.1}:${MYSQL_PORT:3306}/showcase_pay?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 30000
      max-lifetime: 1800000
      connection-timeout: 30000

# MyBatis Plus Configuration
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.showcase.pay.payment.entity

# RocketMQ Configuration
rocketmq:
  name-server: ${ROCKETMQ_NAMESRV:127.0.0.1:9876}
  producer:
    group: payment-producer-group
    send-message-timeout: 3000
    retry-times-when-send-failed: 3
    retry-times-when-send-async-failed: 3

# Payment Gateway Configuration (mock values for demo)
payment:
  gateway:
    alipay:
      app-id: ${ALIPAY_APP_ID:mock_alipay_app_id}
      private-key: ${ALIPAY_PRIVATE_KEY}
      alipay-public-key: ${ALIPAY_PUBLIC_KEY}
      server-url: https://openapi.alipaydev.com/gateway.do
      notify-url: ${PAYMENT_BASE_URL:http://localhost:8083}/api/payment/callback
      return-url: ${PAYMENT_BASE_URL:http://localhost:8083}/api/payment/return
    wechat:
      app-id: ${WECHAT_APP_ID:mock_wechat_app_id}
      mch-id: ${WECHAT_MCH_ID:mock_wechat_mch_id}
      api-key: ${WECHAT_API_KEY}
      notify-url: ${PAYMENT_BASE_URL:http://localhost:8083}/api/payment/callback
      cert-path: ${WECHAT_CERT_PATH:/etc/certs/wechat/}
',
    MD5('# Payment Service Configuration

server:
  port: 8083

spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:127.0.0.1}:${MYSQL_PORT:3306}/showcase_pay?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 30000
      max-lifetime: 1800000
      connection-timeout: 30000

# MyBatis Plus Configuration
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.showcase.pay.payment.entity

# RocketMQ Configuration
rocketmq:
  name-server: ${ROCKETMQ_NAMESRV:127.0.0.1:9876}
  producer:
    group: payment-producer-group
    send-message-timeout: 3000
    retry-times-when-send-failed: 3
    retry-times-when-send-async-failed: 3

# Payment Gateway Configuration (mock values for demo)
payment:
  gateway:
    alipay:
      app-id: ${ALIPAY_APP_ID:mock_alipay_app_id}
      private-key: ${ALIPAY_PRIVATE_KEY}
      alipay-public-key: ${ALIPAY_PUBLIC_KEY}
      server-url: https://openapi.alipaydev.com/gateway.do
      notify-url: ${PAYMENT_BASE_URL:http://localhost:8083}/api/payment/callback
      return-url: ${PAYMENT_BASE_URL:http://localhost:8083}/api/payment/return
    wechat:
      app-id: ${WECHAT_APP_ID:mock_wechat_app_id}
      mch-id: ${WECHAT_MCH_ID:mock_wechat_mch_id}
      api-key: ${WECHAT_API_KEY}
      notify-url: ${PAYMENT_BASE_URL:http://localhost:8083}/api/payment/callback
      cert-path: ${WECHAT_CERT_PATH:/etc/certs/wechat/}
'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    'Payment service configuration including datasource, RocketMQ, and payment gateway settings',
    'global',
    'true',
    'yaml',
    '',
    ''
) ON DUPLICATE KEY UPDATE
    `content` = VALUES(`content`),
    `md5` = VALUES(`md5`),
    `gmt_modified` = NOW();

-- ============================================
-- Verification Query
-- ============================================
SELECT 'Nacos configuration data initialized successfully!' AS result;
SELECT `data_id`, `group_id`, `type`, `c_desc` FROM `config_info` WHERE `app_name` = 'showcase-pay' OR `app_name` LIKE 'showcase-pay-%';
