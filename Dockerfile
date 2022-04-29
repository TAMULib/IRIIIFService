# Settings.
ARG USER_ID=3001
ARG USER_NAME=iriifservice
ARG HOME_DIR=/$USER_NAME
ARG SOURCE_DIR=/$HOME_DIR/source

# Maven stage.
FROM maven:3-openjdk-11-slim as maven
ARG USER_ID
ARG USER_NAME
ARG HOME_DIR
ARG SOURCE_DIR

# Create the group (use a high ID to attempt to avoid conflits).
RUN groupadd -g $USER_ID $USER_NAME

# Create the user (use a high ID to attempt to avoid conflits).
RUN useradd -d $HOME_DIR -m -u $USER_ID -g $USER_ID $USER_NAME

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
RUN ["mvn", "package", "-Pjar", "-DskipTests=true"]

# Copy over the built artifact.
COPY ./target/ROOT.jar $HOME_DIR/iriif.jar

# Switch to Normal JRE Stage.
FROM openjdk:11-jre-slim as runtime
ARG USER_ID
ARG USER_NAME
ARG HOME_DIR
ARG SOURCE_DIR

# Create the group (use a high ID to attempt to avoid conflits).
RUN groupadd -g $USER_ID $USER_NAME

# Create the user (use a high ID to attempt to avoid conflits).
RUN useradd -d $HOME_DIR -m -u $USER_ID -g $USER_ID $USER_NAME

# Login as user.
USER $USER_NAME

# Set deployment directory.
WORKDIR $HOME_DIR

# Copy over the built artifact from the maven image.
COPY --from=maven $SOURCE_DIR/target/ROOT.jar ./iriif.jar

# Run java command.
RUN ["java", "-jar", "./iriif.jar"]
