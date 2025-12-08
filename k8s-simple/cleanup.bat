@echo off
REM Cinema Booking System - Kubernetes Cleanup Script (Windows Batch)
REM This script removes all resources deployed by the cinema booking system
REM Added step: fix permissions for movie-service uploads before cleanup

setlocal enabledelayedexpansion

set NAMESPACE=cinema-booking
set POD_NAME=movie-service
set UPLOAD_DIR=/uploads

cls
echo.
echo ================================================
echo Cinema Booking System - K8s Cleanup
echo ================================================
echo.
echo WARNING: This will delete all resources in namespace '%NAMESPACE%'
echo.

set /p confirm="Are you sure you want to proceed? (yes/no): "

if /i not "%confirm%"=="yes" (
    echo Cleanup cancelled
    exit /b 0
)

echo.
echo Attempting to fix permissions for movie-service uploads...
kubectl exec -n %NAMESPACE% %POD_NAME% -- chmod -R 755 %UPLOAD_DIR%
if errorlevel 1 (
    echo [WARNING] Could not fix permissions. Pod may not exist or folder missing.
) else (
    echo [OK] Permissions fixed successfully
)

echo.
echo Deleting namespace and all resources...
echo.

kubectl delete namespace %NAMESPACE% --ignore-not-found=true
if errorlevel 1 (
    echo [ERROR] Error deleting namespace
    pause
    exit /b 1
)

echo [OK] Namespace deleted successfully
echo.
echo Waiting for namespace deletion to complete...
timeout /t 5 /nobreak >nul

echo.
echo [OK] Cleanup complete!
echo.
echo To verify all resources are deleted:
echo   kubectl get namespace
echo.
pause
