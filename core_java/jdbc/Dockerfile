FROM openjdk:8-alpine

# Create the directory structure
RUN mkdir -p /usr/local/app/src/main/resources

# Copy the JAR and properties file
COPY target/jdbc-1.0-SNAPSHOT.jar /usr/local/app/stockquote.jar
COPY src/main/resources/properties.txt /usr/local/app/src/main/resources/properties.txt

WORKDIR /usr/local/app

ENTRYPOINT ["java", "-jar", "stockquote.jar"]
