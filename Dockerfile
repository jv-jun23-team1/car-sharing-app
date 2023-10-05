# Builder stage
FROM openjdk:17-jdk-slim as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract
LABEL authors="kvlad"
# Final stage
FROM openjdk:17-jdk-slim
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

ADD https://github.com/vishnubob/wait-for-it/raw/master/wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh

CMD ["./wait-for-it.sh", "mysqldb:$MYSQL_DOCKER_PORT", "--", "java", "org.springframework.boot.loader.JarLauncher"]
EXPOSE 8080

