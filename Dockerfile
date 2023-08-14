FROM maven:3.9.0-eclipse-temurin-11

RUN apt update && \
    apt install --only-upgrade curl libcurl4 libcurl3-gnutls -y
VOLUME /tmp

WORKDIR /app

COPY ./target/mambu-portx-cbs-connector-1.0.78-SNAPSHOT.jar /app/app.jar
COPY ./agent/opentelemetry-javaagent.jar /etc/agent/opentelemetry-javaagent.jar

EXPOSE 8080 8090

ENTRYPOINT ["java", "-javaagent:/etc/agent/opentelemetry-javaagent.jar" , "-jar", "/app/app.jar"]
