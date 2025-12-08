#!/bin/bash

# Cinema Booking System - Kubernetes Monitoring Script
# This script provides real-time monitoring of the deployed system

NAMESPACE="cinema-booking"

echo "================================================"
echo "Cinema Booking System - K8s Monitoring"
echo "================================================"
echo ""

# Function to display menu
show_menu() {
    echo ""
    echo "Select option:"
    echo "1. Show pod status"
    echo "2. Show service status"
    echo "3. Show deployment status"
    echo "4. Show MySQL logs"
    echo "5. Show API Gateway logs"
    echo "6. Show all resource details"
    echo "7. Port forward API Gateway (8888:8888)"
    echo "8. Port forward MySQL (3306:3306)"
    echo "9. Exit"
    echo ""
    read -p "Enter your choice (1-9): " choice
}

# Function to show pod status
show_pods() {
    echo ""
    echo "Pod Status:"
    echo "==========="
    kubectl get pods -n $NAMESPACE --no-headers=true -o custom-columns=NAME:.metadata.name,STATUS:.status.phase,RESTARTS:.status.containerStatuses[0].restartCount,AGE:.metadata.creationTimestamp
}

# Function to show service status
show_services() {
    echo ""
    echo "Service Status:"
    echo "==============="
    kubectl get svc -n $NAMESPACE --no-headers=true -o custom-columns=NAME:.metadata.name,TYPE:.spec.type,CLUSTER-IP:.spec.clusterIP,PORT:.spec.ports[0].port
}

# Function to show deployment status
show_deployments() {
    echo ""
    echo "Deployment Status:"
    echo "=================="
    kubectl get deployments -n $NAMESPACE --no-headers=true -o custom-columns=NAME:.metadata.name,DESIRED:.spec.replicas,READY:.status.readyReplicas,AVAILABLE:.status.availableReplicas,AGE:.metadata.creationTimestamp
}

# Function to show MySQL logs
show_mysql_logs() {
    echo ""
    echo "MySQL Logs (last 50 lines):"
    echo "==========================="
    kubectl logs -n $NAMESPACE -l app=mysql --tail=50 --timestamps=true
}

# Function to show API Gateway logs
show_gateway_logs() {
    echo ""
    echo "API Gateway Logs (last 50 lines):"
    echo "================================="
    kubectl logs -n $NAMESPACE -l app=api-gateway --tail=50 --timestamps=true
}

# Function to show all resource details
show_all_details() {
    echo ""
    echo "All Resource Details:"
    echo "===================="
    kubectl describe all -n $NAMESPACE
}

# Function to port forward
port_forward_api() {
    echo ""
    echo "Port forwarding API Gateway to localhost:8888"
    echo "Press Ctrl+C to stop"
    kubectl port-forward -n $NAMESPACE svc/api-gateway 8888:8888
}

port_forward_mysql() {
    echo ""
    echo "Port forwarding MySQL to localhost:3306"
    echo "Press Ctrl+C to stop"
    kubectl port-forward -n $NAMESPACE svc/mysql 3306:3306
}

# Main loop
while true; do
    show_menu
    
    case $choice in
        1) show_pods ;;
        2) show_services ;;
        3) show_deployments ;;
        4) show_mysql_logs ;;
        5) show_gateway_logs ;;
        6) show_all_details ;;
        7) port_forward_api ;;
        8) port_forward_mysql ;;
        9) echo "Exiting..."; exit 0 ;;
        *) echo "Invalid choice. Please try again." ;;
    esac
done
