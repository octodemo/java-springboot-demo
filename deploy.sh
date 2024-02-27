#!/bin/bash

# Get the commit message of the commit that triggered the workflow
COMMIT_MESSAGE=$(git log --format=%B -n 1 HEAD)

# Extract just the tagname from inputs.image_tag
IMAGE_TAG=$(echo "${1}" | cut -d ":" -f 2)

# Set debug flag from second argument, default to 0 if not set
DEBUG=${2:-0}

# Check if the app deployment exists
if kubectl get deployments app-release-my-chart-v1 2>/dev/null && kubectl get deployments app-release-my-chart-v2 2>/dev/null; then
    # Get the current v1 and v2 image tags from the app deployment in the cluster
    CURRENT_V1_IMAGE_TAG=$(kubectl get pods -l app=spring-app -o json | jq -r '.items[] | select(.metadata.name | contains("v1")) | .spec.containers[0].image' | cut -d ":" -f 2 | head -n 1)
    CURRENT_V2_IMAGE_TAG=$(kubectl get pods -l app=spring-app -o json | jq -r '.items[] | select(.metadata.name | contains("v2")) | .spec.containers[0].image' | cut -d ":" -f 2 | head -n 1)
    
    echo "Current v1 image tag: $CURRENT_V1_IMAGE_TAG"
    echo "Current v2 image tag: $CURRENT_V2_IMAGE_TAG"
    
    # If the commit message starts with [v1], it's an update for v1
    if [[ "$COMMIT_MESSAGE" == \[v1\]* ]]; then
        echo "Update is for v1. Not setting v2.image.tag."
        # Update the app without changing the v2 image but instead change the v1 image to IMAGE_TAG ensure the v2.image.tag is not changed from the values.yaml file
        helm upgrade --install app-release ./my-chart --set v1.springAppContainer.image.tag=$IMAGE_TAG,v2.springAppContainer.image.tag=$CURRENT_V2_IMAGE_TAG $(if [ "$DEBUG" -eq 1 ]; then echo "--debug"; fi)
    else
        # If the commit message does not start with [v1], it's an update for v2
        echo "Update is for v2. Setting v2.image.tag to: $IMAGE_TAG."
        # Update the app and change the v2 image to IMAGE_TAG
        helm upgrade --install app-release ./my-chart --set v2.springAppContainer.image.tag=$IMAGE_TAG,v1.springAppContainer.image.tag=$CURRENT_V1_IMAGE_TAG $(if [ "$DEBUG" -eq 1 ]; then echo "--debug"; fi)
    fi
else
    # If the app deployment does not exist
    echo "App deployment does not exist. Setting v2.image.tag to: $IMAGE_TAG."
    # Set v2.image.tag to IMAGE_TAG as a new deployment starting point and leave v1.image.tag as the default value in the values.yaml file
    helm upgrade --install app-release ./my-chart --set v2.springAppContainer.image.tag=$IMAGE_TAG $(if [ "$DEBUG" -eq 1 ]; then echo "--debug"; fi)
fi

# Wait for 30 seconds
sleep 30

# Get the status of the deployments
kubectl get deployments