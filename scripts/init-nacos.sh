#!/bin/bash
# ============================================
# Showcase Pay - Initialize Nacos Configurations
# Import configurations to Nacos server
# ============================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
NACOS_DIR="$PROJECT_DIR/docker/nacos"

# Configuration
NACOS_ADDR="${NACOS_ADDR:-http://localhost:8848}"
NACOS_USERNAME="${NACOS_USERNAME:-nacos}"
NACOS_PASSWORD="${NACOS_PASSWORD:-nacos}"
NAMESPACE="${NAMESPACE:-}"  # Optional namespace ID

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

log_debug() {
    echo -e "${BLUE}[DEBUG]${NC} $1"
}

# Check if Nacos is running
check_nacos() {
    log_info "Checking Nacos connectivity..."
    if ! curl -s -f "$NACOS_ADDR/nacos/" >/dev/null 2>&1; then
        log_error "Nacos is not accessible at $NACOS_ADDR"
        log_error "Please ensure Nacos is running: docker-compose up -d nacos"
        exit 1
    fi
    log_info "Nacos is accessible at $NACOS_ADDR"
}

# Get Nacos auth token
get_nacos_token() {
    local response
    response=$(curl -s -X POST "$NACOS_ADDR/nacos/v1/auth/login" \
        -d "username=$NACOS_USERNAME" \
        -d "password=$NACOS_PASSWORD")

    NACOS_TOKEN=$(echo "$response" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

    if [ -z "$NACOS_TOKEN" ]; then
        log_warn "Failed to get Nacos auth token, proceeding without authentication"
        AUTH_HEADER=""
    else
        AUTH_HEADER="Authorization: Bearer $NACOS_TOKEN"
    fi
}

# Import a configuration file to Nacos
import_config() {
    local data_id="$1"
    local group="$2"
    local file_path="$3"

    if [ ! -f "$file_path" ]; then
        log_error "Configuration file not found: $file_path"
        return 1
    fi

    local content
    content=$(cat "$file_path")

    log_info "Importing $data_id (Group: $group)..."

    local response
    response=$(curl -s -X POST "$NACOS_ADDR/nacos/v1/cs/configs" \
        -H "$AUTH_HEADER" \
        -d "dataId=$data_id" \
        -d "group=$group" \
        -d "tenant=$NAMESPACE" \
        -d "type=yaml" \
        -d "content=$content")

    if echo "$response" | grep -q "true"; then
        log_info "  Successfully imported $data_id"
        return 0
    else
        log_error "  Failed to import $data_id: $response"
        return 1
    fi
}

# Import all configurations
import_all_configs() {
    local success_count=0
    local fail_count=0

    # Common configuration
    if import_config "common-config.yaml" "DEFAULT_GROUP" "$NACOS_DIR/common-config.yaml"; then
        ((success_count++))
    else
        ((fail_count++))
    fi

    # Gateway configuration
    if import_config "gateway-config.yaml" "DEFAULT_GROUP" "$NACOS_DIR/gateway-config.yaml"; then
        ((success_count++))
    else
        ((fail_count++))
    fi

    # Order configuration
    if import_config "order-config.yaml" "DEFAULT_GROUP" "$NACOS_DIR/order-config.yaml"; then
        ((success_count++))
    else
        ((fail_count++))
    fi

    # Payment configuration
    if import_config "payment-config.yaml" "DEFAULT_GROUP" "$NACOS_DIR/payment-config.yaml"; then
        ((success_count++))
    else
        ((fail_count++))
    fi

    echo ""
    log_info "============================================"
    log_info "Configuration Import Summary"
    log_info "============================================"
    log_info "  Successful: $success_count"
    if [ $fail_count -gt 0 ]; then
        log_error "  Failed: $fail_count"
        exit 1
    else
        log_info "  Failed: 0"
    fi
    log_info "============================================"
}

# Main execution
main() {
    log_info "============================================"
    log_info "Showcase Pay - Initialize Nacos Configurations"
    log_info "============================================"
    log_info "  Nacos Address: $NACOS_ADDR"
    log_info "  Namespace: ${NAMESPACE:-default}"
    log_info "============================================"
    echo ""

    # Check if config directory exists
    if [ ! -d "$NACOS_DIR" ]; then
        log_error "Nacos configuration directory not found: $NACOS_DIR"
        exit 1
    fi

    check_nacos
    get_nacos_token
    import_all_configs

    log_info ""
    log_info "Nacos configurations initialized successfully!"
    log_info ""
}

main "$@"
