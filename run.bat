@echo off
REM ============================================================
REM  Gym Membership & Billing System - Build & Run Script
REM  23CSR306 - Java OOP Mini Project
REM ============================================================

SET "JAVAC=C:\Users\Karsna N\Downloads\eclipse-java-2026-06-R-win32-x86_64\eclipse\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_21.0.11.v20260515-1531\jre\bin\javac.exe"
SET "JAVA=C:\Users\Karsna N\Downloads\eclipse-java-2026-06-R-win32-x86_64\eclipse\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_21.0.11.v20260515-1531\jre\bin\java.exe"
SET "SRC=%~dp0src"
SET "OUT=%~dp0out"

echo.
echo  +============================================================+
echo  ^|   GYM MEMBERSHIP ^& BILLING MANAGEMENT SYSTEM             ^|
echo  ^|   23CSR306 - Java OOP Mini Project                        ^|
echo  +============================================================+
echo.

echo [1/3] Creating output directory...
if not exist "%OUT%" mkdir "%OUT%"

echo [2/3] Compiling Java sources...
"%JAVAC%" -d "%OUT%" -sourcepath "%SRC%" ^
  "%SRC%\com\gym\model\*.java" ^
  "%SRC%\com\gym\exception\*.java" ^
  "%SRC%\com\gym\service\*.java" ^
  "%SRC%\com\gym\main\*.java"

IF %ERRORLEVEL% NEQ 0 (
  echo.
  echo  [ERROR] Compilation failed. Check error messages above.
  pause
  exit /b 1
)

echo [3/3] Launching Gym Management System...
echo.
"%JAVA%" -cp "%OUT%" com.gym.main.Main

pause
