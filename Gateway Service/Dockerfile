FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copy pom.xml first for better caching
COPY pom.xml .
# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B
# Copy source code
COPY src ./src
# Build the application
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache wget
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8072
ENTRYPOINT ["java", "-jar", "app.jar"]
