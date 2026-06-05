@echo off
TITLE LifeCare AI - Hospital Management System
SETLOCAL

:: Set Project Directory to where this script is located
SET "PROJ_DIR=%~dp0"
cd /d "%PROJ_DIR%"

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
if not exist "%JDK_PATH%\bin\java.exe" (
    echo [ERROR] JDK not found at %JDK_PATH%
    echo Expected Structure: Downloads\hospital management system\jdk-17\
    echo Current Structure detected: Two levels below the root.
    echo.
    echo Searching for alternate JDK path...
    if exist "%PROJ_DIR%..\jdk-17\jdk-17.0.18+8" (
        SET "JDK_PATH=%PROJ_DIR%..\jdk-17\jdk-17.0.18+8"
        echo [INFO] Found JDK at alternate path.
    ) else (
        echo [FATAL] Could not find JDK 17 bin\java.exe.
        pause
        exit /b
    )
)

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
if %errorlevel% equ 0 (
    echo [INFO] Found ngrok. Initializing Public Tunnel...
    :: Clean up any existing ngrok sessions
    taskkill /f /im ngrok.exe >nul 2>nul
    
    :: Start ngrok in background
    start /b ngrok http 8080 > ngrok_startup.log 2>&1
    
    echo [WAIT] Fetching Public Access Link...
    echo.
    powershell -Command "for ($i=0; $i -lt 15; $i++) { try { $url = (Invoke-RestMethod http://localhost:4040/api/tunnels).tunnels[0].public_url; if ($url) { Write-Host '==================================================' -ForegroundColor Cyan; Write-Host '   🌍 LIFECARE AI IS NOW LIVE GLOBALLY!' -ForegroundColor Yellow; Write-Host ('   Link: ' + $url) -ForegroundColor White; Write-Host '==================================================' -ForegroundColor Cyan; return; } } catch { Start-Sleep -Seconds 1 } }"
) else (
    echo [WARN] ngrok not detected. Server will be Local Only ^(localhost:8080^).
)
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
