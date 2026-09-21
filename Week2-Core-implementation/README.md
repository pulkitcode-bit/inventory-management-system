# Library Book Inventory Management System (Java CLI)

Week 2 Task — Junior Java Developer Internship (YuvaIntern — NSDC Job Simulation)
**Author:** Pulkit ([pulkitcode-bit](https://github.com/pulkitcode-bit))

A command-line Java application that performs CRUD (Create, Read, Update,
Delete) operations on a library's book inventory, storing all data in
memory using Java collections.

## Features

- **Add** a new book (title, author, ISBN, publication year)
- **List** all books in a formatted table
- **Update** an existing book's title / author / publication year (leave a
  field blank to keep its current value)
- **Delete** a book by ISBN
- **Search** a book by ISBN
- Input validation on every prompt (empty strings, non-numeric menu choices,
  invalid years) — the app re-prompts instead of crashing
- Custom checked exceptions (`BookNotFoundException`,
  `DuplicateIsbnException`) for clean, explicit error handling

## Project Structure

```
library-app/
├── README.md
└── src/
    └── com/
        └── library/
            ├── LibraryManagementApp.java      # main class — CLI/menu only
            ├── inventory/
            │   ├── Book.java                  # entity
            │   └── BookInventory.java         # CRUD logic (in-memory store)
            └── exception/
                ├── BookNotFoundException.java
                └── DuplicateIsbnException.java
```

The code is intentionally split into three layers — CLI (`LibraryManagementApp`),
business logic (`BookInventory`), and data (`Book`) — mirroring the layered
architecture used in the Week 1 design document, so the CRUD logic itself has
no dependency on `Scanner`/`System.out` and could be reused elsewhere.

## Data Storage

Books are stored in-memory in a `LinkedHashMap<String, Book>`, keyed by ISBN.
This is cleared when the program exits — there is no database or file
persistence in this task (that is intentionally left for a later stage of
the project, once Spring Boot + MySQL are introduced).

## How to Compile and Run

**Prerequisite:** JDK 17 or later installed (`java -version` / `javac -version`
to check).

1. Open a terminal in the `library-app` folder (the one containing `src/`).
2. Compile all source files into an `out` folder:

   ```bash
   javac -d out $(find src -name "*.java")
   ```

   On Windows (PowerShell), compile each package explicitly instead:

   ```powershell
   javac -d out src/com/library/*.java src/com/library/inventory/*.java src/com/library/exception/*.java
   ```

3. Run the application:

   ```bash
   java -cp out com.library.LibraryManagementApp
   ```

4. Use the on-screen menu (1–6) to add, list, update, delete, or search books.
   The app starts with two sample books already loaded so "List all books"
   isn't empty on first run.

## Sample Session

```
1. Add a new book
2. List all books
3. Update a book
4. Delete a book
5. Search a book by ISBN
6. Exit
Enter your choice (1-6): 1

-- Add New Book --
Enter ISBN: 978-1234567890
Enter title: Clean Code
Enter author: Robert Martin
Enter publication year: 2008
Book added successfully.
```

## Notes on Design Choices

- **ISBN as the map key:** ISBN uniquely identifies a book in a real library,
  so it is used as the `Map` key for O(1) lookup/update/delete. `setIsbn()`
  is deliberately not provided on `Book` — changing an ISBN is done by
  deleting the old entry and adding a new one, to avoid the object's ISBN
  and the map's key going out of sync.
- **Checked exceptions:** `BookNotFoundException` and `DuplicateIsbnException`
  are checked exceptions so the compiler forces every call site to handle
  the "not found" / "duplicate" case explicitly, instead of the program
  crashing with an unhandled exception.
- **Separation of concerns:** `BookInventory` has no `import java.util.Scanner`
  or any console I/O — it is pure business logic, which makes it easy to
  unit-test in Week 3 without needing to simulate console input.
