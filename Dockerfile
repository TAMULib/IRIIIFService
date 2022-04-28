# Settings.
ARG USER_NAME=iriifservice
ARG HOME_DIR=/$USER_NAME
ARG SOURCE_DIR=/$HOME_DIR/source

# Maven stage.
FROM maven:3-openjdk-11-slim as maven
ARG USER_NAME
ARG HOME_DIR
ARG SOURCE_DIR

# Create the group (use a high ID to attempt to avoid conflits).
RUN groupadd -g 3001 $USER_NAME

# Create the user (use a high ID to attempt to avoid conflits).
RUN useradd -d $HOME_DIR -m -u 3001 -g 3001 $USER_NAME

# Update the system.
RUN apt update && apt upgrade -y

# Set deployment directory.
WORKDIR $SOURCE_DIR

# Setup work directory sticky bit.
RUN chown 3001:3001 -R $HOME_DIR

# Login as user.
USER $USER_NAME

# Copy files over.
COPY --chown=3001:3001 ./pom.xml ./pom.xml
COPY --chown=3001:3001 ./src ./src

# Build.
RUN ["mvn", "package", "-Pjar", "-DskipTests=true"]

# Switch to Normal JRE Stage.
FROM openjdk:11-jre-slim as runtime
ARG USER_NAME
ARG HOME_DIR
ARG SOURCE_DIR

# Login as user.
USER $USER_NAME

# Set deployment directory.
WORKDIR $HOME_DIR

# Copy over the built artifact from the maven image.
COPY --chown=3001:3001 --from=maven $SOURCE_DIR/target/ROOT.jar ./iriif.jar

# Run java command.
CMD ["java", "-jar", "./iriif.jar"]
