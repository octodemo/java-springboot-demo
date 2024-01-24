# !/bin/bash
cleanup() {
    docker stop redis_container > /dev/null 2>&1
    docker rm redis_container > /dev/null 2>&1
    rm *db > /dev/null 2>&1
}

# Trap the EXIT signal to perform cleanup
trap cleanup EXIT

mvn clean package -Dmaven.test.skip=true
docker run -d -p 6379:6379 --name redis_container redis
java -jar target/salesmanager-*-SNAPSHOT.jar --spring.redis.host=localhost --spring.redis.port=6379 --spring.redis.mode=standalone --server.port=8086 --spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
