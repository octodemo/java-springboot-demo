#!/bin/bash

# Script to monitor cluster resources during deployments
# Usage: ./monitor-resources.sh

echo "🔍 Monitoring AKS cluster resources..."
echo "=================================="

# Check node capacity and allocation
echo "📊 Node Resource Capacity:"
kubectl describe nodes | grep -E "(Name:|cpu:|memory:|Allocated resources)"

echo ""
echo "📈 Current Resource Usage:"
kubectl top nodes 2>/dev/null || echo "❌ Metrics server not available. Install with: kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml"

echo ""
echo "🏃 Running Pods Resource Usage:"
kubectl top pods --all-namespaces 2>/dev/null || echo "❌ Metrics server not available"

echo ""
echo "🎯 Resource Requests vs Limits by Namespace:"
kubectl describe nodes | grep -A 10 "Allocated resources"

echo ""
echo "⚠️  Pending Pods (Resource constraints):"
kubectl get pods --all-namespaces --field-selector=status.phase=Pending

echo ""
echo "🔄 Current Deployments Status:"
kubectl get deployments --all-namespaces

echo ""
echo "📋 Horizontal Pod Autoscaler Status:"
kubectl get hpa --all-namespaces

echo ""
echo "🛡️  Pod Disruption Budgets:"
kubectl get pdb --all-namespaces
