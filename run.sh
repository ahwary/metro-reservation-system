#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
LIB_DIR="$ROOT_DIR/lib"
APP_JAR="$ROOT_DIR/out/MetroReservationTN.jar"

if [ ! -f "$APP_JAR" ]; then
    "$ROOT_DIR/build.sh"
fi

JARS=("$LIB_DIR"/*.jar)
CLASSPATH="$(IFS=:; echo "${JARS[*]}")"

java \
    --module-path "$CLASSPATH" \
    --add-modules javafx.controls,javafx.fxml \
    -cp "$APP_JAR:$CLASSPATH" \
    Metro.Main
