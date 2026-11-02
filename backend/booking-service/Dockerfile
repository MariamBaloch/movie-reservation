FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build --chown=10001:10001 /app/target/booking-service-0.0.1-SNAPSHOT.jar /app/booking-service.jar

USER 10001
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/booking-service.jar"]
