package com.academy;

/**
 * Day 4 Part 2 — Access Modifiers, Non-Access Modifiers, Static vs Instance, this
 *
 * Theme: Library Management System
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║  Day 4 Part 2 — Access Modifiers & Static Demo   ║");
        System.out.println("╚═══════════════════════════════════════════════════╝\n");

        System.out.println("=== Access Modifiers Demo ===");
        Book b1 = new Book("Clean Code", "Robert C. Martin", 2008);
        Book b2 = new Book("Effective Java", "Joshua Bloch", 2018);
        Book b3 = new Book("The Pragmatic Programmer", "Andy Hunt", 1999);

        b1.printInfo();
        b2.printInfo();
        b3.printInfo();

        System.out.println("\n=== Checking Out Books ===");
        b1.checkOut("Alice");
        b1.checkOut("Bob");    // already checked out
        b1.returnBook();
        b2.checkOut("Carol");

        System.out.println("\n=== Static Members ===");
        System.out.println("  Total books in library: " + Book.getTotalBooks());
        System.out.println("  Library name:           " + Book.LIBRARY_NAME);   // static final

        System.out.println("\n=== final Field Demo ===");
        Book rare = new Book("RARE-001", "First Edition", 1899);
        // rare.isbn = "new-isbn";  // compile error — isbn is final!
        System.out.println("  Rare book ISBN (final, cannot change): " + rare.getIsbn());

        System.out.println("\n✓ Access Modifiers & Static demo complete.");
    }
}

class Book {

    // public static final — accessible everywhere, shared, immutable
    public static final String LIBRARY_NAME = "Academy Learning Library";

    // private static — shared counter, only accessible within Book class
    private static int totalBooks = 0;

    // private instance fields — encapsulated, access via getters/setters
    private final String isbn;          // final = cannot be reassigned after constructor
    private String title;
    private String author;
    private int    year;
    private boolean checkedOut;
    private String checkedOutBy;

    // package-private (default) — visible within same package only
    int internalId;

    public Book(String isbn, String title, int year) {
        this(isbn, title, "Unknown", year);
    }

    public Book(String title, String author, int year) {
        // 'this' disambiguates field vs parameter with same name
        this.isbn      = "ISBN-" + (++totalBooks);   // auto-generated
        this.title     = title;
        this.author    = author;
        this.year      = year;
        this.checkedOut = false;
        this.internalId = totalBooks;
    }

    // ── Methods ────────────────────────────────────────────────

    public void checkOut(String borrower) {
        if (this.checkedOut) {
            System.out.println("  ✗ \"" + title + "\" is already checked out by " + checkedOutBy);
            return;
        }
        this.checkedOut    = true;
        this.checkedOutBy  = borrower;
        System.out.println("  ✓ \"" + title + "\" checked out to " + borrower);
    }

    public void returnBook() {
        if (!this.checkedOut) {
            System.out.println("  ✗ \"" + title + "\" is not checked out");
            return;
        }
        System.out.println("  ✓ \"" + title + "\" returned by " + checkedOutBy);
        this.checkedOut   = false;
        this.checkedOutBy = null;
    }

    public void printInfo() {
        String status = checkedOut ? "(checked out by " + checkedOutBy + ")" : "(available)";
        System.out.printf("  [%s] \"%s\" by %s (%d) %s%n",
                isbn, title, author, year, status);
    }

    // ── Getters ────────────────────────────────────────────────
    public String  getIsbn()       { return isbn; }
    public String  getTitle()      { return title; }
    public String  getAuthor()     { return author; }
    public int     getYear()       { return year; }
    public boolean isCheckedOut()  { return checkedOut; }

    // ── Setters — only for mutable fields ─────────────────────
    public void setTitle(String title)   { this.title  = title; }
    public void setAuthor(String author) { this.author = author; }

    // ── Static method ──────────────────────────────────────────
    public static int getTotalBooks() { return totalBooks; }
}
