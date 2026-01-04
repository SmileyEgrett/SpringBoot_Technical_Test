#!/bin/sh
# Maven Wrapper script

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd "$(dirname "$0")" && pwd)}"
MAVEN_WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

if [ -r "$MAVEN_WRAPPER_PROPERTIES" ]; then
    MAVEN_URL=$(grep -E "^distributionUrl=" "$MAVEN_WRAPPER_PROPERTIES" | cut -d'=' -f2-)
fi

MAVEN_HOME="${MAVEN_HOME:-$HOME/.m2/wrapper/dists/apache-maven-3.9.6}"
MAVEN_CMD="$MAVEN_HOME/bin/mvn"

if [ ! -x "$MAVEN_CMD" ]; then
    echo "Downloading Maven..."
    mkdir -p "$MAVEN_HOME"
    MAVEN_ZIP="/tmp/maven.zip"
    curl -sL "$MAVEN_URL" -o "$MAVEN_ZIP"
    unzip -q "$MAVEN_ZIP" -d "$HOME/.m2/wrapper/dists"
    mv "$HOME/.m2/wrapper/dists/apache-maven-3.9.6"/* "$MAVEN_HOME/" 2>/dev/null || true
fi

exec "$MAVEN_CMD" "$@"
