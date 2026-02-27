import java.util.Arrays;

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

    public void addBook(Book b) {
        if (bookCount < books.length)
            books[bookCount++] = b;
    }

    public void addMember(Member m) {
        if (memberCount < members.length)
            members[memberCount++] = m;
    }

    private Book findBook(int bookId) {
        for (int i = 0; i < bookCount; i++)
            if (books[i].getId() == bookId)
                return books[i];
        return null;
    }

    private Member findMember(int memberId) {
        for (int i = 0; i < memberCount; i++)
            if (members[i].getId() == memberId)
                return members[i];
        return null;
    }

    public Member getMember(int memberId) {
        return findMember(memberId);
    }

    public void checkout(int memberId, int bookId) {
        Book book = findBook(bookId);
        Member member = findMember(memberId);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }
        if (!book.isAvailable()) {
            System.out.println("Book is already checked out.");
            return;
        }
        if (member.borrowBook(book)) {
            book.setAvailable(false);
            System.out.printf("✓ %s checked out '%s'%n", member.getName(), book.getTitle());
        }
    }

    public void returnBook(int memberId, int bookId) {
        Book book = findBook(bookId);
        Member member = findMember(memberId);
        if (book == null || member == null) {
            System.out.println("Invalid id.");
            return;
        }
        if (member.returnBook(bookId)) {
            book.setAvailable(true);
            System.out.printf("✓ %s returned '%s'%n", member.getName(), book.getTitle());
        }
    }

    public Book[] searchByAuthor(String author) {
        Book[] temp = new Book[bookCount];
        int count = 0;
        for (int i = 0; i < bookCount; i++)
            if (books[i].getAuthor().toLowerCase().contains(author.toLowerCase()))
                temp[count++] = books[i];
        return Arrays.copyOf(temp, count);
    }

    public void printCatalog() {
        System.out.println("=== " + name + " ===");
        int available = 0;
        for (int i = 0; i < bookCount; i++) {
            System.out.println("  " + books[i]);
            if (books[i].isAvailable())
                available++;
        }
        System.out.printf("Available: %d / %d total%n", available, bookCount);
    }
}
