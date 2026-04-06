#!/bin/bash
# ============================================
# Rebuild and restart business services only
# Infrastructure services (MySQL, Redis, Nacos, etc.) are NOT restarted
# Usage: ./scripts/rebuild-services.sh
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

# Step 1: Stop business services only
echo -e "\n${YELLOW}[1/4] Stopping business services...${NC}"
docker-compose stop gateway payment order
echo -e "${GREEN}✓ Business services stopped${NC}"

# Step 2: Build Java JARs
echo -e "\n${YELLOW}[2/4] Building Java JARs...${NC}"
mvn clean package -DskipTests -q
echo -e "${GREEN}✓ Java build completed${NC}"

# Step 3: Rebuild Docker images for business services
echo -e "\n${YELLOW}[3/4] Rebuilding Docker images...${NC}"
docker-compose build gateway payment order
echo -e "${GREEN}✓ Docker images rebuilt${NC}"

# Step 4: Start business services
echo -e "\n${YELLOW}[4/4] Starting business services...${NC}"
docker-compose up -d gateway payment order
echo -e "${GREEN}✓ Business services started${NC}"

# Check status
echo -e "\n${YELLOW}Checking service status...${NC}"
sleep 5

services=("gateway" "payment" "order")
all_running=true

for service in "${services[@]}"; do
    container_name="showcase-pay-$service"
    if docker ps --format '{{.Names}}' | grep -q "$container_name"; then
        echo -e "${GREEN}✓ $container_name is running${NC}"
    else
        echo -e "${RED}✗ $container_name is NOT running${NC}"
        all_running=false
    fi
done

echo -e "\n${GREEN}========================================${NC}"
if [ "$all_running" = true ]; then
    echo -e "${GREEN}Done! All business services are up and running.${NC}"
else
    echo -e "${RED}Warning: Some services failed to start. Check logs above.${NC}"
fi
echo -e "${GREEN}========================================${NC}"
