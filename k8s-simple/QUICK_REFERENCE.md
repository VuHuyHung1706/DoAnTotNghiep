# Cinema Booking System - K8s Deployment Quick Reference

## TL;DR - 3-Minute Setup

### Prerequisites
- Docker Desktop with Kubernetes enabled
- kubectl installed
- All 6 service images built

### Deploy
```powershell
cd k8s-simple
.\deploy.ps1
# Wait 2-3 minutes for MySQL initialization
```

### Access
```powershell
kubectl port-forward -n cinema-booking svc/api-gateway 8888:8888
# Visit: http://localhost:8888
```

### Cleanup
```powershell
bash cleanup.sh
```

---

## File Overview

### Main Manifest
- **all-in-one.yaml**: 2000+ lines, single deployment file with:
  - Namespace, ConfigMap (init-db.sql)
  - MySQL 8.4.6 with automatic initialization
  - 6 microservices (User, Movie, Cinema, Booking, Recommendation, API Gateway)
  - All services configured with environment variables
  - Resource limits (256 Mi RAM request, 512 Mi limit)

### Deployment Scripts

| Script | OS | Use Case |
|--------|-----|----------|
| deploy.ps1 | Windows | PowerShell with nice colors |
| deploy.bat | Windows | Universal batch file |
| deploy.sh | Linux/Mac/WSL | Bash script |

All scripts do the same thing:
1. Check kubectl availability
2. Verify Kubernetes connection
3. Validate Docker images
4. Deploy manifests
5. Wait for MySQL
6. Show access instructions

### Utilities

- **monitor.sh**: 8-option interactive monitoring dashboard
  - Pod status, service status, deployment status
  - View logs, resource details
  - Port forwarding shortcuts

- **cleanup.sh**: Safe resource deletion with confirmation

### Documentation

- **README.md**: 400+ line comprehensive guide
- **DEPLOYMENT_SUMMARY.md**: This deployment overview
- **QUICK_REFERENCE.md**: This file

---

## Database Details

### 5 Databases Created

```
userdb
├── accounts (17 users: admin, staff, user1-user5)
├── customers (5 customers linked to accounts)
├── managers (admin, staff)
└── invalidated_tokens (empty table)

moviedb
├── movies (5 sample films)
├── genres (10 genres)
├── actors (5 actors)
├── movie_genres (genre relationships)
├── movie_actors (actor relationships)
├── showtimes (5 sample showtimes)
└── reviews (2 reviews)

cinemaadb
├── cinemas (4 locations)
├── rooms (16 total, 4 per cinema)
└── seats (896 total, 56 per room)

bookingdb
├── invoices (4 sample bookings)
└── tickets (4 sample tickets)

recommendationdb
├── user_preferences (3 users)
└── viewing_history (3 viewing records)
```

### Credentials
- **MySQL Root**: root / 123456
- **Test User**: admin / 123456
- **Test Users**: user1 - user5 / 123456

---

## Service Endpoints

### Internal (within Kubernetes cluster)
```
mysql:3306                          # MySQL database
user-service:8080                   # User management API
movie-service:8081                  # Movie catalog API
cinema-service:8082                 # Cinema management API
booking-service:8083                # Booking management API
recommendation-service:8084         # Recommendations API
api-gateway:8888                    # Main API Gateway
```

### External (after port forwarding)
```
localhost:8888                      # API Gateway
localhost:3306                      # MySQL
```

---

## Common Commands

### Deployment
```powershell
# Deploy
.\deploy.ps1

# Deploy and skip image check
.\deploy.ps1 -SkipImageCheck

# Deploy with bash (WSL/Git Bash)
bash deploy.sh
```

### Monitoring
```powershell
# Interactive dashboard
bash monitor.sh

# View all resources
kubectl get all -n cinema-booking

# Watch pods
kubectl get pods -n cinema-booking -w

# Check specific pod
kubectl describe pod -n cinema-booking [pod-name]

# View resource usage
kubectl top pods -n cinema-booking
```

### Logs
```powershell
# View logs
kubectl logs -n cinema-booking -f deployment/mysql
kubectl logs -n cinema-booking -f deployment/api-gateway

# View pod logs
kubectl logs -n cinema-booking [pod-name]

# Tail last 100 lines
kubectl logs -n cinema-booking -f deployment/mysql --tail=100
```

### Port Forwarding
```powershell
# API Gateway
kubectl port-forward -n cinema-booking svc/api-gateway 8888:8888

# MySQL
kubectl port-forward -n cinema-booking svc/mysql 3306:3306

# Both (in different terminals)
kubectl port-forward -n cinema-booking svc/api-gateway 8888:8888 &
kubectl port-forward -n cinema-booking svc/mysql 3306:3306
```

### Pod Access
```powershell
# Shell access
kubectl exec -it -n cinema-booking deployment/mysql -- /bin/bash

# Run MySQL CLI
kubectl exec -it -n cinema-booking deployment/mysql -- mysql -uroot -p123456

# Run command
kubectl exec -n cinema-booking deployment/mysql -- mysql -uroot -p123456 -e "SHOW DATABASES;"
```

