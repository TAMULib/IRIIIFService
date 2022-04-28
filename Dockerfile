FROM maven:3-openjdk-11-slim as maven

# Copy pom.xml.
COPY pom.xml pom.xml

# Copy src files.
COPY src src

# Build.
RUN mvn -DskipTests=true -Pdocker clean package

# Final base image.
FROM openjdk:11-jre-slim

# Set deployment directory.
WORKDIR /IRIIIFService

# Copy over the built artifact from the maven image.
COPY --from=maven /target/ROOT.jar /IRIIIFService/ROOT.jar

# Settings.
ENV SERVER_PORT='9000'
ENV SPRING_SQL_INIT_PLATFORM='h2'
ENV SPRING_DATASOURCE_DRIVERCLASSNAME='org.h2.Driver'
ENV SPRING_DATASOURCE_URL='jdbc:h2:mem:AZ;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE'
ENV SPRING_JPA_DATABASEPLATFORM='org.hibernate.dialect.H2Dialect'
ENV SPRING_JPA_HIBERNATE_DDLAUTO='create-drop'
ENV SPRING_DATASOURCE_USERNAME='spring'
ENV SPRING_DATASOURCE_PASSWORD='spring'

# Expose port.
EXPOSE ${SERVER_PORT}

# Run java command.
CMD java -jar /IRIIIFService/ROOT.jar
