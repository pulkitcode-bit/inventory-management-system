# Library Inventory Management System — Developer Documentation

**Week 5: Integration, Deployment & Handover**
Junior Java Developer Internship (YuvaIntern — NSDC Job Simulation)
Prepared by: Pulkit | GitHub: pulkitcode-bit | Version 1.0.0

> The formal submitted report is `Week5_Developer_Documentation.docx`
> (1,500+ words, required deliverable format). This file is a markdown
> mirror of the same content, kept in the repo for convenience.

## 1. Purpose of This Document

Written for the next developer who picks up this codebase — someone
else, or me in six months having forgotten the details. It explains what
the application does, how it's structured, how to build/run/test it, and
how to extend it safely.

## 2. Application Overview

A menu-driven, command-line Java application performing full CRUD
(Create, Read, Update, Delete) operations on a library's book inventory.
Built incrementally across five weeks: designed (Week 1), implemented
(Week 2), tested and debugged (Week 3), refactored and benchmarked
(Week 4), and integrated/packaged/documented (Week 5, this submission).
No external runtime dependencies; in-memory storage for the session;
distributed as a single runnable JAR.

## 3. How the Five Weeks Integrate Into This Build

| Week | Focus | What it left behind for this build |
|---|---|---|
| 1 | Design & architecture | Layered Controller→Service→Repository concept (here: CLI→Service) and the design-pattern choices for a future Spring Boot migration |
| 2 | Core CRUD implementation | `Book`, `BookInventory`, exception types, original `LibraryManagementApp` |
| 3 | Unit testing & debugging | 33-test JUnit 5 suite; 3 real bugs found and fixed |
| 4 | Refactoring & optimization | `ConsoleIO` extraction, Command pattern, shared `execute()` handler, `InventoryException` hierarchy, measured ~180–535x lookup speedup |
| 5 | Integration, deployment, docs | CLI flags, graceful shutdown, `build.sh`/`run.sh`, Dockerfile, this document |

## 4. Architecture

A simplified version of the Week 1 layered design: CLI/menu layer
(`LibraryManagementApp` + `ConsoleIO`) plays the Controller role, and
`BookInventory` plays the Service role. There's no separate Repository
layer since data is held directly in memory rather than behind a
persistence framework.

| Package | Key Classes | Responsibility |
|---|---|---|
| `com.library` | `LibraryManagementApp` | Entry point, CLI flags, Command-pattern menu dispatch |
| `com.library.io` | `ConsoleIO` | All Scanner input / System.out output |
| `com.library.inventory` | `Book`, `BookInventory` | Domain model + all business logic (CRUD, validation) |
| `com.library.exception` | `InventoryException` + 2 subtypes | Business-rule failure exceptions, unified base type |

Flow: `ConsoleIO` reads/validates input → `LibraryManagementApp` calls
`BookInventory` inside the shared `execute()` helper → `BookInventory`
applies business rules on its `LinkedHashMap<String, Book>` → any
`InventoryException`/`IllegalArgumentException` is caught in one place.

## 5. Technology Stack

- **Language:** Java 17, standard library only (zero runtime dependencies)
- **Test dependency:** JUnit 5 Jupiter, bundled as a standalone jar (no Maven needed)
- **Build tooling:** plain `javac`/`jar` via `deploy/build.sh`
- **Packaging:** single runnable JAR with a `Main-Class` manifest entry

## 6. Design Patterns in the Shipped Code

The Week 1 document proposed 7 patterns for a full Spring Boot version;
this CLI scope doesn't need the web/persistence-layer patterns
(Repository, DTO, Observer, Factory remain the natural next step — see
Section 11.3). What's actually in the shipped code:

- **Layered Architecture** — CLI vs. Service split
- **Singleton (implicitly)** — `ConsoleIO`/`BookInventory` constructed once, shared for the process lifetime
- **Command Pattern (new in Week 4)** — `menuActions` map turns each menu choice into an invokable command, replacing a switch statement

## 7. Build Tooling: Why Plain javac/jar Instead of Maven

Deliberate choice: zero third-party runtime dependencies means a full
Maven/Gradle setup would add overhead without adding capability.
`deploy/build.sh` does everything a build tool would here — compile,
test, package with a manifest — in ~60 lines of plain shell, no internet
required. If real dependencies get added later (e.g. Spring Boot per the
original design), the existing `src/`/`test/` layout already follows
Maven's standard-layout convention, so migration would be straightforward.

## 8. Building and Packaging

```bash
./deploy/build.sh            # compile + test + package
./deploy/build.sh --skip-tests   # faster local loop
```

