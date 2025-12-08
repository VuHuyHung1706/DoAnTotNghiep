# Cinema Booking System - Docker Desktop Kubernetes Deployment

This directory contains simplified Kubernetes deployment files for the Cinema Booking System, optimized for Docker Desktop's built-in Kubernetes cluster.

## Quick Start

### Prerequisites

1. **Docker Desktop** with Kubernetes enabled
   - Install: https://www.docker.com/products/docker-desktop
   - Enable Kubernetes:
     - Open Docker Desktop Settings
     - Navigate to Kubernetes tab
     - Check "Enable Kubernetes"
     - Click Apply

2. **kubectl** (Kubernetes Command Line Tool)
   - Usually included with Docker Desktop
   - Verify: `kubectl version --client`

3. **Service Docker Images**
   - Build all 6 service images before deployment
   - See "Building Service Images" section below

### Building Service Images

From the workspace root directory, build each service:

```bash
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

### Deploying to Kubernetes

```bash
# Grant execute permission to deploy script (Linux/Mac)
chmod +x deploy.sh

# Run deployment
./deploy.sh

# On Windows, use:
bash deploy.sh
```

The deployment script will:
1. Verify kubectl is installed
2. Check Docker Desktop Kubernetes connection
3. Validate all required Docker images
4. Deploy all resources (namespace, MySQL, services, deployments)
5. Wait for MySQL initialization (2-3 minutes)
6. Display deployment summary with access instructions

### Accessing the System

#### Option 1: Port Forwarding (Recommended for Local Testing)

```bash
# Forward API Gateway port
kubectl port-forward -n cinema-booking svc/api-gateway 8888:8888

# In another terminal, forward MySQL port
kubectl port-forward -n cinema-booking svc/mysql 3306:3306
```

Then access:
- API Gateway: `http://localhost:8888`
- MySQL: `localhost:3306`

#### Option 2: Using LoadBalancer Service

```bash
# Get external IP (may show <pending> on Docker Desktop)
kubectl get svc -n cinema-booking api-gateway

# On Docker Desktop, use: localhost:8888
# The LoadBalancer service is accessible via the service's external IP
```

## File Structure

```
k8s-simple/
├── all-in-one.yaml          # Single manifest with all resources
├── deploy.sh                # Deployment script
├── cleanup.sh               # Resource cleanup script
├── monitor.sh               # Monitoring and debugging tool
└── README.md               # This file
```

## Configuration Details

### Namespace
- **Name**: `cinema-booking`
- **Purpose**: Isolates all resources for easy management

### Database (MySQL 8.4.6)
- **Service Name**: `mysql`
- **Port**: 3306
- **Root Password**: `123456`
- **Databases Created**:
  - `userdb`: User management (accounts, customers, managers)
  - `moviedb`: Movie catalog (movies, actors, showtimes, reviews)
  - `cinemaadb`: Cinema locations (cinemas, rooms, seats)
  - `bookingdb`: Bookings (invoices, tickets)
  - `recommendationdb`: Recommendations (user preferences, history)

### Microservices

| Service | Port | Database | Container Port |
|---------|------|----------|-----------------|
| User Service | 8080 | userdb | 8080 |
| Movie Service | 8081 | moviedb | 8081 |
| Cinema Service | 8082 | cinemaadb | 8082 |
| Booking Service | 8083 | bookingdb | 8083 |
| Recommendation Service | 8084 | recommendationdb | 8084 |
| API Gateway | 8888 | - | 8888 |

### Database Connection URLs

Services connect to MySQL via internal Kubernetes DNS:
```
jdbc:mysql://mysql:3306/[database]?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
```

Where `mysql` is the Kubernetes service name, automatically resolved within the cluster.

## Monitoring and Debugging

### Using the Monitoring Script

```bash
chmod +x monitor.sh
./monitor.sh
```

Available options:
1. Show pod status
2. Show service status
3. Show deployment status
4. Show MySQL logs
5. Show API Gateway logs
6. Show all resource details
7. Port forward API Gateway
8. Port forward MySQL

### Manual kubectl Commands

```bash
# Show all resources
kubectl get all -n cinema-booking

# Show pods with detailed info
kubectl get pods -n cinema-booking -o wide

# Show deployment status
kubectl get deployments -n cinema-booking

# View logs
kubectl logs -n cinema-booking deployment/mysql --tail=100
kubectl logs -n cinema-booking deployment/api-gateway --tail=100

# Describe a specific pod
kubectl describe pod -n cinema-booking [pod-name]

# Get shell access to a pod
kubectl exec -it -n cinema-booking [pod-name] -- /bin/bash
```

## Test Data

The system is initialized with test data:

### User Accounts
- **Admin**: `admin` / `123456`
- **Staff**: `staff` / `123456`
- **Regular Users**: `user1` - `user5` with test data

