@echo off
REM Set UTF-8 encoding for Vietnamese text
chcp 65001 >nul
echo ART AND DECOR DATABASE SETUP
echo ===============================

REM Get current directory
set CURRENT_DIR=%~dp0

REM Define XAMPP paths
set XAMPP_PATH1=D:\App\Xampp\mysql\bin\mysql.exe
set XAMPP_PATH2=C:\xampp\mysql\bin\mysql.exe

REM Check for MySQL
set MYSQL_PATH=

if exist "%XAMPP_PATH1%" (
    set MYSQL_PATH=%XAMPP_PATH1%
    goto :mysql_found
)

if exist "%XAMPP_PATH2%" (
    set MYSQL_PATH=%XAMPP_PATH2%
    goto :mysql_found
)

echo ERROR: MySQL not found in XAMPP paths
echo Please check XAMPP installation
pause
exit /b 1

:mysql_found
echo MySQL found: %MYSQL_PATH%

REM MySQL credentials (XAMPP default: root with no password)
set /p MYSQL_USER="MySQL username (default: root): "
if "%MYSQL_USER%"=="" set MYSQL_USER=root

set /p USE_PASSWORD="Use password? (y/n, default: n): "
if "%USE_PASSWORD%"=="" set USE_PASSWORD=n

REM Check SQL files
if not exist "%CURRENT_DIR%CREATE_DB_ART_AND_DECOR.sql" (
    echo ERROR: CREATE_DB_ART_AND_DECOR.sql not found
    pause
    exit /b 1
)

if not exist "%CURRENT_DIR%INSERT_SAMPLE_DATA.sql" (
    echo ERROR: INSERT_SAMPLE_DATA.sql not found  
    pause
    exit /b 1
)

echo.
echo Executing database setup...

REM Execute SQL files in sequence
echo.
echo Step 1: Creating database and tables...
if /i "%USE_PASSWORD%"=="y" (
    "%MYSQL_PATH%" -u %MYSQL_USER% -p --default-character-set=utf8mb4 < "%CURRENT_DIR%CREATE_DB_ART_AND_DECOR.sql"
) else (
    "%MYSQL_PATH%" -u %MYSQL_USER% --default-character-set=utf8mb4 < "%CURRENT_DIR%CREATE_DB_ART_AND_DECOR.sql"
)

if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to create database
    pause
    exit /b 1
)

REM Step 2: Clear sample data (COMMENTED - uncomment if needed)
REM echo Step 2: Clearing sample data...
REM if /i "%USE_PASSWORD%"=="y" (
REM     "%MYSQL_PATH%" -u %MYSQL_USER% -p < "%CURRENT_DIR%CLEAR_SAMPLE_DATA.sql"
REM ) else (
REM     "%MYSQL_PATH%" -u %MYSQL_USER% < "%CURRENT_DIR%CLEAR_SAMPLE_DATA.sql"
REM )

echo Step 3: Inserting sample data...
if /i "%USE_PASSWORD%"=="y" (
    "%MYSQL_PATH%" -u %MYSQL_USER% -p --default-character-set=utf8mb4 < "%CURRENT_DIR%INSERT_SAMPLE_DATA.sql"
) else (
    "%MYSQL_PATH%" -u %MYSQL_USER% --default-character-set=utf8mb4 < "%CURRENT_DIR%INSERT_SAMPLE_DATA.sql"
)

if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to insert sample data
    pause
    exit /b 1
)

echo.
echo DATABASE SETUP COMPLETED SUCCESSFULLY!
echo Database ART_AND_DECOR is ready.
pause