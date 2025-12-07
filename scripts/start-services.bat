@echo off
echo ================================================
echo    AquaWatch-MS - Demarrage des services
echo    Avec Eureka + PostgreSQL
echo ================================================
echo.

REM Tuer les anciens processus Java
echo Arret des anciens processus Java...
taskkill /F /IM java.exe 2>nul
timeout /t 3 /nobreak >nul

echo.
echo [1/7] Demarrage de discovery-service (Eureka - port 8761)...
cd /d "%~dp0..\discovery-service"
start "Discovery-Service-Eureka" cmd /c "mvnw.cmd spring-boot:run"
echo     Attente de 30s pour Eureka...
timeout /t 30 /nobreak >nul

echo [2/7] Demarrage de sensor-service (port 8081)...
cd /d "%~dp0..\sensor-service"
start "Sensor-Service" cmd /c "mvnw.cmd spring-boot:run"
timeout /t 5 /nobreak >nul

echo [3/7] Demarrage de satellite-service (port 8082)...
cd /d "%~dp0..\satellite-service"
start "Satellite-Service" cmd /c "mvnw.cmd spring-boot:run"
timeout /t 5 /nobreak >nul

echo [4/7] Demarrage de alert-service (port 8084)...
cd /d "%~dp0..\alert-service"
start "Alert-Service" cmd /c "mvnw.cmd spring-boot:run"
timeout /t 5 /nobreak >nul

echo [5/7] Demarrage de stmodel-service (port 8083)...
cd /d "%~dp0..\stmodel-service"
start "STModel-Service" cmd /c "mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak >nul

echo [6/7] Demarrage de gateway-service (port 8080)...
cd /d "%~dp0..\gateway-service"
start "Gateway-Service" cmd /c "mvnw.cmd spring-boot:run"
timeout /t 5 /nobreak >nul

echo [7/7] Demarrage de map-service (port 8085)...
cd /d "%~dp0..\map-service"
start "Map-Service" cmd /c "mvnw.cmd spring-boot:run"

echo.
echo ================================================
echo    Tous les services sont en cours de demarrage
echo ================================================
echo.
echo Services:
echo   - Eureka Dashboard:  http://localhost:8761
echo   - Gateway:           http://localhost:8080
echo   - sensor-service:    http://localhost:8081
echo   - satellite-service: http://localhost:8082
echo   - stmodel-service:   http://localhost:8083
echo   - alert-service:     http://localhost:8084
echo   - map-service:       http://localhost:8085
echo.
echo Attendez 60-90 secondes pour le demarrage complet.
echo.
pause
