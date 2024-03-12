#!/bin/bash
set -e

# Validate the input to ensure that the image tag is in the correct format
function validate_input() {
    if [[ ! "${1}" =~ ^.*:.*$ ]]; then
        echo "Invalid image tag. Expected format: repo:tag"
        exit 1
    fi
}

# Get the image tag from the specified image
function get_image_tag() {
    echo "${1}" | rev | cut -d ":" -f 1 | rev
}

# Get the current image tag for the specified container
function get_current_image_tag() {
    kubectl get pods -l app=spring-app -o json | jq -r ".items[] | select(.metadata.name | contains(\"${1}\")) | .spec.containers[0].image" | cut -d ":" -f 2 | head -n 1
}

function upgrade_release() {
    # The first argument is the debug flag
    # The second argument is the v1 image tag
    # The third argument is the v2 image tag
    local debug_flag=""
    # If the debug flag is set, enable verbose output for debugging
    if [ "${1}" -eq 1 ]; then
        debug_flag="--debug"
    fi
    # If the v1 or v2 image tag is set, set the flag for the helm upgrade command
    local set_flag=""
    if [ -n "${2}" ]; then
        set_flag="v1.springAppContainer.image.tag=${2}"
    fi
    # If the v2 image tag is set, append it to the set flag for the helm upgrade command with a comma separator
    if [ -n "${3}" ]; then
        if [ -n "${set_flag}" ]; then
            set_flag="${set_flag},"
        fi
        # Append the v2 image tag to the set flag
        set_flag="${set_flag}v2.springAppContainer.image.tag=${3}"
    fi
    # If the set flag is set, append the flag to the helm upgrade command with the --set flag
    if [ -n "${set_flag}" ]; then
        set_flag="--set ${set_flag}"
    fi
    # Deploy the release with the new image tag and wait for the rollout to finish before exiting the script with a success status
    # If the deployment is not ready within the timeout, the script will exit with an error status and the deployment will be marked as failed
    # A rollback will be triggered by the helm controller if the deployment fails
    # The --atomic flag ensures that the deployment will be rolled back if the upgrade fails
    # The --install flag ensures that the release will be installed if it does not exist
    # The --debug flag enables verbose output for debugging
    # The --set flag sets the specified values on the command line
    trap 'catch_errors' ERR
    catch_errors() {
    echo "Error occurred during deployment. Rolled back to version: $(helm history app-release --max 1 --output json | jq -r '.[0].app_version')"
    exit 1
    }
    helm upgrade --install --atomic app-release ./my-chart ${set_flag} ${debug_flag}
    kubectl rollout status deployment/app-release-my-chart-v1
    kubectl rollout status deployment/app-release-my-chart-v2
    echo "Current deployment status:"
    kubectl get deployments
    echo "Current releases history:"
    helm history app-release
}

validate_input "${1}"
IMAGE_TAG=$(get_image_tag "${1}")
DEBUG=${2:-0}
COMMIT_MESSAGE=$(git log --format=%B -n 1 HEAD)

# If the app deployment exists, get the current image tags for v1 and v2 and upgrade the release with the new image tag
if kubectl get deployments app-release-my-chart-v1 2>/dev/null && kubectl get deployments app-release-my-chart-v2 2>/dev/null; then
    CURRENT_V1_IMAGE_TAG=$(get_current_image_tag "v1")
    CURRENT_V2_IMAGE_TAG=$(get_current_image_tag "v2")
    echo "Current v1 image tag: $CURRENT_V1_IMAGE_TAG"
    echo "Current v2 image tag: $CURRENT_V2_IMAGE_TAG"
    # If the commit message contains [v1], upgrade the release with the new image tag for v1
    if [[ "$COMMIT_MESSAGE" == \[v1\]* ]]; then
        echo "Update is for v1. Not setting v2.image.tag."
        upgrade_release "${DEBUG}" "${IMAGE_TAG}" "${CURRENT_V2_IMAGE_TAG}"
    else
        # If the commit message contains [v2], upgrade the release with the new image tag for v2
        echo "Update is for v2. Setting v2.image.tag to: $IMAGE_TAG."
        upgrade_release "${DEBUG}" "${CURRENT_V1_IMAGE_TAG}" "${IMAGE_TAG}"
    fi
else
    # If the app deployment does not exist, upgrade the release with the new image tag for v2
    echo "App deployment does not exist. Setting v2.image.tag to: $IMAGE_TAG."
    upgrade_release "${DEBUG}" "" "${IMAGE_TAG}"
fi