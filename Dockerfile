FROM openjdk:21-slim-buster
ENV HOST_IP=localhost
ENV USER_NAME=system
ENV PASSWORD=oracle
ENV CHANGELOG_VERSION=master.xml
RUN mkdir workspace
WORKDIR /workspace/
COPY target/salesmanager-*-SNAPSHOT.jar .
EXPOSE 8086
CMD ["/bin/sh", "-c", "java -Doracle.jdbc.timezoneAsRegion=false -jar salesmanager-*-SNAPSHOT.jar --spring.datasource.url=jdbc:oracle:thin:@${HOST_IP}:49161/xe --spring.datasource.username=${USER_NAME} --spring.datasource.password=${PASSWORD} --spring.liquibase.change-log=classpath:db/changelog/${CHANGELOG_VERSION}"]
