public class Member {
    private int id;
    private String name;
    private Book[] borrowed;
    private int borrowCount;

    public Member(int id, String name) {
        this.id = id;
        this.name = name;
        this.borrowed = new Book[3];
        this.borrowCount = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Book[] getBorrowed() {
        return borrowed;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public boolean borrowBook(Book b) {
        if (borrowCount >= 3) {
            System.out.println("Cannot borrow more than 3 books.");
            return false;
        }
        borrowed[borrowCount++] = b;
        return true;
    }

    public boolean returnBook(int bookId) {
        for (int i = 0; i < borrowCount; i++) {
            if (borrowed[i].getId() == bookId) {
                // shift left
                for (int j = i; j < borrowCount - 1; j++)
                    borrowed[j] = borrowed[j + 1];
                borrowed[--borrowCount] = null;
                return true;
            }
        }
        System.out.println("Book not found in member's borrowed list.");
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Member[").append(id).append("] ").append(name).append(" — Borrowed: [");
        for (int i = 0; i < borrowCount; i++) {
            if (i > 0)
                sb.append(", ");
            sb.append("'").append(borrowed[i].getTitle()).append("'");
        }
        sb.append("]");
        return sb.toString();
    }
}
