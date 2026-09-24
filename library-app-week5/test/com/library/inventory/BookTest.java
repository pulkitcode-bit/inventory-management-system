package com.library.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BookTest.java
 *
 * Unit tests for Book.isValidPublicationYear(), the shared boundary check
 * used by both BookInventory and LibraryManagementApp (see the Week 3
 * debugging report — this check used to be duplicated in two places with
 * slightly different logic, which is exactly the kind of bug boundary
 * tests are good at catching).
 */
class BookTest {

    @Test
    void yearBelowMinimum_isInvalid() {
        assertFalse(Book.isValidPublicationYear(1449), "1449 is just below the accepted range and should be rejected");
    }

    @Test
    void yearAtMinimumBoundary_isValid() {
        assertTrue(Book.isValidPublicationYear(1450), "1450 is the inclusive lower boundary and should be accepted");
    }

    @Test
    void currentYear_isValid() {
        int currentYear = java.time.Year.now().getValue();
        assertTrue(Book.isValidPublicationYear(currentYear), "The current year should be a valid publication year");
    }

    @Test
    void yearOneAfterCurrent_isInvalid() {
        int nextYear = java.time.Year.now().getValue() + 1;
        assertFalse(Book.isValidPublicationYear(nextYear), "A future publication year should never be accepted");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -1000})
    void nonPositiveYears_areInvalid(int year) {
        assertFalse(Book.isValidPublicationYear(year), "Zero/negative years are not valid publication years");
    }

    @ParameterizedTest
    @ValueSource(ints = {1500, 1900, 2000, 2020})
    void typicalHistoricalYears_areValid(int year) {
        assertTrue(Book.isValidPublicationYear(year), "Common historical publication years should be accepted");
    }
}
