@echo off
TITLE LifeCare AI - Hospital Management System
SETLOCAL

:: Set Project Directory to where this script is located
SET "PROJ_DIR=%~dp0"
cd /d "%PROJ_DIR%"

:: Load environment variables from .env file if it exists
if exist "%PROJ_DIR%.env" (
    echo [INFO] Loading environment variables from .env...
    for /f "usebackq tokens=1* delims==" %%i in (`findstr /v "^#" "%PROJ_DIR%.env"`) do (
        set "%%i=%%j"
    )
)


:: Path to the bundled JDK
:: Structure is:
:: Downloads\hospital management system\   <-- ROOT
::   jdk-17\                                <-- JDK Location
::   hospital management system\            <-- Subfolder 1
::     hospital-management-system\          <-- Script Location
:: So we need to go UP twice (..\..)
SET "JDK_PATH=%PROJ_DIR%..\..\jdk-17\jdk-17.0.18+8"

echo ===========================================
echo    🚀 STARTING LIFECARE AI SYSTEM...
echo ===========================================
echo [DEBUG] Current DIR: %CD%
echo [DEBUG] JDK Path  : %JDK_PATH%
echo [DEBUG] Target Port: 8080
echo ===========================================

:: Verify JDK 
if exist "%JDK_PATH%\bin\java.exe" (
    echo [INFO] Found bundled JDK at "%JDK_PATH%"
    goto jdk_verified
)

if exist "%PROJ_DIR%..\jdk-17\jdk-17.0.18+8\bin\java.exe" (
    SET "JDK_PATH=%PROJ_DIR%..\jdk-17\jdk-17.0.18+8"
    echo [INFO] Found JDK at alternate path: "%PROJ_DIR%..\jdk-17\jdk-17.0.18+8"
    goto jdk_verified
)

if not defined JAVA_HOME goto check_path
if not exist "%JAVA_HOME%\bin\java.exe" goto check_path
SET "JDK_PATH=%JAVA_HOME%"
echo [INFO] Found JDK using JAVA_HOME: "%JAVA_HOME%"
goto jdk_verified

:check_path
:: Try to find java in PATH
where java >nul 2>nul
if not %errorlevel% equ 0 goto jdk_not_found

for /f "delims=" %%i in ('where java') do (
    set "JAVA_EXE=%%i"
    goto resolved_path
)

:resolved_path
if not defined JAVA_EXE goto jdk_not_found
for %%j in ("%JAVA_EXE%") do set "BIN_DIR=%%~dpj"
for %%k in ("%BIN_DIR%\..") do set "JDK_PATH=%%~fsk"
echo [INFO] Found JDK in PATH: "%JDK_PATH%"
goto jdk_verified

:jdk_not_found
echo [FATAL] Could not find JDK 17 bin\java.exe.
pause
exit /b

:jdk_verified

:: Set Environment Variables
set "JAVA_HOME=%JDK_PATH%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: --- SERVER EXECUTION LOOP ---
:main_loop
cls
echo ===========================================
echo    🚀 LIFECARE AI - CONTROL CENTER
echo ===========================================
echo [STATUS] Ready to Launch
echo [CONFIG] Port: 8080
echo [JDK]    %JAVA_HOME%
echo ===========================================
echo.

:: --- NGROK TUNNELING (EXTERIOR ACCESS) ---
echo.
echo [INFO] Looking for ngrok installations...
where ngrok >nul 2>nul
if not %errorlevel% equ 0 (
    echo [WARN] ngrok not detected. Server will be Local Only ^(localhost:8080^).
    goto run_app
)

echo [INFO] Found ngrok. Initializing Public Tunnel...
:: Clean up any existing ngrok sessions
taskkill /f /im ngrok.exe >nul 2>nul

:: Start ngrok in background
start /b ngrok http 8080 > ngrok_startup.log 2>&1

echo [WAIT] Fetching Public Access Link...
echo.
powershell -NoProfile -NonInteractive -Command "for ($i=0; $i -lt 10; $i++) { try { $url = (Invoke-RestMethod http://localhost:4040/api/tunnels -TimeoutSec 2 -ErrorAction Stop).tunnels[0].public_url; if ($url) { Write-Host '==================================================' -ForegroundColor Cyan; Write-Host '   LIFECARE AI IS NOW LIVE GLOBALLY!' -ForegroundColor Yellow; Write-Host ('   Link: ' + $url) -ForegroundColor White; Write-Host '==================================================' -ForegroundColor Cyan; return; } } catch { Start-Sleep -Seconds 1 } }"

:run_app
echo.
echo ==================================================
echo    🌍 LOCAL WEB ACCESS LINKS
echo ==================================================
echo    Main Web App: http://localhost:8080
echo    H2 Database:  http://localhost:8080/h2-console
echo ==================================================
echo.

:: Run Application
call mvnw.cmd spring-boot:run

echo.
echo ===========================================
echo    🛑 SERVER HAS STOPPED
echo ===========================================
echo.
echo Choose an action:
echo [R] Restart the Server
echo [E] Exit to Windows
echo.

choice /c RE /n /m "Selection: "

if errorlevel 2 goto end_script
if errorlevel 1 goto main_loop

:end_script
echo [INFO] Shutting down LifeCare AI Operations...
timeout /t 2 >nul
exit /b
