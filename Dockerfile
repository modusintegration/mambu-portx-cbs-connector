FROM amazoncorretto:17
VOLUME /tmp

WORKDIR /app

COPY ./target/*.jar /app/app.jar

EXPOSE 8080 8090

ENTRYPOINT java $JAVA_OPTS -jar /app/app.jar
