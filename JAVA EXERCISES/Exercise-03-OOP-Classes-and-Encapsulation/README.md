# Exercise 03 — OOP Part 1: Classes & Encapsulation

## Overview

Build a **Library Management System**. You will model real-world entities as Java classes, enforce encapsulation with private fields and public accessors, and see how objects interact.

---

## Concepts Covered

- Defining classes with fields and methods
- Constructors (default, parameterized, copy constructor)
- `private` fields with `public` getters and setters
- The `this` keyword
- `toString()` override
- `equals()` override
- `static` fields and methods
- Object arrays and object interaction

---

## Classes to Build

| Class | Responsibility |
|---|---|
| `Book` | Represents a book (id, title, author, year, available) |
| `Member` | Represents a library member (id, name, borrowed list) |
| `Library` | Manages books and members, checkout/return operations |
| `Main` | Entry point — drives the demo |

---

## TODOs

### Book.java
- [ ] **TODO 1** — Add private fields: `int id`, `String title`, `String author`, `int year`, `boolean available`
- [ ] **TODO 2** — Implement a parameterized constructor
- [ ] **TODO 3** — Implement all getters; add a `setAvailable(boolean)` setter
- [ ] **TODO 4** — Override `toString()` to produce a readable one-line summary
- [ ] **TODO 5** — Override `equals()` — two Books are equal if their `id` matches

### Member.java
- [ ] **TODO 6** — Add private fields: `int id`, `String name`, `Book[] borrowed` (max 3), `int borrowCount`
- [ ] **TODO 7** — Implement `borrowBook(Book b)` — adds book to array if space available
- [ ] **TODO 8** — Implement `returnBook(int bookId)` — removes book from array, shifts remaining
- [ ] **TODO 9** — Override `toString()` to list the member name and borrowed titles

### Library.java
- [ ] **TODO 10** — Implement `addBook(Book b)` and `addMember(Member m)`
- [ ] **TODO 11** — Implement `checkout(int memberId, int bookId)` — marks book unavailable
- [ ] **TODO 12** — Implement `returnBook(int memberId, int bookId)` — marks book available
- [ ] **TODO 13** — Implement `searchByAuthor(String author)` — returns matching books
- [ ] **TODO 14** — Implement `printCatalog()` — prints all books with availability

---

## Running the Program

```bash
cd starter-code/src
javac *.java
java Main
```
