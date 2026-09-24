#!/usr/bin/env bash
# run.sh — launches the packaged JAR (build it first with build.sh if
# dist/library-inventory.jar doesn't exist yet). Any arguments given to
# this script are passed straight through to the application, so
# ./deploy/run.sh --version and ./deploy/run.sh --help both work.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
JAR_PATH="$PROJECT_ROOT/dist/library-inventory.jar"

if [[ ! -f "$JAR_PATH" ]]; then
    echo "No build found at $JAR_PATH — building first..."
    "$SCRIPT_DIR/build.sh"
fi

java -jar "$JAR_PATH" "$@"
