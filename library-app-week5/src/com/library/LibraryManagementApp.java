package com.library;

import com.library.exception.DuplicateIsbnException;
import com.library.exception.InventoryException;
import com.library.inventory.Book;
import com.library.inventory.BookInventory;
import com.library.io.ConsoleIO;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * LibraryManagementApp.java  (INTEGRATED & PACKAGED — Week 5)
 *
 * Entry point of the application. This class does exactly one job — wiring
 * the menu to the right BookInventory operation — and delegates everything
 * else: ConsoleIO owns all Scanner/System.out calls (Week 4), BookInventory
 * owns all business logic (Week 2), and InventoryException unifies error
 * handling (Week 4).
 *
 * Week 5 additions (integration/deployment hardening — see the developer
 * documentation for the full rationale):
 *   1. Command-line flags (--version, --help) so the packaged JAR behaves
 *      like a normal command-line tool an ops engineer would expect,
 *      instead of only ever launching straight into the interactive menu.
 *   2. Graceful handling of an unexpectedly closed input stream (e.g. the
 *      JAR is run under a process supervisor that closes stdin, or input
 *      is piped from a file that runs out of lines). Previously this
 *      crashed with an uncaught NoSuchElementException and a raw stack
 *      trace; it now exits with a clean one-line message.
 */
public class LibraryManagementApp {

    public static final String APP_NAME = "Library Inventory Management System";
    public static final String APP_VERSION = "1.0.0";

    private final ConsoleIO io = new ConsoleIO();
    private final BookInventory inventory = new BookInventory();

    /**
     * Command pattern: each menu number maps to the operation that
     * handles it. run() no longer needs to know HOW MANY menu options
     * exist or branch on each one individually — it just looks the
     * choice up. Option 6 (Exit) is intentionally not registered here;
     * it is handled once in run() since it changes the loop's control
     * flow rather than performing an inventory operation.
     */
    private final Map<Integer, Runnable> menuActions = new LinkedHashMap<>();

    public LibraryManagementApp() {
        menuActions.put(1, this::handleAddBook);
        menuActions.put(2, this::handleListBooks);
        menuActions.put(3, this::handleUpdateBook);
        menuActions.put(4, this::handleDeleteBook);
        menuActions.put(5, this::handleSearchBook);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "--version" -> System.out.println(APP_NAME + " v" + APP_VERSION);
                case "--help" -> printUsage();
                default -> {
                    System.out.println("Unknown option: " + args[0]);
                    printUsage();
                    System.exit(1);
                }
            }
            return; // flags are informational only — they don't start the interactive menu
        }
        new LibraryManagementApp().run();
    }

    private static void printUsage() {
        System.out.println(APP_NAME + " v" + APP_VERSION);
        System.out.println("Usage: java -jar library-inventory.jar [option]");
        System.out.println("Options:");
        System.out.println("  (no option)   Start the interactive menu-driven CLI");
        System.out.println("  --version     Print the application version and exit");
        System.out.println("  --help        Print this usage message and exit");
    }

    private void run() {
        io.printBanner();
        seedSampleData();

        boolean running = true;
        try {
            while (running) {
                io.printMenu();
                int choice = io.readMenuChoice();

                if (choice == 6) {
                    io.printSuccess("Exiting. Goodbye!");
                    running = false;
                } else {
                    Runnable action = menuActions.get(choice);
                    if (action != null) {
                        action.run();
                    } else {
                        io.printError("Invalid choice. Please enter a number between 1 and 6.");
                    }
                }
            }
        } catch (NoSuchElementException e) {
            // Input stream ended unexpectedly (stdin closed, piped input ran
            // out, etc.) — exit cleanly instead of an uncaught stack trace.
            System.out.println("\nInput stream closed. Exiting.");
        } finally {
            io.close();
        }
    }

    // ---------------------------------------------------------------
    // Functional interface + shared executor for Week 4's DRY fix:
    // every handler below that can fail now reports its own success
    // message on the happy path and lets InventoryException /
    // IllegalArgumentException bubble up to ONE place instead of each
    // handler repeating its own try/catch.
    // ---------------------------------------------------------------

    @FunctionalInterface
    private interface InventoryOperation {
        void run() throws InventoryException;
    }

    private void execute(InventoryOperation operation) {
        try {
            operation.run();
        } catch (InventoryException | IllegalArgumentException e) {
            io.printError(e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Menu handlers — each is now just "gather input, delegate to
    // BookInventory, report the result"; no repeated try/catch.
    // ---------------------------------------------------------------

    private void handleAddBook() {
        System.out.println("\n-- Add New Book --");
        String isbn = io.readNonBlankString("Enter ISBN: ");
        String title = io.readNonBlankString("Enter title: ");
        String author = io.readNonBlankString("Enter author: ");
        int year = io.readValidYear("Enter publication year: ");

        execute(() -> {
            inventory.addBook(new Book(isbn, title, author, year));
            io.printSuccess("Book added successfully.");
        });
    }

    private void handleListBooks() {
        System.out.println("\n-- All Books (" + inventory.size() + ") --");
        io.printBooks(inventory.listBooks());
    }

    private void handleUpdateBook() {
        System.out.println("\n-- Update Book --");
        String isbn = io.readNonBlankString("Enter ISBN of the book to update: ");

        execute(() -> {
            Book existing = inventory.findBook(isbn);
            System.out.println("Current details: " + existing);
            System.out.println("Leave a field blank and press Enter to keep its current value.");

            String newTitle = io.readLine("New title [" + existing.getTitle() + "]: ");
            String newAuthor = io.readLine("New author [" + existing.getAuthor() + "]: ");
            Integer newYear = io.readOptionalYear(
                    "New publication year [" + existing.getPublicationYear() + "] (blank to keep): ");

            inventory.updateBook(isbn, newTitle, newAuthor, newYear);
            io.printSuccess("Book updated successfully.");
        });
    }

    private void handleDeleteBook() {
        System.out.println("\n-- Delete Book --");
        String isbn = io.readNonBlankString("Enter ISBN of the book to delete: ");

        execute(() -> {
            inventory.deleteBook(isbn);
            io.printSuccess("Book deleted successfully.");
        });
    }

    private void handleSearchBook() {
        System.out.println("\n-- Search Book --");
        String isbn = io.readNonBlankString("Enter ISBN to search for: ");

        execute(() -> {
            Book book = inventory.findBook(isbn);
            System.out.println("Found:");
            io.printBook(book);
        });
    }

    /** Pre-loads a couple of sample books so the app is immediately demonstrable. */
    private void seedSampleData() {
        try {
            inventory.addBook(new Book("978-0134685991", "Effective Java", "Joshua Bloch", 2018));
            inventory.addBook(new Book("978-0596009205", "Head First Design Patterns", "Eric Freeman", 2004));
        } catch (DuplicateIsbnException e) {
            // Cannot happen on a fresh, empty inventory — kept only to satisfy the compiler.
        }
    }
}
