# Library Book Inventory Management System — Week 5 (Integration, Deployment & Documentation)

Junior Java Developer Internship (YuvaIntern — NSDC Job Simulation)
**Author:** Pulkit ([pulkitcode-bit](https://github.com/pulkitcode-bit))

This is the final, integrated build of the application developed across
Weeks 1–5: designed in Week 1, implemented in Week 2, tested in Week 3,
refactored in Week 4, and packaged for deployment in Week 5.

**For the full write-up — architecture, deployment process, and future
maintenance strategy — see `DEVELOPER_DOCUMENTATION.md`
(also submitted as `Week5_Developer_Documentation.docx`).**

## Quick Start

```bash
# Build (compiles, runs the 33-test suite, packages a runnable JAR)
./deploy/build.sh

# Run
./deploy/run.sh
# or directly:
java -jar dist/library-inventory-1.0.0.jar

# Check version / usage without starting the interactive menu
java -jar dist/library-inventory-1.0.0.jar --version
java -jar dist/library-inventory-1.0.0.jar --help
```

A pre-built JAR is already included at `dist/library-inventory-1.0.0.jar`
so it can be run immediately without a build step, if preferred.

## What's New This Week

- **CLI flags** (`--version`, `--help`) so the packaged JAR behaves like a
  normal command-line tool, not only an interactive-only program.
- **Graceful shutdown** on unexpected end-of-input (previously an
  uncaught `NoSuchElementException` and a raw stack trace; now a clean
  one-line exit message).
- **`deploy/build.sh`** — one command that compiles, runs the full Week 3
  test suite, and packages a runnable JAR with a proper manifest
  (`Main-Class` set, so `java -jar ...` works with no extra flags).
- **`deploy/run.sh`** — launches the packaged JAR, building it first if
  needed.
- **`deploy/Dockerfile`** — multi-stage container build for future
  containerized deployment (see the developer documentation for an
  important honesty note about this file's testing status).
- **`DEVELOPER_DOCUMENTATION.md`** — the full handover document.

## Project Structure

```
library-app-week5/
├── README.md                         (this file)
├── DEVELOPER_DOCUMENTATION.md        (full write-up, 1500+ words)
├── deploy/
│   ├── build.sh                      compile + test + package
│   ├── run.sh                        launch the packaged JAR
│   └── Dockerfile                    containerized deployment (see note above)
├── dist/
│   └── library-inventory-1.0.0.jar   pre-built runnable JAR
├── lib/
│   └── junit-platform-console-standalone.jar
├── src/com/library/                  application source (see documentation for full breakdown)
└── test/com/library/inventory/       JUnit 5 test suite (33 tests, unchanged since Week 3)
```