### Sample Data
- **Cinemas**: 4 locations (CGV, Lotte, Galaxy, BHD Star)
- **Rooms**: 16 total (4 per cinema, 56 seats each)
- **Seats**: 896 total (8 rows × 7 columns per room)
- **Movies**: 5 sample films with genres, actors, showtimes
- **Showtimes**: 5 sample showtimes
- **Bookings**: 4 sample invoices with 4 tickets

## Troubleshooting

### Issue: "Cannot connect to Kubernetes cluster"

**Solution**: Enable Kubernetes in Docker Desktop
- Open Docker Desktop → Settings → Kubernetes
- Check "Enable Kubernetes"
- Wait for "Kubernetes is running" message
- If needed, reset Kubernetes: Docker Desktop Settings → Kubernetes → Reset Kubernetes Cluster

### Issue: Services won't start / ImagePullBackOff error

**Solution**: Build and load Docker images
```bash
# Verify images exist
docker images | grep -E "api-gateway|user-service|movie-service|cinema-service|booking-service|recommendation-service"

# If missing, build them (see "Building Service Images" section)
```

### Issue: MySQL won't initialize

**Solution**: Check MySQL pod logs
```bash
kubectl logs -n cinema-booking -l app=mysql --tail=50
```

Common causes:
- MySQL pod needs more time (wait 2-3 minutes)
- Insufficient memory (increase Docker Desktop resources)

### Issue: Database connection errors

**Solution**: Verify MySQL is ready
```bash
kubectl exec -it -n cinema-booking deployment/mysql -- mysql -uroot -p123456 -e "SHOW DATABASES;"
```

If it fails, MySQL may not be fully initialized. Wait another minute.

### Issue: Services can't connect to MySQL

**Solution**: Verify service environment variables
```bash
kubectl describe deployment -n cinema-booking user-service

# Look for:
# - SPRING_DATASOURCE_URL should be: jdbc:mysql://mysql:3306/userdb...
# - SPRING_DATASOURCE_USERNAME should be: root
# - SPRING_DATASOURCE_PASSWORD should be: 123456
```

## Cleanup

To remove all deployed resources:

```bash
chmod +x cleanup.sh
./cleanup.sh

# Or manually:
kubectl delete namespace cinema-booking
```

This will delete:
- All pods
- All services
- All deployments
- All ConfigMaps
- The entire namespace

**Note**: This does NOT delete the Docker images. To remove them:
```bash
docker rmi api-gateway-image:latest
docker rmi user-service-image:latest
docker rmi movie-service-image:latest
docker rmi cinema-service-image:latest
docker rmi booking-service-image:latest
docker rmi recommendation-service-image:latest
```

## Resource Limits

Each service is configured with:
- **Memory Request**: 256 Mi
- **Memory Limit**: 512 Mi
- **CPU Request**: 250m
- **CPU Limit**: 500m

Adjust these in `all-in-one.yaml` if you have resource constraints or need more performance.

## Scaling

To scale a service to multiple replicas:

```bash
kubectl scale deployment -n cinema-booking [service-name] --replicas=3

# Example: Scale API Gateway to 3 replicas
kubectl scale deployment -n cinema-booking api-gateway --replicas=3
```

## Next Steps

1. **Access the API Gateway**: Use port forwarding to localhost:8888
2. **Test the services**: Use the provided Postman collection or curl
3. **Monitor the system**: Use the monitor.sh script
4. **Review logs**: Check service logs for any issues
5. **Develop and test**: Deploy new versions of services as needed

## Security Notes

⚠️ **WARNING**: This deployment is for **development and testing only** on Docker Desktop.

Not suitable for production because:
- Credentials are hardcoded (root/123456)
- No TLS/SSL encryption
- No authentication/authorization policies
- No network policies
- MySQL uses emptyDir storage (data is lost on pod restart)
- Services use imagePullPolicy: Never

For production deployments:
- Use Secrets for credentials
- Enable TLS/SSL
- Implement RBAC policies
- Use Network Policies
- Use persistent storage
- Use proper image registries
- Implement monitoring and alerting

## Support

For issues or questions:
1. Check the Troubleshooting section above
2. Review Kubernetes documentation: https://kubernetes.io/docs/
3. Check service logs: `kubectl logs -n cinema-booking -f deployment/[service-name]`
4. Check namespace events: `kubectl get events -n cinema-booking`

## Useful Resources

- **Docker Desktop Kubernetes**: https://docs.docker.com/desktop/kubernetes/
- **kubectl Cheat Sheet**: https://kubernetes.io/docs/reference/kubectl/cheatsheet/
- **Kubernetes Documentation**: https://kubernetes.io/docs/
- **Kubernetes API**: https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.29/
