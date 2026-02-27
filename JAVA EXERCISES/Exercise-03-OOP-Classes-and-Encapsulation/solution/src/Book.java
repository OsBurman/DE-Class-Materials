public class Book {
    private int     id;
    private String  title;
    private String  author;
    private int     year;
    private boolean available;

    public Book(int id, String title, String author, int year) {
        this.id        = id;
        this.title     = title;
        this.author    = author;
        this.year      = year;
        this.available = true;
    }

    public int     getId()       { return id;        }
    public String  getTitle()    { return title;     }
    public String  getAuthor()   { return author;    }
    public int     getYear()     { return year;      }
    public boolean isAvailable() { return available; }
    public void    setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        String status = available ? "Available" : "Checked Out";
        return String.format("[ID:%d] '%s' by %s (%d) — %s", id, title, author, year, status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Book)) return false;
        return this.id == ((Book) obj).id;
    }
}
