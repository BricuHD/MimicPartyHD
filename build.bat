@echo off
title Build Mimic Party
where gradle >nul 2>nul
if errorlevel 1 (
  echo Gradle was not found. Install Gradle 8.14.3 or use the GitHub Actions build workflow.
  pause
  exit /b 1
)
gradle build
if errorlevel 1 (
  echo Build failed. Copy the error message and send it for troubleshooting.
  pause
  exit /b 1
)
echo.
echo Build successful. Your JAR is in build\libs\
pause
