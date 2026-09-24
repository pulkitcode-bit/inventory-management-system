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

    /**
     * Week 4 refactor (DRY): the tabular column format used to print a book
     * was previously duplicated in three places — this toString() method,
     * and two separate System.out.printf() header calls inside
     * LibraryManagementApp (one for "List all books", one for "Search").
     * All three now share this single constant, so widening a column or
     * adding a field is a one-line change instead of a three-place hunt.
     */
    public static final String TABLE_ROW_FORMAT = "%-16s | %-30s | %-20s | %s";
    public static final String TABLE_HEADER = String.format(TABLE_ROW_FORMAT, "ISBN", "TITLE", "AUTHOR", "YEAR");

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
     * on the command line. Shares TABLE_ROW_FORMAT with the CLI's table
     * headers (see the field comment above) so the columns always line up.
     */
    @Override
    public String toString() {
        return String.format(TABLE_ROW_FORMAT, isbn, title, author, publicationYear);
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
