# Cinema Booking System - Kubernetes Deployment Guide

## Summary

You now have a complete Kubernetes deployment package for Docker Desktop with all necessary files. This guide consolidates everything you need to deploy the cinema booking system on Kubernetes.

## What Was Created

### 1. **all-in-one.yaml** (2000+ lines)
Single comprehensive Kubernetes manifest containing:
- **Namespace**: `cinema-booking` - Isolated resource container
- **ConfigMap**: Complete `init-db.sql` with all 5 databases and test data
- **MySQL Deployment**: Database server with init script mounted
- **6 Microservice Deployments**: User, Movie, Cinema, Booking, Recommendation, API Gateway
- **Services**: ClusterIP for internal services, LoadBalancer for API Gateway
- **Resource Limits**: 256Mi RAM request, 512Mi limit per pod

### 2. **Deployment Scripts**

#### Option A: Bash Script (Linux/Mac/WSL)
- **File**: `deploy.sh`
- **Features**: 
  - Comprehensive error checking
  - Image validation
  - Waits for MySQL initialization
  - Port forwarding instructions
  - Service readiness verification

#### Option B: PowerShell Script (Windows)
- **File**: `deploy.ps1`
- **Features**: 
  - Full Windows color output
  - Detailed progress reporting
  - Service timeout tracking
  - Skip image check option

#### Option C: Batch Script (Windows)
- **File**: `deploy.bat`
- **Features**: 
  - Simple batch implementation
  - Works on all Windows versions
  - Basic error handling

### 3. **Supporting Scripts**

- **cleanup.sh**: Removes all deployed resources safely
- **monitor.sh**: Interactive monitoring dashboard with 8 options
- **README.md**: Comprehensive documentation (400+ lines)

## Quick Start (Windows)

### Step 1: Build Docker Images

```powershell
cd ApiGateway
docker build -t api-gateway-image:latest .
cd ..

cd UserService
docker build -t user-service-image:latest .
cd ..

cd MovieService
docker build -t movie-service-image:latest .
cd ..

cd CinemaService
docker build -t cinema-service-image:latest .
cd ..

cd BookingService
docker build -t booking-service-image:latest .
cd ..

cd RecommendationService
docker build -t recommendation-service-image:latest .
cd ..
```

### Step 2: Run Deployment

```powershell
cd k8s-simple

# Option A: PowerShell (Recommended for Windows)
.\deploy.ps1

# Option B: Batch
deploy.bat

# Option C: Git Bash or WSL
bash deploy.sh
```

### Step 3: Verify Deployment

```powershell
kubectl get pods -n cinema-booking
kubectl get svc -n cinema-booking
```

### Step 4: Access the System

```powershell
# In one terminal
kubectl port-forward -n cinema-booking svc/api-gateway 8888:8888

# In another terminal
kubectl port-forward -n cinema-booking svc/mysql 3306:3306
```

Then access:
- **API Gateway**: http://localhost:8888
- **MySQL**: localhost:3306 (user: root, password: 123456)

## Directory Structure

```
k8s-simple/
├── all-in-one.yaml      # Main Kubernetes manifest
├── deploy.sh            # Linux/Mac deployment script
├── deploy.ps1           # Windows PowerShell script
├── deploy.bat           # Windows Batch script
├── cleanup.sh           # Remove all resources
├── monitor.sh           # Interactive monitoring tool
├── README.md            # Detailed documentation
└── DEPLOYMENT_SUMMARY.md  # This file
```

## Database Configuration

### Initialization
- **Source**: ConfigMap with embedded init-db.sql
- **Execution**: Automatic on MySQL pod startup
- **Data**: 5 databases, 16 tables, 2000+ test records

### Databases
1. **userdb**: 4 tables (accounts, customers, managers, invalidated_tokens)
2. **moviedb**: 7 tables (movies, genres, actors, movie_genres, movie_actors, showtimes, reviews)
3. **cinemaadb**: 3 tables (cinemas, rooms, seats)
4. **bookingdb**: 2 tables (invoices, tickets)
5. **recommendationdb**: 2 tables (user_preferences, viewing_history)

### Test Data
- **Accounts**: Admin + Staff + 5 regular users
- **Cinemas**: 4 locations with 16 rooms (4 each) and 896 seats
- **Movies**: 5 sample films with genres, actors, showtimes
- **Bookings**: 4 sample invoices with tickets

## Service Configuration

### Inter-Service Communication
Services communicate via Kubernetes DNS:
```
jdbc:mysql://mysql:3306/[database]
```

### Service Ports
| Service | Internal Port | Database | K8s Service |
|---------|--------------|----------|-------------|
| API Gateway | 8888 | - | LoadBalancer |
| User Service | 8080 | userdb | ClusterIP |
| Movie Service | 8081 | moviedb | ClusterIP |
| Cinema Service | 8082 | cinemaadb | ClusterIP |
| Booking Service | 8083 | bookingdb | ClusterIP |
| Recommendation Service | 8084 | recommendationdb | ClusterIP |

## Monitoring

