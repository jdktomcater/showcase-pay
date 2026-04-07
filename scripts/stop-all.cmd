@echo off
setlocal EnableExtensions

REM ============================================
REM Showcase Pay - Stop Script
REM ============================================

cd /d "%~dp0.."

echo Stopping Showcase Pay Services...

REM Stop all services
docker-compose down

echo.
echo All services stopped.
echo.

endlocal
