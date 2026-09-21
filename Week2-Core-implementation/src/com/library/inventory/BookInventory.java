package com.library.inventory;

import com.library.exception.BookNotFoundException;
import com.library.exception.DuplicateIsbnException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * BookInventory.java
 *
 * Holds all business logic for managing the library's book inventory.
 * This class is completely independent of the command-line interface
 * (see LibraryManagementApp) — it only knows about Book objects and the
 * in-memory collection that stores them. Keeping this separation means the
 * CRUD logic itself could later be reused behind a REST API, a GUI, or
 * unit tests without any changes.
 *
 * Data structure choice: a LinkedHashMap<String, Book>, keyed by ISBN.
 *  - HashMap gives O(1) average-time lookup/update/delete by ISBN, which is
 *    the natural key operations users perform (update/delete a specific book).
 *  - The "Linked" part preserves insertion order, so "List All Books" shows
 *    books in the order they were added, which is friendlier for a CLI
 *    than the arbitrary order a plain HashMap would give.
 */
public class BookInventory {

    private final Map<String, Book> books = new LinkedHashMap<>();

    /**
     * Adds a new book to the inventory.
     *
     * @throws DuplicateIsbnException if a book with the same ISBN already exists
     */
    public void addBook(Book book) throws DuplicateIsbnException {
        if (books.containsKey(book.getIsbn())) {
            throw new DuplicateIsbnException(book.getIsbn());
        }
        books.put(book.getIsbn(), book);
    }

    /**
     * Returns all books currently in the inventory, in the order they
     * were added. A defensive copy (Collection view backed by the map's
     * values) is returned so callers cannot mutate the internal map directly.
     */
    public Collection<Book> listBooks() {
        return books.values();
    }

    /**
     * Looks up a single book by its ISBN.
     *
     * @throws BookNotFoundException if no book with that ISBN exists
     */
    public Book findBook(String isbn) throws BookNotFoundException {
        Book book = books.get(isbn);
        if (book == null) {
            throw new BookNotFoundException(isbn);
        }
        return book;
    }

    /**
     * Updates the title, author, and publication year of an existing book.
     * The ISBN itself cannot be changed (see the note in Book.java) — if
     * newTitle/newAuthor is null or blank, that particular field is left
     * unchanged, which lets the CLI support "press Enter to keep current
     * value" style prompts.
     *
     * @throws BookNotFoundException if no book with that ISBN exists
     */
    public void updateBook(String isbn, String newTitle, String newAuthor, Integer newYear)
            throws BookNotFoundException {
        Book book = findBook(isbn); // reuses lookup + throws if missing

        if (newTitle != null && !newTitle.isBlank()) {
            book.setTitle(newTitle);
        }
        if (newAuthor != null && !newAuthor.isBlank()) {
            book.setAuthor(newAuthor);
        }
        if (newYear != null) {
            book.setPublicationYear(newYear);
        }
    }

    /**
     * Removes a book from the inventory.
     *
     * @throws BookNotFoundException if no book with that ISBN exists
     */
    public void deleteBook(String isbn) throws BookNotFoundException {
        if (!books.containsKey(isbn)) {
            throw new BookNotFoundException(isbn);
        }
        books.remove(isbn);
    }
    public int size() {
        return books.size();
    }
}
