package com.library.inventory;

import com.library.exception.BookNotFoundException;
import com.library.exception.DuplicateIsbnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BookInventoryTest.java
 *
 * Unit tests for BookInventory — the pure business-logic / service layer
 * of the library system. Every CRUD operation is tested for both its
 * expected ("happy path") behaviour and its error/edge-case behaviour,
 * per the Week 3 task requirements.
 *
 * A fresh BookInventory is created before every test (see setUp()) so
 * tests never depend on, or leak state into, one another.
 */
class BookInventoryTest {

    private BookInventory inventory;

    private static final String ISBN_1 = "978-0134685991";
    private static final String ISBN_2 = "978-0596009205";

    @BeforeEach
    void setUp() {
        inventory = new BookInventory();
    }

    // ----------------------------------------------------------------
    // addBook() — happy path
    // ----------------------------------------------------------------

    @Test
    void addBook_validBook_increasesSize() throws DuplicateIsbnException {
        inventory.addBook(new Book(ISBN_1, "Effective Java", "Joshua Bloch", 2018));
        assertEquals(1, inventory.size());
    }

    @Test
    void addBook_validBook_isRetrievableByIsbn() throws DuplicateIsbnException, BookNotFoundException {
        inventory.addBook(new Book(ISBN_1, "Effective Java", "Joshua Bloch", 2018));
        Book found = inventory.findBook(ISBN_1);
        assertEquals("Effective Java", found.getTitle());
        assertEquals("Joshua Bloch", found.getAuthor());
        assertEquals(2018, found.getPublicationYear());
    }

    // ----------------------------------------------------------------
    // addBook() — edge cases / error scenarios
    // ----------------------------------------------------------------

    @Test
    void addBook_duplicateIsbn_throwsDuplicateIsbnException() throws Exception {
        inventory.addBook(new Book(ISBN_1, "Effective Java", "Joshua Bloch", 2018));
        Book duplicate = new Book(ISBN_1, "A Different Title", "Someone Else", 2020);

        assertThrows(DuplicateIsbnException.class, () -> inventory.addBook(duplicate));
        // The original book's data must be untouched by the failed add.
        assertEquals("Effective Java", inventory.findBook(ISBN_1).getTitle());
        assertEquals(1, inventory.size());
    }

    @Test
    void addBook_blankIsbn_throwsIllegalArgumentException() {
        Book invalid = new Book("   ", "Some Title", "Some Author", 2020);
        assertThrows(IllegalArgumentException.class, () -> inventory.addBook(invalid));
        assertEquals(0, inventory.size());
    }

    @Test
    void addBook_nullIsbn_throwsIllegalArgumentException() {
        Book invalid = new Book(null, "Some Title", "Some Author", 2020);
        assertThrows(IllegalArgumentException.class, () -> inventory.addBook(invalid));
    }

    @Test
    void addBook_blankTitle_throwsIllegalArgumentException() {
        Book invalid = new Book(ISBN_1, "   ", "Some Author", 2020);
        assertThrows(IllegalArgumentException.class, () -> inventory.addBook(invalid));
    }

    @Test
    void addBook_blankAuthor_throwsIllegalArgumentException() {
        Book invalid = new Book(ISBN_1, "Some Title", "", 2020);
        assertThrows(IllegalArgumentException.class, () -> inventory.addBook(invalid));
    }

    @Test
    void addBook_futureYear_throwsIllegalArgumentException() {
        int nextYear = java.time.Year.now().getValue() + 1;
        Book invalid = new Book(ISBN_1, "Some Title", "Some Author", nextYear);
        assertThrows(IllegalArgumentException.class, () -> inventory.addBook(invalid));
    }

    @Test
    void addBook_zeroYear_throwsIllegalArgumentException() {
        Book invalid = new Book(ISBN_1, "Some Title", "Some Author", 0);
        assertThrows(IllegalArgumentException.class, () -> inventory.addBook(invalid));
    }

    @Test
    void addBook_negativeYear_throwsIllegalArgumentException() {
        Book invalid = new Book(ISBN_1, "Some Title", "Some Author", -500);
        assertThrows(IllegalArgumentException.class, () -> inventory.addBook(invalid));
    }

    // ----------------------------------------------------------------
    // listBooks()
    // ----------------------------------------------------------------

    @Test
    void listBooks_emptyInventory_returnsEmptyCollection() {
        assertTrue(inventory.listBooks().isEmpty());
    }

    @Test
    void listBooks_preservesInsertionOrder() throws DuplicateIsbnException {
        inventory.addBook(new Book(ISBN_1, "Effective Java", "Joshua Bloch", 2018));
        inventory.addBook(new Book(ISBN_2, "Head First Design Patterns", "Eric Freeman", 2004));

        List<Book> books = List.copyOf(inventory.listBooks());
        Iterator<Book> it = books.iterator();
        assertEquals(ISBN_1, it.next().getIsbn());
        assertEquals(ISBN_2, it.next().getIsbn());
    }

