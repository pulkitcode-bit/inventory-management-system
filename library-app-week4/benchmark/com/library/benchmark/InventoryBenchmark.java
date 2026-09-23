package com.library.benchmark;

import com.library.exception.DuplicateIsbnException;
import com.library.inventory.Book;
import com.library.inventory.BookInventory;

import java.util.Random;

/**
 * InventoryBenchmark.java  (benchmark-only — NOT part of the app)
 *
 * Measures actual wall-clock time for findByIsbn()-style lookups on:
 *   (a) NaiveListBookInventory — ArrayList + linear scan, O(n) per lookup
 *   (b) BookInventory          — LinkedHashMap, O(1) average per lookup
 *
 * This produces the real numbers quoted in the Week 4 refactoring report's
 * "Performance Enhancements" section, instead of asserting Big-O
 * improvement without evidence. Run with:
 *   java -cp out:benchmark-out com.library.benchmark.InventoryBenchmark
 */
public class InventoryBenchmark {

    private static final int BOOK_COUNT = 20_000;
    private static final int LOOKUP_COUNT = 20_000;

    public static void main(String[] args) throws DuplicateIsbnException {
        System.out.println("Populating " + BOOK_COUNT + " books into both implementations...");

        NaiveListBookInventory naive = new NaiveListBookInventory();
        BookInventory optimized = new BookInventory();
        String[] isbns = new String[BOOK_COUNT];

        for (int i = 0; i < BOOK_COUNT; i++) {
            String isbn = "978-" + String.format("%010d", i);
            isbns[i] = isbn;
            Book book = new Book(isbn, "Book Title " + i, "Author " + (i % 500), 1990 + (i % 34));
            naive.addBook(book);
            optimized.addBook(book);
        }

        // Look up a random sample of ISBNs (same random order for both, so
        // the comparison isn't skewed by lucky/unlucky access patterns).
        Random random = new Random(42);
        String[] lookupIsbns = new String[LOOKUP_COUNT];
        for (int i = 0; i < LOOKUP_COUNT; i++) {
            lookupIsbns[i] = isbns[random.nextInt(BOOK_COUNT)];
        }

        long naiveTimeNs = timeNaiveLookups(naive, lookupIsbns);
        long optimizedTimeNs = timeOptimizedLookups(optimized, lookupIsbns);

        double naiveMs = naiveTimeNs / 1_000_000.0;
        double optimizedMs = optimizedTimeNs / 1_000_000.0;

        System.out.println();
        System.out.println("=== Results: " + LOOKUP_COUNT + " lookups across " + BOOK_COUNT + " books ===");
        System.out.printf("NaiveListBookInventory (ArrayList, O(n) scan): %.2f ms total (%.4f ms/lookup)%n",
                naiveMs, naiveMs / LOOKUP_COUNT);
        System.out.printf("BookInventory (LinkedHashMap, O(1) average):   %.2f ms total (%.4f ms/lookup)%n",
                optimizedMs, optimizedMs / LOOKUP_COUNT);
        System.out.printf("Speedup: %.1fx faster%n", naiveMs / optimizedMs);
    }

    private static long timeNaiveLookups(NaiveListBookInventory naive, String[] lookupIsbns) {
        // Warm-up pass so JIT compilation doesn't distort the timed run.
        for (String isbn : lookupIsbns) {
            naive.findByIsbn(isbn);
        }
        long start = System.nanoTime();
        for (String isbn : lookupIsbns) {
            naive.findByIsbn(isbn);
        }
        return System.nanoTime() - start;
    }

    private static long timeOptimizedLookups(BookInventory optimized, String[] lookupIsbns) {
        try {
            for (String isbn : lookupIsbns) {
                optimized.findBook(isbn);
            }
            long start = System.nanoTime();
            for (String isbn : lookupIsbns) {
                optimized.findBook(isbn);
            }
            return System.nanoTime() - start;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
