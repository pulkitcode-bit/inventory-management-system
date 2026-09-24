#!/usr/bin/env bash
# build.sh — compiles, tests, and packages the Library Inventory Management
# System into a single runnable JAR under dist/.
#
# This is a deliberately plain shell + javac/jar script rather than a
# Maven/Gradle build. The application has zero third-party runtime
# dependencies (only JUnit, a TEST-scope dependency, is external), so a
# full build tool would add setup overhead without adding real capability
# here. See DEVELOPER_DOCUMENTATION.md, section "Build Tooling", for the
# reasoning and for how this would change if the app grew real dependencies.
#
# Usage:
#   ./deploy/build.sh            # compile + run tests + package
#   ./deploy/build.sh --skip-tests   # compile + package only (faster local loop)

set -euo pipefail  # fail fast on any error, undefined variable, or pipeline failure

# Resolve paths relative to the project root regardless of where this
# script is invoked from.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_ROOT"

APP_NAME="library-inventory"
VERSION="1.0.0"
MAIN_CLASS="com.library.LibraryManagementApp"
JUNIT_JAR="lib/junit-platform-console-standalone.jar"

OUT_DIR="build/classes"
TEST_OUT_DIR="build/test-classes"
DIST_DIR="dist"
JAR_NAME="${APP_NAME}-${VERSION}.jar"

SKIP_TESTS=false
if [[ "${1:-}" == "--skip-tests" ]]; then
    SKIP_TESTS=true
fi

echo "== 1/4  Cleaning previous build output =="
rm -rf "$OUT_DIR" "$TEST_OUT_DIR" "$DIST_DIR"
mkdir -p "$OUT_DIR" "$DIST_DIR"

echo "== 2/4  Compiling application source =="
javac -d "$OUT_DIR" $(find src -name "*.java")
echo "Compiled to $OUT_DIR"

if [[ "$SKIP_TESTS" == false ]]; then
    echo "== 3/4  Running test suite =="
    mkdir -p "$TEST_OUT_DIR"
    javac -cp "$OUT_DIR:$JUNIT_JAR" -d "$TEST_OUT_DIR" $(find test -name "*.java")
    java -jar "$JUNIT_JAR" execute \
        -cp "$OUT_DIR" -cp "$TEST_OUT_DIR" \
        --scan-classpath "$TEST_OUT_DIR" \
        --details=summary \
        --fail-if-no-tests
    echo "All tests passed."
else
    echo "== 3/4  Skipping tests (--skip-tests) =="
fi

echo "== 4/4  Packaging runnable JAR =="
MANIFEST_FILE="$(mktemp)"
{
    echo "Main-Class: $MAIN_CLASS"
    echo "Implementation-Title: Library Inventory Management System"
    echo "Implementation-Version: $VERSION"
} > "$MANIFEST_FILE"

jar cfm "$DIST_DIR/$JAR_NAME" "$MANIFEST_FILE" -C "$OUT_DIR" .
rm -f "$MANIFEST_FILE"

# Convenience symlink-style copy so run.sh always has a stable name to call,
# even as $VERSION changes across releases.
cp "$DIST_DIR/$JAR_NAME" "$DIST_DIR/${APP_NAME}.jar"

echo
echo "Build complete: $DIST_DIR/$JAR_NAME"
echo "Run it with:    java -jar $DIST_DIR/$JAR_NAME"
echo "Or:             ./deploy/run.sh"
