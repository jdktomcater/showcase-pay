# Showcase Pay - Payment System with Spring Cloud Microservices

A production-ready payment processing microservices platform built with Spring Cloud, featuring order management, multi-channel payment processing, distributed tracing, centralized logging, and comprehensive monitoring.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Architecture Diagram](#architecture-diagram)
- [Quick Start Guide](#quick-start-guide)
- [Service Ports](#service-ports)
- [API Documentation](#api-documentation)
- [Configuration Management with Nacos](#configuration-management-with-nacos)
- [Monitoring & Logging](#monitoring--logging)
- [Database Schema Overview](#database-schema-overview)
- [Development Guidelines](#development-guidelines)
- [Troubleshooting](#troubleshooting)

---

## Project Overview

Showcase Pay is a distributed payment system designed to demonstrate enterprise-grade microservices architecture using the Spring Cloud ecosystem. The system provides:

- **Order Management** -- Create, query, cancel, and track orders with full lifecycle support
- **Payment Processing** -- Multi-channel payment support (Alipay, WeChat Pay, Bank Card) with callback handling
- **Service Discovery & Configuration** -- Centralized service registration and dynamic configuration via Nacos
- **Message-Driven Communication** -- Asynchronous event processing via RocketMQ
- **Distributed Tracing** -- End-to-end request tracing with Apache SkyWalking
- **Centralized Logging** -- Log aggregation and analysis via ELK Stack (Elasticsearch, Logstash, Kibana)
- **API Gateway** -- Centralized routing, rate limiting, and CORS handling via Spring Cloud Gateway

The system is designed for high availability, horizontal scalability, and observability in production environments.

---

## Tech Stack

| Category | Technology | Version |
|----------|-----------|---------|
| **Runtime** | JDK | 17 |
| **Framework** | Spring Boot | 3.2.4 |
| **Cloud** | Spring Cloud | 2023.0.1 |
| **Cloud Alibaba** | Spring Cloud Alibaba | 2023.0.1.0 |
| **Database** | MySQL | 8.0 |
| **Cache** | Redis | 7.x |
| **Message Queue** | RocketMQ | 5.1.4 |
| **Service Registry/Config** | Nacos | 2.3.0 |
| **ORM** | MyBatis-Plus | 3.5.5 |
| **API Docs** | Knife4j (OpenAPI 3) | 4.4.0 |
| **Object Mapping** | MapStruct | 1.5.5.Final |
| **Utilities** | Hutool | 5.8.26 |
| **Tracing** | Apache SkyWalking | 9.7.0 |
| **Logging** | ELK Stack | 8.11.0 |
| **Containerization** | Docker / Docker Compose | -- |
| **Build Tool** | Maven | 3.x |

---

## Project Structure

```
showcase-pay/
├── showcase-pay-common/          # Shared library module
│   ├── src/main/java/
│   │   └── com.showcase.pay.common/
│   │       ├── dto/              # Common DTOs
│   │       ├── entity/           # Base entity classes
│   │       ├── exception/        # Global exception handling
│   │       ├── result/           # Unified API response wrapper
│   │       └── util/             # Utility classes
│   └── pom.xml
│
├── showcase-pay-gateway/         # API Gateway service
│   ├── src/main/java/
│   │   └── com.showcase.pay.gateway/
│   │       └── GatewayApplication.java
│   ├── src/main/resources/
│   │   └── application.yml       # Gateway routing & config
│   └── pom.xml
│
├── showcase-pay-order/           # Order management microservice
│   ├── src/main/java/
│   │   └── com.showcase.pay.order/
│   │       ├── controller/       # REST endpoints
│   │       ├── service/          # Business logic
│   │       ├── mapper/           # MyBatis mappers
│   │       ├── entity/           # Database entities
│   │       ├── dto/              # Request/Response DTOs
│   │       └── OrderApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml       # Service configuration
│   │   └── mapper/               # MyBatis XML mappers
│   └── pom.xml
│
├── showcase-pay-payment/         # Payment processing microservice
│   ├── src/main/java/
│   │   └── com.showcase.pay.payment/
│   │       ├── controller/       # REST endpoints
│   │       ├── service/          # Business logic & gateway integration
│   │       ├── mapper/           # MyBatis mappers
│   │       ├── entity/           # Database entities
│   │       ├── dto/              # Request/Response DTOs
│   │       └── PaymentApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml       # Service configuration
│   │   └── mapper/               # MyBatis XML mappers
│   └── pom.xml
│
├── docker-compose.yml            # Infrastructure & service orchestration
├── Dockerfile.service            # Shared Dockerfile for all services
├── pom.xml                       # Parent POM with dependency management
│
├── docker/
│   └── logstash/
│       └── logstash.conf         # Logstash pipeline configuration
│
├── scripts/
│   ├── init-nacos.sh             # Initialize Nacos configurations
│   ├── start-all.sh              # Start all infrastructure & services
│   ├── stop-all.sh               # Stop all services
│   ├── build.sh                  # Build all modules
│   ├── start.sh                  # Start individual services
│   ├── stop.sh                   # Stop individual services
│   └── rebuild-services.sh       # Rebuild and restart business services
│
└── sql/
    ├── init.sql                  # Database initialization with sample data
    ├── schema.sql                # Database schema definitions
    └── nacos_config.sql          # Nacos configuration database schema
```

### Module Descriptions

| Module | Description |
|--------|-------------|
| **showcase-pay-common** | Shared library containing common DTOs, base entities, unified response wrapper (`Result<T>`), global exception handling, and utility classes. Used as a dependency by all other modules. |
| **showcase-pay-gateway** | Spring Cloud Gateway service providing centralized routing, load balancing (via Spring Cloud LoadBalancer), rate limiting (Redis-based), CORS configuration, and Swagger UI aggregation for downstream services. |
| **showcase-pay-order** | Order management microservice handling order creation, querying, cancellation, and status tracking. Integrates with MySQL for persistence, Redis for caching, and RocketMQ for async event publishing. |
| **showcase-pay-payment** | Payment processing microservice supporting multiple payment channels (Alipay, WeChat Pay, Bank Card). Handles payment creation, gateway integration, callback processing, refunds, and cancellation. |

---

## Architecture Diagram

```
                           ┌─────────────────────────────────────────────┐
                           │             Client / Frontend                │
                           └──────────────────────┬──────────────────────┘
                                                  │
                                                  ▼
                           ┌──────────────────────────────────────────────┐
                           │          Spring Cloud Gateway (8080)         │
                           │  ┌────────────────────────────────────────┐  │
                           │  │  Routes:                               │  │
                           │  │  /api/orders/**  -> Order Service      │  │
                           │  │  /api/payments/** -> Payment Service   │  │
                           │  │  Rate Limiting via Redis               │  │
                           │  └────────────────────────────────────────┘  │
                           └──────┬───────────────────────┬───────────────┘
                                  │                       │
                    ┌─────────────▼─────────┐   ┌─────────▼──────────────┐
                    │   Order Service (8082) │   │ Payment Service (8083) │
                    │                        │   │                        │
                    │  - Order CRUD          │   │  - Payment creation    │
                    │  - Order query         │   │  - Gateway integration │
                    │  - Order cancellation  │   │  - Callback handling   │
                    │  - Status tracking     │   │  - Refund processing   │
                    │                        │   │                        │
                    │  ┌──────────────────┐  │   │  ┌──────────────────┐  │
                    │  │  MyBatis-Plus    │  │   │  │  MyBatis-Plus    │  │
                    │  │  Redis Cache     │  │   │  │  Redis Cache     │  │
                    │  │  RocketMQ Prod.  │  │   │  │  RocketMQ Prod.  │  │
                    │  └──────────────────┘  │   │  └──────────────────┘  │
                    └───────────┬────────────┘   └──────────┬─────────────┘
                                │                          │
              ┌─────────────────┼──────────────────────────┼─────────────────┐
              │                 │                          │                 │
              ▼                 ▼                          ▼                 ▼
    ┌──────────────────┐ ┌──────────────┐ ┌──────────────────────┐ ┌──────────────┐
    │   MySQL 8.0      │ │    Redis 7   │ │    RocketMQ 5.1.4    │ │    Nacos     │
    │                  │ │              │ │                      │ │   2.3.0      │
    │  - t_order       │ │  - Rate      │ │  - NameServer: 9876  │ │              │
    │  - t_payment_    │ │    limiting  │ │  - Broker: 10911     │ │  - Service   │
    │    record        │ │  - Session   │ │  - Console: 8090     │ │    Discovery │
    │  - t_payment_    │ │    cache     │ │                      │ │  - Config    │
    │    channel       │ │              │ │                      │ │    Center    │
    └──────────────────┘ └──────────────┘ └──────────────────────┘ └──────────────┘

    ┌──────────────────────────────────┐  ┌──────────────────────────────────┐
    │     ELK Stack (8.11.0)           │  │    SkyWalking (9.7.0)            │
    │                                  │  │                                  │
    │  Elasticsearch: 9200             │  │  OAP: 11800 / 12800              │
    │  Logstash: 4560 / 5044           │  │  UI: 8085                        │
    │  Kibana: 5601                    │  │                                  │
    │                                  │  │  Distributed tracing             │
    │  Centralized log aggregation     │  │  Topology & performance analysis │
    └──────────────────────────────────┘  └──────────────────────────────────┘
```

### Service Communication Flow

```
  Order Service                    Payment Service
       │                                 │
       │  1. Create Order                │
       │◄────────────────                │
       │                                 │
       │  2. Create Payment              │
       │────────────────►                │
       │                                 │
       │  3. Process payment via gateway │
       │                                 │────────────► Alipay/WeChat
       │                                 │
       │  4. Payment callback            │
       │                                 │◄──────────── Alipay/WeChat
       │                                 │
       │  5. Publish payment event       │
       │  via RocketMQ                   │
       │◄────────────────                │
       │                                 │
       │  6. Update order status         │
       │                                 │
```

---

## Quick Start Guide

### Prerequisites

Ensure the following tools are installed on your system:

| Tool | Minimum Version | Verify Command |
|------|----------------|----------------|
| JDK | 17 | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| Docker | 20.10+ | `docker --version` |
| Docker Compose | 2.0+ | `docker compose version` |
| Git | 2.x | `git --version` |

**System Requirements:**
- Minimum 8 GB RAM (16 GB recommended for running all services)
- 20 GB free disk space
- macOS, Linux, or Windows (with WSL2)

### Step 1: Clone and Build the Project

```bash
# Clone the repository
git clone <repository-url>
cd showcase-pay

# Build all modules (skip tests for faster build)
mvn clean package -DskipTests
```

### Step 2: Start Infrastructure Services

```bash
# Start all infrastructure and application services
./scripts/start-all.sh
```

Alternatively, start services step by step:

```bash
# Start core infrastructure (MySQL, Redis, Nacos, RocketMQ)
docker-compose up -d mysql redis nacos rocketmq-namesrv

# Wait for services to be healthy (approx. 30 seconds)
sleep 30

# Start RocketMQ broker and console
docker-compose up -d rocketmq-broker rocketmq-console

# Start ELK stack
docker-compose up -d elasticsearch logstash kibana

# Start SkyWalking
docker-compose up -d skywalking-oap skywalking-ui

# Start application services
docker-compose up -d gateway payment order
```

### Step 3: Initialize Nacos Configurations

After Nacos is running, import the service configurations:

```bash
# Run the Nacos initialization script
./scripts/init-nacos.sh
```

This script imports the following configurations into Nacos:
- `common-config.yaml` -- Shared configuration (Redis, common settings)
- `gateway-config.yaml` -- Gateway-specific configuration
- `order-config.yaml` -- Order service configuration
- `payment-config.yaml` -- Payment service configuration

You can also manually configure services via the Nacos console at `http://localhost:8848/nacos` (credentials: `nacos/nacos`).

### Step 4: Verify Services

Check that all services are running:

```bash
# Check Docker containers
docker-compose ps

# Verify service health endpoints
curl http://localhost:8080/actuator/health     # Gateway
curl http://localhost:8082/actuator/health     # Order Service
curl http://localhost:8083/actuator/health     # Payment Service
```

Access the management consoles:

| Service | URL                                   | Credentials |
|---------|---------------------------------------|-------------|
| Nacos Console | http://localhost:8848/nacos           | nacos / nacos |
| RocketMQ Console | http://localhost:8090                 | -- |
| Kibana | http://localhost:5601                 | -- |
| SkyWalking UI | http://localhost:8085                 | -- |
| Knife4j (Order API Docs) | http://localhost:8082/swagger-ui.html | -- |
| Knife4j (Payment API Docs) | http://localhost:8081/swagger-ui.html | -- |

### Stopping Services

```bash
# Stop all services
./scripts/stop-all.sh

# Or using docker-compose directly
docker-compose down
```

### Rebuilding Services After Code Changes

After modifying Java code or configuration files, rebuild and restart the affected services:

```bash
# Rebuild all business services (gateway, payment, order)
./scripts/rebuild-services.sh

# Rebuild specific services only
./scripts/rebuild-services.sh gateway payment
./scripts/rebuild-services.sh order
```

This script automatically:
1. Builds Java JARs with Maven
2. Rebuilds Docker images
3. Restarts the services
4. Checks service health status

---

## Admin Panel

The system includes a web-based admin panel built with React + Ant Design for managing orders, payments, and monitoring services.

### Features

- **Dashboard** -- Overview of order/payment statistics and recent activity
- **Order Management** -- View, search, and cancel orders with detailed information
- **Payment Management** -- View, search, cancel payments and process refunds
- **Service Health** -- Monitor the health status of all infrastructure and application services

### Access

| Panel | URL | Description |
|-------|-----|-------------|
| Admin Panel | http://localhost:3000 | Web-based admin UI |
| API Gateway | http://localhost:8080 | API entry point |

### Tech Stack

| Category | Technology |
|----------|-----------|
| **Framework** | React 18 |
| **Build Tool** | Vite 5 |
| **Language** | TypeScript |
| **UI Library** | Ant Design 5 |
| **HTTP Client** | Axios |
| **Routing** | React Router 6 |
| **Container** | Nginx (Alpine) |

### Development

```bash
# Install dependencies
cd showcase-pay-admin
npm install

# Start dev server (proxies API to localhost:8080)
npm run dev

# Build for production
npm run build
```

---

## Service Ports

| Service | Port | Description |
|---------|------|-------------|
| **Admin Panel** | 3000 | Web-based admin management UI |
| **API Gateway** | 8080 | Central entry point for all API requests |
| **Payment Service** | 8083 | Payment processing microservice (internal port; mapped to 8081 in docker-compose) |
| **Order Service** | 8082 | Order management microservice |
| **Nacos** | 8848 | Service registry and configuration center |
| **Nacos gRPC** | 9848 | Nacos gRPC port for client communication |
| **RocketMQ NameServer** | 9876 | RocketMQ name server |
| **RocketMQ Broker** | 10909, 10911, 10912 | RocketMQ broker ports |
| **RocketMQ Console** | 8090 | RocketMQ web console |
| **Elasticsearch** | 9200, 9300 | Search and analytics engine |
| **Logstash** | 4560, 5044 | Log processing pipeline |
| **Kibana** | 5601 | Log visualization dashboard |
| **SkyWalking OAP** | 11800, 12800 | Observability analysis platform |
| **SkyWalking UI** | 8085 | Distributed tracing dashboard |
| **MySQL** | 3306 | Relational database |
| **Redis** | 6379 | In-memory cache and data store |

---

## API Documentation

All REST APIs are documented via Knife4j (Swagger UI). Access interactive API documentation at:

- **Order Service API**: http://localhost:8082/swagger-ui.html
- **Payment Service API**: http://localhost:8081/swagger-ui.html

Alternatively, access APIs through the Gateway at `http://localhost:8080`.

### Order APIs

#### Create an Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1001,
    "amount": 99.90,
    "currency": "CNY",
    "subject": "Premium Subscription",
    "description": "Monthly premium subscription plan"
  }'
```

**Response:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1234567890,
    "orderNo": "ORD20260404000003",
    "userId": 1001,
    "amount": 99.90,
    "currency": "CNY",
    "status": "CREATED",
    "subject": "Premium Subscription",
    "createTime": "2026-04-04T10:00:00"
  }
}
```

#### Get Order Details

```bash
curl http://localhost:8080/api/orders/ORD20260404000003
```

#### Query User Orders (Paginated)

```bash
curl "http://localhost:8080/api/orders/user/1001?pageNum=1&pageSize=10"
```

#### Cancel an Order

```bash
curl -X POST http://localhost:8080/api/orders/ORD20260404000003/cancel
```

### Payment APIs

#### Create a Payment

```bash
curl -X POST http://localhost:8080/api/payments/create \
  -H "Content-Type: application/json" \
  -d '{
    "orderNo": "ORD20260404000003",
    "userId": 1001,
    "paymentMethod": "ALIPAY",
    "amount": 99.90,
    "currency": "CNY",
    "subject": "Premium Subscription Payment"
  }'
```

**Response:**
```json
{
  "code": 200,
  "message": "Payment created successfully",
  "data": {
    "paymentNo": "PAY20260404000001",
    "orderNo": "ORD20260404000003",
    "status": "PENDING",
    "gatewayUrl": "https://openapi.alipaydev.com/gateway.do?...",
    "qrCode": "https://qr.alipay.com/..."
  }
}
```

#### Query Payment Details

```bash
curl -X POST http://localhost:8080/api/payments/query \
  -H "Content-Type: application/json" \
  -d '{
    "paymentNo": "PAY20260404000001"
  }'
```

#### Query Payment Status from Gateway

```bash
curl http://localhost:8080/api/payments/status/PAY20260404000001
```

#### Query User Payments (Paginated)

```bash
curl "http://localhost:8080/api/payments/list/1001?pageNum=1&pageSize=10"
```

#### Cancel a Payment

```bash
curl -X POST http://localhost:8080/api/payments/cancel/PAY20260404000001
```

#### Process a Refund

```bash
curl -X POST http://localhost:8080/api/payments/refund \
  -H "Content-Type: application/json" \
  -d '{
    "paymentNo": "PAY20260404000001",
    "refundAmount": 99.90,
    "reason": "Customer requested refund"
  }'
```

#### Payment Callback (for gateway simulation)

```bash
curl -X POST http://localhost:8080/api/payments/callback \
  -H "Content-Type: application/json" \
  -d '{
    "paymentNo": "PAY20260404000001",
    "status": "SUCCESS",
    "transactionId": "2026040410000001",
    "payTime": "2026-04-04T10:05:00"
  }'
```

---

## Configuration Management with Nacos

Nacos serves as the centralized service discovery and configuration management platform for all microservices.

### How It Works

1. **Service Registration**: Each microservice registers itself with Nacos on startup using `spring-cloud-starter-alibaba-nacos-discovery`.
2. **Configuration Pull**: Services fetch their configuration from Nacos using `spring-cloud-starter-alibaba-nacos-config`.
3. **Dynamic Refresh**: Configuration changes in Nacos are pushed to running services without restart (via `refresh: true` in shared configs).

### Configuration Files

| Data ID | Group | Description |
|---------|-------|-------------|
| `common-config.yaml` | DEFAULT_GROUP | Shared configuration: Redis settings, common properties |
| `gateway-config.yaml` | DEFAULT_GROUP | Gateway routes, rate limiting, CORS settings |
| `order-config.yaml` | DEFAULT_GROUP | Order service: datasource, MyBatis, RocketMQ |
| `payment-config.yaml` | DEFAULT_GROUP | Payment service: datasource, payment gateway credentials |

### Managing Configurations via Nacos Console

1. Open http://localhost:8848/nacos and log in with `nacos`/`nacos`.
2. Navigate to **Configuration Management** > **Configuration List**.
3. Click the **+** button to create or edit a configuration.
4. Set the `Data ID`, `Group`, `Format` (YAML), and paste the configuration content.
5. Click **Publish** to apply changes.

### Environment Variables

Services support environment variable overrides for key settings:

| Variable | Default | Description |
|----------|---------|-------------|
| `NACOS_ADDR` | 127.0.0.1:8848 | Nacos server address |
| `NACOS_NAMESPACE` | (empty) | Nacos namespace ID |
| `NACOS_GROUP` | DEFAULT_GROUP | Nacos configuration group |
| `MYSQL_HOST` | 127.0.0.1 | MySQL host |
| `MYSQL_PORT` | 3306 | MySQL port |
| `MYSQL_USERNAME` | root | MySQL username |
| `MYSQL_PASSWORD` | required | MySQL password |
| `REDIS_HOST` | 127.0.0.1 | Redis host |
| `REDIS_PORT` | 6379 | Redis port |
| `REDIS_PASSWORD` | (empty) | Redis password |
| `ROCKETMQ_NAMESRV` | 127.0.0.1:9876 | RocketMQ NameServer address |
| `ALIPAY_APP_ID` | (mock) | Alipay application ID |
| `ALIPAY_PRIVATE_KEY` | required | Alipay private key |
| `ALIPAY_PUBLIC_KEY` | required | Alipay public key |
| `WECHAT_APP_ID` | (mock) | WeChat application ID |
| `WECHAT_API_KEY` | required | WeChat payment API key |

---

## Monitoring & Logging

### ELK Stack (Elasticsearch, Logstash, Kibana)

The ELK stack provides centralized log collection, processing, and visualization.

**Architecture:**
```
  Application Services
         │
         │ (Logback JSON format)
         ▼
  Logstash (4560/5044)
         │
         │ (Processing & enrichment)
         ▼
  Elasticsearch (9200)
         │
         ▼
  Kibana (5601) -- Visualization
```

**Configuration:**
- Services use `logstash-logback-encoder` to emit structured JSON logs.
- Logstash (`docker/logstash/logstash.conf`) ingests logs and forwards them to Elasticsearch.
- Kibana provides a web UI for searching and visualizing logs.

**Access Kibana**: http://localhost:5601

**Sample Kibana Queries:**
- Find all ERROR logs: `level: "ERROR"`
- Logs for a specific service: `service.name: "showcase-pay-order"`
- Trace a specific request: `traceId: "<trace-id>"`

### Apache SkyWalking

SkyWalking provides distributed tracing, service topology visualization, and performance monitoring.

**Features:**
- **Service Topology**: Visual map of service dependencies and communication paths.
- **Distributed Tracing**: End-to-end request tracing across all microservices.
- **Performance Metrics**: Response time, throughput, error rates per service and endpoint.
- **Database Monitoring**: SQL query analysis and slow query detection.

**Configuration:**
- Services include `apm-toolkit-logback-1.x` and `apm-toolkit-trace` dependencies.
- SkyWalking agent is injected via JVM arguments (configured in Dockerfile.service).
- Trace IDs are propagated through logs via the `%X{traceId}` pattern.

**Access SkyWalking UI**: http://localhost:8085

### Actuator Endpoints

Each service exposes Spring Boot Actuator endpoints for health checks and metrics:

| Endpoint | URL | Description |
|----------|-----|-------------|
| Health | `/actuator/health` | Service health status with details |
| Info | `/actuator/info` | Application information |
| Metrics | `/actuator/metrics` | Runtime metrics |
| Gateway Routes | `/actuator/gateway` | Gateway route info (Gateway only) |

---

## Database Schema Overview

The system uses MySQL 8.0 as the primary datastore. The database `showcase_pay` is initialized automatically via `sql/init.sql`.

### Tables

#### t_order

Stores order records with lifecycle tracking.

| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT | Primary key |
| `order_no` | VARCHAR(64) | Unique order number (business key) |
| `user_id` | BIGINT | User identifier |
| `amount` | DECIMAL(12,2) | Order amount |
| `currency` | VARCHAR(10) | Currency code (default: CNY) |
| `status` | VARCHAR(32) | CREATED, PENDING_PAYMENT, PAID, SHIPPED, COMPLETED, CANCELLED, REFUNDED |
| `subject` | VARCHAR(256) | Order title |
| `description` | TEXT | Order details |
| `pay_time` | DATETIME | When payment was completed |
| `expire_time` | DATETIME | Order expiration time |
| `extra_data` | JSON | Additional metadata |
| `version` | INT | Optimistic locking version |
| `created_at` | DATETIME | Creation timestamp |
| `updated_at` | DATETIME | Last update timestamp |
| `deleted` | TINYINT | Logical delete flag (0=active, 1=deleted) |

#### t_payment_record

Stores payment transaction records.

| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT | Primary key |
| `payment_no` | VARCHAR(64) | Unique payment number (business key) |
| `order_no` | VARCHAR(64) | Associated order number |
| `user_id` | BIGINT | User identifier |
| `payment_method` | VARCHAR(32) | ALIPAY, WECHAT, CARD, CREDIT, BALANCE, APPLE_PAY |
| `amount` | DECIMAL(12,2) | Payment amount |
| `currency` | VARCHAR(10) | Currency code (default: CNY) |
| `status` | VARCHAR(32) | PENDING, PROCESSING, SUCCESS, FAILED, CANCELLED, REFUNDED, TIMEOUT |
| `transaction_id` | VARCHAR(128) | Third-party transaction ID |
| `gateway_response` | JSON | Gateway response payload |
| `callback_time` | DATETIME | Callback notification timestamp |
| `version` | INT | Optimistic locking version |
| `created_at` | DATETIME | Creation timestamp |
| `updated_at` | DATETIME | Last update timestamp |
| `deleted` | TINYINT | Logical delete flag |

#### t_payment_channel

Stores payment channel configurations.

| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT | Primary key |
| `channel_code` | VARCHAR(32) | Channel identifier: ALIPAY, WECHAT, CARD |
| `channel_name` | VARCHAR(64) | Display name |
| `status` | TINYINT | 0=disabled, 1=enabled |
| `config` | JSON | Channel-specific configuration (merchant ID, keys, gateway URLs) |
| `version` | INT | Optimistic locking version |
| `created_at` | DATETIME | Creation timestamp |
| `updated_at` | DATETIME | Last update timestamp |

### Indexes

All tables include indexes on frequently queried columns:
- `order_no`, `payment_no` -- unique business keys
- `user_id` -- user-based queries
- `status` -- status filtering
- `transaction_id` -- gateway callback lookups
- `created_at` -- time-range queries

---

## Development Guidelines

### Building the Project

```bash
# Full build with tests
mvn clean install

# Build without tests (faster)
mvn clean package -DskipTests

# Build a specific module
mvn clean package -pl showcase-pay-order -am
```

### Running Services Locally

To run individual services outside Docker (for development/debugging):

```bash
# Ensure infrastructure is running (MySQL, Redis, Nacos, RocketMQ)
docker-compose up -d mysql redis nacos rocketmq-namesrv rocketmq-broker

# Run Order Service
cd showcase-pay-order
mvn spring-boot:run

# Run Payment Service
cd showcase-pay-payment
mvn spring-boot:run

# Run Gateway
cd showcase-pay-gateway
mvn spring-boot:run
```

### Code Style

- Follow standard Java naming conventions (camelCase for variables/methods, PascalCase for classes).
- Use Lombok annotations (`@Data`, `@Builder`, `@RequiredArgsConstructor`) to reduce boilerplate.
- Use MapStruct for DTO-to-entity conversions.
- Use Hutool for common utility operations (date formatting, string handling, etc.).
- All API responses should use the unified `Result<T>` wrapper.

### API Design Principles

- All endpoints return `Result<T>` with `code`, `message`, and `data` fields.
- Use `@Valid` and Jakarta Validation annotations for request validation.
- Document all endpoints with OpenAPI 3 annotations (`@Operation`, `@Parameter`, `@Tag`).
- Use `@RestController` and `@RequestMapping` with resource-oriented paths.

### Error Handling

- Use the global exception handler in the common module.
- Throw business exceptions with specific error codes.
- Log errors with trace IDs for distributed tracing correlation.

### Testing

```bash
# Run all tests
mvn test

# Run tests for a specific module
mvn test -pl showcase-pay-order

# Run a single test class
mvn test -Dtest=OrderServiceTest
```

### Database Migrations

- Schema changes should be added to `sql/schema.sql`.
- Seed data should be added to `sql/init.sql`.
- For production, use a migration tool like Flyway or Liquibase (not included in this showcase).

---

## Troubleshooting

### Services Fail to Start

**Problem**: Container exits immediately after startup.

```bash
# Check container logs
docker logs showcase-pay-gateway
docker logs showcase-pay-order
docker logs showcase-pay-payment

# Check service health
docker-compose ps
```

**Common causes:**
- Nacos is not running or not accessible. Verify with: `curl http://localhost:8848/nacos`
- MySQL/Redis connection failure. Check credentials in environment variables.
- Port conflicts. Ensure no other services are using the required ports.

### Nacos Configuration Issues

**Problem**: Services cannot fetch configuration from Nacos.

```bash
# Verify Nacos is running
curl http://localhost:8848/nacos/v1/cs/configs?dataId=common-config.yaml&group=DEFAULT_GROUP

# Check Nacos logs
docker logs showcase-pay-nacos

# Re-initialize configurations
./scripts/init-nacos.sh
```

### RocketMQ Connection Issues

**Problem**: Producer fails to send messages.

```bash
# Verify NameServer is running
docker logs showcase-pay-rocketmq-namesrv

# Verify Broker is connected to NameServer
docker logs showcase-pay-rocketmq-broker

# Access RocketMQ console
open http://localhost:8090
```

### Database Connection Issues

**Problem**: Cannot connect to MySQL.

```bash
# Check MySQL is running
docker exec showcase-pay-mysql mysqladmin ping -u root -proot

# Verify database exists
docker exec showcase-pay-mysql mysql -u root -proot -e "SHOW DATABASES;"

# Re-initialize the database
docker exec -i showcase-pay-mysql mysql -u root -proot < sql/init.sql
```

### High Memory Usage

The full stack (all services + infrastructure) can consume 6-8 GB of RAM. If experiencing memory pressure:

1. Increase Docker memory limits in Docker Desktop settings.
2. Reduce JVM heap sizes in `docker-compose.yml` (`JAVA_OPTS=-Xms256m -Xmx512m`).
3. Stop non-essential services (e.g., SkyWalking, ELK) when not needed.

### Logging Issues

**Problem**: Logs not appearing in Kibana.

```bash
# Check Logstash is receiving logs
docker logs showcase-pay-logstash

# Check Elasticsearch is running
curl http://localhost:9200/_cluster/health

# Verify Logstash pipeline config
cat docker/logstash/logstash.conf
```

### SkyWalking Not Collecting Traces

**Problem**: SkyWalking UI shows no data.

1. Verify the SkyWalking agent is attached to the JVM:
   ```bash
   docker exec showcase-pay-order ps aux | grep skywalking
   ```
2. Check the OAP server is running:
   ```bash
   docker logs showcase-pay-skywalking-oap
   ```
3. Verify the agent configuration points to the correct OAP address (`SW_AGENT_COLLECTOR_BACKEND_SERVICES=skywalking-oap:11800`).

### Port Conflicts

If any required port is already in use:

1. Identify the conflicting process:
   ```bash
   lsof -i :8080   # Replace with the conflicting port
   ```
2. Either stop the conflicting process or modify the port mapping in `docker-compose.yml`.

### Clean Restart

If the system is in an inconsistent state:

```bash
# Stop all services
docker-compose down

# Remove volumes (WARNING: this deletes all data)
docker-compose down -v

# Rebuild and restart
mvn clean package -DskipTests
docker-compose up -d
./scripts/init-nacos.sh
```

---

## License

This project is provided as a showcase/demo for educational and reference purposes.

## Contributing

Contributions are welcome. Please follow these guidelines:

1. Fork the repository and create a feature branch.
2. Write clean, well-documented code following the existing code style.
3. Ensure all tests pass before submitting a pull request.
4. Update documentation if adding new features or changing existing behavior.
