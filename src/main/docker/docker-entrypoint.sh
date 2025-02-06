#!/bin/sh
set -e

echo 'Starting kafka-producer Spring Boot App'

# Enables application to take PID 1 and receive SIGTERM sent by Docker stop command.
# See here https://docs.docker.com/engine/reference/builder/#/entrypoint
exec java $JAVA_OPTS \
       -Djava.security.egd=file:/dev/./urandom -jar \
       ${APP_HOME}/$ARTIFACT_NAME
