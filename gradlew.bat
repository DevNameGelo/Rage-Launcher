@echo off
setlocal
set VERSION=9.6.0
where gradle >nul 2>nul
if %ERRORLEVEL% EQU 0 (
  gradle %*
  exit /b %ERRORLEVEL%
)
set BASE=%USERPROFILE%\.gradle\music-rage-bootstrap
set DIST=%BASE%\gradle-%VERSION%
if not exist "%DIST%\bin\gradle.bat" (
  if not exist "%BASE%" mkdir "%BASE%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%VERSION%-bin.zip' -OutFile '%BASE%\gradle-%VERSION%-bin.zip'; Expand-Archive -Path '%BASE%\gradle-%VERSION%-bin.zip' -DestinationPath '%BASE%' -Force; Remove-Item '%BASE%\gradle-%VERSION%-bin.zip'"
)
call "%DIST%\bin\gradle.bat" %*
exit /b %ERRORLEVEL%
