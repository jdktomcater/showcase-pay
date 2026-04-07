@echo off
setlocal EnableExtensions

REM ============================================
REM Showcase Pay - Startup Script
REM ============================================

cd /d "%~dp0.."

echo Starting Showcase Pay Infrastructure Services...

REM Start infrastructure services first
docker-compose up -d mysql redis nacos rocketmq-namesrv

echo Waiting for infrastructure services to be ready...
timeout /t 30 /nobreak >nul

REM Start RocketMQ and monitoring
docker-compose up -d rocketmq-broker rocketmq-console

echo Waiting for RocketMQ to be ready...
timeout /t 15 /nobreak >nul

REM Start ELK stack
docker-compose up -d elasticsearch logstash kibana

echo Waiting for Elasticsearch to be ready...
timeout /t 20 /nobreak >nul

REM Start SkyWalking
docker-compose up -d skywalking-oap skywalking-ui

echo Waiting for SkyWalking to be ready...
timeout /t 15 /nobreak >nul

REM Build and start application services
echo Building application services...
call mvn clean package -DskipTests

echo Starting application services...
docker-compose up -d gateway payment order admin

echo.
echo ============================================
echo Showcase Pay Services Started Successfully!
echo ============================================
echo.
echo Service URLs:
echo   - API Gateway:        http://localhost:8080
echo   - Payment Service:    http://localhost:8081
echo   - Order Service:      http://localhost:8082
echo   - Admin Service:      http://localhost:3000
echo   - Nacos Console:      http://localhost:8848/nacos
echo   - RocketMQ Console:   http://localhost:8090
echo   - Kibana:             http://localhost:5601
echo   - SkyWalking UI:      http://localhost:8085
echo.
echo Default Credentials:
echo   - MySQL: root/root
echo   - Nacos: nacos/nacos
echo.

endlocal
