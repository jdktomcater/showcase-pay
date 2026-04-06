#!/bin/bash
# ============================================
# Rebuild and restart business services
# Usage: ./scripts/rebuild-services.sh [service1 service2 ...]
# If no service specified, rebuild all business services
# ============================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Rebuild & Restart Business Services${NC}"
echo -e "${GREEN}========================================${NC}"

# Default services to rebuild
SERVICES=("$@")
if [ ${#SERVICES[@]} -eq 0 ]; then
    SERVICES=(gateway payment order)
fi

echo -e "${YELLOW}Target services: ${SERVICES[*]}${NC}"

# Step 1: Build Java JARs (if source code changed)
echo -e "\n${YELLOW}[1/4] Building Java JARs...${NC}"
mvn clean package -DskipTests -q
echo -e "${GREEN}✓ Java build completed${NC}"

# Step 2: Rebuild Docker images
echo -e "\n${YELLOW}[2/4] Rebuilding Docker images...${NC}"
for service in "${SERVICES[@]}"; do
    echo -e "${YELLOW}Building $service...${NC}"
    docker-compose build "$service"
done
echo -e "${GREEN}✓ Docker images rebuilt${NC}"

# Step 3: Restart services
echo -e "\n${YELLOW}[3/4] Restarting services...${NC}"
docker-compose up -d "${SERVICES[@]}"
echo -e "${GREEN}✓ Services restarted${NC}"

# Step 4: Check status
echo -e "\n${YELLOW}[4/4] Checking service status...${NC}"
sleep 5
for service in "${SERVICES[@]}"; do
    container_name="showcase-pay-$service"
    if docker ps --format '{{.Names}}' | grep -q "$container_name"; then
        echo -e "${GREEN}✓ $container_name is running${NC}"
    else
        echo -e "${RED}✗ $container_name is NOT running${NC}"
        echo -e "${YELLOW}Logs:${NC}"
        docker logs "$container_name" --tail 20
    fi
done

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}Done! Services are up and running.${NC}"
echo -e "${GREEN}========================================${NC}"
