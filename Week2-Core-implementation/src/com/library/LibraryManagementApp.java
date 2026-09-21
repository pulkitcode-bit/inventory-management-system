package com.library;

import com.library.exception.BookNotFoundException;
import com.library.exception.DuplicateIsbnException;
import com.library.inventory.Book;
import com.library.inventory.BookInventory;

import java.util.Scanner;

/**
 * LibraryManagementApp.java
 *
 * Entry point of the application. This class is only responsible for the
 * command-line interface: printing the menu, reading and validating user
 * input, and delegating the actual work to BookInventory. Keeping I/O
 * logic separate from business logic (see BookInventory) is a small but
 * deliberate design choice, in line with the layered architecture used
 * throughout this internship's project.
 */
public class LibraryManagementApp {

    private final Scanner scanner = new Scanner(System.in);
    private final BookInventory inventory = new BookInventory();

    public static void main(String[] args) {
        LibraryManagementApp app = new LibraryManagementApp();
        app.run();
    }

    /**
     * Main application loop: shows the menu, reads a choice, and keeps
     * looping until the user chooses to exit.
     */
    private void run() {
        System.out.println("=================================================");
        System.out.println(" LIBRARY BOOK INVENTORY MANAGEMENT SYSTEM");
        System.out.println("=================================================");

        seedSampleData(); // a couple of starter books so "List" isn't empty on first run

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1 -> handleAddBook();
                case 2 -> handleListBooks();
                case 3 -> handleUpdateBook();
                case 4 -> handleDeleteBook();
                case 5 -> handleSearchBook();
                case 6 -> {
                    System.out.println("Exiting. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please enter a number between 1 and 6.");
            }
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n--------------- MENU ---------------");
        System.out.println("1. Add a new book");
        System.out.println("2. List all books");
        System.out.println("3. Update a book");
        System.out.println("4. Delete a book");
        System.out.println("5. Search a book by ISBN");
        System.out.println("6. Exit");
        System.out.print("Enter your choice (1-6): ");
    }

    // ---------------------------------------------------------------
    // Menu handlers — each wraps one CRUD operation with input
    // validation and exception handling so the app never crashes on
    // bad input; it just prints an error and returns to the menu.
    // ---------------------------------------------------------------

    private void handleAddBook() {
        System.out.println("\n-- Add New Book --");
        String isbn = readNonBlankString("Enter ISBN: ");
        String title = readNonBlankString("Enter title: ");
        String author = readNonBlankString("Enter author: ");
        int year = readValidYear("Enter publication year: ");

        try {
            inventory.addBook(new Book(isbn, title, author, year));
            System.out.println("Book added successfully.");
        } catch (DuplicateIsbnException e) {
            // Business-rule violation (duplicate key) — handled gracefully,
            // the user is told exactly what went wrong and can retry.
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleListBooks() {
        System.out.println("\n-- All Books (" + inventory.size() + ") --");
        if (inventory.size() == 0) {
            System.out.println("The inventory is currently empty.");
            return;
        }
        System.out.printf("%-16s | %-30s | %-20s | %s%n", "ISBN", "TITLE", "AUTHOR", "YEAR");
        System.out.println("-".repeat(90));
        for (Book book : inventory.listBooks()) {
            System.out.println(book);
        }
    }

    private void handleUpdateBook() {
        System.out.println("\n-- Update Book --");
        String isbn = readNonBlankString("Enter ISBN of the book to update: ");

        try {
            Book existing = inventory.findBook(isbn);
            System.out.println("Current details: " + existing);
            System.out.println("Leave a field blank and press Enter to keep its current value.");

            System.out.print("New title [" + existing.getTitle() + "]: ");
            String newTitle = scanner.nextLine();

            System.out.print("New author [" + existing.getAuthor() + "]: ");
            String newAuthor = scanner.nextLine();

            System.out.print("New publication year [" + existing.getPublicationYear() + "] (blank to keep): ");
            String yearInput = scanner.nextLine();
            Integer newYear = null;
            if (!yearInput.isBlank()) {
                newYear = parseYearOrNull(yearInput);
                if (newYear == null) {
                    System.out.println("Invalid year entered — publication year left unchanged.");
                }
            }

            inventory.updateBook(isbn, newTitle, newAuthor, newYear);
            System.out.println("Book updated successfully.");
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleDeleteBook() {
        System.out.println("\n-- Delete Book --");
        String isbn = readNonBlankString("Enter ISBN of the book to delete: ");
        try {
            inventory.deleteBook(isbn);
            System.out.println("Book deleted successfully.");
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleSearchBook() {
        System.out.println("\n-- Search Book --");
        String isbn = readNonBlankString("Enter ISBN to search for: ");
        try {
            Book book = inventory.findBook(isbn);
            System.out.println("Found:");
            System.out.printf("%-16s | %-30s | %-20s | %s%n", "ISBN", "TITLE", "AUTHOR", "YEAR");
            System.out.println(book);
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Input-validation helpers — centralize all Scanner input handling
    // so every menu handler above stays focused on CRUD logic instead
    // of repeating the same try/catch-and-retry loops.
    // ---------------------------------------------------------------

    /** Reads the numeric menu choice, re-prompting on non-numeric input instead of crashing. */
    private int readMenuChoice() {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number (1-6): ");
            }
        }
    }

    /** Keeps prompting until the user enters a non-empty, non-whitespace-only string. */
    private String readNonBlankString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("This field cannot be empty. Please try again.");
            }
        } while (input.isEmpty());
        return input;
    }

    /**
     * Keeps prompting until the user enters a valid 4-digit-looking year
     * that isn't in the future (a simple, practical sanity check for a
     * publication year).
     */
    private int readValidYear(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            Integer year = parseYearOrNull(input);
            if (year != null) {
                return year;
            }
            System.out.println("Invalid year. Please enter a 4-digit year no later than the current year.");
        }
    }

    /** Parses a year string; returns null (instead of throwing) if it is not a sane year. */
    private Integer parseYearOrNull(String input) {
        try {
            int year = Integer.parseInt(input);
            int currentYear = java.time.Year.now().getValue();
            if (year >= 1450 && year <= currentYear) { // 1450 ~ Gutenberg-press era, a practical lower bound
                return year;
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
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
