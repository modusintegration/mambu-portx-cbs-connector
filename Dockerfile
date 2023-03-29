FROM adoptopenjdk/openjdk11:jdk-11.0.8_10-centos-slim
VOLUME /tmp

WORKDIR /app

COPY ./target/mambu-portx-cbs-connector-1.0.71.jar /app/app.jar
COPY ./agent/opentelemetry-javaagent.jar /etc/agent/opentelemetry-javaagent.jar

EXPOSE 8080 8090

ENTRYPOINT ["java", "-javaagent:/etc/agent/opentelemetry-javaagent.jar" , "-jar", "/app/app.jar"]
