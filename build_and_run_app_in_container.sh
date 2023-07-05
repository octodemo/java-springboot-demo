# !/bin/bash
# Getting local ip address from ifconfig 
LOCAL_IP=$(ifconfig | grep 'inet ' | grep -Fv 127.0.0.1 | awk '{print $2}')
# To run a local containerized oracle db, run the following command:
# docker run -e CHANGELOG_VERSION=changelog_version-3.2.oracle.sql -e DB_URL=jdbc:oracle:thin:@${LOCAL_IP}:49161/xe -t -d -p 80:8086 --name app-test myapp-img:v1
# Stop and remove the container if it exists
docker stop app-test
docker rm app-test
docker rmi myapp-img:v1
# Run any previous migrations and tag the latest version with Liquibase
mvn liquibase:rollback -Dliquibase.rollbackCount=999 -Dliquibase.url=jdbc:oracle:thin:@${LOCAL_IP}:49161/xe -Dliquibase.username=system -Dliquibase.password=oracle -Dliquibase.changeLogFile=db/changelog/changelog_version-3.2.oracle.sql
mvn liquibase:tag -Dliquibase.tag=v3.2 -Dliquibase.url=jdbc:oracle:thin:@${LOCAL_IP}:49161/xe -Dliquibase.username=system -Dliquibase.password=oracle -Dliquibase.changeLogFile=db/changelog/changelog_version-3.2.oracle.sql
# Build the app and create a docker image from local dockerfile
mvn clean package
docker build -t myapp-img:v1 .
docker run -e CHANGELOG_VERSION=changelog_version-3.2.oracle.sql -e DB_URL=jdbc:oracle:thin:@${LOCAL_IP}:49161/xe -t -d -p 80:8086 --name app-test myapp-img:v1
docker ps -a
echo "Waiting for 80 seconds for the app to start"
sleep 80
# Print the logs of the app from the container
docker logs app-test