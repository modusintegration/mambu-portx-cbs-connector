FROM eclipse-temurin:11.0.18_10-jre

RUN apt update && apt install --only-upgrade curl -y
RUN apt update && apt install --only-upgrade libcurl4 -y
RUN apt update && apt install --only-upgrade libcurl3-gnutls -y
RUN apt update && apt install --only-upgrade libssl3 -y
RUN apt update && apt install --only-upgrade libssh-4 -y
RUN apt update && apt install --only-upgrade libfreetype6 -y
RUN apt update && apt install --only-upgrade libncursesw6 -y
RUN apt update && apt install --only-upgrade libtinfo6 -y
RUN apt update && apt install --only-upgrade libncurses6 -y
RUN apt update && apt install --only-upgrade ncurses-bin -y
RUN apt update && apt install --only-upgrade perl -y
RUN apt update && apt install --only-upgrade openssl -y
RUN apt update && apt install --only-upgrade libcap2


VOLUME /tmp

RUN useradd -u 1000 -m -d /app app-user
USER app-user

WORKDIR /app

COPY ./target/*.jar /app/app.jar
COPY ./agent/opentelemetry-javaagent.jar /etc/agent/opentelemetry-javaagent.jar

EXPOSE 8080 8090

ENTRYPOINT ["java", "-javaagent:/etc/agent/opentelemetry-javaagent.jar" , "-jar", "/app/app.jar"]
