# Library Book Inventory Management System — Week 4 (Refactoring & Optimization)

Junior Java Developer Internship (YuvaIntern — NSDC Job Simulation)
**Author:** Pulkit ([pulkitcode-bit](https://github.com/pulkitcode-bit))

This folder contains the Week 4 refactor of the Week 2/3 CLI application.
Behaviour is unchanged (verified by re-running the Week 3 test suite
unmodified — still 33/33 passing); what changed is structure, readability,
and a measured performance comparison. See
`Week4_Refactoring_Report.docx` (submitted alongside this zip) for the full
before/after write-up.

## What Changed This Week

1. **Modularization** — all Scanner/System.out calls moved out of
   `LibraryManagementApp` into a new `ConsoleIO` class. The main class
   shrank from ~230 lines mixing I/O + menu logic to under 120 lines that
   only wire menu choices to `BookInventory` operations.
2. **Command pattern** — the menu's six-branch `switch` statement was
   replaced with a `Map<Integer, Runnable>` (`menuActions`), so adding a
   future menu option means registering one map entry instead of editing
   a growing switch.
3. **DRY: shared error handling** — four nearly-identical try/catch blocks
   (one per add/update/delete/search handler) were replaced by a single
   `execute(InventoryOperation)` helper, enabled by a new common
   `InventoryException` base class that `BookNotFoundException` and
   `DuplicateIsbnException` both now extend.
4. **DRY: shared table format** — the tabular column format string, which
   was duplicated three times (`Book.toString()` plus two
   `System.out.printf` header calls), is now one constant,
   `Book.TABLE_ROW_FORMAT` / `Book.TABLE_HEADER`.
5. **Measured performance comparison** — a benchmark (`benchmark/`,
   excluded from the main app) compares the current `LinkedHashMap`-based
   `BookInventory` against a deliberately naive `ArrayList` + linear-scan
   implementation, to put a real number behind the "O(1) vs O(n)" claim.
   See the report for the actual timing results.

## Project Structure

```
library-app-week4/
├── README.md
├── lib/
│   └── junit-platform-console-standalone.jar
├── src/
│   └── com/library/
│       ├── LibraryManagementApp.java        # refactored: Command pattern + execute() helper
│       ├── io/
│       │   └── ConsoleIO.java                # NEW — all console I/O, extracted for modularity
│       ├── inventory/
│       │   ├── Book.java                     # + shared TABLE_ROW_FORMAT constant (DRY)
│       │   └── BookInventory.java             # unchanged logic; exceptions now share a base type
│       └── exception/
│           ├── InventoryException.java        # NEW — common base for the two exceptions below
│           ├── BookNotFoundException.java
│           └── DuplicateIsbnException.java
├── benchmark/                                  # NOT part of the shipped app — evidence only
│   └── com/library/benchmark/
│       ├── NaiveListBookInventory.java         # deliberately naive O(n) "before" comparison
│       └── InventoryBenchmark.java             # measures and prints real timing numbers
└── test/
    └── com/library/inventory/                  # UNCHANGED from Week 3 — still 33/33 passing
        ├── BookTest.java
        └── BookInventoryTest.java
```

## How to Compile and Run

**Prerequisite:** JDK 17+ (`javac -version` to check). The JUnit 5 jar
needed for tests is already in `lib/` — no internet/Maven required.

### 1. Compile and run the application

```bash
javac -d out $(find src -name "*.java")
java -cp out com.library.LibraryManagementApp
```

### 2. Run the (unchanged) Week 3 test suite against the refactored code

```bash
javac -cp "out:lib/junit-platform-console-standalone.jar" -d test-out $(find test -name "*.java")
java -jar lib/junit-platform-console-standalone.jar execute -cp out -cp test-out --scan-classpath test-out --details=tree
```

Expected: **33 tests found, 33 successful, 0 failed** — proof the refactor
didn't change behaviour.

### 3. Run the performance benchmark

```bash
javac -cp out -d benchmark-out $(find benchmark -name "*.java")
java -cp "out:benchmark-out" com.library.benchmark.InventoryBenchmark
```

This populates 20,000 books into both a naive `ArrayList`-based inventory
and the real `BookInventory`, runs 20,000 random ISBN lookups against
each, and prints the actual measured time for both plus the speedup
factor. See the report for a captured run and its interpretation.

*(On Windows, replace `:` with `;` in the `-cp` arguments above.)*
