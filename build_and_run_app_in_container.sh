#!/bin/bash

# This script will build the app and run it in a docker container

# Function to perform housekeeping tasks
cleanup() {
    echo "Performing cleanup..."
    # Stop and remove the app container if it exists
    docker stop app-test
    docker rm app-test
    # Stop and remove the postgresql container if it exists
    docker stop postgres_container
    docker rm postgres_container
    # Remove image if exists
    docker rmi myapp-img:v1
    # Remove volume if exists
    docker volume rm postgresql-data
}

# Trap the EXIT signal to perform cleanup
trap cleanup EXIT

# Getting local ip address from ifconfig 
LOCAL_IP=$(ifconfig | grep 'inet ' | grep -Fv 127.0.0.1 | awk '{print $2}')
# Run a postgresql container with a volume to persist data
docker run -d -p 5432:5432 --name postgres_container -e POSTGRES_PASSWORD=Password123 -v postgresql-data:/var/lib/postgresql/data postgres
# Run any previous migrations and tag the latest version with Liquibase
mvn liquibase:tag -Dliquibase.tag=v3.2 -Dliquibase.url=jdbc:postgresql://${LOCAL_IP}:5432/postgres -Dliquibase.username=postgres -Dliquibase.password=Password123 -Dliquibase.changeLogFile=db/changelog/changelog_version-3.3.xml
# Build the app and create a docker image from local dockerfile
mvn clean package
docker build -t myapp-img:v1 .
docker run -d -e USER_NAME=postgres -e PASSWORD=Password123 -e CHANGELOG_VERSION=changelog_version-3.3.xml -e DB_URL=jdbc:postgresql://${LOCAL_IP}:5432/postgres -t -p 80:8086 --name app-test myapp-img:v1
docker ps -a
echo "Waiting for 10 seconds for the app to start"
sleep 10
# Print the logs of the app from the container
docker logs -f app-test
