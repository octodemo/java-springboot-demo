#!/bin/bash
set -e

function validate_input() {
    if [[ ! "${1}" =~ ^.*:.*$ ]]; then
        echo "Invalid image tag. Expected format: repo:tag"
        exit 1
    fi
}

function get_image_tag() {
    echo "${1}" | cut -d ":" -f 2
}

function get_current_image_tag() {
    kubectl get pods -l app=spring-app -o json | jq -r ".items[] | select(.metadata.name | contains(\"${1}\")) | .spec.containers[0].image" | cut -d ":" -f 2 | head -n 1
}

function upgrade_release() {
    local debug_flag=""
    if [ "${1}" -eq 1 ]; then
        debug_flag="--debug"
    fi
    helm upgrade --install --atomic app-release ./my-chart --set v1.springAppContainer.image.tag=${2},v2.springAppContainer.image.tag=${3} ${debug_flag}
    kubectl rollout status deployment/app-release-my-chart-v1
    kubectl rollout status deployment/app-release-my-chart-v2
}

validate_input "${1}"
IMAGE_TAG=$(get_image_tag "${1}")
DEBUG=${2:-0}
COMMIT_MESSAGE=$(git log --format=%B -n 1 HEAD)

if kubectl get deployments app-release-my-chart-v1 2>/dev/null && kubectl get deployments app-release-my-chart-v2 2>/dev/null; then
    CURRENT_V1_IMAGE_TAG=$(get_current_image_tag "v1")
    CURRENT_V2_IMAGE_TAG=$(get_current_image_tag "v2")
    echo "Current v1 image tag: $CURRENT_V1_IMAGE_TAG"
    echo "Current v2 image tag: $CURRENT_V2_IMAGE_TAG"
    if [[ "$COMMIT_MESSAGE" == \[v1\]* ]]; then
        echo "Update is for v1. Not setting v2.image.tag."
        upgrade_release "${DEBUG}" "${IMAGE_TAG}" "${CURRENT_V2_IMAGE_TAG}"
    else
        echo "Update is for v2. Setting v2.image.tag to: $IMAGE_TAG."
        upgrade_release "${DEBUG}" "${CURRENT_V1_IMAGE_TAG}" "${IMAGE_TAG}"
    fi
else
    echo "App deployment does not exist. Setting v2.image.tag to: $IMAGE_TAG."
    upgrade_release "${DEBUG}" "" "${IMAGE_TAG}"
fi