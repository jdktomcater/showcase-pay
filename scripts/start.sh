#!/bin/bash
# ============================================
# Showcase Pay - Start Script
# Start all services with docker-compose
# ============================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if docker is running
check_docker() {
    if ! docker info >/dev/null 2>&1; then
        log_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Check if docker-compose.yml exists
check_compose_file() {
    if [ ! -f "$PROJECT_DIR/docker-compose.yml" ]; then
        log_error "docker-compose.yml not found in $PROJECT_DIR"
        exit 1
    fi
}

# Start infrastructure services
start_infrastructure() {
    log_info "Starting infrastructure services..."
    cd "$PROJECT_DIR"
    docker-compose up -d mysql redis nacos rocketmq-namesrv

    log_info "Waiting for MySQL and Redis to be ready..."
    sleep 15

    log_info "Starting RocketMQ broker and console..."
    docker-compose up -d rocketmq-broker rocketmq-console
    sleep 10

    log_info "Starting ELK stack..."
    docker-compose up -d elasticsearch logstash kibana
    sleep 20

    log_info "Starting SkyWalking..."
    docker-compose up -d skywalking-oap skywalking-ui
    sleep 10
}

# Start application services
start_applications() {
    log_info "Building application services..."
    mvn clean package -DskipTests -f "$PROJECT_DIR/pom.xml"

    log_info "Starting application services..."
    cd "$PROJECT_DIR"
    docker-compose up -d gateway payment order
}

# Main execution
main() {
    log_info "============================================"
    log_info "Showcase Pay - Starting Services"
    log_info "============================================"

    check_docker
    check_compose_file

    # Check if services are already running
    if docker-compose ps | grep -q "Up"; then
        log_warn "Some services are already running."
        read -p "Do you want to stop them first? (y/n): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            docker-compose down
            sleep 5
        fi
    fi

    start_infrastructure
    start_applications

    log_info ""
    log_info "============================================"
    log_info "Showcase Pay Services Started Successfully!"
    log_info "============================================"
    log_info ""
    log_info "Service URLs:"
    log_info "  - API Gateway:        http://localhost:8080"
    log_info "  - Payment Service:    http://localhost:8081"
    log_info "  - Order Service:      http://localhost:8082"
    log_info "  - Nacos Console:      http://localhost:8848/nacos (nacos/nacos)"
    log_info "  - RocketMQ Console:   http://localhost:8090"
    log_info "  - Kibana:             http://localhost:5601"
    log_info "  - SkyWalking UI:      http://localhost:8085"
    log_info ""
}

main "$@"
