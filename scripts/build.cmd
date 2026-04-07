@echo off
setlocal EnableExtensions

REM ============================================
REM Showcase Pay - Build Script
REM Build all services with Maven
REM ============================================

for %%I in ("%~dp0..") do set "PROJECT_DIR=%%~fI"
set "BUILD_TYPE=full"
set "BUILD_DOCKER=false"

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

:check_maven
where mvn >nul 2>&1
if errorlevel 1 (
    call :log_error "Maven is not installed or not in PATH"
    call :log_error "Please install Maven: https://maven.apache.org/install.html"
    exit /b 1
)
for /f "delims=" %%v in ('mvn -version 2^>^&1 ^| findstr /r /c:"Apache Maven"') do (
    call :log_info "Maven version: %%v"
    goto :mvn_ver_done
)
:mvn_ver_done
exit /b 0

:check_pom
if not exist "%PROJECT_DIR%\pom.xml" (
    call :log_error "pom.xml not found in %PROJECT_DIR%"
    exit /b 1
)
exit /b 0

:clean_build
call :log_info "Cleaning project..."
call mvn clean -f "%PROJECT_DIR%\pom.xml"
exit /b 0

:build_with_tests
call :log_info "Building with tests..."
call mvn clean install -f "%PROJECT_DIR%\pom.xml"
exit /b 0

:build_skip_tests
call :log_info "Building without tests..."
call mvn clean package -DskipTests -f "%PROJECT_DIR%\pom.xml"
exit /b 0

:build_docker_images
call :log_info "Building Docker images..."
cd /d "%PROJECT_DIR%"
docker-compose build gateway payment order
exit /b 0

:show_help
echo Usage: %~nx0 [OPTION]
echo.
echo Build Showcase Pay services with Maven
echo.
echo Options:
echo   --clean         Clean build artifacts only
echo   --full          Build with tests (default)
echo   --skip-tests    Build without tests
echo   --docker        Build Docker images after Maven build
echo   --help, -h      Show this help message
echo.
echo Examples:
echo   %~nx0               # Build with tests
echo   %~nx0 --skip-tests  # Build without tests
echo   %~nx0 --docker      # Build and create Docker images
echo.
exit /b 0

:main
call :log_info "============================================"
call :log_info "Showcase Pay - Build Script"
call :log_info "============================================"

call :check_maven
if errorlevel 1 exit /b 1
call :check_pom
if errorlevel 1 exit /b 1

:parse_args
if "%~1"=="" goto :args_done
if /i "%~1"=="--clean" set "BUILD_TYPE=clean" & shift & goto :parse_args
if /i "%~1"=="--full" set "BUILD_TYPE=full" & shift & goto :parse_args
if /i "%~1"=="--skip-tests" set "BUILD_TYPE=skip-tests" & shift & goto :parse_args
if /i "%~1"=="--docker" set "BUILD_DOCKER=true" & shift & goto :parse_args
if /i "%~1"=="--help" call :show_help & exit /b 0
if /i "%~1"=="-h" call :show_help & exit /b 0
call :log_error "Unknown option: %~1"
call :show_help
exit /b 1

:args_done
if "%BUILD_TYPE%"=="clean" (
    call :clean_build
    goto :after_build
)
if "%BUILD_TYPE%"=="full" (
    call :build_with_tests
    goto :after_build
)
if "%BUILD_TYPE%"=="skip-tests" (
    call :build_skip_tests
    goto :after_build
)
call :log_error "Internal error: unknown BUILD_TYPE"
exit /b 1

:after_build
if errorlevel 1 exit /b 1

if /i "%BUILD_DOCKER%"=="true" (
    call :build_docker_images
    if errorlevel 1 exit /b 1
)

call :log_info ""
call :log_info "============================================"
call :log_info "Build completed successfully!"
call :log_info "============================================"
call :log_info ""

endlocal
exit /b 0
