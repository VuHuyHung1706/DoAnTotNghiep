@echo off
REM Cinema Booking System - Kubernetes Monitoring Script (Windows Batch)
REM This script provides real-time monitoring of the deployed system

setlocal enabledelayedexpansion

set NAMESPACE=cinema-booking

:menu
cls
echo.
echo ================================================
echo Cinema Booking System - K8s Monitoring
echo ================================================
echo.
echo Select option:
echo 1. Show pod status
echo 2. Show service status
echo 3. Show deployment status
echo 4. Show MySQL logs
echo 5. Show API Gateway logs
echo 6. Show all resource details
echo 7. Port forward API Gateway (8888:8888)
echo 8. Port forward MySQL (3306:3306)
echo 9. Exit
echo.

set /p choice="Enter your choice (1-9): "

if "%choice%"=="1" goto show_pods
if "%choice%"=="2" goto show_services
if "%choice%"=="3" goto show_deployments
if "%choice%"=="4" goto show_mysql_logs
if "%choice%"=="5" goto show_gateway_logs
if "%choice%"=="6" goto show_all_details
if "%choice%"=="7" goto port_forward_api
if "%choice%"=="8" goto port_forward_mysql
if "%choice%"=="9" goto exit_monitor
echo Invalid choice. Please try again.
timeout /t 2 /nobreak >nul
goto menu

:show_pods
echo.
echo Pod Status:
echo ===========
kubectl get pods -n %NAMESPACE% -o wide
pause
goto menu

:show_services
echo.
echo Service Status:
echo ===============
kubectl get svc -n %NAMESPACE%
pause
goto menu

:show_deployments
echo.
echo Deployment Status:
echo ==================
kubectl get deployments -n %NAMESPACE%
pause
goto menu

:show_mysql_logs
echo.
echo MySQL Logs (last 50 lines):
echo ===========================
kubectl logs -n %NAMESPACE% -l app=mysql --tail=50 --timestamps=true
pause
goto menu

:show_gateway_logs
echo.
echo API Gateway Logs (last 50 lines):
echo =================================
kubectl logs -n %NAMESPACE% -l app=api-gateway --tail=50 --timestamps=true
pause
goto menu

:show_all_details
echo.
echo All Resource Details:
echo ====================
kubectl describe all -n %NAMESPACE%
pause
goto menu

:port_forward_api
echo.
echo Port forwarding API Gateway to localhost:8888
echo Press Ctrl+C to stop
echo.
kubectl port-forward -n %NAMESPACE% svc/api-gateway 8888:8888
goto menu

:port_forward_mysql
echo.
echo Port forwarding MySQL to localhost:3306
echo Press Ctrl+C to stop
echo.
kubectl port-forward -n %NAMESPACE% svc/mysql 3306:3306
goto menu

:exit_monitor
echo.
echo Exiting...
exit /b 0
