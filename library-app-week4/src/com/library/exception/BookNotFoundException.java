package com.library.exception;

/**
 * BookNotFoundException.java
 *
 * Thrown when an operation (update, delete, search) is attempted on an
 * ISBN that does not exist in the inventory. A checked exception is used
 * deliberately here (instead of an unchecked RuntimeException) so that the
 * caller in the CLI layer is forced by the compiler to handle the
 * "book not found" case explicitly, rather than letting it crash the app.
 *
 * Week 4 refactor: now extends InventoryException instead of Exception
 * directly, so LibraryManagementApp can handle it through the single
 * shared execute() helper alongside DuplicateIsbnException (see
 * InventoryException.java for the full rationale). The exception's own
 * behaviour and message are unchanged.
 */
public class BookNotFoundException extends InventoryException {

    public BookNotFoundException(String isbn) {
        super("No book found with ISBN: " + isbn);
    }
}
