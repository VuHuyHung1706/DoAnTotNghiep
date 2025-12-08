# Cinema Booking System - Docker Desktop Kubernetes Deployment Script (Windows PowerShell)
# This script deploys the entire cinema booking system to Docker Desktop Kubernetes

param(
    [switch]$SkipImageCheck = $false
)

$NAMESPACE = "cinema-booking"
$MANIFEST_FILE = Join-Path -Path (Split-Path -Parent $MyInvocation.MyCommand.Path) -ChildPath "all-in-one.yaml"

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Cinema Booking System - K8s Deployment (Windows)" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Check if kubectl is available
Write-Host "Checking for kubectl..." -NoNewline
try {
    $null = kubectl version --client 2>$null
    Write-Host " ✓" -ForegroundColor Green
} catch {
    Write-Host " ✗" -ForegroundColor Red
    Write-Host ""
    Write-Host "ERROR: kubectl is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install kubectl: https://kubernetes.io/docs/tasks/tools/" -ForegroundColor Yellow
    exit 1
}

# Check if connected to Kubernetes cluster
Write-Host "Checking Kubernetes cluster connection..." -NoNewline
try {
    $null = kubectl cluster-info 2>$null
    Write-Host " ✓" -ForegroundColor Green
} catch {
    Write-Host " ✗" -ForegroundColor Red
    Write-Host ""
    Write-Host "ERROR: Cannot connect to Kubernetes cluster" -ForegroundColor Red
    Write-Host "Please ensure Docker Desktop Kubernetes is running:" -ForegroundColor Yellow
    Write-Host "  - Open Docker Desktop Settings" -ForegroundColor Yellow
    Write-Host "  - Go to Kubernetes tab" -ForegroundColor Yellow
    Write-Host "  - Enable Kubernetes" -ForegroundColor Yellow
    exit 1
}

# Check if all service images are available
if (-not $SkipImageCheck) {
    Write-Host ""
    Write-Host "Checking required Docker images..."
    $REQUIRED_IMAGES = @(
        "user-service-image:latest",
        "movie-service-image:latest",
        "cinema-service-image:latest",
        "booking-service-image:latest",
        "recommendation-service-image:latest",
        "api-gateway-image:latest"
    )

    $MISSING_IMAGES = 0
    foreach ($image in $REQUIRED_IMAGES) {
        try {
            $null = docker image inspect $image 2>$null
            Write-Host "  ✓ $image" -ForegroundColor Green
        } catch {
            Write-Host "  ✗ $image (missing)" -ForegroundColor Yellow
            $MISSING_IMAGES++
        }
    }

    if ($MISSING_IMAGES -gt 0) {
        Write-Host ""
        Write-Host "WARNING: $MISSING_IMAGES service image(s) not found" -ForegroundColor Yellow
        Write-Host "You need to build Docker images for all services:" -ForegroundColor Yellow
        Write-Host "  cd ApiGateway; docker build -t api-gateway-image:latest ." -ForegroundColor Gray
        Write-Host "  cd ../UserService; docker build -t user-service-image:latest ." -ForegroundColor Gray
        Write-Host "  cd ../MovieService; docker build -t movie-service-image:latest ." -ForegroundColor Gray
        Write-Host "  cd ../CinemaService; docker build -t cinema-service-image:latest ." -ForegroundColor Gray
        Write-Host "  cd ../BookingService; docker build -t booking-service-image:latest ." -ForegroundColor Gray
        Write-Host "  cd ../RecommendationService; docker build -t recommendation-service-image:latest ." -ForegroundColor Gray
        Write-Host ""
        
        $response = Read-Host "Continue without all images? (yes/no)"
        if ($response -ne "yes") {
            exit 1
        }
    }
}

# Apply manifests
Write-Host ""
Write-Host "Deploying to Kubernetes..."
try {
    & kubectl apply -f "$MANIFEST_FILE" | Out-Null
    Write-Host "✓ Manifests applied successfully" -ForegroundColor Green
} catch {
    Write-Host "✗ ERROR: Failed to apply manifests" -ForegroundColor Red
    exit 1
}

