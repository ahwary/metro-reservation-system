#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
BUILD_DIR="$ROOT_DIR/out"
CLASSES_DIR="$BUILD_DIR/classes"
LIB_DIR="$ROOT_DIR/lib"
SOURCES_FILE="$BUILD_DIR/sources.txt"

rm -rf "$BUILD_DIR"
mkdir -p "$CLASSES_DIR"

JARS=("$LIB_DIR"/*.jar)
if [ ! -e "${JARS[0]}" ]; then
    echo "No dependency jars found in $LIB_DIR"
    exit 1
fi

CLASSPATH="$(IFS=:; echo "${JARS[*]}")"
find "$ROOT_DIR/src" -name '*.java' -print0 | while IFS= read -r -d '' source_file; do
    printf '"%s"\n' "$source_file"
done > "$SOURCES_FILE"

javac --release 21 \
    --module-path "$CLASSPATH" \
    --add-modules javafx.controls,javafx.fxml \
    -cp "$CLASSPATH" \
    -d "$CLASSES_DIR" \
    @"$SOURCES_FILE"

cp -R "$ROOT_DIR/src/Metro/"*.fxml "$CLASSES_DIR/Metro/"

jar --create --file "$BUILD_DIR/MetroReservationTN.jar" \
    --main-class Metro.Main \
    -C "$CLASSES_DIR" .

echo "Built $BUILD_DIR/MetroReservationTN.jar"
