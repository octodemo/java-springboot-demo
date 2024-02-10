#!/bin/bash

# This script will build the app and run it in a docker container

# Function to perform housekeeping tasks
cleanup() {
    echo "Performing cleanup..."
    # Stop and remove the app container if it exists
    docker stop app-test || true
    docker rm app-test || true
    # Stop and remove the postgresql container if it exists
    docker stop postgres_container || true
    docker rm postgres_container || true
    # Remove image if exists
    docker rmi myapp-img:v1 || true
    # Remove volume if exists
    docker volume rm postgresql-data || true
    # Stop and remove the redis container if it exists
    docker stop redis_container || true
    docker rm redis_container || true
}

# Trap the EXIT signal to perform cleanup
trap cleanup EXIT

set -e # Exit immediately if a command exits with a non-zero status.
# Getting local ip address from ifconfig 
LOCAL_IP=$(ifconfig | grep 'inet ' | grep -Fv 127.0.0.1 | awk '{print $2}')
# Run a postgresql container with a volume to persist data
docker run -d -p 5432:5432 --name postgres_container -e POSTGRES_PASSWORD=Password123 -v postgresql-data:/var/lib/postgresql/data postgres
# [Optional] Run any previous migrations and tag the latest version with Liquibase
# mvn liquibase:tag -Dliquibase.tag=v3.2 -Dliquibase.url=jdbc:postgresql://${LOCAL_IP}:5432/postgres -Dliquibase.username=postgres -Dliquibase.password=Password123 -Dliquibase.changeLogFile=db/changelog/changelog_version-3.3.xml
# Package the application and run migrations
mvn clean package
# Create a docker image from local dockerfile
docker build -t myapp-img:v1 .
# Run the app in a container with environment variables for the database connection and credentials
docker run -d -e USER_NAME=postgres -e PASSWORD=Password123 -e CHANGELOG_VERSION=changelog_version-3.3.xml -e DB_URL=jdbc:postgresql://${LOCAL_IP}:5432/postgres -e REDIS_HOST=$LOCAL_IP -t -p 80:8086 --name app-test myapp-img:v1
# spin up a Redis container for caching user sessions and data
docker run -d -p 6379:6379 --name redis_container redis
# Show the running containers and their status
docker ps -a
echo "Waiting for 10 seconds for the app to start"
# Wait for the app to start
sleep 10
# Print the logs of the app from the container
docker logs -f app-test
