/**
 * Exercise 03 — OOP Part 1 (STARTER)
 * Represents a library member.
 */
public class Member {

    // TODO 6: Declare private fields
    // int id
    // String name
    // Book[] borrowed (capacity: 3)
    // int borrowCount

    // Constructor provided — fills in once you add fields
    public Member(int id, String name) {
        // TODO: assign id, name; initialize borrowed array; set borrowCount = 0
    }

    public int getId() {
        return 0;
    } // TODO: return id

    public String getName() {
        return "";
    } // TODO: return name

    // TODO 7: Implement borrowBook(Book b)
    // - If borrowCount >= 3, print "Cannot borrow more than 3 books." and return
    // false
    // - Add book to borrowed[borrowCount], increment borrowCount, return true
    public boolean borrowBook(Book b) {
        return false; // your code here
    }

    // TODO 8: Implement returnBook(int bookId)
    // - Find the book in borrowed[] by matching book.getId() == bookId
    // - If not found, print "Book not found in member's borrowed list." and return
    // false
    // - Remove it by shifting remaining elements left, decrement borrowCount
    // - Return true
    public boolean returnBook(int bookId) {
        return false; // your code here
    }

    public Book[] getBorrowed() {
        return new Book[0];
    } // TODO: return borrowed

    public int getBorrowCount() {
        return 0;
    } // TODO: return borrowCount

    // TODO 9: Override toString()
    // Format: "Member[1] Alice — Borrowed: ['Effective Java', 'Clean Code']"
    @Override
    public String toString() {
        return "Member(not yet implemented)";
    }
}
