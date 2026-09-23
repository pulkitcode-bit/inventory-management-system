package com.library.exception;

/**
 * InventoryException.java  (NEW — Week 4 refactor)
 *
 * Common checked-exception base for every business-rule failure the
 * inventory service layer can report (a missing book, a duplicate ISBN,
 * and so on).
 *
 * Why this was introduced: before this refactor, LibraryManagementApp had
 * to catch BookNotFoundException and DuplicateIsbnException separately in
 * every single menu handler (add/update/delete/search), even though the
 * handling code for both was identical — print "Error: " + message and
 * return to the menu. That is a textbook DRY violation. Giving both
 * exceptions a common supertype lets the CLI catch ONE type in ONE place
 * (see LibraryManagementApp.execute()) while BookInventory can still throw
 * the specific subtype it always did, so callers that DO care about the
 * distinction (e.g. a future REST layer) can still catch
 * BookNotFoundException vs DuplicateIsbnException individually.
 */
public abstract class InventoryException extends Exception {
    protected InventoryException(String message) {
        super(message);
    }
}
