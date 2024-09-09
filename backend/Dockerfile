# Use the official Gradle image as the base image
FROM gradle:8.7.0-jdk21 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the build.gradle and settings.gradle files to the container
COPY build.gradle .
COPY settings.gradle .

# Copy the source code to the container
COPY src src

# Build the application using Gradle
RUN gradle build --no-daemon

# Use the official OpenJDK image as the base image for the runtime container
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the build container to the runtime container
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port that the Spring Boot app listens on
EXPOSE 8080

# Set the entry point for the runtime container
ENTRYPOINT ["java", "-jar", "app.jar"]
