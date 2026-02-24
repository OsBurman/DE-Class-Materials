# Exercise 05: Creating and Injecting a Service

## Objective
Create an Angular service with `@Injectable`, register it with Angular's DI system using `providedIn: 'root'`, and inject it into a component via the constructor.

## Background
Services are singleton objects managed by Angular's injector. They encapsulate reusable logic (data access, calculations, logging) that components should not own directly. You will build a `BookService` that maintains an in-memory list of books, and a `BookListComponent` that consumes it.

## Requirements

### `BookService`
1. Create `book.service.ts` decorated with `@Injectable({ providedIn: 'root' })`.
2. Declare a private `books` array of objects: `{ id: number, title: string, author: string }`. Pre-populate it with at least 3 books.
3. Implement three public methods:
   - `getBooks()` — returns the full books array.
   - `addBook(title: string, author: string)` — creates a new book object (auto-increment the `id`) and pushes it into the array.
   - `removeBook(id: number)` — filters out the book with the matching `id`.

### `BookListComponent`
1. Inject `BookService` via the constructor (TypeScript constructor injection).
2. Declare a `books` property initialized to `this.bookService.getBooks()` inside `ngOnInit`.
3. Declare `newTitle = ''` and `newAuthor = ''` bound to two text inputs with `[(ngModel)]`.
4. Implement `addBook()` — calls `bookService.addBook(newTitle, newAuthor)`, then clears both input fields.
5. Implement `removeBook(id: number)` — calls `bookService.removeBook(id)`.
6. In the template:
   - List all books with `*ngFor`, showing title and author.
   - Add a "Remove" button per book that calls `removeBook(book.id)`.
   - Show an add-book form with the two bound inputs and an "Add Book" button.

7. Import `FormsModule` in `AppModule` (required for `[(ngModel)]`).

## Hints
- `@Injectable({ providedIn: 'root' })` registers the service in the root injector — no need to add it to `providers: []`.
- Inject the service with `constructor(private bookService: BookService) {}`.
- Call `getBooks()` in `ngOnInit` rather than the constructor so it runs after Angular initialization.
- `[(ngModel)]` is two-way binding: it reads and writes the property simultaneously.

## Expected Output
```
Book Library

• Clean Code — Robert C. Martin        [Remove]
• The Pragmatic Programmer — Hunt & Thomas [Remove]
• Designing Data-Intensive Applications — Martin Kleppmann [Remove]

[Add Book]
Title: [____________]  Author: [____________]  [Add Book]

(After adding "Refactoring" by "Martin Fowler"):
• Refactoring — Martin Fowler          [Remove]
```
