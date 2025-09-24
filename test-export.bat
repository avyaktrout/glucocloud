@echo off
echo Testing GlucoCloud Export Functionality
echo.

echo 1. First, start the application with: mvn spring-boot:run
echo.
echo 2. Login to get JWT token:
echo curl -X POST http://localhost:8080/auth/login ^
echo   -H "Content-Type: application/json" ^
echo   -d "{\"email\": \"demo@glucocloud.com\", \"password\": \"demo123\"}"
echo.
echo 3. Test export endpoints (replace YOUR_JWT_TOKEN with actual token):
echo.

echo === Glucose Export ===
echo curl -X GET "http://localhost:8080/export/glucose" ^
echo   -H "Authorization: Bearer YOUR_JWT_TOKEN" ^
echo   --output glucose_readings.csv
echo.

echo === Meals Export ===
echo curl -X GET "http://localhost:8080/export/meals" ^
echo   -H "Authorization: Bearer YOUR_JWT_TOKEN" ^
echo   --output meals.csv
echo.

echo === Medications Export ===
echo curl -X GET "http://localhost:8080/export/medications" ^
echo   -H "Authorization: Bearer YOUR_JWT_TOKEN" ^
echo   --output medications.csv
echo.

echo === Comprehensive Health Report ===
echo curl -X GET "http://localhost:8080/export/comprehensive-report" ^
echo   -H "Authorization: Bearer YOUR_JWT_TOKEN" ^
echo   --output health_report.csv
echo.

echo === Export Formats Info ===
echo curl -X GET "http://localhost:8080/export/formats"
echo.

echo === Date-Filtered Export Example ===
echo curl -X GET "http://localhost:8080/export/glucose?from=2024-01-01T00:00:00&to=2024-01-31T23:59:59" ^
echo   -H "Authorization: Bearer YOUR_JWT_TOKEN" ^
echo   --output glucose_january.csv
echo.

echo Note: All CSV files will be saved to the current directory
echo These files can be opened in Excel, Google Sheets, or any spreadsheet application
echo.
pause