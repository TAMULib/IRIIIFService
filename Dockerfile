# Settings.
ARG USER_ID=3001
ARG USER_NAME=iriif
ARG SOURCE_DIR=/$USER_NAME/source

# Maven stage.
FROM maven:3-openjdk-11-slim as maven
ARG USER_ID
ARG USER_NAME
ARG SOURCE_DIR

# Create the user and group (use a high ID to attempt to avoid conflicts).
RUN groupadd --non-unique -g $USER_ID $USER_NAME && \
    useradd --non-unique -d /$USER_NAME -m -u $USER_ID -g $USER_ID $USER_NAME

# Update the system.
RUN apt-get update && apt-get upgrade -y

# Set deployment directory.
WORKDIR $SOURCE_DIR

# Copy files over.
COPY ./pom.xml ./pom.xml
COPY ./src ./src

# Assign file permissions.
RUN chown -R ${USER_ID}:${USER_ID} ${SOURCE_DIR}

# Login as user.
USER $USER_NAME

# Build.
RUN mvn package -Pjar -DskipTests=true

# Switch to Normal JRE Stage.
FROM openjdk:11-jre-slim
ARG USER_ID
ARG USER_NAME
ARG SOURCE_DIR

# Create the user and group (use a high ID to attempt to avoid conflicts).
RUN groupadd --non-unique -g $USER_ID $USER_NAME && \
    useradd --non-unique -d /$USER_NAME -m -u $USER_ID -g $USER_ID $USER_NAME

# Login as user.
USER $USER_NAME

# Set deployment directory.
WORKDIR /$USER_NAME

# Copy over the built artifact from the maven image.
COPY --from=maven $SOURCE_DIR/target/ROOT.jar ./iriif.jar

# Run java command.
CMD ["java", "-jar", "./iriif.jar"]
