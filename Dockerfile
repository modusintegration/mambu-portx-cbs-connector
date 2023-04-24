FROM eclipse-temurin:11.0.18_10-jre

RUN apt update && apt install --only-upgrade curl -y
RUN apt update && apt install --only-upgrade libcurl4 -y
RUN apt update && apt install --only-upgrade libcurl3-gnutls -y

VOLUME /tmp

RUN useradd -u 1000 -m -d /app app-user
USER app-user

WORKDIR /app

COPY ./target/*.jar /app/app.jar

EXPOSE 8080 8090

ENTRYPOINT java $JAVA_OPTS -jar /app/app.jar