### Scaling
```powershell
# Scale to 3 replicas
kubectl scale deployment -n cinema-booking api-gateway --replicas=3

# Check status
kubectl get deployment -n cinema-booking api-gateway
```

### Cleanup
```powershell
# Safe cleanup (with confirmation)
bash cleanup.sh

# Force delete
kubectl delete namespace cinema-booking

# Remove images
docker rmi api-gateway-image:latest user-service-image:latest movie-service-image:latest cinema-service-image:latest booking-service-image:latest recommendation-service-image:latest
```

---

## Troubleshooting Checklist

### Kubernetes Won't Connect
```powershell
# ✓ Enable Kubernetes in Docker Desktop
# ✓ Wait for "Kubernetes is running"
# ✓ Verify: kubectl cluster-info
# ✓ Reset if needed: Docker Desktop Settings > Kubernetes > Reset
```

### Images Not Found
```powershell
# ✓ Build all images first:
docker build -t api-gateway-image:latest ./ApiGateway
docker build -t user-service-image:latest ./UserService
docker build -t movie-service-image:latest ./MovieService
docker build -t cinema-service-image:latest ./CinemaService
docker build -t booking-service-image:latest ./BookingService
docker build -t recommendation-service-image:latest ./RecommendationService

# ✓ Verify: docker images
```

### MySQL Won't Initialize
```powershell
# ✓ Wait 2-3 minutes
# ✓ Check logs: kubectl logs -n cinema-booking -l app=mysql
# ✓ Increase Docker Desktop RAM to 4GB+
# ✓ Check init-db.sql: kubectl get configmap -n cinema-booking init-db-script
```

### Services Can't Connect to MySQL
```powershell
# ✓ Verify MySQL is running: kubectl get pods -n cinema-booking
# ✓ Check connection string: jdbc:mysql://mysql:3306/[db]
# ✓ Verify credentials: root / 123456
# ✓ Test from pod: kubectl exec -it deployment/mysql -- mysql -uroot -p123456
```

### Port Forwarding Not Working
```powershell
# ✓ Kill existing processes: lsof -ti:8888 | xargs kill -9
# ✓ Restart port forward: kubectl port-forward -n cinema-booking svc/api-gateway 8888:8888
# ✓ Use different port: kubectl port-forward -n cinema-booking svc/api-gateway 9999:8888
```

---

## Performance Tips

### Increase Docker Desktop Resources
For smoother deployment:
- Memory: 4GB → 8GB
- CPU: 2 cores → 4 cores
- Disk: 30GB → 50GB

### Faster Initialization
- Pre-warm MySQL in Docker before K8s
- Use faster disk (SSD vs HDD)
- Disable unnecessary Docker Desktop features

### Monitor Performance
```powershell
kubectl top pods -n cinema-booking
kubectl top nodes
```

---

## File Manifest

```
k8s-simple/
├── all-in-one.yaml              # Main manifest (~2000 lines)
├── deploy.sh                    # Linux/Mac deployment (~150 lines)
├── deploy.ps1                   # Windows PowerShell (~180 lines)
├── deploy.bat                   # Windows Batch (~120 lines)
├── cleanup.sh                   # Resource cleanup (~40 lines)
├── monitor.sh                   # Monitoring tool (~100 lines)
├── README.md                    # Full documentation (~400 lines)
├── DEPLOYMENT_SUMMARY.md        # Deployment overview (~200 lines)
└── QUICK_REFERENCE.md          # This file (~300 lines)
```

---

## Important Notes

✅ **What's Working**
- Single-file all-in-one manifest
- Automatic database initialization
- Service auto-discovery via Kubernetes DNS
- Resource limits configured
- Health checks for MySQL
- Multiple deployment script options
- Comprehensive monitoring and cleanup

⚠️ **Limitations (Development Only)**
- Ephemeral storage (data lost on restart)
- Hardcoded credentials
- No TLS/SSL encryption
- No network policies
- No RBAC configuration
- Single-node cluster (Docker Desktop)

🔐 **NOT for Production**
Use proper secrets management, persistent storage, and security policies for production deployments.

---

## Quick Start Template

Copy-paste this for quick setup:

```powershell
# Build images (one-time)
foreach ($svc in @("ApiGateway", "UserService", "MovieService", "CinemaService", "BookingService", "RecommendationService")) {
    cd $svc
    $imageName = if ($svc -eq "ApiGateway") { "api-gateway-image" } else { $svc.ToLower() + "-service-image" }
    docker build -t "${imageName}:latest" .
    cd ..
}

# Deploy
cd k8s-simple
.\deploy.ps1

# Access
kubectl port-forward -n cinema-booking svc/api-gateway 8888:8888
# Now visit: http://localhost:8888

# Monitor
bash monitor.sh

# Cleanup when done
bash cleanup.sh
```

---

**Last Updated**: 2025-12-08
**Status**: Production-Ready for Docker Desktop
**Support**: See README.md for detailed troubleshooting
