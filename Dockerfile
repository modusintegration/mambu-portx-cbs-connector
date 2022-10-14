FROM 854173534877.dkr.ecr.us-west-2.amazonaws.com/demos/base-image:builder-latest as maven

FROM maven as builder

COPY . $CODE_HOME

RUN mvn clean package

FROM openjdk:18-jdk-slim as runtime

RUN useradd -u 1000 -m -d /app app-user

USER app-user

WORKDIR /app

COPY --from=builder /build/target/*.jar mambu-portx-cbs-connector.jar

CMD java -jar mambu-portx-cbs-connector.jar

