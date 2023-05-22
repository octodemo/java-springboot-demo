# !/bin/bash
LOCAL_IP=$(ifconfig | grep 'inet ' | grep -Fv 127.0.0.1 | awk '{print $2}')
docker stop app-test
docker rm app-test
docker rmi myapp-img:v1
mvn liquibase:rollback -Dliquibase.rollbackCount=999 -Dliquibase.url=jdbc:oracle:thin:@${LOCAL_IP}:49161/xe -Dliquibase.username=system -Dliquibase.password=oracle -Dliquibase.changeLogFile=db/changelog/changelog_version-3.2.oracle.sql
mvn liquibase:tag -Dliquibase.tag=v3.2 -Dliquibase.url=jdbc:oracle:thin:@${LOCAL_IP}:49161/xe -Dliquibase.username=system -Dliquibase.password=oracle -Dliquibase.changeLogFile=db/changelog/changelog_version-3.2.oracle.sql
mvn clean package
docker build -t myapp-img:v1 .
docker run -e CHANGELOG_VERSION=changelog_version-3.2.oracle.sql -e HOST_IP=$LOCAL_IP -t -d -p 80:8086 --name app-test myapp-img:v1
docker ps -a
echo "Waiting for 80 seconds for the app to start"
sleep 80
docker logs app-test