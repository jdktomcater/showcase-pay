@echo off
setlocal EnableExtensions EnableDelayedExpansion

REM ============================================
REM Showcase Pay - Initialize Nacos Configurations
REM Import configurations to Nacos server
REM ============================================

for %%I in ("%~dp0..") do set "PROJECT_DIR=%%~fI"
set "NACOS_DIR=%PROJECT_DIR%\docker\nacos"

if not defined NACOS_ADDR set "NACOS_ADDR=http://localhost:8848"
if not defined NACOS_USERNAME set "NACOS_USERNAME=nacos"
if not defined NACOS_PASSWORD set "NACOS_PASSWORD=nacos"
if not defined NAMESPACE set "NAMESPACE="

set "SUCCESS_COUNT=0"
set "FAIL_COUNT=0"

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

:log_debug
echo [DEBUG] %~1
goto :eof

:check_nacos
call :log_info "Checking Nacos connectivity..."
curl -s -f "%NACOS_ADDR%/nacos/" >nul 2>&1
if errorlevel 1 (
    call :log_error "Nacos is not accessible at %NACOS_ADDR%"
    call :log_error "Please ensure Nacos is running: docker-compose up -d nacos"
    exit /b 1
)
call :log_info "Nacos is accessible at %NACOS_ADDR%"
exit /b 0

:get_nacos_token
set "NACOS_TOKEN="
set "AUTH_HEADER="
set "LOGIN_JSON=%TEMP%\nacos_login_%RANDOM%.json"
curl -s -X POST "%NACOS_ADDR%/nacos/v1/auth/login" -d "username=%NACOS_USERNAME%" -d "password=%NACOS_PASSWORD%" -o "%LOGIN_JSON%" 2>nul
if not exist "%LOGIN_JSON%" goto :token_done
for /f "usebackq delims=" %%t in (`powershell -NoProfile -Command "try { (Get-Content -LiteralPath '%LOGIN_JSON%' -Raw ^| ConvertFrom-Json).accessToken } catch { }"`) do set "NACOS_TOKEN=%%t"
del "%LOGIN_JSON%" 2>nul
:token_done
if defined NACOS_TOKEN (
    set "AUTH_HEADER=Authorization: Bearer !NACOS_TOKEN!"
) else (
    call :log_warn "Failed to get Nacos auth token, proceeding without authentication"
)
exit /b 0

REM Import one config; returns 0 on success, 1 on failure
:import_config
set "DATA_ID=%~1"
set "GRP=%~2"
set "FILE_PATH=%~3"
if not exist "%FILE_PATH%" (
    call :log_error "Configuration file not found: %FILE_PATH%"
    exit /b 1
)
call :log_info "Importing %DATA_ID% (Group: %GRP%)..."
set "RESP_FILE=%TEMP%\nacos_import_%RANDOM%.txt"
if defined AUTH_HEADER (
    curl -s -X POST "%NACOS_ADDR%/nacos/v1/cs/configs" -H "!AUTH_HEADER!" --data-urlencode "dataId=%DATA_ID%" --data-urlencode "group=%GRP%" --data-urlencode "tenant=%NAMESPACE%" --data-urlencode "type=yaml" --data-urlencode "content@%FILE_PATH%" -o "!RESP_FILE!"
) else (
    curl -s -X POST "%NACOS_ADDR%/nacos/v1/cs/configs" --data-urlencode "dataId=%DATA_ID%" --data-urlencode "group=%GRP%" --data-urlencode "tenant=%NAMESPACE%" --data-urlencode "type=yaml" --data-urlencode "content@%FILE_PATH%" -o "!RESP_FILE!"
)
set "IMPORT_OK=0"
if exist "!RESP_FILE!" (
    findstr /c:"true" "!RESP_FILE!" >nul 2>&1
    if not errorlevel 1 set "IMPORT_OK=1"
    if "!IMPORT_OK!"=="0" type "!RESP_FILE!"
    del "!RESP_FILE!" 2>nul
)
if "!IMPORT_OK!"=="1" (
    call :log_info "  Successfully imported %DATA_ID%"
    exit /b 0
)
call :log_error "  Failed to import %DATA_ID%"
exit /b 1

:import_all_configs
call :import_config "common-config.yaml" "DEFAULT_GROUP" "%NACOS_DIR%\common-config.yaml"
if errorlevel 1 (set /a FAIL_COUNT+=1) else (set /a SUCCESS_COUNT+=1)

call :import_config "gateway-config.yaml" "DEFAULT_GROUP" "%NACOS_DIR%\gateway-config.yaml"
if errorlevel 1 (set /a FAIL_COUNT+=1) else (set /a SUCCESS_COUNT+=1)

call :import_config "order-config.yaml" "DEFAULT_GROUP" "%NACOS_DIR%\order-config.yaml"
if errorlevel 1 (set /a FAIL_COUNT+=1) else (set /a SUCCESS_COUNT+=1)

call :import_config "payment-config.yaml" "DEFAULT_GROUP" "%NACOS_DIR%\payment-config.yaml"
if errorlevel 1 (set /a FAIL_COUNT+=1) else (set /a SUCCESS_COUNT+=1)

echo.
call :log_info "============================================"
call :log_info "Configuration Import Summary"
call :log_info "============================================"
call :log_info "  Successful: !SUCCESS_COUNT!"
if !FAIL_COUNT! gtr 0 (
    call :log_error "  Failed: !FAIL_COUNT!"
    exit /b 1
) else (
    call :log_info "  Failed: 0"
)
call :log_info "============================================"
exit /b 0

:main
call :log_info "============================================"
call :log_info "Showcase Pay - Initialize Nacos Configurations"
call :log_info "============================================"
call :log_info "  Nacos Address: %NACOS_ADDR%"
if defined NAMESPACE (
    call :log_info "  Namespace: %NAMESPACE%"
) else (
    call :log_info "  Namespace: default"
)
call :log_info "============================================"
echo.

if not exist "%NACOS_DIR%\" (
    call :log_error "Nacos configuration directory not found: %NACOS_DIR%"
    exit /b 1
)

call :check_nacos
if errorlevel 1 exit /b 1
call :get_nacos_token
call :import_all_configs
if errorlevel 1 exit /b 1

call :log_info ""
call :log_info "Nacos configurations initialized successfully!"
call :log_info ""

endlocal
exit /b 0