# Wait for MySQL to be ready
Write-Host ""
Write-Host "Waiting for MySQL to be ready (this may take 2-3 minutes)..."
$retries = 180  # 3 minutes with 1-second intervals
$ready = $false
for ($i = 0; $i -lt $retries; $i++) {
    $podStatus = kubectl get pods -n $NAMESPACE -l app=mysql -o jsonpath='{.items[0].status.phase}' 2>$null
    if ($podStatus -eq "Running") {
        $containerReady = kubectl get pods -n $NAMESPACE -l app=mysql -o jsonpath='{.items[0].status.containerStatuses[0].ready}' 2>$null
        if ($containerReady -eq "true") {
            Write-Host "✓ MySQL is ready" -ForegroundColor Green
            $ready = $true
            break
        }
    }
    if ($i % 30 -eq 0 -and $i -gt 0) {
        Write-Host "  Still waiting... ($i seconds elapsed)"
    }
    Start-Sleep -Seconds 1
}

if (-not $ready) {
    Write-Host "⚠️  MySQL pod not ready within timeout" -ForegroundColor Yellow
    Write-Host "Checking pod status..."
    kubectl get pods -n $NAMESPACE
}

# Wait for services to be ready
Write-Host ""
Write-Host "Waiting for services to be ready..."
$SERVICES = @("user-service", "movie-service", "cinema-service", "booking-service", "recommendation-service", "api-gateway")
$service_timeouts = @()

foreach ($service in $SERVICES) {
    $retries = 120
    $ready = $false
    for ($i = 0; $i -lt $retries; $i++) {
        $deployment = kubectl get deployment -n $NAMESPACE $service -o jsonpath='{.status.replicas}' 2>$null
        $readyReplicas = kubectl get deployment -n $NAMESPACE $service -o jsonpath='{.status.readyReplicas}' 2>$null
        
        if ($deployment -eq "1" -and $readyReplicas -eq "1") {
            Write-Host "✓ $service is ready" -ForegroundColor Green
            $ready = $true
            break
        }
        Start-Sleep -Seconds 1
    }
    
    if (-not $ready) {
        Write-Host "⚠️  $service pod not ready (may be starting)" -ForegroundColor Yellow
        $service_timeouts += $service
    }
}

# Display deployment summary
Write-Host ""
Write-Host "================================================" -ForegroundColor Green
Write-Host "Deployment Complete!" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Deployed Services:" -ForegroundColor Cyan
Write-Host "  - MySQL:                        mysql:3306"
Write-Host "  - User Service:                 user-service:8080"
Write-Host "  - Movie Service:                movie-service:8081"
Write-Host "  - Cinema Service:               cinema-service:8082"
Write-Host "  - Booking Service:              booking-service:8083"
Write-Host "  - Recommendation Service:       recommendation-service:8084"
Write-Host "  - API Gateway:                  api-gateway:8888"
Write-Host ""
Write-Host "To access services from outside the cluster, use port-forward:" -ForegroundColor Cyan
Write-Host "  kubectl port-forward -n $NAMESPACE svc/api-gateway 8888:8888" -ForegroundColor Gray
Write-Host "  kubectl port-forward -n $NAMESPACE svc/mysql 3306:3306" -ForegroundColor Gray
Write-Host ""
Write-Host "View pod status:" -ForegroundColor Cyan
Write-Host "  kubectl get pods -n $NAMESPACE" -ForegroundColor Gray
Write-Host "  kubectl describe pods -n $NAMESPACE" -ForegroundColor Gray
Write-Host ""
Write-Host "View logs:" -ForegroundColor Cyan
Write-Host "  kubectl logs -n $NAMESPACE -f deployment/api-gateway" -ForegroundColor Gray
Write-Host "  kubectl logs -n $NAMESPACE -f deployment/mysql" -ForegroundColor Gray
Write-Host ""
Write-Host "Default test credentials:" -ForegroundColor Cyan
Write-Host "  Username: admin" -ForegroundColor Gray
Write-Host "  Password: 123456" -ForegroundColor Gray
Write-Host ""

if ($service_timeouts.Count -gt 0) {
    Write-Host "⚠️  Some services may still be starting. Run 'kubectl get pods -n $NAMESPACE' to verify." -ForegroundColor Yellow
}
