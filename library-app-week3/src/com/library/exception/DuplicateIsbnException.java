package com.library.exception;

/**
 * DuplicateIsbnException.java
 *
 * Thrown when the user tries to add a new book using an ISBN that already
 * exists in the inventory. Since ISBN is the unique key of the inventory
 * map, silently overwriting an existing book on "add" would be a data-loss
 * bug, so this is treated as an explicit error instead.
 */
public class DuplicateIsbnException extends Exception {

    public DuplicateIsbnException(String isbn) {
        super("A book with ISBN " + isbn + " already exists. Use 'Update' instead.");
    }
}
