# !/bin/bash
if [ -z "$1" ]
then
    SEARCH_FEATURE=""
else
    SEARCH_FEATURE="-DenableSearchFeature=$1"
fi

cleanup() {
    docker stop redis_container > /dev/null 2>&1 || true
    docker rm redis_container > /dev/null 2>&1 || true
    rm *db > /dev/null 2>&1 || true
}

# Trap the EXIT signal to perform cleanup
# trap cleanup EXIT

set -e # Exit immediately if a command exits with a non-zero status.
mvn clean package -Dmaven.test.skip=true
docker run -d -p 6379:6379 --name redis_container redis || true
java $SEARCH_FEATURE -jar target/salesmanager-*-SNAPSHOT.jar --spring.redis.host=localhost --spring.redis.port=6379 --spring.redis.mode=standalone --server.port=8086 --spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
