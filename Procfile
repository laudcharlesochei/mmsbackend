release: java $JAVA_OPTS -Dspring.profiles.active=migrate -jar target/mms-api.jar
web:     java $JAVA_OPTS -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/mms-api.jar
