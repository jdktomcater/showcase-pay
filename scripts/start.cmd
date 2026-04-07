@echo off
setlocal EnableExtensions EnableDelayedExpansion

REM ============================================
REM Showcase Pay - Start Script
REM Start all services with docker-compose
REM ============================================

for %%I in ("%~dp0..") do set "PROJECT_DIR=%%~fI"

goto :main

:log_info
echo [INFO] %~1
goto :eof

:log_warn
echo [WARN] %~1
goto :eof

:log_error
echo [ERROR] %~1
goto :eof

:check_docker
docker info >nul 2>&1
if errorlevel 1 (
    call :log_error "Docker is not running. Please start Docker and try again."
    exit /b 1
)
exit /b 0

:check_compose_file
if not exist "%PROJECT_DIR%\docker-compose.yml" (
    call :log_error "docker-compose.yml not found in %PROJECT_DIR%"
    exit /b 1
)
exit /b 0

:start_infrastructure
call :log_info "Starting infrastructure services..."
cd /d "%PROJECT_DIR%"
docker-compose up -d mysql redis nacos rocketmq-namesrv

call :log_info "Waiting for MySQL and Redis to be ready..."
timeout /t 15 /nobreak >nul

call :log_info "Starting RocketMQ broker and console..."
docker-compose up -d rocketmq-broker rocketmq-console
timeout /t 10 /nobreak >nul

call :log_info "Starting ELK stack..."
docker-compose up -d elasticsearch logstash kibana
timeout /t 20 /nobreak >nul

call :log_info "Starting SkyWalking..."
docker-compose up -d skywalking-oap skywalking-ui
timeout /t 10 /nobreak >nul
exit /b 0

:start_applications
call :log_info "Building application services..."
call mvn clean package -DskipTests -f "%PROJECT_DIR%\pom.xml"

call :log_info "Starting application services..."
cd /d "%PROJECT_DIR%"
docker-compose up -d gateway payment order
exit /b 0

:main
call :log_info "============================================"
call :log_info "Showcase Pay - Starting Services"
call :log_info "============================================"

call :check_docker
if errorlevel 1 exit /b 1
call :check_compose_file
if errorlevel 1 exit /b 1

cd /d "%PROJECT_DIR%"
docker-compose ps 2>nul | findstr /i /c:"Up" >nul
if not errorlevel 1 (
    call :log_warn "Some services are already running."
    set /p "REPLY=Do you want to stop them first? (y/n): "
    if /i "!REPLY!"=="y" (
        docker-compose down
        timeout /t 5 /nobreak >nul
    )
)

call :start_infrastructure
if errorlevel 1 exit /b 1
call :start_applications
if errorlevel 1 exit /b 1

call :log_info ""
call :log_info "============================================"
call :log_info "Showcase Pay Services Started Successfully!"
call :log_info "============================================"
call :log_info ""
call :log_info "Service URLs:"
call :log_info "  - API Gateway:        http://localhost:8080"
call :log_info "  - Payment Service:    http://localhost:8081"
call :log_info "  - Order Service:      http://localhost:8082"
call :log_info "  - Nacos Console:      http://localhost:8848/nacos (nacos/nacos)"
call :log_info "  - RocketMQ Console:   http://localhost:8090"
call :log_info "  - Kibana:             http://localhost:5601"
call :log_info "  - SkyWalking UI:      http://localhost:8085"
call :log_info ""

endlocal
exit /b 0
