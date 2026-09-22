# Library Book Inventory Management System — Week 3 (Unit Testing & Debugging)

Junior Java Developer Internship (YuvaIntern — NSDC Job Simulation)
**Author:** Pulkit ([pulkitcode-bit](https://github.com/pulkitcode-bit))

This folder extends the Week 2 CLI application with a JUnit 5 test suite,
plus fixes for three bugs that testing exposed. See
`Week3_Bug_Report_and_Test_Results.docx` (submitted alongside this zip,
outside this folder) for the full write-up of each bug, how it was found,
and how it was fixed.

## What's New This Week

- **33 JUnit 5 test cases** across `BookTest` and `BookInventoryTest`,
  covering every CRUD operation's happy path and edge cases.
- **Three real bugs found and fixed** while writing tests (see the report):
  1. `addBook()` could silently overwrite a book on a duplicate ISBN.
  2. `updateBook()` treated a whitespace-only field as "provided" and
     overwrote existing data with blank spaces.
  3. `BookInventory` did not validate blank fields or nonsensical
     publication years at all — only the CLI layer did, so calling the
     service layer directly bypassed validation entirely.
- **Refactor:** the year-boundary check that used to be duplicated inside
  `LibraryManagementApp` now lives in one place —
  `Book.isValidPublicationYear()` — and both the CLI and `BookInventory`
  call it, so there is a single source of truth that unit tests can target
  directly.

## Project Structure

```
library-app-week3/
├── README.md
├── lib/
│   └── junit-platform-console-standalone.jar   # JUnit 5 (all-in-one runner)
├── src/
│   └── com/library/
│       ├── LibraryManagementApp.java
│       ├── inventory/
│       │   ├── Book.java
│       │   └── BookInventory.java
│       └── exception/
│           ├── BookNotFoundException.java
│           └── DuplicateIsbnException.java
└── test/
    └── com/library/inventory/
        ├── BookTest.java             # unit tests for Book.isValidPublicationYear()
        └── BookInventoryTest.java    # unit tests for all CRUD operations
```

## How to Compile and Run

**Prerequisite:** JDK 17+ installed (`javac -version` to check). The JUnit 5
console-standalone jar needed to run the tests is already included in `lib/`
— no internet access or Maven/Gradle setup required.

### 1. Compile the application source

```bash
javac -d out $(find src -name "*.java")
```

### 2. Run the application itself

```bash
java -cp out com.library.LibraryManagementApp
```

### 3. Compile the tests

```bash
javac -cp "out:lib/junit-platform-console-standalone.jar" -d test-out $(find test -name "*.java")
```

*(On Windows, replace the `:` in `-cp` with `;`.)*

### 4. Run the test suite

```bash
java -jar lib/junit-platform-console-standalone.jar execute -cp out -cp test-out --scan-classpath test-out --details=tree
```

Expected result: **33 tests found, 33 successful, 0 failed.**

## Test Coverage Summary

| Class | What's tested |
|---|---|
| `BookTest` | Boundary values for `isValidPublicationYear()` — just-below-minimum, exact minimum (1450), current year, one year in the future, zero/negative years, typical historical years |
| `BookInventoryTest` | `addBook`: valid add, duplicate ISBN, blank/null ISBN, blank title, blank author, future/zero/negative year · `listBooks`: empty inventory, insertion order preserved · `findBook`: not-found case · `updateBook`: full update, null fields keep originals, **whitespace-only fields keep originals (regression test for Bug #2)**, not-found case, invalid year rejected without mutating state · `deleteBook`: successful delete, not-found case, deleting one book doesn't affect others · `size()` tracks add/delete correctly |

See the separate bug report document for the actual pass/fail console output
captured while each bug was still present, and after each fix.
