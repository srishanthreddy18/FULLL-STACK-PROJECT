@echo off
echo Starting Student Portfolio System...

REM Set email environment variables (if not already set)
if not defined MAIL_USERNAME set MAIL_USERNAME=saisathwik0739@gmail.com
if not defined MAIL_APP_PASSWORD set MAIL_APP_PASSWORD=vgtjnnoeagqoglil

echo Mail Configuration:
echo   MAIL_USERNAME=%MAIL_USERNAME%
echo   MAIL_APP_PASSWORD is set
echo.

echo Starting Spring Boot Backend (Port 8080)...
start "Portfolio Backend" cmd /k "cd /d d:\Full-stack\student-portfolio && set MAIL_USERNAME=%MAIL_USERNAME% && set MAIL_APP_PASSWORD=%MAIL_APP_PASSWORD% && mvn clean -q -DskipTests && mvn spring-boot:run"

timeout /t 3 /nobreak

echo Starting React Frontend (Port 5173)...
start "Portfolio Frontend" cmd /k "cd /d d:\Full-stack\student-portfolio-frontend && npm run dev"

echo.
echo Both servers are starting in separate windows.
echo DO NOT CLOSE those new black terminal windows while using the app!
echo You can now access the app at: http://localhost:5173
pause


