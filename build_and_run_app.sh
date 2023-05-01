# !/bin/bash
mvn clean package -Dmaven.test.skip=true
java -jar target/salesmanager-*-SNAPSHOT.jar
rm *db