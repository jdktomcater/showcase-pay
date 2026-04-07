@echo off
setlocal EnableExtensions

REM ============================================
REM Showcase Pay - Stop Script
REM Stop all services
REM Optional: pass --clean to run docker-compose down
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

:check_compose_file
if not exist "%PROJECT_DIR%\docker-compose.yml" (
    call :log_error "docker-compose.yml not found in %PROJECT_DIR%"
    exit /b 1
)
exit /b 0

:stop_services
call :log_info "Stopping all services..."
cd /d "%PROJECT_DIR%"

docker-compose stop gateway payment order 2>nul
docker-compose stop skywalking-ui skywalking-oap kibana logstash elasticsearch 2>nul
docker-compose stop rocketmq-console rocketmq-broker rocketmq-namesrv 2>nul
docker-compose stop nacos redis mysql 2>nul

call :log_info "All services stopped."
exit /b 0

:cleanup_containers
if /i "%~1"=="--clean" (
    call :log_info "Removing containers..."
    cd /d "%PROJECT_DIR%"
    docker-compose down
    call :log_info "Containers removed."
)
exit /b 0

:main
call :log_info "============================================"
call :log_info "Showcase Pay - Stopping Services"
call :log_info "============================================"

call :check_compose_file
if errorlevel 1 exit /b 1
call :stop_services
if errorlevel 1 exit /b 1
call :cleanup_containers "%~1"

call :log_info ""
call :log_info "All services stopped successfully."
call :log_info ""

endlocal
exit /b 0
