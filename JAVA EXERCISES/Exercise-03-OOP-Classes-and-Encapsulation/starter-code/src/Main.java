/**
 * Exercise 03 — OOP Part 1 (STARTER)
 * Entry point — do NOT modify.
 */
public class Main {
    public static void main(String[] args) {
        Library lib = new Library("City Central Library", 20);

        // Add books
        lib.addBook(new Book(1, "Effective Java", "Joshua Bloch", 2018));
        lib.addBook(new Book(2, "Clean Code", "Robert C. Martin", 2008));
        lib.addBook(new Book(3, "The Pragmatic Programmer", "David Thomas", 2019));
        lib.addBook(new Book(4, "Java Concurrency in Practice", "Brian Goetz", 2006));
        lib.addBook(new Book(5, "Head First Java", "Kathy Sierra", 2005));

        // Add members
        lib.addMember(new Member(101, "Alice"));
        lib.addMember(new Member(102, "Bob"));

        // Print initial catalog
        lib.printCatalog();

        System.out.println("\n--- Checkouts ---");
        lib.checkout(101, 1); // Alice borrows Effective Java
        lib.checkout(101, 3); // Alice borrows Pragmatic Programmer
        lib.checkout(102, 2); // Bob borrows Clean Code
        lib.checkout(102, 1); // Should fail: already checked out

        System.out.println("\n--- Member Status ---");
        System.out.println(lib.getMember(101));
        System.out.println(lib.getMember(102));

        System.out.println("\n--- Return ---");
        lib.returnBook(101, 1);
        lib.checkout(102, 1); // Now Bob can borrow it

        System.out.println("\n--- Search by Author: 'Robert' ---");
        Book[] results = lib.searchByAuthor("Robert");
        for (Book b : results)
            System.out.println("  " + b);

        System.out.println("\n--- Updated Catalog ---");
        lib.printCatalog();
    }
}
