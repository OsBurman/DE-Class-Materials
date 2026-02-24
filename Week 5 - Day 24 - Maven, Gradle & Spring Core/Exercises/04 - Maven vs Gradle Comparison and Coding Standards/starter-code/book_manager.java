package com.library;

// Violation 1: Class name uses lowercase — should be UpperCamelCase
// Violation 6: Missing Javadoc on the public class
public class book_manager {

    // Violation 2: Instance variable uses UpperCamelCase — should be lowerCamelCase
    private String BookTitle;

    // Violation 3: Constant uses lowerCamelCase — should be SCREAMING_SNAKE_CASE
    private static final double lateFeePerDay = 0.50;

    // Violation 8: Magic number 3 used directly — should be a named constant
    private int overdueDays = 3;

    public book_manager(String title) {
        this.BookTitle = title;
    }

    // Violation 4: Method name uses UpperCamelCase — should be lowerCamelCase
    // Violation 7: Missing Javadoc on this public method
    public void PrintBookInfo() {
        // Violation 5: Single-letter variable name (not a loop counter)
        double f = lateFeePerDay * overdueDays;

        System.out.println("Book title: " + BookTitle);
        System.out.printf("Late fee: $%.2f%n", f);

        // Violation 8 (cont.): the literal 3 is also used in a comparison below
        if (overdueDays >= 3) {
            System.out.println("Status: OVERDUE");
        } else {
            System.out.println("Status: OK");
        }
    }

    public static void main(String[] args) {
        book_manager m = new book_manager("Clean Code");
        m.PrintBookInfo();
    }
}
