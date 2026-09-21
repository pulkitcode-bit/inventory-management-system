package com.library.exception;

/**
 * BookNotFoundException.java
 *
 * Thrown when an operation (update, delete, search) is attempted on an
 * ISBN that does not exist in the inventory. A checked exception is used
 * deliberately here (instead of an unchecked RuntimeException) so that the
 * caller in the CLI layer is forced by the compiler to handle the
 * "book not found" case explicitly, rather than letting it crash the app.
 */
public class BookNotFoundException extends Exception {

    public BookNotFoundException(String isbn) {
        super("No book found with ISBN: " + isbn);
    }
}
