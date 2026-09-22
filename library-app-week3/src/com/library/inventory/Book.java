package com.library.inventory;

/**
 * Book.java
 *
 * Represents a single book entity in the library's inventory.
 *
 * Design note: ISBN is treated as the unique identifier for a book (a real
 * library never has two books with the same ISBN), so BookInventory uses it
 * as the key of its internal Map. All other fields (title, author,
 * publicationYear) can be updated freely.
 */
public class Book {

    private String isbn;
    private String title;
    private String author;
    private int publicationYear;

    /**
     * Constructs a new Book with all required properties.
     *
     * @param isbn            unique identifier of the book (e.g. "978-0132350884")
     * @param title           title of the book
     * @param author          author of the book
     * @param publicationYear year the book was published (must be a positive number)
     */
    public Book(String isbn, String title, String author, int publicationYear) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
    }

    // ---------- Getters ----------

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    // ---------- Setters ----------
    // Note: setIsbn() is intentionally NOT provided. The ISBN is the map key
    // inside BookInventory, so changing it on an existing object would make
    // the object's key and the map's key inconsistent. To "change" an ISBN,
    // the correct flow is: delete the old book, then add a new one.

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    /**
     * Human-readable, tabular representation used when listing books
     * on the command line.
     */
    @Override
    public String toString() {
        return String.format("%-16s | %-30s | %-20s | %d",
                isbn, title, author, publicationYear);
    }

    /**
     * Checks whether a publication year is plausible for a real book.
     *
     * Week 3 note: this logic originally lived only inside
     * LibraryManagementApp (the CLI layer), which meant it was impossible
     * to unit-test without simulating console input, and BookInventory
     * (the service layer) had no way to reject an invalid year if it was
     * ever called directly instead of through the menu. It has been moved
     * here — a single, testable source of truth — and both BookInventory
     * and LibraryManagementApp now call this method instead of each
     * having their own copy of the boundary check.
     *
     * @param year candidate publication year
     * @return true if 1450 <= year <= the current year
     */
    public static boolean isValidPublicationYear(int year) {
        int currentYear = java.time.Year.now().getValue();
        return year >= 1450 && year <= currentYear; // 1450 ~ Gutenberg-press era, a practical lower bound
    }
}
