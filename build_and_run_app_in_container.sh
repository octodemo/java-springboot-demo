# !/bin/bash
# Getting local ip address from ifconfig 
LOCAL_IP=$(ifconfig | grep 'inet ' | grep -Fv 127.0.0.1 | awk '{print $2}')
# To run a local containerized postgresql db, run the following command:
# docker run -d -p 5432:5432 --name postgres_container -e POSTGRES_PASSWORD=password -v postgresql-data:/var/lib/postgresql/data postgres
# Stop and remove the app container if it exists
docker stop app-test
docker rm app-test
# Remove image if exists
docker rmi myapp-img:v1
# Run any previous migrations and tag the latest version with Liquibase
mvn liquibase:rollback -Dliquibase.rollbackCount=999 -Dliquibase.url=jdbc:postgresql://${LOCAL_IP}:5432/postgres -Dliquibase.username=postgres -Dliquibase.password=password -Dliquibase.changeLogFile=db/changelog/changelog_version-3.3.xml
# jdbc:postgresql://hostname:5432/MY_TEST_DATABASE
mvn liquibase:tag -Dliquibase.tag=v3.2 -Dliquibase.url=jdbc:postgresql://${LOCAL_IP}:5432/postgres -Dliquibase.username=postgres -Dliquibase.password=password -Dliquibase.changeLogFile=db/changelog/changelog_version-3.3.xml
# Build the app and create a docker image from local dockerfile
mvn clean package
docker build -t myapp-img:v1 .
docker run -e USER_NAME=postgres -e PASSWORD=password -e CHANGELOG_VERSION=changelog_version-3.3.xml -e DB_URL=jdbc:postgresql://${LOCAL_IP}:5432/postgres -t -d -p 80:8086 --name app-test myapp-img:v1
docker ps -a
echo "Waiting for 80 seconds for the app to start"
sleep 80
# Print the logs of the app from the container
docker logs app-test