#!/bin/bash

# Cinema Booking System - Docker Desktop Kubernetes Deployment Script
# This script deploys the entire cinema booking system to Docker Desktop Kubernetes

set -e

NAMESPACE="cinema-booking"
MANIFEST_FILE="$(dirname "$0")/all-in-one.yaml"

echo "================================================"
echo "Cinema Booking System - K8s Deployment"
echo "================================================"

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "❌ ERROR: kubectl is not installed or not in PATH"
    echo "Please install kubectl: https://kubernetes.io/docs/tasks/tools/"
    exit 1
fi

echo "✓ kubectl found"

# Check if connected to Kubernetes cluster
echo "Checking Kubernetes cluster connection..."
if ! kubectl cluster-info &> /dev/null; then
    echo "❌ ERROR: Cannot connect to Kubernetes cluster"
    echo "Please ensure Docker Desktop Kubernetes is running:"
    echo "  - Open Docker Desktop Settings"
    echo "  - Go to Kubernetes tab"
    echo "  - Enable Kubernetes"
    exit 1
fi

echo "✓ Connected to Kubernetes cluster"

# Check if all service images are available
echo ""
echo "Checking required Docker images..."
REQUIRED_IMAGES=(
    "user-service-image:latest"
    "movie-service-image:latest"
    "cinema-service-image:latest"
    "booking-service-image:latest"
    "recommendation-service-image:latest"
    "api-gateway-image:latest"
)

MISSING_IMAGES=0
for image in "${REQUIRED_IMAGES[@]}"; do
    if docker image inspect "$image" &> /dev/null; then
        echo "  ✓ $image"
    else
        echo "  ✗ $image (missing)"
        MISSING_IMAGES=$((MISSING_IMAGES + 1))
    fi
done

if [ $MISSING_IMAGES -gt 0 ]; then
    echo ""
    echo "⚠️  WARNING: $MISSING_IMAGES service image(s) not found"
    echo "You need to build Docker images for all services:"
    echo "  cd ApiGateway && docker build -t api-gateway-image:latest ."
    echo "  cd ../UserService && docker build -t user-service-image:latest ."
    echo "  cd ../MovieService && docker build -t movie-service-image:latest ."
    echo "  cd ../CinemaService && docker build -t cinema-service-image:latest ."
    echo "  cd ../BookingService && docker build -t booking-service-image:latest ."
    echo "  cd ../RecommendationService && docker build -t recommendation-service-image:latest ."
fi

# Apply manifests
echo ""
echo "Deploying to Kubernetes..."
if kubectl apply -f "$MANIFEST_FILE"; then
    echo "✓ Manifests applied successfully"
else
    echo "❌ ERROR: Failed to apply manifests"
    exit 1
fi

# Wait for MySQL to be ready
echo ""
echo "Waiting for MySQL to be ready (this may take 2-3 minutes)..."
if kubectl wait --for=condition=ready pod -l app=mysql -n $NAMESPACE --timeout=300s 2>/dev/null; then
    echo "✓ MySQL is ready"
else
    echo "⚠️  MySQL pod not ready within timeout"
    echo "Checking pod status..."
    kubectl get pods -n $NAMESPACE
fi

# Wait for services to be ready
echo ""
echo "Waiting for services to be ready..."
SERVICES=("user-service" "movie-service" "cinema-service" "booking-service" "recommendation-service" "api-gateway")
for service in "${SERVICES[@]}"; do
    if kubectl wait --for=condition=ready pod -l app=$service -n $NAMESPACE --timeout=120s 2>/dev/null; then
        echo "✓ $service is ready"
    else
        echo "⚠️  $service pod not ready (may be starting)"
    fi
done

# Display deployment summary
echo ""
echo "================================================"
echo "Deployment Complete!"
echo "================================================"
echo ""
echo "Deployed Services:"
echo "  - MySQL:                mysql:3306"
echo "  - User Service:         user-service:8080"
echo "  - Movie Service:        movie-service:8081"
echo "  - Cinema Service:       cinema-service:8082"
echo "  - Booking Service:      booking-service:8083"
echo "  - Recommendation Service: recommendation-service:8084"
echo "  - API Gateway:          api-gateway:8888"
echo ""
echo "To access services from outside the cluster, use port-forward:"
echo "  kubectl port-forward -n $NAMESPACE svc/api-gateway 8888:8888"
echo "  kubectl port-forward -n $NAMESPACE svc/mysql 3306:3306"
echo ""
echo "View pod status:"
echo "  kubectl get pods -n $NAMESPACE"
echo "  kubectl describe pods -n $NAMESPACE"
echo ""
echo "View logs:"
echo "  kubectl logs -n $NAMESPACE -f deployment/api-gateway"
echo "  kubectl logs -n $NAMESPACE -f deployment/mysql"
echo ""
echo "Default test credentials:"
echo "  Username: admin"
echo "  Password: 123456"
echo ""