### View Real-time Status
```powershell
# Interactive monitoring
bash monitor.sh

# Manual commands
kubectl get pods -n cinema-booking -w
kubectl get svc -n cinema-booking
kubectl top pods -n cinema-booking
```

### View Logs
```powershell
kubectl logs -n cinema-booking -f deployment/mysql
kubectl logs -n cinema-booking -f deployment/api-gateway
kubectl logs -n cinema-booking -f deployment/user-service
```

### Access Pod Shell
```powershell
kubectl exec -it -n cinema-booking deployment/mysql -- mysql -uroot -p123456
kubectl exec -it -n cinema-booking [pod-name] -- /bin/bash
```

## Troubleshooting

### MySQL Won't Start
```powershell
# Check pod status
kubectl describe pod -n cinema-booking -l app=mysql

# View initialization logs
kubectl logs -n cinema-booking -l app=mysql
```

### Services Can't Connect to Database
```powershell
# Verify MySQL is ready
kubectl exec -it -n cinema-booking deployment/mysql -- mysql -uroot -p123456 -e "SHOW DATABASES;"

# Check service environment variables
kubectl describe deployment -n cinema-booking user-service
```

### Images Not Found
```powershell
# Verify images exist
docker images | Select-String "service"

# Build missing images manually
docker build -t [service-name]-image:latest ./[ServiceFolder]
```

## Cleanup

```powershell
# Using cleanup script
bash cleanup.sh

# Or manually
kubectl delete namespace cinema-booking

# Remove Docker images
docker rmi api-gateway-image:latest
docker rmi user-service-image:latest
docker rmi movie-service-image:latest
docker rmi cinema-service-image:latest
docker rmi booking-service-image:latest
docker rmi recommendation-service-image:latest
```

## Environment Details

### Kubernetes Cluster
- **Type**: Docker Desktop built-in Kubernetes
- **Nodes**: 1 (single-node cluster)
- **API Server**: localhost:6443
- **Container Runtime**: Docker

### Resource Allocation
- **Per Pod**: 256 Mi RAM request, 512 Mi limit
- **CPU**: 250m request, 500m limit
- **Total for Full System**: ~2 GB RAM recommended

### Storage
- **MySQL Data**: emptyDir (ephemeral, lost on restart)
- **ConfigMap**: init-db.sql (persists)

## Next Steps

1. **Verify Deployment**
   - Run `kubectl get pods -n cinema-booking`
   - Wait for all pods to be "Running"

2. **Test Connectivity**
   - Port forward services
   - Test API endpoints
   - Verify database connection

3. **Load Test Data**
   - Default credentials: admin / 123456
   - 5 test users available (user1-user5)
   - 4 cinemas with sample movies and showtimes

4. **Scale Services** (if needed)
   - `kubectl scale deployment -n cinema-booking [service-name] --replicas=3`

5. **Monitor Performance**
   - Run `bash monitor.sh` for interactive monitoring
   - Check pod resource usage with `kubectl top pods`

## Important Files

| File | Purpose | Size |
|------|---------|------|
| all-in-one.yaml | Main K8s manifest | ~2000 lines |
| deploy.sh | Linux/Mac deployment | ~150 lines |
| deploy.ps1 | Windows PowerShell deployment | ~180 lines |
| deploy.bat | Windows Batch deployment | ~120 lines |
| monitor.sh | Interactive monitoring | ~100 lines |
| cleanup.sh | Resource cleanup | ~40 lines |
| README.md | Full documentation | ~400 lines |

## File Locations

All files are in: `k8s-simple/`

From workspace root:
```
c:\STUDY\DATN\DoAnTotNghiep\k8s-simple\
```

## Support Resources

- **Kubernetes Official Docs**: https://kubernetes.io/docs/
- **Docker Desktop Kubernetes**: https://docs.docker.com/desktop/kubernetes/
- **kubectl Cheat Sheet**: https://kubernetes.io/docs/reference/kubectl/cheatsheet/
- **Troubleshooting**: https://kubernetes.io/docs/tasks/debug-application-cluster/

## Version Information

- **Kubernetes**: 1.28+ (via Docker Desktop)
- **MySQL**: 8.4.6
- **Container Runtime**: Docker

## Notes

- ✅ **Database initialization**: Fully automated via ConfigMap
- ✅ **Service discovery**: Kubernetes DNS resolution
- ✅ **Resource management**: Memory and CPU limits configured
- ✅ **Health checks**: Liveness probe configured for MySQL
- ⚠️ **Storage**: Ephemeral (data lost on pod restart)
- ⚠️ **Security**: Development mode (not production-ready)

## Quick Reference

```powershell
# Deploy
.\deploy.ps1

# Check status
kubectl get pods -n cinema-booking

# View logs
kubectl logs -n cinema-booking -f deployment/mysql

# Access pod
kubectl exec -it -n cinema-booking deployment/mysql -- bash

# Scale service
kubectl scale deployment -n cinema-booking api-gateway --replicas=3

# Port forward
kubectl port-forward -n cinema-booking svc/api-gateway 8888:8888

# Cleanup
bash cleanup.sh
```

---

**Created**: 2025-12-08
**Status**: Complete and Ready for Deployment
