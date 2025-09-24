@echo off
echo Starting GlucoCloud API...
echo.
echo Make sure you have Java 17+ installed!
echo.
echo After startup, you can:
echo - Access H2 Console: http://localhost:8080/h2-console
echo - Test API: http://localhost:8080/auth/register
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if %ERRORLEVEL% == 0 (
    echo Using system Maven...
    mvn spring-boot:run
) else (
    echo Maven not found. Please install Maven or Java 17+ to run the application.
    echo.
    echo Quick install options:
    echo 1. Install Maven: https://maven.apache.org/download.cgi
    echo 2. Or use IDE like IntelliJ IDEA or Eclipse
    echo.
    pause
)