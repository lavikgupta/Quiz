@echo off
echo Starting Online Quiz System...
start "" http://localhost:8080
call .\gradlew.bat bootRun
pause
