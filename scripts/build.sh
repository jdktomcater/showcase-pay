#!/bin/bash
# ============================================
# Showcase Pay - Build Script
# Build all services with Maven
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

# Check if Maven is installed
check_maven() {
    if ! command -v mvn &> /dev/null; then
        log_error "Maven is not installed or not in PATH"
        log_error "Please install Maven: https://maven.apache.org/install.html"
        exit 1
    fi
    log_info "Maven version: $(mvn -version | head -1)"
}

# Check if pom.xml exists
check_pom() {
    if [ ! -f "$PROJECT_DIR/pom.xml" ]; then
        log_error "pom.xml not found in $PROJECT_DIR"
        exit 1
    fi
}

# Clean build
clean_build() {
    log_info "Cleaning project..."
    mvn clean -f "$PROJECT_DIR/pom.xml"
}

# Build with tests
build_with_tests() {
    log_info "Building with tests..."
    mvn clean install -f "$PROJECT_DIR/pom.xml"
}

# Build without tests
build_skip_tests() {
    log_info "Building without tests..."
    mvn clean package -DskipTests -f "$PROJECT_DIR/pom.xml"
}

# Build Docker images
build_docker_images() {
    log_info "Building Docker images..."
    cd "$PROJECT_DIR"
    docker-compose build gateway payment order
}

# Show help
show_help() {
    echo "Usage: $0 [OPTION]"
    echo ""
    echo "Build Showcase Pay services with Maven"
    echo ""
    echo "Options:"
    echo "  --clean         Clean build artifacts only"
    echo "  --full          Build with tests (default)"
    echo "  --skip-tests    Build without tests"
    echo "  --docker        Build Docker images after Maven build"
    echo "  --help, -h      Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0               # Build with tests"
    echo "  $0 --skip-tests  # Build without tests"
    echo "  $0 --docker      # Build and create Docker images"
    echo ""
}

# Main execution
main() {
    log_info "============================================"
    log_info "Showcase Pay - Build Script"
    log_info "============================================"

    check_maven
    check_pom

    local build_type="full"
    local build_docker=false

    # Parse arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --clean)
                build_type="clean"
                shift
                ;;
            --full)
                build_type="full"
                shift
                ;;
            --skip-tests)
                build_type="skip-tests"
                shift
                ;;
            --docker)
                build_docker=true
                shift
                ;;
            --help|-h)
                show_help
                exit 0
                ;;
            *)
                log_error "Unknown option: $1"
                show_help
                exit 1
                ;;
        esac
    done

    # Execute build
    case $build_type in
        clean)
            clean_build
            ;;
        full)
            build_with_tests
            ;;
        skip-tests)
            build_skip_tests
            ;;
    esac

    # Build Docker images if requested
    if [ "$build_docker" = true ]; then
        build_docker_images
    fi

    log_info ""
    log_info "============================================"
    log_info "Build completed successfully!"
    log_info "============================================"
    log_info ""
}

main "$@"
