package com.library.benchmark;

import com.library.inventory.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * NaiveListBookInventory.java  (benchmark-only — NOT part of the app)
 *
 * This class intentionally reproduces the naive, "first draft" way to
 * implement an in-memory inventory: an ArrayList that is scanned linearly
 * every time a book needs to be found by ISBN. It exists ONLY so
 * InventoryBenchmark.java can measure it against the real
 * com.library.inventory.BookInventory (which uses a HashMap) and put an
 * actual number, not just a claim, behind the "O(1) instead of O(n)"
 * optimization described in the Week 4 refactoring report.
 *
 * findByIsbn(), update, and delete here are all O(n) because every one of
 * them has to walk the list looking for a matching ISBN — the same shape
 * of code a lot of beginner Java tutorials show for "storing a list of
 * things," and exactly what BookInventory deliberately avoided from
 * Week 2 onward by using a Map instead.
 */
public class NaiveListBookInventory {

    private final List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book); // O(1) — appending is the one operation a list is naturally fast at
    }

    /** O(n): every lookup walks the list from the start until it finds a match (or doesn't). */
    public Book findByIsbn(String isbn) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                return book;
            }
        }
        throw new NoSuchElementException("No book found with ISBN: " + isbn);
    }

    public int size() {
        return books.size();
    }
}