Four steps: clean → compile `src/` → compile+run `test/` (fails the
build on any test failure) → package into
`dist/library-inventory-1.0.0.jar` with a `Main-Class` manifest entry.
A pre-built JAR is already included in this submission.

## 9. Deployment / Running the Application

### 9.1 Direct JAR execution (primary supported path)

```bash
java -jar dist/library-inventory-1.0.0.jar          # interactive menu
java -jar dist/library-inventory-1.0.0.jar --version
java -jar dist/library-inventory-1.0.0.jar --help
```

Chosen because the app is a stateless, single-user, in-memory CLI tool
with no network surface — a runnable JAR needs only "have Java" on any OS.

### 9.2 `deploy/run.sh`

Builds the JAR first if missing, then launches it, forwarding arguments.

### 9.3 Containerized deployment (Dockerfile — honesty note)

`deploy/Dockerfile` is a standard multi-stage build (JDK to compile, JRE
to run). **It could not be build-tested in the sandbox used to prepare
this submission** (no Docker daemon, no registry access). Everything
else here (JAR, build.sh, run.sh) *was* actually built and run with real
captured output. Verify with `docker build` + `docker run -i` before
relying on it operationally — the `-i` flag is required since this is an
interactive, stdin-reading app.

### 9.4 Exit Codes

- Normal exit / `--version` / `--help`: **0**
- Unknown flag: prints usage, exits **1**
- Stdin closed unexpectedly: graceful one-line message, exits **0** (no stack trace)

## 10. Testing and Quality Assurance

The 33-case JUnit 5 suite from Week 3 (`BookTest`, `BookInventoryTest`)
was carried forward unmodified through Week 4's refactor and this week's
changes, and re-verified passing as part of every `build.sh` run (which
fails the build on any test failure).

```bash
javac -cp "build/classes:lib/junit-platform-console-standalone.jar" -d build/test-classes $(find test -name "*.java")
java -jar lib/junit-platform-console-standalone.jar execute -cp build/classes -cp build/test-classes --scan-classpath build/test-classes --details=tree
```

Manual regression also covered this week's additions: `--version`,
`--help`, an unknown flag (confirmed exit code 1), and a deliberately
truncated input stream (confirmed graceful shutdown, no stack trace).

## 11. Extending the Application

**11.1 Adding a new menu option:** add a handler method (gather input via
`ConsoleIO`, wrap the `BookInventory` call in `execute()`), register it
in `menuActions` in the constructor, update `ConsoleIO.printMenu()`.

**11.2 Adding a new Book field:** add field/getter/setter to `Book.java`,
update `TABLE_ROW_FORMAT`/`TABLE_HEADER`, add validation in
`BookInventory` if needed, add tests to `BookInventoryTest`.

**11.3 Migrating to a real database:** `BookInventory`'s public method
signatures don't expose the `LinkedHashMap` behind them. A future
implementation backed by Spring Data JPA + MySQL (the Repository pattern
from Week 1) could replace it without `LibraryManagementApp`/`ConsoleIO`
needing to change at all, as long as signatures and exception contracts
are preserved.

**11.4 Conventions:** business rules stay in `BookInventory`; new
business-rule exceptions should extend `InventoryException`; every new
`BookInventory` behavior gets a happy-path test + an edge-case test.

## 12. Known Limitations and Future Roadmap

- No persistence — data lost on exit (Section 11.3 is the fix)
- Single-user, single-process — no concurrency control
- No network interface — the original Week 1 REST layer was never built (out of scope for this CLI deliverable)
- Dockerfile unverified in a real Docker environment (Section 9.3)
- No structured logging — direct stdout/stderr via `ConsoleIO`

## 13. Maintenance Strategy

Version lives in one constant, `LibraryManagementApp.APP_VERSION`,
surfaced via `--version` — bump it whenever a change is shipped. The test
suite is the primary safety net: run `deploy/build.sh` (fails on any test
failure) before considering any change to `BookInventory`/`Book`/the
exception types done. For CLI-flag behavior and graceful shutdown (not
covered by the automated suite), re-run the manual steps in Section 10.

## 14. Conclusion

This submission is the application in its complete, integrated form: the
architecture planned in Week 1, implemented in Week 2, hardened by real
bugs found in Week 3, structurally improved and measurably optimized in
Week 4, and packaged into a deployable JAR with CLI ergonomics, graceful
failure handling, and this documentation in Week 5. Everything described
as "working" here was actually executed while preparing this submission
— with the one explicitly flagged exception of the Dockerfile, which
needs build-verification in a real Docker environment before operational use.
