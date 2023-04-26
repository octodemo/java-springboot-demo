FROM openjdk:21-slim-buster
RUN mkdir workspace
WORKDIR /workspace/
COPY target/* .
EXPOSE 8086
CMD ["/bin/sh", "-c", "java -Doracle.jdbc.timezoneAsRegion=false -jar SalesManager-*-SNAPSHOT.jar --spring.datasource.url=jdbc:oracle:thin:@172.16.5.4:49161/xe"]