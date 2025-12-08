@echo off
REM Cinema Booking System - Docker Desktop Kubernetes Deployment Script (Windows Batch)
REM This script deploys the entire cinema booking system to Docker Desktop Kubernetes

setlocal enabledelayedexpansion

set NAMESPACE=cinema-booking
set MANIFEST_FILE=%~dp0all-in-one.yaml

cls
echo.
echo ================================================
echo Cinema Booking System - K8s Deployment
echo ================================================
echo.

REM Check if kubectl is available
echo Checking for kubectl...
where kubectl >nul 2>&1
if errorlevel 1 (
    echo [ERROR] kubectl is not installed or not in PATH
    echo Please install kubectl: https://kubernetes.io/docs/tasks/tools/
    echo.
    pause
    exit /b 1
)
echo [OK] kubectl found
echo.

REM Check if connected to Kubernetes cluster
echo Checking Kubernetes cluster connection...
kubectl cluster-info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Cannot connect to Kubernetes cluster
    echo.
    echo Please ensure Docker Desktop Kubernetes is running:
    echo   - Open Docker Desktop Settings
    echo   - Go to Kubernetes tab
    echo   - Enable Kubernetes
    echo.
    pause
    exit /b 1
)
echo [OK] Connected to Kubernetes cluster
echo.

REM Check if all service images are available
echo Checking required Docker images...

set MISSING_COUNT=0
for %%i in (
    user-service-image:latest
    movie-service-image:latest
    cinema-service-image:latest
    booking-service-image:latest
    recommendation-service-image:latest
    api-gateway-image:latest
) do (
    docker image inspect %%i >nul 2>&1
    if errorlevel 1 (
        echo   [MISSING] %%i
        set /a MISSING_COUNT=!MISSING_COUNT!+1
    ) else (
        echo   [OK] %%i
    )
)
echo.

if !MISSING_COUNT! gtr 0 (
    echo [WARNING] !MISSING_COUNT! service image^(s^) not found
    echo.
    echo You need to build Docker images for all services:
    echo   cd ApiGateway
    echo   docker build -t api-gateway-image:latest .
    echo   cd ..\UserService
    echo   docker build -t user-service-image:latest .
    echo   cd ..\MovieService
    echo   docker build -t movie-service-image:latest .
    echo   cd ..\CinemaService
    echo   docker build -t cinema-service-image:latest .
    echo   cd ..\BookingService
    echo   docker build -t booking-service-image:latest .
    echo   cd ..\RecommendationService
    echo   docker build -t recommendation-service-image:latest .
    echo.
)

REM Apply manifests
echo Deploying to Kubernetes...
kubectl apply -f "%MANIFEST_FILE%"
if errorlevel 1 (
    echo [ERROR] Failed to apply manifests
    echo.
    pause
    exit /b 1
)
echo [OK] Manifests applied successfully
echo.

REM Wait for MySQL to be ready
echo Waiting for MySQL to be ready (this may take 2-3 minutes)...
setlocal enabledelayedexpansion
set WAIT_COUNT=0
:wait_mysql
kubectl get pods -n %NAMESPACE% -l app=mysql -o jsonpath="{.items[0].status.phase}" >nul 2>&1
if errorlevel 1 goto mysql_not_ready
set /a WAIT_COUNT=!WAIT_COUNT!+1
if !WAIT_COUNT! gtr 180 (
    echo [WARNING] MySQL pod not ready within timeout
    kubectl get pods -n %NAMESPACE%
    goto skip_mysql_wait
)
if !WAIT_COUNT! gtr 0 (
    if !WAIT_COUNT:~-1! equ 0 echo   Still waiting... (!WAIT_COUNT! seconds elapsed)
)
timeout /t 1 /nobreak >nul
goto wait_mysql
:mysql_not_ready
echo [OK] MySQL is ready
:skip_mysql_wait
echo.

REM Display deployment summary
echo ================================================
echo Deployment Complete!
echo ================================================
echo.
echo Deployed Services:
echo   - MySQL:                        mysql:3306
echo   - User Service:                 user-service:8080
echo   - Movie Service:                movie-service:8081
echo   - Cinema Service:               cinema-service:8082
echo   - Booking Service:              booking-service:8083
echo   - Recommendation Service:       recommendation-service:8084
echo   - API Gateway:                  api-gateway:8888
echo.
echo To access services from outside the cluster, use port-forward:
echo   kubectl port-forward -n %NAMESPACE% svc/api-gateway 8888:8888
echo   kubectl port-forward -n %NAMESPACE% svc/mysql 3306:3306
echo.
echo View pod status:
echo   kubectl get pods -n %NAMESPACE%
echo   kubectl describe pods -n %NAMESPACE%
echo.
echo View logs:
echo   kubectl logs -n %NAMESPACE% -f deployment/api-gateway
echo   kubectl logs -n %NAMESPACE% -f deployment/mysql
echo.
echo Default test credentials:
echo   Username: admin
echo   Password: 123456
echo.
pause
