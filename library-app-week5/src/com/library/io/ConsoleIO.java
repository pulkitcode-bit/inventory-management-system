package com.library.io;

import com.library.inventory.Book;

import java.util.Collection;
import java.util.Scanner;

/**
 * ConsoleIO.java  (NEW — Week 4 refactor)
 *
 * Week 4 modularization: every Scanner read and System.out print used to
 * live directly inside LibraryManagementApp, mixed in with the menu
 * dispatch logic. That made the main class long (~230 lines) and mixed two
 * unrelated concerns — "how do I read/validate console input" and "which
 * menu option does what" — in the same file.
 *
 * All console I/O now lives here instead. LibraryManagementApp depends on
 * this class but contains no Scanner/System.out calls of its own anymore.
 * This mirrors the same separation-of-concerns decision already made
 * between LibraryManagementApp and BookInventory back in Week 2 (CLI layer
 * vs. business-logic layer) — this refactor just draws the next line down,
 * separating "CLI orchestration" from "raw console I/O" as well.
 *
 * A nice side effect: this class has no dependency on BookInventory at
 * all, so it could be reused as-is for a completely different
 * menu-driven CLI application.
 */
public class ConsoleIO {

    private final Scanner scanner = new Scanner(System.in);

    // ---------------------------------------------------------------
    // Menu printing
    // ---------------------------------------------------------------

    public void printBanner() {
        System.out.println("=================================================");
        System.out.println(" LIBRARY BOOK INVENTORY MANAGEMENT SYSTEM");
        System.out.println("=================================================");
    }

    public void printMenu() {
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
    // Book table printing — uses Book.TABLE_ROW_FORMAT / TABLE_HEADER so
    // the header and each row can never drift out of alignment (see the
    // DRY note in Book.java).
    // ---------------------------------------------------------------

    public void printBookTableHeader() {
        System.out.println(Book.TABLE_HEADER);
        System.out.println("-".repeat(Book.TABLE_HEADER.length()));
    }

    public void printBooks(Collection<Book> books) {
        if (books.isEmpty()) {
            System.out.println("The inventory is currently empty.");
            return;
        }
        printBookTableHeader();
        books.forEach(System.out::println);
    }

    public void printBook(Book book) {
        printBookTableHeader();
        System.out.println(book);
    }

    public void printError(String message) {
        System.out.println("Error: " + message);
    }

    public void printSuccess(String message) {
        System.out.println(message);
    }

    // ---------------------------------------------------------------
    // Input reading + validation (unchanged behaviour from Week 2/3,
    // moved here verbatim aside from now calling Book.isValidPublicationYear
    // directly rather than duplicating the boundary check)
    // ---------------------------------------------------------------

    /** Reads the numeric menu choice, re-prompting on non-numeric input instead of crashing. */
    public int readMenuChoice() {
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
    public String readNonBlankString(String prompt) {
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

    /** Reads one line of input as-is (used for optional "leave blank to keep" prompts). */
    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Keeps prompting until the user enters a valid publication year, per
     * Book.isValidPublicationYear() — the single shared definition of
     * "valid year" introduced in Week 3.
     */
    public int readValidYear(String prompt) {
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

    /**
     * Reads an OPTIONAL year for the "update" flow: blank input means
     * "keep the current value" (returns null); non-blank input must parse
     * as a valid year or the caller is told and null is returned so the
     * field is left unchanged rather than the whole operation crashing.
     */
    public Integer readOptionalYear(String prompt) {
        String input = readLine(prompt);
        if (input.isBlank()) {
            return null;
        }
        Integer year = parseYearOrNull(input);
        if (year == null) {
            System.out.println("Invalid year entered — publication year left unchanged.");
        }
        return year;
    }

    /** Parses a year string; returns null (instead of throwing) if it is not a sane year. */
    private Integer parseYearOrNull(String input) {
        try {
            int year = Integer.parseInt(input);
            return Book.isValidPublicationYear(year) ? year : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void close() {
        scanner.close();
    }
}