    // ----------------------------------------------------------------
    // findBook()
    // ----------------------------------------------------------------

    @Test
    void findBook_nonExistentIsbn_throwsBookNotFoundException() {
        assertThrows(BookNotFoundException.class, () -> inventory.findBook("does-not-exist"));
    }

    // ----------------------------------------------------------------
    // updateBook() — happy path
    // ----------------------------------------------------------------

    @Test
    void updateBook_allFieldsProvided_updatesEverything() throws Exception {
        inventory.addBook(new Book(ISBN_1, "Old Title", "Old Author", 2000));

        inventory.updateBook(ISBN_1, "New Title", "New Author", 2020);

        Book updated = inventory.findBook(ISBN_1);
        assertEquals("New Title", updated.getTitle());
        assertEquals("New Author", updated.getAuthor());
        assertEquals(2020, updated.getPublicationYear());
    }

    @Test
    void updateBook_nullFields_keepsOriginalValues() throws Exception {
        inventory.addBook(new Book(ISBN_1, "Old Title", "Old Author", 2000));

        inventory.updateBook(ISBN_1, null, null, null);

        Book unchanged = inventory.findBook(ISBN_1);
        assertEquals("Old Title", unchanged.getTitle());
        assertEquals("Old Author", unchanged.getAuthor());
        assertEquals(2000, unchanged.getPublicationYear());
    }

    /**
     * Regression test for Bug #2 in the Week 3 debugging report: an
     * earlier version of updateBook() used String.isEmpty() instead of
     * String.isBlank() to decide whether to skip a field. A whitespace-only
     * string ("   ") is NOT empty(), so the buggy version overwrote the
     * title/author with blank spaces instead of leaving them unchanged.
     * This test fails against that buggy version and passes against the
     * fixed one.
     */
    @Test
    void updateBook_whitespaceOnlyFields_areTreatedAsBlankAndKeepOriginalValues() throws Exception {
        inventory.addBook(new Book(ISBN_1, "Old Title", "Old Author", 2000));

        inventory.updateBook(ISBN_1, "   ", "\t", null);

        Book unchanged = inventory.findBook(ISBN_1);
        assertEquals("Old Title", unchanged.getTitle(), "Whitespace-only title must not overwrite the original");
        assertEquals("Old Author", unchanged.getAuthor(), "Whitespace-only author must not overwrite the original");
    }

    // ----------------------------------------------------------------
    // updateBook() — edge cases / error scenarios
    // ----------------------------------------------------------------

    @Test
    void updateBook_nonExistentIsbn_throwsBookNotFoundException() {
        assertThrows(BookNotFoundException.class,
                () -> inventory.updateBook("does-not-exist", "Title", "Author", 2020));
    }

    @Test
    void updateBook_invalidYear_throwsIllegalArgumentExceptionAndLeavesYearUnchanged() throws Exception {
        inventory.addBook(new Book(ISBN_1, "Old Title", "Old Author", 2000));

        assertThrows(IllegalArgumentException.class,
                () -> inventory.updateBook(ISBN_1, null, null, java.time.Year.now().getValue() + 5));

        // The year must still be the original 2000 — the exception should
        // be thrown before any field is mutated.
        assertEquals(2000, inventory.findBook(ISBN_1).getPublicationYear());
    }

    // ----------------------------------------------------------------
    // deleteBook()
    // ----------------------------------------------------------------

    @Test
    void deleteBook_existingIsbn_removesItFromInventory() throws Exception {
        inventory.addBook(new Book(ISBN_1, "Effective Java", "Joshua Bloch", 2018));

        inventory.deleteBook(ISBN_1);

        assertEquals(0, inventory.size());
        assertThrows(BookNotFoundException.class, () -> inventory.findBook(ISBN_1));
    }

    @Test
    void deleteBook_nonExistentIsbn_throwsBookNotFoundException() {
        assertThrows(BookNotFoundException.class, () -> inventory.deleteBook("does-not-exist"));
    }

    @Test
    void deleteBook_doesNotAffectOtherBooks() throws Exception {
        inventory.addBook(new Book(ISBN_1, "Effective Java", "Joshua Bloch", 2018));
        inventory.addBook(new Book(ISBN_2, "Head First Design Patterns", "Eric Freeman", 2004));

        inventory.deleteBook(ISBN_1);

        assertEquals(1, inventory.size());
        assertNotNull(inventory.findBook(ISBN_2));
    }

    // ----------------------------------------------------------------
    // size()
    // ----------------------------------------------------------------

    @Test
    void size_reflectsAddAndDeleteOperations() throws Exception {
        assertEquals(0, inventory.size());
        inventory.addBook(new Book(ISBN_1, "Effective Java", "Joshua Bloch", 2018));
        assertEquals(1, inventory.size());
        inventory.addBook(new Book(ISBN_2, "Head First Design Patterns", "Eric Freeman", 2004));
        assertEquals(2, inventory.size());
        inventory.deleteBook(ISBN_1);
        assertEquals(1, inventory.size());
    }
}
