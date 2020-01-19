FROM maven:3.5.2-jdk-8-alpine AS MAVEN_BUILD

COPY . /app
WORKDIR /app/
RUN mvn package -DskipTests

FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=MAVEN_BUILD /app/rest-app/target/rest-app-1.0-SNAPSHOT.jar /app/
ENTRYPOINT ["java", "-jar", "rest-app-1.0-SNAPSHOT.jar"]