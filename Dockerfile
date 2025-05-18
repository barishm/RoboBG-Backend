# Use OpenJDK base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file (make sure you build the JAR first)
COPY target/robobg-app.jar robobg-app.jar

# Expose the port your Spring Boot app is running on (default is 8080)
EXPOSE 5000

# Run the JAR file
ENTRYPOINT ["java", "-jar", "robobg-app.jar"]
