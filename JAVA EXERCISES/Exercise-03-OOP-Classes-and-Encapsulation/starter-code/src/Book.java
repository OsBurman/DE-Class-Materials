/**
 * Exercise 03 — OOP Part 1  (STARTER)
 * Represents a book in the library.
 */
public class Book {

    // TODO 1: Declare private fields
    //   int    id
    //   String title
    //   String author
    //   int    year
    //   boolean available  (default: true)

    // TODO 2: Implement a parameterized constructor: Book(int id, String title, String author, int year)
    //   Set available = true by default.

    // TODO 3: Implement getters for all fields.
    //   getId(), getTitle(), getAuthor(), getYear(), isAvailable()
    //   Implement one setter: setAvailable(boolean available)

    // TODO 4: Override toString()
    //   Format: "[ID:1] 'Effective Java' by Joshua Bloch (2018) — Available"
    //   Use isAvailable() to show "Available" or "Checked Out"
    @Override
    public String toString() {
        return "Book(not yet implemented)";
    }

    // TODO 5: Override equals()
    //   Two Book objects are equal if and only if their id fields are equal.
    @Override
    public boolean equals(Object obj) {
        return false; // your code here
    }
}
