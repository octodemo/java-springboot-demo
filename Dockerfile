FROM openjdk:21-slim-buster
ENV DB_URL=jdbcConnectionString
ENV REDIS_HOST=redis
ENV USER_NAME=postgres
ENV PASSWORD=password
ENV CHANGELOG_VERSION=master.xml
ENV SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA=public
RUN mkdir workspace
WORKDIR /workspace/
COPY target/salesmanager-*-SNAPSHOT.jar .
EXPOSE 8086
CMD ["/bin/sh", "-c", "java -jar salesmanager-*-SNAPSHOT.jar --spring.datasource.url=${DB_URL} --spring.datasource.username=${USER_NAME} --spring.datasource.password=${PASSWORD} --spring.redis.port=6379 --spring.redis.host=${REDIS_HOST} --spring.liquibase.change-log=classpath:db/changelog/${CHANGELOG_VERSION}"]