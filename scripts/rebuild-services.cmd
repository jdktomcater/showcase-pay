@echo off
setlocal EnableExtensions EnableDelayedExpansion

REM ============================================
REM Rebuild and restart business services only
REM Infrastructure services (MySQL, Redis, Nacos, etc.) are NOT restarted
REM Usage: scripts\rebuild-services.cmd
REM ============================================

for %%I in ("%~dp0..") do set "PROJECT_DIR=%%~fI"
cd /d "%PROJECT_DIR%"

echo ========================================
echo Rebuild ^& Restart Business Services
echo ========================================

echo.
echo [1/5] Stopping business services...
docker-compose stop gateway payment order admin 2>nul
echo [OK] Business services stopped

echo.
echo [2/5] Building Java JARs...
call mvn clean package -DskipTests -q
if errorlevel 1 exit /b 1
echo [OK] Java build completed

echo.
echo [3/5] Building admin frontend...
pushd showcase-pay-admin
call npm install
if errorlevel 1 (
    popd
    exit /b 1
)
call npm run build
if errorlevel 1 (
    popd
    exit /b 1
)
popd
echo [OK] Admin frontend built

echo.
echo [4/5] Rebuilding Docker images...
docker-compose build gateway payment order admin
if errorlevel 1 exit /b 1
echo [OK] Docker images rebuilt

echo.
echo [5/5] Starting business services...
docker-compose up -d gateway payment order admin
if errorlevel 1 exit /b 1
echo [OK] Business services started

echo.
echo Checking service status...
timeout /t 5 /nobreak >nul

set "ALL_RUNNING=1"
for %%S in (gateway payment order admin) do (
    docker ps --format "{{.Names}}" 2>nul | findstr /i /c:"showcase-pay-%%S" >nul
    if errorlevel 1 (
        echo [X] showcase-pay-%%S is NOT running
        set "ALL_RUNNING=0"
    ) else (
        echo [OK] showcase-pay-%%S is running
    )
)

echo.
echo ========================================
if "!ALL_RUNNING!"=="1" (
    echo Done! All business services are up and running.
    echo Admin Panel: http://localhost:3000
) else (
    echo Warning: Some services failed to start. Check logs above.
)
echo ========================================

endlocal
exit /b 0
