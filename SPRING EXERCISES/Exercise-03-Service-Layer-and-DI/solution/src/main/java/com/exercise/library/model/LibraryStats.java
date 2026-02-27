package com.exercise.library.model;

public class LibraryStats {
    private long totalBooks;
    private long availableBooks;
    private long checkedOutBooks;

    public LibraryStats() {
    }

    public LibraryStats(long totalBooks, long availableBooks, long checkedOutBooks) {
        this.totalBooks = totalBooks;
        this.availableBooks = availableBooks;
        this.checkedOutBooks = checkedOutBooks;
    }

    public long getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(long totalBooks) {
        this.totalBooks = totalBooks;
    }

    public long getAvailableBooks() {
        return availableBooks;
    }

    public void setAvailableBooks(long availableBooks) {
        this.availableBooks = availableBooks;
    }

    public long getCheckedOutBooks() {
        return checkedOutBooks;
    }

    public void setCheckedOutBooks(long checkedOutBooks) {
        this.checkedOutBooks = checkedOutBooks;
    }
}
