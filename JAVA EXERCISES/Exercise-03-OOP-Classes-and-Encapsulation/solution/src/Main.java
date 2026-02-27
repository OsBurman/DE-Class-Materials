public class Main {
    public static void main(String[] args) {
        Library lib = new Library("City Central Library", 20);

        lib.addBook(new Book(1, "Effective Java",              "Joshua Bloch",    2018));
        lib.addBook(new Book(2, "Clean Code",                  "Robert C. Martin",2008));
        lib.addBook(new Book(3, "The Pragmatic Programmer",    "David Thomas",    2019));
        lib.addBook(new Book(4, "Java Concurrency in Practice","Brian Goetz",     2006));
        lib.addBook(new Book(5, "Head First Java",             "Kathy Sierra",    2005));

        lib.addMember(new Member(101, "Alice"));
        lib.addMember(new Member(102, "Bob"));

        lib.printCatalog();

        System.out.println("\n--- Checkouts ---");
        lib.checkout(101, 1);
        lib.checkout(101, 3);
        lib.checkout(102, 2);
        lib.checkout(102, 1);

        System.out.println("\n--- Member Status ---");
        System.out.println(lib.getMember(101));
        System.out.println(lib.getMember(102));

        System.out.println("\n--- Return ---");
        lib.returnBook(101, 1);
        lib.checkout(102, 1);

        System.out.println("\n--- Search by Author: 'Robert' ---");
        for (Book b : lib.searchByAuthor("Robert")) System.out.println("  " + b);

        System.out.println("\n--- Updated Catalog ---");
        lib.printCatalog();
    }
}
