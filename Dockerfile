FROM adoptopenjdk/openjdk11:jdk-11.0.8_10-centos-slim
VOLUME /tmp

WORKDIR /app
 
COPY ./target/mambu-portx-cbs-connector-1.0.21.jar /app/app.jar

EXPOSE 8080 8090

ENTRYPOINT java $JAVA_OPTS -jar /app/app.jar
