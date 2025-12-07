@echo off
REM ============================================
REM AquaWatch-MS - Script de démarrage
REM ============================================
REM Ordre de démarrage:
REM 1. Discovery Service (Eureka)
REM 2. Gateway Service
REM 3. Microservices métier
REM ============================================

echo ============================================
echo    AquaWatch-MS - Demarrage des services
echo ============================================

echo.
echo [1/7] Demarrage de Discovery Service (Eureka) sur port 8761...
cd discovery-service
start "Discovery Service" cmd /k "mvnw spring-boot:run"
cd ..

echo Attente 20 secondes pour Eureka...
timeout /t 20 /nobreak

echo.
echo [2/7] Demarrage de Gateway Service sur port 8080...
cd gateway-service
start "Gateway Service" cmd /k "mvnw spring-boot:run"
cd ..

timeout /t 5 /nobreak

echo.
echo [3/7] Demarrage de Sensor Service sur port 8081...
cd sensor-service
start "Sensor Service" cmd /k "mvnw spring-boot:run"
cd ..

echo.
echo [4/7] Demarrage de Satellite Service sur port 8082...
cd satellite-service
start "Satellite Service" cmd /k "mvnw spring-boot:run"
cd ..

echo.
echo [5/7] Demarrage de STModel Service sur port 8083...
cd stmodel-service
start "STModel Service" cmd /k "mvnw spring-boot:run"
cd ..

echo.
echo [6/7] Demarrage de Alert Service sur port 8084...
cd alert-service
start "Alert Service" cmd /k "mvnw spring-boot:run"
cd ..

echo.
echo [7/7] Demarrage de Map Service sur port 8085...
cd map-service
start "Map Service" cmd /k "mvnw spring-boot:run"
cd ..

echo.
echo ============================================
echo    Tous les services sont en cours de demarrage!
echo ============================================
echo.
echo URLs importantes:
echo   - Eureka Dashboard:  http://localhost:8761
echo   - API Gateway:       http://localhost:8080
echo   - Sensor Service:    http://localhost:8081
echo   - Satellite Service: http://localhost:8082
echo   - STModel Service:   http://localhost:8083
echo   - Alert Service:     http://localhost:8084
echo   - Map Service:       http://localhost:8085
echo.
echo Appuyez sur une touche pour fermer cette fenetre...
pause > nul
