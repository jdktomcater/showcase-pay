#!/bin/bash
# ============================================
# Showcase Pay - Stop Script
# Stop all services
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

# Check if docker-compose.yml exists
check_compose_file() {
    if [ ! -f "$PROJECT_DIR/docker-compose.yml" ]; then
        log_error "docker-compose.yml not found in $PROJECT_DIR"
        exit 1
    fi
}

# Stop services
stop_services() {
    log_info "Stopping all services..."
    cd "$PROJECT_DIR"

    # Stop application services first
    docker-compose stop gateway payment order 2>/dev/null || true

    # Stop middleware services
    docker-compose stop skywalking-ui skywalking-oap kibana logstash elasticsearch 2>/dev/null || true
    docker-compose stop rocketmq-console rocketmq-broker rocketmq-namesrv 2>/dev/null || true

    # Stop infrastructure
    docker-compose stop nacos redis mysql 2>/dev/null || true

    log_info "All services stopped."
}

# Optional: Remove containers
cleanup_containers() {
    if [ "${1:-}" = "--clean" ]; then
        log_info "Removing containers..."
        cd "$PROJECT_DIR"
        docker-compose down
        log_info "Containers removed."
    fi
}

# Main execution
main() {
    log_info "============================================"
    log_info "Showcase Pay - Stopping Services"
    log_info "============================================"

    check_compose_file
    stop_services
    cleanup_containers "${1:-}"

    log_info ""
    log_info "All services stopped successfully."
    log_info ""
}

main "$@"
