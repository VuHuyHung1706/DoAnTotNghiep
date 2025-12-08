#!/bin/bash

# Cinema Booking System - Kubernetes Cleanup Script
# This script removes all resources deployed by the cinema booking system

set -e

NAMESPACE="cinema-booking"

echo "================================================"
echo "Cinema Booking System - K8s Cleanup"
echo "================================================"
echo ""
echo "⚠️  WARNING: This will delete all resources in namespace '$NAMESPACE'"
echo ""
read -p "Are you sure you want to proceed? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Cleanup cancelled"
    exit 0
fi

echo ""
echo "Deleting namespace and all resources..."

if kubectl delete namespace "$NAMESPACE" --ignore-not-found=true; then
    echo "✓ Namespace deleted successfully"
else
    echo "❌ Error deleting namespace"
    exit 1
fi

echo ""
echo "Waiting for namespace deletion to complete..."
sleep 5

echo "✓ Cleanup complete!"
echo ""
echo "To verify all resources are deleted:"
echo "  kubectl get namespace"
echo ""
