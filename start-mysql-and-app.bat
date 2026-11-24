@echo off
title Car Workshop Inventory - Launcher with Database
echo ========================================================
echo   CAR WORKSHOP INVENTORY - LAUNCHER
echo ========================================================
echo.

:: Check if MySQL is already running
netstat -ano | findstr "3306" >nul
if %errorlevel% equ 0 (
    echo [OK] MySQL Server is already active on port 3306.
) else (
    echo [INFO] Starting MySQL Server from Laragon...
    start "" /B "C:\laragon\bin\mysql\mysql-8.4.3-winx64\bin\mysqld.exe" --defaults-file="C:\laragon\bin\mysql\mysql-8.4.3-winx64\my.ini"
    timeout /t 3 /nobreak >nul
)

echo.
echo [INFO] Launching Desktop Application...
java -jar target\car-workshop-inventory-1.0.0-jar-with-dependencies.jar

pause
