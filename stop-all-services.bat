@echo off
REM ============================================
REM AquaWatch-MS - Script d'arrêt
REM ============================================

echo ============================================
echo    AquaWatch-MS - Arret des services
echo ============================================

echo Arret de tous les processus Java Spring Boot...

REM Tue tous les processus Java (Spring Boot)
taskkill /F /FI "WINDOWTITLE eq Discovery Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Gateway Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Sensor Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Satellite Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq STModel Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Alert Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Map Service*" 2>nul

echo.
echo Tous les services ont ete arretes.
pause
