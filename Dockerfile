# Use Java 17
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy project files
COPY . .

# Give permission to mvnw
RUN chmod +x mvnw

# Build the project
#RUN ./mvnw clean package
RUN ./mvnw clean package -DskipTests

# Run the jar file
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]
