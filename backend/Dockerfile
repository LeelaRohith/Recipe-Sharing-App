FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

#COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar .

COPY --from=build /app/target/recipesharingapp.jar .

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/recipesharingapp.jar"]

#docker build -t recipe-sharing-app .
#docker tag recipe-sharing-app leelarohith/recipe-sharing-app:latest
# docker push leelarohith/recipe-sharing-app:latest
