/**
 * Exercise 03 — OOP Part 1 (STARTER)
 * Manages books and members.
 */
public class Library {

    private String name;
    private Book[] books;
    private Member[] members;
    private int bookCount;
    private int memberCount;

    public Library(String name, int capacity) {
        this.name = name;
        this.books = new Book[capacity];
        this.members = new Member[capacity];
        this.bookCount = 0;
        this.memberCount = 0;
    }

    // TODO 10a: Implement addBook(Book b)
    // Add the book to books[] and increment bookCount (if there is room).
    public void addBook(Book b) {
        // your code here
    }

    // TODO 10b: Implement addMember(Member m)
    public void addMember(Member m) {
        // your code here
    }

    // Helper: find a book by id (returns null if not found)
    private Book findBook(int bookId) {
        for (int i = 0; i < bookCount; i++) {
            if (books[i].getId() == bookId)
                return books[i];
        }
        return null;
    }

    // Helper: find a member by id (returns null if not found)
    private Member findMember(int memberId) {
        for (int i = 0; i < memberCount; i++) {
            if (members[i].getId() == memberId)
                return members[i];
        }
        return null;
    }

    // TODO 11: Implement checkout(int memberId, int bookId)
    // 1. Find the book and the member (print error and return if either is null)
    // 2. If book is not available, print "Book is already checked out." and return
    // 3. Call member.borrowBook(book); if successful, set book.setAvailable(false)
    // 4. Print success message
    public void checkout(int memberId, int bookId) {
        // your code here
    }

    // TODO 12: Implement returnBook(int memberId, int bookId)
    // 1. Find book and member
    // 2. Call member.returnBook(bookId); if successful, set book.setAvailable(true)
    // 3. Print success message
    public void returnBook(int memberId, int bookId) {
        // your code here
    }

    // TODO 13: Implement searchByAuthor(String author)
    // Return an array of books whose author contains the given string
    // (case-insensitive).
    // Hint: build a temporary array of matching books, return a trimmed copy.
    public Book[] searchByAuthor(String author) {
        return new Book[0]; // your code here
    }

    // TODO 14: Implement printCatalog()
    // Print the library name, then each book using book.toString().
    // Also print: "Available: X / Y total"
    public void printCatalog() {
        // your code here
    }
}
