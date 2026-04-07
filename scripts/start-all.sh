#!/bin/bash

# ============================================
# Showcase Pay - Startup Script
# ============================================

echo "Starting Showcase Pay Infrastructure Services..."

# Start infrastructure services first
docker-compose up -d mysql redis nacos rocketmq-namesrv

echo "Waiting for infrastructure services to be ready..."
sleep 30

# Start RocketMQ and monitoring
docker-compose up -d rocketmq-broker rocketmq-console

echo "Waiting for RocketMQ to be ready..."
sleep 15

# Start ELK stack
docker-compose up -d elasticsearch logstash kibana

echo "Waiting for Elasticsearch to be ready..."
sleep 20

# Start SkyWalking
docker-compose up -d skywalking-oap skywalking-ui

echo "Waiting for SkyWalking to be ready..."
sleep 15

# Build and start application services
echo "Building application services..."
mvn clean package -DskipTests

echo "Starting application services..."
docker-compose up -d gateway payment order admin

echo ""
echo "============================================"
echo "Showcase Pay Services Started Successfully!"
echo "============================================"
echo ""
echo "Service URLs:"
echo "  - API Gateway:        http://localhost:8080"
echo "  - Payment Service:    http://localhost:8081"
echo "  - Order Service:      http://localhost:8082"
echo "  - Nacos Console:      http://localhost:8848/nacos"
echo "  - RocketMQ Console:   http://localhost:8090"
echo "  - Kibana:             http://localhost:5601"
echo "  - SkyWalking UI:      http://localhost:8085"
echo ""
echo "Default Credentials:"
echo "  - MySQL: root/root"
echo "  - Nacos: nacos/nacos"
echo ""
