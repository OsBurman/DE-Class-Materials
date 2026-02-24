package com.library;

/**
 * Manages book information and calculates late fees for overdue items.
 *
 * <p>Fix 1: Class renamed from {@code book_manager} to {@code WellWrittenCode}
 * using UpperCamelCase as required by Java naming conventions.</p>
 *
 * <p>Fix 6: Added class-level Javadoc describing the class purpose.</p>
 */
public class WellWrittenCode {

    // Fix 2: Renamed from BookTitle → bookTitle (lowerCamelCase for instance variables)
    private String bookTitle;

    // Fix 3: Renamed from lateFeePerDay → LATE_FEE_PER_DAY (SCREAMING_SNAKE_CASE for constants)
    private static final double LATE_FEE_PER_DAY = 0.50;

    // Fix 8: Extracted magic number 3 into a named constant so its meaning is clear
    private static final int OVERDUE_THRESHOLD_DAYS = 3;

    private int overdueDays = OVERDUE_THRESHOLD_DAYS;

    public WellWrittenCode(String title) {
        // Fix 2 (cont.): reference the correctly named field
        this.bookTitle = title;
    }

    /**
     * Prints the book title, calculated late fee, and overdue status to standard output.
     *
     * <p>Fix 4: Method renamed from PrintBookInfo → printBookInfo (lowerCamelCase for methods).</p>
     * <p>Fix 7: Added Javadoc describing what this method does.</p>
     */
    public void printBookInfo() {
        // Fix 5: Renamed single-letter variable 'f' → 'lateFee' (descriptive name)
        double lateFee = LATE_FEE_PER_DAY * overdueDays;

        // Fix 2 (cont.): use correctly named field bookTitle
        System.out.println("Book title: " + bookTitle);
        System.out.printf("Late fee: $%.2f%n", lateFee);

        // Fix 8 (cont.): use the named constant instead of the magic literal 3
        if (overdueDays >= OVERDUE_THRESHOLD_DAYS) {
            System.out.println("Status: OVERDUE");
        } else {
            System.out.println("Status: OK");
        }
    }

    public static void main(String[] args) {
        WellWrittenCode manager = new WellWrittenCode("Clean Code");
        manager.printBookInfo();
    }
}
