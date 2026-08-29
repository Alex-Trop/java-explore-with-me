FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
ENV TZ=Asia/Novosibirsk
ARG JAR_FILE=stats-service/stats-inner-service/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]