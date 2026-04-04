#!/bin/bash

# ============================================
# Showcase Pay - Stop Script
# ============================================

echo "Stopping Showcase Pay Services..."

# Stop all services
docker-compose down

echo ""
echo "All services stopped."
echo ""
