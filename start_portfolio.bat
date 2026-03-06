@echo off
echo Starting Student Portfolio System...

echo Starting Spring Boot Backend (Port 8080)...
start "Portfolio Backend" cmd /k "cd /d d:\Full-stack\student-portfolio && mvn spring-boot:run"

echo Starting React Frontend (Port 5173)...
start "Portfolio Frontend" cmd /k "cd /d d:\Full-stack\student-portfolio-frontend && npm run dev"

echo Both servers are starting in separate windows.
echo DO NOT CLOSE those new black terminal windows while using the app!
echo You can now access the app at: http://localhost:5173
pause
