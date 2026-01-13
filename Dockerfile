# Use Java 21 runtime
FROM eclipse-temurin:21-jre

# Copy the compiled JAR into the container
COPY target/*.jar app.jar

# Run the JAR
ENTRYPOINT ["java","-jar","/app.jar"]
