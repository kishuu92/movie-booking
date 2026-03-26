# Use lightweight Java image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar into container
COPY target/movie-booking-0.0.1-SNAPSHOT.jar app.jar

# Expose app port
EXPOSE 8081

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]